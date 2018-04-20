package ca.ulaval.ima.mp.game;

public class Potion {

    public enum Type {LIFE, DEATH}

    private Type    type;
    private int     target;

    public Potion(Type type, int target) {
        this.type = type;
        this.target = target;
    }

    public Type getType() {
        return type;
    }

    public int getTarget() {
        return target;
    }
}
