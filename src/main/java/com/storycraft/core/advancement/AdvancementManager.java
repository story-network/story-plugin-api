package com.storycraft.core.advancement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.storycraft.MiniPlugin;
import com.storycraft.util.ConnectionUtil;

import org.bukkit.craftbukkit.v1_14_R1.*;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_14_R1.Advancement;
import net.minecraft.server.v1_14_R1.AdvancementFrameType;
import net.minecraft.server.v1_14_R1.AdvancementProgress;
import net.minecraft.server.v1_14_R1.AdvancementRewards;
import net.minecraft.server.v1_14_R1.ChatComponentText;
import net.minecraft.server.v1_14_R1.Criterion;
import net.minecraft.server.v1_14_R1.CriterionInstance;
import net.minecraft.server.v1_14_R1.CriterionProgress;
import net.minecraft.server.v1_14_R1.MinecraftKey;
import net.minecraft.server.v1_14_R1.PacketPlayOutAdvancements;

public class AdvancementManager extends MiniPlugin {

    public void broadcastToast(String text, AdvancementType frameType, org.bukkit.inventory.ItemStack icon) {
        sendToastToPlayer(getPlugin().getServer().getOnlinePlayers(), text, frameType, icon);
    }

    public void sendToastToPlayer(Player p, String text, AdvancementType frameType, org.bukkit.inventory.ItemStack icon) {
        sendToastToPlayer(Lists.newArrayList(p), text, frameType, icon);
    }

    public void sendToastToPlayer(Collection<? extends Player> playerList, String text, AdvancementType frameType, org.bukkit.inventory.ItemStack icon) {

        List<Advancement> list = new ArrayList<>(1);

        Map<MinecraftKey, AdvancementProgress> progressMap = new HashMap<>();
        Map<MinecraftKey, AdvancementProgress> doneProgressMap = new HashMap<>();

        String tempName = "advancement_" + System.nanoTime();

        Advancement d = createAdvancement(tempName, new AdvancementDisplay.Builder().setTitle(text).setFrameType(frameType).setHidden(true).setIcon(icon).getDisplay(), Lists.newArrayList(createSimpleCriterion("server:toast")), new String[][] { { "server:toast" } });
        list.add(d);

        AdvancementProgress ad = createProgress(d);
        ad.a("server:toast");

        progressMap.put(d.getName(), createProgress(d));
        doneProgressMap.put(d.getName(), ad);

        PacketPlayOutAdvancements advancementDonePacket = new PacketPlayOutAdvancements(false, list, new HashSet<>(), doneProgressMap);
        PacketPlayOutAdvancements advancementRemovePacket = new PacketPlayOutAdvancements(false, new ArrayList<>(), Sets.newHashSet(new MinecraftKey(tempName)), new HashMap<>());

        for (Player p : playerList)
            ConnectionUtil.sendPacket(p, advancementDonePacket, advancementRemovePacket);
    }

    public Advancement createAdvancement(String name) {
        return createAdvancement(name, null);
    }

    public Advancement createAdvancement(String name, AdvancementDisplay display) {
        return createAdvancement(name, display, new ArrayList<>());
    }

    public Advancement createAdvancement(String name, AdvancementDisplay display, List<Criterion> criterionList) {
        return createAdvancement(name, display, criterionList, new String[0][0]);
    }

    public Advancement createAdvancement(String name, AdvancementDisplay display, List<Criterion> criterionList, String[][] doneList) {
        Map<String, Criterion> criterionMap = new HashMap<>();

        for (Criterion crit : criterionList) {
            criterionMap.put(crit.a().a().toString(), crit);
        }

        Advancement advancement = new Advancement(new MinecraftKey(name), null, display == null ? null : getNMSDisplay(display), AdvancementRewards.a, criterionMap, doneList);

        return advancement;
    }

    public AdvancementProgress createProgress(Advancement advancement) {
        AdvancementProgress progress = new AdvancementProgress();

        progress.a(advancement.getCriteria(), advancement.i());

        return progress;
    }

    public CriterionProgress createCriterionProgress(long time) {
        return CriterionProgress.a(time + "");
    }

    public Criterion createSimpleCriterion(String name) {
        return new Criterion(new CriterionInstance(){
        
            @Override
            public MinecraftKey a() {
                return new MinecraftKey(name);
            }
        });
    }

    public net.minecraft.server.v1_14_R1.AdvancementDisplay getNMSDisplay(AdvancementDisplay display) {
        
        net.minecraft.server.v1_14_R1.AdvancementDisplay nmsDisplay = new net.minecraft.server.v1_14_R1.AdvancementDisplay(
            CraftItemStack.asNMSCopy(display.getIcon()),
            new ChatComponentText(display.getTitle()),
            new ChatComponentText(display.getDescription()),
            null,
            getNMSFrameType(display.getFrameType()),
            display.needAnnounce(),
            display.isToast(),
            display.isHidden());
    
        return nmsDisplay;
    }

    public AdvancementFrameType getNMSFrameType(AdvancementType type) {
        if (type == null)
            return null;

        switch (type) {
            case TASK:
                return AdvancementFrameType.TASK;
            case GOAL:
                return AdvancementFrameType.GOAL;
            case CHALLENGE:
                return AdvancementFrameType.CHALLENGE;
            default:
                return null;
        }
    }
}
