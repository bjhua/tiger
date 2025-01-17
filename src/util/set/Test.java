package util.set;

public class Test {
    public static void main(String[] args) {
        FunSet<String> set1 = new FunSet<>();
        var set2 = set1.add("a").add("b").add("c").add("d").add("a").add("c");

        System.out.print("\nset1: ");
        set1.toList().forEach((x) -> System.out.print(x + ", "));
        System.out.print("\nset2: ");
        set2.toList().forEach((x) -> System.out.print(x + ", "));

        var set3 = set1.add("a").add("b");
        System.out.print("\nset3: ");
        set3.toList().forEach((x) -> System.out.print(x + ", "));

        var b = set2.isSame(set3);
        System.out.print("\nisSame: " + b);

        var set4 = set2.remove("d").remove("c");
        System.out.print("\nset4: ");
        set4.toList().forEach((x) -> System.out.print(x + ", "));
        b = set3.isSame(set4);
        System.out.print("\nisSame: " + b);
    }
}
