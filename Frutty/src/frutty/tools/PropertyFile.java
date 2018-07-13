package frutty.tools;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public final class PropertyFile {
	private final ArrayList<Prop> storage = new ArrayList<>();
	private final Path path;
	
	public PropertyFile(String filePath) {
		path = Paths.get(filePath);
		
		try(var reader = Files.newBufferedReader(path)){
			for(String line = reader.readLine(); line != null; line = reader.readLine()) {
				String[] split = line.split(":");
				
				switch(split[0]) {
					case "int": storage.add(new PrimitiveProperty(split[1].split("="))); break;
					case "str": storage.add(new GenericProperty(split[1].split("="))); break;
				}
			}
		} catch (IOException e) {
			try {
				Files.createFile(path);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}
	
	public String getString(String key, String defaultValue) {
		for(Prop prop : storage) {
			if(prop.key.equals(key) && prop instanceof GenericProperty) {
				return ((GenericProperty)prop).value;
			}
		}
		storage.add(new GenericProperty(key, defaultValue));
		save();
		return defaultValue;
	}
	
	public boolean getBoolean(String key, boolean defaultValue){
		for(Prop prop : storage) {
			if(prop.key.equals(key) && prop instanceof PrimitiveProperty) {
				return ((PrimitiveProperty)prop).value == 1;
			}
		}
		storage.add(new PrimitiveProperty(key, defaultValue ? 1 : 0));
		save();
		return defaultValue;
	}
	
	public int getInt(String key, int defaultValue){
		for(Prop prop : storage) {
			if(prop.key.equals(key) && prop instanceof PrimitiveProperty) {
				return ((PrimitiveProperty)prop).value;
			}
		}
		storage.add(new PrimitiveProperty(key, defaultValue));
		save();
		return defaultValue;
	}
	
	public boolean containsKey(String key) {
		for(Prop prop : storage) {
			if(prop.key.equals(key)) {
				return true;
			}
		}
		return false;
	}
	
	public void setInt(String key, int value) {
		for(Prop props : storage) {
			if(props.key.equals(key) && props instanceof PrimitiveProperty) {
				((PrimitiveProperty)props).value = value;
			}
		}
	}
	
	public void setBoolean(String key, boolean value) {
		for(Prop props : storage) {
			if(props.key.equals(key) && props instanceof PrimitiveProperty) {
				((PrimitiveProperty)props).value = value ? 1 : 0;
			}
		}
	}
	
	public void setString(String key, String value) {
		for(Prop props : storage) {
			if(props.key.equals(key) && props instanceof GenericProperty) {
				((GenericProperty)props).value = value;
			}
		}
	}
	
	public void save() {
		try(var output = Files.newBufferedWriter(path)){
			for(Prop props : storage) {
				output.write(props.toString() + '\n');
			}
		} catch (IOException e) {}
	}
	
	protected static abstract class Prop{
		public final String key;
		
		public Prop(String key) {
			this.key = key;
		}
	}
	
	protected static final class PrimitiveProperty extends Prop{
		public int value;
		
		public PrimitiveProperty(String key, int val) {
			super(key);
			value = val;
		}
		
		public PrimitiveProperty(String[] split) {
			super(split[0]);
			value = Integer.parseInt(split[1]);
		}
		
		@Override
		public String toString() {
			return "int:" + key + '=' + value;
		}
	}
	
	protected static final class GenericProperty extends Prop{
		public String value;
		
		public GenericProperty(String key, String val) {
			super(key);
			value = val;
		}
		
		public GenericProperty(String[] split) {
			super(split[0]);
			value = split[1];
		}
		
		@Override
		public String toString() {
			return "str:" + key + '=' + value;
		}
	}
}