package com.storycraft.server;

import com.storycraft.StoryPlugin;
import com.storycraft.server.forge.ForgeServerManager;
import com.storycraft.server.packet.ServerNetworkManager;

import net.minecraft.server.v1_12_R1.MinecraftServer;
import org.bukkit.craftbukkit.v1_12_R1.CraftServer;

import java.util.ArrayList;
import java.util.List;

public class ServerManager {

    private StoryPlugin plugin;

    private ServerNetworkManager networkManager;
    private ForgeServerManager forgeServerManager;

    private List<ServerExtension> extensionList;

    public ServerManager(StoryPlugin plugin) {
        this.plugin = plugin;
        this.extensionList = new ArrayList<>();

        registerHandler();
    }

    private void registerHandler() {
        addServerExtension(networkManager = new ServerNetworkManager(this));
        addServerExtension(forgeServerManager = new ForgeServerManager(this));
    }

    protected List<ServerExtension> getExtensionList() {
        return extensionList;
    }

    public StoryPlugin getPlugin() {
        return plugin;
    }

    public void addServerExtension(ServerExtension extension) {
        getPlugin().getMiniPluginLoader().addMiniPlugin(extension);

        getExtensionList().add(extension);
    }

    public void removeServerExtension(ServerExtension extension) {
        if (!getExtensionList().contains(extension))
            return;

        getPlugin().getMiniPluginLoader().removeMiniPlugin(extension);

        getExtensionList().remove(extension);
    }

    public MinecraftServer getMinecraftServer() {
        return ((CraftServer)getPlugin().getServer()).getHandle().getServer();
    }

    public ServerNetworkManager getNetworkManager() {
        return networkManager;
    }

    public ForgeServerManager getForgeServerManager() {
        return forgeServerManager;
    }
}
