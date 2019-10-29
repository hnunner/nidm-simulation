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
  pDegree <- pDegree + geom_smooth(aes(y = ssData$net.clustering.av),
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
                               expression(paste("Open vs. closed triads (", α,")",sep="")),
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



####################################### COMMAND LINE EXECUTION #######################################
if (length(args) >= 1) {
  print("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::")
  print(paste(":: BEGINNING TO ANALYZE DATA IN: ", DATA_PATH, sep = ""))
  print(":::::: Loading simulation summary data..")
  ssData <- loadSimulationSummaryData()
  print(":::::: Exporting summary statistics..")
  exportSummary(ssData = ssData)
  print(":::::: Exporting complete regression models..")
  exportRegressionModels(ssData = ssData)
  print(":::::: Exporting plots..")
  exportPlots()
  print(":: ANALYSIS FINISHED SUCCESSFULLY!")
  print("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::")
}
