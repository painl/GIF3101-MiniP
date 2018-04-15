package ca.ulaval.ima.mp.game.roles;

public abstract class Role implements IRole {

    public enum Side {VILLAGER, WOLF}

    public enum Type {VILLAGER, WITCH, SALVATER, PSYCHIC, WOLF}

    private final String    name;
    private final String    photoURL;
    private final Side      side;
    private final boolean   male;
    private final Type      type;

    protected Role(String name, String photoURL, Side side, boolean male, Type type) {
        this.name = name;
        this.photoURL = photoURL;
        this.side = side;
        this.male = male;
        this.type = type;
    }

    public Side getSide() {
        return side;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public String getName() {
        return name;
    }

    public boolean getmale() {
        return male;
    }

    public Type getType() {
        return type;
    }
}
