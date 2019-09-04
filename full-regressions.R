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