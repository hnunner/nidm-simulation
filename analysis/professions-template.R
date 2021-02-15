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
CSV_ROUND_SUMMARY_PATH          <- paste(DATA_PATH, "round-summary.csv", sep = "")
CSV_PROFESSIONS_PATH            <- paste(DATA_PATH, "professions.csv", sep = "")
CSV_PROFESSION_DISTRIBUTION_PATH<- paste(DATA_PATH, "profession-dist.csv", sep = "")

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
                                 Vaccinated          = "#669966",   # green

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
load_csv <- function(filePath, header = TRUE) {
  csv <- read.csv(file=filePath, header=header, sep=";")
  return(csv)
}

#----------------------------------------------------------------------------------------------------#
# function: load_simulation_summary_data
#     Loads summary data for NIDM simulations.
# return: the summary data for NIDM simulations
#----------------------------------------------------------------------------------------------------#
load_simulation_summary_data <- function() {
  data.ss <- load_csv(CSV_SUMMARY_PATH)
  return(data.ss)
}

#----------------------------------------------------------------------------------------------------#
# function: load_agent_details_data
#     Loads agent details data.
# return: the agent details data
#----------------------------------------------------------------------------------------------------#
load_agent_details_data <- function() {
  agents_data <- load_csv(CSV_AGENT_DETAILS_PATH)
  agents_data$agent.profession <- gsub("ï»¿", "", agents_data$agent.profession)       # in case CSV contains byte order mark
  return(agents_data)
}

#----------------------------------------------------------------------------------------------------#
# function: prepare_round_summary_data
#     Prepares round summary data.
# return: the prepared round summary data
#----------------------------------------------------------------------------------------------------#
load_round_summary_prepared_data <- function() {

  data.rs <- load_csv(CSV_ROUND_SUMMARY_PATH)

  round.max <- max(data.rs$sim.round)
  while (round.max %% 10 != 0) {
    round.max <- round.max + 1
  }

  res <- data.rs[0,]
  for (uid in unique(data.rs$sim.uid)) {

    print(paste("preparing round summary records for uid:", uid))

    records.rs.by.uid <- subset(data.rs, sim.uid == uid)
    finished <- do.call("rbind", replicate(n = round.max - max(records.rs.by.uid$sim.round),
                                           expr =  records.rs.by.uid[max(records.rs.by.uid$sim.round),],
                                           simplify = FALSE))
    records.rs.by.uid.filled <- rbind(records.rs.by.uid, finished)

    # renumber rounds
    records.rs.by.uid.filled$sim.round <- seq(1, nrow(records.rs.by.uid.filled))
    records.rs.by.uid.filled$sim.uid <- paste(records.rs.by.uid.filled$sim.upc, "-",
                                              records.rs.by.uid.filled$sim.cnt, "-",
                                              records.rs.by.uid.filled$sim.it,
                                              sep = "")

    res <- rbind(res, records.rs.by.uid.filled)
  }

  # renumber rows
  rownames(res) <- 1:nrow(res)

  return(res)
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

#----------------------------------------------------------------------------------------------------#
# function: load_profession_distribution_data
#     Loads profession distribution data.
# return: the profession distribution data
#----------------------------------------------------------------------------------------------------#
load_profession_distribution_data <- function() {
  return(load_csv(CSV_PROFESSION_DISTRIBUTION_PATH, header = FALSE))
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
               round(min(vec, na.rm = TRUE), digits = 2),
               round(max(vec, na.rm = TRUE), digits = 2),
               round(skewness(vec, na.rm = TRUE), digits = 2),
               sep = " & ")
  out <- paste(out, " \\", "\\[", row.height, "pt]", " \n", sep = "")

  return(out)
}

get_descriptives_table_prefix <- function() {
  out <- paste("\\begin{table}[h]\n\\caption{Descriptive statistics", sep = "")
  out <- paste(out, ".}\n\\label{tab:descriptives}\n", sep = "")
  out <- paste(out, "\\begin{adjustbox}{width=1\\textwidth,center=\\textwidth}\n\\begin{tabular}{l *{5}{S[\n", sep = "")
  out <- paste(out, "input-symbols = {(- )},\ndetect-weight,\ngroup-separator = {,},\ntable-format=5.2]}}\n\\toprule\n", sep = "")
  out <- paste(out, "& \\text{\\thead{Mean}} & \\text{\\thead{SD}} & \\text{\\thead{Min}} & \\text{\\thead{Max}} & ", sep = "")
  out <- paste(out, "\\text{\\thead{Skew}}", " \\", "\\ \n\\midrule", sep = "")
  return(out)
}

