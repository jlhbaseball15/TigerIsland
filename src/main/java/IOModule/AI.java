package IOModule;

import GameInteractionModule.Turn;
import GameStateModule.*;
import ServerModule.Adapter;

import java.util.*;

/**
 * Created by johnhenning on 4/4/17.
 */
public class AI implements Player {
    private ArrayList<Coordinate> validTotoroCoordinates;
    private ArrayList<Coordinate> validTigerCoordinates;
    private Random r;
//    private ArrayList<Coordinate>

    public AI() {
        validTotoroCoordinates = new ArrayList<>();
        validTigerCoordinates = new ArrayList<>();
        r = new Random();
    }
    @Override
    public void completeTurn(Message message, GameState gameState) {
        //Calculate Tile Placement
        calculateRightMostCoordinate(gameState);
        ArrayList<Coordinate> tilePlacement = calculateTilePlacement(message.tile, gameState);
        Tile tile = message.tile;
        tile.setCoordinates(tilePlacement);
        Turn.makeTileMove(tile, gameState);

        //Calculate Build Move
        BuildMove buildMove = calculateBuildMove(gameState);
        message.buildMove = buildMove;
        Turn.makeBuildMove(buildMove, gameState);

        //Send updated message back to server
        sendMessageToServer(message);
    }

    public Message DecisionTree(Message message, GameState gameState, double nearSettlementProb) {
        if (canPlaceNearSettlement(gameState)) {
            double placeNearSettlement = r.nextDouble();
            if (placeNearSettlement <= nearSettlementProb) {
                return placeTileNearSettlement(message, gameState);
            } else {
                return placeTileRandomly(message, gameState);
            }

        } else {
            return placeTileRandomly(message, gameState);
        }

    }

    private Message placeTileNearSettlement(Message message, GameState gameState) {

    }

    private Message placeTileRandomly(Message message, GameState gameState) {
        ArrayList<Tile> validTiles = calculateValidTilePlacements(gameState);
        int tileIndex = r.nextInt(validTiles.size());
        message.tile = validTiles.get(tileIndex);

        if (canPlaceTotoro(gameState)) {
            return placeTotoro(message, gameState);
        } else if (canPlaceTiger(gameState)) {
            return placeTiger(message, gameState);
        } else {
            double expandProb = 0.65;
            double foundProb = 0.75;
            return expandSettlement(message, gameState, expandProb, foundProb);
        }
    }

    private Message expandSettlement(Message message, GameState gameState, double expandProb, double foundProb) {
        double expand = r.nextDouble();
        if(expand <= expandProb) {
            return calculateExpansion(message, gameState);
        } else {
            return foundSettlement(foundProb);
        }
    }

    private Message foundSettlement(double foundProb) {

    }

    private Message calculateExpansion(Message message, GameState gameState) {

    }

    private Message placeTiger(Message message, GameState gameState) {
        int coordinateIndex = r.nextInt(validTigerCoordinates.size());
        message.buildMove.buildMoveType = BuildMoveType.PLACETIGER;
        message.buildMove.coordinate = validTigerCoordinates.get(coordinateIndex);

        validTigerCoordinates.remove(coordinateIndex);

        return message;
    }

    private boolean canPlaceTiger(GameState gameState) {

    }

    private Message placeTotoro(Message message, GameState gameState) {
        int coordinateIndex = r.nextInt(validTotoroCoordinates.size());
        message.buildMove.buildMoveType = BuildMoveType.PLACETOTORO;
        message.buildMove.coordinate = validTotoroCoordinates.get(coordinateIndex);

        validTotoroCoordinates.remove(coordinateIndex);

        return message;
    }

    private boolean canPlaceTotoro(GameState gameState) {
        if (validTotoroCoordinates.size() > 0) {
            return true;
        } else {
            findValidTotoroPlacements(gameState);
            if (validTotoroCoordinates.size() > 0) {
                return true;
            }

            return false;
        }
    }

    private void findValidTotoroPlacements(GameState gameState) {

    }

    private ArrayList<Tile> calculateValidTilePlacements(Message message, GameState gameState) {
        ArrayList<Coordinate> nullCoordinates = getAdjacentNullCoordinates(gameState);
        ArrayList<TerrainType> terrains = new ArrayList<>();
        for (Hex hex : message.tile.getHexes()) {
            terrains.add(hex.getTerrain());
        }
        ArrayList<Tile> validTiles = new ArrayList<>();
        for (Coordinate coordinate : nullCoordinates) {
            for (int i = 1; i <= 6; i++) {
                Coordinate [] coordinates  = Adapter.getCoordinatesOfOpponentsTile(coordinate, i);
                boolean coordinatesAreNull = gameState.getHex(coordinates[0]) == null
                        && gameState.getHex(coordinates[1]) == null;
                if (coordinatesAreNull) {
                    ArrayList<Coordinate> validCoordinates = new ArrayList<>();
                    validCoordinates.add(coordinate);
                    validCoordinates.add(coordinates[0]);
                    validCoordinates.add(coordinates[1]);
                    Tile tile = new Tile(validCoordinates, terrains);
                    validTiles.add(tile);
                }
            }
        }
        return validTiles;
    }

