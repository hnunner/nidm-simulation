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
source_libs <- function(libs) {
  for (lib in libs) {
    if(lib %in% rownames(installed.packages()) == FALSE) {install.packages(lib)}
    library(lib, character.only = TRUE)
  }
}
source_libs(c("ggplot2",      # plots
              "reshape2",     # melt function
              "lme4",         # regression analyses
              "sjstats",      # "icc" function
              "texreg",       # html export
              "QuantPsyc",    # 'meanCenter' function
              "dplyr",        # 'select' function
              "ggpubr",       # arranging a list of plots with one common legend
              "e1071",        # skewness
              "hexbin",       # hexbin plots
              "Cairo",        # smooth plot lines
              "rsq",          # adjusted R2
              "car"           # VIFs
              # "gridExtra",   # side-by-side plots
              # "psych",       # summary statistics
))


########################################### GLOBAL CONSTANTS ##########################################
### DATA ###
CUT_OFF_LARGE_ATTACK_RATE     <- 90

### IO ###
# input/output directory
DATA_PATH                   <- ""
args = commandArgs(trailingOnly=TRUE)
if (length(args) == 0) {
  DATA_PATH                     <- paste(dirname(sys.frame(1)$ofile), "/", sep = "")
} else {
  DATA_PATH                     <- args[1]
}
# file names of generated data
CSV_SUMMARY_PATH                <- paste(DATA_PATH, "simulation-summary.csv", sep = "")
CSV_AGENT_DETAILS_PATH          <- paste(DATA_PATH, "agent-details.csv", sep = "")
CSV_AGENT_DETAILS_PREPARED_PATH <- paste(DATA_PATH, "agent-details-prepared.csv", sep = "")
# export files
EXPORT_DIR_NUM                  <- "numerical/"
EXPORT_PATH_NUM                 <- paste(DATA_PATH, EXPORT_DIR_NUM, sep = "")
EXPORT_FILE_TYPE_REG            <- "html"
EXPORT_FILE_EXTENSION_REG       <- paste(".", EXPORT_FILE_TYPE_REG, sep = "")
EXPORT_FILE_TYPE_DESC           <- "txt"
EXPORT_FILE_EXTENSION_DESC      <- paste(".", EXPORT_FILE_TYPE_DESC, sep = "")
EXPORT_DIR_PLOTS                <- "figures/"
EXPORT_PATH_PLOTS               <- paste(DATA_PATH, EXPORT_DIR_PLOTS, sep = "")
EXPORT_FILE_TYPE_PLOTS          <- "png"
EXPORT_FILE_EXTENSION_PLOTS     <- paste(".", EXPORT_FILE_TYPE_PLOTS, sep = "")

### PLOT APPEARANCE ###
# general
BINS                            <- 100
POINT_SIZE                      <- 0.6
POINT_ALPHA                     <- 0.4
LINE_SIZE                       <- 0.6
COLOR_1                         <- "#D55E00"      # colors (http://mkweb.bcgsc.ca/colorblind/)
LINE_1                          <- "solid"
SHAPE_1                         <- 16
COLOR_2                         <- "#0072B2"      # colors (http://mkweb.bcgsc.ca/colorblind/)
LINE_2                          <- "dashed"
SHAPE_2                         <- 17
SHOW_LEGEND                     <- FALSE
LIMITS_0_1                      <- c(0, 1)
SEQ_0_1                         <- seq(0, 1, 0.25)
LIMITS_0_2                      <- c(0, 2)
SEQ_0_2                         <- seq(0, 2, 0.5)
LIMITS_0_100                    <- c(0, 100)
SEQ_0_100                       <- seq(0, 100, 25)
# risk perception
LABEL_RISKPERCEPTION            <- "Risk perception"
LIMITS_RISKPERCEPTION           <- LIMITS_0_2
BREAKS_RISKPERCEPTION           <- SEQ_0_2
# probability of infection
LABEL_PROBABILITYINFECTION      <- "Infection probability"
LIMITS_PROBABILITYINFECTION     <- LIMITS_0_1
BREAKS_PROBABILITYINFECTION     <- SEQ_0_1
# clustering
LABEL_CLUSTERING                <- "Clustering"
LIMITS_CLUSTERING               <- LIMITS_0_1
BREAKS_CLUSTERING               <- SEQ_0_1
# path length
LABEL_PATHLENGTH                <- "Path length"
LIMITS_PATHLENGTH               <- c(1, 7)
BREAKS_PATHLENGTH               <- seq(1, 7, 1)
# betweenness (normalized)
LABEL_BETWEENNESS               <- "Betweenness (normalized)"
LIMITS_BETWEENNESS              <- LIMITS_0_1
BREAKS_BETWEENNESS              <- SEQ_0_1
# assortativity
LABEL_ASSORTATIVITY             <- "Assortativity"
LIMITS_ASSORTATIVITY            <- LIMITS_0_1
BREAKS_ASSORTATIVITY            <- SEQ_0_1
# attack rate
LABEL_ATTACKRATE                <- "Attack rate"
LIMITS_ATTACKRATE               <- LIMITS_0_100
BREAKS_ATTACKRATE               <- SEQ_0_100
# duration/peak
LABEL_DURATION                  <- "Duration"
LABEL_PEAK                      <- "Peak"
LIMITS_DURATIONPEAK             <- c(0, 30)
BREAKS_DURATIONPEAK             <- seq(0, 30, 5)
LABEL_PEAKSIZE                  <- "Peak size"
LIMITS_PEAKSIZE                 <- c(0, 80)
BREAKS_PEAKSIZE                 <- seq(0, 80, 10)
# network decisions
LABEL_NETDECISIONS              <- "Network decisions"
LIMITS_NETDECISIONS             <- c(0, 5)
BREAKS_NETDECISIONS             <- seq(0, 5, 1)
# export image settings
EXPORT_PLOT_WIDTH_LONG          <- 250
EXPORT_PLOT_WIDTH               <- 40
EXPORT_PLOT_HEIGHT              <- 40
EXPORT_SIZE_UNITS               <- "mm"
EXPORT_DPI                      <- 1200
THEME_BASE_SIZE                 <- 8


##################################### IMPORTS / DATA PREPARATIONS ####################################
#----------------------------------------------------------------------------------------------------#
# function: loadCSV
#     Loads data from a CSV file.
# param:  filePath
#     path to the file to be loaded
# return: the CSV file data as data frame
#----------------------------------------------------------------------------------------------------#
load_csv <- function(filePath) {
  csv <- read.csv(file=filePath, header=TRUE, sep=",")
  return(csv)
}

#----------------------------------------------------------------------------------------------------#
# function: remove_exclusions_simulation_summary_data
#     Removes excluded records from simulation summary data.
# return: the simulation summary data cleaned from excluded data records
#----------------------------------------------------------------------------------------------------#
remove_exclusions_simulation_summary_data <- function(data.ss) {
  data.ss <- subset(data.ss, data.ss$net.pathlength.pre.epidemic.av <= 80)
  return(data.ss)
}

#----------------------------------------------------------------------------------------------------#
# function: load_simulation_summary_data
#     Loads summary data for NIDM simulations.
# return: the summary data for NIDM simulations
#----------------------------------------------------------------------------------------------------#
load_simulation_summary_data <- function(remove_exclusions = TRUE) {
  data.ss <- load_csv(CSV_SUMMARY_PATH)
  if (remove_exclusions) {
    data.ss <- remove_exclusions_simulation_summary_data(data.ss)
  }
  return(data.ss)
}

#----------------------------------------------------------------------------------------------------#
# function: prepare_agent_details_data
#     Removes excluded records from agent details data, keeps only records of finished simulations,
#     and adds a flag whether the agent was infected or not.
# return: the prepared agent detail data
#----------------------------------------------------------------------------------------------------#
prepare_agent_details_data <- function(data.ad) {
  # only data of not excluded records
  data.ad <- subset(data.ad, sim.uid %in% load_simulation_summary_data()$sim.uid)
  # only finished simulations
  data.ad <- subset(data.ad, sim.stage == "finished")
  # add flag whether agent has been infected or not
  data.ad$agent.infected <- ifelse(data.ad$agent.dis.state == "RECOVERED", 1, ifelse(data.ad$agent.dis.state == "INFECTED", 1, 0))
  return(data.ad)
}

#----------------------------------------------------------------------------------------------------#
# function: load_agent_details_data
#     Loads agent details data.
# return: the agent details data
#----------------------------------------------------------------------------------------------------#
load_agent_details_data <- function(prepare_data = TRUE) {
  data.ad <- load_csv(CSV_AGENT_DETAILS_PATH)
  if (prepare_data) {
    data.ad <- prepare_agent_details_data(data.ad)
  }
  return(data.ad)
}

#----------------------------------------------------------------------------------------------------#
# function: load_agent_details_prepared_data
#     Loads readily prepared agent details data.
# return: the agent details data
#----------------------------------------------------------------------------------------------------#
load_agent_details_prepared_data <- function() {
  return(load_csv(CSV_AGENT_DETAILS_PREPARED_PATH))
}







############################################# DESCRIPTIVES ###########################################
#----------------------------------------------------------------------------------------------------#
# function: get_descriptive
#     Gets a single descriptive statistic.
# param:  vec
#     a vector of values to generate the descriptive statistic for.
# param:  title
#     the title/name of the vector
#----------------------------------------------------------------------------------------------------#
get_descriptive <- function(vec, title) {

  out <- paste(title,
               round(mean(vec, na.rm = TRUE), digits = 2),
               round(sd(vec, na.rm = TRUE), digits = 2),
               round(min(vec, na.rm = TRUE), digits = 2),
               round(max(vec, na.rm = TRUE), digits = 2),
               round(skewness(vec, na.rm = TRUE), digits = 2),
               sep = " & ")
  out <- paste(out, " \\", "\\", " \n", sep = "")

  return(out)
}

