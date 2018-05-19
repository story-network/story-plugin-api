package com.storycraft.server;

import com.storycraft.StoryPlugin;
import com.storycraft.server.forge.ForgeServerManager;
import com.storycraft.server.packet.ServerNetworkManager;

import com.storycraft.server.plugin.ServerPluginManager;
import com.storycraft.server.tick.TickEventInvoker;
import com.storycraft.server.world.WorldManager;
import net.minecraft.server.v1_12_R1.MinecraftServer;
import org.bukkit.craftbukkit.v1_12_R1.CraftServer;

import java.util.ArrayList;
import java.util.List;

public class ServerManager {

    private StoryPlugin plugin;

    private ServerPluginManager serverPluginManager;
    private ServerNetworkManager networkManager;
    private ForgeServerManager forgeServerManager;
    private WorldManager worldManager;
    private TickEventInvoker tickEventInvoker;

    private List<ServerExtension> extensionList;

    public ServerManager(StoryPlugin plugin) {
        this.plugin = plugin;
        this.extensionList = new ArrayList<>();

        registerHandler();
    }

    private void registerHandler() {
        addServerExtension(serverPluginManager = new ServerPluginManager());
        addServerExtension(networkManager = new ServerNetworkManager(this));
        addServerExtension(forgeServerManager = new ForgeServerManager(this));
        addServerExtension(worldManager = new WorldManager());
        addServerExtension(tickEventInvoker = new TickEventInvoker());
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

    public ServerPluginManager getServerPluginManager() {
        return serverPluginManager;
    }

    public WorldManager getWorldManager() {
        return worldManager;
    }
}
