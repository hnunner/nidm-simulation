#!/usr/bin/env Rscript

# Copyright (C) 2017 - 2021
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
              "performance",  # "icc" function
              "texreg",       # html export
              "QuantPsyc",    # 'meanCenter' function
              "dplyr",        # 'select' function
              "plyr",         # 'ddply' function
              "ggpubr",       # arranging a list of plots with one common legend
              "e1071",        # skewness
              "hexbin",       # hexbin plots
              "Cairo",        # smooth plot lines
              "rsq",          # adjusted R2
              "car",          # VIFs
              "stringr",      # string manipulations
              "gridExtra"     # side-by-side plots
              # "psych",       # summary statistics
))


########################################### GLOBAL CONSTANTS ##########################################
### IO ###
# input/output directory
DATA_PATH                       <- ""
args = commandArgs(trailingOnly=TRUE)
if (length(args) == 0) {
  DATA_PATH                     <- paste(dirname(sys.frame(1)$ofile), "/", sep = "")
} else {
  DATA_PATH                     <- args[1]
}
# file name prefixes and suffixes
CSV_SUMMARY_PREFIX              <- "profession-data_"
CSV_ROUND_SUMMARY_PREFIX        <- "profession-round-summary_"
CSV_SUMMARY_SUFFIX              <- ".csv"
# file names of generated data
CSV_SUMMARY_PATH                <- paste(DATA_PATH, "profession-data.csv", sep = "")
CSV_AGENT_STATS_PATH            <- paste(DATA_PATH, "profession-agent-stats.csv", sep = "")
CSV_ROUND_SUMMARY_PATH          <- paste(DATA_PATH, "profession-round-summary.csv", sep = "")
CSV_ROUND_SUMMARY_PREPARED_PATH <- paste(DATA_PATH, "profession-round-summary-prepared.csv", sep = "")
CSV_PROFESSIONS_PATH            <- paste(DATA_PATH, "professions.csv", sep = "")
BELOT_PUBLIC_DATA_PATH  <- paste(DATA_PATH, "belot-public.csv", sep = "")
SOC_PROFESSION_STRUCTURE_PATH <- paste(DATA_PATH, "soc-profession-structure.csv", sep = "")

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
RIBBON_ALPHA                    <- 0.3
# colors (http://mkweb.bcgsc.ca/colorblind/)
COLORS                          <- c(Susceptible         = "#F0E442",   # yellow
                                     Infected            = "#E69F00",   # orange
                                     Recovered           = "#0072B2",   # blue
                                     Vaccinated          = "#669966",   # green

                                     Av.degree           = "#888888",   # grey
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

COLORS.CONDITIONS               <- c(Baseline = "#D81B60",
                                     Random   = "#FFC107",
                                     Targeted = "#1E88E5")
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
load_csv <- function(filePath, header = TRUE, sep = ";") {
  csv <- read.csv(file=filePath, header=header, sep=sep)
  return(csv)
}

#----------------------------------------------------------------------------------------------------#
# function: load_simulation_summary_data
#     Loads summary data for NIDM simulations.
# return: the summary data for NIDM simulations
#----------------------------------------------------------------------------------------------------#
load_simulation_summary_data <- function() {
  data.ss <- load_csv(CSV_SUMMARY_PATH)

  # remove all baseline
  data.ss.baseline <- subset(data.ss,
                             nb.prof.vaccine.distribution == "none" &
                               (grepl("C:/Users/Hendrik/git/NIDM/simulation/exports/20210321-024702/networks/professions.lockdown.1-60", import.filename, fixed = TRUE) |
                                  grepl("C:/Users/Hendrik/git/NIDM/simulation/exports/20210321-024702/networks/professions.lockdown.21-30", import.filename, fixed = TRUE) |
                                  grepl("C:/Users/Hendrik/git/NIDM/simulation/exports/20210321-024702/networks/professions.lockdown.31-40", import.filename, fixed = TRUE) |
                                  grepl("C:/Users/Hendrik/git/NIDM/simulation/exports/20210321-024702/networks/professions.lockdown.41-50", import.filename, fixed = TRUE) |
                                  grepl("C:/Users/Hendrik/git/NIDM/simulation/exports/20210321-024702/networks/professions.lockdown.51-60", import.filename, fixed = TRUE) |
                                  grepl("C:/Users/Hendrik/git/NIDM/simulation/exports/20210321-024702/networks/professions.lockdown.61-70", import.filename, fixed = TRUE) |
                                  grepl("C:/Users/Hendrik/git/NIDM/simulation/exports/20210321-024702/networks/professions.lockdown.71-80", import.filename, fixed = TRUE) |
                                  grepl("C:/Users/Hendrik/git/NIDM/simulation/exports/20210321-024702/networks/professions.lockdown.81-90", import.filename, fixed = TRUE)))
  data.ss.nobaseline <- subset(data.ss, nb.prof.vaccine.distribution != "none")
  data.ss.prepared <- rbind(data.ss.baseline, data.ss.nobaseline)

  return(data.ss.prepared)
}

#----------------------------------------------------------------------------------------------------#
# function: load_simulation_summary_data
#     Loads summary data for NIDM simulations.
# return: the summary data for NIDM simulations
#----------------------------------------------------------------------------------------------------#
load_agent_stats_data <- function() {
  data.as <- load_csv(CSV_AGENT_STATS_PATH)
  return(data.as)
}

#----------------------------------------------------------------------------------------------------#
# function: prepare_round_summary_data
#     Prepares round summary data.
# return: the prepared round summary data
#----------------------------------------------------------------------------------------------------#
prepare_round_summary_data <- function() {

  data.rs <- load_csv(CSV_ROUND_SUMMARY_PATH)

  round.max <- max(data.rs$round)
  while (round.max %% 10 != 0) {
    round.max <- round.max + 1
  }

  res <- data.rs[0,]
  file_number <- 1
  for (uid in unique(data.rs$sim.uid)) {

    print(paste("preparing round summary records for uid:", uid))

    records.rs.by.uid <- subset(data.rs, sim.uid == uid)
    finished <- do.call("rbind", replicate(n = round.max - max(records.rs.by.uid$round),
                                           expr =  records.rs.by.uid[max(records.rs.by.uid$round),],
                                           simplify = FALSE))
    records.rs.by.uid.filled <- rbind(records.rs.by.uid, finished)

    # renumber rounds
    records.rs.by.uid.filled$round <- seq(1, nrow(records.rs.by.uid.filled))
    records.rs.by.uid.filled$sim.uid <- rep(uid, nrow(records.rs.by.uid.filled))

    res <- rbind(res, records.rs.by.uid.filled)

    if (nrow(res) >= 100000) {
      # renumber rows
      rownames(res) <- 1:nrow(res)
      # export
      write.table(res, file=paste(DATA_PATH, "round-summary-prepared_", file_number,".csv", sep = ""), row.names=FALSE, sep=";")
      file_number <- file_number+1
      res <- data.rs[0,]
    }

  }

  if (nrow(res) > 0) {
    # renumber rows
    rownames(res) <- 1:nrow(res)
    # export
    write.table(res, file=paste(DATA_PATH, "round-summary-prepared_", file_number,".csv", sep = ""), row.names=FALSE, sep=";")
    # reset
    file_number <- file_number+1
  }

}


merge_round_summary_data <- function() {

  res <- NA

  for (i in seq(1, 747, 1)) {
    currFile <- load_csv(paste(DATA_PATH, "round-summary-prepared_", i, ".csv", sep = ""))

    if (i == 1) {
      res <- currFile
      print("Output file initialization complete.")
    } else {
      res <- rbind(res, currFile)
      print(paste("Rbind complete for file:", i))
    }
  }

  write.table(res, file=paste(CSV_ROUND_SUMMARY_PREPARED_PATH), row.names=FALSE, sep=";")

}

load_round_summary_prepared_data <- function() {
  return(load_csv(CSV_ROUND_SUMMARY_PREPARED_PATH))
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
# function: load_professions_data
#     Loads professions data.
# return: the professions data
#----------------------------------------------------------------------------------------------------#
load_professions_data <- function() {
  professions_data <- load_csv(CSV_PROFESSIONS_PATH, header = FALSE)
  professions_data[1,1] <- gsub("ï»¿", "", professions_data[1,1])       # in case CSV contains byte order mark
  return(professions_data)
}




prepare_profession_data <- function() {

  wholeSummary <- NA
  wholeRoundSummary <- NA

  filesImported <- 1

  for (range in c("gen-1",
                  "gen-2")) {

    currSummary <- load_csv(paste(DATA_PATH, CSV_SUMMARY_PREFIX, range, CSV_SUMMARY_SUFFIX, sep = ""))
    currRoundSummary <- load_csv(paste(DATA_PATH, CSV_ROUND_SUMMARY_PREFIX, range, CSV_SUMMARY_SUFFIX, sep = ""))

    if (filesImported == 1) {
      wholeSummary <- currSummary
      wholeRoundSummary <- currRoundSummary
    } else {
      wholeSummary <- rbind(wholeSummary, currSummary)
      wholeRoundSummary <- rbind(wholeRoundSummary, currRoundSummary)
    }

    filesImported <- filesImported+1
  }

  # # corrections
  # # only 20 iterations
  # wholeSummary <- subset(wholeSummary, nb.m <= 20)
  # rownames(wholeSummary) <- 1:nrow(wholeSummary)
  # wholeRoundSummary <- subset(wholeRoundSummary, nb.m <= 20)
  # rownames(wholeRoundSummary) <- 1:nrow(wholeRoundSummary)
  #
  # # rounds was erroneously increased once more after end of epidemic
  # wholeSummary$net.epidemic.duration <- wholeSummary$net.epidemic.duration-1

  # unique parameter combinations are after every 20 iterations
  upcs <- c()
  for (i in seq(1, nrow(wholeSummary)/20)) {
    upcs <- c(upcs, rep(i, 20))
  }
  wholeSummary$sim.upc <- upcs

  # uids as combination of upcs and iterations
  uids <- with(wholeSummary, paste0(sim.upc, "#", nb.m))
  wholeSummary$sim.uid <- uids

  # add rounds and uids to round summaries
  rounds <- c()
  uids <- c()
  for (i in seq(1, nrow(wholeSummary))) {

    if (i %% 1000 == 0) {
      print(paste(i, "of", nrow((wholeSummary))))
    }

    rounds <- c(rounds, seq(1:wholeSummary[i,]$net.epidemic.duration))
    uids <- c(uids, rep(wholeSummary[i,]$sim.uid, wholeSummary[i,]$net.epidemic.duration))
  }
  wholeRoundSummary$round <- rounds
  wholeRoundSummary$sim.uid <- uids

  write.table(wholeSummary, file=CSV_SUMMARY_PATH, row.names=FALSE, sep=";")
  write.table(wholeRoundSummary, file=CSV_ROUND_SUMMARY_PATH, row.names=FALSE, sep=";")
}

#----------------------------------------------------------------------------------------------------#
# function: load_belot_public_data
#     Loads the publicly accessible data from Belot et al. (2020)
# return: the publicly accessible data from Belot et al. (2020)
#----------------------------------------------------------------------------------------------------#
load_belot_public_data <- function() {
  return(load_csv(BELOT_PUBLIC_DATA_PATH, sep = ","))
}

#----------------------------------------------------------------------------------------------------#
# function: load_soc_profession_structure
#     Loads occupational hierarchy data (SOC - https://www.bls.gov/soc/2018/home.htm)
# return: occupational hierarchy data (SOC - https://www.bls.gov/soc/2018/home.htm)
#----------------------------------------------------------------------------------------------------#
load_soc_profession_structure <- function() {
  return(load_csv(SOC_PROFESSION_STRUCTURE_PATH))
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
get_descriptive <- function(vec, title, row.height = 0) {

  out <- paste(title,
               round(mean(vec, na.rm = TRUE), digits = 2),
               round(sd(vec, na.rm = TRUE), digits = 2),
               round(median(vec, na.rm = TRUE), digits = 2),
               round(min(vec, na.rm = TRUE), digits = 2),
               round(max(vec, na.rm = TRUE), digits = 2),
               round(skewness(vec, na.rm = TRUE), digits = 2),
               sep = " & ")
  out <- paste(out, " \\", "\\[", row.height, "pt]", " \n", sep = "")

  return(out)
}

get_descriptive_compressed <- function(vec1, vec2, vec3, title, row.height = 0) {

  if (is.na(vec1)) {
    out <- paste(title,

                 "",
                 "",
                 "",

                 round(mean(vec2, na.rm = TRUE), digits = 2),
                 round(sd(vec2, na.rm = TRUE), digits = 2),
                 round(median(vec2, na.rm = TRUE), digits = 2),

                 round(mean(vec3, na.rm = TRUE), digits = 2),
                 round(sd(vec3, na.rm = TRUE), digits = 2),
                 round(median(vec3, na.rm = TRUE), digits = 2),

                 sep = " & ")

  } else {
    out <- paste(title,

                 round(mean(vec1, na.rm = TRUE), digits = 2),
                 round(sd(vec1, na.rm = TRUE), digits = 2),
                 round(median(vec1, na.rm = TRUE), digits = 2),

                 round(mean(vec2, na.rm = TRUE), digits = 2),
                 round(sd(vec2, na.rm = TRUE), digits = 2),
                 round(median(vec2, na.rm = TRUE), digits = 2),

                 round(mean(vec3, na.rm = TRUE), digits = 2),
                 round(sd(vec3, na.rm = TRUE), digits = 2),
                 round(median(vec3, na.rm = TRUE), digits = 2),

                 sep = " & ")
  }
  out <- paste(out, " \\", "\\[", row.height, "pt]", " \n", sep = "")

  return(out)
}

get_descriptive_summary <- function(vec1, vec2, vec3, title, show.vec1 = TRUE, row.height = 0, increase = FALSE, alpha = 0.8) {

  color.imp <- "color-improvement"
  color.dec <- "color-decline"

  mean.vec1 <- mean(vec1, na.rm = TRUE)
  mean.vec2 <- mean(vec2, na.rm = TRUE)
  mean.vec3 <- mean(vec3, na.rm = TRUE)

  diff.vec1.vec2 <- mean.vec2-mean.vec1
  diff.vec1.vec3 <- mean.vec3-mean.vec1
  diff.vec2.vec3 <- mean.vec3-mean.vec2

  out <- paste(title, " & ", sep = "")

  if (show.vec1) {
    out <- paste(out, format(round(mean.vec1, digits = 2), nsmall = 2), " & ", sep = "")
  } else {
    out <- paste(out, "", " & ", sep = "")
  }

  if (diff.vec1.vec2 >= 0) {
    out <- paste(out, "\\cellcolor{", color.dec, sep = "")
  } else {
    out <- paste(out, "\\cellcolor{", color.imp, sep = "")
  }
  out <- paste(out, "!", (abs(round(diff.vec1.vec2*alpha, digits = 0))), "}",
               format(round(diff.vec1.vec2, digits = 2), nsmall = 2), " & ",
               sep = "")

  if (diff.vec1.vec3 >= 0) {
    out <- paste(out, "\\cellcolor{", color.dec, sep = "")
  } else {
    out <- paste(out, "\\cellcolor{", color.imp, sep = "")
  }
  out <- paste(out, "!", (abs(round(diff.vec1.vec3*alpha, digits = 0))), "}",
               format(round(diff.vec1.vec3, digits = 2), nsmall = 2), " & ",
               sep = "")

  if (diff.vec2.vec3 >= 0) {
    out <- paste(out, "\\cellcolor{", color.dec, sep = "")
  } else {
    out <- paste(out, "\\cellcolor{", color.imp, sep = "")
  }
  out <- paste(out, "!", (abs(round(diff.vec2.vec3*alpha, digits = 0))), "}",
               format(round(diff.vec2.vec3, digits = 2), nsmall = 2),
               sep = "")

  out <- paste(out, " \\", "\\[", row.height, "pt]", " \n", sep = "")

  return(out)
}

get_descriptives_table_prefix <- function(observations, scenario = NA) {
  out <- NA
  if (is.na(scenario)) {
    out <- paste("\\begin{table}[hbt!] \n", sep = "")
    out <- paste(out, "\\caption{Descriptive statistics", sep = "")
    out <- paste(out, " (observations: ", observations, ")", sep = "")
    out <- paste(out, ".}\n\\label{tab:descriptives}\n", sep = "")
  } else {
    out <- paste("\\begin{table}[hbt!] \n", sep = "")
    out <- paste(out, "\\captionof{table}{Descriptive statistics ", scenario, " (observations: ", observations, ")", sep = "")
    out <- paste(out, ".}\n\\label{tab:descriptives-", scenario, "}\n", sep = "")
  }
  out <- paste(out, "\\begin{adjustbox}{width=1\\textwidth,center=\\textwidth}\n\\begin{tabular}{l *{6}{S[\n", sep = "")
  out <- paste(out, "input-symbols = {(- )},\ndetect-weight,\ngroup-separator = {,},\ntable-format=5.2]}}\n\\toprule\n", sep = "")


  out <- paste(out, "& \\text{\\thead{Mean}} & \\text{\\thead{SD}} & \\text{\\thead{Median}} & \\text{\\thead{Min}} & \\text{\\thead{Max}} & ", sep = "")
  out <- paste(out, "\\text{\\thead{Skew}}", " \\", "\\ \n\\midrule \n", sep = "")

  return(out)
}

get_descriptives_table_suffix <- function(scenario = NA) {
  out <- "\\bottomrule\n%\\multicolumn{7}{c}{\\emph{Notes:} bold numbers are significant at $p<0.001$, SEs in parentheses}\n"
  if (is.na(scenario)) {
    out <- paste(out, "\\end{tabular}\n\\end{adjustbox}\n\\end{table}", sep = "")
  } else {
    out <- paste(out, "\\end{tabular}\n\\end{adjustbox}", sep = "")
  }
  return(out)
}

get_descriptives_short_table_prefix <- function() {

  out <- paste("\\begin{table}[hbt!] \n", sep = "")
  out <- paste(out, "\\caption{Comparison of mean degrees per major occupational group between empirical generated network data.", sep = "")
  out <- paste(out, ".}\n\\label{tab:mean-degrees}\n", sep = "")

  out <- paste(out, "\\begin{adjustbox}{width=1\\textwidth,center=\\textwidth}\n\\begin{tabular}{l *{4}{S[\n", sep = "")
  out <- paste(out, "input-symbols = {(- )},\ndetect-weight,\ngroup-separator = {,},\ntable-format=3.2]}}\n\\toprule\n", sep = "")

  out <- paste(out, "& \\multicolumn{2}{c}{\\textbf{Normal}} & \\multicolumn{2}{c}{\\textbf{Lockdown}}", " \\", "\\ \n", sep = "")
  out <- paste(out, "& \\text{\\thead{Empirical}} & \\text{\\thead{Generated}} & \\text{\\thead{Empirical}} & \\text{\\thead{Generated}} \\", "\\ \n\\midrule \n", sep = "")

  return(out)
}

get_descriptives_short_table_suffix <- function() {
  out <- "\\bottomrule\n%\\multicolumn{5}{c}{\\emph{Notes:} bold numbers are significant at $p<0.001$, SEs in parentheses}\n"
  out <- paste(out, "\\end{tabular}\n\\end{adjustbox}\n\\end{table}", sep = "")
  return(out)
}

get_finalsize_table_prefix <- function() {

  out <- paste("\\begin{table}[hbt!] \n", sep = "")
  out <- paste(out, "\\caption{Final sizes by conditions and controls", sep = "")
  out <- paste(out, ".}\n\\label{tab:final-sizes-by-conditions}\n", sep = "")
  out <- paste(out, "\\begin{adjustbox}{width=1\\textwidth,center=\\textwidth}\n\\begin{tabular}{l|ccc|ccc|ccc}\n\\toprule\n", sep = "")

  out <- paste(out, "& \\multicolumn{3}{c}{\\textbf{Baseline}} & \\multicolumn{3}{c}{\\textbf{Random}} & \\multicolumn{3}{c}{\\textbf{Targeted}}", " \\", "\\ \n", sep = "")

  out <- paste(out, "& \\textbf{Mean} & \\textbf{SD} & \\textbf{Median}", sep = "")
  out <- paste(out, "& \\textbf{Mean} & \\textbf{SD} & \\textbf{Median}", sep = "")
  out <- paste(out, "& \\textbf{Mean} & \\textbf{SD} & \\textbf{Median}", " \\", "\\ \n\\midrule\n", sep = "")

  # out <- paste("\\begin{table}[hbt!] \n", sep = "")
  # out <- paste(out, "\\caption{Final sizes by conditions and controls", sep = "")
  # out <- paste(out, ".}\n\\label{tab:final-sizes-by-conditions}\n", sep = "")
  # out <- paste(out, "\\begin{adjustbox}{width=1\\textwidth,center=\\textwidth}\n\\begin{tabular}{l *{9}{S[\n", sep = "")
  # out <- paste(out, "input-symbols = {(- )},\ndetect-weight,\ngroup-separator = {,},\ntable-format=3.2]}}\n\\toprule\n", sep = "")
  #
  # out <- paste(out, "& \\multicolumn{3}{|c}{\\textbf{Baseline}} & \\multicolumn{3}{|c}{\\textbf{Random}} & \\multicolumn{3}{|c}{\\textbf{Targeted}}", " \\", "\\ \n", sep = "")
  #
  # out <- paste(out, "& \\multicolumn{1}{|c}{\\textbf{Mean}} & \\multicolumn{1}{c}{\\textbf{SD}} & \\multicolumn{1}{c}{\\textbf{Median}}", sep = "")
  # out <- paste(out, "& \\multicolumn{1}{|c}{\\textbf{Mean}} & \\multicolumn{1}{c}{\\textbf{SD}} & \\multicolumn{1}{c}{\\textbf{Median}}", sep = "")
  # out <- paste(out, "& \\multicolumn{1}{|c}{\\textbf{Mean}} & \\multicolumn{1}{c}{\\textbf{SD}} & \\multicolumn{1}{c}{\\textbf{Median}}", " \\", "\\ \n\\midrule\n", sep = "")
}

get_duration_table_prefix <- function() {

  out <- paste("\\begin{table}[hbt!] \n", sep = "")
  out <- paste(out, "\\caption{Duration by conditions and controls", sep = "")
  out <- paste(out, ".}\n\\label{tab:duration-by-conditions}\n", sep = "")
  out <- paste(out, "\\begin{adjustbox}{width=1\\textwidth,center=\\textwidth}\n\\begin{tabular}{l|ccc|ccc|ccc}\n\\toprule\n", sep = "")

  out <- paste(out, "& \\multicolumn{3}{c}{\\textbf{Baseline}} & \\multicolumn{3}{c}{\\textbf{Random}} & \\multicolumn{3}{c}{\\textbf{Targeted}}", " \\", "\\ \n", sep = "")

  out <- paste(out, "& \\textbf{Mean} & \\textbf{SD} & \\textbf{Median}", sep = "")
  out <- paste(out, "& \\textbf{Mean} & \\textbf{SD} & \\textbf{Median}", sep = "")
  out <- paste(out, "& \\textbf{Mean} & \\textbf{SD} & \\textbf{Median}", " \\", "\\ \n\\midrule\n", sep = "")

  # out <- paste("\\begin{table}[hbt!] \n", sep = "")
  # out <- paste(out, "\\caption{Final sizes by conditions and controls", sep = "")
  # out <- paste(out, ".}\n\\label{tab:final-sizes-by-conditions}\n", sep = "")
  # out <- paste(out, "\\begin{adjustbox}{width=1\\textwidth,center=\\textwidth}\n\\begin{tabular}{l *{9}{S[\n", sep = "")
  # out <- paste(out, "input-symbols = {(- )},\ndetect-weight,\ngroup-separator = {,},\ntable-format=3.2]}}\n\\toprule\n", sep = "")
  #
  # out <- paste(out, "& \\multicolumn{3}{|c}{\\textbf{Baseline}} & \\multicolumn{3}{|c}{\\textbf{Random}} & \\multicolumn{3}{|c}{\\textbf{Targeted}}", " \\", "\\ \n", sep = "")
  #
  # out <- paste(out, "& \\multicolumn{1}{|c}{\\textbf{Mean}} & \\multicolumn{1}{c}{\\textbf{SD}} & \\multicolumn{1}{c}{\\textbf{Median}}", sep = "")
  # out <- paste(out, "& \\multicolumn{1}{|c}{\\textbf{Mean}} & \\multicolumn{1}{c}{\\textbf{SD}} & \\multicolumn{1}{c}{\\textbf{Median}}", sep = "")
  # out <- paste(out, "& \\multicolumn{1}{|c}{\\textbf{Mean}} & \\multicolumn{1}{c}{\\textbf{SD}} & \\multicolumn{1}{c}{\\textbf{Median}}", " \\", "\\ \n\\midrule\n", sep = "")
}

get_peaksize_table_prefix <- function() {

  out <- paste("\\begin{table}[hbt!] \n", sep = "")
  out <- paste(out, "\\caption{Peak sizes by conditions and controls", sep = "")
  out <- paste(out, ".}\n\\label{tab:peak-sizes-by-conditions}\n", sep = "")
  out <- paste(out, "\\begin{adjustbox}{width=1\\textwidth,center=\\textwidth}\n\\begin{tabular}{l|ccc|ccc|ccc}\n\\toprule\n", sep = "")

  out <- paste(out, "& \\multicolumn{3}{c}{\\textbf{Baseline}} & \\multicolumn{3}{c}{\\textbf{Random}} & \\multicolumn{3}{c}{\\textbf{Targeted}}", " \\", "\\ \n", sep = "")

  out <- paste(out, "& \\textbf{Mean} & \\textbf{SD} & \\textbf{Median}", sep = "")
  out <- paste(out, "& \\textbf{Mean} & \\textbf{SD} & \\textbf{Median}", sep = "")
  out <- paste(out, "& \\textbf{Mean} & \\textbf{SD} & \\textbf{Median}", " \\", "\\ \n\\midrule\n", sep = "")

  # out <- paste("\\begin{table}[hbt!] \n", sep = "")
  # out <- paste(out, "\\caption{Final sizes by conditions and controls", sep = "")
  # out <- paste(out, ".}\n\\label{tab:final-sizes-by-conditions}\n", sep = "")
  # out <- paste(out, "\\begin{adjustbox}{width=1\\textwidth,center=\\textwidth}\n\\begin{tabular}{l *{9}{S[\n", sep = "")
  # out <- paste(out, "input-symbols = {(- )},\ndetect-weight,\ngroup-separator = {,},\ntable-format=3.2]}}\n\\toprule\n", sep = "")
  #
  # out <- paste(out, "& \\multicolumn{3}{|c}{\\textbf{Baseline}} & \\multicolumn{3}{|c}{\\textbf{Random}} & \\multicolumn{3}{|c}{\\textbf{Targeted}}", " \\", "\\ \n", sep = "")
  #
  # out <- paste(out, "& \\multicolumn{1}{|c}{\\textbf{Mean}} & \\multicolumn{1}{c}{\\textbf{SD}} & \\multicolumn{1}{c}{\\textbf{Median}}", sep = "")
  # out <- paste(out, "& \\multicolumn{1}{|c}{\\textbf{Mean}} & \\multicolumn{1}{c}{\\textbf{SD}} & \\multicolumn{1}{c}{\\textbf{Median}}", sep = "")
  # out <- paste(out, "& \\multicolumn{1}{|c}{\\textbf{Mean}} & \\multicolumn{1}{c}{\\textbf{SD}} & \\multicolumn{1}{c}{\\textbf{Median}}", " \\", "\\ \n\\midrule\n", sep = "")
}


get_finalsize_table_suffix <- function(scenario = NA) {
  out <- "\\bottomrule\n%\\multicolumn{10}{c}{\\emph{Notes:} bold numbers are significant at $p<0.001$, SEs in parentheses}\n"
  out <- paste(out, "\\end{tabular}\n\\end{adjustbox}\n\\end{table}", sep = "")
  return(out)
}

get_finalsize_summary_table_prefix <- function() {

  out <- paste("\\begin{table}[hbt!] \n", sep = "")
  out <- paste(out, "\\caption{Mean final size of baseline condition and reduction by test condition in percent", sep = "")
  out <- paste(out, ".}\n\\label{tab:final-sizes-by-conditions-summary}\n", sep = "")
  out <- paste(out, "\\begin{adjustbox}{width=1\\textwidth,center=\\textwidth}\n\\begin{tabular}{l|c|c|cc}\n\\toprule\n", sep = "")

  out <- paste(out, "& \\textbf{Baseline} & \\textbf{Random} & \\multicolumn{2}{c}{\\textbf{Targeted}}", " \\", "\\ \n", sep = "")

  out <- paste(out, "& ", sep = "")
  out <- paste(out, "& \\textbf{to baseline} ", sep = "")
  out <- paste(out, "& \\textbf{to baseline} & \\textbf{to Random} ", " \\", "\\ \n\\midrule\n", sep = "")

  return(out)
}

get_duration_summary_table_prefix <- function() {

  out <- paste("\\begin{table}[hbt!] \n", sep = "")
  out <- paste(out, "\\caption{Mean duration of baseline condition and reduction by test condition in percent", sep = "")
  out <- paste(out, ".}\n\\label{tab:duration-by-conditions-summary}\n", sep = "")
  out <- paste(out, "\\begin{adjustbox}{width=1\\textwidth,center=\\textwidth}\n\\begin{tabular}{l|c|c|cc}\n\\toprule\n", sep = "")

  out <- paste(out, "& \\textbf{Baseline} & \\textbf{Random} & \\multicolumn{2}{c}{\\textbf{Targeted}}", " \\", "\\ \n", sep = "")

  out <- paste(out, "& ", sep = "")
  out <- paste(out, "& \\textbf{to baseline} ", sep = "")
  out <- paste(out, "& \\textbf{to baseline} & \\textbf{to Random} ", " \\", "\\ \n\\midrule\n", sep = "")

  return(out)
}

get_peaksize_summary_table_prefix <- function() {

  out <- paste("\\begin{table}[hbt!] \n", sep = "")
  out <- paste(out, "\\caption{Mean peak size of baseline condition and reduction by test condition in percent", sep = "")
  out <- paste(out, ".}\n\\label{tab:peak-sizes-by-conditions-summary}\n", sep = "")
  out <- paste(out, "\\begin{adjustbox}{width=1\\textwidth,center=\\textwidth}\n\\begin{tabular}{l|c|c|cc}\n\\toprule\n", sep = "")

  out <- paste(out, "& \\textbf{Baseline} & \\textbf{Random} & \\multicolumn{2}{c}{\\textbf{Targeted}}", " \\", "\\ \n", sep = "")

  out <- paste(out, "& ", sep = "")
  out <- paste(out, "& \\textbf{to baseline} ", sep = "")
  out <- paste(out, "& \\textbf{to baseline} & \\textbf{to Random} ", " \\", "\\ \n\\midrule\n", sep = "")

  return(out)
}

get_summary_table_suffix <- function(scenario = NA) {
  out <- "\\bottomrule\n\\multicolumn{5}{c}{\\emph{Notes:} ...}\n"
  out <- paste(out, "\\end{tabular}\n\\end{adjustbox}\n\\end{table}", sep = "")
  return(out)
}

get_professions_table_prefix <- function(number) {
  out <- paste("\\begin{table}[hbt!]\n\\caption{Professional groups, part ", number, sep = "")
  out <- paste(out, ".}\n\\label{tab:professions", number, "}\n", sep = "")
  out <- paste(out, "\\begin{adjustbox}{width=1\\textwidth,center=\\textwidth}\n", sep = "")
  out <- paste(out, "\\begin{tabular}{l *{6}{S[\n", sep = "")
  out <- paste(out, "input-symbols = {(- )},\ndetect-weight,\ngroup-separator = {,},\ntable-format=5.2]}}\n\\toprule\n", sep = "")
  out <- paste(out, "& \\text{\\thead{Mean}} & \\text{\\thead{SD}} & \\text{\\thead{Median}} & \\text{\\thead{Min}} & \\text{\\thead{Max}} & ", sep = "")
  out <- paste(out, "\\text{\\thead{Skew}}", " \\", "\\ \n\\midrule", sep = "")
  return(out)
}

get_professions_table_suffix <- function() {
  out <- "\\bottomrule\n%\\multicolumn{7}{c}{\\emph{Notes:} bold numbers are significant at $p<0.001$, SEs in parentheses}\n"
  out <- paste(out, "\\end{tabular}\n\\end{adjustbox}\n\\end{table}", sep = "")
  return(out)
}

#----------------------------------------------------------------------------------------------------#
# function: export_descriptives
#     Exports general descriptive statistics.
# param:  data.ss
#     the simulation summary data
# param:  data.as
#     the agent stats data
# param:  data.pr
#     the professions data
# param:  data.ad
#     the agent details data
#----------------------------------------------------------------------------------------------------#
export_descriptives <- function(data.ss = load_simulation_summary_data(),
                                # data.as = load_agent_stats_data(),
                                data.pr = load_professions_data(),
                                filename.ext = NA,
                                include.groups = TRUE) {

  if(.Platform$OS.type == "windows") {
    memory.limit(9999999999)
  }

  data.ss.normal <- subset(data.ss, nb.prof.lockdown.condition == "lc.pre")
  data.ss.lockdown <- subset(data.ss, nb.prof.lockdown.condition == "lc.during")

  # begin: overall descriptives
  print(paste("Begin overall descriptives."))
  out <- paste(get_descriptives_table_prefix(observations = nrow(data.ss)), sep = "")

  out <- paste(out, "\\midrule \n", sep = "")
  out <- paste(out, "\\multicolumn{7}{l}{\\textbf{\\textit{I. Network}}}", " \\", "\\[6pt] \n", sep = "")
  # out <- paste(out, "\\multicolumn{7}{l}{\\textit{I.I. Overall}}", " \\", "\\ \n", sep = "")
  # out <- paste(out, get_descriptive(data.ss$net.degree.av, "Degree ($\\mathcal{D}_{G}$)"))
  # out <- paste(out, get_descriptive(data.ss$net.clustering.av, "Clustering ($\\mathcal{C}_{G}$)"))
  # out <- paste(out, get_descriptive(data.ss$net.pathlength.av, "Path length ($\\mathcal{L}_{G}$)"))
  # out <- paste(out, get_descriptive(data.ss$net.assortativity.profession, "Assortativity, profession ($\\mathcal{A}^{p}_{G}$)"))

  out <- paste(out, "\\multicolumn{7}{l}{\\textit{I.I. Normal}}", " \\", "\\ \n", sep = "")
  out <- paste(out, get_descriptive(data.ss.normal$net.degree.av, "Av. degree ($\\mathcal{D}_{G}$)"))
  out <- paste(out, get_descriptive(data.ss.normal$net.clustering.av, "Clustering ($\\mathcal{C}_{G}$)"))
  out <- paste(out, get_descriptive(data.ss.normal$net.pathlength.av, "Av. path length ($\\mathcal{L}_{G}$)"))
  out <- paste(out, get_descriptive(data.ss.normal$net.assortativity.profession,
                                    "Assortativity, profession ($\\mathcal{A}^{p}_{G}$)", row.height = 6))

  out <- paste(out, "\\multicolumn{7}{l}{\\textit{I.II. Lockdown}}", " \\", "\\ \n", sep = "")
  out <- paste(out, get_descriptive(data.ss.lockdown$net.degree.av, "Av. degree ($\\mathcal{D}_{G}$)"))
  out <- paste(out, get_descriptive(data.ss.lockdown$net.clustering.av, "Clustering ($\\mathcal{C}_{G}$)"))
  out <- paste(out, get_descriptive(data.ss.lockdown$net.pathlength.av, "Av. path length ($\\mathcal{L}_{G}$)"))
  out <- paste(out, get_descriptive(data.ss.lockdown$net.assortativity.profession, "Assortativity, profession ($\\mathcal{A}^{p}_{G}$)"))

  out <- paste(out, "\\midrule \n", sep = "")
  out <- paste(out, "\\multicolumn{7}{l}{\\textbf{\\textit{II. Index case}}}", " \\", "\\[6pt] \n", sep = "")
  # out <- paste(out, "\\multicolumn{7}{l}{\\textit{II.I. Overall}}", " \\", "\\ \n", sep = "")
  # out <- paste(out, get_descriptive(data.ss$index.degree, "Degree"))
  # out <- paste(out, get_descriptive(data.ss$index.clustering, "Clustering"))
  # out <- paste(out, get_descriptive(data.ss$index.closeness, "Closeness"))
  # out <- paste(out, get_descriptive(data.ss$index.assortativity.profession, "Assortativity (profession)"))

  out <- paste(out, "\\multicolumn{7}{l}{\\textit{II.I. Normal}}", " \\", "\\ \n", sep = "")
  out <- paste(out, get_descriptive(data.ss.normal$index.degree, "Degree"))
  out <- paste(out, get_descriptive(data.ss.normal$index.clustering, "Clustering"))
  out <- paste(out, get_descriptive(data.ss.normal$index.closeness, "Closeness"))
  out <- paste(out, get_descriptive(data.ss.normal$index.assortativity.profession,
                                    "Assortativity (profession)", row.height = 6))

  out <- paste(out, "\\multicolumn{7}{l}{\\textit{II.II. Lockdown}}", " \\", "\\ \n", sep = "")
  out <- paste(out, get_descriptive(data.ss.lockdown$index.degree, "Degree"))
  out <- paste(out, get_descriptive(data.ss.lockdown$index.clustering, "Clustering"))
  out <- paste(out, get_descriptive(data.ss.lockdown$index.closeness, "Closeness"))
  out <- paste(out, get_descriptive(data.ss.lockdown$index.assortativity.profession, "Assortativity (profession)"))

  out <- paste(out, "\\midrule \n", sep = "")
  out <- paste(out, "\\multicolumn{7}{l}{\\textbf{\\textit{III. Epidemic}}}", " \\", "\\[6pt] \n", sep = "")
  # out <- paste(out, "\\multicolumn{7}{l}{\\textit{III.I. Overall}}", " \\", "\\ \n", sep = "")
  # out <- paste(out, get_descriptive(data.ss$net.pct.rec, "Final size"))
  # out <- paste(out, get_descriptive(data.ss$net.epidemic.duration, "Duration"))
  # # out <- paste(out, get_descriptive(data.ss$net.epidemic.peak.time, "Epidemic peak time"))
  # out <- paste(out, get_descriptive(data.ss$net.epidemic.peak.size, "Epidemic peak size"))

  out <- paste(out, "\\multicolumn{7}{l}{\\textit{III.I. Normal}}", " \\", "\\ \n", sep = "")
  out <- paste(out, get_descriptive(data.ss.normal$net.pct.rec, "Final size"))
  out <- paste(out, get_descriptive(data.ss.normal$net.epidemic.duration, "Duration"))
  # out <- paste(out, get_descriptive(data.ss.normal$net.epidemic.peak.time, "Epidemic peak time"))
  out <- paste(out, get_descriptive(data.ss.normal$net.epidemic.peak.size, "Epidemic peak size", row.height = 6))

  out <- paste(out, "\\multicolumn{7}{l}{\\textit{III.II. Lockdown}}", " \\", "\\ \n", sep = "")
  out <- paste(out, get_descriptive(data.ss.lockdown$net.pct.rec, "Final size"))
  out <- paste(out, get_descriptive(data.ss.lockdown$net.epidemic.duration, "Duration"))
  # out <- paste(out, get_descriptive(data.ss.lockdown$net.epidemic.peak.time, "Epidemic peak time"))
  out <- paste(out, get_descriptive(data.ss.lockdown$net.epidemic.peak.size, "Epidemic peak size"))

  out <- paste(out, "\\midrule \n", sep = "")
  out <- paste(out, "\\multicolumn{7}{l}{\\textbf{\\textit{IV. Counter measures}}}", " \\", "\\[6pt] \n", sep = "")
  out <- paste(out, "\\multicolumn{7}{l}{\\textit{IV.I. Vaccinations}}", " \\", "\\ \n", sep = "")
  out <- paste(out, get_descriptive(data.ss$nb.prof.vaccine.shots.given / data.ss$nb.N, "Vaccinated (proportion)"))
  out <- paste(out, get_descriptive(data.ss$net.pct.vac / 100, "Immunized (proportion)", row.height = 6))

  data.ss.baseline = subset(data.ss, data.ss$nb.prof.vaccine.distribution == "none")
  out <- paste(out, "\\multicolumn{7}{l}{\\textit{IV.II. Baseline}}", " \\", "\\ \n", sep = "")
  out <- paste(out, get_descriptive(data.ss.baseline$net.pct.rec, "Final size"))
  out <- paste(out, get_descriptive(data.ss.baseline$net.epidemic.duration, "Duration"))
  out <- paste(out, get_descriptive(data.ss.baseline$net.epidemic.peak.size, "Peak size", row.height = 6))
  # out <- paste(out, get_descriptive(data.ss.baseline$net.epidemic.peak.time, "Peak time"))

  data.ss.random = subset(data.ss, data.ss$nb.prof.vaccine.distribution == "random")
  out <- paste(out, "\\multicolumn{7}{l}{\\textit{IV.III. Random vaccine distribution}}", " \\", "\\ \n", sep = "")
  out <- paste(out, get_descriptive(data.ss.random$net.pct.rec, "Final size"))
  out <- paste(out, get_descriptive(data.ss.random$net.epidemic.duration, "Duration"))
  out <- paste(out, get_descriptive(data.ss.random$net.epidemic.peak.size, "Peak size", row.height = 6))
  # out <- paste(out, get_descriptive(data.ss.random$net.epidemic.peak.time, "Peak time"))

  data.ss.targeted = subset(data.ss, data.ss$nb.prof.vaccine.distribution == "by.av.degree.per.prof.group")
  out <- paste(out, "\\multicolumn{7}{l}{\\textit{IV.IV. Vaccine distribution prioritizing professional groups by largest degree}}", " \\", "\\ \n", sep = "")
  out <- paste(out, get_descriptive(data.ss.targeted$net.pct.rec, "Final size"))
  out <- paste(out, get_descriptive(data.ss.targeted$net.epidemic.duration, "Duration"))
  out <- paste(out, get_descriptive(data.ss.targeted$net.epidemic.peak.size, "Peak size"))
  # out <- paste(out, get_descriptive(data.ss.targeted$net.epidemic.peak.time, "Peak time"))


  out <- paste(out, get_descriptives_table_suffix(), "\n\n", sep = "")
  print(paste("End overall descriptives."))
  # end: overall descriptive


  # begin: occupational groups
  if (include.groups) {

    ######### PREPARATIONS BELOT DATA #########
    belot <- load_belot_public_data()
    # removing implausible data point with degree of 2*10^12
    belot <- subset(belot, close_recentint_less15_child < 2000000000000)

    # exclusion criteria: no industry, no profession, not employed
    belot <- subset(belot, belot$industry != "")
    belot <- subset(belot, belot$profession != "")
    belot <- subset(belot, belot$labor_status != "Not in employment")

    # adding occupational hierarchy (SOC - https://www.bls.gov/soc/2018/home.htm)
    soc <- load_soc_profession_structure()
    belot_soc <- merge(belot, soc, by.x = "profession", by.y = "detailed_occupation", all.x = TRUE)
    belot_soc <- subset(belot_soc, !is.na(major_group))

    out.short <- get_descriptives_short_table_prefix()

    t <- 1
    t.number <- 1
    table.closed <- TRUE
    row.height <- 0

    colIndex <- 1
    for (col in colnames(data.ss)) {

      if (grepl('^nb.prof.n.', col)) {

        prof <- str_replace(col, "nb.prof.n.", "")
        prof.full.name <- subset(data.pr, tolower(V1) == prof)[1,8]
        print(paste("Begin descriptives for: ", prof.full.name, sep = ""))

        # preparing Belot data
        belot_soc_mg <- subset(belot_soc, major_group == prof.full.name)
        belot_soc_mg_normal <- c(belot_soc_mg$close_workint_less15_child,
                                 belot_soc_mg$close_workint_less15_adult,
                                 belot_soc_mg$close_workint_less15_elder,
                                 belot_soc_mg$close_workint_more15_child,
                                 belot_soc_mg$close_workint_more15_adult,
                                 belot_soc_mg$close_workint_more15_elder)
        belot_soc_mg_lockdown <- c(belot_soc_mg$close_recentint_less15_child,
                                   belot_soc_mg$close_recentint_less15_adult,
                                   belot_soc_mg$close_recentint_less15_elder,
                                   belot_soc_mg$close_recentint_more15_child,
                                   belot_soc_mg$close_recentint_more15_adult,
                                   belot_soc_mg$close_recentint_more15_elder)

        if (t == 1) {
          out <- paste (out, get_professions_table_prefix(t.number), sep = "")
          table.closed <- FALSE
        }

        out <- paste(out, "\\midrule \n", sep = "")
        out <- paste(out, "\\multicolumn{7}{l}{\\textbf{", prof.full.name, "}}", " \\", "\\[", (row.height + 6), "pt] \n", sep = "")

        ### DATA
        # N
        out <- paste(out, "Share of the labor market (empirical) & ",
                     round(subset(data.pr, tolower(V1) == prof)[1,2] / sum(data.pr[,2]), 2),
                     "& & & & ", " \\", "\\[", row.height, "pt] \n", sep = "")
        out <- paste(out, get_descriptive(data.ss[,colIndex] / data.ss$nb.N, "Share of the labor market (generated)", row.height = row.height+6))

        # degree, normal
        out <- paste(out,
                     paste(
                       "Degree, normal (empirical)",
                       round(mean(belot_soc_mg_normal, na.rm = TRUE), digits = 2),               # mean av. degree
                       round(sd(belot_soc_mg_normal, na.rm = TRUE), digits = 2),                 # sd of av. degree
                       round(median(belot_soc_mg_normal, na.rm = TRUE), digits = 2),             # median av. degree
                       round(min(belot_soc_mg_normal, na.rm = TRUE), digits = 2),                # min av. degree
                       round(max(belot_soc_mg_normal, na.rm = TRUE), digits = 2),                # max av. degree
                       round(skewness(belot_soc_mg_normal, na.rm = TRUE), digits = 2),           # skewness av. degree
                       sep = " & "))
        out <- paste(out, " \\", "\\[", row.height, "pt]", " \n", sep = "")

        normal <- subset(data.ss, nb.prof.lockdown.condition == "lc.pre")
        out <- paste(out,
                     paste(
                       "Degree, normal (network av.)",
                       round(mean(normal[,colIndex+1], na.rm = TRUE), digits = 2),               # mean av. degree
                       round(mean(normal[,colIndex+3], na.rm = TRUE), digits = 2),               # sd of av. degree
                       round(median(normal[,colIndex+1], na.rm = TRUE), digits = 2),             # median av. degree
                       round(min(normal[,colIndex+1], na.rm = TRUE), digits = 2),                # min av. degree
                       round(max(normal[,colIndex+1], na.rm = TRUE), digits = 2),                # max av. degree
                       round(skewness(normal[,colIndex+1], na.rm = TRUE), digits = 2),           # skewness av. degree
                       sep = " & "))
        out <- paste(out, " \\", "\\[", row.height, "pt]", " \n", sep = "")

        # data.as.prof.pre <- subset(data.as, tolower(agent.profession) == prof & nb.prof.lockdown.condition == "lc.pre")
        # out <- paste(out,
        #              paste(
        #                "Degree, normal (generated)",
        #                round(mean(data.as.prof.pre$agent.degree, na.rm = TRUE), digits = 2),     # mean av. degree
        #                round(sd(data.as.prof.pre$agent.degree, na.rm = TRUE), digits = 2),       # sd of av. degree
        #                round(median(data.as.prof.pre$agent.degree, na.rm = TRUE), digits = 2),   # median av. degree
        #                round(min(data.as.prof.pre$agent.degree, na.rm = TRUE), digits = 2),      # min av. degree
        #                round(max(data.as.prof.pre$agent.degree, na.rm = TRUE), digits = 2),      # max av. degree
        #                round(skewness(data.as.prof.pre$agent.degree, na.rm = TRUE), digits = 2), # skewness av. degree
        #                sep = " & "))
        # out <- paste(out, " \\", "\\[", (row.height+6), "pt]", " \n", sep = "")

        # out <- paste(out, "Degree, normal (Belot) & ",
        #              subset(data.pr, tolower(V1) == prof)[1,3], " & ",
        #              subset(data.pr, tolower(V1) == prof)[1,4], " & NA & NA & NA ", " \\", "\\[", (row.height + 6), "pt] \n", sep = "")

        # degree, lockdown
        out <- paste(out,
                     paste(
                       "Degree, lockdown (empirical)",
                       round(mean(belot_soc_mg_lockdown, na.rm = TRUE), digits = 2),               # mean av. degree
                       round(sd(belot_soc_mg_lockdown, na.rm = TRUE), digits = 2),                 # sd of av. degree
                       round(median(belot_soc_mg_lockdown, na.rm = TRUE), digits = 2),             # median av. degree
                       round(min(belot_soc_mg_lockdown, na.rm = TRUE), digits = 2),                # min av. degree
                       round(max(belot_soc_mg_lockdown, na.rm = TRUE), digits = 2),                # max av. degree
                       round(skewness(belot_soc_mg_lockdown, na.rm = TRUE), digits = 2),           # skewness av. degree
                       sep = " & "))
        out <- paste(out, " \\", "\\[", row.height, "pt]", " \n", sep = "")

        lockdown <- subset(data.ss, nb.prof.lockdown.condition == "lc.during")
        out <- paste(out,
                     paste(
                       "Degree, lockdown (network av.)",
                       round(mean(lockdown[,colIndex+1], na.rm = TRUE), digits = 2),               # mean av. degree
                       round(mean(lockdown[,colIndex+3], na.rm = TRUE), digits = 2),               # sd of av. degree
                       round(median(lockdown[,colIndex+1], na.rm = TRUE), digits = 2),             # median av. degree
                       round(min(lockdown[,colIndex+1], na.rm = TRUE), digits = 2),                # min av. degree
                       round(max(lockdown[,colIndex+1], na.rm = TRUE), digits = 2),                # max av. degree
                       round(skewness(lockdown[,colIndex+1], na.rm = TRUE), digits = 2),           # skewness av. degree
                       sep = " & "))
        out <- paste(out, " \\", "\\[", row.height, "pt]", " \n", sep = "")

        # data.as.prof.lockdown <- subset(data.as, tolower(agent.profession) == prof & nb.prof.lockdown.condition == "lc.during")
        # out <- paste(out,
        #              paste(
        #                "Degree, lockdown (generated)",
        #                round(mean(data.as.prof.lockdown$agent.degree, na.rm = TRUE), digits = 2),     # mean av. degree
        #                round(sd(data.as.prof.lockdown$agent.degree, na.rm = TRUE), digits = 2),       # sd of av. degree
        #                round(median(data.as.prof.lockdown$agent.degree, na.rm = TRUE), digits = 2),   # median av. degree
        #                round(min(data.as.prof.lockdown$agent.degree, na.rm = TRUE), digits = 2),      # min av. degree
        #                round(max(data.as.prof.lockdown$agent.degree, na.rm = TRUE), digits = 2),      # max av. degree
        #                round(skewness(data.as.prof.lockdown$agent.degree, na.rm = TRUE), digits = 2), # skewness av. degree
        #                sep = " & "))
        # out <- paste(out, " \\", "\\[", row.height, "pt]", " \n", sep = "")

        # out <- paste(out, "Degree, lockdown (Belot) & ",
        #              subset(data.pr, tolower(V1) == prof)[1,5], " & ",
        #              subset(data.pr, tolower(V1) == prof)[1,6], " & NA & NA & NA ", " \\", "\\ \n", sep = "")

        # short

        out.short <- paste(out.short,
                           subset(data.pr, tolower(V1) == prof)[1,8], " & ",
                           subset(data.pr, tolower(V1) == prof)[1,3], " & ",
                           round(mean(normal[,colIndex+1], na.rm = TRUE), digits = 2), " & ",
                           subset(data.pr, tolower(V1) == prof)[1,5], " & ",
                           round(mean(lockdown[,colIndex+1], na.rm = TRUE), digits = 2), " \\", "\\ \n", sep = "")

        # housekeeping
        if (t >= 6) {
          out <- paste (out, get_professions_table_suffix(), "\n\n", sep = "")
          t <- 1
          table.closed <- TRUE
          t.number <- t.number+1
        } else {
          t <- t+1
        }

      }
      colIndex <- colIndex+1
    }
    if (!table.closed) {
      out <- paste (out, get_professions_table_suffix(), "\n\n", sep = "")
    }


    out.short <- paste(out.short, get_descriptives_short_table_suffix())
    out <- paste(out, out.short, sep = "")
  }

  # export to file
  dir.create(EXPORT_PATH_NUM, showWarnings = FALSE)
  filename <- paste(EXPORT_PATH_NUM, "descriptives", sep = "")
  if (!is.na(filename.ext)) {
    filename <- paste(filename, "-", filename.ext, sep = "")
  }
  filename <- paste(filename, EXPORT_FILE_EXTENSION_DESC, sep = "")
  cat(out, file = filename)
}




export_overview <- function(data.ss = load_simulation_summary_data(),
                            filename.ext = NA) {

  alpha.finsize <- 1.0
  alpha.duration <- 1.3
  alpha.peaksize <- 1.3

  if(.Platform$OS.type == "windows") {
    memory.limit(9999999999)
  }

  data.ss.baseline = subset(data.ss, data.ss$nb.prof.vaccine.distribution == "none")
  data.ss.random = subset(data.ss, data.ss$nb.prof.vaccine.distribution == "random")
  data.ss.targeted = subset(data.ss, data.ss$nb.prof.vaccine.distribution == "by.av.degree.per.prof.group")

  out.finsize <- paste(get_finalsize_table_prefix(), sep = "")
  out.finsize.summary <- paste(get_finalsize_summary_table_prefix(), sep = "")

  out.duration <- paste(get_duration_table_prefix(), sep = "")
  out.duration.summary <- paste(get_duration_summary_table_prefix(), sep = "")

  out.peaksize <- paste(get_peaksize_table_prefix(), sep = "")
  out.peaksize.summary <- paste(get_peaksize_summary_table_prefix(), sep = "")

  for (lc in c("lc.pre", "lc.during")) {

    data.ss.baseline.lc <- subset(data.ss.baseline, nb.prof.lockdown.condition == lc)
    data.ss.random.lc <- subset(data.ss.random, nb.prof.lockdown.condition == lc)
    data.ss.targeted.lc <- subset(data.ss.targeted, nb.prof.lockdown.condition == lc)

    if (lc == "lc.pre") {
      out.finsize <- paste(out.finsize, "\\textbf{I. No lockdown} & & & & & & & & &", " \\", "\\ \n", sep = "")
      out.finsize <- paste(out.finsize, "\\midrule \n", sep = "")

      out.finsize.summary <- paste(out.finsize.summary, "\\textbf{I. No lockdown} & & & &", " \\", "\\ \n", sep = "")
      out.finsize.summary <- paste(out.finsize.summary, "\\midrule \n", sep = "")

      out.duration <- paste(out.duration, "\\textbf{I. No lockdown} & & & & & & & & &", " \\", "\\ \n", sep = "")
      out.duration <- paste(out.duration, "\\midrule \n", sep = "")

      out.duration.summary <- paste(out.duration.summary, "\\textbf{I. No lockdown} & & & &", " \\", "\\ \n", sep = "")
      out.duration.summary <- paste(out.duration.summary, "\\midrule \n", sep = "")

      out.peaksize <- paste(out.peaksize, "\\textbf{I. No lockdown} & & & & & & & & &", " \\", "\\ \n", sep = "")
      out.peaksize <- paste(out.peaksize, "\\midrule \n", sep = "")

      out.peaksize.summary <- paste(out.peaksize.summary, "\\textbf{I. No lockdown} & & & &", " \\", "\\ \n", sep = "")
      out.peaksize.summary <- paste(out.peaksize.summary, "\\midrule \n", sep = "")

    } else {
      out.finsize <- paste(out.finsize, "\\midrule \n", sep = "")
      out.finsize <- paste(out.finsize, "\\textbf{II. Lockdown} & & & & & & & & &", " \\", "\\ \n", sep = "")
      out.finsize <- paste(out.finsize, "\\midrule \n", sep = "")

      out.finsize.summary <- paste(out.finsize.summary, "\\midrule \n", sep = "")
      out.finsize.summary <- paste(out.finsize.summary, "\\textbf{II. Lockdown} & & & &", " \\", "\\ \n", sep = "")
      out.finsize.summary <- paste(out.finsize.summary, "\\midrule \n", sep = "")

      out.duration <- paste(out.duration, "\\midrule \n", sep = "")
      out.duration <- paste(out.duration, "\\textbf{II. Lockdown} & & & & & & & & &", " \\", "\\ \n", sep = "")
      out.duration <- paste(out.duration, "\\midrule \n", sep = "")

      out.duration.summary <- paste(out.duration.summary, "\\midrule \n", sep = "")
      out.duration.summary <- paste(out.duration.summary, "\\textbf{II. Lockdown} & & & &", " \\", "\\ \n", sep = "")
      out.duration.summary <- paste(out.duration.summary, "\\midrule \n", sep = "")

      out.peaksize <- paste(out.peaksize, "\\midrule \n", sep = "")
      out.peaksize <- paste(out.peaksize, "\\textbf{II. Lockdown} & & & & & & & & &", " \\", "\\ \n", sep = "")
      out.peaksize <- paste(out.peaksize, "\\midrule \n", sep = "")

      out.peaksize.summary <- paste(out.peaksize.summary, "\\midrule \n", sep = "")
      out.peaksize.summary <- paste(out.peaksize.summary, "\\textbf{II. Lockdown} & & & &", " \\", "\\ \n", sep = "")
      out.peaksize.summary <- paste(out.peaksize.summary, "\\midrule \n", sep = "")
    }

    ### OVERALL
    out.finsize <- paste(out.finsize, get_descriptive_compressed(data.ss.baseline.lc$net.pct.rec,
                                                                 data.ss.random.lc$net.pct.rec,
                                                                 data.ss.targeted.lc$net.pct.rec,
                                                                 "Overall"))
    out.finsize <- paste(out.finsize, "\\midrule \n", sep = "")

    out.finsize.summary <- paste(out.finsize.summary, get_descriptive_summary(data.ss.baseline.lc$net.pct.rec,
                                                                              data.ss.random.lc$net.pct.rec,
                                                                              data.ss.targeted.lc$net.pct.rec,
                                                                              "Overall", alpha = alpha.finsize))
    out.finsize.summary <- paste(out.finsize.summary, "\\midrule \n", sep = "")

    out.duration <- paste(out.duration, get_descriptive_compressed(data.ss.baseline.lc$net.epidemic.duration,
                                                                 data.ss.random.lc$net.epidemic.duration,
                                                                 data.ss.targeted.lc$net.epidemic.duration,
                                                                 "Overall"))
    out.duration <- paste(out.duration, "\\midrule \n", sep = "")

    out.duration.summary <- paste(out.duration.summary, get_descriptive_summary(data.ss.baseline.lc$net.epidemic.duration,
                                                                              data.ss.random.lc$net.epidemic.duration,
                                                                              data.ss.targeted.lc$net.epidemic.duration,
                                                                              "Overall", alpha = alpha.duration))
    out.duration.summary <- paste(out.duration.summary, "\\midrule \n", sep = "")

    out.peaksize <- paste(out.peaksize, get_descriptive_compressed(data.ss.baseline.lc$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                 data.ss.random.lc$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                 data.ss.targeted.lc$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                 "Overall"))
    out.peaksize <- paste(out.peaksize, "\\midrule \n", sep = "")

    out.peaksize.summary <- paste(out.peaksize.summary, get_descriptive_summary(data.ss.baseline.lc$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                              data.ss.random.lc$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                              data.ss.targeted.lc$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                              "Overall", alpha = alpha.peaksize))
    out.peaksize.summary <- paste(out.peaksize.summary, "\\midrule \n", sep = "")

    ### VACCINE AVAILIBILITY
    out.finsize <- paste(out.finsize, get_descriptive_compressed(NA,                 # data.ss.baseline.lc$net.pct.rec,
                                                                 subset(data.ss.random.lc, nb.prof.vaccine.availibility == 0.05)$net.pct.rec,
                                                                 subset(data.ss.targeted.lc, nb.prof.vaccine.availibility == 0.05)$net.pct.rec,
                                                                 "Vax. availibility 5\\%"))
    out.finsize <- paste(out.finsize, get_descriptive_compressed(NA,                 # data.ss.baseline.lc$net.pct.rec,
                                                                 subset(data.ss.random.lc, nb.prof.vaccine.availibility == 0.1)$net.pct.rec,
                                                                 subset(data.ss.targeted.lc, nb.prof.vaccine.availibility == 0.1)$net.pct.rec,
                                                                 "Vax. availibility 10\\%"))
    out.finsize <- paste(out.finsize, get_descriptive_compressed(NA,                 # data.ss.baseline.lc$net.pct.rec,
                                                                 subset(data.ss.random.lc, nb.prof.vaccine.availibility == 0.2)$net.pct.rec,
                                                                 subset(data.ss.targeted.lc, nb.prof.vaccine.availibility == 0.2)$net.pct.rec,
                                                                 "Vax. availibility 20\\%"))
    out.finsize <- paste(out.finsize, get_descriptive_compressed(NA,                 # data.ss.baseline.lc$net.pct.rec,
                                                                 subset(data.ss.random.lc, nb.prof.vaccine.availibility == 0.3)$net.pct.rec,
                                                                 subset(data.ss.targeted.lc, nb.prof.vaccine.availibility == 0.3)$net.pct.rec,
                                                                 "Vax. availibility 30\\%"))
    out.finsize <- paste(out.finsize, get_descriptive_compressed(NA,                 # data.ss.baseline.lc$net.pct.rec,
                                                                 subset(data.ss.random.lc, nb.prof.vaccine.availibility == 0.4)$net.pct.rec,
                                                                 subset(data.ss.targeted.lc, nb.prof.vaccine.availibility == 0.4)$net.pct.rec,
                                                                 "Vax. availibility 40\\%"))
    out.finsize <- paste(out.finsize, get_descriptive_compressed(NA,                 # data.ss.baseline.lc$net.pct.rec,
                                                                 subset(data.ss.random.lc, nb.prof.vaccine.availibility == 0.5)$net.pct.rec,
                                                                 subset(data.ss.targeted.lc, nb.prof.vaccine.availibility == 0.5)$net.pct.rec,
                                                                 "Vax. availibility 50\\%"))
    out.finsize <- paste(out.finsize, "\\midrule \n", sep = "")

    out.finsize.summary <- paste(out.finsize.summary, get_descriptive_summary(data.ss.baseline.lc$net.pct.rec,
                                                                              subset(data.ss.random.lc, nb.prof.vaccine.availibility == 0.05)$net.pct.rec,
                                                                              subset(data.ss.targeted.lc, nb.prof.vaccine.availibility == 0.05)$net.pct.rec,
                                                                              "Vax. availibility 5\\%", show.vec1 = FALSE, alpha = alpha.finsize))
    out.finsize.summary <- paste(out.finsize.summary, get_descriptive_summary(data.ss.baseline.lc$net.pct.rec,
                                                                              subset(data.ss.random.lc, nb.prof.vaccine.availibility == 0.1)$net.pct.rec,
                                                                              subset(data.ss.targeted.lc, nb.prof.vaccine.availibility == 0.1)$net.pct.rec,
                                                                              "Vax. availibility 10\\%", show.vec1 = FALSE, alpha = alpha.finsize))
    out.finsize.summary <- paste(out.finsize.summary, get_descriptive_summary(data.ss.baseline.lc$net.pct.rec,
                                                                              subset(data.ss.random.lc, nb.prof.vaccine.availibility == 0.2)$net.pct.rec,
                                                                              subset(data.ss.targeted.lc, nb.prof.vaccine.availibility == 0.2)$net.pct.rec,
                                                                              "Vax. availibility 20\\%", show.vec1 = FALSE, alpha = alpha.finsize))
    out.finsize.summary <- paste(out.finsize.summary, get_descriptive_summary(data.ss.baseline.lc$net.pct.rec,
                                                                              subset(data.ss.random.lc, nb.prof.vaccine.availibility == 0.3)$net.pct.rec,
                                                                              subset(data.ss.targeted.lc, nb.prof.vaccine.availibility == 0.3)$net.pct.rec,
                                                                              "Vax. availibility 30\\%", show.vec1 = FALSE, alpha = alpha.finsize))
    out.finsize.summary <- paste(out.finsize.summary, get_descriptive_summary(data.ss.baseline.lc$net.pct.rec,
                                                                              subset(data.ss.random.lc, nb.prof.vaccine.availibility == 0.4)$net.pct.rec,
                                                                              subset(data.ss.targeted.lc, nb.prof.vaccine.availibility == 0.4)$net.pct.rec,
                                                                              "Vax. availibility 40\\%", show.vec1 = FALSE, alpha = alpha.finsize))
    out.finsize.summary <- paste(out.finsize.summary, get_descriptive_summary(data.ss.baseline.lc$net.pct.rec,
                                                                              subset(data.ss.random.lc, nb.prof.vaccine.availibility == 0.5)$net.pct.rec,
                                                                              subset(data.ss.targeted.lc, nb.prof.vaccine.availibility == 0.5)$net.pct.rec,
                                                                              "Vax. availibility 50\\%", show.vec1 = FALSE, alpha = alpha.finsize))
    out.finsize.summary <- paste(out.finsize.summary, "\\midrule \n", sep = "")


    out.duration <- paste(out.duration, get_descriptive_compressed(NA,                 # data.ss.baseline.lc$net.epidemic.duration,
                                                                 subset(data.ss.random.lc, nb.prof.vaccine.availibility == 0.05)$net.epidemic.duration,
                                                                 subset(data.ss.targeted.lc, nb.prof.vaccine.availibility == 0.05)$net.epidemic.duration,
                                                                 "Vax. availibility 5\\%"))
    out.duration <- paste(out.duration, get_descriptive_compressed(NA,                 # data.ss.baseline.lc$net.epidemic.duration,
                                                                 subset(data.ss.random.lc, nb.prof.vaccine.availibility == 0.1)$net.epidemic.duration,
                                                                 subset(data.ss.targeted.lc, nb.prof.vaccine.availibility == 0.1)$net.epidemic.duration,
                                                                 "Vax. availibility 10\\%"))
    out.duration <- paste(out.duration, get_descriptive_compressed(NA,                 # data.ss.baseline.lc$net.epidemic.duration,
                                                                 subset(data.ss.random.lc, nb.prof.vaccine.availibility == 0.2)$net.epidemic.duration,
                                                                 subset(data.ss.targeted.lc, nb.prof.vaccine.availibility == 0.2)$net.epidemic.duration,
                                                                 "Vax. availibility 20\\%"))
    out.duration <- paste(out.duration, get_descriptive_compressed(NA,                 # data.ss.baseline.lc$net.epidemic.duration,
                                                                 subset(data.ss.random.lc, nb.prof.vaccine.availibility == 0.3)$net.epidemic.duration,
                                                                 subset(data.ss.targeted.lc, nb.prof.vaccine.availibility == 0.3)$net.epidemic.duration,
                                                                 "Vax. availibility 30\\%"))
    out.duration <- paste(out.duration, get_descriptive_compressed(NA,                 # data.ss.baseline.lc$net.epidemic.duration,
                                                                 subset(data.ss.random.lc, nb.prof.vaccine.availibility == 0.4)$net.epidemic.duration,
                                                                 subset(data.ss.targeted.lc, nb.prof.vaccine.availibility == 0.4)$net.epidemic.duration,
                                                                 "Vax. availibility 40\\%"))
    out.duration <- paste(out.duration, get_descriptive_compressed(NA,                 # data.ss.baseline.lc$net.epidemic.duration,
                                                                 subset(data.ss.random.lc, nb.prof.vaccine.availibility == 0.5)$net.epidemic.duration,
                                                                 subset(data.ss.targeted.lc, nb.prof.vaccine.availibility == 0.5)$net.epidemic.duration,
                                                                 "Vax. availibility 50\\%"))
    out.duration <- paste(out.duration, "\\midrule \n", sep = "")

    out.duration.summary <- paste(out.duration.summary, get_descriptive_summary(data.ss.baseline.lc$net.epidemic.duration,
                                                                              subset(data.ss.random.lc, nb.prof.vaccine.availibility == 0.05)$net.epidemic.duration,
                                                                              subset(data.ss.targeted.lc, nb.prof.vaccine.availibility == 0.05)$net.epidemic.duration,
                                                                              "Vax. availibility 5\\%", show.vec1 = FALSE, alpha = alpha.duration))
    out.duration.summary <- paste(out.duration.summary, get_descriptive_summary(data.ss.baseline.lc$net.epidemic.duration,
                                                                              subset(data.ss.random.lc, nb.prof.vaccine.availibility == 0.1)$net.epidemic.duration,
                                                                              subset(data.ss.targeted.lc, nb.prof.vaccine.availibility == 0.1)$net.epidemic.duration,
                                                                              "Vax. availibility 10\\%", show.vec1 = FALSE, alpha = alpha.duration))
    out.duration.summary <- paste(out.duration.summary, get_descriptive_summary(data.ss.baseline.lc$net.epidemic.duration,
                                                                              subset(data.ss.random.lc, nb.prof.vaccine.availibility == 0.2)$net.epidemic.duration,
                                                                              subset(data.ss.targeted.lc, nb.prof.vaccine.availibility == 0.2)$net.epidemic.duration,
                                                                              "Vax. availibility 20\\%", show.vec1 = FALSE, alpha = alpha.duration))
    out.duration.summary <- paste(out.duration.summary, get_descriptive_summary(data.ss.baseline.lc$net.epidemic.duration,
                                                                              subset(data.ss.random.lc, nb.prof.vaccine.availibility == 0.3)$net.epidemic.duration,
                                                                              subset(data.ss.targeted.lc, nb.prof.vaccine.availibility == 0.3)$net.epidemic.duration,
                                                                              "Vax. availibility 30\\%", show.vec1 = FALSE, alpha = alpha.duration))
    out.duration.summary <- paste(out.duration.summary, get_descriptive_summary(data.ss.baseline.lc$net.epidemic.duration,
                                                                              subset(data.ss.random.lc, nb.prof.vaccine.availibility == 0.4)$net.epidemic.duration,
                                                                              subset(data.ss.targeted.lc, nb.prof.vaccine.availibility == 0.4)$net.epidemic.duration,
                                                                              "Vax. availibility 40\\%", show.vec1 = FALSE, alpha = alpha.duration))
    out.duration.summary <- paste(out.duration.summary, get_descriptive_summary(data.ss.baseline.lc$net.epidemic.duration,
                                                                              subset(data.ss.random.lc, nb.prof.vaccine.availibility == 0.5)$net.epidemic.duration,
                                                                              subset(data.ss.targeted.lc, nb.prof.vaccine.availibility == 0.5)$net.epidemic.duration,
                                                                              "Vax. availibility 50\\%", show.vec1 = FALSE, alpha = alpha.duration))
    out.duration.summary <- paste(out.duration.summary, "\\midrule \n", sep = "")


    out.peaksize <- paste(out.peaksize, get_descriptive_compressed(NA,                 # data.ss.baseline.lc$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                 subset(data.ss.random.lc, nb.prof.vaccine.availibility == 0.05)$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                 subset(data.ss.targeted.lc, nb.prof.vaccine.availibility == 0.05)$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                 "Vax. availibility 5\\%"))
    out.peaksize <- paste(out.peaksize, get_descriptive_compressed(NA,                 # data.ss.baseline.lc$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                 subset(data.ss.random.lc, nb.prof.vaccine.availibility == 0.1)$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                 subset(data.ss.targeted.lc, nb.prof.vaccine.availibility == 0.1)$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                 "Vax. availibility 10\\%"))
    out.peaksize <- paste(out.peaksize, get_descriptive_compressed(NA,                 # data.ss.baseline.lc$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                 subset(data.ss.random.lc, nb.prof.vaccine.availibility == 0.2)$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                 subset(data.ss.targeted.lc, nb.prof.vaccine.availibility == 0.2)$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                 "Vax. availibility 20\\%"))
    out.peaksize <- paste(out.peaksize, get_descriptive_compressed(NA,                 # data.ss.baseline.lc$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                 subset(data.ss.random.lc, nb.prof.vaccine.availibility == 0.3)$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                 subset(data.ss.targeted.lc, nb.prof.vaccine.availibility == 0.3)$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                 "Vax. availibility 30\\%"))
    out.peaksize <- paste(out.peaksize, get_descriptive_compressed(NA,                 # data.ss.baseline.lc$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                 subset(data.ss.random.lc, nb.prof.vaccine.availibility == 0.4)$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                 subset(data.ss.targeted.lc, nb.prof.vaccine.availibility == 0.4)$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                 "Vax. availibility 40\\%"))
    out.peaksize <- paste(out.peaksize, get_descriptive_compressed(NA,                 # data.ss.baseline.lc$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                 subset(data.ss.random.lc, nb.prof.vaccine.availibility == 0.5)$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                 subset(data.ss.targeted.lc, nb.prof.vaccine.availibility == 0.5)$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                 "Vax. availibility 50\\%"))
    out.peaksize <- paste(out.peaksize, "\\midrule \n", sep = "")

    out.peaksize.summary <- paste(out.peaksize.summary, get_descriptive_summary(data.ss.baseline.lc$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                              subset(data.ss.random.lc, nb.prof.vaccine.availibility == 0.05)$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                              subset(data.ss.targeted.lc, nb.prof.vaccine.availibility == 0.05)$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                              "Vax. availibility 5\\%", show.vec1 = FALSE, alpha = alpha.peaksize))
    out.peaksize.summary <- paste(out.peaksize.summary, get_descriptive_summary(data.ss.baseline.lc$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                              subset(data.ss.random.lc, nb.prof.vaccine.availibility == 0.1)$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                              subset(data.ss.targeted.lc, nb.prof.vaccine.availibility == 0.1)$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                              "Vax. availibility 10\\%", show.vec1 = FALSE, alpha = alpha.peaksize))
    out.peaksize.summary <- paste(out.peaksize.summary, get_descriptive_summary(data.ss.baseline.lc$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                              subset(data.ss.random.lc, nb.prof.vaccine.availibility == 0.2)$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                              subset(data.ss.targeted.lc, nb.prof.vaccine.availibility == 0.2)$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                              "Vax. availibility 20\\%", show.vec1 = FALSE, alpha = alpha.peaksize))
    out.peaksize.summary <- paste(out.peaksize.summary, get_descriptive_summary(data.ss.baseline.lc$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                              subset(data.ss.random.lc, nb.prof.vaccine.availibility == 0.3)$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                              subset(data.ss.targeted.lc, nb.prof.vaccine.availibility == 0.3)$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                              "Vax. availibility 30\\%", show.vec1 = FALSE, alpha = alpha.peaksize))
    out.peaksize.summary <- paste(out.peaksize.summary, get_descriptive_summary(data.ss.baseline.lc$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                              subset(data.ss.random.lc, nb.prof.vaccine.availibility == 0.4)$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                              subset(data.ss.targeted.lc, nb.prof.vaccine.availibility == 0.4)$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                              "Vax. availibility 40\\%", show.vec1 = FALSE, alpha = alpha.peaksize))
    out.peaksize.summary <- paste(out.peaksize.summary, get_descriptive_summary(data.ss.baseline.lc$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                              subset(data.ss.random.lc, nb.prof.vaccine.availibility == 0.5)$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                              subset(data.ss.targeted.lc, nb.prof.vaccine.availibility == 0.5)$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                              "Vax. availibility 50\\%", show.vec1 = FALSE, alpha = alpha.peaksize))
    out.peaksize.summary <- paste(out.peaksize.summary, "\\midrule \n", sep = "")



    ### VACCINE EFFECTIVITY
    out.finsize <- paste(out.finsize, get_descriptive_compressed(NA,                 # data.ss.baseline.lc$net.pct.rec,
                                                 subset(data.ss.random.lc, nb.prof.vaccine.efficacy == 0.6)$net.pct.rec,
                                                 subset(data.ss.targeted.lc, nb.prof.vaccine.efficacy == 0.6)$net.pct.rec,
                                                 "Vax. effectivity 60\\%"))
    out.finsize <- paste(out.finsize, get_descriptive_compressed(NA,                 # data.ss.baseline.lc$net.pct.rec,
                                                 subset(data.ss.random.lc, nb.prof.vaccine.efficacy == 0.75)$net.pct.rec,
                                                 subset(data.ss.targeted.lc, nb.prof.vaccine.efficacy == 0.75)$net.pct.rec,
                                                 "Vax. effectivity 75\\%"))
    out.finsize <- paste(out.finsize, get_descriptive_compressed(NA,                 # data.ss.baseline.lc$net.pct.rec,
                                                 subset(data.ss.random.lc, nb.prof.vaccine.efficacy == 0.9)$net.pct.rec,
                                                 subset(data.ss.targeted.lc, nb.prof.vaccine.efficacy == 0.9)$net.pct.rec,
                                                 "Vax. effectivity 90\\%"))
    out.finsize <- paste(out.finsize, "\\midrule \n", sep = "")

    out.finsize.summary <- paste(out.finsize.summary, get_descriptive_summary(data.ss.baseline.lc$net.pct.rec,
                                                              subset(data.ss.random.lc, nb.prof.vaccine.efficacy == 0.6)$net.pct.rec,
                                                              subset(data.ss.targeted.lc, nb.prof.vaccine.efficacy == 0.6)$net.pct.rec,
                                                              "Vax. effectivity 60\\%", show.vec1 = FALSE, alpha = alpha.finsize))
    out.finsize.summary <- paste(out.finsize.summary, get_descriptive_summary(data.ss.baseline.lc$net.pct.rec,
                                                              subset(data.ss.random.lc, nb.prof.vaccine.efficacy == 0.75)$net.pct.rec,
                                                              subset(data.ss.targeted.lc, nb.prof.vaccine.efficacy == 0.75)$net.pct.rec,
                                                              "Vax. effectivity 75\\%", show.vec1 = FALSE, alpha = alpha.finsize))
    out.finsize.summary <- paste(out.finsize.summary, get_descriptive_summary(data.ss.baseline.lc$net.pct.rec,
                                                              subset(data.ss.random.lc, nb.prof.vaccine.efficacy == 0.9)$net.pct.rec,
                                                              subset(data.ss.targeted.lc, nb.prof.vaccine.efficacy == 0.9)$net.pct.rec,
                                                              "Vax. effectivity 90\\%", show.vec1 = FALSE, alpha = alpha.finsize))
    out.finsize.summary <- paste(out.finsize.summary, "\\midrule \n", sep = "")

    out.duration <- paste(out.duration, get_descriptive_compressed(NA,                 # data.ss.baseline.lc$net.epidemic.duration,
                                                                 subset(data.ss.random.lc, nb.prof.vaccine.efficacy == 0.6)$net.epidemic.duration,
                                                                 subset(data.ss.targeted.lc, nb.prof.vaccine.efficacy == 0.6)$net.epidemic.duration,
                                                                 "Vax. effectivity 60\\%"))
    out.duration <- paste(out.duration, get_descriptive_compressed(NA,                 # data.ss.baseline.lc$net.epidemic.duration,
                                                                 subset(data.ss.random.lc, nb.prof.vaccine.efficacy == 0.75)$net.epidemic.duration,
                                                                 subset(data.ss.targeted.lc, nb.prof.vaccine.efficacy == 0.75)$net.epidemic.duration,
                                                                 "Vax. effectivity 75\\%"))
    out.duration <- paste(out.duration, get_descriptive_compressed(NA,                 # data.ss.baseline.lc$net.epidemic.duration,
                                                                 subset(data.ss.random.lc, nb.prof.vaccine.efficacy == 0.9)$net.epidemic.duration,
                                                                 subset(data.ss.targeted.lc, nb.prof.vaccine.efficacy == 0.9)$net.epidemic.duration,
                                                                 "Vax. effectivity 90\\%"))
    out.duration <- paste(out.duration, "\\midrule \n", sep = "")

    out.duration.summary <- paste(out.duration.summary, get_descriptive_summary(data.ss.baseline.lc$net.epidemic.duration,
                                                                              subset(data.ss.random.lc, nb.prof.vaccine.efficacy == 0.6)$net.epidemic.duration,
                                                                              subset(data.ss.targeted.lc, nb.prof.vaccine.efficacy == 0.6)$net.epidemic.duration,
                                                                              "Vax. effectivity 60\\%", show.vec1 = FALSE, alpha = alpha.duration))
    out.duration.summary <- paste(out.duration.summary, get_descriptive_summary(data.ss.baseline.lc$net.epidemic.duration,
                                                                              subset(data.ss.random.lc, nb.prof.vaccine.efficacy == 0.75)$net.epidemic.duration,
                                                                              subset(data.ss.targeted.lc, nb.prof.vaccine.efficacy == 0.75)$net.epidemic.duration,
                                                                              "Vax. effectivity 75\\%", show.vec1 = FALSE, alpha = alpha.duration))
    out.duration.summary <- paste(out.duration.summary, get_descriptive_summary(data.ss.baseline.lc$net.epidemic.duration,
                                                                              subset(data.ss.random.lc, nb.prof.vaccine.efficacy == 0.9)$net.epidemic.duration,
                                                                              subset(data.ss.targeted.lc, nb.prof.vaccine.efficacy == 0.9)$net.epidemic.duration,
                                                                              "Vax. effectivity 90\\%", show.vec1 = FALSE, alpha = alpha.duration))
    out.duration.summary <- paste(out.duration.summary, "\\midrule \n", sep = "")

    out.peaksize <- paste(out.peaksize, get_descriptive_compressed(NA,                 # data.ss.baseline.lc$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                 subset(data.ss.random.lc, nb.prof.vaccine.efficacy == 0.6)$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                 subset(data.ss.targeted.lc, nb.prof.vaccine.efficacy == 0.6)$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                 "Vax. effectivity 60\\%"))
    out.peaksize <- paste(out.peaksize, get_descriptive_compressed(NA,                 # data.ss.baseline.lc$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                 subset(data.ss.random.lc, nb.prof.vaccine.efficacy == 0.75)$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                 subset(data.ss.targeted.lc, nb.prof.vaccine.efficacy == 0.75)$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                 "Vax. effectivity 75\\%"))
    out.peaksize <- paste(out.peaksize, get_descriptive_compressed(NA,                 # data.ss.baseline.lc$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                 subset(data.ss.random.lc, nb.prof.vaccine.efficacy == 0.9)$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                 subset(data.ss.targeted.lc, nb.prof.vaccine.efficacy == 0.9)$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                 "Vax. effectivity 90\\%"))
    out.peaksize <- paste(out.peaksize, "\\midrule \n", sep = "")

    out.peaksize.summary <- paste(out.peaksize.summary, get_descriptive_summary(data.ss.baseline.lc$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                              subset(data.ss.random.lc, nb.prof.vaccine.efficacy == 0.6)$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                              subset(data.ss.targeted.lc, nb.prof.vaccine.efficacy == 0.6)$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                              "Vax. effectivity 60\\%", show.vec1 = FALSE, alpha = alpha.peaksize))
    out.peaksize.summary <- paste(out.peaksize.summary, get_descriptive_summary(data.ss.baseline.lc$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                              subset(data.ss.random.lc, nb.prof.vaccine.efficacy == 0.75)$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                              subset(data.ss.targeted.lc, nb.prof.vaccine.efficacy == 0.75)$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                              "Vax. effectivity 75\\%", show.vec1 = FALSE, alpha = alpha.peaksize))
    out.peaksize.summary <- paste(out.peaksize.summary, get_descriptive_summary(data.ss.baseline.lc$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                              subset(data.ss.random.lc, nb.prof.vaccine.efficacy == 0.9)$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                              subset(data.ss.targeted.lc, nb.prof.vaccine.efficacy == 0.9)$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                              "Vax. effectivity 90\\%", show.vec1 = FALSE, alpha = alpha.peaksize))
    out.peaksize.summary <- paste(out.peaksize.summary, "\\midrule \n", sep = "")

    ### AV. DEGREE
    degree.min <- min(min(data.ss.baseline.lc$net.degree.av),
                      min(data.ss.random.lc$net.degree.av),
                      min(data.ss.targeted.lc$net.degree.av))
    degree.max <- max(max(data.ss.baseline.lc$net.degree.av),
                      max(data.ss.random.lc$net.degree.av),
                      max(data.ss.targeted.lc$net.degree.av))
    degree.step.size <- (degree.max - degree.min) / 3

    degree.lo.max <- degree.min + degree.step.size
    degree.me.max <- degree.min + 2*degree.step.size

    data.ss.baseline.lc.degree.lo <- subset(data.ss.baseline.lc, net.degree.av <= degree.lo.max)
    data.ss.random.lc.degree.lo <- subset(data.ss.random.lc, net.degree.av <= degree.lo.max)
    data.ss.targeted.lc.degree.lo <- subset(data.ss.targeted.lc, net.degree.av <= degree.lo.max)

    data.ss.baseline.lc.degree.me <- subset(data.ss.baseline.lc, net.degree.av > degree.lo.max & net.degree.av <= degree.me.max)
    data.ss.random.lc.degree.me <- subset(data.ss.random.lc, net.degree.av > degree.lo.max & net.degree.av <= degree.me.max)
    data.ss.targeted.lc.degree.me <- subset(data.ss.targeted.lc, net.degree.av > degree.lo.max & net.degree.av <= degree.me.max)

    data.ss.baseline.lc.degree.hi <- subset(data.ss.baseline.lc, net.degree.av > degree.me.max)
    data.ss.random.lc.degree.hi <- subset(data.ss.random.lc, net.degree.av > degree.me.max)
    data.ss.targeted.lc.degree.hi <- subset(data.ss.targeted.lc, net.degree.av > degree.me.max)

    out.finsize <- paste(out.finsize, get_descriptive_compressed(data.ss.baseline.lc.degree.lo$net.pct.rec,
                                                 data.ss.random.lc.degree.lo$net.pct.rec,
                                                 data.ss.targeted.lc.degree.lo$net.pct.rec,
                                                 paste("Low av. degree (", round(degree.min, 2), "-", round(degree.lo.max, 2), ")",  sep = "")))
    out.finsize <- paste(out.finsize, get_descriptive_compressed(data.ss.baseline.lc.degree.me$net.pct.rec,
                                                 data.ss.random.lc.degree.me$net.pct.rec,
                                                 data.ss.targeted.lc.degree.me$net.pct.rec,
                                                 paste("Medium av. degree (", round(degree.lo.max, 2), "-", round(degree.me.max, 2), ")",  sep = "")))
    out.finsize <- paste(out.finsize, get_descriptive_compressed(data.ss.baseline.lc.degree.hi$net.pct.rec,
                                                 data.ss.random.lc.degree.hi$net.pct.rec,
                                                 data.ss.targeted.lc.degree.hi$net.pct.rec,
                                                 paste("High av. degree (", round(degree.me.max, 2), "-", round(degree.max, 2), ")",  sep = "")))
    out.finsize <- paste(out.finsize, "\\midrule \n", sep = "")

    # out.finsize.summary <- paste(out.finsize.summary, get_descriptive_summary(data.ss.baseline.lc.degree.lo$net.pct.rec,
    #                                                           data.ss.random.lc.degree.lo$net.pct.rec,
    #                                                           data.ss.targeted.lc.degree.lo$net.pct.rec,
    #                                                           paste("Low av. degree (", round(degree.min, 2), "-", round(degree.lo.max, 2), ")",  sep = "")))
    # out.finsize.summary <- paste(out.finsize.summary, get_descriptive_summary(data.ss.baseline.lc.degree.me$net.pct.rec,
    #                                                           data.ss.random.lc.degree.me$net.pct.rec,
    #                                                           data.ss.targeted.lc.degree.me$net.pct.rec,
    #                                                           paste("Medium av. degree (", round(degree.lo.max, 2), "-", round(degree.me.max, 2), ")",  sep = "")))
    # out.finsize.summary <- paste(out.finsize.summary, get_descriptive_summary(data.ss.baseline.lc.degree.hi$net.pct.rec,
    #                                                           data.ss.random.lc.degree.hi$net.pct.rec,
    #                                                           data.ss.targeted.lc.degree.hi$net.pct.rec,
    #                                                           paste("High av. degree (", round(degree.me.max, 2), "-", round(degree.max, 2), ")",  sep = "")))
    # out.finsize.summary <- paste(out.finsize.summary, "\\midrule \n", sep = "")


    out.duration <- paste(out.duration, get_descriptive_compressed(data.ss.baseline.lc.degree.lo$net.epidemic.duration,
                                                                 data.ss.random.lc.degree.lo$net.epidemic.duration,
                                                                 data.ss.targeted.lc.degree.lo$net.epidemic.duration,
                                                                 paste("Low av. degree (", round(degree.min, 2), "-", round(degree.lo.max, 2), ")",  sep = "")))
    out.duration <- paste(out.duration, get_descriptive_compressed(data.ss.baseline.lc.degree.me$net.epidemic.duration,
                                                                 data.ss.random.lc.degree.me$net.epidemic.duration,
                                                                 data.ss.targeted.lc.degree.me$net.epidemic.duration,
                                                                 paste("Medium av. degree (", round(degree.lo.max, 2), "-", round(degree.me.max, 2), ")",  sep = "")))
    out.duration <- paste(out.duration, get_descriptive_compressed(data.ss.baseline.lc.degree.hi$net.epidemic.duration,
                                                                 data.ss.random.lc.degree.hi$net.epidemic.duration,
                                                                 data.ss.targeted.lc.degree.hi$net.epidemic.duration,
                                                                 paste("High av. degree (", round(degree.me.max, 2), "-", round(degree.max, 2), ")",  sep = "")))
    out.duration <- paste(out.duration, "\\midrule \n", sep = "")

    # out.duration.summary <- paste(out.duration.summary, get_descriptive_summary(data.ss.baseline.lc.degree.lo$net.epidemic.duration,
    #                                                                           data.ss.random.lc.degree.lo$net.epidemic.duration,
    #                                                                           data.ss.targeted.lc.degree.lo$net.epidemic.duration,
    #                                                                           paste("Low av. degree (", round(degree.min, 2), "-", round(degree.lo.max, 2), ")",  sep = "")))
    # out.duration.summary <- paste(out.duration.summary, get_descriptive_summary(data.ss.baseline.lc.degree.me$net.epidemic.duration,
    #                                                                           data.ss.random.lc.degree.me$net.epidemic.duration,
    #                                                                           data.ss.targeted.lc.degree.me$net.epidemic.duration,
    #                                                                           paste("Medium av. degree (", round(degree.lo.max, 2), "-", round(degree.me.max, 2), ")",  sep = "")))
    # out.duration.summary <- paste(out.duration.summary, get_descriptive_summary(data.ss.baseline.lc.degree.hi$net.epidemic.duration,
    #                                                                           data.ss.random.lc.degree.hi$net.epidemic.duration,
    #                                                                           data.ss.targeted.lc.degree.hi$net.epidemic.duration,
    #                                                                           paste("High av. degree (", round(degree.me.max, 2), "-", round(degree.max, 2), ")",  sep = "")))
    # out.duration.summary <- paste(out.duration.summary, "\\midrule \n", sep = "")


    out.peaksize <- paste(out.peaksize, get_descriptive_compressed(data.ss.baseline.lc.degree.lo$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                 data.ss.random.lc.degree.lo$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                 data.ss.targeted.lc.degree.lo$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                 paste("Low av. degree (", round(degree.min, 2), "-", round(degree.lo.max, 2), ")",  sep = "")))
    out.peaksize <- paste(out.peaksize, get_descriptive_compressed(data.ss.baseline.lc.degree.me$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                 data.ss.random.lc.degree.me$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                 data.ss.targeted.lc.degree.me$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                 paste("Medium av. degree (", round(degree.lo.max, 2), "-", round(degree.me.max, 2), ")",  sep = "")))
    out.peaksize <- paste(out.peaksize, get_descriptive_compressed(data.ss.baseline.lc.degree.hi$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                 data.ss.random.lc.degree.hi$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                 data.ss.targeted.lc.degree.hi$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                 paste("High av. degree (", round(degree.me.max, 2), "-", round(degree.max, 2), ")",  sep = "")))
    out.peaksize <- paste(out.peaksize, "\\midrule \n", sep = "")

    # out.peaksize.summary <- paste(out.peaksize.summary, get_descriptive_summary(data.ss.baseline.lc.degree.lo$net.epidemic.peak.size/data.ss$nb.N*100,
    #                                                                           data.ss.random.lc.degree.lo$net.epidemic.peak.size/data.ss$nb.N*100,
    #                                                                           data.ss.targeted.lc.degree.lo$net.epidemic.peak.size/data.ss$nb.N*100,
    #                                                                           paste("Low av. degree (", round(degree.min, 2), "-", round(degree.lo.max, 2), ")",  sep = ""), alpha = 0.01))
    # out.peaksize.summary <- paste(out.peaksize.summary, get_descriptive_summary(data.ss.baseline.lc.degree.me$net.epidemic.peak.size/data.ss$nb.N*100,
    #                                                                           data.ss.random.lc.degree.me$net.epidemic.peak.size/data.ss$nb.N*100,
    #                                                                           data.ss.targeted.lc.degree.me$net.epidemic.peak.size/data.ss$nb.N*100,
    #                                                                           paste("Medium av. degree (", round(degree.lo.max, 2), "-", round(degree.me.max, 2), ")",  sep = ""), alpha = 0.01))
    # out.peaksize.summary <- paste(out.peaksize.summary, get_descriptive_summary(data.ss.baseline.lc.degree.hi$net.epidemic.peak.size/data.ss$nb.N*100,
    #                                                                           data.ss.random.lc.degree.hi$net.epidemic.peak.size/data.ss$nb.N*100,
    #                                                                           data.ss.targeted.lc.degree.hi$net.epidemic.peak.size/data.ss$nb.N*100,
    #                                                                           paste("High av. degree (", round(degree.me.max, 2), "-", round(degree.max, 2), ")",  sep = ""), alpha = 0.01))
    # out.peaksize.summary <- paste(out.peaksize.summary, "\\midrule \n", sep = "")

    ### CLUSTERING
    cluster.min <- min(min(data.ss.baseline.lc$net.clustering.av),
                       min(data.ss.random.lc$net.clustering.av),
                       min(data.ss.targeted.lc$net.clustering.av))
    cluster.max <- max(max(data.ss.baseline.lc$net.clustering.av),
                       max(data.ss.random.lc$net.clustering.av),
                       max(data.ss.targeted.lc$net.clustering.av))
    cluster.step.size <- (cluster.max - cluster.min) / 3

    cluster.lo.max <- cluster.min + cluster.step.size
    cluster.me.max <- cluster.min + 2*cluster.step.size

    data.ss.baseline.lc.cluster.lo <- subset(data.ss.baseline.lc, net.clustering.av <= cluster.lo.max)
    data.ss.random.lc.cluster.lo <- subset(data.ss.random.lc, net.clustering.av <= cluster.lo.max)
    data.ss.targeted.lc.cluster.lo <- subset(data.ss.targeted.lc, net.clustering.av <= cluster.lo.max)

    data.ss.baseline.lc.cluster.me <- subset(data.ss.baseline.lc, net.clustering.av > cluster.lo.max & net.clustering.av <= cluster.me.max)
    data.ss.random.lc.cluster.me <- subset(data.ss.random.lc, net.clustering.av > cluster.lo.max & net.clustering.av <= cluster.me.max)
    data.ss.targeted.lc.cluster.me <- subset(data.ss.targeted.lc, net.clustering.av > cluster.lo.max & net.clustering.av <= cluster.me.max)

    data.ss.baseline.lc.cluster.hi <- subset(data.ss.baseline.lc, net.clustering.av > cluster.me.max)
    data.ss.random.lc.cluster.hi <- subset(data.ss.random.lc, net.clustering.av > cluster.me.max)
    data.ss.targeted.lc.cluster.hi <- subset(data.ss.targeted.lc, net.clustering.av > cluster.me.max)

    out.finsize <- paste(out.finsize, get_descriptive_compressed(data.ss.baseline.lc.cluster.lo$net.pct.rec,
                                                                 data.ss.random.lc.cluster.lo$net.pct.rec,
                                                                 data.ss.targeted.lc.cluster.lo$net.pct.rec,
                                                                 paste("Low clustering (", round(cluster.min, 2), "-", round(cluster.lo.max, 2), ")",  sep = "")))
    out.finsize <- paste(out.finsize, get_descriptive_compressed(data.ss.baseline.lc.cluster.me$net.pct.rec,
                                                                 data.ss.random.lc.cluster.me$net.pct.rec,
                                                                 data.ss.targeted.lc.cluster.me$net.pct.rec,
                                                                 paste("Medium clustering (", round(cluster.lo.max, 2), "-", round(cluster.me.max, 2), ")",  sep = "")))
    out.finsize <- paste(out.finsize, get_descriptive_compressed(data.ss.baseline.lc.cluster.hi$net.pct.rec,
                                                                 data.ss.random.lc.cluster.hi$net.pct.rec,
                                                                 data.ss.targeted.lc.cluster.hi$net.pct.rec,
                                                                 paste("High clustering (", round(cluster.me.max, 2), "-", round(cluster.max, 2), ")",  sep = "")))
    out.finsize <- paste(out.finsize, "\\midrule \n", sep = "")

    out.finsize.summary <- paste(out.finsize.summary, get_descriptive_summary(data.ss.baseline.lc.cluster.lo$net.pct.rec,
                                                                              data.ss.random.lc.cluster.lo$net.pct.rec,
                                                                              data.ss.targeted.lc.cluster.lo$net.pct.rec,
                                                                              paste("Low clustering",  sep = ""), alpha = alpha.finsize))
    out.finsize.summary <- paste(out.finsize.summary, get_descriptive_summary(data.ss.baseline.lc.cluster.me$net.pct.rec,
                                                                              data.ss.random.lc.cluster.me$net.pct.rec,
                                                                              data.ss.targeted.lc.cluster.me$net.pct.rec,
                                                                              paste("Medium clustering",  sep = ""), alpha = alpha.finsize))
    out.finsize.summary <- paste(out.finsize.summary, get_descriptive_summary(data.ss.baseline.lc.cluster.hi$net.pct.rec,
                                                                              data.ss.random.lc.cluster.hi$net.pct.rec,
                                                                              data.ss.targeted.lc.cluster.hi$net.pct.rec,
                                                                              paste("High clustering",  sep = ""), alpha = alpha.finsize))
    out.finsize.summary <- paste(out.finsize.summary, "\\midrule \n", sep = "")


    out.duration <- paste(out.duration, get_descriptive_compressed(data.ss.baseline.lc.cluster.lo$net.epidemic.duration,
                                                                 data.ss.random.lc.cluster.lo$net.epidemic.duration,
                                                                 data.ss.targeted.lc.cluster.lo$net.epidemic.duration,
                                                                 paste("Low clustering (", round(cluster.min, 2), "-", round(cluster.lo.max, 2), ")",  sep = "")))
    out.duration <- paste(out.duration, get_descriptive_compressed(data.ss.baseline.lc.cluster.me$net.epidemic.duration,
                                                                 data.ss.random.lc.cluster.me$net.epidemic.duration,
                                                                 data.ss.targeted.lc.cluster.me$net.epidemic.duration,
                                                                 paste("Medium clustering (", round(cluster.lo.max, 2), "-", round(cluster.me.max, 2), ")",  sep = "")))
    out.duration <- paste(out.duration, get_descriptive_compressed(data.ss.baseline.lc.cluster.hi$net.epidemic.duration,
                                                                 data.ss.random.lc.cluster.hi$net.epidemic.duration,
                                                                 data.ss.targeted.lc.cluster.hi$net.epidemic.duration,
                                                                 paste("High clustering (", round(cluster.me.max, 2), "-", round(cluster.max, 2), ")",  sep = "")))
    out.duration <- paste(out.duration, "\\midrule \n", sep = "")

    out.duration.summary <- paste(out.duration.summary, get_descriptive_summary(data.ss.baseline.lc.cluster.lo$net.epidemic.duration,
                                                                              data.ss.random.lc.cluster.lo$net.epidemic.duration,
                                                                              data.ss.targeted.lc.cluster.lo$net.epidemic.duration,
                                                                              paste("Low clustering",  sep = ""), alpha = alpha.duration))
    out.duration.summary <- paste(out.duration.summary, get_descriptive_summary(data.ss.baseline.lc.cluster.me$net.epidemic.duration,
                                                                              data.ss.random.lc.cluster.me$net.epidemic.duration,
                                                                              data.ss.targeted.lc.cluster.me$net.epidemic.duration,
                                                                              paste("Medium clustering",  sep = ""), alpha = alpha.duration))
    out.duration.summary <- paste(out.duration.summary, get_descriptive_summary(data.ss.baseline.lc.cluster.hi$net.epidemic.duration,
                                                                              data.ss.random.lc.cluster.hi$net.epidemic.duration,
                                                                              data.ss.targeted.lc.cluster.hi$net.epidemic.duration,
                                                                              paste("High clustering",  sep = ""), alpha = alpha.duration))
    out.duration.summary <- paste(out.duration.summary, "\\midrule \n", sep = "")


    out.peaksize <- paste(out.peaksize, get_descriptive_compressed(data.ss.baseline.lc.cluster.lo$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                 data.ss.random.lc.cluster.lo$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                 data.ss.targeted.lc.cluster.lo$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                 paste("Low clustering (", round(cluster.min, 2), "-", round(cluster.lo.max, 2), ")",  sep = "")))
    out.peaksize <- paste(out.peaksize, get_descriptive_compressed(data.ss.baseline.lc.cluster.me$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                 data.ss.random.lc.cluster.me$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                 data.ss.targeted.lc.cluster.me$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                 paste("Medium clustering (", round(cluster.lo.max, 2), "-", round(cluster.me.max, 2), ")",  sep = "")))
    out.peaksize <- paste(out.peaksize, get_descriptive_compressed(data.ss.baseline.lc.cluster.hi$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                 data.ss.random.lc.cluster.hi$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                 data.ss.targeted.lc.cluster.hi$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                 paste("High clustering (", round(cluster.me.max, 2), "-", round(cluster.max, 2), ")",  sep = "")))
    out.peaksize <- paste(out.peaksize, "\\midrule \n", sep = "")

    out.peaksize.summary <- paste(out.peaksize.summary, get_descriptive_summary(data.ss.baseline.lc.cluster.lo$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                              data.ss.random.lc.cluster.lo$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                              data.ss.targeted.lc.cluster.lo$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                              paste("Low clustering",  sep = ""), alpha = alpha.peaksize))
    out.peaksize.summary <- paste(out.peaksize.summary, get_descriptive_summary(data.ss.baseline.lc.cluster.me$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                              data.ss.random.lc.cluster.me$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                              data.ss.targeted.lc.cluster.me$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                              paste("Medium clustering",  sep = ""), alpha = alpha.peaksize))
    out.peaksize.summary <- paste(out.peaksize.summary, get_descriptive_summary(data.ss.baseline.lc.cluster.hi$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                              data.ss.random.lc.cluster.hi$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                              data.ss.targeted.lc.cluster.hi$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                              paste("High clustering",  sep = ""), alpha = alpha.peaksize))
    out.peaksize.summary <- paste(out.peaksize.summary, "\\midrule \n", sep = "")

    ### PATH LENGTH
    plength.min <- min(min(data.ss.baseline.lc$net.pathlength.av),
                       min(data.ss.random.lc$net.pathlength.av),
                       min(data.ss.targeted.lc$net.pathlength.av))
    plength.max <- max(max(data.ss.baseline.lc$net.pathlength.av),
                       max(data.ss.random.lc$net.pathlength.av),
                       max(data.ss.targeted.lc$net.pathlength.av))
    plength.step.size <- (plength.max - plength.min) / 3

    plength.lo.max <- plength.min + plength.step.size
    plength.me.max <- plength.min + 2*plength.step.size

    data.ss.baseline.lc.plength.lo <- subset(data.ss.baseline.lc, net.pathlength.av <= plength.lo.max)
    data.ss.random.lc.plength.lo <- subset(data.ss.random.lc, net.pathlength.av <= plength.lo.max)
    data.ss.targeted.lc.plength.lo <- subset(data.ss.targeted.lc, net.pathlength.av <= plength.lo.max)

    data.ss.baseline.lc.plength.me <- subset(data.ss.baseline.lc, net.pathlength.av > plength.lo.max & net.pathlength.av <= plength.me.max)
    data.ss.random.lc.plength.me <- subset(data.ss.random.lc, net.pathlength.av > plength.lo.max & net.pathlength.av <= plength.me.max)
    data.ss.targeted.lc.plength.me <- subset(data.ss.targeted.lc, net.pathlength.av > plength.lo.max & net.pathlength.av <= plength.me.max)

    data.ss.baseline.lc.plength.hi <- subset(data.ss.baseline.lc, net.pathlength.av > plength.me.max)
    data.ss.random.lc.plength.hi <- subset(data.ss.random.lc, net.pathlength.av > plength.me.max)
    data.ss.targeted.lc.plength.hi <- subset(data.ss.targeted.lc, net.pathlength.av > plength.me.max)

    out.finsize <- paste(out.finsize, get_descriptive_compressed(data.ss.baseline.lc.plength.lo$net.pct.rec,
                                                                 data.ss.random.lc.plength.lo$net.pct.rec,
                                                                 data.ss.targeted.lc.plength.lo$net.pct.rec,
                                                                 paste("Low av. path length (", round(plength.min, 2), "-", round(plength.lo.max, 2), ")",  sep = "")))
    out.finsize <- paste(out.finsize, get_descriptive_compressed(data.ss.baseline.lc.plength.me$net.pct.rec,
                                                                 data.ss.random.lc.plength.me$net.pct.rec,
                                                                 data.ss.targeted.lc.plength.me$net.pct.rec,
                                                                 paste("Medium av. path length (", round(plength.lo.max, 2), "-", round(plength.me.max, 2), ")",  sep = "")))
    out.finsize <- paste(out.finsize, get_descriptive_compressed(data.ss.baseline.lc.plength.hi$net.pct.rec,
                                                                 data.ss.random.lc.plength.hi$net.pct.rec,
                                                                 data.ss.targeted.lc.plength.hi$net.pct.rec,
                                                                 paste("High av. path length (", round(plength.me.max, 2), "-", round(plength.max, 2), ")",  sep = "")))
    out.finsize <- paste(out.finsize, "\\midrule \n", sep = "")

    # out.finsize.summary <- paste(out.finsize.summary, get_descriptive_summary(data.ss.baseline.lc.plength.lo$net.pct.rec,
    #                                                                           data.ss.random.lc.plength.lo$net.pct.rec,
    #                                                                           data.ss.targeted.lc.plength.lo$net.pct.rec,
    #                                                                           paste("Low av. path length (", round(plength.min, 2), "-", round(plength.lo.max, 2), ")",  sep = "")))
    # out.finsize.summary <- paste(out.finsize.summary, get_descriptive_summary(data.ss.baseline.lc.plength.me$net.pct.rec,
    #                                                                           data.ss.random.lc.plength.me$net.pct.rec,
    #                                                                           data.ss.targeted.lc.plength.me$net.pct.rec,
    #                                                                           paste("Medium av. path length (", round(plength.lo.max, 2), "-", round(plength.me.max, 2), ")",  sep = "")))
    # out.finsize.summary <- paste(out.finsize.summary, get_descriptive_summary(data.ss.baseline.lc.plength.hi$net.pct.rec,
    #                                                                           data.ss.random.lc.plength.hi$net.pct.rec,
    #                                                                           data.ss.targeted.lc.plength.hi$net.pct.rec,
    #                                                                           paste("High av. path length (", round(plength.me.max, 2), "-", round(plength.max, 2), ")",  sep = "")))
    # out.finsize.summary <- paste(out.finsize.summary, "\\midrule \n", sep = "")

    out.duration <- paste(out.duration, get_descriptive_compressed(data.ss.baseline.lc.plength.lo$net.epidemic.duration,
                                                                 data.ss.random.lc.plength.lo$net.epidemic.duration,
                                                                 data.ss.targeted.lc.plength.lo$net.epidemic.duration,
                                                                 paste("Low av. path length (", round(plength.min, 2), "-", round(plength.lo.max, 2), ")",  sep = "")))
    out.duration <- paste(out.duration, get_descriptive_compressed(data.ss.baseline.lc.plength.me$net.epidemic.duration,
                                                                 data.ss.random.lc.plength.me$net.epidemic.duration,
                                                                 data.ss.targeted.lc.plength.me$net.epidemic.duration,
                                                                 paste("Medium av. path length (", round(plength.lo.max, 2), "-", round(plength.me.max, 2), ")",  sep = "")))
    out.duration <- paste(out.duration, get_descriptive_compressed(data.ss.baseline.lc.plength.hi$net.epidemic.duration,
                                                                 data.ss.random.lc.plength.hi$net.epidemic.duration,
                                                                 data.ss.targeted.lc.plength.hi$net.epidemic.duration,
                                                                 paste("High av. path length (", round(plength.me.max, 2), "-", round(plength.max, 2), ")",  sep = "")))
    out.duration <- paste(out.duration, "\\midrule \n", sep = "")

    # out.duration.summary <- paste(out.duration.summary, get_descriptive_summary(data.ss.baseline.lc.plength.lo$net.epidemic.duration,
    #                                                                           data.ss.random.lc.plength.lo$net.epidemic.duration,
    #                                                                           data.ss.targeted.lc.plength.lo$net.epidemic.duration,
    #                                                                           paste("Low av. path length (", round(plength.min, 2), "-", round(plength.lo.max, 2), ")",  sep = "")))
    # out.duration.summary <- paste(out.duration.summary, get_descriptive_summary(data.ss.baseline.lc.plength.me$net.epidemic.duration,
    #                                                                           data.ss.random.lc.plength.me$net.epidemic.duration,
    #                                                                           data.ss.targeted.lc.plength.me$net.epidemic.duration,
    #                                                                           paste("Medium av. path length (", round(plength.lo.max, 2), "-", round(plength.me.max, 2), ")",  sep = "")))
    # out.duration.summary <- paste(out.duration.summary, get_descriptive_summary(data.ss.baseline.lc.plength.hi$net.epidemic.duration,
    #                                                                           data.ss.random.lc.plength.hi$net.epidemic.duration,
    #                                                                           data.ss.targeted.lc.plength.hi$net.epidemic.duration,
    #                                                                           paste("High av. path length (", round(plength.me.max, 2), "-", round(plength.max, 2), ")",  sep = "")))
    # out.duration.summary <- paste(out.duration.summary, "\\midrule \n", sep = "")

    out.peaksize <- paste(out.peaksize, get_descriptive_compressed(data.ss.baseline.lc.plength.lo$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                 data.ss.random.lc.plength.lo$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                 data.ss.targeted.lc.plength.lo$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                 paste("Low av. path length (", round(plength.min, 2), "-", round(plength.lo.max, 2), ")",  sep = "")))
    out.peaksize <- paste(out.peaksize, get_descriptive_compressed(data.ss.baseline.lc.plength.me$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                 data.ss.random.lc.plength.me$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                 data.ss.targeted.lc.plength.me$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                 paste("Medium av. path length (", round(plength.lo.max, 2), "-", round(plength.me.max, 2), ")",  sep = "")))
    out.peaksize <- paste(out.peaksize, get_descriptive_compressed(data.ss.baseline.lc.plength.hi$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                 data.ss.random.lc.plength.hi$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                 data.ss.targeted.lc.plength.hi$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                 paste("High av. path length (", round(plength.me.max, 2), "-", round(plength.max, 2), ")",  sep = "")))
    out.peaksize <- paste(out.peaksize, "\\midrule \n", sep = "")

    # out.peaksize.summary <- paste(out.peaksize.summary, get_descriptive_summary(data.ss.baseline.lc.plength.lo$net.epidemic.peak.size/data.ss$nb.N*100,
    #                                                                           data.ss.random.lc.plength.lo$net.epidemic.peak.size/data.ss$nb.N*100,
    #                                                                           data.ss.targeted.lc.plength.lo$net.epidemic.peak.size/data.ss$nb.N*100,
    #                                                                           paste("Low av. path length (", round(plength.min, 2), "-", round(plength.lo.max, 2), ")",  sep = ""), alpha = 0.01))
    # out.peaksize.summary <- paste(out.peaksize.summary, get_descriptive_summary(data.ss.baseline.lc.plength.me$net.epidemic.peak.size/data.ss$nb.N*100,
    #                                                                           data.ss.random.lc.plength.me$net.epidemic.peak.size/data.ss$nb.N*100,
    #                                                                           data.ss.targeted.lc.plength.me$net.epidemic.peak.size/data.ss$nb.N*100,
    #                                                                           paste("Medium av. path length (", round(plength.lo.max, 2), "-", round(plength.me.max, 2), ")",  sep = ""), alpha = 0.01))
    # out.peaksize.summary <- paste(out.peaksize.summary, get_descriptive_summary(data.ss.baseline.lc.plength.hi$net.epidemic.peak.size/data.ss$nb.N*100,
    #                                                                           data.ss.random.lc.plength.hi$net.epidemic.peak.size/data.ss$nb.N*100,
    #                                                                           data.ss.targeted.lc.plength.hi$net.epidemic.peak.size/data.ss$nb.N*100,
    #                                                                           paste("High av. path length (", round(plength.me.max, 2), "-", round(plength.max, 2), ")",  sep = ""), alpha = 0.01))
    # out.peaksize.summary <- paste(out.peaksize.summary, "\\midrule \n", sep = "")

    ### HOMOPHILY
    homo.min <- min(min(data.ss.baseline.lc$net.assortativity.profession),
                          min(data.ss.random.lc$net.assortativity.profession),
                          min(data.ss.targeted.lc$net.assortativity.profession))
    homo.max <- max(max(data.ss.baseline.lc$net.assortativity.profession),
                          max(data.ss.random.lc$net.assortativity.profession),
                          max(data.ss.targeted.lc$net.assortativity.profession))
    homo.step.size <- (homo.max - homo.min) / 3

    homo.lo.max <- homo.min + homo.step.size
    homo.me.max <- homo.min + 2*homo.step.size

    data.ss.baseline.lc.homo.lo <- subset(data.ss.baseline.lc, net.assortativity.profession <= homo.lo.max)
    data.ss.random.lc.homo.lo <- subset(data.ss.random.lc, net.assortativity.profession <= homo.lo.max)
    data.ss.targeted.lc.homo.lo <- subset(data.ss.targeted.lc, net.assortativity.profession <= homo.lo.max)

    data.ss.baseline.lc.homo.me <- subset(data.ss.baseline.lc, net.assortativity.profession > homo.lo.max & net.assortativity.profession <= homo.me.max)
    data.ss.random.lc.homo.me <- subset(data.ss.random.lc, net.assortativity.profession > homo.lo.max & net.assortativity.profession <= homo.me.max)
    data.ss.targeted.lc.homo.me <- subset(data.ss.targeted.lc, net.assortativity.profession > homo.lo.max & net.assortativity.profession <= homo.me.max)

    data.ss.baseline.lc.homo.hi <- subset(data.ss.baseline.lc, net.assortativity.profession > homo.me.max)
    data.ss.random.lc.homo.hi <- subset(data.ss.random.lc, net.assortativity.profession > homo.me.max)
    data.ss.targeted.lc.homo.hi <- subset(data.ss.targeted.lc, net.assortativity.profession > homo.me.max)

    out.finsize <- paste(out.finsize, get_descriptive_compressed(data.ss.baseline.lc.homo.lo$net.pct.rec,
                                                                 data.ss.random.lc.homo.lo$net.pct.rec,
                                                                 data.ss.targeted.lc.homo.lo$net.pct.rec,
                                                                 paste("Low homophily (", round(homo.min, 2), "-", round(homo.lo.max, 2), ")",  sep = "")))
    out.finsize <- paste(out.finsize, get_descriptive_compressed(data.ss.baseline.lc.homo.me$net.pct.rec,
                                                                 data.ss.random.lc.homo.me$net.pct.rec,
                                                                 data.ss.targeted.lc.homo.me$net.pct.rec,
                                                                 paste("Medium homophily (", round(homo.lo.max, 2), "-", round(homo.me.max, 2), ")",  sep = "")))
    out.finsize <- paste(out.finsize, get_descriptive_compressed(data.ss.baseline.lc.homo.hi$net.pct.rec,
                                                                 data.ss.random.lc.homo.hi$net.pct.rec,
                                                                 data.ss.targeted.lc.homo.hi$net.pct.rec,
                                                                 paste("High homophily (", round(homo.me.max, 2), "-", round(homo.max, 2), ")",  sep = "")))

    out.finsize.summary <- paste(out.finsize.summary, get_descriptive_summary(data.ss.baseline.lc.homo.lo$net.pct.rec,
                                                                              data.ss.random.lc.homo.lo$net.pct.rec,
                                                                              data.ss.targeted.lc.homo.lo$net.pct.rec,
                                                                              paste("Low homophily",  sep = ""), alpha = alpha.finsize))
    out.finsize.summary <- paste(out.finsize.summary, get_descriptive_summary(data.ss.baseline.lc.homo.me$net.pct.rec,
                                                                              data.ss.random.lc.homo.me$net.pct.rec,
                                                                              data.ss.targeted.lc.homo.me$net.pct.rec,
                                                                              paste("Medium homophily",  sep = ""), alpha = alpha.finsize))
    out.finsize.summary <- paste(out.finsize.summary, get_descriptive_summary(data.ss.baseline.lc.homo.hi$net.pct.rec,
                                                                              data.ss.random.lc.homo.hi$net.pct.rec,
                                                                              data.ss.targeted.lc.homo.hi$net.pct.rec,
                                                                              paste("High homophily",  sep = ""), alpha = alpha.finsize))


    out.duration <- paste(out.duration, get_descriptive_compressed(data.ss.baseline.lc.homo.lo$net.epidemic.duration,
                                                                 data.ss.random.lc.homo.lo$net.epidemic.duration,
                                                                 data.ss.targeted.lc.homo.lo$net.epidemic.duration,
                                                                 paste("Low homophily (", round(homo.min, 2), "-", round(homo.lo.max, 2), ")",  sep = "")))
    out.duration <- paste(out.duration, get_descriptive_compressed(data.ss.baseline.lc.homo.me$net.epidemic.duration,
                                                                 data.ss.random.lc.homo.me$net.epidemic.duration,
                                                                 data.ss.targeted.lc.homo.me$net.epidemic.duration,
                                                                 paste("Medium homophily (", round(homo.lo.max, 2), "-", round(homo.me.max, 2), ")",  sep = "")))
    out.duration <- paste(out.duration, get_descriptive_compressed(data.ss.baseline.lc.homo.hi$net.epidemic.duration,
                                                                 data.ss.random.lc.homo.hi$net.epidemic.duration,
                                                                 data.ss.targeted.lc.homo.hi$net.epidemic.duration,
                                                                 paste("High homophily (", round(homo.me.max, 2), "-", round(homo.max, 2), ")",  sep = "")))

    out.duration.summary <- paste(out.duration.summary, get_descriptive_summary(data.ss.baseline.lc.homo.lo$net.epidemic.duration,
                                                                              data.ss.random.lc.homo.lo$net.epidemic.duration,
                                                                              data.ss.targeted.lc.homo.lo$net.epidemic.duration,
                                                                              paste("Low homophily",  sep = ""), alpha = alpha.duration))
    out.duration.summary <- paste(out.duration.summary, get_descriptive_summary(data.ss.baseline.lc.homo.me$net.epidemic.duration,
                                                                              data.ss.random.lc.homo.me$net.epidemic.duration,
                                                                              data.ss.targeted.lc.homo.me$net.epidemic.duration,
                                                                              paste("Medium homophily",  sep = ""), alpha = alpha.duration))
    out.duration.summary <- paste(out.duration.summary, get_descriptive_summary(data.ss.baseline.lc.homo.hi$net.epidemic.duration,
                                                                              data.ss.random.lc.homo.hi$net.epidemic.duration,
                                                                              data.ss.targeted.lc.homo.hi$net.epidemic.duration,
                                                                              paste("High homophily",  sep = ""), alpha = alpha.duration))


    out.peaksize <- paste(out.peaksize, get_descriptive_compressed(data.ss.baseline.lc.homo.lo$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                 data.ss.random.lc.homo.lo$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                 data.ss.targeted.lc.homo.lo$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                 paste("Low homophily (", round(homo.min, 2), "-", round(homo.lo.max, 2), ")",  sep = "")))
    out.peaksize <- paste(out.peaksize, get_descriptive_compressed(data.ss.baseline.lc.homo.me$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                 data.ss.random.lc.homo.me$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                 data.ss.targeted.lc.homo.me$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                 paste("Medium homophily (", round(homo.lo.max, 2), "-", round(homo.me.max, 2), ")",  sep = "")))
    out.peaksize <- paste(out.peaksize, get_descriptive_compressed(data.ss.baseline.lc.homo.hi$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                 data.ss.random.lc.homo.hi$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                 data.ss.targeted.lc.homo.hi$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                 paste("High homophily (", round(homo.me.max, 2), "-", round(homo.max, 2), ")",  sep = "")))

    out.peaksize.summary <- paste(out.peaksize.summary, get_descriptive_summary(data.ss.baseline.lc.homo.lo$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                              data.ss.random.lc.homo.lo$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                              data.ss.targeted.lc.homo.lo$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                              paste("Low homophily",  sep = ""), alpha = alpha.peaksize))
    out.peaksize.summary <- paste(out.peaksize.summary, get_descriptive_summary(data.ss.baseline.lc.homo.me$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                              data.ss.random.lc.homo.me$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                              data.ss.targeted.lc.homo.me$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                              paste("Medium homophily",  sep = ""), alpha = alpha.peaksize))
    out.peaksize.summary <- paste(out.peaksize.summary, get_descriptive_summary(data.ss.baseline.lc.homo.hi$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                              data.ss.random.lc.homo.hi$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                              data.ss.targeted.lc.homo.hi$net.epidemic.peak.size/data.ss$nb.N*100,
                                                                              paste("High homophily",  sep = ""), alpha = alpha.peaksize))

  }

  out.finsize <- paste(out.finsize, get_finalsize_table_suffix(), sep = "")
  out.finsize.summary <- paste(out.finsize.summary, get_summary_table_suffix(), sep = "")

  out.duration <- paste(out.duration, get_finalsize_table_suffix(), sep = "")
  out.duration.summary <- paste(out.duration.summary, get_summary_table_suffix(), sep = "")

  out.peaksize <- paste(out.peaksize, get_finalsize_table_suffix(), sep = "")
  out.peaksize.summary <- paste(out.peaksize.summary, get_summary_table_suffix(), sep = "")

  out <- paste(out.finsize, "\n\n\n",
               out.duration, "\n\n\n",
               out.peaksize, "\n\n\n",
               out.finsize.summary, "\n\n\n",
               out.duration.summary, "\n\n\n",
               out.peaksize.summary,
               sep = "")

  # export to file
  dir.create(EXPORT_PATH_NUM, showWarnings = FALSE)
  filename <- paste(EXPORT_PATH_NUM, "overview", sep = "")
  if (!is.na(filename.ext)) {
    filename <- paste(filename, "-", filename.ext, sep = "")
  }
  filename <- paste(filename, EXPORT_FILE_EXTENSION_DESC, sep = "")
  cat(out, file = filename)
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
                               ssData = load_simulation_summary_data(),
                               showLegend = FALSE,
                               showRibbons = TRUE,
                               showAdditional = "none",
                               showAdditionalRibbons = FALSE,
                               showAxes = TRUE,
                               showAxesTitles = FALSE,
                               maxRounds = NA) {

  ### SHARED data
  rounds                <- min(rsData$round):max(rsData$round)

  ### SIR data
  # preparations :: statistical summaries per compartment
  summarySus            <- as.data.frame(do.call(rbind, with(rsData, tapply(net.pct.sus/100, round, summary))))
  summaryInf            <- as.data.frame(do.call(rbind, with(rsData, tapply(net.pct.inf/100, round, summary))))
  summaryRec            <- as.data.frame(do.call(rbind, with(rsData, tapply(net.pct.rec/100, round, summary))))
  summaryVac            <- as.data.frame(do.call(rbind, with(rsData, tapply(net.pct.vac/100, round, summary))))

  # data for lines :: medians for all compartments
  plotData              <- data.frame(rounds,
                                      summarySus$Median,
                                      summaryInf$Median,
                                      summaryRec$Median,
                                      summaryVac$Median)
  names(plotData)       <- c("Round", "Susceptible", "Infected", "Recovered", "Vaccinated")
  plotData              <- melt(plotData, id.vars = "Round")
  names(plotData)       <- c("Timestep", "Measure", "Proportion")

  # data for ribbons :: 1st and 3rd quartile per compartment
  ribbonData            <- data.frame(rounds,
                                      summarySus$`1st Qu.`, summarySus$`3rd Qu.`,
                                      summaryInf$`1st Qu.`, summaryInf$`3rd Qu.`,
                                      summaryRec$`1st Qu.`, summaryRec$`3rd Qu.`,
                                      summaryVac$`1st Qu.`, summaryVac$`3rd Qu.`)
  names(ribbonData)     <- c("Timestep",
                             "SusMin", "SusMax",
                             "InfMin", "InfMax",
                             "RecMin", "RecMax",
                             "VacMin", "VacMax")

  ### NETWORK PROPERTY data
  if (showAdditional == "clustering" ||
      showAdditional == "betweenness" ||
      showAdditional == "closeness" ||
      showAdditional == "assortativity") {
    scaleFactor           <- 100
  }
  if (showAdditional == "degree" ||
      showAdditional == "pathlength") {
    scaleFactor           <- 0.1
  }

  ### DEGREE data
  if (showAdditional == "degree") {
    # preparations :: statistical summary for degrees
    summaryDegrees       <- as.data.frame(do.call(rbind, with(ssData, tapply(net.degree.av, sim.it, summary))))
    # summaryDegreesIndex  <- as.data.frame(do.call(rbind, with(ssData, tapply(index.degree, sim.it, summary))))

    # data for lines :: median
    # degreeData          <- data.frame(rounds, "Av.degree", summaryDegrees$Median * scaleFactor)
    # !!!!!!!!!!! WORKS ONLY BECAUSE NETWORKS ARE STATIC !!!!!!!!!!! #
    degreeData          <- data.frame(rounds, "Av.degree", rep(summaryDegrees$Median[1], length(rounds)) * scaleFactor)
    names(degreeData)   <- c("Timestep", "Measure", "Proportion")
    plotData            <- rbind(plotData, degreeData)

    # degreeIndexData     <- data.frame(rounds, "Degree.index", summaryDegreesIndex$Median * scaleFactor)
    # names(degreeIndexData)   <- c("Timestep", "Measure", "Proportion")
    # plotData            <- rbind(plotData, degreeIndexData)

    # data for ribbons :: 1st and 3rd quartile per compartment
    degreeRibbonData    <- data.frame(rounds,
                                      rep(summaryDegrees$`1st Qu.`[1], length(rounds)) * scaleFactor,
                                      rep(summaryDegrees$`3rd Qu.`[1], length(rounds)) * scaleFactor)
    names(degreeRibbonData) <- c("Timestep", "DegMin", "DegMax")
    ribbonData$DegMin       <- degreeRibbonData$DegMin[match(ribbonData$Timestep, degreeRibbonData$Timestep)]
    ribbonData$DegMax       <- degreeRibbonData$DegMax[match(ribbonData$Timestep, degreeRibbonData$Timestep)]

    # degreeRibbonIndexData   <- data.frame(rounds,
    #                                   summaryDegreesIndex$`1st Qu.` * scaleFactor,
    #                                   summaryDegreesIndex$`3rd Qu.` * scaleFactor)
    # names(degreeRibbonIndexData) <- c("Timestep", "DegIndexMin", "DegIndexMax")
    # ribbonData$DegIndexMin       <- degreeRibbonIndexData$DegIndexMin[match(ribbonData$Timestep, degreeRibbonIndexData$Timestep)]
    # ribbonData$DegIndexMax       <- degreeRibbonIndexData$DegIndexMax[match(ribbonData$Timestep, degreeRibbonIndexData$Timestep)]
  }

  ### NETWORK CLUSTERING data
  if (showAdditional == "clustering") {
    # preparations :: statistical summary for clustering
    summaryClustering      <- as.data.frame(do.call(rbind, with(rsData, tapply(net.clustering.av, round, summary))))
    summaryClusteringIndex <- as.data.frame(do.call(rbind, with(rsData, tapply(index.clustering, round, summary))))

    # data for lines :: median
    clusteringData        <- data.frame(rounds, "Clustering", summaryClustering$Median * scaleFactor)
    names(clusteringData) <- c("Timestep", "Measure", "Proportion")
    plotData              <- rbind(plotData, clusteringData)

    clusteringIndexData        <- data.frame(rounds, "Clustering.index", summaryClusteringIndex$Median * scaleFactor)
    names(clusteringIndexData) <- c("Timestep", "Measure", "Proportion")
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
    ribbonData$ClusIndexMin          <- clusteringIndexRibbonData$ClusIndexMin[match(ribbonData$Timestep,
                                                                                     clusteringIndexRibbonData$Timestep)]
    ribbonData$ClusIndexMax          <- clusteringIndexRibbonData$ClusIndexMax[match(ribbonData$Timestep,
                                                                                     clusteringIndexRibbonData$Timestep)]
  }

  ### BETWEENNESS data
  if (showAdditional == "betweenness") {
    # preparations :: statistical summary for betweenness
    summaryBetweenness      <- as.data.frame(do.call(rbind, with(rsData, tapply(net.betweenness.av, round, summary))))
    summaryBetweennessIndex <- as.data.frame(do.call(rbind, with(rsData, tapply(index.betweenness.normalized, round, summary))))

    # data for lines :: median
    betweennessData        <- data.frame(rounds, "Betweenness", summaryBetweenness$Median * scaleFactor)
    names(betweennessData) <- c("Timestep", "Measure", "Proportion")
    plotData              <- rbind(plotData, betweennessData)

    betweennessIndexData        <- data.frame(rounds, "Betweenness.index", summaryBetweennessIndex$Median * scaleFactor)
    names(betweennessIndexData) <- c("Timestep", "Measure", "Proportion")
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
    ribbonData$BetwIndexMin          <- betweennessIndexRibbonData$BetwIndexMin[match(ribbonData$Timestep,
                                                                                      betweennessIndexRibbonData$Timestep)]
    ribbonData$BetwIndexMax          <- betweennessIndexRibbonData$BetwIndexMax[match(ribbonData$Timestep,
                                                                                      betweennessIndexRibbonData$Timestep)]
  }

  ### CLOSENESS data
  if (showAdditional == "closeness") {
    # preparations :: statistical summary for closeness
    summaryCloseness      <- as.data.frame(do.call(rbind, with(rsData, tapply(net.closeness.av, round, summary))))
    summaryClosenessIndex <- as.data.frame(do.call(rbind, with(rsData, tapply(index.closeness, round, summary))))

    # data for lines :: median
    closenessData        <- data.frame(rounds, "Closeness", summaryCloseness$Median * scaleFactor)
    names(closenessData) <- c("Timestep", "Measure", "Proportion")
    plotData              <- rbind(plotData, closenessData)

    closenessIndexData        <- data.frame(rounds, "Closeness.index", summaryClosenessIndex$Median * scaleFactor)
    names(closenessIndexData) <- c("Timestep", "Measure", "Proportion")
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
    ribbonData$ClosIndexMin          <- closenessIndexRibbonData$ClosIndexMin[match(ribbonData$Timestep,
                                                                                    closenessIndexRibbonData$Timestep)]
    ribbonData$ClosIndexMax          <- closenessIndexRibbonData$ClosIndexMax[match(ribbonData$Timestep,
                                                                                    closenessIndexRibbonData$Timestep)]
  }

  ### ASSORTATIVITY data
  if (showAdditional == "assortativity") {
    # preparations :: statistical summary for assortativity
    summaryAssortativity      <- as.data.frame(do.call(rbind, with(rsData, tapply(net.assortativity, round, summary))))
    summaryAssortativityIndex <- as.data.frame(do.call(rbind, with(rsData, tapply(index.assortativity, round, summary))))

    # data for lines :: median
    assortativityData        <- data.frame(rounds, "Assortativity", summaryAssortativity$Median * scaleFactor)
    names(assortativityData) <- c("Timestep", "Measure", "Proportion")
    plotData              <- rbind(plotData, assortativityData)

    assortativityIndexData        <- data.frame(rounds, "Assortativity.index", summaryAssortativityIndex$Median * scaleFactor)
    names(assortativityIndexData) <- c("Timestep", "Measure", "Proportion")
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
    ribbonData$AssoIndexMin          <- assortativityIndexRibbonData$AssoIndexMin[match(ribbonData$Timestep,
                                                                                        assortativityIndexRibbonData$Timestep)]
    ribbonData$AssoIndexMax          <- assortativityIndexRibbonData$AssoIndexMax[match(ribbonData$Timestep,
                                                                                        assortativityIndexRibbonData$Timestep)]
  }

  ### AVERAGE PATH LENGTH data
  if (showAdditional == "pathlength") {
    # preparations :: statistical summary for pathlength
    summaryPathlength      <- as.data.frame(do.call(rbind, with(rsData, tapply(net.pathlength.av, round, summary))))

    # data for lines :: median
    pathlengthData        <- data.frame(rounds, "Pathlength", summaryPathlength$Median * scaleFactor)
    names(pathlengthData) <- c("Timestep", "Measure", "Proportion")
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
  plot <- NA
  if (is.na(maxRounds)) {
    plot <- ggplot(plotData, aes(x = Timestep, y = Proportion, col = Measure)) +

      scale_y_continuous(labels = paste(seq(0, 1, by = 0.25)),
                         breaks = seq(0, 1, by = 0.25),
                         limits = c(0, 1)) +

      theme(legend.title = element_blank())  +

      scale_x_continuous(labels = paste(seq(min(rounds) - 1, max(rounds), by = 25)),
                         breaks = seq(min(rounds) - 1, max(rounds), by = 25),
                         limits = c(0, max(rounds)))

  } else {
    plot <- ggplot(plotData, aes(x = Timestep, y = Proportion, col = Measure)) +

      scale_y_continuous(labels = paste(seq(0, 1, by = 0.25)),
                         breaks = seq(0, 1, by = 0.25),
                         limits = c(0, 1)) +

      theme(legend.title = element_blank()) +

      scale_x_continuous(labels = paste(seq(min(rounds)-1, maxRounds, by = 25)),
                         breaks = seq(min(rounds)-1, maxRounds, by = 25),
                         limits = c(0, maxRounds))
  }

  # ribbons
  if (showAdditionalRibbons) {

    # ribbon :: degree
    if (showAdditional == "degree") {
      plot <- plot +
        # average degree
        geom_ribbon(data = ribbonData,
                    aes(x = Timestep, ymin = DegMin, ymax = DegMax),
                    inherit.aes = FALSE,
                    fill = COLORS["Av.degree"],
                    alpha = RIBBON_ALPHA) # +
        # geom_ribbon(data = ribbonData,
        #             aes(x = Timestep, ymin = DegIndexMin, ymax = DegIndexMax),
        #             inherit.aes = FALSE,
        #             fill = COLORS["Degree.index"],
        #             alpha = RIBBON_ALPHA)
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
    # ribbons :: susceptible, infected, recovered, vaccinated
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
                  alpha = RIBBON_ALPHA) +
      geom_ribbon(data = ribbonData,
                  aes(x = Timestep, ymin = VacMin, ymax = VacMax),
                  inherit.aes = FALSE,
                  fill = COLORS["Vaccinated"],
                  alpha = RIBBON_ALPHA)
  }

  # lines
  plot <- plot +
    geom_line(show.legend = showLegend) +
    scale_color_manual(values = COLORS)

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

    if (!showAxesTitles) {
      plot <- plot +
        theme(axis.title = element_blank())
    }

    if (showAdditional == "degree") {
      plot <- plot +
        scale_y_continuous(sec.axis = sec_axis(~./scaleFactor, name = "Av.degree"),
                           labels = paste(seq(0, 1, by = 0.25)),
                           breaks = seq(0, 1, by = 0.25),
                           limits = c(0, 1))


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

#----------------------------------------------------------------------------------------------------#
# function: export_sirs
#     Exports SIR plots
# param:  data.rs
#     the round summary data
#----------------------------------------------------------------------------------------------------#
export_sirs <- function(data.rs = load_round_summary_prepared_data(),
                        data.ss = load_simulation_summary_data(),
                        max.rounds = 150,
                        p.width = 60,
                        p.height = 35) {

  # create directory if necessary
  dir.create(EXPORT_PATH_PLOTS, showWarnings = FALSE)

  filename <- paste(EXPORT_PATH_PLOTS, "sirv", sep = "")

  for (lc in c("lc.pre")) { # unique(data.ss$nb.prof.lockdown.condition)) {

    print(paste("Start creating SIRV plots for lockdown condition:", lc))

    # file name
    filename.lc <- paste(filename, "_", str_replace(lc, "\\.", "-"), sep = "")

    # subsets by lockdown condition
    data.ss.lc <- subset(data.ss, nb.prof.lockdown.condition == lc)
    uids.lc <- data.ss.lc$sim.uid
    data.rs.lc <- data.rs[data.rs$sim.uid %in% uids.lc, ]

    # OVERALL DATA
    print("Start creating OVERALL SIRV plots.")
    # baseline
    ggsave(paste(filename.lc, "_01-overall_baseline", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
           plotSIRDevelopment(subset(data.rs.lc, nb.prof.vaccine.distribution == "none"),
                              subset(data.ss.lc, nb.prof.vaccine.distribution == "none"),
                              maxRounds = max.rounds),
           width = p.width,
           height = p.height,
           units = EXPORT_SIZE_UNITS,
           dpi = EXPORT_DPI,
           device = EXPORT_FILE_TYPE_PLOTS)
    # random
    ggsave(paste(filename.lc, "_01-overall_random", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
           plotSIRDevelopment(subset(data.rs.lc, nb.prof.vaccine.distribution == "random"),
                              subset(data.ss.lc, nb.prof.vaccine.distribution == "random"),
                              maxRounds = max.rounds),
           width = p.width,
           height = p.height,
           units = EXPORT_SIZE_UNITS,
           dpi = EXPORT_DPI,
           device = EXPORT_FILE_TYPE_PLOTS)
    # targeted
    ggsave(paste(filename.lc, "_01-overall_targeted", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
           plotSIRDevelopment(subset(data.rs.lc, nb.prof.vaccine.distribution == "by.av.degree.per.prof.group"),
                              subset(data.ss.lc, nb.prof.vaccine.distribution == "by.av.degree.per.prof.group"),
                              maxRounds = max.rounds),
           width = p.width,
           height = p.height,
           units = EXPORT_SIZE_UNITS,
           dpi = EXPORT_DPI,
           device = EXPORT_FILE_TYPE_PLOTS)

    # VACCINATION AVAILIBILITY
    for (vax.avl in c(0.05, 0.10, 0.20, 0.30, 0.40, 0.50)) {

      print(paste("Start creating SIRV plots for vaccine availibility:", vax.avl))

      # data preparations
      data.rs.vax <- subset(data.rs.lc, nb.prof.vaccine.availibility == vax.avl)
      data.ss.vax <- subset(data.ss.lc, nb.prof.vaccine.availibility == vax.avl)
      filename.lc.vax <- paste(filename.lc, "_02-vaxavl-", str_replace(format(round(vax.avl, 2), nsmall = 2), "\\.", ""), sep = "")

      # random
      print("Start creating random SIRV plot.")
      ggsave(paste(filename.lc.vax, "_random", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
             plotSIRDevelopment(subset(data.rs.vax, nb.prof.vaccine.distribution == "random"),
                                subset(data.ss.vax, nb.prof.vaccine.distribution == "random"),
                                maxRounds = max.rounds),
             width = p.width,
             height = p.height,
             units = EXPORT_SIZE_UNITS,
             dpi = EXPORT_DPI,
             device = EXPORT_FILE_TYPE_PLOTS)

      # targeted
      print("Start creating targeted SIRV plot.")
      ggsave(paste(filename.lc.vax, "_targeted", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
             plotSIRDevelopment(subset(data.rs.vax, nb.prof.vaccine.distribution == "by.av.degree.per.prof.group"),
                                subset(data.ss.vax, nb.prof.vaccine.distribution == "by.av.degree.per.prof.group"),
                                maxRounds = max.rounds),
             width = p.width,
             height = p.height,
             units = EXPORT_SIZE_UNITS,
             dpi = EXPORT_DPI,
             device = EXPORT_FILE_TYPE_PLOTS)
    }

    # VACCINATION EFFECTIVENESS
    for (vax.eff in c(0.60, 0.75, 0.90)) {

      print(paste("Start creating SIRV plots for vaccine effectiveness:", vax.eff))

      # data preparations
      data.rs.vax <- subset(data.rs.lc, nb.prof.vaccine.efficacy == vax.eff)
      data.ss.vax <- subset(data.ss.lc, nb.prof.vaccine.efficacy == vax.eff)
      filename.lc.vax <- paste(filename.lc, "_03-vaxeff-", str_replace(format(round(vax.eff, 2), nsmall = 2), "\\.", ""), sep = "")

      # random
      print("Start creating random SIRV plot.")
      ggsave(paste(filename.lc.vax, "_random", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
             plotSIRDevelopment(subset(data.rs.vax, nb.prof.vaccine.distribution == "random"),
                                subset(data.ss.vax, nb.prof.vaccine.distribution == "random"),
                                maxRounds = max.rounds),
             width = p.width,
             height = p.height,
             units = EXPORT_SIZE_UNITS,
             dpi = EXPORT_DPI,
             device = EXPORT_FILE_TYPE_PLOTS)

      # targeted
      print("Start creating targeted SIRV plot.")
      ggsave(paste(filename.lc.vax, "_targeted", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
             plotSIRDevelopment(subset(data.rs.vax, nb.prof.vaccine.distribution == "by.av.degree.per.prof.group"),
                                subset(data.ss.vax, nb.prof.vaccine.distribution == "by.av.degree.per.prof.group"),
                                maxRounds = max.rounds),
             width = p.width,
             height = p.height,
             units = EXPORT_SIZE_UNITS,
             dpi = EXPORT_DPI,
             device = EXPORT_FILE_TYPE_PLOTS)
    }

    # CLUSTERING
    cluster.min <- min(data.ss.lc$net.clustering.av)
    cluster.max <- max(data.ss.lc$net.clustering.av)
    cluster.step.size <- (cluster.max - cluster.min) / 3
    cluster.lo.max <- cluster.min + cluster.step.size
    cluster.me.max <- cluster.min + 2*cluster.step.size

    for (clus in list(c(cluster.min, cluster.lo.max),
                   c(cluster.lo.max, cluster.me.max),
                   c(cluster.me.max, cluster.max))) {

      print(paste("Start creating SIRV plots for clustering:", clus[1], "-", clus[2]))

      # data preparations
      data.ss.clus <- subset(data.ss.lc, net.clustering.av >= clus[1] & net.clustering.av < clus[2])
      uids.clus <- data.ss.clus$sim.uid
      data.rs.clus <- data.rs[data.rs$sim.uid %in% uids.clus, ]
      filename.lc.clus <- paste(filename.lc, "_04-clus",
                                str_replace(format(round(clus[1], 2), nsmall = 2), "\\.", ""), "-",
                                str_replace(format(round(clus[2], 2), nsmall = 2), "\\.", ""),
                                sep = "")

      # baseline
      print("Start creating baseline SIRV plot.")
      ggsave(paste(filename.lc.clus, "_baseline", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
             plotSIRDevelopment(subset(data.rs.clus, nb.prof.vaccine.distribution == "none"),
                                subset(data.ss.clus, nb.prof.vaccine.distribution == "none"),
                                maxRounds = max.rounds),
             width = p.width,
             height = p.height,
             units = EXPORT_SIZE_UNITS,
             dpi = EXPORT_DPI,
             device = EXPORT_FILE_TYPE_PLOTS)

      # random
      print("Start creating random SIRV plot.")
      ggsave(paste(filename.lc.clus, "_random", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
             plotSIRDevelopment(subset(data.rs.clus, nb.prof.vaccine.distribution == "random"),
                                subset(data.ss.clus, nb.prof.vaccine.distribution == "random"),
                                maxRounds = max.rounds),
             width = p.width,
             height = p.height,
             units = EXPORT_SIZE_UNITS,
             dpi = EXPORT_DPI,
             device = EXPORT_FILE_TYPE_PLOTS)

      # targeted
      print("Start creating targeted SIRV plot.")
      ggsave(paste(filename.lc.clus, "_targeted", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
             plotSIRDevelopment(subset(data.rs.clus, nb.prof.vaccine.distribution == "by.av.degree.per.prof.group"),
                                subset(data.ss.clus, nb.prof.vaccine.distribution == "by.av.degree.per.prof.group"),
                                maxRounds = max.rounds),
             width = p.width,
             height = p.height,
             units = EXPORT_SIZE_UNITS,
             dpi = EXPORT_DPI,
             device = EXPORT_FILE_TYPE_PLOTS)
    }

    # HOMOPHILY
    homo.min <- min(data.ss.lc$net.assortativity.profession)
    homo.max <- max(data.ss.lc$net.assortativity.profession)
    homo.step.size <- (homo.max - homo.min) / 3
    homo.lo.max <- homo.min + homo.step.size
    homo.me.max <- homo.min + 2*homo.step.size

    for (homo in list(c(homo.min, homo.lo.max),
                   c(homo.lo.max, homo.me.max),
                   c(homo.me.max, homo.max))) {

      print(paste("Start creating SIRV plots for homophily:", homo))

      # data preparations
      data.ss.homo <- subset(data.ss.lc, net.assortativity.profession >= homo[1] & net.assortativity.profession < homo[2])
      uids.homo <- data.ss.homo$sim.uid
      data.rs.homo <- data.rs[data.rs$sim.uid %in% uids.homo, ]
      filename.lc.homo <- paste(filename.lc, "_homo",
                                str_replace(format(round(homo[1], 2), nsmall = 2), "\\.", ""), "-",
                                str_replace(format(round(homo[2], 2), nsmall = 2), "\\.", ""),
                                sep = "")

      # baseline
      print("Start creating baseline SIRV plot.")
      ggsave(paste(filename.lc.homo, "_baseline", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
             plotSIRDevelopment(subset(data.rs.homo, nb.prof.vaccine.distribution == "none"),
                                subset(data.ss.homo, nb.prof.vaccine.distribution == "none"),
                                maxRounds = max.rounds),
             width = p.width,
             height = p.height,
             units = EXPORT_SIZE_UNITS,
             dpi = EXPORT_DPI,
             device = EXPORT_FILE_TYPE_PLOTS)

      # random
      print("Start creating random SIRV plot.")
      ggsave(paste(filename.lc.homo, "_random", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
             plotSIRDevelopment(subset(data.rs.homo, nb.prof.vaccine.distribution == "random"),
                                subset(data.ss.homo, nb.prof.vaccine.distribution == "random"),
                                maxRounds = max.rounds),
             width = p.width,
             height = p.height,
             units = EXPORT_SIZE_UNITS,
             dpi = EXPORT_DPI,
             device = EXPORT_FILE_TYPE_PLOTS)

      # targeted
      print("Start creating targeted SIRV plot.")
      ggsave(paste(filename.lc.homo, "_targeted", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
             plotSIRDevelopment(subset(data.rs.homo, nb.prof.vaccine.distribution == "by.av.degree.per.prof.group"),
                                subset(data.ss.homo, nb.prof.vaccine.distribution == "by.av.degree.per.prof.group"),
                                maxRounds = max.rounds),
             width = p.width,
             height = p.height,
             units = EXPORT_SIZE_UNITS,
             dpi = EXPORT_DPI,
             device = EXPORT_FILE_TYPE_PLOTS)
    }

  }
}


#----------------------------------------------------------------------------------------------------#
# function: get_boxplots
#     Gets all boxplots
# param:  data.ss
#     the simulation summary data
# return: the boxplots
#----------------------------------------------------------------------------------------------------#
get_boxplots <- function(data.ss = load_simulation_summary_data()) {

  # data
  s <- 1
  d <- NA

  for (q in unique(data.ss$nb.prof.quarantined)) {
    for (v in unique(data.ss$nb.prof.vaccinated)) {
      for (a in unique(data.ss$nb.prof.alpha)) {
        for (o in unique(data.ss$nb.prof.omega)) {
          for (e in unique(data.ss$nb.prof.vaccine.efficacy)) {

            data.ss.by.uids <- subset(data.ss, nb.prof.vaccinated == v &
                                        nb.prof.quarantined == q &
                                        nb.prof.vaccine.efficacy == e &
                                        nb.prof.alpha == a &
                                        nb.prof.omega == o)

            if (nrow(data.ss.by.uids) > 0) {

              if (is.na(d)) {
                d <- data.frame("scenario"    = rep(paste(s), nrow(data.ss)),
                                "final.size"  = data.ss.by.uids$net.epidemic.final.size,
                                "duration"    = data.ss.by.uids$net.epidemic.duration,
                                "peak.time"   = data.ss.by.uids$net.epidemic.peak.time,
                                "peak.size"   = data.ss.by.uids$net.epidemic.peak.size)
              } else {
                d <- rbind(d, data.frame("scenario"    = rep(paste(s), nrow(data.ss)),
                                         "final.size"  = data.ss.by.uids$net.epidemic.final.size,
                                         "duration"    = data.ss.by.uids$net.epidemic.duration,
                                         "peak.time"   = data.ss.by.uids$net.epidemic.peak.time,
                                         "peak.size"   = data.ss.by.uids$net.epidemic.peak.size))
              }

              s <- s+1
            }
          }
        }
      }
    }
  }

  # PLOTS
  # final size
  p.finalsize <- ggplot(d,
                        aes(x = reorder(scenario, final.size, FUN = median),
                            y = final.size,
                            fill = factor(scenario))) +
    geom_boxplot(alpha = 1,
                 show.legend = FALSE,
                 lwd = 0.3,
                 fatten = 0.5,
                 outlier.size = 0.3) +
    scale_x_discrete(name="Scenario") +
    scale_y_continuous(name="Final size") +
    theme(legend.position = "top",
          legend.justification = "right",
          legend.margin = margin(0,0,0,0),
          legend.box.margin = margin(-10,0,-10,0),
          legend.title = element_blank(),
          legend.background = element_rect(fill=alpha('white', 0)),
          legend.key = element_rect(colour = NA, fill = NA)) +
    theme_bw(base_size = THEME_BASE_SIZE)
  plots <- c(list(p.finalsize), "summary-finalsize")

  # duration
  p.duration <- ggplot(d,
                       aes(x = reorder(scenario, duration, FUN = median),
                           y = duration,
                           fill = factor(scenario))) +
    geom_boxplot(alpha = 1,
                 show.legend = FALSE,
                 lwd = 0.3,
                 fatten = 0.5,
                 outlier.size = 0.3) +
    scale_x_discrete(name="Scenario") +
    scale_y_continuous(name="Duration") +
    theme(legend.position = "top",
          legend.justification = "right",
          legend.margin = margin(0,0,0,0),
          legend.box.margin = margin(-10,0,-10,0),
          legend.title = element_blank(),
          legend.background = element_rect(fill=alpha('white', 0)),
          legend.key = element_rect(colour = NA, fill = NA)) +
    theme_bw(base_size = THEME_BASE_SIZE)
  plots <- c(plots, list(p.duration), "summary-duration")

  # peak time
  p.peaktime <- ggplot(d,
                        aes(x = reorder(scenario, peak.time, FUN = median),
                            y = peak.time,
                            fill = factor(scenario))) +
    geom_boxplot(alpha = 1,
                 show.legend = FALSE,
                 lwd = 0.3,
                 fatten = 0.5,
                 outlier.size = 0.3) +
    scale_x_discrete(name="Scenario") +
    scale_y_continuous(name="Peak time") +
    theme(legend.position = "top",
          legend.justification = "right",
          legend.margin = margin(0,0,0,0),
          legend.box.margin = margin(-10,0,-10,0),
          legend.title = element_blank(),
          legend.background = element_rect(fill=alpha('white', 0)),
          legend.key = element_rect(colour = NA, fill = NA)) +
    theme_bw(base_size = THEME_BASE_SIZE)
  plots <- c(plots, list(p.peaktime), "summary-peaktime")


  # peak size
  p.peaksize <- ggplot(d,
                        aes(x = reorder(scenario, peak.size, FUN = median),
                            y = peak.size,
                            fill = factor(scenario))) +
    geom_boxplot(alpha = 1,
                 show.legend = FALSE,
                 lwd = 0.3,
                 fatten = 0.5,
                 outlier.size = 0.3) +
    scale_x_discrete(name="Scenario") +
    scale_y_continuous(name="Peak size") +
    theme(legend.position = "top",
          legend.justification = "right",
          legend.margin = margin(0,0,0,0),
          legend.box.margin = margin(-10,0,-10,0),
          legend.title = element_blank(),
          legend.background = element_rect(fill=alpha('white', 0)),
          legend.key = element_rect(colour = NA, fill = NA)) +
    theme_bw(base_size = THEME_BASE_SIZE)
  plots <- c(plots, list(p.peaksize), "summary-peaksize")

  return(plots)
}

#----------------------------------------------------------------------------------------------------#
# function: export_boxplots
#     Exports all boxplots
# param:  data.ss
#     the simulation summary data
#----------------------------------------------------------------------------------------------------#
export_boxplots <- function(data.ss = load_simulation_summary_data()) {

  # create directory if necessary
  dir.create(EXPORT_PATH_PLOTS, showWarnings = FALSE)

  plots <- get_boxplots(data.ss = data.ss)

  plot.index <- 1
  name.index <- 2
  while (plot.index < length(plots)) {
    ggsave(paste(EXPORT_PATH_PLOTS, plots[name.index][[1]], EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
           plots[plot.index][[1]],
           width = EXPORT_PLOT_WIDTH*4,
           height = EXPORT_PLOT_HEIGHT,
           units = EXPORT_SIZE_UNITS,
           dpi = EXPORT_DPI,
           device = EXPORT_FILE_TYPE_PLOTS)

    plot.index <- plot.index + 2
    name.index <- name.index + 2
  }
}

#----------------------------------------------------------------------------------------------------#
# function: export_main_result_densities
#     Exports density plots for the main results
# param:  data.ss
#     the simulation summary data
#----------------------------------------------------------------------------------------------------#
export_main_result_densities <- function(data.ss = load_simulation_summary_data(),
                                         p.width = 100, p.height = 50) {

  # create directory if necessary
  dir.create(EXPORT_PATH_PLOTS, showWarnings = FALSE)

  data.ss.baseline = subset(data.ss, data.ss$nb.prof.vaccine.distribution == "none")
  data.ss.random = subset(data.ss, data.ss$nb.prof.vaccine.distribution == "random")
  data.ss.targeted = subset(data.ss, data.ss$nb.prof.vaccine.distribution == "by.av.degree.per.prof.group")

  for (lc in c("lc.none", "lc.pre", "lc.during")) {

    data.ss.baseline.lc <- data.ss.baseline
    data.ss.random.lc <- data.ss.random
    data.ss.targeted.lc <- data.ss.targeted

    if (lc != "lc.none") {
      data.ss.baseline.lc <- subset(data.ss.baseline, nb.prof.lockdown.condition == lc)
      data.ss.random.lc <- subset(data.ss.random, nb.prof.lockdown.condition == lc)
      data.ss.targeted.lc <- subset(data.ss.targeted, nb.prof.lockdown.condition == lc)
    }

    ### FINAL SIZE
    # data preparation
    dens.finsize <- data.frame(condition = rep("Baseline", nrow(data.ss.baseline.lc)),
                               final.size = data.ss.baseline.lc$net.pct.rec)
    dens.finsize <- rbind(dens.finsize, data.frame(condition = rep("Random", nrow(data.ss.random.lc)),
                                                   final.size = data.ss.random.lc$net.pct.rec))
    dens.finsize <- rbind(dens.finsize, data.frame(condition = rep("Targeted", nrow(data.ss.targeted.lc)),
                                                   final.size = data.ss.targeted.lc$net.pct.rec))
    means <- ddply(dens.finsize, "condition", summarise, grp.mean=mean(final.size))
    medians <- ddply(dens.finsize, "condition", summarise, grp.median=median(final.size))

    # plot assembly
    p.finsize <- ggplot(dens.finsize,
                        aes(x=final.size,
                            fill=condition)) +
      geom_density(alpha=0.2) +
      geom_vline(data=means,
                 aes(xintercept=grp.mean,
                     color=condition),
                 linetype="dashed") +
      geom_vline(data=medians,
                 aes(xintercept=grp.median,
                     color=condition),
                 linetype="solid") +
      xlim(0, 100) +
      labs(x = "Final size",
           y = "Density",
           color = "Condition",
           fill = "Condition") +
      scale_fill_manual(values=COLORS.CONDITIONS) +
      scale_color_manual(values=COLORS.CONDITIONS) +
      theme(legend.position="none")

    # export
    ggsave(paste(EXPORT_PATH_PLOTS, "final-size_", str_replace(lc, "\\.", ""), EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
           p.finsize,
           width = p.width,
           height = p.height,
           units = EXPORT_SIZE_UNITS,
           dpi = EXPORT_DPI,
           device = EXPORT_FILE_TYPE_PLOTS)

    # plot assembly for final size > 1
    p.finsize.larger1 <- ggplot(dens.finsize,
                                 aes(x=final.size,
                                     fill=condition)) +
      geom_density(alpha=0.2) +
      geom_vline(data=means,
                 aes(xintercept=grp.mean,
                     color=condition),
                 linetype="dashed") +
      geom_vline(data=medians,
                 aes(xintercept=grp.median,
                     color=condition),
                 linetype="solid") +
      xlim(2, 100) +
      labs(x = "Final size",
           y = "Density",
           color = "Condition",
           fill = "Condition") +
      scale_fill_manual(values=COLORS.CONDITIONS) +
      scale_color_manual(values=COLORS.CONDITIONS) +
      theme(legend.position="none")

    # export
    ggsave(paste(EXPORT_PATH_PLOTS, "final-size_", str_replace(lc, "\\.", ""), "_larger1", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
           p.finsize.larger1,
           width = p.width,
           height = p.height,
           units = EXPORT_SIZE_UNITS,
           dpi = EXPORT_DPI,
           device = EXPORT_FILE_TYPE_PLOTS)

    ### DURATION
    # data preparation
    dens.dur <- data.frame(condition = rep("Baseline", nrow(data.ss.baseline.lc)),
                           duration = data.ss.baseline.lc$net.epidemic.duration)
    dens.dur <- rbind(dens.dur, data.frame(condition = rep("Random", nrow(data.ss.random.lc)),
                                           duration = data.ss.random.lc$net.epidemic.duration))
    dens.dur <- rbind(dens.dur, data.frame(condition = rep("Targeted", nrow(data.ss.targeted.lc)),
                                           duration = data.ss.targeted.lc$net.epidemic.duration))
    means <- ddply(dens.dur, "condition", summarise, grp.mean=mean(duration))
    medians <- ddply(dens.dur, "condition", summarise, grp.median=median(duration))

    # plot assembly
    p.duration <- ggplot(dens.dur,
                         aes(x=duration,
                             fill=condition)) +
      geom_density(alpha=0.2) +
      geom_vline(data=means,
                 aes(xintercept=grp.mean,
                     color=condition),
                 linetype="dashed") +
      geom_vline(data=medians,
                 aes(xintercept=grp.median,
                     color=condition),
                 linetype="solid") +
      xlim(0, 200) +
      labs(x = "Duration",
           y = "Density",
           color = "Condition",
           fill = "Condition") +
      scale_fill_manual(values=COLORS.CONDITIONS) +
      scale_color_manual(values=COLORS.CONDITIONS) +
      theme(legend.position="none")

    # export
    ggsave(paste(EXPORT_PATH_PLOTS, "duration_", str_replace(lc, "\\.", ""), EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
           p.duration,
           width = p.width,
           height = p.height,
           units = EXPORT_SIZE_UNITS,
           dpi = EXPORT_DPI,
           device = EXPORT_FILE_TYPE_PLOTS)

    ### PEAK SIZE
    # data preparation
    dens.psize <- data.frame(condition = rep("Baseline", nrow(data.ss.baseline.lc)),
                             peak.size = data.ss.baseline.lc$net.epidemic.peak.size)
    dens.psize <- rbind(dens.psize, data.frame(condition = rep("Random", nrow(data.ss.random.lc)),
                                               peak.size = data.ss.random.lc$net.epidemic.peak.size))
    dens.psize <- rbind(dens.psize, data.frame(condition = rep("Targeted", nrow(data.ss.targeted.lc)),
                                               peak.size = data.ss.targeted.lc$net.epidemic.peak.size))
    means <- ddply(dens.psize, "condition", summarise, grp.mean=mean(peak.size))
    medians <- ddply(dens.psize, "condition", summarise, grp.median=median(peak.size))

    # plot assembly
    p.peaksize <- ggplot(dens.psize,
                         aes(x=peak.size,
                             fill=condition)) +
      geom_density(alpha=0.2) +
      geom_vline(data=means,
                 aes(xintercept=grp.mean,
                     color=condition),
                 linetype="dashed") +
      geom_vline(data=medians,
                 aes(xintercept=grp.median,
                     color=condition),
                 linetype="solid") +
      xlim(0, 10000) +
      labs(x = "Peak size",
           y = "Density",
           color = "Condition",
           fill = "Condition") +
      scale_fill_manual(values=COLORS.CONDITIONS) +
      scale_color_manual(values=COLORS.CONDITIONS) +
      theme(legend.position="none")

    # export
    ggsave(paste(EXPORT_PATH_PLOTS, "peak-size_", str_replace(lc, "\\.", ""), EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
           p.peaksize,
           width = p.width,
           height = p.height,
           units = EXPORT_SIZE_UNITS,
           dpi = EXPORT_DPI,
           device = EXPORT_FILE_TYPE_PLOTS)

    # plot assembly for peak size > 1
    p.peaksize.larger1 <- ggplot(dens.psize,
                         aes(x=peak.size,
                             fill=condition)) +
      geom_density(alpha=0.2) +
      geom_vline(data=means,
                 aes(xintercept=grp.mean,
                     color=condition),
                 linetype="dashed") +
      geom_vline(data=medians,
                 aes(xintercept=grp.median,
                     color=condition),
                 linetype="solid") +
      xlim(2, 10000) +
      labs(x = "Peak size",
           y = "Density",
           color = "Condition",
           fill = "Condition") +
      scale_fill_manual(values=COLORS.CONDITIONS) +
      scale_color_manual(values=COLORS.CONDITIONS) +
      theme(legend.position="none")

    # export
    ggsave(paste(EXPORT_PATH_PLOTS, "peak-size_", str_replace(lc, "\\.", ""), "_larger1", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
           p.peaksize.larger1,
           width = p.width,
           height = p.height,
           units = EXPORT_SIZE_UNITS,
           dpi = EXPORT_DPI,
           device = EXPORT_FILE_TYPE_PLOTS)
  }
}


export_epidemic_measure_relations <- function(data.ss = load_simulation_summary_data(),
                                              p.width = 200, p.height = 65) {

  d <- data.frame(fin.size  = data.ss$net.pct.rec,
                  duration  = data.ss$net.epidemic.duration,
                  peak.size = data.ss$net.epidemic.peak.size,
                  condition = data.ss$nb.prof.vaccine.distribution)

  d$condition[d$condition == "none"] <- "Baseline"
  d$condition[d$condition == "random"] <- "Random"
  d$condition[d$condition == "by.av.degree.per.prof.group"] <- "Targeted"


  # final size - duration
  p.fd <- ggplot(d, aes(x=fin.size,
                        y=duration,
                        shape=condition,
                        color=condition)) +
    stat_summary_bin(fun.data=mean_se,
                     bins  = 100,
                     size  = 0.4,
                     alpha = 0.6) +
    labs(x = "Final size",
         y = "Duration",
         color = "Condition",
         shape = "Condition") +
    scale_color_manual(values=COLORS.CONDITIONS)
  # export
  ggsave(paste(EXPORT_PATH_PLOTS, "finalsize-duration", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
         p.fd,
         width = p.width,
         height = p.height,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)

  # final size - peak size
  p.fp <- ggplot(d, aes(x=fin.size,
                        y=peak.size,
                        shape=condition,
                        color=condition)) +
    stat_summary_bin(fun.data=mean_se,
                     bins  = 100,
                     size  = 0.4,
                     alpha = 0.6) +
    labs(x = "Final size",
         y = "Peak size",
         color = "Condition",
         shape = "Condition") +
    scale_color_manual(values=COLORS.CONDITIONS)
  # export
  ggsave(paste(EXPORT_PATH_PLOTS, "finalsize-peaksize", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
         p.fp,
         width = p.width,
         height = p.height,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)

  # peak size - duration
  p.pd <- ggplot(d, aes(x=peak.size,
                        y=duration,
                        shape=condition,
                        color=condition)) +
    stat_summary_bin(fun.data=mean_se,
                     bins  = 100,
                     size  = 0.4,
                     alpha = 0.6) +
    labs(x = "Peak size",
         y = "Duration",
         color = "Condition",
         shape = "Condition") +
    scale_color_manual(values=COLORS.CONDITIONS)
  # export
  ggsave(paste(EXPORT_PATH_PLOTS, "peaksize-duration", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
         p.pd,
         width = p.width,
         height = p.height,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)

  # multiple plots in one
  p.fd <- p.fd + theme(legend.position = "top")
  legend <- get_legend(p.fd)
  p.fd <- p.fd + theme(legend.position="none")
  p.fp <- p.fp + theme(legend.position="none")


  # export
  ggsave(paste(EXPORT_PATH_PLOTS, "finsize-duration_finsize-peaksize", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
         grid.arrange(legend,
                      p.fd,
                      p.fp,
                      ncol = 2,
                      nrow = 2,
                      layout_matrix = rbind(c(1,1), c(2,3)),
                      widths=c(2.7, 2.7),
                      heights=c(0.2, 2.5)),
         width = p.width,
         height = p.height,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)


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

# function: exportModels
#     Creates file outputs for multi-level regression models (comparison of models, ICCs).
# param:  models
#     the models to create outputs for
#         filename:
#     the name of the output file
#----------------------------------------------------------------------------------------------------#
exportMultilevelModels <- function(models, filename) {

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

# normalizing the data (https://bit.ly/3jd8rFb)
# NOTE: data is normalized (rather than standardized, see https://bit.ly/3jd8rFb),
# because of some predictors being highly skewed (e.g. degree, betweenness)
prepare_predictor <- function(vec, normalize = TRUE) {
  if (normalize) {
    vec <- (vec - min(vec)) / (max(vec) - min(vec))
  }
  vec <- meanCenter(vec)
  vec <- vec[!is.na(vec)]
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
# function: export_regressions
#     Creates and exports regression models for attack rate, duration, peak, and peak size.
# param:  data.ss
#     simulation summary data to produce regression models for
# param:  filenamname.appendix
#     Optional string to append to the standard filename
#----------------------------------------------------------------------------------------------------#
export_regressions <- function(data.ss = load_simulation_summary_data(), filenamname.appendix = "") {

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
  n.vaccinated                      <- prepare_predictor(data.ss$net.n.vaccinated)
  n.quarantined                     <- prepare_predictor(data.ss$net.n.quarantined)
  degree.av                         <- prepare_predictor(data.ss$net.degree.av)
  degree.av.vaccinated              <- prepare_predictor(data.ss$net.degree.av.vaccinated)
  degree.av.not.vaccinated          <- prepare_predictor(data.ss$net.degree.av.vaccinated.not)
  degree.av.quarantined             <- prepare_predictor(data.ss$net.degree.av.quarantined)
  degree.av.not.quarantined         <- prepare_predictor(data.ss$net.degree.av.quarantined.not)
  clustering.av                     <- prepare_predictor(data.ss$net.clustering.av)
  assortativity.profession          <- prepare_predictor(data.ss$net.assortativity.profession)
  vaccine.efficacy                  <- prepare_predictor(data.ss$nb.prof.vaccine.efficacy)

  index.degree                      <- prepare_predictor(data.ss$index.degree)
  index.clustering                  <- prepare_predictor(data.ss$index.clustering)
  index.betweenness                 <- prepare_predictor(data.ss$index.betweenness.normalized)
  index.closeness                   <- prepare_predictor(data.ss$index.closeness)
  index.assortativity.profession    <- prepare_predictor(data.ss$index.betweenness.normalized)

  ### DEPENDENT ###
  final.size                        <- data.ss$net.epidemic.final.size / 100
  duration                          <- data.ss$net.epidemic.duration
  peak.size                         <- data.ss$net.epidemic.peak.size


  ## MAIN EFFECTS
  # final size
  finalsize.main <- glm(final.size ~
                          n.vaccinated +
                          n.quarantined +
                          degree.av +
                          # degree.av.vaccinated +
                          degree.av.not.vaccinated +
                          # degree.av.quarantined +
                          # degree.av.not.quarantined +
                          clustering.av +
                          assortativity.profession +
                          vaccine.efficacy,  # +

                        # index.degree +
                        # index.clustering +
                        # index.betweenness +
                        # index.closeness +
                        # index.assortativity.profession,
                        family = binomial)
  summary(finalsize.main)
  vif(finalsize.main)
  print_r2(finalsize.main)


  ### 2-LEVEL LOGISTIC REGRESSIONS (final size) ###
  ### level 2: parameters combination           ###
  ### level 1: simulation runs                  ###
  # null-model
  finalsize.ml.00    <- glmer(final.size ~
                             1 +
                             (1 | sim.upc),
                           family = binomial,
                           data = data.ss)
  # main effects
  finalsize.ml.main <- glmer(final.size ~
                            n.vaccinated +
                            n.quarantined +
                            degree.av +
                            # degree.av.vaccinated +
                            degree.av.not.vaccinated +
                            # degree.av.quarantined +
                            # degree.av.not.quarantined +
                            clustering.av +
                            assortativity.profession +
                            vaccine.efficacy +

                            # index.degree +
                            # index.clustering +
                            # index.betweenness +
                            # index.closeness +
                            # index.assortativity.profession +
                            (1 | sim.upc),
                          family = binomial,
                          data = data.ss)

  exportMultilevelModels(list(finalsize.ml.00, finalsize.ml.main), "reg-finalsize")







  # duration
  model.2.duration.static <- lm(duration ~
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
  summary(model.2.duration.static)
  vif(model.2.duration.static)
  print_r2(model.2.duration.static)

  # peak
  model.2.peak.static <- lm(peak ~
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
  summary(model.2.peak.static)
  vif(model.2.peak.static)
  print_r2(model.2.peak.static)

  # peak size
  model.2.peak.size.static <- lm(peak.size ~
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
  summary(model.2.peak.size.static)
  vif(model.2.peak.size.static)
  print_r2(model.2.peak.size.static)




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

  model.3.attackrate.static <- glm(final.size ~
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


############################################ BELOT DATA ##############################################
check_belot <- function() {

  belot <- load_belot_data()

  unique(belot$labor_status)
  unique(belot$age_group)
  belot.retired <- subset(subset(belot, labor_status == "Not in employment"),
                          age_group == "Between 66 and 75" | age_group == "Above 75" )
  belot.unemployed <- subset(subset(belot, labor_status == "Not in employment"),
                             age_group != "Between 66 and 75" & age_group != "Above 75" )
  nrow(subset(belot, labor_status == "Not in employment")) == (nrow(belot.retired) + nrow(belot.unemployed))


  contacts.work.retired <- c(belot.retired$close_workint_less15_adult,
                             belot.retired$close_workint_less15_child,
                             belot.retired$close_workint_less15_elder,
                             belot.retired$close_workint_more15_adult,
                             belot.retired$close_workint_more15_child,
                             belot.retired$close_workint_more15_elder)
  contacts.work.retired <- contacts.work.retired[!is.na(contacts.work.retired)]

  contacts.recent.retired <- c(belot.retired$close_recentint_less15_adult,
                               belot.retired$close_recentint_less15_child,
                               belot.retired$close_recentint_less15_elder,
                               belot.retired$close_recentint_more15_adult,
                               belot.retired$close_recentint_more15_child,
                               belot.retired$close_recentint_more15_elder)
  contacts.recent.retired <- contacts.recent.retired[!is.na(contacts.recent.retired)]
  mean(contacts.recent.retired)
  sd(contacts.recent.retired)

  av.workers.recent <- 2.3
  av.workers.work <- 5.64
  mean(contacts.recent.retired) * (av.workers.work / av.workers.recent)

  av.workers.recent.sd <- 11.6
  av.workers.work.sd <- 30.55
  sd(contacts.recent.retired) * (av.workers.work / av.workers.recent)



  contacts.work.unemployed <- c(belot.unemployed$close_workint_less15_adult,
                                belot.unemployed$close_workint_less15_child,
                                belot.unemployed$close_workint_less15_elder,
                                belot.unemployed$close_workint_more15_adult,
                                belot.unemployed$close_workint_more15_child,
                                belot.unemployed$close_workint_more15_elder)
  contacts.work.unemployed <- contacts.work.unemployed[!is.na(contacts.work.unemployed)]

  contacts.recent.unemployed <- c(belot.unemployed$close_recentint_less15_adult,
                                  belot.unemployed$close_recentint_less15_child,
                                  belot.unemployed$close_recentint_less15_elder,
                                  belot.unemployed$close_recentint_more15_adult,
                                  belot.unemployed$close_recentint_more15_child,
                                  belot.unemployed$close_recentint_more15_elder)
  contacts.recent.unemployed <- contacts.recent.unemployed[!is.na(contacts.recent.unemployed)]
  mean(contacts.recent.unemployed)
  sd(contacts.recent.unemployed)

  av.workers.recent <- 2.3
  av.workers.work <- 5.64
  mean(contacts.recent.unemployed) * (av.workers.work / av.workers.recent)

  av.workers.recent.sd <- 11.6
  av.workers.work.sd <- 30.55
  sd(contacts.recent.unemployed) * (av.workers.work / av.workers.recent)
}


############################################ COMPOSITION #############################################
#----------------------------------------------------------------------------------------------------#
# function: export_all
#     Exports all analyses.
#----------------------------------------------------------------------------------------------------#
export_all <- function() {

  # data preparation
  data.ss <- load_simulation_summary_data()
  # data.as <- load_agent_stats_data()
  data.pr <- load_professions_data()

  # descriptive statistics
  export_descriptives(data.ss = data.ss,
                      # data.as = data.as,
                      data.pr = data.pr)
  # export_descriptives(data.ss = subset(data.ss, nb.prof.lockdown.condition == "lc.pre"),
  #                     data.pr = data.pr, filename.ext = "pre", include.groups = FALSE)
  # export_descriptives(data.ss = subset(data.ss, nb.prof.lockdown.condition == "lc.during"),
  #                     data.pr = data.pr, filename.ext = "during", include.groups = FALSE)

  export_overview(data.ss = data.ss)

  # plots
  export_main_result_densities(data.ss = data.ss)
  export_epidemic_measure_relations(data.ss = data.ss, p.height = 65)

  # regression analysis
  #export_regressions(data.ss = data.ss)

  # SIR plots
  data.rs <- load_round_summary_prepared_data()
  export_sirs(data.rs = data.rs, data.ss = data.ss)

}
