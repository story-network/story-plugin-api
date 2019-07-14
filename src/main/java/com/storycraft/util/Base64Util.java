package com.storycraft.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.bukkit.craftbukkit.libs.org.apache.commons.codec.binary.Base64InputStream;

public class Base64Util {

    public static byte[] decode(String data) throws IOException {
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            try (Base64InputStream inputStream = new Base64InputStream(new ByteArrayInputStream(data.getBytes()))) {
                byte[] byteData = new byte[inputStream.available()];
                
                inputStream.read(byteData);

                return byteData;
            }
        }
    }

    public static String encode(ByteArrayInputStream data) throws IOException {
        try (Base64InputStream inputStream = new Base64InputStream(data, true)) {
            byte[] byteData = new byte[inputStream.available()];
            
            inputStream.read(byteData);

            return new String(byteData);
        }
    }

}