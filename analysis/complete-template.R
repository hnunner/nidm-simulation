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
# input/output directory
DATA_PATH                   <- ""
args = commandArgs(trailingOnly=TRUE)
if (length(args) == 0) {
  DATA_PATH                 <- paste(dirname(sys.frame(1)$ofile), "/", sep = "")
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
  roundsToDrop <- max(subset(rsData, sim.stage == 'pre-epidemic')$sim.round) - 10
  # reduce overall amount of rounds
  reducedRsData$sim.round <- reducedRsData$sim.round - roundsToDrop
  # drop all rounds with ids lower than 1
  reducedRsData <- subset(reducedRsData, sim.round > 0)

  # 2. drop rounds of post-epidemic stage
  postEpidemicData <- subset(reducedRsData, sim.stage == "post-epidemic")
  # deterimine the round when post-epidemic stage begins for each simulation
  roundsPerSimulation <- postEpidemicData[!duplicated(postEpidemicData$sim.uid),]$sim.round
  # take the latest round minus a threshold
  roundsToPlot <- max(roundsPerSimulation) - cropTail
  # increase until the amount of rounds is dividable by 10, so that plots can have
  # ticks at a stepsize of 10
  while (roundsToPlot %% 10 != 0) {
    roundsToPlot <- roundsToPlot + 1
  }
  reducedRsData <- subset(reducedRsData, sim.round <= roundsToPlot)
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
  rounds                <- min(rsData$sim.round):max(rsData$sim.round)

  ### SIR data
  # preparations :: statistical summaries per compartment
  summarySus            <- as.data.frame(do.call(rbind, with(rsData, tapply(net.pct.sus, sim.round, summary))))
  summaryInf            <- as.data.frame(do.call(rbind, with(rsData, tapply(net.pct.inf, sim.round, summary))))
  summaryRec            <- as.data.frame(do.call(rbind, with(rsData, tapply(net.pct.rec, sim.round, summary))))

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
    summaryDensities    <- as.data.frame(do.call(rbind, with(rsData, tapply(net.density, sim.round, summary))))

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
    summaryDegrees      <- as.data.frame(do.call(rbind, with(rsData, tapply(net.degree.av, sim.round, summary))))

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
                                         col1 = 'cidm.beta.av',
                                         col2 = 'cidm.mu.av')
  for (subsetBetaMu in subsetsBetaMu) {
    plot <- plotSIRDevelopment(subsetBetaMu, showLegend, showRibbons, showDegree, showDensity, showAxes)

    filepath <- paste(EXPORT_PATH_PLOTS,
                      filePrefix,
                      "beta", unique(subsetBetaMu$cidm.beta.av),
                      "-mu", unique(subsetBetaMu$cidm.mu.av),
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
  if (unique(rsData$cidm.rs.equal) == 1) {
    # r_sigma == r_pi :: only one set of plots
    subsetsRS <- subsetsByColumnValues(data = rsData,
                                       col1 = 'cidm.r.sigma.av',
                                       col2 = 'cidm.sigma.av')
    for (subsetRS in subsetsRS) {
      plot <- plotSIRDevelopment(subsetRS, showLegend, showRibbons, showDegree, showDensity, showAxes)

      filepath <- paste(EXPORT_PATH_PLOTS,
                        filePrefix,
                        "r", unique(subsetRS$cidm.r.sigma.av),
                        "-sigma", unique(subsetRS$cidm.sigma.av),
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
                                       col1 = 'cidm.r.sigma.av',
                                       col2 = 'cidm.sigma.av')
    for (subsetRS in subsetsRS) {
      plot <- plotSIRDevelopment(subsetRS, showLegend, showRibbons, showDegree, showDensity, showAxes)

      filepath <- paste(EXPORT_PATH_PLOTS,
                        filePrefix,
                        "rsigma", unique(subsetRS$cidm.r.sigma.av),
                        "-sigma", unique(subsetRS$cidm.sigma.av),
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
                                       col1 = 'cidm.r.pi.av',
                                       col2 = 'cidm.sigma.av')
    for (subsetRS in subsetsRS) {
      plot <- plotSIRDevelopment(subsetRS, showLegend, showRibbons, showDegree, showDensity, showAxes)

      filepath <- paste(EXPORT_PATH_PLOTS,
                        filePrefix,
                        "rpi", unique(subsetRS$cidm.r.pi.av),
                        "-sigma", unique(subsetRS$cidm.sigma.av),
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
          body.tag = TRUE
          )
}

#----------------------------------------------------------------------------------------------------#
# function: exportAttackRateModelsComplete
#     Creates and exports multi-level logistic regression models for attack rate with all possible
#     parameters and interaction effects.
# param:  ssData
#     simulation summary data to get regression models for
#----------------------------------------------------------------------------------------------------#
# REPLACE LINE WITH GENERATED 'exportRegressionModelsComplete'

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
  ### COPY MODELS FROM ABOVE AND COMMENT OUT INSIGNIFICANT OR IMPLAUSIBLE PARAMETERS
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

  peakData <- data.frame(rsData$cidm.N,
                         rsData$sim.uid,
                         rsData$sim.round-10,
                         rsData$net.pct.inf)
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

  for (N in unique(ssData$cidm.N)) {
    ssN <- subset(ssData, cidm.N == N)
    # network size
    netSizes <- c(netSizes, N)
    # density
    densities <- c(densities, round(mean(ssN$net.density.pre.epidemic), 3))
    sdDensities <- c(sdDensities, round(sd(ssN$net.density.pre.epidemic), 3))
    # degree
    degrees <- c(degrees, round(mean(ssN$net.av.degree.pre.epidemic), 3))
    sdDegrees <- c(sdDegrees, round(sd(ssN$net.av.degree.pre.epidemic), 3))
    # attack rate
    attackRates <- c(attackRates, round(mean(ssN$net.pct.rec), 3))
    sdAttackRates <- c(sdAttackRates, round(sd(ssN$net.pct.rec), 3))
    # duration
    durations <- c(durations, round(mean(ssN$sim.epidemic.duration), 3))
    sdDurations <- c(sdDurations, round(sd(ssN$sim.epidemic.duration), 3))
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
  resBetaMu <- data.frame(matrix(0, ncol= 6, nrow=length(unique(ssData$cidm.beta.av))*length(unique(ssData$cidm.mu.av))))
  colnames(resBetaMu) <- c("beta", "mu", "attack.rate", "duration", "degree.pre.epidemic", "degree.min")

  # ATTACK RATE, DURATION
  ssBetaMu <- subsetsByColumnValues(data = ssData,
                                    col1 = 'cidm.beta.av',
                                    col2 = 'cidm.mu.av')
  for (i in 1:length(ssBetaMu)) {
    ss <- ssBetaMu[[i]]
    resBetaMu[i,1]  <- head(ss, 1)$cidm.beta.av
    resBetaMu[i,2]  <- head(ss, 1)$cidm.mu.av
    resBetaMu[i,3]  <- round(median(ss$net.pct.rec), digits = 3)
    resBetaMu[i,4]  <- round(median(ss$sim.epidemic.duration), digits = 3)
  }

  # AVERAGE DEGREES
  rsBetaMu <- subsetsByColumnValues(data = rsData,
                                    col1 = 'cidm.beta.av',
                                    col2 = 'cidm.mu.av')
  for (rs in rsBetaMu) {
    # average degree prior to epidemic
    resBetaMu$degree.pre.epidemic[
      resBetaMu$beta == head(rs, 1)$cidm.beta.av
      & resBetaMu$mu == head(rs, 1)$cidm.mu.av
      ] <- round(median(subset(rs, sim.round == 10)$net.degree.av), digits = 3)
    # average minimum degree during epidemic
    resBetaMu$degree.min[
      resBetaMu$beta == head(rs, 1)$cidm.beta.av
      & resBetaMu$mu == head(rs, 1)$cidm.mu
      ] <- round(median(aggregate(net.degree.av ~ sim.uid, data = rs, min)$net.degree.av), digits = 3)
  }
  exportDataFrame(resBetaMu, "descriptives-beta-mu")


  ##### SIGMA-R #####
  if (unique(rsData$cidm.rs.equal) == 1) {
    # results data frame
    resSigmaR <- data.frame(matrix(0, ncol= 6, nrow=length(unique(ssData$cidm.sigma.av))
                                   *length(unique(ssData$cidm.r.sigma.av))))
    colnames(resSigmaR) <- c("sigma", "r", "attack.rate", "duration", "degree.pre.epidemic", "degree.min")

    # ATTACK RATE, DURATION
    ssSigmaR <- subsetsByColumnValues(data = ssData,
                                      col1 = 'cidm.sigma.av',
                                      col2 = 'cidm.r.sigma.av')
    for (i in 1:length(ssSigmaR)) {
      ss <- ssSigmaR[[i]]
      resSigmaR[i,1]  <- head(ss, 1)$cidm.sigma.av
      resSigmaR[i,2]  <- head(ss, 1)$cidm.r.sigma.av
      resSigmaR[i,3]  <- round(median(ss$net.pct.rec), digits = 3)
      resSigmaR[i,4]  <- round(median(ss$sim.epidemic.duration), digits = 3)
    }

    # AVERAGE DEGREES
    rsSigmaR <- subsetsByColumnValues(data = rsData,
                                      col1 = 'cidm.sigma.av',
                                      col2 = 'cidm.r.sigma.av')
    for (rs in rsSigmaR) {
      # average degree prior to epidemic
      resSigmaR$degree.pre.epidemic[
        resSigmaR$sigma == head(rs, 1)$cidm.sigma.av
        & resSigmaR$r.combined == head(rs, 1)$cidm.r.sigma.av
        ] <- round(median(subset(rs, sim.round == 10)$net.degree.av), digits = 3)
      # average minimum degree during epidemic
      resSigmaR$degree.min[
        resSigmaR$sigma == head(rs, 1)$cidm.sigma.av
        & resSigmaR$r.combined == head(rs, 1)$cidm.r.sigma.av
        ] <- round(median(aggregate(net.degree.av ~ sim.uid, data = rs, min)$net.degree.av), digits = 3)
    }
    exportDataFrame(resSigmaR, "descriptives-sigma-rcombined")
  } else {
    # results data frame
    resSigmaR <- data.frame(matrix(0, ncol= 7, nrow=length(unique(ssData$cidm.sigma.av))
                                   *length(unique(ssData$cidm.r.sigma.av))
                                   *length(unique(ssData$cidm.r.pi.av))))
    colnames(resSigmaR) <- c("sigma", "r.sigma", "r.pi", "attack.rate", "duration", "degree.pre.epidemic", "degree.min")

    # ATTACK RATE, DURATION
    ssSigmaR <- subsetsByColumnValues(data = ssData,
                                      col1 = 'cidm.sigma.av',
                                      col2 = 'cidm.r.sigma.av',
                                      col3 = 'cidm.r.pi.av')
    for (i in 1:length(ssSigmaR)) {
      ss <- ssSigmaR[[i]]
      resSigmaR[i,1]  <- head(ss, 1)$cidm.sigma.av
      resSigmaR[i,2]  <- head(ss, 1)$cidm.r.sigma.av
      resSigmaR[i,3]  <- head(ss, 1)$cidm.r.pi.av
      resSigmaR[i,4]  <- round(median(ss$net.pct.rec), digits = 3)
      resSigmaR[i,5]  <- round(median(ss$sim.epidemic.duration), digits = 3)
    }

    # AVERAGE DEGREES
    rsSigmaR <- subsetsByColumnValues(data = rsData,
                                      col1 = 'cidm.sigma.av',
                                      col2 = 'cidm.r.sigma.av',
                                      col3 = 'cidm.r.pi.av')
    for (rs in rsSigmaR) {
      # average degree prior to epidemic
      resSigmaR$degree.pre.epidemic[
        resSigmaR$sigma == head(rs, 1)$cidm.sigma.av
        & resSigmaR$r.sigma == head(rs, 1)$cidm.r.sigma.av
        & resSigmaR$r.pi == head(rs, 1)$cidm.r.pi.av
        ] <- round(median(subset(rs, sim.round == 10)$net.degree.av), digits = 3)
      # average minimum degree during epidemic
      resSigmaR$degree.min[
        resSigmaR$sigma == head(rs, 1)$cidm.sigma.av
        & resSigmaR$r.sigma == head(rs, 1)$cidm.r.sigma.av
        & resSigmaR$r.pi == head(rs, 1)$cidm.r.pi.av
        ] <- round(median(aggregate(net.degree.av ~ sim.uid, data = rs, min)$net.degree.av), digits = 3)
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
