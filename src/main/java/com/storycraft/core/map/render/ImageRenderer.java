package com.storycraft.core.map.render;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.map.MapPalette;

public class ImageRenderer implements IMapRenderer {

    private List<OffsetArea> dirtyList;

    private BufferedImage image;
    private Color backgroundColor;

    public ImageRenderer(BufferedImage image, Color backgroundColor) {
        this.dirtyList = new ArrayList<>();

        this.image = image;
        dirtyList.add(new OffsetArea(0, 0, 128, 128));
    }

    public Image getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;

        dirtyList.add(new OffsetArea(0, 0, 128, 128));
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    @Override
    public boolean needRender() {
        return !dirtyList.isEmpty();
    }

    @Override
    public Collection<OffsetArea> getDirtyArea() {
        return dirtyList;
    }

    @Override
    public void clearDirtyArea() {
        dirtyList.clear();
    }

    @Override
    public byte[] render(OffsetArea area) {
        BufferedImage result = new BufferedImage(area.getSizeX(), area.getSizeY(), image.getType());

        int offsetX = area.getX();
        int offsetY = area.getY();

        int width = image.getWidth();
        int height = image.getHeight();

        int scaledWidth = 0;
        int scaledHeight = 0;

        if (width >= height) {
            scaledWidth = 128;
            scaledHeight = (128 * height) / width;
        } else {
            scaledWidth =  (128 * width) / height;
            scaledHeight = 128;
        }

        int scaleX = width / scaledWidth;
        int scaleY = height / scaledHeight;
        
        int spaceX = (128 - scaledWidth) / 2;
        int spaceY = (128 - scaledHeight) / 2;

        int imgOffsetX = (offsetX + spaceX) * scaleX;
        int imgOffsetY = (offsetY - spaceY) * scaleY;

        int imgSizeX = area.getSizeX() * scaleX;
        int imgSizeY = area.getSizeY() * scaleY;

        Graphics2D g2d = result.createGraphics();
        g2d.drawImage(image, 0, 0, area.getSizeX(), area.getSizeY(), imgOffsetX, imgOffsetY, imgOffsetX + imgSizeX, imgOffsetY + imgSizeY, getBackgroundColor(), null);
        g2d.dispose();

        byte[] data = new byte[area.getSizeX() * area.getSizeY()];

        for (int y = 0; y < area.getSizeY(); y++) {
            for (int x = 0; x < area.getSizeX(); x++) {
                int color = result.getRGB(x, y);
                data[y * area.getSizeY() + x] = MapPalette.matchColor((color >> 16) & 0xff, (color >> 8) & 0xff, color & 0xff);
            }
        }

        return data;
    }

}