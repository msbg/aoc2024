package org.goward.aoc.fourteen;

import java.util.regex.Pattern;

public class Robot {
    final int vecX;
    final int vecY;
    int posX;
    int posY;

    public Robot(String def) {
        String[] posMove = def.split(Pattern.quote(" "));
        posX = Integer.parseInt(posMove[0].substring(2, posMove[0].indexOf(',')));
        posY = Integer.parseInt(posMove[0].substring(posMove[0].indexOf(',')+1));
        vecX = Integer.parseInt(posMove[1].substring(2, posMove[1].indexOf(',')));
        vecY = Integer.parseInt(posMove[1].substring(posMove[1].indexOf(',')+1));

    }
}
