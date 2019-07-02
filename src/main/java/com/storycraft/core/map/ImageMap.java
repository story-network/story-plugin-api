package com.storycraft.core.map;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.net.URL;

import javax.imageio.ImageIO;

import com.storycraft.StoryPlugin;
import com.storycraft.command.ICommand;
import com.storycraft.core.MiniPlugin;
import com.storycraft.core.map.render.ImageRenderer;
import com.storycraft.util.AsyncTask;
import com.storycraft.util.MessageUtil;
import com.storycraft.util.MessageUtil.MessageType;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;

public class ImageMap extends MiniPlugin {

    private int idOffset;

    public ImageMap() {
        this.idOffset = 0;
    }

    @Override
    public void onLoad(StoryPlugin plugin) {
        plugin.getCommandManager().addCommand(new ImageMapCommand());
    }

    public int genIdOffset() {
        return --idOffset;
    }


    public class ImageMapCommand implements ICommand {
        @Override
        public String[] getAliases() {
            return new String[] { "imagemap" };
        }
    
        @Override
        public void onCommand(CommandSender sender, String[] args) {
            if (args.length < 1) {
                sender.sendMessage(MessageUtil.getPluginMessage(MessageType.FAIL, "ImageMap", "사용법: /imagemap <주소>"));
                return;
            }

            new AsyncTask<Void>(() -> {
                try {
                    String urlStr = String.join("%20", args);
                    URL url = new URL(urlStr);

                    BufferedImage image = ImageIO.read(url.openStream());

                    int id = genIdOffset();

                    CustomMapData data = new CustomMapData(new ImageRenderer(image, Color.BLACK));

                    Player p = (Player) sender;

                    ItemStack mapItem = new ItemStack(Material.MAP);
                    MapMeta mapMeta = (MapMeta) mapItem.getItemMeta();

                    mapMeta.setMapId(id);

                    mapItem.setItemMeta(mapMeta);

                    p.getInventory().addItem(mapItem);

                    getPlugin().getDecorator().getCustomMapManager().addCustomMap(id, data);
                    sender.sendMessage(MessageUtil.getPluginMessage(MessageType.SUCCESS, "ImageMap", "이미지 렌더링 완료 (영구 저장 되지 않습니다)"));

                } catch (Exception e) {
                    sender.sendMessage(MessageUtil.getPluginMessage(MessageType.FAIL, "ImageMap", "이미지를 가져올 수 없습니다. " + e.getLocalizedMessage()));
                }



                return null;
            }).run();
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
            return true;
        }

        @Override
        public String getPermissionRequired() {
            return "server.command.imagemap";
        }
    }
}