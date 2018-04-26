package ca.ulaval.ima.mp.game.roles;

public abstract class Role implements IRole {

    public enum Side {VILLAGER, WEREWOLF}

    public enum Type {VILLAGER, WITCH, SALVATER, PSYCHIC, WEREWOLF}

    private final String    name;
    private final String    photoName;
    private final Side      side;
    private final boolean   male;
    private final Type      type;

    protected Role(String name, String photoName, Side side, boolean male, Type type) {
        this.name = name;
        this.photoName = photoName;
        this.side = side;
        this.male = male;
        this.type = type;
    }

    public Side getSide() {
        return side;
    }

    public String getPhotoName() {
        return photoName;
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
