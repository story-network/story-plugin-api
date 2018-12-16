package com.storycraft.core.skin;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.storycraft.StoryPlugin;
import com.storycraft.command.ICommand;
import com.storycraft.config.json.JsonConfigEntry;
import com.storycraft.config.json.JsonConfigFile;
import com.storycraft.core.MiniPlugin;
import com.storycraft.server.packet.AsyncPacketOutEvent;
import com.storycraft.util.AsyncTask;
import com.storycraft.util.ConnectionUtil;
import com.storycraft.util.MessageUtil;
import com.storycraft.util.MineSkinAPI;
import com.storycraft.util.MojangAPI;
import com.storycraft.util.AsyncTask.AsyncCallable;
import com.storycraft.util.MessageUtil.MessageType;
import com.storycraft.util.reflect.Reflect;
import com.storycraft.util.reflect.Reflect.WrappedField;

import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_13_R2.CraftServer;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import net.minecraft.server.v1_13_R2.DimensionManager;
import net.minecraft.server.v1_13_R2.EntityPlayer;
import net.minecraft.server.v1_13_R2.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_13_R2.PacketPlayOutPosition;
import net.minecraft.server.v1_13_R2.PacketPlayOutRespawn;
import net.minecraft.server.v1_13_R2.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;

public class PlayerCustomSkin extends MiniPlugin implements Listener {

    private JsonConfigFile skinConfig;

    private WrappedField<EnumPlayerInfoAction, PacketPlayOutPlayerInfo> infoAction;
    private WrappedField<List<?>, PacketPlayOutPlayerInfo> infodataList;
    private WrappedField<GameProfile, Object> gameProfileField;

    private boolean enabled;
    
    @Override
    public void onLoad(StoryPlugin plugin) {
        this.enabled = true;

        plugin.getConfigManager().addConfigFile("player_skin.json", skinConfig = new JsonConfigFile()).then(this::onConfigLoad).run();
        plugin.getCommandManager().addCommand(new CustomSkinCommand());
        setPlugin(plugin);//preload
        
        this.infodataList = Reflect.getField(PacketPlayOutPlayerInfo.class, "b");
        this.infoAction = Reflect.getField(PacketPlayOutPlayerInfo.class, "a");
        this.gameProfileField = Reflect.getField(PacketPlayOutPlayerInfo.class.getDeclaredClasses()[0], "d");

        gameProfileField.unlockFinal();
    }

