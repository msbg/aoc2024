package org.goward.aoc.twentyfour;

import org.apache.commons.io.FileUtils;
import org.goward.aoc.fifteen.MapWarehouse;
import org.goward.aoc.fifteen.Move;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        List<String> initialVals = FileUtils.readLines(new File("C:\\monica\\projects\\AoC\\src\\24InputInitial3.txt"), StandardCharsets.UTF_8);
        List<String> logicVals = FileUtils.readLines(new File("C:\\monica\\projects\\AoC\\src\\24InputCalc3.txt"), StandardCharsets.UTF_8);

        Circuit circuit = new Circuit(initialVals, logicVals);

        int idx = 0;
        long actualNumber = 0;
        long column = 1;
        while(true) {
            String idxString = "" +idx;
            if(idxString.length()<2) {
                idxString = "0" + idxString;
            }
            String valToCheck = "z" + idxString;
            Circuit.Logic logic = circuit.get(valToCheck);
            if(logic!=null) {
                boolean output = logic.getValue();
                if(output) {
                    actualNumber += column;
                }
                System.out.println(valToCheck + ":" +output + ", valSoFar:" + actualNumber);
                column*=2;
                idx++;
            } else {
                break;
            }
        }
    }
}