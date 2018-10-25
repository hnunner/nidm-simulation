package nl.uu.socnetid.netgame.io.types;

/**
 * @author Hendrik Nunner
 */
public enum NetworkProperties {

//  agentsDetailsCSVCols.add("net.stats.stable");
//  agentsDetailsCSVCols.add("net.stats.density");
//  agentsDetailsCSVCols.add("net.stats.density.pre.epidemic");
//  agentsDetailsCSVCols.add("net.stats.density.post.epidemic");
//  agentsDetailsCSVCols.add("net.stats.av.degree");
//  agentsDetailsCSVCols.add("net.stats.av.degree.pre.epidemic");
//  agentsDetailsCSVCols.add("net.stats.av.degree.post.epidemic");
//  agentsDetailsCSVCols.add("net.stats.av.clustering");
//  agentsDetailsCSVCols.add("net.stats.av.clustering.pre.epidemic");
//  agentsDetailsCSVCols.add("net.stats.av.clustering.post.epidemic");
//  agentsDetailsCSVCols.add("net.stats.ties.broken.during.epidemic");

  STABLE("net.prop.stable"),
  DENSITY("net.prop.density"),
  DENSITY_PRE("net.prop.density.pre.epidemic"),
  DENSITY_POST("net.prop.density.post.epidemic"),
  AV_DEGREE("net.prop.av.degree"),
  AV_DEGREE_PRE("net.prop.av.degree.pre.epidemic"),
  AV_DEGREE_POST("net.prop.av.degree.post.epidemic"),
  AV_CLUSTERING("net.prop.av.clustering"),
  AV_CLUSTERING_PRE("net.prop.av.clustering.pre.epidemic"),
  AV_CLUSTERING_POST("net.prop.av.clustering.post.epidemic"),
  TIES_BROKEN_EPIDEMIC("net.prop.ties.broken.epidemic");

  // the name
  private String name;

  /**
   * Constructor, setting the name.
   *
   * @param name
   *          the name of the enum
   */
  NetworkProperties(String name) {
      this.name = name;
  }

  /*
   * (non-Javadoc)
   * @see java.lang.Enum#toString()
   */
  @Override
  public String toString() {
      return name;
  }
}

