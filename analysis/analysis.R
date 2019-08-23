#!/usr/bin/env Rscript


############################################## LIBRARIES ##############################################
sourceLibs <- function(libs) {
  for (lib in libs) {
    if(lib %in% rownames(installed.packages()) == FALSE) {install.packages(lib)}
    library(lib, character.only = TRUE)
  }
}
sourceLibs(c("reshape2",    # 'melt' function
             "ggplot2",     # all plots
             "QuantPsyc",   # 'meanCenter' function
             "lme4",        # regression analyses
             "sjstats",     # "icc" function
             "texreg"       # html export
             )
           )

########################################### GLOBAL CONSTANTS ##########################################
# input/output directory and
DATA_PATH                   <- ""
args = commandArgs(trailingOnly=TRUE)
if (length(args) == 0) {
  DATA_DIR                  <- "20190823-171943"
  DATA_PATH                 <- paste(dirname(sys.frame(1)$ofile), "/../data/", DATA_DIR, "/", sep = "")
} else {
  DATA_PATH                 <- paste(args[1], "/", sep = "")
}
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
#     Loads summary data for NIDM simulations.
# return: the summary data for NIDM simulations
#----------------------------------------------------------------------------------------------------#
loadSimulationSummaryData <- function() {
  return(loadCSV(CSV_SUMMARY_PATH))
}

#----------------------------------------------------------------------------------------------------#
# function: loadRoundSummaryData
#     Loads summary data for all simulated NIDM rounds.
# return: the summary data for all simulated NIDM rounds
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
  roundsToDrop <- max(subset(rsData, prop.sim.stage == 'pre-epidemic')$prop.sim.round) - 10
  # reduce overall amount of rounds
  reducedRsData$prop.sim.round <- reducedRsData$prop.sim.round - roundsToDrop
  # drop all rounds with ids lower than 1
  reducedRsData <- subset(reducedRsData, prop.sim.round > 0)

  # 2. drop rounds of post-epidemic stage
  postEpidemicData <- subset(reducedRsData, prop.sim.stage == "post-epidemic")
  # deterimine the round when post-epidemic stage begins for each simulation
  roundsPerSimulation <- postEpidemicData[!duplicated(postEpidemicData$param.sim.uid),]$prop.sim.round
  # take the latest round minus a threshold
  roundsToPlot <- max(roundsPerSimulation) - cropTail
  # increase until the amount of rounds is dividable by 10, so that plots can have
  # ticks at a stepsize of 10
  while (roundsToPlot %% 10 != 0) {
    roundsToPlot <- roundsToPlot + 1
  }
  reducedRsData <- subset(reducedRsData, prop.sim.round <= roundsToPlot)
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
subsetsByColumnValues <- function(data, col1, col2=NA, col3=NA) {

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

  # subsets of column 2 for previous subsets
  subsetsCol2 <- list()
  i <- 1
  for (subsetCol1 in subsetsCol1) {
    for (subsetCol2 in subsetsByColumnValues(subsetCol1, col2)) {
      subsetsCol2[[i]] <- subsetCol2
      i <- i + 1
    }
  }

  if (is.na(col3)) {
    return(subsetsCol2)
  }

  # subsets of column 3 for previous subsets
  subsetsCol3 <- list()
  i <- 1
  for (subsetCol2 in subsetsCol2) {
    for (subsetCol3 in subsetsByColumnValues(subsetCol2, col3)) {
      subsetsCol3[[i]] <- subsetCol3
      i <- i + 1
    }
  }

  return(subsetsCol3)
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
  rounds                <- min(rsData$prop.sim.round):max(rsData$prop.sim.round)

  ### SIR data
  # preparations :: statistical summaries per compartment
  summarySus            <- as.data.frame(do.call(rbind, with(rsData, tapply(prop.net.pct.sus, prop.sim.round, summary))))
  summaryInf            <- as.data.frame(do.call(rbind, with(rsData, tapply(prop.net.pct.inf, prop.sim.round, summary))))
  summaryRec            <- as.data.frame(do.call(rbind, with(rsData, tapply(prop.net.pct.rec, prop.sim.round, summary))))

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
    summaryDensities    <- as.data.frame(do.call(rbind, with(rsData, tapply(prop.net.density, prop.sim.round, summary))))

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
    summaryDegrees      <- as.data.frame(do.call(rbind, with(rsData, tapply(prop.net.av.degree, prop.sim.round, summary))))

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

  # create directory if necessary
  dir.create(EXPORT_PATH_PLOTS, showWarnings = FALSE)

  # 1. care factor (mu) by denefit of indirect connections (beta)
  subsetsBetaMu <- subsetsByColumnValues(data = rsData,
                                         col1 = 'param.cidm.beta.av',
                                         col2 = 'param.cidm.mu.av')
  for (subsetBetaMu in subsetsBetaMu) {
    plot <- plotSIRDevelopment(subsetBetaMu, showLegend, showRibbons, showDegree, showDensity, showAxes)

    filepath <- paste(EXPORT_PATH_PLOTS,
                      filePrefix,
                      "beta", unique(subsetBetaMu$param.cidm.beta.av),
                      "-mu", unique(subsetBetaMu$param.cidm.mu.av),
                      EXPORT_FILE_EXTENSION_PLOTS,
                      sep = "")
    ggsave(filepath,
           plot,
           width = plotWidth,
           height = plotHeight,
           units = EXPORT_SIZE_UNITS,
           dpi = EXPORT_DPI,
           device = EXPORT_FILE_TYPE_PLOTS)
    print(paste(":::::::: Export of ", filepath, " succesful.", sep = ""))
  }

  # 2. disease severity (sigma) by risk behavior (r)
  if (unique(rsData$param.cidm.rs.equal) == 1) {
    # r_sigma == r_pi :: only one set of plots
    subsetsRS <- subsetsByColumnValues(data = rsData,
                                       col1 = 'param.cidm.r.sigma.av',
                                       col2 = 'param.cidm.sigma.av')
    for (subsetRS in subsetsRS) {
      plot <- plotSIRDevelopment(subsetRS, showLegend, showRibbons, showDegree, showDensity, showAxes)

      filepath <- paste(EXPORT_PATH_PLOTS,
                        filePrefix,
                        "r", unique(subsetRS$param.cidm.r.sigma.av),
                        "-sigma", unique(subsetRS$param.cidm.sigma.av),
                        EXPORT_FILE_EXTENSION_PLOTS,
                        sep = "")
      ggsave(filepath,
             plot,
             width = plotWidth,
             height = plotHeight,
             units = EXPORT_SIZE_UNITS,
             dpi = EXPORT_DPI,
             device = EXPORT_FILE_TYPE_PLOTS)

      print(paste(":::::::: Export of ", filepath, " succesful.", sep = ""))
    }

  } else {
    # r_sigma != r_pi :: two sets of plots
    # 2.1. disease severity (sigma) by risk behavior (r_sigma)
    subsetsRS <- subsetsByColumnValues(data = rsData,
                                       col1 = 'param.cidm.r.sigma.av',
                                       col2 = 'param.cidm.sigma.av')
    for (subsetRS in subsetsRS) {
      plot <- plotSIRDevelopment(subsetRS, showLegend, showRibbons, showDegree, showDensity, showAxes)

      filepath <- paste(EXPORT_PATH_PLOTS,
                        filePrefix,
                        "rsigma", unique(subsetRS$param.cidm.r.sigma.av),
                        "-sigma", unique(subsetRS$param.cidm.sigma.av),
                        EXPORT_FILE_EXTENSION_PLOTS,
                        sep = "")
      ggsave(filepath,
             plot,
             width = plotWidth,
             height = plotHeight,
             units = EXPORT_SIZE_UNITS,
             dpi = EXPORT_DPI,
             device = EXPORT_FILE_TYPE_PLOTS)

      print(paste(":::::::: Export of ", filepath, " succesful.", sep = ""))
    }

    # 2.2. disease severity (sigma) by risk behavior (r_pi)
    subsetsRS <- subsetsByColumnValues(data = rsData,
                                       col1 = 'param.cidm.r.pi.av',
                                       col2 = 'param.cidm.sigma.av')
    for (subsetRS in subsetsRS) {
      plot <- plotSIRDevelopment(subsetRS, showLegend, showRibbons, showDegree, showDensity, showAxes)

      filepath <- paste(EXPORT_PATH_PLOTS,
                        filePrefix,
                        "rpi", unique(subsetRS$param.cidm.r.pi.av),
                        "-sigma", unique(subsetRS$param.cidm.sigma.av),
                        EXPORT_FILE_EXTENSION_PLOTS,
                        sep = "")
      ggsave(filepath,
             plot,
             width = plotWidth,
             height = plotHeight,
             units = EXPORT_SIZE_UNITS,
             dpi = EXPORT_DPI,
             device = EXPORT_FILE_TYPE_PLOTS)

      print(paste(":::::::: Export of ", filepath, " succesful.", sep = ""))
    }
  }
}

