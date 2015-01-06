package codegen.C;

import java.util.Hashtable;
import java.util.LinkedList;

import ast.Ast.Exp.NewIntArray;
import ast.Ast.Type.Error;
import codegen.C.Ast.Class;
import codegen.C.Ast.Class.ClassSingle;
import codegen.C.Ast.Dec;
import codegen.C.Ast.Exp;
import codegen.C.Ast.Exp.Call;
import codegen.C.Ast.Exp.Id;
import codegen.C.Ast.Exp.Lt;
import codegen.C.Ast.Exp.NewObject;
import codegen.C.Ast.Exp.Num;
import codegen.C.Ast.Exp.Sub;
import codegen.C.Ast.Exp.This;
import codegen.C.Ast.Exp.Times;
import codegen.C.Ast.MainMethod;
import codegen.C.Ast.MainMethod.MainMethodSingle;
import codegen.C.Ast.Method;
import codegen.C.Ast.Method.MethodSingle;
import codegen.C.Ast.Program;
import codegen.C.Ast.Program.ProgramSingle;
import codegen.C.Ast.Stm;
import codegen.C.Ast.Stm.Assign;
import codegen.C.Ast.Stm.If;
import codegen.C.Ast.Stm.Print;
import codegen.C.Ast.Stm.T;
import codegen.C.Ast.Type;
import codegen.C.Ast.Type.ClassType;
import codegen.C.Ast.Vtable;
import codegen.C.Ast.Vtable.VtableSingle;

// Given a Java ast, translate it into a C ast and outputs it.

public class TranslateVisitor implements ast.Visitor
{
  private ClassTable table;
  private String classId;
  private Type.T type; // type after translation
  private Dec.T dec;
  private Stm.T stm;
  private Exp.T exp;
  private Method.T method;
  private LinkedList<Dec.T> tmpVars;
  private LinkedList<Class.T> classes;
  private LinkedList<Vtable.T> vtables;
  private LinkedList<Method.T> methods;
  private MainMethod.T mainMethod;
  public Program.T program;
  int varnum = 0;
  
  public TranslateVisitor()
  {
    this.table = new ClassTable();
    this.classId = null;
    this.type = null;
    this.dec = null;
    this.stm = null;
    this.exp = null;
    this.method = null;
    this.classes = new LinkedList<Class.T>();
    this.vtables = new LinkedList<Vtable.T>();
    this.methods = new LinkedList<Method.T>();
    this.mainMethod = null;
    this.program = null;
  }

  // //////////////////////////////////////////////////////
  //
  public String genId()
  {
    return util.Temp.next();
  }

  // /////////////////////////////////////////////////////
  // expressions
  @Override
  public void visit(ast.Ast.Exp.Add e)
  {
	  e.left.accept(this);
	  Exp.T left = this.exp;
	  e.right.accept(this);
	  Exp.T right = this.exp;
	  this.exp = new codegen.C.Ast.Exp.Add(left, right);
	  return;
  }

  @Override
  public void visit(ast.Ast.Exp.And e)
  {
	  e.left.accept(this);
	  Exp.T left = this.exp;
	  e.right.accept(this);
	  Exp.T right = this.exp;
	  this.exp = new codegen.C.Ast.Exp.And(left, right);
	  return;
  }

  @Override
  public void visit(ast.Ast.Exp.ArraySelect e)
  {
	  e.array.accept(this);
	  Exp.T e1 = this.exp;
	  e.index.accept(this);
	  Exp.T e2 = this.exp;
	  this.exp = new codegen.C.Ast.Exp.ArraySelect(e1, e2);
  }

  @Override
  public void visit(ast.Ast.Exp.Call e)
  {
    e.exp.accept(this);
    String newid = this.genId();
    this.tmpVars.add(new Dec.DecSingle(new Type.ClassType(e.type), newid));
    Exp.T exp = this.exp;
    LinkedList<Exp.T> args = new LinkedList<Exp.T>();
    for (ast.Ast.Exp.T x : e.args) {
      x.accept(this);
      args.add(this.exp);
    }
    e.rt.accept(this);
    Type.T retType = this.type;
    this.exp = new Call(newid, exp, e.id, args);
    return;
  }

