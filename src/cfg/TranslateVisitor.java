package cfg;

import java.util.LinkedList;

// Traverse the C AST, and generate
// a control-flow graph.
public class TranslateVisitor implements codegen.C.Visitor
{
  private String classId;
  private cfg.type.T type; // type after translation
  private cfg.operand.T operand;
  private cfg.dec.T dec;
  // A dirty hack. Can hold stm, transfer, or label.
  private java.util.ArrayList<Object> stmOrTransfer;
  private util.Label entry;
  private java.util.LinkedList<cfg.dec.T> newLocals;
  private cfg.method.T method;
  private cfg.classs.T classs;
  private cfg.vtable.T vtable;
  private cfg.mainMethod.T mainMethod;
  public cfg.program.T program;

  public TranslateVisitor()
  {
    this.classId = null;
    this.type = null;
    this.dec = null;
    this.stmOrTransfer = new java.util.ArrayList<Object>();
    this.newLocals = new java.util.LinkedList<cfg.dec.T>();
    this.method = null;
    this.classs = null;
    this.vtable = null;
    this.mainMethod = null;
    this.program = null;
  }

  // /////////////////////////////////////////////////////
  // utility functions
  private java.util.LinkedList<cfg.block.T> cookBlocks()
  {
    java.util.LinkedList<cfg.block.T> blocks
    = new java.util.LinkedList<cfg.block.T>();
    
    int i = 0;
    int size = this.stmOrTransfer.size();
    while (i<size){
      util.Label label;
      cfg.block.Block b;
      java.util.LinkedList<cfg.stm.T> stms
      = new java.util.LinkedList<cfg.stm.T>();
      cfg.transfer.T transfer;
      
      if (!(this.stmOrTransfer.get(i) instanceof util.Label)){
        new util.Bug();
      }
      label = (util.Label)this.stmOrTransfer.get(i++);
      while (i<size && this.stmOrTransfer.get(i) instanceof cfg.stm.T){
        stms.add((cfg.stm.T)this.stmOrTransfer.get(i++));
      }
      transfer = (cfg.transfer.T)this.stmOrTransfer.get(i++);
      b = new cfg.block.Block(label, stms, transfer);
      blocks.add(b);
    }
    this.stmOrTransfer = new java.util.ArrayList<Object>();
    return blocks;
  }
  
  private void emit(Object obj)
  {
    this.stmOrTransfer.add(obj);
  }
  
  private String genVar ()
  {
    String fresh = util.Temp.next();
    cfg.dec.Dec dec = new cfg.dec.Dec(new cfg.type.Int(), fresh);
    this.newLocals.add(dec);
    return fresh;
  }
  
  private String genVar (cfg.type.T ty)
  {
    String fresh = util.Temp.next();
    cfg.dec.Dec dec = new cfg.dec.Dec(ty, fresh);
    this.newLocals.add(dec);
    return fresh;
  }

  // /////////////////////////////////////////////////////
  // expressions
  @Override
  public void visit(codegen.C.Ast.Exp.Add e)
  {
  }

  @Override
  public void visit(codegen.C.Ast.Exp.And e)
  {
  }

  @Override
  public void visit(codegen.C.Ast.Exp.ArraySelect e)
  {
  }

  @Override
  public void visit(codegen.C.Ast.Exp.Call e)
  {
    e.retType.accept(this);
    String dst = genVar (this.type);
    String obj = null;
    e.exp.accept(this);
    cfg.operand.T objOp = this.operand;
    if (objOp instanceof cfg.operand.Var) {
      cfg.operand.Var var = (cfg.operand.Var) objOp;
      obj = var.id;
    } else {
      new util.Bug();
    }

    LinkedList<cfg.operand.T> newArgs = new LinkedList<cfg.operand.T>();
    for (codegen.C.Ast.Exp.T x : e.args) {
      x.accept(this);
      newArgs.add(this.operand);
    }
    emit(new cfg.stm.InvokeVirtual(dst, obj, e.id, newArgs));
    this.operand = new cfg.operand.Var(dst);
    return;
  }

  @Override
  public void visit(codegen.C.Ast.Exp.Id e)
  {
    this.operand = new cfg.operand.Var(e.id);
    return;
  }

