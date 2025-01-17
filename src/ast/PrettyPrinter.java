package ast;

import ast.Ast.*;
import util.Id;
import util.Todo;
import util.Tuple;

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
        while (i-- > 0)
            System.out.print(" ");
    }

    private <T> void say(T s) {
        printSpaces();
        System.out.print(s);
    }

    private <T> void sayln(T s) {
        printSpaces();
        System.out.println(s);
    }

    private <T> void sayLocal(T s) {
        System.out.print(s);
    }


    // /////////////////////////////////////////////////////
    // ast id
    public void ppAstId(AstId aid) {
        sayLocal(aid.freshId);
    }

    // /////////////////////////////////////////////////////
    // expressions
    public void ppExp(Exp.T e) {
        switch (e) {
            case Exp.ExpId(AstId aid) -> ppAstId(aid);
            case Exp.Call(
                    Exp.T callee,
                    AstId methodId,
                    List<Exp.T> args,
                    Tuple.One<Id> theObjectType,
                    Tuple.One<Type.T> retType
            ) -> {
                ppExp(callee);
                sayLocal(".");
                ppAstId(methodId);
                sayLocal("(");
                for (Exp.T arg : args) {
                    ppExp(arg);
                    sayLocal(", ");
                }
                sayLocal(")");
            }
            case Exp.NewObject(Id id) -> {
                sayLocal("new " + id.toString() + "()");
            }
            case Exp.Num(int n) -> sayLocal(n);
            case Exp.Bop(
                    Exp.T left,
                    String bop,
                    Exp.T right
            ) -> {
                ppExp(left);
                sayLocal(" " + bop + " ");
                ppExp(right);
            }
            case Exp.This() -> sayLocal("this");
            default -> throw new Todo();
        }
    }

    // statement
    public void ppStm(Stm.T s) {
        switch (s) {
            case Stm.If(
                    Exp.T cond,
                    Stm.T then_,
                    Stm.T else_
            ) -> {
                say("if(");
                ppExp(cond);
                sayLocal("){\n");
                indent();
                ppStm(then_);
                unIndent();
                sayln("}else{");
                indent();
                ppStm(else_);
                unIndent();
                sayln("}");
            }
            case Stm.Print(Exp.T exp) -> {
                say("System.out.println(");
                ppExp(exp);
                sayLocal(");\n");
            }
            case Stm.Assign(
                    AstId aid,
                    Exp.T exp
            ) -> {
                say("");
                ppAstId(aid);
                sayLocal(" = ");
                ppExp(exp);
                sayLocal(";\n");
            }
            default -> throw new Todo();
        }
    }

    // type
    public void ppType(Type.T t) {
        switch (t) {
            case Type.Int() -> sayLocal("int");
            default -> throw new Todo();
        }
    }

    // dec
    public void ppDec(Dec.T dec) {
        Dec.Singleton d = (Dec.Singleton) dec;
        ppType(d.type());
        sayLocal(" ");
        ppAstId(d.aid());
    }

    // method
    public void ppMethod(Method.T mtd) {
        Method.Singleton m = (Method.Singleton) mtd;
        this.say("public ");
        ppType(m.retType());
        this.sayLocal(" ");
        ppAstId(m.methodId());
        this.sayLocal("(");
        m.formals().forEach(x -> {
            ppDec(x);
            sayLocal(", ");
        });
        this.sayLocal("){\n");
        indent();
        m.locals().forEach(x -> {
            this.say("");
            ppDec(x);
            this.sayLocal(";\n");
        });
        this.sayln("");
        m.stms().forEach(this::ppStm);
        this.say("return ");
        ppExp(m.retExp());
        this.sayLocal(";\n");
        unIndent();
        this.sayln("}");
    }

    // class
    public void ppOneClass(Ast.Class.T cls) {
        Ast.Class.Singleton c = (Ast.Class.Singleton) cls;
        this.say("class " + c.classId());
        if (c.extends_() != null) {
            this.sayLocal(" extends " + c.extends_());
        } else {
            this.sayLocal("");
        }
        this.sayLocal("{\n");
        indent();
        c.decs().forEach(this::ppDec);
        c.methods().forEach(this::ppMethod);
        unIndent();
        this.sayln("}");
    }

    // main class
    public void ppMainClass(MainClass.T m) {
        MainClass.Singleton mc = (MainClass.Singleton) m;
        this.sayln("class " + mc.classId() + "{");
        indent();
        this.say("public static void main(String[] ");
        ppAstId(mc.arg());
        sayLocal("){\n");
        indent();
        ppStm(mc.stm());
        unIndent();
        this.sayln("}");
        unIndent();
        this.sayln("}");
    }

    // program
    public void ppProgram(Program.T prog) {
        Program.Singleton p = (Program.Singleton) prog;
        ppMainClass(p.mainClass());
        this.sayln("");
        p.classes().forEach(this::ppOneClass);
        this.sayln("\n");
    }

}

