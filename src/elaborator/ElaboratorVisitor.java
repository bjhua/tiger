package elaborator;

import ast.Ast.*;
import ast.Ast.Class;
import ast.Ast.Class.ClassSingle;
import ast.Ast.Exp.*;
import ast.Ast.Method.MethodSingle;
import ast.Ast.Program.ProgramSingle;
import ast.Ast.Stm.*;
import ast.Ast.Type.ClassType;
import java.util.LinkedList;

public class ElaboratorVisitor implements ast.Visitor {
    public ClassTable classTable;   // symbol table for class
    public MethodTable methodTable; // symbol table for each method
    public String currentClass;     // the class name being elaborated
    public Type.T type;             // type of the expression being elaborated

    public ElaboratorVisitor() {
        this.classTable = new ClassTable();
        this.methodTable = new MethodTable();
        this.currentClass = null;
        this.type = null;
    }

    private void error() {
        System.out.println("type mismatch");
        System.exit(1);
    }

    @Override
    public void visit(BopBool e) {}

    // /////////////////////////////////////////////////////
    // expressions
    @Override
    public void visit(ArraySelect e) {}

    @Override
    public void visit(Call e) {
        Type.T leftty;
        Type.ClassType ty = null;

        e.exp().accept(this);
        leftty = this.type;
        if (leftty instanceof ClassType) {
            ty = (ClassType)leftty;
            // e.type = ty.id();
        } else
            error();
        MethodType mty = this.classTable.getm(ty.id(), e.id());
        java.util.LinkedList<Type.T> argsty = new LinkedList<Type.T>();
        for (Exp.T a : e.args()) {
            a.accept(this);
            argsty.addLast(this.type);
        }
        if (mty.argsType.size() != argsty.size())
            error();
        for (int i = 0; i < argsty.size(); i++) {
            Dec.DecSingle dec = (Dec.DecSingle)mty.argsType.get(i);
            if (dec.type().toString().equals(argsty.get(i).toString()))
                ;
            else
                error();
        }
        this.type = mty.retType;
        //        e.at() = argsty;
        //        e.rt() = this.type;
        return;
    }

    @Override
    public void visit(False e) {}

    @Override
    public void visit(Id e) {
        // first look up the id in method table
        Type.T type = this.methodTable.get(e.id());
        // if search failed, then s.id must be a class field.
        if (type == null) {
            type = this.classTable.get(this.currentClass, e.id());
            // mark this id as a field id, this fact will be
            // useful in later phase.
            //            e.isField() = true;
        }
        if (type == null)
            error();
        this.type = type;
        // record this type on this node for future use.
        //        e.type() = type;
        return;
    }

    @Override
    public void visit(Length e) {}

    @Override
    public void visit(NewIntArray e) {}

    @Override
    public void visit(NewObject e) {
        this.type = new Type.ClassType(e.id());
        return;
    }

    @Override
    public void visit(Uop e) {}

    @Override
    public void visit(Num e) {
        this.type = new Type.Int();
        return;
    }

    @Override
    public void visit(Bop e) {
        e.left().accept(this);
        Type.T leftty = this.type;
        e.right().accept(this);
        if (!this.type.toString().equals(leftty.toString()))
            error();
        this.type = new Type.Int();
        return;
    }

    @Override
    public void visit(This e) {
        this.type = new Type.ClassType(this.currentClass);
        return;
    }

    @Override
    public void visit(True e) {}

    // statements
    @Override
    public void visit(Assign s) {
        // first look up the id in method table
        Type.T type = this.methodTable.get(s.id());
        // if search failed, then s.id must
        if (type == null)
            type = this.classTable.get(this.currentClass, s.id());
        if (type == null)
            error();
        s.exp().accept(this);
        //        s.type = type;
        this.type.toString().equals(type.toString());
        return;
    }

    @Override
    public void visit(AssignArray s) {}

    @Override
    public void visit(Block s) {}

    @Override
    public void visit(If s) {
        s.cond().accept(this);
        if (!this.type.toString().equals("@boolean"))
            error();
        s.thenn().accept(this);
        s.elsee().accept(this);
        return;
    }

    @Override
    public void visit(Print s) {
        s.exp().accept(this);
        if (!this.type.toString().equals("@int"))
            error();
        return;
    }

    @Override
    public void visit(While s) {}

    // type
    @Override
    public void visit(Type.Boolean t) {}

    @Override
    public void visit(Type.ClassType t) {}

    @Override
    public void visit(Type.Int t) {
        System.out.println("aaaa");
    }

    @Override
    public void visit(Type.IntArray t) {}

    // dec
    @Override
    public void visit(Dec.DecSingle d) {}

    // method
    @Override
    public void visit(Method.MethodSingle m) {
        // construct the method table
        this.methodTable.put(m.formals(), m.locals());

        for (Stm.T s : m.stms())
            s.accept(this);
        m.retExp().accept(this);
        return;
    }

    // class
    @Override
    public void visit(Class.ClassSingle c) {
        this.currentClass = c.id();

        for (Method.T m : c.methods()) {
            m.accept(this);
        }
        return;
    }

    // main class
    @Override
    public void visit(MainClass.MainClassSingle c) {
        this.currentClass = c.id();
        // "main" has an argument "arg" of type "String[]", but
        // one has no chance to use it. So it's safe to skip it...

        c.stm().accept(this);
        return;
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
            Dec.DecSingle d = (Dec.DecSingle)dec;
            this.classTable.put(c.id(), d.id(), d.type());
        }
        for (Method.T method : c.methods()) {
            MethodSingle m = (MethodSingle)method;
            this.classTable.put(c.id(), m.id(),
                                new MethodType(m.retType(), m.formals()));
        }
    }

    // step 1: end
    // ///////////////////////////////////////////////////

    // program
    @Override
    public void visit(ProgramSingle p) {
        // ////////////////////////////////////////////////
        // step 1: build a symbol table for class (the class table)
        // a class table is a mapping from class names to class bindings
        // classTable: className -> ClassBinding{extends, fields, methods}
        buildMainClass((MainClass.MainClassSingle)p.mainClass());
        for (Class.T c : p.classes()) {
            buildClass((ClassSingle)c);
        }

        // we can double-check that the class table is OK!
        //        if (control.Control.ConAst.elabClassTable) {
        //            this.classTable.dump();
        //        }

        // ////////////////////////////////////////////////
        // step 2: elaborate each class in turn, under the class table
        // built above.
        p.mainClass().accept(this);
        for (Class.T c : p.classes()) {
            c.accept(this);
        }
    }
}
