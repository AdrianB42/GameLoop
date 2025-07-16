package org.ferris.gameLoop;

import java.util.ArrayList;
import java.util.UUID;

public class GameSession {
    public int worldBorderSize;
    public ArrayList<UUID> players;

    public GameSession(int worldBorderSize, ArrayList<UUID> players) {
        this.worldBorderSize = worldBorderSize;
        this.players = players;

    }
}