get_descriptives_table_suffix <- function() {
  out <- "\\bottomrule\n%\\multicolumn{5}{c}{\\emph{Notes:} bold numbers are significant at $p<0.001$, SEs in parentheses}\n"
  out <- paste(out, "\\end{tabular}\n\\end{adjustbox}\n\\end{table}", sep = "")
  return(out)
}

get_professions_table_prefix <- function(number) {
  out <- paste("\\begin{table}[h]\n\\caption{Professional groups, part ", number, sep = "")
  out <- paste(out, ".}\n\\label{tab:professions", number, "}\n \\begin{tabular}{l *{5}{S[\n", sep = "")
  out <- paste(out, "input-symbols = {(- )},\ndetect-weight,\ngroup-separator = {,},\ntable-format=5.2]}}\n\\toprule\n", sep = "")
  out <- paste(out, "& \\text{\\thead{Mean}} & \\text{\\thead{SD}} & \\text{\\thead{Min}} & \\text{\\thead{Max}} & ", sep = "")
  out <- paste(out, "\\text{\\thead{Skew}}", " \\", "\\ \n\\midrule", sep = "")
  return(out)
}

get_professions_table_suffix <- function() {
  out <- "\\bottomrule\n%\\multicolumn{5}{c}{\\emph{Notes:} bold numbers are significant at $p<0.001$, SEs in parentheses}\n"
  out <- paste(out, "\\end{tabular}\n\\end{table}", sep = "")
  return(out)
}

