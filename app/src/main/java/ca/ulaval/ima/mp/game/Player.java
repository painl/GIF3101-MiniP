package ca.ulaval.ima.mp.game;

import ca.ulaval.ima.mp.game.roles.Role;

public class Player {

    private final int       id;
    private final String    name;
    private final Role      role;
    private boolean         alive;

    public Player(int id, String name, Role role) {
        this.id = id;
        this.name = name;
        this.role = role;
        this.alive = true;
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
}
