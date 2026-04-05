package util;

public class StreamConvert {

    public static <X, Y> java.util.stream.Stream<Y> mapAllButLast(java.util.stream.Stream<X> stream,
                                                                  java.util.function.Function<X, Y> mapper) {
        
        return null;
    }

    public static <X> void appAllButLast(java.util.stream.Stream<X> stream,
                                         java.util.function.Consumer<X> all,
                                         java.util.function.Consumer<X> last) {
        var prefixList = stream.toList();
        var prefixStream = prefixList.subList(0, prefixList.size() - 1).stream();
        var theLast = prefixList.getLast();
        prefixStream.forEach(all);
        last.accept(theLast);
    }

}
