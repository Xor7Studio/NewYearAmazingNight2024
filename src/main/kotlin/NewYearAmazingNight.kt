import org.bukkit.plugin.java.JavaPlugin

@Suppress("unused")
class NewYearAmazingNight : JavaPlugin() {
    override fun onEnable() {
        server.pluginManager.registerEvents(Listener(), this)
    }
}