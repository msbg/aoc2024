package org.goward.aoc.fourteen;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        List<String> robotDefs = FileUtils.readLines(new File("C:\\monica\\projects\\AoC\\src\\14Robots2.txt"), StandardCharsets.UTF_8);
        List<Robot> robots = robotDefs.stream().map(Robot::new).toList();
        //List<Robot> robots = Collections.singletonList(new Robot("p=2,4 v=2,-3"));
        //Map map = new Map(11,7, robots);
        Map map = new Map(101,103, robots);
        //map.printMap();
        for(int idx = 0; idx < 10000; idx++) {
            map.move();
            if(map.detectXMasTree()) {
                map.printMap();
                System.out.println(idx);
            }
            //map.printMap();
        }
        //map.printMap();
        //map.printSafetyFactor();
    }
}