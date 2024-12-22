package org.goward.aoc.twentytwo;

import org.apache.commons.io.FileUtils;
import org.goward.aoc.fifteen.MapWarehouse;
import org.goward.aoc.fifteen.Move;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Main {

    public static final int MAX_PRICES = 2001;

    public static void main(String[] args) throws IOException {

        List<String> inputDef = FileUtils.readLines(new File("C:\\monica\\projects\\AoC\\src\\22Input2.txt"), StandardCharsets.UTF_8);
        List<MonkeyTrader> monkeyTraders = inputDef.stream().map(MonkeyTrader::new).toList();
//        long total = 0;
//        for(MonkeyTrader monkeyTrader:monkeyTraders) {
//            long result = monkeyTrader.getSecret(2000);
//            System.out.println(monkeyTrader.getSecret(0) + ": " +result);
//            total+=result;
//        }
//        System.out.println("Total:" + total);

        Set<Changes> changesSet = new HashSet<>();
        for(MonkeyTrader monkeyTrader:monkeyTraders) {
            for(int start = 1; start <= (MAX_PRICES - 4); start++) {
                long change1 = monkeyTrader.getChange(start);
                long change2 = monkeyTrader.getChange(start+1);
                long change3 = monkeyTrader.getChange(start+2);
                long change4 = monkeyTrader.getChange(start+3);
                changesSet.add(new Changes(change1, change2, change3, change4));
            }
        }
        int changesetSize = changesSet.size();
        System.out.println("Total changes to check:" + changesetSize);
        long maxBananas = 0;
        Changes changeMax = null;
        int changesetCount = 0;
        int percentComplete = 0;
        for(Changes changes:changesSet) {
            long bananas = getBananas(monkeyTraders, changes);
            if(maxBananas<bananas) {
                maxBananas = bananas;
                changeMax = changes;
            }
            changesetCount++;
            int percent = (changesetCount*100/changesetSize);
            if(percent!=percentComplete) {
                percentComplete = percent;
                System.out.println("Checked " + percentComplete + "% - " + changesetCount + "/" + changesetSize);
            }
        }
        System.out.println("Max Bananas=" + maxBananas);
        System.out.println("Change=" + Arrays.toString(changeMax.changes));


    }

    private static long getBananas(List<MonkeyTrader> monkeyTraders, Changes changes) {
        long total = 0;
        for(MonkeyTrader monkeyTrader:monkeyTraders) {
            long price = monkeyTrader.findFirstPrice(changes);
            total += price;
        }
        return total;
    }
}