package com.storycraft.core.map;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Set;
import java.util.Map.Entry;

import javax.imageio.ImageIO;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.storycraft.StoryPlugin;
import com.storycraft.command.ICommand;
import com.storycraft.config.json.JsonConfigEntry;
import com.storycraft.config.json.JsonConfigFile;
import com.storycraft.config.json.JsonConfigPrettyFile;
import com.storycraft.core.MiniPlugin;
import com.storycraft.core.map.render.ImageRenderer;
import com.storycraft.util.AsyncTask;
import com.storycraft.util.Base64Util;
import com.storycraft.util.MessageUtil;
import com.storycraft.util.MessageUtil.MessageType;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.org.apache.commons.codec.binary.Base64InputStream;
import org.bukkit.craftbukkit.libs.org.apache.commons.codec.digest.DigestUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class ImageMap extends MiniPlugin {

    private JsonConfigFile configFile;

    public ImageMap() {

    }

    @Override
    public void onLoad(StoryPlugin plugin) {
        plugin.getCommandManager().addCommand(new ImageMapCommand());
        
        try {
            plugin.getConfigManager().addConfigFile("imagemap_db.json", configFile = new JsonConfigPrettyFile()).getSync();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onEnable() {
        new AsyncTask<Void>(() -> {
            JsonConfigEntry entry = getIdStorage();

            for (Entry<String, JsonElement> element : entry.getJsonObject().entrySet()) {
                String strId = element.getKey();

                try {
                    int id = Integer.parseInt(strId);

                    getPlugin().getDecorator().getCustomMapManager().addCustomMap(id, new CustomMapData(new ImageRenderer(getBufferedImage(id), Color.BLACK)));
                } catch (Exception e) {
                    continue;
                }
            }

            return null;
        }).run();
    }

    private JsonConfigEntry getEntry(JsonConfigEntry parent, String name) {
        JsonConfigEntry entry = parent.getObject("storage");
        if (entry == null) {
            parent.set("storage", entry = parent.createEntry());
        }

        return entry;
    }

    protected JsonConfigEntry getHashStorage() {
        return getEntry(configFile, "storage");
    }

    protected JsonConfigEntry getIdStorage() {
        return getEntry(configFile, "id_set");
    }

    protected JsonConfigEntry getImageInfo(int id) {
        return getEntry(getIdStorage(), id + "");
    }

    public boolean hasImageInfo(int id) {
        return getImageInfo(id).contains("image");
    }

    public String getImageHash(int id) {
        JsonConfigEntry entry = getImageInfo(id);

        if (!entry.contains("image"))
            return null;

        try {
            String str = entry.get("image").getAsString();

            if ("null".equals(str))
                return null;

            return str;
        } catch (Exception e) {
            e.printStackTrace();

            entry.set("image", "null");

            return null;
        }
    }

    public long getImageCreated(int id) {
        JsonConfigEntry entry = getImageInfo(id);

        if (!entry.contains("created"))
            return -1;

        try {
            long date = entry.get("created").getAsLong();

            return date;
        } catch (Exception e) {
            e.printStackTrace();

            entry.set("created", -1L);

            return -1;
        }
    }

    public String getImageFromStorage(String hash) {
        try {
            return getHashStorage().get(hash).getAsString();
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    public String getBase64Image(int id) {
        String hash = getImageHash(id);

        if (hash == null)
            return null;

        return getImageFromStorage(hash);
    }

    public BufferedImage getBufferedImage(int id) {
        String encoded = getBase64Image(id);

        if (encoded == null)
            return null;

        try {
            return ImageIO.read(new ByteArrayInputStream(Base64Util.decode(encoded).toByteArray()));
        } catch (Exception e) {
            e.printStackTrace();

            return null;
        }
    }

    public void setImage(int id, BufferedImage image) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ImageIO.write(image, "png", output);

        byte[] data = output.toByteArray();

        String hash = DigestUtils.sha1Hex(data);

        setStorageImage(hash, Base64Util.encode(new ByteArrayInputStream(data)));
        setIdInfo(id, System.currentTimeMillis(), hash);
    }

    protected void setStorageImage(String hash, String data) {
        getHashStorage().set(hash, data);
    }

    protected void setIdInfo(int id, long created, String hash) {
        JsonConfigEntry entry = getEntry(getIdStorage(), id + "");

        entry.set("image", hash);
        entry.set("created", created);
    }

    public class ImageMapCommand implements ICommand {

        @Override
        public String[] getAliases() {
            return new String[] { "imagemap" };
        }

        public int genNextId() {
            MapView view = getPlugin().getServer().createMap(getPlugin().getDefaultWorld());
            for (MapRenderer renderer : view.getRenderers()) {
                view.removeRenderer(renderer);
            }

            return view.getId();
        }
    
        @Override
        public void onCommand(CommandSender sender, String[] args) {
            if (args.length < 2) {
                sender.sendMessage(MessageUtil.getPluginMessage(MessageType.FAIL, "ImageMap", "사용법: /imagemap <맵 id> <주소>"));
                return;
            }

            new AsyncTask<Void>(() -> {
                try {
                    String urlStr = String.join("%20", args);
                    URL url = new URL(urlStr);

                    BufferedImage rawImage = ImageIO.read(url.openStream());

                    int width = rawImage.getWidth();
                    int height = rawImage.getHeight();

                    double scaledWidth = 0;
                    double scaledHeight = 0;

                    if (width >= height) {
                        scaledWidth = 128;
                        scaledHeight = (128 * height) / width;
                    } else {
                        scaledWidth =  (128 * width) / height;
                        scaledHeight = 128;
                    }

                    BufferedImage resized = new BufferedImage((int) scaledWidth, (int) scaledHeight, BufferedImage.TYPE_INT_ARGB);

                    Graphics2D g2d = resized.createGraphics();
                    g2d.drawImage(rawImage.getScaledInstance((int) scaledWidth, (int) scaledHeight, Image.SCALE_SMOOTH), 0, 0, null);
                    g2d.dispose();
                    
                    int id = genNextId();

                    setImage(id, resized);

                    CustomMapData data = new CustomMapData(new ImageRenderer(resized, Color.BLACK));

                    Player p = (Player) sender;

                    ItemStack mapItem = new ItemStack(Material.FILLED_MAP, 1);

                    MapMeta mapMeta = (MapMeta) mapItem.getItemMeta();
                    mapMeta.setMapId(id);
                    mapItem.setItemMeta(mapMeta);

                    p.getInventory().addItem(mapItem);

                    getPlugin().getDecorator().getCustomMapManager().addCustomMap(id, data);
                    sender.sendMessage(MessageUtil.getPluginMessage(MessageType.SUCCESS, "ImageMap", "이미지 렌더링 완료 (id = " + id + ")"));

                } catch (Exception e) {
                    sender.sendMessage(MessageUtil.getPluginMessage(MessageType.FAIL, "ImageMap", "이미지를 가져올 수 없습니다. 올바른 이미지가 아닙니다." + e.getLocalizedMessage()));
                }

                return null;
            }).run();
        }
    
        @Override
        public boolean availableOnConsole() {
            return true;
        }
    
        @Override
        public boolean availableOnCommandBlock() {
            return true;
        }
    
        @Override
        public boolean isPermissionRequired() {
            return false;
        }

        @Override
        public String getPermissionRequired() {
            return "server.command.imagemap";
        }
    }
}
