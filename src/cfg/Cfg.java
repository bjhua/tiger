package cfg;

import ast.Ast;
import util.Label;

import java.util.List;

public class Cfg {

    // the pretty printer
    private static int indentLevel = 0;

    private static void indent() {
        indentLevel += 4;
    }

    private static void unIndent() {
        indentLevel -= 4;
    }

    private static void printSpaces() {
        int i = indentLevel;
        while (i-- != 0) {
            say(" ");
        }
    }

    private static <T> void sayln(T s) {
        System.out.println(s);
    }

    private static <T> void say(T s) {
        System.out.print(s);
    }

    //  ///////////////////////////////////////////////////////////
    //  type
    public static class Type {
        public sealed interface T
                permits ClassType, Int, IntArray {
        }

        public record Int() implements T {
        }

        public record ClassType(String id) implements T {
        }

        public record IntArray() implements T {
        }

        public static void pp(T ty) {
            switch (ty) {
                case Int() -> {
                    say("int");
                }
                case ClassType(String id) -> {
                    say(id);
                }
                case IntArray() -> {
                    say("int[]");
                }
            }
        }
    }

    // ///////////////////////////////////////////////////
    // declaration
    public static class Dec {
        public sealed interface T permits Singleton {
        }

        public record Singleton(Type.T type,
                                String id) implements T {
        }

        public static void pp(T dec) {
            switch (dec) {
                case Singleton(Type.T type, String id) -> {
                    Type.pp(type);
                    say(" " + id);
                }
            }
        }

    }

    // /////////////////////////////////////////////////////////
    // virtual function table
    public static class Vtable {
        public sealed interface T permits Singleton {
        }

        public record Entry(Type.T retType,
                            String funcName,
                            List<Dec.T> argTypes) {
        }

        public record Singleton(String name,
                                List<Entry> funcTypes) implements T {
        }

        public static void pp(T vtable) {
            switch (vtable) {
                case Singleton(String name, List<Entry> funcTypes) -> {
                    printSpaces();
                    say("struct V_" + name + " {\n");
                    // all entries
                    indent();
                    for (Entry e : funcTypes) {
                        printSpaces();
                        Type.pp(e.retType);
                        say(" " + e.funcName + "(");
                        for (Dec.T dec : e.argTypes) {
                            Dec.pp(dec);
                            say(", ");
                        }
                        say(");\n");
                    }
                    unIndent();
                    printSpaces();
                    say("};\n");
                }
            }
        }
    }

    // /////////////////////////////////////////////////////////
    // structures
    public static class Struct {
        public sealed interface T permits Singleton {
        }

        public record Singleton(String name,
                                List<Cfg.Dec.T> fields) implements T {
        }

        public static void pp(T s) {
            switch (s) {
                case Singleton(String name, List<Cfg.Dec.T> fields) -> {
                    printSpaces();
                    say("struct S_" + name + " {\n");
                    indent();
                    for (Cfg.Dec.T dec : fields) {
                        printSpaces();
                        Dec.pp(dec);

                    }
                    unIndent();
                    printSpaces();
                    say("};\n");
                }
            }
        }
    }

    // /////////////////////////////////////////////////////////
    // values
    public static class Value {
        public interface T {
        }

        // integer constant
        public record Int(int n) implements T {
        }

        // variable
        public record Id(String x, Ast.Type.T ty) implements T {
        }
    }
    // end of value

    // /////////////////////////////////////////////////////////
    // statement
    public static class Stm {
        public interface T {
        }

        // assign
        public record Assign(String id, Value.T right, Ast.Type.T type) implements T {
        }

        // assign-array
        public record AssignArray(String id, Value.T index, Value.T right) implements T {
        }


        // Print
        public record Print(Value.T value) implements T {
        }
    }
    // end of statement

    // /////////////////////////////////////////////////////////
    // block
    public static class Block {
        public sealed interface T permits Singleton {
        }

        public record Singleton(Label label,
                                List<Stm.T> stms,
                                Transfer.T transfer) implements T {
        }

        public static void pp(T b) {
            say("block todo\n;");
        }
    }

    // /////////////////////////////////////////////////////////
    // transfer
    public static class Transfer {
        public sealed interface T permits Jmp {
        }

        public record Jmp(Block.T target) implements T {
        }
    }

    // /////////////////////////////////////////////////////////
    // function
    public static class Function {
        public sealed interface T permits Singleton {
        }

        public record Singleton(Type.T retType,
                                String id,
                                List<Dec.T> formals,
                                List<Dec.T> locals,
                                List<Block.T> blocks) implements T {
        }

        public static void pp(T f) {
            switch (f) {
                case Singleton(
                        Type.T retType, String id, List<Dec.T> formals, List<Dec.T> locals, List<Block.T> blocks
                ) -> {
                    printSpaces();
                    Type.pp(retType);
                    say(" " + id + "(");
                    for (Dec.T dec : formals) {
                        Dec.pp(dec);
                        say(", ");
                    }
                    say("){\n");
                    indent();
                    for (Dec.T dec : locals) {
                        printSpaces();
                        Dec.pp(dec);
                        sayln(";");
                    }
                    for (Block.T block : blocks) {
                        Block.pp(block);
                    }
                    unIndent();
                    say("}\n");
                }

            }
        }
    }

    // whole program
    public static class Program {
        public sealed interface T permits Singleton {
        }

        public record Singleton(String entryFuncName, // name of the entry function
                                List<Vtable.T> vtables,
                                List<Struct.T> structs,
                                List<Function.T> functions) implements T {
        }

        public static void pp(T prog) {
            switch (prog) {
                case Singleton(
                        String entryFuncName, List<Vtable.T> vtables, List<Struct.T> structs, List<Function.T> functions
                ) -> {
                    printSpaces();
                    sayln("// the entry function name: " + entryFuncName);
                    // vtables
                    for (Vtable.T vtable : vtables) {
                        Vtable.pp(vtable);
                    }
                    // structs
                    for (Struct.T struct : structs) {
                        Struct.pp(struct);
                    }
                    // functions:
                    for (Function.T func : functions) {
                        Function.pp(func);
                    }
                }
            }
        }
    }
}
