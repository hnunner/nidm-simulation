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
CSV_ROUND_SUMMARY_PATH          <- paste(DATA_PATH, "round-summary.csv", sep = "")
CSV_ROUND_SUMMARY_PREPARED_PATH <- paste(DATA_PATH, "round-summary-prepared.csv", sep = "")
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
# sir plots
RIBBON_ALPHA                <- 0.3
# colors (http://mkweb.bcgsc.ca/colorblind/)
COLORS                      <- c(Susceptible         = "#F0E442",   # yellow
                                 Infected            = "#E69F00",   # orange
                                 Recovered           = "#0072B2",   # blue

                                 Degree              = "#888888",   # grey
                                 Degree.index        = "#000000",   # black

                                 Clustering          = "#888888",   # grey
                                 Clustering.index    = "#000000",   # black

                                 Pathlength          = "#888888",   # grey
                                 Pathlength.index    = "#000000",   # black

                                 Betweenness         = "#888888",   # grey
                                 Betweenness.index   = "#000000",   # black

                                 Closeness           = "#888888",   # grey
                                 Closeness.index     = "#000000",   # black

                                 Assortativity       = "#888888",   # grey
                                 Assortativity.index = "#000000")   # white
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
# function: load_agent_details_data
#     Loads agent details data.
# return: the agent details data
#----------------------------------------------------------------------------------------------------#
load_agent_details_data <- function() {
  return(load_csv(CSV_AGENT_DETAILS_PATH))
}

#----------------------------------------------------------------------------------------------------#
# function: load_agent_details_prepared_data
#     Loads readily prepared agent details data.
# return: the agent details data
#----------------------------------------------------------------------------------------------------#
load_agent_details_prepared_data <- function() {
  return(load_csv(CSV_AGENT_DETAILS_PREPARED_PATH))
}

#----------------------------------------------------------------------------------------------------#
# function: load_round_summary_data
#     Loads round summary data.
# return: the round summary data
#----------------------------------------------------------------------------------------------------#
load_round_summary_data <- function() {
  return(load_csv(CSV_ROUND_SUMMARY_PATH))
}

