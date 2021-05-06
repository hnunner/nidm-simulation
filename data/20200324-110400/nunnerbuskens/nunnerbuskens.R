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
  print(paste("observations:", nrow(data.ss)))
  print(paste("exlcusions (unstable):", nrow(subset(data.ss, data.ss$net.stable.pre == 0))))
  print(paste("exlcusions (disconnected):", nrow(subset(data.ss, data.ss$net.pathlength.pre.epidemic.av < 1))))
  print("------------------------------------------")

  # remove exclusions
  data.ss <- subset(data.ss,
                    data.ss$net.stable.pre == 1
                    & data.ss$net.pathlength.pre.epidemic.av >= 1)

  print(paste("av. risk perception: ", round(mean(data.ss$nb.r.sigma.av), digits = 2),         # rs.equal = true
              " (", round(sd(data.ss$nb.r.sigma.av), digits = 2), ")", sep = ""))
  print("------------------------------------------")
  print(paste("av. degree (network): ", round(mean(data.ss$net.degree.pre.epidemic.av), digits = 2),
              " (", round(sd(data.ss$net.degree.pre.epidemic.av), digits = 2), ")", sep = ""))
  print(paste("av. clustering (network): ", round(mean(data.ss$net.clustering.pre.epidemic.av), digits = 2),
              " (", round(sd(data.ss$net.clustering.pre.epidemic.av), digits = 2), ")", sep = ""))
  print(paste("av. path length (network): ", round(mean(data.ss$net.pathlength.pre.epidemic.av), digits = 2),
              " (", round(sd(data.ss$net.pathlength.pre.epidemic.av), digits = 2), ")", sep = ""))
  print("------------------------------------------")
  print(paste("av. degree (patient-0): ", round(mean(data.ss$index.degree), digits = 2),
              " (", round(sd(data.ss$index.degree), digits = 2), ")", sep = ""))
  print(paste("av. clustering (patient-0): ", round(mean(data.ss$index.clustering), digits = 2),
              " (", round(sd(data.ss$index.clustering), digits = 2), ")", sep = ""))
  print(paste("av. betweenness (patient-0): ", round(mean(data.ss$index.betweenness.normalized), digits = 2),
              " (", round(sd(data.ss$index.betweenness.normalized), digits = 2), ")", sep = ""))
  print("------------------------------------------")
  print(paste("mean attack rate: ", round(mean(data.ss$net.pct.rec), digits = 2),
              " (", round(sd(data.ss$net.pct.rec), digits = 2), ")", sep = ""))
  print(paste("median attack rate: ", round(median(data.ss$net.pct.rec), digits = 2),
              " (", round(quantile(data.ss$net.pct.rec)[2], digits = 2), ", ",
              round(quantile(data.ss$net.pct.rec)[4], digits = 2), ")", sep = ""))
  print("")
}

