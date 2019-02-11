package frutty.tools;

import java.awt.image.*;
import java.io.*;
import java.nio.file.*;
import javax.imageio.*;

public final class IOHelper {
	private IOHelper() {}
	
	public static ObjectOutputStream newObjectOS(String filePath) {
		try {
			return new ObjectOutputStream(Files.newOutputStream(Path.of(filePath), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static BufferedImage loadTexture(String path) {
		try(var inputStream = Files.newInputStream(Path.of(path))){
			return ImageIO.read(inputStream);
		}catch(IOException e){
			e.printStackTrace();
			return Material.missingTexture;
		}
	}
	
	public static ObjectInputStream newObjectIS(String filePath) {
		try {
			return new ObjectInputStream(Files.newInputStream(Path.of(filePath)));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static BufferedImage loadTexture(String prefix, String name) {
		try(var inputStream = Files.newInputStream(Path.of("./textures/" + prefix + '/' + name))){
			return ImageIO.read(inputStream);
		}catch(IOException e){
			System.err.println("Can't find texture: " + prefix + '/' + name + " from class: " + Thread.currentThread().getStackTrace()[2].getClassName());
			e.printStackTrace();
			return null;
		}
	}
}