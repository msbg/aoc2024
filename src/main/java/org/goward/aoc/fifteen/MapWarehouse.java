package org.goward.aoc.fifteen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
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
                        row.add(MapTile.WALL);
                    break;
                    case 'O':
                        row.add(MapTile.BOXLEFT);
                        row.add(MapTile.BOXRIGHT);
                        break;
                    case '@':
                        row.add(MapTile.ROBOT);
                        robotX = x;
                        robotY = y;
                        row.add(MapTile.EMPTY);
                        break;
                    default:
                        row.add(MapTile.EMPTY);
                        row.add(MapTile.EMPTY);

                }
                x++;
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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            BoxCoords boxCoords = (BoxCoords) o;
            return x == boxCoords.x && y == boxCoords.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }

    public void moveRobot(Move move, int mapCount) {


        int nextX = move.nextX(robotX);
        int nextY = move.nextY(robotY);
        Set<BoxCoords> boxCoords = getTouchingBoxCoords(Collections.singleton(new BoxCoords(nextX, nextY, MapTile.UNKNOWN)), move);
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

    private Set<BoxCoords> getTouchingBoxCoords(Set<BoxCoords> boxCoordSetUnknown, Move move) {
        Set<BoxCoords> returnSet = new HashSet<>();

        Set<BoxCoords> newChildren = new HashSet<>();
        boxCoordSetUnknown.forEach(boxCoords1 -> {
            if(getMapItem(boxCoords1.x, boxCoords1.y).equals(MapTile.BOXLEFT)) {
                boxCoords1.mapTile = MapTile.BOXLEFT;
                returnSet.add(boxCoords1);
                returnSet.add(new BoxCoords(boxCoords1.x+1, boxCoords1.y, MapTile.BOXRIGHT));
                newChildren.add(new BoxCoords(move.nextX(boxCoords1.x), move.nextY(boxCoords1.y),MapTile.UNKNOWN));
                newChildren.add(new BoxCoords(move.nextX(boxCoords1.x+1), move.nextY(boxCoords1.y),MapTile.UNKNOWN));
            }
            if(getMapItem(boxCoords1.x, boxCoords1.y).equals(MapTile.BOXRIGHT)) {
                boxCoords1.mapTile = MapTile.BOXRIGHT;
                returnSet.add(boxCoords1);
                returnSet.add(new BoxCoords(boxCoords1.x-1, boxCoords1.y, MapTile.BOXLEFT));
                newChildren.add(new BoxCoords(move.nextX(boxCoords1.x), move.nextY(boxCoords1.y),MapTile.UNKNOWN));
                newChildren.add(new BoxCoords(move.nextX(boxCoords1.x-1), move.nextY(boxCoords1.y),MapTile.UNKNOWN));
            }
        });
        //Now we have the full set of box co-ords, pass this down:
        if(!newChildren.isEmpty()) {
            newChildren.removeAll(returnSet);
            Set<BoxCoords> children = getTouchingBoxCoords(newChildren, move);
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
        WALL('#'),BOX('O'),BOXLEFT('['),BOXRIGHT(']'), ROBOT('@'), EMPTY('.'), UNKNOWN('U');
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
                if(map.get(ydx).get(xdx).equals(MapTile.BOXLEFT)) {
                    total+=((ydx*100)+xdx);
                }
            }
        }
        return total;
    }

}
