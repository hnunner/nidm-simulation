#!/usr/bin/env Rscript
############################################## LIBRARIES ##############################################
library(reshape2)
library(ggplot2)
library(QuantPsyc)
library(texreg)
library(lme4)
library(sjstats)
library(usdm)
library(xtable)
library(dplyr)
library(xtable)


########################################### GLOBAL CONSTANTS ##########################################
# input/output directory
args = commandArgs(trailingOnly=TRUE)
if (length(args) != 1) {
  stop("Exactly one argument must be supplied (input/output directory).n", call.=FALSE)
}
IO_DIR <- args[1]
# path to generated data (*.csv files)
DATA_PATH                   <- paste("data/", IO_DIR, "/", sep = "")
# file names of generated data
CSV_SUMMARY_PATH            <- paste(DATA_PATH, "simulation-summary.csv", sep = "")
CSV_ROUND_SUMMARY_PATH      <- paste(DATA_PATH, "round-summary.csv", sep = "")
CSV_AGENT_DETAILS_PATH      <- paste(DATA_PATH, "agent-details.csv", sep = "")
# export - file system
EXPORT_DIR_PLOTS            <- "figures/"
EXPORT_PATH_PLOTS           <- paste(DATA_PATH, EXPORT_DIR_PLOTS, sep = "")
EXPORT_FILE_TYPE_PLOTS      <- "png"
EXPORT_FILE_EXTENSION_PLOTS <- paste(".", EXPORT_FILE_TYPE_PLOTS, sep = "")
EXPORT_DIR_NUM              <- "numerical/"
EXPORT_PATH_NUM             <- paste(DATA_PATH, EXPORT_DIR_NUM, sep = "")
EXPORT_FILE_TYPE_REG        <- "html"
EXPORT_FILE_EXTENSION_REG   <- paste(".", EXPORT_FILE_TYPE_REG, sep = "")
EXPORT_FILE_TYPE_DF         <- "csv"
EXPORT_FILE_EXTENSION_DF    <- paste(".", EXPORT_FILE_TYPE_DF, sep = "")
# export image settings
EXPORT_PLOT_WIDTH           <- 250
EXPORT_PLOT_HEIGHT          <- 150
EXPORT_SIZE_UNITS           <- "mm"
EXPORT_DPI                  <- 1200
RIBBON_ALPHA                <- 0.3
# colors (http://mkweb.bcgsc.ca/colorblind/)
COLORS                      <- c(Susceptible = "#F0E442",   # yellow
                                 Infected    = "#E69F00",   # orange
                                 Recovered   = "#0072B2",   # blue
                                 Degree      = "#888888",   # grey
                                 Density     = "#000000")   # black


##################################### IMPORTS / DATA PREPARATIONS ####################################
#----------------------------------------------------------------------------------------------------#
# function: loadCSV
#     Loads data from a CSV file.
# param:  filePath
#     path to the file to be loaded
# return: the CSV file data as data frame
#----------------------------------------------------------------------------------------------------#
loadCSV <- function(filePath) {
  csv <- read.csv(file=filePath, header=TRUE, sep=",")
  return(csv)
}

#----------------------------------------------------------------------------------------------------#
# function: loadSimulationSummaryData
#     Loads summary data for NetGame simulations.
# return: the summary data for NetGame simulations
#----------------------------------------------------------------------------------------------------#
loadSimulationSummaryData <- function() {
  return(loadCSV(CSV_SUMMARY_PATH))
}

#----------------------------------------------------------------------------------------------------#
# function: loadRoundSummaryData
#     Loads summary data for all simulated NetGame rounds.
# return: the summary data for all simulated NetGame rounds
#----------------------------------------------------------------------------------------------------#
loadRoundSummaryData <- function() {
  return(loadCSV(CSV_ROUND_SUMMARY_PATH))
}

#----------------------------------------------------------------------------------------------------#
# function: reduceRoundSummaryData
#     Reduces round summary data. That is, only last 10 rounds of the pre-epidemic stage are kept.
#     This is especially useful when only dynamics of the epidemic stage are considered for analysis.
#     For example: SIR development plots, in which the pre-epidemic stage does not affect disease
#     states.
# param:  rsData
#     the round summary data to be reduced
# param:  cropTail
#     the number of rounds to be dropped at the end
# return: the round summary data with only 10 rounds in the pre-epidemic stage
#----------------------------------------------------------------------------------------------------#
reduceRoundSummaryData <- function(rsData, cropTail = 0) {

  reducedRsData <- rsData

  # 1. drop rounds of pre-epidemic stage
  # determine how many rounds to drop
  roundsToDrop <- max(subset(rsData, sim.prop.stage == 'pre-epidemic')$sim.prop.round) - 10
  # reduce overall amount of rounds
  reducedRsData$sim.prop.round <- reducedRsData$sim.prop.round - roundsToDrop
  # drop all rounds with ids lower than 1
  reducedRsData <- subset(reducedRsData, sim.prop.round > 0)

  # 2. drop rounds of post-epidemic stage
  postEpidemicData <- subset(reducedRsData, sim.prop.stage == "post-epidemic")
  # deterimine the round when post-epidemic stage begins for each simulation
  roundsPerSimulation <- postEpidemicData[!duplicated(postEpidemicData$sim.param.uid),]$sim.prop.round
  # take the latest round minus a threshold
  roundsToPlot <- max(roundsPerSimulation) - cropTail
  # increase until the amount of rounds is dividable by 10, so that plots can have
  # ticks at a stepsize of 10
  while (roundsToPlot %% 10 != 0) {
    roundsToPlot <- roundsToPlot + 1
  }
  reducedRsData <- subset(reducedRsData, sim.prop.round <= roundsToPlot)

  return(reducedRsData)
}

