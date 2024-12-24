package org.goward.aoc.twentyfour;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Circuit {

    Map<String,Logic> logicMap = new HashMap<>();

    public Circuit(List<String> initialVals, List<String> logicVals) {
        initialVals.forEach(s -> {
            String [] a = s.split(Pattern.quote(":"));
            String name = a[0];
            boolean value = Integer.parseInt(a[1].trim())==1;
            logicMap.put(name, new Logic(value));
        });
        logicVals.forEach(new Consumer<String>() {
            @Override
            public void accept(String s) {
                String[] a = s.split(Pattern.quote(" "));
                String input1Name = a[0];
                String gateString = a[1];
                String input2Name = a[2];
                String outputName = a[4];
                logicMap.put(outputName, new Logic(Gate.valueOf(gateString), input1Name, input2Name));
            }
        });
    }


    public Logic get(String wireName) {
        return logicMap.get(wireName);
    }

    public class Logic {
        final Gate gate;
        String input1Name,input2Name;
        boolean value;
        boolean calculated = false;

        public Logic(boolean value) {
            this.gate = Gate.NONE;
            this.calculated = true;
            this.value = value;
        }
        public Logic(Gate gate, String input1Name,String input2Name) {
            this.gate = gate;
            this.input1Name = input1Name;
            this.input2Name = input2Name;
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
}
