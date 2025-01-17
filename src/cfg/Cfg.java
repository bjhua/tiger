package cfg;

import util.Dot;
import util.Id;
import util.Label;
import util.Todo;

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
                permits ClassType, CodePtr, Int, IntArray {
        }

        public record ClassType(Id id) implements T {
        }

        public record CodePtr() implements T {
        }

        public record Int() implements T {
        }

        public record IntArray() implements T {
        }

        public static void pp(T ty) {
            switch (ty) {
                case ClassType(Id id) -> say(id.toString());
                case CodePtr() -> say("CodePtr");
                case Int() -> say("int");
                case IntArray() -> say("int[]");
            }
        }
    }

    // ///////////////////////////////////////////////////
    // declaration
    public static class Dec {
        public sealed interface T
                permits Singleton {
        }

        public record Singleton(Type.T type,
                                Id id) implements T {
            // only compare "id"
            @Override
            public boolean equals(Object o) {
                if (o == null)
                    return false;
                if (!(o instanceof Singleton))
                    return false;
                return this.id().equals(((Dec.Singleton) o).id());
            }

            @Override
            public int hashCode() {
                return this.id().hashCode();
            }
        }

        public static void pp(T dec) {
            switch (dec) {
                case Singleton(Type.T type, Id id) -> {
                    Type.pp(type);
                    say(" " + id);
                }
            }
        }
    }

    // /////////////////////////////////////////////////////////
    // virtual function table
    public static class Vtable {
        public sealed interface T
                permits Singleton {
        }

        public record Entry(Type.T retType,
                            Id classId,
                            Id functionId,
                            List<Dec.T> argTypes) {
        }

        public record Singleton(Id name,
                                List<Entry> funcTypes) implements T {
        }

        public static void pp(T vtable) {
            switch (vtable) {
                case Singleton(Id name, List<Entry> funcTypes) -> {
                    printSpaces();
                    sayln("struct V_" + name + " {");
                    // all entries
                    indent();
                    for (Entry e : funcTypes) {
                        printSpaces();
                        Type.pp(e.retType);
                        say(" " + e.functionId + "(");
                        for (Dec.T dec : e.argTypes) {
                            Dec.pp(dec);
                            say(", ");
                        }
                        sayln(");");
                    }
                    unIndent();
                    printSpaces();
                    sayln("} V_" + name + "_ = {");
                    indent();
                    for (Entry e : funcTypes) {
                        printSpaces();
                        say("." + e.functionId + " = " + e.classId + "_" + e.functionId);
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
        public sealed interface T
                permits Singleton {
        }

        public record Singleton(Id className,
                                List<Cfg.Dec.T> fields) implements T {
        }

        public static void pp(T s) {
            switch (s) {
                case Singleton(
                        Id clsName,
                        List<Cfg.Dec.T> fields
                ) -> {
                    printSpaces();
                    sayln("struct S_" + clsName.toString() + " {");
                    indent();
                    // the first field is special
                    printSpaces();
                    sayln("struct V_" + clsName + " *vptr;");
                    for (Cfg.Dec.T dec : fields) {
                        printSpaces();
                        Dec.pp(dec);
                    }
                    unIndent();
                    printSpaces();
                    sayln("} S_" + clsName + "_ = {");
                    indent();
                    printSpaces();
                    sayln(".vptr = &V_" + clsName + "_;");
                    unIndent();
                    printSpaces();
                    say("};\n\n");
                }
            }
        }
    }

    // /////////////////////////////////////////////////////////
    // expression
    public static class Exp {
        public sealed interface T
                permits Bop, Call, Eid, GetMethod, Int, New, Print {
        }

        public record Bop(String op,
                          List<Id> operands,
                          Type.T type) implements T {
        }

        public record Call(Id func,
                           List<Id> operands,
                           Type.T retType) implements T {
        }

        public record Eid(Id x,
                          Type.T type) implements T {
        }

        // get virtual method:
        // getMethod(objId, classId, methodId)
        public record GetMethod(Id objId,
                                Id classId,
                                Id methodId) implements T {
        }

        public record Int(int n) implements T {
        }

        public record New(Id classId) implements T {
        }

        public record Print(Id x) implements T {
        }

        public static void pp(Exp.T t) {
            switch (t) {
                case Bop(String op, List<Id> operands, Type.T type) -> {
                    say(op + "(");
                    operands.forEach((e) -> {
                        say(e.toString() + ", ");
                    });
                    say(")  @ty:");
                    Type.pp(type);
                }
                case Call(Id func, List<Id> args, Type.T retType) -> {
                    say(func.toString() + "(");
                    args.forEach((e) -> {
                        say(e.toString() + ", ");
                    });
                    say(")  @retType:");
                    Type.pp(retType);
                }
                case Eid(Id id, Type.T type) -> say(id);
                case GetMethod(Id objId, Id classId, Id methodId) ->
                        say("getMethod(" + objId.toString() + ", " + classId.toString() + ", " + methodId.toString());
                case Int(int n) -> say(n);
                case New(Id classId) -> say("new " + classId.toString() + "()");
                case Print(Id x) -> say("print(" + x.toString() + ")");
                default -> throw new Todo(t);
            }
        }
    }
    // end of expression

    // /////////////////////////////////////////////////////////
    // statement
    public static class Stm {
        public sealed interface T
                permits Assign {
        }

        // assign
        // "x" should not be "null", even if the exp is not used.
        public record Assign(Id x,
                             Exp.T exp) implements T {
        }

        static void pp(Stm.T t) {
            switch (t) {
                case Assign(Id x, Exp.T exp) -> {
                    printSpaces();
                    say(x + " = ");
                    Exp.pp(exp);
                    sayln(";");
                }
            }
        }
    }
    // end of statement

    // /////////////////////////////////////////////////////////
    // transfer
    public static class Transfer {
        public sealed interface T
                permits If, Jmp, Ret {
        }

        public record If(Id x,
                         Block.T trueBlock,
                         Block.T falseBlock) implements T {
        }

        public record Jmp(Block.T target) implements T {
        }

        public record Ret(Id x) implements T {
        }

        public static void dot(Dot d, String from, T t) {
            switch (t) {
                case If(
                        _,
                        Block.T thenn,
                        Block.T elsee
                ) -> {
                    d.insert(from, Block.getLabel(thenn).toString());
                    d.insert(from, Block.getLabel(elsee).toString());
                }
                case Jmp(Block.T target) -> d.insert(from,
                        Block.getLabel(target).toString());
                case Ret(_) -> {
                }
            }
        }

        public static void pp(T t) {
            switch (t) {
                case If(
                        Id x,
                        Block.T thenn,
                        Block.T elsee
                ) -> {
                    printSpaces();
                    say("if(" + x.toString());
                    say(", " + Block.getLabel(thenn).toString() + ", " + Block.getLabel(elsee).toString() + ");");
                }
                case Jmp(Block.T target) -> {
                    printSpaces();
                    say("jmp " + Block.getLabel(target).toString() + ";");

                }
                case Ret(Id x) -> {
                    printSpaces();
                    say("ret " + x.toString() + ";");
                }
            }
        }
    }

    // /////////////////////////////////////////////////////////
    // block
    public static class Block {
        public sealed interface T
                permits Singleton {
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
                case Singleton(
                        _,
                        List<Stm.T> stms,
                        _
                ) -> stms.add(s);
            }
        }

        public static void addTransfer(T b, Transfer.T s) {
            switch (b) {
                case Singleton(
                        Label _,
                        List<Stm.T> _,
                        List<Transfer.T> transfer
                ) -> transfer.add(s);
            }
        }

        public static void dot(Block.T t, Dot d) {
            switch (t) {
                case Singleton(
                        Label label,
                        List<Stm.T> _,
                        List<Transfer.T> trans
                ) -> trans.forEach((tr) -> Transfer.dot(d,
                        label.toString(), tr));
            }
        }

        public static Label getLabel(Block.T t) {
            switch (t) {
                case Singleton(
                        Label label,
                        List<Stm.T> _,
                        List<Transfer.T> _
                ) -> {
                    return label;
                }
            }
        }

        public static void pp(T b) {
            switch (b) {
                case Singleton(
                        Label label,
                        List<Stm.T> stms,
                        List<Transfer.T> transfer
                ) -> {
                    printSpaces();
                    sayln(label.toString() + ":");
                    indent();
                    stms.forEach(Stm::pp);
                    Transfer.pp(transfer.getFirst());
                    unIndent();
                    sayln("");
                }
            }
        }
    }

    // /////////////////////////////////////////////////////////
    // function
    public static class Function {
        public sealed interface T
                permits Singleton {
        }

        public record Singleton(Type.T retType,
                                Id classId,
                                Id functionId,
                                List<Dec.T> formals,
                                List<Dec.T> locals,
                                List<Block.T> blocks) implements T {
        }

        public static void addBlock(T func, Block.T block) {
            switch (func) {
                case Singleton(
                        Type.T _,
                        Id _,
                        Id _,
                        List<Dec.T> _,
                        List<Dec.T> _,
                        List<Block.T> blocks
                ) -> blocks.add(block);
            }
        }

        public static void addFirstFormal(T func, Dec.T formal) {
            switch (func) {
                case Singleton(
                        _,
                        _,
                        _,
                        List<Dec.T> formals,
                        _,
                        _
                ) -> formals.addFirst(formal);
            }
        }

        public static void addDecs(T func, List<Dec.T> decs) {
            switch (func) {
                case Singleton(
                        _,
                        _,
                        _,
                        _,
                        List<Dec.T> locals,
                        _
                ) -> locals.addAll(decs);
            }
        }

        // TODO: lab3, exercise 4.
        public static void dot(T func) {
            switch (func) {
                case Singleton(
                        Type.T retType,
                        Id classId,
                        Id functionId,
                        List<Dec.T> formals,
                        List<Dec.T> locals,
                        List<Block.T> blocks
                ) -> {
                    Dot d = new util.Dot(classId.toString() + "-" + functionId.toString());
                    blocks.forEach((b) -> Block.dot(b, d));
                    d.visualize();
                }
            }
        }

        public static void pp(T f) {
            switch (f) {
                case Singleton(
                        Type.T retType,
                        Id classId,
                        Id id,
                        List<Dec.T> formals,
                        List<Dec.T> locals,
                        List<Block.T> blocks
                ) -> {
                    printSpaces();
                    Type.pp(retType);
                    say(" " + id + "(");
                    formals.forEach(x -> {
                        Dec.pp(x);
                        say(", ");
                    });
                    say("){ @classId: " + classId.toString() + "\n");
                    indent();
                    locals.forEach(x -> {
                        printSpaces();
                        Dec.pp(x);
                        sayln(";");
                    });
                    blocks.forEach(Block::pp);
                    unIndent();
                    printSpaces();
                    say("}\n\n");
                }
            }
        }
    }

    // whole program
    public static class Program {
        public sealed interface T
                permits Singleton {
        }

        public record Singleton(Id mainClassId,
                                Id mainFuncId, // name of the entry function
                                List<Vtable.T> vtables,
                                List<Struct.T> structs,
                                List<Function.T> functions) implements T {
        }

        public static void dot(T prog) {
            switch (prog) {
                case Singleton(
                        Id mainClassId,
                        Id mainFuncId,
                        List<Vtable.T> vtables,
                        List<Struct.T> structs,
                        List<Function.T> functions
                ) -> functions.forEach(Function::dot);
            }
        }

        public static void pp(T prog) {
            switch (prog) {
                case Singleton(
                        Id mainClassId,
                        Id mainFuncId,
                        List<Vtable.T> vtables,
                        List<Struct.T> structs,
                        List<Function.T> functions
                ) -> {
                    printSpaces();
                    sayln("// the entry function: " + mainClassId + ": " + mainFuncId);
                    // vtables
                    vtables.forEach(Vtable::pp);
                    // structs
                    structs.forEach(Struct::pp);
                    // functions:
                    functions.forEach(Function::pp);
                }
            }
        }
    }
}
