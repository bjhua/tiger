package ast;

import ast.Ast.*;
import util.Bug;
import util.Todo;

import java.util.List;

public class PrettyPrinter {
    private int indentLevel = 4;

    public PrettyPrinter() {
        this.indentLevel = 0;
    }

    private void indent() {
        this.indentLevel += 4;
    }

    private void unIndent() {
        this.indentLevel -= 4;
    }

    private void printSpaces() {
        int i = this.indentLevel;
        while (i-- != 0)
            this.say(" ");
    }

    private <T> void sayln(T s) {
        System.out.println(s);
    }

    private <T> void say(T s) {
        System.out.print(s);
    }

    // /////////////////////////////////////////////////////
    // expressions
    public void ppExp(Exp.T e) throws Exception {
        switch (e) {
            case Exp.Call(
                    Exp.T callee,
                    String id,
                    List<Exp.T> args,
                    List<Type.T> calleeType,
                    List<Type.T> argTypes,
                    List<Type.T> retType
            ) -> {
                ppExp(callee);
                say("." + id + "(");
                for (Exp.T arg : args) {
                    ppExp(arg);
                    say(", ");
                }
                say(")");
            }
            case Exp.NewObject(String id) -> {
                say("new " + id + "()");
            }
            case Exp.Num(int n) -> {
                say(n);
            }
            case Exp.Bop(Exp.T left, String bop, Exp.T right) -> {
                ppExp(left);
                say(" " + bop + " ");
                ppExp(right);
            }
            case Exp.Id(String x, Type.T ty, boolean isField) -> {
                say(x);
            }
            case Exp.This() -> {
                say("this");
            }
            default -> {
                throw new Todo();
            }
        }
    }

    // statement
    public void ppStm(Stm.T s) throws Exception {
        switch (s) {
            case Stm.If(Exp.T cond, Stm.T then_, Stm.T else_) -> {
                printSpaces();
                say("if(");
                ppExp(cond);
                sayln("){");
                indent();
                ppStm(then_);
                unIndent();
                printSpaces();
                sayln("}else{");
                indent();
                ppStm(else_);
                unIndent();
                printSpaces();
                sayln("}");
            }
            case Stm.Print(Exp.T exp) -> {
                printSpaces();
                say("System.out.println(");
                ppExp(exp);
                sayln(");");
            }
            case Stm.Assign(String x, Exp.T exp, Type.T ty) -> {
                printSpaces();
                say(x + " = ");
                ppExp(exp);
                sayln(";");
            }
            default -> {
                throw new Todo();
            }
        }
    }

    // type
    public void ppType(Type.T t) throws Exception {
        switch (t) {
            case Type.Int() -> {
                say("int");
            }
            default -> {
                throw new Todo();
            }
        }
    }

    // dec
    public void ppDec(Dec.T dec) throws Exception {
        Dec.Singleton d = (Dec.Singleton) dec;
        ppType(d.type());
        say(" ");
        say(d.id());
    }

    // method
    public void ppMethod(Method.T mtd) throws Exception {
        Method.Singleton m = (Method.Singleton) mtd;
        printSpaces();
        this.say("public ");
        ppType(m.retType());
        this.say(" " + m.id() + "(");
        for (Dec.T d : m.formals()) {
            ppDec(d);
            say(", ");
        }
        this.sayln("){");
        indent();
        for (Dec.T d : m.locals()) {
            printSpaces();
            ppDec(d);
            this.sayln(";");
        }
        this.sayln("");
        for (Stm.T s : m.stms())
            ppStm(s);
        printSpaces();
        this.say("return ");
        ppExp(m.retExp());
        this.sayln(";");
        unIndent();
        printSpaces();
        this.sayln("}");
    }

    // class
    public void ppOneClass(Ast.Class.T cls) throws Exception {
        Ast.Class.Singleton c = (Ast.Class.Singleton) cls;
        this.say("class " + c.id());
        if (c.extends_() != null)
            this.say(" extends " + c.extends_());
        else
            this.say("");
        this.sayln("{");
        indent();
        for (Dec.T d : c.decs()) {
            ppDec(d);
        }
        for (Method.T mthd : c.methods())
            ppMethod(mthd);
        this.sayln("}");
        unIndent();
    }

    // main class
    public void ppMainClass(MainClass.T m) throws Exception {
        MainClass.Singleton mc = (MainClass.Singleton) m;
        this.sayln("class " + mc.id() + "{");
        this.sayln("\tpublic static void main(String [] " + mc.arg() + "){");
        indent();
        indent();
        ppStm(mc.stm());
        unIndent();
        unIndent();
        this.sayln("\t}");
        this.sayln("}");
        return;
    }

    // program
    public void ppProgram(Program.T prog) throws Exception {
        Program.Singleton p = (Program.Singleton) prog;
        ppMainClass(p.mainClass());
        this.sayln("");
        for (Ast.Class.T cls : p.classes()) {
            ppOneClass(cls);
        }
        System.out.println("\n\n");
    }
}