#----------------------------------------------------------------------------------------------------#
# function: exportGridPlots
#     Exports plots of selected interaction effects for grid assembly (no axes and labels).
#----------------------------------------------------------------------------------------------------#
exportGridPlots <- function(rsData = reduceRoundSummaryData(loadRoundSummaryData(), cropTail = 20)) {
  exportPlots(
    rsData,
    showLegend = FALSE,
    showRibbons = TRUE,
    showDegree = TRUE,
    showDensity = FALSE,
    showAxes = FALSE,
    filePrefix = "grid.",
    plotWidth = 50,
    plotHeight = 35)
}


############################################ REGRESSIONS #############################################
#----------------------------------------------------------------------------------------------------#
# function: exportModels
#     Creates file outputs for regression models (comparison of models, ICCs).
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

  # create directory if necessary
  dir.create(EXPORT_PATH_NUM, showWarnings = FALSE)

  # close standard notes and begin new standard row
  notes <- "</span></td>\n</tr>\n<tr>\n"
  # intraclass correlation coefficients (ICC)
  notes <- paste(notes, "<td class=\"bottomRule\">ICC</td>\n", sep = "")
  for (i in 1:length(models)) {
    notes <- paste(notes, "<td class=\"bottomRule\">",
                   round(icc(models[[i]])[[1]], digits = 3),
                   "</td>\n",
                   sep = "")
  }
  # close additional infos
  notes <- paste(notes, "</span></td>\n</tr>")
  # correlation stars
  notes <- paste(notes,
                 "<tr>\n<td colspan=\"", length(models)+1,
                 "\"><span style=\"font-size:0.8em\">",
                 "<sup>***</sup>p &lt; 0.001, <sup>**</sup>p &lt; 0.01, <sup>*</sup>p &lt; 0.05</span></td>", sep = "")

  htmlreg(models,
          filepath,
          custom.note = notes,
          inline.css = FALSE,
          doctype = TRUE,
          html.tag = TRUE,
          head.tag = TRUE,
          body.tag = TRUE,
          )
}

