package util;

import cfg.Cfg;

import java.util.function.Function;

public class Maybe {
    public sealed interface T<X> permits Some, None {
    }

    public record Some<X>(X a) implements T<X> {
//        X data;
    }

    public record None<X>() implements T<X> {
    }

    // factory methods
    public static <X> Maybe.T<X> some(X a) {
        return new Some<>(a);
    }

    public static <X> Maybe.T<X> none() {
        return new None<X>();
    }

    public static <X, Y> T<Y> map(T<X> x, Function<X, Y> mapper) {
        return switch (x) {
            case Some<X>(X a) -> new Some<>(mapper.apply(a));
            case None() -> new None<Y>();
        };
    }

    public static <X> X getOrDefault(T<X> x, X defaultValue) {
        return switch (x) {
            case Some<X>(X a) -> a;
            case None() -> defaultValue;
        };
    }
}
