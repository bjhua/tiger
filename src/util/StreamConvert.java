package util;

public class StreamConvert {

    public static <X, Y> java.util.stream.Stream<Y> mapAllButLast(java.util.stream.Stream<X> stream,
                                                                  java.util.function.Function<X, Y> mapper) {

        return null;
    }

    public static <X> void appAllExceptLast(java.util.stream.Stream<X> stream,
                                            java.util.function.Consumer<X> all,
                                            java.util.function.Consumer<X> extra) {
        var prefixList = stream.toList();
        var prefixStream = prefixList.subList(0, prefixList.size() - 1).stream();
        var theLast = prefixList.getLast();
        prefixStream.forEach(x -> {
            all.accept(x);
            extra.accept(x);
        });
        all.accept(theLast);
    }

}
