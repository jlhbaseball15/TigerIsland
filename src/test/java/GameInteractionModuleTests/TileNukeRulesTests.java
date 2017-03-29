package GameInteractionModuleTests;

import GameInteractionModule.Rules.TileNukeRules;
import GameStateModule.*;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

/**
 * Created by Kyle on 3/28/2017.
 */
public class TileNukeRulesTests{
    private ArrayList<Settlement> overlappedSettlment;
    private ArrayList<Settlement> validSettlment;
    private Player player1;

    private ArrayList<Coordinate> coordinates;
    private ArrayList<Coordinate> secondSettlementCoords;

    private ArrayList<TerrainType> terrains;
    private Tile tile;


    @Before
    public void setup() throws Exception{
        overlappedSettlment = new ArrayList<>();
        validSettlment = new ArrayList<>();
        coordinates = new ArrayList<>();
        terrains = new ArrayList<>();
        player1 = new Player();
        secondSettlementCoords = new ArrayList<>();

        coordinates.add(new Coordinate(100,100));
        coordinates.add(new Coordinate(101,101));
        coordinates.add(new Coordinate(100,101));

        terrains.add(TerrainType.VOLCANO);
        terrains.add(TerrainType.GRASSLAND);
        terrains.add(TerrainType.JUNGLE);

        overlappedSettlment.add(new Settlement(new Coordinate(100,100), player1));

        secondSettlementCoords.add(new Coordinate(100, 100));
        secondSettlementCoords.add(new Coordinate(101, 100));

        validSettlment.add(new Settlement(secondSettlementCoords, player1));

        tile = new Tile(coordinates,terrains);
    }

    @Test
    public void coordsOverlapTest(){
//        TileNukeRules.doCoordinatesOverlap(
    }

}
