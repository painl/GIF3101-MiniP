package ca.ulaval.ima.mp.game.roles;

import ca.ulaval.ima.mp.game.Player;

public class Salvater extends Role {

    private Player lastProtected;

    public Salvater() {
        super("Salvateur", "salvater.png", Side.VILLAGER, true, Type.SALVATER);
        this.lastProtected = null;
    }

    public Player getLastProtected() {
        return lastProtected;
    }

    public void setLastProtected(Player lastProtected) {
        this.lastProtected = lastProtected;
    }
}
