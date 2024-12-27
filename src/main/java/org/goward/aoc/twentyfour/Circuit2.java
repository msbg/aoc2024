package org.goward.aoc.twentyfour;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Circuit2 {

    Map<String,Logic> logicMap = new HashMap<>();
    Map<String,Set<Logic>> inputMap = new HashMap<>();
    final Logic[] logicIndexes;
    Set<String> outputNames = new HashSet<>();
    public Circuit2(List<String> initialVals, String[] input1, String[] input2, String[] gateString, String[] outputName) {
        logicIndexes = new Logic[input1.length + initialVals.size()];
        int idx = 0;
        for (String initialVal : initialVals) {
            String[] a = initialVal.split(Pattern.quote(":"));
            String name = a[0];
            boolean value = Integer.parseInt(a[1].trim()) == 1;
            Logic value1 = new Logic(name, value);
            logicIndexes[idx] = value1;
            logicMap.put(name, value1);
            if (value1.outputName.startsWith("z")) {
                outputNames.add(value1.outputName);
            }
            idx++;
        }
        ;
        while(idx < input1.length + initialVals.size()) {
            int i = idx - initialVals.size();
            Logic value = new Logic(outputName[i], Gate.valueOf(gateString[i]), input1[i], input2[i]);
            logicMap.put(outputName[i], value);
            logicIndexes[idx] = value;
            Set<Logic> logicSet = inputMap.computeIfAbsent(value.input1Name, s -> new HashSet<>());
            logicSet.add(value);
            logicSet = inputMap.computeIfAbsent(value.input2Name, s -> new HashSet<>());
            logicSet.add(value);
            if(value.outputName.startsWith("z")) {
                outputNames.add(value.outputName);
                inputMap.computeIfAbsent(value.outputName, s -> new HashSet<>());
            }
            idx++;
    }
        logicMap.values().forEach(new Consumer<Logic>() {
            @Override
            public void accept(Logic logic) {
                logic.input1 = logicMap.get(logic.input1Name);
                logic.input2 = logicMap.get(logic.input2Name);
                logic.outputRefs = inputMap.get(logic.outputName);
            }
        });

    }


    public Logic get(String wireName) {
        return logicMap.get(wireName);
    }

    public void swap(int[][] swaps) {
        for(int[] swap:swaps) {
            Logic a = logicIndexes[swap[0]];
            Logic b = logicIndexes[swap[1]];
            a.swap(b);
            b.swap(a);
        }
    }

    public void invalidate(int[][] swaps) {
        for(Logic logic:logicIndexes) {
            logic.invalidate();
        }
        /*for(int[] swap:swaps) {
            Logic a = logicIndexes[swap[0]];
            Logic b = logicIndexes[swap[1]];
            a.invalidate();
            b.invalidate();
        }*/
    }

    public class Logic {
        final Gate gate;
        Logic input1, input2;
        Logic swappedInput1, swappedInput2;
        String input1Name, input2Name;
        Set<Logic> outputRefs = new HashSet<>();
        final String outputName;

        boolean value;
        boolean calculated = false;
        boolean calculating = false;

        public Logic(String outputName, boolean value) {
            this.gate = Gate.NONE;
            this.calculated = true;
            this.value = value;
            this.outputName = outputName;
        }
        public Logic(String outputName, Gate gate, String input1, String input2) {
            this.gate = gate;
            this.input1Name = input1;
            this.input2Name = input2;
            this.outputName = outputName;
        }


        public boolean getValue() {
            if(!calculated) {
                throw new RuntimeException("Cannot get value - not yet calculated");
            }
            return value;
        }

        boolean calcWithCycleDetection() {
            if(!calculated){
                if(this.calculating) {
                    return true;
                }
                this.calculating = true;
                Logic input1 = getInput1();
                Logic input2 = getInput2();
                if(input1.calcWithCycleDetection() || input2.calcWithCycleDetection()) {
                    return true;
                } else {
                    value = gate.calc(input1.getValue(), input2.getValue());
                }

                calculating = false;
                calculated = true;
            }
            return false;
        }


        private Logic getInput1() {
            if(swappedInput1!=null) {
                return swappedInput1;
            } else {
                return input1;
            }
        }

        private Logic getInput2() {
            if(swappedInput2!=null) {
                return swappedInput2;
            } else {
                return input2;
            }
        }

        public void swap(Logic toSwap) {
            for(Logic swappedResult:outputRefs) {
                swappedResult.swapInput(toSwap, this.outputName);
            }
        }
        public void swapInput(Logic newInputRef, String name) {
            invalidate();
            if(input1.outputName.equals(name)) {
                this.swappedInput1 = newInputRef;
            }
            if(input2.outputName.equals(name)) {
                this.swappedInput2 = newInputRef;
            }
            invalidate();
        }


        public void invalidate() {
            this.calculating = false;
            this.swappedInput1 = null;
            this.swappedInput2 = null;
            if(this.gate==Gate.NONE) {
                return;
            }
            this.calculated = false;
            /*for(Logic outputRef:outputRefs) {
                outputRef.invalidate();
            }*/
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
            Circuit2.Logic logic = get(valToCheck);
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

    boolean[] convertToBinary(long orig) {
        String binaryString = Long.toBinaryString(orig);
        boolean[] output = new boolean[outputNames.size()];

        char[] binaryCharArray = binaryString.toCharArray();
        for(int idx = 0; idx < output.length; idx++) {
            int charIndex = binaryCharArray.length - (1 + idx);
            if(charIndex<0) {
                output[idx]= false;
            } else {
                output[idx] = binaryCharArray[charIndex] == '1';
            }
        }
        return output;
    }
}
