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
              "dplyr"         # 'select' function
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
EXPORT_FILE_TYPE_DESC       <- "txt"
EXPORT_FILE_EXTENSION_DESC  <- paste(".", EXPORT_FILE_TYPE_DESC, sep = "")
EXPORT_DIR_PLOTS            <- "figures/"
EXPORT_PATH_PLOTS           <- paste(DATA_PATH, EXPORT_DIR_PLOTS, sep = "")
EXPORT_FILE_TYPE_PLOTS      <- "png"
EXPORT_FILE_EXTENSION_PLOTS <- paste(".", EXPORT_FILE_TYPE_PLOTS, sep = "")
# export image settings
EXPORT_PLOT_WIDTH           <- 250
EXPORT_PLOT_HEIGHT          <- 100
EXPORT_SIZE_UNITS           <- "mm"
EXPORT_DPI                  <- 1200
RIBBON_ALPHA                <- 0.3
# colors (http://mkweb.bcgsc.ca/colorblind/)
COLORS                      <- c(Susceptible = "#F0E442",   # yellow
                                 Infected    = "#E69F00",   # orange
                                 Recovered   = "#0072B2",   # blue
                                 Degree      = "#888888",   # grey
                                 Density     = "#000000")   # black


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
  data.ss <- subset(data.ss, data.ss$net.pathlength.pre.epidemic.av >= 1)
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
# function: prepare_round_summary_data
#     Prepares round summary data.
# param:  rsData
#     the round summary data to be prepared
# return: the prepared round summary data
#----------------------------------------------------------------------------------------------------#
prepare_round_summary_data <- function(data.rs = load_round_summary_data()) {

  # only data of not excluded records
  data.rs <- subset(data.rs, sim.uid %in% load_simulation_summary_data()$sim.uid)

  all.sims.pre <- table(subset(data.rs, sim.stage == "pre-epidemic")$sim.round) == length(unique(data.rs$sim.uid))
  round.min <- 1
  round.min.found <- FALSE
  while (!round.min.found) {
    if (all.sims.pre[round.min + 1]) {
      round.min <- round.min + 1
    } else {
      round.min.found = TRUE
    }
  }
  data.rs.pre <- subset(data.rs, sim.stage == "pre-epidemic")

  data.rs <- subset(data.rs, sim.stage != "pre-epidemic")
  round.max <- max(subset(data.rs, sim.stage == "active-epidemic")$sim.round)
  while (round.max %% 10 != 0) {
    round.max <- round.max + 1
  }
  round.max <- round.max - round.min

  res <- data.rs[0,]
  for (ep.structure in c("static", "dynamic")) {

    data.rs.by.ep.structure <- subset(data.rs, nb.ep.structure == ep.structure)

    for (uid in unique(data.rs.by.ep.structure$sim.uid)) {

      print(paste("preparing", ep.structure, "round summary records for uid:", uid))

      records.rs.by.uid <- subset(data.rs.by.ep.structure, sim.uid == uid)
      finished <- subset(records.rs.by.uid, sim.stage == "finished")

      if (finished$sim.round < round.max+1) {
        # fill up rounds with finished records till we get the required number of rounds
        records.rs.by.uid <- subset(records.rs.by.uid, sim.stage != "finished")
        finished <- do.call("rbind", replicate(round.max - max(records.rs.by.uid$sim.round), finished, simplify = FALSE))
        records.rs.by.uid <- rbind(records.rs.by.uid, finished)
      } else {
        # drop rounds exceeding the round maximum
        records.rs.by.uid <- subset(records.rs.by.uid, sim.round <= round.max)
      }

      # add pre-epidemic rounds
      records.rs.by.uid.pre <- subset(data.rs.pre, sim.uid == uid)
      records.rs.by.uid.pre <- subset(records.rs.by.uid.pre, sim.round > max(records.rs.by.uid.pre$sim.round - round.min))
      records.rs.by.uid.pre$nb.ep.structure <- ep.structure
      records.rs.by.uid <- rbind(records.rs.by.uid.pre, records.rs.by.uid)

      # renumber rounds
      records.rs.by.uid$sim.round <- seq(1, nrow(records.rs.by.uid))

      res <- rbind(res, records.rs.by.uid)
    }
  }

  return(res)
}

#----------------------------------------------------------------------------------------------------#
# function: load_round_summary_data
#     Loads summary data for all simulated NIDM rounds.
# return: the summary data for all simulated NIDM rounds
#----------------------------------------------------------------------------------------------------#
load_round_summary_data <- function(prepare_data = TRUE) {

  data.rs <- load_csv(CSV_ROUND_SUMMARY_PATH)
  if (prepare_data) {
    data.rs <- prepare_round_summary_data(data.rs)
  }
  return(data.rs)
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
# function: loadAgentDetailsData
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
  out <- "------------------------------------------\n"
  out <- paste(out, " ", title, "\n", sep = "")
  out <- paste(out, "    mean:   ", round(mean(vec), digits = 2), " (", round(sd(vec), digits = 2), ")\n", sep = "")
  out <- paste(out, "    min:    ", round(min(vec), digits = 2), "\n", sep = "")
  out <- paste(out, "    max:    ", round(max(vec), digits = 2), "\n", sep = "")
  out <- paste(out, "    median: ", round(median(vec), digits = 2),
               " (", round(quantile(vec)[2], digits = 2), ", ",
               round(quantile(vec)[4], digits = 2), ")\n", sep = "")
  return(out)
}

