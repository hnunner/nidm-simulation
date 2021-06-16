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
    if(lib %in% rownames(installed.packages()) == FALSE) {
      install.packages(lib)
    }
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
              "car",          # VIFs
              "Rmisc"         # CI function
              # "scales",
              # "ggtern"       # ternary diagrams
              # "gridExtra",   # side-by-side plots
              # "psych",       # summary statistics
))


########################################### GLOBAL CONSTANTS ##########################################
### DATA ###
CUT_OFF_LARGE_ATTACK_RATE     <- 90
CUT_OFF_SMALL_ATTACK_RATE     <- 10

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
CSV_ROUND_SUMMARY_PATH          <- paste(DATA_PATH, "round-summary.csv", sep = "")
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
POINT_SIZE                      <- 6
POINT_ALPHA                     <- 0.6
LINE_SIZE                       <- 3
LINE_ALPHA                      <- 0.6
COLOR_0                         <- "#000000"
LINE_0                          <- "solid"
SHAPE_0                         <- 16
COLOR_1                         <- "#D55E00"      # colors (http://mkweb.bcgsc.ca/colorblind/)
LINE_1                          <- "solid"
SHAPE_1                         <- 16
COLOR_2                         <- "#0072B2"      # colors (http://mkweb.bcgsc.ca/colorblind/)
LINE_2                          <- "dashed"
SHAPE_2                         <- 17
COLOR_3                         <- "#009E73"      # colors (http://mkweb.bcgsc.ca/colorblind/)
LINE_3                          <- "twodash"
SHAPE_3                         <- 18
SHOW_LEGEND                     <- FALSE
LIMITS_0_1                      <- c(0, 1)
SEQ_0_1                         <- seq(0, 1, 0.25)
LIMITS_0_2                      <- c(0, 2)
SEQ_0_2                         <- seq(0, 2, 0.5)
LIMITS_0_100                    <- c(0, 100)
SEQ_0_100                       <- seq(0, 100, 25)
# risk perception
LABEL_RISKPERCEPTION            <- "Risk perception"
LIMITS_RISKPERCEPTION           <- c(0.4, 1.6)
BREAKS_RISKPERCEPTION           <- seq(0.5, 1.5, 0.5)
# degree
LABEL_DEGREE                    <- "Av. degree"
LIMITS_DEGREE                   <- c(6.75, 8.1)
BREAKS_DEGREE                   <- seq(7.0, 8.0, 0.5)
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
LIMITS_PATHLENGTH               <- c(2, 5)
BREAKS_PATHLENGTH               <- seq(2, 5, 1)
# betweenness (normalized)
LABEL_BETWEENNESS               <- "Betweenness (normalized)"
LIMITS_BETWEENNESS              <- c(0, 0.3)
BREAKS_BETWEENNESS              <- seq(0, 0.3, 0.1)
# assortativity
LABEL_ASSORTATIVITY             <- "Assortativity"
LIMITS_ASSORTATIVITY            <- c(-0.25, 1.0)
BREAKS_ASSORTATIVITY            <- seq(-0.25, 1.0, 0.25)
# attack rate
LABEL_ATTACKRATE                <- "Attack rate"
LIMITS_ATTACKRATE               <- LIMITS_0_100
BREAKS_ATTACKRATE               <- SEQ_0_100
# duration/peak
LABEL_DURATION                  <- "Duration"
LABEL_PEAK                      <- "Peak"
LIMITS_DURATIONPEAK             <- c(0, 40)
BREAKS_DURATIONPEAK             <- seq(0, 40, 10)
LABEL_PEAKSIZE                  <- "Peak size"
LIMITS_PEAKSIZE                 <- c(0, 80)
BREAKS_PEAKSIZE                 <- seq(0, 80, 20)
# network decisions
LABEL_NETDECISIONS              <- "Network decisions"
LIMITS_NETDECISIONS             <- c(0, 5)
BREAKS_NETDECISIONS             <- seq(0, 5, 1)
LIMITS_NETDECISIONS_NETWORK     <- c(0, 1500)
BREAKS_NETDECISIONS_NETWORK     <- seq(0, 1500, 500)
LIMITS_NETDECISIONS_NETWORK_Y   <- c(0, 400)
BREAKS_NETDECISIONS_NETWORK_Y   <- seq(0, 400, 100)
# sigma
LABEL_SIGMA                     <- "Sigma"
LIMITS_SIGMA                    <- c(0, 100)
BREAKS_SIGMA                    <- seq(0, 100, 25)
# gamma
LABEL_GAMMA                     <- "Gamma"
LIMITS_GAMMA                    <- c(0, 0.2)
BREAKS_GAMMA                    <- seq(0, 0.2, 0.05)
# export image settings
EXPORT_PLOT_WIDTH_LONG          <- 250
EXPORT_PLOT_WIDTH               <- 238
EXPORT_PLOT_HEIGHT              <- 175
EXPORT_SIZE_UNITS               <- "mm"
EXPORT_DPI                      <- 600
THEME_BASE_SIZE                 <- 44


##################################### IMPORTS / DATA PREPARATIONS ####################################
#----------------------------------------------------------------------------------------------------#
# function: loadCSV
#     Loads data from a CSV file.
# param:  filePath
#     path to the file to be loaded
# return: the CSV file data as data frame
#----------------------------------------------------------------------------------------------------#
load_csv <- function(filePath) {
  csv <- read.csv(file=filePath, header=TRUE, sep=";")
  return(csv)
}