  @Override
  public void visit(ast.Ast.Exp.False e)
  {
	this.exp = new codegen.C.Ast.Exp.Num(0);
	return;
  }

  @Override
  public void visit(ast.Ast.Exp.Id e)
  {
    this.exp = new Id(e.id);
    return;
  }

  @Override
  public void visit(ast.Ast.Exp.Length e)
  {
	e.array.accept(this);
	Exp.T e1 = this.exp;
	this.exp = new codegen.C.Ast.Exp.Length(e1);
	return;
  }

  @Override
  public void visit(ast.Ast.Exp.Lt e)
  {
    e.left.accept(this);
    Exp.T left = this.exp;
    e.right.accept(this);
    Exp.T right = this.exp;
    this.exp = new Lt(left, right);
    return;
  }

  @Override
  public void visit(ast.Ast.Exp.NewIntArray e)
  {
	e.exp.accept(this);
	Exp.T e1 = this.exp;
	this.exp = new codegen.C.Ast.Exp.NewIntArray(e1);
	return;
  }

  @Override
  public void visit(ast.Ast.Exp.NewObject e)
  {
	String name = "var_"+varnum;
	varnum++;
    this.exp = new NewObject(e.id,name);
    return;
  }

  @Override
  public void visit(ast.Ast.Exp.Not e)
  {
	 e.exp.accept(this);
	 Exp.T e1 = this.exp;
	 this.exp = new codegen.C.Ast.Exp.Not(e1);
	 return;
  }

  @Override
  public void visit(ast.Ast.Exp.Num e)
  {
    this.exp = new Num(e.num);
    return;
  }

  @Override
  public void visit(ast.Ast.Exp.Sub e)
  {
    e.left.accept(this);
    Exp.T left = this.exp;
    e.right.accept(this);
    Exp.T right = this.exp;
    this.exp = new Sub(left, right);
    return;
  }

  @Override
  public void visit(ast.Ast.Exp.This e)
  {
    this.exp = new This();
    return;
  }

  @Override
  public void visit(ast.Ast.Exp.Times e)
  {
    e.left.accept(this);
    Exp.T left = this.exp;
    e.right.accept(this);
    Exp.T right = this.exp;
    this.exp = new Times(left, right);
    return;
  }

  @Override
  public void visit(ast.Ast.Exp.True e)
  {
	  this.exp = new codegen.C.Ast.Exp.Num(1);
	  return;
  }

  // //////////////////////////////////////////////
  // statements
  @Override
  public void visit(ast.Ast.Stm.Assign s)
  {
    s.exp.accept(this);
    this.stm = new Assign(s.id, this.exp);
    return;
  }

  @Override
  public void visit(ast.Ast.Stm.AssignArray s)
  {
	  s.exp.accept(this);
	  Exp.T s1 = this.exp;
	  s.index.accept(this);
	  Exp.T s2 = this.exp;
	  this.stm = new codegen.C.Ast.Stm.AssignArray(s.id, s1, s2);
  }

  @Override
  public void visit(ast.Ast.Stm.Block s)
  {
	  LinkedList<Stm.T> stmss = new LinkedList<Stm.T>();
	  for(ast.Ast.Stm.T c:s.stms)
	  {
		  c.accept(this);
		  Stm.T k = this.stm;
		  stmss.add(k);
	  }
	  this.stm = new codegen.C.Ast.Stm.Block(stmss);
	  return;
  }

  @Override
  public void visit(ast.Ast.Stm.If s)
  {
    s.condition.accept(this);
    Exp.T condition = this.exp;
    s.thenn.accept(this);
    Stm.T thenn = this.stm;
    s.elsee.accept(this);
    Stm.T elsee = this.stm;
    this.stm = new codegen.C.Ast.Stm.If(condition, thenn, elsee);
    return;
  }

