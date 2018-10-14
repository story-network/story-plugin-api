package com.storycraft.core.player.debug;

import com.storycraft.StoryPlugin;
import com.storycraft.core.MiniPlugin;
import com.storycraft.core.rank.ServerRank;
import com.storycraft.server.packet.AsyncPacketOutEvent;
import com.storycraft.util.reflect.Reflect;
import net.minecraft.server.v1_13_R1.PacketPlayOutLogin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ForceReducedDebug extends MiniPlugin implements Listener {

    public static final int REQUIRED_RANK_LEVEL = ServerRank.DEVELOPER.getRankLevel();

    private Reflect.WrappedField<Boolean, PacketPlayOutLogin> reducedDebugField;

    public void onLoad(StoryPlugin plugin) {
        this.reducedDebugField = Reflect.getField(PacketPlayOutLogin.class, "h");
    }

    public void onEnable(){
        getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
    }

    @EventHandler
    public void onPlayerLogin(AsyncPacketOutEvent e) {
        if (e.getPacket() instanceof PacketPlayOutLogin) {
            PacketPlayOutLogin packet = (PacketPlayOutLogin) e.getPacket();

            if (e.getTarget() != null && getPlugin().getRankManager().getRank(e.getTarget()).getRankLevel() >= REQUIRED_RANK_LEVEL) {
                return;
            }

            reducedDebugField.set(packet, true);
        }
    }
}
