package util;

import control.Control;

import java.util.function.Consumer;
import java.util.function.Function;

public record Trace<X, Y>(String name,
                          Function<X, Y> f,
                          X x,
                          Consumer<X> consumeX,
                          Consumer<Y> consumeY) {
    public Y doit() {
        boolean flag = Control.beingTraced(name);
        if (flag) {
            System.out.println("before " + this.name);
            consumeX.accept(x);
        }
        Y y = f.apply(x);
        if (flag) {
            System.out.println("after " + this.name);
            consumeY.accept(y);
        }
        return y;
    }
}