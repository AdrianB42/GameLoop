package org.ferris.gameLoop;

import org.bukkit.plugin.java.JavaPlugin;

public final class GameLoop extends JavaPlugin {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new PlayerEventListener(), this);

        getCommand("gameloop").setExecutor(new GameloopCommand());

        GameSessionManager.initialize(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
