package frutty.stuff;

import java.util.Objects;
import java.util.function.Supplier;

public final class Lazy<T>{
	private final Supplier<T> mkFunction;
	private T value;
	private boolean hasValue;
	
	public Lazy(Supplier<T> fun) {
		Objects.requireNonNull(fun, "Initializer can't be null!");
		mkFunction = fun;
	}
	
	public T get() {
		if(!hasValue) {
			value = mkFunction.get();
			hasValue = true;
		}
		return value;
	}
	
	@Override
	public String toString() {
		return !hasValue ? "Empty Lazy Value" : "Lazy Value: " + value;
	}
}