#----------------------------------------------------------------------------------------------------#
# function: exportAttackRateModelsComplete
#     Creates and exports multi-level logistic regression models for attack rate with all possible
#     parameters and interaction effects.
# param:  ssData
#     simulation summary data to get regression models for
#----------------------------------------------------------------------------------------------------#
exportRegressionModelsComplete <- function(ssData = loadSimulationSummaryData()) {

  # r_sigma == r_pi?
  # MAIN EFFECTS
  # CIDM parameters
  alpha                              <- meanCenter(ssData$param.cidm.alpha.av)
  kappa                              <- meanCenter(ssData$param.cidm.kappa.av)
  beta                               <- meanCenter(ssData$param.cidm.beta.av)
  lamda                              <- meanCenter(ssData$param.cidm.lamda.av)
  c                                  <- meanCenter(ssData$param.cidm.c.av)
  mu                                 <- meanCenter(ssData$param.cidm.mu.av)
  sigma                              <- meanCenter(ssData$param.cidm.sigma.av / 50)
  gamma                              <- meanCenter(ssData$param.cidm.gamma.av)
  r.sigma                            <- meanCenter(ssData$param.cidm.r.sigma.av)
  r.pi                               <- meanCenter(ssData$param.cidm.r.pi.av)
  N                                  <- meanCenter(ssData$param.cidm.N / 50)
  iota                               <- meanCenter(ssData$param.cidm.iota)
  phi                                <- meanCenter(ssData$param.cidm.phi.av)
  tau                                <- meanCenter(ssData$param.cidm.tau.av)
  # network properties
  density                            <- meanCenter(ssData$prop.net.density.pre.epidemic)
  av.degree                          <- meanCenter(ssData$prop.net.av.degree.pre.epidemic)
  av.degree2                         <- meanCenter(ssData$prop.net.av.degree2.pre.epidemic)
  av.closeness                       <- meanCenter(ssData$prop.net.av.closeness.pre.epidemic)
  av.clustering                      <- meanCenter(ssData$prop.net.av.clustering.pre.epidemic)
  agent.degree                       <- meanCenter(ssData$prop.agent.net.degree)
  agent.degree2                      <- meanCenter(ssData$prop.agent.net.degree2)
  agent.closeness                    <- meanCenter(ssData$prop.agent.net.closeness)
  agent.clustering                   <- meanCenter(ssData$prop.agent.net.clustering)

  # INTERACTION EFFECTS
  # combinations of alpha
  alpha.X.kappa                      <- (alpha - mean(alpha)                     *  (kappa - mean(kappa)))
  alpha.X.beta                       <- (alpha - mean(alpha)                     *  (beta - mean(beta)))
  alpha.X.lamda                      <- (alpha - mean(alpha)                     *  (lamda - mean(lamda)))
  alpha.X.c                          <- (alpha - mean(alpha)                     *  (c - mean(c)))
  alpha.X.mu                         <- (alpha - mean(alpha)                     *  (mu - mean(mu)))
  alpha.X.sigma                      <- (alpha - mean(alpha)                     *  (sigma - mean(sigma)))
  alpha.X.gamma                      <- (alpha - mean(alpha)                     *  (gamma - mean(gamma)))
  alpha.X.r.sigma                    <- (alpha - mean(alpha)                     *  (r.sigma - mean(r.sigma)))
  alpha.X.r.pi                       <- (alpha - mean(alpha)                     *  (r.pi - mean(r.pi)))
  alpha.X.N                          <- (alpha - mean(alpha)                     *  (N - mean(N)))
  alpha.X.iota                       <- (alpha - mean(alpha)                     *  (iota - mean(iota)))
  alpha.X.phi                        <- (alpha - mean(alpha)                     *  (phi - mean(phi)))
  alpha.X.tau                        <- (alpha - mean(alpha)                     *  (tau - mean(tau)))
  alpha.X.density                    <- (alpha - mean(alpha)                     *  (density - mean(density)))
  alpha.X.av.degree                  <- (alpha - mean(alpha)                     *  (av.degree - mean(av.degree)))
  alpha.X.av.degree2                 <- (alpha - mean(alpha)                     *  (av.degree2 - mean(av.degree2)))
  alpha.X.av.closeness               <- (alpha - mean(alpha)                     *  (av.closeness - mean(av.closeness)))
  alpha.X.av.clustering              <- (alpha - mean(alpha)                     *  (av.clustering - mean(av.clustering)))
  alpha.X.agent.degree               <- (alpha - mean(alpha)                     *  (agent.degree - mean(agent.degree)))
  alpha.X.agent.degree2              <- (alpha - mean(alpha)                     *  (agent.degree2 - mean(agent.degree2)))
  alpha.X.agent.closeness            <- (alpha - mean(alpha)                     *  (agent.closeness - mean(agent.closeness)))
  alpha.X.agent.clustering           <- (alpha - mean(alpha)                     *  (agent.clustering - mean(agent.clustering)))
  # combinations of kappa
  kappa.X.beta                       <- (kappa - mean(kappa)                     *  (beta - mean(beta)))
  kappa.X.lamda                      <- (kappa - mean(kappa)                     *  (lamda - mean(lamda)))
  kappa.X.c                          <- (kappa - mean(kappa)                     *  (c - mean(c)))
  kappa.X.mu                         <- (kappa - mean(kappa)                     *  (mu - mean(mu)))
  kappa.X.sigma                      <- (kappa - mean(kappa)                     *  (sigma - mean(sigma)))
  kappa.X.gamma                      <- (kappa - mean(kappa)                     *  (gamma - mean(gamma)))
  kappa.X.r.sigma                    <- (kappa - mean(kappa)                     *  (r.sigma - mean(r.sigma)))
  kappa.X.r.pi                       <- (kappa - mean(kappa)                     *  (r.pi - mean(r.pi)))
  kappa.X.N                          <- (kappa - mean(kappa)                     *  (N - mean(N)))
  kappa.X.iota                       <- (kappa - mean(kappa)                     *  (iota - mean(iota)))
  kappa.X.phi                        <- (kappa - mean(kappa)                     *  (phi - mean(phi)))
  kappa.X.tau                        <- (kappa - mean(kappa)                     *  (tau - mean(tau)))
  kappa.X.density                    <- (kappa - mean(kappa)                     *  (density - mean(density)))
  kappa.X.av.degree                  <- (kappa - mean(kappa)                     *  (av.degree - mean(av.degree)))
  kappa.X.av.degree2                 <- (kappa - mean(kappa)                     *  (av.degree2 - mean(av.degree2)))
  kappa.X.av.closeness               <- (kappa - mean(kappa)                     *  (av.closeness - mean(av.closeness)))
  kappa.X.av.clustering              <- (kappa - mean(kappa)                     *  (av.clustering - mean(av.clustering)))
  kappa.X.agent.degree               <- (kappa - mean(kappa)                     *  (agent.degree - mean(agent.degree)))
  kappa.X.agent.degree2              <- (kappa - mean(kappa)                     *  (agent.degree2 - mean(agent.degree2)))
  kappa.X.agent.closeness            <- (kappa - mean(kappa)                     *  (agent.closeness - mean(agent.closeness)))
  kappa.X.agent.clustering           <- (kappa - mean(kappa)                     *  (agent.clustering - mean(agent.clustering)))
  # combinations of beta
  beta.X.lamda                       <- (beta - mean(beta)                       *  (lamda - mean(lamda)))
  beta.X.c                           <- (beta - mean(beta)                       *  (c - mean(c)))
  beta.X.mu                          <- (beta - mean(beta)                       *  (mu - mean(mu)))
  beta.X.sigma                       <- (beta - mean(beta)                       *  (sigma - mean(sigma)))
  beta.X.gamma                       <- (beta - mean(beta)                       *  (gamma - mean(gamma)))
  beta.X.r.sigma                     <- (beta - mean(beta)                       *  (r.sigma - mean(r.sigma)))
  beta.X.r.pi                        <- (beta - mean(beta)                       *  (r.pi - mean(r.pi)))
  beta.X.N                           <- (beta - mean(beta)                       *  (N - mean(N)))
  beta.X.iota                        <- (beta - mean(beta)                       *  (iota - mean(iota)))
  beta.X.phi                         <- (beta - mean(beta)                       *  (phi - mean(phi)))
  beta.X.tau                         <- (beta - mean(beta)                       *  (tau - mean(tau)))
  beta.X.density                     <- (beta - mean(beta)                       *  (density - mean(density)))
  beta.X.av.degree                   <- (beta - mean(beta)                       *  (av.degree - mean(av.degree)))
  beta.X.av.degree2                  <- (beta - mean(beta)                       *  (av.degree2 - mean(av.degree2)))
  beta.X.av.closeness                <- (beta - mean(beta)                       *  (av.closeness - mean(av.closeness)))
  beta.X.av.clustering               <- (beta - mean(beta)                       *  (av.clustering - mean(av.clustering)))
  beta.X.agent.degree                <- (beta - mean(beta)                       *  (agent.degree - mean(agent.degree)))
  beta.X.agent.degree2               <- (beta - mean(beta)                       *  (agent.degree2 - mean(agent.degree2)))
  beta.X.agent.closeness             <- (beta - mean(beta)                       *  (agent.closeness - mean(agent.closeness)))
  beta.X.agent.clustering            <- (beta - mean(beta)                       *  (agent.clustering - mean(agent.clustering)))
  # combinations of lamda
  lamda.X.c                          <- (lamda - mean(lamda)                     *  (c - mean(c)))
  lamda.X.mu                         <- (lamda - mean(lamda)                     *  (mu - mean(mu)))
  lamda.X.sigma                      <- (lamda - mean(lamda)                     *  (sigma - mean(sigma)))
  lamda.X.gamma                      <- (lamda - mean(lamda)                     *  (gamma - mean(gamma)))
  lamda.X.r.sigma                    <- (lamda - mean(lamda)                     *  (r.sigma - mean(r.sigma)))
  lamda.X.r.pi                       <- (lamda - mean(lamda)                     *  (r.pi - mean(r.pi)))
  lamda.X.N                          <- (lamda - mean(lamda)                     *  (N - mean(N)))
  lamda.X.iota                       <- (lamda - mean(lamda)                     *  (iota - mean(iota)))
  lamda.X.phi                        <- (lamda - mean(lamda)                     *  (phi - mean(phi)))
  lamda.X.tau                        <- (lamda - mean(lamda)                     *  (tau - mean(tau)))
  lamda.X.density                    <- (lamda - mean(lamda)                     *  (density - mean(density)))
  lamda.X.av.degree                  <- (lamda - mean(lamda)                     *  (av.degree - mean(av.degree)))
  lamda.X.av.degree2                 <- (lamda - mean(lamda)                     *  (av.degree2 - mean(av.degree2)))
  lamda.X.av.closeness               <- (lamda - mean(lamda)                     *  (av.closeness - mean(av.closeness)))
  lamda.X.av.clustering              <- (lamda - mean(lamda)                     *  (av.clustering - mean(av.clustering)))
  lamda.X.agent.degree               <- (lamda - mean(lamda)                     *  (agent.degree - mean(agent.degree)))
  lamda.X.agent.degree2              <- (lamda - mean(lamda)                     *  (agent.degree2 - mean(agent.degree2)))
  lamda.X.agent.closeness            <- (lamda - mean(lamda)                     *  (agent.closeness - mean(agent.closeness)))
  lamda.X.agent.clustering           <- (lamda - mean(lamda)                     *  (agent.clustering - mean(agent.clustering)))
  # combinations of c
  c.X.mu                             <- (c - mean(c)                             *  (mu - mean(mu)))
  c.X.sigma                          <- (c - mean(c)                             *  (sigma - mean(sigma)))
  c.X.gamma                          <- (c - mean(c)                             *  (gamma - mean(gamma)))
  c.X.r.sigma                        <- (c - mean(c)                             *  (r.sigma - mean(r.sigma)))
  c.X.r.pi                           <- (c - mean(c)                             *  (r.pi - mean(r.pi)))
  c.X.N                              <- (c - mean(c)                             *  (N - mean(N)))
  c.X.iota                           <- (c - mean(c)                             *  (iota - mean(iota)))
  c.X.phi                            <- (c - mean(c)                             *  (phi - mean(phi)))
  c.X.tau                            <- (c - mean(c)                             *  (tau - mean(tau)))
  c.X.density                        <- (c - mean(c)                             *  (density - mean(density)))
  c.X.av.degree                      <- (c - mean(c)                             *  (av.degree - mean(av.degree)))
  c.X.av.degree2                     <- (c - mean(c)                             *  (av.degree2 - mean(av.degree2)))
  c.X.av.closeness                   <- (c - mean(c)                             *  (av.closeness - mean(av.closeness)))
  c.X.av.clustering                  <- (c - mean(c)                             *  (av.clustering - mean(av.clustering)))
  c.X.agent.degree                   <- (c - mean(c)                             *  (agent.degree - mean(agent.degree)))
  c.X.agent.degree2                  <- (c - mean(c)                             *  (agent.degree2 - mean(agent.degree2)))
  c.X.agent.closeness                <- (c - mean(c)                             *  (agent.closeness - mean(agent.closeness)))
  c.X.agent.clustering               <- (c - mean(c)                             *  (agent.clustering - mean(agent.clustering)))
  # combinations of mu
  mu.X.sigma                         <- (mu - mean(mu)                           *  (sigma - mean(sigma)))
  mu.X.gamma                         <- (mu - mean(mu)                           *  (gamma - mean(gamma)))
  mu.X.r.sigma                       <- (mu - mean(mu)                           *  (r.sigma - mean(r.sigma)))
  mu.X.r.pi                          <- (mu - mean(mu)                           *  (r.pi - mean(r.pi)))
  mu.X.N                             <- (mu - mean(mu)                           *  (N - mean(N)))
  mu.X.iota                          <- (mu - mean(mu)                           *  (iota - mean(iota)))
  mu.X.phi                           <- (mu - mean(mu)                           *  (phi - mean(phi)))
  mu.X.tau                           <- (mu - mean(mu)                           *  (tau - mean(tau)))
  mu.X.density                       <- (mu - mean(mu)                           *  (density - mean(density)))
  mu.X.av.degree                     <- (mu - mean(mu)                           *  (av.degree - mean(av.degree)))
  mu.X.av.degree2                    <- (mu - mean(mu)                           *  (av.degree2 - mean(av.degree2)))
  mu.X.av.closeness                  <- (mu - mean(mu)                           *  (av.closeness - mean(av.closeness)))
  mu.X.av.clustering                 <- (mu - mean(mu)                           *  (av.clustering - mean(av.clustering)))
  mu.X.agent.degree                  <- (mu - mean(mu)                           *  (agent.degree - mean(agent.degree)))
  mu.X.agent.degree2                 <- (mu - mean(mu)                           *  (agent.degree2 - mean(agent.degree2)))
  mu.X.agent.closeness               <- (mu - mean(mu)                           *  (agent.closeness - mean(agent.closeness)))
  mu.X.agent.clustering              <- (mu - mean(mu)                           *  (agent.clustering - mean(agent.clustering)))
  # combinations of sigma
  sigma.X.gamma                      <- (sigma - mean(sigma)                     *  (gamma - mean(gamma)))
  sigma.X.r.sigma                    <- (sigma - mean(sigma)                     *  (r.sigma - mean(r.sigma)))
  sigma.X.r.pi                       <- (sigma - mean(sigma)                     *  (r.pi - mean(r.pi)))
  sigma.X.N                          <- (sigma - mean(sigma)                     *  (N - mean(N)))
  sigma.X.iota                       <- (sigma - mean(sigma)                     *  (iota - mean(iota)))
  sigma.X.phi                        <- (sigma - mean(sigma)                     *  (phi - mean(phi)))
  sigma.X.tau                        <- (sigma - mean(sigma)                     *  (tau - mean(tau)))
  sigma.X.density                    <- (sigma - mean(sigma)                     *  (density - mean(density)))
  sigma.X.av.degree                  <- (sigma - mean(sigma)                     *  (av.degree - mean(av.degree)))
  sigma.X.av.degree2                 <- (sigma - mean(sigma)                     *  (av.degree2 - mean(av.degree2)))
  sigma.X.av.closeness               <- (sigma - mean(sigma)                     *  (av.closeness - mean(av.closeness)))
  sigma.X.av.clustering              <- (sigma - mean(sigma)                     *  (av.clustering - mean(av.clustering)))
  sigma.X.agent.degree               <- (sigma - mean(sigma)                     *  (agent.degree - mean(agent.degree)))
  sigma.X.agent.degree2              <- (sigma - mean(sigma)                     *  (agent.degree2 - mean(agent.degree2)))
  sigma.X.agent.closeness            <- (sigma - mean(sigma)                     *  (agent.closeness - mean(agent.closeness)))
  sigma.X.agent.clustering           <- (sigma - mean(sigma)                     *  (agent.clustering - mean(agent.clustering)))
  # combinations of gamma
  gamma.X.r.sigma                    <- (gamma - mean(gamma)                     *  (r.sigma - mean(r.sigma)))
  gamma.X.r.pi                       <- (gamma - mean(gamma)                     *  (r.pi - mean(r.pi)))
  gamma.X.N                          <- (gamma - mean(gamma)                     *  (N - mean(N)))
  gamma.X.iota                       <- (gamma - mean(gamma)                     *  (iota - mean(iota)))
  gamma.X.phi                        <- (gamma - mean(gamma)                     *  (phi - mean(phi)))
  gamma.X.tau                        <- (gamma - mean(gamma)                     *  (tau - mean(tau)))
  gamma.X.density                    <- (gamma - mean(gamma)                     *  (density - mean(density)))
  gamma.X.av.degree                  <- (gamma - mean(gamma)                     *  (av.degree - mean(av.degree)))
  gamma.X.av.degree2                 <- (gamma - mean(gamma)                     *  (av.degree2 - mean(av.degree2)))
  gamma.X.av.closeness               <- (gamma - mean(gamma)                     *  (av.closeness - mean(av.closeness)))
  gamma.X.av.clustering              <- (gamma - mean(gamma)                     *  (av.clustering - mean(av.clustering)))
  gamma.X.agent.degree               <- (gamma - mean(gamma)                     *  (agent.degree - mean(agent.degree)))
  gamma.X.agent.degree2              <- (gamma - mean(gamma)                     *  (agent.degree2 - mean(agent.degree2)))
  gamma.X.agent.closeness            <- (gamma - mean(gamma)                     *  (agent.closeness - mean(agent.closeness)))
  gamma.X.agent.clustering           <- (gamma - mean(gamma)                     *  (agent.clustering - mean(agent.clustering)))
  # combinations of r.sigma
  r.sigma.X.r.pi                     <- (r.sigma - mean(r.sigma)                 *  (r.pi - mean(r.pi)))
  r.sigma.X.N                        <- (r.sigma - mean(r.sigma)                 *  (N - mean(N)))
  r.sigma.X.iota                     <- (r.sigma - mean(r.sigma)                 *  (iota - mean(iota)))
  r.sigma.X.phi                      <- (r.sigma - mean(r.sigma)                 *  (phi - mean(phi)))
  r.sigma.X.tau                      <- (r.sigma - mean(r.sigma)                 *  (tau - mean(tau)))
  r.sigma.X.density                  <- (r.sigma - mean(r.sigma)                 *  (density - mean(density)))
  r.sigma.X.av.degree                <- (r.sigma - mean(r.sigma)                 *  (av.degree - mean(av.degree)))
  r.sigma.X.av.degree2               <- (r.sigma - mean(r.sigma)                 *  (av.degree2 - mean(av.degree2)))
  r.sigma.X.av.closeness             <- (r.sigma - mean(r.sigma)                 *  (av.closeness - mean(av.closeness)))
  r.sigma.X.av.clustering            <- (r.sigma - mean(r.sigma)                 *  (av.clustering - mean(av.clustering)))
  r.sigma.X.agent.degree             <- (r.sigma - mean(r.sigma)                 *  (agent.degree - mean(agent.degree)))
  r.sigma.X.agent.degree2            <- (r.sigma - mean(r.sigma)                 *  (agent.degree2 - mean(agent.degree2)))
  r.sigma.X.agent.closeness          <- (r.sigma - mean(r.sigma)                 *  (agent.closeness - mean(agent.closeness)))
  r.sigma.X.agent.clustering         <- (r.sigma - mean(r.sigma)                 *  (agent.clustering - mean(agent.clustering)))
  # combinations of r.pi
  r.pi.X.N                           <- (r.pi - mean(r.pi)                       *  (N - mean(N)))
  r.pi.X.iota                        <- (r.pi - mean(r.pi)                       *  (iota - mean(iota)))
  r.pi.X.phi                         <- (r.pi - mean(r.pi)                       *  (phi - mean(phi)))
  r.pi.X.tau                         <- (r.pi - mean(r.pi)                       *  (tau - mean(tau)))
  r.pi.X.density                     <- (r.pi - mean(r.pi)                       *  (density - mean(density)))
  r.pi.X.av.degree                   <- (r.pi - mean(r.pi)                       *  (av.degree - mean(av.degree)))
  r.pi.X.av.degree2                  <- (r.pi - mean(r.pi)                       *  (av.degree2 - mean(av.degree2)))
  r.pi.X.av.closeness                <- (r.pi - mean(r.pi)                       *  (av.closeness - mean(av.closeness)))
  r.pi.X.av.clustering               <- (r.pi - mean(r.pi)                       *  (av.clustering - mean(av.clustering)))
  r.pi.X.agent.degree                <- (r.pi - mean(r.pi)                       *  (agent.degree - mean(agent.degree)))
  r.pi.X.agent.degree2               <- (r.pi - mean(r.pi)                       *  (agent.degree2 - mean(agent.degree2)))
  r.pi.X.agent.closeness             <- (r.pi - mean(r.pi)                       *  (agent.closeness - mean(agent.closeness)))
  r.pi.X.agent.clustering            <- (r.pi - mean(r.pi)                       *  (agent.clustering - mean(agent.clustering)))
  # combinations of N
  N.X.iota                           <- (N - mean(N)                             *  (iota - mean(iota)))
  N.X.phi                            <- (N - mean(N)                             *  (phi - mean(phi)))
  N.X.tau                            <- (N - mean(N)                             *  (tau - mean(tau)))
  N.X.density                        <- (N - mean(N)                             *  (density - mean(density)))
  N.X.av.degree                      <- (N - mean(N)                             *  (av.degree - mean(av.degree)))
  N.X.av.degree2                     <- (N - mean(N)                             *  (av.degree2 - mean(av.degree2)))
  N.X.av.closeness                   <- (N - mean(N)                             *  (av.closeness - mean(av.closeness)))
  N.X.av.clustering                  <- (N - mean(N)                             *  (av.clustering - mean(av.clustering)))
  N.X.agent.degree                   <- (N - mean(N)                             *  (agent.degree - mean(agent.degree)))
  N.X.agent.degree2                  <- (N - mean(N)                             *  (agent.degree2 - mean(agent.degree2)))
  N.X.agent.closeness                <- (N - mean(N)                             *  (agent.closeness - mean(agent.closeness)))
  N.X.agent.clustering               <- (N - mean(N)                             *  (agent.clustering - mean(agent.clustering)))
  # combinations of iota
  iota.X.phi                         <- (iota - mean(iota)                       *  (phi - mean(phi)))
  iota.X.tau                         <- (iota - mean(iota)                       *  (tau - mean(tau)))
  iota.X.density                     <- (iota - mean(iota)                       *  (density - mean(density)))
  iota.X.av.degree                   <- (iota - mean(iota)                       *  (av.degree - mean(av.degree)))
  iota.X.av.degree2                  <- (iota - mean(iota)                       *  (av.degree2 - mean(av.degree2)))
  iota.X.av.closeness                <- (iota - mean(iota)                       *  (av.closeness - mean(av.closeness)))
  iota.X.av.clustering               <- (iota - mean(iota)                       *  (av.clustering - mean(av.clustering)))
  iota.X.agent.degree                <- (iota - mean(iota)                       *  (agent.degree - mean(agent.degree)))
  iota.X.agent.degree2               <- (iota - mean(iota)                       *  (agent.degree2 - mean(agent.degree2)))
  iota.X.agent.closeness             <- (iota - mean(iota)                       *  (agent.closeness - mean(agent.closeness)))
  iota.X.agent.clustering            <- (iota - mean(iota)                       *  (agent.clustering - mean(agent.clustering)))
  # combinations of phi
  phi.X.tau                          <- (phi - mean(phi)                         *  (tau - mean(tau)))
  phi.X.density                      <- (phi - mean(phi)                         *  (density - mean(density)))
  phi.X.av.degree                    <- (phi - mean(phi)                         *  (av.degree - mean(av.degree)))
  phi.X.av.degree2                   <- (phi - mean(phi)                         *  (av.degree2 - mean(av.degree2)))
  phi.X.av.closeness                 <- (phi - mean(phi)                         *  (av.closeness - mean(av.closeness)))
  phi.X.av.clustering                <- (phi - mean(phi)                         *  (av.clustering - mean(av.clustering)))
  phi.X.agent.degree                 <- (phi - mean(phi)                         *  (agent.degree - mean(agent.degree)))
  phi.X.agent.degree2                <- (phi - mean(phi)                         *  (agent.degree2 - mean(agent.degree2)))
  phi.X.agent.closeness              <- (phi - mean(phi)                         *  (agent.closeness - mean(agent.closeness)))
  phi.X.agent.clustering             <- (phi - mean(phi)                         *  (agent.clustering - mean(agent.clustering)))
  # combinations of tau
  tau.X.density                      <- (tau - mean(tau)                         *  (density - mean(density)))
  tau.X.av.degree                    <- (tau - mean(tau)                         *  (av.degree - mean(av.degree)))
  tau.X.av.degree2                   <- (tau - mean(tau)                         *  (av.degree2 - mean(av.degree2)))
  tau.X.av.closeness                 <- (tau - mean(tau)                         *  (av.closeness - mean(av.closeness)))
  tau.X.av.clustering                <- (tau - mean(tau)                         *  (av.clustering - mean(av.clustering)))
  tau.X.agent.degree                 <- (tau - mean(tau)                         *  (agent.degree - mean(agent.degree)))
  tau.X.agent.degree2                <- (tau - mean(tau)                         *  (agent.degree2 - mean(agent.degree2)))
  tau.X.agent.closeness              <- (tau - mean(tau)                         *  (agent.closeness - mean(agent.closeness)))
  tau.X.agent.clustering             <- (tau - mean(tau)                         *  (agent.clustering - mean(agent.clustering)))
  # combinations of density
  density.X.av.degree                <- (density - mean(density)                 *  (av.degree - mean(av.degree)))
  density.X.av.degree2               <- (density - mean(density)                 *  (av.degree2 - mean(av.degree2)))
  density.X.av.closeness             <- (density - mean(density)                 *  (av.closeness - mean(av.closeness)))
  density.X.av.clustering            <- (density - mean(density)                 *  (av.clustering - mean(av.clustering)))
  density.X.agent.degree             <- (density - mean(density)                 *  (agent.degree - mean(agent.degree)))
  density.X.agent.degree2            <- (density - mean(density)                 *  (agent.degree2 - mean(agent.degree2)))
  density.X.agent.closeness          <- (density - mean(density)                 *  (agent.closeness - mean(agent.closeness)))
  density.X.agent.clustering         <- (density - mean(density)                 *  (agent.clustering - mean(agent.clustering)))
  # combinations of av.degree
  av.degree.X.av.degree2             <- (av.degree - mean(av.degree)             *  (av.degree2 - mean(av.degree2)))
  av.degree.X.av.closeness           <- (av.degree - mean(av.degree)             *  (av.closeness - mean(av.closeness)))
  av.degree.X.av.clustering          <- (av.degree - mean(av.degree)             *  (av.clustering - mean(av.clustering)))
  av.degree.X.agent.degree           <- (av.degree - mean(av.degree)             *  (agent.degree - mean(agent.degree)))
  av.degree.X.agent.degree2          <- (av.degree - mean(av.degree)             *  (agent.degree2 - mean(agent.degree2)))
  av.degree.X.agent.closeness        <- (av.degree - mean(av.degree)             *  (agent.closeness - mean(agent.closeness)))
  av.degree.X.agent.clustering       <- (av.degree - mean(av.degree)             *  (agent.clustering - mean(agent.clustering)))
  # combinations of av.degree2
  av.degree2.X.av.closeness          <- (av.degree2 - mean(av.degree2)           *  (av.closeness - mean(av.closeness)))
  av.degree2.X.av.clustering         <- (av.degree2 - mean(av.degree2)           *  (av.clustering - mean(av.clustering)))
  av.degree2.X.agent.degree          <- (av.degree2 - mean(av.degree2)           *  (agent.degree - mean(agent.degree)))
  av.degree2.X.agent.degree2         <- (av.degree2 - mean(av.degree2)           *  (agent.degree2 - mean(agent.degree2)))
  av.degree2.X.agent.closeness       <- (av.degree2 - mean(av.degree2)           *  (agent.closeness - mean(agent.closeness)))
  av.degree2.X.agent.clustering      <- (av.degree2 - mean(av.degree2)           *  (agent.clustering - mean(agent.clustering)))
  # combinations of av.closeness
  av.closeness.X.av.clustering       <- (av.closeness - mean(av.closeness)       *  (av.clustering - mean(av.clustering)))
  av.closeness.X.agent.degree        <- (av.closeness - mean(av.closeness)       *  (agent.degree - mean(agent.degree)))
  av.closeness.X.agent.degree2       <- (av.closeness - mean(av.closeness)       *  (agent.degree2 - mean(agent.degree2)))
  av.closeness.X.agent.closeness     <- (av.closeness - mean(av.closeness)       *  (agent.closeness - mean(agent.closeness)))
  av.closeness.X.agent.clustering    <- (av.closeness - mean(av.closeness)       *  (agent.clustering - mean(agent.clustering)))
  # combinations of av.clustering
  av.clustering.X.agent.degree       <- (av.clustering - mean(av.clustering)     *  (agent.degree - mean(agent.degree)))
  av.clustering.X.agent.degree2      <- (av.clustering - mean(av.clustering)     *  (agent.degree2 - mean(agent.degree2)))
  av.clustering.X.agent.closeness    <- (av.clustering - mean(av.clustering)     *  (agent.closeness - mean(agent.closeness)))
  av.clustering.X.agent.clustering   <- (av.clustering - mean(av.clustering)     *  (agent.clustering - mean(agent.clustering)))
  # combinations of agent.degree
  agent.degree.X.agent.degree2       <- (agent.degree - mean(agent.degree)       *  (agent.degree2 - mean(agent.degree2)))
  agent.degree.X.agent.closeness     <- (agent.degree - mean(agent.degree)       *  (agent.closeness - mean(agent.closeness)))
  agent.degree.X.agent.clustering    <- (agent.degree - mean(agent.degree)       *  (agent.clustering - mean(agent.clustering)))
  # combinations of agent.degree2
  agent.degree2.X.agent.closeness    <- (agent.degree2 - mean(agent.degree2)     *  (agent.closeness - mean(agent.closeness)))
  agent.degree2.X.agent.clustering   <- (agent.degree2 - mean(agent.degree2)     *  (agent.clustering - mean(agent.clustering)))
  # combinations of agent.closeness
  agent.closeness.X.agent.clustering <- (agent.closeness - mean(agent.closeness) *  (agent.clustering - mean(agent.clustering)))

  ### 2-LEVEL LOGISTIC REGRESSIONS (attack rate)  ###
  ### level 2: parameters combination             ###
  ### level 1: simulation runs                    ###
  # null-model
  reg00 <- glmer(ssData$prop.net.pct.rec/100 ~
                   1 +
                   (1 | param.sim.upc),
                 family = binomial,
                 data = ssData)
  # main effects: varied CIDM parameters
  reg1Main <- glmer(ssData$prop.net.pct.rec/100 ~
                      # CIDM parameters
                      beta +
                      mu +
                      sigma +
                      r.sigma +
                      r.pi +
                      N +
                      (1 | param.sim.upc),
                    family = binomial,
                    data = ssData)
  # main effects: varied CIDM parameters + network properties at the time of the first infection
  reg2Main <- glmer(ssData$prop.net.pct.rec/100 ~
                      # CIDM parameters
                      beta +
                      mu +
                      sigma +
                      r.sigma +
                      r.pi +
                      N +
                      # network properties
                      density +
                      av.degree +
                      av.closeness +
                      av.clustering +
                      agent.degree +
                      agent.closeness +
                      agent.clustering +
                      (1 | param.sim.upc),
                    family = binomial,
                    data = ssData)
  # interaction effects
  reg2Int <- glmer(ssData$prop.net.pct.rec/100 ~
                     # CIDM parameters
                     beta +
                     mu +
                     sigma +
                     r.sigma +
                     r.pi +
                     N +
                     # network properties
                     density +
                     av.degree +
                     av.closeness +
                     av.clustering +
                     agent.degree +
                     agent.closeness +
                     agent.clustering +
                     # interaction effects
                     beta.X.mu +
                     beta.X.sigma +
                     beta.X.r.sigma +
                     beta.X.r.pi +
                     beta.X.N +
                     beta.X.density +
                     beta.X.av.degree +
                     beta.X.av.closeness +
                     beta.X.av.clustering +
                     beta.X.agent.degree +
                     beta.X.agent.closeness +
                     beta.X.agent.clustering +
                     mu.X.sigma +
                     mu.X.r.sigma +
                     mu.X.r.pi +
                     mu.X.N +
                     mu.X.density +
                     mu.X.av.degree +
                     mu.X.av.closeness +
                     mu.X.av.clustering +
                     mu.X.agent.degree +
                     mu.X.agent.closeness +
                     mu.X.agent.clustering +
                     sigma.X.r.sigma +
                     sigma.X.r.pi +
                     sigma.X.N +
                     sigma.X.density +
                     sigma.X.av.degree +
                     sigma.X.av.closeness +
                     sigma.X.av.clustering +
                     sigma.X.agent.degree +
                     sigma.X.agent.closeness +
                     sigma.X.agent.clustering +
                     r.sigma.X.r.pi +
                     r.sigma.X.N +
                     r.sigma.X.density +
                     r.sigma.X.av.degree +
                     r.sigma.X.av.closeness +
                     r.sigma.X.av.clustering +
                     r.sigma.X.agent.degree +
                     r.sigma.X.agent.closeness +
                     r.sigma.X.agent.clustering +
                     r.pi.X.N +
                     r.pi.X.density +
                     r.pi.X.av.degree +
                     r.pi.X.av.closeness +
                     r.pi.X.av.clustering +
                     r.pi.X.agent.degree +
                     r.pi.X.agent.closeness +
                     r.pi.X.agent.clustering +
                     N.X.density +
                     N.X.av.degree +
                     N.X.av.closeness +
                     N.X.av.clustering +
                     N.X.agent.degree +
                     N.X.agent.closeness +
                     N.X.agent.clustering +
                     density.X.av.degree +
                     density.X.av.closeness +
                     density.X.av.clustering +
                     density.X.agent.degree +
                     density.X.agent.closeness +
                     density.X.agent.clustering +
                     av.degree.X.av.closeness +
                     av.degree.X.av.clustering +
                     av.degree.X.agent.degree +
                     av.degree.X.agent.closeness +
                     av.degree.X.agent.clustering +
                     av.closeness.X.av.clustering +
                     av.closeness.X.agent.degree +
                     av.closeness.X.agent.closeness +
                     av.closeness.X.agent.clustering +
                     av.clustering.X.agent.degree +
                     av.clustering.X.agent.closeness +
                     av.clustering.X.agent.clustering +
                     agent.degree.X.agent.closeness +
                     agent.degree.X.agent.clustering +
                     agent.closeness.X.agent.clustering +
                     (1 | param.sim.upc),
                   family = binomial,
                   data = ssData)
  exportModels(list(reg00,reg1Main,reg2Main,reg2Int), "reg-attack-rate-complete")


  ### 2-LEVEL LINEAR REGRESSIONS (duration)   ###
  ### level 2: parameters combination         ###
  ### level 1: simulation runs                ###
  # null-model
  reg00 <- lmer(ssData$prop.sim.epidemic.duration
                ~ 1 +
                  (1 | param.sim.upc),
                data = ssData,
                REML = FALSE)
  # main effects: varied CIDM parameters
  reg1Main <- lmer(ssData$prop.sim.epidemic.duration ~
                     # CIDM parameters
                     beta +
                     mu +
                     sigma +
                     r.sigma +
                     r.pi +
                     N +
                     (1 | param.sim.upc),
                   data = ssData,
                   REML = FALSE)
  # main effects: varied CIDM parameters + network properties at the time of the first infection
  reg2Main <- lmer(ssData$prop.sim.epidemic.duration ~
                     # CIDM parameters
                     beta +
                     mu +
                     sigma +
                     r.sigma +
                     r.pi +
                     N +
                     # network properties
                     density +
                     av.degree +
                     av.closeness +
                     av.clustering +
                     agent.degree +
                     agent.closeness +
                     agent.clustering +
                     (1 | param.sim.upc),
                   data = ssData,
                   REML = FALSE)
  # interaction effects
  reg2Int <- lmer(ssData$prop.sim.epidemic.duration ~
                    # CIDM parameters
                    beta +
                    mu +
                    sigma +
                    r.sigma +
                    r.pi +
                    N +
                    # network properties
                    density +
                    av.degree +
                    av.closeness +
                    av.clustering +
                    agent.degree +
                    agent.closeness +
                    agent.clustering +
                    # interaction effects
                    beta.X.mu +
                    beta.X.sigma +
                    beta.X.r.sigma +
                    beta.X.r.pi +
                    beta.X.N +
                    beta.X.density +
                    beta.X.av.degree +
                    beta.X.av.closeness +
                    beta.X.av.clustering +
                    beta.X.agent.degree +
                    beta.X.agent.closeness +
                    beta.X.agent.clustering +
                    mu.X.sigma +
                    mu.X.r.sigma +
                    mu.X.r.pi +
                    mu.X.N +
                    mu.X.density +
                    mu.X.av.degree +
                    mu.X.av.closeness +
                    mu.X.av.clustering +
                    mu.X.agent.degree +
                    mu.X.agent.closeness +
                    mu.X.agent.clustering +
                    sigma.X.r.sigma +
                    sigma.X.r.pi +
                    sigma.X.N +
                    sigma.X.density +
                    sigma.X.av.degree +
                    sigma.X.av.closeness +
                    sigma.X.av.clustering +
                    sigma.X.agent.degree +
                    sigma.X.agent.closeness +
                    sigma.X.agent.clustering +
                    r.sigma.X.r.pi +
                    r.sigma.X.N +
                    r.sigma.X.density +
                    r.sigma.X.av.degree +
                    r.sigma.X.av.closeness +
                    r.sigma.X.av.clustering +
                    r.sigma.X.agent.degree +
                    r.sigma.X.agent.closeness +
                    r.sigma.X.agent.clustering +
                    r.pi.X.N +
                    r.pi.X.density +
                    r.pi.X.av.degree +
                    r.pi.X.av.closeness +
                    r.pi.X.av.clustering +
                    r.pi.X.agent.degree +
                    r.pi.X.agent.closeness +
                    r.pi.X.agent.clustering +
                    N.X.density +
                    N.X.av.degree +
                    N.X.av.closeness +
                    N.X.av.clustering +
                    N.X.agent.degree +
                    N.X.agent.closeness +
                    N.X.agent.clustering +
                    density.X.av.degree +
                    density.X.av.closeness +
                    density.X.av.clustering +
                    density.X.agent.degree +
                    density.X.agent.closeness +
                    density.X.agent.clustering +
                    av.degree.X.av.closeness +
                    av.degree.X.av.clustering +
                    av.degree.X.agent.degree +
                    av.degree.X.agent.closeness +
                    av.degree.X.agent.clustering +
                    av.closeness.X.av.clustering +
                    av.closeness.X.agent.degree +
                    av.closeness.X.agent.closeness +
                    av.closeness.X.agent.clustering +
                    av.clustering.X.agent.degree +
                    av.clustering.X.agent.closeness +
                    av.clustering.X.agent.clustering +
                    agent.degree.X.agent.closeness +
                    agent.degree.X.agent.clustering +
                    agent.closeness.X.agent.clustering +
                    (1 | param.sim.upc),
                  data = ssData,
                  REML = FALSE)

  exportModels(list(reg00,reg1Main,reg2Main,reg2Int), "reg-duration-complete")
}