#----------------------------------------------------------------------------------------------------#
# function: load_round_summary_prepared_data
#     Loads readily prepared round summary data.
# return: the round summary data
#----------------------------------------------------------------------------------------------------#
load_round_summary_prepared_data <- function() {
  return(load_csv(CSV_ROUND_SUMMARY_PREPARED_PATH))
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
  out <- paste(out, get_descriptive(data.ss$net.static.epidemic.duration, "Duration"))
  out <- paste(out, get_descriptive(data.ss.arlarge$net.static.epidemic.duration, "Duration (attack rates 90\\%+)"))
  out <- paste(out, get_descriptive(data.ss$net.static.epidemic.peak, "Epidemic peak"))
  out <- paste(out, get_descriptive(data.ss.arlarge$net.static.epidemic.peak, "Epidemic peak (attack rates 90\\%+)"))
  out <- paste(out, get_descriptive(data.ss$net.static.epidemic.peak.size, "Epidemic peak size"))
  out <- paste(out, get_descriptive(data.ss.arlarge$net.static.epidemic.peak.size, "Epidemic peak size (attack rates 90\\%+)"))
  out <- paste(out, get_descriptive(data.ss$net.static.ties.broken.active.epidemic, "Ties broken"))
  out <- paste(out, get_descriptive(data.ss$net.static.ties.out.accepted.epidemic - data.ss$net.static.ties.broken.epidemic, "Ties formed"))
  out <- paste(out, "\\multicolumn{6}{l}{\\textbf{I.II. Epidemic, dynamic}}", " \\", "\\ \n", sep = "")
  out <- paste(out, get_descriptive(data.ss$net.dynamic.pct.rec, "Attack rate"))
  out <- paste(out, get_descriptive(data.ss$net.dynamic.epidemic.duration, "Duration"))
  out <- paste(out, get_descriptive(data.ss.arlarge$net.dynamic.epidemic.duration, "Duration (attack rates 90\\%+)"))
  out <- paste(out, get_descriptive(data.ss$net.dynamic.epidemic.peak, "Epidemic peak"))
  out <- paste(out, get_descriptive(data.ss.arlarge$net.dynamic.epidemic.peak, "Epidemic peak (attack rates 90\\%+)"))
  out <- paste(out, get_descriptive(data.ss$net.dynamic.epidemic.peak.size, "Epidemic peak size"))
  out <- paste(out, get_descriptive(data.ss.arlarge$net.dynamic.epidemic.peak.size, "Epidemic peak size (attack rates 90\\%+)"))
  out <- paste(out, get_descriptive(data.ss$net.dynamic.ties.broken.active.epidemic, "Ties broken"))
  out <- paste(out, get_descriptive(data.ss$net.dynamic.ties.out.accepted.epidemic, "Ties formed"))

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
  out <- paste(out, "\\multicolumn{6}{l}{\\textbf{III.II. Agent, dependent}}", " \\", "\\ \n", sep = "")
  out <- paste(out, get_descriptive(data.ad$agent.degree, "Degree ($\\mathcal{D}_{i}$)*"))
  out <- paste(out, get_descriptive(data.ad$agent.clustering, "Clustering ($\\mathcal{C}_{i}$)*"))
  out <- paste(out, get_descriptive(data.ad$agent.betweenness.normalized, "Betweenness ($\\mathcal{B}_{i}$)*"))
  out <- paste(out, get_descriptive(data.ad$nb.r.sigma.neighborhood, "Risk perception of direct ties ($r^{t_{i}}_{\\sigma, \\gamma})$"))
  # out <- paste(out, "\\multicolumn{6}{l}{\\textbf{III.II. Agent, dependent (index case)}}", " \\", "\\ \n", sep = "")
  # out <- paste(out, get_descriptive(data.ss$index.degree, "Degree ($\\mathcal{D}_{index}$)*"))
  # out <- paste(out, get_descriptive(data.ss$index.clustering, "Clustering ($\\mathcal{C}_{index}$)*"))
  # out <- paste(out, get_descriptive(data.ss$index.betweenness.normalized, "Betweenness ($\\mathcal{B}_{index}$)*"))
  # out <- paste(out, get_descriptive(data.ss$index.r.sigma.neighborhood, "Risk perception of direct ties ($r^{t_{index}}_{\\sigma, \\gamma})$"))

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
                          "alpha", "Network clustering",
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
                                         "network clustering"),
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
  ggsave(paste(EXPORT_PATH_PLOTS, "cor-3-alpha-betweenness", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
         plot_correlation(data.ss,
                          data.ss$nb.alpha, data.ss$net.betweenness.pre.epidemic.av,
                          "alpha", "Average betweenness",
                          c(0, 1), c(0, 1),
                          seq(0, 1, 1/4), seq(0, 1, 1/4)),
         width = EXPORT_PLOT_HEIGHT,
         height = EXPORT_PLOT_HEIGHT,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)
  # correlation
  out <- paste(out, get_correlation_text(data.ss$nb.alpha,
                                         data.ss$net.betweenness.pre.epidemic.av,
                                         "alpha",
                                         "av. betweenness"),
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

  # out <- paste(out, "\n\n", sep = "")
  # out <- paste(out, "##############################################################\n", sep = "")
  # out <- paste(out, "#######            CORRELATIONS OF CLUSTERING           ######\n", sep = "")
  # out <- paste(out, "##############################################################\n\n", sep = "")
  #
  # # plot
  # ggsave(paste(EXPORT_PATH_PLOTS, "cor-5-clustering-pathlength", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
  #        plot_correlation(data.ss,
  #                         data.ss$net.clustering.pre.epidemic.av, data.ss$net.pathlength.pre.epidemic.av,
  #                         "Network clustering", "Average path length",
  #                         c(0, 1), c(1, 7),
  #                         seq(0, 1, 1/4), seq(1, 7, 1)),
  #        width = EXPORT_PLOT_HEIGHT,
  #        height = EXPORT_PLOT_HEIGHT,
  #        units = EXPORT_SIZE_UNITS,
  #        dpi = EXPORT_DPI,
  #        device = EXPORT_FILE_TYPE_PLOTS)
  # # correlation
  # out <- paste(out, get_correlation_text(data.ss$net.clustering.pre.epidemic.av,
  #                                        data.ss$net.pathlength.pre.epidemic.av,
  #                                        "network clustering",
  #                                        "av. path length"),
  #              sep = "")
  #
  # out <- paste(out, "\n\n", sep = "")
  # out <- paste(out, "##############################################################\n", sep = "")
  # out <- paste(out, "#######       CORRELATIONS OF DEPENDENT VARIABLES       ######\n", sep = "")
  # out <- paste(out, "##############################################################\n\n", sep = "")
  #
  # # plot
  # ggsave(paste(EXPORT_PATH_PLOTS, "cor-6-attackrate-duration-dynamic", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
  #        plot_correlation(data.ss,
  #                         data.ss$net.dynamic.pct.rec, data.ss$net.dynamic.epidemic.duration,
  #                         "Attack rate", "Duration",
  #                         c(0, 100), c(5, 50),
  #                         seq(0, 100, 25), seq(5, 50, 5)),
  #        width = EXPORT_PLOT_HEIGHT,
  #        height = EXPORT_PLOT_HEIGHT,
  #        units = EXPORT_SIZE_UNITS,
  #        dpi = EXPORT_DPI,
  #        device = EXPORT_FILE_TYPE_PLOTS)
  # # correlation
  # out <- paste(out, get_correlation_text(data.ss$net.dynamic.pct.rec,
  #                                        data.ss$net.dynamic.epidemic.duration,
  #                                        "attack rate (dynamic)",
  #                                        "duration (dynamic)"),
  #              sep = "")
  #
  # # plot
  # ggsave(paste(EXPORT_PATH_PLOTS, "cor-6-attackrate-duration-static", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
  #        plot_correlation(data.ss,
  #                         data.ss$net.static.pct.rec, data.ss$net.static.epidemic.duration,
  #                         "Attack rate", "Duration",
  #                         c(0, 100), c(5, 50),
  #                         seq(0, 100, 25), seq(5, 50, 5)),
  #        width = EXPORT_PLOT_HEIGHT,
  #        height = EXPORT_PLOT_HEIGHT,
  #        units = EXPORT_SIZE_UNITS,
  #        dpi = EXPORT_DPI,
  #        device = EXPORT_FILE_TYPE_PLOTS)
  # # correlation
  # out <- paste(out, get_correlation_text(data.ss$net.static.pct.rec,
  #                                        data.ss$net.static.epidemic.duration,
  #                                        "attack rate (static)",
  #                                        "duration (static)"),
  #              sep = "")
  #
  # # plot
  # ggsave(paste(EXPORT_PATH_PLOTS, "cor-7-attackrate-peak-dynamic", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
  #        plot_correlation(data.ss,
  #                         data.ss$net.dynamic.pct.rec, data.ss$net.dynamic.epidemic.peak,
  #                         "Attack rate", "Peak",
  #                         c(0, 100), c(5, 50),
  #                         seq(0, 100, 25), seq(5, 50, 5)),
  #        width = EXPORT_PLOT_HEIGHT,
  #        height = EXPORT_PLOT_HEIGHT,
  #        units = EXPORT_SIZE_UNITS,
  #        dpi = EXPORT_DPI,
  #        device = EXPORT_FILE_TYPE_PLOTS)
  # # correlation
  # out <- paste(out, get_correlation_text(data.ss$net.dynamic.pct.rec,
  #                                        data.ss$net.dynamic.epidemic.peak,
  #                                        "attack rate (dynamic)",
  #                                        "peak (dynamic)"),
  #              sep = "")
  #
  # # plot
  # ggsave(paste(EXPORT_PATH_PLOTS, "cor-7-attackrate-peak-static", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
  #        plot_correlation(data.ss,
  #                         data.ss$net.static.pct.rec, data.ss$net.static.epidemic.peak,
  #                         "Attack rate", "Peak",
  #                         c(0, 100), c(5, 50),
  #                         seq(0, 100, 25), seq(5, 50, 5)),
  #        width = EXPORT_PLOT_HEIGHT,
  #        height = EXPORT_PLOT_HEIGHT,
  #        units = EXPORT_SIZE_UNITS,
  #        dpi = EXPORT_DPI,
  #        device = EXPORT_FILE_TYPE_PLOTS)
  # # correlation
  # out <- paste(out, get_correlation_text(data.ss$net.static.pct.rec,
  #                                        data.ss$net.static.epidemic.peak,
  #                                        "attack rate (static)",
  #                                        "peak (static)"),
  #              sep = "")
  #
  # # plot
  # ggsave(paste(EXPORT_PATH_PLOTS, "cor-8-duration-peak-dynamic", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
  #        plot_correlation(data.ss,
  #                         data.ss$net.dynamic.epidemic.duration, data.ss$net.dynamic.epidemic.peak,
  #                         "Attack rate", "Peak",
  #                         c(5, 50), c(5, 50),
  #                         seq(5, 50, 5), seq(5, 50, 5)),
  #        width = EXPORT_PLOT_HEIGHT,
  #        height = EXPORT_PLOT_HEIGHT,
  #        units = EXPORT_SIZE_UNITS,
  #        dpi = EXPORT_DPI,
  #        device = EXPORT_FILE_TYPE_PLOTS)
  # # correlation
  # out <- paste(out, get_correlation_text(data.ss$net.dynamic.epidemic.duration,
  #                                        data.ss$net.dynamic.epidemic.peak,
  #                                        "duration (dynamic)",
  #                                        "peak (dynamic)"),
  #              sep = "")
  #
  # # plot
  # ggsave(paste(EXPORT_PATH_PLOTS, "cor-8-duration-peak-static", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
  #        plot_correlation(data.ss,
  #                         data.ss$net.static.epidemic.duration, data.ss$net.static.epidemic.peak,
  #                         "Attack rate", "Peak",
  #                         c(5, 50), c(5, 50),
  #                         seq(5, 50, 5), seq(5, 50, 5)),
  #        width = EXPORT_PLOT_HEIGHT,
  #        height = EXPORT_PLOT_HEIGHT,
  #        units = EXPORT_SIZE_UNITS,
  #        dpi = EXPORT_DPI,
  #        device = EXPORT_FILE_TYPE_PLOTS)
  # # correlation
  # out <- paste(out, get_correlation_text(data.ss$net.static.epidemic.duration,
  #                                        data.ss$net.static.epidemic.peak,
  #                                        "duration (static)",
  #                                        "peak (static)"),
  #              sep = "")

  ##### EXPORT
  dir.create(EXPORT_PATH_NUM, showWarnings = FALSE)
  cat(out, file = paste(EXPORT_PATH_NUM,
                        "correlations",
                        EXPORT_FILE_EXTENSION_DESC,
                        sep = ""))
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
plotSIRDevelopment <- function(rsData = load_round_summary_prepared_data(),
                               showLegend = TRUE,
                               showRibbons = TRUE,
                               showAdditional = "none",
                               showAdditionalRibbons = FALSE,
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
  if (showAdditional == "clustering" ||
      showAdditional == "betweenness" ||
      showAdditional == "closeness" ||
      showAdditional == "assortativity") {
    scaleFactor           <- 100
  }
  if (showAdditional == "degree" ||
      showAdditional == "pathlength") {
    scaleFactor           <- 10
  }

  ### DEGREE data
  if (showAdditional == "degree") {
    # preparations :: statistical summary for degrees
    summaryDegrees       <- as.data.frame(do.call(rbind, with(rsData, tapply(net.degree.av, sim.round, summary))))
    summaryDegreesIndex  <- as.data.frame(do.call(rbind, with(rsData, tapply(index.degree, sim.round, summary))))

    # data for lines :: median
    degreeData          <- data.frame(rounds, "Degree", summaryDegrees$Median * scaleFactor)
    names(degreeData)   <- c("Timestep", "Measure", "Frequency")
    plotData            <- rbind(plotData, degreeData)

    degreeIndexData     <- data.frame(rounds, "Degree.index", summaryDegreesIndex$Median * scaleFactor)
    names(degreeIndexData)   <- c("Timestep", "Measure", "Frequency")
    plotData            <- rbind(plotData, degreeIndexData)

    # data for ribbons :: 1st and 3rd quartile per compartment
    degreeRibbonData    <- data.frame(rounds,
                                      summaryDegrees$`1st Qu.` * scaleFactor,
                                      summaryDegrees$`3rd Qu.` * scaleFactor)
    names(degreeRibbonData) <- c("Timestep", "DegMin", "DegMax")
    ribbonData$DegMin       <- degreeRibbonData$DegMin[match(ribbonData$Timestep, degreeRibbonData$Timestep)]
    ribbonData$DegMax       <- degreeRibbonData$DegMax[match(ribbonData$Timestep, degreeRibbonData$Timestep)]

    degreeRibbonIndexData   <- data.frame(rounds,
                                      summaryDegreesIndex$`1st Qu.` * scaleFactor,
                                      summaryDegreesIndex$`3rd Qu.` * scaleFactor)
    names(degreeRibbonIndexData) <- c("Timestep", "DegIndexMin", "DegIndexMax")
    ribbonData$DegIndexMin       <- degreeRibbonIndexData$DegIndexMin[match(ribbonData$Timestep, degreeRibbonIndexData$Timestep)]
    ribbonData$DegIndexMax       <- degreeRibbonIndexData$DegIndexMax[match(ribbonData$Timestep, degreeRibbonIndexData$Timestep)]
  }

  ### NETWORK CLUSTERING data
  if (showAdditional == "clustering") {
    # preparations :: statistical summary for clustering
    summaryClustering      <- as.data.frame(do.call(rbind, with(rsData, tapply(net.clustering.av, sim.round, summary))))
    summaryClusteringIndex <- as.data.frame(do.call(rbind, with(rsData, tapply(index.clustering, sim.round, summary))))

    # data for lines :: median
    clusteringData        <- data.frame(rounds, "Clustering", summaryClustering$Median * scaleFactor)
    names(clusteringData) <- c("Timestep", "Measure", "Frequency")
    plotData              <- rbind(plotData, clusteringData)

    clusteringIndexData        <- data.frame(rounds, "Clustering.index", summaryClusteringIndex$Median * scaleFactor)
    names(clusteringIndexData) <- c("Timestep", "Measure", "Frequency")
    plotData                   <- rbind(plotData, clusteringIndexData)

    # data for ribbons :: 1st and 3rd quartile per compartment
    clusteringRibbonData  <- data.frame(rounds,
                                        summaryClustering$`1st Qu.` * scaleFactor,
                                        summaryClustering$`3rd Qu.` * scaleFactor)
    names(clusteringRibbonData) <- c("Timestep", "ClusMin", "ClusMax")
    ribbonData$ClusMin          <- clusteringRibbonData$ClusMin[match(ribbonData$Timestep, clusteringRibbonData$Timestep)]
    ribbonData$ClusMax          <- clusteringRibbonData$ClusMax[match(ribbonData$Timestep, clusteringRibbonData$Timestep)]

    clusteringIndexRibbonData   <- data.frame(rounds,
                                              summaryClusteringIndex$`1st Qu.` * scaleFactor,
                                              summaryClusteringIndex$`3rd Qu.` * scaleFactor)
    names(clusteringIndexRibbonData) <- c("Timestep", "ClusIndexMin", "ClusIndexMax")
    ribbonData$ClusIndexMin          <- clusteringIndexRibbonData$ClusIndexMin[match(ribbonData$Timestep, clusteringIndexRibbonData$Timestep)]
    ribbonData$ClusIndexMax          <- clusteringIndexRibbonData$ClusIndexMax[match(ribbonData$Timestep, clusteringIndexRibbonData$Timestep)]
  }

  ### BETWEENNESS data
  if (showAdditional == "betweenness") {
    # preparations :: statistical summary for betweenness
    summaryBetweenness      <- as.data.frame(do.call(rbind, with(rsData, tapply(net.betweenness.av, sim.round, summary))))
    summaryBetweennessIndex <- as.data.frame(do.call(rbind, with(rsData, tapply(index.betweenness.normalized, sim.round, summary))))

    # data for lines :: median
    betweennessData        <- data.frame(rounds, "Betweenness", summaryBetweenness$Median * scaleFactor)
    names(betweennessData) <- c("Timestep", "Measure", "Frequency")
    plotData              <- rbind(plotData, betweennessData)

    betweennessIndexData        <- data.frame(rounds, "Betweenness.index", summaryBetweennessIndex$Median * scaleFactor)
    names(betweennessIndexData) <- c("Timestep", "Measure", "Frequency")
    plotData                   <- rbind(plotData, betweennessIndexData)

    # data for ribbons :: 1st and 3rd quartile per compartment
    betweennessRibbonData  <- data.frame(rounds,
                                        summaryBetweenness$`1st Qu.` * scaleFactor,
                                        summaryBetweenness$`3rd Qu.` * scaleFactor)
    names(betweennessRibbonData) <- c("Timestep", "BetwMin", "BetwMax")
    ribbonData$BetwMin          <- betweennessRibbonData$BetwMin[match(ribbonData$Timestep, betweennessRibbonData$Timestep)]
    ribbonData$BetwMax          <- betweennessRibbonData$BetwMax[match(ribbonData$Timestep, betweennessRibbonData$Timestep)]

    betweennessIndexRibbonData   <- data.frame(rounds,
                                              summaryBetweennessIndex$`1st Qu.` * scaleFactor,
                                              summaryBetweennessIndex$`3rd Qu.` * scaleFactor)
    names(betweennessIndexRibbonData) <- c("Timestep", "BetwIndexMin", "BetwIndexMax")
    ribbonData$BetwIndexMin          <- betweennessIndexRibbonData$BetwIndexMin[match(ribbonData$Timestep, betweennessIndexRibbonData$Timestep)]
    ribbonData$BetwIndexMax          <- betweennessIndexRibbonData$BetwIndexMax[match(ribbonData$Timestep, betweennessIndexRibbonData$Timestep)]
  }

  ### CLOSENESS data
  if (showAdditional == "closeness") {
    # preparations :: statistical summary for closeness
    summaryCloseness      <- as.data.frame(do.call(rbind, with(rsData, tapply(net.closeness.av, sim.round, summary))))
    summaryClosenessIndex <- as.data.frame(do.call(rbind, with(rsData, tapply(index.closeness, sim.round, summary))))

    # data for lines :: median
    closenessData        <- data.frame(rounds, "Closeness", summaryCloseness$Median * scaleFactor)
    names(closenessData) <- c("Timestep", "Measure", "Frequency")
    plotData              <- rbind(plotData, closenessData)

    closenessIndexData        <- data.frame(rounds, "Closeness.index", summaryClosenessIndex$Median * scaleFactor)
    names(closenessIndexData) <- c("Timestep", "Measure", "Frequency")
    plotData                   <- rbind(plotData, closenessIndexData)

    # data for ribbons :: 1st and 3rd quartile per compartment
    closenessRibbonData  <- data.frame(rounds,
                                        summaryCloseness$`1st Qu.` * scaleFactor,
                                        summaryCloseness$`3rd Qu.` * scaleFactor)
    names(closenessRibbonData) <- c("Timestep", "ClosMin", "ClosMax")
    ribbonData$ClosMin          <- closenessRibbonData$ClosMin[match(ribbonData$Timestep, closenessRibbonData$Timestep)]
    ribbonData$ClosMax          <- closenessRibbonData$ClosMax[match(ribbonData$Timestep, closenessRibbonData$Timestep)]

    closenessIndexRibbonData   <- data.frame(rounds,
                                              summaryClosenessIndex$`1st Qu.` * scaleFactor,
                                              summaryClosenessIndex$`3rd Qu.` * scaleFactor)
    names(closenessIndexRibbonData) <- c("Timestep", "ClosIndexMin", "ClosIndexMax")
    ribbonData$ClosIndexMin          <- closenessIndexRibbonData$ClosIndexMin[match(ribbonData$Timestep, closenessIndexRibbonData$Timestep)]
    ribbonData$ClosIndexMax          <- closenessIndexRibbonData$ClosIndexMax[match(ribbonData$Timestep, closenessIndexRibbonData$Timestep)]
  }

  ### ASSORTATIVITY data
  if (showAdditional == "assortativity") {
    # preparations :: statistical summary for assortativity
    summaryAssortativity      <- as.data.frame(do.call(rbind, with(rsData, tapply(net.assortativity, sim.round, summary))))
    summaryAssortativityIndex <- as.data.frame(do.call(rbind, with(rsData, tapply(index.assortativity, sim.round, summary))))

    # data for lines :: median
    assortativityData        <- data.frame(rounds, "Assortativity", summaryAssortativity$Median * scaleFactor)
    names(assortativityData) <- c("Timestep", "Measure", "Frequency")
    plotData              <- rbind(plotData, assortativityData)

    assortativityIndexData        <- data.frame(rounds, "Assortativity.index", summaryAssortativityIndex$Median * scaleFactor)
    names(assortativityIndexData) <- c("Timestep", "Measure", "Frequency")
    plotData                   <- rbind(plotData, assortativityIndexData)

    # data for ribbons :: 1st and 3rd quartile per compartment
    assortativityRibbonData  <- data.frame(rounds,
                                        summaryAssortativity$`1st Qu.` * scaleFactor,
                                        summaryAssortativity$`3rd Qu.` * scaleFactor)
    names(assortativityRibbonData) <- c("Timestep", "AssoMin", "AssoMax")
    ribbonData$AssoMin          <- assortativityRibbonData$AssoMin[match(ribbonData$Timestep, assortativityRibbonData$Timestep)]
    ribbonData$AssoMax          <- assortativityRibbonData$AssoMax[match(ribbonData$Timestep, assortativityRibbonData$Timestep)]

    assortativityIndexRibbonData   <- data.frame(rounds,
                                              summaryAssortativityIndex$`1st Qu.` * scaleFactor,
                                              summaryAssortativityIndex$`3rd Qu.` * scaleFactor)
    names(assortativityIndexRibbonData) <- c("Timestep", "AssoIndexMin", "AssoIndexMax")
    ribbonData$AssoIndexMin          <- assortativityIndexRibbonData$AssoIndexMin[match(ribbonData$Timestep, assortativityIndexRibbonData$Timestep)]
    ribbonData$AssoIndexMax          <- assortativityIndexRibbonData$AssoIndexMax[match(ribbonData$Timestep, assortativityIndexRibbonData$Timestep)]
  }

  ### AVERAGE PATH LENGTH data
  if (showAdditional == "pathlength") {
    # preparations :: statistical summary for pathlength
    summaryPathlength      <- as.data.frame(do.call(rbind, with(rsData, tapply(net.pathlength.av, sim.round, summary))))

    # data for lines :: median
    pathlengthData        <- data.frame(rounds, "Pathlength", summaryPathlength$Median * scaleFactor)
    names(pathlengthData) <- c("Timestep", "Measure", "Frequency")
    plotData              <- rbind(plotData, pathlengthData)

    # data for ribbons :: 1st and 3rd quartile per compartment
    pathlengthRibbonData  <- data.frame(rounds,
                                        summaryPathlength$`1st Qu.` * scaleFactor,
                                        summaryPathlength$`3rd Qu.` * scaleFactor)
    names(pathlengthRibbonData) <- c("Timestep", "PathMin", "PathMax")
    ribbonData$PathMin          <- pathlengthRibbonData$PathMin[match(ribbonData$Timestep, pathlengthRibbonData$Timestep)]
    ribbonData$PathMax          <- pathlengthRibbonData$PathMax[match(ribbonData$Timestep, pathlengthRibbonData$Timestep)]
  }

  ### PLOT assembly
  # initializations
  plot <- ggplot(plotData, aes(x = Timestep, y = Frequency, col = Measure))

  # ribbons
  if (showAdditionalRibbons) {

    # ribbon :: degree
    if (showAdditional == "degree") {
      plot <- plot +
        # average degree
        geom_ribbon(data = ribbonData,
                    aes(x = Timestep, ymin = DegMin, ymax = DegMax),
                    inherit.aes = FALSE,
                    fill = COLORS["Degree"],
                    alpha = RIBBON_ALPHA) +
        geom_ribbon(data = ribbonData,
                    aes(x = Timestep, ymin = DegIndexMin, ymax = DegIndexMax),
                    inherit.aes = FALSE,
                    fill = COLORS["Degree.index"],
                    alpha = RIBBON_ALPHA)
    }

    # ribbon :: clustering
    if (showAdditional == "clustering") {
      plot <- plot +
        # density
        geom_ribbon(data = ribbonData,
                    aes(x = Timestep, ymin = ClusMin, ymax = ClusMax),
                    inherit.aes = FALSE,
                    fill = COLORS["Clustering"],
                    alpha = RIBBON_ALPHA) +
        geom_ribbon(data = ribbonData,
                    aes(x = Timestep, ymin = ClusIndexMin, ymax = ClusIndexMax),
                    inherit.aes = FALSE,
                    fill = COLORS["Clustering.index"],
                    alpha = RIBBON_ALPHA)
    }

    # ribbon :: betweenness
    if (showAdditional == "betweenness") {
      plot <- plot +
        # density
        geom_ribbon(data = ribbonData,
                    aes(x = Timestep, ymin = BetwMin, ymax = BetwMax),
                    inherit.aes = FALSE,
                    fill = COLORS["Betweenness"],
                    alpha = RIBBON_ALPHA) +
        geom_ribbon(data = ribbonData,
                    aes(x = Timestep, ymin = BetwIndexMin, ymax = BetwIndexMax),
                    inherit.aes = FALSE,
                    fill = COLORS["Betweenness.index"],
                    alpha = RIBBON_ALPHA)
    }

    # ribbon :: closeness
    if (showAdditional == "closeness") {
      plot <- plot +
        # density
        geom_ribbon(data = ribbonData,
                    aes(x = Timestep, ymin = ClosMin, ymax = ClosMax),
                    inherit.aes = FALSE,
                    fill = COLORS["Closeness"],
                    alpha = RIBBON_ALPHA) +
        geom_ribbon(data = ribbonData,
                    aes(x = Timestep, ymin = ClosIndexMin, ymax = ClosIndexMax),
                    inherit.aes = FALSE,
                    fill = COLORS["Closeness.index"],
                    alpha = RIBBON_ALPHA)
    }

    # ribbon :: assortativity
    if (showAdditional == "assortativity") {
      plot <- plot +
        # density
        geom_ribbon(data = ribbonData,
                    aes(x = Timestep, ymin = AssoMin, ymax = AssoMax),
                    inherit.aes = FALSE,
                    fill = COLORS["Assortativity"],
                    alpha = RIBBON_ALPHA) +
        geom_ribbon(data = ribbonData,
                    aes(x = Timestep, ymin = AssoIndexMin, ymax = AssoIndexMax),
                    inherit.aes = FALSE,
                    fill = COLORS["Assortativity.index"],
                    alpha = RIBBON_ALPHA)
    }

    # ribbon :: path length
    if (showAdditional == "pathlength") {
      plot <- plot +
        # density
        geom_ribbon(data = ribbonData,
                    aes(x = Timestep, ymin = PathMin, ymax = PathMax),
                    inherit.aes = FALSE,
                    fill = COLORS["Pathlength"],
                    alpha = RIBBON_ALPHA)
    }
  }

  if (showRibbons) {
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
    if (showAdditional == "degree") {
      plot <- plot +
        scale_y_continuous(sec.axis = sec_axis(~./scaleFactor, name = "Degree"))
    } else if (showAdditional == "clustering") {
      plot <- plot +
        scale_y_continuous(sec.axis = sec_axis(~./scaleFactor, name = "Network clustering"))
    } else if (showAdditional == "betweenness") {
      plot <- plot +
        scale_y_continuous(sec.axis = sec_axis(~./scaleFactor, name = "Betweenness"))
    } else if (showAdditional == "closeness") {
      plot <- plot +
        scale_y_continuous(sec.axis = sec_axis(~./scaleFactor, name = "Closeness"))
    } else if (showAdditional == "assortativity") {
      plot <- plot +
        scale_y_continuous(sec.axis = sec_axis(~./scaleFactor, name = "Assortativity"))
    } else if (showAdditional == "pathlength") {
      plot <- plot +
        scale_y_continuous(sec.axis = sec_axis(~./scaleFactor, name = "Path length"))
    }
  }

  return(plot)
}

export_sirs <- function(data.rs = load_round_summary_prepared_data()) {

  for (ep.structure in c("dynamic", "static")) {
    ggsave(paste(EXPORT_PATH_PLOTS, ep.structure, "-sir", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
           plotSIRDevelopment(subset(data.rs, nb.ep.structure == ep.structure)),
           width = 150,
           height = 50,
           units = EXPORT_SIZE_UNITS,
           dpi = EXPORT_DPI,
           device = EXPORT_FILE_TYPE_PLOTS)

    ggsave(paste(EXPORT_PATH_PLOTS, ep.structure, "-sir-1-degree", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
           plotSIRDevelopment(subset(data.rs, nb.ep.structure == ep.structure), showAdditional = "degree"),
           width = 150,
           height = 50,
           units = EXPORT_SIZE_UNITS,
           dpi = EXPORT_DPI,
           device = EXPORT_FILE_TYPE_PLOTS)

    ggsave(paste(EXPORT_PATH_PLOTS, ep.structure, "-sir-2-clustering", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
           plotSIRDevelopment(subset(data.rs, nb.ep.structure == ep.structure), showAdditional = "clustering"),
           width = 150,
           height = 50,
           units = EXPORT_SIZE_UNITS,
           dpi = EXPORT_DPI,
           device = EXPORT_FILE_TYPE_PLOTS)

    ggsave(paste(EXPORT_PATH_PLOTS, ep.structure, "-sir-3-betweenness", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
           plotSIRDevelopment(subset(data.rs, nb.ep.structure == ep.structure), showAdditional = "betweenness"),
           width = 150,
           height = 50,
           units = EXPORT_SIZE_UNITS,
           dpi = EXPORT_DPI,
           device = EXPORT_FILE_TYPE_PLOTS)

    ggsave(paste(EXPORT_PATH_PLOTS, ep.structure, "-sir-4-closeness-", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
           plotSIRDevelopment(subset(data.rs, nb.ep.structure == ep.structure), showAdditional = "closeness"),
           width = 150,
           height = 50,
           units = EXPORT_SIZE_UNITS,
           dpi = EXPORT_DPI,
           device = EXPORT_FILE_TYPE_PLOTS)

    ggsave(paste(EXPORT_PATH_PLOTS, ep.structure, "-sir-5-assortativity", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
           plotSIRDevelopment(subset(data.rs, nb.ep.structure == ep.structure), showAdditional = "assortativity"),
           width = 150,
           height = 50,
           units = EXPORT_SIZE_UNITS,
           dpi = EXPORT_DPI,
           device = EXPORT_FILE_TYPE_PLOTS)

    ggsave(paste(EXPORT_PATH_PLOTS, ep.structure, "-sir-6-pathlength", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
           plotSIRDevelopment(subset(data.rs, nb.ep.structure == ep.structure), showAdditional = "pathlength"),
           width = 150,
           height = 50,
           units = EXPORT_SIZE_UNITS,
           dpi = EXPORT_DPI,
           device = EXPORT_FILE_TYPE_PLOTS)
  }
}



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
                  "peak.size"   = data.ss$net.static.epidemic.peak.size,
                  "color"       = rep("average.static", nrow(data.ss)))

  d.90.static <- subset(data.ss, net.static.pct.rec >= 90)
  d <- rbind(d, data.frame("structure"   = rep("static", nrow(d.90.static)),
                           "attack.rate" = rep("90-100%", nrow(d.90.static)),
                           "peak.size"   = d.90.static$net.static.epidemic.peak.size,
                           "color"       = rep("90-100%.static", nrow(d.90.static))))

  d <- rbind(d, data.frame("structure"   = rep("dynamic", nrow(data.ss)),
                           "attack.rate" = rep("average", nrow(data.ss)),
                           "peak.size"   = data.ss$net.dynamic.epidemic.peak.size,
                           "color"       = rep("average.dynamic", nrow(data.ss))))

  d.90.dynamic <- subset(data.ss, net.dynamic.pct.rec >= 90)
  d <- rbind(d, data.frame("structure"   = rep("dynamic", nrow(d.90.dynamic)),
                           "attack.rate" = rep("90-100%", nrow(d.90.dynamic)),
                           "peak.size"        = d.90.dynamic$net.dynamic.epidemic.peak.size,
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
  plots <- c(list(plot_c1_a(data.ss)), "0-01-attackrate")
  plots <- c(plots, list(plot_c1_b_duration(data.ss)), "0-02-duration")
  plots <- c(plots, list(plot_c1_b_peak(data.ss = )), "0-03-peak")
  plots <- c(plots, list(plot_c1_b_peaksize(data.ss)), "0-04-peaksize")

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

             "0-05-attackrate-duration")

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

             "0-06-attackrate-peak")

  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss$net.dynamic.pct.rec,
                                                y = data.ss$net.dynamic.epidemic.peak.size),

                              df.2 = data.frame(x = data.ss$net.static.pct.rec,
                                                y = data.ss$net.static.epidemic.peak.size),

                              name.x   = LABEL_ATTACKRATE,
                              limits.x = LIMITS_ATTACKRATE,
                              breaks.x = BREAKS_ATTACKRATE,

                              name.y   = LABEL_PEAKSIZE,
                              limits.y = LIMITS_PEAKSIZE,
                              breaks.y = BREAKS_PEAKSIZE,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),

             "0-07-attackrate-peaksize")

  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss$net.dynamic.epidemic.duration,
                                                y = data.ss$net.dynamic.epidemic.peak),

                              df.2 = data.frame(x = data.ss$net.dynamic.epidemic.duration,
                                                y = data.ss$net.static.epidemic.peak),

                              name.x   = LABEL_DURATION,
                              limits.x = LIMITS_DURATIONPEAK,
                              breaks.x = LIMITS_DURATIONPEAK,

                              name.y   = LABEL_PEAK,
                              limits.y = LIMITS_DURATIONPEAK,
                              breaks.y = BREAKS_DURATIONPEAK,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),

             "0-08-duration-peak")

  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss$net.dynamic.epidemic.duration,
                                                y = data.ss$net.dynamic.epidemic.peak.size),

                              df.2 = data.frame(x = data.ss$net.dynamic.epidemic.duration,
                                                y = data.ss$net.static.epidemic.peak.size),

                              name.x   = LABEL_DURATION,
                              limits.x = LIMITS_DURATIONPEAK,
                              breaks.x = LIMITS_DURATIONPEAK,

                              name.y   = LABEL_PEAKSIZE,
                              limits.y = LIMITS_PEAKSIZE,
                              breaks.y = BREAKS_PEAKSIZE,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),

             "0-09-duration-peaksize")

  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss$net.dynamic.epidemic.peak,
                                                y = data.ss$net.dynamic.epidemic.peak.size),

                              df.2 = data.frame(x = data.ss$net.dynamic.epidemic.peak,
                                                y = data.ss$net.static.epidemic.peak.size),

                              name.x   = LABEL_PEAK,
                              limits.x = LIMITS_DURATIONPEAK,
                              breaks.x = LIMITS_DURATIONPEAK,

                              name.y   = LABEL_PEAKSIZE,
                              limits.y = LIMITS_PEAKSIZE,
                              breaks.y = BREAKS_PEAKSIZE,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),

             "0-10-duration-peaksize")



  ### NETWORK DECISIONS / PROBABILITY OF INFECTIONS ###
  for (ep.structure in c("dynamic", "static")) {
    data.ad.by.struc <- subset(data.ad, nb.ep.structure == ep.structure)

    # risk perception
    plots <- c(plots,
               list(plot_levels(df.1 = data.frame(x = data.ad.by.struc$nb.r.sigma,
                                                  y = data.ad.by.struc$agent.cons.broken.active.epidemic),

                                df.2 = data.frame(x = data.ad.by.struc$nb.r.sigma.neighborhood,
                                                  y = data.ad.by.struc$agent.cons.out.accepted.epidemic),

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

                                df.2 = data.frame(x = data.ad.by.struc$nb.r.sigma.neighborhood,
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

    # clustering TODO: take only clustering in first round of epidemic
    plots <- c(plots,
               list(plot_levels(df.1 = data.frame(x = data.ad.by.struc$agent.clustering,
                                                  y = data.ad.by.struc$agent.cons.broken.active.epidemic),

                                df.2 = data.frame(x = data.ad.by.struc$net.clustering.pre.epidemic.av,
                                                  y = data.ad.by.struc$agent.cons.out.accepted.epidemic),

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

                                df.2 = data.frame(x = data.ad.by.struc$net.clustering.pre.epidemic.av,
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
               list(plot_levels(df.1 = data.frame(x = data.ad.by.struc$net.pathlength.pre.epidemic.av,
                                                  y = data.ad.by.struc$agent.cons.broken.active.epidemic),

                                df.2 = data.frame(x = data.ad.by.struc$net.pathlength.pre.epidemic.av,
                                                  y = data.ad.by.struc$agent.cons.out.accepted.epidemic),

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
               list(plot_levels(df.1 = data.frame(x = data.ad.by.struc$net.pathlength.pre.epidemic.av,
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
                                                  y = data.ad.by.struc$agent.cons.out.accepted.epidemic),

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
                                                  y = data.ad.by.struc$agent.cons.out.accepted.epidemic),

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
                              df.2 = NA,
                              # df.2 = data.frame(x = data.ss$index.r.sigma.neighborhood,
                              #                   y = data.ss$net.dynamic.pct.rec),

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
                              df.2 = NA,
                              # df.2 = data.frame(x = data.ss$index.r.sigma.neighborhood,
                              #                   y = data.ss$net.static.pct.rec),

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
                              df.2 = NA,
                              # df.2 = data.frame(x = data.ss$index.clustering,
                              #                   y = data.ss$net.dynamic.pct.rec),

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
                              df.2 = NA,
                              # df.2 = data.frame(x = data.ss$index.clustering,
                              #                   y = data.ss$net.static.pct.rec),

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

  ### DURATION / PEAK / PEAK SIZE ###
  data.ss.arlarge <- subset(data.ss, net.dynamic.pct.rec >= CUT_OFF_LARGE_ATTACK_RATE)
  # risk perception
  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss$nb.r.sigma.av,
                                                y = data.ss$net.dynamic.epidemic.duration),

                              df.2 = data.frame(x = data.ss.arlarge$nb.r.sigma.av,
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

                              df.2 = data.frame(x = data.ss.arlarge$nb.r.sigma.av,
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
                                                y = data.ss$net.dynamic.epidemic.peak.size),

                              df.2 = data.frame(x = data.ss.arlarge$nb.r.sigma.av,
                                                y = data.ss.arlarge$net.dynamic.epidemic.peak.size),

                              name.x   = LABEL_RISKPERCEPTION,
                              limits.x = LIMITS_RISKPERCEPTION,
                              breaks.x = BREAKS_RISKPERCEPTION,

                              name.y   = LABEL_PEAK,
                              limits.y = LIMITS_DURATIONPEAK,
                              breaks.y = BREAKS_DURATIONPEAK,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "dynamic-1-6-riskperception-peaksize")

  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss$nb.r.sigma.av,
                                                y = data.ss$net.static.epidemic.duration),

                              df.2 = data.frame(x = data.ss.arlarge$nb.r.sigma.av,
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

                              df.2 = data.frame(x = data.ss.arlarge$nb.r.sigma.av,
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

  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss$nb.r.sigma.av,
                                                y = data.ss$net.static.epidemic.peak.size),

                              df.2 = data.frame(x = data.ss.arlarge$nb.r.sigma.av,
                                                y = data.ss.arlarge$net.static.epidemic.peak.size),

                              name.x   = LABEL_RISKPERCEPTION,
                              limits.x = LIMITS_RISKPERCEPTION,
                              breaks.x = BREAKS_RISKPERCEPTION,

                              name.y   = LABEL_PEAK,
                              limits.y = LIMITS_DURATIONPEAK,
                              breaks.y = BREAKS_DURATIONPEAK,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "static-1-6-riskperception-peaksize")

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
                                                y = data.ss$net.dynamic.epidemic.peak.size),

                              df.2 = data.frame(x = data.ss.arlarge$net.clustering.pre.epidemic.av,
                                                y = data.ss.arlarge$net.dynamic.epidemic.peak.size),

                              name.x   = LABEL_CLUSTERING,
                              limits.x = LIMITS_CLUSTERING,
                              breaks.x = BREAKS_CLUSTERING,

                              name.y   = LABEL_PEAK,
                              limits.y = LIMITS_DURATIONPEAK,
                              breaks.y = BREAKS_DURATIONPEAK,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "dynamic-2-6-clustering-peaksize")

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

  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss$net.clustering.pre.epidemic.av,
                                                y = data.ss$net.static.epidemic.peak.size),

                              df.2 = data.frame(x = data.ss.arlarge$net.clustering.pre.epidemic.av,
                                                y = data.ss.arlarge$net.static.epidemic.peak.size),

                              name.x   = LABEL_CLUSTERING,
                              limits.x = LIMITS_CLUSTERING,
                              breaks.x = BREAKS_CLUSTERING,

                              name.y   = LABEL_PEAK,
                              limits.y = LIMITS_DURATIONPEAK,
                              breaks.y = BREAKS_DURATIONPEAK,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "static-2-6-clustering-peaksize")

  # path length
  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss$net.pathlength.pre.epidemic.av,
                                                y = data.ss$net.dynamic.epidemic.duration),

                              df.2 = data.frame(x = data.ss.arlarge$net.pathlength.pre.epidemic.av,
                                                y = data.ss.arlarge$net.dynamic.epidemic.duration),

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

                              df.2 = data.frame(x = data.ss.arlarge$net.pathlength.pre.epidemic.av,
                                                y = data.ss.arlarge$net.dynamic.epidemic.peak),

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
                                                y = data.ss$net.dynamic.epidemic.peak.size),

                              df.2 = data.frame(x = data.ss.arlarge$net.pathlength.pre.epidemic.av,
                                                y = data.ss.arlarge$net.dynamic.epidemic.peak.size),

                              name.x   = LABEL_PATHLENGTH,
                              limits.x = LIMITS_PATHLENGTH,
                              breaks.x = BREAKS_PATHLENGTH,

                              name.y   = LABEL_PEAK,
                              limits.y = LIMITS_DURATIONPEAK,
                              breaks.y = BREAKS_DURATIONPEAK,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "dynamic-3-6-pathlength-peaksize")

  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss$net.pathlength.pre.epidemic.av,
                                                y = data.ss$net.static.epidemic.duration),

                              df.2 = data.frame(x = data.ss.arlarge$net.pathlength.pre.epidemic.av,
                                                y = data.ss.arlarge$net.static.epidemic.duration),

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

                              df.2 = data.frame(x = data.ss.arlarge$net.pathlength.pre.epidemic.av,
                                                y = data.ss.arlarge$net.static.epidemic.peak),

                              name.x   = LABEL_PATHLENGTH,
                              limits.x = LIMITS_PATHLENGTH,
                              breaks.x = BREAKS_PATHLENGTH,

                              name.y   = LABEL_PEAK,
                              limits.y = LIMITS_DURATIONPEAK,
                              breaks.y = BREAKS_DURATIONPEAK,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "static-3-5-pathlength-peak")

  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss$net.pathlength.pre.epidemic.av,
                                                y = data.ss$net.static.epidemic.peak.size),

                              df.2 = data.frame(x = data.ss.arlarge$net.pathlength.pre.epidemic.av,
                                                y = data.ss.arlarge$net.static.epidemic.peak.size),

                              name.x   = LABEL_PATHLENGTH,
                              limits.x = LIMITS_PATHLENGTH,
                              breaks.x = BREAKS_PATHLENGTH,

                              name.y   = LABEL_PEAK,
                              limits.y = LIMITS_DURATIONPEAK,
                              breaks.y = BREAKS_DURATIONPEAK,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "static-3-6-pathlength-peaksize")

  # betweenness (normalized)
  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss$index.betweenness.normalized,
                                                y = data.ss$net.dynamic.epidemic.duration),

                              df.2 = data.frame(x = data.ss.arlarge$index.betweenness.normalized,
                                                y = data.ss.arlarge$net.dynamic.epidemic.duration),

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

                              df.2 = data.frame(x = data.ss.arlarge$index.betweenness.normalized,
                                                y = data.ss.arlarge$net.dynamic.epidemic.peak),

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
                                                y = data.ss$net.dynamic.epidemic.peak.size),

                              df.2 = data.frame(x = data.ss.arlarge$index.betweenness.normalized,
                                                y = data.ss.arlarge$net.dynamic.epidemic.peak.size),

                              name.x   = LABEL_BETWEENNESS,
                              limits.x = LIMITS_BETWEENNESS,
                              breaks.x = BREAKS_BETWEENNESS,

                              name.y   = LABEL_PEAK,
                              limits.y = LIMITS_DURATIONPEAK,
                              breaks.y = BREAKS_DURATIONPEAK,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "dynamic-4-6-betweenness-peaksize")

  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss$index.betweenness.normalized,
                                                y = data.ss$net.static.epidemic.duration),

                              df.2 = data.frame(x = data.ss.arlarge$index.betweenness.normalized,
                                                y = data.ss.arlarge$net.static.epidemic.duration),

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

                              df.2 = data.frame(x = data.ss.arlarge$index.betweenness.normalized,
                                                y = data.ss.arlarge$net.static.epidemic.peak),

                              name.x   = LABEL_BETWEENNESS,
                              limits.x = LIMITS_BETWEENNESS,
                              breaks.x = BREAKS_BETWEENNESS,

                              name.y   = LABEL_PEAK,
                              limits.y = LIMITS_DURATIONPEAK,
                              breaks.y = BREAKS_DURATIONPEAK,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "static-4-5-betweenness-peak")

  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss$index.betweenness.normalized,
                                                y = data.ss$net.static.epidemic.peak.size),

                              df.2 = data.frame(x = data.ss.arlarge$index.betweenness.normalized,
                                                y = data.ss.arlarge$net.static.epidemic.peak.size),

                              name.x   = LABEL_BETWEENNESS,
                              limits.x = LIMITS_BETWEENNESS,
                              breaks.x = BREAKS_BETWEENNESS,

                              name.y   = LABEL_PEAK,
                              limits.y = LIMITS_DURATIONPEAK,
                              breaks.y = BREAKS_DURATIONPEAK,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "static-4-6-betweenness-peaksize")

  # assortativity
  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss$net.assortativity.pre.epidemic,
                                                y = data.ss$net.dynamic.epidemic.duration),

                              df.2 = data.frame(x = data.ss.arlarge$net.assortativity.pre.epidemic,
                                                y = data.ss.arlarge$net.dynamic.epidemic.duration),

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

                              df.2 = data.frame(x = data.ss.arlarge$net.assortativity.pre.epidemic,
                                                y = data.ss.arlarge$net.dynamic.epidemic.peak),

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
                                                y = data.ss$net.dynamic.epidemic.peak.size),

                              df.2 = data.frame(x = data.ss.arlarge$net.assortativity.pre.epidemic,
                                                y = data.ss.arlarge$net.dynamic.epidemic.peak.size),

                              name.x   = LABEL_ASSORTATIVITY,
                              limits.x = LIMITS_ASSORTATIVITY,
                              breaks.x = BREAKS_ASSORTATIVITY,

                              name.y   = LABEL_PEAK,
                              limits.y = LIMITS_DURATIONPEAK,
                              breaks.y = BREAKS_DURATIONPEAK,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "dynamic-5-6-assortativity-peaksize")

  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss$net.assortativity.pre.epidemic,
                                                y = data.ss$net.static.epidemic.duration),

                              df.2 = data.frame(x = data.ss.arlarge$net.assortativity.pre.epidemic,
                                                y = data.ss.arlarge$net.static.epidemic.duration),

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

                              df.2 = data.frame(x = data.ss.arlarge$net.assortativity.pre.epidemic,
                                                y = data.ss.arlarge$net.static.epidemic.peak),

                              name.x   = LABEL_ASSORTATIVITY,
                              limits.x = LIMITS_ASSORTATIVITY,
                              breaks.x = BREAKS_ASSORTATIVITY,

                              name.y   = LABEL_PEAK,
                              limits.y = LIMITS_DURATIONPEAK,
                              breaks.y = BREAKS_DURATIONPEAK,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "static-5-5-assortativity-peak")

  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss$net.assortativity.pre.epidemic,
                                                y = data.ss$net.static.epidemic.peak.size),

                              df.2 = data.frame(x = data.ss.arlarge$net.assortativity.pre.epidemic,
                                                y = data.ss.arlarge$net.static.epidemic.peak.size),

                              name.x   = LABEL_ASSORTATIVITY,
                              limits.x = LIMITS_ASSORTATIVITY,
                              breaks.x = BREAKS_ASSORTATIVITY,

                              name.y   = LABEL_PEAK,
                              limits.y = LIMITS_DURATIONPEAK,
                              breaks.y = BREAKS_DURATIONPEAK,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "static-5-6-assortativity-peaksize")

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