  @Override
  public void visit(ast.Ast.Stm.Print s)
  {
    s.exp.accept(this);
    this.stm = new Print(this.exp);
    return;
  }

  @Override
  public void visit(ast.Ast.Stm.While s)
  {
	  s.condition.accept(this);
	  Exp.T condition = this.exp;
	  s.body.accept(this);
	  Stm.T body = this.stm;
	  this.stm = new codegen.C.Ast.Stm.While(condition,body);
	  return;
  }

  // ///////////////////////////////////////////
  // type
  @Override
  public void visit(ast.Ast.Type.Boolean t)
  {
	  this.type = new Type.Int();
  }

  @Override
  public void visit(ast.Ast.Type.ClassType t)
  {
	  this.type = new Type.ClassType(t.id);
  }

  @Override
  public void visit(ast.Ast.Type.Int t)
  {
    this.type = new Type.Int();
  }

  @Override
  public void visit(ast.Ast.Type.IntArray t)
  {
	  this.type = new Type.IntArray();
  }

  // ////////////////////////////////////////////////
  // dec
  int numm = 1;
  @Override
  public void visit(ast.Ast.Dec.DecSingle d)
  {
    d.type.accept(this);
    if(d.id.equals("prev"))
    {
    	d.id = "prev_"+numm;
    	numm++;
    }
    this.dec = new codegen.C.Ast.Dec.DecSingle(this.type, d.id);
    return;
  }

  // method
  @Override
  public void visit(ast.Ast.Method.MethodSingle m)
  {
	LinkedList<Dec.T> newFormals = new LinkedList<Dec.T>();
	Hashtable<String,Integer> var = new Hashtable<String,Integer>();
    this.tmpVars = new LinkedList<Dec.T>();
    m.retType.accept(this);
    Type.T newRetType = this.type;
    
    newFormals.add(new Dec.DecSingle(new ClassType(this.classId), "this"));
    for (ast.Ast.Dec.T d : m.formals) {
      ast.Ast.Dec.DecSingle dec = (ast.Ast.Dec.DecSingle)d;
      d.accept(this);
      newFormals.add(this.dec);
      var.put(dec.id,1);
    }
    LinkedList<Dec.T> locals = new LinkedList<Dec.T>();
    for (ast.Ast.Dec.T d : m.locals) {
      ast.Ast.Dec.DecSingle dec = (ast.Ast.Dec.DecSingle)d;
      d.accept(this);
      locals.add(this.dec);
      if(dec.type.getNum()<=0)
          var.put(dec.id,1);      
    }
    LinkedList<Stm.T> newStm = new LinkedList<Stm.T>();
    for (ast.Ast.Stm.T s : m.stms) {
      s.accept(this);
      newStm.add(this.stm);
    }
    m.retExp.accept(this);
    Exp.T retExp = this.exp;
    for (Dec.T dec : this.tmpVars) {
      Dec.DecSingle decc = (Dec.DecSingle)dec;
      locals.add(decc);
    }
    MethodSingle m1 = new MethodSingle(newRetType, this.classId, m.id,
        newFormals, locals, newStm, retExp,var,m.classvar);
    this.method = m1;
    return;
  }

  // class
  @Override
  public void visit(ast.Ast.Class.ClassSingle c)
  {
	Hashtable<String,Integer> classvar = new Hashtable<String,Integer>();
	String res = new String();
    ClassBinding cb = this.table.get(c.id);
	LinkedList<Tuple> m1 = cb.fields;
	for(Tuple m2:m1)
	{   
        classvar.put(m2.id, 1);
        if(m2.type instanceof Type.Int)
        {
        	res = res+"0";
        }
        else
        {
        	res = res+"1";
        }
	}
    this.classes.add(new ClassSingle(c.id, cb.fields,classvar));
    this.vtables.add(new VtableSingle(c.id, cb.methods,res));
    this.classId = c.id;
    
    for (ast.Ast.Method.T m : c.methods)
    {
      ast.Ast.Method.MethodSingle mm = (ast.Ast.Method.MethodSingle)m;
      mm.classvar = classvar;
      mm.accept(this);
      this.methods.add(this.method);
    }
    
    return;
  }

