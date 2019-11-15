#!/usr/bin/env Rscript

# Copyright (C) 2017 - 2019
#     Hendrik Nunner    <h.nunner@gmail.com>
#
# This file is part of the NIDM-Simulation project <https://github.com/hnunner/NIDM-simulation>.
#
# This project is a stand-alone Java program of the Networking during Infectious Diseases Model
# (NIDM; Nunner, Buskens, & Kretzschmar, 2019) to simulate the dynamic interplay of social network
# formation and infectious diseases.
#
# This program is free software: you can redistribute it and/or modify it under the
# terms of the GNU General Public License as published by the Free Software Foundation,
# either version 3 of the License, or (at your option) any later version.
#
# This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
# without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
# See the GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License along with this program.
# If not, see <http://www.gnu.org/licenses/>.
#
# References:
#     Nunner, H., Buskens, V., & Kretzschmar, M. (2019). A model for the co-evolution of dynamic
#     social networks and infectious diseases. Manuscript sumbitted for publication.


############################################## LIBRARIES ##############################################
sourceLibs <- function(libs) {
  for (lib in libs) {
    if(lib %in% rownames(installed.packages()) == FALSE) {install.packages(lib)}
    library(lib, character.only = TRUE)
  }
}
sourceLibs(c("ggplot2",     # plots
             "gridExtra",   # side-by-side plots
             "QuantPsyc",   # 'meanCenter' function
             "lme4",        # regression analyses
             "sjstats",     # "icc" function
             "texreg",      # html export
             "psych"        # summary statistics
))


########################################### GLOBAL CONSTANTS ##########################################
# input/output directory
DATA_PATH                   <- ""
args = commandArgs(trailingOnly=TRUE)
if (length(args) == 0) {
  DATA_PATH                 <- paste(dirname(sys.frame(1)$ofile), "/", sep = "")
} else {
  DATA_PATH                 <- args[1]
}
# file names of generated data
CSV_SUMMARY_PATH            <- paste(DATA_PATH, "simulation-summary.csv", sep = "")
# export - file system
EXPORT_DIR_NUMERIC          <- "numeric/"
EXPORT_PATH_NUMERIC         <- paste(DATA_PATH, EXPORT_DIR_NUMERIC, sep = "")
EXPORT_FILE_TYPE_REG        <- "html"
EXPORT_FILE_EXTENSION_REG   <- paste(".", EXPORT_FILE_TYPE_REG, sep = "")
EXPORT_FILE_TYPE_SUMMARY    <- "csv"
EXPORT_FILE_EXTENSION_SUMMARY <- paste(".", EXPORT_FILE_TYPE_SUMMARY, sep = "")
EXPORT_DIR_PLOTS            <- "figures/"
EXPORT_PATH_PLOTS           <- paste(DATA_PATH, EXPORT_DIR_PLOTS, sep = "")
EXPORT_FILE_TYPE_PLOTS      <- "png"
EXPORT_FILE_EXTENSION_PLOTS <- paste(".", EXPORT_FILE_TYPE_PLOTS, sep = "")
# export image settings
EXPORT_PLOT_WIDTH           <- 250
EXPORT_PLOT_HEIGHT          <- 400
EXPORT_SIZE_UNITS           <- "mm"
EXPORT_DPI                  <- 1200
SCATTER_ALPHA               <- 0.1
COLOR_STABLE_POINT          <- "grey50"
COLOR_STABLE_SMOOTH         <- "grey30"
COLOR_DEGREE_POINT          <- "green3"
COLOR_DEGREE_SMOOTH         <- "green4"
COLOR_CLUSTERING_POINT      <- "royalblue2"
COLOR_CLUSTERING_SMOOTH     <- "royalblue3"
COLOR_PATHLENGTH_POINT      <- "red3"
COLOR_PATHLENGTH_SMOOTH     <- "red4"

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
# function: exportModels
#     Creates file outputs for regression models (comparison of models, ICCs).
# param:  models
#     the models to create outputs for
#         modelNames
#     the names of the models
#         filename:
#     the name of the output file
#----------------------------------------------------------------------------------------------------#
exportModels <- function(models, modelNames, filename) {

  filepath <- paste(EXPORT_PATH_NUMERIC,
                    filename,
                    EXPORT_FILE_EXTENSION_REG,
                    sep = "")

  # create directory if necessary
  dir.create(EXPORT_PATH_NUMERIC, showWarnings = FALSE)

  # close standard notes and begin new standard row
  notes <- "</span></td>\n</tr>\n<tr>\n"
  # correlation stars
  notes <- paste(notes,
                 "<tr>\n<td colspan=\"", length(models)+1,
                 "\"><span style=\"font-size:0.8em\">",
                 "<sup>***</sup>p &lt; 0.001, <sup>**</sup>p &lt; 0.01, <sup>*</sup>p &lt; 0.05</span></td>", sep = "")

  htmlreg(models,
          custom.model.names = modelNames,
          filepath,
          custom.note = notes,
          inline.css = FALSE,
          doctype = TRUE,
          html.tag = TRUE,
          head.tag = TRUE,
          body.tag = TRUE
  )
}