#----------------------------------------------------------------------------------------------------#
# function: subsetsByColumnValues
#     Creates subsets of a data set based on unique values within one or two columns.
# param:  data
#     the data to create subsets from
# param:  col1
#     the name of column one
# param:  col2
#     the name of column two; if 'NA' subsets for col1 are generated only
# return: the subsets based on unique column values
#----------------------------------------------------------------------------------------------------#
subsetsByColumnValues <- function(data, col1, col2=NA) {

  # subsets of column 1
  subsetsCol1 <- list()
  i <- 1
  for (u in sort(unique(data[, col1]))) {
    subsetsCol1[[i]] <- data[data[, col1] == u, ]
    i <- i + 1
  }

  if (is.na(col2)) {
    return(subsetsCol1)
  }

  # subsets of column 2 for subsets of column 1
  subsetsCol2ForCol1 <- list()
  i <- 1
  for (subsetCol1 in subsetsCol1) {
    for (subsetCol2ForCol1 in subsetsByColumnValues(subsetCol1, col2)) {
      subsetsCol2ForCol1[[i]] <- subsetCol2ForCol1
      i <- i + 1
    }
  }

  return(subsetsCol2ForCol1)
}


################################################ PLOTS ###############################################
#----------------------------------------------------------------------------------------------------#
# function: plotSIRDevelopment
#     Creates an SIR development plot. That is, a plot showing the proportions of susceptible,
#     infected, and recovered over the course of an epidemic. Optionally degree and/or density
#     can be added including a second y-axis.
# param:  rsData
#     the round summary data to be plotted
# param:  showLegend
#     flag indicating whether a legend is ought to be included
# param:  showRibbons
#     flag indicating whether ribbons for the lines ought to be included
# param:  showDegree
#     flag indicating whether degree is ought to be plotted
# param:  showDensity
#     flag indicating whether density is ought to be plotted
# param:  showAxes
#     flag indicating whether axes ought to be included
# return: the SIR development plot
#----------------------------------------------------------------------------------------------------#
plotSIRDevelopment <- function(rsData = reduceRoundSummaryData(loadRoundSummaryData()),
                               showLegend = TRUE,
                               showRibbons = TRUE,
                               showDegree = TRUE,
                               showDensity = FALSE,
                               showAxes = TRUE) {

  ### SHARED data
  rounds                <- min(rsData$sim.prop.round):max(rsData$sim.prop.round)

  ### SIR data
  # preparations :: statistical summaries per compartment
  summarySus            <- as.data.frame(do.call(rbind, with(rsData, tapply(dis.prop.pct.sus, sim.prop.round, summary))))
  summaryInf            <- as.data.frame(do.call(rbind, with(rsData, tapply(dis.prop.pct.inf, sim.prop.round, summary))))
  summaryRec            <- as.data.frame(do.call(rbind, with(rsData, tapply(dis.prop.pct.rec, sim.prop.round, summary))))

  # data for lines :: medians for all compartments
  plotData              <- data.frame(rounds, summarySus$Median, summaryInf$Median, summaryRec$Median)
  names(plotData)       <- c("Round", "Susceptible", "Infected", "Recovered")
  plotData              <- melt(plotData, id.vars = "Round")
  names(plotData)       <- c("Timestep", "Measure", "Frequency")

  # data for ribbons :: 1st and 3rd quartile per compartment
  ribbonData            <- data.frame(rounds,
                                      summarySus$`1st Qu.`, summarySus$`3rd Qu.`,
                                      summaryInf$`1st Qu.`, summaryInf$`3rd Qu.`,
                                      summaryRec$`1st Qu.`, summaryRec$`3rd Qu.`)
  names(ribbonData)     <- c("Timestep", "SusMin", "SusMax", "InfMin", "InfMax", "RecMin", "RecMax")

  ### NETWORK PROPERTY data
  if (showDensity) {
    scaleFactor           <- 100
  }
  if (showDegree) {
    scaleFactor           <- 10
  }
  ### NETWORK DENSITY data
  if (showDensity) {
    # preparations :: statistical summary for densities
    summaryDensities    <- as.data.frame(do.call(rbind, with(rsData, tapply(net.prop.density, sim.prop.round, summary))))

    # data for lines :: median
    densityData         <- data.frame(rounds, "Density", summaryDensities$Median * scaleFactor)
    names(densityData)  <- c("Timestep", "Measure", "Frequency")
    plotData            <- rbind(plotData, densityData)

    # data for ribbons :: 1st and 3rd quartile per compartment
    densityRibbonData   <- data.frame(rounds,
                                      summaryDensities$`1st Qu.` * scaleFactor,
                                      summaryDensities$`3rd Qu.` * scaleFactor)
    names(densityRibbonData) <- c("Timestep", "DenMin", "DenMax")
    ribbonData$DenMin   <- densityRibbonData$DenMin[match(ribbonData$Timestep, densityRibbonData$Timestep)]
    ribbonData$DenMax   <- densityRibbonData$DenMax[match(ribbonData$Timestep, densityRibbonData$Timestep)]
  }
  ### DEGREE data
  if (showDegree) {
    # preparations :: statistical summary for degrees
    summaryDegrees      <- as.data.frame(do.call(rbind, with(rsData, tapply(net.prop.av.degree, sim.prop.round, summary))))

    # data for lines :: median
    degreeData          <- data.frame(rounds, "Degree", summaryDegrees$Median * scaleFactor)
    names(degreeData)   <- c("Timestep", "Measure", "Frequency")
    plotData            <- rbind(plotData, degreeData)

    # data for ribbons :: 1st and 3rd quartile per compartment
    degreeRibbonData    <- data.frame(rounds,
                                      summaryDegrees$`1st Qu.` * scaleFactor,
                                      summaryDegrees$`3rd Qu.` * scaleFactor)
    names(degreeRibbonData) <- c("Timestep", "DegMin", "DegMax")
    ribbonData$DegMin   <- degreeRibbonData$DegMin[match(ribbonData$Timestep, degreeRibbonData$Timestep)]
    ribbonData$DegMax   <- degreeRibbonData$DegMax[match(ribbonData$Timestep, degreeRibbonData$Timestep)]
  }


  ### PLOT assembly

  # initializations
  plot <- ggplot(plotData, aes(x = Timestep, y = Frequency, col = Measure))

  # ribbons
  if (showRibbons) {

    # ribbon :: density
    if (showDensity) {
      plot <- plot +
        # density
        geom_ribbon(data = ribbonData,
                    aes(x = Timestep, ymin = DenMin, ymax = DenMax),
                    inherit.aes = FALSE,
                    fill = COLORS["Density"],
                    alpha = RIBBON_ALPHA)
    }

    # ribbon :: degree
    if (showDegree) {
      plot <- plot +
        # average degree
        geom_ribbon(data = ribbonData,
                    aes(x = Timestep, ymin = DegMin, ymax = DegMax),
                    inherit.aes = FALSE,
                    fill = COLORS["Degree"],
                    alpha = RIBBON_ALPHA)
    }
    # ribbons :: susceptible, infected, recovered
    plot <- plot +
      geom_ribbon(data = ribbonData,
                  aes(x = Timestep, ymin = SusMin, ymax = SusMax),
                  inherit.aes = FALSE,
                  fill = COLORS["Susceptible"],
                  alpha = RIBBON_ALPHA) +
      geom_ribbon(data = ribbonData,
                  aes(x = Timestep, ymin = InfMin, ymax = InfMax),
                  inherit.aes = FALSE,
                  fill = COLORS["Infected"],
                  alpha = RIBBON_ALPHA) +
      geom_ribbon(data = ribbonData,
                  aes(x = Timestep, ymin = RecMin, ymax = RecMax),
                  inherit.aes = FALSE,
                  fill = COLORS["Recovered"],
                  alpha = RIBBON_ALPHA)
  }

  # lines
  plot <- plot +
    geom_line(show.legend = showLegend) +
    scale_color_manual(values = COLORS) +
    scale_x_continuous(breaks = seq(min(rounds) - 1, max(rounds), by = 10))

  # axes
  if (!showAxes) {
    plot <- plot +
      labs(x=NULL, y=NULL, title=NULL) +
      theme(axis.title = element_blank(),
            axis.text = element_blank(),
            axis.ticks = element_blank(),
            axis.ticks.length = unit(0, "mm"),
            plot.margin=grid::unit(c(0,0,0,0), "mm"))
  } else {
    if (showDegree & showDensity) {
      warning("Degree and density are on different scales! Values of second y-axis is adjusted to degree!")
      plot <- plot +
        scale_y_continuous(sec.axis = sec_axis(~./scaleFactor, name = "Degree / density"))
    } else if (showDegree) {
      plot <- plot +
        scale_y_continuous(sec.axis = sec_axis(~./scaleFactor, name = "Degree"))
    } else if (showDensity) {
      plot <- plot +
        scale_y_continuous(sec.axis = sec_axis(~./scaleFactor, name = "Density"))
    }
  }

  return(plot)
}