#----------------------------------------------------------------------------------------------------#
# function: export_descriptives
#     Exports general descriptive statistics.
# param:  data.ss
#     the simulation summary data
# param:  data.pr
#     the professions data
# param:  data.ad
#     the agent details data
#----------------------------------------------------------------------------------------------------#
export_descriptives <- function(data.ss = load_simulation_summary_data(),
                                data.pr = load_professions_data(),
                                data.ad = load_agent_details_data(),
                                filename.ext = NA,
                                include.groups = TRUE) {

  memory.limit(9999999999)

  out <- paste(" observations: ", nrow(data.ss), "\n", sep = "")


  # begin: overall descriptive
  out <- paste(out, get_descriptives_table_prefix(), sep = "")

  out <- paste(out, "\\midrule \n", sep = "")
  out <- paste(out, "\\multicolumn{6}{l}{\\textbf{I. Network}}", " \\", "\\ \n", sep = "")
  out <- paste(out, get_descriptive(data.ss$nb.prof.N, "Size ($N$)"))
  out <- paste(out, get_descriptive(data.ss$net.degree.av.theoretic, "Degree theoretical ($\\mathcal{D}_{G}$)*"))
  out <- paste(out, get_descriptive(data.ss$net.degree.av, "Degree ($\\mathcal{D}_{G}$)*"))
  out <- paste(out, get_descriptive(data.ss$net.clustering.av, "Clustering ($\\mathcal{C}_{G}$)*"))
  out <- paste(out, get_descriptive(data.ss$net.assortativity.profession, "Assortativity, profession ($\\mathcal{A}^{p}_{G}$)*"))

  out <- paste(out, "\\midrule \n", sep = "")
  out <- paste(out, "\\multicolumn{6}{l}{\\textbf{II. Index case}}", " \\", "\\ \n", sep = "")
  out <- paste(out, get_descriptive(data.ss$index.degree, "Degree"))
  out <- paste(out, get_descriptive(data.ss$index.clustering, "Clustering"))
  out <- paste(out, get_descriptive(data.ss$index.betweenness.normalized, "Betweenness (normalized)"))
  out <- paste(out, get_descriptive(data.ss$index.closeness, "Closeness"))
  out <- paste(out, get_descriptive(data.ss$index.assortativity.profession, "Assortativity (profession)"))

  out <- paste(out, "\\midrule \n", sep = "")
  out <- paste(out, "\\multicolumn{6}{l}{\\textbf{III. Counter measures}}", " \\", "\\ \n", sep = "")
  out <- paste(out, get_descriptive(data.ss$nb.prof.vaccinated.percent / 100, "Vaccinated (proportion)"))
  out <- paste(out, get_descriptive(data.ss$nb.prof.vaccine.efficacy, "Vaccine efficacy"))
  out <- paste(out, get_descriptive(data.ss$nb.prof.quarantined.percent / 100, "Quarantined (proportion)"))

  out <- paste(out, "\\midrule \n", sep = "")
  out <- paste(out, "\\multicolumn{6}{l}{\\textbf{IV. Epidemic}}", " \\", "\\ \n", sep = "")
  out <- paste(out, get_descriptive(data.ss$nb.prof.gamma, "Prob. of dis. transm. / contact ($\\gamma$)"))
  out <- paste(out, get_descriptive(data.ss$nb.prof.tau, "Time to recover ($\\tau$)"))
  out <- paste(out, get_descriptive(data.ss$net.epidemic.final.size, "Final size"))
  out <- paste(out, get_descriptive(data.ss$net.epidemic.duration, "Duration"))
  out <- paste(out, get_descriptive(data.ss$net.epidemic.peak.time, "Epidemic peak time"))
  out <- paste(out, get_descriptive(data.ss$net.epidemic.peak.size, "Epidemic peak size"))

  out <- paste(out, get_descriptives_table_suffix(), "\n\n", sep = "")
  # end: overall descriptive


  if (include.groups) {
    t <- 1
    for (prof in unique(data.pr$V1)) {

      if (prof %in% c("ArchEng", "ConstrExtr", "InstMaintRepair", "Prod")) {
        out <- paste (out, get_professions_table_prefix(t), sep = "")
        t <- t+1
      }

      out <- paste(out, "\\midrule \n", sep = "")
      out <- paste(out, "\\multicolumn{6}{l}{\\textbf{", subset(data.pr, V1 == prof)[1,7], "}}", " \\", "\\ \n", sep = "")


      # TODO
      #   - discuss: what is best way to present size of professional groups? percentage (as is), total, both
      # size
      out <- paste(out, get_descriptive((data.ss[grepl(tolower(prof), colnames(data.ss))][1] / data.ss$nb.prof.N)[,1], "N"))
      out <- paste(out, "N (labor market) & ", round(subset(data.pr, V1 == prof)[1,2] / sum(data.pr[,2]), 2), "& & & & ", " \\", "\\ \n", sep = "")


      # normal
      normal.uids <- filter(data.ss, !grepl(prof, nb.prof.quarantined))$sim.uid
      normal <- subset(data.ad, sim.uid %in% normal.uids & grepl(prof, agent.profession))
      out <- paste(out, get_descriptive(normal$agent.degree, "Degree, normal"))
      out <- paste(out, "Degree, normal (Belot) & ", subset(data.pr, V1 == prof)[1,3], " & ", subset(data.pr, V1 == prof)[1,4], " & NA & NA & NA ", " \\", "\\ \n", sep = "")
      # quarantined
      quarantined.uids <- filter(data.ss, grepl(prof, nb.prof.quarantined))$sim.uid
      quarantined <- subset(data.ad, sim.uid %in% quarantined.uids & grepl(prof, agent.profession))
      if (nrow(quarantined) > 1) {
        out <- paste(out, get_descriptive(quarantined$agent.degree, "Degree, quarantined"))
      } else {
        out <- paste(out, "Degree, quarantined & NA & NA & NA & NA & NA ", " \\", "\\ \n", sep = "")
      }
      out <- paste(out, "Degree, quarantined (Belot) & ", subset(data.pr, V1 == prof)[1,5], " & ", subset(data.pr, V1 == prof)[1,6], " & NA & NA & NA ", " \\", "\\ \n", sep = "")

      if (prof %in% c("CompMath", "HealthSupp", "PersCareServ", "Unemployed")) {
        out <- paste (out, get_professions_table_suffix(), "\n\n", sep = "")
      }
    }
  }

  # export to file
  dir.create(EXPORT_PATH_NUM, showWarnings = FALSE)
  filename <- paste(EXPORT_PATH_NUM, "descriptives", sep = "")
  if (!is.na(filename.ext)) {
    paste(filename, "-", filename.ext, sep = "")
  }
  filename <- paste(filename, EXPORT_FILE_EXTENSION_DESC, sep = "")
  cat(out, file = filename)
}

