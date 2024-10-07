package winlyps.lavaSponge

import org.bukkit.plugin.java.JavaPlugin

class LavaSponge : JavaPlugin() {

    override fun onEnable() {
        // Register the command and listener
        getCommand("lavasponge")?.setExecutor(LavaSpongeCommand(this))
        server.pluginManager.registerEvents(LavaSpongeListener(this), this)
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }
}