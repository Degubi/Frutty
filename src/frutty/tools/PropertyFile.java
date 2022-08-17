package frutty.tools;

import frutty.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public final class PropertyFile {
    private final ArrayList<Prop> storage;
    private final Path path;

    public PropertyFile(String filePath) {
        System.out.println(Main.ioSystemLabel + "Initializing property file: " + filePath);

        path = Path.of(GamePaths.WORK_DIR + filePath);
        storage = new ArrayList<>();

        try(var reader = Files.newBufferedReader(path)){
            for(var line = reader.readLine(); line != null; line = reader.readLine()) {
                var strBytes = line.getBytes();

                var typeIndex = 0;
                var equalsIndex = 0;
                for(var index = 0; index < strBytes.length; ++index) {
                    if(typeIndex == 0 && strBytes[index] == ':') {
                        typeIndex = index;
                        continue;
                    }else if(strBytes[index] == '='){
                        equalsIndex = index;
                        break;
                    }
                }

                var first = new String(strBytes, typeIndex + 1, equalsIndex - typeIndex - 1);
                var second = new String(strBytes, equalsIndex + 1, strBytes.length - 1 - equalsIndex);

                switch(new String(strBytes, 0, typeIndex)) {
                    case "int": storage.add(new PrimitiveProperty(first, Integer.parseInt(second))); break;
                    case "str": storage.add(new GenericProperty(first, second)); break;
                }
            }
        } catch (IOException e) {
            if(!Files.exists(path)) {
                try {
                    Files.createFile(path);
                } catch (IOException ex) {}
            }
        }
    }

    public String getString(String key, String defaultValue) {
        for(var prop : storage) {
            if(prop.key.equals(key) && prop instanceof GenericProperty) {
                return ((GenericProperty)prop).value;
            }
        }
        storage.add(new GenericProperty(key, defaultValue));
        save();
        return defaultValue;
    }

    public boolean getBoolean(String key, boolean defaultValue){
        for(var prop : storage) {
            if(prop.key.equals(key) && prop instanceof PrimitiveProperty) {
                return ((PrimitiveProperty)prop).value == 1;
            }
        }
        storage.add(new PrimitiveProperty(key, defaultValue ? 1 : 0));
        save();
        return defaultValue;
    }

    public int getInt(String key, int defaultValue){
        for(var prop : storage) {
            if(prop.key.equals(key) && prop instanceof PrimitiveProperty) {
                return ((PrimitiveProperty)prop).value;
            }
        }
        storage.add(new PrimitiveProperty(key, defaultValue));
        save();
        return defaultValue;
    }

    public boolean containsKey(String key) {
        for(var prop : storage) {
            if(prop.key.equals(key)) {
                return true;
            }
        }
        return false;
    }

    public void setInt(String key, int value) {
        for(var props : storage) {
            if(props.key.equals(key) && props instanceof PrimitiveProperty) {
                ((PrimitiveProperty)props).value = value;
            }
        }
    }

    public void setBoolean(String key, boolean value) {
        for(var props : storage) {
            if(props.key.equals(key) && props instanceof PrimitiveProperty) {
                ((PrimitiveProperty)props).value = value ? 1 : 0;
            }
        }
    }

    public void setString(String key, String value) {
        for(var props : storage) {
            if(props.key.equals(key) && props instanceof GenericProperty) {
                ((GenericProperty)props).value = value;
            }
        }
    }

    public void save() {
        try(var output = Files.newBufferedWriter(path)){
            for(var props : storage) {
                output.write(props.toString() + '\n');
            }
        } catch (IOException e) {}
    }

    protected static abstract class Prop {
        public final String key;

        public Prop(String key) {
            this.key = key;
        }
    }

    protected static final class PrimitiveProperty extends Prop {
        public int value;

        public PrimitiveProperty(String key, int val) {
            super(key);
            value = val;
        }

        @Override
        public String toString() {
            return "int:" + key + '=' + value;
        }
    }

    protected static final class GenericProperty extends Prop {
        public String value;

        public GenericProperty(String key, String val) {
            super(key);
            value = val;
        }

        @Override
        public String toString() {
            return "str:" + key + '=' + value;
        }
    }
}