############################################# REGRESSIONS ############################################
exportRegressionModels <- function(ssData = loadSimulationSummaryData()) {

  # MAIN EFFECTS
  # independent variables
  b1                                 <- meanCenter(ssData$nb.b1)
  b2                                 <- meanCenter(ssData$nb.b2)
  alpha                              <- meanCenter(ssData$nb.alpha)
  c1                                 <- meanCenter(ssData$nb.c1)
  c2                                 <- meanCenter(ssData$nb.c2)
  yglobal                            <- meanCenter(ssData$nb.yglobal)
  N                                  <- meanCenter(ssData$nb.N)
  iota                               <- meanCenter(ssData$nb.iota)
  phi                                <- meanCenter(ssData$nb.phi)
  # dependent variables
  stable                             <- meanCenter(ssData$net.stable)
  av.degree                          <- meanCenter(ssData$net.degree.av)
  av.clustering                      <- meanCenter(ssData$net.clustering.av)
  av.pathlength                      <- meanCenter(ssData$net.pathlength.av)


  ### LINEAR REGRESSIONS ###
  # stability
  regStable <- lm(stable ~ b1 + b2 + alpha + c1 + c2 + yglobal + N + iota + phi, data = ssData)
  # average degree
  regDegree <- lm(av.degree ~ b1 + b2 + alpha  + c1 + c2 + yglobal + N + iota + phi, data = ssData)
  # average clustering
  regClustering <- lm(av.clustering ~ b1 + b2 + alpha  + c1 + c2 + yglobal + N + iota + phi, data = ssData)
  # average path length
  regPathLength <- lm(av.pathlength ~ b1 + b2 + alpha  + c1 + c2 + yglobal + N + iota + phi, data = ssData)

  # export
  exportModels(list(regStable, regDegree, regClustering, regPathLength),
               c("Stability", "Av. degree", "Clustering", "Av. path length"),
               "reg-combined")
}


exportSummary <- function(ssData = loadSimulationSummaryData()) {
  dir.create(EXPORT_PATH_NUMERIC, showWarnings = FALSE)

  # total
  filepathTotal <- paste(EXPORT_PATH_NUMERIC,
                         "summary",
                         EXPORT_FILE_EXTENSION_SUMMARY,
                         sep = "")
  write.csv(describe(ssData), filepathTotal)

  # grouped by network size
  filepathByN <- paste(EXPORT_PATH_NUMERIC,
                       "summary-by-N",
                       EXPORT_FILE_EXTENSION_SUMMARY,
                       sep = "")
  write.csv(do.call("rbind",(describeBy(ssData, group = ssData$nb.N))), filepathByN)
}


