package org.ferris.gameLoop;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/*
This class handles the logic for the "/gameloop" command.
The name of the class (GameloopCommand.java) is arbitrary however it should reflect whatever command it is tied to.
On top of also handling the logic for the "/gameloop" command, it also handles the autocomplete feature when typing in "/gameloop" in game.
 */
public class GameloopCommand implements CommandExecutor, TabExecutor {

    /*
    onCommand() handles the logic for what should happen when "/gameloop" is run.
    commandSender -> Whatever sent the command (could be the server, player, etc...)
    command -> The name of the command being run. If you're using separate classes for each command (as you should to make your code neater), this becomes practically useless.
    s -> Honestly idk, so you probably don't need to use it unless you know how to.
    strings -> The list of arguments succeeding the main command (aka in "/gameloop start", "start" would be the first argument).
     */
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage("Only players can use this command.");
            return true;
        }

        if (strings.length >= 1) {
            switch (strings[0].toLowerCase()) {
                /*
                "/gameloop start" is not intended to have an actual use, instead it just exists as a way to test the plugin.
                Starting a game will instead be handled in GameSessionManager.initialize().
                 */
                case "start":
                    ArrayList<UUID> playersNotInSession = PlayerEventListener.playersNotInSession;
                    if (!playersNotInSession.isEmpty()) {
                        ArrayList<UUID> players = new ArrayList<>(playersNotInSession.subList(0, Math.min(playersNotInSession.size(), 20)));
                        playersNotInSession.subList(0, Math.min(playersNotInSession.size(), 20)).clear();

                        GameSession session = new GameSession(100);
                        session.addPlayers(players);
                        GameSessionManager.addSession(UUID.randomUUID(), session);

                        player.sendMessage("Started new session with " + players.size() + " players.");
                    } else {
                        player.sendMessage("There are no available players.");
                    }
                    return true;

                /*
                "/gameloop stop" is not intended to have an actual use, instead it just exists as a way to test the plugin.
                Stopping a game will instead be handled by the PlayerEventListener.
                 */
                case "stop":
                    if (strings.length == 1) {
                        player.sendMessage("Ongoing Session UUIDs: " + GameSessionManager.gameSessions());
                        return true;
                    } else if (strings.length == 2) {
                        try {
                            UUID uuid = UUID.fromString(strings[1]);

                            GameSessionManager.endSession(uuid);
                        } catch (IllegalArgumentException e) {
                            player.sendMessage("Invalid UUID");
                        }
                    }
                    return true;

                /*
                "/gameloop list" just returns the currently ongoing gameSessions, listing their UUID, status, and player count.
                 */
                case "list":
                    for (UUID x : GameSessionManager.gameSessions()) {
                        GameSession session = GameSessionManager.getSession(x);
                        player.sendMessage("UUID: " + x + "     Status: " + session.status + "     Players: " + session.players.size());
                    }
                    return true;

                /*
                "/gameloop join" is how players will join an ongoing gameSession.
                Players don't have to type in the message themselves, as the prompt they receive in chat to join a session runs the command for them.
                 */
                case "join":
                    if (strings.length == 1) {
                        player.sendMessage("Ongoing Session UUIDs: " + GameSessionManager.gameSessions());
                        return true;
                    } else if (strings.length == 2) {
                        try {
                            UUID uuid = UUID.fromString(strings[1]);

                            GameSessionManager.getSession(uuid).addPlayer(player.getUniqueId());

                            PlayerEventListener.playersNotInSession.remove(player.getUniqueId());

                            player.sendMessage("Joined session");
                        } catch (IllegalArgumentException e) {
                            player.sendMessage("Invalid UUID");
                        }
                    }
                    return true;

                /*
                "/gameloop quit" is how players will quit a gameSession, if they are in one.
                 */
                case "quit":
                    GameSession session = GameSessionManager.getSessionByPlayer(player.getUniqueId());
                    if (session != null) {
                        session.removePlayer(player.getUniqueId());
                        player.sendMessage("You have successfully left the game.");
                    } else {
                        player.sendMessage("You are not currently in a game.");
                    }
                    return true;
            }
        }

        return false;
    }

    /*
    onTabComplete() handles what should happen when a player who's typing a command into chat presses the tab key to autocomplete their command.
    The autocomplete suggestions are given to the player via a list of strings.
    commandSender -> Whatever sent the command (could be the server, player, etc...)
    command -> The name of the command being run. If you're using separate classes for each command (as you should to make your code neater), this becomes practically useless.
    s -> Honestly idk, so you probably don't need to use it unless you know how to.
    strings -> The list of arguments succeeding the main command (aka in "/gameloop start", "start" would be the first argument).
     */
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if (strings.length == 1) {
            return List.of("start",
                    "stop", "list", "join", "quit");
        }
        if (strings.length == 2 && strings[0].equalsIgnoreCase("stop") || strings[0].equalsIgnoreCase("join")) {
            return GameSessionManager.gameSessions().stream().map(UUID::toString).collect(Collectors.toList());
        }

        return List.of();
    }
}
