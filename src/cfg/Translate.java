package cfg;

import ast.Ast;
import util.Label;
import util.Temp;
import util.Todo;

import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

public class Translate {
    private final Vector<Cfg.Vtable.T> vtables;
    private final Vector<Cfg.Struct.T> structs;
    private final Vector<Cfg.Function.T> functions;
    // for code generation purpose
    private String currentClassName = null;
    private Cfg.Function.T currentFunction = null;
    private Cfg.Block.T currentBlock = null;
    private LinkedList<Cfg.Dec.T> newDecs = new LinkedList<>();
    private boolean shouldCloseMethod = true;

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


    private void emit(Cfg.Stm.T s) {
        Cfg.Block.add(this.currentBlock, s);
    }

    private void emitTransfer(Cfg.Transfer.T s) {
        Cfg.Block.addTransfer(this.currentBlock, s);
    }

    private void emitDec(Cfg.Dec.T dec) {
        this.newDecs.add(dec);
    }

    /////////////////////////////
    // translate an expression
    public Cfg.Value.T transExp(Ast.Exp.T exp) throws Exception {
        switch (exp) {
            case Ast.Exp.Id(String id, Ast.Type.T type, boolean isField) -> {
                // for now, this is a fake type
                Cfg.Type.T newType = new Cfg.Type.Int();
                return new Cfg.Value.Id(id, newType);
            }
            case Ast.Exp.Num(int num) -> {
                return new Cfg.Value.Int(num);
            }
            case Ast.Exp.Call(
                    Ast.Exp.T exp1, String id, List<Ast.Exp.T> args,
                    List<Ast.Type.T> calleeType,
                    List<Ast.Type.T> at,
                    List<Ast.Type.T> rt
            ) -> {
                String newVar = Temp.fresh();
                // a fake type
                Cfg.Type.T newRetType = new Cfg.Type.Int();
                emitDec(new Cfg.Dec.Singleton(newRetType, newVar));

                Cfg.Value.T callee = transExp(exp1);

                String newId = Temp.fresh();
                Cfg.Type.T newType = new Cfg.Type.Ptr();
                emitDec(new Cfg.Dec.Singleton(newType, newId));
                emit(new Cfg.Stm.GetMethod(newId, callee, transType(calleeType.get(0)), id));

                LinkedList<Cfg.Value.T> newArgs = new LinkedList<>();
                newArgs.add(callee);
                for (Ast.Exp.T arg : args) {
                    newArgs.add(transExp(arg));
                }

                emit(new Cfg.Stm.AssignCall(newVar, newId, newArgs, newRetType));
                return new Cfg.Value.Id(newVar, newRetType);
            }
            case Ast.Exp.Bop(Ast.Exp.T left, String op, Ast.Exp.T right) -> {
                Cfg.Value.T lvalue = transExp(left);
                Cfg.Value.T rvalue = transExp(right);
                String newVar = Temp.fresh();
                switch (op) {
                    case "+" -> {
                        Cfg.Type.T newType = new Cfg.Type.Int();
                        emitDec(new Cfg.Dec.Singleton(newType, newVar));
                        emit(new Cfg.Stm.AssignBop(newVar, lvalue, "+", rvalue, newType));
                        return new Cfg.Value.Id(newVar, newType);
                    }
                    case "-" -> {
                        Cfg.Type.T newType = new Cfg.Type.Int();
                        emitDec(new Cfg.Dec.Singleton(newType, newVar));
                        emit(new Cfg.Stm.AssignBop(newVar, lvalue, "-", rvalue, newType));
                        return new Cfg.Value.Id(newVar, newType);
                    }
                    case "<" -> {
                        Cfg.Type.T newType = new Cfg.Type.Int();
                        emitDec(new Cfg.Dec.Singleton(newType, newVar));
                        emit(new Cfg.Stm.AssignBop(newVar, lvalue, "<", rvalue, newType));
                        return new Cfg.Value.Id(newVar, newType);
                    }
                    default -> {
                        throw new Todo();
                    }
                }
            }
            case Ast.Exp.This() -> {
                return new Cfg.Value.Id("this", new Cfg.Type.ClassType(this.currentClassName));
            }
            case Ast.Exp.NewObject(String id) -> {
                String newVar = Temp.fresh();
                Cfg.Type.T newType = new Cfg.Type.ClassType(id);
                emitDec(new Cfg.Dec.Singleton(newType, newVar));
                emit(new Cfg.Stm.AssignNew(newVar, id));
                return new Cfg.Value.Id(newVar, newType);
            }
            default -> {
                throw new Todo();
            }
        }
    }

