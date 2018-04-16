package ca.ulaval.ima.mp.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ca.ulaval.ima.mp.game.roles.Psychic;
import ca.ulaval.ima.mp.game.roles.Salvater;
import ca.ulaval.ima.mp.game.roles.Villager;
import ca.ulaval.ima.mp.game.roles.Witch;
import ca.ulaval.ima.mp.game.roles.Wolf;

public class Game {

    private final List<Player> players;

    public Game(List<String> names) {
        this.players = this.initPlayers(names);
    }

    private List<Player> initPlayers(List<String> names) {
        List<Player> players = new ArrayList<>();
        Collections.shuffle(names);
        if (names.size() == 3) {
            players.add(new Player(this.getNextId(), names.get(0), new Wolf()));
            players.add(new Player(this.getNextId(), names.get(1), new Salvater()));
            players.add(new Player(this.getNextId(), names.get(2), new Psychic()));
        }
        else if (names.size() == 4) {
            players.add(new Player(this.getNextId(), names.get(0), new Wolf()));
            players.add(new Player(this.getNextId(), names.get(1), new Wolf()));
            players.add(new Player(this.getNextId(), names.get(2), new Witch()));
            players.add(new Player(this.getNextId(), names.get(3), new Psychic()));
        }
        else {
            players.add(new Player(this.getNextId(), names.get(0), new Wolf()));
            players.add(new Player(this.getNextId(), names.get(1), new Wolf()));
            players.add(new Player(this.getNextId(), names.get(2), new Witch()));
            players.add(new Player(this.getNextId(), names.get(3), new Psychic()));
            players.add(new Player(this.getNextId(), names.get(4), new Salvater()));
            for (int it = 5; it < names.size(); it++) {
                players.add(new Player(this.getNextId(), names.get(it), new Villager()));
            }
        }
        return players;
    }

    private int getNextId() {
        int id = -1;
        for (Player player: this.players) {
            if (player.getId() > id)
                id = player.getId();
        }
        id++;
        return id;
    }
}
