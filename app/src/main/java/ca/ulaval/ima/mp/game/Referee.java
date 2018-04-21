package ca.ulaval.ima.mp.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ca.ulaval.ima.mp.game.roles.Psychic;
import ca.ulaval.ima.mp.game.roles.Role;
import ca.ulaval.ima.mp.game.roles.Salvater;
import ca.ulaval.ima.mp.game.roles.Villager;
import ca.ulaval.ima.mp.game.roles.Witch;
import ca.ulaval.ima.mp.game.roles.Wolf;

public class Referee {

    private Game game;

    public Referee(Game game) {
        this.game = game;
    }

    public List<Player> dispatchRoles(List<String> names) {
        List<Player> players = new ArrayList<>();
        Collections.shuffle(names);
        if (names.size() == 3) {
            players.add(new Player(this.game.getNextId(), names.get(0), new Wolf()));
            players.add(new Player(this.game.getNextId(), names.get(1), new Salvater()));
            players.add(new Player(this.game.getNextId(), names.get(2), new Psychic()));
        }
        else if (names.size() == 4) {
            players.add(new Player(this.game.getNextId(), names.get(0), new Wolf()));
            players.add(new Player(this.game.getNextId(), names.get(1), new Wolf()));
            players.add(new Player(this.game.getNextId(), names.get(2), new Witch()));
            players.add(new Player(this.game.getNextId(), names.get(3), new Psychic()));
        }
        else {
            players.add(new Player(this.game.getNextId(), names.get(0), new Wolf()));
            players.add(new Player(this.game.getNextId(), names.get(1), new Wolf()));
            players.add(new Player(this.game.getNextId(), names.get(2), new Witch()));
            players.add(new Player(this.game.getNextId(), names.get(3), new Psychic()));
            players.add(new Player(this.game.getNextId(), names.get(4), new Salvater()));
            for (int it = 5; it < names.size(); it++) {
                players.add(new Player(this.game.getNextId(), names.get(it), new Villager()));
            }
        }
        return players;
    }

    private boolean wolfAlive() {
        for (Player player: this.game.getPlayers()) {
            if (player.getRole().getType() == Role.Type.WOLF && player.isAlive())
                return true;
        }
        return false;
    }

    private boolean villagerAlive() {
        for (Player player: this.game.getPlayers()) {
            if (player.getRole().getType() == Role.Type.VILLAGER && player.isAlive())
                return true;
        }
        return false;
    }

    public Role.Side getWinner() {
        Role.Side winner = (this.villagerAlive()) ? Role.Side.VILLAGER : Role.Side.WOLF;
        return winner;
    }

    public boolean isGameProceeding() {
        return (this.wolfAlive() && this.villagerAlive());
    }

    public int getChoosenPlayerId(List<Integer> votes) {
        int choosenOne = 0;
        int frequency = 0;
        Collections.shuffle(votes);
        HashMap<Integer, Integer> elementCountMap = new HashMap<Integer, Integer>();
        for (Integer vote: votes) {
            if (elementCountMap.containsKey(vote))
                elementCountMap.put(vote, elementCountMap.get(vote)+1);
            else
                elementCountMap.put(vote, 1);
        }
        Iterator it = elementCountMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            if (((Integer)pair.getKey() > frequency)) {
                choosenOne = (Integer) pair.getValue();
                frequency = (Integer) pair.getKey();
            }
            it.remove();
        }
        return choosenOne;
    }

    public List<Player> getDeadPlayers() {
        List<Player> deadPlayers = new ArrayList<>();
        for (Player player: this.game.getPlayers()) {
            if (player.hasDeathMark() && !player.hasSalvaterMark())
                deadPlayers.add(player);
        }
        return deadPlayers;
    }

    public List<Player> getAlivePlayers(boolean dying) {
        List<Player> alivePlayers = new ArrayList<>();
        for (Player player: this.game.getPlayers()) {
            if (player.isAlive() && player.hasDeathMark() == dying)
                alivePlayers.add(player);
        }
        return alivePlayers;
    }

    public List<Player> getWolfMeals() {
        List<Player> meals = new ArrayList<>();
        for (Player player: this.game.getPlayers()) {
            if (player.isAlive() && player.getRole().getType() != Role.Type.WOLF)
                meals.add(player);
        }
        return meals;
    }

    public List<Player> getPlayersToDefend(Player lastProtected) {
        List<Player> playersToDefend = new ArrayList<>();
        for (Player player: this.game.getPlayers()) {
            if (player.isAlive() && (lastProtected == null || lastProtected.getId() != player.getId()))
                playersToDefend.add(player);
        }
        return playersToDefend;
    }

    private boolean playerIdInList(int playerId, List<Player> players) {
        for (Player player: players) {
            if (player.getId() == playerId)
                return true;
        }
        return false;
    }

    public List<Player> getPlayersToSee(List<Player> seenPlayers) {
        List<Player> playersToSee = new ArrayList<>();
        for (Player player: this.game.getPlayers()) {
            if (player.isAlive() && !this.playerIdInList(player.getId(), seenPlayers))
                playersToSee.add(player);
        }
        return playersToSee;
    }

    public List<Player> getWolves() {
        List<Player> wolves = new ArrayList<>();
        for (Player player: this.game.getPlayers()) {
            if (player.isAlive() && !this.playerIdInList(player.getId(), wolves))
                wolves.add(player);
        }
        return wolves;
    }
}
