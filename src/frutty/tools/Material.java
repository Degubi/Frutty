package frutty.tools;

import frutty.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.lang.StackWalker.*;
import java.nio.file.*;
import java.util.*;
import javax.imageio.*;
import javax.swing.*;

public final class Material implements Serializable{
    public static final LinkedHashMap<String, Material> materialRegistry = new LinkedHashMap<>(4);
    public static final BufferedImage missingTexture = loadTexture("./textures/missing.png");

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
        texture = loadTexture("textures/map/" + texturePath + ".png");
        particleColor = new Color(texture.getRGB(2, 2));
        
        editorTexture = new Lazy<>(() -> new ImageIcon(texture.getScaledInstance(64, 64, Image.SCALE_DEFAULT)));
        editorUpscaledTexture = new Lazy<>(() -> new ImageIcon(texture.getScaledInstance(128, 128, Image.SCALE_DEFAULT)));
        materialRegistry.put(texturePath, this);
    }
    
    public static String[] getMaterialNames() {
        return materialRegistry.keySet().toArray(String[]::new);
    }
    
    private static BufferedImage loadTexture(String path) {
        try(var inputStream = Files.newInputStream(Path.of(Main.executionDir + path))){
            return ImageIO.read(inputStream);
        }catch(IOException e){
            e.printStackTrace();
            return Material.missingTexture;
        }
    }
    
    @SuppressWarnings("boxing")
    @Override
    public int hashCode() {
        return Objects.hash(index, name);
    }
    
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Material) {
            var mat = (Material) obj;
            return mat.index == index && mat.name.equals(name);
        }
        return false;
    }
    
    @Override
    public String toString() {
        return name;
    }
    
    public static BufferedImage loadTexture(String prefix, String name) {
        try(var inputStream = Files.newInputStream(Path.of(Main.executionDir + "textures/" + prefix + '/' + name))){
            return ImageIO.read(inputStream);
        }catch(IOException e){
            System.err.println("Can't find texture: " + prefix + '/' + name + " from class: " + StackWalker.getInstance(Option.RETAIN_CLASS_REFERENCE).getCallerClass().getName());
            return null;
        }
    }
    
    public static BufferedImage[] loadTextures(String prefix, String... names) {
        var textures = new BufferedImage[names.length];
        
        for(var x = 0; x < names.length; ++x) {
            textures[x] = loadTexture(prefix, names[x]);
        }
        
        return textures;
    }
}