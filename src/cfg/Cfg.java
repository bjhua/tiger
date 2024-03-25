package cfg;

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
                permits ClassType, Int, IntArray, Ptr {
        }

        public record Int() implements T {
        }

        public record ClassType(String id) implements T {
        }

        public record IntArray() implements T {
        }

        public record Ptr() implements T {
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
                case Ptr() -> {
                    say("Ptr");
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
                            String clsName,
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
                    say("} V_" + name + "_ = {\n");
                    indent();
                    for (Entry e : funcTypes) {
                        printSpaces();
                        say("." + e.funcName + " = " + e.clsName + "_" + e.funcName);
                        say(",\n");
                    }
                    unIndent();
                    printSpaces();
                    say("};\n\n");
                }
            }
        }
    }

    // /////////////////////////////////////////////////////////
    // structures
    public static class Struct {
        public sealed interface T permits Singleton {
        }

        public record Singleton(String clsName,
                                List<Cfg.Dec.T> fields) implements T {
        }

        public static void pp(T s) {
            switch (s) {
                case Singleton(String clsName, List<Cfg.Dec.T> fields) -> {
                    printSpaces();
                    say("struct S_" + clsName + " {\n");
                    indent();
                    // the first field is special
                    printSpaces();
                    say("struct V_" + clsName + " *vptr;\n");
                    for (Cfg.Dec.T dec : fields) {
                        printSpaces();
                        Dec.pp(dec);
                    }
                    unIndent();
                    printSpaces();
                    say("} S_" + clsName + "_ = {\n");
                    indent();
                    printSpaces();
                    say(".vptr = " + "&V_" + clsName + "_;\n");
                    unIndent();
                    printSpaces();
                    say("};\n\n");
                }
            }
        }
    }

    // /////////////////////////////////////////////////////////
    // values
    public static class Value {
        public sealed interface T
                permits Int, Id {
        }

        // integer constant
        public record Int(int n) implements T {
        }

        // variable
        public record Id(String x, Type.T ty) implements T {
        }

        public static void pp(T ty) {
            switch (ty) {
                case Int(int n) -> {
                    say(Integer.toString(n));
                }
                case Id(String x, _) -> {
                    say(x);
                }
            }
        }
    }
    // end of value

    // /////////////////////////////////////////////////////////
    // statement
    public static class Stm {
        public sealed interface T
                permits Assign, AssignBop, AssignCall, AssignNew, AssignArray, Print, GetMethod {
        }

        // assign
        public record Assign(String id, Value.T right, Type.T type) implements T {
        }

        // assign
        public record AssignBop(String id, Value.T left, String bop, Value.T right, Type.T type) implements T {
        }

        // assign
        public record AssignCall(String id, String func, List<Value.T> args, Type.T retType) implements T {
        }

        public record AssignNew(String id, String cls) implements T {
        }


        // assign-array
        public record AssignArray(String id, Value.T index, Value.T right) implements T {
        }

        // Print
        public record Print(Value.T value) implements T {
        }

        // get virtual method
        public record GetMethod(String id, Value.T value, Type.T cls, String methodName) implements T {
        }

        public static void pp(T t) {
            switch (t) {
                case Assign(String id, Value.T right, Type.T type) -> {
                    printSpaces();
                    say(id + " = ");
                    Value.pp(right);
                    say(";  @ty:");
                    Type.pp(type);
                    sayln("");
                }
                case AssignBop(String id, Value.T left, String op, Value.T right, Type.T type) -> {
                    printSpaces();
                    say(id + " = ");
                    Value.pp(left);
                    say(" " + op + " ");
                    Value.pp(right);
                    say(";  @ty:");
                    Type.pp(type);
                    sayln("");
                }
                case AssignCall(String id, String func, List<Value.T> args, Type.T retType) -> {
                    printSpaces();
                    say(id + " = " + func + "(");
                    for (Value.T arg : args) {
                        Value.pp(arg);
                        say(", ");
                    }
                    say(");  @ty:");
                    Type.pp(retType);
                    sayln("");
                }
                case AssignNew(String id, String cls) -> {
                    printSpaces();
                    say(id + " = new " + cls + "();\n");
                }
                case Print(Value.T value) -> {
                    printSpaces();
                    say("print(");
                    Value.pp(value);
                    say(");\n");
                }
                case GetMethod(String id, Value.T value, Type.T cls, String methodName) -> {
                    printSpaces();
                    say(id + " = getMethod(");
                    Value.pp(value);
                    say(", \"" + methodName + "\");  @ty:");
                    Type.pp(cls);
                    say("\n");
                }
                default -> {
                    System.out.println("to do\n");
                }
            }
        }
    }
    // end of statement


    // /////////////////////////////////////////////////////////
    // transfer
    public static class Transfer {
        public sealed interface T permits If, Jmp, Ret {
        }

        public record If(Value.T value, Block.T trueBlock, Block.T falseBlock)
                implements T {
        }

        public record Jmp(Block.T target) implements T {
        }

        public record Ret(Value.T retValue) implements T {

        }

        public static void pp(T t) {
            switch (t) {
                case If(Value.T value, Block.T thenn, Block.T elsee) -> {
                    printSpaces();
                    say("if(");
                    Value.pp(value);
                    say(", " + Block.getName(thenn) + ", " + Block.getName(elsee) + ");");
                }
                case Jmp(Block.T target) -> {
                    printSpaces();
                    say("jmp " + Block.getName(target));

                }
                case Ret(Value.T value) -> {
                    printSpaces();
                    say("ret ");
                    Value.pp(value);
                }
            }
        }
    }

    // /////////////////////////////////////////////////////////
    // block
    public static class Block {
        public sealed interface T permits Singleton {
        }

        public record Singleton(Label label,
                                List<Stm.T> stms,
                                // this is a special hack
                                // the transfer field is final, so that
                                // we use a list instead of a singleton field
                                List<Transfer.T> transfer) implements T {
        }

        public static void add(T b, Stm.T s) {
            switch (b) {
                case Singleton(Label label, List<Stm.T> stms, List<Transfer.T> transfer) -> {
                    stms.add(s);
                }
            }
        }

        public static void addTransfer(T b, Transfer.T s) {
            switch (b) {
                case Singleton(Label label, List<Stm.T> stms, List<Transfer.T> transfer) -> {
                    transfer.add(s);
                }
            }
        }

        public static String getName(Block.T t) {
            switch (t) {
                case Singleton(Label label, List<Stm.T> stms, List<Transfer.T> transfer) -> {
                    return label.toString();
                }
            }
        }

        public static void pp(T b) {
            switch (b) {
                case Singleton(Label label, List<Stm.T> stms, List<Transfer.T> transfer) -> {
                    printSpaces();
                    say(label.toString() + ":\n");
                    indent();
                    for (Stm.T s : stms) {
                        Stm.pp(s);
                    }
                    Transfer.pp(transfer.get(0));
                    unIndent();
                    sayln("");
                }
            }
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

        public static void addBlock(T func, Block.T block) {
            switch (func) {
                case Singleton(
                        Type.T retType, String id, List<Dec.T> formals, List<Dec.T> locals, List<Block.T> blocks
                ) -> {
                    blocks.add(block);
                }
            }
        }

        public static void addFirstFormal(T func, Dec.T formal) {
            switch (func) {
                case Singleton(
                        Type.T retType, String id, List<Dec.T> formals, List<Dec.T> locals, List<Block.T> blocks
                ) -> {
                    formals.addFirst(formal);
                }
            }
        }

        public static void addDecs(T func, List<Dec.T> decs) {
            switch (func) {
                case Singleton(
                        Type.T retType, String id, List<Dec.T> formals, List<Dec.T> locals, List<Block.T> blocks
                ) -> {
                    locals.addAll(decs);
                }
            }
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
                    printSpaces();
                    say("}\n\n");
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
