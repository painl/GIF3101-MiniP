package ca.ulaval.ima.mp.game.roles;

import java.util.ArrayList;
import java.util.List;

import ca.ulaval.ima.mp.game.Player;

public class Psychic extends Role {

    private List<Player> seen;

    public Psychic() {
        super("Voyante", "psychic.png", Side.VILLAGER, false, Type.PSYCHIC);
        this.seen = new ArrayList<>();
    }

    public void addSeen(Player player) {
        this.seen.add(player);
    }

    public List<Player> getSeen() {
        return seen;
    }
}
