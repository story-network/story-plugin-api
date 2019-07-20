package com.storycraft.core;

import com.storycraft.MiniPlugin;
import com.storycraft.MiniPluginLoader;
import com.storycraft.StoryPlugin;
import com.storycraft.core.broadcast.BroadcastManager;
import com.storycraft.core.broadcast.ToastCommand;
import com.storycraft.core.chat.ChatManager;
import com.storycraft.core.chat.ColoredChat;
import com.storycraft.core.combat.DamageHologram;
import com.storycraft.core.command.SayCommand;
import com.storycraft.core.config.IngameConfigManager;
import com.storycraft.core.discord.DiscordChatHook;
import com.storycraft.core.disguise.HeadDisguise;
import com.storycraft.core.player.PlayerManager;
import com.storycraft.core.player.debug.UserDebug;
import com.storycraft.core.player.home.HomeManager;
import com.storycraft.core.playerlist.CustomPlayerList;
import com.storycraft.core.jukebox.JukeboxPlay;
import com.storycraft.core.map.ImageMap;
import com.storycraft.core.entity.EntityBlood;
import com.storycraft.core.entity.EntityManager;
import com.storycraft.core.explosion.Explosion;
import com.storycraft.core.faq.FAQCommand;
import com.storycraft.core.fly.FlyCommand;
import com.storycraft.core.dropping.DropCounter;
import com.storycraft.core.dropping.HologramXPDrop;
import com.storycraft.core.motd.ServerMotd;
import com.storycraft.core.payload.PayloadBrandEditor;
import com.storycraft.core.permission.PermissionManager;
import com.storycraft.core.plugin.IngamePluginManager;
import com.storycraft.core.punish.PunishManager;
import com.storycraft.core.randomtp.RandomTP;
import com.storycraft.core.rank.RankManager;
import com.storycraft.core.saving.AutoSaveManager;
import com.storycraft.core.skin.PlayerCustomSkin;
import com.storycraft.core.spawn.ServerSpawnManager;
import com.storycraft.core.teleport.TeleportAskCommand;
import com.storycraft.core.uuid.UUIDRevealCommand;
import com.storycraft.core.world.WorldTeleporter;
import com.storycraft.mod.ModManager;

public class CoreManager extends MiniPlugin {

    private StoryPlugin plugin;

    private PlayerManager playerManager;
    private EntityManager entityManager;

    private PunishManager punishManager;

    private RankManager rankManager;

    private ModManager modManager;

    private DiscordChatHook discordChat;

    public CoreManager(StoryPlugin plugin) {
        this.plugin = plugin;
    }

    public StoryPlugin getPlugin() {
        return plugin;
    }

    public MiniPluginLoader getMiniPluginLoader() {
        return getPlugin().getMiniPluginLoader();
    }

    @Override
    public void onLoad(StoryPlugin plugin) {
        if (plugin != getPlugin()) {
            throw new RuntimeException("Illegal load");
        }

        preInitMiniPlugin();

        initMiniPlugin();
    }

    protected void preInitMiniPlugin() {
        MiniPluginLoader loader = getMiniPluginLoader();
        loader.addMiniPlugin(rankManager = new RankManager());
        loader.addMiniPlugin(playerManager = new PlayerManager());
        loader.addMiniPlugin(entityManager = new EntityManager());
        loader.addMiniPlugin(punishManager = new PunishManager());
    }

    protected void initMiniPlugin() {
        MiniPluginLoader loader = getMiniPluginLoader();
        loader.addMiniPlugin(new PermissionManager());
        loader.addMiniPlugin(new Explosion());
        loader.addMiniPlugin(new ServerSpawnManager());
        loader.addMiniPlugin(new IngameConfigManager());
        loader.addMiniPlugin(new ChatManager());
        loader.addMiniPlugin(new EntityBlood());
        loader.addMiniPlugin(new DropCounter());
        loader.addMiniPlugin(new RandomTP());
        loader.addMiniPlugin(discordChat = new DiscordChatHook());
        loader.addMiniPlugin(new DamageHologram());
        loader.addMiniPlugin(new JukeboxPlay());
        loader.addMiniPlugin(new BroadcastManager());
        loader.addMiniPlugin(new ColoredChat());
        loader.addMiniPlugin(new FlyCommand());
        loader.addMiniPlugin(new FAQCommand());
        loader.addMiniPlugin(new ToastCommand());
        loader.addMiniPlugin(new HeadDisguise());
        loader.addMiniPlugin(new HologramXPDrop());
        loader.addMiniPlugin(new UUIDRevealCommand());
        loader.addMiniPlugin(new AutoSaveManager());
        loader.addMiniPlugin(new WorldTeleporter());
        loader.addMiniPlugin(new IngamePluginManager());
        loader.addMiniPlugin(new TeleportAskCommand());
        loader.addMiniPlugin(new PlayerCustomSkin());
        loader.addMiniPlugin(new CustomPlayerList());
        loader.addMiniPlugin(new ImageMap());
        loader.addMiniPlugin(new PayloadBrandEditor());

        postInitMiniPlugin();
    }

    protected void postInitMiniPlugin() {
        MiniPluginLoader loader = getMiniPluginLoader();

        loader.addMiniPlugin(modManager = new ModManager(getPlugin()));
    }

    public ModManager getModManager() {
        return modManager;
    }

    public DiscordChatHook getDiscordChat() {
        return discordChat;
    }

    public RankManager getRankManager() {
        return rankManager;
    }

    public PunishManager getPunishManager() {
        return punishManager;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }
}