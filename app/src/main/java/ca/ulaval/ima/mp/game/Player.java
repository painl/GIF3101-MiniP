package ca.ulaval.ima.mp.game;

import ca.ulaval.ima.mp.game.roles.Role;

public class Player {

    private final int       id;
    private final String    name;
    private final Role      role;
    private boolean         alive;
    private boolean         deathMark;
    private boolean         salvaterMark;
    private Role.Type       murderer;
    private Player          vote;

    public Player(int id, String name, Role role) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.alive = true;
        this.deathMark = false;
        this.salvaterMark = false;
        this.murderer = null;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Role getRole() {
        return role;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public boolean hasDeathMark() {
        return deathMark;
    }

    public void setDeathMark(boolean deathMark) {
        this.deathMark = deathMark;
    }

    public Role.Type getMurderer() {
        return murderer;
    }

    public void setMurderer(Role.Type murderer) {
        this.murderer = murderer;
    }

    public boolean hasSalvaterMark() {
        return salvaterMark;
    }

    public void setSalvaterMark(boolean salvaterMark) {
        this.salvaterMark = salvaterMark;
    }

    public Player getVote() {
        return vote;
    }

    public void setVote(Player vote) {
        this.vote = vote;
    }
}
