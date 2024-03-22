package cfg;

import ast.Ast;

import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

public class Translate {
    private Vector<Cfg.Vtable.T> vtables;
    private Vector<Cfg.Struct.T> structs;
    private Vector<Cfg.Function.T> functions;

    public Translate() {
        this.vtables = new Vector<>();
        this.structs = new Vector<>();
        this.functions = new Vector<>();
    }

    public Cfg.Function.T translateMethod(Ast.Method.T method) {
        switch (method) {
            case Ast.Method.Singleton(
                    Ast.Type.T retType,
                    String id,
                    List<Ast.Dec.T> formals,
                    List<Ast.Dec.T> locals,
                    List<Ast.Stm.T> stms,
                    Ast.Exp.T retExp
            ) -> {
                Cfg.Function.T func = new Cfg.Function.T(retType,
                        id,
                        formals,
                        locals,
                        new LinkedList<Cfg.Block.T>());
                return func;
            }
            default -> {
                return null;
            }
        }
    }


    public void prefixing(Ast.Program.T ast,
                          InheritTree.Node root,
                          Vector<Ast.Dec.T> decs,
                          Vector<Cfg.Vtable.Entry> functions) {
        Ast.Class.T cls = Ast.Program.searchClass(ast, root.className);
        assert cls != null;

        // instance variables
        List<Ast.Dec.T> localDecs = ((Ast.Class.Singleton) cls).decs();
        Vector<Ast.Dec.T> newDecs = (Vector<Ast.Dec.T>) decs.clone();
        for (Ast.Dec.T localDec : localDecs) {
            int index = decs.indexOf(localDec);
            if (index == -1) {
                newDecs.add(localDec);
            } else {
                newDecs.set(index, localDec);
            }
        }
        Cfg.Struct.T struct = new Cfg.Struct.T(root.className,
                newDecs);
        this.structs.add(struct);

        // methods
        List<Ast.Method.T> localMethods = ((Ast.Class.Singleton) cls).methods();
        Vector<Cfg.Vtable.Entry> newEntries = (Vector<Cfg.Vtable.Entry>) functions.clone();
        for (Ast.Method.T localMethod : localMethods) {
            Ast.Method.Singleton lm = (Ast.Method.Singleton) localMethod;
            Cfg.Vtable.Entry newEntry = new Cfg.Vtable.Entry(lm.retType(),
                    lm.id(),
                    lm.formals());
            for (int i = 0; i < functions.size(); i++) {
                Cfg.Vtable.Entry ve = functions.get(i);
                // method overriding
                if (lm.id().equals(ve.funcName())) {
                    newEntries.set(i, newEntry);
                }
            }
            newEntries.add(newEntry);
            // translate the method
            Cfg.Function.T newFunc = translateMethod(localMethod);
            this.functions.add(newFunc);
        }
        Cfg.Vtable.T vtable = new Cfg.Vtable.T(root.className, newEntries);
        this.vtables.add(vtable);

        // process childrens
        for (InheritTree.Node child : root.children) {
            prefixing(ast, child, newDecs, newEntries);
        }
    }


    // given an abstract syntax tree, lower it down
    // to a corresponding control-flow graph.
    public Cfg.Program.T translate(Ast.Program.T ast) {
        // build the inheritance tree
        InheritTree tree = new InheritTree();
        tree.addClass("Object");
        // scan all class for the first time,
        // to add all classes
        List<Ast.Class.T> classes = ((Ast.Program.Singleton) ast).classes();
        for (Ast.Class.T c : classes) {
            tree.addClass(((Ast.Class.Singleton) c).id());
        }
        // scan all class for the second time,
        // to add the parent-child
        for (Ast.Class.T c : classes) {
            tree.addChild(((Ast.Class.Singleton) c).extends_(),
                    ((Ast.Class.Singleton) c).id());
        }

        // start from the root, perform prefixing
        prefixing(ast,
                tree.root,
                new Vector<>(),
                new Vector<>());

        Cfg.Program.T cfg = new Cfg.Program.T("tiger_main",
                this.vtables,
                this.structs,
                this.functions);
        return cfg;
    }

}
