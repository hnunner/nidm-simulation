#!/usr/bin/env Rscript

############################################## LIBRARIES ##############################################
sourceLibs <- function(libs) {
  for (lib in libs) {
    if(lib %in% rownames(installed.packages()) == FALSE) {install.packages(lib)}
    library(lib, character.only = TRUE)
  }
}
sourceLibs(c("QuantPsyc",   # 'meanCenter' function
             "lme4",        # regression analyses
             "sjstats",     # "icc" function
             "texreg"       # html export
            )
          )

########################################### GLOBAL CONSTANTS ##########################################
# input/output directory
args = commandArgs(trailingOnly=TRUE)
DATA_PATH                 <- paste(args[1], "/", sep = "")
# file names of generated data
CSV_SUMMARY_PATH            <- paste(DATA_PATH, "simulation-summary.csv", sep = "")
# export - file system
EXPORT_DIR_NUM              <- "numerical/"
EXPORT_PATH_NUM             <- paste(DATA_PATH, EXPORT_DIR_NUM, sep = "")
EXPORT_FILE_TYPE_REG        <- "html"
EXPORT_FILE_EXTENSION_REG   <- paste(".", EXPORT_FILE_TYPE_REG, sep = "")

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
  return(csv)
}

#----------------------------------------------------------------------------------------------------#
# function: loadSimulationSummaryData
#     Loads summary data for NIDM simulations.
# return: the summary data for NIDM simulations
#----------------------------------------------------------------------------------------------------#
loadSimulationSummaryData <- function() {
  return(loadCSV(CSV_SUMMARY_PATH))
}

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