print_descriptives_per_condition <- function(data.ss = load_simulation_summary_data()) {
  print("#################### OVERALL ####################")
  print_descriptives(data.ss)
  print("#################### RRM ####################")
  print_descriptives(subset(data.ss, data.ss$nb.alpha == 0.15 & data.ss$nb.omega == 0.0 & data.ss$nb.sigma == 2.0))
  print("#################### RRS ####################")
  print_descriptives(subset(data.ss, data.ss$nb.alpha == 0.15 & data.ss$nb.omega == 0.0 & data.ss$nb.sigma == 50.0))
  print("#################### RAM ####################")
  print_descriptives(subset(data.ss, data.ss$nb.alpha == 0.15 & data.ss$nb.omega == 0.8 & data.ss$nb.sigma ==  2.0))
  print("#################### RAS ####################")
  print_descriptives(subset(data.ss, data.ss$nb.alpha == 0.15 & data.ss$nb.omega == 0.8 & data.ss$nb.sigma == 50.0))
  print("#################### SRM ####################")
  print_descriptives(subset(data.ss, data.ss$nb.alpha == 0.85 & data.ss$nb.omega == 0.0 & data.ss$nb.sigma ==  2.0))
  print("#################### SRS ####################")
  print_descriptives(subset(data.ss, data.ss$nb.alpha == 0.85 & data.ss$nb.omega == 0.0 & data.ss$nb.sigma == 50.0))
  print("#################### SAM ####################")
  print_descriptives(subset(data.ss, data.ss$nb.alpha == 0.85 & data.ss$nb.omega == 0.8 & data.ss$nb.sigma ==  2.0))
  print("#################### SAS ####################")
  print_descriptives(subset(data.ss, data.ss$nb.alpha == 0.85 & data.ss$nb.omega == 0.8 & data.ss$nb.sigma == 50.0))
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
# function: export_regression_models_complete
#     Creates and exports multi-level logistic regression models for attack rate with all possible
#     parameters and interaction effects.
# param:  data.ss
#     simulation summary data to get regression models for
#----------------------------------------------------------------------------------------------------#
export_regression_models_complete <- function(data.ss = load_simulation_summary_data(), name.extension = "") {

  # remove exclusions
  data.ss <- subset(data.ss,
                    data.ss$net.stable.pre == 1
                    & data.ss$net.pathlength.pre.epidemic.av >= 1)

  # MAIN EFFECTS
  # SWIDM parameters
  alpha                                 <- meanCenter(data.ss$nb.alpha)
  omega                                 <- meanCenter(data.ss$nb.omega)
  sigma                                 <- meanCenter(data.ss$nb.sigma)
  r.av                                  <- meanCenter(data.ss$nb.r.sigma.av)    # rs.equal = true
  # network properties (macro)
  net.degree                            <- meanCenter(data.ss$net.degree.pre.epidemic.av)
  net.clustering                        <- meanCenter(data.ss$net.clustering.pre.epidemic.av)
  net.pathlength                        <- meanCenter(data.ss$net.pathlength.pre.epidemic.av)
  # network properties (macro)
  index.degree                          <- meanCenter(data.ss$index.degree)
  index.clustering                      <- meanCenter(data.ss$index.clustering)
  index.betweenness                     <- meanCenter(data.ss$index.betweenness)

  # INTERACTION EFFECTS
  # combinations of alpha
  alpha.X.omega                         <- (alpha - mean(alpha))                        *  (omega - mean(omega))
  alpha.X.sigma                         <- (alpha - mean(alpha))                        *  (sigma - mean(sigma))
  alpha.X.r.av                          <- (alpha - mean(alpha))                        *  (r.av - mean(r.av))
  alpha.X.net.degree                    <- (alpha - mean(alpha))                        *  (net.degree - mean(net.degree))
  alpha.X.net.clustering                <- (alpha - mean(alpha))                        *  (net.clustering - mean(net.clustering))
  alpha.X.net.pathlength                <- (alpha - mean(alpha))                        *  (net.pathlength - mean(net.pathlength))
  alpha.X.index.degree                  <- (alpha - mean(alpha))                        *  (index.degree - mean(index.degree))
  alpha.X.index.clustering              <- (alpha - mean(alpha))                        *  (index.clustering - mean(index.clustering))
  alpha.X.index.betweenness             <- (alpha - mean(alpha))                        *  (index.betweenness - mean(index.betweenness))

  # combinations of omega
  omega.X.sigma                         <- (omega - mean(omega))                        *  (sigma - mean(sigma))
  omega.X.r.av                          <- (omega - mean(omega))                        *  (r.av - mean(r.av))
  omega.X.net.degree                    <- (omega - mean(omega))                        *  (net.degree - mean(net.degree))
  omega.X.net.clustering                <- (omega - mean(omega))                        *  (net.clustering - mean(net.clustering))
  omega.X.net.pathlength                <- (omega - mean(omega))                        *  (net.pathlength - mean(net.pathlength))
  omega.X.index.degree                  <- (omega - mean(omega))                        *  (index.degree - mean(index.degree))
  omega.X.index.clustering              <- (omega - mean(omega))                        *  (index.clustering - mean(index.clustering))
  omega.X.index.betweenness             <- (omega - mean(omega))                        *  (index.betweenness - mean(index.betweenness))

  # combinations of sigma
  sigma.X.r.av                          <- (sigma - mean(sigma))                        *  (r.av - mean(r.av))
  sigma.X.net.degree                    <- (sigma - mean(sigma))                        *  (net.degree - mean(net.degree))
  sigma.X.net.clustering                <- (sigma - mean(sigma))                        *  (net.clustering - mean(net.clustering))
  sigma.X.net.pathlength                <- (sigma - mean(sigma))                        *  (net.pathlength - mean(net.pathlength))
  sigma.X.index.degree                  <- (sigma - mean(sigma))                        *  (index.degree - mean(index.degree))
  sigma.X.index.clustering              <- (sigma - mean(sigma))                        *  (index.clustering - mean(index.clustering))
  sigma.X.index.betweenness             <- (sigma - mean(sigma))                        *  (index.betweenness - mean(index.betweenness))

  # combinations of r.av
  r.av.X.net.degree                     <- (r.av - mean(r.av))                          *  (net.degree - mean(net.degree))
  r.av.X.net.clustering                 <- (r.av - mean(r.av))                          *  (net.clustering - mean(net.clustering))
  r.av.X.net.pathlength                 <- (r.av - mean(r.av))                          *  (net.pathlength - mean(net.pathlength))
  r.av.X.index.degree                   <- (r.av - mean(r.av))                          *  (index.degree - mean(index.degree))
  r.av.X.index.clustering               <- (r.av - mean(r.av))                          *  (index.clustering - mean(index.clustering))
  r.av.X.index.betweenness              <- (r.av - mean(r.av))                          *  (index.betweenness - mean(index.betweenness))

  # combinations of net.degree
  net.degree.X.net.clustering           <- (net.degree - mean(net.degree))              *  (net.clustering - mean(net.clustering))
  net.degree.X.net.pathlength           <- (net.degree - mean(net.degree))              *  (net.pathlength - mean(net.pathlength))
  net.degree.X.index.degree             <- (net.degree - mean(net.degree))              *  (index.degree - mean(index.degree))
  net.degree.X.index.clustering         <- (net.degree - mean(net.degree))              *  (index.clustering - mean(index.clustering))
  net.degree.X.index.betweenness        <- (net.degree - mean(net.degree))              *  (index.betweenness - mean(index.betweenness))

  # combinations of net.clustering
  net.clustering.X.net.pathlength       <- (net.clustering - mean(net.clustering))      *  (net.pathlength - mean(net.pathlength))
  net.clustering.X.index.degree         <- (net.clustering - mean(net.clustering))      *  (index.degree - mean(index.degree))
  net.clustering.X.index.clustering     <- (net.clustering - mean(net.clustering))      *  (index.clustering - mean(index.clustering))
  net.clustering.X.index.betweenness    <- (net.clustering - mean(net.clustering))      *  (index.betweenness - mean(index.betweenness))

  # combinations of net.pathlength
  net.pathlength.X.index.degree         <- (net.pathlength - mean(net.pathlength))      *  (index.degree - mean(index.degree))
  net.pathlength.X.index.clustering     <- (net.pathlength - mean(net.pathlength))      *  (index.clustering - mean(index.clustering))
  net.pathlength.X.index.betweenness    <- (net.pathlength - mean(net.pathlength))      *  (index.betweenness - mean(index.betweenness))

  # combinations of index.degree
  index.degree.X.index.clustering       <- (index.degree - mean(index.degree))          *  (index.clustering - mean(index.clustering))
  index.degree.X.index.betweenness      <- (index.degree - mean(index.degree))          *  (index.betweenness - mean(index.betweenness))

  # combinations of index.clustering
  index.clustering.X.index.betweenness  <- (index.clustering - mean(index.clustering))  *  (index.betweenness - mean(index.betweenness))


  ### 2-LEVEL LOGISTIC REGRESSIONS (attack rate)  ###
  ### level 2: parameters combination             ###
  ### level 1: simulation runs                    ###
  # null-model
  reg.00    <- glmer(data.ss$net.pct.rec/100 ~
                       1 +
                       (1 | sim.upc),
                     family = binomial,
                     data = data.ss)
  # main effects: varied SWIDM parameters
  reg.param <- glmer(data.ss$net.pct.rec/100 ~
                       #  model parameters
                       alpha +
                       omega +
                       sigma +
                       r.av +
                       (1 | sim.upc),
                     family = binomial,
                     data = data.ss)
  # network properties
  reg.net.macro <- glmer(data.ss$net.pct.rec/100 ~
                           #  network properties
                           net.degree +
                           net.clustering +
                           net.pathlength +
                           (1 | sim.upc),
                         family = binomial,
                         data = data.ss)
  # network properties
  reg.net.micro <- glmer(data.ss$net.pct.rec/100 ~
                           #  network properties
                           index.degree +
                           index.clustering +
                           index.betweenness +
                           (1 | sim.upc),
                         family = binomial,
                         data = data.ss)
  # network properties
  reg.net.all <- glmer(data.ss$net.pct.rec/100 ~
                         #  network properties
                         net.degree +
                         net.clustering +
                         net.pathlength +
                         index.degree +
                         index.clustering +
                         index.betweenness +
                         (1 | sim.upc),
                       family = binomial,
                       data = data.ss)
  # network properties
  reg.all <- glmer(data.ss$net.pct.rec/100 ~
                     #  model parameters
                     alpha +
                     omega +
                     sigma +
                     r.av +
                     #  network properties
                     net.degree +
                     net.clustering +
                     net.pathlength +
                     index.degree +
                     index.clustering +
                     index.betweenness +
                     (1 | sim.upc),
                   family = binomial,
                   data = data.ss)
  # interaction effects
  reg.int  <- glmer(data.ss$net.pct.rec/100 ~
                      #  model parameters
                      alpha +
                      omega +
                      sigma +
                      r.av +
                      #  network properties
                      net.degree +
                      net.clustering +
                      net.pathlength +
                      index.degree +
                      index.clustering +
                      index.betweenness +
                      #  interactions
                      alpha.X.omega +
                      alpha.X.sigma +
                      alpha.X.r.av +
                      alpha.X.net.degree +
                      alpha.X.net.clustering +
                      alpha.X.net.pathlength +
                      alpha.X.index.degree +
                      alpha.X.index.clustering +
                      alpha.X.index.betweenness +
                      omega.X.sigma +
                      omega.X.r.av +
                      omega.X.net.degree +
                      omega.X.net.clustering +
                      omega.X.net.pathlength +
                      omega.X.index.degree +
                      omega.X.index.clustering +
                      omega.X.index.betweenness +
                      sigma.X.r.av +
                      sigma.X.net.degree +
                      sigma.X.net.clustering +
                      sigma.X.net.pathlength +
                      sigma.X.index.degree +
                      sigma.X.index.clustering +
                      sigma.X.index.betweenness +
                      r.av.X.net.degree +
                      r.av.X.net.clustering +
                      r.av.X.net.pathlength +
                      r.av.X.index.degree +
                      r.av.X.index.clustering +
                      r.av.X.index.betweenness +
                      net.degree.X.net.clustering +
                      net.degree.X.net.pathlength +
                      net.degree.X.index.degree +
                      net.degree.X.index.clustering +
                      net.degree.X.index.betweenness +
                      net.clustering.X.net.pathlength +
                      net.clustering.X.index.degree +
                      net.clustering.X.index.clustering +
                      net.clustering.X.index.betweenness +
                      net.pathlength.X.index.degree +
                      net.pathlength.X.index.clustering +
                      net.pathlength.X.index.betweenness +
                      index.degree.X.index.clustering +
                      index.degree.X.index.betweenness +
                      index.clustering.X.index.betweenness +
                      (1 | sim.upc),
                    family = binomial,
                    data = data.ss)
  exportModels(list(reg.00,
                    reg.param,
                    reg.net.macro,
                    reg.net.micro,
                    reg.net.all,
                    reg.all,
                    reg.int), paste("reg-attackrate-complete", name.extension, sep = ""))
}

#----------------------------------------------------------------------------------------------------#
# function: export_regression_models_selected
#     Creates and exports multi-level logistic regression models for attack rate with only
#     selected parameters and interactions. This method is a copy of 'exportAttackRateModelsComplete',
#     but is intended to find the best models with only significant and expressive parameters by
#     modyfying the model parameters without changing the complete export for command line
#     invocations.
# param:  data.ss
#     simulation summary data to get regression models for
#----------------------------------------------------------------------------------------------------#
export_regression_models_selected <- function(data.ss = load_simulation_summary_data()) {
  ### COPY MODELS FROM ABOVE AND COMMENT OUT INSIGNIFICANT OR IMPLAUSIBLE PARAMETERS
}



export_compare_dyn_stat <- function() {

  data.ss <- load_simulation_summary_data()

  # remove exclusions
  data.ss <- subset(data.ss,
                    data.ss$net.stable.pre == 1
                    & data.ss$net.pathlength.pre.epidemic.av >= 1)

  ar.rrm <- select(subset(data.ss, data.ss$nb.alpha == 0.15 &
                            data.ss$nb.omega == 0.0 &
                            data.ss$nb.sigma == 2.0),
                   net.pct.rec, nb.ep.static)
  ar.rrm <- data.frame("submodel" = rep("RRM", nrow(ar.rrm)),
                       "a.rate" = ar.rrm$net.pct.rec,
                       "static" = ar.rrm$nb.ep.static)
  ar.rrs <- select(subset(data.ss, data.ss$nb.alpha == 0.15 &
                            data.ss$nb.omega == 0.0 &
                            data.ss$nb.sigma == 50.0),
                   net.pct.rec, nb.ep.static)
  ar.rrs <- data.frame("submodel" = rep("RRS", nrow(ar.rrs)),
                       "a.rate" = ar.rrs$net.pct.rec,
                       "static" = ar.rrs$nb.ep.static)
  ar.ram <- select(subset(data.ss, data.ss$nb.alpha == 0.15 &
                            data.ss$nb.omega == 0.8 &
                            data.ss$nb.sigma == 2.0),
                   net.pct.rec, nb.ep.static)
  ar.ram <- data.frame("submodel" = rep("RAM", nrow(ar.ram)),
                       "a.rate" = ar.ram$net.pct.rec,
                       "static" = ar.ram$nb.ep.static)
  ar.ras <- select(subset(data.ss, data.ss$nb.alpha == 0.15 &
                            data.ss$nb.omega == 0.8 &
                            data.ss$nb.sigma == 50.0),
                   net.pct.rec, nb.ep.static)
  ar.ras <- data.frame("submodel" = rep("RAS", nrow(ar.ras)),
                       "a.rate" = ar.ras$net.pct.rec,
                       "static" = ar.ras$nb.ep.static)
  ar.srm <- select(subset(data.ss, data.ss$nb.alpha == 0.85 &
                            data.ss$nb.omega == 0.0 &
                            data.ss$nb.sigma == 2.0),
                   net.pct.rec, nb.ep.static)
  ar.srm <- data.frame("submodel" = rep("SRM", nrow(ar.srm)),
                       "a.rate" = ar.srm$net.pct.rec,
                       "static" = ar.srm$nb.ep.static)
  ar.srs <- select(subset(data.ss, data.ss$nb.alpha == 0.85 &
                            data.ss$nb.omega == 0.0 &
                            data.ss$nb.sigma == 50.0),
                   net.pct.rec, nb.ep.static)
  ar.srs <- data.frame("submodel" = rep("SRS", nrow(ar.srs)),
                       "a.rate" = ar.srs$net.pct.rec,
                       "static" = ar.srs$nb.ep.static)
  ar.sam <- select(subset(data.ss, data.ss$nb.alpha == 0.85 &
                            data.ss$nb.omega == 0.8 &
                            data.ss$nb.sigma == 2.0),
                   net.pct.rec, nb.ep.static)
  ar.sam <- data.frame("submodel" = rep("SAM", nrow(ar.sam)),
                       "a.rate" = ar.sam$net.pct.rec,
                       "static" = ar.sam$nb.ep.static)
  ar.sas <- select(subset(data.ss, data.ss$nb.alpha == 0.85 &
                            data.ss$nb.omega == 0.8 &
                            data.ss$nb.sigma == 50.0),
                   net.pct.rec, nb.ep.static)
  ar.sas <- data.frame("submodel" = rep("SAS", nrow(ar.sas)),
                       "a.rate" = ar.sas$net.pct.rec,
                       "static" = ar.sas$nb.ep.static)

  ar.complete <- rbind(ar.rrm,
                       ar.rrs,
                       ar.ram,
                       ar.ras,
                       ar.srm,
                       ar.srs,
                       ar.sam,
                       ar.sas)

  # ggplot(ar.complete, aes(x=submodel, y=a.rate)) +
  #   geom_boxplot()
  #
  # ggplot(ar.complete, aes(x=submodel, y=a.rate, fill=factor(static))) +
  #   geom_boxplot()
  #
  # ggplot(ar.complete,aes(x=factor(static),y=a.rate,fill=factor(static)))+
  #   geom_boxplot()+
  #   facet_wrap(~submodel)

  static.labs <- c("dynamic", "static")
  names(static.labs) <- c(0, 1)

  plot <- ggplot(ar.complete,aes(x=submodel,y=a.rate,fill=submodel))+
    geom_boxplot()+
    facet_wrap(~static,
               labeller = labeller(static = static.labs)) +
    xlab("Submodel") +
    ylab("Attack rate") +
    scale_fill_discrete(name = "Submodel")

  plot

  # create directory if necessary
  dir.create(EXPORT_PATH_PLOTS, showWarnings = FALSE)

  filepath <- paste(EXPORT_PATH_PLOTS,
                    "stat-dyn-comparison",
                    EXPORT_FILE_EXTENSION_PLOTS,
                    sep = "")

  ggsave(filepath,
         plot,
         width = EXPORT_PLOT_WIDTH,
         height = EXPORT_PLOT_HEIGHT,
         units = EXPORT_SIZE_UNITS,
         dpi = EXPORT_DPI,
         device = EXPORT_FILE_TYPE_PLOTS)

  print(paste(":::::::: Export of ", filepath, " succesful.", sep = ""))
}


export_all <- function() {

  data.ss <- load_simulation_summary_data()
  data.ss <- subset(data.ss, data.ss$nb.ep.static == 0)
  print("##################################### DESCRIPTIVE STATISTICS #####################################")
  print_descriptives_per_condition(data.ss)

  print("################################## EXPORTING REGRESSION ANALYSES #################################")
  export_regression_models_complete(data.ss)

  print("####################### EXPORTING COMPARISON OF DYNAMIC AND STATIC NETWORKS ######################")
  export_compare_dyn_stat()


  # print("######################################### STATIC NETWORKS ########################################")
  # data.ss <- load_simulation_summary_data()
  # data.ss <- subset(data.ss, data.ss$nb.ep.static == 1)
  # print("##################################### DESCRIPTIVE STATISTICS #####################################")
  # print("################################ N = 20 ################################")
  # print_descriptives_per_condition(subset(data.ss, data.ss$nb.N == 20))
  # print("################################ N = 24 ################################")
  # print_descriptives_per_condition(subset(data.ss, data.ss$nb.N == 24))
  #
  # print("################################## EXPORTING REGRESSION ANALYSES #################################")
  # export_regression_models_complete(subset(data.ss, data.ss$nb.N == 20), "-N20-static")
  # export_regression_models_complete(subset(data.ss, data.ss$nb.N == 24), "-N24-static")
  #
  # print("######################################## DYNAMIC NETWORKS ########################################")
  # data.ss <- load_simulation_summary_data()
  # data.ss <- subset(data.ss, data.ss$nb.ep.static == 0)
  # print("##################################### DESCRIPTIVE STATISTICS #####################################")
  # print("################################ N = 20 ################################")
  # print_descriptives_per_condition(subset(data.ss, data.ss$nb.N == 20))
  # print("################################ N = 24 ################################")
  # print_descriptives_per_condition(subset(data.ss, data.ss$nb.N == 24))
  #
  # print("################################## EXPORTING REGRESSION ANALYSES #################################")
  # export_regression_models_complete(subset(data.ss, data.ss$nb.N == 20), "-N20-dynamic")
  # export_regression_models_complete(subset(data.ss, data.ss$nb.N == 24), "-N24-dynamic")
  #

}

####################################### COMMAND LINE EXECUTION #######################################
if (length(args) >= 1) {
  export_all()
}






manual_analysis <- function() {
  data.ss <- load_simulation_summary_data()

  table(data.ss$nb.gamma)

  data.ss$nb.r.sigma.av.factor <- as.factor(data.ss$nb.r.sigma.av)
  ggplot(subset(data.ss, nb.sigma < 50 &
                  net.stable.pre == 1 &
                  nb.gamma == 0.05),
         aes(x=nb.sigma,
             y=net.pct.rec,
             color=nb.r.sigma.av.factor)) +
    geom_point(alpha=0.2) +
    geom_smooth(se=FALSE)



  mean(subset(data.ss, nb.r.sigma.av == 0.5)$net.pct.rec)
  mean(subset(data.ss, nb.r.sigma.av == 0.75)$net.pct.rec)
  mean(subset(data.ss, nb.r.sigma.av == 1)$net.pct.rec)

}

