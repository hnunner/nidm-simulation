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
# function: remove_exclusions_simulation_summary_data
#     Removes excluded records from simulation summary data.
# return: the simulation summary data cleaned from excluded data records
#----------------------------------------------------------------------------------------------------#
remove_exclusions_simulation_summary_data <- function(data.ss) {
  data.ss <- subset(data.ss, data.ss$net.pathlength.pre.epidemic.av >= 1)
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



############################################# DESCRIPTIVES ###########################################
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



export_descriptives <- function(data.ss = load_simulation_summary_data()) {

  # observations
  obs <- nrow(data.ss)
  out <- paste(" observations: ", obs, "\n", sep = "")
  data.ss <- remove_exclusions_simulation_summary_data(data.ss)
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

  ### DATA PREPARATIONS ###
  data.ss <- remove_exclusions_simulation_summary_data(data.ss)

  # main effects
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
  ## DYNAMIC ##
  # null-model
  reg.dynamic.00   <- glmer(data.ss$net.dynamic.pct.rec/100 ~
                              1 +
                              (1 | sim.cnt),
                            family = binomial,
                            data = data.ss)
  # model conjecture 2
  net.dynamic.c2   <- glmer(data.ss$net.dynamic.pct.rec/100 ~
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
  net.dynamic.c3   <- glmer(data.ss$net.dynamic.pct.rec/100 ~
                              net.clustering +
                              i.r.neigh +
                              net.pathlenth +
                              i.betw +
                              i.betw.X.i.r.neigh +
                              (1 | sim.cnt),
                            family = binomial,
                            data = data.ss)
  # model conjecture 4
  net.dynamic.c4   <- glmer(data.ss$net.dynamic.pct.rec/100 ~
                              net.assortativity +
                              net.clustering +
                              net.clustering.X.net.assortativity +
                              i.r.neigh +
                              i.r.neigh.X.net.assortativity +
                              (1 | sim.cnt),
                            family = binomial,
                            data = data.ss)
  # model all combined
  net.dynamic.all  <- glmer(data.ss$net.dynamic.pct.rec/100 ~
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
  ## STATIC ##
  # null-model
  reg.static.00   <- glmer(data.ss$net.static.pct.rec/100 ~
                             1 +
                             (1 | sim.cnt),
                           family = binomial,
                           data = data.ss)
  # model conjecture 2
  net.static.c2   <- glmer(data.ss$net.static.pct.rec/100 ~
                             r.sigma +
                             sigma +
                             gamma +
                             r.sigma.X.sigma +
                             r.sigma.X.gamma +
                             (1 | sim.cnt),
                           family = binomial,
                           data = data.ss)
  # model conjecture 3
  net.static.c3   <- glmer(data.ss$net.static.pct.rec/100 ~
                             net.clustering +
                             i.r.neigh +
                             net.pathlenth +
                             i.betw +
                             i.betw.X.i.r.neigh +
                             (1 | sim.cnt),
                           family = binomial,
                           data = data.ss)
  # model conjecture 4
  net.static.c4   <- glmer(data.ss$net.static.pct.rec/100 ~
                             net.assortativity +
                             net.clustering +
                             net.clustering.X.net.assortativity +
                             i.r.neigh +
                             i.r.neigh.X.net.assortativity +
                             (1 | sim.cnt),
                           family = binomial,
                           data = data.ss)
  # model all combined
  net.static.all  <- glmer(data.ss$net.static.pct.rec/100 ~
                             r.sigma +
                             sigma +
                             gamma +
                             net.clustering +
                             i.r.neigh +
                             net.pathlenth +
                             i.betw +
                             net.assortativity +
                             r.sigma.X.sigma +
                             r.sigma.X.gamma +
                             i.betw.X.i.r.neigh +
                             net.clustering.X.net.assortativity +
                             i.r.neigh.X.net.assortativity +
                             (1 | sim.cnt),
                           family = binomial,
                           data = data.ss)

  ### FILE EXPORT ###
  filename <- "reg-attackrate-dynamic"
  if (filenamname.appendix != "") {
    filename <- paste("-", filenamname.appendix, sep = "")
  }
  exportModels(list(reg.dynamic.00,
                    net.dynamic.c2,
                    net.dynamic.c3,
                    net.dynamic.c4,
                    net.dynamic.all), filename)
  filename <- "reg-attackrate-static"
  if (filenamname.appendix != "") {
    filename <- paste("-", filenamname.appendix, sep = "")
  }
  exportModels(list(reg.static.00,
                    net.static.c2,
                    net.static.c3,
                    net.static.c4,
                    net.static.all), filename)
}

#----------------------------------------------------------------------------------------------------#
#----------------------------------------------------------------------------------------------------#
export_agent_regression_model <- function(data.ad = load_agent_details_data(), filenamname.appendix = "") {

  ### DATA PREPARATIONS ###
  # only finished simulations
  data.ad <- subset(data.ad, sim.stage == "finished")
  # only data of not excluded records
  data.ad <- subset(data.ad, sim.uid %in% remove_exclusions_simulation_summary_data(load_simulation_summary_data())$sim.uid)
  # transform disease state into flage whether agent has been infected or not
  data.ad$agent.infected <- ifelse(data.ad$agent.dis.state == "RECOVERED", 1, ifelse(data.ad$agent.dis.state == "INFECTED", 1, 0))

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
  ## DYNAMIC ##
  # null-model
  infprob.dynamic.00   <- glmer(data.ad$agent.infected ~
                                  1 +
                                  (1 | sim.cnt),
                                family = binomial,
                                data = data.ad)
  # model conjecture 2
  infprob.dynamic.c2   <- glmer(data.ad$agent.infected ~
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
  infprob.dynamic.c3   <- glmer(data.ad$agent.infected ~
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
  infprob.dynamic.c4   <- glmer(data.ad$agent.infected ~
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
  infprob.dynamic.all   <- glmer(data.ad$agent.infected ~
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
  exportModels(list(infprob.dynamic.00,
                    infprob.dynamic.c2,
                    infprob.dynamic.c3,
                    infprob.dynamic.c4,
                    infprob.dynamic.all), filename)

}

#----------------------------------------------------------------------------------------------------#
#----------------------------------------------------------------------------------------------------#
export_agent_regression_models <- function(data.ad = load_agent_details_data(), filenamname.appendix = "") {
  export_agent_regression_model(subset(data.ad, nb.ep.structure == "dynamic"), paste(filenamname.appendix, "dynamic", sep = ""))
  export_agent_regression_model(subset(data.ad, nb.ep.structure == "static"), paste(filenamname.appendix, "static", sep = ""))
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
  export_attackrate_models(data.ss)
  export_agent_regression_models(data.ad)

}






conjecture1 <- function(data.ss = load_simulation_summary_data()) {

  summary(data.ss$net.static.pct.rec)
  summary(data.ss$net.dynamic.pct.rec)


  d <- data.frame("structure"   = rep("static", nrow(data.ss)),
                  "attack rate" = data.ss$net.static.pct.rec,
                  "duration"    = data.ss$net.static.epidemic.duration)

  d <- rbind(d, data.frame("structure"   = rep("dynamic", nrow(data.ss)),
                           "attack rate" = data.ss$net.dynamic.pct.rec,
                           "duration"    = data.ss$net.dynamic.epidemic.duration))

  # attack rate - all simulations
  p.attackrate <- ggplot(d,
                         aes(x = structure,
                             y = attack.rate)) +
    geom_boxplot() +
    scale_x_discrete(name="Network structure during epidemic") +
    scale_y_continuous(name="Attack rate",
                       limits=c(0, 100))
  p.attackrate
  # duration - all simulations
  p.duration <- ggplot(d,
                       aes(x = structure,
                           y = duration)) +
    geom_boxplot() +
    scale_x_discrete(name="Network structure during epidemic") +
    scale_y_continuous(name="Duration")
  p.duration

}


conjecture2a <- function(data.ad = load_agent_details_data()) {

  d <- subset(data.ad, nb.ep.structure == "dynamic" & sim.stage == "finished")
  summary(subset(d, agent.dis.state == "RECOVERED")$nb.r.sigma)
  summary(subset(d, agent.dis.state == "SUSCEPTIBLE")$nb.r.sigma)

  d.reg <- data.frame("sim.uid" = d$sim.uid,
                      "sigma" = d$nb.sigma,
                      "gamma" = d$nb.gamma,
                      "agent.id" = d$agent.id,
                      "r.sigma" = d$nb.r.sigma,
                      "infected" = d$agent.dis.state == "RECOVERED")

  summary(d.reg)
  l <- glm(infected ~ r.sigma + gamma + sigma, data = d.reg, family = "binomial")
  summary(l)


}







####################################### COMMAND LINE EXECUTION #######################################
if (length(args) >= 1) {
  export_all()
}
