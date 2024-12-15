package org.goward.aoc.fifteen;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class MapWarehouse {
    private int robotY;
    private int robotX;
    List<List<MapTile>> map = new ArrayList<>();
    public MapWarehouse(List<String> mapDef) {
        int x = 0;
        int y = 0;
        for(String mapLine:mapDef) {
            List<MapTile> row = new ArrayList<>();
            for (char mapItem:mapLine.toCharArray()) {
                switch (mapItem) {
                    case '#':
                        row.add(MapTile.WALL);
                    break;
                    case 'O':
                        row.add(MapTile.BOX);
                        break;
                    case '@':
                        row.add(MapTile.ROBOT);
                        robotX = x;
                        robotY = y;
                        break;
                    default:
                        row.add(MapTile.EMPTY);

                }
                x++;
            }
            map.add(row);
            x = 0;
            y++;
        }
    }

    public void printMap(int mapCount) {
        System.out.println("Map:" + mapCount);
        for(int ydx = 0; ydx < map.size(); ydx++) {
            for(int xdx = 0; xdx < map.get(ydx).size(); xdx++) {
                System.out.print(map.get(ydx).get(xdx).print());
            }
            System.out.print("\n");
        }
    }

    class BoxCoords {
        int x;
        int y;
        MapTile mapTile;

        public BoxCoords(int x, int y, MapTile mapTile) {
            this.x = x;
            this.y = y;
            this.mapTile = mapTile;
        }
    }

    public void moveRobot(Move move, int mapCount) {


        int nextX = move.nextX(robotX);
        int nextY = move.nextY(robotY);
        Set<BoxCoords> boxCoords = getTouchingBoxCoords(new BoxCoords(nextX, nextY, MapTile.UNKNOWN), move);
        if(!getMapItem(nextX, nextY).equals(MapTile.WALL)&& boxCoords.stream().noneMatch(boxCoords1 -> getMapItem(move.nextX(boxCoords1.x), move.nextY(boxCoords1.y)).equals(MapTile.WALL))) {
            //We're not going to hit a wall here, so actually move.
            //Mark all old positions as empty
            setMapItem(robotX, robotY, MapTile.EMPTY);
            boxCoords.forEach(boxCoordsItem -> setMapItem(boxCoordsItem.x, boxCoordsItem.y, MapTile.EMPTY));
            //Update with correct tile data
            setMapItem(nextX, nextY, MapTile.ROBOT);
            boxCoords.forEach(boxCoordsItem -> setMapItem(move.nextX(boxCoordsItem.x), move.nextY(boxCoordsItem.y), boxCoordsItem.mapTile));

        }

    }

    private Set<BoxCoords> getTouchingBoxCoords(BoxCoords boxCoords, Move move) {
        Set<BoxCoords> returnSet = new HashSet<>();
        if(getMapItem(boxCoords.x, boxCoords.y).equals(MapTile.BOX)) {
            boxCoords.mapTile = MapTile.BOX;
            returnSet.add(boxCoords);
            Set<BoxCoords> children = getTouchingBoxCoords(new BoxCoords(move.nextX(boxCoords.x), move.nextY(boxCoords.y),MapTile.UNKNOWN) , move);
            returnSet.addAll(children);
        }
        return returnSet;
    }

    private MapTile getMapItem(int x, int y) {
        return map.get(y).get(x);
    }

    private void setMapItem(int x, int y, MapTile mapTile) {
        map.get(y).set(x, mapTile);
        if(mapTile.equals(MapTile.ROBOT)) {
            robotX = x;
            robotY = y;
        }
    }


    enum MapTile {
        WALL('#'),BOX('O'),ROBOT('@'), EMPTY('.'), UNKNOWN('U');
        private final char representation;

        MapTile(char representation) {
            this.representation = representation;
        }
        public String print() {
            return String.valueOf(representation);
        }
    }

    long boxCount() {
        int total= 0;
        for(int ydx = 0; ydx < map.size(); ydx++) {
            for(int xdx = 0; xdx < map.get(ydx).size(); xdx++) {
                if(map.get(ydx).get(xdx).equals(MapTile.BOX)) {
                    total+=((ydx*100)+xdx);
                }
            }
        }
        return total;
    }

    class MoveData {
        int x,y;

    }
}
