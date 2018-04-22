package ca.ulaval.ima.mp.game;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import ca.ulaval.ima.mp.activities.GameActivity;
import ca.ulaval.ima.mp.game.roles.Psychic;
import ca.ulaval.ima.mp.game.roles.Role;
import ca.ulaval.ima.mp.game.roles.Salvater;
import ca.ulaval.ima.mp.game.roles.Witch;

public class Game {

    public enum State {RUNNING, WAITING}

    public enum Time {DAY, NIGHT}

    private Context mContext;

    private final Referee referee;
    private List<Player> players;
    private State state;
    private Time time;
    private int turn;
    public int index;

    public Game(Context context, List<String> names) {
        this.mContext = context;
        this.referee = new Referee(this);
        this.players = new ArrayList<>();
        this.referee.dispatchRoles(this.players, names);
        this.state = State.RUNNING;
        this.time = Time.DAY;
        this.turn = 0;
        this.index = 1;
        ((GameActivity) mContext).startRolesStep(players);
    }

    public void play(int index)
    {
        if (this.referee.isGameProceeding()) {
            if (index == 1)
                debateTurn();
            else if (index == 3 || (index == 2 && this.turn == 0))
                this.wolfTurn();
            else if (index == 2)
                this.villagerTurn();
    /*       else if (index == 4)
                this.witchTurn();
            else if (index == 5)
                this.psychicTurn(); */
            else if (index == 6)
                this.endTurn();
        }
        else {
            Role.Side winner = referee.getWinner();
            ((GameActivity) mContext).startWinStep(winner);
        }
    }

 /*   public void play_cpy() {
        while (this.referee.isGameProceeding()) {
            if (this.time == Time.DAY) {
                this.debateTurn();
                if (this.turn != 0)
                    this.villagerTurn();
            } else if (this.time == Time.NIGHT) {
                this.wolfTurn();
                this.witchTurn();
                this.salvaterTurn();
                this.psychicTurn();
                this.endTurn();
                this.turn++;
            }
            this.time = (this.time == Time.DAY) ? Time.NIGHT : Time.DAY;
        }
        Role.Side winner = this.referee.getWinner();
    } */

