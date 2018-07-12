package frutty.tools;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.function.Supplier;

public final class PropertyFile {
	private final String[] storage;
	private final Path path;
	
	public PropertyFile(String filePath, Supplier<Map<String, Object>> defaultFileKeyValue) {
		path = Paths.get(filePath);
		
		int index = 0;
		String[] tempStorage = new String[20];
		
		try(var reader = Files.newBufferedReader(path)){
			for(String line = reader.readLine(); line != null; line = reader.readLine()) {
				String[] split = line.split("=");
				
				if(index + 2 > tempStorage.length) {
					String[] newTemp = new String[tempStorage.length + 10];
					System.arraycopy(tempStorage, 0, newTemp, 0, index);
					tempStorage = newTemp;
				}
				tempStorage[index++] = split[0];
				tempStorage[index++] = split[1];
			}
		} catch (IOException e) {
			System.err.println("Can't find property file: " + filePath + ", creating default one.");
			Map<String, Object> defFile = defaultFileKeyValue.get();
			
			try(var writer = Files.newBufferedWriter(path)){
				for(var entry : defFile.entrySet()) {
					String key = entry.getKey();
					Object value = entry.getValue();
					
					if(index + 2 > tempStorage.length) {
						String[] newTemp = new String[tempStorage.length + 10];
						System.arraycopy(tempStorage, 0, newTemp, 0, index);
						tempStorage = newTemp;
					}
					
					tempStorage[index++] = key;
					tempStorage[index++] = value.toString();
					writer.write(key + '=' + value + '\n');
				}
			} catch (IOException e1) {}
		}
		
		storage = new String[index];
		System.arraycopy(tempStorage, 0, storage, 0, index);
	}

	public String get(String key){
		String[] pull = storage;
		for(int k = 0; k < pull.length; k += 2) {
			if(pull[k].equals(key)) {
				return pull[k + 1];
			}
		}
		throw new IllegalArgumentException("Can't find key: " + key + " in file: " + path);
	}
	
	public boolean getBoolean(String key){
		String[] pull = storage;
		for(int k = 0; k < pull.length; k += 2) {
			if(pull[k].equals(key)) {
				return Boolean.parseBoolean(pull[k + 1]);
			}
		}
		throw new IllegalArgumentException("Can't find key: " + key + " in file: " + path);
	}
	
	public int getInt(String key){
		String[] pull = storage;
		for(int k = 0; k < pull.length; k += 2) {
			if(pull[k].equals(key)) {
				return Integer.parseInt(pull[k + 1]);
			}
		}
		throw new IllegalArgumentException("Can't find key: " + key + " in file: " + path);
	}
	
	public boolean containsKey(String key) {
		String[] pull = storage;
		for(int k = 0; k < pull.length; k += 2) {
			if(pull[k].equals(key)) {
				return true;
			}
		}
		return false;
	}
	
	public void set(String key, Object value) {
		for(int k = 0; k < storage.length; k += 2) {
			if(storage[k].equals(key)) {
				storage[k + 1] = value.toString();
			}
		}
	}
	
	public void save() {
		try(var output = Files.newBufferedWriter(path)){
			for(int k = 0; k < storage.length; k += 2) {
				output.write(storage[k].toString() + '=' + storage[k + 1].toString() + '\n');
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public String toString() {
		String[] pull = storage;
		StringBuilder builder = new StringBuilder(pull.length * 6).append('[');
		
		int stop = pull.length - 2;
		for(int k = 0; k < pull.length; k += 2) {
			builder.append(pull[k]);
			builder.append('=');
			builder.append(pull[k + 1]);
			
			if(k != stop) {
				builder.append(", ");
			}
		}

		return builder.append(']').toString();
	}
}

/*
public static final PropertyFile settings = new PropertyFile("settings.prop", () -> Map.ofEntries(
entry("difficulty", 0), entry("god", false), entry("fps", 50), entry("graphics", 2),
entry("disableEnemies", false), entry("debugCollisions", false), entry("renderDebugLevel", 0),
entry("upKey", 'W'), entry("downKey", 'S'), entry("leftKey", 'A'), entry("rightKey", 'D'),
entry("mapDebugLeftPanel", false), entry("debugLevelsEnabled", false)));*/