############################################# REGRESSIONS ############################################
exportRegressionModelsComplete <- function(ssData = loadSimulationSummaryData()) {

  # MAIN EFFECTS
  # Cidm parameters
  beta                               <- meanCenter(ssData$cidm.beta.av)
  mu                                 <- meanCenter(ssData$cidm.mu.av)
  sigma                              <- meanCenter(ssData$cidm.sigma.av / 50)
  r                                  <- meanCenter(ssData$cidm.r.sigma.av)
  N                                  <- meanCenter(ssData$cidm.N / 50)
  iota                               <- meanCenter(ssData$cidm.iota)
  # network properties
  density                            <- meanCenter(ssData$net.density.pre.epidemic)
  index.degree                       <- meanCenter(ssData$index.degree)

  # INTERACTION EFFECTS
  # combinations of beta
  beta.X.mu                          <- (beta - mean(beta)                       *  (mu - mean(mu)))
  beta.X.sigma                       <- (beta - mean(beta)                       *  (sigma - mean(sigma)))
  beta.X.r                           <- (beta - mean(beta)                       *  (r - mean(r)))
  beta.X.N                           <- (beta - mean(beta)                       *  (N - mean(N)))
  beta.X.iota                        <- (beta - mean(beta)                       *  (iota - mean(iota)))
  beta.X.density                     <- (beta - mean(beta)                       *  (density - mean(density)))
  beta.X.index.degree                <- (beta - mean(beta)                       *  (index.degree - mean(index.degree)))
  # combinations of mu
  mu.X.sigma                         <- (mu - mean(mu)                           *  (sigma - mean(sigma)))
  mu.X.r                             <- (mu - mean(mu)                           *  (r - mean(r)))
  mu.X.N                             <- (mu - mean(mu)                           *  (N - mean(N)))
  mu.X.iota                          <- (mu - mean(mu)                           *  (iota - mean(iota)))
  mu.X.density                       <- (mu - mean(mu)                           *  (density - mean(density)))
  mu.X.index.degree                  <- (mu - mean(mu)                           *  (index.degree - mean(index.degree)))
  # combinations of sigma
  sigma.X.r                          <- (sigma - mean(sigma)                     *  (r - mean(r)))
  sigma.X.N                          <- (sigma - mean(sigma)                     *  (N - mean(N)))
  sigma.X.iota                       <- (sigma - mean(sigma)                     *  (iota - mean(iota)))
  sigma.X.density                    <- (sigma - mean(sigma)                     *  (density - mean(density)))
  sigma.X.index.degree               <- (sigma - mean(sigma)                     *  (index.degree - mean(index.degree)))
  # combinations of r
  r.X.N                              <- (r - mean(r)                             *  (N - mean(N)))
  r.X.iota                           <- (r - mean(r)                             *  (iota - mean(iota)))
  r.X.density                        <- (r - mean(r)                             *  (density - mean(density)))
  r.X.index.degree                   <- (r - mean(r)                             *  (index.degree - mean(index.degree)))
  # combinations of N
  N.X.iota                           <- (N - mean(N)                             *  (iota - mean(iota)))
  N.X.density                        <- (N - mean(N)                             *  (density - mean(density)))
  N.X.index.degree                   <- (N - mean(N)                             *  (index.degree - mean(index.degree)))
  # combinations of iota
  iota.X.density                     <- (iota - mean(iota)                       *  (density - mean(density)))
  iota.X.index.degree                <- (iota - mean(iota)                       *  (index.degree - mean(index.degree)))
  # combinations of density
  density.X.index.degree             <- (density - mean(density)                 *  (index.degree - mean(index.degree)))


  ### 2-LEVEL LOGISTIC REGRESSIONS (attack rate)  ###
  ### level 2: parameters combination             ###
  ### level 1: simulation runs                    ###
  # null-model
  reg00    <- glmer(ssData$net.pct.rec/100 ~
                      1 +
                      (1 | sim.upc),
                    family = binomial,
                    data = ssData)
  # main effects: varied CIDM parameters
  reg1Main <- glmer(ssData$net.pct.rec/100 ~
                      #  model parameters
                      beta +
                      mu +
                      sigma +
                      r +
                      N +
                      iota +
                      (1 | sim.upc),
                    family = binomial,
                    data = ssData)
  # network properties
  reg2Main <- glmer(ssData$net.pct.rec/100 ~
                      #  model parameters
                      beta +
                      mu +
                      sigma +
                      r +
                      N +
                      iota +
                      #  network properties
                      density +
                      index.degree +
                      (1 | sim.upc),
                    family = binomial,
                    data = ssData)
  # interaction effects
  reg2Int  <- glmer(ssData$net.pct.rec/100 ~
                      #  model parameters
                      beta +
                      mu +
                      sigma +
                      r +
                      N +
                      iota +
                      #  network properties
                      density +
                      index.degree +
                      #  interactions
                      beta.X.mu +
                      beta.X.sigma +
                      beta.X.r +
                      beta.X.N +
                      beta.X.iota +
                      beta.X.density +
                      beta.X.index.degree +
                      mu.X.sigma +
                      mu.X.r +
                      mu.X.N +
                      mu.X.iota +
                      mu.X.density +
                      mu.X.index.degree +
                      sigma.X.r +
                      sigma.X.N +
                      sigma.X.iota +
                      sigma.X.density +
                      sigma.X.index.degree +
                      r.X.N +
                      r.X.iota +
                      r.X.density +
                      r.X.index.degree +
                      N.X.iota +
                      N.X.density +
                      N.X.index.degree +
                      iota.X.density +
                      iota.X.index.degree +
                      density.X.index.degree +
                      (1 | sim.upc),
                    family = binomial,
                    data = ssData)
  exportModels(list(reg00,reg1Main,reg2Main,reg2Int), "reg-attackrate-complete")

  ### 2-LEVEL LINEAR REGRESSIONS (duration)  ###
  ### level 2: parameters combination             ###
  ### level 1: simulation runs                    ###
  # null-model
  reg00    <- lmer(ssData$sim.epidemic.duration/100 ~
                      1 +
                      (1 | sim.upc),
                    data = ssData,
                    REML = FALSE)
  # main effects: varied CIDM parameters
  reg1Main <- lmer(ssData$sim.epidemic.duration/100 ~
                      #  model parameters
                      beta +
                      mu +
                      sigma +
                      r +
                      N +
                      iota +
                      (1 | sim.upc),
                    data = ssData,
                    REML = FALSE)
  # network properties
  reg2Main <- lmer(ssData$sim.epidemic.duration/100 ~
                      #  model parameters
                      beta +
                      mu +
                      sigma +
                      r +
                      N +
                      iota +
                      #  network properties
                      density +
                      index.degree +
                      (1 | sim.upc),
                    data = ssData,
                    REML = FALSE)
  # interaction effects
  reg2Int  <- lmer(ssData$sim.epidemic.duration/100 ~
                      #  model parameters
                      beta +
                      mu +
                      sigma +
                      r +
                      N +
                      iota +
                      #  network properties
                      density +
                      index.degree +
                      #  interactions
                      beta.X.mu +
                      beta.X.sigma +
                      beta.X.r +
                      beta.X.N +
                      beta.X.iota +
                      beta.X.density +
                      beta.X.index.degree +
                      mu.X.sigma +
                      mu.X.r +
                      mu.X.N +
                      mu.X.iota +
                      mu.X.density +
                      mu.X.index.degree +
                      sigma.X.r +
                      sigma.X.N +
                      sigma.X.iota +
                      sigma.X.density +
                      sigma.X.index.degree +
                      r.X.N +
                      r.X.iota +
                      r.X.density +
                      r.X.index.degree +
                      N.X.iota +
                      N.X.density +
                      N.X.index.degree +
                      iota.X.density +
                      iota.X.index.degree +
                      density.X.index.degree +
                      (1 | sim.upc),
                    data = ssData,
                    REML = FALSE)
  exportModels(list(reg00,reg1Main,reg2Main,reg2Int), "reg-duration-complete")
}


####################################### COMMAND LINE EXECUTION #######################################
if (length(args) >= 1) {
  print("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::")
  print(paste(":: BEGINNING TO ANALYZE DATA IN: ", DATA_PATH, sep = ""))

    print(":::::: Exporting complete regression models..")
  exportRegressionModelsComplete(ssData = ssData)

  print(paste(":: ANALYSIS FINISHED SUCCESSFULLY!", sep = ""))
  print("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::")
}
