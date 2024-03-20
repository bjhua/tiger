package checker;

import ast.Ast.Class;
import ast.Ast.Class.ClassSingle;
import ast.Ast.*;
import ast.Ast.Method.MethodSingle;
import ast.Ast.Program.ProgramSingle;
import util.Todo;

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

    private void error() {
        System.out.println("type mismatch");
        System.exit(1);
    }

    // /////////////////////////////////////////////////////
    // expressions
    public void elabExp(Exp.T e) throws Exception {
        throw new Todo();
    }

    // statements
    public void elabStm(Stm.T s) throws Exception {
        throw new Todo();
    }

    // type
    public void elabType(Type.T t) {
    }


    // dec
    public void elabDec(Dec.T d) {
    }

    // method
    public void elabMethod(Method.MethodSingle m) {
        // construct the method table
        this.methodTable.put(m.formals(), m.locals());
    }

    // class
    public void elabClass(Class.ClassSingle c) {
        this.currentClass = c.id();
    }

    // main class
    public void elabMainClass(MainClass.MainClassSingle c) {
        this.currentClass = c.id();
        // "main" has an argument "arg" of type "String[]", but
        // one has no chance to use it. So it's safe to skip it...

    }

    // ////////////////////////////////////////////////////////
    // step 1: build the class table for Main class
    private void buildMainClass(MainClass.MainClassSingle main) {
        this.classTable.put(main.id(), new ClassBinding(null));
    }

    // class table for normal classes
    private void buildClass(ClassSingle c) {
        this.classTable.put(c.id(), new ClassBinding(c.extendss()));
        for (Dec.T dec : c.decs()) {
            Dec.DecSingle d = (Dec.DecSingle) dec;
            this.classTable.put(c.id(), d.id(), d.type());
        }
        for (Method.T method : c.methods()) {
            MethodSingle m = (MethodSingle) method;
            this.classTable.put(c.id(), m.id(),
                    new MethodType(m.retType(), m.formals()));
        }
    }


    // to check a program
    public void checkProgramSingle(ProgramSingle p) {
        // ////////////////////////////////////////////////
        // step 1: build a symbol table for class (the class table)
        // a class table is a mapping from class names to class bindings
        // classTable: className -> ClassBinding{extends, fields, methods}
        buildMainClass((MainClass.MainClassSingle) p.mainClass());
        for (Class.T c : p.classes()) {
            buildClass((ClassSingle) c);
        }

        // we can double-check that the class table is OK!
        //        if (control.Control.ConAst.elabClassTable) {
        //            this.classTable.dump();
        //        }

        // ////////////////////////////////////////////////
        // step 2: elaborate each class in turn, under the class table
        // built above.

    }
}
