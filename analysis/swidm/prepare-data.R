# input/output directory
DATA_PATH                   <- ""
args = commandArgs(trailingOnly=TRUE)
if (length(args) == 0) {
  DATA_PATH                 <- paste(dirname(sys.frame(1)$ofile), "/", sep = "")
} else {
  DATA_PATH                 <- args[1]
}

CSV_SUMMARY_PATH                <- paste(DATA_PATH, "simulation-summary.csv", sep = "")
CSV_AGENT_DETAILS_PATH          <- paste(DATA_PATH, "agent-details.csv", sep = "")
CSV_ROUND_SUMMARY_PATH          <- paste(DATA_PATH, "round-summary.csv", sep = "")


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

merge_data <- function(data) {

  res <- data[[1]][0,]

  sim.cnt.prev.max <- 0
  for (d in data) {
    d$sim.cnt <- d$sim.cnt + sim.cnt.prev.max
    sim.cnt.prev.max <- max(d$sim.cnt)
    d$sim.uid <- paste(d$sim.upc, "-", d$sim.cnt, "-", d$sim.it, sep = "")
    print(paste("merging data with alpha of first row: ", d[1,]$nb.alpha))
    res <- rbind(res, d)
  }
  return(res)
}


merge_data_by_paths <- function(paths.in, path.out, names) {

  for (name in names) {
    d <- vector(mode = "list", length = length(paths.in))
    i <- 1
    for (path.in in paths.in) {
      path.complete <- paste(path.in, name, ".csv", sep = "")
      print(paste("Loading:", path.complete))
      d[[i]]<- load_csv(path.complete)
      i <- i+1
    }
    d <- merge_data(d)
    write.csv(d, paste(path.out, name, ".csv", sep = ""), row.names = FALSE)
  }

}


merge_data_all <- function() {

  paths.in <- c("C:/Users/Hendrik/git/NIDM/simulation/exports/20200818-195401/data/nunnerbuskens/",
                "C:/Users/Hendrik/git/NIDM/simulation/exports/20200818-195402/data/nunnerbuskens/",
                "C:/Users/Hendrik/git/NIDM/simulation/exports/20200818-195403/data/nunnerbuskens/",
                "C:/Users/Hendrik/git/NIDM/simulation/exports/20200818-195404/data/nunnerbuskens/",
                "C:/Users/Hendrik/git/NIDM/simulation/exports/20200818-195405/data/nunnerbuskens/",
                "C:/Users/Hendrik/git/NIDM/simulation/exports/20200818-195407/data/nunnerbuskens/",
                "C:/Users/Hendrik/git/NIDM/simulation/exports/20200818-195409/data/nunnerbuskens/",
                "C:/Users/Hendrik/git/NIDM/simulation/exports/20200818-195410/data/nunnerbuskens/")
  path.out <- "C:/Users/Hendrik/git/NIDM/simulation/exports/20200819-134542/"

  paths.in <- c("/Users/hendrik/git/uu/nidm/simulation/exports/20200901-152629/data/nunnerbuskens/",
                "/Users/hendrik/git/uu/nidm/simulation/exports/20200901-152630/data/nunnerbuskens/")
  path.out <- "/Users/hendrik/git/uu/nidm/simulation/exports/20200901-152842/"

  names <- c("simulation-summary", "agent-details", "round-summary")

  merge_data_by_paths(paths.in = paths.in, path.out = path.out, names = names)

}









#----------------------------------------------------------------------------------------------------#
# function: remove_exclusions_simulation_summary_data
#     Removes excluded records from simulation summary data.
# return: the simulation summary data cleaned from excluded data records
#----------------------------------------------------------------------------------------------------#
remove_exclusions_simulation_summary_data <- function(data.ss) {
  data.ss <- subset(data.ss, data.ss$net.pathlength.pre.epidemic.av <= 80)
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
# function: load_agent_details_data
#     Loads agent details data.
# return: the agent details data
#----------------------------------------------------------------------------------------------------#
load_agent_details_data <- function() {
  return(load_csv(CSV_AGENT_DETAILS_PATH))
}

#----------------------------------------------------------------------------------------------------#
# function: load_round_summary_data
#     Loads summary data for all simulated NIDM rounds.
# return: the summary data for all simulated NIDM rounds
#----------------------------------------------------------------------------------------------------#
load_round_summary_data <- function(prepare_data = TRUE) {
  return(load_csv(CSV_ROUND_SUMMARY_PATH))
}

#----------------------------------------------------------------------------------------------------#
# function: prepare_round_summary_data
#     Prepares round summary data.
# param:  rsData
#     the round summary data to be prepared
# return: the prepared round summary data
#----------------------------------------------------------------------------------------------------#
prepare_round_summary_data <- function(data.rs = load_round_summary_data()) {

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


prepare_data <-function() {
   data.ss.complete <- load_simulation_summary_data(remove_exclusions = FALSE)
   data.ss.reduced <- load_simulation_summary_data(remove_exclusions = TRUE)

   data.ad.complete <- load_agent_details_data()
   data.ad <- subset(data.ad.complete, sim.uid %in% data.ss.reduced$sim.uid)
   write.csv(data.ad, "/Users/hendrik/git/uu/nidm/simulation/exports/20200901-152842/agent-details-prepared.csv", row.names = FALSE)

   data.rs.complete <- load_round_summary_data(prepare_data = FALSE)
   data.rs <- subset(data.rs.complete, sim.uid %in% data.ss.reduced$sim.uid)
   data.rs.prepared <- prepare_round_summary_data(data.rs = data.rs)
   write.csv(data.rs.prepared, "/Users/hendrik/git/uu/nidm/simulation/exports/20200901-152842/round-summary-prepared.csv", row.names = FALSE)


}







