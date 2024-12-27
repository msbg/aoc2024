package org.goward.aoc.twentyfour;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Circuit {

    Map<String,Logic> logicMap = new HashMap<>();
    Map<String,Set<Logic>> inputMap = new HashMap<>();
    final Logic[] logicIndexes;
    Set<String> outputNames = new HashSet<>();
    public Circuit(List<String> initialVals, String[] input1, String[] input2, String[] gateString, String[] outputName) {

        initialVals.forEach(s -> {
            String [] a = s.split(Pattern.quote(":"));
            String name = a[0];
            boolean value = Integer.parseInt(a[1].trim())==1;
            Logic value1 = new Logic(name, value);
            logicMap.put(name, value1);
            if(value1.outputName.startsWith("z")) {
                outputNames.add(value1.outputName);
            }

        });
        logicIndexes = new Logic[input1.length];
        for(int idx=0; idx < input1.length; idx++) {
            Logic value = new Logic(outputName[idx], Gate.valueOf(gateString[idx]), input1[idx], input2[idx], idx);
            logicMap.put(outputName[idx], value);
            logicIndexes[idx] = value;
            Set<Logic> logicSet = inputMap.computeIfAbsent(value.input1Name, s -> new HashSet<>());
            logicSet.add(value);
            logicSet = inputMap.computeIfAbsent(value.input2Name, s -> new HashSet<>());
            logicSet.add(value);
            if(value.outputName.startsWith("z")) {
                outputNames.add(value.outputName);
            }
    }

    }


    public Logic get(String wireName) {
        return logicMap.get(wireName);
    }

    public class Logic {
        final Gate gate;
        String input1Name,input2Name;
        boolean value;
        boolean calculated = false;
        final String outputName;
        final int index;

        public Logic(String outputName, boolean value) {
            this.gate = Gate.NONE;
            this.calculated = true;
            this.value = value;
            this.outputName = outputName;
            this.index = -1;
        }
        public Logic(String outputName, Gate gate, String input1Name,String input2Name, int index) {
            this.gate = gate;
            this.input1Name = input1Name;
            this.input2Name = input2Name;
            this.outputName = outputName;
            this.index = index;
        }

        public boolean getValue() {
            if(!calculated){
                Logic input1 = logicMap.get(input1Name);
                Logic input2 = logicMap.get(input2Name);
                value = gate.calc(input1.getValue(), input2.getValue());
                calculated = true;
            }
            return value;
        }

        public int getIndex() {
            return index;
        }
    }

    public enum Gate {
        XOR,OR,AND, NONE;

        public boolean calc(boolean a, boolean b) {
            if(this.equals(AND)) {
                return a && b;
            }
            if(this.equals(OR)) {
                return a || b;
            }
            if(this.equals(XOR)) {
                return a ^ b;
            }
            throw new RuntimeException("Gate calc requested on type 'NONE'!");
        }
    }

    long getVal(String prefix) {
        int idx = 0;
        long actualNumber = 0;
        long column = 1;
        while(true) {
            String idxString = "" +idx;
            if(idxString.length()<2) {
                idxString = "0" + idxString;
            }

            String valToCheck = prefix + idxString;
            Circuit.Logic logic = get(valToCheck);
            if(logic!=null) {
                boolean output = logic.getValue();
                if(output) {
                    actualNumber += column;
                }
                //System.out.println(valToCheck + ":" +output + ", valSoFar:" + actualNumber);
                column*=2;
                idx++;
            } else {
                break;
            }
        }
        return actualNumber;
    }

    Map<String, Boolean> hasCycleCache = new HashMap<>();
    boolean hasCycles(int[][] swaps) {
        for(int[] swap:swaps) {
            Arrays.sort(swap);
        }
        Arrays.sort(swaps, Arrays::compare);
        StringBuffer cacheBuff = new StringBuffer();
        for(int idx =0; idx <swaps.length; idx++) {
            cacheBuff.append( swaps[idx][0] + ":" + swaps[idx][1]+ ",");
        }

        String pairKey = cacheBuff.toString();

        if(hasCycleCache.containsKey(pairKey)) {
            return hasCycleCache.get(pairKey);
        }
        if(swaps.length>1) {
            for (int idx = 0; idx < swaps.length; idx++) {
                if (hasCycles(new int[][]{swaps[idx]})) {
                    return true;
                }
            }
        }
        if(swaps.length>2) {
            for(int jdx = 0;jdx < swaps.length - 2; jdx++ ) {
                for (int idx = jdx; idx < swaps.length - 1; idx++) {
                    if (hasCycles(new int[][]{swaps[jdx], swaps[idx + 1]})) {
                        return true;
                    }
                }
            }
        }

        Set<Logic> allLogic = outputNames.stream().map((Function<String, Logic>) s -> getLogicFromOutputName(s, swaps)).collect(Collectors.toSet());
        Set<Logic> newLogic = new HashSet<>(allLogic);
        Set<Logic> tempLogic = new HashSet<>();
        while( !newLogic.isEmpty()) {
            for(Logic logic:newLogic) {
                if(logic.gate!=Gate.NONE) {
                    tempLogic.add(getLogicFromOutputName(logic.input1Name, swaps));
                    tempLogic.add(getLogicFromOutputName(logic.input2Name, swaps));
                }
            }
            if (tempLogic.stream().anyMatch(allLogic::contains)) {
                hasCycleCache.put(pairKey, true);
                return true;
            }
            newLogic.clear();
            newLogic.addAll(tempLogic);
            allLogic.addAll(tempLogic);
            tempLogic.clear();
        }
        hasCycleCache.put(pairKey, false);
        return false;
    }

    Logic getLogicFromOutputName(String outputName, int[][] swaps) {
        Logic initialLogic = logicMap.get(outputName);
        if(initialLogic==null) {
            System.out.println("asd");
        }
        if(initialLogic.gate==Gate.NONE) {
            return initialLogic;
        }
        int index = getSwappedIndex(swaps, initialLogic);

        return logicIndexes[index];
    }

    private static int getSwappedIndex(int[][] swaps, Logic initialLogic) {
        int index = initialLogic.getIndex();

        for(int[] swap: swaps) {
            if(swap[0]==index) {
                index = swap[1];
            }
            if(swap[1]==index) {
                index = swap[0];
            }
        }
        return index;
    }
}