    private ArrayList<Coordinate> getAdjacentNullCoordinates(GameState gameState) {
        ArrayList<Coordinate> nullCoordinates = new ArrayList<>();

        for (Tile tile : gameState.getGameboard().getPlacedTiles()) {
            for (Hex hex : tile.getHexes()) {
                ArrayList<Coordinate> coordinates = gameState.getGameboard().getNeighborHexes(hex);
                for (Coordinate coordinate : coordinates) {
                    if (gameState.getHex(coordinate) == null) {
                        nullCoordinates.add(coordinate);
                    }
                }
            }
        }
        nullCoordinates = Coordinate.removeDuplicates(nullCoordinates);
        return nullCoordinates;
    }

    private boolean canPlaceNearSettlement(GameState gameState) {
        return true;
    }


    private ArrayList<Coordinate> calculateTilePlacement(Tile tile, GameState gameState) {
        Coordinate rightMostCoordinate = gameState.getRightMostCoordinate();
        ArrayList<Coordinate> coordinates = new ArrayList<>();
        if (rightMostCoordinate.getY() % 2 == 0) {
            coordinates.add(new Coordinate(rightMostCoordinate.getX() + 1, rightMostCoordinate.getY()));
            coordinates.add(new Coordinate(rightMostCoordinate.getX() + 2, rightMostCoordinate.getY()));
            coordinates.add(new Coordinate(rightMostCoordinate.getX() + 1, rightMostCoordinate.getY() - 1));

        } else {
            coordinates.add(new Coordinate(rightMostCoordinate.getX() + 1, rightMostCoordinate.getY()));
            coordinates.add(new Coordinate(rightMostCoordinate.getX() + 2, rightMostCoordinate.getY()));
            coordinates.add(new Coordinate(rightMostCoordinate.getX() + 2, rightMostCoordinate.getY() - 1));
        }
        return coordinates;
    }

    private BuildMove calculateBuildMove(GameState gameState) {
        ArrayList<Tile> placedTiles = gameState.getGameboard().getPlacedTiles();
        Tile mostRecentTile = placedTiles.get(placedTiles.size() - 1);
        Coordinate coordinate = new Coordinate(0,0);

        int min = 0;
        int max = 3;

        Random rand = new Random();
        int buildmove = rand.nextInt((max - min) + 1) + min;

        switch (buildmove) {
            case 0: //Totoro Placement
                BuildMove totoroBuildMove = calculateTotoroMove(gameState);
                if (totoroBuildMove != null) return totoroBuildMove;

            case 1: //Tiger Placement
                BuildMove tigerBuildMove = calculateTigerBuildMove(gameState);
                if (tigerBuildMove != null) return tigerBuildMove;

            case 2: //Expand Settlement
                BuildMove expansionBuildMove = calculateExpansionBuildMove(gameState);
                if (expansionBuildMove != null) return expansionBuildMove;

            case 3: //Found Settlement
                BuildMove foundationBuildMove = calculateFoundationBuildMove(gameState);
                return foundationBuildMove;
            default:
                return new BuildMove(null, null, null);
        }

    }

    private BuildMove calculateFoundationBuildMove(GameState gameState) {
        ArrayList<Coordinate> unoccupiedCoordinates = gameState.getUnoccupiedCoordinates();
        Random rand = new Random();
        int randomCoordinateIndex = rand.nextInt(unoccupiedCoordinates.size());
        Coordinate coordinate = unoccupiedCoordinates.get(randomCoordinateIndex);
        BuildMove buildMove = new BuildMove(BuildMoveType.FOUNDSETTLEMENT, coordinate, null);
        return buildMove;
    }

    private BuildMove calculateExpansionBuildMove(GameState gameState) {
        Random rand = new Random();
        ArrayList<Settlement> settlements = gameState.getSettlementList();
        Collections.shuffle(settlements);

        for (Settlement settlement : settlements) {
            boolean playerHasEnoughMeeples = gameState.getCurrentPlayer().getNumMeeples() > 0;
            boolean isOwnedByAI = settlement.getOwner() == gameState.getCurrentPlayer();

            if ( isOwnedByAI && playerHasEnoughMeeples) {
                ArrayList<Coordinate> unoccupiedCoordinates = gameState.getNeighborUnoccupiedCoordinates(settlement);
                TerrainType selectedTerrainType = selectTerrainType(unoccupiedCoordinates, gameState);
                return new BuildMove(BuildMoveType.EXPANDSETTLEMENT, settlement.getSettlementCoordinates().get(0), selectedTerrainType);
            }
        }
        return null;
    }

