package fr.ubx.poo.ubgarden.game.launcher;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class TextMapRepoWithCompression {

    private static final char EOL = 'x';

    private int getHeight(String data) {
        return (int) data.chars().filter(c -> c == EOL).count();
    }

    private int getWidth(String data) {
        int height = getHeight(data);
        return (data.length() - height) / height;
    }

    private String decompress(String compressed) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < compressed.length(); i++) {
            char c = compressed.charAt(i);
            if (Character.isDigit(c)) {
                char previous = compressed.charAt(i - 1);
                int count = Character.getNumericValue(c);
                result.append(String.valueOf(previous).repeat(count - 1));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    public MapLevel loadMapLevel(String data, boolean compressed) {
        if (compressed) {
            data = decompress(data);
        }

        int height = getHeight(data);
        int width = getWidth(data);
        MapLevel mapLevel = new MapLevel(width, height);
        data = data.replace("x", "");

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                char c = data.charAt(y * width + x);
                MapEntity entity = MapEntity.fromCode(c);
                mapLevel.set(x, y, entity);
            }
        }
        return mapLevel;
    }
}
