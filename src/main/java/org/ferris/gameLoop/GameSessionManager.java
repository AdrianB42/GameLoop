package org.ferris.gameLoop;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


/*
The GameSessionManager is probably the most important part of this whole plugin, as it handles the creation of each minigame session.
 */
public class GameSessionManager {

    /*
    The game sessions are stored in a hashmap of gameSession UUIDs that points to each gameSession.
     */
    private static final Map<UUID, GameSession> sessions = new HashMap<>();

    /*
    Adds a session to the hashmap.
    This function is only used in the code for the "/gameloop start" command, which isn't the intended method of creating a gameSession (See the run() clock in initialize()).
     */
    public static void addSession(UUID uuid, GameSession gameSession) {
        sessions.put(uuid, gameSession);
    }

    /*
    Removes a session from the hashmap, and adds all players that were within that session back to the PlayerEventListener.playersNotInSession list.
     */
    public static void endSession(UUID uuid) {
        PlayerEventListener.playersNotInSession.addAll(sessions.remove(uuid).players);
    }

    /*
    Removes a session from the hashmap.
    This is only used when a session is already empty of players.
     */
    public static void endSession(GameSession gameSession) {
        sessions.entrySet().removeIf(entry -> entry.getValue().equals(gameSession));
    }

    /*
    Returns the gameSession that the UUID of a player is in.
    If that player isn't currently in a session, getSessionByPlayer() returns null.
     */
    public static GameSession getSessionByPlayer(UUID playerId) {
        return sessions.values().stream()
                .filter(session -> session.players.contains(playerId))
                .findFirst().orElse(null);
    }

    /*
    Returns the list of gameSession UUIDs.
     */
    public static ArrayList<UUID> gameSessions() {
        return new ArrayList<>(sessions.keySet());
    }

    /*
    Returns the session associated with a specific UUID.
     */
    public static GameSession getSession(UUID sessionUuid) {
        return sessions.get(sessionUuid);
    }

    /*
    This is the clock that handles creating and populating gameSessions.
     */
    public static void initialize(JavaPlugin pl) {


        new BukkitRunnable() {
            @Override
            public void run() {
                /*
                First, checks if there are any ongoing sessions, if not, it creates one.
                 */
                if (sessions.isEmpty()) {
                    sessions.put(UUID.randomUUID(), new GameSession(100));
                }
                sessions.forEach((uuid, session) -> {
                    /*
                    Next, the clock goes through each gameSession. If a gameSession has the status of WAITING_FOR_PLAYERS, but has enough players to start the game, its status is changed to RUNNING and a new gameSession is created to be populated.
                     */
                    if (session.players.size() >= 10 && session.status == GameStatus.WAITING_FOR_PLAYERS) {
                        session.status = GameStatus.RUNNING;
                        sessions.put(UUID.randomUUID(), new GameSession(100));
                    } else {
                        /*
                        Finally, if the gameSession is still WAITING_FOR_PLAYERS and doesn't have enough players to start the game, it will send out a message to all players in PlayerEventListener.playersNotInSession, prompting them to click on a message that will queue them into a game.
                         */
                        Component msg = Component.text("A new game is starting, click here to join.")
                                .color(NamedTextColor.GREEN)
                                .decorate(TextDecoration.BOLD)
                                .clickEvent(ClickEvent.runCommand("/gameloop join " + uuid));

                        Bukkit.getOnlinePlayers().stream()
                                .filter(p -> PlayerEventListener.playersNotInSession.contains(p.getUniqueId()))
                                .forEach(p -> p.sendMessage(msg));
                    }
                });
            }
        }.runTaskTimer(pl, 0, 20 * 60);
        /*
        runTaskTimer() determines how often the task will be run, in ticks (1/20 of a second).
         */
    }
}



