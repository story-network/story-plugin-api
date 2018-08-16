package com.storycraft.server.forge;

import com.google.gson.*;
import net.minecraft.server.v1_13_R1.IChatBaseComponent;
import net.minecraft.server.v1_13_R1.ServerPing;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ForgeServerPing extends ServerPing {

    protected ServerPing serverPing;
    private List<ModItem> modItemList;

    public ForgeServerPing(ServerPing serverPing) {
        this(serverPing, new ArrayList<>());
    }

    public ForgeServerPing(ServerPing serverPing, List<ModItem> modItemList) {
        this.serverPing = serverPing;
        this.modItemList = modItemList;
    }

    public List<ModItem> getModItemList() {
        return modItemList;
    }

    @Override
    public IChatBaseComponent a() {
        return serverPing.a();
    }

    @Override
    public void setMOTD(IChatBaseComponent var1) {
        serverPing.setMOTD(var1);
    }

    @Override
    public ServerPing.ServerPingPlayerSample b() {
        return serverPing.b();
    }

    @Override
    public void setPlayerSample(ServerPing.ServerPingPlayerSample var1) {
        serverPing.setPlayerSample(var1);
    }

    @Override
    public ServerPing.ServerData getServerData() {
        return serverPing.getServerData();
    }

    @Override
    public void setServerInfo(ServerPing.ServerData var1) {
        serverPing.setServerInfo(var1);
    }

    @Override
    public void setFavicon(String var1) {
        serverPing.setFavicon(var1);
    }

    public class ModItem {

        private String modId;
        private String modVer;

        public ModItem(String modId, String modVer) {
            this.modId = modId;
            this.modVer = modVer;
        }

        public String getModId() {
            return modId;
        }

        public String getModVer() {
            return modVer;
        }
    }

    public static class Serializer extends ServerPing implements JsonDeserializer<ForgeServerPing>, JsonSerializer<ForgeServerPing> {

        private JsonObject createModInfo(ForgeServerPing ping) {
            JsonObject obj = new JsonObject();

            obj.addProperty("type", "FML");
            obj.add("modList", createModList(ping));

            return obj;
        }

        private JsonArray createModList(ForgeServerPing forgeServerPing) {
            JsonArray array = new JsonArray();

            for (ModItem item : forgeServerPing.getModItemList())
                array.add(createModItem(item.modId, item.modVer));

            return array;
        }

        private JsonObject createModItem(String modId, String modVer) {
            JsonObject obj = new JsonObject();

            obj.addProperty("modid", modId);
            obj.addProperty("version", modVer);

            return obj;
        }

        @Override
        public JsonElement serialize(ForgeServerPing forgeServerPing, Type type, JsonSerializationContext jsc) {
            JsonObject obj = (JsonObject) new ServerPing.Serializer().a(forgeServerPing.serverPing, type, jsc);

            obj.add("modinfo", createModInfo(forgeServerPing));

            return obj;
        }

        @Override
        public ForgeServerPing deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jdc) throws JsonParseException {
            return new ForgeServerPing(new ServerPing.Serializer().a(jsonElement, type, jdc));
        }
    }
}
