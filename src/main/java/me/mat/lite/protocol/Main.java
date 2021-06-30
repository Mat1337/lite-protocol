package me.mat.lite.protocol;

import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        // register lite-protocol
        LiteProtocol.register(this);
    }

}
