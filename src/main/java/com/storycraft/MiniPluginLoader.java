package com.storycraft;

import com.storycraft.util.MessageUtil;
import com.storycraft.util.MessageUtil.MessageType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MiniPluginLoader<T extends MainPlugin> {
    private T plugin;

    private List<MiniPlugin<T>> miniPluginList;
    private boolean enabled;

    public MiniPluginLoader(T plugin) {
        this.plugin = plugin;
        this.miniPluginList = new ArrayList<>();
        this.enabled = false;
    }

    public void onEnable() {
        setEnabled(true);

        new ArrayList<>(getMiniPluginList()).forEach(new Consumer<MiniPlugin<T>>() {
            @Override
            public void accept(MiniPlugin<T> miniPlugin) {
                if (!miniPlugin.isEnabled()) {
                    miniPlugin.onEnable();
                    miniPlugin.setEnabled(true);
                }
            }
        });
    }

    public void onDisable(boolean reload) {
        setEnabled(false);

        new ArrayList<>(getMiniPluginList()).forEach(new Consumer<MiniPlugin<T>>() {
            @Override
            public void accept(MiniPlugin<T> miniPlugin) {
                if (miniPlugin.isEnabled()) {
                    miniPlugin.onDisable(reload);
                    miniPlugin.setEnabled(false);
                }
            }
        });
    }

    public void addMiniPlugin(MiniPlugin<T> miniPlugin) {
        try {
            getMiniPluginList().add(miniPlugin);

            miniPlugin.onLoad(getPlugin());
            miniPlugin.setPlugin(getPlugin());

            if (isEnabled()){
                miniPlugin.onEnable();
                miniPlugin.setEnabled(true);
            }

            getPlugin().getConsoleSender().sendMessage(MessageUtil.getPluginMessage(MessageType.SUCCESS, "MiniPluginLoader", "모듈 " + miniPlugin.getClass().getSimpleName() + " 이(가) 로드되었습니다"));
        } catch (Exception e) {
            getPlugin().getConsoleSender().sendMessage(MessageUtil.getPluginMessage(MessageType.FAIL, "MiniPluginLoader", "모듈 " + miniPlugin.getClass().getName() + " 로드를 실패 했습니다"));
            e.printStackTrace();
        }
    }

    public boolean hasMiniPlugin(MiniPlugin<T> miniPlugin) {
        return getMiniPluginList().contains(miniPlugin);
    }

    public void removeMiniPlugin(MiniPlugin<T> miniPlugin) {
        getMiniPluginList().remove(miniPlugin);
    }

    public T getPlugin() {
        return plugin;
    }

    public boolean isEnabled() {
        return enabled;
    }

    protected void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<MiniPlugin<T>> getMiniPluginList() {
        return miniPluginList;
    }
}
