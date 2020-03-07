package frutty.tools;

import java.util.*;
import java.util.function.*;

public final class Lazy<T>{
    private Supplier<T> mkFunction;
    private T value;
    
    public Lazy(Supplier<T> fun) {
        mkFunction = Objects.requireNonNull(fun, "Initializer can't be null!");
    }
    
    public T get() {
        if(value == null) {
            value = mkFunction.get();
            mkFunction = null;
        }
        return value;
    }
    
    @Override
    public String toString() {
        return value == null ? "Empty Lazy Value" : "Lazy Value: " + value;
    }
}