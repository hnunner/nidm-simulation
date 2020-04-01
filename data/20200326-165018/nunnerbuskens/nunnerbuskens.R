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
              "lme4",         # regression analyses
              "sjstats",      # "icc" function
              "texreg",       # html export
              "QuantPsyc",    # 'meanCenter' function
              "dplyr",        # 'select' function
              "plyr",         # 'round_any' function
              "hexbin"        # 'geom_hex' function
              # "gridExtra",   # side-by-side plots
              # "psych",       # summary statistics
              # "ggpubr"       # arranging a list of plots with one common legend
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
CSV_ROUND_SUMMARY_PATH      <- paste(DATA_PATH, "round-summary.csv", sep = "")
CSV_AGENT_DETAILS_PATH      <- paste(DATA_PATH, "agent-details.csv", sep = "")
# export files
EXPORT_DIR_NUM              <- "numerical/"
EXPORT_PATH_NUM             <- paste(DATA_PATH, EXPORT_DIR_NUM, sep = "")
EXPORT_FILE_TYPE_REG        <- "html"
EXPORT_FILE_EXTENSION_REG   <- paste(".", EXPORT_FILE_TYPE_REG, sep = "")
EXPORT_DIR_PLOTS            <- "figures/"
EXPORT_PATH_PLOTS           <- paste(DATA_PATH, EXPORT_DIR_PLOTS, sep = "")
EXPORT_FILE_TYPE_PLOTS      <- "png"
EXPORT_FILE_EXTENSION_PLOTS <- paste(".", EXPORT_FILE_TYPE_PLOTS, sep = "")
# export image settings
EXPORT_PLOT_WIDTH           <- 250
EXPORT_PLOT_HEIGHT          <- 100
EXPORT_SIZE_UNITS           <- "mm"
EXPORT_DPI                  <- 1200


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
# function: load_simulation_summary_data
#     Loads summary data for NIDM simulations.
# return: the summary data for NIDM simulations
#----------------------------------------------------------------------------------------------------#
load_simulation_summary_data <- function() {
  return(load_csv(CSV_SUMMARY_PATH))
}

load_prepared_simulation_summary_data <- function() {
  data.ss <- load_simulation_summary_data()
  data.ss$nb.r.sigma.av.rounded <- round_any(data.ss$nb.r.sigma.av, 0.05)
  data.ss$nb.r.sigma.av.rounded.factor <- as.factor(data.ss$nb.r.sigma.av.rounded)
  data.ss$nb.gamma.factor <- as.factor(data.ss$nb.gamma)
  data.ss$nb.sigma.rounded <- round(data.ss$nb.sigma)
  data.ss$nb.sigma.rounded.factor <- as.factor(data.ss$nb.sigma.rounded)
  data.ss$nb.sigma.rounded.10 <- round_any(data.ss$nb.sigma, 10)
  data.ss$nb.sigma.rounded.10.factor <- as.factor(data.ss$nb.sigma.rounded.10)
  return(data.ss)
}

#----------------------------------------------------------------------------------------------------#
# function: load_round_summary_data
#     Loads summary data for all simulated NIDM rounds.
# return: the summary data for all simulated NIDM rounds
#----------------------------------------------------------------------------------------------------#
load_round_summary_data <- function() {
  return(load_csv(CSV_ROUND_SUMMARY_PATH))
}

#----------------------------------------------------------------------------------------------------#
# function: loadAgentDetailsData
#     Loads agent details data.
# return: the agent details data
#----------------------------------------------------------------------------------------------------#
load_agent_details_data <- function() {
  return(load_csv(CSV_AGENT_DETAILS_PATH))
}