  @Override
  public void visit(codegen.C.Ast.Exp.Length e)
  {
  }

  @Override
  public void visit(codegen.C.Ast.Exp.Lt e)
  {
    String dst = genVar();
    e.left.accept(this);
    cfg.operand.T left = this.operand;
    e.right.accept(this);
    emit(new cfg.stm.Lt(dst, null, left, this.operand));
    this.operand = new cfg.operand.Var(dst);
    return;
  }

  @Override
  public void visit(codegen.C.Ast.Exp.NewIntArray e)
  {
  }

  @Override
  public void visit(codegen.C.Ast.Exp.NewObject e)
  {
    String dst = genVar(new cfg.type.Class(e.id));
    emit(new cfg.stm.NewObject(dst, e.id));
    this.operand = new cfg.operand.Var(dst);
    return;
  }

  @Override
  public void visit(codegen.C.Ast.Exp.Not e)
  {
  }

  @Override
  public void visit(codegen.C.Ast.Exp.Num e)
  {
    this.operand = new cfg.operand.Int(e.num);
    return;
  }

  @Override
  public void visit(codegen.C.Ast.Exp.Sub e)
  {
    String dst = genVar ();
    e.left.accept(this);
    cfg.operand.T left = this.operand;
    e.right.accept(this);
    emit(new cfg.stm.Sub(dst, null, left, this.operand));
    this.operand = new cfg.operand.Var(dst);
    return;
  }

  @Override
  public void visit(codegen.C.Ast.Exp.This e)
  {
    this.operand = new cfg.operand.Var("this");
    return;
  }

  @Override
  public void visit(codegen.C.Ast.Exp.Times e)
  {
    String dst = genVar();
    e.left.accept(this);
    cfg.operand.T left = this.operand;
    e.right.accept(this);
    emit(new cfg.stm.Times(dst, null, left, this.operand));
    this.operand = new cfg.operand.Var(dst);
    return;
  }

  // statements
  @Override
  public void visit(codegen.C.Ast.Stm.Assign s)
  {
    s.exp.accept(this);
    emit(new cfg.stm.Move(s.id, null, this.operand));
    return;
  }

  @Override
  public void visit(codegen.C.Ast.Stm.AssignArray s)
  {
  }

  @Override
  public void visit(codegen.C.Ast.Stm.Block s)
  {
  }

  @Override
  public void visit(codegen.C.Ast.Stm.If s)
  {
    util.Label tl = new util.Label(), fl = new util.Label(), el = new util.Label();
    s.condition.accept(this);
    emit(new cfg.transfer.If(this.operand, tl, fl));
    emit(fl);
    s.elsee.accept(this);
    emit(new cfg.transfer.Goto(el));
    emit(tl);
    s.thenn.accept(this);
    emit(new cfg.transfer.Goto(el));
    emit(el);
    return;
  }

  @Override
  public void visit(codegen.C.Ast.Stm.Print s)
  {
    s.exp.accept(this);
    emit (new cfg.stm.Print(this.operand));
    return;
  }

  @Override
  public void visit(codegen.C.Ast.Stm.While s)
  {
  }

  // type
  @Override
  public void visit(codegen.C.Ast.Type.ClassType t)
  {
    this.type = new cfg.type.Class(t.id);
  }

  @Override
  public void visit(codegen.C.Ast.Type.Int t)
  {
    this.type = new cfg.type.Int();
  }

  @Override
  public void visit(codegen.C.Ast.Type.IntArray t)
  {
  }

  // dec
  @Override
  public void visit(codegen.C.Ast.Dec.DecSingle d)
  {
    d.type.accept(this);
    this.dec = new cfg.dec.Dec(this.type, d.id);
    return;
  }