############################################ PLOT EXPORTS ############################################
#----------------------------------------------------------------------------------------------------#
# function: exportPlots
#     Exports plots for visual inspection of selected interaction effects.
# param:  rsData
#     the round summary data to be plotted
# param:  showLegend
#     flag indicating whether a legend is ought to be included
# param:  showRibbons
#     flag indicating whether ribbons for the lines ought to be included
# param:  showDegree
#     flag indicating whether degree is ought to be plotted
# param:  showDensity
#     flag indicating whether density is ought to be plotted
# param:  showAxes
#     flag indicating whether axes ought to be included
# param:  filePrefix
#     prefix for file name
# param:  plotWidth
#     width of plot in EXPORT_SIZE_UNITS (see CONSTANTS)
# param:  plotHeight
#     height of plot in EXPORT_SIZE_UNITS (see CONSTANTS)
#----------------------------------------------------------------------------------------------------#
exportPlots <- function(rsData = reduceRoundSummaryData(loadRoundSummaryData(), cropTail = 20),
                        showLegend = TRUE,
                        showRibbons = TRUE,
                        showDegree = TRUE,
                        showDensity = FALSE,
                        showAxes = TRUE,
                        filePrefix = "individual.",
                        plotWidth = EXPORT_PLOT_WIDTH,
                        plotHeight = EXPORT_PLOT_HEIGHT) {

  # 1. care factor (mu) by denefit of indirect connections (beta)
  subsetsBetaMu <- subsetsByColumnValues(data = rsData,
                                         col1 = 'net.param.beta',
                                         col2 = 'dis.param.mu')

  for (subsetBetaMu in subsetsBetaMu) {
    plot <- plotSIRDevelopment(subsetBetaMu, showLegend, showRibbons, showDegree, showDensity, showAxes)

    filepath <- paste(EXPORT_PATH_PLOTS,
                      filePrefix,
                      "beta", unique(subsetBetaMu$net.param.beta),
                      "-mu", unique(subsetBetaMu$dis.param.mu),
                      EXPORT_FILE_EXTENSION_PLOTS,
                      sep = "")
    print(paste(".. exporting: ", filepath))
    ggsave(filepath,
           plot,
           width = plotWidth,
           height = plotHeight,
           units = EXPORT_SIZE_UNITS,
           dpi = EXPORT_DPI,
           device = EXPORT_FILE_TYPE_PLOTS)
  }

  # 2. disease severity (sigma) by risk behavior (r)
  subsetsRS <- subsetsByColumnValues(data = rsData,
                                     col1 = 'net.param.r',
                                     col2 = 'dis.param.s')

  for (subsetRS in subsetsRS) {
    plot <- plotSIRDevelopment(subsetRS, showLegend, showRibbons, showDegree, showDensity, showAxes)

    filepath <- paste(EXPORT_PATH_PLOTS,
                      filePrefix,
                      "r", unique(subsetRS$net.param.r),
                      "-sigma", unique(subsetRS$dis.param.s),
                      EXPORT_FILE_EXTENSION_PLOTS,
                      sep = "")
    print(paste(".. exporting: ", filepath))
    ggsave(filepath,
           plot,
           width = plotWidth,
           height = plotHeight,
           units = EXPORT_SIZE_UNITS,
           dpi = EXPORT_DPI,
           device = EXPORT_FILE_TYPE_PLOTS)
  }

}

