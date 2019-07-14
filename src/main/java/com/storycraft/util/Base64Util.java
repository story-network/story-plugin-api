package com.storycraft.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.bukkit.craftbukkit.libs.org.apache.commons.codec.binary.Base64InputStream;
import org.bukkit.craftbukkit.libs.org.apache.commons.codec.binary.Base64OutputStream;

public class Base64Util {

    public static ByteArrayOutputStream decode(String data) throws IOException {
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            try (Base64InputStream inputStream = new Base64InputStream(new ByteArrayInputStream(data.getBytes()))) {
                inputStream.transferTo(output);
            }

            return output;
        }
    }

    public static String encode(ByteArrayInputStream data) throws IOException {
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            try (Base64OutputStream outputStream = new Base64OutputStream(output)) {
                outputStream.write(data.readAllBytes());
            }

            return new String(output.toByteArray());
        }
    }

}