#----------------------------------------------------------------------------------------------------#
# function: load_simulation_summary_data
#     Loads summary data for NIDM simulations.
# return: the summary data for NIDM simulations
#----------------------------------------------------------------------------------------------------#
load_simulation_summary_data <- function() {
  return(load_csv(CSV_SUMMARY_PATH))
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
# function: load_round_summary_data
#     Loads round summary data.
# return: the round summary data
#----------------------------------------------------------------------------------------------------#
load_round_summary_data <- function() {
  return(load_csv(CSV_ROUND_SUMMARY_PATH))
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
  obs <- nrow(load_simulation_summary_data())
  out <- paste(" observations: ", obs, "\n", sep = "")
  out <- paste(out, " exclusions:   ", obs - nrow(data.ss), "\n\n", sep = "")

  # data

  data.ss.arsmall <- subset(data.ss, net.pct.rec <= CUT_OFF_SMALL_ATTACK_RATE)
  data.ss.arlarge <- subset(data.ss, net.pct.rec >= CUT_OFF_LARGE_ATTACK_RATE)

  out <- paste(out, "\\midrule \n", sep = "")
  out <- paste(out, "\\multicolumn{6}{l}{\\textbf{I.I. Epidemic}}", " \\", "\\ \n", sep = "")
  out <- paste(out, get_descriptive(data.ss$net.pct.rec, "Attack rate"))
  out <- paste(out, get_descriptive(data.ss$net.epidemic.duration, "Duration"))
  out <- paste(out, get_descriptive(data.ss.arsmall$net.epidemic.duration, "Duration (attack rates $\\leq 10\\%$)"))
  out <- paste(out, get_descriptive(data.ss.arlarge$net.epidemic.duration, "Duration (attack rates $\\geq 90\\%$)"))
  out <- paste(out, get_descriptive(data.ss$net.epidemic.peak.time, "Epidemic peak time"))
  out <- paste(out, get_descriptive(data.ss.arsmall$net.epidemic.peak.time, "Epidemic peak time (attack rates $\\leq 10\\%$)"))
  out <- paste(out, get_descriptive(data.ss.arlarge$net.epidemic.peak.time, "Epidemic peak time (attack rates 90\\%+)"))
  out <- paste(out, get_descriptive(data.ss$net.epidemic.peak.size, "Epidemic peak size"))
  out <- paste(out, get_descriptive(data.ss.arsmall$net.epidemic.peak.size, "Epidemic peak size (attack rates $\\leq 10\\%$)"))
  out <- paste(out, get_descriptive(data.ss.arlarge$net.epidemic.peak.size, "Epidemic peak size (attack rates $\\geq 90\\%$)"))
  out <- paste(out, get_descriptive(data.ss$net.dynamic.ties.broken.active.epidemic, "Ties broken"))
  out <- paste(out, get_descriptive(data.ss$net.dynamic.ties.out.accepted.epidemic, "Ties formed"))
  out <- paste(out, get_descriptive(data.ss$net.dynamic.ties.broken.active.epidemic, "Number of broken ties ($t^{-}_{G}$)"))
  out <- paste(out, get_descriptive(data.ss$net.dynamic.ties.out.accepted.epidemic, "Number of created ties ($t^{+}_{G}$)"))

  out <- paste(out, "\n\\midrule \n", sep = "")
  out <- paste(out, "\\multicolumn{6}{l}{\\textbf{II. Agent, independent}}", " \\", "\\ \n", sep = "")
  out <- paste(out, get_descriptive(data.ss$nb.alpha, "Preferred proportion of closed triads ($\\alpha$)"))
  out <- paste(out, get_descriptive(data.ss$nb.omega, "Likelihood of ties similar in risk perception ($\\omega$)"))
  out <- paste(out, get_descriptive(data.ad$nb.r.sigma, "Risk perception ($r_{\\sigma, \\pi}$)"))
  out <- paste(out, "\\multicolumn{6}{l}{\\textbf{III.II. Agent, dependent}}", " \\", "\\ \n", sep = "")
  out <- paste(out, get_descriptive(data.ad$agent.degree, "Degree ($\\mathcal{D}_{i}$)*"))
  out <- paste(out, get_descriptive(data.ad$agent.clustering, "Clustering ($\\mathcal{C}_{i}$)*"))
  out <- paste(out, get_descriptive(data.ad$agent.betweenness.normalized, "Betweenness ($\\mathcal{B}_{i}$)*"))
  out <- paste(out, get_descriptive(data.ad$nb.r.sigma.neighborhood, "Risk perception of direct ties ($r^{t_{i}}_{\\sigma, \\gamma})$"))
  out <- paste(out, "\\multicolumn{6}{l}{\\textbf{III.II. Agent, dependent (index case)}}", " \\", "\\ \n", sep = "")
  out <- paste(out, get_descriptive(data.ss$index.degree, "Degree ($\\mathcal{D}_{index}$)*"))
  out <- paste(out, get_descriptive(data.ss$index.clustering, "Clustering ($\\mathcal{C}_{index}$)*"))
  out <- paste(out, get_descriptive(data.ss$index.betweenness.normalized, "Betweenness ($\\mathcal{B}_{index}$)*"))
  out <- paste(out, get_descriptive(data.ss$index.r.sigma.neighborhood, "Risk perception of direct ties ($r^{t_{index}}_{\\sigma, \\gamma})$"))

  out <- paste(out, "\n\\midrule \n", sep = "")
  out <- paste(out, "\\multicolumn{6}{l}{\\textbf{III.I. Network, independent}}", " \\", "\\ \n", sep = "")
  out <- paste(out, get_descriptive(data.ss$nb.r.min, "Minimum risk perception ($r_{min}$)"))
  out <- paste(out, get_descriptive(data.ss$nb.r.max, "Maximum risk perception ($r_{max}$)"))
  out <- paste(out, "\n\\multicolumn{6}{l}{\\textbf{III.II. Network, dependent}}", " \\", "\\ \n", sep = "")
  out <- paste(out, get_descriptive(data.ss$net.clustering.pre.epidemic.av, "Clustering ($\\mathcal{C}_{G}$)*"))
  out <- paste(out, get_descriptive(data.ss$net.pathlength.pre.epidemic.av, "Path length ($\\mathcal{L}_{G}$)*"))
  out <- paste(out, get_descriptive(data.ss$net.assortativity.risk.perception.pre.epidemic, "Homophily ($\\mathcal{H}_{G}$)*"))
  out <- paste(out, get_descriptive(data.ss$net.degree.pre.epidemic.av, "Av. degree ($\\mathcal{D}_{G}$)*"))
  out <- paste(out, get_descriptive(data.ss$nb.r.av, "Av. risk perception ($r_{\\sigma, \\pi}$)"))

  out <- paste(out, "\n\\midrule \n", sep = "")
  out <- paste(out, "\\multicolumn{6}{l}{\\textbf{IV. Infectious diseases, independent}}", " \\", "\\ \n", sep = "")
  out <- paste(out, get_descriptive(data.ss$nb.sigma, "Disease severity ($\\sigma$)"))
  out <- paste(out, get_descriptive(data.ss$nb.tau, "Recovery time ($\\tau$)"))
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
    geom_point(size = 0.4, stroke = 0, alpha = 0.002, position = position_jitter(h = 0, w = 0)) +
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
  # out <- ""
  # out <- paste(out, "##############################################################\n", sep = "")
  # out <- paste(out, "#######              CORRELATIONS OF ALPHA              ######\n", sep = "")
  # out <- paste(out, "##############################################################\n\n", sep = "")

  ### DEGREE
  # plot
  ggsave(paste(EXPORT_PATH_PLOTS, "cor-1-alpha-degree", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),

         plot_levels(df.1 = data.frame(x = data.ss$nb.alpha,
                                       y = data.ss$net.degree.pre.epidemic.av),

                     name.x   = "alpha",
                     limits.x = c(0, 1),
                     breaks.x = seq(0, 1, 0.25),

                     name.y   = "degree",
                     limits.y = LIMITS_DEGREE,
                     breaks.y = BREAKS_DEGREE,

                     show.legend = FALSE,
                     probability.infections = FALSE),

         # plot_correlation(data.ss,
         #                  data.ss$nb.alpha, data.ss$net.clustering.pre.epidemic.av,
         #                  "alpha", "Network clustering",
         #                  c(0, 1), c(0, 1),
         #                  seq(0, 1, 1/4), seq(0, 1, 1/4)),

         width = EXPORT_PLOT_WIDTH,
         height = EXPORT_PLOT_WIDTH,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)

  ### CLUSTERING
  # plot
  ggsave(paste(EXPORT_PATH_PLOTS, "cor-2-alpha-clustering", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),

         plot_levels(df.1 = data.frame(x = data.ss$nb.alpha,
                                       y = data.ss$net.clustering.pre.epidemic.av),

                     name.x   = "alpha",
                     limits.x = c(0, 1),
                     breaks.x = seq(0, 1, 0.25),

                     name.y   = "clustering",
                     limits.y = LIMITS_CLUSTERING,
                     breaks.y = BREAKS_CLUSTERING,

                     show.legend = FALSE,
                     probability.infections = FALSE),

         # plot_correlation(data.ss,
         #                  data.ss$nb.alpha, data.ss$net.clustering.pre.epidemic.av,
         #                  "alpha", "Network clustering",
         #                  c(0, 1), c(0, 1),
         #                  seq(0, 1, 1/4), seq(0, 1, 1/4)),

         width = EXPORT_PLOT_WIDTH,
         height = EXPORT_PLOT_WIDTH,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)
  # correlation
  # out <- paste(out, get_correlation_text(data.ss$nb.alpha,
  #                                        data.ss$net.clustering.pre.epidemic.av,
  #                                        "alpha",
  #                                        "network clustering"),
  #              sep = "")

  ### PATH LENGTH
  # plot
  ggsave(paste(EXPORT_PATH_PLOTS, "cor-3-alpha-pathlength", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),

         plot_levels(df.1 = data.frame(x = data.ss$nb.alpha,
                                       y = data.ss$net.pathlength.pre.epidemic.av),

                     name.x   = "alpha",
                     limits.x = c(0, 1),
                     breaks.x = seq(0, 1, 0.25),

                     name.y   = "path length",
                     limits.y = c(2, 4),
                     breaks.y = seq(2, 4, 0.5),

                     show.legend = FALSE,
                     probability.infections = FALSE),

         # plot_correlation(data.ss,
         #                  data.ss$nb.alpha, data.ss$net.pathlength.pre.epidemic.av,
         #                  "alpha", "Average path length",
         #                  c(0, 1), c(2, 5),
         #                  seq(0, 1, 1/4), seq(2, 5, 0.5)),

         width = EXPORT_PLOT_WIDTH,
         height = EXPORT_PLOT_WIDTH,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)

  ### ASSORTATIVITY
  # plot
  ggsave(paste(EXPORT_PATH_PLOTS, "cor-4-alpha-assortativity", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),

         plot_levels(df.1 = data.frame(x = data.ss$nb.alpha,
                                       y = data.ss$net.assortativity.pre.epidemic),

                     name.x   = "alpha",
                     limits.x = c(0, 1),
                     breaks.x = seq(0, 1, 0.25),

                     name.y   = "assortativity",
                     limits.y = c(0, 1),
                     breaks.y = seq(0, 1, 0.25),

                     show.legend = FALSE,
                     probability.infections = FALSE),

         # plot_correlation(data.ss,
         #                  data.ss$nb.alpha, data.ss$net.pathlength.pre.epidemic.av,
         #                  "alpha", "Average path length",
         #                  c(0, 1), c(2, 5),
         #                  seq(0, 1, 1/4), seq(2, 5, 0.5)),

         width = EXPORT_PLOT_WIDTH,
         height = EXPORT_PLOT_WIDTH,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)

  # correlation
  # out <- paste(out, get_correlation_text(data.ss$nb.alpha,
  #                                        data.ss$net.pathlength.pre.epidemic.av,
  #                                        "alpha",
  #                                        "av. path length"),
  #              sep = "")

  # ### BETWEENNESS (INDEX CASE)
  # # plot
  # ggsave(paste(EXPORT_PATH_PLOTS, "cor-3-alpha-betweenness", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
  #        plot_correlation(data.ss,
  #                         data.ss$nb.alpha, data.ss$net.betweenness.pre.epidemic.av,
  #                         "alpha", "Average betweenness",
  #                         c(0, 1), c(0, 0.3),
  #                         seq(0, 1, 1/4), seq(0, 0.3, 0.05)),
  #        width = EXPORT_PLOT_HEIGHT,
  #        height = EXPORT_PLOT_HEIGHT,
  #        units = EXPORT_SIZE_UNITS,
  #        dpi = EXPORT_DPI,
  #        device = EXPORT_FILE_TYPE_PLOTS)
  # # correlation
  # out <- paste(out, get_correlation_text(data.ss$nb.alpha,
  #                                        data.ss$net.betweenness.pre.epidemic.av,
  #                                        "alpha",
  #                                        "av. betweenness"),
  #              sep = "")

  ##### OMEGA
  # out <- paste(out, "\n\n", sep = "")
  # out <- paste(out, "##############################################################\n", sep = "")
  # out <- paste(out, "#######              CORRELATIONS OF OMEGA              ######\n", sep = "")
  # out <- paste(out, "##############################################################\n\n", sep = "")

  ### DEGREE
  # plot
  ggsave(paste(EXPORT_PATH_PLOTS, "cor-5-omega-degree", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),

         plot_levels(df.1 = data.frame(x = data.ss$nb.omega,
                                       y = data.ss$net.degree.pre.epidemic.av),

                     name.x   = "omega",
                     limits.x = c(0, 1),
                     breaks.x = seq(0, 1, 0.25),

                     name.y   = "degree",
                     limits.y = LIMITS_DEGREE,
                     breaks.y = BREAKS_DEGREE,

                     show.legend = FALSE,
                     probability.infections = FALSE),

         # plot_correlation(data.ss,
         #                  data.ss$nb.omega, data.ss$net.assortativity.pre.epidemic,
         #                  "omega", "Assortativity",
         #                  c(0, 1), c(0, 1),
         #                  seq(0, 1, 1/4), seq(0, 1, 1/4)),

         width = EXPORT_PLOT_WIDTH,
         height = EXPORT_PLOT_WIDTH,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)

  ### CLUSTERING
  # plot
  ggsave(paste(EXPORT_PATH_PLOTS, "cor-6-omega-clustering", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),

         plot_levels(df.1 = data.frame(x = data.ss$nb.omega,
                                       y = data.ss$net.clustering.pre.epidemic.av),

                     name.x   = "omega",
                     limits.x = c(0, 1),
                     breaks.x = seq(0, 1, 0.25),

                     name.y   = "clustering",
                     limits.y = LIMITS_CLUSTERING,
                     breaks.y = BREAKS_CLUSTERING,

                     show.legend = FALSE,
                     probability.infections = FALSE),

         # plot_correlation(data.ss,
         #                  data.ss$nb.omega, data.ss$net.assortativity.pre.epidemic,
         #                  "omega", "Assortativity",
         #                  c(0, 1), c(0, 1),
         #                  seq(0, 1, 1/4), seq(0, 1, 1/4)),

         width = EXPORT_PLOT_WIDTH,
         height = EXPORT_PLOT_WIDTH,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)

  ### PATH LENGTH
  # plot
  ggsave(paste(EXPORT_PATH_PLOTS, "cor-7-omega-pathlength", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),

         plot_levels(df.1 = data.frame(x = data.ss$nb.omega,
                                       y = data.ss$net.pathlength.pre.epidemic.av),

                     name.x   = "omega",
                     limits.x = c(0, 1),
                     breaks.x = seq(0, 1, 0.25),

                     name.y   = "path length",
                     limits.y = c(2, 4),
                     breaks.y = seq(2, 4, 0.5),

                     show.legend = FALSE,
                     probability.infections = FALSE),

         # plot_correlation(data.ss,
         #                  data.ss$nb.omega, data.ss$net.assortativity.pre.epidemic,
         #                  "omega", "Assortativity",
         #                  c(0, 1), c(0, 1),
         #                  seq(0, 1, 1/4), seq(0, 1, 1/4)),

         width = EXPORT_PLOT_WIDTH,
         height = EXPORT_PLOT_WIDTH,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)

  ### ASSORTATIVITY
  # plot
  ggsave(paste(EXPORT_PATH_PLOTS, "cor-8-omega-assortativity", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),

         plot_levels(df.1 = data.frame(x = data.ss$nb.omega,
                                       y = data.ss$net.assortativity.pre.epidemic),

                     name.x   = "omega",
                     limits.x = c(0, 1),
                     breaks.x = seq(0, 1, 0.25),

                     name.y   = "assortativity",
                     limits.y = c(0, 1),
                     breaks.y = seq(0, 1, 0.25),

                     show.legend = FALSE,
                     probability.infections = FALSE),

         # plot_correlation(data.ss,
         #                  data.ss$nb.omega, data.ss$net.assortativity.pre.epidemic,
         #                  "omega", "Assortativity",
         #                  c(0, 1), c(0, 1),
         #                  seq(0, 1, 1/4), seq(0, 1, 1/4)),

         width = EXPORT_PLOT_WIDTH,
         height = EXPORT_PLOT_WIDTH,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)
  # correlation
  # out <- paste(out, get_correlation_text(data.ss$nb.omega,
  #                                        data.ss$net.assortativity.pre.epidemic,
  #                                        "omega",
  #                                        "assortativity"),
  #              sep = "")

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
  #                         data.ss$net.pct.rec, data.ss$net.epidemic.duration,
  #                         "Attack rate", "Duration",
  #                         c(0, 100), c(5, 50),
  #                         seq(0, 100, 25), seq(5, 50, 5)),
  #        width = EXPORT_PLOT_HEIGHT,
  #        height = EXPORT_PLOT_HEIGHT,
  #        units = EXPORT_SIZE_UNITS,
  #        dpi = EXPORT_DPI,
  #        device = EXPORT_FILE_TYPE_PLOTS)
  # # correlation
  # out <- paste(out, get_correlation_text(data.ss$net.pct.rec,
  #                                        data.ss$net.epidemic.duration,
  #                                        "attack rate (dynamic)",
  #                                        "duration (dynamic)"),
  #              sep = "")
  #
  # # plot
  # ggsave(paste(EXPORT_PATH_PLOTS, "cor-7-attackrate-peak-dynamic", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
  #        plot_correlation(data.ss,
  #                         data.ss$net.pct.rec, data.ss$net.epidemic.peak,
  #                         "Attack rate", "Peak",
  #                         c(0, 100), c(5, 50),
  #                         seq(0, 100, 25), seq(5, 50, 5)),
  #        width = EXPORT_PLOT_HEIGHT,
  #        height = EXPORT_PLOT_HEIGHT,
  #        units = EXPORT_SIZE_UNITS,
  #        dpi = EXPORT_DPI,
  #        device = EXPORT_FILE_TYPE_PLOTS)
  # # correlation
  # out <- paste(out, get_correlation_text(data.ss$net.pct.rec,
  #                                        data.ss$net.epidemic.peak,
  #                                        "attack rate (dynamic)",
  #                                        "peak (dynamic)"),
  #              sep = "")
  #
  # # plot
  # ggsave(paste(EXPORT_PATH_PLOTS, "cor-8-duration-peak-dynamic", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
  #        plot_correlation(data.ss,
  #                         data.ss$net.epidemic.duration, data.ss$net.epidemic.peak,
  #                         "Attack rate", "Peak",
  #                         c(5, 50), c(5, 50),
  #                         seq(5, 50, 5), seq(5, 50, 5)),
  #        width = EXPORT_PLOT_HEIGHT,
  #        height = EXPORT_PLOT_HEIGHT,
  #        units = EXPORT_SIZE_UNITS,
  #        dpi = EXPORT_DPI,
  #        device = EXPORT_FILE_TYPE_PLOTS)
  # # correlation
  # out <- paste(out, get_correlation_text(data.ss$net.epidemic.duration,
  #                                        data.ss$net.epidemic.peak,
  #                                        "duration (dynamic)",
  #                                        "peak (dynamic)"),
  #              sep = "")
  #

  ##### EXPORT
  # dir.create(EXPORT_PATH_NUM, showWarnings = FALSE)
  # cat(out, file = paste(EXPORT_PATH_NUM,
  #                       "correlations",
  #                       EXPORT_FILE_EXTENSION_DESC,
  #                       sep = ""))
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

se <- function(vec) {
  return(sd(vec)/sqrt(length(vec)))
}

prepare_level <- function(df, type, probability.infections, prepare.ci) {
  df.sorted <- df[order(df$x),]
  df.prepared <- data.frame(x    = numeric(0),
                            y    = numeric(0),
                            se   = numeric(0),
                            ci   = numeric(0),
                            type = character(0))
  bin.prev <- 0.00
  for (bin in seq(0, nrow(df), nrow(df)/BINS)) {
    bin <- ceiling(bin)
    df.binned <- df.sorted[bin.prev:bin,]
    if (probability.infections) {
      if (prepare.ci) {
        df.prepared <- rbind(df.prepared,
                             data.frame(x    = mean(df.binned$x),
                                        y    = sum(df.binned$y) / length(df.binned$y),
                                        se   = se(df.binned$y),
                                        ci   = CI(df.binned$y, ci = 0.95)[1]-CI(df.binned$y, ci = 0.95)[2],
                                        type = type))
      } else {
        df.prepared <- rbind(df.prepared,
                             data.frame(x    = mean(df.binned$x),
                                        y    = sum(df.binned$y) / length(df.binned$y),
                                        se   = se(df.binned$y),
                                        type = type))
      }
    } else {
      if (prepare.ci) {
        df.prepared <- rbind(df.prepared,
                             data.frame(x    = mean(df.binned$x),
                                        y    = mean(df.binned$y),
                                        se   = se(df.binned$y),
                                        ci   = CI(df.binned$y, ci = 0.95)[1]-CI(df.binned$y, ci = 0.95)[2],
                                        type = type))
      } else {
        df.prepared <- rbind(df.prepared,
                             data.frame(x    = mean(df.binned$x),
                                        y    = mean(df.binned$y),
                                        se   = se(df.binned$y),
                                        type = type))
      }
    }
    bin.prev <- bin
  }
  return(df.prepared)
}

plot_levels <- function(df.1,
                        df.2 = c(),
                        df.3 = c(),
                        name.x, limits.x, breaks.x,
                        name.y, limits.y, breaks.y,
                        show.legend,
                        probability.infections,
                        plot.reg.line = FALSE,
                        plot.se = FALSE,
                        plot.ci = FALSE) {

  # mean data preparations
  df.plot <- prepare_level(df = df.1, "level.1", probability.infections, FALSE)
  if (length(df.2) > 0) {
    df.plot <- rbind(df.plot,
                     prepare_level(df = df.2, "level.2",
                                   probability.infections,
                                   FALSE))
  }
  if (length(df.3) > 0) {
    df.plot <- rbind(df.plot,
                     prepare_level(df = df.3, "level.3",
                                   probability.infections,
                                   FALSE))
  }

  df.plot <- subset(df.plot, !is.na(x))

  error.width = (limits.x[2] - limits.x[1]) * 0.02

  ### PLOT ###
  # data
  p <- ggplot(df.plot,
              aes(x     = x,
                  y     = y,
                  color = type,
                  shape = type))

  # standard errors
  if (plot.se) {
    p <- p + geom_errorbar(aes(ymin = y-se, ymax = y+se),
                           width = error.width,
                           alpha = POINT_ALPHA)
  }

  # confidence intervals
  if (plot.ci) {
    p <- p + geom_errorbar(aes(ymin = y-ci, ymax = y+ci),
                           width = error.width,
                           alpha = POINT_ALPHA)
  }

  # points
  p <- p + geom_point(alpha = POINT_ALPHA,
                      size  = POINT_SIZE,
                      aes(colour = type,
                          shape  = factor(type)))

  # colors and shapes
  if (length(df.2) == 0) {
    p <- p +
      scale_color_manual(values = c("level.1" = COLOR_0)) +
      scale_shape_manual(values = c("level.1" = SHAPE_0))
  } else if (length(df.3) == 0) {
    p <- p +
      scale_color_manual(values = c("level.1" = COLOR_1,
                                    "level.2" = COLOR_2)) +
      scale_shape_manual(values = c("level.1" = SHAPE_1,
                                    "level.2" = SHAPE_2))
  } else {
    p <- p +
      scale_color_manual(values = c("level.1" = COLOR_1,
                                    "level.2" = COLOR_2,
                                    "level.3" = COLOR_3)) +
      scale_shape_manual(values = c("level.1" = SHAPE_1,
                                    "level.2" = SHAPE_2,
                                    "level.3" = SHAPE_3))
  }

  # linear models
  if (plot.reg.line) {
    fit.1 <- lm(y~x, data = df.1)
    if (length(df.2) == 0) {
      p <- p +
        geom_abline(slope     = fit.1$coefficients[2],
                    intercept = fit.1$coefficients[1],
                    size      = LINE_SIZE,
                    linetype  = LINE_1,
                    colour    = COLOR_0,
                    alpha     = LINE_ALPHA)
    } else {
      p <- p +
        geom_abline(slope     = fit.1$coefficients[2],
                    intercept = fit.1$coefficients[1],
                    size      = LINE_SIZE,
                    linetype  = LINE_1,
                    colour    = COLOR_1,
                    alpha     = LINE_ALPHA)
      fit.2 <- lm(y~x, data = df.2)
      p <- p +
        geom_abline(slope     = fit.2$coefficients[2],
                    intercept = fit.2$coefficients[1],
                    size      = LINE_SIZE,
                    linetype  = LINE_2,
                    colour    = COLOR_2,
                    alpha     = LINE_ALPHA)
    }
  }

  # axes
  p <- p +
    scale_x_continuous(limits = limits.x,
                       breaks = breaks.x) +
    xlab(name.x) +
    scale_y_continuous(limits = limits.y,
                       breaks = breaks.y) +
    ylab(name.y)

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
  d <- rbind(d, data.frame("structure"    = rep("dynamic", nrow(data.ss)),
                           "attack.rate"  = data.ss$net.pct.rec))
  d$structure <- factor(d$structure, levels = c("dynamic", "static"))

  # plot
  p.attackrate <- ggplot(d, aes(x = structure, y = attack.rate, fill = factor(structure))) +
    #geom_point(aes(colour = factor(structure), shape  = factor(structure)), position = position_jitter(), alpha=0.6, show.legend = FALSE) +
    geom_boxplot(alpha = 1, show.legend = FALSE, lwd = 2.5, fatten = 1.5, outlier.size = 3) +
    scale_color_manual(values = c("static" = "#AAAAAA", "dynamic" = "#AAAAAA")) +
    scale_fill_manual(values = c("static" = "#AAAAAA", "dynamic" = "#AAAAAA")) +
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

  # static: 0-10
  d.small.static <- subset(data.ss, net.static.pct.rec <= CUT_OFF_SMALL_ATTACK_RATE)
  d <- data.frame("structure"   = rep("static", nrow(d.small.static)),
                  "attack.rate" = rep("small", nrow(d.small.static)),
                  "duration"    = d.small.static$net.static.epidemic.duration,
                  "color"       = rep("small", nrow(d.small.static)))

  # static: 10-90
  d.med.static <- subset(data.ss, net.static.pct.rec > CUT_OFF_SMALL_ATTACK_RATE &
                           net.static.pct.rec < CUT_OFF_LARGE_ATTACK_RATE)
  d <- rbind(d, data.frame("structure"   = rep("static", nrow(d.med.static)),
                  "attack.rate" = rep("med", nrow(d.med.static)),
                  "duration"    = d.med.static$net.static.epidemic.duration,
                  "color"       = rep("med", nrow(d.med.static))))

  # static: 90-100
  d.large.static <- subset(data.ss, net.static.pct.rec >= CUT_OFF_LARGE_ATTACK_RATE)
  d <- rbind(d, data.frame("structure"   = rep("static", nrow(d.large.static)),
                           "attack.rate" = rep("large", nrow(d.large.static)),
                           "duration"    = d.large.static$net.static.epidemic.duration,
                           "color"       = rep("large", nrow(d.large.static))))


  # dynamic: 0-10
  d.small <- subset(data.ss, net.pct.rec <= CUT_OFF_SMALL_ATTACK_RATE)
  d <- rbind(d, data.frame("structure"   = rep("dynamic", nrow(d.small)),
                  "attack.rate" = rep("small", nrow(d.small)),
                  "duration"    = d.small$net.epidemic.duration,
                  "color"       = rep("small", nrow(d.small))))

  # dynamic: 10-90
  d.med <- subset(data.ss, net.pct.rec > CUT_OFF_SMALL_ATTACK_RATE &
                           net.pct.rec < CUT_OFF_LARGE_ATTACK_RATE)
  d <- rbind(d, data.frame("structure"   = rep("dynamic", nrow(d.med)),
                  "attack.rate" = rep("med", nrow(d.med)),
                  "duration"    = d.med$net.epidemic.duration,
                  "color"       = rep("med", nrow(d.med))))

  # dynamic: 90-100
  d.large <- subset(data.ss, net.pct.rec >= CUT_OFF_LARGE_ATTACK_RATE)
  d <- rbind(d, data.frame("structure"   = rep("dynamic", nrow(d.large)),
                           "attack.rate" = rep("large", nrow(d.large)),
                           "duration"    = d.large$net.epidemic.duration,
                           "color"       = rep("large", nrow(d.large))))

  d$structure <- factor(d$structure, levels = c("dynamic", "static"))
  d$color <- factor(d$color, c("small", "med", "large"))

  # plot
  p.duration <- ggplot(d,
                       aes(x = structure, y = duration, fill = color)) +

    geom_boxplot(lwd = 2.5,
                 fatten = 1.5,
                 outlier.size = 3) +

    scale_color_manual(values = c("small" = COLOR_2,
                                  "med"   = COLOR_1,
                                  "large" = COLOR_3)) +

    scale_fill_manual(values = c("small" = COLOR_2,
                                 "med"   = COLOR_1,
                                 "large" = COLOR_3)) +

    scale_x_discrete(name="Network during epidemic") +

    scale_y_continuous(name="Duration",
                       limits=c(0, 50),
                       breaks = seq(0, 50, 10)) +

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

  d.90.static <- subset(data.ss, net.static.pct.rec >= CUT_OFF_LARGE_ATTACK_RATE)
  d <- rbind(d, data.frame("structure"   = rep("static", nrow(d.90.static)),
                           "attack.rate" = rep("90-100%", nrow(d.90.static)),
                           "peak"        = d.90.static$net.static.epidemic.peak,
                           "color"       = rep("90-100%.static", nrow(d.90.static))))

  d <- rbind(d, data.frame("structure"   = rep("dynamic", nrow(data.ss)),
                           "attack.rate" = rep("average", nrow(data.ss)),
                           "peak"        = data.ss$net.epidemic.peak,
                           "color"       = rep("average", nrow(data.ss))))

  d.90 <- subset(data.ss, net.pct.rec >= CUT_OFF_LARGE_ATTACK_RATE)
  d <- rbind(d, data.frame("structure"   = rep("dynamic", nrow(d.90)),
                           "attack.rate" = rep("90-100%", nrow(d.90)),
                           "peak"        = d.90$net.epidemic.peak,
                           "color"       = rep("90-100%", nrow(d.90))))

  d$structure <- factor(d$structure, levels = c("dynamic", "static"))
  d$color <- factor(d$color, c("average", "90-100%", "average.static", "90-100%.static"))

  # plot
  p.peak <- ggplot(d, aes(x = structure, y = peak, fill = color)) +
    geom_boxplot(lwd = 2.5, fatten = 1.5, outlier.size = 3) +
    scale_color_manual(values = c("average" = "#D55E00",
                                  "90-100%" = "#ffbd88",
                                  "average.static"  = "#0072B2",
                                  "90-100%.static"  = "#64c7ff")) +
    scale_fill_manual(values = c("average"  = "#D55E00",
                                 "90-100%"  = "#ffbd88",
                                 "average.static"   = "#0072B2",
                                 "90-100%.static"   = "#64c7ff")) +
    scale_x_discrete(name="Network during epidemic") +
    scale_y_continuous(name="Peak", limits=c(0, 50), breaks = seq(0, 50, 10)) +
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


  # static: 0-10
  d.small.static <- subset(data.ss, net.static.pct.rec <= CUT_OFF_SMALL_ATTACK_RATE)
  d <- data.frame("structure"   = rep("static", nrow(d.small.static)),
                  "attack.rate" = rep("small", nrow(d.small.static)),
                  "peak.size"    = d.small.static$net.static.epidemic.peak.size,
                  "color"       = rep("small", nrow(d.small.static)))

  # static: 10-90
  d.med.static <- subset(data.ss, net.static.pct.rec > CUT_OFF_SMALL_ATTACK_RATE &
                           net.static.pct.rec < CUT_OFF_LARGE_ATTACK_RATE)
  d <- rbind(d, data.frame("structure"   = rep("static", nrow(d.med.static)),
                           "attack.rate" = rep("med", nrow(d.med.static)),
                           "peak.size"    = d.med.static$net.static.epidemic.peak.size,
                           "color"       = rep("med", nrow(d.med.static))))

  # static: 90-100
  d.large.static <- subset(data.ss, net.static.pct.rec >= CUT_OFF_LARGE_ATTACK_RATE)
  d <- rbind(d, data.frame("structure"   = rep("static", nrow(d.large.static)),
                           "attack.rate" = rep("large", nrow(d.large.static)),
                           "peak.size"    = d.large.static$net.static.epidemic.peak.size,
                           "color"       = rep("large", nrow(d.large.static))))


  # dynamic: 0-10
  d.small <- subset(data.ss, net.pct.rec <= CUT_OFF_SMALL_ATTACK_RATE)
  d <- rbind(d, data.frame("structure"   = rep("dynamic", nrow(d.small)),
                           "attack.rate" = rep("small", nrow(d.small)),
                           "peak.size"    = d.small$net.epidemic.peak.size,
                           "color"       = rep("small", nrow(d.small))))

  # dynamic: 10-90
  d.med <- subset(data.ss, net.pct.rec > CUT_OFF_SMALL_ATTACK_RATE &
                            net.pct.rec < CUT_OFF_LARGE_ATTACK_RATE)
  d <- rbind(d, data.frame("structure"   = rep("dynamic", nrow(d.med)),
                           "attack.rate" = rep("med", nrow(d.med)),
                           "peak.size"    = d.med$net.epidemic.peak.size,
                           "color"       = rep("med", nrow(d.med))))

  # dynamic: 90-100
  d.large <- subset(data.ss, net.pct.rec >= CUT_OFF_LARGE_ATTACK_RATE)
  d <- rbind(d, data.frame("structure"   = rep("dynamic", nrow(d.large)),
                           "attack.rate" = rep("large", nrow(d.large)),
                           "peak.size"    = d.large$net.epidemic.peak.size,
                           "color"       = rep("large", nrow(d.large))))

  d$structure <- factor(d$structure, levels = c("dynamic", "static"))
  d$color <- factor(d$color, c("small", "med", "large"))

  # plot
  p.peak.size <- ggplot(d,
                       aes(x = structure, y = peak.size, fill = color)) +

    geom_boxplot(lwd = 2.5,
                 fatten = 1.5,
                 outlier.size = 3) +

    scale_color_manual(values = c("small" = COLOR_2,
                                  "med"   = COLOR_1,
                                  "large" = COLOR_3)) +

    scale_fill_manual(values = c("small" = COLOR_2,
                                 "med"   = COLOR_1,
                                 "large" = COLOR_3)) +

    scale_x_discrete(name="Network during epidemic") +

    scale_y_continuous(name="Peak size", limits=c(0, 80)) +

    theme_bw(base_size = THEME_BASE_SIZE) +

    guides(fill=guide_legend(title="Attack rate"))

  if (show.legend) {
    p.peak.size <- p.peak.size +
      theme(legend.position = "top",
            legend.justification = "right",
            legend.margin = margin(0,0,0,0),
            legend.box.margin = margin(-4,0,-6,0),
            legend.title = element_blank(),
            legend.background = element_rect(fill=alpha('white', 0)),
            legend.key = element_rect(colour = NA, fill = NA))
  } else {
    p.peak.size <- p.peak.size +
      theme(legend.position = "none")
  }

  return(p.peak.size)
}


get_plots_dynamics <- function(data.ss = load_simulation_summary_data()) {

  plots <- c(list(plot_c1_a(data.ss)), "0-01-attackrate")
  plots <- c(plots, list(plot_c1_b_duration(data.ss)), "0-02-duration")
  # plots <- c(plots, list(plot_c1_b_peak(data.ss = )), "0-03-peak")
  plots <- c(plots, list(plot_c1_b_peaksize(data.ss)), "0-03-peaksize")


  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss$net.ties.broken.active.epidemic +
                                                  data.ss$net.ties.out.accepted.epidemic,
                                                y = data.ss$net.pct.rec),

                              df.2 = data.frame(x = data.ss$net.static.ties.broken.active.epidemic +
                                                  data.ss$net.static.ties.out.accepted.epidemic,
                                                y = data.ss$net.static.pct.rec),

                              name.x   = LABEL_NETDECISIONS,
                              limits.x = c(0, 600),
                              breaks.x = seq(0, 600, 100),

                              name.y   = LABEL_ATTACKRATE,
                              limits.y = LIMITS_ATTACKRATE,
                              breaks.y = BREAKS_ATTACKRATE,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),

             "0-04-netchanges-attackrate")

  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss$net.pct.rec,
                                                y = data.ss$net.ties.broken.active.epidemic +
                                                  data.ss$net.ties.out.accepted.epidemic),

                              df.2 = data.frame(x = data.ss$net.static.pct.rec,
                                                y = data.ss$net.static.ties.broken.active.epidemic +
                                                  data.ss$net.static.ties.out.accepted.epidemic),

                              name.x   = LABEL_ATTACKRATE,
                              limits.x = LIMITS_ATTACKRATE,
                              breaks.x = BREAKS_ATTACKRATE,

                              name.y   = LABEL_NETDECISIONS,
                              limits.y = c(0, 600),
                              breaks.y = seq(0, 600, 100),

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),

             "0-04-attackrate-netchanges")

  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss$net.epidemic.duration,
                                                y = data.ss$net.ties.broken.active.epidemic +
                                                  data.ss$net.ties.out.accepted.epidemic),

                              df.2 = data.frame(x = data.ss$net.static.epidemic.duration,
                                                y = data.ss$net.static.ties.broken.active.epidemic +
                                                  data.ss$net.static.ties.out.accepted.epidemic),

                              name.x   = LABEL_DURATION,
                              limits.x = LIMITS_DURATIONPEAK,
                              breaks.x = BREAKS_DURATIONPEAK,

                              name.y   = LABEL_NETDECISIONS,
                              limits.y = c(0, 600),
                              breaks.y = seq(0, 600, 100),

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),

             "0-05-netchanges-duration")

  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss$net.epidemic.peak.size,
                                                y = data.ss$net.ties.broken.active.epidemic +
                                                  data.ss$net.ties.out.accepted.epidemic),

                              df.2 = data.frame(x = data.ss$net.static.epidemic.peak.size,
                                                y = data.ss$net.static.ties.broken.active.epidemic +
                                                  data.ss$net.static.ties.out.accepted.epidemic),

                              name.x   = LABEL_PEAKSIZE,
                              limits.x = LIMITS_PEAKSIZE,
                              breaks.x = BREAKS_PEAKSIZE,

                              name.y   = LABEL_NETDECISIONS,
                              limits.y = c(0, 600),
                              breaks.y = seq(0, 600, 100),

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),

             "0-06-netchanges-peaksize")

   plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss$net.pct.rec,
                                                y = data.ss$net.epidemic.duration),

                              df.2 = data.frame(x = data.ss$net.static.pct.rec,
                                                y = data.ss$net.static.epidemic.duration),

                              name.x   = LABEL_ATTACKRATE,
                              limits.x = LIMITS_ATTACKRATE,
                              breaks.x = BREAKS_ATTACKRATE,

                              name.y   = LABEL_DURATION,
                              limits.y = c(0, 40),
                              breaks.y = seq(0, 40, 10),

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),

             "0-07-attackrate-duration")

  # plots <- c(plots,
  #            list(plot_levels(df.1 = data.frame(x = data.ss$net.pct.rec,
  #                                               y = data.ss$net.epidemic.peak),
  #
  #                             df.2 = data.frame(x = data.ss$net.static.pct.rec,
  #                                               y = data.ss$net.static.epidemic.peak),
  #
  #                             name.x   = LABEL_ATTACKRATE,
  #                             limits.x = LIMITS_ATTACKRATE,
  #                             breaks.x = BREAKS_ATTACKRATE,
  #
  #                             name.y   = LABEL_PEAK,
  #                             limits.y = c(0, 40),
  #                             breaks.y = seq(0, 40, 10),
  #
  #                             show.legend = SHOW_LEGEND,
  #                             probability.infections = FALSE)),
  #
  #            "0-06-attackrate-peak")

  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss$net.pct.rec,
                                                y = data.ss$net.epidemic.peak.size),

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

             "0-08-attackrate-peaksize")

  # plots <- c(plots,
  #            list(plot_levels(df.1 = data.frame(x = data.ss$net.epidemic.duration,
  #                                               y = data.ss$net.epidemic.peak),
  #
  #                             df.2 = data.frame(x = data.ss$net.static.epidemic.duration,
  #                                               y = data.ss$net.static.epidemic.peak),
  #
  #                             name.x   = LABEL_DURATION,
  #                             limits.x = LIMITS_DURATIONPEAK,
  #                             breaks.x = LIMITS_DURATIONPEAK,
  #
  #                             name.y   = LABEL_PEAK,
  #                             limits.y = LIMITS_DURATIONPEAK,
  #                             breaks.y = BREAKS_DURATIONPEAK,
  #
  #                             show.legend = SHOW_LEGEND,
  #                             probability.infections = FALSE)),
  #
  #            "0-08-duration-peak")

  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss$net.epidemic.duration,
                                                y = data.ss$net.epidemic.peak.size),

                              df.2 = data.frame(x = data.ss$net.static.epidemic.duration,
                                                y = data.ss$net.static.epidemic.peak.size),

                              name.x   = LABEL_DURATION,
                              limits.x = LIMITS_DURATIONPEAK,
                              breaks.x = BREAKS_DURATIONPEAK,

                              name.y   = LABEL_PEAKSIZE,
                              limits.y = LIMITS_PEAKSIZE,
                              breaks.y = BREAKS_PEAKSIZE,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),

             "0-09-duration-peaksize")

  # plots <- c(plots,
  #            list(plot_levels(df.1 = data.frame(x = data.ss$net.epidemic.peak,
  #                                               y = data.ss$net.epidemic.peak.size),
  #
  #                             df.2 = data.frame(x = data.ss$net.static.epidemic.peak,
  #                                               y = data.ss$net.static.epidemic.peak.size),
  #
  #                             name.x   = LABEL_PEAK,
  #                             limits.x = LIMITS_DURATIONPEAK,
  #                             breaks.x = LIMITS_DURATIONPEAK,
  #
  #                             name.y   = LABEL_PEAKSIZE,
  #                             limits.y = LIMITS_PEAKSIZE,
  #                             breaks.y = BREAKS_PEAKSIZE,
  #
  #                             show.legend = SHOW_LEGEND,
  #                             probability.infections = FALSE)),
  #
  #            "0-10-peak-peaksize")

  return(plots)
}


