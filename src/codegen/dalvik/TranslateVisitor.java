package codegen.dalvik;

import java.util.LinkedList;

import codegen.dalvik.Ast.Class;
import codegen.dalvik.Ast.Class.ClassSingle;
import codegen.dalvik.Ast.Dec;
import codegen.dalvik.Ast.Dec.DecSingle;
import codegen.dalvik.Ast.MainClass;
import codegen.dalvik.Ast.MainClass.MainClassSingle;
import codegen.dalvik.Ast.Method;
import codegen.dalvik.Ast.Method.MethodSingle;
import codegen.dalvik.Ast.Program;
import codegen.dalvik.Ast.Program.ProgramSingle;
import codegen.dalvik.Ast.Stm;
import codegen.dalvik.Ast.Stm.*;
import codegen.dalvik.Ast.Type;
import util.Label;
import util.Temp;

// Given a Java AST, translate it into Dalvik bytecode.

public class TranslateVisitor implements ast.Visitor
{
  private String classId;
  private Type.T type; // type after translation
  private Dec.T dec;
  // these two fields are expression-related: after
  // translating an expression, we get the expression's
  // type "etype" and the expression name "evar";
  private Type.T etype;
  private String evar;
  private LinkedList<Dec.T> tmpVars;
  private LinkedList<Stm.T> stms;
  private Method.T method;
  private Class.T clazz;
  private MainClass.T mainClass;
  public Program.T program;

  public TranslateVisitor()
  {
    this.classId = null;
    this.type = null;
    this.dec = null;
    this.tmpVars = new LinkedList<Dec.T>();
    this.etype = null;
    this.evar = null;
    this.stms = new LinkedList<Stm.T>();
    this.method = null;
    this.clazz = null;
    this.mainClass = null;
    this.program = null;
  }

  static int count = 0;

  String getTemp()
  {
    // however, we should check that "count<=256"
    return "v" + count++;
  }

  void resetCount()
  {
    count = 0;
  }

  // utility functions
  private void emitDec(Type.T ty, String id)
  {
    this.tmpVars.addLast(new DecSingle(ty, id));
  }

  private void emit(Stm.T s)
  {
    this.stms.add(s);
  }

  // /////////////////////////////////////////////////////
  // expressions
  @Override
  public void visit(ast.Ast.Exp.Add e)
  {
  }

  @Override
  public void visit(ast.Ast.Exp.And e)
  {
  }

  @Override
  public void visit(ast.Ast.Exp.ArraySelect e)
  {
  }

  @Override
  public void visit(ast.Ast.Exp.Call e)
  {
    e.exp.accept(this);
    for (ast.Ast.Exp.T x : e.args) {
      x.accept(this);
    }
    e.rt.accept(this);
    Type.T rt = this.type;
    java.util.LinkedList<Type.T> at = new LinkedList<Type.T>();
    for (ast.Ast.Type.T t : e.at) {
      t.accept(this);
      at.add(this.type);
    }
    emit(new Invokevirtual(e.id, e.type, at, rt));
    return;
  }

  @Override
  public void visit(ast.Ast.Exp.False e)
  {
  }

  @Override
  public void visit(ast.Ast.Exp.Id e)
  {
    e.type.accept(this);
    ;
    this.evar = e.id;
    this.etype = this.type;
    // but what about this is a field?
    return;
  }

  @Override
  public void visit(ast.Ast.Exp.Length e)
  {
  }

  @Override
  public void visit(ast.Ast.Exp.Lt e)
  {
    Label tl = new Label(), fl = new Label(), el = new Label();
    e.left.accept(this);
    String lname = this.evar;
    e.right.accept(this);
    String rname = this.evar;
    String newname = util.Temp.next();
    this.evar = newname;
    this.etype = new Type.Int();
    emit(new Iflt(lname, rname, tl));
    emit(new LabelJ(fl));
    emit(new Const(newname, 0));
    emit(new Goto32(el));
    emit(new LabelJ(tl));
    emit(new Const(newname, 1));
    emit(new Goto32(el));
    emit(new LabelJ(el));
    return;
  }

  @Override
  public void visit(ast.Ast.Exp.NewIntArray e)
  {
  }

  @Override
  public void visit(ast.Ast.Exp.NewObject e)
  {
    String newname = Temp.next();
    this.evar = newname;
    this.etype = new Type.ClassType(e.id);
    emit(new NewInstance(newname, e.id));
    return;
  }

  @Override
  public void visit(ast.Ast.Exp.Not e)
  {
  }

  @Override
  public void visit(ast.Ast.Exp.Num e)
  {
    String newname = Temp.next();
    this.evar = newname;
    this.etype = new Type.Int();
    emitDec(this.type, newname);
    emit(new Const(newname, e.num));
    return;
  }

  @Override
  public void visit(ast.Ast.Exp.Sub e)
  {
    e.left.accept(this);
    String left = this.evar;
    e.right.accept(this);
    String right = this.evar;
    String newname = Temp.next();
    this.evar = newname;
    this.etype = new Type.Int();
    emitDec(this.etype, this.evar);
    emit(new Subint(newname, left, right));
    return;
  }

  @Override
  public void visit(ast.Ast.Exp.This e)
  {
    return;
  }