    /////////////////////////////
    // translate a statement
    // this function does not return its result,
    // as the result has been saved into currentBlock
    public void transStm(Ast.Stm.T stm) throws Exception {
        switch (stm) {
            case Ast.Stm.Assign(String id, Ast.Exp.T exp, Ast.Type.T type) -> {
                Cfg.Value.T value = transExp(exp);
                // a fake type, to be corrected
                Cfg.Type.T newType = new Cfg.Type.Int();
                emit(new Cfg.Stm.Assign(id, value, newType));
            }
            case Ast.Stm.If(Ast.Exp.T cond, Ast.Stm.T thenn, Ast.Stm.T elsee) -> {
                Cfg.Value.T value = transExp(cond);
                Cfg.Block.T trueBlock = new Cfg.Block.Singleton(new Label(),
                        new LinkedList<>(),
                        new LinkedList<>());
                Cfg.Block.T falseBlock = new Cfg.Block.Singleton(new Label(),
                        new LinkedList<>(),
                        new LinkedList<>());
                Cfg.Block.T mergeBlock = new Cfg.Block.Singleton(new Label(),
                        new LinkedList<>(),
                        new LinkedList<>());

                // a branching point
                emitTransfer(new Cfg.Transfer.If(value, trueBlock, falseBlock));
                // all jump to the merge block
                Cfg.Block.addTransfer(trueBlock, new Cfg.Transfer.Jmp(mergeBlock));
                Cfg.Block.addTransfer(falseBlock, new Cfg.Transfer.Jmp(mergeBlock));
                this.currentBlock = trueBlock;
                transStm(thenn);
                Cfg.Function.addBlock(currentFunction, trueBlock);
                this.currentBlock = falseBlock;
                transStm(elsee);
                Cfg.Function.addBlock(currentFunction, falseBlock);
                Cfg.Function.addBlock(currentFunction, mergeBlock);
                this.currentBlock = mergeBlock;
            }
            case Ast.Stm.Print(Ast.Exp.T exp) -> {
                Cfg.Value.T value = transExp(exp);
                emit(new Cfg.Stm.Print(value));
            }
            default -> {
                throw new Todo();
            }
        }
    }

    public Cfg.Function.T translateMethod(Ast.Method.T method) throws Exception {
        switch (method) {
            case Ast.Method.Singleton(
                    Ast.Type.T retType,
                    String id,
                    List<Ast.Dec.T> formals,
                    List<Ast.Dec.T> locals,
                    List<Ast.Stm.T> stms,
                    Ast.Exp.T retExp
            ) -> {
                // clear the caches:
                Cfg.Function.T newFunc = new Cfg.Function.Singleton(transType(retType),
                        this.currentClassName + "_" + id,
                        transDecList(formals),
                        transDecList(locals),
                        new LinkedList<Cfg.Block.T>());

                Cfg.Block.T newBlock = new Cfg.Block.Singleton(new Label(),
                        new LinkedList<>(),
                        new LinkedList<>());
                // add the new block into the function
                Cfg.Function.addBlock(newFunc, newBlock);
                // clear the caches:
                this.currentFunction = newFunc;
                this.currentBlock = newBlock;
                this.newDecs = new LinkedList<>();
                for (Ast.Stm.T stmt : stms) {
                    transStm(stmt);
                }

                // translate the "retExp"
                Cfg.Value.T retValue = transExp(retExp);
                emitTransfer(new Cfg.Transfer.Ret(retValue));

                // close the method, if it is non-static:
                if (this.shouldCloseMethod) {
                    Cfg.Dec.T newFormal = new Cfg.Dec.Singleton(new Cfg.Type.ClassType(this.currentClassName), "this");
                    Cfg.Function.addFirstFormal(newFunc, newFormal);
                }
                // add newly generated locals
                Cfg.Function.addDecs(newFunc, this.newDecs);

                return newFunc;
            }
        }
    }

    // the prefixing algorithm
    public void prefixing(InheritTree.Node root,
                          Vector<Cfg.Dec.T> decs,
                          Vector<Cfg.Vtable.Entry> functions) throws Exception {
        Ast.Class.T cls = root.theClass;
        this.currentClassName = Ast.Class.getName(cls);

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
                    this.currentClassName,
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
    public Cfg.Program.T translate(Ast.Program.T ast) throws Exception {
        // build the inheritance tree
        InheritTree.Node root = new InheritTree(ast).buildTree();

        // start from the tree root, perform prefixing
        prefixing(root,
                new Vector<>(),
                new Vector<>());

        // main class is special, it has neither vtable nor class.
        // we create a temporary method, and translate it.
        Ast.MainClass.Singleton mainCls = (Ast.MainClass.Singleton) Ast.Program.getMainClass(ast);
        this.currentClassName = mainCls.id();
        this.shouldCloseMethod = false;
        Ast.Method.T mainMethod = new Ast.Method.Singleton(new Ast.Type.Int(),
                "main",
                new LinkedList<>(),
                new LinkedList<>(),
                List.of(mainCls.stm()),
                new Ast.Exp.Num(0));
        this.functions.add(translateMethod(mainMethod));

        return new Cfg.Program.Singleton(mainCls.id() + "_main",
                this.vtables,
                this.structs,
                this.functions);
    }

}