# get_plots_probability <- function(data.ad = load_agent_details_prepared_data(), ep.structure) {
#
#
#   data.ad.by.struc <- subset(data.ad, nb.ep.structure == ep.structure)
#
#   # net changes
#   plots <- c(list(plot_levels(df.1 = data.frame(x = data.ad.by.struc$agent.cons.broken.active.epidemic +
#                                                   data.ad.by.struc$agent.cons.out.accepted.epidemic,
#                                                 y = data.ad.by.struc$agent.infected),
#
#                               name.x   = LABEL_NETDECISIONS,
#                               limits.x = LIMITS_NETDECISIONS,
#                               breaks.x = BREAKS_NETDECISIONS,
#
#                               name.y   = LABEL_PROBABILITYINFECTION,
#                               limits.y = LIMITS_PROBABILITYINFECTION,
#                               breaks.y = BREAKS_PROBABILITYINFECTION,
#
#                               show.legend = SHOW_LEGEND,
#                               probability.infections = TRUE)),
#
#              paste(ep.structure, "-0-1-netchanges-probabilityinfection", sep = ""))
#
#   # risk perception
#   plots <- c(plots,
#              list(plot_levels(df.1 = data.frame(x = data.ad.by.struc$nb.r.sigma,
#                                                 y = data.ad.by.struc$agent.infected),
#
#                               name.x   = LABEL_RISKPERCEPTION,
#                               limits.x = LIMITS_RISKPERCEPTION,
#                               breaks.x = BREAKS_RISKPERCEPTION,
#
#                               name.y   = LABEL_PROBABILITYINFECTION,
#                               limits.y = LIMITS_PROBABILITYINFECTION,
#                               breaks.y = BREAKS_PROBABILITYINFECTION,
#
#                               show.legend = SHOW_LEGEND,
#                               probability.infections = TRUE)),
#
#              paste(ep.structure, "-1-1-riskperception-probabilityinfection", sep = ""))
#
#   # degree
#   plots <- c(plots,
#              list(plot_levels(df.1 = data.frame(x = data.ad.by.struc$agent.degree,
#                                                 y = data.ad.by.struc$agent.infected),
#
#                               name.x   = LABEL_DEGREE,
#                               limits.x = LIMITS_DEGREE,
#                               breaks.x = BREAKS_DEGREE,
#
#                               name.y   = LABEL_PROBABILITYINFECTION,
#                               limits.y = LIMITS_PROBABILITYINFECTION,
#                               breaks.y = BREAKS_PROBABILITYINFECTION,
#
#                               show.legend = SHOW_LEGEND,
#                               probability.infections = TRUE)),
#
#              paste(ep.structure, "-2-1-degree-probabilityinfection", sep = ""))
#
#
#   # clustering
#   plots <- c(plots,
#              list(plot_levels(df.1 = data.frame(x = data.ad.by.struc$net.clustering.pre.epidemic.av,
#                                                 y = data.ad.by.struc$agent.infected),
#
#                               name.x   = LABEL_CLUSTERING,
#                               limits.x = LIMITS_CLUSTERING,
#                               breaks.x = BREAKS_CLUSTERING,
#
#                               name.y   = LABEL_PROBABILITYINFECTION,
#                               limits.y = LIMITS_PROBABILITYINFECTION,
#                               breaks.y = BREAKS_PROBABILITYINFECTION,
#
#                               show.legend = SHOW_LEGEND,
#                               probability.infections = TRUE)),
#
#              paste(ep.structure, "-3-1-clustering-probabilityinfection", sep = ""))
#
#   # path length
#   plots <- c(plots,
#              list(plot_levels(df.1 = data.frame(x = data.ad.by.struc$net.pathlength.pre.epidemic.av,
#                                                 y = data.ad.by.struc$agent.infected),
#
#                               name.x   = LABEL_PATHLENGTH,
#                               limits.x = LIMITS_PATHLENGTH,
#                               breaks.x = BREAKS_PATHLENGTH,
#
#                               name.y   = LABEL_PROBABILITYINFECTION,
#                               limits.y = LIMITS_PROBABILITYINFECTION,
#                               breaks.y = BREAKS_PROBABILITYINFECTION,
#
#                               show.legend = SHOW_LEGEND,
#                               probability.infections = TRUE)),
#
#              paste(ep.structure, "-4-1-pathlength-probabilityinfection", sep = ""))
#
#   # betweenness (normalized)
#   plots <- c(plots,
#              list(plot_levels(df.1 = data.frame(x = data.ad.by.struc$agent.betweenness.normalized,
#                                                 y = data.ad.by.struc$agent.infected),
#
#                               name.x   = LABEL_BETWEENNESS,
#                               limits.x = LIMITS_BETWEENNESS,
#                               breaks.x = BREAKS_BETWEENNESS,
#
#                               name.y   = LABEL_PROBABILITYINFECTION,
#                               limits.y = LIMITS_PROBABILITYINFECTION,
#                               breaks.y = BREAKS_PROBABILITYINFECTION,
#
#                               show.legend = SHOW_LEGEND,
#                               probability.infections = TRUE)),
#
#              paste(ep.structure, "-5-1-betweenness-probabilityinfection", sep = ""))
#
#   # assortativity
#   plots <- c(plots,
#              list(plot_levels(df.1 = data.frame(x = data.ad.by.struc$net.assortativity,
#                                                 y = data.ad.by.struc$agent.infected),
#
#                               name.x   = LABEL_ASSORTATIVITY,
#                               limits.x = LIMITS_ASSORTATIVITY,
#                               breaks.x = BREAKS_ASSORTATIVITY,
#
#                               name.y   = LABEL_PROBABILITYINFECTION,
#                               limits.y = LIMITS_PROBABILITYINFECTION,
#                               breaks.y = BREAKS_PROBABILITYINFECTION,
#
#                               show.legend = SHOW_LEGEND,
#                               probability.infections = TRUE)),
#
#              paste(ep.structure, "-6-1-assortativity-probabilityinfection", sep = ""))
#
#   # sigma
#   plots <- c(plots,
#              list(plot_levels(df.1 = data.frame(x = data.ad.by.struc$nb.sigma,
#                                                 y = data.ad.by.struc$agent.infected),
#
#                               name.x   = LABEL_SIGMA,
#                               limits.x = LIMITS_SIGMA,
#                               breaks.x = BREAKS_SIGMA,
#
#                               name.y   = LABEL_PROBABILITYINFECTION,
#                               limits.y = LIMITS_PROBABILITYINFECTION,
#                               breaks.y = BREAKS_PROBABILITYINFECTION,
#
#                               show.legend = SHOW_LEGEND,
#                               probability.infections = TRUE)),
#
#              paste(ep.structure, "-7-1-sigma-probabilityinfection", sep = ""))
#
#   # gamma
#   plots <- c(plots,
#              list(plot_levels(df.1 = data.frame(x = data.ad.by.struc$nb.gamma,
#                                                 y = data.ad.by.struc$agent.infected),
#
#                               name.x   = LABEL_GAMMA,
#                               limits.x = LIMITS_GAMMA,
#                               breaks.x = BREAKS_GAMMA,
#
#                               name.y   = LABEL_PROBABILITYINFECTION,
#                               limits.y = LIMITS_PROBABILITYINFECTION,
#                               breaks.y = BREAKS_PROBABILITYINFECTION,
#
#                               show.legend = SHOW_LEGEND,
#                               probability.infections = TRUE)),
#
#              paste(ep.structure, "-8-1-gamma-probabilityinfection", sep = ""))
#
#   return(plots)
# }

get_plots_netchanges <- function(#data.ad = load_agent_details_prepared_data(),
                                 data.ss = load_simulation_summary_data(),
                                 ep.structure) {

  data.ad.by.struc <- subset(data.ad, nb.ep.structure == ep.structure)

  # risk perception
  # plots <- c(list(plot_levels(df.1 = data.frame(x = data.ad.by.struc$nb.r.sigma,
  #                                               y = data.ad.by.struc$agent.cons.broken.active.epidemic +
  #                                                 data.ad.by.struc$agent.cons.out.accepted.epidemic),
  #
  #                             name.x   = LABEL_RISKPERCEPTION,
  #                             limits.x = LIMITS_RISKPERCEPTION,
  #                             breaks.x = BREAKS_RISKPERCEPTION,
  #
  #                             name.y   = LABEL_NETDECISIONS,
  #                             limits.y = LIMITS_NETDECISIONS,
  #                             breaks.y = BREAKS_NETDECISIONS,
  #
  #                             show.legend = SHOW_LEGEND,
  #                             probability.infections = FALSE)),
  #            paste(ep.structure, "-1-0-agent-riskperception-netchanges", sep = ""))

  if (ep.structure == "dynamic") {
    plots <- c(list(plot_levels(df.1 = data.frame(x = data.ss$nb.r.sigma.av,
                                                  y = data.ss$net.ties.broken.active.epidemic +
                                                    data.ss$net.ties.out.accepted.epidemic),

                                name.x   = LABEL_RISKPERCEPTION,
                                limits.x = LIMITS_RISKPERCEPTION,
                                breaks.x = BREAKS_RISKPERCEPTION,

                                name.y   = LABEL_NETDECISIONS,
                                limits.y = LIMITS_NETDECISIONS_NETWORK_Y,
                                breaks.y = BREAKS_NETDECISIONS_NETWORK_Y,

                                show.legend = SHOW_LEGEND,
                                probability.infections = FALSE)),
               paste(ep.structure, "-8-1-network-riskperception-netchanges", sep = ""))
  }

  if (ep.structure == "static") {
    plots <- c(list(plot_levels(df.1 = data.frame(x = data.ss$nb.r.sigma.av,
                                                  y = data.ss$net.static.ties.broken.active.epidemic +
                                                    data.ss$net.static.ties.out.accepted.epidemic),

                                name.x   = LABEL_RISKPERCEPTION,
                                limits.x = LIMITS_RISKPERCEPTION,
                                breaks.x = BREAKS_RISKPERCEPTION,

                                name.y   = LABEL_NETDECISIONS,
                                limits.y = LIMITS_NETDECISIONS_NETWORK_Y,
                                breaks.y = BREAKS_NETDECISIONS_NETWORK_Y,

                                show.legend = SHOW_LEGEND,
                                probability.infections = FALSE)),
               paste(ep.structure, "-8-1-network-riskperception-netchanges", sep = ""))
  }

  # degree
  # plots <- c(plots,
  #            list(plot_levels(df.1 = data.frame(x = data.ad.by.struc$net.degree.pre.epidemic.av,
  #                                               y = data.ad.by.struc$agent.cons.broken.active.epidemic +
  #                                                 data.ad.by.struc$agent.cons.out.accepted.epidemic),
  #
  #                             name.x   = LABEL_DEGREE,
  #                             limits.x = LIMITS_DEGREE,
  #                             breaks.x = BREAKS_DEGREE,
  #
  #                             name.y   = LABEL_NETDECISIONS,
  #                             limits.y = LIMITS_NETDECISIONS,
  #                             breaks.y = BREAKS_NETDECISIONS,
  #
  #                             show.legend = SHOW_LEGEND,
  #                             probability.infections = FALSE)),
  #            paste(ep.structure, "-2-0-agent-degree-netchanges", sep = ""))

  if (ep.structure == "dynamic") {
    plots <- c(plots,
               list(plot_levels(df.1 = data.frame(x = data.ss$net.degree.pre.epidemic.av,
                                                  y = data.ss$net.ties.broken.active.epidemic +
                                                    data.ss$net.ties.out.accepted.epidemic),

                                name.x   = LABEL_DEGREE,
                                limits.x = LIMITS_DEGREE,
                                breaks.x = BREAKS_DEGREE,

                                name.y   = LABEL_NETDECISIONS,
                                limits.y = LIMITS_NETDECISIONS_NETWORK_Y,
                                breaks.y = BREAKS_NETDECISIONS_NETWORK_Y,

                                show.legend = SHOW_LEGEND,
                                probability.infections = FALSE)),
               paste(ep.structure, "-2-1-network-degree-netchanges", sep = ""))
  }

  if (ep.structure == "static") {
    plots <- c(plots,
               list(plot_levels(df.1 = data.frame(x = data.ss$net.degree.pre.epidemic.av,
                                                  y = data.ss$net.static.ties.broken.active.epidemic +
                                                    data.ss$net.static.ties.out.accepted.epidemic),

                                name.x   = LABEL_DEGREE,
                                limits.x = LIMITS_DEGREE,
                                breaks.x = BREAKS_DEGREE,

                                name.y   = LABEL_NETDECISIONS,
                                limits.y = LIMITS_NETDECISIONS_NETWORK_Y,
                                breaks.y = BREAKS_NETDECISIONS_NETWORK_Y,

                                show.legend = SHOW_LEGEND,
                                probability.infections = FALSE)),
               paste(ep.structure, "-2-1-network-degree-netchanges", sep = ""))
  }

  # clustering
  # plots <- c(plots,
  #            list(plot_levels(df.1 = data.frame(x = data.ad.by.struc$net.clustering.pre.epidemic.av,
  #                                               y = data.ad.by.struc$agent.cons.broken.active.epidemic +
  #                                                 data.ad.by.struc$agent.cons.out.accepted.epidemic),
  #
  #                             name.x   = LABEL_CLUSTERING,
  #                             limits.x = LIMITS_CLUSTERING,
  #                             breaks.x = BREAKS_CLUSTERING,
  #
  #                             name.y   = LABEL_NETDECISIONS,
  #                             limits.y = LIMITS_NETDECISIONS,
  #                             breaks.y = BREAKS_NETDECISIONS,
  #
  #                             show.legend = SHOW_LEGEND,
  #                             probability.infections = FALSE)),
  #            paste(ep.structure, "-3-0-agent-clustering-netchanges", sep = ""))

  if (ep.structure == "dynamic") {
    plots <- c(plots,
               list(plot_levels(df.1 = data.frame(x = data.ss$net.clustering.pre.epidemic.av,
                                                  y = data.ss$net.ties.broken.active.epidemic +
                                                    data.ss$net.ties.out.accepted.epidemic),

                                name.x   = LABEL_CLUSTERING,
                                limits.x = LIMITS_CLUSTERING,
                                breaks.x = BREAKS_CLUSTERING,

                                name.y   = LABEL_NETDECISIONS,
                                limits.y = LIMITS_NETDECISIONS_NETWORK_Y,
                                breaks.y = BREAKS_NETDECISIONS_NETWORK_Y,

                                show.legend = SHOW_LEGEND,
                                probability.infections = FALSE)),
               paste(ep.structure, "-3-1-network-clustering-netchanges", sep = ""))
  }

  if (ep.structure == "static") {
    plots <- c(plots,
               list(plot_levels(df.1 = data.frame(x = data.ss$net.clustering.pre.epidemic.av,
                                                  y = data.ss$net.static.ties.broken.active.epidemic +
                                                    data.ss$net.static.ties.out.accepted.epidemic),

                                name.x   = LABEL_CLUSTERING,
                                limits.x = LIMITS_CLUSTERING,
                                breaks.x = BREAKS_CLUSTERING,

                                name.y   = LABEL_NETDECISIONS,
                                limits.y = LIMITS_NETDECISIONS_NETWORK_Y,
                                breaks.y = BREAKS_NETDECISIONS_NETWORK_Y,

                                show.legend = SHOW_LEGEND,
                                probability.infections = FALSE)),
               paste(ep.structure, "-3-1-network-clustering-netchanges", sep = ""))
  }

  # path length
  # plots <- c(plots,
  #            list(plot_levels(df.1 = data.frame(x = data.ad.by.struc$net.pathlength.pre.epidemic.av,
  #                                               y = data.ad.by.struc$agent.cons.broken.active.epidemic +
  #                                                 data.ad.by.struc$agent.cons.out.accepted.epidemic),
  #
  #                             name.x   = LABEL_PATHLENGTH,
  #                             limits.x = LIMITS_PATHLENGTH,
  #                             breaks.x = BREAKS_PATHLENGTH,
  #
  #                             name.y   = LABEL_NETDECISIONS,
  #                             limits.y = LIMITS_NETDECISIONS,
  #                             breaks.y = BREAKS_NETDECISIONS,
  #
  #                             show.legend = SHOW_LEGEND,
  #                             probability.infections = FALSE)),
  #            paste(ep.structure, "-4-0-agent-pathlength-netchanges", sep = ""))

  if (ep.structure == "dynamic") {
    plots <- c(plots,
               list(plot_levels(df.1 = data.frame(x = data.ss$net.pathlength.pre.epidemic.av,
                                                  y = data.ss$net.ties.broken.active.epidemic +
                                                    data.ss$net.ties.out.accepted.epidemic),

                                name.x   = LABEL_PATHLENGTH,
                                limits.x = LIMITS_PATHLENGTH,
                                breaks.x = BREAKS_PATHLENGTH,

                                name.y   = LABEL_NETDECISIONS,
                                limits.y = LIMITS_NETDECISIONS_NETWORK_Y,
                                breaks.y = BREAKS_NETDECISIONS_NETWORK_Y,

                                show.legend = SHOW_LEGEND,
                                probability.infections = FALSE)),
               paste(ep.structure, "-4-1-network-pathlength-netchanges", sep = ""))
  }

  if (ep.structure == "static") {
    plots <- c(plots,
               list(plot_levels(df.1 = data.frame(x = data.ss$net.pathlength.pre.epidemic.av,
                                                  y = data.ss$net.static.ties.broken.active.epidemic +
                                                    data.ss$net.static.ties.out.accepted.epidemic),

                                name.x   = LABEL_PATHLENGTH,
                                limits.x = LIMITS_PATHLENGTH,
                                breaks.x = BREAKS_PATHLENGTH,

                                name.y   = LABEL_NETDECISIONS,
                                limits.y = LIMITS_NETDECISIONS_NETWORK_Y,
                                breaks.y = BREAKS_NETDECISIONS_NETWORK_Y,

                                show.legend = SHOW_LEGEND,
                                probability.infections = FALSE)),
               paste(ep.structure, "-4-1-network-pathlength-netchanges", sep = ""))
  }

  # betweenness (normalized)
  # plots <- c(plots,
  #            list(plot_levels(df.1 = data.frame(x = data.ad.by.struc$index.betweenness.normalized,
  #                                               y = data.ad.by.struc$agent.cons.broken.active.epidemic +
  #                                                 data.ad.by.struc$agent.cons.out.accepted.epidemic),
  #
  #                             name.x   = LABEL_BETWEENNESS,
  #                             limits.x = LIMITS_BETWEENNESS,
  #                             breaks.x = BREAKS_BETWEENNESS,
  #
  #                             name.y   = LABEL_NETDECISIONS,
  #                             limits.y = LIMITS_NETDECISIONS,
  #                             breaks.y = BREAKS_NETDECISIONS,
  #
  #                             show.legend = SHOW_LEGEND,
  #                             probability.infections = FALSE)),
  #            paste(ep.structure, "-5-0-agent-betweenness-netchanges", sep = ""))

  # if (ep.structure == "dynamic") {
  #   plots <- c(plots,
  #              list(plot_levels(df.1 = data.frame(x = data.ss$index.betweenness.normalized,
  #                                                 y = data.ss$net.ties.broken.active.epidemic +
  #                                                   data.ss$net.ties.out.accepted.epidemic),
  #
  #                               name.x   = LABEL_BETWEENNESS,
  #                               limits.x = LIMITS_BETWEENNESS,
  #                               breaks.x = BREAKS_BETWEENNESS,
  #
  #                               name.y   = LABEL_NETDECISIONS,
  #                               limits.y = LIMITS_NETDECISIONS_NETWORK_Y,
  #                               breaks.y = BREAKS_NETDECISIONS_NETWORK_Y,
  #
  #                               show.legend = SHOW_LEGEND,
  #                               probability.infections = FALSE)),
  #              paste(ep.structure, "-5-1-network-betweenness-netchanges", sep = ""))
  # }
  #
  # if (ep.structure == "static") {
  #   plots <- c(plots,
  #              list(plot_levels(df.1 = data.frame(x = data.ss$index.betweenness.normalized,
  #                                                 y = data.ss$net.static.ties.broken.active.epidemic +
  #                                                   data.ss$net.static.ties.out.accepted.epidemic),
  #
  #                               name.x   = LABEL_BETWEENNESS,
  #                               limits.x = LIMITS_BETWEENNESS,
  #                               breaks.x = BREAKS_BETWEENNESS,
  #
  #                               name.y   = LABEL_NETDECISIONS,
  #                               limits.y = LIMITS_NETDECISIONS_NETWORK_Y,
  #                               breaks.y = BREAKS_NETDECISIONS_NETWORK_Y,
  #
  #                               show.legend = SHOW_LEGEND,
  #                               probability.infections = FALSE)),
  #              paste(ep.structure, "-5-1-network-betweenness-netchanges", sep = ""))
  # }

  # assortativity
  # plots <- c(plots,
  #            list(plot_levels(df.1 = data.frame(x = data.ad.by.struc$net.assortativity.pre.epidemic,
  #                                               y = data.ad.by.struc$agent.cons.broken.active.epidemic +
  #                                                 data.ad.by.struc$agent.cons.out.accepted.epidemic),
  #
  #                             name.x   = LABEL_ASSORTATIVITY,
  #                             limits.x = LIMITS_ASSORTATIVITY,
  #                             breaks.x = BREAKS_ASSORTATIVITY,
  #
  #                             name.y   = LABEL_NETDECISIONS,
  #                             limits.y = LIMITS_NETDECISIONS,
  #                             breaks.y = BREAKS_NETDECISIONS,
  #
  #                             show.legend = SHOW_LEGEND,
  #                             probability.infections = FALSE)),
  #            paste(ep.structure, "-6-0-agent-assortativity-netchanges", sep = ""))

  if (ep.structure == "dynamic") {
    plots <- c(plots,
               list(plot_levels(df.1 = data.frame(x = data.ss$net.assortativity.pre.epidemic,
                                                  y = data.ss$net.ties.broken.active.epidemic +
                                                    data.ss$net.ties.out.accepted.epidemic),

                                name.x   = LABEL_ASSORTATIVITY,
                                limits.x = LIMITS_ASSORTATIVITY,
                                breaks.x = BREAKS_ASSORTATIVITY,

                                name.y   = LABEL_NETDECISIONS,
                                limits.y = LIMITS_NETDECISIONS_NETWORK_Y,
                                breaks.y = BREAKS_NETDECISIONS_NETWORK_Y,

                                show.legend = SHOW_LEGEND,
                                probability.infections = FALSE)),
               paste(ep.structure, "-5-1-network-assortativity-netchanges", sep = ""))
  }

  if (ep.structure == "static") {
    plots <- c(plots,
               list(plot_levels(df.1 = data.frame(x = data.ss$net.assortativity.pre.epidemic,
                                                  y = data.ss$net.static.ties.broken.active.epidemic +
                                                    data.ss$net.static.ties.out.accepted.epidemic),

                                name.x   = LABEL_ASSORTATIVITY,
                                limits.x = LIMITS_ASSORTATIVITY,
                                breaks.x = BREAKS_ASSORTATIVITY,

                                name.y   = LABEL_NETDECISIONS,
                                limits.y = LIMITS_NETDECISIONS_NETWORK_Y,
                                breaks.y = BREAKS_NETDECISIONS_NETWORK_Y,

                                show.legend = SHOW_LEGEND,
                                probability.infections = FALSE)),
               paste(ep.structure, "-5-1-network-assortativity-netchanges", sep = ""))
  }

  # sigma
  # plots <- c(plots,
  #            list(plot_levels(df.1 = data.frame(x = data.ad.by.struc$nb.sigma,
  #                                               y = data.ad.by.struc$agent.cons.broken.active.epidemic +
  #                                                 data.ad.by.struc$agent.cons.out.accepted.epidemic),
  #
  #                             name.x   = LABEL_SIGMA,
  #                             limits.x = LIMITS_SIGMA,
  #                             breaks.x = BREAKS_SIGMA,
  #
  #                             name.y   = LABEL_NETDECISIONS,
  #                             limits.y = LIMITS_NETDECISIONS,
  #                             breaks.y = BREAKS_NETDECISIONS,
  #
  #                             show.legend = SHOW_LEGEND,
  #                             probability.infections = FALSE)),
  #            paste(ep.structure, "-7-0-agent-sigma-netchanges", sep = ""))

  if (ep.structure == "dynamic") {
    plots <- c(plots,
               list(plot_levels(df.1 = data.frame(x = data.ss$nb.sigma,
                                                  y = data.ss$net.ties.broken.active.epidemic +
                                                    data.ss$net.ties.out.accepted.epidemic),

                                name.x   = LABEL_SIGMA,
                                limits.x = LIMITS_SIGMA,
                                breaks.x = BREAKS_SIGMA,

                                name.y   = LABEL_NETDECISIONS,
                                limits.y = LIMITS_NETDECISIONS_NETWORK_Y,
                                breaks.y = BREAKS_NETDECISIONS_NETWORK_Y,

                                show.legend = SHOW_LEGEND,
                                probability.infections = FALSE)),
               paste(ep.structure, "-6-1-network-sigma-netchanges", sep = ""))
  }

  if (ep.structure == "static") {
    plots <- c(plots,
               list(plot_levels(df.1 = data.frame(x = data.ss$nb.sigma,
                                                  y = data.ss$net.static.ties.broken.active.epidemic +
                                                    data.ss$net.static.ties.out.accepted.epidemic),

                                name.x   = LABEL_SIGMA,
                                limits.x = LIMITS_SIGMA,
                                breaks.x = BREAKS_SIGMA,

                                name.y   = LABEL_NETDECISIONS,
                                limits.y = LIMITS_NETDECISIONS_NETWORK_Y,
                                breaks.y = BREAKS_NETDECISIONS_NETWORK_Y,

                                show.legend = SHOW_LEGEND,
                                probability.infections = FALSE)),
               paste(ep.structure, "-6-1-network-sigma-netchanges", sep = ""))
  }

  # gamma
  # plots <- c(plots,
  #            list(plot_levels(df.1 = data.frame(x = data.ad.by.struc$nb.gamma,
  #                                               y = data.ad.by.struc$agent.cons.broken.active.epidemic +
  #                                                 data.ad.by.struc$agent.cons.out.accepted.epidemic),
  #
  #                             name.x   = LABEL_GAMMA,
  #                             limits.x = LIMITS_GAMMA,
  #                             breaks.x = BREAKS_GAMMA,
  #
  #                             name.y   = LABEL_NETDECISIONS,
  #                             limits.y = LIMITS_NETDECISIONS,
  #                             breaks.y = BREAKS_NETDECISIONS,
  #
  #                             show.legend = SHOW_LEGEND,
  #                             probability.infections = FALSE)),
  #            paste(ep.structure, "-8-0-agent-gamma-netchanges", sep = ""))

  if (ep.structure == "dynamic") {
    plots <- c(plots,
               list(plot_levels(df.1 = data.frame(x = data.ss$nb.gamma,
                                                  y = data.ss$net.ties.broken.active.epidemic +
                                                    data.ss$net.ties.out.accepted.epidemic),

                                name.x   = LABEL_GAMMA,
                                limits.x = LIMITS_GAMMA,
                                breaks.x = BREAKS_GAMMA,

                                name.y   = LABEL_NETDECISIONS,
                                limits.y = LIMITS_NETDECISIONS_NETWORK_Y,
                                breaks.y = BREAKS_NETDECISIONS_NETWORK_Y,

                                show.legend = SHOW_LEGEND,
                                probability.infections = FALSE)),
               paste(ep.structure, "-7-1-network-gamma-netchanges", sep = ""))
  }

  if (ep.structure == "static") {
    plots <- c(plots,
               list(plot_levels(df.1 = data.frame(x = data.ss$nb.gamma,
                                                  y = data.ss$net.static.ties.broken.active.epidemic +
                                                    data.ss$net.static.ties.out.accepted.epidemic),

                                name.x   = LABEL_GAMMA,
                                limits.x = LIMITS_GAMMA,
                                breaks.x = BREAKS_GAMMA,

                                name.y   = LABEL_NETDECISIONS,
                                limits.y = LIMITS_NETDECISIONS_NETWORK_Y,
                                breaks.y = BREAKS_NETDECISIONS_NETWORK_Y,

                                show.legend = SHOW_LEGEND,
                                probability.infections = FALSE)),
               paste(ep.structure, "-7-1-network-gamma-netchanges", sep = ""))
  }

  return(plots)
}