    public int getNextId() {
        int id = -1;
        for (Player player : this.players) {
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
    synchronized public void nextState() {
        this.state = State.RUNNING;
    }

    private void debateTurn() {
        ((GameActivity) mContext).startDebateStep();
    }

    private void endTurn() {
        List<Player> deadPlayers = this.referee.getDeadPlayers();
        for (Player deadPlayer : deadPlayers) {
            deadPlayer.setAlive(false);
        }
        ((GameActivity)mContext).startDeathStep(deadPlayers, Time.NIGHT);
        this.turn++;
/*        for (Player player: this.players) {
            player.setSalvaterMark(false);
        }*/
    }

    private void villagerTurn() {
        List<Player> choices = this.referee.getAlivePlayers(false);
        ((GameActivity)mContext).startVotesStep(choices);
    }

    /**
     * Has to to be called after all villagers has voted.
     *
     * @param votes List of Player IDs chosen
     */
    public void villagerVoted(List<Integer> votes) {
        int chosenOne = this.referee.getChoosenPlayerId(votes);
        Log.d("chooseOne", String.valueOf(chosenOne));
        Player player = this.getPlayerById(chosenOne);
        player.setDeathMark(true);
        player.setMurderer(Role.Type.VILLAGER);
        List<Player> deadPlayers = this.referee.getDeadPlayers();
        for (Player deadPlayer : deadPlayers) {
            deadPlayer.setAlive(false);
        }
        ((GameActivity)mContext).startDeathStep(deadPlayers, Time.DAY);
    }

    private void wolfTurn() {
        List<Player> wolves = this.referee.getWolves();
        List<Player> meals = this.referee.getWolfMeals();
        ((GameActivity)mContext).startWolvesStep(wolves, meals);
    }

    /**
     * Has to be called after all wolves has voted.
     *
     * @param votes List of Player IDs chosen by each wolf.
     */
    public void wolfPlayed(List<Integer> votes) {
        int chosenOne = this.referee.getChoosenPlayerId(votes);
        Log.d("WOLF MEAL =>", String.valueOf(chosenOne));
        Player player = this.getPlayerById(chosenOne);
        player.setDeathMark(true);
        player.setMurderer(Role.Type.WOLF);
        this.state = State.RUNNING;
    }

    private void witchTurn() {
        Player witch = this.getPlayerByRole(Role.Type.WITCH);
        if (!witch.isAlive())
            return;
        List<Player> playersToSave = (((Witch)witch.getRole()).hasLifePotion()) ? this.referee.getAlivePlayers(true) : null;
        List<Player> playersToMurder = (((Witch)witch.getRole()).hasDeathPotion()) ? this.referee.getAlivePlayers(false) : null;
        this.waitForInput();
    }

    /**
     * Has to be called after the witch ended her turn.
     *
     * @param potions List of Potions that the witch used. The list's size must be 2. If a potion is not used, it must be set to null.
     */
    public void witchPlayed(List<Potion> potions) {
        for (Potion potion : potions) {
            if (potion != null) {
                if (potion.getType() == Potion.Type.LIFE) {
                    Player target = this.getPlayerById(potion.getTarget());
                    Witch witch = (Witch) this.getPlayerByRole(Role.Type.WITCH).getRole();
                    target.setDeathMark(false);
                    target.setMurderer(null);
                    witch.setLifePotion(false);
                } else if (potion.getType() == Potion.Type.DEATH) {
                    Player target = this.getPlayerById(potion.getTarget());
                    Witch witch = (Witch) this.getPlayerByRole(Role.Type.WITCH).getRole();
                    target.setDeathMark(true);
                    target.setMurderer(Role.Type.WITCH);
                    witch.setDeathPotion(false);
                }
            }
        }
        this.state = State.RUNNING;
    }

    private void salvaterTurn() {
        Player salvater = this.getPlayerByRole(Role.Type.SALVATER);
        if (!salvater.isAlive())
            return;
        List<Player> playersToDefend = this.referee.getPlayersToDefend(((Salvater) salvater.getRole()).getLastProtected());
        this.waitForInput();
    }

    /**
     * Has to be called after the salvater ended his turn.
     *
     * @param targetId The ID of the target protected by the salvater.
     */
    public void salvaterPlayed(int targetId) {
        Player target = this.getPlayerById(targetId);
        Salvater salvater = (Salvater) this.getPlayerByRole(Role.Type.SALVATER).getRole();
        target.setSalvaterMark(true);
        salvater.setLastProtected(target);
        this.state = State.RUNNING;
    }

    private void psychicTurn() {
        Player psychic = this.getPlayerByRole(Role.Type.PSYCHIC);
        if (!psychic.isAlive())
            return;
        List<Player> playersToSee = this.referee.getPlayersToSee(((Psychic) psychic.getRole()).getSeen());
        this.waitForInput();
    }

    /**
     * Has to be called after the psychic ended her turn.
     *
     * @param targetId The ID of the target seen by the psychic.
     */
    public void psychicPlayed(int targetId) {
        Player target = this.getPlayerById(targetId);
        Psychic psychic = (Psychic) this.getPlayerByRole(Role.Type.PSYCHIC).getRole();
        psychic.addSeen(target);
        this.revealRole(target);
    }

    private void revealRole(Player target) {
        this.waitForInput();
    }

    public int getTurn() {
        return turn;
    }

    private Player getPlayerById(int id) {
        for (Player player : this.players) {
            if (player.getId() == id)
                return player;
        }
        return null;
    }

    private Player getPlayerByRole(Role.Type type) {
        for (Player player : this.players) {
            if (player.getRole().getType() == type)
                return player;
        }
        return null;
    }

    synchronized private void waitForInput() {
        this.state = State.WAITING;
        while (this.state == State.WAITING) {
            try {
                Thread.sleep(750);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}