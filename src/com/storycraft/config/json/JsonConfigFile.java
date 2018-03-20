package com.storycraft.config.json;

import com.google.gson.JsonParser;
import com.storycraft.config.IConfigFile;

import java.io.*;

public class JsonConfigFile extends JsonConfigEntry implements IConfigFile {

    @Override
    public void load(InputStream is) {
        setJsonObject(new JsonParser().parse(new InputStreamReader(is)).getAsJsonObject());
    }

    @Override
    public void save(OutputStream os) throws IOException {
        os.write(getJsonObject().toString().getBytes());
    }
}
