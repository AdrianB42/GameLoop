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

/*
PlayerEventListener.java listens to events triggered by players and handles what should happen upon specific events.
This class determines when a gameSession has met a "win condition" and when to remove it from the GameSessionManager hashmap.
 */
public class PlayerEventListener implements Listener {
    /*
    playersNotInSession keeps track of all players who are not currently in a gameSession.
     */
    public static ArrayList<UUID> playersNotInSession = new ArrayList<>();

    /*
    onPlayerDeath() handles the logic for when a player dies, checking if that player was in a gameSession, removing them from that gameSession if they were, and deleting the gameSession if that player was the last remaining.
     */
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

    /*
    onPlayerQuit() handles the logic for when a player quits the server, checking if that player was in a gameSession, removing them from that gameSession if they were, and deleting the gameSession if that player was the last remaining.
     */
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

    /*
    onPlayerJoin() handles the logic for when a player joins the server, automatically adding their UUID to playersNotInSession.
     */
    @EventHandler
    public void onPlayerJoin(@NotNull PlayerJoinEvent e) {
        playersNotInSession.add(e.getPlayer().getUniqueId());
    }
}
