package codegen;

import util.Id;
import util.Label;

import java.util.List;
import java.util.stream.Stream;

public class X64 {
    // the pretty printer
    // assembly need tabs, instead of white spaces
    private static int indentLevel = 0;

    private static void indent() {
        indentLevel += 1;
    }

    private static void unIndent() {
        indentLevel -= 1;
    }

    private static void printSpaces() {
        int i = indentLevel;
        while (i-- != 0) {
            say("\t");
        }
    }

    private static <T> void sayln(T s) {
        System.out.println(s);
    }

    private static <T> void say(T s) {
        System.out.print(s);
    }

    //  ///////////////////////////////////////////////////////////
    //  word size and alignment
    public static class WordSize {
        public static int bytesOfWord = 8;
    }

    //  ///////////////////////////////////////////////////////////
    //  physical registers
    public static class Register {
        public static List<Id> allRegs = Stream.of(
                "%rax",
                "%rbx",
                "%rcx",
                "%rdx",
                "%rdi",
                "%rsi",
                "%rbp",
                "%rsp",
                "%r8",
                "%r9",
                "%r10",
                "%r11",
                "%r12",
                "%r13",
                "%r14",
                "%r15").map(Id::newName).toList();

        // the first 6 arguments are passed through the following registers:
        public static List<Id> argPassingRegs = Stream.of(
                "%rdi",
                "%rsi",
                "%rdx",
                "%rcx",
                "%r8",
                "%r9").map(Id::newName).toList();

        // the return value register
        public static Id retReg = Id.newName("%rax");

        // callee-saved regs
        public static List<Id> calleeSavedRegs = Stream.of(
                "%rbx",
                //"%rbp", // we reserve %rbp as the stack base pointer
                "%r12",
                "%r13",
                "%r14",
                "%r15").map(Id::newName).toList();

        // caller-saved regs
        public static List<Id> callerSavedRegs = Stream.of(
                "%rax",
                "%rcx",
                "%rdx",
                "%rdi",
                "%rsi",
                //"%rsp", // we reserve %rsp as the stack top pointer
                "%r8",
                "%r9",
                "%r10",
                "%r11").map(Id::newName).toList();
    }


    //  ///////////////////////////////////////////////////////////
    //  type
    public static class Type {
        public sealed interface T
                permits ClassType, Int, IntArray, CodePtr {
        }

        public record Int() implements T {
        }

        public record ClassType(Id id) implements T {
        }

        public record IntArray() implements T {
        }

        // a pointer to code
        public record CodePtr() implements T {
        }

