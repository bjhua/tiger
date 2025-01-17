package util;

public class Todo extends AssertionError {
    private final String foregroundColor = "\033[31;4m";
    private final String backgroundColor = "\033[0m";

    public Todo() {
        super();
        System.out.println("\n" + foregroundColor + "TODO: please add your code here:\n" + backgroundColor);
    }

    public Todo(Object o) {
        super(o);
        System.out.println("\n" + foregroundColor + "TODO: please add your code here:\n" + backgroundColor);
    }
}
