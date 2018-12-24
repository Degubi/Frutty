package frutty.tools;

import java.util.*;
import java.util.function.*;

public final class Lazy<T>{
	private final Supplier<T> mkFunction;
	private T value;
	
	public Lazy(Supplier<T> fun) {
		mkFunction = Objects.requireNonNull(fun, "Initializer can't be null!");
	}
	
	public T get() {
		return value == null ? value = mkFunction.get() : value;
	}
	
	@Override
	public String toString() {
		return value == null ? "Empty Lazy Value" : "Lazy Value: " + value;
	}
}