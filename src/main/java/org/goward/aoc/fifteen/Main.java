package org.goward.aoc.fifteen;

import org.apache.commons.io.FileUtils;


import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws IOException {
        List<String> mapDef = FileUtils.readLines(new File("C:\\monica\\projects\\AoC\\src\\15InputMap2.txt"), StandardCharsets.UTF_8);
        MapWarehouse mapWarehouse = new MapWarehouse(mapDef);
        List<String> moveDefs = FileUtils.readLines(new File("C:\\monica\\projects\\AoC\\src\\15InputMoves2.txt"), StandardCharsets.UTF_8);

        char[] moveChars = String.join("", moveDefs).toCharArray();
        List<Move> moves = new ArrayList<>();
        for (char moveChar : moveChars) {
            moves.add(new Move(moveChar));
        }
        int moveCount = 0;
        for(Move move:moves) {
            mapWarehouse.moveRobot(move, moveCount);
            mapWarehouse.printMap(moveCount);
            moveCount++;
        }

        System.out.println("boxCount:" + mapWarehouse.boxCount());

    }
}