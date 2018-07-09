package frutty.material;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class Material{
	public final String textureName;
	public final boolean breakable;
	
	private Material(String texture, boolean breaks) {
		textureName = texture;
		breakable = breaks;
	}
	
	public static Material loadMaterial(Path path) {
		String texture = null;
		boolean breakable = true;
		
		try {
			String[] all = new String(Files.readAllBytes(path)).split(";");
			for(String line : all) {
				String[] split = line.split(":");
				
				String key = split[0].replace("\"", "").trim();
				String value = split[1].replace("\"", "").trim();
				
				switch(key) {
					case "breakable": breakable = Boolean.parseBoolean(value); break;
					case "texture": texture = value; break;
					default: throw new IllegalArgumentException("Unknown key in material. Key: " + key + ", file: " + path);
				}
			}
			
			return new Material(texture, breakable);
		} catch (IOException e1) {
			e1.printStackTrace();
			throw new IllegalStateException("Unable to load material file: " + path);
		}
	}
	
	@Override
	public String toString() {
		return "Texture: " + textureName + ", breakable: " + breakable;
	}
	
	/*
	public static void main(String[] args) throws IOException {
		Map<Path, Material> materials = Files.walk(Paths.get("./materials/"))
							 				 .filter(Files::isRegularFile)
							 				 .collect(Collectors.toMap(path -> path, Main::parseMaterial));
		
		System.out.println(materials.size());
		
		materials.entrySet().stream().forEach(System.out::println);
	}
	*/
}