export_interactions <- function(main.effects, filename.appendix, gaps) {
  out.init.ints   <- ""
  out.model.ints  <- ""
  for (m1 in seq(1, length(main.effects)-1, 1)) {
    main1 <- main.effects[m1]

    for (m2 in seq(m1+1, length(main.effects), 1)) {
      main2 <- main.effects[m2]

      int <- paste(main1, ".X.", main2, sep = "")
      out.model.ints <- paste(out.model.ints, int, " +\n", sep = "")
      for (i in seq(1: (gaps[1] - nchar(int)))) {
        int <- paste(int, " ", sep = "")
      }
      int <- paste(int, "<- ", main1, sep = "")
      for (i in seq(1: (gaps[2] - nchar(main1)))) {
        int <- paste(int, " ", sep = "")
      }
      int <- paste(int, "* ", main2, sep = "")
      out.init.ints <- paste(out.init.ints, int, "\n", sep = "")
    }
  }

  out.model.mains <- ""
  for (main in main.effects) {
    out.model.mains <- paste(out.model.mains, main, " +\n", sep = "")
  }

  out <- paste(out.init.ints, "\n\n\n\n",
               out.model.mains, "\n",
               out.model.ints, sep = "")

  dir.create(EXPORT_PATH_NUM, showWarnings = FALSE)
  cat(out, file = paste(EXPORT_PATH_NUM,
                        paste("interactions-", filename.appendix, sep = ""),
                        EXPORT_FILE_EXTENSION_DESC,
                        sep = ""))
}