#----------------------------------------------------------------------------------------------------#
# function: export_descriptives
#     Exports general descriptive statistics.
# param:  data.ss
#     the simulation summary data
#----------------------------------------------------------------------------------------------------#
export_descriptives <- function(data.ss = load_simulation_summary_data(),
                                data.ad = load_agent_details_prepared_data()) {

  # observations
  obs <- nrow(load_simulation_summary_data(remove_exclusions = FALSE))
  out <- paste(" observations: ", obs, "\n", sep = "")
  out <- paste(out, " exclusions:   ", obs - nrow(data.ss), "\n\n", sep = "")

  # data

  data.ss.arlarge <- subset(data.ss, net.dynamic.pct.rec >= CUT_OFF_LARGE_ATTACK_RATE)

  out <- paste(out, "\\midrule \n", sep = "")
  out <- paste(out, "\\multicolumn{6}{l}{\\textbf{I.I. Epidemic, static}}", " \\", "\\ \n", sep = "")
  out <- paste(out, get_descriptive(data.ss$net.static.pct.rec, "Attack rate"))
  out <- paste(out, get_descriptive(data.ss$net.static.epidemic.max.infections, "Epidemic max infections"))
  out <- paste(out, get_descriptive(data.ss$net.static.epidemic.duration, "Duration"))
  out <- paste(out, get_descriptive(data.ss.arlarge$net.static.epidemic.duration, "Duration (attack rates 90\\%+)"))
  out <- paste(out, get_descriptive(data.ss$net.static.epidemic.peak, "Epidemic peak"))
  out <- paste(out, get_descriptive(data.ss.arlarge$net.static.epidemic.peak, "Epidemic peak (attack rates 90\\%+)"))
  out <- paste(out, get_descriptive(data.ss$net.static.ties.broken.epidemic, "Ties broken"))
  out <- paste(out, get_descriptive(data.ss$net.static.network.changes.epidemic - data.ss$net.static.ties.broken.epidemic, "Ties formed"))
  out <- paste(out, "\\multicolumn{6}{l}{\\textbf{I.II. Epidemic, dynamic}}", " \\", "\\ \n", sep = "")
  out <- paste(out, get_descriptive(data.ss$net.dynamic.pct.rec, "Attack rate"))
  out <- paste(out, get_descriptive(data.ss$net.dynamic.epidemic.max.infections, "Epidemic max infections"))
  out <- paste(out, get_descriptive(data.ss$net.dynamic.epidemic.duration, "Duration"))
  out <- paste(out, get_descriptive(data.ss.arlarge$net.dynamic.epidemic.duration, "Duration (attack rates 90\\%+)"))
  out <- paste(out, get_descriptive(data.ss$net.dynamic.epidemic.peak, "Epidemic peak"))
  out <- paste(out, get_descriptive(data.ss.arlarge$net.dynamic.epidemic.peak, "Epidemic peak (attack rates 90\\%+)"))
  out <- paste(out, get_descriptive(data.ss$net.dynamic.ties.broken.epidemic, "Ties broken"))
  out <- paste(out, get_descriptive(data.ss$net.dynamic.network.changes.epidemic - data.ss$net.static.ties.broken.epidemic, "Ties formed"))

  out <- paste(out, "\\midrule \n", sep = "")
  out <- paste(out, "\\multicolumn{6}{l}{\\textbf{II.I. Network, independent}}", " \\", "\\ \n", sep = "")
  out <- paste(out, get_descriptive(data.ss$nb.r.min, "Minimum risk perception ($r_{min}$)"))
  out <- paste(out, get_descriptive(data.ss$nb.r.max, "Maximum risk perception ($r_{max}$)"))
  out <- paste(out, "\\multicolumn{6}{l}{\\textbf{II.II. Network, dependent}}", " \\", "\\ \n", sep = "")
  out <- paste(out, get_descriptive(data.ss$nb.r.sigma.av, "Risk perception ($r_{\\sigma, \\pi}$)"))
  out <- paste(out, get_descriptive(data.ss$net.degree.pre.epidemic.av, "Degree ($\\mathcal{D}_{G}$)*"))
  out <- paste(out, get_descriptive(data.ss$net.clustering.pre.epidemic.av, "Clustering ($\\mathcal{C}_{G}$)*"))
  out <- paste(out, get_descriptive(data.ss$net.pathlength.pre.epidemic.av, "Path length ($\\mathcal{L}_{G}$)*"))
  out <- paste(out, get_descriptive(data.ss$net.assortativity.pre.epidemic, "Assortativity ($\\mathcal{A}_{G}$)*"))

  out <- paste(out, "\\midrule \n", sep = "")
  out <- paste(out, "\\multicolumn{6}{l}{\\textbf{III.I. Agent, independent}}", " \\", "\\ \n", sep = "")
  out <- paste(out, get_descriptive(data.ss$nb.alpha, "Preferred proportion of closed triads ($\\alpha$)"))
  out <- paste(out, get_descriptive(data.ss$nb.omega, "Likelihood of ties similar in risk perception ($\\omega$)"))
  out <- paste(out, get_descriptive(data.ad$nb.r.sigma, "Risk perception ($r_{\\sigma, \\pi}$)"))
  # out <- paste(out, "\\multicolumn{6}{l}{\\textbf{III.II. Agent, dependent (general)}}", " \\", "\\ \n", sep = "")
  # out <- paste(out, get_descriptive(data.ad$agent.degree, "Degree ($\\mathcal{D}_{i}$)*"))
  # out <- paste(out, get_descriptive(data.ad$agent.clustering, "Clustering ($\\mathcal{C}_{i}$)*"))
  # out <- paste(out, get_descriptive(data.ad$agent.betweenness.normalized, "Betweenness ($\\mathcal{B}_{i}$)*"))
  # out <- paste(out, get_descriptive(data.ad$agent.neighborhood.r.sigma.av, "Risk perception of direct ties ($r^{t_{i}}_{\\sigma, \\gamma})$"))
  out <- paste(out, "\\multicolumn{6}{l}{\\textbf{III.II. Agent, dependent (index case)}}", " \\", "\\ \n", sep = "")
  out <- paste(out, get_descriptive(data.ss$index.degree, "Degree ($\\mathcal{D}_{index}$)*"))
  out <- paste(out, get_descriptive(data.ss$index.clustering, "Clustering ($\\mathcal{C}_{index}$)*"))
  out <- paste(out, get_descriptive(data.ss$index.betweenness.normalized, "Betweenness ($\\mathcal{B}_{index}$)*"))
  out <- paste(out, get_descriptive(data.ss$index.r.sigma.neighborhood, "Risk perception of direct ties ($r^{t_{index}}_{\\sigma, \\gamma})$"))

  out <- paste(out, "\\midrule \n", sep = "")
  out <- paste(out, "\\multicolumn{6}{l}{\\textbf{IV. Infectious diseases, independent}}", " \\", "\\ \n", sep = "")
  out <- paste(out, get_descriptive(data.ss$nb.sigma, "Disease severity ($\\sigma$)"))
  out <- paste(out, get_descriptive(data.ss$nb.gamma, "Probability of disease transmission per contact ($\\gamma$)"))

  # export to file
  dir.create(EXPORT_PATH_NUM, showWarnings = FALSE)
  cat(out, file = paste(EXPORT_PATH_NUM,
                        "descriptives",
                        EXPORT_FILE_EXTENSION_DESC,
                        sep = ""))
}


############################################ CORRELATIONS ############################################
#----------------------------------------------------------------------------------------------------#
# function: plot_correlation
#     Creates a scatter plot with fitted linear model for two vectors.
# param:  data
#     The data to create correlations forv
# param:  vec.x
#     The vector to be displayed on the x-axis
# param:  vec.y
#     The vector to be displayed on the y-axis
# param:  title.x
#     The title of the x-axis
# param:  title.y
#     The title of the y-axis
# return: a scatter plot with fitted linear model for two vectors
#----------------------------------------------------------------------------------------------------#
plot_correlation <- function(data, vec.x, vec.y,
                             title.x, title.y,
                             limits.x, limits.y,
                             breaks.x, breaks.y,
                             fitting.method = "lm") {
  min.y <- trunc(min(vec.y)   * 10) / 10
  max.y <- ceiling(max(vec.y) * 10) / 10
  p <- ggplot(data, aes(x = vec.x, y = vec.y)) +
    geom_point(size = 0.4, stroke = 0, alpha = 0.008, position = position_jitter(h = 0, w = 0)) +
    geom_line(stat="smooth", method = fitting.method, se = FALSE, fullrange=TRUE, size = 0.5) +
    scale_x_continuous(name = title.x,
                       limits = limits.x,
                       breaks = breaks.x) +
    scale_y_continuous(name = title.y,
                       limits = limits.y,
                       breaks = breaks.y) +
    theme_bw(base_size = THEME_BASE_SIZE) +
    theme(legend.position = "top",
          legend.title = element_blank())
  return(p)
}

#----------------------------------------------------------------------------------------------------#
# function: get_correlation_text
#     Creates a text representation of the correlation analysis of two vectors.
# param:  vec.1
#     The first vector for the correlation analysis
# param:  vec.2
#     The second vector for the correlation analysis
# param:  title.1
#     The title of the first vector
# param:  title.2
#     The title of second vector
# return: a text representation of the correlation analysis of two vectors
#----------------------------------------------------------------------------------------------------#
get_correlation_text <- function(vec.1, vec.2, title.1, title.2) {
  shapiro.1 <- shapiro.test(tail(vec.1, 5000))
  shapiro.2 <- shapiro.test(tail(vec.2, 5000))
  if (shapiro.1[2] > 0.05 & shapiro.2[2] > 0.05) {
    pearson <- cor.test(vec.1, vec.2,  method = "pearson")
    out <- paste(title.1, " - ", title.2,"\n    Pearson (normally distributed): ",
                 "cor.coeff = ", round(pearson[4][[1]], 4), ", p = ", round(pearson[3][[1]], 4), "\n", sep = "")
  } else {
    kendall <- cor.test(vec.1, vec.2,  method = "kendall")
    out <- paste(title.1, " - ", title.2, "\n    Kendall (not normally distributed): ",
                 "tau = ", round(kendall[4][[1]], 4), ", p = ", round(kendall[3][[1]], 4), "\n", sep = "")
  }
  out <- paste(out, "--------------------------------------------------------------\n", sep = "")
  return(out)
}