#----------------------------------------------------------------------------------------------------#
# function: exportGridPlots
#     Exports plots of selected interaction effects for grid assembly (no axes and labels).
#----------------------------------------------------------------------------------------------------#
exportGridPlots <- function() {
  exportPlots(
    rsData = reduceRoundSummaryData(loadRoundSummaryData(), cropTail = 20),
    showLegend = FALSE,
    showRibbons = TRUE,
    showDegree = TRUE,
    showDensity = FALSE,
    showAxes = FALSE,
    filePrefix = "grid.",
    plotWidth = 50,
    plotHeight = 35)
}


######################################### REGRESSION EXPORT ##########################################
#----------------------------------------------------------------------------------------------------#
# function: exportModels
#     Creates file outputs for regression models (comparison of models, anova, ICCs).
# param:  models
#     the models to create outputs for
#         filename:
#     the name of the output file
#----------------------------------------------------------------------------------------------------#
exportModels <- function(models, filename) {

  filepath <- paste(EXPORT_PATH_NUM,
                    filename,
                    EXPORT_FILE_EXTENSION_REG,
                    sep = "")

  htmlreg(models,
          filepath,
          inline.css = FALSE,
          doctype = TRUE,
          html.tag = TRUE,
          head.tag = TRUE,
          body.tag = TRUE)
}

#----------------------------------------------------------------------------------------------------#
# function: exportAttackRateModels
#     Exports regression models for attack rate.
# param:  models
#     the models to create outputs for
#----------------------------------------------------------------------------------------------------#
exportAttackRateModels <- function(models = getAttackRateModels()) {
  exportModels(models, "attack-rate")
}

#----------------------------------------------------------------------------------------------------#
# function: exportAttackRateLinearityChecks
#     Exports regression linearity checks for attack rate.
# param:  models
#     the models to create linearity checks for
#----------------------------------------------------------------------------------------------------#
exportAttackRateLinearityChecks <- function(models = getAttackRateLinearityChecks()) {
  exportModels(models, "attack-rate-linearity-checks")
}

#----------------------------------------------------------------------------------------------------#
# function: exportDurationModels
#     Exports regression models for duration of epidemics.
# param:  models
#     the models to create outputs for
#----------------------------------------------------------------------------------------------------#
exportDurationModels <- function(models = getDurationModels()) {
  exportModels(models, "duration-regressions")
}

#----------------------------------------------------------------------------------------------------#
# function: exportDurationLinearityChecks
#     Exports regression linearity checks for duration of epidemics.
# param:  models
#     the models to create linearity checks for
#----------------------------------------------------------------------------------------------------#
exportDurationLinearityChecks <- function(models = getDurationLinearityChecks()) {
  exportModels(models, "duration-linearity-checks")
}


