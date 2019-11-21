package frutty.tools;

import java.util.*;

public final class GeneralFunctions {
    private GeneralFunctions() {}
    
    @SafeVarargs
    public static<T> List<T> toMutableList(T... objs){
        var list = new ArrayList<T>(objs.length);
        
        for(T el : objs) {
            list.add(el);
        }
        return list;
    }
    
    public static<T> int indexOf(T value, T[] values) {
        for(int k = 0; k < values.length; ++k) {
            if(values[k].equals(value)) {
                return k;
            }
        }
        throw new IllegalArgumentException("Should not get there...");
    }
}