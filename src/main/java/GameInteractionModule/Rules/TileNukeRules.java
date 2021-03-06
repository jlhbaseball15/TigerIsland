package GameInteractionModule.Rules;

import GameStateModule.*;

import java.util.*;

/**
 * Created by johnhenning on 3/22/17.
 */
public class TileNukeRules extends Rules {

    public static void isValidNuke(Tile tile, Hex[][] gameboard, GameState gameState){
        CheckLowerHexesAreSameLevel(tile,gameboard);
        CheckHexesSpanMultipleTiles(tile, gameboard);
        CheckVolcanoesLineUp(tile,gameboard);
        CheckTileNotContainTotoro(tile, gameboard);
        CheckTileNotContainTiger(tile, gameboard);
        if(CheckNukeDoesNotWipeoutSettlement(tile, gameState)) throw new AssertionError("This location cannot be nuked");
    }

    public static int getNewTileLevel(Tile tile, Hex[][] gameboard){
        int lowerLevel = CheckLowerHexesAreSameLevel(tile, gameboard);
        return lowerLevel+1;
    }
    public static int CheckLowerHexesAreSameLevel(Tile tile, Hex[][] gameboard) {
        Hex hex0 = tile.getHexes().get(0);
        Hex hex1 = tile.getHexes().get(1);
        Hex hex2 = tile.getHexes().get(2);

        Hex lower_hex0 = gameboard[hex0.getx()][hex0.gety()];
        Hex lower_hex1 = gameboard[hex1.getx()][hex1.gety()];
        Hex lower_hex2 = gameboard[hex2.getx()][hex2.gety()];

        if (lower_hex0 == null || lower_hex1 == null || lower_hex2 == null) { throw new AssertionError("There aren't hexes on each of these coordinates"); }

        int lowerLevel0 = lower_hex0.getLevel();
        int lowerLevel1 = lower_hex1.getLevel();
        int lowerLevel2 = lower_hex2.getLevel();

        if (lowerLevel0 == lowerLevel1 && lowerLevel1 == lowerLevel2) {
            return lowerLevel0;
        } else {
            throw new AssertionError("Hexes aren't on same plane");
        }
    }

    public static boolean CheckHexesSpanMultipleTiles(Tile tile, Hex[][] gameboard) {
        Hex hex0 = tile.getHexes().get(0);
        Hex hex1 = tile.getHexes().get(1);
        Hex hex2 = tile.getHexes().get(2);

        Hex hex_zero = gameboard[hex0.getx()][hex0.gety()];
        Hex hex_one = gameboard[hex1.getx()][hex1.gety()];
        Hex hex_two = gameboard[hex2.getx()][hex2.gety()];

        if (hex_zero == null || hex_one == null || hex_two == null) { return false; }

        int tileIndex0 = hex_zero.getTurnPlaced();
        int tileIndex1 = hex_one.getTurnPlaced();
        int tileIndex2 = hex_two.getTurnPlaced();

        if(tileIndex0 == tileIndex1 && tileIndex1 == tileIndex2){
            throw new AssertionError("Hex is over the same tile");
        }

        return true;

    }

    public static boolean CheckVolcanoesLineUp(Tile tile, Hex[][] gameboard) {

        for (Hex hex : tile.getHexes()) {
            if (hex.getTerrain() == TerrainType.VOLCANO) {
                if (gameboard[hex.getx()][hex.gety()] != null && gameboard[hex.getx()][hex.gety()].getTerrain() == TerrainType.VOLCANO) { return true; }
            }
        }
        throw new AssertionError("Volcanoes don't line up");
    }

    public static boolean CheckTileNotContainTotoro(Tile tile, Hex[][]gameboard){
        Hex hex0 = tile.getHexes().get(0);
        Hex hex1 = tile.getHexes().get(1);
        Hex hex2 = tile.getHexes().get(2);

        Hex hex_zero = gameboard[hex0.getx()][hex0.gety()];
        Hex hex_one = gameboard[hex1.getx()][hex1.gety()];
        Hex hex_two = gameboard[hex2.getx()][hex2.gety()];

        if ((hex_zero != null && hex_zero.hasTotoro()) || (hex_one != null && hex_one.hasTotoro())
                || (hex_two != null && hex_two.hasTotoro())){
            throw new AssertionError("Tile does not contain totoro");
        }
        return true;

    }

