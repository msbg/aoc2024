package org.goward.aoc.fifteen;

public class Move {
    final MoveType moveType;
    public Move(Character moveDef) {
        MoveType moveType1 = MoveType.UNKNOWN;
        if(moveDef.equals('<')) {
            moveType1 = MoveType.LEFT;
        }
        if(moveDef.equals('>')) {
            moveType1 = MoveType.RIGHT;
        }
        if(moveDef.equals('^')) {
            moveType1 = MoveType.UP;
        }
        if(moveDef.equals('v')) {
            moveType1 = MoveType.DOWN;
        }

        moveType = moveType1;
    }

    public int nextX(int nextX) {
        return nextX+ moveType.xDelta;
    }

    public int nextY(int nextY) {
        return nextY+ moveType.yDelta;
    }

    enum MoveType {
        LEFT(-1, 0),RIGHT(1,0),UP(0,-1),DOWN(0,1), UNKNOWN(0,0);

        private final int xDelta;
        private final int yDelta;

        MoveType(int xDelta, int yDelta) {
            this.xDelta = xDelta;
            this.yDelta = yDelta;
        }
    }
}