######################################### REGRESSION MODELS ##########################################
#----------------------------------------------------------------------------------------------------#
# function: getAttackRateModels
#     Creates multi-level logistic regression models for attack rate.
# param:  ssData
#     simulation summary data to get regression models for
# return: multi-level logistic regression models for attack rate
#----------------------------------------------------------------------------------------------------#
getAttackRateModels <- function(ssData = loadSimulationSummaryData()) {

  # main effects
  N     <- meanCenter(ssData$net.param.N / 50)
  beta  <- meanCenter(ssData$net.param.beta)
  mu    <- meanCenter(ssData$dis.param.mu)
  r     <- meanCenter(ssData$net.param.r)
  s     <- meanCenter(ssData$dis.param.s / 50)
  deg1  <- meanCenter(ssData$act.prop.net.degree.order.1 / (ssData$net.param.N-1))
  dens  <- meanCenter(ssData$net.prop.density.pre.epidemic)
  # interaction effects
  intNBeta      <- (N - mean(N))        *   (beta - mean(beta))
  intNMu        <- (N - mean(N))        *   (mu - mean(mu))
  intNR         <- (N - mean(N))        *   (r - mean(r))
  intNS         <- (N - mean(N))        *   (s - mean(s))
  intNDeg1      <- (N - mean(N))        *   (deg1 - mean(deg1))
  intNDens      <- (N - mean(N))        *   (dens - mean(dens))
  intBetaMu     <- (beta - mean(beta))  *   (mu - mean(mu))
  intBetaR      <- (beta - mean(beta))  *   (r - mean(r))
  intBetaS      <- (beta - mean(beta))  *   (s - mean(s))
  intBetaDeg1   <- (beta - mean(beta))  *   (deg1 - mean(deg1))
  intBetaDens   <- (beta - mean(beta))  *   (dens - mean(dens))
  intMuR        <- (mu - mean(mu))      *   (r - mean(r))
  intMuS        <- (mu - mean(mu))      *   (s - mean(s))
  intMuDeg1     <- (mu - mean(mu))      *   (deg1 - mean(deg1))
  intMuDens     <- (mu - mean(mu))      *   (dens - mean(dens))
  intRS         <- (r - mean(r))        *   (s - mean(s))
  intRDeg1      <- (r - mean(r))        *   (deg1 - mean(deg1))
  intRDens      <- (r - mean(r))        *   (dens - mean(dens))
  intSDeg1      <- (s - mean(s))        *   (deg1 - mean(deg1))
  intSDens      <- (s - mean(s))        *   (dens - mean(dens))
  intDeg1Dens   <- (deg1 - mean(deg1))  *   (dens - mean(dens))

  ### 2-LEVEL LOGISTIC REGRESSIONS    ###
  ### level 2: parameters combination ###
  ### level 1: simulation runs        ###
  # Null-Modell
  reg00 <- glmer(ssData$dis.prop.pct.rec/100 ~
                   1 +
                   (1 | sim.param.upc),
                 family = binomial,
                 data = ssData)
  # Random intercepts
  reg1Main <- glmer(ssData$dis.prop.pct.rec/100 ~
                      #  model parameters
                      beta + mu + s + r + N +
                      (1 | sim.param.upc),
                    family = binomial,
                    data = ssData)
  reg2Main <- glmer(ssData$dis.prop.pct.rec/100 ~
                      #  model parameters
                      beta + mu + s + r + N +
                      # network properties
                      dens + deg1 +
                      (1 | sim.param.upc),
                    family = binomial,
                    data = ssData)
  reg2Int <- glmer(ssData$dis.prop.pct.rec/100 ~
                     #  model parameters
                     beta + mu + s + r + N +
                     # network properties
                     dens + deg1 +
                     # interaction effects
                     intBetaMu +
                     intMuS +
                     intMuR +
                     intRS +
                     intRDens +
                     intNDeg1 +
                     (1 | sim.param.upc),
                   family = binomial,
                   data = ssData)

  return(list(reg00,reg1Main,reg2Main,reg2Int))

}

#----------------------------------------------------------------------------------------------------#
# function: getAttackRateLinearityChecks
#     Creates linearity checks for multi-level logistic regression models for attack rate.
# param:  ssData
#     simulation summary data to get regression models for
# return: linearity checks for multi-level logistic regression models for attack rate
#----------------------------------------------------------------------------------------------------#
getAttackRateLinearityChecks <- function(ssData = loadSimulationSummaryData()) {

  # main effects
  N     <- ssData$net.param.N
  beta  <- ssData$net.param.beta
  mu    <- ssData$dis.param.mu
  r     <- ssData$net.param.r
  s     <- ssData$dis.param.s
  # dummies
  N10   <- ifelse(N == 10, 10, 0)
  N15   <- ifelse(N == 15, 15, 0)
  N20   <- ifelse(N == 20, 20, 0)
  N25   <- ifelse(N == 25, 25, 0)
  r1.0  <- ifelse(r == 1.0, 1.0, 0)
  r1.5  <- ifelse(r == 1.5, 1.5, 0)
  s10   <- ifelse(s == 10, 10, 0)
  s50   <- ifelse(s == 50, 50, 0)

  ### 2-LEVEL LOGISTIC REGRESSIONS    ###
  ### level 2: parameters combination ###
  ### level 1: simulation runs        ###
  # Null-Modell
  reg00 <- glmer(ssData$dis.prop.pct.rec/100 ~
                   1 +
                   (1 | sim.param.upc),
                 family = binomial,
                 data = ssData)
  # Random intercepts
  reg1Main <- glmer(ssData$dis.prop.pct.rec/100 ~
                      #  model parameters
                      beta + mu + s + r + N +
                      (1 | sim.param.upc),
                    family = binomial,
                    data = ssData)
  # Linearity checks
  reg1LCheck <- glmer(ssData$dis.prop.pct.rec/100 ~
                        #  model parameters
                        beta + mu + s10 + s50 + r1.0 + r1.5 + N10 + N15 + N20 + N25 +
                        (1 | sim.param.upc),
                      family = binomial,
                      data = ssData)

  return(list(reg00,reg1Main,reg1LCheck))

}

