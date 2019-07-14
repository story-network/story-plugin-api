package com.storycraft.util;

import java.io.IOException;
import java.util.Base64;

public class Base64Util {

    public static byte[] decode(String data) throws IOException {
        return Base64.getDecoder().decode(data.getBytes());
    }

    public static String encode(byte[] data) throws IOException {
        return new String(Base64.getEncoder().encode(data), "UTF-8");
    }

}