package ast;

import ast.Ast.*;
import ast.Ast.Method.MethodSingle;
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
                    String type,
                    List<Type.T> argTypes,
                    Type.T retType
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
    public void ppDec(Dec.DecSingle d) throws Exception {
        ppType(d.type());
        say(" ");
        say(d.id());
    }

    // method
    public void ppMethod(Method.T m) throws Exception {
        switch (m) {
            case MethodSingle(
                    Type.T retType,
                    String id,
                    List<Dec.T> formals,
                    List<Dec.T> locals,
                    List<Stm.T> stms,
                    Exp.T retExp
            ) -> {
                printSpaces();
                this.say("public ");
                ppType(retType);
                this.say(" " + id + "(");
                for (Dec.T d : formals) {
                    Dec.DecSingle dec = (Dec.DecSingle) d;
                    ppDec(dec);
                    say(", ");
                }
                this.sayln("){");
                indent();
                for (Dec.T d : locals) {
                    Dec.DecSingle dec = (Dec.DecSingle) d;
                    printSpaces();
                    ppDec(dec);
                    this.sayln(";");
                }
                this.sayln("");
                for (Stm.T s : stms)
                    ppStm(s);
                printSpaces();
                this.say("return ");
                ppExp(retExp);
                this.sayln(";");
                unIndent();
                printSpaces();
                this.sayln("}");
            }
            default -> {
                throw new Bug();
            }
        }
    }

    // class
    public void ppOneClass(Ast.Class.T c) throws Exception {
        switch (c) {
            case Ast.Class.ClassSingle(
                    String id,
                    String extendss, // null for non-existing "extends"
                    List<Dec.T> decs,
                    List<ast.Ast.Method.T> methods
            ) -> {
                this.say("class " + id);
                if (extendss != null)
                    this.say(" extends " + extendss);
                else
                    this.say("");
                this.sayln("{");
                indent();
                for (Dec.T d : decs) {
                    Dec.DecSingle dec = (Dec.DecSingle) d;
                    ppDec(dec);
                }
                for (Method.T mthd : methods)
                    ppMethod(mthd);
                this.sayln("}");
                unIndent();
            }
            default -> {
                throw new Bug();
            }
        }
    }

    // main class
    public void ppMainClass(MainClass.T m) throws Exception {
        switch (m) {
            case MainClass.MainClassSingle(String id, String arg, Stm.T stm) -> {
                this.sayln("class " + id + "{");
                this.sayln("\tpublic static void main(String [] " + arg + "){");
                indent();
                indent();
                ppStm(stm);
                unIndent();
                unIndent();
                this.sayln("\t}");
                this.sayln("}");
                return;
            }
            default -> {
                throw new Bug();
            }
        }
    }

    // program
    public void ppProgram(Program.T p) throws Exception {
        switch (p) {
            case Program.ProgramSingle(MainClass.T m, List<Ast.Class.T> classes) -> {
                ppMainClass(m);
                this.sayln("");
                for (Ast.Class.T oneClass : classes) {
                    ppOneClass(oneClass);
                }
                System.out.println("\n\n");
            }
            default -> {
                throw new Bug();
            }
        }
    }
}