#----------------------------------------------------------------------------------------------------#
# function: getDurationLinearityChecks
#     Creates linearity checks for multi-level linear regression models for duration of epidemics.
# param:  ssData
#     simulation summary data to get regression models for
# return: linearity checks for multi-level linear regression models for duration of epidemics
#----------------------------------------------------------------------------------------------------#
getDurationLinearityChecks <- function(ssData = loadSimulationSummaryData()) {

  # main effects
  N     <- ssData$net.param.N
  beta  <- ssData$net.param.beta
  mu    <- ssData$dis.param.mu
  r     <- ssData$net.param.r
  s     <- ssData$dis.param.s
  # dummies
  N10   <- ifelse(N == 10, 10, 0)
  N15   <- ifelse(N == 15, 15, 0)
  N20   <- ifelse(N == 20, 20, 0)
  N25   <- ifelse(N == 25, 25, 0)
  r1.0  <- ifelse(r == 1.0, 1.0, 0)
  r1.5  <- ifelse(r == 1.5, 1.5, 0)
  s10   <- ifelse(s == 10, 10, 0)
  s50   <- ifelse(s == 50, 50, 0)

  ### 2-LEVEL LINEAR REGRESSIONS      ###
  ### level 2: parameters combination ###
  ### level 1: simulation runs        ###
  # Null-Modell
  reg00 <- lmer(ssData$dis.prop.duration
                ~ 1 +
                  (1 | sim.param.upc),
                data = ssData,
                REML = FALSE)
  # Random intercepts
  reg1Main <- lmer(ssData$dis.prop.duration ~
                     #  model parameters
                     beta + mu + s + r + N +
                     (1 | sim.param.upc),
                   data = ssData,
                   REML = FALSE)
  # Linearity checks
  reg1LCheck <- lmer(ssData$dis.prop.duration ~
                       #  model parameters
                       beta + mu + s10 + s50 + r1.0 + r1.5 + N + #N10 + N15 + N20 + N25 +
                       (1 | sim.param.upc),
                     data = ssData,
                     REML = FALSE)

  return(list(reg00,reg1Main,reg1LCheck))
}

#----------------------------------------------------------------------------------------------------#
# function: getDurationModels
#     Creates multi-level linear regression models for duration of epidemics.
# param:  ssData
#     simulation summary data to get regression models for
# return: multi-level linear regression models for duration of epidemics
#----------------------------------------------------------------------------------------------------#
getDurationModels <- function(ssData = loadSimulationSummaryData()) {

  # main effects
  N     <- meanCenter(ssData$net.param.N / 50)
  beta  <- meanCenter(ssData$net.param.beta)
  mu    <- meanCenter(ssData$dis.param.mu)
  r     <- meanCenter(ssData$net.param.r)
  s     <- meanCenter(ssData$dis.param.s / 50)
  deg1  <- meanCenter(ssData$act.prop.net.degree.order.1 / (ssData$net.param.N-1))
  dens  <- meanCenter(ssData$net.prop.density.pre.epidemic)
  # interaction effects
  intNBeta      <- (N - mean(N))        *   (beta - mean(beta))
  intNMu        <- (N - mean(N))        *   (mu - mean(mu))
  intNR         <- (N - mean(N))        *   (r - mean(r))
  intNS         <- (N - mean(N))        *   (s - mean(s))
  intNDeg1      <- (N - mean(N))        *   (deg1 - mean(deg1))
  intNDens      <- (N - mean(N))        *   (dens - mean(dens))
  intBetaMu     <- (beta - mean(beta))  *   (mu - mean(mu))
  intBetaR      <- (beta - mean(beta))  *   (r - mean(r))
  intBetaS      <- (beta - mean(beta))  *   (s - mean(s))
  intBetaDeg1   <- (beta - mean(beta))  *   (deg1 - mean(deg1))
  intBetaDens   <- (beta - mean(beta))  *   (dens - mean(dens))
  intMuR        <- (mu - mean(mu))      *   (r - mean(r))
  intMuS        <- (mu - mean(mu))      *   (s - mean(s))
  intMuDeg1     <- (mu - mean(mu))      *   (deg1 - mean(deg1))
  intMuDens     <- (mu - mean(mu))      *   (dens - mean(dens))
  intRS         <- (r - mean(r))        *   (s - mean(s))
  intRDeg1      <- (r - mean(r))        *   (deg1 - mean(deg1))
  intRDens      <- (r - mean(r))        *   (dens - mean(dens))
  intSDeg1      <- (s - mean(s))        *   (deg1 - mean(deg1))
  intSDens      <- (s - mean(s))        *   (dens - mean(dens))
  intDeg1Dens   <- (deg1 - mean(deg1))  *   (dens - mean(dens))

  ### 2-LEVEL LINEAR REGRESSIONS      ###
  ### level 2: parameters combination ###
  ### level 1: simulation runs        ###
  # Null-Modell
  reg00 <- lmer(ssData$dis.prop.duration
                ~ 1 +
                  (1 | sim.param.upc),
                data = ssData,
                REML = FALSE)
  # Random intercepts
  reg1Main <- lmer(ssData$dis.prop.duration ~
                     #  model parameters
                     beta + mu + s + r + N +
                     (1 | sim.param.upc),
                   data = ssData,
                   REML = FALSE)
  reg2Main <- lmer(ssData$dis.prop.duration ~
                     #  model parameters
                     beta + mu + s + r + N +
                     # network properties
                     dens + deg1 +
                     (1 | sim.param.upc),
                   data = ssData,
                   REML = FALSE)
  reg2Int <- lmer(ssData$dis.prop.duration ~
                    #  model parameters
                    beta + mu + s + r + N +
                    # network properties
                    dens + deg1 +
                    # interaction effects
                    intBetaMu +
                    intNBeta +
                    intMuDens +
                    intRS +
                    intNS +
                    intNR +
                    intNDens +
                    (1 | sim.param.upc),
                  data = ssData,
                  REML = FALSE)

  return(list(reg00,reg1Main,reg2Main,reg2Int))

}


