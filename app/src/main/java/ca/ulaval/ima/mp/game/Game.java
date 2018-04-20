package ca.ulaval.ima.mp.game;

import java.util.List;

import ca.ulaval.ima.mp.game.roles.Psychic;
import ca.ulaval.ima.mp.game.roles.Role;
import ca.ulaval.ima.mp.game.roles.Salvater;
import ca.ulaval.ima.mp.game.roles.Witch;

public class Game {

    public enum State {RUNNING, WAITING}
    public enum Time {DAY, NIGHT}

    private final Referee       referee;
    private final List<Player>  players;
    private State               state;
    private Time                time;
    private int                 turn;

    public Game(List<String> names) {
        this.referee = new Referee(this);
        this.players = this.referee.dispatchRoles(names);
        this.state = State.RUNNING;
        this.time = Time.DAY;
        this.turn = 0;
        this.play();
    }

    public void play() {
        while (this.referee.isGameProceeding()) {
            if (this.time == Time.DAY) {
                this.debateTurn();
                if (this.turn != 0)
                    this.villagerTurn();
            }
            else if (this.time == Time.NIGHT) {
                this.wolfTurn();
                this.witchTurn();
                this.salvaterTurn();
                this.psychicTurn();
                this.turn++;
            }
            this.time = (this.time == Time.DAY) ? Time.NIGHT : Time.DAY;
        }
    }

    public int getNextId() {
        int id = -1;
        for (Player player: this.players) {
            if (player.getId() > id)
                id = player.getId();
        }
        id++;
        return id;
    }

    public List<Player> getPlayers() {
        return this.players;
    }

    /**
     * Has to be called if there is no particular data that the game needs to know. E.G: When the user taps "NEXT" after the game reveals a death.
     */
    public void nextState() {
        this.state = State.RUNNING;
    }

    private void revealDeath(Player deadPlayer) {
        deadPlayer.setAlive(false);
        this.waitForInput();
    }

    private void debateTurn() {
        this.waitForInput();
    }

    private void villagerTurn() {
        this.waitForInput();
    }

    /**
     * Has to to be called after all villagers has voted.
     * @param votes List of Player IDs chosen
     */
    public void villagerVoted(List<Integer> votes) {
        int chosenOne = this.referee.getChoosenPlayerId(votes);
        Player player = this.getPlayerById(chosenOne);
        player.setMurderer(Role.Type.VILLAGER);
        this.revealDeath(player);
    }

    private void wolfTurn() {
        this.waitForInput();
    }

    /**
     * Has to be called after all wolves has voted.
     * @param votes List of Player IDs chosen by each wolf.
     */
    public void wolfPlayed(List<Integer> votes) {
        int chosenOne = this.referee.getChoosenPlayerId(votes);
        Player player = this.getPlayerById(chosenOne);
        player.setDeathMark(true);
        player.setMurderer(Role.Type.WOLF);
        this.state = State.RUNNING;
    }

    private void witchTurn() {
        this.waitForInput();
    }

    /**
     * Has to be called after the witch ended her turn.
     * @param potions List of Potions that the witch used. The list's size must be 2. If a potion is not used, it must be set to null.
     */
    public void witchPlayed(List<Potion> potions) {
        for (Potion potion: potions) {
            if (potion != null) {
                if (potion.getType() == Potion.Type.LIFE) {
                    Player target = this.getPlayerById(potion.getTarget());
                    Witch witch = (Witch)this.getPlayerByRole(Role.Type.WITCH).getRole();
                    target.setDeathMark(false);
                    target.setMurderer(null);
                    witch.setLifePotion(false);
                }
                else if (potion.getType() == Potion.Type.DEATH) {
                    Player target = this.getPlayerById(potion.getTarget());
                    Witch witch = (Witch)this.getPlayerByRole(Role.Type.WITCH).getRole();
                    target.setDeathMark(true);
                    target.setMurderer(Role.Type.WITCH);
                    witch.setDeathPotion(false);
                }
            }
        }
        this.state = State.RUNNING;
    }

    private void salvaterTurn() {
        this.waitForInput();
    }

    /**
     * Has to be called after the salvater ended his turn.
     * @param targetId The ID of the target protected by the salvater.
     */
    public void salvaterPlayed(int targetId) {
        Player target = this.getPlayerById(targetId);
        Salvater salvater = (Salvater)this.getPlayerByRole(Role.Type.SALVATER).getRole();
        target.setSalvaterMark(true);
        salvater.setLastProtected(target);
        this.state = State.RUNNING;
    }

    private void psychicTurn() {
        this.waitForInput();
    }

    /**
     * Has to be called after the psychic ended her turn.
     * @param targetId The ID of the target seen by the psychic.
     */
    public void psychicPlayed(int targetId) {
        Player target = this.getPlayerById(targetId);
        Psychic psychic = (Psychic)this.getPlayerByRole(Role.Type.PSYCHIC).getRole();
        psychic.addSeen(target);
        this.revealRole(target);
    }

    private void revealRole(Player target) {
        this.waitForInput();
    }

    private void setState(State state) {
        this.state = state;
    }

    public Time getTime() {
        return time;
    }

    public int getTurn() {
        return turn;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    private Player getPlayerById(int id) {
        for(Player player: this.players) {
            if (player.getId() == id)
                return player;
        }
        return null;
    }

    private Player getPlayerByRole(Role.Type type) {
        for(Player player: this.players) {
            if (player.getRole().getType() == type)
                return player;
        }
        return null;
    }

    private void waitForInput() {
        this.state = State.WAITING;
        while(this.state == State.WAITING){}
    }
}