#----------------------------------------------------------------------------------------------------#
# function: exportAttackRateModelsSelected
#     Creates and exports multi-level logistic regression models for attack rate with only
#     selected parameters and interactions. This method is a copy of 'exportAttackRateModelsComplete',
#     but is intended to find the best models with only significant and expressive parameters by
#     modyfying the model parameters without changing the complete export for command line
#     invocations.
# param:  ssData
#     simulation summary data to get regression models for
#----------------------------------------------------------------------------------------------------#
exportRegressionModelsSelected <- function(ssData = loadSimulationSummaryData()) {

  ### COPY MODELS FROM ABOVE AND COMMENT OUT INSIGNIFICANT PARAMETERS

  # exportModels(list(reg00,reg1Main,reg2Main,reg2Int), "reg-attack-rate-selected")
}


############################################ DESCRIPIVES #############################################
#----------------------------------------------------------------------------------------------------#
# function: exportDataFrame
#     Exports a generic data frame.
# param:  df
#     the data frame to export
# param:  filename
#     the filename for the data frame to export
#----------------------------------------------------------------------------------------------------#
exportDataFrame <- function(df, filename) {

  # create directory if necessary
  dir.create(EXPORT_PATH_NUM, showWarnings = FALSE)

  filepath <- paste(EXPORT_PATH_NUM,
                    filename,
                    EXPORT_FILE_EXTENSION_DF,
                    sep = "")

  write.table(df, file=filepath, sep = ";", row.names = TRUE, col.names = NA)
}

