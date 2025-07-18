package org.ferris.gameLoop;

import org.bukkit.plugin.java.JavaPlugin;

/*
This is the main class of the plugin, although not much code is needed here as the primary function of the GameLoop.java class is simply to initialize the rest of the plugin.
 */
public final class GameLoop extends JavaPlugin {

    /*
    onEnable() handles initializing the plugin upon the start/reloading of the server.
    Any classes/objects that need to be initialized before being used should go here.
     */
    @Override
    public void onEnable() {
        /*
        registerEvents() is what initializes event listeners.
        In this case, the PlayerEventListener class is being initialized.
         */
        getServer().getPluginManager().registerEvents(new PlayerEventListener(), this);

        /*
        getCommand() is what initializes in-game commands.
        the function takes the name of the command (as according to the plugin.yml file) and directs its function towards a command class (GameloopCommand.java).
         */
        getCommand("gameloop").setExecutor(new GameloopCommand());

        /*
        This line initializes the clock within GameSessionManager.java.
         */
        GameSessionManager.initialize(this);
    }

    /*
    onDisable() handles logic that should occur upon stopping/reloading the server.
    Oftentimes, very little, if at all has to be in here.
     */
    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