  // main class
  @Override
  public void visit(ast.Ast.MainClass.MainClassSingle c)
  {
	Hashtable<String,Integer> classvar = new Hashtable<String,Integer>();
    ClassBinding cb = this.table.get(c.id);
    Class.T newc = new ClassSingle(c.id, cb.fields,classvar);
    this.classes.add(newc);
    this.vtables.add(new VtableSingle(c.id, cb.methods,""));

    this.tmpVars = new LinkedList<Dec.T>();

    c.stm.accept(this);
    MainMethod.T mthd = new MainMethodSingle(this.tmpVars, this.stm);
    this.mainMethod = mthd;
    return;
  }

  // /////////////////////////////////////////////////////
  // the first pass
  public void scanMain(ast.Ast.MainClass.T m)
  {
    this.table.init(((ast.Ast.MainClass.MainClassSingle) m).id, null);
    // this is a special hacking in that we don't want to
    // enter "main" into the table. 
    return;
  }

  public void scanClasses(java.util.LinkedList<ast.Ast.Class.T> cs)
  {
    // put empty chuncks into the table
    for (ast.Ast.Class.T c : cs) {
      ast.Ast.Class.ClassSingle cc = (ast.Ast.Class.ClassSingle) c;
      this.table.init(cc.id, cc.extendss);
    }

    // put class fields and methods into the table
    for (ast.Ast.Class.T c : cs) 
    {
      ast.Ast.Class.ClassSingle cc = (ast.Ast.Class.ClassSingle) c;
      LinkedList<Dec.T> newDecs = new LinkedList<Dec.T>();
      for (ast.Ast.Dec.T dec : cc.decs) 
      {
        dec.accept(this);
        newDecs.add(this.dec);
      }
      
      this.table.initDecs(cc.id, newDecs);

      // all methods
      java.util.LinkedList<ast.Ast.Method.T> methods = cc.methods;
      for (ast.Ast.Method.T mthd : methods) 
      {
        ast.Ast.Method.MethodSingle m = (ast.Ast.Method.MethodSingle) mthd;
        LinkedList<Dec.T> newArgs = new LinkedList<Dec.T>();
        // newArgs.addAll(newDecs);
        for (ast.Ast.Dec.T arg : m.formals) 
        {
          arg.accept(this);
          newArgs.add(this.dec);
        }
        m.retType.accept(this);
        Type.T newRet = this.type;
        this.table.initMethod(cc.id, newRet, newArgs, m.id);
      }
    }

    // calculate all inheritance information
    for (ast.Ast.Class.T c : cs) {
      ast.Ast.Class.ClassSingle cc = (ast.Ast.Class.ClassSingle) c;
      this.table.inherit(cc.id);
    }
  }

  public void scanProgram(ast.Ast.Program.T p)
  {
    ast.Ast.Program.ProgramSingle pp = (ast.Ast.Program.ProgramSingle) p;
    scanMain(pp.mainClass);
    scanClasses(pp.classes);
    return;
  }

  // end of the first pass
  // ////////////////////////////////////////////////////

  // program
  @Override
  public void visit(ast.Ast.Program.ProgramSingle p)
  {
    // The first pass is to scan the whole program "p", and
    // to collect all information of inheritance.
    scanProgram(p);
    // do translations
    p.mainClass.accept(this);
    for (ast.Ast.Class.T classs : p.classes) 
    {
      classs.accept(this);
    }
    ProgramSingle prog = new ProgramSingle(this.classes, this.vtables,
        this.methods, this.mainMethod);
    this.program = prog;
    return;
  }

  @Override
  public void visit(Error t) {
	// TODO Auto-generated method stub
	
}
}