exportOverviewParameters <- function(ssData = loadSimulationSummaryData()) {

  b1 <- c()
  alpha <- c()
  observations <- c()
  exclusions <- c()
  average.degree <- c()
  clustering <- c()
  average.path.length <- c()

  for (currB1 in seq(1.00, 1.20, 0.01)) {
    for (currAlpha in seq(0.5, 0.95, 0.01)) {

      ssDataSub  <- subset(ssData,
                           ssData$nb.b1 >= currB1
                           & ssData$nb.b1 < (currB1 + 0.01)
                           & ssData$nb.alpha >= currAlpha
                           & ssData$nb.alpha < (currAlpha + 0.01))

      ssDataSub2 <- subset(ssDataSub, ssDataSub$net.pathlength.av > 1.0)

      obs <- nrow(ssDataSub2)
      exc <- nrow(ssDataSub) - nrow(ssDataSub2)

      if (obs + exc > 0) {
        b1                    <- c(b1, currB1)
        alpha                 <- c(alpha, currAlpha)
        observations          <- c(observations, obs)
        exclusions            <- c(exclusions, exc)
        average.degree        <- c(average.degree, mean(ssDataSub2$net.degree.av))
        clustering            <- c(clustering, mean(ssDataSub2$net.clustering.av))
        average.path.length   <- c(average.path.length, mean(ssDataSub2$net.pathlength.av))
      }
    }
  }

  overviewParams <- data.frame(b1,
                               alpha,
                               observations,
                               exclusions,
                               average.degree,
                               clustering,
                               average.path.length)

  # csv
  filepathTotal <- paste(EXPORT_PATH_NUMERIC,
                         "overview-parameters",
                         EXPORT_FILE_EXTENSION_SUMMARY,
                         sep = "")
  write.csv(overviewParams, filepathTotal)
}

################################################ PLOTS ###############################################
getPlotRow <- function(ssData = loadSimulationSummaryData(),
                       xAxis,
                       xTitle,
                       dotSize = 1,
                       includeStability = TRUE) {

  # stability
  if (includeStability) {
    pStability <- ggplot(ssData, aes(x = xAxis))
    pStability <- pStability + geom_point(aes(y = ssData$net.stable),
                                          size = dotSize,
                                          alpha = SCATTER_ALPHA,
                                          colour = COLOR_STABLE_POINT)
    pStability <- pStability + geom_smooth(aes(y = ssData$net.stable),
                                           method = "lm",
                                           color = COLOR_STABLE_SMOOTH,
                                           se=FALSE)
    pStability <- pStability + labs(y = "Network stability",
                                    x = xTitle)
  }

  # average degree
  pDegree <- ggplot(ssData, aes(x = xAxis))
  pDegree <- pDegree + geom_point(aes(y = ssData$net.degree.av),
                                  size = dotSize,
                                  alpha = SCATTER_ALPHA,
                                  colour = COLOR_DEGREE_POINT)
  pDegree <- pDegree + geom_smooth(aes(y = ssData$net.degree.av),
                                   method = "lm",
                                   color = COLOR_DEGREE_SMOOTH,
                                   se=FALSE)
  pDegree <- pDegree + labs(y = "Average degree",
                            x = xTitle)

  # clustering
  pClustering <- ggplot(ssData, aes(x = xAxis))
  pClustering <- pClustering + geom_point(aes(y = ssData$net.clustering.av),
                                          size = dotSize,
                                          alpha = SCATTER_ALPHA,
                                          colour = COLOR_CLUSTERING_POINT)
  pClustering <- pClustering + geom_smooth(aes(y = ssData$net.clustering.av),
                                           method = "lm",
                                           color = COLOR_CLUSTERING_SMOOTH,
                                           se=FALSE)
  pClustering <- pClustering + labs(y = "Average clustering",
                                    x = xTitle)

  # path length
  pPathLength <- ggplot(ssData, aes(x = xAxis))
  pPathLength <- pPathLength + geom_point(aes(y = ssData$net.pathlength.av),
                                          size = dotSize,
                                          alpha = SCATTER_ALPHA,
                                          colour = COLOR_PATHLENGTH_POINT)
  pPathLength <- pPathLength + geom_smooth(aes(y = ssData$net.pathlength.av),
                                           method = "lm",
                                           color = COLOR_PATHLENGTH_SMOOTH,
                                           se=FALSE)
  pPathLength <- pPathLength + labs(y = "Average path length",
                                    x = xTitle)

  if (includeStability) {
    return(list(pStability, pDegree, pClustering, pPathLength))
  } else {
    return(list(pDegree, pClustering, pPathLength))
  }
}



