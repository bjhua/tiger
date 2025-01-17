package regalloc;

import codegen.X64;
import codegen.X64.*;
import control.Control;
import util.Error;
import util.Id;
import util.Label;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.List;

public class PpAssem {

    static BufferedWriter writer;

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

    private static <T> void sayln(String s) {
        try {
            writer.write(s);
            writer.newLine();
        } catch (Exception _) {
            throw new Error();
        }
    }

    private static <T> void say(String s) {
        try {
            writer.write(s);
        } catch (Exception _) {
            throw new Error();
        }
    }

    // ///////////////////////////////////////////////////
    // declaration
    private void ppDec(X64.Dec.T dec) {
        switch (dec) {
            case X64.Dec.Singleton(
                    X64.Type.T type,
                    Id id
            ) -> {
                // Type.pp(type);
                say(" " + id);
            }
        }
    }

    // /////////////////////////////////////////////////////////
    // virtual function table
    private void ppVtable(X64.Vtable.T vtable) {
        switch (vtable) {
            case X64.Vtable.Singleton(
                    Id name,
                    List<String> funcs
            ) -> {
                printSpaces();
                say(".V_" + name + ":");
                // all entries
                indent();
                funcs.forEach((s) -> {
                    printSpaces();
                    sayln(".quad " + s);
                });
                unIndent();
            }
        }
    }

    // /////////////////////////////////////////////////////////
    // structures

    // /////////////////////////////////////////////////////////
    // virtual regs
    private void ppVirtualReg(X64.VirtualReg.T vt) {
        switch (vt) {
            case X64.VirtualReg.Vid(Id x, _) -> {
                throw new Error(x);
            }
            case X64.VirtualReg.Reg(
                    Id x,
                    X64.Type.T type
            ) -> {
                say(x.toString());
            }
        }
    }
    // end of virtual regs


    // /////////////////////////////////////////////////////////
    // instruction
    private void ppInstr(X64.Instr.T t) {
        switch (t) {
            case X64.Instr.Singleton(
                    Instr.Kind kind,
                    List<VirtualReg.T> uses,
                    List<VirtualReg.T> defs,
                    java.util.function.Function<Instr.Singleton, String> instrFn
            ) -> {
                printSpaces();
                sayln(instrFn.apply((Instr.Singleton) t));
//        say("\t// uses=[");
//        for (VirtualReg.T use : uses) {
//            VirtualReg.pp(use);
//            say(", ");
//        }
//        say("], defs=[");
//        for (VirtualReg.T def : defs) {
//            VirtualReg.pp(def);
//            say(", ");
//        }
//        sayln("]");
            }
        }
    }
    // end of statement

    // /////////////////////////////////////////////////////////
    // transfer
    public void ppTransfer(X64.Transfer.T t) {
        switch (t) {
            case X64.Transfer.If(
                    String instr,
                    X64.Block.T thenn,
                    X64.Block.T elsee
            ) -> {
                printSpaces();
                say(instr + " ");
                sayln(X64.Block.getLabel(thenn).toString());
                printSpaces();
                say("jmp ");
                sayln(X64.Block.getLabel(elsee).toString());
            }
            case X64.Transfer.Jmp(X64.Block.T target) -> {
                printSpaces();
                sayln("jmp " + Block.getLabel(target).toString());
            }
            case X64.Transfer.Ret() -> {
                printSpaces();
                sayln("ret ");
            }
        }
    }

    // /////////////////////////////////////////////////////////
// block
    private void ppBlock(X64.Block.T b) {
        switch (b) {
            case X64.Block.Singleton(
                    Label label,
                    List<X64.Instr.T> instrs,
                    List<X64.Transfer.T> transfers
            ) -> {
                printSpaces();
                say(label.toString() + ":");
                indent();
                instrs.forEach(this::ppInstr);
                ppTransfer(transfers.getFirst());
                unIndent();
            }
        }
    }// end of basic block

    // /////////////////////////////////////////////////////////
// function
    private void ppFunction(X64.Function.T f) {
        switch (f) {
            case X64.Function.Singleton(
                    Type.T retType,
                    Id classId,
                    Id methodId,
                    List<Dec.T> formals,
                    List<Dec.T> locals,
                    List<Block.T> blocks
            ) -> {
                printSpaces();
//                Type.pp(retType);
                sayln("\t.globl " + classId + "_" + methodId);
                printSpaces();
//                Type.pp(retType);
                sayln(classId + "_" + methodId + ":");
//                for (Dec.T dec : formals) {
//                    Dec.pp(dec);
//                    say(", ");
//                }
//                say("){\n");
//                indent();
//                for (Dec.T dec : locals) {
//                    printSpaces();
//                    Dec.pp(dec);
//                    sayln(";");
//                }
                blocks.forEach(this::ppBlock);
//                unIndent();
                printSpaces();
                sayln("\n");
            }
        }
    }

    public void ppProgram(X64.Program.T prog) {
        switch (prog) {
            case X64.Program.Singleton(
                    Id classId,
                    Id entryFuncName,
                    List<X64.Vtable.T> vtables,
                    List<X64.Struct.T> structs,
                    List<X64.Function.T> functions
            ) -> {
                // set up the output stream
                try {
                    writer = new BufferedWriter(new FileWriter(Control.X64.assemFile));
                } catch (Exception _) {
                    throw new Error();
                }
                //
                printSpaces();
                sayln("// x64 assembly generated by the Tiger compiler.");
                printSpaces();
                sayln("// Do NOT modify.");
                // vtables
                printSpaces();
                sayln("\t.data");
                vtables.forEach(this::ppVtable);
                // structs
//                structs.forEach(this::ppStruct);
                printSpaces();
                sayln("\n\n\t.text");
                // functions:
                functions.forEach(this::ppFunction);
                // an entry:
                printSpaces();
                sayln("\t.globl Tiger_main");
                printSpaces();
                sayln("Tiger_main:");
                indent();
                printSpaces();
                sayln("call\t" + classId + "_" + entryFuncName);
                printSpaces();
                sayln("ret");
                unIndent();

                // extra information to turn off GAS assembler warnings
                printSpaces();
                sayln("");
                sayln("\t.ident\t\"Tiger compiler: 0.1\"");
                printSpaces();
                sayln("\t.section\t.note.GNU-stack,\"\",@progbits");
                try {
                    writer.close();
                } catch (Exception _) {
                    throw new Error();
                }
            }
        }
    }
}
