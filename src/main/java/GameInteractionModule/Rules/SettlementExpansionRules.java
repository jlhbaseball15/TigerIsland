package GameInteractionModule.Rules;

import GameStateModule.*;

import java.util.ArrayList;
import java.util.Set;
import java.util.Stack;

/**
 * Created by johnhenning on 3/22/17.
 */
public class SettlementExpansionRules extends BuildRules{
    public static int getMeeplesRequiredExpansion(GameState gameState, ArrayList<Coordinate> coordinates){
        int meeplesRequired = 0;
        for(Coordinate c: coordinates){
            Hex h = gameState.getHex(c);
            meeplesRequired += h.getLevel();
        }
        return meeplesRequired;
    }

    public static boolean expansionIsValid(Hex hex){
        if(hex != null && hex.getSettlementID() != 0 && isNotVolcano(hex)){
            return true;
        }
        else{
            throw new AssertionError("This is not a proper settlement expansion");
        }
    }
    public static boolean getAdjacentSettlements(Grid gameBoard, Settlement newSettlement){
        int newSettlementID = newSettlement.getSettlementID();
        Hex h;
        boolean adjacencyFound = false;
        ArrayList<Coordinate> adjacentCoordinates = new ArrayList<>();
        for(Coordinate c : newSettlement.getSettlementCoordinates()){
            h = downLeft(gameBoard, c);
            if((h != null) && (h.getSettlementID() != newSettlementID) && (h.getSettlementID() != 0)){
                adjacentCoordinates.add(c);
                h.setSettlementID(newSettlementID);
                adjacencyFound = true;
            }
            h = downRight(gameBoard, c);
            if((h != null) && (h.getSettlementID() != newSettlementID) && (h.getSettlementID() != 0)){
                adjacentCoordinates.add(c);
                h.setSettlementID(newSettlementID);
                adjacencyFound = true;
            }
            h = topRight(gameBoard, c);
            if((h != null) && (h.getSettlementID() != newSettlementID) && (h.getSettlementID() != 0)){
                adjacentCoordinates.add(c);
                h.setSettlementID(newSettlementID);
                adjacencyFound = true;
            }
            h = topLeft(gameBoard, c);
            if((h != null) && (h.getSettlementID() != newSettlementID) && (h.getSettlementID() != 0)){
                adjacentCoordinates.add(c);
                h.setSettlementID(newSettlementID);
                adjacencyFound = true;
            }
            h = leftOfHex(gameBoard, c);
            if((h != null) && (h.getSettlementID() != newSettlementID) && (h.getSettlementID() != 0)){
                adjacentCoordinates.add(c);
                h.setSettlementID(newSettlementID);
                adjacencyFound = true;
            }
            h = rightOfHex(gameBoard, c);
            if((h != null) && (h.getSettlementID() != newSettlementID) && (h.getSettlementID() != 0)){
                adjacentCoordinates.add(c);
                h.setSettlementID(newSettlementID);
                adjacencyFound = true;
            }
        }
        newSettlement.getSettlementCoordinates().addAll(adjacentCoordinates);
        return adjacencyFound;
    }

    public ArrayList<Coordinate> expansionDFS(Grid gameboard, TerrainType terrain, Settlement settlement){
        ArrayList<Coordinate> hexesEncountered = settlement.getSettlementCoordinates();
        ArrayList<Coordinate> newHexesAdded = new ArrayList<>();
        Stack<Coordinate> coords = new Stack();
        coords.addAll(hexesEncountered);

        while(!coords.empty()){
            Coordinate currentAdjacentCoordinate = coords.pop();
            ArrayList<Coordinate> neighboringCoordinates = findAdjacentCoords(gameboard, terrain, currentAdjacentCoordinate);
            for(int i=0; i<neighboringCoordinates.size(); i++){
                if(contains(hexesEncountered, neighboringCoordinates.get(i))== false){
                    newHexesAdded.add(neighboringCoordinates.get(i));
                    hexesEncountered.add(neighboringCoordinates.get(i));
                    coords.push(neighboringCoordinates.get(i));
                }
            }
        }
        return newHexesAdded;
    }

    public static boolean contains(ArrayList<Coordinate> hexEncountered, Coordinate currentCoord){
        for(Coordinate c : hexEncountered){
            if(c.getX() == currentCoord.getX() && c.getY() == currentCoord.getY()){
                return true;
            }
        }
        return false;
    }

    public ArrayList<Coordinate> findAdjacentCoords(Grid gameboard, TerrainType terrain, Coordinate coordinate){
        ArrayList<Coordinate> adjacentCoordinates = new ArrayList<>();
        //we only want to add the coordinates, if they have the same terrain type, and they are unoccupied
        //if the match occurs we need to add the coordinates of that match to the array list
        Hex hex;
        hex = downLeft(gameboard, coordinate);
        if(hex != null && (hex.getTerrain() == terrain) && isUnnocupied(hex))
            adjacentCoordinates.add(hex.getCoordinate());
        hex = downRight(gameboard, coordinate);
        if(hex != null && (hex.getTerrain() == terrain) && isUnnocupied(hex))
            adjacentCoordinates.add(hex.getCoordinate());
        hex = topRight(gameboard, coordinate);
        if(hex != null && (hex.getTerrain() == terrain) && isUnnocupied(hex))
            adjacentCoordinates.add(hex.getCoordinate());
        hex = topLeft(gameboard, coordinate);
        if(hex != null && (hex.getTerrain() == terrain) && isUnnocupied(hex))
            adjacentCoordinates.add(hex.getCoordinate());
        hex = leftOfHex(gameboard, coordinate);
        if(hex != null && (hex.getTerrain() == terrain) && isUnnocupied(hex))
            adjacentCoordinates.add(hex.getCoordinate());
        hex = rightOfHex(gameboard, coordinate);
        if(hex != null && (hex.getTerrain() == terrain) && isUnnocupied(hex))
            adjacentCoordinates.add(hex.getCoordinate());

        return adjacentCoordinates;
    }
}

