package ca.ulaval.ima.mp.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import ca.ulaval.ima.mp.game.roles.Role;

public class Referee {

    private Game game;

    Referee(Game game) {
        this.game = game;
    }

    public static LinkedHashMap<String, Role.Type> assignRoles(List<String> names) {
        List<Role.Type> roles = new ArrayList<>();
        LinkedHashMap<String, Role.Type> players = new LinkedHashMap<>();
        int werewolfNb = names.size() % 3;
        int i;

        for (i = 0; i < werewolfNb; i++) { roles.add(Role.Type.WEREWOLF); }
        for (; i < names.size(); i++) { roles.add(Role.Type.VILLAGER); }

        Collections.shuffle(roles);

        int index = 0;
        for (Role.Type role: roles) {
            players.put(names.get(index++), role);
        }

        return players;
    }

    private boolean wolfAlive() {
        for (Player player: this.game.getPlayers()) {
            if (player.getRole().getSide() == Role.Side.WEREWOLF && player.isAlive())
                return true;
        }
        return false;
    }

    private boolean villagerAlive() {
        for (Player player: this.game.getPlayers()) {
            if (player.getRole().getSide() == Role.Side.VILLAGER && player.isAlive())
                return true;
        }
        return false;
    }

    Role.Side getWinner() {
        return (this.villagerAlive()) ? Role.Side.VILLAGER : Role.Side.WEREWOLF;
    }

    boolean isGameProceeding() {
        return (this.wolfAlive() && this.villagerAlive());
    }

    Player getChosenPlayer(List<Player> votes) {
        Player          chosen = null;
        List<Player>    players = getAlivePlayers(false);
        int frequency = -1;

        // Collections.shuffle(players);
        for (Player player : players) {
            int tmpFrequency = Collections.frequency(votes, player);
            if (tmpFrequency > frequency) {
                frequency = tmpFrequency;
                chosen = player;
            }
        }
        return chosen;
    }

    List<Player> getDeadPlayers() {
        List<Player> deadPlayers = new ArrayList<>();
        for (Player player: this.game.getPlayers()) {
            if (player.isAlive() && player.hasDeathMark() && !player.hasSalvaterMark()) {
                deadPlayers.add(player);
                player.setAlive(false);
            }
        }
        return deadPlayers;
    }

    List<Player> getAlivePlayers(boolean dying) {
        List<Player> alivePlayers = new ArrayList<>();
        for (Player player: this.game.getPlayers()) {
            if (player.isAlive() && player.hasDeathMark() == dying)
                alivePlayers.add(player);
        }
        return alivePlayers;
    }

    List<Player> getWolfMeals() {
        List<Player> meals = new ArrayList<>();
        for (Player player: this.game.getPlayers()) {
            if (player.isAlive() && player.getRole().getType() != Role.Type.WEREWOLF)
                meals.add(player);
        }
        return meals;
    }

    List<Player> getPlayersToDefend(Player lastProtected) {
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

    List<Player> getPlayersToSee(List<Player> seenPlayers) {
        List<Player> playersToSee = new ArrayList<>();
        for (Player player: this.game.getPlayers()) {
            if (player.isAlive() && !this.playerIdInList(player.getId(), seenPlayers))
                playersToSee.add(player);
        }
        return playersToSee;
    }

    List<Player> getWolves() {
        List<Player> wolves = new ArrayList<>();
        for (Player player: this.game.getPlayers()) {
            if (player.isAlive() && player.getRole().getType() == Role.Type.WEREWOLF)
                wolves.add(player);
        }
        return wolves;
    }
}
