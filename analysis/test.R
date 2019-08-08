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

########################################### GLOBAL CONSTANTS ##########################################
# input/output directory
args = commandArgs(trailingOnly=TRUE)
if (length(args) != 1) {
  stop("Exactly one argument must be supplied (input/output directory).n", call.=FALSE)
}
IO_DIR <- args[1]
# path to generated data (*.csv files)
DATA_PATH                   <- paste(dirname(sys.frame(1)$ofile), "/data/", IO_DIR, sep = "")
# file names of generated data
CSV_SUMMARY_PATH            <- paste(DATA_PATH, "simulation-summary.csv", sep = "")
CSV_ROUND_SUMMARY_PATH      <- paste(DATA_PATH, "round-summary.csv", sep = "")
CSV_AGENT_DETAILS_PATH      <- paste(DATA_PATH, "agent-details.csv", sep = "")


########################################## SCRIPT COMPOSITION ########################################



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

  # exclusion criteria
  csv <- subset(csv, net.param.N != 5)

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
















fileConn <- file(paste("data/", ioDir,"/output.txt", sep = ""))
writeLines(c("Hello", ioDir), fileConn)
close(fileConn)
