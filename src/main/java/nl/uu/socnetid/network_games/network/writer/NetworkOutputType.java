package nl.uu.socnetid.network_games.network.writer;

/**
 * @author Hendrik Nunner
 */
public enum NetworkOutputType {

    EDGE_LIST ("Edge List"),
    ADJACENCY_MATRIX ("Adjacency Matrix");

    private final String name;

    private NetworkOutputType(String s) {
        name = s;
    }

    public boolean equalsName(String otherName) {
        return this.name.equals(otherName);
    }

    @Override
    public String toString() {
       return this.name;
    }
}
