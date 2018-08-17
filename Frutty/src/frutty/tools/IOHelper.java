package frutty.tools;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.function.Predicate;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import javax.imageio.ImageIO;

public final class IOHelper {
	private IOHelper() {}
	
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
	
	public static boolean fileExists(String filePath) {
		return Files.exists(Paths.get(filePath));
	}
	
	public static String[] listFiles(String path) {
		try {
			return Files.list(Paths.get(path)).map(Path::getFileName).map(Path::toString).toArray(String[]::new);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static int fileSize(String path) {
		try {
			return (int) Files.size(Paths.get(path));
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	public static ObjectOutputStream newObjectOS(String filePath) {
		try {
			return new ObjectOutputStream(Files.newOutputStream(Paths.get(filePath), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static ObjectInputStream newObjectIS(String filePath) {
		try {
			return new ObjectInputStream(Files.newInputStream(Paths.get(filePath)));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static BufferedWriter newBufferedWriter(String filePath) {
		try {
			return Files.newBufferedWriter(Paths.get(filePath), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static BufferedReader newBufferedReader(String filePath) {
		try {
			return Files.newBufferedReader(Paths.get(filePath));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
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