#----------------------------------------------------------------------------------------------------#
# function: exportNetworkSizeMeasures
#     Exports various measures dependent on network size, such as density, average degree,
#     attack rate, duration of epidemics, and time steps to epidemic peaks.
# param:  ssData
#     simulation summary data to export network size dependent measures for
# param:  rsData
#     round summary data to export network size dependent measures for
# param:  filename
#     the name of the export file
#----------------------------------------------------------------------------------------------------#
exportNetworkSizeMeasures <- function(ssData = loadSimulationSummaryData(),
                                      rsData = reduceRoundSummaryData(loadRoundSummaryData(), cropTail = 20),
                                      filename = "network-size-measures") {

  peakData <- data.frame(rsData$param.cidm.N,
                         rsData$param.sim.uid,
                         rsData$prop.sim.round-10,
                         rsData$prop.net.pct.inf)
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

  for (N in unique(ssData$param.cidm.N)) {
    ssN <- subset(ssData, param.cidm.N == N)
    # network size
    netSizes <- c(netSizes, N)
    # density
    densities <- c(densities, round(mean(ssN$prop.net.density.pre.epidemic), 3))
    sdDensities <- c(sdDensities, round(sd(ssN$prop.net.density.pre.epidemic), 3))
    # degree
    degrees <- c(degrees, round(mean(ssN$prop.net.av.degree.pre.epidemic), 3))
    sdDegrees <- c(sdDegrees, round(sd(ssN$prop.net.av.degree.pre.epidemic), 3))
    # attack rate
    attackRates <- c(attackRates, round(mean(ssN$prop.net.pct.rec), 3))
    sdAttackRates <- c(sdAttackRates, round(sd(ssN$prop.net.pct.rec), 3))
    # duration
    durations <- c(durations, round(mean(ssN$prop.sim.epidemic.duration), 3))
    sdDurations <- c(sdDurations, round(sd(ssN$prop.sim.epidemic.duration), 3))
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

  exportDataFrame(nsMeasures, filename)
}

#----------------------------------------------------------------------------------------------------#
# function: exportEpidemicMeasures
#     Exports descriptive statistics for epidemics, such as attack rate, duration,
#     average degree prior to epidemics, and average minimum degree during epidemics.
# param:  ssData
#     simulation summary data to export epidemic measures for
# param:  rsData
#     round summary data to export epidemic measures for
#----------------------------------------------------------------------------------------------------#
exportEpidemicMeasures <- function(ssData = loadSimulationSummaryData(),
                                   rsData = reduceRoundSummaryData(loadRoundSummaryData(), cropTail = 20)) {

  ##### BETA-MU #####
  # results data frame
  resBetaMu <- data.frame(matrix(0, ncol= 6, nrow=length(unique(ssData$param.cidm.beta.av))*length(unique(ssData$param.cidm.mu.av))))
  colnames(resBetaMu) <- c("beta", "mu", "attack.rate", "duration", "degree.pre.epidemic", "degree.min")

  # ATTACK RATE, DURATION
  ssBetaMu <- subsetsByColumnValues(data = ssData,
                                    col1 = 'param.cidm.beta.av',
                                    col2 = 'param.cidm.mu.av')
  for (i in 1:length(ssBetaMu)) {
    ss <- ssBetaMu[[i]]
    resBetaMu[i,1]  <- head(ss, 1)$param.cidm.beta.av
    resBetaMu[i,2]  <- head(ss, 1)$param.cidm.mu.av
    resBetaMu[i,3]  <- round(median(ss$prop.net.pct.rec), digits = 3)
    resBetaMu[i,4]  <- round(median(ss$prop.sim.epidemic.duration), digits = 3)
  }

  # AVERAGE DEGREES
  rsBetaMu <- subsetsByColumnValues(data = rsData,
                                    col1 = 'param.cidm.beta.av',
                                    col2 = 'param.cidm.mu.av')
  for (rs in rsBetaMu) {
    # average degree prior to epidemic
    resBetaMu$degree.pre.epidemic[
      resBetaMu$beta == head(rs, 1)$param.cidm.beta.av
      & resBetaMu$mu == head(rs, 1)$param.cidm.mu.av
      ] <- round(median(subset(rs, prop.sim.round == 10)$prop.net.av.degree), digits = 3)
    # average minimum degree during epidemic
    resBetaMu$degree.min[
      resBetaMu$beta == head(rs, 1)$param.cidm.beta.av
      & resBetaMu$mu == head(rs, 1)$param.cidm.mu
      ] <- round(median(aggregate(prop.net.av.degree ~ param.sim.uid, data = rs, min)$prop.net.av.degree), digits = 3)
  }
  exportDataFrame(resBetaMu, "descriptives-beta-mu")


  ##### SIGMA-R #####
  if (unique(rsData$param.cidm.rs.equal) == 1) {
    # results data frame
    resSigmaR <- data.frame(matrix(0, ncol= 6, nrow=length(unique(ssData$param.cidm.sigma.av))
                                   *length(unique(ssData$param.cidm.r.sigma.av))))
    colnames(resSigmaR) <- c("sigma", "r", "attack.rate", "duration", "degree.pre.epidemic", "degree.min")

    # ATTACK RATE, DURATION
    ssSigmaR <- subsetsByColumnValues(data = ssData,
                                      col1 = 'param.cidm.sigma.av',
                                      col2 = 'param.cidm.r.sigma.av')
    for (i in 1:length(ssSigmaR)) {
      ss <- ssSigmaR[[i]]
      resSigmaR[i,1]  <- head(ss, 1)$param.cidm.sigma.av
      resSigmaR[i,2]  <- head(ss, 1)$param.cidm.r.sigma.av
      resSigmaR[i,3]  <- round(median(ss$prop.net.pct.rec), digits = 3)
      resSigmaR[i,4]  <- round(median(ss$prop.sim.epidemic.duration), digits = 3)
    }

    # AVERAGE DEGREES
    rsSigmaR <- subsetsByColumnValues(data = rsData,
                                      col1 = 'param.cidm.sigma.av',
                                      col2 = 'param.cidm.r.sigma.av')
    for (rs in rsSigmaR) {
      # average degree prior to epidemic
      resSigmaR$degree.pre.epidemic[
        resSigmaR$sigma == head(rs, 1)$param.cidm.sigma.av
        & resSigmaR$r.combined == head(rs, 1)$param.cidm.r.sigma.av
        ] <- round(median(subset(rs, prop.sim.round == 10)$prop.net.av.degree), digits = 3)
      # average minimum degree during epidemic
      resSigmaR$degree.min[
        resSigmaR$sigma == head(rs, 1)$param.cidm.sigma.av
        & resSigmaR$r.combined == head(rs, 1)$param.cidm.r.sigma.av
        ] <- round(median(aggregate(prop.net.av.degree ~ param.sim.uid, data = rs, min)$prop.net.av.degree), digits = 3)
    }
    exportDataFrame(resSigmaR, "descriptives-sigma-rcombined")
  } else {
    # results data frame
    resSigmaR <- data.frame(matrix(0, ncol= 7, nrow=length(unique(ssData$param.cidm.sigma.av))
                                   *length(unique(ssData$param.cidm.r.sigma.av))
                                   *length(unique(ssData$param.cidm.r.pi.av))))
    colnames(resSigmaR) <- c("sigma", "r.sigma", "r.pi", "attack.rate", "duration", "degree.pre.epidemic", "degree.min")

    # ATTACK RATE, DURATION
    ssSigmaR <- subsetsByColumnValues(data = ssData,
                                      col1 = 'param.cidm.sigma.av',
                                      col2 = 'param.cidm.r.sigma.av',
                                      col3 = 'param.cidm.r.pi.av')
    for (i in 1:length(ssSigmaR)) {
      ss <- ssSigmaR[[i]]
      resSigmaR[i,1]  <- head(ss, 1)$param.cidm.sigma.av
      resSigmaR[i,2]  <- head(ss, 1)$param.cidm.r.sigma.av
      resSigmaR[i,3]  <- head(ss, 1)$param.cidm.r.pi.av
      resSigmaR[i,4]  <- round(median(ss$prop.net.pct.rec), digits = 3)
      resSigmaR[i,5]  <- round(median(ss$prop.sim.epidemic.duration), digits = 3)
    }

    # AVERAGE DEGREES
    rsSigmaR <- subsetsByColumnValues(data = rsData,
                                      col1 = 'param.cidm.sigma.av',
                                      col2 = 'param.cidm.r.sigma.av',
                                      col3 = 'param.cidm.r.pi.av')
    for (rs in rsSigmaR) {
      # average degree prior to epidemic
      resSigmaR$degree.pre.epidemic[
        resSigmaR$sigma == head(rs, 1)$param.cidm.sigma.av
        & resSigmaR$r.sigma == head(rs, 1)$param.cidm.r.sigma.av
        & resSigmaR$r.pi == head(rs, 1)$param.cidm.r.pi.av
        ] <- round(median(subset(rs, prop.sim.round == 10)$prop.net.av.degree), digits = 3)
      # average minimum degree during epidemic
      resSigmaR$degree.min[
        resSigmaR$sigma == head(rs, 1)$param.cidm.sigma.av
        & resSigmaR$r.sigma == head(rs, 1)$param.cidm.r.sigma.av
        & resSigmaR$r.pi == head(rs, 1)$param.cidm.r.pi.av
        ] <- round(median(aggregate(prop.net.av.degree ~ param.sim.uid, data = rs, min)$prop.net.av.degree), digits = 3)
    }
    exportDataFrame(resSigmaR, "descriptives-sigma-rsigma-rpi")
  }
}


############################## SCRIPT COMPOSITION FOR COMPLETE EXPORT ################################
#----------------------------------------------------------------------------------------------------#
# function: exportAll
#     Exports all analyses. Note: Regressions are complete. Regressions with selected parameters
#     and interaction effects need to be exported individually using methods
#     'exportAttackRateModelsSelected()' and 'exportDurationModelsSelected()'!
#----------------------------------------------------------------------------------------------------#
exportAll <- function() {

  print("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::")
  print(paste(":: BEGINNING TO ANALYZE DATA IN: ", DATA_PATH, sep = ""))

  print(":::: DATA IMPORT")
  print(paste(":::::: Importing ", CSV_ROUND_SUMMARY_PATH, sep = ""))
  rsData <- reduceRoundSummaryData(loadRoundSummaryData(), cropTail = 20)
  print(paste(":::::: Importing ", CSV_SUMMARY_PATH, ":", sep = ""))
  ssData <- loadSimulationSummaryData()

  print(":::: PLOT EXPORT")
  print(":::::: Exporting individual plots:")
  exportPlots(rsData = rsData)
  print(":::::: Exporting grid plots:")
  exportGridPlots(rsData = rsData)

  print(":::: NUMERICAL ANALYSES")
  print(":::::: Exporting complete regression models..")
  exportRegressionModelsComplete(ssData = ssData)
  print(":::::: Exporting selected regression models..")
  exportRegressionModelsSelected(ssData = ssData)
  print(":::::: Exporting network size measures..")
  exportNetworkSizeMeasures(ssData = ssData, rsData = rsData)
  print(":::::: Exporting epidemic measures..")
  exportEpidemicMeasures(ssData = ssData, rsData = rsData)

  print(paste(":: ANALYSIS FINISHED SUCCESSFULLY!", sep = ""))
  print("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::")

  quit()
}


####################################### COMMAND LINE EXECUTION #######################################
if (length(args) >= 1) {
  exportAll()
}