load_prepared_agent_details_data <- function() {
  data.ad <- load_agent_details_data()
  data.ad$r.factor.025 <- as.factor(ifelse(data.ad$nb.r.sigma >= 0.25 & data.ad$nb.r.sigma < 0.5, "0.25<=r<0.50",
                                           ifelse(data.ad$nb.r.sigma >= 0.5 & data.ad$nb.r.sigma < 0.75, "0.50<=r<0.75",
                                                  ifelse(data.ad$nb.r.sigma >= 0.75 & data.ad$nb.r.sigma < 1.0, "0.75<=r<1.00",
                                                         ifelse(data.ad$nb.r.sigma >= 1.0 & data.ad$nb.r.sigma < 1.25, "1.00<=r<1.25",
                                                                ifelse(data.ad$nb.r.sigma >= 1.25 & data.ad$nb.r.sigma < 1.5, "1.25<=r<1.50",
                                                                       ifelse(data.ad$nb.r.sigma >= 1.5 & data.ad$nb.r.sigma < 1.75, "1.50<=r<1.75",
                                                                              "NA")))))))

  data.ad$r.factor.075 <- as.factor(ifelse(data.ad$nb.r.sigma >= 0.25 & data.ad$nb.r.sigma < 1.0, "0.25<=r<1.00",
                                           ifelse(data.ad$nb.r.sigma >= 1.0 & data.ad$nb.r.sigma < 1.75, "1.00<=r<1.75",
                                                  "NA")))

  data.ad.pre.epidemic <- subset(data.ad, sim.stage=="pre-epidemic")
  data.ad.post.epidemic <- subset(data.ad, sim.stage=="post-epidemic")

  ties.broken.during.epidemic <- data.frame(sim.uid=data.ad.pre.epidemic$sim.uid,
                                            agent.id=data.ad.pre.epidemic$agent.id,
                                            ties.broken.during.epidemic=data.ad.post.epidemic$agent.cons.broken.active -
                                              data.ad.pre.epidemic$agent.cons.broken.active)



  data.ad.res <- merge(data.ad, ties.broken.during.epidemic, by=c("sim.uid", "agent.id"))
  return(data.ad.res)
}

plot_duration <- function(data.ss, by.factor) {
  ggplot(data.ss,
         aes(x=sim.epidemic.duration,
             fill=by.factor,
             color=by.factor)) +
    geom_histogram(binwidth=1,
                   alpha=0.5) +
    geom_vline(aes(xintercept=mean(sim.epidemic.duration)),
               color="darkred",
               linetype="dashed",
               size=1) +
    xlab("Duration of epidemics") +
    ylab("Frequency") +
    labs(fill=by.factor,
         color=by.factor) +
    scale_x_continuous(limits=c(0,30), breaks = round(seq(min(0), max(30), by = 5),1))
}

plot_ties_broken_during_epidemic <- function(data, r.factor) {
  ggplot(data,
         aes(x=ties.broken.during.epidemic,
             fill=r.factor,
             color=r.factor)) +
    geom_histogram(binwidth=1,
                   alpha=0.5,
                   position="dodge") +
    scale_x_continuous(limits=c(0,15), breaks = round(seq(min(0), max(15), by = 1),1))
}

plot_infections_by_risk_group <- function(data, r.factor) {
  ggplot(subset(data),
         aes(x=agent.dis.state,
             fill=r.factor,
             color=r.factor)) +
    geom_bar(alpha=0.5)
}

plot_stuff <- function(data) {
  ggplot(data,
         aes(x=agent.dis.state,
             fill=r.factor,
             color=r.factor)) +
    geom_bar(alpha=0.5)
}


manual_analysis <- function() {
  data.ss <- load_prepared_simulation_summary_data()
  # plot_duration(data.ss, data.ss$nb.gamma.factor) # gamma need to be factorized (like sigma)
  plot_duration(data.ss, data.ss$nb.sigma.rounded.10.factor)


  data.ad <- load_prepared_agent_details_data()
  plot_ties_broken_during_epidemic(data, data$r.factor.025)
  plot_ties_broken_during_epidemic(data, data$r.factor.075)

  plot_infections_by_risk_group(data, data$r.factor.075)


  data <- subset(data.ad, sim.stage == "finished" & agent.dis.state == "RECOVERED")
  ggplot(data,
         aes(x=nb.sigma,
             y=nb.gamma,
             fill=r.factor.025,
             color=r.factor.025)) +
    geom_point(alpha=0.2,
               position="jitter") +
    geom_smooth(method=lm)

  summary(data)
  ggplot(data,
         aes(x=nb.sigma,
             y=nb.gamma)) +
    geom_hex(bins=12) +
    scale_fill_continuous(type = "viridis") +
    theme_bw()



  for (gamma in unique(data.ad$nb.gamma)) {
    cat("gamma:", gamma, "\n")
    sigma.prev = 0
    for (sigma in seq(10, 100, by=10)) {
      cat("   ", "sigma:", sigma.prev, "-", sigma, "\n")
      sigma.prev <- sigma
      for (r in unique(data.ad$r.factor)) {
        cat("      ", "r:", r)
      }
      cat("\n")
    }
  }

  mean(subset(data.ad, r.factor == "0.25<=r<0.50")$ties.broken.during.epidemic)
  mean(subset(data.ad, r.factor == "1.50<=r<1.75")$ties.broken.during.epidemic)

}

