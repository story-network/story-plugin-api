package com.storycraft.server.playerlist;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.storycraft.StoryPlugin;
import com.storycraft.config.json.JsonConfigFile;
import com.storycraft.MiniPlugin;
import com.storycraft.config.event.ConfigUpdateEvent;
import com.storycraft.server.ServerExtension;
import com.storycraft.server.event.server.ServerUpdateEvent;
import com.storycraft.util.ConnectionUtil;
import net.minecraft.server.v1_14_R1.ChatComponentText;
import net.minecraft.server.v1_14_R1.PacketPlayOutPlayerListHeaderFooter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class ServerPlayerList extends ServerExtension implements Listener {

    private boolean needUpdate;

    private String[] headerText;
    private String[] footerText;

    public ServerPlayerList(){
        this.needUpdate = false;
        this.headerText = new String[0];
        this.footerText = new String[0];
    }

    @Override
    public void onLoad(StoryPlugin plugin) {

    }

    @Override
    public void onEnable(){
        getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
    }

    public void update(){
        this.needUpdate = true;
    }

    public String[] getFooterText() {
        return footerText;
    }

    public String[] getHeaderText() {
        return headerText;
    }

    public void setHeaderText(String[] headerText) {
        this.headerText = headerText;
        update();
    }

    public void setFooterText(String[] footerText) {
        this.footerText = footerText;
        update();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        if (e.getPlayer() == null)
            return;

        ConnectionUtil.sendPacket(e.getPlayer(), createHeaderFooterPacket());
    }

    @EventHandler
    public void onAsyncUpdate(ServerUpdateEvent e){
        if (this.needUpdate){
            this.needUpdate = false;

            ConnectionUtil.sendPacket(createHeaderFooterPacket());
        }
    }

    private PacketPlayOutPlayerListHeaderFooter createHeaderFooterPacket(){
        PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter();

        packet.header = new ChatComponentText(String.join("\n", getHeaderText()));
        packet.footer = new ChatComponentText(String.join("\n", getFooterText()));

        return packet;
    }
}
