package org.goward.aoc.twentyfour;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) throws IOException {
        List<String> initialVals = FileUtils.readLines(new File("C:\\monica\\projects\\AoC\\src\\24InputInitial3.txt"), StandardCharsets.UTF_8);
        List<String> logicVals = FileUtils.readLines(new File("C:\\monica\\projects\\AoC\\src\\24InputCalc3.txt"), StandardCharsets.UTF_8);

        Main main = new Main();
        main.setup(initialVals, logicVals, 4);

        main.start();
//        main.generateAllPairs();

//        pairSwapSet.forEach(new Consumer<PairSwap>() {
//            @Override
//            public void accept(PairSwap pairSwap) {
//
//                long xVal = circuit.getVal("x");
//                long yVal = circuit.getVal("y");
//                long zVal = circuit.getVal("z");
//                System.out.println("X:" +xVal);
//                System.out.println("Y:" +yVal);
//                System.out.println("X+Y:"  + (xVal+yVal));
//                System.out.println("Z:" +zVal);
//            }
//        });


    }
    int max;
    int[][] swaps;
    int[][] lastSwaps;
    int swapSize;
    private List<String> initialVals;
    List<String> logicVals;
    boolean[] wanted;
    int initialScore;
    Circuit3 circuit;
    void setup(List<String> initialVals, List<String> logicVals, int swapSize) {
        lastSwaps =  new int[swapSize][2];
        swaps =  new int[swapSize][2];
        this.swapSize = swapSize;

        this.initialVals = initialVals;
        this.logicVals = logicVals;

        circuit = createCircuit(initialVals, logicVals);
        max = circuit.wireIndexes.length;
        long x = circuit.getVal("x");
        long y = circuit.getVal("y");
        long out = circuit.getVal("z");
        long z = x + y;
        wanted = circuit.convertToBinary(z);
    }

    void start() {
        Map<Integer,List<int[][]>> scoreMap = new HashMap<>();
        scoreMap.computeIfAbsent(0, _ -> new ArrayList<>()).add(new int[0][0]);
        initialScore = testSwaps(new int[0][0], circuit, wanted);
        int[][] swapsFound = null;
        while(swapsFound==null) {
            Integer maxScoreSoFar = scoreMap.keySet().stream().max(Integer::compareTo).get();
            List<int[][]> nextList = scoreMap.get(maxScoreSoFar);
            int[][] swapbase = nextList.removeFirst();
            if(nextList.isEmpty()) {
                scoreMap.remove(maxScoreSoFar);
            }
            String baseString = Arrays.deepToString(swapbase);
            System.out.println("Testing swaps starting with base:" + baseString + " with a score so far of:" + maxScoreSoFar);
            swapsFound = gen(swapbase, scoreMap);
        }

    }

    int[][] gen(int[][] base, Map<Integer,List<int[][]>> scoreMap) {
        int i = base.length>0?base[base.length - 1][0] + 1:initialVals.size();

        for(int a1dx = i; a1dx < max; a1dx++) {
            for (int a2dx = a1dx + 1; a2dx < max; a2dx++) {
                int[][] swap = new int[base.length+1][4];
                for(int idx = 0;idx < base.length; idx++) {
                    swap[idx][0] = base[idx][0];
                    swap[idx][1] = base[idx][1];
                    swap[idx][2] = base[idx][2];
                    swap[idx][3] = base[idx][3];
                }
                swap[base.length][0] = a1dx;
                swap[base.length][1] = a2dx;
                if (sanityCheck(swap)) {
                    int scoreForSwap = testSwaps(swap, circuit, wanted);
                    if(scoreForSwap==wanted.length) {
                        return swap;
                    }
                    if(swap.length<swapSize) {
                        swap[base.length][2] = scoreForSwap;
                        //only add to map if more swaps are available.
                        if(swap.length==1) {
                            swap[swap.length - 1][3] = scoreForSwap - initialScore;
                        } else {
                            swap[swap.length -1][3] = scoreForSwap - swap[swap.length -2][2];
                        }


                        int totalScore = 0;//scoreForSwap;
                        //totalScore+=(swapSize - swap.length)*(3);
                        /*for (int idx = 0; idx < swap.length; idx++) {
                            totalScore+=((swap[idx][2]) * (swapSize - idx));
                        }*/
                        for (int idx = 0; idx < swap.length; idx++) {
                            totalScore+=((swap[idx][3]) );
                        }
                        totalScore = (totalScore * swapSize)/swap.length;



                        /*for (int idx = 0; idx < swapSize - 1; idx++) {
                            if (swap.length > idx) {
                                totalScore += swap[idx][2];
                            } else {
                                //totalScore += wanted.length;
                            }
                        }*/
                        //totalScore = scoreForSwap;
                        scoreMap.computeIfAbsent(totalScore, _ -> new ArrayList<>()).add(swap);
                    }


                }
            }
        }
        return null;
    }

    void generateAllPairs() {

        int startIndex = initialVals.size();
        for(int a1dx = startIndex; a1dx < max; a1dx++) {
            swaps[0][0] = a1dx;
            swaps[0][1] = -1;
            swaps[1][0] = -1;
            swaps[1][1] = -1;
            swaps[2][0] = -1;
            swaps[2][1] = -1;
            swaps[3][0] = -1;
            swaps[3][1] = -1;
            if (sanityCheck(swaps)) {
                for (int a2dx = a1dx + 1; a2dx < max; a2dx++) {
                    swaps[0][1] = a2dx;
                    swaps[1][0] = -1;
                    swaps[1][1] = -1;
                    swaps[2][0] = -1;
                    swaps[2][1] = -1;
                    swaps[3][0] = -1;
                    swaps[3][1] = -1;
                    if (sanityCheck(swaps)) {
                        for (int b1dx = a1dx + 1; b1dx < max; b1dx++) {
                            swaps[1][0] = b1dx;
                            swaps[1][1] = -1;
                            swaps[2][0] = -1;
                            swaps[2][1] = -1;
                            swaps[3][0] = -1;
                            swaps[3][1] = -1;
                            if (sanityCheck(swaps)) {
                                for (int b2dx = a2dx + 1; b2dx < max; b2dx++) {
                                    swaps[1][1] = b2dx;
                                    swaps[2][0] = -1;
                                    swaps[2][1] = -1;
                                    swaps[3][0] = -1;
                                    swaps[3][1] = -1;
                                    if (sanityCheck(swaps)) {
                                        for (int c1dx = b1dx + 1; c1dx < max; c1dx++) {
                                            swaps[2][0] = c1dx;
                                            swaps[2][1] = -1;
                                            swaps[3][0] = -1;
                                            swaps[3][1] = -1;
                                            if (sanityCheck(swaps)) {
                                                for (int c2dx = b2dx + 1; c2dx < max; c2dx++) {
                                                    swaps[2][1] = c2dx;
                                                    swaps[3][0] = -1;
                                                    swaps[3][1] = -1;
                                                    if (sanityCheck(swaps)) {
                                                        for (int d1dx = c1dx + 1; d1dx < max; d1dx++) {
                                                            swaps[3][0] = d1dx;
                                                            swaps[3][1] = -1;
                                                            for (int d2dx = c2dx + 1; d2dx < max; d2dx++) {
                                                                swaps[3][1] = d2dx;
                                                                if (sanityCheck(swaps)) {
                                                                    testSwaps(swaps, circuit, wanted);
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        for(int[][] swapFound:swapsFound) {
            verify(initialVals, logicVals, swapFound, circuit, wanted);

        }

        //and now check they're /really/ correct..

    }

    private boolean sanityCheck(int[][] swaps) {

        for(int idx = 0; idx<swaps.length; idx++) {

            if(swaps[idx][0]==max ||swaps[idx][1]==max) {
                //breached max
                return false;
            }
            if(swaps[idx][0]!=-1) {
                if(swaps[idx][0]==swaps[idx][1]) {
                    return false; //dupe
                }
                for(int jdx = 0; jdx<swaps.length; jdx++) {
                    if(idx!=jdx) {
                        if(swaps[jdx][0]!=-1) {
                            boolean hasDupe = swaps[idx][0] == swaps[jdx][0];
                            if (hasDupe) {
                                return false;
                            }
                        }
                        if(swaps[jdx][1]!=-1) {
                            boolean hasDupe = swaps[idx][0] == swaps[jdx][1];
                            if (hasDupe) {
                                return false;
                            }
                        }
                    }
                }
            }
            if(swaps[idx][1]!=-1) {
                for(int jdx = 0; jdx<swaps.length; jdx++) {
                    if(idx!=jdx) {
                        if(swaps[jdx][0]!=-1) {
                            boolean hasDupe = swaps[idx][1] == swaps[jdx][0];
                            if (hasDupe) {
                                return false;
                            }
                        }
                        if(swaps[jdx][1]!=-1) {
                            boolean hasDupe = swaps[idx][1] == swaps[jdx][1];
                            if (hasDupe) {
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    private static boolean verify(List<String> initialVals, List<String> logicVals, int[][] swapFound, Circuit3 circuit, boolean[] wanted) {
        List<String> swapPrint = new ArrayList<>();
        Circuit3 newCircuit3 = createCircuit(initialVals, logicVals);
        for (int[] swap : swapFound) {
            System.out.println(circuit.getWireName(swap[0]) + circuit.getWireName(swap[1]));
            swapPrint.add(circuit.getWireName(swap[0]));
            swapPrint.add(circuit.getWireName(swap[1]));
            newCircuit3.swap(swap);
        }
        Collections.sort(swapPrint);
        int val = checkVal(newCircuit3, wanted);
        System.out.println("Swaps:" + swapPrint + " verified:" + (val == wanted.length));
        return val == wanted.length;
    }

    private static Circuit3 createCircuit(List<String> initialVals, List<String> logicVals) {
        String[] input1 = new String[logicVals.size()];
        String[] input2 = new String[logicVals.size()];
        String[] gateString = new String[logicVals.size()];
        String[] output = new String[logicVals.size()];
        Map<String, Integer> outputMap = new HashMap<>();
        for(int idx = 0; idx < logicVals.size(); idx++) {
            String[] a = logicVals.get(idx).split(Pattern.quote(" "));
            input1[idx] = a[0];
            gateString[idx] = a[1];
            input2[idx] = a[2];
            output[idx] = a[4];
            outputMap.put(output[idx], idx);
        }
        Circuit3 circuit = new Circuit3(initialVals, input1, input2, gateString, output);
        return circuit;
    }

    List<int[][]> swapsFound = new ArrayList<>();

    private int testSwaps(int[][] swaps, Circuit3 circuit, boolean[] val) {

        for(int idx = 0; idx < swaps.length; idx++) {
            if(lastSwaps[idx][0]!=swaps[idx][0] ||
                    lastSwaps[idx][1]!=swaps[idx][1]
              ) {
                circuit.invalidateSwap(lastSwaps[idx]);
            }
        }
        for(int idx = 0; idx < swaps.length; idx++) {
            if(lastSwaps[idx][0]!=swaps[idx][0] ||
                    lastSwaps[idx][1]!=swaps[idx][1]
            ) {
                circuit.swap(swaps[idx]);
                lastSwaps[idx][0] = swaps[idx][0];
                lastSwaps[idx][1] = swaps[idx][1];
            }
        }
        int valFound = checkVal(circuit, val);
        if(valFound==val.length) {

            System.out.println("Found swaps:" );
            int[][] copy = Arrays.stream(swaps).map(int[]::clone).toArray(int[][]::new);
            swapsFound.add(copy);
            for(int[]swap: swaps) {
                System.out.println(circuit.getWireName(swap[0]) + circuit.getWireName(swap[1]));

            }
            if(!verify(initialVals, logicVals,swaps, circuit, val)) {
                System.out.println("Mismatch");
            }
        }
        return valFound;
    }

    private static int checkVal(Circuit3 circuit, boolean[] val) {
        int idx = 0;
        int correctOutput = 0;
        while(idx < val.length) {
            Circuit3.Logic logic = circuit.getOutputVal(idx);
            logic.calc();
            if(logic.calculating) {
                break;
            }
            if (val[idx] == logic.value) {
                correctOutput++;
            }
           idx++;
        }
        return correctOutput;
    }

}