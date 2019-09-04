exportAttackRateModelsComplete <- function(ssData = loadSimulationSummaryData()) {

  # MAIN EFFECTS
  # CIDM parameters
  beta  <- meanCenter(ssData$net.param.beta)
  mu    <- meanCenter(ssData$dis.param.mu)
  sigma <- meanCenter(ssData$dis.param.s / 50)
  r     <- meanCenter(ssData$net.param.r)
  N     <- meanCenter(ssData$net.param.N / 50)
  iota  <- meanCenter(ssData$net.param.net.empty)
  # network properties
  deg1  <- meanCenter(ssData$act.prop.net.degree.order.1 / (ssData$net.param.N-1))
  dens  <- meanCenter(ssData$net.prop.density.pre.epidemic)
  # INTERACTION EFFECTS
  intBetaMu     <- (beta - mean(beta))    *   (mu - mean(mu))
  intBetaSigma  <- (beta - mean(beta))    *   (sigma - mean(sigma))
  intBetaR      <- (beta - mean(beta))    *   (r - mean(r))
  intBetaN      <- (beta - mean(beta))    *   (N - mean(N))
  intBetaIota   <- (beta - mean(beta))    *   (iota - mean(iota))
  intBetaDens   <- (beta - mean(beta))    *   (dens - mean(dens))
  intBetaDeg1   <- (beta - mean(beta))    *   (deg1 - mean(deg1))
  # combinations of mu
  intMuSigma    <- (mu - mean(mu))        *   (sigma - mean(sigma))
  intMuR        <- (mu - mean(mu))        *   (r - mean(r))
  intMuN        <- (mu - mean(mu))        *   (N - mean(N))
  intMuIota     <- (mu - mean(mu))        *   (iota - mean(iota))
  intMuDens     <- (mu - mean(mu))        *   (dens - mean(dens))
  intMuDeg1     <- (mu - mean(mu))        *   (deg1 - mean(deg1))
  # combinations of sigma
  intSigmaR     <- (sigma - mean(sigma))  *   (r - mean(r))
  intSigmaN     <- (sigma - mean(sigma))  *   (N - mean(N))
  intSigmaIota  <- (sigma - mean(sigma))  *   (iota - mean(iota))
  intSigmaDens  <- (sigma - mean(sigma))  *   (dens - mean(dens))
  intSigmaDeg1  <- (sigma - mean(sigma))  *   (deg1 - mean(deg1))
  # combinations of r
  intRN         <- (r - mean(r))          *   (N - mean(N))
  intRIota      <- (r - mean(r))          *   (iota - mean(iota))
  intRDens      <- (r - mean(r))          *   (dens - mean(dens))
  intRDeg1      <- (r - mean(r))          *   (deg1 - mean(deg1))
  # combinations of N
  intNIota      <- (N - mean(N))          *   (iota - mean(iota))
  intNDens      <- (N - mean(N))          *   (dens - mean(dens))
  intNDeg1      <- (N - mean(N))          *   (deg1 - mean(deg1))
  # combinations of iota
  intIotaDens   <- (iota - mean(iota))    *   (dens - mean(dens))
  intIotaDeg1   <- (iota - mean(iota))    *   (deg1 - mean(deg1))
  # combinations of density
  intDensDeg1   <- (dens - mean(dens))  *   (deg1 - mean(deg1))

  ### 2-LEVEL LOGISTIC REGRESSIONS    ###
  ### level 2: parameters combination ###
  ### level 1: simulation runs        ###
  # null-model
  reg00 <- glmer(ssData$dis.prop.pct.rec/100 ~
                   1 +
                   (1 | sim.param.upc),
                 family = binomial,
                 data = ssData)
  # main effects: varied CIDM parameters
  reg1Main <- glmer(ssData$dis.prop.pct.rec/100 ~
                      #  model parameters
                      beta + mu + sigma + r + N + iota +
                      (1 | sim.param.upc),
                    family = binomial,
                    data = ssData)
  # main effects: varied CIDM parameters + network properties at the time of the first infection
  reg2Main <- glmer(ssData$dis.prop.pct.rec/100 ~
                      #  model parameters
                      beta + mu + sigma + r + N + iota +
                      # network properties
                      dens + deg1 +
                      (1 | sim.param.upc),
                    family = binomial,
                    data = ssData)
  # interaction effects
  reg2Int <- glmer(ssData$dis.prop.pct.rec/100 ~
                     #  model parameters
                     beta + mu + sigma + r + N + iota +
                     # network properties
                     dens + deg1 +
                     # interaction effects
                     intBetaMu +
                     intBetaSigma +
                     intBetaR +
                     intBetaN +
                     intBetaIota +
                     intBetaDens +
                     intBetaDeg1 +
                     intMuSigma +
                     intMuR +
                     intMuN +
                     intMuIota +
                     intMuDens +
                     intMuDeg1 +
                     intSigmaR +
                     intSigmaN +
                     intSigmaIota +
                     intSigmaDens +
                     intSigmaDeg1 +
                     intRN +
                     intRIota +
                     intRDens +
                     intRDeg1 +
                     intNIota +
                     intNDens +
                     intNDeg1 +
                     intIotaDens +
                     intIotaDeg1 +
                     intDensDeg1 +
                     (1 | sim.param.upc),
                   family = binomial,
                   data = ssData)
  exportModels(list(reg00,reg1Main,reg2Main,reg2Int), "reg-attack-rate-complete")
}

