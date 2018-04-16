package ca.ulaval.ima.mp.game.roles;

public class Witch extends Role {

    private boolean lifePotion;
    private boolean deathPotion;

    public Witch() {
        super("Sorcière", "witch.png", Side.VILLAGER, false, Type.WITCH);
        this.lifePotion = true;
        this.deathPotion = true;
    }

    public boolean hasLifePotion() {
        return lifePotion;
    }

    public void setLifePotion(boolean lifePotion) {
        this.lifePotion = lifePotion;
    }

    public boolean hasDeathPotion() {
        return deathPotion;
    }

    public void setDeathPotion(boolean deathPotion) {
        this.deathPotion = deathPotion;
    }
}
