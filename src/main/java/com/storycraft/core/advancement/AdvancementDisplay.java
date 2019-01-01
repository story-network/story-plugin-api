package com.storycraft.core.advancement;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class AdvancementDisplay {

    private String title;
    private String description;
    private ItemStack icon;
    private Advancement.FrameType frameType;

    private boolean announce;
    private boolean toast;
    private boolean hidden;

    public AdvancementDisplay() {
        this.title = "";
        this.description = "";
        this.icon = new ItemStack(Material.STONE);
        this.frameType = Advancement.FrameType.TASK;

        this.announce = true;
        this.toast = true;
        this.hidden = false;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Advancement.FrameType getFrameType() {
        return frameType;
    }

    public boolean needAnnounce() {
        return announce;
    }

    public boolean isHidden() {
        return hidden;
    }

    public boolean isToast() {
        return toast;
    }

    public ItemStack getIcon() {
        return icon;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public void setAnnounce(boolean announce) {
        this.announce = announce;
    }

    public void setToastEnabled(boolean toast) {
        this.toast = toast;
    }

    public void setFrameType(Advancement.FrameType frameType) {
        this.frameType = frameType;
    }

    public void setIcon(ItemStack icon) {
        this.icon = icon;
    }

    public static class Builder {

        private AdvancementDisplay display;

        public Builder() {
            this.display = new AdvancementDisplay();
        }

        public Builder setTitle(String title) {
            display.setTitle(title);

            return this;
        }

        public Builder setDescription(String description) {
            display.setDescription(description);

            return this;
        }

        public Builder setHidden(boolean hidden) {
            display.setHidden(hidden);

            return this;
        }
    
        public Builder setAnnounce(boolean flag) {
            display.setAnnounce(flag);

            return this;
        }
    
        public Builder setToastEnabled(boolean toast) {
            display.setToastEnabled(toast);

            return this;
        }

        public Builder setFrameType(Advancement.FrameType frameType) {
            display.setFrameType(frameType);;

            return this;
        }
    
        public Builder setIcon(ItemStack icon) {
            display.setIcon(icon);;

            return this;
        }

        public AdvancementDisplay getDisplay() {
            return display;
        }
    }
}