package cfg;

import ast.Ast;

import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

public class Translate {
    private final Vector<Cfg.Vtable.T> vtables;
    private final Vector<Cfg.Struct.T> structs;
    private final Vector<Cfg.Function.T> functions;

    public Translate() {
        this.vtables = new Vector<>();
        this.structs = new Vector<>();
        this.functions = new Vector<>();
    }

    /////////////////////////////
    // translate a type
    public Cfg.Type.T transType(Ast.Type.T ty) {
        switch (ty) {
            case Ast.Type.ClassType(String id) -> {
                return new Cfg.Type.ClassType(id);
            }
            case Ast.Type.Boolean() -> {
                return new Cfg.Type.Int();
            }
            case Ast.Type.IntArray() -> {
                return new Cfg.Type.IntArray();
            }
            case Ast.Type.Int() -> {
                return new Cfg.Type.Int();
            }
        }
    }

    public Cfg.Dec.T transDec(Ast.Dec.T dec) {
        switch (dec) {
            case Ast.Dec.Singleton(Ast.Type.T type, String id) -> {
                return new Cfg.Dec.Singleton(transType(type), id);
            }
        }
    }

    public List<Cfg.Dec.T> transDecList(List<Ast.Dec.T> decs) {
        LinkedList<Cfg.Dec.T> newDecs = new LinkedList<>();
        for (Ast.Dec.T dec : decs) {
            newDecs.add(transDec(dec));
        }
        return newDecs;
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
                return new Cfg.Function.Singleton(transType(retType),
                        id,
                        transDecList(formals),
                        transDecList(locals),
                        new LinkedList<Cfg.Block.T>());
            }
        }
    }


    public void prefixing(InheritTree.Node root,
                          Vector<Cfg.Dec.T> decs,
                          Vector<Cfg.Vtable.Entry> functions) {
        Ast.Class.T cls = root.theClass;

        // instance variables
        List<Ast.Dec.T> localDecs = ((Ast.Class.Singleton) cls).decs();
        Vector<Cfg.Dec.T> newDecs = (Vector<Cfg.Dec.T>) decs.clone();
        for (Ast.Dec.T localDec : localDecs) {
            Cfg.Dec.T newLocalDec = transDec(localDec);
            int index = decs.indexOf(newLocalDec);
            if (index == -1) {
                newDecs.add(newLocalDec);
            } else {
                newDecs.set(index, newLocalDec);
            }
        }

        Cfg.Struct.T struct = new Cfg.Struct.Singleton(Ast.Class.getName(root.theClass),
                newDecs);
        this.structs.add(struct);

        // methods
        List<Ast.Method.T> localMethods = ((Ast.Class.Singleton) cls).methods();
        Vector<Cfg.Vtable.Entry> newEntries = (Vector<Cfg.Vtable.Entry>) functions.clone();
        for (Ast.Method.T localMethod : localMethods) {
            Ast.Method.Singleton lm = (Ast.Method.Singleton) localMethod;
            Cfg.Vtable.Entry newEntry = new Cfg.Vtable.Entry(transType(lm.retType()),
                    lm.id(),
                    transDecList(lm.formals()));
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
        Cfg.Vtable.T vtable = new Cfg.Vtable.Singleton(Ast.Class.getName(root.theClass), newEntries);
        this.vtables.add(vtable);

        // process childrens, recursively
        for (InheritTree.Node child : root.children) {
            prefixing(child, newDecs, newEntries);
        }
    }


    // given an abstract syntax tree, lower it down
    // to a corresponding control-flow graph.
    public Cfg.Program.T translate(Ast.Program.T ast) {
        // build the inheritance tree
        InheritTree.Node root = new InheritTree(ast).buildTree();

        // start from the tree root, perform prefixing
        prefixing(root,
                new Vector<>(),
                new Vector<>());

        // main class is special, it does not have vtable or class...
        Cfg.Function.T main = new Cfg.Function.Singleton(new Cfg.Type.Int(),
                "tiger_main",
                new LinkedList<>(),
                new LinkedList<>(),
                new LinkedList<>());
        this.functions.add(main);

        return new Cfg.Program.Singleton("tiger_main",
                this.vtables,
                this.structs,
                this.functions);
    }

}