get_plots_attackrate <- function(data.ss = load_simulation_summary_data()) {

  # net changes
  plots <- c(list(plot_levels(df.1 = data.frame(x = data.ss$net.ties.broken.active.epidemic +
                                                  data.ss$net.ties.out.accepted.epidemic,
                                                y = data.ss$net.pct.rec),

                              name.x   = LABEL_NETDECISIONS,
                              limits.x = LIMITS_NETDECISIONS_NETWORK,
                              breaks.x = BREAKS_NETDECISIONS_NETWORK,

                              name.y   = LABEL_ATTACKRATE,
                              limits.y = LIMITS_ATTACKRATE,
                              breaks.y = BREAKS_ATTACKRATE,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "dynamic-1-2-netchanges-attackrate")

  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss$net.static.ties.broken.active.epidemic +
                                                  data.ss$net.static.ties.out.accepted.epidemic,
                                                y = data.ss$net.static.pct.rec),

                              name.x   = LABEL_NETDECISIONS,
                              limits.x = LIMITS_NETDECISIONS_NETWORK,
                              breaks.x = BREAKS_NETDECISIONS_NETWORK,

                              name.y   = LABEL_ATTACKRATE,
                              limits.y = LIMITS_ATTACKRATE,
                              breaks.y = BREAKS_ATTACKRATE,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "static-1-2-netchanges-attackrate")

  # risk perception
  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss$nb.r.sigma.av,
                                                y = data.ss$net.pct.rec),
                              # df.2 = data.frame(x = data.ss$index.r.sigma.neighborhood,
                              #                   y = data.ss$net.pct.rec),

                              name.x   = LABEL_RISKPERCEPTION,
                              limits.x = LIMITS_RISKPERCEPTION,
                              breaks.x = BREAKS_RISKPERCEPTION,

                              name.y   = LABEL_ATTACKRATE,
                              limits.y = LIMITS_ATTACKRATE,
                              breaks.y = BREAKS_ATTACKRATE,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "dynamic-8-2-riskperception-attackrate")

  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss$nb.r.sigma.av,
                                                y = data.ss$net.static.pct.rec),
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
             "static-8-2-riskperception-attackrate")

  # degree
  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss$net.degree.pre.epidemic.av,
                                                y = data.ss$net.pct.rec),

                              name.x   = LABEL_DEGREE,
                              limits.x = LIMITS_DEGREE,
                              breaks.x = BREAKS_DEGREE,

                              name.y   = LABEL_ATTACKRATE,
                              limits.y = LIMITS_ATTACKRATE,
                              breaks.y = BREAKS_ATTACKRATE,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "dynamic-2-2-degree-attackrate")

  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss$net.degree.pre.epidemic.av,
                                                y = data.ss$net.static.pct.rec),

                              name.x   = LABEL_DEGREE,
                              limits.x = LIMITS_DEGREE,
                              breaks.x = BREAKS_DEGREE,

                              name.y   = LABEL_ATTACKRATE,
                              limits.y = LIMITS_ATTACKRATE,
                              breaks.y = BREAKS_ATTACKRATE,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "static-2-2-degree-attackrate")

  # clustering
  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss$net.clustering.pre.epidemic.av,
                                                y = data.ss$net.pct.rec),
                              # df.2 = data.frame(x = data.ss$index.clustering,
                              #                   y = data.ss$net.pct.rec),

                              name.x   = LABEL_CLUSTERING,
                              limits.x = LIMITS_CLUSTERING,
                              breaks.x = BREAKS_CLUSTERING,

                              name.y   = LABEL_ATTACKRATE,
                              limits.y = LIMITS_ATTACKRATE,
                              breaks.y = BREAKS_ATTACKRATE,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "dynamic-3-2-clustering-attackrate")

  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss$net.clustering.pre.epidemic.av,
                                                y = data.ss$net.static.pct.rec),
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
             "static-3-2-clustering-attackrate")

  # path length
  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss$net.pathlength.pre.epidemic.av,
                                                y = data.ss$net.pct.rec),

                              name.x   = LABEL_PATHLENGTH,
                              limits.x = LIMITS_PATHLENGTH,
                              breaks.x = BREAKS_PATHLENGTH,

                              name.y   = LABEL_ATTACKRATE,
                              limits.y = LIMITS_ATTACKRATE,
                              breaks.y = BREAKS_ATTACKRATE,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "dynamic-4-2-pathlength-attackrate")

  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss$net.pathlength.pre.epidemic.av,
                                                y = data.ss$net.static.pct.rec),

                              name.x   = LABEL_PATHLENGTH,
                              limits.x = LIMITS_PATHLENGTH,
                              breaks.x = BREAKS_PATHLENGTH,

                              name.y   = LABEL_ATTACKRATE,
                              limits.y = LIMITS_ATTACKRATE,
                              breaks.y = BREAKS_ATTACKRATE,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "static-4-2-pathlength-attackrate")

  # # betweenness (normalized)
  # plots <- c(plots,
  #            list(plot_levels(df.1 = data.frame(x = data.ss$index.betweenness.normalized,
  #                                               y = data.ss$net.pct.rec),
  #
  #                             name.x   = LABEL_BETWEENNESS,
  #                             limits.x = LIMITS_BETWEENNESS,
  #                             breaks.x = BREAKS_BETWEENNESS,
  #
  #                             name.y   = LABEL_ATTACKRATE,
  #                             limits.y = LIMITS_ATTACKRATE,
  #                             breaks.y = BREAKS_ATTACKRATE,
  #
  #                             show.legend = SHOW_LEGEND,
  #                             probability.infections = FALSE)),
  #            "dynamic-5-2-betweenness-attackrate")
  #
  # plots <- c(plots,
  #            list(plot_levels(df.1 = data.frame(x = data.ss$index.betweenness.normalized,
  #                                               y = data.ss$net.static.pct.rec),
  #
  #                             name.x   = LABEL_BETWEENNESS,
  #                             limits.x = LIMITS_BETWEENNESS,
  #                             breaks.x = BREAKS_BETWEENNESS,
  #
  #                             name.y   = LABEL_ATTACKRATE,
  #                             limits.y = LIMITS_ATTACKRATE,
  #                             breaks.y = BREAKS_ATTACKRATE,
  #
  #                             show.legend = SHOW_LEGEND,
  #                             probability.infections = FALSE)),
  #            "static-5-2-betweenness-attackrate")

  # assortativity
  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss$net.assortativity.pre.epidemic,
                                                y = data.ss$net.pct.rec),

                              name.x   = LABEL_ASSORTATIVITY,
                              limits.x = LIMITS_ASSORTATIVITY,
                              breaks.x = BREAKS_ASSORTATIVITY,

                              name.y   = LABEL_ATTACKRATE,
                              limits.y = LIMITS_ATTACKRATE,
                              breaks.y = BREAKS_ATTACKRATE,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "dynamic-5-2-assortativity-attackrate")

  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss$net.assortativity.pre.epidemic,
                                                y = data.ss$net.static.pct.rec),

                              name.x   = LABEL_ASSORTATIVITY,
                              limits.x = LIMITS_ASSORTATIVITY,
                              breaks.x = BREAKS_ASSORTATIVITY,

                              name.y   = LABEL_ATTACKRATE,
                              limits.y = LIMITS_ATTACKRATE,
                              breaks.y = BREAKS_ATTACKRATE,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "static-5-2-assortativity-attackrate")

  # sigma
  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss$nb.sigma,
                                                y = data.ss$net.pct.rec),

                              name.x   = LABEL_SIGMA,
                              limits.x = LIMITS_SIGMA,
                              breaks.x = BREAKS_SIGMA,

                              name.y   = LABEL_ATTACKRATE,
                              limits.y = LIMITS_ATTACKRATE,
                              breaks.y = BREAKS_ATTACKRATE,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "dynamic-6-2-sigma-attackrate")

  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss$nb.sigma,
                                                y = data.ss$net.static.pct.rec),

                              name.x   = LABEL_SIGMA,
                              limits.x = LIMITS_SIGMA,
                              breaks.x = BREAKS_SIGMA,

                              name.y   = LABEL_ATTACKRATE,
                              limits.y = LIMITS_ATTACKRATE,
                              breaks.y = BREAKS_ATTACKRATE,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "static-6-2-sigma-attackrate")

  # gamma
  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss$nb.gamma,
                                                y = data.ss$net.pct.rec),

                              name.x   = LABEL_GAMMA,
                              limits.x = LIMITS_GAMMA,
                              breaks.x = BREAKS_GAMMA,

                              name.y   = LABEL_ATTACKRATE,
                              limits.y = LIMITS_ATTACKRATE,
                              breaks.y = BREAKS_ATTACKRATE,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "dynamic-7-2-gamma-attackrate")

  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss$nb.gamma,
                                                y = data.ss$net.static.pct.rec),

                              name.x   = LABEL_GAMMA,
                              limits.x = LIMITS_GAMMA,
                              breaks.x = BREAKS_GAMMA,

                              name.y   = LABEL_ATTACKRATE,
                              limits.y = LIMITS_ATTACKRATE,
                              breaks.y = BREAKS_ATTACKRATE,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "static-7-2-gamma-attackrate")

  return(plots)
}


get_plots_duration <- function(data.ss = load_simulation_summary_data()) {

  data.ss.arsmall.dyn <- subset(data.ss, net.pct.rec <= CUT_OFF_SMALL_ATTACK_RATE)
  data.ss.arlarge.dyn <- subset(data.ss, net.pct.rec >= CUT_OFF_LARGE_ATTACK_RATE)
  data.ss.armid.dyn   <- subset(data.ss, net.pct.rec > CUT_OFF_SMALL_ATTACK_RATE &
                                  net.pct.rec < CUT_OFF_LARGE_ATTACK_RATE)

  data.ss.arsmall.stat <- subset(data.ss, net.static.pct.rec <= CUT_OFF_SMALL_ATTACK_RATE)
  data.ss.arlarge.stat <- subset(data.ss, net.static.pct.rec >= CUT_OFF_LARGE_ATTACK_RATE)
  data.ss.armid.stat   <- subset(data.ss, net.static.pct.rec > CUT_OFF_SMALL_ATTACK_RATE &
                                   net.static.pct.rec < CUT_OFF_LARGE_ATTACK_RATE)

  # net changes
  plots <- c(list(plot_levels(df.1 = data.frame(x = data.ss.armid.dyn$net.ties.broken.active.epidemic +
                                                  data.ss.armid.dyn$net.ties.out.accepted.epidemic,
                                                y = data.ss.armid.dyn$net.epidemic.duration),

                              df.2 = data.frame(x = data.ss.arsmall.dyn$net.ties.broken.active.epidemic +
                                                  data.ss.arsmall.dyn$net.ties.out.accepted.epidemic,
                                                y = data.ss.arsmall.dyn$net.epidemic.duration),

                              df.3 = data.frame(x = data.ss.arlarge.dyn$net.ties.broken.active.epidemic +
                                                  data.ss.arlarge.dyn$net.ties.out.accepted.epidemic,
                                                y = data.ss.arlarge.dyn$net.epidemic.duration),

                              name.x   = LABEL_NETDECISIONS,
                              limits.x = LIMITS_NETDECISIONS_NETWORK,
                              breaks.x = BREAKS_NETDECISIONS_NETWORK,

                              name.y   = LABEL_DURATION,
                              limits.y = LIMITS_DURATIONPEAK,
                              breaks.y = BREAKS_DURATIONPEAK,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "dynamic-1-3-netchanges-duration")

  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss.armid.stat$net.static.ties.broken.active.epidemic +
                                                  data.ss.armid.stat$net.static.ties.out.accepted.epidemic,
                                                y = data.ss.armid.stat$net.epidemic.duration),

                              df.2 = data.frame(x = data.ss.arsmall.stat$net.static.ties.broken.active.epidemic +
                                                  data.ss.arsmall.stat$net.static.ties.out.accepted.epidemic,
                                                y = data.ss.arsmall.stat$net.epidemic.duration),

                              df.3 = data.frame(x = data.ss.arlarge.stat$net.static.ties.broken.active.epidemic +
                                                  data.ss.arlarge.stat$net.static.ties.out.accepted.epidemic,
                                                y = data.ss.arlarge.stat$net.epidemic.duration),

                              name.x   = LABEL_NETDECISIONS,
                              limits.x = LIMITS_NETDECISIONS_NETWORK,
                              breaks.x = BREAKS_NETDECISIONS_NETWORK,

                              name.y   = LABEL_DURATION,
                              limits.y = LIMITS_DURATIONPEAK,
                              breaks.y = BREAKS_DURATIONPEAK,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "static-1-3-netchanges-duration")

  # risk perception
  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss.armid.dyn$nb.r.sigma.av,
                                                y = data.ss.armid.dyn$net.epidemic.duration),

                              df.2 = data.frame(x = data.ss.arsmall.dyn$nb.r.sigma.av,
                                                y = data.ss.arsmall.dyn$net.epidemic.duration),

                              df.3 = data.frame(x = data.ss.arlarge.dyn$nb.r.sigma.av,
                                                y = data.ss.arlarge.dyn$net.epidemic.duration),

                              name.x   = LABEL_RISKPERCEPTION,
                              limits.x = LIMITS_RISKPERCEPTION,
                              breaks.x = BREAKS_RISKPERCEPTION,

                              name.y   = LABEL_DURATION,
                              limits.y = LIMITS_DURATIONPEAK,
                              breaks.y = BREAKS_DURATIONPEAK,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "dynamic-8-3-riskperception-duration")

  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss.armid.stat$nb.r.sigma.av,
                                                y = data.ss.armid.stat$net.static.epidemic.duration),

                              df.2 = data.frame(x = data.ss.arsmall.stat$nb.r.sigma.av,
                                                y = data.ss.arsmall.stat$net.static.epidemic.duration),

                              df.3 = data.frame(x = data.ss.arlarge.stat$nb.r.sigma.av,
                                                y = data.ss.arlarge.stat$net.static.epidemic.duration),

                              name.x   = LABEL_RISKPERCEPTION,
                              limits.x = LIMITS_RISKPERCEPTION,
                              breaks.x = BREAKS_RISKPERCEPTION,

                              name.y   = LABEL_DURATION,
                              limits.y = LIMITS_DURATIONPEAK,
                              breaks.y = BREAKS_DURATIONPEAK,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "static-8-3-riskperception-duration")

  # degree
  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss.armid.dyn$net.degree.pre.epidemic.av,
                                                y = data.ss.armid.dyn$net.epidemic.duration),

                              df.2 = data.frame(x = data.ss.arsmall.dyn$net.degree.pre.epidemic.av,
                                                y = data.ss.arsmall.dyn$net.epidemic.duration),

                              df.3 = data.frame(x = data.ss.arlarge.dyn$net.degree.pre.epidemic.av,
                                                y = data.ss.arlarge.dyn$net.epidemic.duration),

                              name.x   = LABEL_DEGREE,
                              limits.x = LIMITS_DEGREE,
                              breaks.x = BREAKS_DEGREE,

                              name.y   = LABEL_DURATION,
                              limits.y = LIMITS_DURATIONPEAK,
                              breaks.y = BREAKS_DURATIONPEAK,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "dynamic-2-3-degree-duration")

  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss.armid.stat$net.degree.pre.epidemic.av,
                                                y = data.ss.armid.stat$net.static.epidemic.duration),

                              df.2 = data.frame(x = data.ss.arsmall.stat$net.degree.pre.epidemic.av,
                                                y = data.ss.arsmall.stat$net.static.epidemic.duration),

                              df.3 = data.frame(x = data.ss.arlarge.stat$net.degree.pre.epidemic.av,
                                                y = data.ss.arlarge.stat$net.static.epidemic.duration),

                              name.x   = LABEL_DEGREE,
                              limits.x = LIMITS_DEGREE,
                              breaks.x = BREAKS_DEGREE,

                              name.y   = LABEL_DURATION,
                              limits.y = LIMITS_DURATIONPEAK,
                              breaks.y = BREAKS_DURATIONPEAK,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "static-2-3-degree-duration")

  # clustering
  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss.armid.dyn$net.clustering.pre.epidemic.av,
                                                y = data.ss.armid.dyn$net.epidemic.duration),

                              df.2 = data.frame(x = data.ss.arsmall.dyn$net.clustering.pre.epidemic.av,
                                                y = data.ss.arsmall.dyn$net.epidemic.duration),

                              df.3 = data.frame(x = data.ss.arlarge.dyn$net.clustering.pre.epidemic.av,
                                                y = data.ss.arlarge.dyn$net.epidemic.duration),

                              name.x   = LABEL_CLUSTERING,
                              limits.x = LIMITS_CLUSTERING,
                              breaks.x = BREAKS_CLUSTERING,

                              name.y   = LABEL_DURATION,
                              limits.y = LIMITS_DURATIONPEAK,
                              breaks.y = BREAKS_DURATIONPEAK,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "dynamic-3-3-clustering-duration")

  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss.armid.stat$net.clustering.pre.epidemic.av,
                                                y = data.ss.armid.stat$net.static.epidemic.duration),

                              df.2 = data.frame(x = data.ss.arsmall.stat$net.clustering.pre.epidemic.av,
                                                y = data.ss.arsmall.stat$net.static.epidemic.duration),

                              df.3 = data.frame(x = data.ss.arlarge.stat$net.clustering.pre.epidemic.av,
                                                y = data.ss.arlarge.stat$net.static.epidemic.duration),

                              name.x   = LABEL_CLUSTERING,
                              limits.x = LIMITS_CLUSTERING,
                              breaks.x = BREAKS_CLUSTERING,

                              name.y   = LABEL_DURATION,
                              limits.y = LIMITS_DURATIONPEAK,
                              breaks.y = BREAKS_DURATIONPEAK,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "static-3-3-clustering-duration")

  # path length
  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss.armid.dyn$net.pathlength.pre.epidemic.av,
                                                y = data.ss.armid.dyn$net.epidemic.duration),

                              df.2 = data.frame(x = data.ss.arsmall.dyn$net.pathlength.pre.epidemic.av,
                                                y = data.ss.arsmall.dyn$net.epidemic.duration),

                              df.3 = data.frame(x = data.ss.arlarge.dyn$net.pathlength.pre.epidemic.av,
                                                y = data.ss.arlarge.dyn$net.epidemic.duration),

                              name.x   = LABEL_PATHLENGTH,
                              limits.x = LIMITS_PATHLENGTH,
                              breaks.x = BREAKS_PATHLENGTH,

                              name.y   = LABEL_DURATION,
                              limits.y = LIMITS_DURATIONPEAK,
                              breaks.y = BREAKS_DURATIONPEAK,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "dynamic-4-3-pathlength-duration")

  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss.armid.stat$net.pathlength.pre.epidemic.av,
                                                y = data.ss.armid.stat$net.static.epidemic.duration),

                              df.2 = data.frame(x = data.ss.arsmall.stat$net.pathlength.pre.epidemic.av,
                                                y = data.ss.arsmall.stat$net.static.epidemic.duration),

                              df.3 = data.frame(x = data.ss.arlarge.stat$net.pathlength.pre.epidemic.av,
                                                y = data.ss.arlarge.stat$net.static.epidemic.duration),

                              name.x   = LABEL_PATHLENGTH,
                              limits.x = LIMITS_PATHLENGTH,
                              breaks.x = BREAKS_PATHLENGTH,

                              name.y   = LABEL_DURATION,
                              limits.y = LIMITS_DURATIONPEAK,
                              breaks.y = BREAKS_DURATIONPEAK,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "static-4-3-pathlength-duration")

  # # betweenness (normalized)
  # plots <- c(plots,
  #            list(plot_levels(df.1 = data.frame(x = data.ss$index.betweenness.normalized,
  #                                               y = data.ss$net.epidemic.duration),
  #
  #                             df.2 = data.frame(x = data.ss.arlarge$index.betweenness.normalized,
  #                                               y = data.ss.arlarge$net.epidemic.duration),
  #
  #                             name.x   = LABEL_BETWEENNESS,
  #                             limits.x = LIMITS_BETWEENNESS,
  #                             breaks.x = BREAKS_BETWEENNESS,
  #
  #                             name.y   = LABEL_DURATION,
  #                             limits.y = LIMITS_DURATIONPEAK,
  #                             breaks.y = BREAKS_DURATIONPEAK,
  #
  #                             show.legend = SHOW_LEGEND,
  #                             probability.infections = FALSE)),
  #            "dynamic-5-3-betweenness-duration")
  #
  # plots <- c(plots,
  #            list(plot_levels(df.1 = data.frame(x = data.ss$index.betweenness.normalized,
  #                                               y = data.ss$net.static.epidemic.duration),
  #
  #                             df.2 = data.frame(x = data.ss.arlarge$index.betweenness.normalized,
  #                                               y = data.ss.arlarge$net.static.epidemic.duration),
  #
  #                             name.x   = LABEL_BETWEENNESS,
  #                             limits.x = LIMITS_BETWEENNESS,
  #                             breaks.x = BREAKS_BETWEENNESS,
  #
  #                             name.y   = LABEL_DURATION,
  #                             limits.y = LIMITS_DURATIONPEAK,
  #                             breaks.y = BREAKS_DURATIONPEAK,
  #
  #                             show.legend = SHOW_LEGEND,
  #                             probability.infections = FALSE)),
  #            "static-5-3-betweenness-duration")

  # assortativity
  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss.armid.dyn$net.assortativity.pre.epidemic,
                                                y = data.ss.armid.dyn$net.epidemic.duration),

                              df.2 = data.frame(x = data.ss.arsmall.dyn$net.assortativity.pre.epidemic,
                                                y = data.ss.arsmall.dyn$net.epidemic.duration),

                              df.3 = data.frame(x = data.ss.arlarge.dyn$net.assortativity.pre.epidemic,
                                                y = data.ss.arlarge.dyn$net.epidemic.duration),

                              name.x   = LABEL_ASSORTATIVITY,
                              limits.x = LIMITS_ASSORTATIVITY,
                              breaks.x = BREAKS_ASSORTATIVITY,

                              name.y   = LABEL_DURATION,
                              limits.y = LIMITS_DURATIONPEAK,
                              breaks.y = BREAKS_DURATIONPEAK,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "dynamic-5-3-assortativity-duration")

  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss.armid.stat$net.assortativity.pre.epidemic,
                                                y = data.ss.armid.stat$net.static.epidemic.duration),

                              df.2 = data.frame(x = data.ss.arsmall.stat$net.assortativity.pre.epidemic,
                                                y = data.ss.arsmall.stat$net.static.epidemic.duration),

                              df.3 = data.frame(x = data.ss.arlarge.stat$net.assortativity.pre.epidemic,
                                                y = data.ss.arlarge.stat$net.static.epidemic.duration),

                              name.x   = LABEL_ASSORTATIVITY,
                              limits.x = LIMITS_ASSORTATIVITY,
                              breaks.x = BREAKS_ASSORTATIVITY,

                              name.y   = LABEL_DURATION,
                              limits.y = LIMITS_DURATIONPEAK,
                              breaks.y = BREAKS_DURATIONPEAK,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "static-5-3-assortativity-duration")

  # sigma
  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss.armid.dyn$nb.sigma,
                                                y = data.ss.armid.dyn$net.epidemic.duration),

                              df.2 = data.frame(x = data.ss.arsmall.dyn$nb.sigma,
                                                y = data.ss.arsmall.dyn$net.epidemic.duration),

                              df.3 = data.frame(x = data.ss.arlarge.dyn$nb.sigma,
                                                y = data.ss.arlarge.dyn$net.epidemic.duration),

                              name.x   = LABEL_SIGMA,
                              limits.x = LIMITS_SIGMA,
                              breaks.x = BREAKS_SIGMA,

                              name.y   = LABEL_DURATION,
                              limits.y = LIMITS_DURATIONPEAK,
                              breaks.y = BREAKS_DURATIONPEAK,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "dynamic-6-3-sigma-duration")

  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss.armid.stat$nb.sigma,
                                                y = data.ss.armid.stat$net.static.epidemic.duration),

                              df.2 = data.frame(x = data.ss.arsmall.stat$nb.sigma,
                                                y = data.ss.arsmall.stat$net.static.epidemic.duration),

                              df.3 = data.frame(x = data.ss.arlarge.stat$nb.sigma,
                                                y = data.ss.arlarge.stat$net.static.epidemic.duration),

                              name.x   = LABEL_SIGMA,
                              limits.x = LIMITS_SIGMA,
                              breaks.x = BREAKS_SIGMA,

                              name.y   = LABEL_DURATION,
                              limits.y = LIMITS_DURATIONPEAK,
                              breaks.y = BREAKS_DURATIONPEAK,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "static-6-3-sigma-duration")

  # gamma
  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss.armid.dyn$nb.gamma,
                                                y = data.ss.armid.dyn$net.epidemic.duration),

                              df.2 = data.frame(x = data.ss.arsmall.dyn$nb.gamma,
                                                y = data.ss.arsmall.dyn$net.epidemic.duration),

                              df.3 = data.frame(x = data.ss.arlarge.dyn$nb.gamma,
                                                y = data.ss.arlarge.dyn$net.epidemic.duration),

                              name.x   = LABEL_GAMMA,
                              limits.x = LIMITS_GAMMA,
                              breaks.x = BREAKS_GAMMA,

                              name.y   = LABEL_DURATION,
                              limits.y = LIMITS_DURATIONPEAK,
                              breaks.y = BREAKS_DURATIONPEAK,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "dynamic-7-3-gamma-duration")

  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss.armid.stat$nb.gamma,
                                                y = data.ss.armid.stat$net.static.epidemic.duration),

                              df.2 = data.frame(x = data.ss.arsmall.stat$nb.gamma,
                                                y = data.ss.arsmall.stat$net.static.epidemic.duration),

                              df.3 = data.frame(x = data.ss.arlarge.stat$nb.gamma,
                                                y = data.ss.arlarge.stat$net.static.epidemic.duration),

                              name.x   = LABEL_GAMMA,
                              limits.x = LIMITS_GAMMA,
                              breaks.x = BREAKS_GAMMA,

                              name.y   = LABEL_DURATION,
                              limits.y = LIMITS_DURATIONPEAK,
                              breaks.y = BREAKS_DURATIONPEAK,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "static-7-3-gamma-duration")

  return(plots)
}


