package com.storycraft.util;

import org.bukkit.ChatColor;

public class MessageUtil {

    public static String getPluginMessage(MessageType type, String title, String content) {
        return getPluginMessage(MessageTemplate.DEFAULT, type, title, content);
    }

    public static String getPluginMessage(MessageTemplate template, MessageType type, String title, String content) {
        return template.getMessage(type, title, content);
    }

    public abstract static class MessageTemplate {

        public static final MessageTemplate DEFAULT = new MessageTemplate() {
            @Override
            public String getMessage(MessageType type, String title, String content) {
                return type.getTitleColor() + title + "> " + type.getContentColor() + content;
            }
        };

        public static final MessageTemplate OLD = new MessageTemplate() {
            @Override
            public String getMessage(MessageType type, String title, String content) {
                return ChatColor.LIGHT_PURPLE + "[ " + type.getTitleColor() + title + ChatColor.LIGHT_PURPLE + "] " + type.getContentColor() + content;
            }
        };

        public abstract String getMessage(MessageType type, String title, String content);
    }

    public enum MessageType {
        ALERT(ChatColor.YELLOW, ChatColor.GRAY),
        FAIL(ChatColor.RED, ChatColor.GRAY),
        SUCCESS(ChatColor.GREEN, ChatColor.GRAY),
        TIP(ChatColor.BLUE, ChatColor.GRAY),
        TIP_WHITE(ChatColor.BLUE, ChatColor.WHITE);

        private ChatColor titleColor;
        private ChatColor contentColor;

        MessageType(ChatColor titleColor, ChatColor contentColor) {
            this.titleColor = titleColor;
            this.contentColor = contentColor;
        }

        public ChatColor getTitleColor() {
            return titleColor;
        }

        public ChatColor getContentColor() {
            return contentColor;
        }
    }
}