exportDataFrame <- function(df, filename) {

  filepath <- paste(EXPORT_PATH_NUM,
                    filename,
                    EXPORT_FILE_EXTENSION_DF,
                    sep = "")

  write.table(df, file=filepath, sep = ";", row.names = TRUE, col.names = NA)
}

getNetworkSizeMeasures <- function() {

  ssData <- loadSimulationSummaryData()
  rsData  <- reduceRoundSummaryData(loadRoundSummaryData(), cropTail = 20)
  peakData <- data.frame(rsData$net.param.N,
                         rsData$sim.param.uid,
                         rsData$sim.prop.round-10,
                         rsData$dis.prop.pct.inf)
  colnames(peakData) <- c("n", "uid", "round", "inf")
  peakData <- subset(peakData, round > 0)

  netSizes <- c()
  densities <- c()
  sdDensities <- c()
  degrees <- c()
  sdDegrees <- c()
  attackRates <- c()
  sdAttackRates <- c()
  durations <- c()
  sdDurations <- c()
  epiPeaks <- c()
  sdEpiPeaks <- c()

  for (N in unique(ssData$net.param.N)) {
    ssN <- subset(ssData, net.param.N == N)
    # network size
    netSizes <- c(netSizes, N)
    # density
    densities <- c(densities, round(mean(ssN$net.prop.density.pre.epidemic), 3))
    sdDensities <- c(sdDensities, round(sd(ssN$net.prop.density.pre.epidemic), 3))
    # degree
    degrees <- c(degrees, round(mean(ssN$net.prop.av.degree.pre.epidemic), 3))
    sdDegrees <- c(sdDegrees, round(sd(ssN$net.prop.av.degree.pre.epidemic), 3))
    # attack rate
    attackRates <- c(attackRates, round(mean(ssN$dis.prop.pct.rec), 3))
    sdAttackRates <- c(sdAttackRates, round(sd(ssN$dis.prop.pct.rec), 3))
    # duration
    durations <- c(durations, round(mean(ssN$dis.prop.duration), 3))
    sdDurations <- c(sdDurations, round(sd(ssN$dis.prop.duration), 3))
    # epidemic peaks
    # gives the relative number of the row (here: 'inf') per uid ('uid') for the maximum number of infected
    # the relative number of row corresponds to the time step ('round') of the simulation
    peaks <- aggregate(inf ~ uid, data = subset(peakData, n == N), which.max)$inf
    epiPeaks <- c(epiPeaks, round(mean(peaks), 3))
    sdEpiPeaks <- c(sdEpiPeaks, round(sd(peaks), 3))
  }

  nsMeasures <- data.frame(netSizes,
                           densities, sdDensities,
                           degrees, sdDegrees,
                           attackRates, sdAttackRates,
                           durations, sdDurations,
                           epiPeaks, sdEpiPeaks)
  return(nsMeasures)

}

exportNetworkSizeMeasures <- function() {
  exportDataFrame(getNetworkSizeMeasures(), "network-size-measures")
}




