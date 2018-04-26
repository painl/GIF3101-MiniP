package ca.ulaval.ima.mp.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import ca.ulaval.ima.mp.activities.GameActivity;
import ca.ulaval.ima.mp.game.roles.Role;
import ca.ulaval.ima.mp.game.roles.Villager;
import ca.ulaval.ima.mp.game.roles.Wolf;

public class Game {

    public enum State {RUNNING, WAITING}

    public enum Time {DAY, NIGHT}

    private GameActivity mContext;

    private final Referee referee;
    private List<Player> players;
    private State state;
    private int turn;

    public Game(GameActivity context, LinkedHashMap<String, Role.Type> roleMap) {
        this.mContext = context;
        this.referee = new Referee(this);
        this.state = State.RUNNING;
        this.turn = 0;
        this.players = new ArrayList<>();
        assignRoles(roleMap);
    }

    private void assignRoles(LinkedHashMap<String, Role.Type> roleMap) {
        int id = 0;
        for (Map.Entry<String, Role.Type> entry: roleMap.entrySet()) {
            Role newRole;
            switch (entry.getValue()) {
                case WEREWOLF:
                    newRole = new Wolf();
                    break;
                default:
                    newRole = new Villager();
                    break;
            }
            this.players.add(new Player(id++, entry.getKey(), newRole));
        }
    }

    public void play(int index)
    {
        if (this.referee.isGameProceeding()) {
            if (index == 1)
                debateTurn();
            else if (index == 3 || (index == 2 && this.turn == 0))
                this.werewolfTurn();
            else if (index == 2)
                this.villagerTurn();
            else if (index == 5)
                this.villagerVoted();
            else if (index == 6)
                this.endTurn();
        } else {
            Role.Side winner = referee.getWinner();
            mContext.startWinStep(winner);
        }
    }

    public List<Player> getPlayers() {
        return this.players;
    }

    private void debateTurn() {
        initVotes();
        mContext.startDebateStep();
    }

    private void endTurn() {
        this.werewolfPlayed();
        this.turn++;
        mContext.startDeathStep(this.referee.getDeadPlayers(), Time.NIGHT);
    }

    private void villagerTurn() {
        initVotes();
        List<Player> choices = this.referee.getAlivePlayers(false);
        mContext.startVotesStep(choices);
    }

    /**
     * Has to to be called after all villagers have voted.
     */
    private void villagerVoted() {
        Player player = this.referee.getChosenPlayer(getVotes());
        player.setDeathMark(true);
        player.setMurderer(Role.Type.VILLAGER);
        mContext.startDeathStep(this.referee.getDeadPlayers(), Time.DAY);
    }

    public List<Player> getVotes()
    {
        List<Player> votes = new ArrayList<>();
        for (Player p : players)
            if (p.getVote() != null)
                votes.add(p.getVote());
        return votes;
    }

    private void werewolfTurn() {
        initVotes();
        List<Player> wolves = this.referee.getWolves();
        List<Player> meals = this.referee.getWolfMeals();
        mContext.startWolvesStep(wolves, meals);
    }

    private void initVotes()
    {
        for (Player player : getPlayers())
            player.setVote(null);
    }

    public void playerVote(int playerId, int targetId) {
        Player player = getPlayerById(playerId);
        if (player != null)
            player.setVote(getPlayerById(targetId));
    }

    /**
     * Has to be called after all wolves have voted.
     *
     */
    private void werewolfPlayed() {
        Player chosen = this.referee.getChosenPlayer(getVotes());
        if (chosen != null) {
            chosen.setDeathMark(true);
            chosen.setMurderer(Role.Type.WEREWOLF);
        }
        this.state = State.RUNNING;
    }

    /*private void witchTurn() {
        Player witch = this.getPlayerByRole(Role.Type.WITCH);
        if (!witch.isAlive())
            return;
        List<Player> playersToSave = (((Witch)witch.getRole()).hasLifePotion()) ? this.referee.getAlivePlayers(true) : null;
        List<Player> playersToMurder = (((Witch)witch.getRole()).hasDeathPotion()) ? this.referee.getAlivePlayers(false) : null;
    }*/

    /**
     * Has to be called after the witch ended her turn.
     *
     * @param potions List of Potions that the witch used. The list's size must be 2. If a potion is not used, it must be set to null.
     */
    /*public void witchPlayed(List<Potion> potions) {
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
    }*/

    /*private void salvaterTurn() {
        Player salvater = this.getPlayerByRole(Role.Type.SALVATER);
        if (!salvater.isAlive())
            return;
        List<Player> playersToDefend = this.referee.getPlayersToDefend(((Salvater) salvater.getRole()).getLastProtected());
    }*/

    /**
     * Has to be called after the salvater ended his turn.
     *
     * @param targetId The ID of the target protected by the salvater.
     */
    /*public void salvaterPlayed(int targetId) {
        Player target = this.getPlayerById(targetId);
        Salvater salvater = (Salvater) this.getPlayerByRole(Role.Type.SALVATER).getRole();
        target.setSalvaterMark(true);
        salvater.setLastProtected(target);
        this.state = State.RUNNING;
    }*/

    /*private void psychicTurn() {
        Player psychic = this.getPlayerByRole(Role.Type.PSYCHIC);
        if (!psychic.isAlive())
            return;
        List<Player> playersToSee = this.referee.getPlayersToSee(((Psychic) psychic.getRole()).getSeen());
    }*/

    /**
     * Has to be called after the psychic ended her turn.
     *
     */
    /*public void psychicPlayed(int targetId) {
        Player target = this.getPlayerById(targetId);
        Psychic psychic = (Psychic) this.getPlayerByRole(Role.Type.PSYCHIC).getRole();
        psychic.addSeen(target);
        // this.revealRole(target);
    }*/

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
}