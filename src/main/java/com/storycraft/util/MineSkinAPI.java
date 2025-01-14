package com.storycraft.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class MineSkinAPI {

    public static JsonObject getSessionPlayerProperty(String rawId) throws IOException {
        URL url = new URL("http://api.mineskin.org/generate/user/" + rawId);
        InputStreamReader reader = new InputStreamReader(url.openStream());

        JsonObject rawProperty = new Gson().fromJson(reader, JsonObject.class).getAsJsonObject("data").getAsJsonObject("texture");
        JsonObject textureProperty = new JsonObject();

        textureProperty.addProperty("value", rawProperty.get("value").getAsString());
        textureProperty.addProperty("signature", rawProperty.get("signature").getAsString());

        //String texture = textureProperty.get("value").getAsString();
        //String signature = textureProperty.get("signature").getAsString();

        return textureProperty;
    }

}