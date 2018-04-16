package ca.ulaval.ima.mp.game;

import java.util.List;

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

            }
            else {

            }
            this.time = (this.time == Time.DAY) ? Time.NIGHT : Time.DAY;
            this.turn++;
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

    public void setState(State state) {
        this.state = state;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }
}
