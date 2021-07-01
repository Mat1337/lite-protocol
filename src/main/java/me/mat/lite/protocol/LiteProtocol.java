package me.mat.lite.protocol;

import me.mat.lite.protocol.connection.ConnectionManager;
import me.mat.lite.protocol.connection.packet.LitePacketProvider;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

public class LiteProtocol implements Listener {

    private final Plugin plugin;
    private final ConnectionManager connectionManager;

    public LiteProtocol(Plugin plugin) {
        this.plugin = plugin;
        this.connectionManager = new ConnectionManager(plugin);

        // load the packet provider
        LitePacketProvider.load();

        this.register(connectionManager);
    }

    @EventHandler
    private void pluginEnable(PluginEnableEvent event) {
        // loop through all the online players and add their connections
        plugin.getServer().getOnlinePlayers().forEach(connectionManager::addConnection);
    }

    @EventHandler
    private void pluginDisable(PluginDisableEvent event) {
        System.err.println("plugin disable");

        // close the connection manager
        connectionManager.close();
    }

    /**
     * Registers a listener to the plugin
     *
     * @param listener that you want to register
     */

    private void register(Listener listener) {
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
    }

    /**
     * Registers lite-protocol
     * to a plugin
     *
     * @param plugin plugin that you want to register lite-protocol to
     */

    public static void register(Plugin plugin) {
        // register the instance
        plugin.getServer().getPluginManager().registerEvents(
                new LiteProtocol(plugin),
                plugin
        );
    }

}