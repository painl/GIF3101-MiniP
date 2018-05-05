package ca.ulaval.ima.mp.game;

public class Potion {

    private Type type;
    private int target;

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

    public enum Type {LIFE, DEATH}
}