    public void onEnable() {
        getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());
    }

    public void onDisable(boolean reload) {
        this.enabled = false;

        for (Player p : getPlugin().getServer().getOnlinePlayers()) {
            UUID profileId = getPlayerProfileId(p);

            if (!p.isOnline() || !isPlayerHaveCustomSkin(profileId))
                continue;

            updatePlayerInfo(p);
        }
    }

    protected JsonConfigEntry getPlayerEntry(UUID profileId) {
        JsonConfigEntry entry = skinConfig.getObject(profileId.toString());

        if (entry == null) {
            skinConfig.set(profileId.toString(), entry = skinConfig.createEntry());
        }

        return entry;
    }

    protected JsonConfigEntry getPlayerEntry(Player p) {
        return getPlayerEntry(getPlayerProfileId(p));
    }

    public boolean isPlayerHaveCustomSkin(UUID profileId) {
        JsonConfigEntry entry = getPlayerEntry(profileId);

        try {
            return entry.get("enabled").getAsBoolean();
        } catch (Exception e) {
            setPlayerHaveCustomSkin(profileId, false);
            return false;
        }
    }

    public boolean isPlayerHaveCustomSkin(Player p) {
        return isPlayerHaveCustomSkin(getPlayerProfileId(p));
    }

    public void setPlayerHaveCustomSkin(UUID profileId, boolean flag) {
        getPlayerEntry(profileId).set("enabled", flag);
    }

    public void setPlayerHaveCustomSkin(Player p, boolean flag) {
        setPlayerHaveCustomSkin(getPlayerProfileId(p), flag);
    }

    public String getPlayerSkinTexture(UUID profileId) throws IOException {
        if (!isPlayerHaveCustomSkin(profileId))
            return "";
        
        try {
            return getPlayerEntry(profileId).get("skin-textures").getAsString();
        } catch (Exception e) {
            JsonObject property = MineSkinAPI.getSessionPlayerProperty(profileId.toString().replaceAll("-", ""));
            String textures;

            setPlayerSkinTexture(profileId, textures = property.get("value").getAsString(), property.get("signature").getAsString());

            return textures;
        }
    }

    public String getPlayerSkinTexture(Player p) throws IOException {
        return getPlayerSkinTexture(getPlayerProfileId(p));
    }

    public void setPlayerSkinTexture(UUID profileId, String textures, String signature) {
        JsonConfigEntry entry = getPlayerEntry(profileId);
        entry.set("skin-textures", textures);
        entry.set("skin-signature", signature);

        setPlayerHaveCustomSkin(profileId, true);
    }

    public String getPlayerSkinSignature(UUID profileId) throws IOException {
        if (!isPlayerHaveCustomSkin(profileId))
            return "";
        
        try {
            return getPlayerEntry(profileId).get("skin-signature").getAsString();
        } catch (Exception e) {
            JsonObject property = MineSkinAPI.getSessionPlayerProperty(profileId.toString().replaceAll("-", ""));
            String signature;

            setPlayerSkinTexture(profileId, property.get("value").getAsString(), signature = property.get("signature").getAsString());

            return signature;
        }
    }

    public String getPlayerSkinSignature(Player p) throws IOException {
        return getPlayerSkinSignature(getPlayerProfileId(p));
    }

    public void setPlayerSkinTexture(Player p, String textures, String signature) {
        setPlayerSkinTexture(getPlayerProfileId(p), textures, signature);
    }

    public UUID getPlayerProfileId(Player p) {
        return ((CraftPlayer) p).getHandle().getProfile().getId();
    }

    public void setPlayerSkin(Player p, String name) throws IOException {
        JsonObject object = MineSkinAPI.getSessionPlayerProperty(MojangAPI.getSessionPlayerUUID(name));
        setPlayerSkinTexture(p, object.get("value").getAsString(), object.get("signature").getAsString());
    }

    protected void onConfigLoad(Void v, Throwable t) {
        for (Player p : new ArrayList<>(getPlugin().getServer().getOnlinePlayers())) {
            UUID profileId = getPlayerProfileId(p);

            if (!p.isOnline() || !isPlayerHaveCustomSkin(profileId))
                continue;

            updatePlayerInfo(p);
        }
    }

    public void updatePlayerInfo(Player p) {
        EntityPlayer ep = ((CraftPlayer) p).getHandle();
        PacketPlayOutPlayerInfo removePacket = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER, ep);
        PacketPlayOutPlayerInfo infoPacket = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER, ep);
        PacketPlayOutRespawn respawnPacketOther = new PacketPlayOutRespawn(ep.dimension.getDimensionID() >= 0 ? DimensionManager.NETHER : DimensionManager.OVERWORLD, ep.getWorld().getDifficulty(), ep.getWorld().S(), ep.playerInteractManager.getGameMode());
        PacketPlayOutRespawn respawnPacket = new PacketPlayOutRespawn(ep.dimension, ep.getWorld().getDifficulty(), ep.getWorld().S(), ep.playerInteractManager.getGameMode());
        PacketPlayOutPosition positionCorrectionPacket = new PacketPlayOutPosition(ep.locX, ep.locY, ep.locZ, ep.yaw, ep.pitch, new HashSet<>(), (int) (Math.random() * 9999999));
        ConnectionUtil.sendPacket(removePacket, infoPacket);
        ConnectionUtil.sendPacket(p, respawnPacketOther, respawnPacket, positionCorrectionPacket);
        p.updateInventory();
    }

    @EventHandler
    public void onPlayerListPacketSent(AsyncPacketOutEvent e) {
        if (e.getPacket() instanceof PacketPlayOutPlayerInfo && enabled) {
            PacketPlayOutPlayerInfo infoPacket = (PacketPlayOutPlayerInfo) e.getPacket();

            if (infoAction.get(infoPacket) == EnumPlayerInfoAction.REMOVE_PLAYER) {
                return;
            }

            List<?> list = infodataList.get(infoPacket);

            for (Object data : list) {
                GameProfile profile = gameProfileField.get(data);

                if (!isPlayerHaveCustomSkin(profile.getId()))
                    continue;

                try {
                    String textures = getPlayerSkinTexture(profile.getId());
                    String signature = getPlayerSkinSignature(profile.getId());

                    GameProfile newProfile = new GameProfile(profile.getId(), profile.getName());

                    newProfile.getProperties().put("textures", new Property("textures", textures, signature));
                
                    gameProfileField.set(data, newProfile);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public class CustomSkinCommand implements ICommand {

        @Override
        public String[] getAliases() {
            return new String[] { "skin" };
        }

        @Override
        public void onCommand(CommandSender sender, String[] args) {
            if (args.length < 1) {
                sender.sendMessage(MessageUtil.getPluginMessage(MessageType.FAIL, "CustomSkin", "사용법 /skin <set / disable>"));
                return;
            }

            Player p = (Player) sender;
            String option = args[0];

            if ("set".equals(option)) {
                if (args.length < 2) {
                    p.sendMessage(MessageUtil.getPluginMessage(MessageType.FAIL, "CustomSkin", "사용법 /skin set <플레이어 이름>"));
                    return;
                }

                String skinPlayer = args[1];

                new AsyncTask<Void>(new AsyncCallable<Void>() {
                    
                    @Override
                    public Void get() throws IOException {

                        setPlayerSkin(p, skinPlayer);
                        updatePlayerInfo(p);

                        return null;
                    }

                }).then((Void v, Throwable t) -> {
                    if (t != null) {
                        p.sendMessage(MessageUtil.getPluginMessage(MessageType.FAIL, "CustomSkin", "플레이어 " + skinPlayer + " 스킨 적용이 실패 했습니다. 약 1분후 재시도 해주세요"));
                        return;
                    }

                    p.sendMessage(MessageUtil.getPluginMessage(MessageType.SUCCESS, "CustomSkin", "플레이어 " + skinPlayer + " 스킨이 적용되었습니다"));
                }).run();

            } else if ("disable".equals(option)) {
                if (!isPlayerHaveCustomSkin(p)) {
                    p.sendMessage(MessageUtil.getPluginMessage(MessageType.FAIL, "CustomSkin", "이미 비활성화 상태입니다"));
                }
                else {
                    updatePlayerInfo(p);
                    p.sendMessage(MessageUtil.getPluginMessage(MessageType.SUCCESS, "CustomSkin", "스킨이 비활성화 되었습니다"));
                }
            } else {
                p.sendMessage(MessageUtil.getPluginMessage(MessageType.FAIL, "CustomSkin", "사용법 /skin <get / set / disable>"));
                return;
            }
        }

        @Override
        public boolean availableOnConsole() {
            return false;
        }

        @Override
        public boolean availableOnCommandBlock() {
            return false;
        }

        @Override
        public boolean isPermissionRequired() {
            return false;
		}

    }

}
