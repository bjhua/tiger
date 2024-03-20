package checker;

import ast.Ast;
import ast.Ast.Class;
import ast.Ast.*;
import util.Bug;
import util.Todo;

import java.util.List;

public class Checker {
    // symbol table for all classes
    public ClassTable classTable;
    // symbol table for each method
    public MethodTable methodTable;
    // the class name being checked
    public String currentClass;

    public Checker() {
        this.classTable = new ClassTable();
        this.methodTable = new MethodTable();
        this.currentClass = null;
    }

    private void error(String s) {
        System.out.println("type mismatch: " + s);
        System.exit(1);
    }

    private void error(String s, Type.T expected, Type.T got) throws Exception {
        System.out.println("type mismatch: " + s);
        Type.output(expected);
        Type.output(got);
        System.exit(1);
    }

    // /////////////////////////////////////////////////////
    // expressions
    // type check an expression will return its type.
    public Type.T checkExp(Exp.T e) throws Exception {
        switch (e) {
            case Exp.Call(
                    Exp.T callee,
                    String id,
                    List<Exp.T> args,
                    String type,
                    List<Type.T> argTypes,
                    Type.T retType
            ) -> {

                return new Type.Int();
            }
            case Exp.NewObject(String id) -> {
                new Type.ClassType(id);
            }
            case Exp.Num(int n) -> {
                return new Type.Int();
            }
            case Exp.Bop(Exp.T left, String bop, Exp.T right) -> {
                Type.T tyLeft = checkExp(left);
                Type.T tyRight = checkExp(right);

                switch (bop) {
                    case "+" -> {
                        if (!Type.equals(tyLeft, new Type.Int()) ||
                                !Type.equals(tyRight, new Type.Int())) {
                            error("+");
                        }
                        return new Type.Int();
                    }
                    case "-" -> {
                        System.out.print("-");
                        throw new Todo();
                    }
                    case "<" -> {
                        if (!Type.equals(tyLeft, new Type.Int()) ||
                                !Type.equals(tyRight, new Type.Int())) {
                            error("<");
                        }
                        return new Type.Boolean();
                    }
                    default -> {
                        throw new Todo();
                    }
                }
            }
            case Exp.Id(String x, Type.T ty, boolean isField) -> {
                ty = null;
                return new Type.Int();
            }
            case Exp.This() -> {
                return new Type.ClassType(this.currentClass);
            }
            default -> {
                throw new Todo();
            }
        }
        throw new Bug();
    }

    // statements
    public void checkStm(Stm.T s) throws Exception {
        switch (s) {
            case Stm.If(Exp.T cond, Stm.T then_, Stm.T else_) -> {
                Type.T tyCond = checkExp(cond);
                if (!Type.equals(tyCond, new Type.Boolean())) {
                    error("if require a boolean type");
                }
                checkStm(then_);
                checkStm(else_);
            }
            case Stm.Print(Exp.T exp) -> {
                Type.T tyExp = checkExp(exp);
                if (!Type.equals(tyExp, new Type.Int())) {
                    error("print requires an integer type");
                }
            }
            case Stm.Assign(String x, Exp.T exp, Type.T ty) -> {
                // first lookup in the method table
                Type.T tyVar = checkExp(new Exp.Id(x, null, false));
                Type.T tyExp = checkExp(exp);
                if (!Type.equals(tyVar, tyExp)) {
                    error("=");
                }
            }
            default -> {
                throw new Todo();
            }
        }
    }

    // type
    public void checkType(Type.T t) {
    }


    // dec
    public void checkDec(Dec.T d) {
    }

    // method
    public void checkMethod(Method.T mtd) throws Exception {
        Method.Singleton m = (Method.Singleton) mtd;
        // construct the method table
        this.methodTable = new MethodTable();
        this.methodTable.put(m.formals(), m.locals());
        for (Stm.T stm : m.stms()) {
            checkStm(stm);
        }
        Type.T realRetType = checkExp(m.retExp());
        if (!Type.equals(realRetType, m.retType())) {
            error("ret type mismatch", m.retType(), realRetType);
        }
    }

    // class
    public void checkClass(Class.T c) throws Exception {
        Class.Singleton cls = (Class.Singleton) c;
        this.currentClass = cls.id();
        for (Method.T mtd : cls.methods()) {
            checkMethod(mtd);
        }
    }

    // main class
    public void checkMainClass(MainClass.T c) throws Exception {
        MainClass.Singleton mainClass = (MainClass.Singleton) c;
        this.currentClass = mainClass.id();
        // "main" method has an argument "arg" of type "String[]", but
        // MiniJava programs do not use it. So it is safe to skip it.
        checkStm(mainClass.stm());
    }

    // ////////////////////////////////////////////////////////
    // step 1: create class table for Main class
    private void buildMainClass(MainClass.T main) {
        MainClass.Singleton mc = (MainClass.Singleton) main;
        this.classTable.put(mc.id(), new ClassBinding(null));
    }

    // create class table for normal classes
    private void buildClass(Class.T cls) {
        Class.Singleton c = (Class.Singleton) cls;
        this.classTable.put(c.id(),
                new ClassBinding(c.extends_()));
        // add all instance variables into the class table
        for (Dec.T dec : c.decs()) {
            Dec.Singleton d = (Dec.Singleton) dec;
            this.classTable.put(c.id(), d.id(), d.type());
        }
        // add all methods into the class table
        for (Method.T method : c.methods()) {
            Method.Singleton m = (Method.Singleton) method;
            this.classTable.put(c.id(),
                    m.id(),
                    // for now, do not worry to check
                    // method formals, as we will check
                    // this during method table construction.
                    new MethodType(m.retType(),
                            m.formals()));
        }
    }


    // to check a program
    public void checkProgram(Program.T p) throws Exception {
        // "p" is singleton
        Program.Singleton prog = (Program.Singleton) p;
        // ////////////////////////////////////////////////
        // step 1: build the class table
        // a class table maps a class name to a class binding, that is:
        // classTable: className -> ClassBinding{extends_, fields, methods}
        buildMainClass(prog.mainClass());
        for (Class.T c : prog.classes()) {
            buildClass(c);
        }

        // we can double-check that the class table is OK!
        //        if (control.Control.ConAst.elabClassTable) {
        //            this.classTable.dump();
        //        }

        // ////////////////////////////////////////////////
        // step 2: elaborate each class in turn, under the class table
        // built above.
        checkMainClass(prog.mainClass());
        for (Class.T c : prog.classes()) {
            checkClass(c);
        }

    }
}


