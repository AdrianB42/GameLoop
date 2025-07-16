package org.ferris.gameLoop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GameSessionManager {
    private static final Map<UUID, GameSession> sessions = new HashMap<>();

    public static void addSession(UUID uuid, GameSession gameSession) {
        sessions.put(uuid, gameSession);
    }

    public static void endSession(UUID uuid) {
        PlayerEventListener.playersNotInSession.addAll(sessions.remove(uuid).players);
    }

    public static void endSession(GameSession gameSession) {
        sessions.entrySet().removeIf(entry -> entry.getValue().equals(gameSession));
    }

    public static GameSession getSessionByPlayer(UUID playerId) {
        return sessions.values().stream()
                .filter(session -> session.players.contains(playerId))
                .findFirst().orElse(null);
    }

    public static ArrayList<UUID> gameSessions() {
        return new ArrayList<>(sessions.keySet());
    }
}