# get_plots_peak <- function(data.ss = load_simulation_summary_data()) {
#
#   data.ss.arlarge <- subset(data.ss, net.pct.rec >= CUT_OFF_LARGE_ATTACK_RATE)
#
#   # net changes
#   plots <- c(list(plot_levels(df.1 = data.frame(x = data.ss$net.ties.broken.active.epidemic +
#                                                   data.ss$net.ties.out.accepted.epidemic,
#                                                 y = data.ss$net.epidemic.peak),
#
#                               df.2 = data.frame(x = data.ss.arlarge$net.ties.broken.active.epidemic +
#                                                   data.ss.arlarge$net.ties.out.accepted.epidemic,
#                                                 y = data.ss.arlarge$net.epidemic.peak),
#
#                               name.x   = LABEL_NETDECISIONS,
#                               limits.x = LIMITS_NETDECISIONS_NETWORK,
#                               breaks.x = BREAKS_NETDECISIONS_NETWORK,
#
#                               name.y   = LABEL_PEAK,
#                               limits.y = LIMITS_DURATIONPEAK,
#                               breaks.y = BREAKS_DURATIONPEAK,
#
#                               show.legend = SHOW_LEGEND,
#                               probability.infections = FALSE)),
#              "dynamic-0-4-netchanges-peak")
#
#   plots <- c(plots,
#              list(plot_levels(df.1 = data.frame(x = data.ss$net.static.ties.broken.active.epidemic +
#                                                   data.ss$net.static.ties.out.accepted.epidemic,
#                                                 y = data.ss$net.epidemic.peak),
#
#                               df.2 = data.frame(x = data.ss.arlarge$net.static.ties.broken.active.epidemic +
#                                                   data.ss.arlarge$net.static.ties.out.accepted.epidemic,
#                                                 y = data.ss.arlarge$net.epidemic.peak),
#
#                               name.x   = LABEL_NETDECISIONS,
#                               limits.x = LIMITS_NETDECISIONS_NETWORK,
#                               breaks.x = BREAKS_NETDECISIONS_NETWORK,
#
#                               name.y   = LABEL_PEAK,
#                               limits.y = LIMITS_DURATIONPEAK,
#                               breaks.y = BREAKS_DURATIONPEAK,
#
#                               show.legend = SHOW_LEGEND,
#                               probability.infections = FALSE)),
#              "static-0-4-netchanges-peak")
#
#   plots <- c(plots,
#              list(plot_levels(df.1 = data.frame(x = data.ss$nb.r.sigma.av,
#                                                 y = data.ss$net.epidemic.peak),
#
#                               df.2 = data.frame(x = data.ss.arlarge$nb.r.sigma.av,
#                                                 y = data.ss.arlarge$net.epidemic.peak),
#
#                               name.x   = LABEL_RISKPERCEPTION,
#                               limits.x = LIMITS_RISKPERCEPTION,
#                               breaks.x = BREAKS_RISKPERCEPTION,
#
#                               name.y   = LABEL_PEAK,
#                               limits.y = LIMITS_DURATIONPEAK,
#                               breaks.y = BREAKS_DURATIONPEAK,
#
#                               show.legend = SHOW_LEGEND,
#                               probability.infections = FALSE)),
#              "dynamic-1-4-riskperception-peak")
#
#   plots <- c(plots,
#              list(plot_levels(df.1 = data.frame(x = data.ss$nb.r.sigma.av,
#                                                 y = data.ss$net.static.epidemic.peak),
#
#                               df.2 = data.frame(x = data.ss.arlarge$nb.r.sigma.av,
#                                                 y = data.ss.arlarge$net.static.epidemic.peak),
#
#                               name.x   = LABEL_RISKPERCEPTION,
#                               limits.x = LIMITS_RISKPERCEPTION,
#                               breaks.x = BREAKS_RISKPERCEPTION,
#
#                               name.y   = LABEL_PEAK,
#                               limits.y = LIMITS_DURATIONPEAK,
#                               breaks.y = BREAKS_DURATIONPEAK,
#
#                               show.legend = SHOW_LEGEND,
#                               probability.infections = FALSE)),
#              "static-1-4-riskperception-peak")
#
#   # degree
#   plots <- c(plots,
#              list(plot_levels(df.1 = data.frame(x = data.ss$net.degree.pre.epidemic.av,
#                                                 y = data.ss$net.epidemic.peak),
#
#                               df.2 = data.frame(x = data.ss.arlarge$net.degree.pre.epidemic.av,
#                                                 y = data.ss.arlarge$net.epidemic.peak),
#
#                               name.x   = LABEL_DEGREE,
#                               limits.x = LIMITS_DEGREE,
#                               breaks.x = BREAKS_DEGREE,
#
#                               name.y   = LABEL_PEAK,
#                               limits.y = LIMITS_DURATIONPEAK,
#                               breaks.y = BREAKS_DURATIONPEAK,
#
#                               show.legend = SHOW_LEGEND,
#                               probability.infections = FALSE)),
#              "dynamic-2-4-degree-peak")
#
#   plots <- c(plots,
#              list(plot_levels(df.1 = data.frame(x = data.ss$net.degree.pre.epidemic.av,
#                                                 y = data.ss$net.static.epidemic.peak),
#
#                               df.2 = data.frame(x = data.ss.arlarge$net.degree.pre.epidemic.av,
#                                                 y = data.ss.arlarge$net.static.epidemic.peak),
#
#                               name.x   = LABEL_DEGREE,
#                               limits.x = LIMITS_DEGREE,
#                               breaks.x = BREAKS_DEGREE,
#
#                               name.y   = LABEL_PEAK,
#                               limits.y = LIMITS_DURATIONPEAK,
#                               breaks.y = BREAKS_DURATIONPEAK,
#
#                               show.legend = SHOW_LEGEND,
#                               probability.infections = FALSE)),
#              "static-2-4-degree-peak")
#
#   plots <- c(plots,
#              list(plot_levels(df.1 = data.frame(x = data.ss$net.clustering.pre.epidemic.av,
#                                                 y = data.ss$net.epidemic.peak),
#
#                               df.2 = data.frame(x = data.ss.arlarge$net.clustering.pre.epidemic.av,
#                                                 y = data.ss.arlarge$net.epidemic.peak),
#
#                               name.x   = LABEL_CLUSTERING,
#                               limits.x = LIMITS_CLUSTERING,
#                               breaks.x = BREAKS_CLUSTERING,
#
#                               name.y   = LABEL_PEAK,
#                               limits.y = LIMITS_DURATIONPEAK,
#                               breaks.y = BREAKS_DURATIONPEAK,
#
#                               show.legend = SHOW_LEGEND,
#                               probability.infections = FALSE)),
#              "dynamic-3-4-clustering-peak")
#
#   plots <- c(plots,
#              list(plot_levels(df.1 = data.frame(x = data.ss$net.clustering.pre.epidemic.av,
#                                                 y = data.ss$net.static.epidemic.peak),
#
#                               df.2 = data.frame(x = data.ss.arlarge$net.clustering.pre.epidemic.av,
#                                                 y = data.ss.arlarge$net.static.epidemic.peak),
#
#                               name.x   = LABEL_CLUSTERING,
#                               limits.x = LIMITS_CLUSTERING,
#                               breaks.x = BREAKS_CLUSTERING,
#
#                               name.y   = LABEL_PEAK,
#                               limits.y = LIMITS_DURATIONPEAK,
#                               breaks.y = BREAKS_DURATIONPEAK,
#
#                               show.legend = SHOW_LEGEND,
#                               probability.infections = FALSE)),
#              "static-3-4-clustering-peak")
#
#   plots <- c(plots,
#              list(plot_levels(df.1 = data.frame(x = data.ss$net.pathlength.pre.epidemic.av,
#                                                 y = data.ss$net.epidemic.peak),
#
#                               df.2 = data.frame(x = data.ss.arlarge$net.pathlength.pre.epidemic.av,
#                                                 y = data.ss.arlarge$net.epidemic.peak),
#
#                               name.x   = LABEL_PATHLENGTH,
#                               limits.x = LIMITS_PATHLENGTH,
#                               breaks.x = BREAKS_PATHLENGTH,
#
#                               name.y   = LABEL_PEAK,
#                               limits.y = LIMITS_DURATIONPEAK,
#                               breaks.y = BREAKS_DURATIONPEAK,
#
#                               show.legend = SHOW_LEGEND,
#                               probability.infections = FALSE)),
#              "dynamic-4-4-pathlength-peak")
#
#   plots <- c(plots,
#              list(plot_levels(df.1 = data.frame(x = data.ss$net.pathlength.pre.epidemic.av,
#                                                 y = data.ss$net.static.epidemic.peak),
#
#                               df.2 = data.frame(x = data.ss.arlarge$net.pathlength.pre.epidemic.av,
#                                                 y = data.ss.arlarge$net.static.epidemic.peak),
#
#                               name.x   = LABEL_PATHLENGTH,
#                               limits.x = LIMITS_PATHLENGTH,
#                               breaks.x = BREAKS_PATHLENGTH,
#
#                               name.y   = LABEL_PEAK,
#                               limits.y = LIMITS_DURATIONPEAK,
#                               breaks.y = BREAKS_DURATIONPEAK,
#
#                               show.legend = SHOW_LEGEND,
#                               probability.infections = FALSE)),
#              "static-4-4-pathlength-peak")
#
#   # plots <- c(plots,
#   #            list(plot_levels(df.1 = data.frame(x = data.ss$index.betweenness.normalized,
#   #                                               y = data.ss$net.epidemic.peak),
#   #
#   #                             df.2 = data.frame(x = data.ss.arlarge$index.betweenness.normalized,
#   #                                               y = data.ss.arlarge$net.epidemic.peak),
#   #
#   #                             name.x   = LABEL_BETWEENNESS,
#   #                             limits.x = LIMITS_BETWEENNESS,
#   #                             breaks.x = BREAKS_BETWEENNESS,
#   #
#   #                             name.y   = LABEL_PEAK,
#   #                             limits.y = LIMITS_DURATIONPEAK,
#   #                             breaks.y = BREAKS_DURATIONPEAK,
#   #
#   #                             show.legend = SHOW_LEGEND,
#   #                             probability.infections = FALSE)),
#   #            "dynamic-5-4-betweenness-peak")
#   #
#   # plots <- c(plots,
#   #            list(plot_levels(df.1 = data.frame(x = data.ss$index.betweenness.normalized,
#   #                                               y = data.ss$net.static.epidemic.peak),
#   #
#   #                             df.2 = data.frame(x = data.ss.arlarge$index.betweenness.normalized,
#   #                                               y = data.ss.arlarge$net.static.epidemic.peak),
#   #
#   #                             name.x   = LABEL_BETWEENNESS,
#   #                             limits.x = LIMITS_BETWEENNESS,
#   #                             breaks.x = BREAKS_BETWEENNESS,
#   #
#   #                             name.y   = LABEL_PEAK,
#   #                             limits.y = LIMITS_DURATIONPEAK,
#   #                             breaks.y = BREAKS_DURATIONPEAK,
#   #
#   #                             show.legend = SHOW_LEGEND,
#   #                             probability.infections = FALSE)),
#   #            "static-5-4-betweenness-peak")
#
#   plots <- c(plots,
#              list(plot_levels(df.1 = data.frame(x = data.ss$net.assortativity.pre.epidemic,
#                                                 y = data.ss$net.epidemic.peak),
#
#                               df.2 = data.frame(x = data.ss.arlarge$net.assortativity.pre.epidemic,
#                                                 y = data.ss.arlarge$net.epidemic.peak),
#
#                               name.x   = LABEL_ASSORTATIVITY,
#                               limits.x = LIMITS_ASSORTATIVITY,
#                               breaks.x = BREAKS_ASSORTATIVITY,
#
#                               name.y   = LABEL_PEAK,
#                               limits.y = LIMITS_DURATIONPEAK,
#                               breaks.y = BREAKS_DURATIONPEAK,
#
#                               show.legend = SHOW_LEGEND,
#                               probability.infections = FALSE)),
#              "dynamic-6-4-assortativity-peak")
#
#   plots <- c(plots,
#              list(plot_levels(df.1 = data.frame(x = data.ss$net.assortativity.pre.epidemic,
#                                                 y = data.ss$net.static.epidemic.peak),
#
#                               df.2 = data.frame(x = data.ss.arlarge$net.assortativity.pre.epidemic,
#                                                 y = data.ss.arlarge$net.static.epidemic.peak),
#
#                               name.x   = LABEL_ASSORTATIVITY,
#                               limits.x = LIMITS_ASSORTATIVITY,
#                               breaks.x = BREAKS_ASSORTATIVITY,
#
#                               name.y   = LABEL_PEAK,
#                               limits.y = LIMITS_DURATIONPEAK,
#                               breaks.y = BREAKS_DURATIONPEAK,
#
#                               show.legend = SHOW_LEGEND,
#                               probability.infections = FALSE)),
#              "static-6-4-assortativity-peak")
#
#   # sigma
#   plots <- c(plots,
#              list(plot_levels(df.1 = data.frame(x = data.ss$nb.sigma,
#                                                 y = data.ss$net.epidemic.peak),
#
#                               df.2 = data.frame(x = data.ss.arlarge$nb.sigma,
#                                                 y = data.ss.arlarge$net.epidemic.peak),
#
#                               name.x   = LABEL_SIGMA,
#                               limits.x = LIMITS_SIGMA,
#                               breaks.x = BREAKS_SIGMA,
#
#                               name.y   = LABEL_PEAK,
#                               limits.y = LIMITS_DURATIONPEAK,
#                               breaks.y = BREAKS_DURATIONPEAK,
#
#                               show.legend = SHOW_LEGEND,
#                               probability.infections = FALSE)),
#              "dynamic-7-4-sigma-peak")
#
#   plots <- c(plots,
#              list(plot_levels(df.1 = data.frame(x = data.ss$nb.sigma,
#                                                 y = data.ss$net.static.epidemic.peak),
#
#                               df.2 = data.frame(x = data.ss.arlarge$nb.sigma,
#                                                 y = data.ss.arlarge$net.static.epidemic.peak),
#
#                               name.x   = LABEL_SIGMA,
#                               limits.x = LIMITS_SIGMA,
#                               breaks.x = BREAKS_SIGMA,
#
#                               name.y   = LABEL_PEAK,
#                               limits.y = LIMITS_DURATIONPEAK,
#                               breaks.y = BREAKS_DURATIONPEAK,
#
#                               show.legend = SHOW_LEGEND,
#                               probability.infections = FALSE)),
#              "static-7-4-sigma-peak")
#
#   # gamma
#   plots <- c(plots,
#              list(plot_levels(df.1 = data.frame(x = data.ss$nb.gamma,
#                                                 y = data.ss$net.epidemic.peak),
#
#                               df.2 = data.frame(x = data.ss.arlarge$nb.gamma,
#                                                 y = data.ss.arlarge$net.epidemic.peak),
#
#                               name.x   = LABEL_GAMMA,
#                               limits.x = LIMITS_GAMMA,
#                               breaks.x = BREAKS_GAMMA,
#
#                               name.y   = LABEL_PEAK,
#                               limits.y = LIMITS_DURATIONPEAK,
#                               breaks.y = BREAKS_DURATIONPEAK,
#
#                               show.legend = SHOW_LEGEND,
#                               probability.infections = FALSE)),
#              "dynamic-8-4-gamma-peak")
#
#   plots <- c(plots,
#              list(plot_levels(df.1 = data.frame(x = data.ss$nb.gamma,
#                                                 y = data.ss$net.static.epidemic.peak),
#
#                               df.2 = data.frame(x = data.ss.arlarge$nb.gamma,
#                                                 y = data.ss.arlarge$net.static.epidemic.peak),
#
#                               name.x   = LABEL_GAMMA,
#                               limits.x = LIMITS_GAMMA,
#                               breaks.x = BREAKS_GAMMA,
#
#                               name.y   = LABEL_PEAK,
#                               limits.y = LIMITS_DURATIONPEAK,
#                               breaks.y = BREAKS_DURATIONPEAK,
#
#                               show.legend = SHOW_LEGEND,
#                               probability.infections = FALSE)),
#              "static-8-4-gamma-peak")
#
#   return(plots)
#
# }