############################################ DESCRIPIVES #############################################
#----------------------------------------------------------------------------------------------------#
# function: exportDescriptiveMeasures
#     Exports descriptive statistics for selected plot properties.y
#----------------------------------------------------------------------------------------------------#
exportDescriptiveMeasures <- function() {

  # INITIALIZATIONS
  # data
  ssData <- loadSimulationSummaryData()
  rsData <- reduceRoundSummaryData(loadRoundSummaryData(), cropTail = 20)

  ##### BETA-MU #####
  # results data frame
  resBetaMu <- data.frame(matrix(0, ncol= 6, nrow=length(unique(ssData$net.param.beta))*length(unique(ssData$dis.param.mu))))
  colnames(resBetaMu) <- c("beta", "mu", "attack.rate", "duration", "degree.pre.epidemic", "degree.min")

  # ATTACK RATE, DURATION
  ssBetaMu <- subsetsByColumnValues(data = ssData,
                                    col1 = 'net.param.beta',
                                    col2 = 'dis.param.mu')
  for (i in 1:length(ssBetaMu)) {
    ss <- ssBetaMu[[i]]
    resBetaMu[i,1]  <- head(ss, 1)$net.param.beta
    resBetaMu[i,2]  <- head(ss, 1)$dis.param.mu
    resBetaMu[i,3]  <- median(ss$dis.prop.pct.rec)
    resBetaMu[i,4]  <- median(ss$dis.prop.duration)
  }

  # AVERAGE DEGREES
  rsBetaMu <- subsetsByColumnValues(data = rsData,
                                    col1 = 'net.param.beta',
                                    col2 = 'dis.param.mu')
  for (rs in rsBetaMu) {
    # average degree prior to epidemic
    resBetaMu$degree.pre.epidemic[
      resBetaMu$beta == head(rs, 1)$net.param.beta
      & resBetaMu$mu == head(rs, 1)$dis.param.mu
      ] <- median(subset(rs, sim.prop.round == 10)$net.prop.av.degree)
    # average minimum degree during epidemic
    resBetaMu$degree.min[
      resBetaMu$beta == head(rs, 1)$net.param.beta
      & resBetaMu$mu == head(rs, 1)$dis.param.mu
      ] <- median(aggregate(net.prop.av.degree ~ sim.param.uid, data = rs, min)$net.prop.av.degree)
  }
  exportDataFrame(resBetaMu, "descriptives-beta-mu")


  ##### SIGMA-R #####
  # results data frame
  resSigmaR <- data.frame(matrix(0, ncol= 6, nrow=length(unique(ssData$dis.param.s))*length(unique(ssData$net.param.r))))
  colnames(resSigmaR) <- c("sigma", "risk.factor", "attack.rate", "duration", "degree.pre.epidemic", "degree.min")

  # ATTACK RATE, DURATION
  ssSigmaR <- subsetsByColumnValues(data = ssData,
                                    col1 = 'dis.param.s',
                                    col2 = 'net.param.r')
  for (i in 1:length(ssSigmaR)) {
    ss <- ssSigmaR[[i]]
    resSigmaR[i,1]  <- head(ss, 1)$dis.param.s
    resSigmaR[i,2]  <- head(ss, 1)$net.param.r
    resSigmaR[i,3]  <- median(ss$dis.prop.pct.rec)
    resSigmaR[i,4]  <- median(ss$dis.prop.duration)
  }

  # AVERAGE DEGREES
  rsSigmaR <- subsetsByColumnValues(data = rsData,
                                    col1 = 'dis.param.s',
                                    col2 = 'net.param.r')
  for (rs in rsSigmaR) {
    # average degree prior to epidemic
    resSigmaR$degree.pre.epidemic[
      resSigmaR$sigma == head(rs, 1)$dis.param.s
      & resSigmaR$risk.factor == head(rs, 1)$net.param.r
      ] <- median(subset(rs, sim.prop.round == 10)$net.prop.av.degree)
    # average minimum degree during epidemic
    resSigmaR$degree.min[
      resSigmaR$sigma == head(rs, 1)$dis.param.s
      & resSigmaR$risk.factor == head(rs, 1)$net.param.r
      ] <- median(aggregate(net.prop.av.degree ~ sim.param.uid, data = rs, min)$net.prop.av.degree)
  }
  exportDataFrame(resSigmaR, "descriptives-sigma-r")
}


########################################## SCRIPT COMPOSITION ########################################
print("###########################################################")
print(paste("Creating directory for figure export:", EXPORT_PATH_PLOTS))
dir.create(EXPORT_PATH_PLOTS)

print("###########################################################")
print("Starting to export individual plots..")
exportPlots()
print("Finished exporting individual plots..")

print("###########################################################")
print("Starting to export grid plots..")
exportGridPlots()
print("Finished exporting grid plots..")

print("###########################################################")
print(paste("Creating directory for numerical analyses export:", EXPORT_PATH_NUM))
dir.create(EXPORT_PATH_NUM)

print("###########################################################")
print("Starting to export regression models for attack rate..")
exportAttackRateModels()
print("Finished exporting regression models for attack rate..")

print("###########################################################")
print("Starting to export regression models linearity checks for attack rate..")
exportAttackRateLinearityChecks()
print("Finished exporting regression models linearity checks for attack rate..")

print("###########################################################")
print("Starting to export regression models for duration of epidemics..")
exportDurationModels()
print("Finished exporting regression models for duration of epidemics..")

print("###########################################################")
print("Starting to export regression models linearity checks for duration of epidemics..")
exportDurationLinearityChecks()
print("Finished exporting regression models linearity checks for duration of epidemics..")

print("###########################################################")
print("Starting to export network size measures..")
exportNetworkSizeMeasures()
print("Finished exporting network size measures..")

print("###########################################################")
print("Starting to export descriptive measures..")
exportDescriptiveMeasures()
print("Finished exporting descriptive measures..")

return(0)