    private TerrainType selectTerrainType(ArrayList<Coordinate> coordinates, GameState gameState) {
        int [] terrainCounts = new int[4];
        for (Coordinate coordinate : coordinates) {
            TerrainType terrainType = gameState.getHex(coordinate).getTerrain();
            switch (terrainType) {
                case LAKE:
                    terrainCounts[0]++;
                    break;
                case JUNGLE:
                    terrainCounts[1]++;
                    break;
                case GRASSLAND:
                    terrainCounts[2]++;
                    break;
                case ROCKY:
                    terrainCounts[3]++;
                    break;
                default:
                    break;
            }
        }

        int maxIndex = 0;
        for (int i = 1; i < terrainCounts.length; i++) {
            if (terrainCounts[i] > terrainCounts[maxIndex]) {
                maxIndex = i;
            }
        }

        switch (maxIndex) {
            case 0:
                return TerrainType.LAKE;
            case 1:
                return TerrainType.JUNGLE;
            case 2:
                return TerrainType.GRASSLAND;
            case 3:
                return TerrainType.ROCKY;
            default:
                return null;
        }

    }

    private BuildMove calculateTigerBuildMove(GameState gameState) {
        Random rand = new Random();
        ArrayList<Settlement> settlements = gameState.getSettlementList();
        Collections.shuffle(settlements);

        for (Settlement settlement : settlements) {

            boolean isLargeEnough = settlement.getSize() >= 5;
            boolean doesNotHaveTiger = settlement.hasTiger() == false;
            boolean playerHasTiger = gameState.getCurrentPlayer().getNumTigers() > 0;
            boolean isOwnedByAI = settlement.getOwner() == gameState.getCurrentPlayer();

            if ( isOwnedByAI && isLargeEnough && doesNotHaveTiger && playerHasTiger ) {
                ArrayList<Coordinate> unoccupiedCoordinates = gameState.getNeighborUnoccupiedCoordinates(settlement);
                int randomHexIndex = rand.nextInt((unoccupiedCoordinates.size()));
                Coordinate unoccupiedCoordinate = unoccupiedCoordinates.get(randomHexIndex);
                return new BuildMove(BuildMoveType.PLACETIGER, unoccupiedCoordinate, null);
            }
        }
        return null;
    }

    private BuildMove calculateTotoroMove(GameState gameState) {
        Random rand = new Random();
        ArrayList<Settlement> settlements = gameState.getSettlementList();
        Collections.shuffle(settlements);

        for (Settlement settlement : settlements) {

            boolean isLargeEnough = settlement.getSize() >= 5;
            boolean doesNotHaveTotoro = settlement.hasTotoro() == false;
            boolean playerHasTotoro = gameState.getCurrentPlayer().getNumTotoros() > 0;
            boolean isOwnedByAI = settlement.getOwner() == gameState.getCurrentPlayer();

            if ( isOwnedByAI && isLargeEnough && doesNotHaveTotoro && playerHasTotoro ) {
                ArrayList<Coordinate> unoccupiedCoordinates = gameState.getNeighborUnoccupiedCoordinates(settlement);
                int randomHexIndex = rand.nextInt((unoccupiedCoordinates.size()));
                Coordinate unoccupiedCoordinate = unoccupiedCoordinates.get(randomHexIndex);
                return new BuildMove(BuildMoveType.PLACETOTORO, unoccupiedCoordinate, null);
            }
        }
        return null;
    }


    private void sendMessageToServer(Message message) {

    }

    private void calculateRightMostCoordinate(GameState gameState) {
        Coordinate rightMostCoordinate = gameState.getRightMostCoordinate();
        ArrayList<Tile> placedTiles = gameState.getGameboard().getPlacedTiles();
        Tile mostRecentTile = placedTiles.get(placedTiles.size() - 1);
        for (Hex hex : mostRecentTile.getHexes()) {
            boolean largerRowAndColumn = hex.getCoordinate().getX() > rightMostCoordinate.getX()
                    && hex.getCoordinate().getY() > rightMostCoordinate.getY();
            boolean evenRowSameColumn = hex.getCoordinate().getX() == rightMostCoordinate.getX()
                    && (rightMostCoordinate.getY() % 2 == 0);
            if (largerRowAndColumn || evenRowSameColumn) {
                rightMostCoordinate = hex.getCoordinate();
            }
        }
    }
}
