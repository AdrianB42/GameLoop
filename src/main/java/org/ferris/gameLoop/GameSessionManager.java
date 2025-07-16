package org.ferris.gameLoop;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

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

    public static GameSession getSession(UUID sessionUuid) {
        return sessions.get(sessionUuid);
    }

    public static void initialize(JavaPlugin pl) {


        new BukkitRunnable() {
            @Override
            public void run() {
                if (sessions.isEmpty()) {
                    sessions.put(UUID.randomUUID(), new GameSession(100));
                }
                sessions.forEach((uuid, session) -> {
                    if (session.players.size() >= 10 && session.status == GameStatus.WAITING_FOR_PLAYERS) {
                        session.status = GameStatus.RUNNING;
                    } else {
                        Component msg = Component.text("A new game is starting, click here to join.")
                                .color(NamedTextColor.GREEN)
                                .clickEvent(ClickEvent.runCommand("/gameloop join " + uuid));

                        Bukkit.getOnlinePlayers().stream()
                                .filter(p -> PlayerEventListener.playersNotInSession.contains(p.getUniqueId()))
                                .forEach(p -> p.sendMessage(msg));
                    }
                });
            }
        }.runTaskTimer(pl, 0, 20 * 60);
    }
}



