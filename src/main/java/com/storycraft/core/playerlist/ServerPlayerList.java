package com.storycraft.core.playerlist;

import com.storycraft.core.MiniPlugin;
import com.storycraft.server.event.server.ServerUpdateEvent;
import com.storycraft.util.ConnectionUtil;
import com.storycraft.util.reflect.Reflect;
import net.minecraft.server.v1_13_R2.ChatComponentText;
import net.minecraft.server.v1_13_R2.PacketPlayOutPlayerListHeaderFooter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class ServerPlayerList extends MiniPlugin implements Listener {

    private String headerText;
    private String footerText;

    private boolean needUpdate;

    private Reflect.WrappedField<ChatComponentText, PacketPlayOutPlayerListHeaderFooter> packetHeaderField;
    private Reflect.WrappedField<ChatComponentText, PacketPlayOutPlayerListHeaderFooter> packetFooterField;

    public ServerPlayerList(){
        this.headerText = "";
        this.footerText = "";

        this.needUpdate = false;
    }

    @Override
    public void onEnable(){
        getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());

        this.packetHeaderField = Reflect.getField(PacketPlayOutPlayerListHeaderFooter.class, "a");
        this.packetFooterField = Reflect.getField(PacketPlayOutPlayerListHeaderFooter.class, "b");

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

        packetHeaderField.set(packet, new ChatComponentText(getHeaderText()));
        packetFooterField.set(packet, new ChatComponentText(getFooterText()));

        return packet;
    }
}