#----------------------------------------------------------------------------------------------------#
# function: export_descriptive_variations
#     Exports descriptive statistics for parameter variations.
# param:  data.ss
#     the simulation summary data
#----------------------------------------------------------------------------------------------------#
export_descriptive_variations <- function(data.ss = load_simulation_summary_data()) {

  memory.limit



  # begin: variations of epidemics
  rows <- 1
  row.height <- 7
  out <- ""
  table.closed <- TRUE
  for (q in unique(data.ss$nb.prof.quarantined)) {
    for (v in unique(data.ss$nb.prof.vaccinated)) {
      for (a in unique(data.ss$nb.prof.alpha)) {
        for (o in unique(data.ss$nb.prof.omega)) {
          for (e in unique(data.ss$nb.prof.vaccine.efficacy)) {

            d <- subset(data.ss, nb.prof.vaccinated == v &
                          nb.prof.quarantined == q &
                          nb.prof.vaccine.efficacy == e &
                          nb.prof.alpha == a &
                          nb.prof.omega == o)

            if (nrow(d) > 0) {
              if (rows == 1) {
                out <- paste(out, get_descriptives_table_prefix())
                table.closed <- FALSE
              }

              out <- paste(out, "\\midrule \n", sep = "")
              out <- paste(out, "\\multicolumn{6}{l}{\\textbf{",
                           "observations \\pmb{$=", nrow(d), "$}, ",
                           "}}", " \\", "\\ \n", sep = "")
              out <- paste(out, "\\multicolumn{6}{l}{\\textbf{",
                           "likelihood of ties to same profession (\\pmb{$\\omega=", o, "$}),",
                           "}}", " \\", "\\ \n", sep = "")
              out <- paste(out, "\\multicolumn{6}{l}{\\textbf{",
                           "optimal share of closed triads (\\pmb{$\\alpha=", a, "$}),",
                           "}}", " \\", "\\ \n", sep = "")
              out <- paste(out, "\\multicolumn{6}{l}{\\textbf{",
                           "vaccinated \\pmb{$=", ifelse(nchar(v) > 0, v, "[]"), "$}, ",
                           "\\pmb{$\\eta=", e, "$}, ",
                           "}}", " \\", "\\ \n", sep = "")
              out <- paste(out, "\\multicolumn{6}{l}{\\textbf{",
                           "quarantined \\pmb{$=", ifelse(nchar(q) > 0, q, "[]"), "$}",
                           "}}", " \\", "\\[", row.height, "pt] \n", sep = "")


              out <- paste(out, get_descriptive(d$nb.prof.c2, "Av. marginal costs ($c_{2}$)"))
              out <- paste(out, get_descriptive(d$net.degree.av.theoretic, "Degree theoretical ($\\mathcal{D}_{G}$)*"))
              out <- paste(out, get_descriptive(d$net.degree.av, "Degree ($\\mathcal{D}_{G}$)*"))
              out <- paste(out, get_descriptive(d$net.clustering.av, "Clustering ($\\mathcal{C}_{G}$)*"))
              out <- paste(out, get_descriptive(d$net.assortativity.profession, "Assortativity, profession ($\\mathcal{A}^{p}_{G}$)*", row.height))

              out <- paste(out, get_descriptive(d$nb.prof.vaccinated.percent / 100, "Quarantined (proportion)"))
              out <- paste(out, get_descriptive(d$nb.prof.quarantined.percent / 100, "Vaccinated (proportion)", row.height))

              out <- paste(out, get_descriptive(d$net.epidemic.final.size, "Final size"))
              out <- paste(out, get_descriptive(d$net.epidemic.duration, "Epidemic duration"))
              out <- paste(out, get_descriptive(d$net.epidemic.peak.time, "Epidemic peak time"))
              out <- paste(out, get_descriptive(d$net.epidemic.peak.size, "Epidemic peak size"))

              rows <- rows+1

              if (rows >= 3) {
                out <- paste(out, get_descriptives_table_suffix(), "\n\n", sep = "")
                rows <- 1
                table.closed <- TRUE
              }
            }
          }
        }
      }
    }
  }
  if (!table.closed) {
    out <- paste(out, get_descriptives_table_suffix(), "\n\n", sep = "")
  }

  # export to file
  dir.create(EXPORT_PATH_NUM, showWarnings = FALSE)
  cat(out, file = paste(EXPORT_PATH_NUM, "variations-networks-epidemics", EXPORT_FILE_EXTENSION_DESC, sep = ""))
  # end: variations of epidemics



  # # begin: variations of epidemics
  # rows <- 1
  # row.height <- 7
  # out <- ""
  # table.closed <- TRUE
  #
  # for (uid in unique(data.ss$sim.uid)) {
  #   d <- subset(data.ss, sim.uid == uid)
  #
  #   if (nrow(d) > 0) {
  #     if (rows == 1) {
  #       out <- paste(out, get_descriptives_table_prefix())
  #       table.closed <- FALSE
  #     }
  #
  #     out <- paste(out, "\\midrule \n", sep = "")
  #     out <- paste(out, "\\multicolumn{6}{l}{\\textbf{",
  #                  "observations \\pmb{$=", nrow(d), "$}, ",
  #                  "}}", " \\", "\\ \n", sep = "")
  #     out <- paste(out, "\\multicolumn{6}{l}{\\textbf{",
  #                  "\\pmb{$\\omega=", d$nb.prof.omega[1], "$}, ",
  #                  "}}", " \\", "\\ \n", sep = "")
  #     out <- paste(out, "\\multicolumn{6}{l}{\\textbf{",
  #                  "\\pmb{$\\alpha=", d$nb.prof.alpha[1], "$}, ",
  #                  "}}", " \\", "\\ \n", sep = "")
  #     out <- paste(out, "\\multicolumn{6}{l}{\\textbf{",
  #                  "vaccinated \\pmb{$=", ifelse(nchar(d$nb.prof.vaccinated[1]) > 0, d$nb.prof.vaccinated[1], "[]"), "$}, ",
  #                  "\\pmb{$\\eta=", d$nb.prof.vaccine.efficacy[1], "$}, ",
  #                  "}}", " \\", "\\ \n", sep = "")
  #     out <- paste(out, "\\multicolumn{6}{l}{\\textbf{",
  #                  "quarantined \\pmb{$=", ifelse(nchar(d$nb.prof.quarantined[1]) > 0, d$nb.prof.quarantined[1], "[]"), "$}",
  #                  "}}", " \\", "\\[", row.height, "pt] \n", sep = "")
  #
  #
  #     out <- paste(out, get_descriptive(d$nb.prof.c2, "Av. marginal costs ($c_{2}$)"))
  #     out <- paste(out, get_descriptive(d$net.degree.av.theoretic, "Degree theoretical ($\\mathcal{D}_{G}$)*"))
  #     out <- paste(out, get_descriptive(d$net.degree.av, "Degree ($\\mathcal{D}_{G}$)*"))
  #     out <- paste(out, get_descriptive(d$net.clustering.av, "Clustering ($\\mathcal{C}_{G}$)*"))
  #     out <- paste(out, get_descriptive(d$net.assortativity.profession, "Assortativity, profession ($\\mathcal{A}^{p}_{G}$)*", row.height))
  #
  #     out <- paste(out, get_descriptive(d$nb.prof.vaccinated.percent / 100, "Quarantined (proportion)"))
  #     out <- paste(out, get_descriptive(d$nb.prof.quarantined.percent / 100, "Vaccinated (proportion)", row.height))
  #
  #     out <- paste(out, get_descriptive(d$net.epidemic.final.size, "Final size"))
  #     out <- paste(out, get_descriptive(d$net.epidemic.duration, "Epidemic duration"))
  #     out <- paste(out, get_descriptive(d$net.epidemic.peak.time, "Epidemic peak time"))
  #     out <- paste(out, get_descriptive(d$net.epidemic.peak.size, "Epidemic peak size"))
  #
  #     rows <- rows+1
  #
  #     if (rows >= 3) {
  #       out <- paste(out, get_descriptives_table_suffix(), "\n\n", sep = "")
  #       rows <- 1
  #       table.closed <- TRUE
  #     }
  #   }
  # }
  # if (!table.closed) {
  #   out <- paste(out, get_descriptives_table_suffix(), "\n\n", sep = "")
  # }
  #
  # # export to file
  # dir.create(EXPORT_PATH_NUM, showWarnings = FALSE)
  # cat(out, file = paste(EXPORT_PATH_NUM, "variations-networks-epidemics", EXPORT_FILE_EXTENSION_DESC, sep = ""))
  # # end: variations of epidemics



  # # begin: variations of networks
  # out <- get_descriptives_table_prefix()
  #
  # for (q in unique(data.ss$nb.prof.quarantined)) {
  #   for (o in unique(data.ss$nb.prof.omega)) {
  #     d <- subset(data.ss, nb.prof.quarantined == q & nb.prof.omega == o)
  #
  #     out <- paste(out, "\\midrule \n", sep = "")
  #     out <- paste(out, "\\multicolumn{6}{l}{\\textbf{",
  #                  "quarantined \\pmb{$=", ifelse(length(q) > 0, q, "[]"), "$}, ",
  #                  "\\pmb{$\\omega=", o, "$}",
  #                  "}}", " \\", "\\ \n", sep = "")
  #     out <- paste(out, get_descriptive(d$nb.prof.N, "Size ($N$)"))
  #     out <- paste(out, get_descriptive(d$nb.prof.quarantined.percent / 100, "Quarantined (proportion)"))
  #     out <- paste(out, get_descriptive(d$nb.prof.c2, "Av. marginal costs ($c_{2}$)"))
  #     out <- paste(out, get_descriptive(d$net.degree.av.theoretic, "Degree theoretical ($\\mathcal{D}_{G}$)*"))
  #     out <- paste(out, get_descriptive(d$net.degree.av, "Degree ($\\mathcal{D}_{G}$)*"))
  #     out <- paste(out, get_descriptive(d$net.clustering.av, "Clustering ($\\mathcal{C}_{G}$)*"))
  #     out <- paste(out, get_descriptive(d$net.assortativity.profession, "Assortativity, profession ($\\mathcal{A}^{p}_{G}$)*"))
  #   }
  # }
  # out <- paste(out, get_descriptives_table_suffix(), "\n\n", sep = "")
  #
  # # export to file
  # dir.create(EXPORT_PATH_NUM, showWarnings = FALSE)
  # cat(out, file = paste(EXPORT_PATH_NUM, "variations-networks", EXPORT_FILE_EXTENSION_DESC, sep = ""))
  # # end: variations of networks
  #
  # # begin: variations of epidemics
  # rows <- 1
  # row.height <- 7
  # out <- ""
  # table.closed <- TRUE
  # for (q in unique(data.ss$nb.prof.quarantined)) {
  #   for (v in unique(data.ss$nb.prof.vaccinated)) {
  #     for (o in unique(data.ss$nb.prof.omega)) {
  #       for (e in unique(data.ss$nb.prof.vaccine.efficacy)) {
  #
  #         d <- subset(data.ss, nb.prof.vaccinated == v &
  #                       nb.prof.quarantined == q &
  #                       nb.prof.vaccine.efficacy == e &
  #                       nb.prof.omega == o)
  #
  #         if (nrow(d) > 0) {
  #           if (rows == 1) {
  #             out <- paste(out, get_descriptives_table_prefix())
  #             table.closed <- FALSE
  #           }
  #
  #           out <- paste(out, "\\midrule \n", sep = "")
  #           out <- paste(out, "\\multicolumn{6}{l}{\\textbf{",
  #                        "observations \\pmb{$=", nrow(d), "$}, ",
  #                        "}}", " \\", "\\ \n", sep = "")
  #           out <- paste(out, "\\multicolumn{6}{l}{\\textbf{",
  #                        "\\pmb{$\\omega=", o, "$}, ",
  #                        "}}", " \\", "\\ \n", sep = "")
  #           out <- paste(out, "\\multicolumn{6}{l}{\\textbf{",
  #                        "vaccinated \\pmb{$=", ifelse(nchar(v) > 0, v, "[]"), "$}, ",
  #                        "\\pmb{$\\eta=", e, "$}, ",
  #                        "}}", " \\", "\\ \n", sep = "")
  #           out <- paste(out, "\\multicolumn{6}{l}{\\textbf{",
  #                        "quarantined \\pmb{$=", ifelse(nchar(q) > 0, q, "[]"), "$}",
  #                        "}}", " \\", "\\[", row.height, "pt] \n", sep = "")
  #
  #
  #           out <- paste(out, get_descriptive(d$nb.prof.c2, "Av. marginal costs ($c_{2}$)"))
  #           out <- paste(out, get_descriptive(d$net.degree.av.theoretic, "Degree theoretical ($\\mathcal{D}_{G}$)*"))
  #           out <- paste(out, get_descriptive(d$net.degree.av, "Degree ($\\mathcal{D}_{G}$)*"))
  #           out <- paste(out, get_descriptive(d$net.clustering.av, "Clustering ($\\mathcal{C}_{G}$)*"))
  #           out <- paste(out, get_descriptive(d$net.assortativity.profession, "Assortativity, profession ($\\mathcal{A}^{p}_{G}$)*", row.height))
  #
  #           out <- paste(out, get_descriptive(d$nb.prof.vaccinated.percent / 100, "Quarantined (proportion)"))
  #           out <- paste(out, get_descriptive(d$nb.prof.quarantined.percent / 100, "Vaccinated (proportion)", row.height))
  #
  #           out <- paste(out, get_descriptive(d$net.epidemic.final.size, "Final size"))
  #           out <- paste(out, get_descriptive(d$net.epidemic.duration, "Epidemic duration"))
  #           out <- paste(out, get_descriptive(d$net.epidemic.peak.time, "Epidemic peak time"))
  #           out <- paste(out, get_descriptive(d$net.epidemic.peak.size, "Epidemic peak size"))
  #
  #           rows <- rows+1
  #
  #           if (rows >= 3) {
  #             out <- paste(out, get_descriptives_table_suffix(), "\n\n", sep = "")
  #             rows <- 1
  #             table.closed <- TRUE
  #           }
  #         }
  #       }
  #     }
  #   }
  # }
  # if (!table.closed) {
  #   out <- paste(out, get_descriptives_table_suffix(), "\n\n", sep = "")
  # }
  #
  # # export to file
  # dir.create(EXPORT_PATH_NUM, showWarnings = FALSE)
  # cat(out, file = paste(EXPORT_PATH_NUM, "variations-networks-epidemics", EXPORT_FILE_EXTENSION_DESC, sep = ""))
  # # end: variations of epidemics

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
  summaryVac            <- as.data.frame(do.call(rbind, with(rsData, tapply(net.pct.vac, sim.round, summary))))

  # data for lines :: medians for all compartments
  plotData              <- data.frame(rounds,
                                      summarySus$Median,
                                      summaryInf$Median,
                                      summaryRec$Median,
                                      summaryVac$Median)
  names(plotData)       <- c("Round", "Susceptible", "Infected", "Recovered", "Vaccinated")
  plotData              <- melt(plotData, id.vars = "Round")
  names(plotData)       <- c("Timestep", "Measure", "Frequency")

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

#----------------------------------------------------------------------------------------------------#
# function: export_sirs
#     Exports SIR plots
# param:  data.rs
#     the round summary data
#----------------------------------------------------------------------------------------------------#
export_sirs <- function(data.rs = load_round_summary_prepared_data()) {

  # create directory if necessary
  dir.create(EXPORT_PATH_PLOTS, showWarnings = FALSE)

  ggsave(paste(EXPORT_PATH_PLOTS, "sir", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
         plotSIRDevelopment(data.rs),
         width = 150,
         height = 50,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)

}


#----------------------------------------------------------------------------------------------------#
# function: export_sirs_variations
#     Exports SIRV development plots for parameter variations.
# param:  data.rs
#     the round summary data
#----------------------------------------------------------------------------------------------------#
export_sirs_variations <- function(data.ss = load_simulation_summary_data(),
                                   data.rs = load_round_summary_prepared_data()) {

  # create directory if necessary
  dir.create(EXPORT_PATH_PLOTS, showWarnings = FALSE)

  plot.cnt <- 1

  # begin: variations of epidemics
  for (q in unique(data.ss$nb.prof.quarantined)) {
    for (v in unique(data.ss$nb.prof.vaccinated)) {
      for (a in unique(data.ss$nb.prof.alpha)) {
        for (o in unique(data.ss$nb.prof.omega)) {
          for (e in unique(data.ss$nb.prof.vaccine.efficacy)) {

            uids <- subset(data.ss, nb.prof.vaccinated == v &
                             nb.prof.quarantined == q &
                             nb.prof.vaccine.efficacy == e &
                             nb.prof.alpha == a &
                             nb.prof.omega == o)$sim.uid

            if (length(uids) > 0) {
              ggsave(paste(EXPORT_PATH_PLOTS,
                           plot.cnt,
                           "-sir",
                           # "-o_", o,
                           # "-a_", a,
                           # "-v_", v,
                           # "-e_", e,
                           # "-q_", q,
                           EXPORT_FILE_EXTENSION_PLOTS,
                           sep = ""),
                     plotSIRDevelopment(subset(data.rs, sim.uid %in% uids)),
                     width = 150,
                     height = 50,
                     units = EXPORT_SIZE_UNITS,
                     dpi = EXPORT_DPI,
                     device = EXPORT_FILE_TYPE_PLOTS)

              plot.cnt <- plot.cnt+1

            }
          }
        }
      }
    }
  }
}










#----------------------------------------------------------------------------------------------------#
# function: plot_attackrate
#     Exports plots for attack rate
# param:  data.ss
#     the simulation summary
# return: the plot
#----------------------------------------------------------------------------------------------------#
plot_attackrate <- function(data.ss = load_simulation_summary_data()) {
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
# function: plot_duration
#     Exports plots for duration
# param:  data.ss
#     the simulation summary data
# param:  show.legend
#     whether legend ought to be displayed
# return: the plot
#----------------------------------------------------------------------------------------------------#
plot_duration <- function(data.ss = load_simulation_summary_data(), show.legend = FALSE) {

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
# function: plot_peak
#     Exports plots for peak
# param:  data.ss
#     the simulation summary data
# param:  show.legend
#     whether legend ought to be displayed
# return: the plot
#----------------------------------------------------------------------------------------------------#
plot_peak <- function(data.ss = load_simulation_summary_data(), show.legend = FALSE) {

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
# function: plot_peaksize
#     Exports plots for peak size
# param:  data.ss
#     the simulation summary data
# param:  show.legend
#     whether legend ought to be displayed
# return: the plot
#----------------------------------------------------------------------------------------------------#
plot_peaksize <- function(data.ss = load_simulation_summary_data(), show.legend = FALSE) {

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

#----------------------------------------------------------------------------------------------------#
# function: get_plots
#     Gets all plots
# param:  data.ss
#     the simulation summary data
# param: data.ad
#     the agent details data
# return: the plots
#----------------------------------------------------------------------------------------------------#
get_plots <- function(data.ss = load_simulation_summary_data(),
                      data.ad = load_agent_details_prepared_data()) {

  ### NETWORK DYNAMICS ###
  plots <- c(list(plot_c1_a(data.ss)), "0-01-attackrate")
  plots <- c(plots, list(plot_c1_b_duration(data.ss)), "0-02-duration")
  plots <- c(plots, list(plot_c1_b_peak(data.ss = )), "0-03-peak")
  plots <- c(plots, list(plot_c1_b_peaksize(data.ss)), "0-04-peaksize")

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
# function: export_regressions
#     Creates and exports regression models for attack rate, duration, peak, and peak size.
# param:  data.ss
#     simulation summary data to produce regression models for
# param:  filenamname.appendix
#     Optional string to append to the standard filename
#----------------------------------------------------------------------------------------------------#
export_regressions <- function(data.ss = load_simulation_summary_data(), filenamname.appendix = "") {



  # TODO
  #   1. data integrity checks
  #   2. main effects of interest from new data set
  #   3. create interactions ("export_interactions()") for main effects
  #   4. reduce interactions to those that are significant and have a certain effect on predictions





  ## DATA INTEGRITY CHECKS
  # quick overview of data
  head(data.ss)
  # check whether columns have correct data (types)
  str(data.ss)
  # check whether NA values occur in relevant outcome and predictor variable(s)
  data.ss[
    is.na(data.ss$net.dynamic.pct.rec) |

      is.na(data.ss$net.dynamic.ties.broken.epidemic) |
      is.na(data.ss$net.dynamic.ties.formed.epidemic) |

      is.na(data.ss$nb.r.min) |
      is.na(data.ss$nb.r.max) |
      is.na(data.ss$nb.r.sigma.av) |
      is.na(data.ss$nb.alpha) |
      is.na(data.ss$nb.omega) |
      is.na(data.ss$nb.sigma) |
      is.na(data.ss$nb.gamma) |

      is.na(data.ss$index.r.sigma.neighborhood) |
      is.na(data.ss$net.clustering.pre.epidemic.av) |
      is.na(data.ss$net.pathlength.pre.epidemic.av) |
      is.na(data.ss$index.betweenness.normalized) |
      is.na(data.ss$net.assortativity.pre.epidemic), ]                           # none - otherwise: remove

  # in case of categorical or boolean predictors, use xtab to check whether there are enough date for each factor, for example:
  xtabs(~ net.dynamic.pct.rec + net.stable.pre, data = data.ss)






  #### DATA PREPARATIONS ####
  ### INDEPENDENT ###
  ## MAIN EFFECTS
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
  attack.rate <- data.ss$net.static.pct.rec / 100
  duration    <- data.ss$net.static.epidemic.duration
  peak        <- data.ss$net.static.epidemic.peak
  peak.size   <- data.ss$net.static.epidemic.peak.size


  ## MAIN EFFECTS
  # attack rate
  model.2.attackrate.static <- glm(attack.rate ~
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
  summary(model.2.attackrate.static)
  vif(model.2.attackrate.static)
  print_r2(model.2.attackrate.static)

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


############################################ COMPOSITION #############################################
#----------------------------------------------------------------------------------------------------#
# function: export_all
#     Exports all analyses.
#----------------------------------------------------------------------------------------------------#
export_all <- function() {

  # data preparation
  data.ss <- load_simulation_summary_data()
  data.rs <- load_round_summary_prepared_data()
  data.pr <- load_professions_data()
  data.ad <- load_agent_details_data()

  export_descriptives(data.ss = data.ss, data.pr = data.pr, data.ad = data.ad)
  export_descriptive_variations(data.ss = data.ss)


  export_plots(data.ss = data.ss, data.ad = data.ad)
  export_regressions(data.ss = data.ss)

  export_sirs(data.rs = data.rs)
  export_sirs_variations(data.rs = data.rs)
}
