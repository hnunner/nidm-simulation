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
source_libs(c("ggplot2"      # plots
              # "gridExtra",   # side-by-side plots
              # "QuantPsyc",   # 'meanCenter' function
              # "lme4",        # regression analyses
              # "sjstats",     # "icc" function
              # "texreg",      # html export
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

print_descriptives <- function(data.ss = load_simulation_summary_data()) {
  print("----------------------------------------------------------------")
  print(paste("observations:", nrow(data.ss)))
  print(paste("exlcusions (unstable):", nrow(subset(data.ss, data.ss$net.stable.pre == 0))))
  print(paste("exlcusions (disconnected):", nrow(subset(data.ss, data.ss$net.pathlength.pre.epidemic.av < 1))))
  print("----------------------------------------------------------------")
  print(paste("av. degree (network): ", round(mean(data.ss$net.degree.pre.epidemic.av), digits = 2),
              " (", round(sd(data.ss$net.degree.pre.epidemic.av), digits = 2), ")", sep = ""))
  print(paste("av. clustering (network): ", round(mean(data.ss$net.clustering.pre.epidemic.av), digits = 2),
              " (", round(sd(data.ss$net.clustering.pre.epidemic.av), digits = 2), ")", sep = ""))
  print(paste("av. path length (network): ", round(mean(data.ss$net.pathlength.pre.epidemic.av), digits = 2),
              " (", round(sd(data.ss$net.pathlength.pre.epidemic.av), digits = 2), ")", sep = ""))
  print("----------------------------------------------------------------")
  print(paste("av. degree (patient-0): ", round(mean(data.ss$index.degree), digits = 2),
              " (", round(sd(data.ss$index.degree), digits = 2), ")", sep = ""))
  print(paste("av. clustering (patient-0): ", round(mean(data.ss$index.clustering), digits = 2),
              " (", round(sd(data.ss$index.clustering), digits = 2), ")", sep = ""))
  print(paste("av. betweenness (patient-0): ", round(mean(data.ss$index.betweenness.normalized), digits = 2),
              " (", round(sd(data.ss$index.betweenness.normalized), digits = 2), ")", sep = ""))
  print("----------------------------------------------------------------")
  print(paste("av. attack rate: ", round(mean(data.ss$net.pct.rec), digits = 2),
              " (", round(sd(data.ss$net.pct.rec), digits = 2), ")", sep = ""))
}

print_descriptives_per_condition <- function(data.ss = load_simulation_summary_data()) {
  print("RRM:")
  print_descriptives(subset(data.ss, data.ss$nb.alpha == 0.15 & data.ss$nb.omega == 0.0 & data.ss$nb.sigma == 2.0))
  print("RRS:")
  print_descriptives(subset(data.ss, data.ss$nb.alpha == 0.15 & data.ss$nb.omega == 0.0 & data.ss$nb.sigma == 50.0))
  print("RAM:")
  print_descriptives(subset(data.ss, data.ss$nb.alpha == 0.15 & data.ss$nb.omega == 0.8 & data.ss$nb.sigma ==  2.0))
  print("RAS:")
  print_descriptives(subset(data.ss, data.ss$nb.alpha == 0.15 & data.ss$nb.omega == 0.8 & data.ss$nb.sigma == 50.0))
  print("SRM:")
  print_descriptives(subset(data.ss, data.ss$nb.alpha == 0.85 & data.ss$nb.omega == 0.0 & data.ss$nb.sigma ==  2.0))
  print("SRS:")
  print_descriptives(subset(data.ss, data.ss$nb.alpha == 0.85 & data.ss$nb.omega == 0.0 & data.ss$nb.sigma == 50.0))
  print("SAM:")
  print_descriptives(subset(data.ss, data.ss$nb.alpha == 0.85 & data.ss$nb.omega == 0.8 & data.ss$nb.sigma ==  2.0))
  print("SAS:")
  print_descriptives(subset(data.ss, data.ss$nb.alpha == 0.85 & data.ss$nb.omega == 0.8 & data.ss$nb.sigma == 50.0))
}

manual_analyses <- function() {
  data.ss <-load_simulation_summary_data()
  print_descriptives_per_condition(subset(data.ss, data.ss$nb.N == 20))
  print_descriptives_per_condition(subset(data.ss, data.ss$nb.N == 24))


  data.rs <- load_round_summary_data()
  data.ad <- load_agent_details_data()

  exclusions.stable <- subset(data.ss, data.ss$net.stable.pre == 0)
  exclusions.disconnected <- subset(data.ss, data.ss$net.pathlength.pre.epidemic.av < 1)
}

