package org.ferris.gameLoop;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.UUID;

public class PlayerEventListener implements Listener {
    public static ArrayList<UUID> playersNotInSession = new ArrayList<>();

    @EventHandler
    public void onPlayerDeath(@NotNull PlayerDeathEvent e) {
        Player player = e.getPlayer();
        GameSession session = GameSessionManager.getSessionByPlayer(player.getUniqueId());
        if (session != null) {
            session.players.remove(player.getUniqueId());
            playersNotInSession.add(player.getUniqueId());
            if (session.players.isEmpty()) {
                GameSessionManager.endSession(session);
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(@NotNull PlayerQuitEvent e) {
        Player player = e.getPlayer();
        GameSession session = GameSessionManager.getSessionByPlayer(player.getUniqueId());
        if (session != null) {
            session.players.remove(player.getUniqueId());
            if (session.players.isEmpty()) {
                GameSessionManager.endSession(session);
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(@NotNull PlayerJoinEvent e) {
        playersNotInSession.add(e.getPlayer().getUniqueId());
    }
}
