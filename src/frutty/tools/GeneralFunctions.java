package frutty.tools;

import frutty.*;
import java.util.*;

public final class GeneralFunctions {

    private GeneralFunctions() {}
    
    @SafeVarargs
    public static<T> List<T> toMutableList(T... objs){
        var list = new ArrayList<T>(objs.length);
        
        for (var obj : objs) {
            list.add(obj);
        }
        return list;
    }
    
    public static<T> int indexOf(T value, T[] values) {
        for(var k = 0; k < values.length; ++k) {
            if(values[k].equals(value)) {
                return k;
            }
        }
        throw new IllegalArgumentException("Should not get there...");
    }
    
    public static<T> boolean contains(T element, T[] elements) {
        for(var arrrg : elements) {
            if(arrrg.equals(element)) {
                return true;
            }
        }
        
        return false;
    }
    
    public static int indexOfInt(int value, int[] values) {
        for(var k = 0; k < values.length; ++k) {
            if(values[k] == value) {
                return k;
            }
        }
        throw new IllegalArgumentException("Should not get there...");
    }
    
    public static String getWorkdir() {
        return Main.executionDir;
    }
}