getPlots <- function(ssData = loadSimulationSummaryData(), includeStability = TRUE, includeN = TRUE) {

  plots <- getPlotRow(ssData,
                      ssData$nb.b1,
                      expression(paste("Social benefits (", b[1],")",sep="")),
                      1,
                      includeStability)

  plots <- c(plots, getPlotRow(ssData,
                               ssData$nb.b2,
                               expression(paste("Weight of triadic benefits (", b[2],")",sep="")),
                               1,
                               includeStability))

  plots <- c(plots, getPlotRow(ssData,
                               ssData$nb.alpha,
                               expression(paste("Open vs. closed triads (", alpha,")",sep="")),
                               1,
                               includeStability))

  plots <- c(plots, getPlotRow(ssData,
                               ssData$nb.c1,
                               expression(paste("Normal social costs (", c[1],")",sep="")),
                               1,
                               includeStability))

  plots <- c(plots, getPlotRow(ssData,
                               ssData$nb.c2,
                               expression(paste("Quadratic social costs (", c[2],")",sep="")),
                               1,
                               includeStability))

  plots <- c(plots, getPlotRow(ssData,
                               ssData$nb.yglobal,
                               "Global open triads",
                               1,
                               includeStability))

  if (includeN) {
    plots <- c(plots, getPlotRow(ssData,
                                 ssData$nb.N,
                                 "Network size (N)",
                                 1,
                                 includeStability))
  }

  plots <- c(plots, getPlotRow(ssData,
                               ssData$nb.iota,
                               expression(paste("Start with complete network (", iota,")",sep="")),
                               1,
                               includeStability))

  plots <- c(plots, getPlotRow(ssData,
                               ssData$nb.phi,
                               expression(paste("Share to evaluate (", phi,")",sep="")),
                               1,
                               includeStability))
}

exportPlots <- function(ssData = loadSimulationSummaryData(),
                        plotWidth = EXPORT_PLOT_WIDTH,
                        plotHeight = EXPORT_PLOT_HEIGHT) {

  # create directory if necessary
  dir.create(EXPORT_PATH_PLOTS, showWarnings = FALSE)

  ggsave(paste(EXPORT_PATH_PLOTS,
               "grid-complete",
               EXPORT_FILE_EXTENSION_PLOTS,
               sep = ""),
         do.call("grid.arrange",
                 c(getPlots(ssData = ssData),
                   ncol=4)),
         width = plotWidth,
         height = plotHeight,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)

  ggsave(paste(EXPORT_PATH_PLOTS,
               "grid-complete-stable-only",
               EXPORT_FILE_EXTENSION_PLOTS,
               sep = ""),
         do.call("grid.arrange",
                 c(getPlots(ssData = subset(ssData, ssData$net.stable == 1),
                            includeStability = FALSE),
                   ncol=3)),
         width = plotWidth,
         height = plotHeight,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)

  ggsave(paste(EXPORT_PATH_PLOTS,
               "grid-N20",
               EXPORT_FILE_EXTENSION_PLOTS,
               sep = ""),
         do.call("grid.arrange",
                 c(getPlots(ssData = subset(ssData, ssData$nb.N == 20),
                            includeN = FALSE),
                   ncol=4)),
         width = plotWidth,
         height = plotHeight,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)

  ggsave(paste(EXPORT_PATH_PLOTS,
               "grid-N20-stable-only",
               EXPORT_FILE_EXTENSION_PLOTS,
               sep = ""),
         do.call("grid.arrange",
                 c(getPlots(ssData = subset(ssData, ssData$nb.N == 20 & ssData$net.stable == 1),
                            includeN = FALSE,
                            includeStability = FALSE),
                   ncol=3)),
         width = plotWidth,
         height = plotHeight,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)
}



