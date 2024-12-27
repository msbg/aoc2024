package org.goward.aoc.twentyfour;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class Circuit3 {

    final List<Wire> wireList = new ArrayList<>();
    final List<Wire> outputWireList = new ArrayList<>();
    final Map<String, Wire> wireMap = new HashMap<>();

    final Wire[] outputWires;
    final Wire[] wireIndexes;
    //Set<String> outputNames = new HashSet<>();
    public Circuit3(List<String> initialVals, String[] input1, String[] input2, String[] gateString, String[] outputName) {

        for (String initialVal : initialVals) {
            String[] a = initialVal.split(Pattern.quote(":"));
            String name = a[0];
            addWire(name);
        }

        for(String outputN: outputName) {
            addWire(outputN);
        }
        //now connect the wires to logic gates:
        for (String initialVal : initialVals) {
            String[] a = initialVal.split(Pattern.quote(":"));
            String name = a[0];
            boolean value = Integer.parseInt(a[1].trim()) == 1;

            Wire outputWire = wireMap.get(name);
            Logic inputLogic = new Logic(value, outputWire);
            outputWire.setInput(inputLogic);
        }

        for(int idx=0; idx < outputName.length; idx++) {

            Wire outputWire = wireMap.get(outputName[idx]);
            Wire input1Wire = wireMap.get(input1[idx]);
            Wire input2Wire = wireMap.get(input2[idx]);

            Logic value = new Logic(Gate.valueOf(gateString[idx]), input1Wire, input2Wire, outputWire);
            outputWire.setInput(value);
            input1Wire.addOutput(value);
            input2Wire.addOutput(value);

        }

        wireIndexes = wireList.toArray(new Wire[0]);
        outputWires = outputWireList.toArray(new Wire[0]);

    }

    private void addWire(String name) {
        Wire wire = new Wire(name);
        wireMap.put(name, wire);
        if (name.startsWith("z")) {
            Integer outputIndex = Integer.parseInt(name.substring(1));
            while(outputWireList.size()<=outputIndex) {
                outputWireList.add(null);
            }
            outputWireList.set(outputIndex, wire);
        } else{
            wireList.add(wire);
        }
    }

    public void swap(int[] swap) {
            Wire a = wireIndexes[swap[0]];
            Wire b = wireIndexes[swap[1]];
            a.swap(b);
            b.swap(a);
    }

    public void invalidateSwap(int[] swap) {
            Wire a = wireIndexes[swap[0]];
            Wire b = wireIndexes[swap[1]];
            a.invalidate();
            a.invalidateSwap();
            b.invalidate();
            b.invalidateSwap();

    }

    public String getWireName(int idx) {
        return wireIndexes[idx].wireName;
    }

    public class Logic {
        final Gate gate;
        final Wire input1, input2, output;

        boolean value;
        boolean calculated = false;
        boolean calculating = false;

        public Logic(boolean value, Wire output) {
            this.gate = Gate.NONE;
            this.calculated = true;
            this.value = value;
            this.input1 = null;
            this.input2 = null;
            this.output = output;
        }
        public Logic(Gate gate, Wire input1, Wire input2, Wire output) {
            this.gate = gate;
            this.input1 = input1;
            this.input2 = input2;
            this.output = output;
        }

        void calc() {
            if(!calculated){
                if(calculating) {
                    return;
                }
                calculating = true;
                input1.input.calc();
                input2.input.calc();
                calculating = input1.input.calculating || input2.input.calculating;
                value = gate.calc(input1.input.value, input2.input.value);
                calculated = true;

            }
        }

        public void invalidate() {
            this.calculating = false;
            if(this.gate== Gate.NONE) {
                return;
            }
            if(calculated) {
                this.calculated = false;
                output.invalidate();
            }
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
            Wire logic = wireMap.get(valToCheck);
            if(logic!=null) {
                logic.input.calc();
                boolean output = logic.input.value;
                if(output) {
                    actualNumber += column;
                }
                System.out.println(valToCheck + ":" + output);
                //System.out.println(valToCheck + ":" +output + ", valSoFar:" + actualNumber);
                column*=2;
                idx++;
            } else {
                break;
            }
        }
        return actualNumber;
    }

    Logic getOutputVal(int idx) {
        return outputWires[idx].input;
    }

    boolean[] convertToBinary(long orig) {
        String binaryString = Long.toBinaryString(orig);
        boolean[] output = new boolean[outputWires.length];

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

    class Wire {
        Logic origInput;
        Logic input;
        Logic[] outputList = new Logic[0];
        String wireName;
        Wire swappedWire;

        public Wire(String name) {
            this.wireName = name;
        }


        public void swap(Wire swappedWire) {
            invalidate();
            input = swappedWire.origInput;
            this.swappedWire = swappedWire;
        }

        public void invalidateSwap() {
            input = origInput;
            swappedWire=null;
        }

        public void invalidate() {
            for(Logic origOutput: outputList) {
                origOutput.invalidate();
            }
            if(swappedWire!=null) {
                for(Logic origOutput: swappedWire.outputList) {
                    origOutput.invalidate();
                }
            }
        }

        public void setInput(Logic inputLogic) {
            origInput = inputLogic;
            input = inputLogic;
        }

        public void addOutput(Logic value) {
            List<Logic> newLogic = new ArrayList<>(Arrays.asList(outputList));
            newLogic.add(value);
            outputList = newLogic.toArray(new Logic[0]);
        }

    }
}
