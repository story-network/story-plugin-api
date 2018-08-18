package com.storycraft.core.saving;

import com.storycraft.core.MiniPlugin;
import com.storycraft.util.MessageUtil;
import org.bukkit.World;

public class AutoSaveManager extends MiniPlugin {

    private static final int START_OFFSET = 20;
    private static final int SAVE_INTERVAL = 20 * 60 * 30;

    private int currentTask;

    public void onEnable() {
        runTask();
    }

    public void onDisable (){
        getPlugin().getServer().getScheduler().cancelTask(currentTask);
    }

    private void runTask() {
        currentTask = getPlugin().getServer().getScheduler().scheduleSyncRepeatingTask(getPlugin(), this::saveAll, START_OFFSET, SAVE_INTERVAL);
    }

    private void saveAll() {
        getPlugin().getServer().broadcastMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.ALERT, "SaveManager", "서버 데이터 저장중..."));

        try {
            for (World w : getPlugin().getServer().getWorlds()) {
                w.save();
            }

            getPlugin().getConfigManager().saveAll();
        } catch (Exception e) {
            getPlugin().getServer().broadcastMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.FAIL, "SaveManager", "데이터 저장이 실패 했습니다 :("));
            return;
        }

        getPlugin().getServer().broadcastMessage(MessageUtil.getPluginMessage(MessageUtil.MessageType.SUCCESS, "SaveManager", "데이터 저장 완료"));
    }
}
