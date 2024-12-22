package org.goward.aoc.twentytwo;

import java.util.Arrays;
import java.util.Objects;

public class Changes {
    long[] changes = new long[4];
    public Changes(long change1, long change2, long change3, long change4) {
        changes[0] = change1;
        changes[1] = change2;
        changes[2] = change3;
        changes[3] = change4;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Changes changes1 = (Changes) o;
        return changes[0]==changes1.changes[0] &&
                changes[1]==changes1.changes[1] &&
                changes[2]==changes1.changes[2] &&
                changes[3]==changes1.changes[3]
                ;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(changes);
    }
}
