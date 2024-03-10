package ast;

import ast.Ast.*;
import ast.Ast.Class.ClassSingle;
import ast.Ast.Exp.*;
import ast.Ast.Method.MethodSingle;
import ast.Ast.Stm.*;
import ast.Ast.Type.Boolean;
import ast.Ast.Type.ClassType;
import ast.Ast.Type.Int;
import ast.Ast.Type.IntArray;
import slp.Main;
import util.Bug;
import util.Todo;

import java.util.List;

public class PrettyPrinter {
    private int indentLevel;

    public PrettyPrinter() { this.indentLevel = 4; }

    private void indent() { this.indentLevel += 4; }

    private void unIndent() { this.indentLevel -= 4; }

    private void printSpaces() {
        int i = this.indentLevel;
        while (i-- != 0)
            this.say(" ");
    }

    private void sayln(String s) { System.out.println(s); }

    private void say(String s) { System.out.print(s); }

    // /////////////////////////////////////////////////////
    // expressions
    public void ppExp(Exp.T e) throws Exception{
        throw new Todo();
    }

    // statement
    public void ppStm(Stm.T s) throws Exception{
        throw new Todo();
    }

    // type
    public void ppType(Type.T t) throws Exception{
        throw new Todo();
    }

    // dec
    public void ppDec(Dec.DecSingle d) {}

    // method
    public void ppMethod(Method.T m) throws Exception{
        switch (m){
            case MethodSingle(Type.T retType, String id,
                              List<Dec.T> formals, List<Dec.T> locals,
                              List<Stm.T> stms, Exp.T retExp) -> {
                this.say("  public ");
                ppType(retType);
                this.say(" " + id + "(");
                for (Dec.T d : formals) {
                    Dec.DecSingle dec = (Dec.DecSingle)d;
                    ppDec(dec);
                    this.say(" " + dec.id() + ", ");
                }
                this.sayln(")");
                this.sayln("  {");

                for (Dec.T d : locals) {
                    Dec.DecSingle dec = (Dec.DecSingle)d;
                    this.say("    ");
                    ppDec(dec);
                    this.say(" " + dec.id() + ";\n");
                }
                this.sayln("");
                for (Stm.T s : stms)
                    ppStm(s);
                this.say("    return ");
                ppExp(retExp);
                this.sayln(";");
                this.sayln("  }");
            }
            default -> {
                throw new Bug();
            }
        }
    }

    // class
    public void ppOneClass(Ast.Class.T c) throws Exception{
        switch (c){
            case Ast.Class.ClassSingle(String id,
                                       String extendss, // null for non-existing "extends"
                                       List<Dec.T> decs, List<ast.Ast.Method.T> methods) -> {
                this.say("class " + id);
                if (extendss != null)
                    this.sayln(" extends " + extendss);
                else
                    this.sayln("");
                this.sayln("{");
                for (Dec.T d : decs) {
                    Dec.DecSingle dec = (Dec.DecSingle)d;
                    this.say("  ");
                    ppDec(dec);
                    this.say(" ");
                    this.sayln(dec.id() + ";");
                }
                for (Method.T mthd : methods)
                    ppMethod(mthd);
                this.sayln("}");
            }
            default -> {
                throw new Bug();
            }
        }
    }

    // main class
    public void ppMainClass(MainClass.T m) throws Exception{
        switch (m) {
            case MainClass.MainClassSingle(String id, String arg, Stm.T stm) -> {
                this.sayln("class " + id);
                this.sayln("{");
                this.sayln("  public static void main (String [] " + arg + ")");
                this.sayln("  {");
                ppStm(stm);
                this.sayln("  }");
                this.sayln("}");
                return;
            }
            default -> {
                throw new Bug();
            }
        }
    }

    // program
    public void ppProgram(Program.T p) throws Exception{
        switch (p){
            case Program.ProgramSingle(MainClass.T m, List<Ast.Class.T> classes)
                    ->{
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
