package org.ferris.gameLoop;

import java.util.ArrayList;
import java.util.UUID;

public class GameSession {
    public int worldBorderSize;
    public ArrayList<UUID> players;
    public GameStatus status;

    public GameSession(int worldBorderSize) {
        this.worldBorderSize = worldBorderSize;
        players = new ArrayList<>();

        status = GameStatus.WAITING_FOR_PLAYERS;
    }

    public void addPlayers(ArrayList<UUID> players) {
        this.players.addAll(players);
    }

    public void addPlayer(UUID uuid) {
        this.players.add(uuid);
    }

    public void removePlayer(UUID uuid) {
        this.players.remove(uuid);
    }
}