#----------------------------------------------------------------------------------------------------#
# function: export_network_models
#     Creates and exports regression models for attack rate, duration, peak, and peak size.
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

  ##### DYNAMIC #####
  #### DATA PREPARATIONS ####
  ### INDEPENDENT ###
  ## MAIN EFFECTS
  net.changes                   <- prepare_predictor(data.ss$net.dynamic.ties.broken.active.epidemic +
                                                       data.ss$net.dynamic.ties.out.accepted.epidemic)
  sigma                         <- prepare_predictor(data.ss$nb.sigma)
  gamma                         <- prepare_predictor(data.ss$nb.gamma)
  r.sigma.av                    <- prepare_predictor(data.ss$nb.r.sigma.av)
  degree.av                     <- prepare_predictor(data.ss$net.degree.pre.epidemic.av)
  clustering.av                 <- prepare_predictor(data.ss$net.clustering.pre.epidemic.av)
  pathlength.av                 <- prepare_predictor(data.ss$net.pathlength.pre.epidemic.av)
  betweenness.av                <- prepare_predictor(data.ss$net.betweenness.pre.epidemic.av)
  closeness.av                  <- prepare_predictor(data.ss$net.closeness.pre.epidemic.av)
  assortativity.av              <- prepare_predictor(data.ss$net.assortativity.pre.epidemic)
  degree.index                  <- prepare_predictor(data.ss$index.degree)
  clustering.index              <- prepare_predictor(data.ss$index.clustering)
  betweenness.index             <- prepare_predictor(data.ss$index.betweenness.normalized)
  closeness.index               <- prepare_predictor(data.ss$index.closeness)
  assortativity.index           <- prepare_predictor(data.ss$index.assortativity)
  r.sigma.index                 <- prepare_predictor(data.ss$index.r.sigma)
  r.sigma.index.neighborhood    <- prepare_predictor(data.ss$index.r.sigma.neighborhood)

  ### DEPENDENT ###
  attack.rate <- data.ss$net.dynamic.pct.rec / 100
  duration    <- data.ss$net.dynamic.epidemic.duration
  peak        <- data.ss$net.dynamic.epidemic.peak
  peak.size   <- data.ss$net.dynamic.epidemic.peak.size

  ## MAIN EFFECTS (NETWORK DYNAMICS)
  # attack rate
  model.1.attackrate.dynamic <- glm(attack.rate ~
                                      net.changes,
                                    family = binomial)
  # summary(model.1.attackrate.dynamic)
  # print_r2(model.1.attackrate.dynamic)
  # duration
  model.1.duration.dynamic <- lm(duration ~
                                   net.changes)
  # summary(model.1.duration.dynamic)
  # print_r2(model.1.duration.dynamic)
  # peak
  model.1.peak.dynamic <- lm(peak ~
                               net.changes)
  # summary(model.1.peak.dynamic)
  # print_r2(model.1.peak.dynamic)
  # peak size
  model.1.peak.size.dynamic <- lm(peak.size ~
                                    net.changes)
  # summary(model.1.peak.size.dynamic)
  # print_r2(model.1.peak.size).dynamic

  ## MAIN EFFECTS (ACTUAL NETWORK PROPERTIES RATHER THAN PARAMETERS TO CONTROL NETWORK PROPERTIES)
  # attack rate
  model.2.attackrate.dynamic <- glm(attack.rate ~
                                      net.changes +
                                      sigma +
                                      gamma +
                                      r.sigma.av +
                                      degree.av +
                                      clustering.av +
                                      pathlength.av +
                                      betweenness.av +
                                      closeness.av +
                                      assortativity.av +
                                      degree.index +
                                      clustering.index +
                                      betweenness.index +
                                      closeness.index +
                                      assortativity.index +
                                      r.sigma.index +
                                      r.sigma.index.neighborhood,
                                    family = binomial)
  # summary(model.2.attackrate.dynamic)
  # vif(model.2.attackrate.dynamic)
  # print_r2(model.2.attackrate.dynamic)
  # duration
  model.2.duration.dynamic <- lm(duration ~
                                   net.changes +
                                   sigma +
                                   gamma +
                                   r.sigma.av +
                                   degree.av +
                                   clustering.av +
                                   pathlength.av +
                                   betweenness.av +
                                   closeness.av +
                                   assortativity.av +
                                   degree.index +
                                   clustering.index +
                                   betweenness.index +
                                   closeness.index +
                                   assortativity.index +
                                   r.sigma.index +
                                   r.sigma.index.neighborhood)
  # summary(model.2.duration.dynamic)
  # vif(model.2.duration.dynamic)
  # print_r2(model.2.duration.dynamic)
  # peak
  model.2.peak.dynamic <- lm(peak ~
                               net.changes +
                               sigma +
                               gamma +
                               r.sigma.av +
                               degree.av +
                               clustering.av +
                               pathlength.av +
                               betweenness.av +
                               closeness.av +
                               assortativity.av +
                               degree.index +
                               clustering.index +
                               betweenness.index +
                               closeness.index +
                               assortativity.index +
                               r.sigma.index +
                               r.sigma.index.neighborhood)
  # summary(model.2.peak.dynamic)
  # vif(model.2.peak.dynamic)
  # print_r2(model.2.peak.dynamic)
  # peak size
  model.2.peak.size.dynamic <- lm(peak.size ~
                                    net.changes +
                                    sigma +
                                    gamma +
                                    r.sigma.av +
                                    degree.av +
                                    clustering.av +
                                    pathlength.av +
                                    betweenness.av +
                                    closeness.av +
                                    assortativity.av +
                                    degree.index +
                                    clustering.index +
                                    betweenness.index +
                                    closeness.index +
                                    assortativity.index +
                                    r.sigma.index +
                                    r.sigma.index.neighborhood)
  # summary(model.2.peak.size.dynamic)
  # vif(model.2.peak.size.dynamic)
  # print_r2(model.2.peak.size.dynamic)

  ## INTERACTION EFFECTS (export significant main effects)
  # export_interactions(c("net.changes",
  #                       "sigma",
  #                       "gamma",
  #                       "r.sigma.av",
  #                       "degree.av",
  #                       "clustering.av",
  #                       "pathlength.av",
  #                       "betweenness.av",
  #                       "closeness.av",
  #                       "assortativity.av",
  #                       "degree.index",
  #                       "clustering.index",
  #                       "betweenness.index",
  #                       "closeness.index",
  #                       "assortativity.index",
  #                       "r.sigma.index",
  #                       "r.sigma.index.neighborhood"),
  #                     "network", c(50, 30))

  model.3.attackrate.dynamic <- glm(attack.rate ~
                                      net.changes +
                                      sigma +
                                      gamma +
                                      r.sigma.av +
                                      degree.av +
                                      clustering.av +
                                      pathlength.av +
                                      betweenness.av +
                                      closeness.av +
                                      assortativity.av +
                                      degree.index +
                                      clustering.index +
                                      betweenness.index +
                                      closeness.index +
                                      assortativity.index +
                                      r.sigma.index +
                                      r.sigma.index.neighborhood

                                      # interactions
                                    ,
                                    family = binomial)
  # summary(model.3.attackrate.dynamic)
  # vif(model.3.attackrate.dynamic)
  # print_r2(model.3.attackrate.dynamic)

  model.3.duration.dynamic <- lm(duration ~
                                   net.changes +
                                   sigma +
                                   gamma +
                                   r.sigma.av +
                                   degree.av +
                                   clustering.av +
                                   pathlength.av +
                                   betweenness.av +
                                   closeness.av +
                                   assortativity.av +
                                   degree.index +
                                   clustering.index +
                                   betweenness.index +
                                   closeness.index +
                                   assortativity.index +
                                   r.sigma.index +
                                   r.sigma.index.neighborhood

                                   # interactions
  )
  # summary(model.3.duration.dynamic)
  # vif(model.3.duration.dynamic)
  # print_r2(model.3.duration.dynamic)

  model.3.peak.dynamic <- lm(peak ~
                               net.changes +
                               sigma +
                               gamma +
                               r.sigma.av +
                               degree.av +
                               clustering.av +
                               pathlength.av +
                               betweenness.av +
                               closeness.av +
                               assortativity.av +
                               degree.index +
                               clustering.index +
                               betweenness.index +
                               closeness.index +
                               assortativity.index +
                               r.sigma.index +
                               r.sigma.index.neighborhood

                               # interactions
  )
  # summary(model.3.peak.dynamic)
  # vif(model.3.peak.dynamic)
  # print_r2(model.3.peak).dynamic

  model.3.peak.size.dynamic <- lm(peak.size ~
                                    net.changes +
                                    sigma +
                                    gamma +
                                    r.sigma.av +
                                    degree.av +
                                    clustering.av +
                                    pathlength.av +
                                    betweenness.av +
                                    closeness.av +
                                    assortativity.av +
                                    degree.index +
                                    clustering.index +
                                    betweenness.index +
                                    closeness.index +
                                    assortativity.index +
                                    r.sigma.index +
                                    r.sigma.index.neighborhood

                                    # interactions
  )
  # summary(model.3.peak.size.dynamic)
  # vif(model.3.peak.size.dynamic)
  # print_r2(model.3.peak.size.dynamic)


  ### FILE EXPORT ###
  filename <- "dynamic-reg-02-attackrate"
  if (filenamname.appendix != "") {
    filename <- paste(filename, "-", filenamname.appendix, sep = "")
  }
  exportModels(list(model.1.attackrate.dynamic,
                    model.2.attackrate.dynamic,
                    model.3.attackrate.dynamic), filename)
  filename <- "dynamic-reg-03-duration"
  if (filenamname.appendix != "") {
    filename <- paste(filename, "-", filenamname.appendix, sep = "")
  }
  exportModels(list(model.1.duration.dynamic,
                    model.2.duration.dynamic,
                    model.3.duration.dynamic), filename)
  filename <- "dynamic-reg-04-peak"
  if (filenamname.appendix != "") {
    filename <- paste(filename, "-", filenamname.appendix, sep = "")
  }
  exportModels(list(model.1.peak.dynamic,
                    model.2.peak.dynamic,
                    model.3.peak.dynamic), filename)
  filename <- "dynamic-reg-05-peaksize"
  if (filenamname.appendix != "") {
    filename <- paste(filename, "-", filenamname.appendix, sep = "")
  }
  exportModels(list(model.1.peak.size.dynamic,
                    model.2.peak.size.dynamic,
                    model.3.peak.size.dynamic), filename)



  ##### STATIC #####
  #### DATA PREPARATIONS ####
  ### INDEPENDENT ###
  ## MAIN EFFECTS
  net.changes                   <- prepare_predictor(data.ss$net.static.ties.broken.active.epidemic +
                                                       data.ss$net.static.ties.out.accepted.epidemic)
  net.changes[is.na(net.changes)] <- 0

  ### DEPENDENT ###
  attack.rate <- data.ss$net.static.pct.rec / 100
  duration    <- data.ss$net.static.epidemic.duration
  peak        <- data.ss$net.static.epidemic.peak
  peak.size   <- data.ss$net.static.epidemic.peak.size

  ## MAIN EFFECTS (NETWORK DYNAMICS)
  # attack rate
  model.1.attackrate.static <- glm(attack.rate ~
                                      net.changes,
                                    family = binomial)
  # summary(model.1.attackrate.static)
  # print_r2(model.1.attackrate.static)
  # duration
  model.1.duration.static <- lm(duration ~
                                   net.changes)
  # summary(model.1.duration.static)
  # print_r2(model.1.duration.static)
  # peak
  model.1.peak.static <- lm(peak ~
                               net.changes)
  # summary(model.1.peak.static)
  # print_r2(model.1.peak.static)
  # peak size
  model.1.peak.size.static <- lm(peak.size ~
                                    net.changes)
  # summary(model.1.peak.size.static)
  # print_r2(model.1.peak.size).static

  ## MAIN EFFECTS (ACTUAL NETWORK PROPERTIES RATHER THAN PARAMETERS TO CONTROL NETWORK PROPERTIES)
  # attack rate
  model.2.attackrate.static <- glm(attack.rate ~
                                     net.changes +
                                     sigma +
                                     gamma +
                                     r.sigma.av +
                                     degree.av +
                                     clustering.av +
                                     pathlength.av +
                                     betweenness.av +
                                     closeness.av +
                                     assortativity.av +
                                     degree.index +
                                     clustering.index +
                                     betweenness.index +
                                     closeness.index +
                                     assortativity.index +
                                     r.sigma.index +
                                     r.sigma.index.neighborhood,
                                    family = binomial)
  # summary(model.2.attackrate.static)
  # vif(model.2.attackrate.static)
  # print_r2(model.2.attackrate.static)
  # duration
  model.2.duration.static <- lm(duration ~
                                  net.changes +
                                  sigma +
                                  gamma +
                                  r.sigma.av +
                                  degree.av +
                                  clustering.av +
                                  pathlength.av +
                                  betweenness.av +
                                  closeness.av +
                                  assortativity.av +
                                  degree.index +
                                  clustering.index +
                                  betweenness.index +
                                  closeness.index +
                                  assortativity.index +
                                  r.sigma.index +
                                  r.sigma.index.neighborhood)
  # summary(model.2.duration.static)
  # vif(model.2.duration.static)
  # print_r2(model.2.duration.static)
  # peak
  model.2.peak.static <- lm(peak ~
                              net.changes +
                              sigma +
                              gamma +
                              r.sigma.av +
                              degree.av +
                              clustering.av +
                              pathlength.av +
                              betweenness.av +
                              closeness.av +
                              assortativity.av +
                              degree.index +
                              clustering.index +
                              betweenness.index +
                              closeness.index +
                              assortativity.index +
                              r.sigma.index +
                              r.sigma.index.neighborhood)
  # summary(model.2.peak.static)
  # vif(model.2.peak.static)
  # print_r2(model.2.peak.static)
  # peak size
  model.2.peak.size.static <- lm(peak.size ~
                                   net.changes +
                                   sigma +
                                   gamma +
                                   r.sigma.av +
                                   degree.av +
                                   clustering.av +
                                   pathlength.av +
                                   betweenness.av +
                                   closeness.av +
                                   assortativity.av +
                                   degree.index +
                                   clustering.index +
                                   betweenness.index +
                                   closeness.index +
                                   assortativity.index +
                                   r.sigma.index +
                                   r.sigma.index.neighborhood)
  # summary(model.2.peak.size.static)
  # vif(model.2.peak.size.static)
  # print_r2(model.2.peak.size.static)

  ## INTERACTION EFFECTS (export significant main effects)
  # export_interactions(c("net.changes",
  #                       "sigma",
  #                       "gamma",
  #                       "r.sigma.av",
  #                       "degree.av",
  #                       "clustering.av",
  #                       "pathlength.av",
  #                       "betweenness.av",
  #                       "closeness.av",
  #                       "assortativity.av",
  #                       "degree.index",
  #                       "clustering.index",
  #                       "betweenness.index",
  #                       "closeness.index",
  #                       "assortativity.index",
  #                       "r.sigma.index",
  #                       "r.sigma.index.neighborhood"),
  #                     "network", c(50, 30))

  model.3.attackrate.static <- glm(attack.rate ~
                                     net.changes +
                                     sigma +
                                     gamma +
                                     r.sigma.av +
                                     degree.av +
                                     clustering.av +
                                     pathlength.av +
                                     betweenness.av +
                                     closeness.av +
                                     assortativity.av +
                                     degree.index +
                                     clustering.index +
                                     betweenness.index +
                                     closeness.index +
                                     assortativity.index +
                                     r.sigma.index +
                                     r.sigma.index.neighborhood

                                     # interactions
                                    ,
                                    family = binomial)
  # summary(model.3.attackrate.static)
  # vif(model.3.attackrate.static)
  # print_r2(model.3.attackrate.static)

  model.3.duration.static <- lm(duration ~
                                  net.changes +
                                  sigma +
                                  gamma +
                                  r.sigma.av +
                                  degree.av +
                                  clustering.av +
                                  pathlength.av +
                                  betweenness.av +
                                  closeness.av +
                                  assortativity.av +
                                  degree.index +
                                  clustering.index +
                                  betweenness.index +
                                  closeness.index +
                                  assortativity.index +
                                  r.sigma.index +
                                  r.sigma.index.neighborhood

                                  # interactions
  )
  # summary(model.3.duration.static)
  # vif(model.3.duration.static)
  # print_r2(model.3.duration.static)

  model.3.peak.static <- lm(peak ~
                              net.changes +
                              sigma +
                              gamma +
                              r.sigma.av +
                              degree.av +
                              clustering.av +
                              pathlength.av +
                              betweenness.av +
                              closeness.av +
                              assortativity.av +
                              degree.index +
                              clustering.index +
                              betweenness.index +
                              closeness.index +
                              assortativity.index +
                              r.sigma.index +
                              r.sigma.index.neighborhood

                              # interactions
  )
  # summary(model.3.peak.static)
  # vif(model.3.peak.static)
  # print_r2(model.3.peak).static

  model.3.peak.size.static <- lm(peak.size ~
                                   net.changes +
                                   sigma +
                                   gamma +
                                   r.sigma.av +
                                   degree.av +
                                   clustering.av +
                                   pathlength.av +
                                   betweenness.av +
                                   closeness.av +
                                   assortativity.av +
                                   degree.index +
                                   clustering.index +
                                   betweenness.index +
                                   closeness.index +
                                   assortativity.index +
                                   r.sigma.index +
                                   r.sigma.index.neighborhood

                                   # interactions
  )
  # summary(model.3.peak.size.static)
  # vif(model.3.peak.size.static)
  # print_r2(model.3.peak.size.static)


  ### FILE EXPORT ###
  filename <- "static-reg-02-attackrate"
  if (filenamname.appendix != "") {
    filename <- paste(filename, "-", filenamname.appendix, sep = "")
  }
  exportModels(list(model.1.attackrate.static,
                    model.2.attackrate.static,
                    model.3.attackrate.static), filename)
  filename <- "static-reg-03-duration"
  if (filenamname.appendix != "") {
    filename <- paste(filename, "-", filenamname.appendix, sep = "")
  }
  exportModels(list(model.1.duration.static,
                    model.2.duration.static,
                    model.3.duration.static), filename)
  filename <- "static-reg-04-peak"
  if (filenamname.appendix != "") {
    filename <- paste(filename, "-", filenamname.appendix, sep = "")
  }
  exportModels(list(model.1.peak.static,
                    model.2.peak.static,
                    model.3.peak.static), filename)
  filename <- "static-reg-05-peaksize"
  if (filenamname.appendix != "") {
    filename <- paste(filename, "-", filenamname.appendix, sep = "")
  }
  exportModels(list(model.1.peak.size.static,
                    model.2.peak.size.static,
                    model.3.peak.size.static), filename)
}