  @Override
  public void visit(ast.Ast.Exp.Times e)
  {
    e.left.accept(this);
    String left = this.evar;
    e.right.accept(this);
    String right = this.evar;
    String newname = Temp.next();
    this.evar = newname;
    this.etype = new Type.Int();
    emit(new Mulint(newname, left, right));
    return;
  }

  @Override
  public void visit(ast.Ast.Exp.True e)
  {
  }

  // ///////////////////////////////////////////////////
  // statements
  @Override
  public void visit(ast.Ast.Stm.Assign s)
  {
    s.exp.accept(this);
    String right = this.evar;
    s.type.accept(this);
    Type.T ty = this.type;
    if (ty instanceof Type.Int) {
      emit(new Move16(s.id, right));
    } else {
      emit(new Moveobject16(s.id, right));
    }
    return;
  }

  @Override
  public void visit(ast.Ast.Stm.AssignArray s)
  {
  }

  @Override
  public void visit(ast.Ast.Stm.Block s)
  {
  }

  @Override
  public void visit(ast.Ast.Stm.If s)
  {
    Label tl = new Label(), fl = new Label(), el = new Label();

    s.condition.accept(this);
    String evar = this.evar;
    emit(new Ifnez(evar, tl));
    emit(new LabelJ(fl));
    s.elsee.accept(this);
    emit(new Goto32(el));
    emit(new LabelJ(tl));
    s.thenn.accept(this);
    emit(new Goto32(el));
    emit(new LabelJ(el));
    return;
  }

  @Override
  public void visit(ast.Ast.Stm.Print s)
  {
    String newname = Temp.next();
    s.exp.accept(this);
    emit(new Print(newname, this.evar));
    return;
  }

  @Override
  public void visit(ast.Ast.Stm.While s)
  {
  }

  // ////////////////////////////////////////////////////////
  // type
  @Override
  public void visit(ast.Ast.Type.Boolean t)
  {
  }

  @Override
  public void visit(ast.Ast.Type.ClassType t)
  {
  }

  @Override
  public void visit(ast.Ast.Type.Int t)
  {
    this.type = new Type.Int();
  }

  @Override
  public void visit(ast.Ast.Type.IntArray t)
  {
  }

  // dec
  @Override
  public void visit(ast.Ast.Dec.DecSingle d)
  {
    d.type.accept(this);
    this.dec = new DecSingle(this.type, d.id);
    return;
  }

  // method
  @Override
  public void visit(ast.Ast.Method.MethodSingle m)
  {
    // There are two passes here:
    // In the 1st pass, the method is translated
    // into a three-address-code like intermediate representation.
    m.retType.accept(this);
    Type.T newRetType = this.type;
    LinkedList<Dec.T> newFormals = new LinkedList<Dec.T>();
    for (ast.Ast.Dec.T d : m.formals) {
      d.accept(this);
      newFormals.add(this.dec);
    }
    LinkedList<Dec.T> locals = new LinkedList<Dec.T>();
    for (ast.Ast.Dec.T d : m.locals) {
      d.accept(this);
      locals.add(this.dec);
    }
    this.stms = new LinkedList<Stm.T>();
    for (ast.Ast.Stm.T s : m.stms) {
      s.accept(this);
    }

    // return statement is specially treated
    m.retExp.accept(this);
    String retName = this.evar;

    if (m.retType.getNum() > 0)
      emit(new ReturnObject(retName));
    else
      emit(new Return(retName));

    // in the second pass, you should rename all method
    // parameters according to the "p"-convention; and
    // rename all method locals according to the "v"-convention.
    // Your code here:

    // cook the final method.
    this.method = new MethodSingle(newRetType, m.id, this.classId, newFormals,
        locals, this.stms, 0, 0);
    return;
  }

  // ///////////////////////////////////////////////////////////////
  // class
  @Override
  public void visit(ast.Ast.Class.ClassSingle c)
  {
    this.classId = c.id;
    LinkedList<Dec.T> newDecs = new LinkedList<Dec.T>();
    for (ast.Ast.Dec.T dec : c.decs) {
      dec.accept(this);
      newDecs.add(this.dec);
    }
    LinkedList<Method.T> newMethods = new LinkedList<Method.T>();
    for (ast.Ast.Method.T m : c.methods) {
      m.accept(this);
      newMethods.add(this.method);
    }
    this.clazz = new ClassSingle(c.id, c.extendss, newDecs, newMethods);
    return;
  }

  // /////////////////////////////////////////////////////////////////
  // main class
  @Override
  public void visit(ast.Ast.MainClass.MainClassSingle c)
  {
    c.stm.accept(this);
    this.mainClass = new MainClassSingle(c.id, c.arg, this.stms);
    this.stms = new LinkedList<Stm.T>();
    return;
  }

  // program
  @Override
  public void visit(ast.Ast.Program.ProgramSingle p)
  {
    // do translations
    p.mainClass.accept(this);

    LinkedList<Class.T> newClasses = new LinkedList<Class.T>();
    for (ast.Ast.Class.T classs : p.classes) {
      classs.accept(this);
      newClasses.add(this.clazz);
    }
    this.program = new ProgramSingle(this.mainClass, newClasses);
    return;
  }
}