#----------------------------------------------------------------------------------------------------#
# function: export_correlations
#     Exports all correlations.
# param:  data.ss
#     The simulation summary data containing the records for the correlation analyses
#----------------------------------------------------------------------------------------------------#
export_correlations <- function(data.ss = load_simulation_summary_data()) {

  # create directory if necessary
  dir.create(EXPORT_PATH_PLOTS, showWarnings = FALSE)

  ##### ALPHA
  out <- ""
  out <- paste(out, "##############################################################\n", sep = "")
  out <- paste(out, "#######              CORRELATIONS OF ALPHA              ######\n", sep = "")
  out <- paste(out, "##############################################################\n\n", sep = "")

  ### CLUSTERING
  # plot
  ggsave(paste(EXPORT_PATH_PLOTS, "cor-1-alpha-clustering", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
         plot_correlation(data.ss,
                          data.ss$nb.alpha, data.ss$net.clustering.pre.epidemic.av,
                          "alpha", "Clustering",
                          c(0, 1), c(0, 1),
                          seq(0, 1, 1/4), seq(0, 1, 1/4)),
         width = EXPORT_PLOT_HEIGHT,
         height = EXPORT_PLOT_HEIGHT,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)
  # correlation
  out <- paste(out, get_correlation_text(data.ss$nb.alpha,
                                         data.ss$net.clustering.pre.epidemic.av,
                                         "alpha",
                                         "clustering"),
               sep = "")

  ### PATH LENGTH
  # plot
  ggsave(paste(EXPORT_PATH_PLOTS, "cor-2-alpha-pathlength", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
         plot_correlation(data.ss,
                          data.ss$nb.alpha, data.ss$net.pathlength.pre.epidemic.av,
                          "alpha", "Average path length",
                          c(0, 1), c(1, 7),
                          seq(0, 1, 1/4), seq(1, 7, 1)),
         width = EXPORT_PLOT_HEIGHT,
         height = EXPORT_PLOT_HEIGHT,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)
  # correlation
  out <- paste(out, get_correlation_text(data.ss$nb.alpha,
                                         data.ss$net.pathlength.pre.epidemic.av,
                                         "alpha",
                                         "av. path length"),
               sep = "")

  ### BETWEENNESS (INDEX CASE)
  # plot
  ggsave(paste(EXPORT_PATH_PLOTS, "cor-3-alpha-betweennessindex", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
         plot_correlation(data.ss,
                          data.ss$nb.alpha, data.ss$index.betweenness.normalized,
                          "alpha", "Betweenness (index case)",
                          c(0, 1), c(0, 1),
                          seq(0, 1, 1/4), seq(0, 1, 1/4)),
         width = EXPORT_PLOT_HEIGHT,
         height = EXPORT_PLOT_HEIGHT,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)
  # correlation
  out <- paste(out, get_correlation_text(data.ss$nb.alpha,
                                         data.ss$index.betweenness.normalized,
                                         "alpha",
                                         "betweenness (index case)"),
               sep = "")

  ##### OMEGA
  out <- paste(out, "\n\n", sep = "")
  out <- paste(out, "##############################################################\n", sep = "")
  out <- paste(out, "#######              CORRELATIONS OF OMEGA              ######\n", sep = "")
  out <- paste(out, "##############################################################\n\n", sep = "")

  ### ASSORTATIVITY
  # plot
  ggsave(paste(EXPORT_PATH_PLOTS, "cor-4-omega-assortativity", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
         plot_correlation(data.ss,
                          data.ss$nb.omega, data.ss$net.assortativity.pre.epidemic,
                          "omega", "Assortativity",
                          c(0, 1), c(0, 1),
                          seq(0, 1, 1/4), seq(0, 1, 1/4)),
         width = EXPORT_PLOT_HEIGHT,
         height = EXPORT_PLOT_HEIGHT,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)
  # correlation
  out <- paste(out, get_correlation_text(data.ss$nb.omega,
                                         data.ss$net.assortativity.pre.epidemic,
                                         "omega",
                                         "assortativity"),
               sep = "")

  out <- paste(out, "\n\n", sep = "")
  out <- paste(out, "##############################################################\n", sep = "")
  out <- paste(out, "#######            CORRELATIONS OF CLUSTERING           ######\n", sep = "")
  out <- paste(out, "##############################################################\n\n", sep = "")

  # plot
  ggsave(paste(EXPORT_PATH_PLOTS, "cor-5-clustering-pathlength", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
         plot_correlation(data.ss,
                          data.ss$net.clustering.pre.epidemic.av, data.ss$net.pathlength.pre.epidemic.av,
                          "Clustering", "Average path length",
                          c(0, 1), c(1, 7),
                          seq(0, 1, 1/4), seq(1, 7, 1)),
         width = EXPORT_PLOT_HEIGHT,
         height = EXPORT_PLOT_HEIGHT,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)
  # correlation
  out <- paste(out, get_correlation_text(data.ss$net.clustering.pre.epidemic.av,
                                         data.ss$net.pathlength.pre.epidemic.av,
                                         "clustering",
                                         "av. path length"),
               sep = "")

  out <- paste(out, "\n\n", sep = "")
  out <- paste(out, "##############################################################\n", sep = "")
  out <- paste(out, "#######       CORRELATIONS OF DEPENDENT VARIABLES       ######\n", sep = "")
  out <- paste(out, "##############################################################\n\n", sep = "")

  # plot
  ggsave(paste(EXPORT_PATH_PLOTS, "cor-6-attackrate-duration-dynamic", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
         plot_correlation(data.ss,
                          data.ss$net.dynamic.pct.rec, data.ss$net.dynamic.epidemic.duration,
                          "Attack rate", "Duration",
                          c(0, 100), c(5, 50),
                          seq(0, 100, 25), seq(5, 50, 5)),
         width = EXPORT_PLOT_HEIGHT,
         height = EXPORT_PLOT_HEIGHT,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)
  # correlation
  out <- paste(out, get_correlation_text(data.ss$net.dynamic.pct.rec,
                                         data.ss$net.dynamic.epidemic.duration,
                                         "attack rate (dynamic)",
                                         "duration (dynamic)"),
               sep = "")

  # plot
  ggsave(paste(EXPORT_PATH_PLOTS, "cor-6-attackrate-duration-static", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
         plot_correlation(data.ss,
                          data.ss$net.static.pct.rec, data.ss$net.static.epidemic.duration,
                          "Attack rate", "Duration",
                          c(0, 100), c(5, 50),
                          seq(0, 100, 25), seq(5, 50, 5)),
         width = EXPORT_PLOT_HEIGHT,
         height = EXPORT_PLOT_HEIGHT,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)
  # correlation
  out <- paste(out, get_correlation_text(data.ss$net.static.pct.rec,
                                         data.ss$net.static.epidemic.duration,
                                         "attack rate (static)",
                                         "duration (static)"),
               sep = "")

  # plot
  ggsave(paste(EXPORT_PATH_PLOTS, "cor-7-attackrate-peak-dynamic", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
         plot_correlation(data.ss,
                          data.ss$net.dynamic.pct.rec, data.ss$net.dynamic.epidemic.peak,
                          "Attack rate", "Peak",
                          c(0, 100), c(5, 50),
                          seq(0, 100, 25), seq(5, 50, 5)),
         width = EXPORT_PLOT_HEIGHT,
         height = EXPORT_PLOT_HEIGHT,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)
  # correlation
  out <- paste(out, get_correlation_text(data.ss$net.dynamic.pct.rec,
                                         data.ss$net.dynamic.epidemic.peak,
                                         "attack rate (dynamic)",
                                         "peak (dynamic)"),
               sep = "")

  # plot
  ggsave(paste(EXPORT_PATH_PLOTS, "cor-7-attackrate-peak-static", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
         plot_correlation(data.ss,
                          data.ss$net.static.pct.rec, data.ss$net.static.epidemic.peak,
                          "Attack rate", "Peak",
                          c(0, 100), c(5, 50),
                          seq(0, 100, 25), seq(5, 50, 5)),
         width = EXPORT_PLOT_HEIGHT,
         height = EXPORT_PLOT_HEIGHT,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)
  # correlation
  out <- paste(out, get_correlation_text(data.ss$net.static.pct.rec,
                                         data.ss$net.static.epidemic.peak,
                                         "attack rate (static)",
                                         "peak (static)"),
               sep = "")

  # plot
  ggsave(paste(EXPORT_PATH_PLOTS, "cor-8-duration-peak-dynamic", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
         plot_correlation(data.ss,
                          data.ss$net.dynamic.epidemic.duration, data.ss$net.dynamic.epidemic.peak,
                          "Attack rate", "Peak",
                          c(5, 50), c(5, 50),
                          seq(5, 50, 5), seq(5, 50, 5)),
         width = EXPORT_PLOT_HEIGHT,
         height = EXPORT_PLOT_HEIGHT,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)
  # correlation
  out <- paste(out, get_correlation_text(data.ss$net.dynamic.epidemic.duration,
                                         data.ss$net.dynamic.epidemic.peak,
                                         "duration (dynamic)",
                                         "peak (dynamic)"),
               sep = "")

  # plot
  ggsave(paste(EXPORT_PATH_PLOTS, "cor-8-duration-peak-static", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
         plot_correlation(data.ss,
                          data.ss$net.static.epidemic.duration, data.ss$net.static.epidemic.peak,
                          "Attack rate", "Peak",
                          c(5, 50), c(5, 50),
                          seq(5, 50, 5), seq(5, 50, 5)),
         width = EXPORT_PLOT_HEIGHT,
         height = EXPORT_PLOT_HEIGHT,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)
  # correlation
  out <- paste(out, get_correlation_text(data.ss$net.static.epidemic.duration,
                                         data.ss$net.static.epidemic.peak,
                                         "duration (static)",
                                         "peak (static)"),
               sep = "")

  ##### EXPORT
  dir.create(EXPORT_PATH_NUM, showWarnings = FALSE)
  cat(out, file = paste(EXPORT_PATH_NUM,
                        "correlations",
                        EXPORT_FILE_EXTENSION_DESC,
                        sep = ""))
}









################################################ PLOTS ###############################################

prepare_level <- function(df, type, probability.infections) {
  df.sorted <- df[order(df$x),]
  df.prepared <- data.frame(x    = numeric(0),
                            y    = numeric(0),
                            type = character(0))
  bin.prev <- 0.00
  for (bin in seq(0, nrow(df), nrow(df)/BINS)) {
    bin <- ceiling(bin)
    df.binned <- df.sorted[bin.prev:bin,]
    if (probability.infections) {
      df.prepared <- rbind(df.prepared,
                           data.frame(x    = mean(df.binned$x),
                                      y    = sum(df.binned$y) / length(df.binned$y),
                                      type = type))
    } else {
      df.prepared <- rbind(df.prepared,
                           data.frame(x    = mean(df.binned$x),
                                      y    = mean(df.binned$y),
                                      type = type))
    }
    bin.prev <- bin
  }
  return(df.prepared)
}



plot_levels <- function(df.1,
                          df.2,
                          name.x, limits.x, breaks.x,
                          name.y, limits.y, breaks.y,
                          show.legend,
                          probability.infections) {

  # mean data preparations
  df.plot <- NA
  if (is.na(df.2)) {
    df.plot <- prepare_level(df = df.1, "level.1", probability.infections)
  } else {
    df.plot <- rbind(prepare_level(df = df.1, "level.1", probability.infections),
                     prepare_level(df = df.2, "level.2", probability.infections))
  }
  df.plot <- subset(df.plot, !is.na(x))

  ### PLOT ###
  # data
  p <- ggplot(df.plot,
              aes(x     = x,
                  y     = y,
                  color = type,
                  shape = type)) +
    # points
    geom_point(alpha = POINT_ALPHA,
               size  = POINT_SIZE,
               aes(colour = type,
                   shape  = factor(type)))

  # colors and shapes
  if (is.na(df.2)) {
    p <- p +
      scale_color_manual(values = c("level.1" = COLOR_1)) +
      scale_shape_manual(values = c("level.1" = 16))
  } else {
    p <- p +
      scale_color_manual(values = c("level.1" = COLOR_1,
                                    "level.2" = COLOR_2)) +
      scale_shape_manual(values = c("level.1" = 16,
                                    "level.2" = 17))
  }

  # linear models
  fit.1 <- lm(y~x, data = df.1)
  p <- p +
    geom_abline(slope     = fit.1$coefficients[2],
                intercept = fit.1$coefficients[1],
                size      = LINE_SIZE,
                linetype  = LINE_1,
                colour    = COLOR_1)
  if (!is.na(df.2)) {
    fit.2 <- lm(y~x, data = df.2)
    p <- p +
      geom_abline(slope     = fit.2$coefficients[2],
                  intercept = fit.2$coefficients[1],
                  size      = LINE_SIZE,
                  linetype  = LINE_2,
                  colour    = COLOR_2)
  }

  # axes
  p <- p +
    scale_x_continuous(name   = name.x,
                       limits = limits.x,
                       breaks = breaks.x) +
    scale_y_continuous(name   = name.y,
                       limits = limits.y,
                       breaks = breaks.y)

  # theme
  p <- p +
    theme_bw(base_size = THEME_BASE_SIZE)

  if (show.legend) {
    p <- p +
      theme(legend.position = "top",
            legend.justification = "right",
            legend.margin = margin(0,0,0,0),
            legend.box.margin = margin(-10,0,-10,0),
            legend.title = element_blank(),
            legend.background = element_rect(fill=alpha('white', 0)),
            legend.key = element_rect(colour = NA, fill = NA))
  } else {
    p <- p +
      theme(legend.position = "none")
  }

  return(p)
}

#----------------------------------------------------------------------------------------------------#
# function: plot_c1_a
#     Exports plots for conjecture 1.a.
# param:  data.ss
#     the simulation summary
# param:  ep.structure
#     whether plot for dynamic or static data ought to be created
# return: the plot
#----------------------------------------------------------------------------------------------------#
plot_c1_a <- function(data.ss = load_simulation_summary_data()) {
  # data
  d <- data.frame("structure"    = rep("static", nrow(data.ss)),
                  "attack.rate"  = data.ss$net.static.pct.rec)
  d <- rbind(d, data.frame("structure"    = rep("dynamic", nrow(data.ss)),
                           "attack.rate"  = data.ss$net.dynamic.pct.rec))
  # plot
  p.attackrate <- ggplot(d, aes(x = structure, y = attack.rate, fill = factor(structure))) +
    #geom_point(aes(colour = factor(structure), shape  = factor(structure)), position = position_jitter(), alpha=0.6, show.legend = FALSE) +
    geom_boxplot(alpha = 1, show.legend = FALSE, lwd = 0.5, fatten = 1.5) +
    scale_color_manual(values = c("static" = "#0072B2", "dynamic" = "#D55E00")) +
    scale_fill_manual(values = c("static" = "#0072B2", "dynamic" = "#D55E00")) +
    scale_x_discrete(name="Network during epidemic") +
    scale_y_continuous(name="Attack rate", limits=c(0, 100)) +
    theme(legend.position = "top",
          legend.justification = "right",
          legend.margin = margin(0,0,0,0),
          legend.box.margin = margin(-10,0,-10,0),
          legend.title = element_blank(),
          legend.background = element_rect(fill=alpha('white', 0)),
          legend.key = element_rect(colour = NA, fill = NA)) +
    theme_bw(base_size = THEME_BASE_SIZE)
  return(p.attackrate)
}

#----------------------------------------------------------------------------------------------------#
# function: plot_c1_b
#     Exports plots for conjecture 1.b.
# param:  data.ss
#     the simulation summary data
# param:  ep.structure
#     whether plot for dynamic or static data ought to be created
# return: the plot
#----------------------------------------------------------------------------------------------------#
plot_c1_b_duration <- function(data.ss = load_simulation_summary_data(), show.legend = FALSE) {

  d <- data.frame("structure"   = rep("static", nrow(data.ss)),
                  "attack.rate" = rep("average", nrow(data.ss)),
                  "duration"    = data.ss$net.static.epidemic.duration,
                  "color"       = rep("average.static", nrow(data.ss)))

  d.90.static <- subset(data.ss, net.static.pct.rec >= 90)
  d <- rbind(d, data.frame("structure"   = rep("static", nrow(d.90.static)),
                           "attack.rate" = rep("90-100%", nrow(d.90.static)),
                           "duration"    = d.90.static$net.static.epidemic.duration,
                           "color"       = rep("90-100%.static", nrow(d.90.static))))

  d <- rbind(d, data.frame("structure"   = rep("dynamic", nrow(data.ss)),
                           "attack.rate" = rep("average", nrow(data.ss)),
                           "duration"    = data.ss$net.dynamic.epidemic.duration,
                           "color"       = rep("average.dynamic", nrow(data.ss))))

  d.90.dynamic <- subset(data.ss, net.dynamic.pct.rec >= 90)
  d <- rbind(d, data.frame("structure"   = rep("dynamic", nrow(d.90.dynamic)),
                           "attack.rate" = rep("90-100%", nrow(d.90.dynamic)),
                           "duration"    = d.90.dynamic$net.dynamic.epidemic.duration,
                           "color"       = rep("90-100%.dynamic", nrow(d.90.dynamic))))

  d$color <- factor(d$color, c("average.dynamic", "90-100%.dynamic", "average.static", "90-100%.static"))

  # plot
  p.duration <- ggplot(d, aes(x = structure, y = duration, fill = color)) +
    geom_boxplot(lwd = 0.5, fatten = 1.5, outlier.size = 0.3) +
    scale_color_manual(values = c("average.dynamic" = "#D55E00",
                                  "90-100%.dynamic" = "#ffbd88",
                                  "average.static" = "#0072B2",
                                  "90-100%.static" = "#64c7ff")) +
    scale_fill_manual(values = c("average.dynamic" = "#D55E00",
                                 "90-100%.dynamic" = "#ffbd88",
                                 "average.static" = "#0072B2",
                                 "90-100%.static" = "#64c7ff")) +
    scale_x_discrete(name="Network during epidemic") +
    scale_y_continuous(name="Epidemic duration", limits=c(0, 80)) +
    theme_bw(base_size = THEME_BASE_SIZE) +
    guides(fill=guide_legend(title="Attack rate"))

  if (show.legend) {
    p.duration <- p.duration +
      theme(legend.position = "top",
            legend.justification = "right",
            legend.margin = margin(0,0,0,0),
            legend.box.margin = margin(-4,0,-6,0),
            legend.title = element_blank(),
            legend.background = element_rect(fill=alpha('white', 0)),
            legend.key = element_rect(colour = NA, fill = NA))
  } else {
    p.duration <- p.duration +
      theme(legend.position = "none")
  }

  return(p.duration)
}

#----------------------------------------------------------------------------------------------------#
# function: plot_c1_b
#     Exports plots for conjecture 1.b.
# param:  data.ss
#     the simulation summary data
# param:  ep.structure
#     whether plot for dynamic or static data ought to be created
# return: the plot
#----------------------------------------------------------------------------------------------------#
plot_c1_b_peak <- function(data.ss = load_simulation_summary_data(), show.legend = FALSE) {

  d <- data.frame("structure"   = rep("static", nrow(data.ss)),
                  "attack.rate" = rep("average", nrow(data.ss)),
                  "peak"        = data.ss$net.static.epidemic.peak,
                  "color"       = rep("average.static", nrow(data.ss)))

  d.90.static <- subset(data.ss, net.static.pct.rec >= 90)
  d <- rbind(d, data.frame("structure"   = rep("static", nrow(d.90.static)),
                           "attack.rate" = rep("90-100%", nrow(d.90.static)),
                           "peak"        = d.90.static$net.static.epidemic.peak,
                           "color"       = rep("90-100%.static", nrow(d.90.static))))

  d <- rbind(d, data.frame("structure"   = rep("dynamic", nrow(data.ss)),
                           "attack.rate" = rep("average", nrow(data.ss)),
                           "peak"        = data.ss$net.dynamic.epidemic.peak,
                           "color"       = rep("average.dynamic", nrow(data.ss))))

  d.90.dynamic <- subset(data.ss, net.dynamic.pct.rec >= 90)
  d <- rbind(d, data.frame("structure"   = rep("dynamic", nrow(d.90.dynamic)),
                           "attack.rate" = rep("90-100%", nrow(d.90.dynamic)),
                           "peak"        = d.90.dynamic$net.dynamic.epidemic.peak,
                           "color"       = rep("90-100%.dynamic", nrow(d.90.dynamic))))

  d$color <- factor(d$color, c("average.dynamic", "90-100%.dynamic", "average.static", "90-100%.static"))

  # plot
  p.peak <- ggplot(d, aes(x = structure, y = peak, fill = color)) +
    geom_boxplot(lwd = 0.5, fatten = 1.5, outlier.size = 0.3) +
    scale_color_manual(values = c("average.dynamic" = "#D55E00",
                                  "90-100%.dynamic" = "#ffbd88",
                                  "average.static"  = "#0072B2",
                                  "90-100%.static"  = "#64c7ff")) +
    scale_fill_manual(values = c("average.dynamic"  = "#D55E00",
                                 "90-100%.dynamic"  = "#ffbd88",
                                 "average.static"   = "#0072B2",
                                 "90-100%.static"   = "#64c7ff")) +
    scale_x_discrete(name="Network during epidemic") +
    scale_y_continuous(name="Epidemic peak", limits=c(0, 80)) +
    theme_bw(base_size = THEME_BASE_SIZE) +
    guides(fill=guide_legend(title="Attack rate"))

  if (show.legend) {
    p.peak <- p.peak +
      theme(legend.position = "top",
            legend.justification = "right",
            legend.margin = margin(0,0,0,0),
            legend.box.margin = margin(-4,0,-6,0),
            legend.title = element_blank(),
            legend.background = element_rect(fill=alpha('white', 0)),
            legend.key = element_rect(colour = NA, fill = NA))
  } else {
    p.peak <- p.peak +
      theme(legend.position = "none")
  }

  return(p.peak)
}

#----------------------------------------------------------------------------------------------------#
# function: plot_c1_b
#     Exports plots for conjecture 1.b.
# param:  data.ss
#     the simulation summary data
# param:  ep.structure
#     whether plot for dynamic or static data ought to be created
# return: the plot
#----------------------------------------------------------------------------------------------------#
plot_c1_b_peaksize <- function(data.ss = load_simulation_summary_data(), show.legend = FALSE) {

  d <- data.frame("structure"   = rep("static", nrow(data.ss)),
                  "attack.rate" = rep("average", nrow(data.ss)),
                  "peak.size"   = data.ss$net.static.epidemic.max.infections,
                  "color"       = rep("average.static", nrow(data.ss)))

  d.90.static <- subset(data.ss, net.static.pct.rec >= 90)
  d <- rbind(d, data.frame("structure"   = rep("static", nrow(d.90.static)),
                           "attack.rate" = rep("90-100%", nrow(d.90.static)),
                           "peak.size"   = d.90.static$net.static.epidemic.max.infections,
                           "color"       = rep("90-100%.static", nrow(d.90.static))))

  d <- rbind(d, data.frame("structure"   = rep("dynamic", nrow(data.ss)),
                           "attack.rate" = rep("average", nrow(data.ss)),
                           "peak.size"        = data.ss$net.dynamic.epidemic.max.infections,
                           "color"       = rep("average.dynamic", nrow(data.ss))))

  d.90.dynamic <- subset(data.ss, net.dynamic.pct.rec >= 90)
  d <- rbind(d, data.frame("structure"   = rep("dynamic", nrow(d.90.dynamic)),
                           "attack.rate" = rep("90-100%", nrow(d.90.dynamic)),
                           "peak.size"        = d.90.dynamic$net.dynamic.epidemic.max.infections,
                           "color"       = rep("90-100%.dynamic", nrow(d.90.dynamic))))

  d$color <- factor(d$color, c("average.dynamic", "90-100%.dynamic", "average.static", "90-100%.static"))

  # plot
  p.peak <- ggplot(d, aes(x = structure, y = peak.size, fill = color)) +
    geom_boxplot(lwd = 0.5, fatten = 1.5, outlier.size = 0.3) +
    scale_color_manual(values = c("average.dynamic" = "#D55E00",
                                  "90-100%.dynamic" = "#ffbd88",
                                  "average.static"  = "#0072B2",
                                  "90-100%.static"  = "#64c7ff")) +
    scale_fill_manual(values = c("average.dynamic"  = "#D55E00",
                                 "90-100%.dynamic"  = "#ffbd88",
                                 "average.static"   = "#0072B2",
                                 "90-100%.static"   = "#64c7ff")) +
    scale_x_discrete(name="Network during epidemic") +
    scale_y_continuous(name="Epidemic peak size", limits=c(0, 80)) +
    theme_bw(base_size = THEME_BASE_SIZE) +
    guides(fill=guide_legend(title="Attack rate"))

  if (show.legend) {
    p.peak <- p.peak +
      theme(legend.position = "top",
            legend.justification = "right",
            legend.margin = margin(0,0,0,0),
            legend.box.margin = margin(-4,0,-6,0),
            legend.title = element_blank(),
            legend.background = element_rect(fill=alpha('white', 0)),
            legend.key = element_rect(colour = NA, fill = NA))
  } else {
    p.peak <- p.peak +
      theme(legend.position = "none")
  }

  return(p.peak)
}


get_plots <- function(data.ss = load_simulation_summary_data(),
                      data.ad = load_agent_details_prepared_data()) {


  ### NETWORK DYNAMICS ###
  plots <- c(list(plot_c1_a(data.ss)), "0-1-attackrate")
  plots <- c(plots, list(plot_c1_b_duration(data.ss)), "0-2-duration")
  plots <- c(plots, list(plot_c1_b_peak(data.ss = )), "0-3-peak")
  plots <- c(plots, list(plot_c1_b_peaksize(data.ss)), "0-4-peaksize")

  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss$net.dynamic.pct.rec,
                                                y = data.ss$net.dynamic.epidemic.duration),

                              df.2 = data.frame(x = data.ss$net.static.pct.rec,
                                                y = data.ss$net.static.epidemic.duration),

                              name.x   = LABEL_ATTACKRATE,
                              limits.x = LIMITS_ATTACKRATE,
                              breaks.x = BREAKS_ATTACKRATE,

                              name.y   = LABEL_DURATION,
                              limits.y = c(0, 40),
                              breaks.y = seq(0, 40, 5),

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),

             "0-4-attackrate-duration")

  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss$net.dynamic.pct.rec,
                                                y = data.ss$net.dynamic.epidemic.peak),

                              df.2 = data.frame(x = data.ss$net.static.pct.rec,
                                                y = data.ss$net.static.epidemic.peak),

                              name.x   = LABEL_ATTACKRATE,
                              limits.x = LIMITS_ATTACKRATE,
                              breaks.x = BREAKS_ATTACKRATE,

                              name.y   = LABEL_PEAK,
                              limits.y = LIMITS_DURATIONPEAK,
                              breaks.y = BREAKS_DURATIONPEAK,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),

             "0-5-attackrate-peak")

  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss$net.dynamic.pct.rec,
                                                y = data.ss$net.dynamic.epidemic.max.infections),

                              df.2 = data.frame(x = data.ss$net.static.pct.rec,
                                                y = data.ss$net.static.epidemic.max.infections),

                              name.x   = LABEL_ATTACKRATE,
                              limits.x = LIMITS_ATTACKRATE,
                              breaks.x = BREAKS_ATTACKRATE,

                              name.y   = LABEL_PEAKSIZE,
                              limits.y = LIMITS_PEAKSIZE,
                              breaks.y = BREAKS_PEAKSIZE,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),

             "0-6-attackrate-peaksize")



  ### NETWORK DECISIONS / PROBABILITY OF INFECTIONS ###
  for (ep.structure in c("dynamic", "static")) {
    data.ad.by.struc <- subset(data.ad, nb.ep.structure == ep.structure)

    # risk perception
    plots <- c(plots,
               list(plot_levels(df.1 = data.frame(x = data.ad.by.struc$nb.r.sigma,
                                                  y = data.ad.by.struc$agent.cons.broken.active.epidemic),

                                df.2 = data.frame(x = data.ad.by.struc$agent.neighborhood.r.sigma.av,
                                                  y = c(data.ad.by.struc$agent.cons.out.accepted.epidemic,
                                                        data.ad.by.struc$agent.cons.in.accepted.epidemic)),

                                name.x   = LABEL_RISKPERCEPTION,
                                limits.x = LIMITS_RISKPERCEPTION,
                                breaks.x = BREAKS_RISKPERCEPTION,

                                name.y   = LABEL_NETDECISIONS,
                                limits.y = LIMITS_NETDECISIONS,
                                breaks.y = BREAKS_NETDECISIONS,

                                show.legend = SHOW_LEGEND,
                                probability.infections = FALSE)),

               paste(ep.structure, "-1-1-riskperception-netdecisions", sep = ""))

    plots <- c(plots,
               list(plot_levels(df.1 = data.frame(x = data.ad.by.struc$nb.r.sigma,
                                                  y = data.ad.by.struc$agent.infected),

                                df.2 = data.frame(x = data.ad.by.struc$agent.neighborhood.r.sigma.av,
                                                  y = data.ad.by.struc$agent.infected),

                                name.x   = LABEL_RISKPERCEPTION,
                                limits.x = LIMITS_RISKPERCEPTION,
                                breaks.x = BREAKS_RISKPERCEPTION,

                                name.y   = LABEL_PROBABILITYINFECTION,
                                limits.y = LIMITS_PROBABILITYINFECTION,
                                breaks.y = BREAKS_PROBABILITYINFECTION,

                                show.legend = SHOW_LEGEND,
                                probability.infections = TRUE)),

               paste(ep.structure, "-1-2-riskperception-probabilityinfection", sep = ""))

    # clustering
    plots <- c(plots,
               list(plot_levels(df.1 = data.frame(x = data.ad.by.struc$agent.clustering,
                                                  y = data.ad.by.struc$agent.cons.broken.active.epidemic),

                                df.2 = data.frame(x = data.ad.by.struc$net.clustering.av,
                                                  y = c(data.ad.by.struc$agent.cons.out.accepted.epidemic,
                                                        data.ad.by.struc$agent.cons.in.accepted.epidemic)),

                                name.x   = LABEL_CLUSTERING,
                                limits.x = LIMITS_CLUSTERING,
                                breaks.x = BREAKS_CLUSTERING,

                                name.y   = LABEL_NETDECISIONS,
                                limits.y = LIMITS_NETDECISIONS,
                                breaks.y = BREAKS_NETDECISIONS,

                                show.legend = SHOW_LEGEND,
                                probability.infections = TRUE)),

               paste(ep.structure, "-2-1-clustering-netdecisions", sep = ""))

    plots <- c(plots,
               list(plot_levels(df.1 = data.frame(x = data.ad.by.struc$agent.clustering,
                                                  y = data.ad.by.struc$agent.infected),

                                df.2 = data.frame(x = data.ad.by.struc$net.clustering.av,
                                                  y = data.ad.by.struc$agent.infected),

                                name.x   = LABEL_CLUSTERING,
                                limits.x = LIMITS_CLUSTERING,
                                breaks.x = BREAKS_CLUSTERING,

                                name.y   = LABEL_PROBABILITYINFECTION,
                                limits.y = LIMITS_PROBABILITYINFECTION,
                                breaks.y = BREAKS_PROBABILITYINFECTION,

                                show.legend = SHOW_LEGEND,
                                probability.infections = TRUE)),

               paste(ep.structure, "-2-2-clustering-probabilityinfection", sep = ""))

    # path length
    plots <- c(plots,
               list(plot_levels(df.1 = data.frame(x = data.ad.by.struc$net.pathlength.av,
                                                  y = data.ad.by.struc$agent.cons.broken.active.epidemic),

                                df.2 = data.frame(x = data.ad.by.struc$net.pathlength.av,
                                                  y = c(data.ad.by.struc$agent.cons.out.accepted.epidemic,
                                                        data.ad.by.struc$agent.cons.in.accepted.epidemic)),

                                name.x   = LABEL_PATHLENGTH,
                                limits.x = LIMITS_PATHLENGTH,
                                breaks.x = BREAKS_PATHLENGTH,

                                name.y   = LABEL_NETDECISIONS,
                                limits.y = LIMITS_NETDECISIONS,
                                breaks.y = BREAKS_NETDECISIONS,

                                show.legend = SHOW_LEGEND,
                                probability.infections = FALSE)),

               paste(ep.structure, "-3-1-pathlength-netdecisions", sep = ""))

    plots <- c(plots,
               list(plot_levels(df.1 = data.frame(x = data.ad.by.struc$net.pathlength.av,
                                                  y = data.ad.by.struc$agent.infected),

                                df.2 = NA,

                                name.x   = LABEL_PATHLENGTH,
                                limits.x = LIMITS_PATHLENGTH,
                                breaks.x = BREAKS_PATHLENGTH,

                                name.y   = LABEL_PROBABILITYINFECTION,
                                limits.y = LIMITS_PROBABILITYINFECTION,
                                breaks.y = BREAKS_PROBABILITYINFECTION,

                                show.legend = SHOW_LEGEND,
                                probability.infections = TRUE)),

               paste(ep.structure, "-3-2-pathlength-probabilityinfection", sep = ""))

    # betweenness (normalized)
    plots <- c(plots,
               list(plot_levels(df.1 = data.frame(x = data.ad.by.struc$agent.betweenness.normalized,
                                                  y = data.ad.by.struc$agent.cons.broken.active.epidemic),

                                df.2 = data.frame(x = data.ad.by.struc$agent.betweenness.normalized,
                                                  y = c(data.ad.by.struc$agent.cons.out.accepted.epidemic,
                                                        data.ad.by.struc$agent.cons.in.accepted.epidemic)),

                                name.x   = LABEL_BETWEENNESS,
                                limits.x = LIMITS_BETWEENNESS,
                                breaks.x = BREAKS_BETWEENNESS,

                                name.y   = LABEL_NETDECISIONS,
                                limits.y = LIMITS_NETDECISIONS,
                                breaks.y = BREAKS_NETDECISIONS,

                                show.legend = SHOW_LEGEND,
                                probability.infections = FALSE)),

               paste(ep.structure, "-4-1-betweenness-netdecisions", sep = ""))

    plots <- c(plots,
               list(plot_levels(df.1 = data.frame(x = data.ad.by.struc$agent.betweenness.normalized,
                                                  y = data.ad.by.struc$agent.infected),
                                df.2 = NA,

                                name.x   = LABEL_BETWEENNESS,
                                limits.x = LIMITS_BETWEENNESS,
                                breaks.x = BREAKS_BETWEENNESS,

                                name.y   = LABEL_PROBABILITYINFECTION,
                                limits.y = LIMITS_PROBABILITYINFECTION,
                                breaks.y = BREAKS_PROBABILITYINFECTION,

                                show.legend = SHOW_LEGEND,
                                probability.infections = TRUE)),

               paste(ep.structure, "-4-2-betweenness-probabilityinfection", sep = ""))

    # assortativity
    plots <- c(plots,
               list(plot_levels(df.1 = data.frame(x = data.ad.by.struc$net.assortativity,
                                                  y = data.ad.by.struc$agent.cons.broken.active.epidemic),

                                df.2 = data.frame(x = data.ad.by.struc$net.assortativity,
                                                  y = c(data.ad.by.struc$agent.cons.out.accepted.epidemic,
                                                        data.ad.by.struc$agent.cons.in.accepted.epidemic)),

                                name.x   = LABEL_ASSORTATIVITY,
                                limits.x = LIMITS_ASSORTATIVITY,
                                breaks.x = BREAKS_ASSORTATIVITY,

                                name.y   = LABEL_NETDECISIONS,
                                limits.y = LIMITS_NETDECISIONS,
                                breaks.y = BREAKS_NETDECISIONS,

                                show.legend = SHOW_LEGEND,
                                probability.infections = FALSE)),

               paste(ep.structure, "-5-1-assortativity-netdecisions", sep = ""))

    plots <- c(plots,
               list(plot_levels(df.1 = data.frame(x = data.ad.by.struc$net.assortativity,
                                                  y = data.ad.by.struc$agent.infected),

                                df.2 = NA,

                                name.x   = LABEL_ASSORTATIVITY,
                                limits.x = LIMITS_ASSORTATIVITY,
                                breaks.x = BREAKS_ASSORTATIVITY,

                                name.y   = LABEL_PROBABILITYINFECTION,
                                limits.y = LIMITS_PROBABILITYINFECTION,
                                breaks.y = BREAKS_PROBABILITYINFECTION,

                                show.legend = SHOW_LEGEND,
                                probability.infections = TRUE)),

               paste(ep.structure, "-5-2-assortativity-probabilityinfection", sep = ""))
  }

  ### ATTACK RATE ###
  # risk perception
  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss$nb.r.sigma.av,
                                                y = data.ss$net.dynamic.pct.rec),
                              df.2 = data.frame(x = data.ss$index.r.sigma.neighborhood,
                                                y = data.ss$net.dynamic.pct.rec),

                              name.x   = LABEL_RISKPERCEPTION,
                              limits.x = LIMITS_RISKPERCEPTION,
                              breaks.x = BREAKS_RISKPERCEPTION,

                              name.y   = LABEL_ATTACKRATE,
                              limits.y = LIMITS_ATTACKRATE,
                              breaks.y = BREAKS_ATTACKRATE,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "dynamic-1-3-riskperception-attackrate")

  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss$nb.r.sigma.av,
                                                y = data.ss$net.static.pct.rec),
                              df.2 = data.frame(x = data.ss$index.r.sigma.neighborhood,
                                                y = data.ss$net.static.pct.rec),

                              name.x   = LABEL_RISKPERCEPTION,
                              limits.x = LIMITS_RISKPERCEPTION,
                              breaks.x = BREAKS_RISKPERCEPTION,

                              name.y   = LABEL_ATTACKRATE,
                              limits.y = LIMITS_ATTACKRATE,
                              breaks.y = BREAKS_ATTACKRATE,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "static-1-3-riskperception-attackrate")

  # clustering
  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss$net.clustering.pre.epidemic.av,
                                                y = data.ss$net.dynamic.pct.rec),
                              df.2 = data.frame(x = data.ss$index.clustering,
                                                y = data.ss$net.dynamic.pct.rec),

                              name.x   = LABEL_CLUSTERING,
                              limits.x = LIMITS_CLUSTERING,
                              breaks.x = BREAKS_CLUSTERING,

                              name.y   = LABEL_ATTACKRATE,
                              limits.y = LIMITS_ATTACKRATE,
                              breaks.y = BREAKS_ATTACKRATE,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "dynamic-2-3-clustering-attackrate")

  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss$net.clustering.pre.epidemic.av,
                                                y = data.ss$net.static.pct.rec),
                              df.2 = data.frame(x = data.ss$index.clustering,
                                                y = data.ss$net.static.pct.rec),

                              name.x   = LABEL_CLUSTERING,
                              limits.x = LIMITS_CLUSTERING,
                              breaks.x = BREAKS_CLUSTERING,

                              name.y   = LABEL_ATTACKRATE,
                              limits.y = LIMITS_ATTACKRATE,
                              breaks.y = BREAKS_ATTACKRATE,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "static-2-3-clustering-attackrate")

  # path length
  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss$net.pathlength.pre.epidemic.av,
                                                y = data.ss$net.dynamic.pct.rec),
                              df.2 = NA,

                              name.x   = LABEL_PATHLENGTH,
                              limits.x = LIMITS_PATHLENGTH,
                              breaks.x = BREAKS_PATHLENGTH,

                              name.y   = LABEL_ATTACKRATE,
                              limits.y = LIMITS_ATTACKRATE,
                              breaks.y = BREAKS_ATTACKRATE,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "dynamic-3-3-pathlength-attackrate")

  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss$net.pathlength.pre.epidemic.av,
                                                y = data.ss$net.static.pct.rec),
                              df.2 = NA,

                              name.x   = LABEL_PATHLENGTH,
                              limits.x = LIMITS_PATHLENGTH,
                              breaks.x = BREAKS_PATHLENGTH,

                              name.y   = LABEL_ATTACKRATE,
                              limits.y = LIMITS_ATTACKRATE,
                              breaks.y = BREAKS_ATTACKRATE,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "static-3-3-pathlength-attackrate")

  # betweenness (normalized)
  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss$index.betweenness.normalized,
                                                y = data.ss$net.dynamic.pct.rec),
                              df.2 = NA,

                              name.x   = LABEL_BETWEENNESS,
                              limits.x = LIMITS_BETWEENNESS,
                              breaks.x = BREAKS_BETWEENNESS,

                              name.y   = LABEL_ATTACKRATE,
                              limits.y = LIMITS_ATTACKRATE,
                              breaks.y = BREAKS_ATTACKRATE,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "dynamic-4-3-betweenness-attackrate")

  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss$index.betweenness.normalized,
                                                y = data.ss$net.static.pct.rec),
                              df.2 = NA,

                              name.x   = LABEL_BETWEENNESS,
                              limits.x = LIMITS_BETWEENNESS,
                              breaks.x = BREAKS_BETWEENNESS,

                              name.y   = LABEL_ATTACKRATE,
                              limits.y = LIMITS_ATTACKRATE,
                              breaks.y = BREAKS_ATTACKRATE,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "static-4-3-betweenness-attackrate")

  # assortativity
  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss$net.assortativity.pre.epidemic,
                                                y = data.ss$net.dynamic.pct.rec),
                              df.2 = NA,

                              name.x   = LABEL_ASSORTATIVITY,
                              limits.x = LIMITS_ASSORTATIVITY,
                              breaks.x = BREAKS_ASSORTATIVITY,

                              name.y   = LABEL_ATTACKRATE,
                              limits.y = LIMITS_ATTACKRATE,
                              breaks.y = BREAKS_ATTACKRATE,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "dynamic-5-3-assortativity-attackrate")

  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss$net.assortativity.pre.epidemic,
                                                y = data.ss$net.static.pct.rec),
                              df.2 = NA,

                              name.x   = LABEL_ASSORTATIVITY,
                              limits.x = LIMITS_ASSORTATIVITY,
                              breaks.x = BREAKS_ASSORTATIVITY,

                              name.y   = LABEL_ATTACKRATE,
                              limits.y = LIMITS_ATTACKRATE,
                              breaks.y = BREAKS_ATTACKRATE,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "static-5-3-assortativity-attackrate")

  ### DURATION / PEAK ###
  data.ss.arlarge <- subset(data.ss, net.dynamic.pct.rec >= CUT_OFF_LARGE_ATTACK_RATE)
  # risk perception
  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss$nb.r.sigma.av,
                                                y = data.ss$net.dynamic.epidemic.duration),

                              df.2 = data.frame(x = data.ss.arlarge$index.r.sigma.neighborhood,
                                                y = data.ss.arlarge$net.dynamic.epidemic.duration),

                              name.x   = LABEL_RISKPERCEPTION,
                              limits.x = LIMITS_RISKPERCEPTION,
                              breaks.x = BREAKS_RISKPERCEPTION,

                              name.y   = LABEL_DURATION,
                              limits.y = LIMITS_DURATIONPEAK,
                              breaks.y = BREAKS_DURATIONPEAK,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "dynamic-1-4-riskperception-duration")

  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss$nb.r.sigma.av,
                                                y = data.ss$net.dynamic.epidemic.peak),

                              df.2 = data.frame(x = data.ss.arlarge$index.r.sigma.neighborhood,
                                                y = data.ss.arlarge$net.dynamic.epidemic.peak),

                              name.x   = LABEL_RISKPERCEPTION,
                              limits.x = LIMITS_RISKPERCEPTION,
                              breaks.x = BREAKS_RISKPERCEPTION,

                              name.y   = LABEL_PEAK,
                              limits.y = LIMITS_DURATIONPEAK,
                              breaks.y = BREAKS_DURATIONPEAK,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "dynamic-1-5-riskperception-peak")

  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss$nb.r.sigma.av,
                                                y = data.ss$net.static.epidemic.duration),

                              df.2 = data.frame(x = data.ss.arlarge$index.r.sigma.neighborhood,
                                                y = data.ss.arlarge$net.static.epidemic.duration),

                              name.x   = LABEL_RISKPERCEPTION,
                              limits.x = LIMITS_RISKPERCEPTION,
                              breaks.x = BREAKS_RISKPERCEPTION,

                              name.y   = LABEL_DURATION,
                              limits.y = LIMITS_DURATIONPEAK,
                              breaks.y = BREAKS_DURATIONPEAK,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "static-1-4-riskperception-duration")

  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss$nb.r.sigma.av,
                                                y = data.ss$net.static.epidemic.peak),

                              df.2 = data.frame(x = data.ss.arlarge$index.r.sigma.neighborhood,
                                                y = data.ss.arlarge$net.static.epidemic.peak),

                              name.x   = LABEL_RISKPERCEPTION,
                              limits.x = LIMITS_RISKPERCEPTION,
                              breaks.x = BREAKS_RISKPERCEPTION,

                              name.y   = LABEL_PEAK,
                              limits.y = LIMITS_DURATIONPEAK,
                              breaks.y = BREAKS_DURATIONPEAK,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "static-1-5-riskperception-peak")

  # clustering
  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss$net.clustering.pre.epidemic.av,
                                                y = data.ss$net.dynamic.epidemic.duration),

                              df.2 = data.frame(x = data.ss.arlarge$net.clustering.pre.epidemic.av,
                                                y = data.ss.arlarge$net.dynamic.epidemic.duration),

                              name.x   = LABEL_CLUSTERING,
                              limits.x = LIMITS_CLUSTERING,
                              breaks.x = BREAKS_CLUSTERING,

                              name.y   = LABEL_DURATION,
                              limits.y = LIMITS_DURATIONPEAK,
                              breaks.y = BREAKS_DURATIONPEAK,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "dynamic-2-4-clustering-duration")

  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss$net.clustering.pre.epidemic.av,
                                                y = data.ss$net.dynamic.epidemic.peak),

                              df.2 = data.frame(x = data.ss.arlarge$net.clustering.pre.epidemic.av,
                                                y = data.ss.arlarge$net.dynamic.epidemic.peak),

                              name.x   = LABEL_CLUSTERING,
                              limits.x = LIMITS_CLUSTERING,
                              breaks.x = BREAKS_CLUSTERING,

                              name.y   = LABEL_PEAK,
                              limits.y = LIMITS_DURATIONPEAK,
                              breaks.y = BREAKS_DURATIONPEAK,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "dynamic-2-5-clustering-peak")

  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss$net.clustering.pre.epidemic.av,
                                                y = data.ss$net.static.epidemic.duration),

                              df.2 = data.frame(x = data.ss.arlarge$net.clustering.pre.epidemic.av,
                                                y = data.ss.arlarge$net.static.epidemic.duration),

                              name.x   = LABEL_CLUSTERING,
                              limits.x = LIMITS_CLUSTERING,
                              breaks.x = BREAKS_CLUSTERING,

                              name.y   = LABEL_DURATION,
                              limits.y = LIMITS_DURATIONPEAK,
                              breaks.y = BREAKS_DURATIONPEAK,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "static-2-4-clustering-duration")

  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss$net.clustering.pre.epidemic.av,
                                                y = data.ss$net.static.epidemic.peak),

                              df.2 = data.frame(x = data.ss.arlarge$net.clustering.pre.epidemic.av,
                                                y = data.ss.arlarge$net.static.epidemic.peak),

                              name.x   = LABEL_CLUSTERING,
                              limits.x = LIMITS_CLUSTERING,
                              breaks.x = BREAKS_CLUSTERING,

                              name.y   = LABEL_PEAK,
                              limits.y = LIMITS_DURATIONPEAK,
                              breaks.y = BREAKS_DURATIONPEAK,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "static-2-5-clustering-peak")

  # path length
  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss$net.pathlength.pre.epidemic.av,
                                                y = data.ss$net.dynamic.epidemic.duration),

                              df.2 = NA,

                              name.x   = LABEL_PATHLENGTH,
                              limits.x = LIMITS_PATHLENGTH,
                              breaks.x = BREAKS_PATHLENGTH,

                              name.y   = LABEL_DURATION,
                              limits.y = LIMITS_DURATIONPEAK,
                              breaks.y = BREAKS_DURATIONPEAK,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "dynamic-3-4-pathlength-duration")

  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss$net.pathlength.pre.epidemic.av,
                                                y = data.ss$net.dynamic.epidemic.peak),

                              df.2 = NA,

                              name.x   = LABEL_PATHLENGTH,
                              limits.x = LIMITS_PATHLENGTH,
                              breaks.x = BREAKS_PATHLENGTH,

                              name.y   = LABEL_PEAK,
                              limits.y = LIMITS_DURATIONPEAK,
                              breaks.y = BREAKS_DURATIONPEAK,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "dynamic-3-5-pathlength-peak")

  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss$net.pathlength.pre.epidemic.av,
                                                y = data.ss$net.static.epidemic.duration),

                              df.2 = NA,

                              name.x   = LABEL_PATHLENGTH,
                              limits.x = LIMITS_PATHLENGTH,
                              breaks.x = BREAKS_PATHLENGTH,

                              name.y   = LABEL_DURATION,
                              limits.y = LIMITS_DURATIONPEAK,
                              breaks.y = BREAKS_DURATIONPEAK,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "static-3-4-pathlength-duration")

  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss$net.pathlength.pre.epidemic.av,
                                                y = data.ss$net.static.epidemic.peak),

                              df.2 = NA,

                              name.x   = LABEL_PATHLENGTH,
                              limits.x = LIMITS_PATHLENGTH,
                              breaks.x = BREAKS_PATHLENGTH,

                              name.y   = LABEL_PEAK,
                              limits.y = LIMITS_DURATIONPEAK,
                              breaks.y = BREAKS_DURATIONPEAK,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "static-3-5-pathlength-peak")

  # betweenness (normalized)
  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss$index.betweenness.normalized,
                                                y = data.ss$net.dynamic.epidemic.duration),

                              df.2 = NA,

                              name.x   = LABEL_BETWEENNESS,
                              limits.x = LIMITS_BETWEENNESS,
                              breaks.x = BREAKS_BETWEENNESS,

                              name.y   = LABEL_DURATION,
                              limits.y = LIMITS_DURATIONPEAK,
                              breaks.y = BREAKS_DURATIONPEAK,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "dynamic-4-4-betweenness-duration")

  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss$index.betweenness.normalized,
                                                y = data.ss$net.dynamic.epidemic.peak),

                              df.2 = NA,

                              name.x   = LABEL_BETWEENNESS,
                              limits.x = LIMITS_BETWEENNESS,
                              breaks.x = BREAKS_BETWEENNESS,

                              name.y   = LABEL_PEAK,
                              limits.y = LIMITS_DURATIONPEAK,
                              breaks.y = BREAKS_DURATIONPEAK,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "dynamic-4-5-betweenness-peak")

  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss$index.betweenness.normalized,
                                                y = data.ss$net.static.epidemic.duration),

                              df.2 = NA,

                              name.x   = LABEL_BETWEENNESS,
                              limits.x = LIMITS_BETWEENNESS,
                              breaks.x = BREAKS_BETWEENNESS,

                              name.y   = LABEL_DURATION,
                              limits.y = LIMITS_DURATIONPEAK,
                              breaks.y = BREAKS_DURATIONPEAK,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "static-4-4-betweenness-duration")

  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss$index.betweenness.normalized,
                                                y = data.ss$net.static.epidemic.peak),

                              df.2 = NA,

                              name.x   = LABEL_BETWEENNESS,
                              limits.x = LIMITS_BETWEENNESS,
                              breaks.x = BREAKS_BETWEENNESS,

                              name.y   = LABEL_PEAK,
                              limits.y = LIMITS_DURATIONPEAK,
                              breaks.y = BREAKS_DURATIONPEAK,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "static-4-5-betweenness-peak")

  # assortativity
  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss$net.assortativity.pre.epidemic,
                                                y = data.ss$net.dynamic.epidemic.duration),

                              df.2 = NA,

                              name.x   = LABEL_ASSORTATIVITY,
                              limits.x = LIMITS_ASSORTATIVITY,
                              breaks.x = BREAKS_ASSORTATIVITY,

                              name.y   = LABEL_DURATION,
                              limits.y = LIMITS_DURATIONPEAK,
                              breaks.y = BREAKS_DURATIONPEAK,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "dynamic-5-4-assortativity-duration")

  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss$net.assortativity.pre.epidemic,
                                                y = data.ss$net.dynamic.epidemic.peak),

                              df.2 = NA,

                              name.x   = LABEL_ASSORTATIVITY,
                              limits.x = LIMITS_ASSORTATIVITY,
                              breaks.x = BREAKS_ASSORTATIVITY,

                              name.y   = LABEL_PEAK,
                              limits.y = LIMITS_DURATIONPEAK,
                              breaks.y = BREAKS_DURATIONPEAK,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "dynamic-5-5-assortativity-peak")

  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss$net.assortativity.pre.epidemic,
                                                y = data.ss$net.static.epidemic.duration),

                              df.2 = NA,

                              name.x   = LABEL_ASSORTATIVITY,
                              limits.x = LIMITS_ASSORTATIVITY,
                              breaks.x = BREAKS_ASSORTATIVITY,

                              name.y   = LABEL_DURATION,
                              limits.y = LIMITS_DURATIONPEAK,
                              breaks.y = BREAKS_DURATIONPEAK,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "static-5-4-assortativity-duration")

  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss$net.assortativity.pre.epidemic,
                                                y = data.ss$net.static.epidemic.peak),

                              df.2 = NA,

                              name.x   = LABEL_ASSORTATIVITY,
                              limits.x = LIMITS_ASSORTATIVITY,
                              breaks.x = BREAKS_ASSORTATIVITY,

                              name.y   = LABEL_PEAK,
                              limits.y = LIMITS_DURATIONPEAK,
                              breaks.y = BREAKS_DURATIONPEAK,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "static-5-5-assortativity-peak")

  return(plots)
}