get_plots_peaksize <- function(data.ss = load_simulation_summary_data()) {

  data.ss.armid.dyn <- subset(data.ss, net.pct.rec > CUT_OFF_SMALL_ATTACK_RATE &
                                net.pct.rec < CUT_OFF_LARGE_ATTACK_RATE)
  data.ss.arsmall.dyn <- subset(data.ss, net.pct.rec <= CUT_OFF_SMALL_ATTACK_RATE)
  data.ss.arlarge.dyn <- subset(data.ss, net.pct.rec >= CUT_OFF_LARGE_ATTACK_RATE)

  data.ss.armid.stat <- subset(data.ss, net.static.pct.rec > CUT_OFF_SMALL_ATTACK_RATE &
                                net.static.pct.rec < CUT_OFF_LARGE_ATTACK_RATE)
  data.ss.arsmall.stat <- subset(data.ss, net.static.pct.rec <= CUT_OFF_SMALL_ATTACK_RATE)
  data.ss.arlarge.stat <- subset(data.ss, net.static.pct.rec >= CUT_OFF_LARGE_ATTACK_RATE)

  # net changes
  plots <- c(list(plot_levels(df.1 = data.frame(x = data.ss.armid.dyn$net.ties.broken.active.epidemic +
                                                  data.ss.armid.dyn$net.ties.out.accepted.epidemic,
                                                y = data.ss.armid.dyn$net.epidemic.peak.size),

                              df.2 = data.frame(x = data.ss.arsmall.dyn$net.ties.broken.active.epidemic +
                                                  data.ss.arsmall.dyn$net.ties.out.accepted.epidemic,
                                                y = data.ss.arsmall.dyn$net.epidemic.peak.size),

                              df.3 = data.frame(x = data.ss.arlarge.dyn$net.ties.broken.active.epidemic +
                                                  data.ss.arlarge.dyn$net.ties.out.accepted.epidemic,
                                                y = data.ss.arlarge.dyn$net.epidemic.peak.size),

                              name.x   = LABEL_NETDECISIONS,
                              limits.x = LIMITS_NETDECISIONS_NETWORK,
                              breaks.x = BREAKS_NETDECISIONS_NETWORK,

                              name.y   = LABEL_PEAKSIZE,
                              limits.y = LIMITS_PEAKSIZE,
                              breaks.y = BREAKS_PEAKSIZE,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "dynamic-1-4-netchanges-peaksize")

  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss.armid.stat$net.static.ties.broken.active.epidemic +
                                                  data.ss.armid.stat$net.static.ties.out.accepted.epidemic,
                                                y = data.ss.armid.stat$net.epidemic.peak.size),

                              df.2 = data.frame(x = data.ss.arsmall.stat$net.static.ties.broken.active.epidemic +
                                                  data.ss.arsmall.stat$net.static.ties.out.accepted.epidemic,
                                                y = data.ss.arsmall.stat$net.epidemic.peak.size),

                              df.3 = data.frame(x = data.ss.arlarge.stat$net.static.ties.broken.active.epidemic +
                                                  data.ss.arlarge.stat$net.static.ties.out.accepted.epidemic,
                                                y = data.ss.arlarge.stat$net.epidemic.peak.size),

                              name.x   = LABEL_NETDECISIONS,
                              limits.x = LIMITS_NETDECISIONS_NETWORK,
                              breaks.x = BREAKS_NETDECISIONS_NETWORK,

                              name.y   = LABEL_PEAKSIZE,
                              limits.y = LIMITS_PEAKSIZE,
                              breaks.y = BREAKS_PEAKSIZE,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "static-1-4-netchanges-peaksize")

  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss.armid.dyn$nb.r.sigma.av,
                                                y = data.ss.armid.dyn$net.epidemic.peak.size),

                              df.2 = data.frame(x = data.ss.arsmall.dyn$nb.r.sigma.av,
                                                y = data.ss.arsmall.dyn$net.epidemic.peak.size),

                              df.3 = data.frame(x = data.ss.arlarge.dyn$nb.r.sigma.av,
                                                y = data.ss.arlarge.dyn$net.epidemic.peak.size),

                              name.x   = LABEL_RISKPERCEPTION,
                              limits.x = LIMITS_RISKPERCEPTION,
                              breaks.x = BREAKS_RISKPERCEPTION,

                              name.y   = LABEL_PEAKSIZE,
                              limits.y = LIMITS_PEAKSIZE,
                              breaks.y = BREAKS_PEAKSIZE,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "dynamic-8-4-riskperception-peaksize")

  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss.armid.stat$nb.r.sigma.av,
                                                y = data.ss.armid.stat$net.static.epidemic.peak.size),

                              df.2 = data.frame(x = data.ss.arsmall.stat$nb.r.sigma.av,
                                                y = data.ss.arsmall.stat$net.static.epidemic.peak.size),

                              df.3 = data.frame(x = data.ss.arlarge.stat$nb.r.sigma.av,
                                                y = data.ss.arlarge.stat$net.static.epidemic.peak.size),

                              name.x   = LABEL_RISKPERCEPTION,
                              limits.x = LIMITS_RISKPERCEPTION,
                              breaks.x = BREAKS_RISKPERCEPTION,

                              name.y   = LABEL_PEAKSIZE,
                              limits.y = LIMITS_PEAKSIZE,
                              breaks.y = BREAKS_PEAKSIZE,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "static-8-4-riskperception-peaksize")

  # degree
  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss.armid.dyn$net.degree.pre.epidemic.av,
                                                y = data.ss.armid.dyn$net.epidemic.peak.size),

                              df.2 = data.frame(x = data.ss.arsmall.dyn$net.degree.pre.epidemic.av,
                                                y = data.ss.arsmall.dyn$net.epidemic.peak.size),

                              df.3 = data.frame(x = data.ss.arlarge.dyn$net.degree.pre.epidemic.av,
                                                y = data.ss.arlarge.dyn$net.epidemic.peak.size),

                              name.x   = LABEL_DEGREE,
                              limits.x = LIMITS_DEGREE,
                              breaks.x = BREAKS_DEGREE,

                              name.y   = LABEL_PEAKSIZE,
                              limits.y = LIMITS_PEAKSIZE,
                              breaks.y = BREAKS_PEAKSIZE,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "dynamic-2-4-degree-peaksize")

  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss.armid.stat$net.degree.pre.epidemic.av,
                                                y = data.ss.armid.stat$net.static.epidemic.peak.size),

                              df.2 = data.frame(x = data.ss.arsmall.stat$net.degree.pre.epidemic.av,
                                                y = data.ss.arsmall.stat$net.static.epidemic.peak.size),

                              df.3 = data.frame(x = data.ss.arlarge.stat$net.degree.pre.epidemic.av,
                                                y = data.ss.arlarge.stat$net.static.epidemic.peak.size),

                              name.x   = LABEL_DEGREE,
                              limits.x = LIMITS_DEGREE,
                              breaks.x = BREAKS_DEGREE,

                              name.y   = LABEL_PEAKSIZE,
                              limits.y = LIMITS_PEAKSIZE,
                              breaks.y = BREAKS_PEAKSIZE,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "static-2-4-degree-peaksize")

  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss.armid.dyn$net.clustering.pre.epidemic.av,
                                                y = data.ss.armid.dyn$net.epidemic.peak.size),

                              df.2 = data.frame(x = data.ss.arsmall.dyn$net.clustering.pre.epidemic.av,
                                                y = data.ss.arsmall.dyn$net.epidemic.peak.size),

                              df.3 = data.frame(x = data.ss.arlarge.dyn$net.clustering.pre.epidemic.av,
                                                y = data.ss.arlarge.dyn$net.epidemic.peak.size),

                              name.x   = LABEL_CLUSTERING,
                              limits.x = LIMITS_CLUSTERING,
                              breaks.x = BREAKS_CLUSTERING,

                              name.y   = LABEL_PEAKSIZE,
                              limits.y = LIMITS_PEAKSIZE,
                              breaks.y = BREAKS_PEAKSIZE,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "dynamic-3-4-clustering-peaksize")

  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss.armid.stat$net.clustering.pre.epidemic.av,
                                                y = data.ss.armid.stat$net.static.epidemic.peak.size),

                              df.2 = data.frame(x = data.ss.arsmall.stat$net.clustering.pre.epidemic.av,
                                                y = data.ss.arsmall.stat$net.static.epidemic.peak.size),

                              df.3 = data.frame(x = data.ss.arlarge.stat$net.clustering.pre.epidemic.av,
                                                y = data.ss.arlarge.stat$net.static.epidemic.peak.size),

                              name.x   = LABEL_CLUSTERING,
                              limits.x = LIMITS_CLUSTERING,
                              breaks.x = BREAKS_CLUSTERING,

                              name.y   = LABEL_PEAKSIZE,
                              limits.y = LIMITS_PEAKSIZE,
                              breaks.y = BREAKS_PEAKSIZE,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "static-3-4-clustering-peaksize")

  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss.armid.dyn$net.pathlength.pre.epidemic.av,
                                                y = data.ss.armid.dyn$net.epidemic.peak.size),

                              df.2 = data.frame(x = data.ss.arsmall.dyn$net.pathlength.pre.epidemic.av,
                                                y = data.ss.arsmall.dyn$net.epidemic.peak.size),

                              df.3 = data.frame(x = data.ss.arlarge.dyn$net.pathlength.pre.epidemic.av,
                                                y = data.ss.arlarge.dyn$net.epidemic.peak.size),

                              name.x   = LABEL_PATHLENGTH,
                              limits.x = LIMITS_PATHLENGTH,
                              breaks.x = BREAKS_PATHLENGTH,

                              name.y   = LABEL_PEAKSIZE,
                              limits.y = LIMITS_PEAKSIZE,
                              breaks.y = BREAKS_PEAKSIZE,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "dynamic-4-4-pathlength-peaksize")

  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss.armid.stat$net.pathlength.pre.epidemic.av,
                                                y = data.ss.armid.stat$net.static.epidemic.peak.size),

                              df.2 = data.frame(x = data.ss.arsmall.stat$net.pathlength.pre.epidemic.av,
                                                y = data.ss.arsmall.stat$net.static.epidemic.peak.size),

                              df.3 = data.frame(x = data.ss.arlarge.stat$net.pathlength.pre.epidemic.av,
                                                y = data.ss.arlarge.stat$net.static.epidemic.peak.size),

                              name.x   = LABEL_PATHLENGTH,
                              limits.x = LIMITS_PATHLENGTH,
                              breaks.x = BREAKS_PATHLENGTH,

                              name.y   = LABEL_PEAKSIZE,
                              limits.y = LIMITS_PEAKSIZE,
                              breaks.y = BREAKS_PEAKSIZE,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "static-4-4-pathlength-peaksize")

  # plots <- c(plots,
  #            list(plot_levels(df.1 = data.frame(x = data.ss$index.betweenness.normalized,
  #                                               y = data.ss$net.epidemic.peak.size),
  #
  #                             df.2 = data.frame(x = data.ss.arlarge$index.betweenness.normalized,
  #                                               y = data.ss.arlarge$net.epidemic.peak.size),
  #
  #                             name.x   = LABEL_BETWEENNESS,
  #                             limits.x = LIMITS_BETWEENNESS,
  #                             breaks.x = BREAKS_BETWEENNESS,
  #
  #                             name.y   = LABEL_PEAKSIZE,
  #                             limits.y = LIMITS_PEAKSIZE,
  #                             breaks.y = BREAKS_PEAKSIZE,
  #
  #                             show.legend = SHOW_LEGEND,
  #                             probability.infections = FALSE)),
  #            "dynamic-5-5-betweenness-peaksize")
  #
  # plots <- c(plots,
  #            list(plot_levels(df.1 = data.frame(x = data.ss$index.betweenness.normalized,
  #                                               y = data.ss$net.static.epidemic.peak.size),
  #
  #                             df.2 = data.frame(x = data.ss.arlarge$index.betweenness.normalized,
  #                                               y = data.ss.arlarge$net.static.epidemic.peak.size),
  #
  #                             name.x   = LABEL_BETWEENNESS,
  #                             limits.x = LIMITS_BETWEENNESS,
  #                             breaks.x = BREAKS_BETWEENNESS,
  #
  #                             name.y   = LABEL_PEAKSIZE,
  #                             limits.y = LIMITS_PEAKSIZE,
  #                             breaks.y = BREAKS_PEAKSIZE,
  #
  #                             show.legend = SHOW_LEGEND,
  #                             probability.infections = FALSE)),
  #            "static-5-5-betweenness-peaksize")

  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss.armid.dyn$net.assortativity.pre.epidemic,
                                                y = data.ss.armid.dyn$net.epidemic.peak.size),

                              df.2 = data.frame(x = data.ss.arsmall.dyn$net.assortativity.pre.epidemic,
                                                y = data.ss.arsmall.dyn$net.epidemic.peak.size),

                              df.3 = data.frame(x = data.ss.arlarge.dyn$net.assortativity.pre.epidemic,
                                                y = data.ss.arlarge.dyn$net.epidemic.peak.size),

                              name.x   = LABEL_ASSORTATIVITY,
                              limits.x = LIMITS_ASSORTATIVITY,
                              breaks.x = BREAKS_ASSORTATIVITY,

                              name.y   = LABEL_PEAKSIZE,
                              limits.y = LIMITS_PEAKSIZE,
                              breaks.y = BREAKS_PEAKSIZE,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "dynamic-5-4-assortativity-peaksize")

  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss.armid.stat$net.assortativity.pre.epidemic,
                                                y = data.ss.armid.stat$net.static.epidemic.peak.size),

                              df.2 = data.frame(x = data.ss.arsmall.stat$net.assortativity.pre.epidemic,
                                                y = data.ss.arsmall.stat$net.static.epidemic.peak.size),

                              df.3 = data.frame(x = data.ss.arlarge.stat$net.assortativity.pre.epidemic,
                                                y = data.ss.arlarge.stat$net.static.epidemic.peak.size),

                              name.x   = LABEL_ASSORTATIVITY,
                              limits.x = LIMITS_ASSORTATIVITY,
                              breaks.x = BREAKS_ASSORTATIVITY,

                              name.y   = LABEL_PEAKSIZE,
                              limits.y = LIMITS_PEAKSIZE,
                              breaks.y = BREAKS_PEAKSIZE,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "static-5-4-assortativity-peaksize")

  # sigma
  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss.armid.dyn$nb.sigma,
                                                y = data.ss.armid.dyn$net.epidemic.peak.size),

                              df.2 = data.frame(x = data.ss.arsmall.dyn$nb.sigma,
                                                y = data.ss.arsmall.dyn$net.epidemic.peak.size),

                              df.3 = data.frame(x = data.ss.arlarge.dyn$nb.sigma,
                                                y = data.ss.arlarge.dyn$net.epidemic.peak.size),

                              name.x   = LABEL_SIGMA,
                              limits.x = LIMITS_SIGMA,
                              breaks.x = BREAKS_SIGMA,

                              name.y   = LABEL_PEAKSIZE,
                              limits.y = LIMITS_PEAKSIZE,
                              breaks.y = BREAKS_PEAKSIZE,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "dynamic-6-4-sigma-peaksize")

  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss.armid.stat$nb.sigma,
                                                y = data.ss.armid.stat$net.static.epidemic.peak.size),

                              df.2 = data.frame(x = data.ss.arsmall.stat$nb.sigma,
                                                y = data.ss.arsmall.stat$net.static.epidemic.peak.size),

                              df.3 = data.frame(x = data.ss.arlarge.stat$nb.sigma,
                                                y = data.ss.arlarge.stat$net.static.epidemic.peak.size),

                              name.x   = LABEL_SIGMA,
                              limits.x = LIMITS_SIGMA,
                              breaks.x = BREAKS_SIGMA,

                              name.y   = LABEL_PEAKSIZE,
                              limits.y = LIMITS_PEAKSIZE,
                              breaks.y = BREAKS_PEAKSIZE,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "static-6-4-sigma-peaksize")

  # gamma
  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss.armid.dyn$nb.gamma,
                                                y = data.ss.armid.dyn$net.epidemic.peak.size),

                              df.2 = data.frame(x = data.ss.arsmall.dyn$nb.gamma,
                                                y = data.ss.arsmall.dyn$net.epidemic.peak.size),

                              df.3 = data.frame(x = data.ss.arlarge.dyn$nb.gamma,
                                                y = data.ss.arlarge.dyn$net.epidemic.peak.size),

                              name.x   = LABEL_GAMMA,
                              limits.x = LIMITS_GAMMA,
                              breaks.x = BREAKS_GAMMA,

                              name.y   = LABEL_PEAKSIZE,
                              limits.y = LIMITS_PEAKSIZE,
                              breaks.y = BREAKS_PEAKSIZE,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "dynamic-7-4-gamma-peak")

  plots <- c(plots,
             list(plot_levels(df.1 = data.frame(x = data.ss.armid.stat$nb.gamma,
                                                y = data.ss.armid.stat$net.static.epidemic.peak.size),

                              df.2 = data.frame(x = data.ss.arsmall.stat$nb.gamma,
                                                y = data.ss.arsmall.stat$net.static.epidemic.peak.size),

                              df.3 = data.frame(x = data.ss.arlarge.stat$nb.gamma,
                                                y = data.ss.arlarge.stat$net.static.epidemic.peak.size),

                              name.x   = LABEL_GAMMA,
                              limits.x = LIMITS_GAMMA,
                              breaks.x = BREAKS_GAMMA,

                              name.y   = LABEL_PEAKSIZE,
                              limits.y = LIMITS_PEAKSIZE,
                              breaks.y = BREAKS_PEAKSIZE,

                              show.legend = SHOW_LEGEND,
                              probability.infections = FALSE)),
             "static-7-4-gamma-peaksize")

  return(plots)
}