#----------------------------------------------------------------------------------------------------#
# function: export_agent_models
#     Creates and exports regression models probability of infections.
# param:  data.ad
#     agent details data to produce regression models for
# param:  filenamname.appendix
#     Optional string to append to the standard filename
#----------------------------------------------------------------------------------------------------#
export_agent_models <- function(data.ad = load_agent_details_prepared_data(), filenamname.appendix = "") {

  # ## DATA INTEGRITY CHECKS
  # # quick overview of data
  # head(data.ad)
  # # check whether columns have correct data (types)
  # str(data.ad)
  # # check whether NA values occur in relevant outcome and predictor variable(s)
  # data.ad[
  #   is.na(data.ad$agent.infected) |
  #
  #     is.na(data.ad$agent.cons.broken.active.epidemic) |
  #     is.na(data.ad$agent.cons.out.accepted.epidemic) |
  #     is.na(data.ad$agent.cons.in.accepted.epidemic) |
  #
  #     is.na(data.ad$nb.r.sigma) |
  #     is.na(data.ad$nb.sigma) |
  #     is.na(data.ad$nb.gamma) |
  #     is.na(data.ad$agent.degree) |
  #
  #     is.na(data.ad$agent.clustering) |
  #     is.na(data.ad$net.pathlength.pre.epidemic.av) |
  #     is.na(data.ad$agent.betweenness.normalized) |
  #     is.na(data.ad$net.assortativity), ]                           # none - otherwise: remove
  #
  # # in case of categorical or boolean predictors, use xtab to check whether there are enough date for each factor, for example:
  # xtabs(~ net.dynamic.pct.rec + net.stable.pre, data = data.ad)

  data.ad <- subset(data.ad, agent.force.infected == 0)

  ###### DYNAMIC NETWORKS ######
  data.ad.dynamic <- subset(data.ad, nb.ep.structure == "dynamic")

  #### DATA PREPARATIONS ####
  ### DEPENDENT ###
  prob.infection                <- data.ad.dynamic$agent.infected
  ### INDEPENDENT ###
  ## MAIN EFFECTS
  net.changes                   <- prepare_predictor(data.ad.dynamic$agent.cons.broken.active.epidemic +
                                                       data.ad.dynamic$agent.cons.out.accepted.epidemic)
  sigma                         <- prepare_predictor(data.ad.dynamic$nb.sigma)
  gamma                         <- prepare_predictor(data.ad.dynamic$nb.gamma)
  degree.av                     <- prepare_predictor(data.ad.dynamic$net.degree.pre.epidemic.av)
  clustering.av                 <- prepare_predictor(data.ad.dynamic$net.clustering.pre.epidemic.av)
  pathlength.av                 <- prepare_predictor(data.ad.dynamic$net.pathlength.pre.epidemic.av)
  betweenness.av                <- prepare_predictor(data.ad.dynamic$net.betweenness.pre.epidemic.av)
  closeness.av                  <- prepare_predictor(data.ad.dynamic$net.closeness.pre.epidemic.av)
  assortativity.av              <- prepare_predictor(data.ad.dynamic$net.assortativity.pre.epidemic)
  r.sigma.av                    <- prepare_predictor(data.ad.dynamic$nb.r.sigma.av)
  degree.index                  <- prepare_predictor(data.ad.dynamic$index.degree)
  clustering.index              <- prepare_predictor(data.ad.dynamic$index.clustering)
  betweenness.index             <- prepare_predictor(data.ad.dynamic$index.betweenness.normalized)
  closeness.index               <- prepare_predictor(data.ad.dynamic$index.closeness)
  assortativity.index           <- prepare_predictor(data.ad.dynamic$index.assortativity)
  r.sigma.index                 <- prepare_predictor(data.ad.dynamic$index.r.sigma)
  r.sigma.neighborhood.index    <- prepare_predictor(data.ad.dynamic$index.r.sigma.neighborhood)
  degree.agent                  <- prepare_predictor(data.ad.dynamic$agent.degree)
  clustering.agent              <- prepare_predictor(data.ad.dynamic$agent.clustering)
  betweenness.agent             <- prepare_predictor(data.ad.dynamic$agent.betweenness.normalized)
  closeness.agent               <- prepare_predictor(data.ad.dynamic$agent.closeness)
  assortativity.agent           <- prepare_predictor(data.ad.dynamic$agent.assortativity)
  index.distance.agent          <- prepare_predictor(data.ad.dynamic$agent.index.distance)
  r.sigma.agent                 <- prepare_predictor(data.ad.dynamic$nb.r.sigma)
  r.sigma.agent.neighborhood    <- prepare_predictor(data.ad.dynamic$nb.r.sigma.neighborhood)

  # dynamics
  log.1.dynamic <- glm(prob.infection ~
                         net.changes,
                       family = binomial)
  # summary(log.1.dynamic)
  # print_r2(log.1.dynamic)

  # main effects
  log.2.dynamic <- glm(prob.infection ~
                         net.changes +
                         sigma +
                         gamma +
                         degree.av +
                         clustering.av +
                         pathlength.av +
                         betweenness.av +
                         closeness.av +
                         assortativity.av +
                         r.sigma.av +
                         degree.index +
                         clustering.index +
                         betweenness.index +
                         closeness.index +
                         assortativity.index +
                         r.sigma.index +
                         r.sigma.neighborhood.index +
                         degree.agent +
                         clustering.agent +
                         betweenness.agent +
                         closeness.agent +
                         assortativity.agent +
                         index.distance.agent +
                         r.sigma.agent +
                         r.sigma.agent.neighborhood,
                       family = binomial)
  # summary(log.2.dynamic)
  # vif(log.2.dynamic)
  # print_r2(log.2.dynamic)

  ## INTERACTION EFFECTS (export significant main effects)
  # export_interactions(c("net.changes",
  #                       "sigma",
  #                       "gamma",
  #                       "degree.av",
  #                       "clustering.av",
  #                       "pathlength.av",
  #                       "betweenness.av",
  #                       "closeness.av",
  #                       "assortativity.av",
  #                       "r.sigma.av",
  #                       "degree.index",
  #                       "clustering.index",
  #                       "betweenness.index",
  #                       "closeness.index",
  #                       "assortativity.index",
  #                       "r.sigma.index",
  #                       "r.sigma.neighborhood.index",
  #                       "degree.agent",
  #                       "clustering.agent",
  #                       "betweenness.agent",
  #                       "closeness.agent",
  #                       "assortativity.agent",
  #                       "index.distance.agent",
  #                       "r.sigma.agent",
  #                       "r.sigma.agent.neighborhood"),
  #                     "agent", c(30, 12))
  log.3.dynamic <- glm(prob.infection ~
                         net.changes +
                         sigma +
                         gamma +
                         degree.av +
                         clustering.av +
                         pathlength.av +
                         betweenness.av +
                         closeness.av +
                         assortativity.av +
                         r.sigma.av +
                         degree.index +
                         clustering.index +
                         betweenness.index +
                         closeness.index +
                         assortativity.index +
                         r.sigma.index +
                         r.sigma.neighborhood.index +
                         degree.agent +
                         clustering.agent +
                         betweenness.agent +
                         closeness.agent +
                         assortativity.agent +
                         index.distance.agent +
                         r.sigma.agent +
                         r.sigma.agent.neighborhood

                         # interactions
                       ,
                       family = binomial)
  # print_r2(log.3.dynamic)
  # summary(log.3.dynamic)
  # vif(log.3.dynamic)

  ### FILE EXPORT ###
  filename <- "dynamic-reg-01-probinfections"
  if (filenamname.appendix != "") {
    filename <- paste(filename, "-", filenamname.appendix, sep = "")
  }
  exportModels(list(log.1.dynamic,
                    log.2.dynamic,
                    log.3.dynamic), filename)


  ###### STATIC NETWORKS ######
  data.ad.static <- subset(data.ad, nb.ep.structure == "static")

  #### DATA PREPARATIONS ####
  ### DEPENDENT ###
  prob.infection                <- data.ad.static$agent.infected
  ### INDEPENDENT ###
  ## MAIN EFFECTS
  net.changes                   <- prepare_predictor(data.ad.static$agent.cons.broken.active.epidemic +
                                                       data.ad.static$agent.cons.out.accepted.epidemic)
  net.changes[is.na(net.changes)] <- 0
  sigma                         <- prepare_predictor(data.ad.static$nb.sigma)
  gamma                         <- prepare_predictor(data.ad.static$nb.gamma)
  degree.av                     <- prepare_predictor(data.ad.static$net.degree.pre.epidemic.av)
  clustering.av                 <- prepare_predictor(data.ad.static$net.clustering.pre.epidemic.av)
  pathlength.av                 <- prepare_predictor(data.ad.static$net.pathlength.pre.epidemic.av)
  betweenness.av                <- prepare_predictor(data.ad.static$net.betweenness.pre.epidemic.av)
  closeness.av                  <- prepare_predictor(data.ad.static$net.closeness.pre.epidemic.av)
  assortativity.av              <- prepare_predictor(data.ad.static$net.assortativity.pre.epidemic)
  r.sigma.av                    <- prepare_predictor(data.ad.static$nb.r.sigma.av)
  degree.index                  <- prepare_predictor(data.ad.static$index.degree)
  clustering.index              <- prepare_predictor(data.ad.static$index.clustering)
  betweenness.index             <- prepare_predictor(data.ad.static$index.betweenness.normalized)
  closeness.index               <- prepare_predictor(data.ad.static$index.closeness)
  assortativity.index           <- prepare_predictor(data.ad.static$index.assortativity)
  r.sigma.index                 <- prepare_predictor(data.ad.static$index.r.sigma)
  r.sigma.neighborhood.index    <- prepare_predictor(data.ad.static$index.r.sigma.neighborhood)
  degree.agent                  <- prepare_predictor(data.ad.static$agent.degree)
  clustering.agent              <- prepare_predictor(data.ad.static$agent.clustering)
  betweenness.agent             <- prepare_predictor(data.ad.static$agent.betweenness.normalized)
  closeness.agent               <- prepare_predictor(data.ad.static$agent.closeness)
  assortativity.agent           <- prepare_predictor(data.ad.static$agent.assortativity)
  index.distance.agent          <- prepare_predictor(data.ad.static$agent.index.distance)
  r.sigma.agent                 <- prepare_predictor(data.ad.static$nb.r.sigma)
  r.sigma.agent.neighborhood    <- prepare_predictor(data.ad.static$nb.r.sigma.neighborhood)

  # dynamics
  log.1.static <- glm(prob.infection ~
                         net.changes,
                       family = binomial)
  # summary(log.1.static)
  # print_r2(log.1.static)

  # main effects
  log.2.static <- glm(prob.infection ~
                         net.changes +
                         sigma +
                         gamma +
                         degree.av +
                         clustering.av +
                         pathlength.av +
                         betweenness.av +
                         closeness.av +
                         assortativity.av +
                         r.sigma.av +
                         degree.index +
                         clustering.index +
                         betweenness.index +
                         closeness.index +
                         assortativity.index +
                         r.sigma.index +
                         r.sigma.neighborhood.index +
                         degree.agent +
                         clustering.agent +
                         betweenness.agent +
                         closeness.agent +
                         assortativity.agent +
                         index.distance.agent +
                         r.sigma.agent +
                         r.sigma.agent.neighborhood,
                       family = binomial)
  # summary(log.2.static)
  # vif(log.2.static)
  # print_r2(log.2.static)

  ## INTERACTION EFFECTS (export significant main effects)
  # export_interactions(c("net.changes",
  #                       "sigma",
  #                       "gamma",
  #                       "degree.av",
  #                       "clustering.av",
  #                       "pathlength.av",
  #                       "betweenness.av",
  #                       "closeness.av",
  #                       "assortativity.av",
  #                       "r.sigma.av",
  #                       "degree.index",
  #                       "clustering.index",
  #                       "betweenness.index",
  #                       "closeness.index",
  #                       "assortativity.index",
  #                       "r.sigma.index",
  #                       "r.sigma.neighborhood.index",
  #                       "degree.agent",
  #                       "clustering.agent",
  #                       "betweenness.agent",
  #                       "closeness.agent",
  #                       "assortativity.agent",
  #                       "index.distance.agent",
  #                       "r.sigma.agent",
  #                       "r.sigma.agent.neighborhood"),
  #                     "agent", c(50, 30))
  log.3.static <- glm(prob.infection ~
                         net.changes +
                         sigma +
                         gamma +
                         degree.av +
                         clustering.av +
                         pathlength.av +
                         betweenness.av +
                         closeness.av +
                         assortativity.av +
                         r.sigma.av +
                         degree.index +
                         clustering.index +
                         betweenness.index +
                         closeness.index +
                         assortativity.index +
                         r.sigma.index +
                         r.sigma.neighborhood.index +
                         degree.agent +
                         clustering.agent +
                         betweenness.agent +
                         closeness.agent +
                         assortativity.agent +
                         index.distance.agent +
                         r.sigma.agent +
                         r.sigma.agent.neighborhood

                       # interactions
                       ,
                       family = binomial)
  # print_r2(log.3.static)
  # summary(log.3.static)
  # vif(log.3.static)

  ### FILE EXPORT ###
  filename <- "static-reg-01-probinfections"
  if (filenamname.appendix != "") {
    filename <- paste(filename, "-", filenamname.appendix, sep = "")
  }
  exportModels(list(log.1.static,
                    log.2.static,
                    log.3.static), filename)

}

export_all <- function() {

  data.ss <- load_simulation_summary_data()
  data.ad <- load_agent_details_prepared_data()

  export_descriptives(data.ss = data.ss, data.ad = data.ad)
  export_correlations(data.ss = data.ss)
  export_plots(data.ss = data.ss, data.ad = data.ad)
  export_network_models(data.ss = data.ss)
  export_agent_models(data.ad = data.ad)

  data.rs <- load_round_summary_prepared_data()
  export_sirs(data.rs = data.rs)
}