export_plots <- function(data.ss = load_simulation_summary_data(),
                         data.ad = load_agent_details_prepared_data()) {

  # create directory if necessary
  dir.create(EXPORT_PATH_PLOTS, showWarnings = FALSE)

  plots <- get_plots(data.ss = data.ss, data.ad = data.ad)

  plot.index <- 1
  name.index <- 2
  while (plot.index < length(plots)) {
    ggsave(paste(EXPORT_PATH_PLOTS, plots[name.index][[1]], EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
           plots[plot.index][[1]],
           width = EXPORT_PLOT_WIDTH,
           height = EXPORT_PLOT_HEIGHT,
           units = EXPORT_SIZE_UNITS,
           dpi = EXPORT_DPI,
           device = EXPORT_FILE_TYPE_PLOTS)

    plot.index <- plot.index + 2
    name.index <- name.index + 2
  }

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
  notes <- paste(notes, "<td class=\"bottomRule\">adjusted R<sup>2</sup></td>\n", sep = "")
  for (i in 1:length(models)) {
    model <- models[[i]]
    ll.null <- model$null.deviance/-2
    ll.proposed <- model$deviance/-2
    notes <- paste(notes, "<td class=\"bottomRule\">",
                   round(rsq(model, adj = TRUE, type = "kl")[[1]], digits = 4),
                   " (",
                   round(1 - pchisq(2 * (ll.proposed - ll.null), df = (length(model$coefficients) - 1)), digits = 4),
                   ")",
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

# normalizing the data (https://bit.ly/3jd8rFb)
# NOTE: data is normalized (rather than standardized, see https://bit.ly/3jd8rFb),
# because of some predictors being highly skewed (e.g. degree, betweenness)
prepare_predictor <- function(vec, normalize = TRUE) {
  if (normalize) {
    vec <- (vec - min(vec)) / (max(vec) - min(vec))
  }
  return(meanCenter(vec))
}

print_r2 <- function(reg) {
  ll.null <- reg$null.deviance/-2
  ll.proposed <- reg$deviance/-2
  print("R^2 according to Cameron & Windmeijer (1997) - https://bit.ly/32rt9dT - using Kullback-Leibler divergence:")
  print(paste("           R^2: ", round(rsq(reg, adj = FALSE, type = "kl"), 4),
              " , p: ", 1 - pchisq(2 * (ll.proposed - ll.null), df = (length(reg$coefficients) - 1)),
              sep = ""))
  print(paste("  adjusted R^2: ", round(rsq(reg, adj = TRUE, type = "kl"), 4),
              " , p: ", 1 - pchisq(2 * (ll.proposed - ll.null), df = (length(reg$coefficients) - 1)),
              sep = ""))
}

#----------------------------------------------------------------------------------------------------#
# function: export_attackrate_models
#     Creates and exports multi-level logistic regression models for attack rate for all conjectures.
# param:  data.ss
#     simulation summary data to produce regression models for
# param:  filenamname.appendix
#     Optional string to append to the standard filename
#----------------------------------------------------------------------------------------------------#
export_network_models <- function(data.ss = load_simulation_summary_data(), filenamname.appendix = "") {

  # ## DATA INTEGRITY CHECKS
  # # quick overview of data
  # head(data.ss)
  # # check whether columns have correct data (types)
  # str(data.ss)
  # # check whether NA values occur in relevant outcome and predictor variable(s)
  # data.ss[
  #   is.na(data.ss$net.dynamic.pct.rec) |
  #
  #     is.na(data.ss$net.dynamic.ties.broken.epidemic) |
  #     is.na(data.ss$net.dynamic.ties.formed.epidemic) |
  #
  #     is.na(data.ss$nb.r.min) |
  #     is.na(data.ss$nb.r.max) |
  #     is.na(data.ss$nb.r.sigma.av) |
  #     is.na(data.ss$nb.alpha) |
  #     is.na(data.ss$nb.omega) |
  #     is.na(data.ss$nb.sigma) |
  #     is.na(data.ss$nb.gamma) |
  #
  #     is.na(data.ss$index.r.sigma.neighborhood) |
  #     is.na(data.ss$net.clustering.pre.epidemic.av) |
  #     is.na(data.ss$net.pathlength.pre.epidemic.av) |
  #     is.na(data.ss$index.betweenness.normalized) |
  #     is.na(data.ss$net.assortativity.pre.epidemic), ]                           # none - otherwise: remove
  #
  # # in case of categorical or boolean predictors, use xtab to check whether there are enough date for each factor, for example:
  # xtabs(~ net.dynamic.pct.rec + net.stable.pre, data = data.ss)

  #### DATA PREPARATIONS ####
  ### INDEPENDENT ###
  ## MAIN EFFECTS
  # ties.broken                   <- prepare_predictor(data.ss$net.dynamic.ties.broken.epidemic)    # collinear with net.changes
  net.changes                   <- prepare_predictor(data.ss$net.dynamic.network.changes.epidemic)
  r.sigma                       <- prepare_predictor(data.ss$nb.r.sigma.av)
  r.sigma.index.neighborhood    <- prepare_predictor(data.ss$index.r.sigma.neighborhood)
  sigma                         <- prepare_predictor(data.ss$nb.sigma)
  gamma                         <- prepare_predictor(data.ss$nb.gamma)
  clustering                    <- prepare_predictor(data.ss$net.clustering.pre.epidemic.av)
  pathlength                    <- prepare_predictor(data.ss$net.pathlength.pre.epidemic.av)
  betweenness                   <- prepare_predictor(data.ss$index.betweenness.normalized)
  assortativity                 <- prepare_predictor(data.ss$net.assortativity.pre.epidemic)
  ## INTERACTION EFFECTS
  net.changes.X.r.sigma                             <- net.changes    * r.sigma
  net.changes.X.r.sigma.index.neighborhood          <- net.changes    * r.sigma.index.neighborhood
  net.changes.X.sigma                               <- net.changes    * sigma
  net.changes.X.gamma                               <- net.changes    * gamma
  net.changes.X.clustering                          <- net.changes    * clustering
  net.changes.X.pathlength                          <- net.changes    * pathlength
  net.changes.X.betweenness                         <- net.changes    * betweenness
  net.changes.X.assortativity                       <- net.changes    * assortativity
  r.sigma.X.r.sigma.index.neighborhood              <- r.sigma        * r.sigma.index.neighborhood
  r.sigma.X.sigma                                   <- r.sigma        * sigma
  r.sigma.X.gamma                                   <- r.sigma        * gamma
  r.sigma.X.clustering                              <- r.sigma        * clustering
  r.sigma.X.pathlength                              <- r.sigma        * pathlength
  r.sigma.X.betweenness                             <- r.sigma        * betweenness
  r.sigma.X.assortativity                           <- r.sigma        * assortativity
  r.sigma.index.neighborhood.X.sigma                <- r.sigma.index.neighborhood             * sigma
  r.sigma.index.neighborhood.X.gamma                <- r.sigma.index.neighborhood             * gamma
  r.sigma.index.neighborhood.X.clustering           <- r.sigma.index.neighborhood             * clustering
  r.sigma.index.neighborhood.X.pathlength           <- r.sigma.index.neighborhood             * pathlength
  r.sigma.index.neighborhood.X.betweenness          <- r.sigma.index.neighborhood             * betweenness
  r.sigma.index.neighborhood.X.assortativity        <- r.sigma.index.neighborhood             * assortativity
  sigma.X.gamma                                     <- sigma          * gamma
  sigma.X.clustering                                <- sigma          * clustering
  sigma.X.pathlength                                <- sigma          * pathlength
  sigma.X.betweenness                               <- sigma          * betweenness
  sigma.X.assortativity                             <- sigma          * assortativity
  gamma.X.clustering                                <- gamma          * clustering
  gamma.X.pathlength                                <- gamma          * pathlength
  gamma.X.betweenness                               <- gamma          * betweenness
  gamma.X.assortativity                             <- gamma          * assortativity
  clustering.X.pathlength                           <- clustering     * pathlength
  clustering.X.betweenness                          <- clustering     * betweenness
  clustering.X.assortativity                        <- clustering     * assortativity
  pathlength.X.betweenness                          <- pathlength     * betweenness
  pathlength.X.assortativity                        <- pathlength     * assortativity
  betweenness.X.assortativity                       <- betweenness    * assortativity

  ### DEPENDENT ###
  attack.rate <- data.ss$net.dynamic.pct.rec / 100
  duration    <- data.ss$net.dynamic.epidemic.duration
  peak        <- data.ss$net.dynamic.epidemic.peak
  peak.size   <- data.ss$net.dynamic.epidemic.max.infections

  ## MAIN EFFECTS (NETWORK DYNAMICS)
  # attack rate
  log.1.attackrate <- glm(attack.rate ~
                            net.changes,
                          family = binomial)
  # summary(log.1.attackrate)
  # print_r2(log.1.attackrate)
  # duration
  log.1.duration <- lm(duration ~
                         net.changes)
  # summary(log.1.duration)
  # print_r2(log.1.duration)
  # peak
  log.1.peak <- lm(peak ~
                     net.changes)
  # summary(log.1.peak)
  # print_r2(log.1.peak)
  # peak size
  log.1.peak.size <- lm(peak.size ~
                          net.changes)
  # summary(log.1.peak.size)
  # print_r2(log.1.peak.size)

  ## MAIN EFFECTS (ACTUAL NETWORK PROPERTIES RATHER THAN PARAMETERS TO CONTROL NETWORK PROPERTIES)
  # attack rate
  log.2.attackrate <- glm(attack.rate ~
                            net.changes +
                            r.sigma +
                            r.sigma.index.neighborhood +
                            sigma +
                            gamma +
                            clustering +
                            pathlength +
                            betweenness +
                            assortativity,
                          family = binomial)
  # summary(log.2.attackrate)
  # vif(log.2.attackrate)
  # print_r2(log.2.attackrate)
  # duration
  log.2.duration <- lm(duration ~
                         net.changes +
                         r.sigma +
                         r.sigma.index.neighborhood +
                         sigma +
                         gamma +
                         clustering +
                         pathlength +
                         betweenness +
                         assortativity)
  # summary(log.2.duration)
  # vif(log.2.duration)
  # print_r2(log.2.duration)
  # peak
  log.2.peak <- lm(peak ~
                     net.changes +
                     r.sigma +
                     r.sigma.index.neighborhood +
                     sigma +
                     gamma +
                     clustering +
                     pathlength +
                     betweenness +
                     assortativity)
  # summary(log.2.peak)
  # vif(log.2.peak)
  # print_r2(log.2.peak)
  # peak size
  log.2.peak.size <- lm(peak.size ~
                          net.changes +
                          r.sigma +
                          r.sigma.index.neighborhood +
                          sigma +
                          gamma +
                          clustering +
                          pathlength +
                          betweenness +
                          assortativity)
  # summary(log.2.peak.size)
  # vif(log.2.peak.size)
  # print_r2(log.2.peak.size)

  ## INTERACTION EFFECTS (NETWORK MEASURES)
  log.3.attackrate <- glm(attack.rate ~
                            net.changes +
                            r.sigma +
                            r.sigma.index.neighborhood +
                            sigma +
                            gamma +
                            clustering +
                            pathlength +
                            betweenness +
                            assortativity +

                            # net.changes.X.r.sigma +
                            # net.changes.X.r.sigma.index.neighborhood +
                            # net.changes.X.sigma +
                            net.changes.X.gamma +
                            # net.changes.X.clustering +
                            # net.changes.X.pathlength +
                            # net.changes.X.betweenness +
                            net.changes.X.assortativity +
                            # r.sigma.X.r.sigma.index.neighborhood +
                            # r.sigma.X.sigma +
                            # r.sigma.X.gamma +
                            # r.sigma.X.clustering +
                            # r.sigma.X.pathlength +
                            # r.sigma.X.betweenness +
                            # r.sigma.X.assortativity +
                            # r.sigma.index.neighborhood.X.sigma +
                            # r.sigma.index.neighborhood.X.gamma +
                            # r.sigma.index.neighborhood.X.clustering +
                            # r.sigma.index.neighborhood.X.pathlength +
                            # r.sigma.index.neighborhood.X.betweenness +
                            # r.sigma.index.neighborhood.X.assortativity +
                            # sigma.X.gamma +
                            # sigma.X.clustering +
                            # sigma.X.pathlength +
                            # sigma.X.betweenness +
                            # sigma.X.assortativity +
                            gamma.X.clustering +
                            # gamma.X.pathlength +
                            # gamma.X.betweenness +
                            gamma.X.assortativity +
                            # clustering.X.pathlength +
                            # clustering.X.betweenness +
                            clustering.X.assortativity
                            # pathlength.X.betweenness +
                            # pathlength.X.assortativity
                            # betweenness.X.assortativity
                          ,
               family = binomial)
  # summary(log.3.attackrate)
  # vif(log.3.attackrate)
  # print_r2(log.3.attackrate)

  log.3.duration <- lm(duration ~
                         net.changes +
                         r.sigma +
                         r.sigma.index.neighborhood +
                         sigma +
                         gamma +
                         clustering +
                         pathlength +
                         betweenness +
                         assortativity +

                         # net.changes.X.r.sigma +
                         # net.changes.X.r.sigma.index.neighborhood +
                         # net.changes.X.sigma +
                         net.changes.X.gamma +
                         # net.changes.X.clustering +
                         net.changes.X.pathlength +
                         # net.changes.X.betweenness +
                         net.changes.X.assortativity +
                         # r.sigma.X.r.sigma.index.neighborhood +
                         # r.sigma.X.sigma +
                         # r.sigma.X.gamma +
                         # r.sigma.X.clustering +
                         # r.sigma.X.pathlength +
                         # r.sigma.X.betweenness +
                         # r.sigma.X.assortativity +
                         # r.sigma.index.neighborhood.X.sigma +
                         # r.sigma.index.neighborhood.X.gamma +
                         # r.sigma.index.neighborhood.X.clustering +
                         # r.sigma.index.neighborhood.X.pathlength +
                         # r.sigma.index.neighborhood.X.betweenness +
                         # r.sigma.index.neighborhood.X.assortativity +
                         # sigma.X.gamma +
                         # sigma.X.clustering +
                         # sigma.X.pathlength +
                         # sigma.X.betweenness +
                         # sigma.X.assortativity +
                         # gamma.X.clustering +
                         # gamma.X.pathlength +
                         # gamma.X.betweenness +
                         # gamma.X.assortativity +
                         # clustering.X.pathlength +
                         # clustering.X.betweenness +
                         clustering.X.assortativity
                         # pathlength.X.betweenness
                         # pathlength.X.assortativity +
                         # betweenness.X.assortativity
                       )
  # summary(log.3.duration)
  # vif(log.3.duration)
  # print_r2(log.3.duration)

  log.3.peak <- lm(peak ~
                     net.changes +
                     r.sigma +
                     r.sigma.index.neighborhood +
                     sigma +
                     gamma +
                     clustering +
                     pathlength +
                     betweenness +
                     assortativity +

                     # net.changes.X.r.sigma +
                     # net.changes.X.r.sigma.index.neighborhood +
                     # net.changes.X.sigma +
                     net.changes.X.gamma +
                     net.changes.X.clustering +
                     # net.changes.X.pathlength +
                     # net.changes.X.betweenness +
                     net.changes.X.assortativity +
                     # r.sigma.X.r.sigma.index.neighborhood +
                     # r.sigma.X.sigma +
                     # r.sigma.X.gamma +
                     # r.sigma.X.clustering +
                     # r.sigma.X.pathlength +
                     # r.sigma.X.betweenness +
                     # r.sigma.X.assortativity +
                     # r.sigma.index.neighborhood.X.sigma +
                     # r.sigma.index.neighborhood.X.gamma +
                     # r.sigma.index.neighborhood.X.clustering +
                     # r.sigma.index.neighborhood.X.pathlength +
                     # r.sigma.index.neighborhood.X.betweenness +
                     # r.sigma.index.neighborhood.X.assortativity +
                     # sigma.X.gamma +
                     # sigma.X.clustering +
                     # sigma.X.pathlength +
                     # sigma.X.betweenness +
                     # sigma.X.assortativity +
                     # gamma.X.clustering +
                     # gamma.X.pathlength +
                     # gamma.X.betweenness +
                     # gamma.X.assortativity +
                     # clustering.X.pathlength +
                     # clustering.X.betweenness +
                     clustering.X.assortativity
                     # pathlength.X.betweenness +
                     # pathlength.X.assortativity
                     # betweenness.X.assortativity
                   )
  # summary(log.3.peak)
  # vif(log.3.peak)
  # print_r2(log.3.peak)

  log.3.peak.size <- lm(peak.size ~
                          net.changes +
                          r.sigma +
                          r.sigma.index.neighborhood +
                          sigma +
                          gamma +
                          clustering +
                          pathlength +
                          betweenness +
                          assortativity +

                          # net.changes.X.r.sigma +
                          # net.changes.X.r.sigma.index.neighborhood +
                          # net.changes.X.sigma +
                          net.changes.X.gamma +
                          # net.changes.X.clustering +
                          # net.changes.X.pathlength +
                          # net.changes.X.betweenness +
                          net.changes.X.assortativity +
                          # r.sigma.X.r.sigma.index.neighborhood +
                          # r.sigma.X.sigma +
                          # r.sigma.X.gamma +
                          # r.sigma.X.clustering +
                          # r.sigma.X.pathlength +
                          # r.sigma.X.betweenness +
                          # r.sigma.X.assortativity +
                          # r.sigma.index.neighborhood.X.sigma +
                          # r.sigma.index.neighborhood.X.gamma +
                          # r.sigma.index.neighborhood.X.clustering +
                          # r.sigma.index.neighborhood.X.pathlength +
                          # r.sigma.index.neighborhood.X.betweenness +
                          # r.sigma.index.neighborhood.X.assortativity +
                          # sigma.X.gamma +
                          # sigma.X.clustering +
                          # sigma.X.pathlength +
                          # sigma.X.betweenness +
                          # sigma.X.assortativity +
                          gamma.X.clustering +
                          # gamma.X.pathlength +
                          # gamma.X.betweenness +
                          gamma.X.assortativity +
                          # clustering.X.pathlength +
                          # clustering.X.betweenness +
                          clustering.X.assortativity
                          # pathlength.X.betweenness +
                          # pathlength.X.assortativity
                          # betweenness.X.assortativity
                        )
  # summary(log.3.peak.size)
  # vif(log.3.peak.size)
  # print_r2(log.3.peak.size)



  ### FILE EXPORT ###
  filename <- "reg-attackrate-all"
  if (filenamname.appendix != "") {
    filename <- paste(filename, "-", filenamname.appendix, sep = "")
  }
  exportModels(list(log.1.attackrate,
                    log.2.attackrate,
                    log.3.attackrate), filename)
  filename <- "reg-duration-all"
  if (filenamname.appendix != "") {
    filename <- paste(filename, "-", filenamname.appendix, sep = "")
  }
  exportModels(list(log.1.duration,
                    log.2.duration,
                    log.3.duration), filename)
  filename <- "reg-peak-all"
  if (filenamname.appendix != "") {
    filename <- paste(filename, "-", filenamname.appendix, sep = "")
  }
  exportModels(list(log.1.peak,
                    log.2.peak,
                    log.3.peak), filename)
  filename <- "reg-peaksize-all"
  if (filenamname.appendix != "") {
    filename <- paste(filename, "-", filenamname.appendix, sep = "")
  }
  exportModels(list(log.1.peak.size,
                    log.2.peak.size,
                    log.3.peak.size), filename)
}


export_interactions_network <- function() {
  mains <- c("net.changes", "r.sigma", "r.sigma.index.neighborhood", "sigma",
             "gamma", "clustering", "pathlength", "betweenness", "assortativity")
  out.init.ints   <- ""
  out.model.ints  <- ""
  for (m1 in seq(1, length(mains)-1, 1)) {
    main1 <- mains[m1]

    for (m2 in seq(m1+1, length(mains), 1)) {
      main2 <- mains[m2]

      int <- paste(main1, ".X.", main2, sep = "")
      out.model.ints <- paste(out.model.ints, int, " +\n", sep = "")
      for (i in seq(1: (50 - nchar(int)))) {
        int <- paste(int, " ", sep = "")
      }
      int <- paste(int, "<- ", main1, sep = "")
      for (i in seq(1: (15 - nchar(main1)))) {
        int <- paste(int, " ", sep = "")
      }
      int <- paste(int, "* ", main2, sep = "")
      out.init.ints <- paste(out.init.ints, int, "\n", sep = "")
    }
  }

  out.model.mains <- ""
  for (main in mains) {
    out.model.mains <- paste(out.model.mains, main, " +\n", sep = "")
  }

  out <- paste(out.init.ints, "\n\n\n\n",
               out.model.mains, "\n",
               out.model.ints, sep = "")

  dir.create(EXPORT_PATH_NUM, showWarnings = FALSE)
  cat(out, file = paste(EXPORT_PATH_NUM,
                        "interactions-network",
                        EXPORT_FILE_EXTENSION_DESC,
                        sep = ""))
}

