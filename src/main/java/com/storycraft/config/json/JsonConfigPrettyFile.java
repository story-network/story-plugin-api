package com.storycraft.config.json;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JsonConfigPrettyFile extends JsonConfigFile {

    @Override
    public void save(OutputStream os) throws IOException {
        try {
            Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();

            os.write(gson.toJson(getJsonObject()).getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}