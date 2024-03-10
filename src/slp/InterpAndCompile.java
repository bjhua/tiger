package slp;

import control.Control;

import java.io.FileWriter;
import java.util.HashSet;
import java.util.List;

import slp.Slp.Exp;
import slp.Slp.Exp.Eseq;
import slp.Slp.Exp.Id;
import slp.Slp.Exp.Num;
import slp.Slp.Exp.Op;
import slp.Slp.Stm;
import util.Bug;
import util.Todo;

// this defines the interpreter and compiler for the SLP language.
public class InterpAndCompile {
    // ///////////////////////////////////////////
    // maximum number of args
    private int maxArgsExp(Exp.T exp) throws Exception {
        throw new Todo();
    }

    private int maxArgsStm(Stm.T stm) throws Exception {
        switch (stm) {
            case Stm.Compound(Stm.T s1, Stm.T s2) -> {
                int n1 = maxArgsStm(s1);
                int n2 = maxArgsStm(s2);
                return Math.max(n1, n2);
            }
            case Stm.Assign(String id, Exp.T e) -> {
                throw new Todo();
            }
            case Stm.Print(List<Exp.T> exps) -> {
                throw new Todo();
            }
            default -> {
                throw new Bug();
            }
        }
    }

    // ////////////////////////////////////////
    // an interpreter
    // interpret an expression
    private void interpExp(Exp.T exp) throws Exception {
        throw new Todo();
    }

    // interpret a statement
    private void interpStm(Stm.T prog) throws Exception {
        switch (prog) {
            case Stm.Compound(Stm.T s1, Stm.T s2) -> {
                throw new Todo();
            }
            case Stm.Assign(String x, Exp.T e) -> {
                throw new Todo();
            }
            case Stm.Print(List<Exp.T> exps) -> {
                throw new Todo();
            }
            default -> {
                throw new Bug();
            }
        }
    }

    // ////////////////////////////////////////
    // a simple compiler for SLP, to x86
    // whether we want to keep the generated assembly file.
    boolean keepAsm = true;
    HashSet<String> ids;
    StringBuffer buf;

    private void emit(String s) {
        buf.append(s);
    }

    private void compileExp(Exp.T exp) throws Exception {
        switch (exp) {
            case Id(String x) -> {
                emit("\tmovl\t" + x + ", %eax\n");
            }
            case Num(int n) -> {
                emit("\tmovl\t$" + n + ", %eax\n");
            }
            case Op(Exp.T l, String op, Exp.T r) -> {
                compileExp(l);
                emit("\tpushl\t%eax\n");
                compileExp(r);
                emit("\tpopl\t%edx\n");
                switch (op) {
                    case "+" -> {
                        emit("\taddl\t%edx, %eax\n");
                    }
                    case "-" -> {
                        emit("\tsubl\t%eax, %edx\n");
                        emit("\tmovl\t%edx, %eax\n");
                    }
                    case "*" -> {
                        emit("\timul\t%edx\n");
                    }
                    case "/" -> {
                        emit("\tmovl\t%eax, %ecx\n");
                        emit("\tmovl\t%edx, %eax\n");
                        emit("\tcltd\n");
                        emit("\tdiv\t%ecx\n");
                    }
                    default -> {
                        throw new Bug();
                    }
                }
            }
            case Eseq(Stm.T s, Exp.T e) -> {
                compileStm(s);
                compileExp(e);
            }
            default -> {
                throw new Bug();
            }
        }
    }

    // to compile a statement
    private void compileStm(Stm.T s) throws Exception {
        switch (s) {
            case Stm.Compound(Stm.T s1, Stm.T s2) -> {
                compileStm(s1);
                compileStm(s2);
            }
            case Stm.Assign(String x, Exp.T e) -> {
                ids.add(x);
                compileExp(e);
                emit("\tmovl\t%eax, " + x + "\n");
            }
            case Stm.Print(List<Exp.T> exps) -> {
                for (Exp.T e : exps) {
                    compileExp(e);
                    emit("\tpushl\t%eax\n");
                    emit("\tpushl\t$slp_format\n");
                    emit("\tcall\tprintf\n");
                    emit("\taddl\t$4, %esp\n");
                }
            }
            default -> {
                throw new Bug();
            }
        }
    }

    // ////////////////////////////////////////
    public void doit(Stm.T prog) throws Exception {
        // calculate the maximum number of arguments
        int numArgs = maxArgsStm(prog);
        System.out.println(numArgs);

        // interpret this program
        interpStm(prog);

        // compile a given SLP program to x86
        ids = new HashSet<String>();
        buf = new StringBuffer();

        compileStm(prog);

        // FileOutputStream out = new FileOutputStream();
        FileWriter writer = new FileWriter("slp_gen.s");
        writer.write(
                "// Automatically generated by the Tiger compiler, do NOT edit.\n\n");
        writer.write("\t.data\n");
        writer.write("slp_format:\n");
        writer.write("\t.string \"%d \"\n");
        writer.write("newline:\n");
        writer.write("\t.string \"\\n\"\n");
        for (String s : this.ids) {
            writer.write(s + ":\n");
            writer.write("\t.int 0\n");
        }
        writer.write("\n\n\t.text\n");
        writer.write("\t.globl main\n");
        writer.write("main:\n");
        writer.write("\tpushl\t%ebp\n");
        writer.write("\tmovl\t%esp, %ebp\n");
        writer.write(buf.toString());
        writer.write("\tleave\n\tret\n\n");
        writer.close();
        String[] cmdStr = {"gcc", "slp_gen.s"};
        Process child = Runtime.getRuntime().exec(cmdStr, null, null);
        child.waitFor();
        if (!keepAsm) {
            String[] cmdStr2 = {"rm", "-rf", "slp_gen.s"};
            Runtime.getRuntime().exec(cmdStr2, null, null);
        }
        // System.out.println(buf.toString());
    }
}