export_plots <- function(plots, plot.square = FALSE) {

  # create directory if necessary
  dir.create(EXPORT_PATH_PLOTS, showWarnings = FALSE)

  plot.index <- 1
  name.index <- 2
  while (plot.index < length(plots)) {
    h = EXPORT_PLOT_HEIGHT
    if (plot.square) {
      h = EXPORT_PLOT_WIDTH
    }
    ggsave(paste(EXPORT_PATH_PLOTS, plots[name.index][[1]], EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
           plots[plot.index][[1]],
           width = 230,
           height = 230,
           units = "mm",
           dpi = 600,
           device = EXPORT_FILE_TYPE_PLOTS)

    plot.index <- plot.index + 2
    name.index <- name.index + 2
  }
}

export_all_plots <- function(data.ss = load_simulation_summary_data()) {
                             #data.ad = load_agent_details_prepared_data()) {

  export_plots(get_plots_dynamics(data.ss = data.ss), plot.square = TRUE)

  export_plots(get_plots_netchanges(data.ss = data.ss, ep.structure = "dynamic"))
  export_plots(get_plots_netchanges(data.ss = data.ss, ep.structure = "static"))
  export_plots(get_plots_attackrate(data.ss = data.ss))
  export_plots(get_plots_duration(data.ss = data.ss))
  export_plots(get_plots_peaksize(data.ss = data.ss))


  # export_plots(get_plots_peak(data.ss = data.ss))
  # export_plots(get_plots_probability(data.ad = data.ad, ep.structure = "dynamic"))
  # export_plots(get_plots_probability(data.ad = data.ad, ep.structure = "static"))
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
prepare_predictor <- function(vec, prep.type = "standardize") {
  if (prep.type == "standardize") {
    vec <- (vec - mean(vec)) / sd(vec)
  } else if (prep.type == "normalize") {
    vec <- (vec - min(vec, na.rm = TRUE)) / (max(vec, na.rm = TRUE) - min(vec, na.rm = TRUE))
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
export_network_models <- function(data.ss = load_simulation_summary_data(), filenamname.appendix = "", print.summaries = FALSE) {

  ##### DYNAMIC #####
  ### INDEPENDENT ###
  ## MAIN EFFECTS
  net.changes                   <- prepare_predictor(data.ss$net.ties.broken.active.epidemic +
                                                       data.ss$net.ties.out.accepted.epidemic)
  degree.av                     <- prepare_predictor(data.ss$net.degree.pre.epidemic.av)
  clustering.av                 <- prepare_predictor(data.ss$net.clustering.pre.epidemic.av)
  pathlength.av                 <- prepare_predictor(data.ss$net.pathlength.pre.epidemic.av)
  assortativity.av              <- prepare_predictor(data.ss$net.assortativity.pre.epidemic)
  sigma                         <- prepare_predictor(data.ss$nb.sigma)
  gamma                         <- prepare_predictor(data.ss$nb.gamma)
  r.sigma.av                    <- prepare_predictor(data.ss$nb.r.sigma.av)

  attack.rate.dyn.iv            <- prepare_predictor(data.ss$net.pct.rec)
  attack.rate.stat.iv           <- prepare_predictor(data.ss$net.static.pct.rec)

  ### DEPENDENT ###
  net.changes.dv <- data.ss$net.ties.broken.active.epidemic + data.ss$net.ties.out.accepted.epidemic
  attack.rate    <- data.ss$net.pct.rec / 100
  duration       <- data.ss$net.epidemic.duration
  peak           <- data.ss$net.epidemic.peak
  peak.size      <- data.ss$net.epidemic.peak.size

  ## MAIN EFFECTS (NETWORK DYNAMICS)
  # attack rate
  model.1.attackrate <- glm(attack.rate ~
                                      net.changes,
                                    family = binomial)
  if (print.summaries) {
    print(summary(model.1.attackrate))
    print(print_r2(model.1.attackrate))
  }
  # duration
  model.1.duration <- lm(duration ~
                                   net.changes)
  if (print.summaries) {
    print(summary(model.1.duration))
    print(print_r2(model.1.duration))
  }
  # peak size
  model.1.peak.size <- lm(peak.size ~
                                    net.changes)
  if (print.summaries) {
    print(summary(model.1.peak.size))
    print(print_r2(model.1.peak.size))
  }

  ## MAIN EFFECTS (ACTUAL NETWORK PROPERTIES RATHER THAN PARAMETERS TO CONTROL NETWORK PROPERTIES)
  # network changes
  model.2.netchanges <- lm(net.changes.dv ~
                                     degree.av +
                                     clustering.av +
                                     pathlength.av +
                                     assortativity.av +
                                     sigma +
                                     gamma +
                                     r.sigma.av
  )
  if (print.summaries) {
    print(summary(model.2.netchanges))
    print(vif(model.2.netchanges))
  }

  # export_interactions(c("degree.av",
  #                       "clustering.av",
  #                       "pathlength.av",
  #                       "assortativity.av",
  #                       "sigma",
  #                       "gamma",
  #                       "r.sigma.av"),
  #                     "network", c(35, 20))
  degree.av.X.clustering.av          <- degree.av           * clustering.av
  degree.av.X.pathlength.av          <- degree.av           * pathlength.av
  degree.av.X.assortativity.av       <- degree.av           * assortativity.av
  degree.av.X.sigma                  <- degree.av           * sigma
  degree.av.X.gamma                  <- degree.av           * gamma
  degree.av.X.r.sigma.av             <- degree.av           * r.sigma.av
  clustering.av.X.pathlength.av      <- clustering.av       * pathlength.av
  clustering.av.X.assortativity.av   <- clustering.av       * assortativity.av
  clustering.av.X.sigma              <- clustering.av       * sigma
  clustering.av.X.gamma              <- clustering.av       * gamma
  clustering.av.X.r.sigma.av         <- clustering.av       * r.sigma.av
  pathlength.av.X.assortativity.av   <- pathlength.av       * assortativity.av
  pathlength.av.X.sigma              <- pathlength.av       * sigma
  pathlength.av.X.gamma              <- pathlength.av       * gamma
  pathlength.av.X.r.sigma.av         <- pathlength.av       * r.sigma.av
  assortativity.av.X.sigma           <- assortativity.av    * sigma
  assortativity.av.X.gamma           <- assortativity.av    * gamma
  assortativity.av.X.r.sigma.av      <- assortativity.av    * r.sigma.av
  sigma.X.gamma                      <- sigma               * gamma
  sigma.X.r.sigma.av                 <- sigma               * r.sigma.av
  gamma.X.r.sigma.av                 <- gamma               * r.sigma.av
  model.3.netchanges <- lm(net.changes.dv ~
                                     degree.av +
                                     clustering.av +
                                     pathlength.av +
                                     assortativity.av +
                                     sigma +
                                     gamma +
                                     r.sigma.av +

                                     degree.av.X.clustering.av +
                                     degree.av.X.pathlength.av +
                                     # degree.av.X.assortativity.av +
                                     # degree.av.X.sigma +
                                     # degree.av.X.gamma +
                                     # degree.av.X.r.sigma.av +
                                     # clustering.av.X.pathlength.av +
                                     clustering.av.X.assortativity.av +
                                     # clustering.av.X.sigma +
                                     # clustering.av.X.gamma +
                                     # clustering.av.X.r.sigma.av +
                                     # pathlength.av.X.assortativity.av +
                                     # pathlength.av.X.sigma +
                                     pathlength.av.X.gamma +
                                     # pathlength.av.X.r.sigma.av +
                                     # assortativity.av.X.sigma +
                                     assortativity.av.X.gamma
                                     # assortativity.av.X.r.sigma.av +
                                     # sigma.X.gamma
                                     # sigma.X.r.sigma.av +
                                     # gamma.X.r.sigma.av
  )
  if (print.summaries) {
    print(summary(model.3.netchanges))
    print(vif(model.3.netchanges))
  }

  # attack rate
  model.2.attackrate <- glm(attack.rate ~
                                      net.changes +
                                      degree.av +
                                      clustering.av +
                                      pathlength.av +
                                      assortativity.av +
                                      sigma +
                                      gamma +
                                      r.sigma.av,
                                    family = binomial)
  if (print.summaries) {
    print(summary(model.2.attackrate))
    print(vif(model.2.attackrate))
    print(print_r2(model.2.attackrate))
  }

  # export_interactions(c("net.changes",
  #                       "degree.av",
  #                       "clustering.av",
  #                       "pathlength.av",
  #                       "assortativity.av",
  #                       "sigma",
  #                       "gamma",
  #                       "r.sigma.av"),
  #                     "network", c(35, 20))
  net.changes.X.degree.av            <- net.changes         * degree.av
  net.changes.X.clustering.av        <- net.changes         * clustering.av
  net.changes.X.pathlength.av        <- net.changes         * pathlength.av
  net.changes.X.assortativity.av     <- net.changes         * assortativity.av
  net.changes.X.sigma                <- net.changes         * sigma
  net.changes.X.gamma                <- net.changes         * gamma
  net.changes.X.r.sigma.av           <- net.changes         * r.sigma.av
  degree.av.X.clustering.av          <- degree.av           * clustering.av
  degree.av.X.pathlength.av          <- degree.av           * pathlength.av
  degree.av.X.assortativity.av       <- degree.av           * assortativity.av
  degree.av.X.sigma                  <- degree.av           * sigma
  degree.av.X.gamma                  <- degree.av           * gamma
  degree.av.X.r.sigma.av             <- degree.av           * r.sigma.av
  clustering.av.X.pathlength.av      <- clustering.av       * pathlength.av
  clustering.av.X.assortativity.av   <- clustering.av       * assortativity.av
  clustering.av.X.sigma              <- clustering.av       * sigma
  clustering.av.X.gamma              <- clustering.av       * gamma
  clustering.av.X.r.sigma.av         <- clustering.av       * r.sigma.av
  pathlength.av.X.assortativity.av   <- pathlength.av       * assortativity.av
  pathlength.av.X.sigma              <- pathlength.av       * sigma
  pathlength.av.X.gamma              <- pathlength.av       * gamma
  pathlength.av.X.r.sigma.av         <- pathlength.av       * r.sigma.av
  assortativity.av.X.sigma           <- assortativity.av    * sigma
  assortativity.av.X.gamma           <- assortativity.av    * gamma
  assortativity.av.X.r.sigma.av      <- assortativity.av    * r.sigma.av
  sigma.X.gamma                      <- sigma               * gamma
  sigma.X.r.sigma.av                 <- sigma               * r.sigma.av
  gamma.X.r.sigma.av                 <- gamma               * r.sigma.av

  model.3.attackrate <- glm(attack.rate ~
                                      net.changes +
                                      degree.av +
                                      clustering.av +
                                      pathlength.av +
                                      assortativity.av +
                                      sigma +
                                      gamma +
                                      r.sigma.av +

                                      # net.changes.X.degree.av +
                                      net.changes.X.clustering.av +
                                      # net.changes.X.pathlength.av +
                                      net.changes.X.assortativity.av +
                                      # net.changes.X.sigma +
                                      net.changes.X.gamma +
                                      # net.changes.X.r.sigma.av +
                                      # degree.av.X.clustering.av +
                                      # degree.av.X.pathlength.av +
                                      # degree.av.X.assortativity.av +
                                      # degree.av.X.sigma +
                                      # degree.av.X.gamma +
                                      # degree.av.X.r.sigma.av +
                                      # clustering.av.X.pathlength.av +
                                      # clustering.av.X.assortativity.av +
                                      # clustering.av.X.sigma +
                                      # clustering.av.X.gamma +
                                      # clustering.av.X.r.sigma.av +
                                      # pathlength.av.X.assortativity.av +
                                      # pathlength.av.X.sigma +
                                      pathlength.av.X.gamma +
                                      # pathlength.av.X.r.sigma.av +
                                      # assortativity.av.X.sigma +
                                      assortativity.av.X.gamma
                                      # assortativity.av.X.r.sigma.av +
                                      # sigma.X.gamma +
                                      # sigma.X.r.sigma.av
                                      # gamma.X.r.sigma.av
                                    ,
                                    family = binomial)
  if (print.summaries) {
    print(summary(model.3.attackrate))
    print(print_r2(model.3.attackrate))
    print(vif(model.3.attackrate))
  }

  # duration
  model.2.duration <- lm(duration ~
                                   net.changes +
                                   degree.av +
                                   clustering.av +
                                   pathlength.av +
                                   assortativity.av +
                                   sigma +
                                   gamma +
                                   r.sigma.av +

                                   attack.rate.dyn.iv
                                 )
  if (print.summaries) {
    print(summary(model.2.duration))
    print(vif(model.2.duration))
  }
  model.3.duration <- lm(duration ~
                                   net.changes +
                                   degree.av +
                                   clustering.av +
                                   pathlength.av +
                                   assortativity.av +
                                   sigma +
                                   gamma +
                                   r.sigma.av +

                                   attack.rate.dyn.iv +

                                   net.changes.X.degree.av +
                                   net.changes.X.clustering.av +
                                   net.changes.X.pathlength.av +
                                   # net.changes.X.assortativity.av +
                                   # net.changes.X.sigma +
                                   net.changes.X.gamma +
                                   # net.changes.X.r.sigma.av +
                                   # degree.av.X.clustering.av +
                                   # degree.av.X.pathlength.av +
                                   # degree.av.X.assortativity.av +
                                   # degree.av.X.sigma +
                                   # degree.av.X.gamma +
                                   # degree.av.X.r.sigma.av +
                                   # clustering.av.X.pathlength.av +
                                   clustering.av.X.assortativity.av
                                   # clustering.av.X.sigma +
                                   # clustering.av.X.gamma
                                   # clustering.av.X.r.sigma.av +
                                   # pathlength.av.X.assortativity.av +
                                   # pathlength.av.X.sigma +
                                   # pathlength.av.X.gamma +
                                   # pathlength.av.X.r.sigma.av +
                                   # assortativity.av.X.sigma +
                                   # assortativity.av.X.gamma +
                                   # assortativity.av.X.r.sigma.av +
                                   # sigma.X.gamma +
                                   # sigma.X.r.sigma.av +
                                   # gamma.X.r.sigma.av
  )
  if (print.summaries) {
    print(summary(model.3.duration))
    print(vif(model.3.duration))
  }

  # peak size
  model.2.peak.size <- lm(peak.size ~
                                    net.changes +
                                    degree.av +
                                    clustering.av +
                                    pathlength.av +
                                    assortativity.av +
                                    sigma +
                                    gamma +
                                    r.sigma.av
                                  )
  if (print.summaries) {
    print(summary(model.2.peak.size))
    print(vif(model.2.peak.size))
  }

  model.3.peak.size <- lm(peak.size ~
                                    net.changes +
                                    degree.av +
                                    clustering.av +
                                    pathlength.av +
                                    assortativity.av +
                                    sigma +
                                    gamma +
                                    r.sigma.av +

                                    # net.changes.X.degree.av +
                                    net.changes.X.clustering.av +
                                    # net.changes.X.pathlength.av +
                                    net.changes.X.assortativity.av +
                                    # net.changes.X.sigma +
                                    net.changes.X.gamma +
                                    # net.changes.X.r.sigma.av +
                                    # degree.av.X.clustering.av +
                                    # degree.av.X.pathlength.av +
                                    # degree.av.X.assortativity.av +
                                    # degree.av.X.sigma +
                                    # degree.av.X.gamma +
                                    # degree.av.X.r.sigma.av +
                                    # clustering.av.X.pathlength.av +
                                    clustering.av.X.assortativity.av +
                                    # clustering.av.X.sigma +
                                    # clustering.av.X.gamma +
                                    # clustering.av.X.r.sigma.av +
                                    # pathlength.av.X.assortativity.av +
                                    # pathlength.av.X.sigma +
                                    pathlength.av.X.gamma +
                                    # pathlength.av.X.r.sigma.av +
                                    # assortativity.av.X.sigma +
                                    assortativity.av.X.gamma
                                    # assortativity.av.X.r.sigma.av +
                                    # sigma.X.gamma +
                                    # sigma.X.r.sigma.av
                                    # gamma.X.r.sigma.av
  )
  if (print.summaries) {
    print(summary(model.3.peak.size))
    print(vif(model.3.peak.size))
  }

  ### FILE EXPORT ###
  filename <- "dynamic-reg-01-netchanges"
  if (filenamname.appendix != "") {
    filename <- paste(filename, "-", filenamname.appendix, sep = "")
  }
  exportModels(list(model.2.netchanges,
                    model.3.netchanges), filename)
  filename <- "dynamic-reg-02-attackrate"
  if (filenamname.appendix != "") {
    filename <- paste(filename, "-", filenamname.appendix, sep = "")
  }
  exportModels(list(model.1.attackrate,
                    model.2.attackrate,
                    model.3.attackrate), filename)
  filename <- "dynamic-reg-03-duration"
  if (filenamname.appendix != "") {
    filename <- paste(filename, "-", filenamname.appendix, sep = "")
  }
  exportModels(list(model.1.duration,
                    model.2.duration,
                    model.3.duration), filename)
  filename <- "dynamic-reg-04-peaksize"
  if (filenamname.appendix != "") {
    filename <- paste(filename, "-", filenamname.appendix, sep = "")
  }
  exportModels(list(model.1.peak.size,
                    model.2.peak.size,
                    model.3.peak.size), filename)



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
  if (print.summaries) {
    print(summary(model.1.attackrate.static))
    print(print_r2(model.1.attackrate.static))
  }
  # duration
  model.1.duration.static <- lm(duration ~
                                   net.changes)
  if (print.summaries) {
    print(summary(model.1.duration.static))
    print(print_r2(model.1.duration.static))
  }
  # peak size
  model.1.peak.size.static <- lm(peak.size ~
                                    net.changes)
  if (print.summaries) {
    print(summary(model.1.peak.size.static))
    print(print_r2(model.1.peak.size.static))
  }

  ## MAIN EFFECTS (ACTUAL NETWORK PROPERTIES RATHER THAN PARAMETERS TO CONTROL NETWORK PROPERTIES)
  # attack rate
  model.2.attackrate.static <- glm(attack.rate ~
                                     degree.av +
                                     clustering.av +
                                     pathlength.av +
                                     assortativity.av +
                                     sigma +
                                     gamma +
                                     r.sigma.av
                                   ,
                                   family = binomial)
  if (print.summaries) {
    print(summary(model.2.attackrate.static))
    print(vif(model.2.attackrate.static))
    print(print_r2(model.2.attackrate.static))
  }

  # export_interactions(c(
  #                       "degree.av",
  #                       "clustering.av",
  #                       "pathlength.av",
  #                       "assortativity.av",
  #                       "sigma",
  #                       "gamma",
  #                       "r.sigma.av"),
  #                     "network", c(35, 20))
  degree.av.X.clustering.av          <- degree.av           * clustering.av
  degree.av.X.pathlength.av          <- degree.av           * pathlength.av
  degree.av.X.assortativity.av       <- degree.av           * assortativity.av
  degree.av.X.sigma                  <- degree.av           * sigma
  degree.av.X.gamma                  <- degree.av           * gamma
  degree.av.X.r.sigma.av             <- degree.av           * r.sigma.av
  clustering.av.X.pathlength.av      <- clustering.av       * pathlength.av
  clustering.av.X.assortativity.av   <- clustering.av       * assortativity.av
  clustering.av.X.sigma              <- clustering.av       * sigma
  clustering.av.X.gamma              <- clustering.av       * gamma
  clustering.av.X.r.sigma.av         <- clustering.av       * r.sigma.av
  pathlength.av.X.assortativity.av   <- pathlength.av       * assortativity.av
  pathlength.av.X.sigma              <- pathlength.av       * sigma
  pathlength.av.X.gamma              <- pathlength.av       * gamma
  pathlength.av.X.r.sigma.av         <- pathlength.av       * r.sigma.av
  assortativity.av.X.sigma           <- assortativity.av    * sigma
  assortativity.av.X.gamma           <- assortativity.av    * gamma
  assortativity.av.X.r.sigma.av      <- assortativity.av    * r.sigma.av
  sigma.X.gamma                      <- sigma               * gamma
  sigma.X.r.sigma.av                 <- sigma               * r.sigma.av
  gamma.X.r.sigma.av                 <- gamma               * r.sigma.av

  model.3.attackrate.static <- glm(attack.rate ~
                                     degree.av +
                                     clustering.av +
                                     pathlength.av +
                                     assortativity.av +
                                     sigma +
                                     gamma +
                                     r.sigma.av +

                                     # degree.av.X.clustering.av +
                                     # degree.av.X.pathlength.av +
                                     # degree.av.X.assortativity.av +
                                     # degree.av.X.sigma +
                                     # degree.av.X.gamma +
                                     # degree.av.X.r.sigma.av +
                                     # clustering.av.X.pathlength.av +
                                     # clustering.av.X.assortativity.av +
                                     # clustering.av.X.sigma +
                                     # clustering.av.X.gamma +
                                     # clustering.av.X.r.sigma.av +
                                     # pathlength.av.X.assortativity.av +
                                     # pathlength.av.X.sigma +
                                     pathlength.av.X.gamma
                                     # pathlength.av.X.r.sigma.av +
                                     # assortativity.av.X.sigma +
                                     # assortativity.av.X.gamma +
                                     # assortativity.av.X.r.sigma.av +
                                     # sigma.X.gamma +
                                     # sigma.X.r.sigma.av +
                                     # gamma.X.r.sigma.av
                                   ,
                                   family = binomial)
  if (print.summaries) {
    print(summary(model.3.attackrate.static))
    print(vif(model.3.attackrate.static))
    print(print_r2(model.3.attackrate.static))
  }

  # duration
  model.2.duration.static <- lm(duration ~
                                  degree.av +
                                  clustering.av +
                                  pathlength.av +
                                  assortativity.av +
                                  sigma +
                                  gamma +
                                  r.sigma.av
                                )
  if (print.summaries) {
    print(summary(model.2.duration.static))
    print(vif(model.2.duration.static))
  }

  model.3.duration.static <- lm(duration ~
                                  degree.av +
                                  clustering.av +
                                  pathlength.av +
                                  assortativity.av +
                                  sigma +
                                  gamma +
                                  r.sigma.av +

                                  # degree.av.X.clustering.av +
                                  # degree.av.X.pathlength.av +
                                  # degree.av.X.assortativity.av +
                                  # degree.av.X.sigma +
                                  degree.av.X.gamma +
                                  # degree.av.X.r.sigma.av +
                                  # clustering.av.X.pathlength.av +
                                  # clustering.av.X.assortativity.av +
                                  # clustering.av.X.sigma +
                                  # clustering.av.X.gamma +
                                  # clustering.av.X.r.sigma.av +
                                  # pathlength.av.X.assortativity.av +
                                  # pathlength.av.X.sigma +
                                  pathlength.av.X.gamma
                                  # pathlength.av.X.r.sigma.av +
                                  # assortativity.av.X.sigma +
                                  # assortativity.av.X.gamma +
                                  # assortativity.av.X.r.sigma.av +
                                  # sigma.X.gamma +
                                  # sigma.X.r.sigma.av +
                                  # gamma.X.r.sigma.av
  )
  if (print.summaries) {
    print(summary(model.3.duration.static))
    print(vif(model.3.duration.static))
  }

  # peak size
  model.2.peak.size.static <- lm(peak.size ~
                                   degree.av +
                                   clustering.av +
                                   pathlength.av +
                                   assortativity.av +
                                   sigma +
                                   gamma +
                                   r.sigma.av
                                 )
  if (print.summaries) {
    print(summary(model.2.peak.size.static))
    print(vif(model.2.peak.size.static))
  }

  model.3.peak.size.static <- lm(peak.size ~
                                   degree.av +
                                   clustering.av +
                                   pathlength.av +
                                   assortativity.av +
                                   sigma +
                                   gamma +
                                   r.sigma.av +

                                   # degree.av.X.clustering.av +
                                   # degree.av.X.pathlength.av +
                                   # degree.av.X.assortativity.av +
                                   # degree.av.X.sigma +
                                   # degree.av.X.gamma +
                                   # degree.av.X.r.sigma.av +
                                   # clustering.av.X.pathlength.av +
                                   # clustering.av.X.assortativity.av +
                                   # clustering.av.X.sigma +
                                   # clustering.av.X.gamma +
                                   # clustering.av.X.r.sigma.av +
                                   # pathlength.av.X.assortativity.av +
                                   # pathlength.av.X.sigma +
                                   pathlength.av.X.gamma
                                   # pathlength.av.X.r.sigma.av +
                                   # assortativity.av.X.sigma +
                                   # assortativity.av.X.gamma +
                                   # assortativity.av.X.r.sigma.av +
                                   # sigma.X.gamma +
                                   # sigma.X.r.sigma.av +
                                   # gamma.X.r.sigma.av
  )
  if (print.summaries) {
    print(summary(model.3.peak.size.static))
    print(vif(model.3.peak.size.static))
  }


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
  filename <- "static-reg-04-peaksize"
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
  # xtabs(~ net.pct.rec + net.stable.pre, data = data.ad)

  data.ad <- subset(data.ad, agent.force.infected == 0)

  ###### DYNAMIC NETWORKS ######
  data.ad <- subset(data.ad, nb.ep.structure == "dynamic")

  #### DATA PREPARATIONS ####
  ### DEPENDENT ###
  prob.infection                <- data.ad$agent.infected
  ### INDEPENDENT ###
  ## MAIN EFFECTS
  net.changes                   <- prepare_predictor(data.ad$agent.cons.broken.active.epidemic +
                                                       data.ad$agent.cons.out.accepted.epidemic)
  sigma                         <- prepare_predictor(data.ad$nb.sigma)
  gamma                         <- prepare_predictor(data.ad$nb.gamma)
  degree.av                     <- prepare_predictor(data.ad$net.degree.pre.epidemic.av)
  clustering.av                 <- prepare_predictor(data.ad$net.clustering.pre.epidemic.av)
  pathlength.av                 <- prepare_predictor(data.ad$net.pathlength.pre.epidemic.av)
  # betweenness.av                <- prepare_predictor(data.ad$net.betweenness.pre.epidemic.av)
  # closeness.av                  <- prepare_predictor(data.ad$net.closeness.pre.epidemic.av)
  assortativity.av              <- prepare_predictor(data.ad$net.assortativity.pre.epidemic)
  # r.sigma.av                    <- prepare_predictor(data.ad$nb.r.sigma.av)
  # degree.index                  <- prepare_predictor(data.ad$index.degree)
  # clustering.index              <- prepare_predictor(data.ad$index.clustering)
  betweenness.index             <- prepare_predictor(data.ad$index.betweenness.normalized)
  # closeness.index               <- prepare_predictor(data.ad$index.closeness)
  # assortativity.index           <- prepare_predictor(data.ad$index.assortativity)
  # r.sigma.index                 <- prepare_predictor(data.ad$index.r.sigma)
  # r.sigma.neighborhood.index    <- prepare_predictor(data.ad$index.r.sigma.neighborhood)
  # degree.agent                  <- prepare_predictor(data.ad$agent.degree)
  # clustering.agent              <- prepare_predictor(data.ad$agent.clustering)
  # betweenness.agent             <- prepare_predictor(data.ad$agent.betweenness.normalized)
  # closeness.agent               <- prepare_predictor(data.ad$agent.closeness)
  # assortativity.agent           <- prepare_predictor(data.ad$agent.assortativity)
  index.distance.agent          <- prepare_predictor(data.ad$agent.index.distance)
  r.sigma.agent                 <- prepare_predictor(data.ad$nb.r.sigma)
  # r.sigma.agent.neighborhood    <- prepare_predictor(data.ad$nb.r.sigma.neighborhood)

  # dynamics
  log.1 <- glm(prob.infection ~
                         net.changes,
                       family = binomial)
  summary(log.1)
  print_r2(log.1)

  # main effects
  log.2 <- glm(prob.infection ~
                         net.changes +
                         sigma +
                         gamma +
                         r.sigma.agent +
                         degree.av +
                         clustering.av +
                         pathlength.av +
                         betweenness.index +
                         assortativity.av +
                         index.distance.agent,

                         # r.sigma.av +
                         # r.sigma.index +
                         # r.sigma.neighborhood.index +
                         # r.sigma.agent.neighborhood +
                         # degree.index,
                         # degree.agent,
                         # clustering.index,
                         # clustering.agent,
                         # betweenness.av +
                         # closeness.av +
                         # betweenness.agent,
                         # closeness.index +
                         # closeness.agent +
                         # assortativity.index,
                         # assortativity.agent,

                       family = binomial)
  summary(log.2)
  vif(log.2)
  print_r2(log.2)

  # INTERACTION EFFECTS (export significant main effects)
  # export_interactions(c("net.changes",
  #                       "sigma",
  #                       "gamma",
  #                       "degree.av",
  #                       "clustering.av",
  #                       "pathlength.av",
  #                       # "betweenness.av",
  #                       # "closeness.av",
  #                       "assortativity.av",
  #                       # "r.sigma.av",
  #                       # "degree.index",
  #                       # "clustering.index",
  #                       "betweenness.index",
  #                       # "closeness.index",
  #                       # "assortativity.index",
  #                       # "r.sigma.index",
  #                       # "r.sigma.neighborhood.index",
  #                       # "degree.agent",
  #                       # "clustering.agent",
  #                       # "betweenness.agent",
  #                       # "closeness.agent",
  #                       # "assortativity.agent",
  #                       "index.distance.agent",
  #                       "r.sigma.agent"
  #                       # "r.sigma.agent.neighborhood"
  #                     ),
  #                     "agent", c(50, 30))
  # net.changes.X.sigma                               <- net.changes                   * sigma
  # net.changes.X.gamma                               <- net.changes                   * gamma
  # net.changes.X.degree.av                           <- net.changes                   * degree.av
  # net.changes.X.clustering.av                       <- net.changes                   * clustering.av
  # net.changes.X.pathlength.av                       <- net.changes                   * pathlength.av
  # net.changes.X.assortativity.av                    <- net.changes                   * assortativity.av
  # net.changes.X.betweenness.index                   <- net.changes                   * betweenness.index
  net.changes.X.index.distance.agent                <- net.changes                   * index.distance.agent
  # net.changes.X.r.sigma.agent                       <- net.changes                   * r.sigma.agent
  # sigma.X.gamma                                     <- sigma                         * gamma
  # sigma.X.degree.av                                 <- sigma                         * degree.av
  # sigma.X.clustering.av                             <- sigma                         * clustering.av
  # sigma.X.pathlength.av                             <- sigma                         * pathlength.av
  # sigma.X.assortativity.av                          <- sigma                         * assortativity.av
  # sigma.X.betweenness.index                         <- sigma                         * betweenness.index
  # sigma.X.index.distance.agent                      <- sigma                         * index.distance.agent
  # sigma.X.r.sigma.agent                             <- sigma                         * r.sigma.agent
  gamma.X.degree.av                                 <- gamma                         * degree.av
  # gamma.X.clustering.av                             <- gamma                         * clustering.av
  # gamma.X.pathlength.av                             <- gamma                         * pathlength.av
  gamma.X.assortativity.av                          <- gamma                         * assortativity.av
  # gamma.X.betweenness.index                         <- gamma                         * betweenness.index
  # gamma.X.index.distance.agent                      <- gamma                         * index.distance.agent
  # gamma.X.r.sigma.agent                             <- gamma                         * r.sigma.agent
  degree.av.X.clustering.av                         <- degree.av                     * clustering.av
  # degree.av.X.pathlength.av                         <- degree.av                     * pathlength.av
  degree.av.X.assortativity.av                      <- degree.av                     * assortativity.av
  # degree.av.X.betweenness.index                     <- degree.av                     * betweenness.index
  # degree.av.X.index.distance.agent                  <- degree.av                     * index.distance.agent
  # degree.av.X.r.sigma.agent                         <- degree.av                     * r.sigma.agent
  clustering.av.X.pathlength.av                     <- clustering.av                 * pathlength.av
  # clustering.av.X.assortativity.av                  <- clustering.av                 * assortativity.av
  # clustering.av.X.betweenness.index                 <- clustering.av                 * betweenness.index
  # clustering.av.X.index.distance.agent              <- clustering.av                 * index.distance.agent
  # clustering.av.X.r.sigma.agent                     <- clustering.av                 * r.sigma.agent
  # pathlength.av.X.assortativity.av                  <- pathlength.av                 * assortativity.av
  # pathlength.av.X.betweenness.index                 <- pathlength.av                 * betweenness.index
  # pathlength.av.X.index.distance.agent              <- pathlength.av                 * index.distance.agent
  # pathlength.av.X.r.sigma.agent                     <- pathlength.av                 * r.sigma.agent
  # assortativity.av.X.betweenness.index              <- assortativity.av              * betweenness.index
  # assortativity.av.X.index.distance.agent           <- assortativity.av              * index.distance.agent
  # assortativity.av.X.r.sigma.agent                  <- assortativity.av              * r.sigma.agent
  # betweenness.index.X.index.distance.agent          <- betweenness.index             * index.distance.agent
  # betweenness.index.X.r.sigma.agent                 <- betweenness.index             * r.sigma.agent
  # index.distance.agent.X.r.sigma.agent              <- index.distance.agent          * r.sigma.agent

  log.3 <- glm(prob.infection ~
                         net.changes +
                         sigma +
                         gamma +
                         degree.av +
                         clustering.av +
                         pathlength.av +
                         assortativity.av +
                         betweenness.index +
                         index.distance.agent +
                         r.sigma.agent +

                         net.changes.X.index.distance.agent +
                         gamma.X.degree.av +
                         gamma.X.assortativity.av +
                         # degree.av.X.clustering.av +
                         degree.av.X.assortativity.av
                         # clustering.av.X.pathlength.av

                         # net.changes.X.sigma
                         # net.changes.X.gamma
                         # net.changes.X.degree.av
                         # net.changes.X.clustering.av
                         # net.changes.X.pathlength.av
                         # net.changes.X.assortativity.av
                         # net.changes.X.betweenness.index
                         # net.changes.X.r.sigma.agent
                         # sigma.X.gamma
                         # sigma.X.degree.av
                         # sigma.X.clustering.av
                         # sigma.X.pathlength.av
                         # sigma.X.assortativity.av
                         # sigma.X.betweenness.index
                         # sigma.X.index.distance.agent
                         # sigma.X.r.sigma.agent
                         # gamma.X.clustering.av
                         # gamma.X.pathlength.av
                         # gamma.X.betweenness.index
                         # gamma.X.index.distance.agent
                         # gamma.X.r.sigma.agent
                         # degree.av.X.clustering.av
                         # degree.av.X.pathlength.av
                         # degree.av.X.betweenness.index
                         # degree.av.X.index.distance.agent
                         # degree.av.X.r.sigma.agent
                         # clustering.av.X.assortativity.av
                         # clustering.av.X.betweenness.index
                         # clustering.av.X.index.distance.agent
                         # clustering.av.X.r.sigma.agent
                         # pathlength.av.X.assortativity.av
                         # pathlength.av.X.betweenness.index
                         # pathlength.av.X.index.distance.agent
                         # pathlength.av.X.r.sigma.agent
                         # assortativity.av.X.betweenness.index
                         # assortativity.av.X.index.distance.agent
                         # assortativity.av.X.r.sigma.agent
                         # betweenness.index.X.index.distance.agent
                         # betweenness.index.X.r.sigma.agent
                         # index.distance.agent.X.r.sigma.agent
                       ,
                       family = binomial)
  summary(log.3)
  print_r2(log.3)
  vif(log.3)

  ### FILE EXPORT ###
  filename <- "dynamic-reg-01-probinfections"
  if (filenamname.appendix != "") {
    filename <- paste(filename, "-", filenamname.appendix, sep = "")
  }
  exportModels(list(log.1,
                    log.2,
                    log.3), filename)


  ###### STATIC NETWORKS ######
  data.ad.static <- subset(data.ad, nb.ep.structure == "static")

  #### DATA PREPARATIONS ####
  ### DEPENDENT ###
  prob.infection                <- data.ad.static$agent.infected
  ### INDEPENDENT ###
  ## MAIN EFFECTS
  # net.changes                   <- prepare_predictor(data.ad.static$agent.cons.broken.active.epidemic +
  #                                                      data.ad.static$agent.cons.out.accepted.epidemic)
  # net.changes[is.na(net.changes)] <- 0
  net.changes                   <- rep(0, length(prob.infection))
  sigma                         <- prepare_predictor(data.ad.static$nb.sigma)
  gamma                         <- prepare_predictor(data.ad.static$nb.gamma)
  degree.av                     <- prepare_predictor(data.ad.static$net.degree.pre.epidemic.av)
  clustering.av                 <- prepare_predictor(data.ad.static$net.clustering.pre.epidemic.av)
  pathlength.av                 <- prepare_predictor(data.ad.static$net.pathlength.pre.epidemic.av)
  # betweenness.av                <- prepare_predictor(data.ad.static$net.betweenness.pre.epidemic.av)
  # closeness.av                  <- prepare_predictor(data.ad.static$net.closeness.pre.epidemic.av)
  assortativity.av              <- prepare_predictor(data.ad.static$net.assortativity.pre.epidemic)
  # r.sigma.av                    <- prepare_predictor(data.ad.static$nb.r.sigma.av)
  # degree.index                  <- prepare_predictor(data.ad.static$index.degree)
  # clustering.index              <- prepare_predictor(data.ad.static$index.clustering)
  betweenness.index             <- prepare_predictor(data.ad.static$index.betweenness.normalized)
  # closeness.index               <- prepare_predictor(data.ad.static$index.closeness)
  # assortativity.index           <- prepare_predictor(data.ad.static$index.assortativity)
  # r.sigma.index                 <- prepare_predictor(data.ad.static$index.r.sigma)
  # r.sigma.neighborhood.index    <- prepare_predictor(data.ad.static$index.r.sigma.neighborhood)
  # degree.agent                  <- prepare_predictor(data.ad.static$agent.degree)
  # clustering.agent              <- prepare_predictor(data.ad.static$agent.clustering)
  # betweenness.agent             <- prepare_predictor(data.ad.static$agent.betweenness.normalized)
  # closeness.agent               <- prepare_predictor(data.ad.static$agent.closeness)
  # assortativity.agent           <- prepare_predictor(data.ad.static$agent.assortativity)
  index.distance.agent          <- prepare_predictor(data.ad.static$agent.index.distance)
  r.sigma.agent                 <- prepare_predictor(data.ad.static$nb.r.sigma)
  # r.sigma.agent.neighborhood    <- prepare_predictor(data.ad.static$nb.r.sigma.neighborhood)

  # dynamics
  log.1.static <- glm(prob.infection ~
                         net.changes,
                       family = binomial)
  # summary(log.1.static)
  # print_r2(log.1.static)

  # main effects
  log.2.static <- glm(prob.infection ~
                         # net.changes +
                         sigma +
                         gamma +
                         degree.av +
                         clustering.av +
                         pathlength.av +
                         # betweenness.av +
                         # closeness.av +
                         assortativity.av +
                         # r.sigma.av +
                         # degree.index +
                         # clustering.index +
                         betweenness.index +
                         # closeness.index +
                         # assortativity.index +
                         # r.sigma.index +
                         # r.sigma.neighborhood.index +
                         # degree.agent +
                         # clustering.agent +
                         # betweenness.agent +
                         # closeness.agent +
                         # assortativity.agent +
                         index.distance.agent +
                         r.sigma.agent,
                         # r.sigma.agent.neighborhood,
                       family = binomial)
  summary(log.2.static)
  vif(log.2.static)
  print_r2(log.2.static)

  ## INTERACTION EFFECTS (export significant main effects)
  # export_interactions(c(
  #                       # "net.changes",
  #                       "sigma",
  #                       "gamma",
  #                       "degree.av",
  #                       "clustering.av",
  #                       "pathlength.av",
  #                       # "betweenness.av",
  #                       # "closeness.av",
  #                       "assortativity.av",
  #                       # "r.sigma.av",
  #                       # "degree.index",
  #                       # "clustering.index",
  #                       "betweenness.index",
  #                       # "closeness.index",
  #                       # "assortativity.index",
  #                       # "r.sigma.index",
  #                       # "r.sigma.neighborhood.index",
  #                       # "degree.agent",
  #                       # "clustering.agent",
  #                       # "betweenness.agent",
  #                       # "closeness.agent",
  #                       # "assortativity.agent",
  #                       "index.distance.agent",
  #                       "r.sigma.agent"
  #                       # "r.sigma.agent.neighborhood"
  #                       ),
  #                     "agent", c(50, 30))

  # sigma.X.gamma                                     <- sigma                         * gamma
  # sigma.X.degree.av                                 <- sigma                         * degree.av
  # sigma.X.clustering.av                             <- sigma                         * clustering.av
  # sigma.X.pathlength.av                             <- sigma                         * pathlength.av
  # sigma.X.assortativity.av                          <- sigma                         * assortativity.av
  # sigma.X.betweenness.index                         <- sigma                         * betweenness.index
  # sigma.X.index.distance.agent                      <- sigma                         * index.distance.agent
  # sigma.X.r.sigma.agent                             <- sigma                         * r.sigma.agent
  gamma.X.degree.av                                 <- gamma                         * degree.av
  gamma.X.clustering.av                             <- gamma                         * clustering.av
  gamma.X.pathlength.av                             <- gamma                         * pathlength.av
  # gamma.X.assortativity.av                          <- gamma                         * assortativity.av
  # gamma.X.betweenness.index                         <- gamma                         * betweenness.index
  # gamma.X.index.distance.agent                      <- gamma                         * index.distance.agent
  # gamma.X.r.sigma.agent                             <- gamma                         * r.sigma.agent
  # degree.av.X.clustering.av                         <- degree.av                     * clustering.av
  # degree.av.X.pathlength.av                         <- degree.av                     * pathlength.av
  # degree.av.X.assortativity.av                      <- degree.av                     * assortativity.av
  # degree.av.X.betweenness.index                     <- degree.av                     * betweenness.index
  # degree.av.X.index.distance.agent                  <- degree.av                     * index.distance.agent
  # degree.av.X.r.sigma.agent                         <- degree.av                     * r.sigma.agent
  # clustering.av.X.pathlength.av                     <- clustering.av                 * pathlength.av
  # clustering.av.X.assortativity.av                  <- clustering.av                 * assortativity.av
  # clustering.av.X.betweenness.index                 <- clustering.av                 * betweenness.index
  # clustering.av.X.index.distance.agent              <- clustering.av                 * index.distance.agent
  # clustering.av.X.r.sigma.agent                     <- clustering.av                 * r.sigma.agent
  # pathlength.av.X.assortativity.av                  <- pathlength.av                 * assortativity.av
  # pathlength.av.X.betweenness.index                 <- pathlength.av                 * betweenness.index
  pathlength.av.X.index.distance.agent              <- pathlength.av                 * index.distance.agent
  # pathlength.av.X.r.sigma.agent                     <- pathlength.av                 * r.sigma.agent
  # assortativity.av.X.betweenness.index              <- assortativity.av              * betweenness.index
  # assortativity.av.X.index.distance.agent           <- assortativity.av              * index.distance.agent
  # assortativity.av.X.r.sigma.agent                  <- assortativity.av              * r.sigma.agent
  # betweenness.index.X.index.distance.agent          <- betweenness.index             * index.distance.agent
  # betweenness.index.X.r.sigma.agent                 <- betweenness.index             * r.sigma.agent
  # index.distance.agent.X.r.sigma.agent              <- index.distance.agent          * r.sigma.agent

  log.3.static <- glm(prob.infection ~
                        sigma +
                        gamma +
                        degree.av +
                        clustering.av +
                        pathlength.av +
                        assortativity.av +
                        betweenness.index +
                        index.distance.agent +
                        r.sigma.agent +

                        gamma.X.degree.av +
                        # gamma.X.clustering.av
                        gamma.X.pathlength.av
                        # degree.av.X.index.distance.agent +
                        # clustering.av.X.index.distance.agent +
                        # pathlength.av.X.index.distance.agent

                        # sigma.X.gamma
                        # sigma.X.degree.av
                        # sigma.X.clustering.av
                        # sigma.X.pathlength.av
                        # sigma.X.assortativity.av
                        # sigma.X.betweenness.index
                        # sigma.X.index.distance.agent
                        # sigma.X.r.sigma.agent
                        # gamma.X.assortativity.av
                        # gamma.X.betweenness.index
                        # gamma.X.index.distance.agent
                        # gamma.X.r.sigma.agent
                        # degree.av.X.clustering.av
                        # degree.av.X.pathlength.av
                        # degree.av.X.assortativity.av
                        # degree.av.X.betweenness.index
                        # degree.av.X.r.sigma.agent
                        # clustering.av.X.pathlength.av
                        # clustering.av.X.assortativity.av
                        # clustering.av.X.betweenness.index
                        # clustering.av.X.r.sigma.agent
                        # pathlength.av.X.assortativity.av
                        # pathlength.av.X.betweenness.index
                        # pathlength.av.X.r.sigma.agent
                        # assortativity.av.X.betweenness.index
                        # assortativity.av.X.index.distance.agent
                        # assortativity.av.X.r.sigma.agent
                        # betweenness.index.X.index.distance.agent
                        # betweenness.index.X.r.sigma.agent
                        # index.distance.agent.X.r.sigma.agent
                       ,
                       family = binomial)
  summary(log.3.static)
  print_r2(log.3.static)
  vif(log.3.static)

  ### FILE EXPORT ###
  filename <- "static-reg-01-probinfections"
  if (filenamname.appendix != "") {
    filename <- paste(filename, "-", filenamname.appendix, sep = "")
  }
  exportModels(list(log.1.static,
                    log.2.static,
                    log.3.static), filename)

}





export_experiment_plots <- function(data.ss = load_simulation_summary_data(), addendum = "", filename.prefix = "") {


  p1 <- ggplot(data.ss,
               aes(x = nb.r.av,
                   y = net.pct.rec)) +
    geom_point() +
    geom_smooth(method=lm) +
    labs(title = "Attack by average risk scores per simulation",
         x = "Mean risk score",
         y = "Attack rate")

  # create directory if necessary
  dir.create(EXPORT_PATH_PLOTS, showWarnings = FALSE)

  ggsave(paste(EXPORT_PATH_PLOTS, filename.prefix, "-attackrate", "-all", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
         p1,
         width = 230,
         height = 230,
         units = "mm",
         dpi = 600,
         device = EXPORT_FILE_TYPE_PLOTS)



  data.ss.above.av <- subset(data.ss, nb.r.av.above == "true")
  data.ss.below.av <- subset(data.ss, nb.r.av.above == "false")

  # additional information
  wt <- wilcox.test(data.ss.above.av$net.pct.rec, data.ss.below.av$net.pct.rec)     # non-parametric independent samples t-test
                                                                                    # for final size (non-normal distribution)
  wt.out <- paste("W = ", wt$statistic, sep = "")
  wt.p <- wt$p.value
  if (wt.p < 0.001) {
    wt.out <- paste(wt.out, ", p < 0.001", sep = "")
  } else {
    wt.out <- paste(wt.out, ", p = ", round(wt$p.value, digits = 3), sep = "")
  }

  dur.above.mean <- mean(data.ss.above.av$net.epidemic.duration)                    # duration - above average
  dur.above.sd   <- sd(data.ss.above.av$net.epidemic.duration)
  dur.above.max   <- max(data.ss.above.av$net.epidemic.duration)
  dur.below.mean <- mean(data.ss.below.av$net.epidemic.duration)                    # duration - below average
  dur.below.sd   <- sd(data.ss.below.av$net.epidemic.duration)
  dur.below.max   <- max(data.ss.below.av$net.epidemic.duration)

  peak.time.above.mean <- mean(data.ss.above.av$net.epidemic.peak.time)              # peak time - above average
  peak.time.above.sd   <- sd(data.ss.above.av$net.epidemic.peak.time)
  peak.time.above.max   <- max(data.ss.above.av$net.epidemic.peak.time)
  peak.time.below.mean <- mean(data.ss.below.av$net.epidemic.peak.time)              # peak time - below average
  peak.time.below.sd   <- sd(data.ss.below.av$net.epidemic.peak.time)
  peak.time.below.max   <- max(data.ss.below.av$net.epidemic.peak.time)

  m.ean <- ddply(data.ss, "nb.r.av.above", summarise, grp.mean=mean(net.pct.rec))
  m.edian <- ddply(data.ss, "nb.r.av.above", summarise, grp.median=median(net.pct.rec))

  p2 <- ggplot(data.ss,
               aes(x = net.pct.rec,
                   fill = nb.r.av.above)) +
    geom_density(alpha = 0.3) +
    geom_vline(data = m.ean,
               aes(xintercept = grp.mean,
                   color = nb.r.av.above),
               linetype="solid") +
    geom_vline(data = m.edian,
               aes(xintercept = grp.median,
                   color = nb.r.av.above),
               linetype="dashed") +
    labs(title = "Final size between simulations differing in average risk scores",
         x = "Final size",
         y = "Density",
         fill = "More risk\navoiding than\naverage (>=1.27)",
         color = "More risk\navoiding than\naverage (>=1.27)",
         caption = paste("\nWilcoxon Signed Rank: ", wt.out, "\n",
                         "Duration (risk avoiding): mean = ", round(dur.above.mean, digits = 2), " (", round(dur.above.sd, digits = 2), "), ",
                         "max: ", dur.above.max, "\n",
                         "Duration (risk seeking): mean = ", round(dur.below.mean, digits = 2), " (", round(dur.below.sd, digits = 2), "), ",
                         "max: ", dur.below.max, "\n",
                         "Peak time (risk avoiding): mean = ", round(peak.time.above.mean, digits = 2), " (", round(peak.time.above.sd, digits = 2), "), ",
                         "max: ", peak.time.above.max, "\n",
                         "Peak time (risk seeking): mean = ", round(peak.time.below.mean, digits = 2), " (", round(peak.time.below.sd, digits = 2), "), ",
                         "max: ", peak.time.below.max, "\n",
                         "\n", addendum, sep = ""))  +
    scale_fill_brewer(palette="Dark2") +
    scale_color_brewer(palette="Dark2") +
    ylim(0.0, 0.025)

  ggsave(paste(EXPORT_PATH_PLOTS, filename.prefix, "-attackrate", "-split", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
         p2,
         width = 230,
         height = 230,
         units = "mm",
         dpi = 600,
         device = EXPORT_FILE_TYPE_PLOTS)




}


export_infected_plots <- function(data.ad = load_agent_details_data(), addendum = "", filename.prefix = "") {

  r <- c()
  inf.cnt <- c()
  agents.cnt <- c()
  dis.cnt <- c()
  dis.total <- 0
  for (i in seq(0.0, 1.9, 0.1)) {
    data.ad.sub <- subset(data.ad, nb.r.sigma == i)
    r <- c(r, i)

    data.ad.curr.r <- subset(data.ad, (nb.r.sigma > i & nb.r.sigma <= i+0.1))

    inf.cnt <- c(inf.cnt, nrow(subset(data.ad, (nb.r.sigma > i & nb.r.sigma <= i+0.1) & agent.infected == 1)))
    agents.cnt <- c(agents.cnt, nrow(data.ad.curr.r))

    curr.r.cons.broken <- sum(data.ad.curr.r$agent.cons.broken.active.epidemic)
    dis.cnt <- c(dis.cnt, curr.r.cons.broken)
    dis.total <- dis.total + curr.r.cons.broken
  }
  inf.pct <- inf.cnt / agents.cnt
  dis.pct <- dis.cnt / dis.total
  dis.per.agent <- dis.cnt / agents.cnt

  data.ad.prep <- data.frame(r, inf.pct, dis.pct, dis.per.agent)

  p1 <- ggplot(data.ad.prep,
               aes(x = r,
                   y = inf.pct)) +
    geom_bar(stat="identity", width = 0.09, position = position_nudge(x = 0.05)) +
    labs(title = "Proportion of agents getting infected per risk score",
         x = "Risk score",
         y = "Proportion of infect agents",
         caption = addendum) +
    ylim(0.0, 1.0)

  # create directory if necessary
  dir.create(EXPORT_PATH_PLOTS, showWarnings = FALSE)

  ggsave(paste(EXPORT_PATH_PLOTS, filename.prefix, "-infections-", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
         p1,
         width = 230,
         height = 230,
         units = "mm",
         dpi = 600,
         device = EXPORT_FILE_TYPE_PLOTS)

  p2 <- ggplot(data.ad.prep,
               aes(x = r,
                   y = dis.per.agent)) +
    geom_bar(stat="identity", width = 0.09, position = position_nudge(x = 0.05)) +
    labs(title = "Average number of disconnects over all agents with similar risk score",
         x = "Risk score",
         y = "Average number of disconnects",
         caption = addendum) +
    ylim(0.0, 1.0)

  ggsave(paste(EXPORT_PATH_PLOTS, filename.prefix, "-disconnects", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
         p2,
         width = 230,
         height = 230,
         units = "mm",
         dpi = 600,
         device = EXPORT_FILE_TYPE_PLOTS)

}


get_averages <- function(data.ss = load_simulation_summary_data(),
                         data.ad = load_agent_details_data(),
                         param.name, param.value) {

  data.ss.by.param <- data.ss[ data.ss[[param.name]] == param.value , ]
  data.ad.by.param <- data.ad[ data.ad[[param.name]] == param.value , ]

  out <- paste(param.name, " = ", param.value, "\n", sep = "")
  out <- paste(out, "     mean attack rate (overall):\t\t",
               round(mean(data.ss.by.param$net.pct.rec), digits = 2),
               " (", round(sd(data.ss.by.param$net.pct.rec), digits = 2), ")\n", sep = "")
  out <- paste(out, "     mean attack rate (risk seeking):\t",
               round(mean(subset(data.ss.by.param, nb.r.av.above == "false")$net.pct.rec), digits = 2),
               " (", round(sd(subset(data.ss.by.param, nb.r.av.above == "false")$net.pct.rec), digits = 2), ")\n", sep = "")
  out <- paste(out, "     mean attack rate (risk avoiding):\t",
               round(mean(subset(data.ss.by.param, nb.r.av.above == "true")$net.pct.rec), digits = 2),
               " (", round(sd(subset(data.ss.by.param, nb.r.av.above == "true")$net.pct.rec), digits = 2), ")\n\n", sep = "")

  out <- paste(out, "     mean connections broken per agent (overall):\t\t",
               round(mean(data.ad.by.param$agent.cons.broken.active.epidemic), digits = 2),
               " (", round(sd(data.ad.by.param$agent.cons.broken.active.epidemic), digits = 2), ")\n", sep = "")
  out <- paste(out, "     mean connections broken per agent (risk seeking):\t",
               round(mean(subset(data.ad.by.param, nb.r.av.above == "false")$agent.cons.broken.active.epidemic), digits = 2),
               " (", round(sd(subset(data.ad.by.param, nb.r.av.above == "false")$agent.cons.broken.active.epidemic), digits = 2), ")\n", sep = "")
  out <- paste(out, "     mean connections broken per agent (risk avoiding):\t",
               round(mean(subset(data.ad.by.param, nb.r.av.above == "true")$agent.cons.broken.active.epidemic), digits = 2),
               " (", round(sd(subset(data.ad.by.param, nb.r.av.above == "true")$agent.cons.broken.active.epidemic), digits = 2), ")\n\n", sep = "")

  out <- paste(out, "     mean duration (overall):\t",
               round(mean(data.ss.by.param$net.epidemic.duration), digits = 2),
               " (", round(sd(data.ss.by.param$net.epidemic.duration), digits = 2), ")\n", sep = "")
  out <- paste(out, "     mean peak time (overall):\t",
               round(mean(data.ss.by.param$net.epidemic.peak.time), digits = 2),
               " (", round(sd(data.ss.by.param$net.epidemic.peak.time), digits = 2), ")\n\n\n", sep = "")

  return(out)
}




export_all <- function() {

  data.ss <- load_simulation_summary_data()
  data.ad <- load_agent_details_data()

  out <- ""
  for (gamma in unique(data.ss$nb.gamma)) {
    out <- paste(out, get_averages(data.ss = data.ss, data.ad = data.ad, param.name = "nb.gamma", param.value =  gamma))

    addendum <- paste("gamma = ", gamma, sep = "")
    filename.prefix <- paste("gamma-", gsub(".", "_", gamma, fixed = TRUE), sep = "")

    export_experiment_plots(data.ss = subset(data.ss, nb.gamma == gamma), filename.prefix = filename.prefix)
    export_infected_plots(data.ad = subset(data.ad, nb.gamma == gamma),  addendum = addendum, filename.prefix = filename.prefix)
  }
  for (tau in unique(data.ss$nb.tau)) {
    out <- paste(out, get_averages(data.ss = data.ss, data.ad = data.ad, param.name = "nb.tau", param.value =  tau))

    addendum <- paste("tau = ", tau, sep = "")
    filename.prefix <- paste("tau-", gsub(".", "_", tau, fixed = TRUE), sep = "")

    export_experiment_plots(data.ss = subset(data.ss, nb.tau == tau), filename.prefix = filename.prefix)
    export_infected_plots(data.ad = subset(data.ad, nb.tau == tau),  addendum = addendum, filename.prefix = filename.prefix)
  }
  for (phi in unique(data.ss$nb.phi)) {
    out <- paste(out, get_averages(data.ss = data.ss, data.ad = data.ad, param.name = "nb.phi", param.value =  phi))

    addendum <- paste("phi = ", tau, sep = "")
    filename.prefix <- paste("phi-", gsub(".", "_", phi, fixed = TRUE), sep = "")

    export_experiment_plots(data.ss = subset(data.ss, nb.phi == phi), filename.prefix = filename.prefix)
    export_infected_plots(data.ad = subset(data.ad, nb.phi == phi),  addendum = addendum, filename.prefix = filename.prefix)
  }
  for (psi in unique(data.ss$nb.psi)) {
    out <- paste(out, get_averages(data.ss = data.ss, data.ad = data.ad, param.name = "nb.psi", param.value =  psi))

    addendum <- paste("psi = ", tau, sep = "")
    filename.prefix <- paste("psi-", gsub(".", "_", psi, fixed = TRUE), sep = "")

    export_experiment_plots(data.ss = subset(data.ss, nb.psi == psi), filename.prefix = filename.prefix)
    export_infected_plots(data.ad = subset(data.ad, nb.psi == psi),  addendum = addendum, filename.prefix = filename.prefix)
  }
  for (xi in unique(data.ss$nb.xi)) {
    out <- paste(out, get_averages(data.ss = data.ss, data.ad = data.ad, param.name = "nb.xi", param.value =  xi))

    addendum <- paste("xi = ", tau, sep = "")
    filename.prefix <- paste("xi-", gsub(".", "_", xi, fixed = TRUE), sep = "")

    export_experiment_plots(data.ss = subset(data.ss, nb.xi == xi), filename.prefix = filename.prefix)
    export_infected_plots(data.ad = subset(data.ad, nb.xi == xi),  addendum = addendum, filename.prefix = filename.prefix)
  }

  # export to file
  dir.create(EXPORT_PATH_NUM, showWarnings = FALSE)
  cat(out, file = paste(EXPORT_PATH_NUM,
                        "averages",
                        EXPORT_FILE_EXTENSION_DESC,
                        sep = ""))


  # finding the best settings
  out <- "Wilcoxon Signed Rank tests for final size between above and below average groups over all parameter combinations\n"
  i <- 1
  for (gamma in unique(data.ss$nb.gamma)) {
    for (tau in unique(data.ss$nb.tau)) {
      for (phi in unique(data.ss$nb.phi)) {
        for (psi in unique(data.ss$nb.psi)) {
          for (xi in unique(data.ss$nb.xi)) {

            out <- paste(out, "\tgamma = ", gamma, ", ",
                         "tau = ", tau, ", ",
                         "phi = ", phi, ", ",
                         "psi = ", psi, ", ",
                         "xi = ", xi,
                         ":\t\t", sep = "")

            data.ss.curr <- subset(data.ss,
                                   nb.gamma == gamma &
                                     nb.tau == tau &
                                     nb.phi == phi &
                                     nb.psi == psi &
                                     nb.xi == xi)

            data.ss.curr.above.av <- subset(data.ss.curr, nb.r.av.above == "true")
            data.ss.curr.below.av <- subset(data.ss.curr, nb.r.av.above == "false")

            wt <- wilcox.test(data.ss.curr.above.av$net.pct.rec, data.ss.curr.below.av$net.pct.rec)     # non-parametric independent samples t-test
                                                                                                        # for final size (non-normal distribution)
            wt.out <- paste("W = ", wt$statistic, sep = "")
            wt.p <- wt$p.value
            if (wt.p < 0.001) {
              wt.out <- paste(wt.out, ",\tp < 0.001", sep = "")
            } else {
              wt.out <- paste(wt.out, ",\tp = ", round(wt$p.value, digits = 3), sep = "")
            }

            out <- paste(out, wt.out, "\n", sep = "")

          }
        }
      }
    }
  }

  # export to file
  dir.create(EXPORT_PATH_NUM, showWarnings = FALSE)
  cat(out, file = paste(EXPORT_PATH_NUM,
                        "wilcoxon-final-size",
                        EXPORT_FILE_EXTENSION_DESC,
                        sep = ""))








  # "best" settings
  gamma <- 0.1
  tau   <- 5
  phi   <- 0.1
  psi   <- 0.5
  xi    <- 0.3

  data.ss.curr <- subset(data.ss,
                         nb.gamma == gamma &
                           nb.tau == tau &
                           nb.phi == phi &
                           nb.psi == psi &
                           nb.xi == xi)

  data.ad.curr <- subset(data.ad,
                         nb.gamma == gamma &
                           nb.tau == tau &
                           nb.phi == phi &
                           nb.psi == psi &
                           nb.xi == xi)

  addendum <- paste("Simulation parameters: ",
                    "gamma = ", gamma,
                    ", tau = ", tau,
                    ", phi = ", phi,
                    ", psi = ", psi,
                    ", xi = ", xi,
                    sep = "")
  filename.prefix <- "!best"

  export_experiment_plots(data.ss = data.ss.curr, addendum = addendum, filename.prefix = filename.prefix)
  export_infected_plots(data.ad = data.ad.curr, addendum = addendum, filename.prefix = filename.prefix)


  # robustness (random samples)
  out <- "Repeated Wilcoxon Signed Rank test for random samples of best parameter setting\n\n"

  it <- 1
  out <- paste(out, "\tall data:\n")
  while (it <= 100) {
    sim.all <- unique(data.ss.curr$sim.cnt)
    sim.sampled <- c()
    while (length(sim.sampled) < 48) {
      sample.from <- setdiff(sim.all, sim.sampled)
      sampled <- sample(sample.from, 1)
      if (!sampled %in% sim.sampled) {
        sim.sampled <- c(sim.sampled, sampled)
      }
    }
    data.ss.curr.sampled <- subset(data.ss.curr, sim.cnt %in% sim.sampled)

    data.ss.curr.sampled.above.av <- subset(data.ss.curr.sampled, nb.r.av.above == "true")
    data.ss.curr.sampled.below.av <- subset(data.ss.curr.sampled, nb.r.av.above == "false")

    wt <- wilcox.test(data.ss.curr.sampled.above.av$net.pct.rec, data.ss.curr.sampled.below.av$net.pct.rec)     # non-parametric independent samples t-test
                                                                                                                # for final size (non-normal distribution)
    wt.out <- paste("W = ", wt$statistic, sep = "")
    wt.p <- wt$p.value
    if (wt.p < 0.001) {
      wt.out <- paste(wt.out, ",\tp < 0.001", sep = "")
    } else {
      wt.out <- paste(wt.out, ",\tp = ", round(wt$p.value, digits = 3), sep = "")
    }

    out <- paste(out, "\t\tsample ", it, ":\t", wt.out, "\n", sep = "")

    it <- it+1

  }
  for (omega in unique(data.ss.curr$nb.omega)) {
    for (alpha in unique(data.ss.curr$nb.alpha)) {

      data.ss.curr.condition <- subset(data.ss.curr, nb.omega == omega & nb.alpha == alpha)

      it <- 1
      out <- paste(out, "\n\n\tomega = ", omega, ", alpha: ", alpha, ":\n")
      while (it <= 100) {
        sim.all <- unique(data.ss.curr.condition$sim.cnt)
        sim.sampled <- c()
        while (length(sim.sampled) < 48) {
          sample.from <- setdiff(sim.all, sim.sampled)
          sampled <- sample(sample.from, 1)
          if (!sampled %in% sim.sampled) {
            sim.sampled <- c(sim.sampled, sampled)
          }
        }
        data.ss.curr.condition.sampled <- subset(data.ss.curr.condition, sim.cnt %in% sim.sampled)

        data.ss.curr.condition.sampled.above.av <- subset(data.ss.curr.condition.sampled, nb.r.av.above == "true")
        data.ss.curr.condition.sampled.below.av <- subset(data.ss.curr.condition.sampled, nb.r.av.above == "false")

        wt <- wilcox.test(data.ss.curr.condition.sampled.above.av$net.pct.rec,     # non-parametric independent samples t-test
                          data.ss.curr.condition.sampled.below.av$net.pct.rec)     # for final size (non-normal distribution)
        wt.out <- paste("W = ", wt$statistic, sep = "")
        wt.p <- wt$p.value
        if (wt.p < 0.001) {
          wt.out <- paste(wt.out, ",\tp < 0.001", sep = "")
        } else {
          wt.out <- paste(wt.out, ",\tp = ", round(wt$p.value, digits = 3), sep = "")
        }

        out <- paste(out, "\t\tsample ", it, ":\t", wt.out, "\n", sep = "")

        it <- it+1

      }
    }
  }



  # export to file
  dir.create(EXPORT_PATH_NUM, showWarnings = FALSE)
  cat(out, file = paste(EXPORT_PATH_NUM,
                        "robustness",
                        EXPORT_FILE_EXTENSION_DESC,
                        sep = ""))
































  # data.ad <- load_agent_details_data()
  # export_infected_plots(data.ad = data.ad, filename.appendix = "all")
  # export_infected_plots(data.ad = subset(data.ad, nb.omega == 0.0),  filename.appendix = "random")
  # export_infected_plots(data.ad = subset(data.ad, nb.omega == 0.8),  filename.appendix = "assortative")


  # TODO: move this into the loop above
  #       add addendum
  #       start with number, then attackrate-all / attackrate-byavriskscore / infections / disconnects

  export_descriptives(data.ss = data.ss, data.ad = data.ad)

  # export_correlations(data.ss = data.ss)
  # export_all_plots(data.ss = data.ss)
  # export_network_models(data.ss = data.ss)
  # # export_agent_models(data.ad = data.ad)
  #
  #
  #
  #
  # data.rs <- load_round_summary_data()
  # export_sirs(data.rs = data.rs)
}