fct <- function() {

  ssData <- loadSimulationSummaryData()
  ssData <- subset(ssData, ssData$nb.b2 == 1.0)

  ###### EXCLUSIONS ######
  exclusions        <- c()
  exclusion.groups  <- unique(ssData$nb.alpha)

  for (alpha in exclusion.groups) {
    ssDataByAlpha   <- subset(ssData, ssData$nb.alpha == alpha)
    exclusions      <- c(exclusions,
                         nrow(subset(ssDataByAlpha, ssDataByAlpha$net.pathlength.av < 1)) /
                           nrow(ssDataByAlpha)
                         )
  }

  data.exclusions <- data.frame(exclusion.groups, exclusions)
  # bar plot
  p.density.exclusions  <- ggplot(data.exclusions, aes(x=factor(exclusion.groups),
                                                       y = exclusions,
                                                       fill=factor(exclusion.groups))) +
    geom_bar(stat = "identity", alpha=.8) +
    theme_minimal()
  p.density.exclusions
  # summary
  describeBy(data.exclusions, group = data.exclusions$exclusion.groups)


  ###### DATA PREPARATION ######
  ssDataNoExclusions <- subset(ssData, ssData$net.pathlength.av > 1)
  groups          <- c()
  n               <- c()
  degrees         <- c()
  degrees.mean    <- c()
  clusters        <- c()
  path.lengths    <- c()
  densities       <- c()

  for (alpha in unique(ssDataNoExclusions$nb.alpha)) {
    ssDataByAlpha <- subset(ssDataNoExclusions, ssDataNoExclusions$nb.alpha == alpha)
    groups        <- c(groups, rep(paste("alpha", alpha), nrow(ssDataByAlpha)))
    n             <- c()

    degrees       <- c(degrees, ssDataByAlpha$net.degree.av)
    degrees.mean  <- c(degrees.mean, mean(ssDataByAlpha$net.degree.av))
    clusters      <- c(clusters, ssDataByAlpha$net.clustering.av)
    path.lengths  <- c(path.lengths, ssDataByAlpha$net.pathlength.av)
    densities     <- c(densities, ssDataByAlpha$net.density)
  }

  ###### SUMMARY ######
  data.summary <- data.frame(
    alpha = unique(ssDataNoExclusions$nb.alpha),
    n =
    degrees.mean)
  data.summary


  ###### PLOTS ######
  data.plots <- data.frame(groups, degrees, clusters, path.lengths, densities)
  ### AVERAGE DEGREES
  p.density.degrees   <- ggplot(data.plots, aes(x=degrees, fill=groups)) +
    geom_density(alpha=.4)
  p.density.degrees

  ### CLUSTERING
  p.density.clusters    <- ggplot(data.plots, aes(x=clusters, fill=groups)) +
    geom_density(alpha=.4)
  p.density.clusters

  ### AVERAGE PATH LENGTH
  p.density.path.length    <- ggplot(data.plots, aes(x=path.lengths, fill=groups)) +
    geom_density(alpha=.4)
  p.density.path.length

  ### DENSITY
  p.density.density    <- ggplot(data.plots, aes(x=densities, fill=groups)) +
    geom_density(alpha=.4)
  p.density.density

  ### alternative: histograms
  # p.histogram.density  <- ggplot(data.plots, aes(x=densities, fill=groups)) +
  #   geom_histogram(binwidth=.1, alpha=0.6, position = 'dodge')  #, position = 'identity')
  # p.histogram.density


}



####################################### COMMAND LINE EXECUTION #######################################
if (length(args) >= 1) {
  print("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::")
  print(paste(":: BEGINNING TO ANALYZE DATA IN: ", DATA_PATH, sep = ""))
  print(":::::: Loading simulation summary data..")
  ssData <- loadSimulationSummaryData()
  print(":::::: Exporting summary statistics..")
  exportSummary(ssData = ssData)
  print(":::::: Exporting overview of parameters..")
  exportOverviewParameters(ssData = ssData)
  print(":::::: Exporting complete regression models..")
  exportRegressionModels(ssData = ssData)
  print(":::::: Exporting plots..")
  exportPlots()
  print(":: ANALYSIS FINISHED SUCCESSFULLY!")
  print("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::")
}
