package org.ferris.gameLoop;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class GameloopCommand implements CommandExecutor, TabExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if (strings.length >= 1) {
            switch (strings[0].toLowerCase()) {
                case "start":
                    ArrayList<UUID> playersNotInSession = PlayerEventListener.playersNotInSession;
                    if (!playersNotInSession.isEmpty()) {
                        ArrayList<UUID> players = new ArrayList<>(playersNotInSession.subList(0, Math.min(playersNotInSession.size(), 20)));
                        playersNotInSession.subList(0, Math.min(playersNotInSession.size(), 20)).clear();

                        GameSession session = new GameSession(100, players);
                        GameSessionManager.addSession(UUID.randomUUID(), session);

                        commandSender.sendMessage("Started new session with " + players.size() + " players.");
                    } else {
                        commandSender.sendMessage("There are no available players.");
                    }
                    return true;

                case "stop":
                    if (strings.length == 1) {
                        commandSender.sendMessage("Ongoing Session UUIDs: " + GameSessionManager.gameSessions());
                        return true;
                    } else if (strings.length == 2) {
                        try {
                            UUID uuid = UUID.fromString(strings[1]);

                            GameSessionManager.endSession(uuid);
                        } catch (IllegalArgumentException e) {
                            commandSender.sendMessage("Invalid UUID");
                        }
                    }
                    return true;

                case "list":
                    commandSender.sendMessage("Sessions: " + GameSessionManager.gameSessions());
                    return true;
            }
        }

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        if (strings.length == 1) {
            return List.of("start", "stop", "list");
        }
        if (strings.length == 2 && strings[0].equalsIgnoreCase("stop")) {
            return GameSessionManager.gameSessions().stream().map(UUID::toString).collect(Collectors.toList());
        }

        return List.of();
    }
}
