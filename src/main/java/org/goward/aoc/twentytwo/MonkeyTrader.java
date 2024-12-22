package org.goward.aoc.twentytwo;

import java.util.ArrayList;
import java.util.List;

public class MonkeyTrader {

    long[] secrets = new long[Main.MAX_PRICES];
    long[] prices = new long[Main.MAX_PRICES];
    long[] changes = new long[Main.MAX_PRICES];
    int currLength = 0;
    public MonkeyTrader(String inital) {
        long initialLong = Long.parseLong(inital);
        secrets[currLength] = initialLong;
        prices[currLength] = initialLong%10;
        changes[currLength] = 0L;
        currLength++;
    }

    long getSecret(int index) {
        if(index>0) {
            calculateSecret(index);
        }
        return secrets[index];
    }

    public long getChange(int index) {
        if(index>0) {
            calculateSecret(index);
        }
        return changes[index];
    }

    private void calculateSecret(int index) {
        while(currLength<=index) {
            long secretCalc = secrets[currLength - 1];
            long previousPrice = prices[currLength - 1];
            long mix1 = mix(secretCalc*64, secretCalc);
            secretCalc = prune(mix1);
            long mix2 = mix(Math.floorDiv(secretCalc, 32), secretCalc);
            secretCalc = prune(mix2);
            long mix3 = mix(secretCalc*2048, secretCalc);
            secretCalc = prune(mix3);
            secrets[currLength] = secretCalc;
            long currentPrice = secretCalc % 10;
            prices[currLength] = currentPrice;
            changes[currLength]=currentPrice-previousPrice;
            currLength++;
        }
    }
    private long mix(long number, long secret) {
        return number^secret;
    }
    private long prune(long secret) {
        return secret%16777216;
    }


    public long findFirstPrice(Changes change) {
        for(int idx = 1; idx <= (currLength-4); idx++) {
            if(changes[idx]==change.changes[0] &&
                    changes[idx+1]==change.changes[1] &&
                    changes[idx+2]==change.changes[2] &&
                    changes[idx+3]==change.changes[3]) {
                return prices[idx+3];
            }
        }
        return 0;
    }


}