  // vtable
  @Override
  public void visit(codegen.C.Ast.Vtable.VtableSingle v)
  {
    java.util.LinkedList<cfg.Ftuple> newTuples 
    = new java.util.LinkedList<cfg.Ftuple>();
    for (codegen.C.Ftuple t : v.ms) {
      t.ret.accept(this);
      cfg.type.T ret = this.type;
      java.util.LinkedList<cfg.dec.T> args
      = new java.util.LinkedList<>();
      for (codegen.C.Ast.Dec.T dec: t.args){
        dec.accept(this);
        args.add(this.dec);
      }
      newTuples.add(new cfg.Ftuple(t.classs, ret, args, t.id));
    }
    this.vtable = new cfg.vtable.Vtable(v.id, newTuples);
    return;
  }

  // class
  @Override
  public void visit(codegen.C.Ast.Class.ClassSingle c)
  {
    java.util.LinkedList<cfg.Tuple> newTuples = new java.util.LinkedList<cfg.Tuple>();
    for (codegen.C.Tuple t : c.decs) {
      t.type.accept(this);
      newTuples.add(new cfg.Tuple(t.classs, this.type, t.id));
    }
    this.classs = new cfg.classs.Class(c.id, newTuples);
    return;
  }

  // method
  @Override
  public void visit(codegen.C.Ast.Method.MethodSingle m)
  {
    this.newLocals = new java.util.LinkedList<>();

    m.retType.accept(this);
    cfg.type.T retType = this.type;

    java.util.LinkedList<cfg.dec.T> newFormals = new java.util.LinkedList<cfg.dec.T>();
    for (codegen.C.Ast.Dec.T c : m.formals) {
      c.accept(this);
      newFormals.add(this.dec);
    }

    java.util.LinkedList<cfg.dec.T> locals = new java.util.LinkedList<cfg.dec.T>();
    for (codegen.C.Ast.Dec.T c : m.locals) {
      c.accept(this);
      locals.add(this.dec);
    }

    // a junk label
    util.Label entry = new util.Label();
    this.entry = entry;
    emit (entry);
    
    for (codegen.C.Ast.Stm.T s : m.stms)
      s.accept(this);
    
    m.retExp.accept(this);
    emit (new cfg.transfer.Return(this.operand));

    //
    java.util.LinkedList<cfg.block.T> blocks = cookBlocks();
    
    for (cfg.dec.T d: this.newLocals)
      locals.add(d);
    
    this.method = new cfg.method.Method(retType, m.id, m.classId, newFormals,
        locals, blocks, entry, null, null);
    return;
  }

  // main method
  @Override
  public void visit(codegen.C.Ast.MainMethod.MainMethodSingle m)
  {
    this.newLocals = new java.util.LinkedList<>();
    
    java.util.LinkedList<cfg.dec.T> locals = new java.util.LinkedList<cfg.dec.T>();
    for (codegen.C.Ast.Dec.T c : m.locals) {
      c.accept(this);
      locals.add(this.dec);
    }
    
    util.Label entry = new util.Label();
    emit (entry);
    
    m.stm.accept(this);
    
    emit (new cfg.transfer.Return(new cfg.operand.Int(0)));
    
    java.util.LinkedList<cfg.block.T> blocks = cookBlocks();
    for (cfg.dec.T d: this.newLocals)
      locals.add(d);
    this.mainMethod = new cfg.mainMethod.MainMethod(locals, blocks);
    return;
  }

  // program
  @Override
  public void visit(codegen.C.Ast.Program.ProgramSingle p)
  {
    java.util.LinkedList<cfg.classs.T> newClasses = new java.util.LinkedList<cfg.classs.T>();
    for (codegen.C.Ast.Class.T c : p.classes) {
      c.accept(this);
      newClasses.add(this.classs);
    }

    java.util.LinkedList<cfg.vtable.T> newVtable = new java.util.LinkedList<cfg.vtable.T>();
    for (codegen.C.Ast.Vtable.T v : p.vtables) {
      v.accept(this);
      newVtable.add(this.vtable);
    }

    java.util.LinkedList<cfg.method.T> newMethods = new java.util.LinkedList<cfg.method.T>();
    for (codegen.C.Ast.Method.T m : p.methods) {
      m.accept(this);
      newMethods.add(this.method);
    }

    p.mainMethod.accept(this);
    cfg.mainMethod.T newMainMethod = this.mainMethod;

    this.program = new cfg.program.Program(newClasses, newVtable, newMethods,
        newMainMethod);
    return;
  }
}
