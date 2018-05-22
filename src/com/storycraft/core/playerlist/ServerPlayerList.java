package com.storycraft.core.playerlist;

import com.storycraft.core.MiniPlugin;
import com.storycraft.server.update.ServerUpdateEvent;
import com.storycraft.util.ConnectionUtil;
import com.storycraft.util.Reflect;
import net.minecraft.server.v1_12_R1.ChatComponentText;
import net.minecraft.server.v1_12_R1.IChatBaseComponent;
import net.minecraft.server.v1_12_R1.PacketPlayOutPlayerListHeaderFooter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class ServerPlayerList extends MiniPlugin implements Listener {

    private String headerText;
    private String footerText;

    private boolean needUpdate;

    public ServerPlayerList(){
        this.headerText = "";
        this.footerText = "";

        this.needUpdate = false;
    }

    @Override
    public void onEnable(){
        getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());

        setHeaderText(getPlugin().getServerName());
        setFooterText(getPlugin().getServerName());
    }

    public void update(){
        this.needUpdate = true;
    }

    public String getFooterText() {
        return footerText;
    }

    public String getHeaderText() {
        return headerText;
    }

    public void setHeaderText(String headerText) {
        this.headerText = headerText;
        update();
    }

    public void setFooterText(String footerText) {
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
        if (this.needUpdate && e.isUpdateType(ServerUpdateEvent.UpdateType.SECOND)){
            this.needUpdate = false;

            ConnectionUtil.sendPacket(createHeaderFooterPacket());
        }
    }

    private PacketPlayOutPlayerListHeaderFooter createHeaderFooterPacket(){
        PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter();

        Reflect.setField(packet, "a", new ChatComponentText(getHeaderText()));
        Reflect.setField(packet, "b", new ChatComponentText(getFooterText()));

        return packet;
    }
}
