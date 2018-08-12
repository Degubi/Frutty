package frutty.tools;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Predicate;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import javax.imageio.ImageIO;

public final class IOHelper {
	private IOHelper() {}
	
	/**
	 * Create a directory at the given path
	 * @param path The directory path
	 */
	public static void createDirectory(String path) {
		Path filePath = Paths.get(path);
		
		if(!Files.exists(filePath)) {
			try {
				Files.createDirectory(filePath);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void createFile(String path) {
		Path filePath = Paths.get(path);
		
		if(!Files.exists(filePath)) {
			try {
				Files.createFile(filePath);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static String[] fileNameList(String path, Predicate<String> nameFilter) {
		try(var list = Files.list(Paths.get(path))){
			return list.map(Path::getFileName).map(Path::toString).filter(nameFilter).toArray(String[]::new);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Manifest getManifestFromJar(File jarPath) {
		try(JarFile jar = new JarFile(jarPath)){
			return jar.getManifest();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static int fileCount(String path) {
		try(var list = Files.list(Paths.get(path))){
			return list.mapToInt(asd -> 1).sum();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	/*** Load texture from the given folder and name
	 * @param prefix Folder of the texture
	 * @param name Name of the texture including format
	 * @return The texture object
	 */
	public static BufferedImage loadTexture(String prefix, String name) {
		try(var inputStream = Files.newInputStream(Paths.get("./textures/" + prefix + '/' + name))){
			return ImageIO.read(inputStream);
		}catch(IOException e){
			System.err.println("Can't find texture: " + prefix + '/' + name + " from class: " + Thread.currentThread().getStackTrace()[2].getClassName());
			e.printStackTrace();
			return null;
		}
	}
}