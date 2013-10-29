package codegen.dalvik;

import java.util.LinkedList;

import util.Label;
import util.Temp;

// Given a Java AST, translate it into Dalvik bytecode.

public class TranslateVisitor implements ast.Visitor
{
  private String classId;
  private codegen.dalvik.type.T type; // type after translation
  private codegen.dalvik.dec.T dec;
  // these two fields are expression-related: after
  // translating an expression, we get the expression's
  // type "etype" and the expression name "evar";
  private codegen.dalvik.type.T etype;
  private String evar;
  private java.util.LinkedList<codegen.dalvik.dec.T> tmpVars;
  private LinkedList<codegen.dalvik.stm.T> stms;
  private codegen.dalvik.method.T method;
  private codegen.dalvik.classs.T classs;
  private codegen.dalvik.mainClass.T mainClass;
  public codegen.dalvik.program.T program;

  public TranslateVisitor()
  {
    this.classId = null;
    this.type = null;
    this.dec = null;
    this.tmpVars = new java.util.LinkedList<codegen.dalvik.dec.T>();
    this.etype = null;
    this.evar = null;
    this.stms = new java.util.LinkedList<codegen.dalvik.stm.T>();
    this.method = null;
    this.classs = null;
    this.mainClass = null;
    this.program = null;
  }
  
  static int count = 0;
  String getTemp ()
  {
    // however, we should check that "count<=256"
    return "v"+count++;
  }
  void resetCount ()
  {
    count = 0;
  }
  
  // utility functions
  private void emitDec (codegen.dalvik.type.T ty, String id)
  {
    this.tmpVars.addLast(new codegen.dalvik.dec.Dec(ty, id));
  }

  private void emit(codegen.dalvik.stm.T s)
  {
    this.stms.add(s);
  }

  // /////////////////////////////////////////////////////
  // expressions
  @Override
  public void visit(ast.exp.Add e)
  {
  }

  @Override
  public void visit(ast.exp.And e)
  {
  }

  @Override
  public void visit(ast.exp.ArraySelect e)
  {
  }

  @Override
  public void visit(ast.exp.Call e)
  {
    e.exp.accept(this);
    for (ast.exp.T x : e.args) {
      x.accept(this);
    }
    e.rt.accept(this);
    codegen.dalvik.type.T rt = this.type;
    java.util.LinkedList<codegen.dalvik.type.T> at = new java.util.LinkedList<codegen.dalvik.type.T>();
    for (ast.type.T t : e.at) {
      t.accept(this);
      at.add(this.type);
    }
    emit(new codegen.dalvik.stm.Invokevirtual(e.id, e.type, at, rt));
    return;
  }

  @Override
  public void visit(ast.exp.False e)
  {
  }

  @Override
  public void visit(ast.exp.Id e)
  {
    e.type.accept(this);;
    this.evar = e.id;
    this.etype = this.type;
    // but what about this is a field?
    return;
  }

  @Override
  public void visit(ast.exp.Length e)
  {
  }

  @Override
  public void visit(ast.exp.Lt e)
  {
    Label tl = new Label(), fl = new Label(), el = new Label();
    e.left.accept(this);
    String lname = this.evar;
    e.right.accept(this);
    String rname = this.evar;
    String newname = util.Temp.next();
    this.evar = newname;
    this.etype = new codegen.dalvik.type.Int();
    emit(new codegen.dalvik.stm.Iflt(lname, rname, tl));
    emit(new codegen.dalvik.stm.Label(fl));
    emit(new codegen.dalvik.stm.Const(newname, 0));
    emit(new codegen.dalvik.stm.Goto32(el));
    emit(new codegen.dalvik.stm.Label(tl));
    emit(new codegen.dalvik.stm.Const(newname, 1));
    emit(new codegen.dalvik.stm.Goto32(el));
    emit(new codegen.dalvik.stm.Label(el));
    return;
  }

  @Override
  public void visit(ast.exp.NewIntArray e)
  {
  }

  @Override
  public void visit(ast.exp.NewObject e)
  {
    String newname = Temp.next();
    this.evar = newname;
    this.etype = new codegen.dalvik.type.Class(e.id);
    emit(new codegen.dalvik.stm.NewInstance(newname, e.id));
    return;
  }

  @Override
  public void visit(ast.exp.Not e)
  {
  }

  @Override
  public void visit(ast.exp.Num e)
  {
    String newname = Temp.next();
    this.evar = newname;
    this.etype = new codegen.dalvik.type.Int();
    emitDec (this.type, newname);
    emit(new codegen.dalvik.stm.Const(newname, e.num));
    return;
  }

  @Override
  public void visit(ast.exp.Sub e)
  {
    e.left.accept(this);
    String left = this.evar;
    e.right.accept(this);
    String right = this.evar;
    String newname = Temp.next();
    this.evar = newname;
    this.etype = new codegen.dalvik.type.Int();
    emitDec (this.etype, this.evar);
    emit(new codegen.dalvik.stm.Subint(newname, left, right));
    return;
  }

  @Override
  public void visit(ast.exp.This e)
  {
    return;
  }

  @Override
  public void visit(ast.exp.Times e)
  {
    e.left.accept(this);
    String left = this.evar;
    e.right.accept(this);
    String right = this.evar;
    String newname = Temp.next();
    this.evar = newname;
    this.etype = new codegen.dalvik.type.Int();
    emit(new codegen.dalvik.stm.Mulint(newname, left, right));
    return;
  }

