package me.mat.lite.protocol.connection;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ConnectionManager implements Listener {

    private final List<PlayerConnection> connections;

    public ConnectionManager() {
        this.connections = new ArrayList<>();
    }

    /**
     * Adds a player connection
     * to the connection manager
     *
     * @param player player that you want to add the connection to
     */

    public void addConnection(Player player) {
        connections.add(new PlayerConnection(
                this,
                player
        ));
    }

    /**
     * Removes a player connection
     * from the connection manager
     *
     * @param player player that you want to remove the connection from
     */

    public void removeConnection(Player player) {
        // attempt to find the player connection
        Optional<PlayerConnection> optional
                = connections.stream().filter(c -> c.getPlayer().getEntityId() == player.getEntityId()).findFirst();

        // if the player connection was not found
        if (!optional.isPresent()) {

            // return out of the method
            return;
        }

        // get the player connection instance
        PlayerConnection playerConnection = optional.get();

        // close the player connection
        playerConnection.close();

        // and remove the connection form the connection manager
        connections.remove(playerConnection);
    }

    /**
     * Checks if the player already
     * has a connection assigned
     *
     * @param player player that you want to check for
     * @return {@link Boolean}
     */

    public boolean hasConnection(Player player) {
        return connections.stream().anyMatch(c -> c.getPlayer().getEntityId() == player.getEntityId());
    }

    /**
     * Closes the connection manager
     */

    public void close() {
        // close all the connections
        connections.forEach(PlayerConnection::close);

        // and clear the connections list
        connections.clear();
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        // get the player
        Player player = event.getPlayer();

        // if the player already has a connection
        if (hasConnection(player)) {

            // return out of the method
            return;
        }

        // else add the connection
        addConnection(player);
    }

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent event) {
        // get the player
        Player player = event.getPlayer();

        // if the player does not have a connection
        if (!hasConnection(player)) {

            // return out of the method
            return;
        }

        // else remove the connection
        removeConnection(player);
    }

}
