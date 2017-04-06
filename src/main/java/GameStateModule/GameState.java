package GameStateModule;
import GameInteractionModule.Rules.*;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by johnhenning on 3/19/17.
 */
public class GameState {
    private Grid gameboard;
    private Player currentPlayer;
    private Player player1;
    private Player player2;
    private ArrayList<Settlement> settlementList;
    private int settlementIDCount;

    public GameState(){
        gameboard = new Grid(200);
        player1 = new Player();
        player2 = new Player();
        settlementList = new ArrayList<Settlement>();

        settlementIDCount = 0;
        currentPlayer = player1;
        placeTile(Tile.getInitialTile());
    }

    public void foundSettlement(Coordinate coordinate, Player player) {
      //TODO: Add Victory Points
        Hex h = gameboard.getHexFromCoordinate(coordinate);
        if (SettlementFoundationRules.isValidFoundation(h, player)) {
            player.removeMeeple();
            placeMeeple(coordinate);
            Settlement settlement = new Settlement(coordinate,player, ++settlementIDCount);
            mergeSettlements(settlement);
        } else {
            throw new AssertionError();
        }
}

    public void expandSettlement(Coordinate coordinate, Player player, TerrainType terrainType) {
        Hex hex = getHex(coordinate);
        ArrayList<Coordinate> hexesToPlaceMeeplesOn = new ArrayList<>();
        if (SettlementExpansionRules.expansionIsValid(hex)) {
            Settlement settlement = getSettlementByID(hex.getSettlementID());
            final Settlement beforeExpansion = settlement;
            hexesToPlaceMeeplesOn = SettlementExpansionRules.expansionDFS(gameboard, terrainType, settlement);
            if(BuildRules.checkPlayerHasEnoughMeeples(player, SettlementExpansionRules.getMeeplesRequiredExpansion(this, hexesToPlaceMeeplesOn))){
                expansionPlaceMeeples(hexesToPlaceMeeplesOn, player);
            }
            else{
                //TODO: Can we think of a way to throw from here so AI knows its expansion wasn't valid
                settlement = beforeExpansion;
            }
            mergeSettlements(settlement);
        }
        else{
            throw new AssertionError();
        }

    }

    public Settlement getSettlementByID(int settlementID) {
        for (Settlement settlement : settlementList) {
            if ( settlement.getSettlementID() == settlementID ) {
                return settlement;
            }
        }
        return null;
    }

    public boolean placeTile(Tile tile) {
        try {
            gameboard.placeTile(tile);
            return true;
        } catch (AssertionError e) {
            return false;
        }
    }

    public boolean levelTile(Tile tile) {
        TileNukeRules.bigDivideSettlements(gameboard, settlementList, tile, ++settlementIDCount);
        cleanSettlements();
        gameboard.levelTile(tile);
        return true;
    }
    public void cleanSettlements(){
        for(int i = 0; i < settlementList.size(); i++){
            if(settlementList.get(i).getSettlementCoordinates().size() == 0) {
                settlementList.remove(i--);
            }
        }
    }
    public  void expansionPlaceMeeples(ArrayList<Coordinate> coordinates, Player player){
        for(Coordinate c : coordinates){
            int meeplesPlaced = placeMeeple(c);
            player.removeMeeple(meeplesPlaced);
        }
    }
    
    public int placeMeeple(Coordinate coordinate) {
        Hex hex = gameboard.getHexFromCoordinate(coordinate);
        int level = hex.getLevel();
        hex.addMeeple(level);
        return level;
    }

    public void placeTotoro(Coordinate coordinate) {
        //TODO: fixplz
        Hex hex = gameboard.getHexFromCoordinate(coordinate);
        assert TotoroBuildRules.isValidTotoroLocation(hex, currentPlayer,this);
        hex.addTotoro();
        Settlement settlement = new Settlement(coordinate, currentPlayer, ++settlementIDCount);
        mergeSettlements(settlement);

    }

    public void placeTiger(Coordinate coordinate) {
        Hex hex = gameboard.getHexFromCoordinate(coordinate);
        assert TigerBuildRules.canPlaceTiger(hex, this);
        hex.addTiger();
        Settlement settlement = new Settlement(coordinate, currentPlayer, ++settlementIDCount);
        mergeSettlements(settlement);
    }

    public ArrayList<Settlement> getSettlementList() {
        return settlementList; 
    }

    public Hex getHex(Coordinate coordinate) { 
        return gameboard.getHexFromCoordinate(coordinate); 
    }

    public Grid getGameboard() {
        return gameboard;
    }



    public void switchPlayer() {
        if (currentPlayer == player1) {
            currentPlayer = player2;
        } else {
            currentPlayer = player1;
        }
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public int getSettlementIDCount() {
        return settlementIDCount;
    }

    private void mergeSettlements(Settlement newSettlement) {
        ArrayList<Coordinate> adjacentCoordinates = newSettlement.getSettlementCoordinates();
        ArrayList<Settlement> playersSettlements = BuildRules.settlementsOfPlayer(settlementList, newSettlement.getOwner());
        playersSettlements.remove(newSettlement);
        settlementList.remove(newSettlement);
        for(Settlement s: playersSettlements){
            int newSettlmentID = newSettlement.getSettlementID();
            if(s.areCoordinatesAdjacent(adjacentCoordinates) && (newSettlmentID != 0 || newSettlmentID != s.getSettlementID())) {
                adjacentCoordinates.addAll(s.getSettlementCoordinates());
                settlementList.remove(s);
            }
        }
        Settlement combinedSettlements = new Settlement(adjacentCoordinates, newSettlement.getOwner(), newSettlement.getSettlementID());
        for(Coordinate c : combinedSettlements.getSettlementCoordinates()){
            Hex h = getHex(c);
            h.setSettlementID(newSettlement.getSettlementID());
        }
        settlementList.add(combinedSettlements);
    }


    public void printHexSummary(int x, int y) {
        //TODO: don't kill me plz
        //printing tiles placed with coords
        gameboard.getHexFromCoordinate(new Coordinate(x, y)).printHex();
    }
    public void printPlacedTiles(){
        gameboard.printLog();
    }
    public void printSettlementSummary(){
        for(Settlement s: settlementList){
            s.printSettlementInfo();
        }
    }
}
