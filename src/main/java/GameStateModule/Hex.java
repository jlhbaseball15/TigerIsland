package GameStateModule;

/**
 * Created by johnhenning on 3/15/17.
 */
public class Hex {
    private final Coordinate coordinate;
    private final TerrainType terrain;
    private int tileIndex;
    private int level;
    private int MeepleCount;
    private boolean Totoro;
    private boolean Tiger;

    public Hex(int x, int y, TerrainType terrain) {
        this.coordinate = new Coordinate(x,y);
        this.terrain = terrain;
        this.MeepleCount = 0;
        this.Totoro = false;
        this.Tiger = false;
    }

    public int getx() {
        return coordinate.getX();
    }

    public int gety() {
        return coordinate.getY();
    }

    public void setLevel(int level) { this.level = level; }

    public TerrainType getTerrain() {
        return terrain;
    }

    public int getLevel() {return level; }

    public int getTileIndex() {
        return tileIndex;
    }

    public int getMeepleCount() { return MeepleCount; }

    public boolean hasTotoro() { return Totoro; }

    public boolean hasTiger() { return Tiger; }

    public void setTileIndex(int tileIndex) {
        this.tileIndex = tileIndex;
    }

    public void addMeeple(int level) {
        MeepleCount += level;
    }

    public void addTotoro() {
        Totoro = true;
    }

}