#----------------------------------------------------------------------------------------------------#
# function: export_descriptives
#     Exports general descriptive statistics.
# param:  data.ss
#     the simulation summary data
#----------------------------------------------------------------------------------------------------#
export_descriptives <- function(data.ss = load_simulation_summary_data()) {

  # observations
  obs <- nrow(load_simulation_summary_data(remove_exclusions = FALSE))
  out <- paste(" observations: ", obs, "\n", sep = "")
  out <- paste(out, " exclusions:   ", obs - nrow(data.ss), "\n", sep = "")

  # data
  out <- paste(out, get_descriptive(data.ss$nb.r.sigma.av, "av. risk perception"))
  out <- paste(out, get_descriptive(data.ss$net.degree.pre.epidemic.av, "degree (network)"))
  out <- paste(out, get_descriptive(data.ss$net.clustering.pre.epidemic.av, "clustering (network)"))
  out <- paste(out, get_descriptive(data.ss$net.pathlength.pre.epidemic.av, "path length (network)"))
  out <- paste(out, get_descriptive(data.ss$index.degree, "degree (index case)"))
  out <- paste(out, get_descriptive(data.ss$index.clustering, "clustering (index case)"))
  out <- paste(out, get_descriptive(data.ss$index.betweenness.normalized, "betweenness (index case)"))
  out <- paste(out, get_descriptive(data.ss$net.static.ties.broken.epidemic, "ties broken during epidemic (static)"))
  out <- paste(out, get_descriptive(data.ss$net.static.network.changes.epidemic, "network changes during epidemic (static)"))
  out <- paste(out, get_descriptive(data.ss$net.dynamic.ties.broken.epidemic, "ties broken during epidemic (dynamic)"))
  out <- paste(out, get_descriptive(data.ss$net.dynamic.network.changes.epidemic, "network changes during epidemic (dynamic)"))
  out <- paste(out, get_descriptive(data.ss$net.static.pct.rec, "attack rate (static)"))
  out <- paste(out, get_descriptive(data.ss$net.dynamic.pct.rec, "attack rate (dynamic)"))
  out <- paste(out, get_descriptive(data.ss$net.static.epidemic.duration, "duration (static)"))
  out <- paste(out, get_descriptive(data.ss$net.dynamic.epidemic.duration, "duration (dynamic)"))
  out <- paste(out, get_descriptive(data.ss$net.static.epidemic.max.infections, "epidemic max infections (static)"))
  out <- paste(out, get_descriptive(data.ss$net.dynamic.epidemic.max.infections, "epidemic max infections (dynamic)"))
  out <- paste(out, get_descriptive(data.ss$net.static.epidemic.peak, "epidemic peak (static)"))
  out <- paste(out, get_descriptive(data.ss$net.dynamic.epidemic.peak, "epidemic peak (dynamic)"))

  # export to file
  dir.create(EXPORT_PATH_NUM, showWarnings = FALSE)
  cat(out, file = paste(EXPORT_PATH_NUM,
                        "descriptives",
                        EXPORT_FILE_EXTENSION_DESC,
                        sep = ""))
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

#----------------------------------------------------------------------------------------------------#
# function: export_attackrate_models
#     Creates and exports multi-level logistic regression models for attack rate for all conjectures.
# param:  data.ss
#     simulation summary data to produce regression models for
# param:  filenamname.appendix
#     Optional string to append to the standard filename
#----------------------------------------------------------------------------------------------------#
export_attackrate_models <- function(data.ss = load_simulation_summary_data(), filenamname.appendix = "") {

  # main effects
  dyn.ties.broken                       <- meanCenter(data.ss$net.ties.broken.epidemic)               # range: 0 - infinity
  dyn.net.changes                       <- meanCenter(data.ss$net.network.changes.epidemic)           # range: 0 - infinity
  r.sigma                               <- meanCenter(data.ss$nb.r.sigma.av)                          # range: 0.0 - 2.0
  i.r.neigh                             <- meanCenter(data.ss$index.r.sigma.neighborhood)             # range: 0.0 - 2.0
  sigma                                 <- meanCenter(data.ss$nb.sigma)                               # range: 0.0 - 100.0
  gamma                                 <- meanCenter(data.ss$nb.gamma)                               # range: 0.0 - 1.0
  net.clustering                        <- meanCenter(data.ss$net.clustering.pre.epidemic.av)         # range: 0.0 - 1.0
  net.pathlenth                         <- meanCenter(data.ss$net.pathlength.pre.epidemic.av)         # range: 0.0 - infinity
  i.betw                                <- meanCenter(data.ss$index.betweenness.normalized)           # range: 0.0 - 1.0
  net.assortativity                     <- meanCenter(data.ss$net.assortativity.pre.epidemic)         # range: 0.0 - 1.0

  # interaction effects
  r.sigma.X.sigma                       <- (r.sigma - mean(r.sigma, na.rm=TRUE))                * (sigma - mean(sigma, na.rm=TRUE))
  r.sigma.X.gamma                       <- (r.sigma - mean(r.sigma, na.rm=TRUE))                * (gamma - mean(gamma, na.rm=TRUE))
  i.r.neigh.X.sigma                     <- (i.r.neigh - mean(i.r.neigh, na.rm=TRUE))            * (sigma - mean(sigma, na.rm=TRUE))
  i.r.neigh.X.gamma                     <- (i.r.neigh - mean(i.r.neigh, na.rm=TRUE))            * (gamma - mean(gamma, na.rm=TRUE))
  i.betw.X.i.r.neigh                    <- (i.betw - mean(i.betw, na.rm=TRUE))                  * (i.r.neigh - mean(i.r.neigh, na.rm=TRUE))
  net.clustering.X.net.assortativity    <- (net.clustering - mean(net.clustering, na.rm=TRUE))  * (net.assortativity - mean(net.assortativity, na.rm=TRUE))
  i.r.neigh.X.net.assortativity         <- (i.r.neigh - mean(i.r.neigh, na.rm=TRUE))            * (net.assortativity - mean(net.assortativity, na.rm=TRUE))

  ### 2-LEVEL LOGISTIC REGRESSIONS (attack rate)  ###
  ### level 2: randomized parameters              ###
  ### level 1: simulation iterations              ###
  # null-model
  reg.00   <- glmer(data.ss$net.pct.rec/100 ~
                      1 +
                      (1 | sim.cnt),
                    family = binomial,
                    data = data.ss)
  # model conjecture 1
  net.c1   <- glmer(data.ss$net.pct.rec/100 ~
                      dyn.ties.broken +
                      dyn.net.changes +
                      (1 | sim.cnt),
                    family = binomial,
                    data = data.ss)
  # model conjecture 2
  net.c2   <- glmer(data.ss$net.pct.rec/100 ~
                      r.sigma +
                      i.r.neigh +
                      sigma +
                      gamma +
                      r.sigma.X.sigma +
                      r.sigma.X.gamma +
                      i.r.neigh.X.sigma +
                      i.r.neigh.X.gamma +
                      (1 | sim.cnt),
                    family = binomial,
                    data = data.ss)
  # model conjecture 3
  net.c3   <- glmer(data.ss$net.pct.rec/100 ~
                      net.clustering +
                      i.r.neigh +
                      net.pathlenth +
                      i.betw +
                      i.betw.X.i.r.neigh +
                      (1 | sim.cnt),
                    family = binomial,
                    data = data.ss)
  # model conjecture 4
  net.c4   <- glmer(data.ss$net.pct.rec/100 ~
                      net.assortativity +
                      net.clustering +
                      net.clustering.X.net.assortativity +
                      i.r.neigh +
                      i.r.neigh.X.net.assortativity +
                      (1 | sim.cnt),
                    family = binomial,
                    data = data.ss)
  # model all combined
  net.all  <- glmer(data.ss$net.pct.rec/100 ~
                      dyn.ties.broken +
                      dyn.net.changes +
                      r.sigma +
                      i.r.neigh +
                      sigma +
                      gamma +
                      net.clustering +
                      net.pathlenth +
                      i.betw +
                      net.assortativity +
                      r.sigma.X.sigma +
                      r.sigma.X.gamma +
                      i.r.neigh.X.sigma +
                      i.r.neigh.X.gamma +
                      i.betw.X.i.r.neigh +
                      net.clustering.X.net.assortativity +
                      i.r.neigh.X.net.assortativity +
                      (1 | sim.cnt),
                    family = binomial,
                    data = data.ss)

  ### FILE EXPORT ###
  filename <- "reg-attackrate"
  if (filenamname.appendix != "") {
    filename <- paste(filename, "-", filenamname.appendix, sep = "")
  }
  exportModels(list(reg.00,
                    net.c1,
                    net.c2,
                    net.c3,
                    net.c4,
                    net.all), filename)
}

#----------------------------------------------------------------------------------------------------#
# function: export_attackrate_models_composition
#     Composition of all regression models for attack rates.
# param:  data.ss
#     simulation summary data to produce regression models for
#----------------------------------------------------------------------------------------------------#
export_attackrate_models_composition <- function(data.ss = load_simulation_summary_data()) {

  # dynamic
  data.ss$net.epidemic.duration <- data.ss$net.dynamic.epidemic.duration
  data.ss$net.ties.broken.epidemic <- data.ss$net.dynamic.ties.broken.epidemic
  data.ss$net.network.changes.epidemic <- data.ss$net.dynamic.network.changes.epidemic
  data.ss$net.pct.rec <- data.ss$net.dynamic.pct.rec
  print("########## attack rate (dynamic)                         ##########")
  export_attackrate_models(data.ss, "dynamic")

  # static
  data.ss$net.epidemic.duration <- data.ss$net.static.epidemic.duration
  data.ss$net.ties.broken.epidemic <- data.ss$net.static.ties.broken.epidemic
  data.ss$net.network.changes.epidemic <- data.ss$net.static.network.changes.epidemic
  data.ss$net.pct.rec <- data.ss$net.static.pct.rec
  print("########## attack rate (static)                          ##########")
  export_attackrate_models(data.ss, "static")
}


#----------------------------------------------------------------------------------------------------#
# function: export_duration_models
#     Creates and exports multi-level logistic regression models for epidemic duration for all
#     conjectures.
# param:  data.ss
#     simulation summary data to produce regression models for
# param:  filenamname.appendix
#     Optional string to append to the standard filename
#----------------------------------------------------------------------------------------------------#
export_duration_models <- function(data.ss = load_simulation_summary_data(), filenamname.appendix = "") {

  # main effects
  dyn.ties.broken                       <- meanCenter(data.ss$net.ties.broken.epidemic)               # range: 0 - infinity
  dyn.net.changes                       <- meanCenter(data.ss$net.network.changes.epidemic)           # range: 0 - infinity
  r.sigma                               <- meanCenter(data.ss$nb.r.sigma.av)                          # range: 0.0 - 2.0
  i.r.neigh                             <- meanCenter(data.ss$index.r.sigma.neighborhood)             # range: 0.0 - 2.0
  sigma                                 <- meanCenter(data.ss$nb.sigma)                               # range: 0.0 - 100.0
  gamma                                 <- meanCenter(data.ss$nb.gamma)                               # range: 0.0 - 1.0
  net.clustering                        <- meanCenter(data.ss$net.clustering.pre.epidemic.av)         # range: 0.0 - 1.0
  net.pathlenth                         <- meanCenter(data.ss$net.pathlength.pre.epidemic.av)         # range: 0.0 - infinity
  i.betw                                <- meanCenter(data.ss$index.betweenness.normalized)           # range: 0.0 - 1.0
  net.assortativity                     <- meanCenter(data.ss$net.assortativity.pre.epidemic)         # range: 0.0 - 1.0

  # interaction effects
  r.sigma.X.sigma                       <- (r.sigma - mean(r.sigma, na.rm=TRUE))                * (sigma - mean(sigma, na.rm=TRUE))
  r.sigma.X.gamma                       <- (r.sigma - mean(r.sigma, na.rm=TRUE))                * (gamma - mean(gamma, na.rm=TRUE))
  i.r.neigh.X.sigma                     <- (i.r.neigh - mean(i.r.neigh, na.rm=TRUE))            * (sigma - mean(sigma, na.rm=TRUE))
  i.r.neigh.X.gamma                     <- (i.r.neigh - mean(i.r.neigh, na.rm=TRUE))            * (gamma - mean(gamma, na.rm=TRUE))
  i.betw.X.i.r.neigh                    <- (i.betw - mean(i.betw, na.rm=TRUE))                  * (i.r.neigh - mean(i.r.neigh, na.rm=TRUE))
  net.clustering.X.net.assortativity    <- (net.clustering - mean(net.clustering, na.rm=TRUE))  * (net.assortativity - mean(net.assortativity, na.rm=TRUE))
  i.r.neigh.X.net.assortativity         <- (i.r.neigh - mean(i.r.neigh, na.rm=TRUE))            * (net.assortativity - mean(net.assortativity, na.rm=TRUE))

  ### 2-LEVEL LOGISTIC REGRESSIONS (attack rate)  ###
  ### level 2: randomized parameters              ###
  ### level 1: simulation iterations              ###
  # null-model
  reg.00   <- lmer(data.ss$net.epidemic.duration ~
                     1 +
                     (1 | sim.cnt),
                   data = data.ss,
                   REML = FALSE)
  # model conjecture 1
  net.c1   <- lmer(data.ss$net.epidemic.duration ~
                     dyn.ties.broken +
                     dyn.net.changes +
                     (1 | sim.cnt),
                   data = data.ss,
                   REML = FALSE)
  # model conjecture 2
  net.c2   <- lmer(data.ss$net.epidemic.duration ~
                     r.sigma +
                     i.r.neigh +
                     sigma +
                     gamma +
                     r.sigma.X.sigma +
                     r.sigma.X.gamma +
                     i.r.neigh.X.sigma +
                     i.r.neigh.X.gamma +
                     (1 | sim.cnt),
                   data = data.ss,
                   REML = FALSE)
  # model conjecture 3
  net.c3   <- lmer(data.ss$net.epidemic.duration ~
                     net.clustering +
                     i.r.neigh +
                     net.pathlenth +
                     i.betw +
                     i.betw.X.i.r.neigh +
                     (1 | sim.cnt),
                   data = data.ss,
                   REML = FALSE)
  # model conjecture 4
  net.c4   <- lmer(data.ss$net.epidemic.duration ~
                     net.assortativity +
                     net.clustering +
                     net.clustering.X.net.assortativity +
                     i.r.neigh +
                     i.r.neigh.X.net.assortativity +
                     (1 | sim.cnt),
                   data = data.ss,
                   REML = FALSE)
  # model all combined
  net.all  <- lmer(data.ss$net.epidemic.duration ~
                     dyn.ties.broken +
                     dyn.net.changes +
                     r.sigma +
                     i.r.neigh +
                     sigma +
                     gamma +
                     net.clustering +
                     net.pathlenth +
                     i.betw +
                     net.assortativity +
                     r.sigma.X.sigma +
                     r.sigma.X.gamma +
                     i.r.neigh.X.sigma +
                     i.r.neigh.X.gamma +
                     i.betw.X.i.r.neigh +
                     net.clustering.X.net.assortativity +
                     i.r.neigh.X.net.assortativity +
                     (1 | sim.cnt),
                   data = data.ss,
                   REML = FALSE)

  ### FILE EXPORT ###
  filename <- "reg-duration"
  if (filenamname.appendix != "") {
    filename <- paste(filename, "-", filenamname.appendix, sep = "")
  }
  exportModels(list(reg.00,
                    net.c1,
                    net.c2,
                    net.c3,
                    net.c4,
                    net.all), filename)
}

#----------------------------------------------------------------------------------------------------#
# function: export_duration_models_composition
#     Exports all regression models for epidemic durations.
# param:  data.ss
#     the simulation summary data
#----------------------------------------------------------------------------------------------------#
export_duration_models_composition <- function(data.ss = load_simulation_summary_data()) {

  # dynamic
  data.ss$net.epidemic.duration <- data.ss$net.dynamic.epidemic.duration
  data.ss$net.ties.broken.epidemic <- data.ss$net.dynamic.ties.broken.epidemic
  data.ss$net.network.changes.epidemic <- data.ss$net.dynamic.network.changes.epidemic
  data.ss$net.pct.rec <- data.ss$net.dynamic.pct.rec
  print("########## epidemic duration (dynamic, all)              ##########")
  export_duration_models(data.ss, "dynamic-all")
  print("########## epidemic duration (dynamic, 25)               ##########")
  export_duration_models(subset(data.ss, net.pct.rec <= 25), "dynamic-0.25")
  print("########## epidemic duration (dynamic, 50)               ##########")
  export_duration_models(subset(data.ss, net.pct.rec > 25 & net.pct.rec <= 50), "dynamic-0.50")
  print("########## epidemic duration (dynamic, 75)               ##########")
  export_duration_models(subset(data.ss, net.pct.rec > 50 & net.pct.rec <= 75), "dynamic-0.75")
  print("########## epidemic duration (dynamic, 100)              ##########")
  export_duration_models(subset(data.ss, net.pct.rec > 75), "dynamic-1.00")

  # static
  data.ss$net.epidemic.duration <- data.ss$net.static.epidemic.duration
  data.ss$net.ties.broken.epidemic <- data.ss$net.static.ties.broken.epidemic
  data.ss$net.network.changes.epidemic <- data.ss$net.static.network.changes.epidemic
  data.ss$net.pct.rec <- data.ss$net.static.pct.rec
  print("########## epidemic duration (static, all)               ##########")
  export_duration_models(data.ss, "static-all")
  print("########## epidemic duration (static, 25)                ##########")
  export_duration_models(subset(data.ss, net.pct.rec <= 25), "static-0.25")
  print("########## epidemic duration (static, 50)                ##########")
  export_duration_models(subset(data.ss, net.pct.rec > 25 & net.pct.rec <= 50), "static-0.50")
  print("########## epidemic duration (static, 75)                ##########")
  export_duration_models(subset(data.ss, net.pct.rec > 50 & net.pct.rec <= 75), "static-0.75")
  print("########## epidemic duration (static, 100)               ##########")
  export_duration_models(subset(data.ss, net.pct.rec > 75), "static-1.00")
}

#----------------------------------------------------------------------------------------------------#
# function: export_peak_models
#     Creates and exports multi-level logistic regression models for epidemic peak for all
#     conjectures.
# param:  data.ss
#     simulation summary data to produce regression models for
# param:  filenamname.appendix
#     Optional string to append to the standard filename
#----------------------------------------------------------------------------------------------------#
export_peak_models <- function(data.ss = load_simulation_summary_data(), filenamname.appendix = "") {

  # main effects
  dyn.ties.broken                       <- meanCenter(data.ss$net.ties.broken.epidemic)               # range: 0 - infinity
  dyn.net.changes                       <- meanCenter(data.ss$net.network.changes.epidemic)           # range: 0 - infinity
  r.sigma                               <- meanCenter(data.ss$nb.r.sigma.av)                          # range: 0.0 - 2.0
  i.r.neigh                             <- meanCenter(data.ss$index.r.sigma.neighborhood)             # range: 0.0 - 2.0
  sigma                                 <- meanCenter(data.ss$nb.sigma)                               # range: 0.0 - 100.0
  gamma                                 <- meanCenter(data.ss$nb.gamma)                               # range: 0.0 - 1.0
  net.clustering                        <- meanCenter(data.ss$net.clustering.pre.epidemic.av)         # range: 0.0 - 1.0
  net.pathlenth                         <- meanCenter(data.ss$net.pathlength.pre.epidemic.av)         # range: 0.0 - infinity
  i.betw                                <- meanCenter(data.ss$index.betweenness.normalized)           # range: 0.0 - 1.0
  net.assortativity                     <- meanCenter(data.ss$net.assortativity.pre.epidemic)         # range: 0.0 - 1.0

  # interaction effects
  r.sigma.X.sigma                       <- (r.sigma - mean(r.sigma, na.rm=TRUE))                * (sigma - mean(sigma, na.rm=TRUE))
  r.sigma.X.gamma                       <- (r.sigma - mean(r.sigma, na.rm=TRUE))                * (gamma - mean(gamma, na.rm=TRUE))
  i.r.neigh.X.sigma                     <- (i.r.neigh - mean(i.r.neigh, na.rm=TRUE))            * (sigma - mean(sigma, na.rm=TRUE))
  i.r.neigh.X.gamma                     <- (i.r.neigh - mean(i.r.neigh, na.rm=TRUE))            * (gamma - mean(gamma, na.rm=TRUE))
  i.betw.X.i.r.neigh                    <- (i.betw - mean(i.betw, na.rm=TRUE))                  * (i.r.neigh - mean(i.r.neigh, na.rm=TRUE))
  net.clustering.X.net.assortativity    <- (net.clustering - mean(net.clustering, na.rm=TRUE))  * (net.assortativity - mean(net.assortativity, na.rm=TRUE))
  i.r.neigh.X.net.assortativity         <- (i.r.neigh - mean(i.r.neigh, na.rm=TRUE))            * (net.assortativity - mean(net.assortativity, na.rm=TRUE))

  ### 2-LEVEL LOGISTIC REGRESSIONS (attack rate)  ###
  ### level 2: randomized parameters              ###
  ### level 1: simulation iterations              ###
  # null-model
  reg.00   <- lmer(data.ss$net.epidemic.peak ~
                     1 +
                     (1 | sim.cnt),
                   data = data.ss,
                   REML = FALSE)
  # model conjecture 1
  net.c1   <- lmer(data.ss$net.epidemic.peak ~
                     dyn.ties.broken +
                     dyn.net.changes +
                     (1 | sim.cnt),
                   data = data.ss,
                   REML = FALSE)
  # model conjecture 2
  net.c2   <- lmer(data.ss$net.epidemic.peak ~
                     r.sigma +
                     i.r.neigh +
                     sigma +
                     gamma +
                     r.sigma.X.sigma +
                     r.sigma.X.gamma +
                     i.r.neigh.X.sigma +
                     i.r.neigh.X.gamma +
                     (1 | sim.cnt),
                   data = data.ss,
                   REML = FALSE)
  # model conjecture 3
  net.c3   <- lmer(data.ss$net.epidemic.peak ~
                     net.clustering +
                     i.r.neigh +
                     net.pathlenth +
                     i.betw +
                     i.betw.X.i.r.neigh +
                     (1 | sim.cnt),
                   data = data.ss,
                   REML = FALSE)
  # model conjecture 4
  net.c4   <- lmer(data.ss$net.epidemic.peak ~
                     net.assortativity +
                     net.clustering +
                     net.clustering.X.net.assortativity +
                     i.r.neigh +
                     i.r.neigh.X.net.assortativity +
                     (1 | sim.cnt),
                   data = data.ss,
                   REML = FALSE)
  # model all combined
  net.all  <- lmer(data.ss$net.epidemic.peak ~
                     dyn.ties.broken +
                     dyn.net.changes +
                     r.sigma +
                     i.r.neigh +
                     sigma +
                     gamma +
                     net.clustering +
                     net.pathlenth +
                     i.betw +
                     net.assortativity +
                     r.sigma.X.sigma +
                     r.sigma.X.gamma +
                     i.r.neigh.X.sigma +
                     i.r.neigh.X.gamma +
                     i.betw.X.i.r.neigh +
                     net.clustering.X.net.assortativity +
                     i.r.neigh.X.net.assortativity +
                     (1 | sim.cnt),
                   data = data.ss,
                   REML = FALSE)

  ### FILE EXPORT ###
  filename <- "reg-peak"
  if (filenamname.appendix != "") {
    filename <- paste(filename, "-", filenamname.appendix, sep = "")
  }
  exportModels(list(reg.00,
                    net.c1,
                    net.c2,
                    net.c3,
                    net.c4,
                    net.all), filename)
}

#----------------------------------------------------------------------------------------------------#
# function: export_peak_models_composition
#     Exports all regression models for epidemic peaks
# param:  data.ss
#     the simulation summary data
#----------------------------------------------------------------------------------------------------#
export_peak_models_composition <- function(data.ss = load_simulation_summary_data()) {

  # dynamic
  data.ss$net.epidemic.peak <- data.ss$net.dynamic.epidemic.peak
  data.ss$net.ties.broken.epidemic <- data.ss$net.dynamic.ties.broken.epidemic
  data.ss$net.network.changes.epidemic <- data.ss$net.dynamic.network.changes.epidemic
  data.ss$net.pct.rec <- data.ss$net.dynamic.pct.rec
  print("########## epidemic peak (dynamic, all)                  ##########")
  export_peak_models(data.ss, "dynamic-all")
  print("########## epidemic peak (dynamic, 25)                   ##########")
  export_peak_models(subset(data.ss, net.pct.rec <= 25), "dynamic-0.25")
  print("########## epidemic peak (dynamic, 50)                   ##########")
  export_peak_models(subset(data.ss, net.pct.rec > 25 & net.pct.rec <= 50), "dynamic-0.50")
  print("########## epidemic peak (dynamic, 75)                   ##########")
  export_peak_models(subset(data.ss, net.pct.rec > 50 & net.pct.rec <= 75), "dynamic-0.75")
  print("########## epidemic peak (dynamic, 100)                  ##########")
  export_peak_models(subset(data.ss, net.pct.rec > 75), "dynamic-1.00")

  # static
  data.ss$net.epidemic.peak <- data.ss$net.static.epidemic.peak
  data.ss$net.ties.broken.epidemic <- data.ss$net.static.ties.broken.epidemic
  data.ss$net.network.changes.epidemic <- data.ss$net.static.network.changes.epidemic
  data.ss$net.pct.rec <- data.ss$net.static.pct.rec
  print("########## epidemic peak (static, all)                   ##########")
  export_peak_models(data.ss, "static-all")
  print("########## epidemic peak (static, 25)                    ##########")
  export_peak_models(subset(data.ss, net.pct.rec <= 25), "static-0.25")
  print("########## epidemic peak (static, 50)                    ##########")
  export_peak_models(subset(data.ss, net.pct.rec > 25 & net.pct.rec <= 50), "static-0.50")
  print("########## epidemic peak (static, 75)                    ##########")
  export_peak_models(subset(data.ss, net.pct.rec > 50 & net.pct.rec <= 75), "static-0.75")
  print("########## epidemic peak (static, 100)                   ##########")
  export_peak_models(subset(data.ss, net.pct.rec > 75), "static-1.00")
}

#----------------------------------------------------------------------------------------------------#
# function: export_agent_regression_models
#     Exports a single regression model for agent data
# param:  data.ss
#     the simulation summary data
# param:  data.ad
#     the agent details data
# param:  filename.appendix
#     optional string to append to the name of the exported file
#----------------------------------------------------------------------------------------------------#
export_agent_regression_models <- function(data.ss = load_simulation_summary_data(),
                                           data.ad = load_agent_details_data(),
                                           filenamname.appendix = "") {

  # main effects
  ties.broken                           <- meanCenter(data.ad$agent.cons.broken.active)               # range: 0.0 - infinity
  ties.rewired                          <- meanCenter(data.ad$agent.cons.out.accepted)                # range: 0.0 - infinity
  r.sigma                               <- meanCenter(data.ad$nb.r.sigma)                             # range: 0.0 - 2.0
  r.sigma.neigh                         <- meanCenter(data.ad$agent.neighborhood.r.sigma.av)          # range: 0.0 - 2.0
  i.r.sigma.neigh                       <- meanCenter(data.ad$agent.index.neighborhood.r.sigma.av)    # range: 0.0 - 2.0
  sigma                                 <- meanCenter(data.ad$nb.sigma)                               # range: 0.0 - 100.0
  gamma                                 <- meanCenter(data.ad$nb.gamma)                               # range: 0.0 - 1.0
  agent.clustering                      <- meanCenter(data.ad$agent.clustering)                       # range: 0.0 - 1.0
  net.clustering                        <- meanCenter(data.ad$net.clustering.av)                      # range: 0.0 - 1.0
  pathlength                            <- meanCenter(data.ad$net.pathlength.av)                      # range: 0.0 - infinity
  betweenness                           <- meanCenter(data.ad$agent.betweenness.normalized)           # range: 0.0 - 1.0
  r.sigma.neighborhood                  <- meanCenter(data.ad$agent.neighborhood.r.sigma.av)          # range: 0.0 - 2.0
  assortativity                         <- meanCenter(data.ad$net.assortativity)                      # range: 0.0 - 1.0

  # interaction effects
  r.sigma.X.sigma                       <- (r.sigma - mean(r.sigma, na.rm=TRUE))                  * (sigma - mean(sigma, na.rm=TRUE))
  r.sigma.X.gamma                       <- (r.sigma - mean(r.sigma, na.rm=TRUE))                  * (gamma - mean(gamma, na.rm=TRUE))
  r.sigma.neigh.X.sigma                 <- (r.sigma.neigh - mean(r.sigma.neigh, na.rm=TRUE))      * (sigma - mean(sigma, na.rm=TRUE))
  r.sigma.neigh.X.gamma                 <- (r.sigma.neigh - mean(r.sigma.neigh, na.rm=TRUE))      * (gamma - mean(gamma, na.rm=TRUE))
  i.r.sigma.neigh.X.sigma               <- (i.r.sigma.neigh - mean(i.r.sigma.neigh, na.rm=TRUE))  * (sigma - mean(sigma, na.rm=TRUE))
  i.r.sigma.neigh.X.gamma               <- (i.r.sigma.neigh - mean(i.r.sigma.neigh, na.rm=TRUE))  * (gamma - mean(gamma, na.rm=TRUE))
  assortativity.X.r.sigma               <- (assortativity - mean(assortativity, na.rm=TRUE))      * (r.sigma - mean(r.sigma, na.rm=TRUE))
  assortativity.X.net.clustering        <- (assortativity - mean(assortativity, na.rm=TRUE))      * (net.clustering - mean(net.clustering, na.rm=TRUE))
  assortativity.X.agent.clustering      <- (assortativity - mean(assortativity, na.rm=TRUE))      * (agent.clustering - mean(agent.clustering, na.rm=TRUE))
  assortativity.X.r.sigma.neigh         <- (assortativity - mean(assortativity, na.rm=TRUE))      * (r.sigma.neigh - mean(r.sigma.neigh, na.rm=TRUE))

  ### 2-LEVEL LOGISTIC REGRESSIONS (attack rate)  ###
  ### level 2: randomized parameters              ###
  ### level 1: simulation iterations              ###
  # null-model
  infprob.00   <- glmer(data.ad$agent.infected ~
                          1 +
                          (1 | sim.cnt),
                        family = binomial,
                        data = data.ad)
  # model conjecture 2
  infprob.c2   <- glmer(data.ad$agent.infected ~
                          ties.broken +
                          ties.rewired +
                          r.sigma +
                          r.sigma.neigh +
                          i.r.sigma.neigh +
                          sigma +
                          gamma +
                          r.sigma.X.sigma +
                          r.sigma.X.gamma +
                          r.sigma.neigh.X.sigma +
                          r.sigma.neigh.X.gamma +
                          i.r.sigma.neigh.X.sigma +
                          i.r.sigma.neigh.X.gamma +
                          (1 | sim.cnt),
                        family = binomial,
                        data = data.ad)
  # model conjecture 3
  infprob.c3   <- glmer(data.ad$agent.infected ~
                          agent.clustering +
                          net.clustering +
                          r.sigma.neigh +
                          i.r.sigma.neigh +
                          pathlength +
                          betweenness +
                          (1 | sim.cnt),
                        family = binomial,
                        data = data.ad)
  # model conjecture 4
  infprob.c4   <- glmer(data.ad$agent.infected ~
                          assortativity +
                          r.sigma +
                          net.clustering +
                          agent.clustering +
                          r.sigma.neigh +
                          assortativity.X.r.sigma +
                          assortativity.X.net.clustering +
                          assortativity.X.agent.clustering +
                          assortativity.X.r.sigma.neigh +
                          (1 | sim.cnt),
                        family = binomial,
                        data = data.ad)
  # model all combined
  infprob.all   <- glmer(data.ad$agent.infected ~
                           ties.broken +
                           ties.rewired +
                           r.sigma +
                           r.sigma.neigh +
                           i.r.sigma.neigh +
                           sigma +
                           gamma +
                           agent.clustering +
                           net.clustering +
                           pathlength +
                           betweenness +
                           assortativity +
                           r.sigma.X.sigma +
                           r.sigma.X.gamma +
                           r.sigma.neigh.X.sigma +
                           r.sigma.neigh.X.gamma +
                           i.r.sigma.neigh.X.sigma +
                           i.r.sigma.neigh.X.gamma +
                           assortativity.X.r.sigma +
                           assortativity.X.net.clustering +
                           assortativity.X.agent.clustering +
                           assortativity.X.r.sigma.neigh +
                           (1 | sim.cnt),
                         family = binomial,
                         data = data.ad)

  filename <- "reg-infectionprobability"
  if (filenamname.appendix != "") {
    filename <- paste(filename, "-", filenamname.appendix, sep = "")
  }
  exportModels(list(infprob.00,
                    infprob.c2,
                    infprob.c3,
                    infprob.c4,
                    infprob.all), filename)

}

#----------------------------------------------------------------------------------------------------#
# function: export_agent_regression_models_composition
#     Exports all regression models for agent data
# param:  data.ad
#     the agent details data
# param:  filename.appendix
#     optional string to append to the name of the exported file
#----------------------------------------------------------------------------------------------------#
export_agent_regression_models_composition <- function(data.ad = load_agent_details_data(), filenamname.appendix = "") {
  print("########## agent's probability to get infected (dynamic) ##########")
  export_agent_regression_models(subset(data.ad, nb.ep.structure == "dynamic"), paste(filenamname.appendix, "dynamic", sep = ""))
  print("########## agent's probability to get infected (static)  ##########")
  export_agent_regression_models(subset(data.ad, nb.ep.structure == "static"), paste(filenamname.appendix, "static", sep = ""))
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
plotSIRDevelopment <- function(rsData = reduce_round_summary_data(load_round_summary_data()),
                               showLegend = TRUE,
                               showRibbons = TRUE,
                               showDegree = TRUE,
                               showDensity = FALSE,
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
  if (showDensity) {
    scaleFactor           <- 100
  }
  if (showDegree) {
    scaleFactor           <- 10
  }

  ### NETWORK DENSITY data
  if (showDensity) {
    # preparations :: statistical summary for densities
    summaryDensities    <- as.data.frame(do.call(rbind, with(rsData, tapply(net.density, sim.round, summary))))

    # data for lines :: median
    densityData         <- data.frame(rounds, "Density", summaryDensities$Median * scaleFactor)
    names(densityData)  <- c("Timestep", "Measure", "Frequency")
    plotData            <- rbind(plotData, densityData)

    # data for ribbons :: 1st and 3rd quartile per compartment
    densityRibbonData   <- data.frame(rounds,
                                      summaryDensities$`1st Qu.` * scaleFactor,
                                      summaryDensities$`3rd Qu.` * scaleFactor)
    names(densityRibbonData) <- c("Timestep", "DenMin", "DenMax")
    ribbonData$DenMin   <- densityRibbonData$DenMin[match(ribbonData$Timestep, densityRibbonData$Timestep)]
    ribbonData$DenMax   <- densityRibbonData$DenMax[match(ribbonData$Timestep, densityRibbonData$Timestep)]
  }

  ### DEGREE data
  if (showDegree) {
    # preparations :: statistical summary for degrees
    summaryDegrees      <- as.data.frame(do.call(rbind, with(rsData, tapply(net.degree.av, sim.round, summary))))

    # data for lines :: median
    degreeData          <- data.frame(rounds, "Degree", summaryDegrees$Median * scaleFactor)
    names(degreeData)   <- c("Timestep", "Measure", "Frequency")
    plotData            <- rbind(plotData, degreeData)

    # data for ribbons :: 1st and 3rd quartile per compartment
    degreeRibbonData    <- data.frame(rounds,
                                      summaryDegrees$`1st Qu.` * scaleFactor,
                                      summaryDegrees$`3rd Qu.` * scaleFactor)
    names(degreeRibbonData) <- c("Timestep", "DegMin", "DegMax")
    ribbonData$DegMin   <- degreeRibbonData$DegMin[match(ribbonData$Timestep, degreeRibbonData$Timestep)]
    ribbonData$DegMax   <- degreeRibbonData$DegMax[match(ribbonData$Timestep, degreeRibbonData$Timestep)]
  }

  ### PLOT assembly
  # initializations
  plot <- ggplot(plotData, aes(x = Timestep, y = Frequency, col = Measure))

  # ribbons
  if (showRibbons) {

    # ribbon :: density
    if (showDensity) {
      plot <- plot +
        # density
        geom_ribbon(data = ribbonData,
                    aes(x = Timestep, ymin = DenMin, ymax = DenMax),
                    inherit.aes = FALSE,
                    fill = COLORS["Density"],
                    alpha = RIBBON_ALPHA)
    }

    # ribbon :: degree
    if (showDegree) {
      plot <- plot +
        # average degree
        geom_ribbon(data = ribbonData,
                    aes(x = Timestep, ymin = DegMin, ymax = DegMax),
                    inherit.aes = FALSE,
                    fill = COLORS["Degree"],
                    alpha = RIBBON_ALPHA)
    }
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
    if (showDegree & showDensity) {
      warning("Degree and density are on different scales! Values of second y-axis is adjusted to degree!")
      plot <- plot +
        scale_y_continuous(sec.axis = sec_axis(~./scaleFactor, name = "Degree / density"))
    } else if (showDegree) {
      plot <- plot +
        scale_y_continuous(sec.axis = sec_axis(~./scaleFactor, name = "Degree"))
    } else if (showDensity) {
      plot <- plot +
        scale_y_continuous(sec.axis = sec_axis(~./scaleFactor, name = "Density"))
    }
  }

  return(plot)
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
    geom_point(aes(colour = factor(structure), shape  = factor(structure)), position = position_jitter(), alpha=0.6, show.legend = FALSE) +
    geom_boxplot(alpha = 0.3, show.legend = FALSE) +
    scale_color_manual(values = c("static" = "#D55E00", "dynamic" = "#0072B2")) +
    scale_fill_manual(values = c("static" = "#D55E00", "dynamic" = "#0072B2")) +
    scale_x_discrete(name="Network structure during epidemic") +
    scale_y_continuous(name="Attack rate", limits=c(0, 100)) +
    theme(legend.position = "none") +
    theme_bw(base_size = 16)
  return(p.attackrate)
}

#----------------------------------------------------------------------------------------------------#
# function: plot_c_b
#     Exports plots for conjecture 1.b.
# param:  data.ss
#     the simulation summary data
# param:  ep.structure
#     whether plot for dynamic or static data ought to be created
# return: the plot
#----------------------------------------------------------------------------------------------------#
plot_c1_b <- function(data.ss = load_simulation_summary_data()) {
  # data - all
  d <- data.frame("structure"   = rep("static", nrow(data.ss)),
                  "attack.rate" = rep("combined", nrow(data.ss)),
                  "duration"    = data.ss$net.static.epidemic.duration)
  d <- rbind(d, data.frame("structure"   = rep("dynamic", nrow(data.ss)),
                           "attack.rate" = rep("combined", nrow(data.ss)),
                           "duration"    = data.ss$net.dynamic.epidemic.duration))
  # data - by attack rate
  min_pct <- 0
  step_size_pct <- 25
  for (max_pct in seq(step_size_pct, 100, step_size_pct)) {
    d.static <- subset(data.ss, net.static.pct.rec > min_pct & net.static.pct.rec <= max_pct)
    d <- rbind(d, data.frame("structure"   = rep("static", nrow(d.static)),
                             "attack.rate" = factor(rep(max_pct, nrow(d.static))),
                             "duration"    = d.static$net.static.epidemic.duration))
    d.dynamic <- subset(data.ss, net.dynamic.pct.rec > min_pct & net.dynamic.pct.rec <= max_pct)
    d <- rbind(d, data.frame("structure"   = rep("dynamic", nrow(d.dynamic)),
                             "attack.rate" = factor(rep(max_pct, nrow(d.dynamic))),
                             "duration"    = d.dynamic$net.dynamic.epidemic.duration))
    min_pct <- max_pct
  }

  # plot
  p.duration <- ggplot(d, aes(x = structure, y = duration, fill = attack.rate)) +
    #geom_point(aes(colour = attack.rate, shape = attack.rate), position = position_jitterdodge(), alpha=0.2) +
    geom_boxplot(alpha = 0.7) +
    scale_color_manual(values = c("combined" = "#000000",
                                  "25"       = "#D55E00",
                                  "50"       = "#E69D00",
                                  "75"       = "#0072B2",
                                  "100"      = "#009E73")) +
    scale_fill_manual(values = c("combined"  = "#000000",
                                 "25"        = "#D55E00",
                                 "50"        = "#E69D00",
                                 "75"        = "#0072B2",
                                 "100"       = "#009E73")) +
    scale_x_discrete(name="Network structure during epidemic") +
    scale_y_continuous(name="Duration", limits=c(0, 30)) +
    theme_bw(base_size = 16) +
    guides(fill=guide_legend(title="Attack rate"))
  # theme(legend.position = "top")
  return(p.duration)
}

#----------------------------------------------------------------------------------------------------#
# function: plot_c2_a
#     Exports plots for conjecture 2.a.
# param:  data.ad
#     the agent details data
# param:  ep.structure
#     whether plot for dynamic or static data ought to be created
# return: the plot
#----------------------------------------------------------------------------------------------------#
plot_c2_a <- function(data.ad = load_agent_details_data(), ep.structure = "dynamic") {

  # data preparations
  data.ad <- subset(data.ad, nb.ep.structure == ep.structure)
  d <- data.frame(r.sigma = numeric(0),
                  ties    = character(0),
                  value   = numeric(0))
  bin.prev <- 0.00
  for (bin in seq(0.01, 2, 0.01)) {
    data.ad.agent.bin <- subset(data.ad, nb.r.sigma > bin.prev & nb.r.sigma <= bin)
    d <- rbind(d,
               data.frame(r.sigma = bin,
                          ties = "broken",
                          value   = mean(data.ad.agent.bin$agent.cons.broken.active.epidemic)))
    data.ad.neigh.bin <- subset(data.ad, agent.neighborhood.r.sigma.av > bin.prev & agent.neighborhood.r.sigma.av <= bin)
    d <- rbind(d,
               data.frame(r.sigma = bin,
                          ties = "created",
                          value   = mean(data.ad.neigh.bin$agent.cons.in.accepted.epidemic +
                                           data.ad.neigh.bin$agent.cons.out.accepted.epidemic)))
    bin.prev <- bin
  }
  d <- subset(d, !is.na(value))
  # plot
  p <- ggplot(d, aes(x = r.sigma, y = value, color = ties, shape = ties)) +
    geom_point(aes(colour = ties, shape = factor(ties)), alpha = 0.7) +
    geom_line(stat="smooth",method = "lm", fullrange=TRUE, size = 1.0, aes(linetype=ties, color=ties)) +
    scale_color_manual(values = c("broken" = "#D55E00",
                                  "created" = "#0072B2")) +
    scale_x_continuous(name="Risk preference", limits = c(0, 2)) +
    scale_y_continuous(name="Network decisions",
                       limits = c(0, max(d$value) + 1),
                       breaks = seq(1, max(d$value) + 1, 1)) +
    theme_bw(base_size = 16) +
    theme(legend.position = "top",
          legend.title = element_blank())
  return(p)
}

#----------------------------------------------------------------------------------------------------#
# function: plot_c2_bc
#     Exports plots for conjectures 2.b. and 2.c.
# param:  data.ad
#     the agent details data
# param:  ep.structure
#     whether plot for dynamic or static data ought to be created
# return: the plot
#----------------------------------------------------------------------------------------------------#
plot_c2_bc <- function(data.ad = load_agent_details_data(), ep.structure = "dynamic") {

  # data preparations
  data.ad <- subset(data.ad, nb.ep.structure == ep.structure)
  d <- data.frame(r.sigma = numeric(0),
                  of.whom = character(0),
                  value   = numeric(0))
  bin.prev <- 0.00
  for (bin in seq(0.01, 2, 0.01)) {
    data.ad.agent.bin <- subset(data.ad, nb.r.sigma > bin.prev & nb.r.sigma <= bin)
    d <- rbind(d,
               data.frame(r.sigma = bin,
                          of.whom = "agent",
                          value   = nrow(subset(data.ad.agent.bin, agent.infected == 1)) / nrow(data.ad.agent.bin)))
    data.ad.neigh.bin <- subset(data.ad, agent.neighborhood.r.sigma.av > bin.prev & agent.neighborhood.r.sigma.av <= bin)
    d <- rbind(d,
               data.frame(r.sigma = bin,
                          of.whom = "neighborhood",
                          value   = nrow(subset(data.ad.neigh.bin, agent.infected == 1)) / nrow(data.ad.neigh.bin)))
    bin.prev <- bin
  }
  d <- subset(d, !is.na(value))
  # plot
  p <- ggplot(d, aes(x = r.sigma, y = value, color = of.whom, shape = of.whom)) +
    geom_point(aes(colour = of.whom, shape = factor(of.whom)), alpha = 0.7) +
    geom_line(stat="smooth",method = "lm", fullrange=TRUE, size = 1.0, aes(linetype=of.whom, color=of.whom)) +
    scale_color_manual(values = c("agent" = "#D55E00",
                                  "neighborhood" = "#0072B2")) +
    scale_x_continuous(name="Risk preference", limits = c(0, 2)) +
    scale_y_continuous(name="Probability of infection", limits=c(0, 1)) +
    theme_bw(base_size = 16) +
    theme(legend.position = "top",
          legend.title = element_blank())
  return(p)
}

#----------------------------------------------------------------------------------------------------#
# function: plot_c2_de
#     Exports plots for conjectures 2.d. and 2.e.
# param:  data.ss
#     the simulation summary data
# param:  ep.structure
#     whether plot for dynamic or static data ought to be created
# return: the plot
#----------------------------------------------------------------------------------------------------#
plot_c2_de <- function(data.ss = load_simulation_summary_data(), ep.structure = "dynamic") {

  # data preparations
  d <- data.frame(r.sigma     = numeric(0),
                  of.whom     = character(0),
                  attack.rate = numeric(0))
  bin.prev <- 0.00
  for (bin in seq(0.01, 2, 0.01)) {
    data.ss.av.bin <- subset(data.ss, nb.r.sigma.av > bin.prev & nb.r.sigma.av <= bin)
    if (nrow(data.ss.av.bin) > 1) {
      if (ep.structure == "dynamic") {
        d <- rbind(d,
                   data.frame(r.sigma     = bin,
                              of.whom     = "network average",
                              attack.rate = mean(data.ss.av.bin$net.dynamic.pct.rec)))
      } else {
        d <- rbind(d,
                   data.frame(r.sigma     = bin,
                              of.whom     = "network average",
                              attack.rate = mean(data.ss.av.bin$net.static.pct.rec)))
      }
    }

    data.ss.neigh.bin <- subset(data.ss, index.r.sigma.neighborhood > bin.prev & index.r.sigma.neighborhood <= bin)
    if (nrow(data.ss.neigh.bin) > 1) {
      if (ep.structure == "dynamic") {
        d <- rbind(d,
                   data.frame(r.sigma     = bin,
                              of.whom     = "index case neighborhood",
                              attack.rate = mean(data.ss.neigh.bin$net.dynamic.pct.rec)))
      } else {
        d <- rbind(d,
                   data.frame(r.sigma     = bin,
                              of.whom     = "index case neighborhood",
                              attack.rate = mean(data.ss.neigh.bin$net.static.pct.rec)))
      }
    }
    bin.prev <- bin
  }
  d <- subset(d, !is.na(attack.rate))
  # plot
  p <- ggplot(d, aes(x = r.sigma, y = attack.rate, color = of.whom, shape = of.whom)) +
    geom_point(aes(colour = of.whom, shape = factor(of.whom)), alpha = 0.7) +
    geom_line(stat="smooth",method = "lm", fullrange=TRUE, size = 1.0, aes(linetype=of.whom, color=of.whom)) +
    scale_color_manual(values = c("network average" = "#D55E00",
                                  "index case neighborhood" = "#0072B2")) +
    scale_x_continuous(name="Risk preference", limits = c(0, 2)) +
    scale_y_continuous(name="Attack rate", limits=c(0, 100)) +
    theme_bw(base_size = 16) +
    theme(legend.position = "top",
          legend.title = element_blank())
  return(p)
}

#----------------------------------------------------------------------------------------------------#
# function: plot_c2_fg_duration
#     Exports plots for conjectures 2.f. and 2.g (epidemic duration).
# param:  data.ss
#     the simulation summary data
# param:  ep.structure
#     whether plot for dynamic or static data ought to be created
# return: the plot
#----------------------------------------------------------------------------------------------------#
plot_c2_fg_duration <- function(data.ss = load_simulation_summary_data(), ep.structure = "dynamic") {

  # data preparations
  d <- data.frame(r.sigma     = numeric(0),
                  attack.rate = character(0),
                  duration    = numeric(0))
  bin.prev <- 0.00
  for (bin in seq(0.01, 2, 0.01)) {
    data.ss.av.bin <- subset(data.ss, nb.r.sigma.av > bin.prev & nb.r.sigma.av <= bin)
    if (nrow(data.ss.av.bin) > 1) {
      # attack rate: all
      if (ep.structure == "dynamic") {
        d <- rbind(d,
                   data.frame(r.sigma     = bin,
                              attack.rate = "all",
                              duration    = mean(data.ss.av.bin$net.dynamic.epidemic.duration)))
      } else {
        d <- rbind(d,
                   data.frame(r.sigma     = bin,
                              attack.rate = "all",
                              duration    = mean(data.ss.av.bin$net.static.epidemic.duration)))
      }
      # by attack rate
      min_pct <- 0
      step_size_pct <- 25
      for (max_pct in seq(step_size_pct, 100, step_size_pct)) {
        if (ep.structure == "dynamic") {
          data.ss.av.bin.by.attackrate <- subset(data.ss.av.bin,
                                                 net.dynamic.pct.rec > min_pct & net.dynamic.pct.rec <= max_pct)
          d <- rbind(d,
                     data.frame(r.sigma     = bin,
                                attack.rate = factor(max_pct),
                                duration    = mean(data.ss.av.bin.by.attackrate$net.dynamic.epidemic.duration)))
        } else {
          data.ss.av.bin.by.attackrate <- subset(data.ss.av.bin,
                                                 net.static.pct.rec > min_pct & net.static.pct.rec <= max_pct)
          d <- rbind(d,
                     data.frame(r.sigma     = bin,
                                attack.rate = factor(max_pct),
                                duration    = mean(data.ss.av.bin.by.attackrate$net.static.epidemic.duration)))
        }
        min_pct <- max_pct
      }
    }
    bin.prev <- bin
  }
  d <- subset(d, !is.na(duration))
  # plot
  p <- ggplot(d, aes(x = r.sigma, y = duration, color = attack.rate, shape = attack.rate)) +
    geom_point(aes(colour = attack.rate, shape = factor(attack.rate)), alpha = 0.7) +
    geom_line(stat="smooth",method = "lm", fullrange=TRUE, size = 1.0, aes(linetype=attack.rate, color=attack.rate)) +
    scale_color_manual(values = c("all" = "#000000",
                                  "25"       = "#D55E00",
                                  "50"       = "#E69D00",
                                  "75"       = "#0072B2",
                                  "100"      = "#009E73")) +
    scale_fill_manual(values = c("all"  = "#000000",
                                 "25"        = "#D55E00",
                                 "50"        = "#E69D00",
                                 "75"        = "#0072B2",
                                 "100"       = "#009E73")) +
    scale_x_continuous(name="Risk preference", limits = c(0, 2)) +
    scale_y_continuous(name="Duration", limits=c(0, max(d$duration))) +
    theme_bw(base_size = 16) +
    theme(legend.position = "top",
          legend.title = element_blank())
  return(p)
}

#----------------------------------------------------------------------------------------------------#
# function: plot_c2_fg_peak
#     Exports plots for conjectures 2.f. and 2.g (epidemic peak).
# param:  data.ss
#     the simulation summary data
# param:  ep.structure
#     whether plot for dynamic or static data ought to be created
# return: the plot
#----------------------------------------------------------------------------------------------------#
plot_c2_fg_peak <- function(data.ss = load_simulation_summary_data(), ep.structure = "dynamic") {

  # data preparations
  d <- data.frame(r.sigma     = numeric(0),
                  attack.rate = character(0),
                  peak        = numeric(0))
  bin.prev <- 0.00
  for (bin in seq(0.01, 2, 0.01)) {
    data.ss.av.bin <- subset(data.ss, nb.r.sigma.av > bin.prev & nb.r.sigma.av <= bin)
    if (nrow(data.ss.av.bin) > 1) {
      # attack rate: all
      if (ep.structure == "dynamic") {
        d <- rbind(d,
                   data.frame(r.sigma     = bin,
                              attack.rate = "all",
                              peak        = mean(data.ss.av.bin$net.dynamic.epidemic.peak)))
      } else {
        d <- rbind(d,
                   data.frame(r.sigma     = bin,
                              attack.rate = "all",
                              peak        = mean(data.ss.av.bin$net.static.epidemic.peak)))
      }
      # by attack rate
      min_pct <- 0
      step_size_pct <- 25
      for (max_pct in seq(step_size_pct, 100, step_size_pct)) {
        if (ep.structure == "dynamic") {
          data.ss.av.bin.by.attackrate <- subset(data.ss.av.bin,
                                                 net.dynamic.pct.rec > min_pct & net.dynamic.pct.rec <= max_pct)
          d <- rbind(d,
                     data.frame(r.sigma     = bin,
                                attack.rate = factor(max_pct),
                                peak    = mean(data.ss.av.bin.by.attackrate$net.dynamic.epidemic.peak)))
        } else {
          data.ss.av.bin.by.attackrate <- subset(data.ss.av.bin,
                                                 net.static.pct.rec > min_pct & net.static.pct.rec <= max_pct)
          d <- rbind(d,
                     data.frame(r.sigma     = bin,
                                attack.rate = factor(max_pct),
                                peak    = mean(data.ss.av.bin.by.attackrate$net.static.epidemic.peak)))
        }
        min_pct <- max_pct
      }
    }
    bin.prev <- bin
  }
  d <- subset(d, !is.na(peak))
  # plot
  p <- ggplot(d, aes(x = r.sigma, y = peak, color = attack.rate, shape = attack.rate)) +
    geom_point(aes(colour = attack.rate, shape = factor(attack.rate)), alpha = 0.7) +
    geom_line(stat="smooth",method = "lm", fullrange=TRUE, size = 1.0, aes(linetype=attack.rate, color=attack.rate)) +
    scale_color_manual(values = c("all" = "#000000",
                                  "25"       = "#D55E00",
                                  "50"       = "#E69D00",
                                  "75"       = "#0072B2",
                                  "100"      = "#009E73")) +
    scale_fill_manual(values = c("all"  = "#000000",
                                 "25"        = "#D55E00",
                                 "50"        = "#E69D00",
                                 "75"        = "#0072B2",
                                 "100"       = "#009E73")) +
    scale_x_continuous(name="Risk preference", limits = c(0, 2)) +
    scale_y_continuous(name="Epidemic peak", limits=c(0, max(d$peak))) +
    theme_bw(base_size = 16) +
    theme(legend.position = "top",
          legend.title = element_blank())
  return(p)
}

#----------------------------------------------------------------------------------------------------#
# function: plot_c3_ab
#     Exports plots for conjecture 3.a. and 3.b.
# param:  data.ad
#     the agent details data
# param:  ep.structure
#     whether plot for dynamic or static data ought to be created
# return: the plot
#----------------------------------------------------------------------------------------------------#
plot_c3_ab <- function(data.ad = load_agent_details_data(), ep.structure = "dynamic") {

  # data preparations
  data.ad <- subset(data.ad, nb.ep.structure == ep.structure)
  d <- data.frame(clustering = numeric(0),
                  of.whom    = character(0),
                  value      = numeric(0))
  bin.min  <- min(min(data.ad$agent.clustering), min(data.ad$net.clustering.av))
  bin.max  <- max(max(data.ad$agent.clustering), max(data.ad$net.clustering.av))
  bin.prev <- 0.00
  for (bin in seq(bin.min, bin.max, 0.01)) {
    data.ad.agent.bin <- subset(data.ad, agent.clustering > bin.prev & agent.clustering <= bin)
    d <- rbind(d,
               data.frame(clustering = bin,
                          of.whom    = "agent",
                          value      = nrow(subset(data.ad.agent.bin, agent.infected == 1)) / nrow(data.ad.agent.bin)))
    data.ad.net.bin <- subset(data.ad, net.clustering.av > bin.prev & net.clustering.av <= bin)
    d <- rbind(d,
               data.frame(clustering = bin,
                          of.whom    = "network",
                          value      = nrow(subset(data.ad.net.bin, agent.infected == 1)) / nrow(data.ad.net.bin)))
    bin.prev <- bin
  }
  d <- subset(d, !is.na(value))
  # plot
  p <- ggplot(d, aes(x = clustering, y = value, color = of.whom, shape = of.whom)) +
    geom_point(aes(colour = of.whom, shape = factor(of.whom)), alpha = 0.7) +
    geom_line(stat="smooth",method = "lm", fullrange=TRUE, size = 1.0, aes(linetype=of.whom, color=of.whom)) +
    scale_color_manual(values = c("agent" = "#D55E00",
                                  "network" = "#0072B2")) +
    scale_x_continuous(name="Clustering", limits = c(0, 1)) +
    scale_y_continuous(name="Probability of infection", limits=c(0, 1)) +
    theme_bw(base_size = 16) +
    theme(legend.position = "top",
          legend.title = element_blank())
  return(p)
}

#----------------------------------------------------------------------------------------------------#
# function: plot_c3_h
#     Exports plots for conjecture 3.h.
# param:  data.ad
#     the agent details data
# param:  ep.structure
#     whether plot for dynamic or static data ought to be created
# return: the plot
#----------------------------------------------------------------------------------------------------#
plot_c3_h <- function(data.ad = load_agent_details_data(), ep.structure = "dynamic") {

  # data preparations
  data.ad <- subset(data.ad, nb.ep.structure == ep.structure)
  d <- data.frame(av.path.length = numeric(0),
                  value          = numeric(0))
  bin.min  <- min(data.ad$net.pathlength.av)
  bin.max  <- max(data.ad$net.pathlength.av)
  bin.prev <- 0.00
  for (bin in seq(bin.min, bin.max, 0.01)) {
    data.ad.agent.bin <- subset(data.ad, net.pathlength.av > bin.prev & net.pathlength.av <= bin)
    d <- rbind(d,
               data.frame(av.path.length = bin,
                          value          = nrow(subset(data.ad.agent.bin, agent.infected == 1)) / nrow(data.ad.agent.bin)))
    bin.prev <- bin
  }
  d <- subset(d, !is.na(value))
  # plot
  p <- ggplot(d, aes(x = av.path.length, y = value)) +
    geom_point(alpha = 0.7, color = "#D55E00") +
    geom_line(stat="smooth",method = "lm", fullrange=TRUE, size = 1.0, color = "#D55E00") +
    scale_x_continuous(name="Average path length", limits = c(bin.min, bin.max)) +
    scale_y_continuous(name="Probability of infection", limits=c(0, 1)) +
    theme_bw(base_size = 16) +
    theme(legend.position = "top",
          legend.title = element_blank())
  return(p)
}

plot_c3_l <- function(data.ad = load_agent_details_data(), ep.structure = "dynamic") {

  # data preparations
  data.ad <- subset(data.ad, nb.ep.structure == ep.structure)
  d <- data.frame(betweenness = numeric(0),
                  value       = numeric(0))
  bin.min  <- min(data.ad$agent.betweenness.normalized)
  bin.max  <- max(data.ad$agent.betweenness.normalized)
  bin.prev <- 0.00
  for (bin in seq(bin.min, bin.max, 0.01)) {
    data.ad.agent.bin <- subset(data.ad, agent.betweenness.normalized > bin.prev & agent.betweenness.normalized <= bin)
    d <- rbind(d,
               data.frame(betweenness = bin,
                          value       = nrow(subset(data.ad.agent.bin, agent.infected == 1)) / nrow(data.ad.agent.bin)))
    bin.prev <- bin
  }
  d <- subset(d, !is.na(value))
  # plot
  p <- ggplot(d, aes(x = betweenness, y = value)) +
    geom_point(alpha = 0.7, color = "#D55E00") +
    geom_line(stat="smooth",method = "lm", fullrange=TRUE, size = 1.0, color = "#D55E00") +
    scale_x_continuous(name="Betweenness", limits = c(bin.min, bin.max)) +
    scale_y_continuous(name="Probability of infection", limits=c(0, 1)) +
    theme_bw(base_size = 16) +
    theme(legend.position = "top",
          legend.title = element_blank())
  return(p)
}

#----------------------------------------------------------------------------------------------------#
# function: plot_c3_d
#     Exports plots for conjecture 3.d.
# param:  data.ss
#     the simulation summary data
# param:  ep.structure
#     whether plot for dynamic or static data ought to be created
# return: the plot
#----------------------------------------------------------------------------------------------------#
plot_c3_d <- function(data.ss = load_simulation_summary_data(), ep.structure = "dynamic") {

  # data preparations
  d <- data.frame(clustering  = numeric(0),
                  of.whom     = character(0),
                  attack.rate = numeric(0))
  bin.min  <- min(min(data.ss$net.clustering.pre.epidemic.av), min(data.ss$index.clustering))
  bin.max  <- max(max(data.ss$net.clustering.pre.epidemic.av), max(data.ss$index.clustering))
  bin.prev <- 0.00
  for (bin in seq(bin.min, bin.max, 0.01)) {
    data.ss.av.bin <- subset(data.ss, net.clustering.pre.epidemic.av > bin.prev & net.clustering.pre.epidemic.av <= bin)
    if (nrow(data.ss.av.bin) > 1) {
      if (ep.structure == "dynamic") {
        d <- rbind(d,
                   data.frame(clustering  = bin,
                              of.whom     = "network average",
                              attack.rate = mean(data.ss.av.bin$net.dynamic.pct.rec)))
      } else {
        d <- rbind(d,
                   data.frame(clustering  = bin,
                              of.whom     = "network average",
                              attack.rate = mean(data.ss.av.bin$net.static.pct.rec)))
      }
    }
  }

  data.ss.index.bin <- subset(data.ss, index.clustering > bin.prev & index.clustering <= bin)
  if (nrow(data.ss.index.bin) > 1) {
    if (ep.structure == "dynamic") {
      d <- rbind(d,
                 data.frame(clustering  = bin,
                            of.whom     = "index case",
                            attack.rate = mean(data.ss.index.bin$net.dynamic.pct.rec)))
    }
    bin.prev <- bin
  }
  d <- subset(d, !is.na(attack.rate))
  # plot
  p <- ggplot(d, aes(x = clustering, y = attack.rate, color = of.whom, shape = of.whom)) +
    geom_point(aes(colour = of.whom, shape = factor(of.whom)), alpha = 0.7) +
    geom_line(stat="smooth",method = "lm", fullrange=TRUE, size = 1.0, aes(linetype=of.whom, color=of.whom)) +
    scale_color_manual(values = c("network average" = "#D55E00",
                                  "index case" = "#0072B2")) +
    scale_x_continuous(name="Clustering", limits = c(0, 1)) +
    scale_y_continuous(name="Attack rate", limits=c(1, 100)) +
    theme_bw(base_size = 16) +
    theme(legend.position = "top",
          legend.title = element_blank())
  return(p)
}

#----------------------------------------------------------------------------------------------------#
# function: plot_c3_i
#     Exports plots for conjecture 3.i.
# param:  data.ss
#     the simulation summary data
# param:  ep.structure
#     whether plot for dynamic or static data ought to be created
# return: the plot
#----------------------------------------------------------------------------------------------------#
plot_c3_i <- function(data.ss = load_simulation_summary_data(), ep.structure = "dynamic") {

  # data preparations
  d <- data.frame(av.path.length  = numeric(0),
                  attack.rate     = numeric(0))
  bin.min  <- min(data.ss$net.pathlength.pre.epidemic.av)
  bin.max  <- max(data.ss$net.pathlength.pre.epidemic.av)
  bin.prev <- 0.00
  for (bin in seq(bin.min, bin.max, 0.01)) {
    data.ss.av.bin <- subset(data.ss, net.pathlength.pre.epidemic.av > bin.prev & net.pathlength.pre.epidemic.av <= bin)
    if (nrow(data.ss.av.bin) > 1) {
      if (ep.structure == "dynamic") {
        d <- rbind(d,
                   data.frame(av.path.length  = bin,
                              attack.rate     = mean(data.ss.av.bin$net.dynamic.pct.rec)))
      } else {
        d <- rbind(d,
                   data.frame(av.path.length  = bin,
                              attack.rate     = mean(data.ss.av.bin$net.static.pct.rec)))

      }
    }
  }
  d <- subset(d, !is.na(attack.rate))
  # plot
  p <- ggplot(d, aes(x = av.path.length, y = attack.rate)) +
    geom_point(alpha = 0.7, color = "#D55E00") +
    geom_line(stat="smooth",method = "lm", fullrange=TRUE, size = 1.0, color = "#D55E00") +
    scale_x_continuous(name="Average path length", limits = c(bin.min, bin.max)) +
    scale_y_continuous(name="Attack rate", limits=c(1, 100)) +
    theme_bw(base_size = 16)
  return(p)
}

plot_c3_m <- function(data.ss = load_simulation_summary_data(), ep.structure = "dynamic") {

  # data preparations
  d <- data.frame(betweenness.index  = numeric(0),
                  attack.rate        = numeric(0))
  bin.min  <- min(data.ss$index.betweenness.normalized)
  bin.max  <- max(data.ss$index.betweenness.normalized)
  bin.prev <- 0.00
  for (bin in seq(bin.min, bin.max, 0.01)) {
    data.ss.av.bin <- subset(data.ss, index.betweenness.normalized > bin.prev & index.betweenness.normalized <= bin)
    if (nrow(data.ss.av.bin) > 1) {
      if (ep.structure == "dynamic") {
        d <- rbind(d,
                   data.frame(betweenness.index = bin,
                              attack.rate       = mean(data.ss.av.bin$net.dynamic.pct.rec)))
      } else {
        d <- rbind(d,
                   data.frame(betweenness.index = bin,
                              attack.rate       = mean(data.ss.av.bin$net.static.pct.rec)))

      }
    }
  }
  d <- subset(d, !is.na(attack.rate))
  # plot
  p <- ggplot(d, aes(x = betweenness.index, y = attack.rate)) +
    geom_point(alpha = 0.7, color = "#D55E00") +
    geom_line(stat="smooth",method = "lm", fullrange=TRUE, size = 1.0, color = "#D55E00") +
    scale_x_continuous(name="Betweenness (index case)", limits = c(bin.min, bin.max)) +
    scale_y_continuous(name="Attack rate", limits=c(1, 100)) +
    theme_bw(base_size = 16)
  return(p)
}

#----------------------------------------------------------------------------------------------------#
# function: plot_c3_fg_duration
#     Exports plots for conjectures 3.f. and 3.g. (epidemic duration).
# param:  data.ss
#     the simulation summary data
# param:  ep.structure
#     whether plot for dynamic or static data ought to be created
# return: the plot
#----------------------------------------------------------------------------------------------------#
plot_c3_fg_duration <- function(data.ss = load_simulation_summary_data(), ep.structure = "dynamic") {

  # data preparations
  d <- data.frame(clustering  = numeric(0),
                  attack.rate = character(0),
                  duration    = numeric(0))
  bin.min  <- min(data.ss$net.clustering.pre.epidemic.av)
  bin.max  <- max(data.ss$net.clustering.pre.epidemic.av)
  bin.prev <- 0.00
  for (bin in seq(bin.min, bin.max, 0.01)) {
    data.ss.av.bin <- subset(data.ss, net.clustering.pre.epidemic.av > bin.prev & net.clustering.pre.epidemic.av <= bin)
    if (nrow(data.ss.av.bin) > 1) {
      # attack rate: all
      if (ep.structure == "dynamic") {
        d <- rbind(d,
                   data.frame(clustering  = bin,
                              attack.rate = "all",
                              duration    = mean(data.ss.av.bin$net.dynamic.epidemic.duration)))
      } else {
        d <- rbind(d,
                   data.frame(clustering  = bin,
                              attack.rate = "all",
                              duration    = mean(data.ss.av.bin$net.static.epidemic.duration)))
      }
      # by attack rate
      min_pct <- 0
      step_size_pct <- 25
      for (max_pct in seq(step_size_pct, 100, step_size_pct)) {
        if (ep.structure == "dynamic") {
          data.ss.av.bin.by.attackrate <- subset(data.ss.av.bin,
                                                 net.dynamic.pct.rec > min_pct & net.dynamic.pct.rec <= max_pct)
          d <- rbind(d,
                     data.frame(clustering  = bin,
                                attack.rate = factor(max_pct),
                                duration    = mean(data.ss.av.bin.by.attackrate$net.dynamic.epidemic.duration)))
        } else {
          data.ss.av.bin.by.attackrate <- subset(data.ss.av.bin,
                                                 net.static.pct.rec > min_pct & net.static.pct.rec <= max_pct)
          d <- rbind(d,
                     data.frame(clustering  = bin,
                                attack.rate = factor(max_pct),
                                duration    = mean(data.ss.av.bin.by.attackrate$net.static.epidemic.duration)))
        }
        min_pct <- max_pct
      }
    }
    bin.prev <- bin
  }
  d <- subset(d, !is.na(duration))
  # plot
  p <- ggplot(d, aes(x = clustering, y = duration, color = attack.rate, shape = attack.rate)) +
    geom_point(aes(colour = attack.rate, shape = factor(attack.rate)), alpha = 0.7) +
    geom_line(stat="smooth",method = "lm", fullrange=TRUE, size = 1.0, aes(linetype=attack.rate, color=attack.rate)) +
    scale_color_manual(values = c("all" = "#000000",
                                  "25"       = "#D55E00",
                                  "50"       = "#E69D00",
                                  "75"       = "#0072B2",
                                  "100"      = "#009E73")) +
    scale_fill_manual(values = c("all"  = "#000000",
                                 "25"        = "#D55E00",
                                 "50"        = "#E69D00",
                                 "75"        = "#0072B2",
                                 "100"       = "#009E73")) +
    scale_x_continuous(name="Clustering", limits = c(min(d$clustering), max(d$clustering))) +
    scale_y_continuous(name="Duration", limits=c(0, max(d$duration))) +
    theme_bw(base_size = 16) +
    theme(legend.position = "top",
          legend.title = element_blank())
  return(p)
}

#----------------------------------------------------------------------------------------------------#
# function: plot_c3_jk_peak
#     Exports plots for conjectures 3.f. and 3.g. (epidemic peak).
# param:  data.ss
#     the simulation summary data
# param:  ep.structure
#     whether plot for dynamic or static data ought to be created
# return: the plot
#----------------------------------------------------------------------------------------------------#
plot_c3_fg_peak <- function(data.ss = load_simulation_summary_data(), ep.structure = "dynamic") {

  # data preparations
  d <- data.frame(clustering  = numeric(0),
                  attack.rate = character(0),
                  peak        = numeric(0))
  bin.min  <- min(data.ss$net.clustering.pre.epidemic.av)
  bin.max  <- max(data.ss$net.clustering.pre.epidemic.av)
  bin.prev <- 0.00
  for (bin in seq(bin.min, bin.max, 0.01)) {
    data.ss.av.bin <- subset(data.ss, net.clustering.pre.epidemic.av > bin.prev & net.clustering.pre.epidemic.av <= bin)
    if (nrow(data.ss.av.bin) > 1) {
      # attack rate: all
      if (ep.structure == "dynamic") {
        d <- rbind(d,
                   data.frame(clustering  = bin,
                              attack.rate = "all",
                              peak        = mean(data.ss.av.bin$net.dynamic.epidemic.peak)))
      } else {
        d <- rbind(d,
                   data.frame(clustering  = bin,
                              attack.rate = "all",
                              peak        = mean(data.ss.av.bin$net.static.epidemic.peak)))
      }
      # by attack rate
      min_pct <- 0
      step_size_pct <- 25
      for (max_pct in seq(step_size_pct, 100, step_size_pct)) {
        if (ep.structure == "dynamic") {
          data.ss.av.bin.by.attackrate <- subset(data.ss.av.bin,
                                                 net.dynamic.pct.rec > min_pct & net.dynamic.pct.rec <= max_pct)
          d <- rbind(d,
                     data.frame(clustering  = bin,
                                attack.rate = factor(max_pct),
                                peak        = mean(data.ss.av.bin.by.attackrate$net.dynamic.epidemic.peak)))
        } else {
          data.ss.av.bin.by.attackrate <- subset(data.ss.av.bin,
                                                 net.static.pct.rec > min_pct & net.static.pct.rec <= max_pct)
          d <- rbind(d,
                     data.frame(clustering  = bin,
                                attack.rate = factor(max_pct),
                                peak        = mean(data.ss.av.bin.by.attackrate$net.static.epidemic.peak)))
        }
        min_pct <- max_pct
      }
    }
    bin.prev <- bin
  }
  d <- subset(d, !is.na(d$peak))
  d <- subset(d, peak > 0)
  # plot
  p <- ggplot(d, aes(x = clustering, y = peak, color = attack.rate, shape = attack.rate)) +
    geom_point(aes(colour = attack.rate, shape = factor(attack.rate)), alpha = 0.7) +
    geom_line(stat="smooth",method = "lm", fullrange=TRUE, size = 1.0, aes(linetype=attack.rate, color=attack.rate)) +
    scale_color_manual(values = c("all" = "#000000",
                                  "25"       = "#D55E00",
                                  "50"       = "#E69D00",
                                  "75"       = "#0072B2",
                                  "100"      = "#009E73")) +
    scale_fill_manual(values = c("all"  = "#000000",
                                 "25"        = "#D55E00",
                                 "50"        = "#E69D00",
                                 "75"        = "#0072B2",
                                 "100"       = "#009E73")) +
    scale_x_continuous(name="Clustering", limits = c(min(d$clustering), max(d$clustering))) +
    scale_y_continuous(name="Epidemic peak", limits=c(0, max(d$peak))) +
    theme_bw(base_size = 16) +
    theme(legend.position = "top",
          legend.title = element_blank())
  return(p)
}

#----------------------------------------------------------------------------------------------------#
# function: plot_c3_jk_duration
#     Exports plots for conjectures 3.j. and 3.k. (epidemic duration).
# param:  data.ss
#     the simulation summary data
# param:  ep.structure
#     whether plot for dynamic or static data ought to be created
# return: the plot
#----------------------------------------------------------------------------------------------------#
plot_c3_jk_duration <- function(data.ss = load_simulation_summary_data(), ep.structure = "dynamic") {

  # data preparations
  d <- data.frame(path.length = numeric(0),
                  attack.rate = character(0),
                  duration    = numeric(0))
  bin.min  <- min(data.ss$net.pathlength.pre.epidemic.av)
  bin.max  <- max(data.ss$net.pathlength.pre.epidemic.av)
  bin.prev <- 0.00
  for (bin in seq(bin.min, bin.max, 0.01)) {
    data.ss.av.bin <- subset(data.ss, net.pathlength.pre.epidemic.av > bin.prev & net.pathlength.pre.epidemic.av <= bin)
    if (nrow(data.ss.av.bin) > 1) {
      # attack rate: all
      if (ep.structure == "dynamic") {
        d <- rbind(d,
                   data.frame(path.length = bin,
                              attack.rate = "all",
                              duration    = mean(data.ss.av.bin$net.dynamic.epidemic.duration)))
      } else {
        d <- rbind(d,
                   data.frame(path.length = bin,
                              attack.rate = "all",
                              duration    = mean(data.ss.av.bin$net.static.epidemic.duration)))
      }
      # by attack rate
      min_pct <- 0
      step_size_pct <- 25
      for (max_pct in seq(step_size_pct, 100, step_size_pct)) {
        if (ep.structure == "dynamic") {
          data.ss.av.bin.by.attackrate <- subset(data.ss.av.bin,
                                                 net.dynamic.pct.rec > min_pct & net.dynamic.pct.rec <= max_pct)
          d <- rbind(d,
                     data.frame(path.length = bin,
                                attack.rate = factor(max_pct),
                                duration    = mean(data.ss.av.bin.by.attackrate$net.dynamic.epidemic.duration)))
        } else {
          data.ss.av.bin.by.attackrate <- subset(data.ss.av.bin,
                                                 net.static.pct.rec > min_pct & net.static.pct.rec <= max_pct)
          d <- rbind(d,
                     data.frame(path.length = bin,
                                attack.rate = factor(max_pct),
                                duration    = mean(data.ss.av.bin.by.attackrate$net.static.epidemic.duration)))
        }
        min_pct <- max_pct
      }
    }
    bin.prev <- bin
  }
  d <- subset(d, !is.na(duration))
  # plot
  p <- ggplot(d, aes(x = path.length, y = duration, color = attack.rate, shape = attack.rate)) +
    geom_point(aes(colour = attack.rate, shape = factor(attack.rate)), alpha = 0.7) +
    geom_line(stat="smooth",method = "lm", fullrange=TRUE, size = 1.0, aes(linetype=attack.rate, color=attack.rate)) +
    scale_color_manual(values = c("all" = "#000000",
                                  "25"       = "#D55E00",
                                  "50"       = "#E69D00",
                                  "75"       = "#0072B2",
                                  "100"      = "#009E73")) +
    scale_fill_manual(values = c("all"  = "#000000",
                                 "25"        = "#D55E00",
                                 "50"        = "#E69D00",
                                 "75"        = "#0072B2",
                                 "100"       = "#009E73")) +
    scale_x_continuous(name="Average path length", limits = c(min(d$path.length), max(d$path.length))) +
    scale_y_continuous(name="Duration", limits=c(0, max(d$duration))) +
    theme_bw(base_size = 16) +
    theme(legend.position = "top",
          legend.title = element_blank())
  return(p)
}

#----------------------------------------------------------------------------------------------------#
# function: plot_c3_jk_peak
#     Exports plots for conjectures 3.j. and 3.k. (epidemic peak).
# param:  data.ss
#     the simulation summary data
# param:  ep.structure
#     whether plot for dynamic or static data ought to be created
# return: the plot
#----------------------------------------------------------------------------------------------------#
plot_c3_jk_peak <- function(data.ss = load_simulation_summary_data(), ep.structure = "dynamic") {

  # data preparations
  d <- data.frame(path.length = numeric(0),
                  attack.rate = character(0),
                  peak        = numeric(0))
  bin.min  <- min(data.ss$net.pathlength.pre.epidemic.av)
  bin.max  <- max(data.ss$net.pathlength.pre.epidemic.av)
  bin.prev <- 0.00
  for (bin in seq(bin.min, bin.max, 0.01)) {
    data.ss.av.bin <- subset(data.ss, net.pathlength.pre.epidemic.av > bin.prev & net.pathlength.pre.epidemic.av <= bin)
    if (nrow(data.ss.av.bin) > 1) {
      # attack rate: all
      if (ep.structure == "dynamic") {
        d <- rbind(d,
                   data.frame(path.length = bin,
                              attack.rate = "all",
                              peak        = mean(data.ss.av.bin$net.dynamic.epidemic.peak)))
      } else {
        d <- rbind(d,
                   data.frame(path.length = bin,
                              attack.rate = "all",
                              peak        = mean(data.ss.av.bin$net.static.epidemic.peak)))
      }
      # by attack rate
      min_pct <- 0
      step_size_pct <- 25
      for (max_pct in seq(step_size_pct, 100, step_size_pct)) {
        if (ep.structure == "dynamic") {
          data.ss.av.bin.by.attackrate <- subset(data.ss.av.bin,
                                                 net.dynamic.pct.rec > min_pct & net.dynamic.pct.rec <= max_pct)
          d <- rbind(d,
                     data.frame(path.length = bin,
                                attack.rate = factor(max_pct),
                                peak        = mean(data.ss.av.bin.by.attackrate$net.dynamic.epidemic.peak)))
        } else {
          data.ss.av.bin.by.attackrate <- subset(data.ss.av.bin,
                                                 net.static.pct.rec > min_pct & net.static.pct.rec <= max_pct)
          d <- rbind(d,
                     data.frame(path.length = bin,
                                attack.rate = factor(max_pct),
                                peak        = mean(data.ss.av.bin.by.attackrate$net.static.epidemic.peak)))
        }
        min_pct <- max_pct
      }
    }
    bin.prev <- bin
  }
  d <- subset(d, !is.na(peak))
  # plot
  p <- ggplot(d, aes(x = path.length, y = peak, color = attack.rate, shape = attack.rate)) +
    geom_point(aes(colour = attack.rate, shape = factor(attack.rate)), alpha = 0.7) +
    geom_line(stat="smooth",method = "lm", fullrange=TRUE, size = 1.0, aes(linetype=attack.rate, color=attack.rate)) +
    scale_color_manual(values = c("all" = "#000000",
                                  "25"       = "#D55E00",
                                  "50"       = "#E69D00",
                                  "75"       = "#0072B2",
                                  "100"      = "#009E73")) +
    scale_fill_manual(values = c("all"  = "#000000",
                                 "25"        = "#D55E00",
                                 "50"        = "#E69D00",
                                 "75"        = "#0072B2",
                                 "100"       = "#009E73")) +
    scale_x_continuous(name="Average path length", limits = c(min(d$path.length), max(d$path.length))) +
    scale_y_continuous(name="Epidemic peak", limits=c(0, max(d$peak))) +
    theme_bw(base_size = 16) +
    theme(legend.position = "top",
          legend.title = element_blank())
  return(p)
}

#----------------------------------------------------------------------------------------------------#
# function: plot_c3_n_duration
#     Exports plots for conjecture 3.n. (epidemic duration).
# param:  data.ss
#     the simulation summary data
# param:  ep.structure
#     whether plot for dynamic or static data ought to be created
# return: the plot
#----------------------------------------------------------------------------------------------------#
plot_c3_n_duration <- function(data.ss = load_simulation_summary_data(), ep.structure = "dynamic") {

  # data preparations
  d <- data.frame(betweenness.index = numeric(0),
                  attack.rate       = character(0),
                  duration          = numeric(0))
  bin.min  <- min(data.ss$index.betweenness.normalized)
  bin.max  <- max(data.ss$index.betweenness.normalized)
  bin.prev <- 0.00
  for (bin in seq(bin.min, bin.max, 0.01)) {
    data.ss.av.bin <- subset(data.ss, index.betweenness.normalized > bin.prev & index.betweenness.normalized <= bin)
    if (nrow(data.ss.av.bin) > 1) {
      # attack rate: all
      if (ep.structure == "dynamic") {
        d <- rbind(d,
                   data.frame(betweenness.index = bin,
                              attack.rate       = "all",
                              duration          = mean(data.ss.av.bin$net.static.epidemic.duration)))
      } else {
        d <- rbind(d,
                   data.frame(betweenness.index = bin,
                              attack.rate       = "all",
                              duration          = mean(data.ss.av.bin$net.static.epidemic.duration)))
      }
      # by attack rate
      min_pct <- 0
      step_size_pct <- 25
      for (max_pct in seq(step_size_pct, 100, step_size_pct)) {
        if (ep.structure == "dynamic") {
          data.ss.av.bin.by.attackrate <- subset(data.ss.av.bin,
                                                 net.dynamic.pct.rec > min_pct & net.dynamic.pct.rec <= max_pct)
          d <- rbind(d,
                     data.frame(betweenness.index = bin,
                                attack.rate       = factor(max_pct),
                                duration          = mean(data.ss.av.bin.by.attackrate$net.dynamic.epidemic.duration)))
        } else {
          data.ss.av.bin.by.attackrate <- subset(data.ss.av.bin,
                                                 net.static.pct.rec > min_pct & net.static.pct.rec <= max_pct)
          d <- rbind(d,
                     data.frame(betweenness.index = bin,
                                attack.rate       = factor(max_pct),
                                duration          = mean(data.ss.av.bin.by.attackrate$net.static.epidemic.duration)))

        }
        min_pct <- max_pct
      }
    }
    bin.prev <- bin
  }
  d <- subset(d, !is.na(duration))
  # plot
  p <- ggplot(d, aes(x = betweenness.index, y = duration, color = attack.rate, shape = attack.rate)) +
    geom_point(aes(colour = attack.rate, shape = factor(attack.rate)), alpha = 0.7) +
    geom_line(stat="smooth",method = "lm", fullrange=TRUE, size = 1.0, aes(linetype=attack.rate, color=attack.rate)) +
    scale_color_manual(values = c("all" = "#000000",
                                  "25"       = "#D55E00",
                                  "50"       = "#E69D00",
                                  "75"       = "#0072B2",
                                  "100"      = "#009E73")) +
    scale_fill_manual(values = c("all"  = "#000000",
                                 "25"        = "#D55E00",
                                 "50"        = "#E69D00",
                                 "75"        = "#0072B2",
                                 "100"       = "#009E73")) +
    scale_x_continuous(name="Betweenness (index case)", limits = c(min(d$betweenness.index), max(d$betweenness.index))) +
    scale_y_continuous(name="Duration", limits=c(0, max(d$duration))) +
    theme_bw(base_size = 16) +
    theme(legend.position = "top",
          legend.title = element_blank())
  return(p)
}

#----------------------------------------------------------------------------------------------------#
# function: plot_c3_n_peak
#     Exports plots for conjecture 3.n. (epidemic peak).
# param:  data.ss
#     the simulation summary data
# param:  ep.structure
#     whether plot for dynamic or static data ought to be created
# return: the plot
#----------------------------------------------------------------------------------------------------#
plot_c3_n_peak <- function(data.ss = load_simulation_summary_data(), ep.structure = "dynamic") {

  # data preparations
  d <- data.frame(betweenness.index = numeric(0),
                  attack.rate       = character(0),
                  peak              = numeric(0))
  bin.min  <- min(data.ss$index.betweenness.normalized)
  bin.max  <- max(data.ss$index.betweenness.normalized)
  bin.prev <- 0.00
  for (bin in seq(bin.min, bin.max, 0.01)) {
    data.ss.av.bin <- subset(data.ss, index.betweenness.normalized > bin.prev & index.betweenness.normalized <= bin)
    if (nrow(data.ss.av.bin) > 1) {
      # attack rate: all
      if (ep.structure == "dynamic") {
        d <- rbind(d,
                   data.frame(betweenness.index = bin,
                              attack.rate       = "all",
                              peak              = mean(data.ss.av.bin$net.dynamic.epidemic.peak)))
      } else {
        d <- rbind(d,
                   data.frame(betweenness.index = bin,
                              attack.rate       = "all",
                              peak              = mean(data.ss.av.bin$net.static.epidemic.peak)))
      }
      # by attack rate
      min_pct <- 0
      step_size_pct <- 25
      for (max_pct in seq(step_size_pct, 100, step_size_pct)) {
        if (ep.structure == "dynamic") {
          data.ss.av.bin.by.attackrate <- subset(data.ss.av.bin,
                                                 net.dynamic.pct.rec > min_pct & net.dynamic.pct.rec <= max_pct)
          d <- rbind(d,
                     data.frame(betweenness.index = bin,
                                attack.rate       = factor(max_pct),
                                peak              = mean(data.ss.av.bin.by.attackrate$net.dynamic.epidemic.peak)))
        } else {
          data.ss.av.bin.by.attackrate <- subset(data.ss.av.bin,
                                                 net.static.pct.rec > min_pct & net.static.pct.rec <= max_pct)
          d <- rbind(d,
                     data.frame(betweenness.index = bin,
                                attack.rate       = factor(max_pct),
                                peak              = mean(data.ss.av.bin.by.attackrate$net.static.epidemic.peak)))
        }
        min_pct <- max_pct
      }
    }
    bin.prev <- bin
  }
  d <- subset(d, !is.na(peak))
  # plot
  p <- ggplot(d, aes(x = betweenness.index, y = peak, color = attack.rate, shape = attack.rate)) +
    geom_point(aes(colour = attack.rate, shape = factor(attack.rate)), alpha = 0.7) +
    geom_line(stat="smooth",method = "lm", fullrange=TRUE, size = 1.0, aes(linetype=attack.rate, color=attack.rate)) +
    scale_color_manual(values = c("all" = "#000000",
                                  "25"       = "#D55E00",
                                  "50"       = "#E69D00",
                                  "75"       = "#0072B2",
                                  "100"      = "#009E73")) +
    scale_fill_manual(values = c("all"  = "#000000",
                                 "25"        = "#D55E00",
                                 "50"        = "#E69D00",
                                 "75"        = "#0072B2",
                                 "100"       = "#009E73")) +
    scale_x_continuous(name="Betweenness (index case)", limits = c(min(d$betweenness.index), max(d$betweenness.index))) +
    scale_y_continuous(name="Epidemic peak", limits=c(0, max(d$peak))) +
    theme_bw(base_size = 16) +
    theme(legend.position = "top",
          legend.title = element_blank())
  return(p)
}

#----------------------------------------------------------------------------------------------------#
# function: plot_c4_ab
#     Exports plots for conjectures 4.a. and 4.b.
# param:  data.ad
#     the agent details data
# param:  ep.structure
#     whether plot for dynamic or static data ought to be created
# return: the plot
#----------------------------------------------------------------------------------------------------#
plot_c4_ab <- function(data.ad = load_agent_details_data(), ep.structure = "dynamic") {

  # data preparations
  data.ad <- subset(data.ad, nb.ep.structure == ep.structure)
  d <- data.frame(assortativity = numeric(0),
                  r.sigma       = character(0),
                  probability   = numeric(0))
  bin.min  <- min(data.ad$net.assortativity)
  bin.max  <- max(data.ad$net.assortativity)
  bin.prev <- bin.min - 0.01
  for (bin in seq(bin.min, bin.max, 0.01)) {
    data.ad.bin <- subset(data.ad, net.assortativity > bin.prev & net.assortativity <= bin)
    if (nrow(data.ad.bin) > 1) {
      d <- rbind(d,
                 data.frame(assortativity = bin,
                            r.sigma       = "all",
                            probability   = nrow(subset(data.ad.bin, agent.infected == 1)) / nrow(data.ad.bin)))

      # by risk preference
      min_r <- 0
      step_size_r <- 0.5
      for (max_r in seq(step_size_r, 2.00, step_size_r)) {
        data.ad.bin.by.r <- subset(data.ad.bin,
                                   nb.r.sigma > min_r
                                   & nb.r.sigma <= max_r)
        d <- rbind(d,
                   data.frame(assortativity = bin,
                              r.sigma       = factor(max_r),
                              probability   = nrow(subset(data.ad.bin.by.r, agent.infected == 1)) / nrow(data.ad.bin.by.r)))
        min_r <- max_r
      }
    }

    bin.prev <- bin
  }
  d <- subset(d, !is.na(probability))
  # plot
  p <- ggplot(d, aes(x = assortativity, y = probability, color = r.sigma, shape = r.sigma)) +
    geom_point(aes(colour = r.sigma, shape = factor(r.sigma)), alpha = 0.5) +
    geom_line(stat="smooth",method = "lm", fullrange=TRUE, size = 1.0, aes(linetype=r.sigma, color=r.sigma)) +
    scale_color_manual(values = c("all"  = "#000000",
                                  "0.5" = "#D55E00",
                                  "1" = "#E69D00",
                                  "1.5" = "#0072B2",
                                  "2" = "#009E73")) +
    scale_x_continuous(name="Assortativity",
                       limits = c(bin.min, bin.max),
                       breaks = seq(trunc(bin.min*10)/10, ceiling(bin.max*10)/10, 0.2)) +
    scale_y_continuous(name="Probability of infection", limits=c(0, 1)) +
    theme_bw(base_size = 16) +
    theme(legend.position = "top",
          legend.title = element_blank())
  return(p)
}

#----------------------------------------------------------------------------------------------------#
# function: plot_c4_ef
#     Exports plots for conjectures 4.e. and 4.f.
# param:  data.ss
#     the simulation summary data
# param:  ep.structure
#     whether plot for dynamic or static data ought to be created
# return: the plot
#----------------------------------------------------------------------------------------------------#
plot_c4_ef <- function(data.ss = load_simulation_summary_data(), ep.structure = "dynamic") {

  # data preparations
  d <- data.frame(assortativity = numeric(0),
                  clustering    = character(0),
                  attack.rate   = numeric(0))
  bin.min  <- min(data.ss$net.assortativity.pre.epidemic)
  bin.max  <- max(data.ss$net.assortativity.pre.epidemic)
  bin.prev <- bin.min - 0.01
  for (bin in seq(bin.min, bin.max, 0.01)) {
    data.ss.bin <- subset(data.ss, net.assortativity.pre.epidemic > bin.prev & net.assortativity.pre.epidemic <= bin)
    if (nrow(data.ss.bin) > 1) {
      if (ep.structure == "dynamic") {
        d <- rbind(d,
                   data.frame(assortativity = bin,
                              clustering    = "all",
                              attack.rate = mean(data.ss.bin$net.dynamic.pct.rec)))
      } else {
        d <- rbind(d,
                   data.frame(assortativity = bin,
                              clustering    = "all",
                              attack.rate = mean(data.ss.bin$net.static.pct.rec)))
      }

      # by clustering
      min_clustering <- 0
      step_size_clustering <- 0.25
      for (max_clustering in seq(step_size_clustering, 1.00, step_size_clustering)) {
        data.ss.bin.by.clustering <- subset(data.ss.bin,
                                            net.clustering.pre.epidemic.av > min_clustering
                                            & net.clustering.pre.epidemic.av <= max_clustering)
        if (ep.structure == "dynamic") {
          d <- rbind(d,
                     data.frame(assortativity = bin,
                                clustering    = factor(max_clustering),
                                attack.rate   = mean(data.ss.bin.by.clustering$net.static.pct.rec)))
        } else {
          d <- rbind(d,
                     data.frame(assortativity = bin,
                                clustering    = factor(max_clustering),
                                attack.rate   = mean(data.ss.bin.by.clustering$net.static.pct.rec)))
        }
        min_clustering <- max_clustering
      }
    }

    bin.prev <- bin
  }
  d <- subset(d, !is.na(attack.rate))
  # plot
  p <- ggplot(d, aes(x = assortativity, y = attack.rate, color = clustering, shape = clustering)) +
    geom_point(aes(colour = clustering, shape = factor(clustering)), alpha = 0.5) +
    geom_line(stat="smooth",method = "lm", fullrange=TRUE, size = 1.0, aes(linetype=clustering, color=clustering)) +
    scale_color_manual(values = c("all"  = "#000000",
                                  "0.25" = "#D55E00",
                                  "0.5" = "#E69D00",
                                  "0.75" = "#0072B2",
                                  "1" = "#009E73")) +
    scale_x_continuous(name="Assortativity",
                       limits = c(bin.min, bin.max),
                       breaks = seq(trunc(bin.min*10)/10, ceiling(bin.max*10)/10, 0.2)) +
    scale_y_continuous(name="Attack rate", limits=c(0, 100)) +
    theme_bw(base_size = 16) +
    theme(legend.position = "top",
          legend.title = element_blank())
  return(p)
}

#----------------------------------------------------------------------------------------------------#
# function: plot_c4_h_duration
#     Exports plots for conjecture 4.h. (epidemic duration).
# param:  data.ss
#     the simulation summary data
# param:  ep.structure
#     whether plot for dynamic or static data ought to be created
# return: the plot
#----------------------------------------------------------------------------------------------------#
plot_c4_h_duration <- function(data.ss = load_simulation_summary_data(), ep.structure = "dynamic") {

  # data preparations
  d <- data.frame(assortativity = numeric(0),
                  attack.rate   = character(0),
                  duration      = numeric(0))
  bin.min  <- min(data.ss$net.assortativity.pre.epidemic)
  bin.max  <- max(data.ss$net.assortativity.pre.epidemic)
  bin.prev <- bin.min - 0.01
  for (bin in seq(bin.min, bin.max, 0.01)) {
    data.ss.bin <- subset(data.ss, net.assortativity.pre.epidemic > bin.prev & net.assortativity.pre.epidemic <= bin)
    if (nrow(data.ss.bin) > 1) {
      # attack rate: all
      if (ep.structure == "dynamic") {
        d <- rbind(d,
                   data.frame(assortativity = bin,
                              attack.rate   = "all",
                              duration      = mean(data.ss.bin$net.dynamic.epidemic.duration)))
      } else {
        d <- rbind(d,
                   data.frame(assortativity = bin,
                              attack.rate   = "all",
                              duration      = mean(data.ss.bin$net.static.epidemic.duration)))
      }
      # by attack rate
      min_pct <- 0
      step_size_pct <- 25
      for (max_pct in seq(step_size_pct, 100, step_size_pct)) {
        if (ep.structure == "dynamic") {
          data.ss.bin.by.attackrate <- subset(data.ss.bin,
                                              net.dynamic.pct.rec > min_pct & net.dynamic.pct.rec <= max_pct)
          d <- rbind(d,
                     data.frame(assortativity = bin,
                                attack.rate   = factor(max_pct),
                                duration      = mean(data.ss.bin.by.attackrate$net.dynamic.epidemic.duration)))
        } else {
          data.ss.bin.by.attackrate <- subset(data.ss.bin,
                                              net.static.pct.rec > min_pct & net.static.pct.rec <= max_pct)
          d <- rbind(d,
                     data.frame(assortativity = bin,
                                attack.rate   = factor(max_pct),
                                duration      = mean(data.ss.bin.by.attackrate$net.static.epidemic.duration)))

        }
        min_pct <- max_pct
      }
    }
    bin.prev <- bin
  }
  d <- subset(d, !is.na(duration))
  # plot
  p <- ggplot(d, aes(x = assortativity, y = duration, color = attack.rate, shape = attack.rate)) +
    geom_point(aes(colour = attack.rate, shape = factor(attack.rate)), alpha = 0.7) +
    geom_line(stat="smooth",method = "lm", fullrange=TRUE, size = 1.0, aes(linetype=attack.rate, color=attack.rate)) +
    scale_color_manual(values = c("all" = "#000000",
                                  "25"       = "#D55E00",
                                  "50"       = "#E69D00",
                                  "75"       = "#0072B2",
                                  "100"      = "#009E73")) +
    scale_fill_manual(values = c("all"  = "#000000",
                                 "25"        = "#D55E00",
                                 "50"        = "#E69D00",
                                 "75"        = "#0072B2",
                                 "100"       = "#009E73")) +
    scale_x_continuous(name="Assortativity",
                       limits = c(bin.min, bin.max),
                       breaks = seq(trunc(bin.min*10)/10, ceiling(bin.max*10)/10, 0.2)) +
    scale_y_continuous(name="Duration", limits=c(0, max(d$duration))) +
    theme_bw(base_size = 16) +
    theme(legend.position = "top",
          legend.title = element_blank())
  return(p)
}

#----------------------------------------------------------------------------------------------------#
# function: plot_c4_h_peak
#     Exports plots for conjecture 4.h (epidemic peak).
# param:  data.ss
#     the simulation summary data
# param:  ep.structure
#     whether plot for dynamic or static data ought to be created
# return: the plot
#----------------------------------------------------------------------------------------------------#
plot_c4_h_peak <- function(data.ss = load_simulation_summary_data(), ep.structure = "dynamic") {

  # data preparations
  d <- data.frame(assortativity = numeric(0),
                  attack.rate   = character(0),
                  peak          = numeric(0))
  bin.min  <- min(data.ss$net.assortativity.pre.epidemic)
  bin.max  <- max(data.ss$net.assortativity.pre.epidemic)
  bin.prev <- bin.min - 0.01
  for (bin in seq(bin.min, bin.max, 0.01)) {
    data.ss.bin <- subset(data.ss, net.assortativity.pre.epidemic > bin.prev & net.assortativity.pre.epidemic <= bin)
    if (nrow(data.ss.bin) > 1) {
      # attack rate: all
      if (ep.structure == "dynamic") {
        d <- rbind(d,
                   data.frame(assortativity = bin,
                              attack.rate   = "all",
                              peak          = mean(data.ss.bin$net.dynamic.epidemic.peak)))
      } else {
        d <- rbind(d,
                   data.frame(assortativity = bin,
                              attack.rate   = "all",
                              peak          = mean(data.ss.bin$net.static.epidemic.peak)))

      }
      # by attack rate
      min_pct <- 0
      step_size_pct <- 25
      for (max_pct in seq(step_size_pct, 100, step_size_pct)) {
        if (ep.structure == "dynamic") {
          data.ss.bin.by.attackrate <- subset(data.ss.bin,
                                              net.dynamic.pct.rec > min_pct & net.dynamic.pct.rec <= max_pct)
          d <- rbind(d,
                     data.frame(assortativity = bin,
                                attack.rate   = factor(max_pct),
                                peak          = mean(data.ss.bin.by.attackrate$net.dynamic.epidemic.peak)))
        } else {
          data.ss.bin.by.attackrate <- subset(data.ss.bin,
                                              net.static.pct.rec > min_pct & net.static.pct.rec <= max_pct)
          d <- rbind(d,
                     data.frame(assortativity = bin,
                                attack.rate   = factor(max_pct),
                                peak          = mean(data.ss.bin.by.attackrate$net.static.epidemic.peak)))
        }
        min_pct <- max_pct
      }
    }
    bin.prev <- bin
  }
  d <- subset(d, !is.na(peak))
  # plot
  p <- ggplot(d, aes(x = assortativity, y = peak, color = attack.rate, shape = attack.rate)) +
    geom_point(aes(colour = attack.rate, shape = factor(attack.rate)), alpha = 0.7) +
    geom_line(stat="smooth",method = "lm", fullrange=TRUE, size = 1.0, aes(linetype=attack.rate, color=attack.rate)) +
    scale_color_manual(values = c("all" = "#000000",
                                  "25"       = "#D55E00",
                                  "50"       = "#E69D00",
                                  "75"       = "#0072B2",
                                  "100"      = "#009E73")) +
    scale_fill_manual(values = c("all"  = "#000000",
                                 "25"        = "#D55E00",
                                 "50"        = "#E69D00",
                                 "75"        = "#0072B2",
                                 "100"       = "#009E73")) +
    scale_x_continuous(name="Assortativity",
                       limits = c(bin.min, bin.max),
                       breaks = seq(trunc(bin.min*10)/10, ceiling(bin.max*10)/10, 0.2)) +
    scale_y_continuous(name="Peak", limits=c(0, max(d$peak))) +
    theme_bw(base_size = 16) +
    theme(legend.position = "top",
          legend.title = element_blank())
  return(p)
}

#----------------------------------------------------------------------------------------------------#
# function: export_plots
#     Exports all plots generated automatically.
# param:  data.ss
#     the simulation summary data
# param:  data.ad
#     the agent details data
# param:  data.rs
#     the round summary data
#----------------------------------------------------------------------------------------------------#
export_plots <- function(data.ss = load_simulation_summary_data(),
                         data.ad = load_agent_details_data(),
                         data.rs = load_round_summary_data()) {

  # create directory if necessary
  dir.create(EXPORT_PATH_PLOTS, showWarnings = FALSE)

  # conjecture 1 - SIRs
  ggsave(paste(EXPORT_PATH_PLOTS, "c1-sir-static", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
         plotSIRDevelopment(subset(data.rs, nb.ep.structure == "static")),
         width = EXPORT_PLOT_WIDTH,
         height = EXPORT_PLOT_HEIGHT,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)
  ggsave(paste(EXPORT_PATH_PLOTS, "c1-sir-dynamic", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
         plotSIRDevelopment(subset(data.rs, nb.ep.structure == "dynamic")),
         width = EXPORT_PLOT_WIDTH,
         height = EXPORT_PLOT_HEIGHT,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)
  ggsave(paste(EXPORT_PATH_PLOTS, "c1-sir-static-no-degree", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
         plotSIRDevelopment(subset(data.rs, nb.ep.structure == "static"), showDegree = FALSE),
         width = EXPORT_PLOT_WIDTH,
         height = EXPORT_PLOT_HEIGHT,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)
  ggsave(paste(EXPORT_PATH_PLOTS, "c1-sir-dynamic-no-degree", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
         plotSIRDevelopment(subset(data.rs, nb.ep.structure == "dynamic"), showDegree = FALSE),
         width = EXPORT_PLOT_WIDTH,
         height = EXPORT_PLOT_HEIGHT,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)

  # conjecture 1 - attack rate
  ggsave(paste(EXPORT_PATH_PLOTS, "c1-attackrate", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
         plot_c1_a(data.ss),
         width = 150,
         height = EXPORT_PLOT_HEIGHT,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)

  # conjecture 1 - duration
  ggsave(paste(EXPORT_PATH_PLOTS, "c1-duration", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
         plot_c1_b(data.ss),
         width = EXPORT_PLOT_WIDTH,
         height = EXPORT_PLOT_HEIGHT,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)

  # conjecture 2.a. - network decisions - dynamic
  ggsave(paste(EXPORT_PATH_PLOTS, "c2-a-networkdecisions-dynamic", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
         plot_c2_a(data.ad),
         width = EXPORT_PLOT_WIDTH,
         height = EXPORT_PLOT_HEIGHT,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)
  # conjecture 2.a. - network decisions - static
  ggsave(paste(EXPORT_PATH_PLOTS, "c2-a-networkdecisions-static", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
         plot_c2_a(data.ad, ep.structure = "static"),
         width = EXPORT_PLOT_WIDTH,
         height = EXPORT_PLOT_HEIGHT,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)

  # conjecture 2.b.c - probability of infections - dynamic
  ggsave(paste(EXPORT_PATH_PLOTS, "c2-bc-probabilityinfections-dynamic", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
         plot_c2_bc(data.ad),
         width = EXPORT_PLOT_WIDTH,
         height = EXPORT_PLOT_HEIGHT,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)
  # conjecture 2.b.c - probability of infections - static
  ggsave(paste(EXPORT_PATH_PLOTS, "c2-bc-probabilityinfections-static", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
         plot_c2_bc(data.ad, ep.structure = "static"),
         width = EXPORT_PLOT_WIDTH,
         height = EXPORT_PLOT_HEIGHT,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)

  # conjecture 2.d.e - attack rate - dynamic
  ggsave(paste(EXPORT_PATH_PLOTS, "c2-de-attackrate-dynamic", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
         plot_c2_de(data.ss),
         width = EXPORT_PLOT_WIDTH,
         height = EXPORT_PLOT_HEIGHT,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)
  # conjecture 2.d.e - attack rate - static
  ggsave(paste(EXPORT_PATH_PLOTS, "c2-de-attackrate-static", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
         plot_c2_de(data.ss, ep.structure = "static"),
         width = EXPORT_PLOT_WIDTH,
         height = EXPORT_PLOT_HEIGHT,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)

  # conjecture 2.f.g - duration - dynamic
  ggsave(paste(EXPORT_PATH_PLOTS, "c2-fg-duration-dynamic", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
         plot_c2_fg_duration(data.ss),
         width = EXPORT_PLOT_WIDTH,
         height = EXPORT_PLOT_HEIGHT,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)
  # conjecture 2.f.g - duration - static
  ggsave(paste(EXPORT_PATH_PLOTS, "c2-fg-duration-static", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
         plot_c2_fg_duration(data.ss, ep.structure = "static"),
         width = EXPORT_PLOT_WIDTH,
         height = EXPORT_PLOT_HEIGHT,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)

  # conjecture 2.f.g - epidemic peak - dynamic
  ggsave(paste(EXPORT_PATH_PLOTS, "c2-fg-peak-dynamic", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
         plot_c2_fg_peak(data.ss),
         width = EXPORT_PLOT_WIDTH,
         height = EXPORT_PLOT_HEIGHT,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)
  # conjecture 2.f.g - epidemic peak - static
  ggsave(paste(EXPORT_PATH_PLOTS, "c2-fg-peak-static", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
         plot_c2_fg_peak(data.ss, ep.structure = "static"),
         width = EXPORT_PLOT_WIDTH,
         height = EXPORT_PLOT_HEIGHT,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)

  # conjecture 3.a.b. - probability of infection - dynamic
  ggsave(paste(EXPORT_PATH_PLOTS, "c3-ab-probabilityinfections-dynamic", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
         plot_c3_ab(data.ad),
         width = EXPORT_PLOT_WIDTH,
         height = EXPORT_PLOT_HEIGHT,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)
  # conjecture 3.a.b. - probability of infection - static
  ggsave(paste(EXPORT_PATH_PLOTS, "c3-ab-probabilityinfections-static", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
         plot_c3_ab(data.ad, ep.structure = "static"),
         width = EXPORT_PLOT_WIDTH,
         height = EXPORT_PLOT_HEIGHT,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)

  # conjecture 3.d. - attack rate - dynamic
  ggsave(paste(EXPORT_PATH_PLOTS, "c3-d-attackrate-dynamic", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
         plot_c3_d(data.ss),
         width = EXPORT_PLOT_WIDTH,
         height = EXPORT_PLOT_HEIGHT,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)
  # conjecture 3.d. - attack rate - static
  ggsave(paste(EXPORT_PATH_PLOTS, "c3-d-attackrate-static", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
         plot_c3_d(data.ss, ep.structure = "static"),
         width = EXPORT_PLOT_WIDTH,
         height = EXPORT_PLOT_HEIGHT,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)

  # conjecture 3.f.g. - duration - dynamic
  ggsave(paste(EXPORT_PATH_PLOTS, "c3-fg-duration-dynamic", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
         plot_c3_fg_duration(data.ss),
         width = EXPORT_PLOT_WIDTH,
         height = EXPORT_PLOT_HEIGHT,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)
  # conjecture 3.f.g. - duration - static
  ggsave(paste(EXPORT_PATH_PLOTS, "c3-fg-duration-static", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
         plot_c3_fg_duration(data.ss, ep.structure = "static"),
         width = EXPORT_PLOT_WIDTH,
         height = EXPORT_PLOT_HEIGHT,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)

  # conjecture 3.f.g. - peak - dynamic
  ggsave(paste(EXPORT_PATH_PLOTS, "c3-fg-peak-dynamic", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
         plot_c3_fg_peak(data.ss),
         width = EXPORT_PLOT_WIDTH,
         height = EXPORT_PLOT_HEIGHT,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)
  # conjecture 3.f.g. - peak - static
  ggsave(paste(EXPORT_PATH_PLOTS, "c3-fg-peak-static", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
         plot_c3_fg_peak(data.ss, ep.structure = "static"),
         width = EXPORT_PLOT_WIDTH,
         height = EXPORT_PLOT_HEIGHT,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)

  # conjecture 3.h. - probability of infection - dynamic
  ggsave(paste(EXPORT_PATH_PLOTS, "c3-h-probabilityinfections-dynamic", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
         plot_c3_h(data.ad),
         width = EXPORT_PLOT_WIDTH,
         height = EXPORT_PLOT_HEIGHT,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)
  # conjecture 3.h. - probability of infection - static
  ggsave(paste(EXPORT_PATH_PLOTS, "c3-h-probabilityinfections-static", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
         plot_c3_h(data.ad, ep.structure = "static"),
         width = EXPORT_PLOT_WIDTH,
         height = EXPORT_PLOT_HEIGHT,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)

  # conjecture 3.i. - attack rate - dynamic
  ggsave(paste(EXPORT_PATH_PLOTS, "c3-i-attackrate-dynamic", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
         plot_c3_i(data.ss),
         width = EXPORT_PLOT_WIDTH,
         height = EXPORT_PLOT_HEIGHT,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)
  # conjecture 3.i. - attack rate - static
  ggsave(paste(EXPORT_PATH_PLOTS, "c3-i-attackrate-static", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
         plot_c3_i(data.ss, ep.structure = "static"),
         width = EXPORT_PLOT_WIDTH,
         height = EXPORT_PLOT_HEIGHT,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)

  # conjecture 3.j.k. - duration - dynamic
  ggsave(paste(EXPORT_PATH_PLOTS, "c3-jk-duration-dynamic", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
         plot_c3_jk_duration(data.ss),
         width = EXPORT_PLOT_WIDTH,
         height = EXPORT_PLOT_HEIGHT,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)
  # conjecture 3.j.k. - duration - static
  ggsave(paste(EXPORT_PATH_PLOTS, "c3-jk-duration-static", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
         plot_c3_jk_duration(data.ss, ep.structure = "static"),
         width = EXPORT_PLOT_WIDTH,
         height = EXPORT_PLOT_HEIGHT,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)

  # conjecture 3.j.k. - peak - dynamic
  ggsave(paste(EXPORT_PATH_PLOTS, "c3-jk-peak-dynamic", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
         plot_c3_jk_peak(data.ss),
         width = EXPORT_PLOT_WIDTH,
         height = EXPORT_PLOT_HEIGHT,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)
  # conjecture 3.j.k. - peak - static
  ggsave(paste(EXPORT_PATH_PLOTS, "c3-jk-peak-static", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
         plot_c3_jk_peak(data.ss, ep.structure = "static"),
         width = EXPORT_PLOT_WIDTH,
         height = EXPORT_PLOT_HEIGHT,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)

  # conjecture 3.l. - probability of infection - dynamic
  ggsave(paste(EXPORT_PATH_PLOTS, "c3-l-probabilityinfections-dynamic", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
         plot_c3_l(data.ad),
         width = EXPORT_PLOT_WIDTH,
         height = EXPORT_PLOT_HEIGHT,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)
  # conjecture 3.l. - probability of infection - static
  ggsave(paste(EXPORT_PATH_PLOTS, "c3-l-probabilityinfections-static", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
         plot_c3_l(data.ad, ep.structure = "static"),
         width = EXPORT_PLOT_WIDTH,
         height = EXPORT_PLOT_HEIGHT,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)

  # conjecture 3.m. - attack rate - dynamic
  ggsave(paste(EXPORT_PATH_PLOTS, "c3-m-attackrate-dynamic", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
         plot_c3_m(data.ss),
         width = EXPORT_PLOT_WIDTH,
         height = EXPORT_PLOT_HEIGHT,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)
  # conjecture 3.m. - attack rate - static
  ggsave(paste(EXPORT_PATH_PLOTS, "c3-m-attackrate-static", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
         plot_c3_m(data.ss, ep.structure = "static"),
         width = EXPORT_PLOT_WIDTH,
         height = EXPORT_PLOT_HEIGHT,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)

  # conjecture 3.n. - duration - dynamic
  ggsave(paste(EXPORT_PATH_PLOTS, "c3-n-duration-dynamic", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
         plot_c3_n_duration(data.ss),
         width = EXPORT_PLOT_WIDTH,
         height = EXPORT_PLOT_HEIGHT,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)
  # conjecture 3.n. - duration - static
  ggsave(paste(EXPORT_PATH_PLOTS, "c3-n-duration-static", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
         plot_c3_n_duration(data.ss, ep.structure = "static"),
         width = EXPORT_PLOT_WIDTH,
         height = EXPORT_PLOT_HEIGHT,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)

  # conjecture 3.n. - peak - dynamic
  ggsave(paste(EXPORT_PATH_PLOTS, "c3-n-peak-dynamic", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
         plot_c3_n_peak(data.ss),
         width = EXPORT_PLOT_WIDTH,
         height = EXPORT_PLOT_HEIGHT,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)
  # conjecture 3.n. - peak - static
  ggsave(paste(EXPORT_PATH_PLOTS, "c3-n-peak-static", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
         plot_c3_n_peak(data.ss, ep.structure = "static"),
         width = EXPORT_PLOT_WIDTH,
         height = EXPORT_PLOT_HEIGHT,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)

  # conjecture 4.a.b. - probability of infection - clustering: all - dynamic
  ggsave(paste(EXPORT_PATH_PLOTS, "c4-ab-probabilityinfections-clusteringall-dynamic", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
         plot_c4_ab(data.ad),
         width = EXPORT_PLOT_WIDTH,
         height = EXPORT_PLOT_HEIGHT,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)
  # conjecture 4.a.b. - probability of infection - clustering: all - static
  ggsave(paste(EXPORT_PATH_PLOTS, "c4-ab-probabilityinfections-clusteringall-static", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
         plot_c4_ab(data.ad, ep.structure = "static"),
         width = EXPORT_PLOT_WIDTH,
         height = EXPORT_PLOT_HEIGHT,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)

  # conjecture 4.a.b. - probability of infection - clustering: 0.25 - dynamic
  ggsave(paste(EXPORT_PATH_PLOTS, "c4-ab-probabilityinfections-clustering25-dynamic", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
         plot_c4_ab(subset(data.ad, net.clustering.av <= 0.25)),
         width = EXPORT_PLOT_WIDTH,
         height = EXPORT_PLOT_HEIGHT,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)
  # conjecture 4.a.b. - probability of infection - clustering: 0.25 - static
  ggsave(paste(EXPORT_PATH_PLOTS, "c4-ab-probabilityinfections-clustering25-static", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
         plot_c4_ab(subset(data.ad, net.clustering.av <= 0.25), ep.structure = "static"),
         width = EXPORT_PLOT_WIDTH,
         height = EXPORT_PLOT_HEIGHT,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)

  # conjecture 4.a.b. - probability of infection - clustering: 0.50 - dynamic
  ggsave(paste(EXPORT_PATH_PLOTS, "c4-ab-probabilityinfections-clustering50-dynamic", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
         plot_c4_ab(subset(data.ad, net.clustering.av > 0.25 & net.clustering.av <= 0.50)),
         width = EXPORT_PLOT_WIDTH,
         height = EXPORT_PLOT_HEIGHT,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)
  # conjecture 4.a.b. - probability of infection - clustering: 0.50 - static
  ggsave(paste(EXPORT_PATH_PLOTS, "c4-ab-probabilityinfections-clustering50-static", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
         plot_c4_ab(subset(data.ad, net.clustering.av > 0.25 & net.clustering.av <= 0.50), ep.structure = "static"),
         width = EXPORT_PLOT_WIDTH,
         height = EXPORT_PLOT_HEIGHT,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)

  # conjecture 4.a.b. - probability of infection - clustering: 0.75 - dynamic
  ggsave(paste(EXPORT_PATH_PLOTS, "c4-ab-probabilityinfections-clustering75-dynamic", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
         plot_c4_ab(subset(data.ad, net.clustering.av > 0.50 & net.clustering.av <= 0.75)),
         width = EXPORT_PLOT_WIDTH,
         height = EXPORT_PLOT_HEIGHT,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)
  # conjecture 4.a.b. - probability of infection - clustering: 0.75 - static
  ggsave(paste(EXPORT_PATH_PLOTS, "c4-ab-probabilityinfections-clustering75-static", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
         plot_c4_ab(subset(data.ad, net.clustering.av > 0.50 & net.clustering.av <= 0.75), ep.structure = "static"),
         width = EXPORT_PLOT_WIDTH,
         height = EXPORT_PLOT_HEIGHT,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)

  # conjecture 4.a.b. - probability of infection - clustering: 1.00 - dynamic
  ggsave(paste(EXPORT_PATH_PLOTS, "c4-ab-probabilityinfections-clustering100-dynamic", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
         plot_c4_ab(subset(data.ad, net.clustering.av > 0.75)),
         width = EXPORT_PLOT_WIDTH,
         height = EXPORT_PLOT_HEIGHT,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)
  # conjecture 4.a.b. - probability of infection - clustering: 1.00 - static
  ggsave(paste(EXPORT_PATH_PLOTS, "c4-ab-probabilityinfections-clustering100-static", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
         plot_c4_ab(subset(data.ad, net.clustering.av > 0.75), ep.structure = "static"),
         width = EXPORT_PLOT_WIDTH,
         height = EXPORT_PLOT_HEIGHT,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)

  # conjecture 4.a.b. - probability of infection - r neighbors: 0.5 - dynamic
  ggsave(paste(EXPORT_PATH_PLOTS, "c4-ab-probabilityinfections-rneighbors05-dynamic", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
         plot_c4_ab(subset(data.ad, agent.neighborhood.r.sigma.av <= 0.50)),
         width = EXPORT_PLOT_WIDTH,
         height = EXPORT_PLOT_HEIGHT,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)
  # conjecture 4.a.b. - probability of infection - r neighbors: 0.5 - static
  ggsave(paste(EXPORT_PATH_PLOTS, "c4-ab-probabilityinfections-rneighbors05-static", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
         plot_c4_ab(subset(data.ad, agent.neighborhood.r.sigma.av <= 0.50), ep.structure = "static"),
         width = EXPORT_PLOT_WIDTH,
         height = EXPORT_PLOT_HEIGHT,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)

  # conjecture 4.a.b. - probability of infection - r neighbors: 1.0 - dynamic
  ggsave(paste(EXPORT_PATH_PLOTS, "c4-ab-probabilityinfections-rneighbors10-dynamic", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
         plot_c4_ab(subset(data.ad, agent.neighborhood.r.sigma.av > 0.50 & agent.neighborhood.r.sigma.av <= 1.00)),
         width = EXPORT_PLOT_WIDTH,
         height = EXPORT_PLOT_HEIGHT,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)
  # conjecture 4.a.b. - probability of infection - r neighbors: 1.0 - static
  ggsave(paste(EXPORT_PATH_PLOTS, "c4-ab-probabilityinfections-rneighbors10-static", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
         plot_c4_ab(subset(data.ad, agent.neighborhood.r.sigma.av > 0.50 & agent.neighborhood.r.sigma.av <= 1.00), ep.structure = "static"),
         width = EXPORT_PLOT_WIDTH,
         height = EXPORT_PLOT_HEIGHT,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)

  # conjecture 4.a.b. - probability of infection - r neighbors: 1.5 - dynamic
  ggsave(paste(EXPORT_PATH_PLOTS, "c4-ab-probabilityinfections-rneighbors15-dynamic", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
         plot_c4_ab(subset(data.ad, agent.neighborhood.r.sigma.av > 1.00 & agent.neighborhood.r.sigma.av <= 1.50)),
         width = EXPORT_PLOT_WIDTH,
         height = EXPORT_PLOT_HEIGHT,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)
  # conjecture 4.a.b. - probability of infection - r neighbors: 1.5 - static
  ggsave(paste(EXPORT_PATH_PLOTS, "c4-ab-probabilityinfections-rneighbors15-static", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
         plot_c4_ab(subset(data.ad, agent.neighborhood.r.sigma.av > 1.00 & agent.neighborhood.r.sigma.av <= 1.50), ep.structure = "static"),
         width = EXPORT_PLOT_WIDTH,
         height = EXPORT_PLOT_HEIGHT,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)

  # conjecture 4.a.b. - probability of infection - r neighbors: 2.0 - dynamic
  ggsave(paste(EXPORT_PATH_PLOTS, "c4-ab-probabilityinfections-rneighbors20-dynamic", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
         plot_c4_ab(subset(data.ad, agent.neighborhood.r.sigma.av > 1.50)),
         width = EXPORT_PLOT_WIDTH,
         height = EXPORT_PLOT_HEIGHT,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)
  # conjecture 4.a.b. - probability of infection - r neighbors: 2.0 - static
  ggsave(paste(EXPORT_PATH_PLOTS, "c4-ab-probabilityinfections-rneighbors20-static", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
         plot_c4_ab(subset(data.ad, agent.neighborhood.r.sigma.av > 1.50), ep.structure = "static"),
         width = EXPORT_PLOT_WIDTH,
         height = EXPORT_PLOT_HEIGHT,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)

  # conjecture 4.e.f. - attack rate - dynamic
  ggsave(paste(EXPORT_PATH_PLOTS, "c4-ef-attackrate-dynamic", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
         plot_c4_ef(data.ss),
         width = EXPORT_PLOT_WIDTH,
         height = EXPORT_PLOT_HEIGHT,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)
  # conjecture 4.e.f. - attack rate - static
  ggsave(paste(EXPORT_PATH_PLOTS, "c4-ef-attackrate-static", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
         plot_c4_ef(data.ss, ep.structure = "static"),
         width = EXPORT_PLOT_WIDTH,
         height = EXPORT_PLOT_HEIGHT,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)

  # conjecture 4.h. - duration - dynamic
  ggsave(paste(EXPORT_PATH_PLOTS, "c4-h-duration-dynamic", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
         plot_c4_h_duration(data.ss),
         width = EXPORT_PLOT_WIDTH,
         height = EXPORT_PLOT_HEIGHT,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)
  # conjecture 4.h. - duration - static
  ggsave(paste(EXPORT_PATH_PLOTS, "c4-h-duration-static", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
         plot_c4_h_duration(data.ss, ep.structure = "static"),
         width = EXPORT_PLOT_WIDTH,
         height = EXPORT_PLOT_HEIGHT,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)

  # conjecture 4.h. - peak - dynamic
  ggsave(paste(EXPORT_PATH_PLOTS, "c4-h-peak-dynamic", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
         plot_c4_h_peak(data.ss),
         width = EXPORT_PLOT_WIDTH,
         height = EXPORT_PLOT_HEIGHT,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)
  # conjecture 4.h. - peak - static
  ggsave(paste(EXPORT_PATH_PLOTS, "c4-h-peak-static", EXPORT_FILE_EXTENSION_PLOTS, sep = ""),
         plot_c4_h_peak(data.ss, ep.structure = "static"),
         width = EXPORT_PLOT_WIDTH,
         height = EXPORT_PLOT_HEIGHT,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)

}


############################################# COMPOSITION ############################################
#----------------------------------------------------------------------------------------------------#
# function: export_all
#     Exports all data for a complete analysis.
#----------------------------------------------------------------------------------------------------#
export_all <- function() {

  data.ss <- load_simulation_summary_data()
  data.ad <- load_agent_details_data()

  print("################################ EXPORTING DESCRIPTIVE STATISTICS ################################")
  export_descriptives(data.ss)

  print("################################## EXPORTING REGRESSION ANALYSES #################################")
  export_agent_regression_models_composition(data.ad)
  export_attackrate_models_composition(data.ss)
  export_duration_models_composition(data.ss)
  export_peak_models_composition(data.ss)

}


####################################### COMMAND LINE EXECUTION #######################################
if (length(args) >= 1) {
  export_all()
}