    public static boolean CheckTileNotContainTiger( Tile tile, Hex[][] gameboard ){
        Hex hex0 = tile.getHexes().get(0);
        Hex hex1 = tile.getHexes().get(1);
        Hex hex2 = tile.getHexes().get(2);

        Hex hex_zero = gameboard[hex0.getx()][hex0.gety()];
        Hex hex_one = gameboard[hex1.getx()][hex1.gety()];
        Hex hex_two = gameboard[hex2.getx()][hex2.gety()];

        if((hex_zero != null && hex_zero.hasTiger()) || (hex_one != null && hex_one.hasTiger())
        || (hex_two != null && hex_two.hasTiger())){
            throw new AssertionError("Tile does not contain tiger");
        }
        return true;

    }

    public static boolean CheckNukeDoesNotWipeoutSettlement(Tile tile, GameState gameState){
//        ArrayList<Coordinate> tileCoords = tile.getCoords();
        ArrayList<Settlement> settlementsInDanger = new ArrayList<>();
//        for(Settlement s: settlementsInDanger){
//            s.getSettlementCoordinates();
//        }
//        return true
        ArrayList<Hex> hexes = new ArrayList<>();
        for(Hex h : tile.getHexes()){
            if(h.getTerrain() != TerrainType.VOLCANO){
                hexes.add(h);
            }
        }
        Hex h = gameState.getHex(hexes.get(0).getCoordinate());
        Hex h2 = gameState.getHex(hexes.get(1).getCoordinate());
        if(h.getSettlementID() != h2.getSettlementID()){
            settlementsInDanger.add(gameState.getSettlementByID(h.getSettlementID()));
            settlementsInDanger.add(gameState.getSettlementByID(h2.getSettlementID()));
        }
        else {
        settlementsInDanger.add(gameState.getSettlementByID(h.getSettlementID()));
        }
        settlementsInDanger = getSettlementsThatCouldBeWipedOut(settlementsInDanger);

        for(Settlement s : settlementsInDanger){
            if(s.getSettlementCoordinates().size() == 2){
                if(settlmentContainsCoordinate(s, h.getCoordinate()) && settlmentContainsCoordinate(s, h2.getCoordinate())){
                    return true;
                }
            }
            else if(s.getSettlementCoordinates().size() == 1){
                if(settlmentContainsCoordinate(s, h.getCoordinate()) || settlmentContainsCoordinate(s, h2.getCoordinate())){
                    return true;
                }

            }
        }

        return false;
    }


    private static ArrayList<Settlement> getSettlementsThatCouldBeWipedOut(ArrayList<Settlement> settlementList){
        ArrayList<Settlement> smallSettlements = new ArrayList<>();
        for(Settlement s: settlementList){
            if(s != null && s.getSettlementCoordinates().size() < 3)
                smallSettlements.add(s);
        }
        return smallSettlements;
    }



    public static boolean settlmentContainsCoordinate(Settlement settlement, Coordinate coordinate){
        for(Coordinate c : settlement.getSettlementCoordinates()){
            if(c.equals(coordinate)){
                return true;
            }
        }
        return false;
    }
    public static ArrayList<Coordinate> findAdjacentSettlementCoords(Grid gameBoard, Coordinate coordinate, Settlement settlement){
        ArrayList<Coordinate> adjacentCoordinates = new ArrayList<>();
        Hex hex;

        hex = downLeft(gameBoard, coordinate);
        if(hex != null && settlmentContainsCoordinate(settlement, hex.getCoordinate()))
            adjacentCoordinates.add(hex.getCoordinate());
        hex = downRight(gameBoard, coordinate);
        if(hex != null && settlmentContainsCoordinate(settlement, hex.getCoordinate()))
            adjacentCoordinates.add(hex.getCoordinate());
        hex = topRight(gameBoard, coordinate);
        if(hex != null && settlmentContainsCoordinate(settlement, hex.getCoordinate()))
            adjacentCoordinates.add(hex.getCoordinate());
        hex = topLeft(gameBoard, coordinate);
        if(hex != null && settlmentContainsCoordinate(settlement, hex.getCoordinate()))
            adjacentCoordinates.add(hex.getCoordinate());
        hex = leftOfHex(gameBoard, coordinate);
        if(hex != null && settlmentContainsCoordinate(settlement, hex.getCoordinate()))
            adjacentCoordinates.add(hex.getCoordinate());
        hex = rightOfHex(gameBoard, coordinate);
        if(hex != null && settlmentContainsCoordinate(settlement, hex.getCoordinate()))
            adjacentCoordinates.add(hex.getCoordinate());
        return adjacentCoordinates;
    }




}
