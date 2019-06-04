package com.storycraft.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class MojangAPI {

    public static String getSessionPlayerUUID(String nickname) throws IOException {
        URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + nickname);
        InputStreamReader reader = new InputStreamReader(url.openStream());

        String uuid = new JsonParser().parse(reader).getAsJsonObject().get("id").getAsString();

        return uuid;
    }

    public static JsonObject getSessionPlayerProperty(String rawId) throws IOException {
        URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + rawId + "?unsigned=false");
        InputStreamReader reader = new InputStreamReader(url.openStream());

        JsonObject rawProperty = new JsonParser().parse(reader).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();
        JsonObject textureProperty = new JsonObject();

        textureProperty.addProperty("value", rawProperty.get("value").getAsString());
        textureProperty.addProperty("signature", rawProperty.get("signature").getAsString());

        //String texture = textureProperty.get("value").getAsString();
        //String signature = textureProperty.get("signature").getAsString();

        return textureProperty;
    }

}