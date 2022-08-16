package frutty.tools;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import javax.imageio.*;
import javax.swing.*;

public final class Material implements Serializable {
    public static final LinkedHashMap<String, Material> materialRegistry = new LinkedHashMap<>(4);
    public static final BufferedImage missingTexture = loadTexture("missing.png");

    private static int indexer = 0;

    public static final Material NORMAL = new Material("normal");
    public static final Material STONE = new Material("stone");
    public static final Material DIRT = new Material("dirt");
    public static final Material BRICK = new Material("brick");

    public final int index = indexer++;
    public final String name;
    public final transient BufferedImage texture;
    public final transient Color particleColor;
    public final transient Lazy<ImageIcon> editorTexture, editorUpscaledTexture;

    Material(String texturePath) {
        name = texturePath;
        texture = loadTexture("world/material/" + texturePath + ".png");
        particleColor = new Color(texture.getRGB(2, 2));

        editorTexture = new Lazy<>(() -> new ImageIcon(texture.getScaledInstance(64, 64, Image.SCALE_DEFAULT)));
        editorUpscaledTexture = new Lazy<>(() -> new ImageIcon(texture.getScaledInstance(128, 128, Image.SCALE_DEFAULT)));
        materialRegistry.put(texturePath, this);
    }

    public static String[] getMaterialNames() {
        return materialRegistry.keySet().toArray(String[]::new);
    }

    @SuppressWarnings("boxing")
    @Override
    public int hashCode() {
        return Objects.hash(index, name);
    }

    @Override
    public boolean equals(Object obj) {
        return obj == this || (obj instanceof Material mat && mat.index == index && mat.name.equals(name));
    }

    @Override
    public String toString() {
        return name;
    }

    public static BufferedImage loadTexture(String path) {
        try(var inputStream = Files.newInputStream(Path.of(GamePaths.TEXTURES_DIR + path))){
            return ImageIO.read(inputStream);
        }catch(IOException e){
            System.err.println("Unable to load texture: " + path);
            e.printStackTrace();
            return Material.missingTexture;
        }
    }

    public static BufferedImage[] loadTextures(String prefix, String... names) {
        var textures = new BufferedImage[names.length];

        for(var x = 0; x < names.length; ++x) {
            textures[x] = loadTexture(prefix + '/' + names[x]);
        }

        return textures;
    }
}