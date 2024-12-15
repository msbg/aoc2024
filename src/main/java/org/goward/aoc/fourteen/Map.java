package org.goward.aoc.fourteen;

import java.util.List;

public class Map {
    final int sizeX;
    final int sizeY;
    private final List<Robot> robots;
    private int[][] currentMap;

    public Map(int sizeX, int sizeY, List<Robot> robots) {
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.robots = robots;
    }

    public void move() {
        currentMap = new int[sizeX][sizeY];
        robots.forEach(robot -> {
            robot.posX+= robot.vecX;
            robot.posY+= robot.vecY;
            if(robot.posX>=sizeX) {
                robot.posX-=sizeX;
            }
            if(robot.posX<0) {
                robot.posX+=sizeX;
            }
            if(robot.posY>=sizeY) {
                robot.posY-=sizeY;
            }
            if(robot.posY<0) {
                robot.posY+=sizeY;
            }
            currentMap[robot.posX][robot.posY]++;
        });
    }

    public void printMap() {
        System.out.println("Map");
        for(int ydx = 0; ydx < sizeY; ydx++) {
            for(int xdx = 0; xdx < sizeX; xdx++) {
                long count = getCountInPosition(xdx, ydx);
                System.out.print(count>0?count:".");
            }
            System.out.print("\n");
        }
    }

    private long getCountInPosition(int x, int y) {
        return currentMap[x][y];
        //return robots.stream().filter(robot -> robot.posX == x && robot.posY == y).count();
    }

    public void printSafetyFactor() {
        long quadTL = getQuadCount(0, (sizeX - 1)/2, 0, (sizeY - 1)/2);
        long quadTR = getQuadCount((sizeX +1)/2,sizeX , 0, (sizeY - 1)/2);
        long quadBL = getQuadCount(0, (sizeX - 1)/2, (sizeY + 1)/2, sizeY);
        long quadBR = getQuadCount((sizeX +1)/2,sizeX , (sizeY + 1)/2, sizeY);
        System.out.println("TL:" + quadTL);
        System.out.println("TR:" + quadTR);
        System.out.println("BL:" + quadBL);
        System.out.println("BR:" + quadBR);
        System.out.println("Safety factor:" + (quadTL*quadTR*quadBL*quadBR));
    }

    private long getQuadCount(int xMin, int xMax, int yMin, int yMax) {
        int quadCount = 0;
        for(int xdx = xMin; xdx< xMax; xdx++) {
            for(int ydx = yMin; ydx< yMax; ydx++) {
                quadCount+=getCountInPosition(xdx,ydx);
            }
        }
        return quadCount;
    }

    public boolean detectXMasTree() {
        int lineCount = 0;
        int maxLineCount = 0;
        for(int xdx = 0; xdx< sizeX; xdx++) {
            for(int ydx = 0; ydx< sizeY; ydx++) {
                boolean hasRobot=getCountInPosition(xdx,ydx)>0;
                if(hasRobot) {
                    lineCount++;
                }else {
                    maxLineCount = Math.max(maxLineCount, lineCount);
                    lineCount=0;
                }
            }
        }
        lineCount = 0;
        for(int ydx = 0; ydx< sizeY; ydx++) {
            for(int xdx = 0; xdx< sizeX; xdx++) {

                boolean hasRobot=getCountInPosition(xdx,ydx)>0;
                if(hasRobot) {
                    lineCount++;
                }else {
                    maxLineCount = Math.max(maxLineCount, lineCount);;
                    lineCount=0;
                }
            }
        }
        return maxLineCount>8;
    }
}