        public static void pp(T ty) {
            switch (ty) {
                case Int() -> say("int");
                case ClassType(Id id) -> say(id.toString());
                case IntArray() -> say("int[]");
                case CodePtr() -> say("CodePtr");
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
        }

        public static void pp(T dec) {
            switch (dec) {
                case Singleton(
                        Type.T type,
                        Id id
                ) -> {
                    Type.pp(type);
                    say(" " + id.toString());
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

        public record Singleton(Id name,
                                List<String> funcs) implements T {
        }

        public static void pp(T vtable) {
            switch (vtable) {
                case Singleton(
                        Id name,
                        List<String> funcs
                ) -> {
                    printSpaces();
                    sayln(".V_" + name + ":");
                    // all entries
                    indent();
                    for (String s : funcs) {
                        printSpaces();
                        sayln("\t.long long " + s);
                    }
                    unIndent();
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

        public record Singleton(Id classId,
                                List<Dec.T> fields) implements T {
        }

        public static void pp(T s) {
            switch (s) {
                case Singleton(Id classId, List<Dec.T> fields) -> {
                    printSpaces();
                    say("struct S_" + classId.toString() + " {");
                    indent();
                    // the first field is special
                    printSpaces();
                    say("struct V_" + classId.toString() + " *vptr;");
                    for (Dec.T dec : fields) {
                        printSpaces();
                        Dec.pp(dec);
                    }
                    unIndent();
                    printSpaces();
                    say("} S_" + classId + "_ = {");
                    indent();
                    printSpaces();
                    say(".vptr = &V_" + classId + "_;");
                    unIndent();
                    printSpaces();
                    say("};\n\n");
                }
            }
        }
    }

    // /////////////////////////////////////////////////////////
    // a virtual register may be a pseudo- or physical one.
    public static class VirtualReg {
        public sealed interface T
                permits Vid, Reg {
        }

        // variable
        public record Vid(Id id,
                          Type.T ty) implements T {
            @Override
            public String toString() {
                return id.toString();
            }
        }

        // physical register
        public record Reg(Id r,
                          Type.T ty) implements T {
            @Override
            public String toString() {
                return r.toString();
            }
        }

        public static void pp(T ty) {
            switch (ty) {
                case Reg(
                        Id x,
                        Type.T type
                ) -> say(x.toString());
                case Vid(
                        Id x,
                        _
                ) -> say(x.toString());
            }
        }
    }
    // end of virtual register

    // /////////////////////////////////////////////////////////
    // instruction
    public static class Instr {
        public sealed interface T
                permits Singleton {
        }

        // names should be alphabetically ordered
        public enum Kind {
            Bop, CallDirect, CallIndirect, Comment,
            Load, Move, MoveConst, Store
        }

        public record Singleton(
                Kind kind,
                List<VirtualReg.T> uses,
                List<VirtualReg.T> defs,
                java.util.function.Function<Singleton, String> instr) implements T {
        }

        public static void pp(T t) {
            switch (t) {
                case Singleton(
                        Kind _,
                        List<VirtualReg.T> uses,
                        List<VirtualReg.T> defs,
                        java.util.function.Function<Singleton, String> instrFn
                ) -> {
                    printSpaces();
                    say(instrFn.apply((Singleton) t));
                    say("\t// uses=[");
                    uses.forEach((x) -> {
                        VirtualReg.pp(x);
                        say(", ");
                    });
                    say("], defs=[");
                    defs.forEach((x) -> {
                        VirtualReg.pp(x);
                        say(", ");
                    });
                    sayln("]");
                }
            }
        }
    }
    // end of instruction

    // /////////////////////////////////////////////////////////
    // transfer
    public static class Transfer {
        public sealed interface T
                permits If, Jmp, Ret {
        }

        public record If(String instr,
                         Block.T trueBlock,
                         Block.T falseBlock) implements T {
        }

        public record Jmp(Block.T target) implements T {
        }

        public record Ret() implements T {
        }

        public static void pp(T t) {
            switch (t) {
                case If(
                        String instr,
                        Block.T thenn,
                        Block.T elsee
                ) -> {
                    printSpaces();
                    say(instr + " ");
                    sayln(Block.getLabel(thenn).toString());
                    printSpaces();
                    say("jmp ");
                    sayln(Block.getLabel(elsee).toString());
                }
                case Jmp(Block.T target) -> {
                    printSpaces();
                    sayln("jmp " + Block.getLabel(target).toString());
                }
                case Ret() -> {
                    printSpaces();
                    sayln("ret");
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
                                List<Instr.T> instrs,
                                // this is a special hack
                                // the transfer field is final, so that
                                // we use a list instead of a singleton field
                                List<Transfer.T> transfer) implements T {
        }

        public static Label getLabel(Block.T t) {
            switch (t) {
                case Singleton(
                        Label label,
                        _,
                        _
                ) -> {
                    return label;
                }
            }
        }

        public static void pp(T b) {
            switch (b) {
                case Singleton(
                        Label label,
                        List<Instr.T> stms,
                        List<Transfer.T> transfers
                ) -> {
                    printSpaces();
                    sayln(label.toString() + ":");
                    indent();
                    stms.forEach(Instr::pp);
                    Transfer.pp(transfers.getFirst());
                    unIndent();
                }
            }
        }
    }// end of basic block

    // /////////////////////////////////////////////////////////
    // function
    public static class Function {
        public sealed interface T
                permits Singleton {
        }

        public record Singleton(Type.T retType,
                                Id classId,
                                Id methodId,
                                List<Dec.T> formals,
                                List<Dec.T> locals,
                                List<Block.T> blocks) implements T {
        }

        public static Block.T getBlock(T func, Label label) {
            switch (func) {
                case Singleton(
                        Type.T retType,
                        Id classId,
                        Id methodId,
                        List<Dec.T> formals,
                        List<Dec.T> locals,
                        List<Block.T> blocks
                ) -> {
                    return blocks.stream().filter(x ->
                            X64.Block.getLabel(x).equals(label)).toList().getFirst();
                }
            }
        }

        public static void pp(T f) {
            switch (f) {
                case Singleton(
                        Type.T retType,
                        Id classId,
                        Id methodId,
                        List<Dec.T> formals,
                        List<Dec.T> locals,
                        List<Block.T> blocks
                ) -> {
                    printSpaces();
                    Type.pp(retType);
                    say(" " + classId + "_" + methodId + "(");
                    formals.forEach((x) -> {
                        Dec.pp(x);
                        say(", ");
                    });
                    sayln("){");
                    indent();
                    locals.forEach((x) -> {
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
    }// end of function

    // /////////////////////////////////////////////////////////
    // whole program
    public static class Program {
        public sealed interface T
                permits Singleton {
        }

        public record Singleton(Id entryClassName,
                                Id entryFuncName,
                                List<Vtable.T> vtables,
                                List<Struct.T> structs,
                                List<Function.T> functions) implements T {
        }

        public static void pp(T prog) {
            switch (prog) {
                case Singleton(
                        Id entryClassName,
                        Id entryFuncName,
                        List<Vtable.T> vtables,
                        List<Struct.T> structs,
                        List<Function.T> functions
                ) -> {
                    printSpaces();
                    sayln("// x64 assembly generated by the Tiger compiler.");
                    printSpaces();
                    sayln("// the entry function: " + entryClassName + "_" + entryFuncName.toString());
                    // vtables
                    vtables.forEach(Vtable::pp);
                    // structs
                    structs.forEach(Struct::pp);
                    // functions
                    functions.forEach(Function::pp);
                }
            }
        }
    }// end of programs
}