  @Override
  public void visit(ast.exp.True e)
  {
  }

  // statements
  @Override
  public void visit(ast.stm.Assign s)
  {
    s.exp.accept(this);
    String right = this.evar;
    s.type.accept(this);
    codegen.dalvik.type.T ty = this.type;
    if (ty instanceof codegen.dalvik.type.Int){
      emit(new codegen.dalvik.stm.Move16(s.id, right));
    }
    else {
      emit(new codegen.dalvik.stm.Moveobject16(s.id, right));
    }
    return;
  }

  @Override
  public void visit(ast.stm.AssignArray s)
  {
  }

  @Override
  public void visit(ast.stm.Block s)
  {
  }

  @Override
  public void visit(ast.stm.If s)
  {
    Label tl = new Label(), fl = new Label(), el = new Label();
    s.condition.accept(this);
    String evar = this.evar;
    emit(new codegen.dalvik.stm.Ifnez(evar, tl));
    emit(new codegen.dalvik.stm.Label(fl));
    s.elsee.accept(this);
    emit(new codegen.dalvik.stm.Goto32(el));
    emit(new codegen.dalvik.stm.Label(tl));
    s.thenn.accept(this);
    emit(new codegen.dalvik.stm.Goto32(el));
    emit(new codegen.dalvik.stm.Label(el));
    return;
  }

  @Override
  public void visit(ast.stm.Print s)
  {
    String newname = Temp.next();
    s.exp.accept(this);
    emit(new codegen.dalvik.stm.Print(newname, this.evar));
    return;
  }

  @Override
  public void visit(ast.stm.While s)
  {
  }

  // type
  @Override
  public void visit(ast.type.Boolean t)
  {
  }

  @Override
  public void visit(ast.type.Class t)
  {
  }

  @Override
  public void visit(ast.type.Int t)
  {
    this.type = new codegen.dalvik.type.Int();
  }

  @Override
  public void visit(ast.type.IntArray t)
  {
  }
  
  // dec
  @Override
  public void visit(ast.dec.Dec d)
  {
    d.type.accept(this);
    this.dec = new codegen.dalvik.dec.Dec(this.type, d.id);
    return;
  }

  // method
  @Override
  public void visit(ast.method.Method m)
  {
    // There are two passes here:
    // In the 1st pass, the method is translated
    // into a three-address-code like intermediate representation.
    m.retType.accept(this);
    codegen.dalvik.type.T newRetType = this.type;
    java.util.LinkedList<codegen.dalvik.dec.T> newFormals = new java.util.LinkedList<codegen.dalvik.dec.T>();
    for (ast.dec.T d : m.formals) {
      d.accept(this);
      newFormals.add(this.dec);
    }
    java.util.LinkedList<codegen.dalvik.dec.T> locals = new java.util.LinkedList<codegen.dalvik.dec.T>();
    for (ast.dec.T d : m.locals) {
      d.accept(this);
      locals.add(this.dec);
    }
    this.stms = new java.util.LinkedList<codegen.dalvik.stm.T>();
    for (ast.stm.T s : m.stms) {
      s.accept(this);
    }

    // return statement is specially treated
    m.retExp.accept(this);
    String retName = this.evar;

    if (m.retType.getNum() > 0)
      emit(new codegen.dalvik.stm.ReturnObject(retName));
    else
      emit(new codegen.dalvik.stm.Return(retName));

    // in the second pass, you should rename all method
    // parameters according to the "p"-convention; and
    // rename all method locals according to the "v"-convention.
    // Your code here:
    
    
    
    // cook the final method.
    this.method = new codegen.dalvik.method.Method(newRetType, m.id,
        this.classId, newFormals, locals, this.stms, 0, 0);
    return;
  }

  // class
  @Override
  public void visit(ast.classs.Class c)
  {
    this.classId = c.id;
    java.util.LinkedList<codegen.dalvik.dec.T> newDecs = new java.util.LinkedList<codegen.dalvik.dec.T>();
    for (ast.dec.T dec : c.decs) {
      dec.accept(this);
      newDecs.add(this.dec);
    }
    java.util.LinkedList<codegen.dalvik.method.T> newMethods = new java.util.LinkedList<codegen.dalvik.method.T>();
    for (ast.method.T m : c.methods) {
      m.accept(this);
      newMethods.add(this.method);
    }
    this.classs = new codegen.dalvik.classs.Class(c.id, c.extendss, newDecs,
        newMethods);
    return;
  }

  // main class
  @Override
  public void visit(ast.mainClass.MainClass c)
  {
    c.stm.accept(this);
    this.mainClass = new codegen.dalvik.mainClass.MainClass(c.id, c.arg,
        this.stms);
    this.stms = new java.util.LinkedList<codegen.dalvik.stm.T>();
    return;
  }

  // program
  @Override
  public void visit(ast.program.Program p)
  {
    // do translations
    p.mainClass.accept(this);

    java.util.LinkedList<codegen.dalvik.classs.T> newClasses = new java.util.LinkedList<codegen.dalvik.classs.T>();
    for (ast.classs.T classs : p.classes) {
      classs.accept(this);
      newClasses.add(this.classs);
    }
    this.program = new codegen.dalvik.program.Program(this.mainClass,
        newClasses);
    return;
  }
}
