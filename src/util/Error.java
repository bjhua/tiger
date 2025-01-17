package util;

public class Error extends AssertionError {
    private final String foregroundColor = "\033[31;4m";
    private final String backgroundColor = "\033[0m";

    public Error() {
        super();
        System.out.println("\n" + this.foregroundColor + "Compiler error" + backgroundColor);
    }

    public Error(Object obj) {
        super(obj);
        System.out.println("\n" + this.foregroundColor + "Compiler error" + backgroundColor);
    }
}
