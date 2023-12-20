import org.bukkit.plugin.java.JavaPlugin

class NewYearAmazingNight : JavaPlugin() {
    override fun onEnable() {
        server.pluginManager.registerEvents(Listener(), this)
    }
}