package ast.optimizations;

import ast.Ast.Class;
import ast.Ast.Exp;
import ast.Ast.Stm;
import ast.Ast.Class.ClassSingle;
import ast.Ast.Dec.DecSingle;
import ast.Ast.Exp.Add;
import ast.Ast.Exp.And;
import ast.Ast.Exp.ArraySelect;
import ast.Ast.Exp.Call;
import ast.Ast.Exp.False;
import ast.Ast.Exp.Id;
import ast.Ast.Exp.Length;
import ast.Ast.Exp.Lt;
import ast.Ast.Exp.NewIntArray;
import ast.Ast.Exp.NewObject;
import ast.Ast.Exp.Not;
import ast.Ast.Exp.Num;
import ast.Ast.Exp.Sub;
import ast.Ast.Exp.This;
import ast.Ast.Exp.Times;
import ast.Ast.Exp.True;
import ast.Ast.MainClass;
import ast.Ast.MainClass.MainClassSingle;
import ast.Ast.Method.MethodSingle;
import ast.Ast.Program;
import ast.Ast.Program.ProgramSingle;
import ast.Ast.Stm.Assign;
import ast.Ast.Stm.AssignArray;
import ast.Ast.Stm.Block;
import ast.Ast.Stm.If;
import ast.Ast.Stm.Print;
import ast.Ast.Stm.While;
import ast.Ast.Type.Boolean;
import ast.Ast.Type.ClassType;
import ast.Ast.Type.Error;
import ast.Ast.Type.Int;
import ast.Ast.Type.IntArray;

// Algebraic simplification optimizations on an AST.

public class AlgSimp implements ast.Visitor
{
  private Class.T newClass;
  private MainClass.T mainClass;
  public Program.T program;
  private java.util.LinkedList<ast.Ast.Class.T> classs;
  private java.util.LinkedList<ast.Ast.Method.T> methods;
  private ast.Ast.Stm.T stm;
  private ast.Ast.Exp.T exp;
  boolean t3 = true;
  public AlgSimp()
  {
    this.newClass = null;
    this.mainClass = null;
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
  public void visit(Add e)
  {
	  e.left.accept(this);
	  ast.Ast.Exp.T e1 = this.exp;
	  e.right.accept(this);
	  ast.Ast.Exp.T e2 = this.exp;
	  if(e1 instanceof ast.Ast.Exp.Num && e2 instanceof ast.Ast.Exp.Num)
	  {
		  int nu = ((ast.Ast.Exp.Num)e1).num + ((ast.Ast.Exp.Num)e2).num;
		  this.exp = new ast.Ast.Exp.Num(nu);
		  this.t3 = false;
		  return;
	  }
	  else
		  this.exp = new Add(e1,e2,e.linenum);
  }

  @Override
  public void visit(And e)
  {
	  e.left.accept(this);
	  Exp.T e1 = this.exp;
	  e.right.accept(this);
	  Exp.T e2 = this.exp;
	  this.exp = new And(e1,e2,e.linenum);
	  return;
  }

  @Override
  public void visit(ArraySelect e)
  {
	  e.index.accept(this);
	  this.exp = new ArraySelect(e.array,this.exp,e.linenum);
	  return;
  }

  @Override
  public void visit(Call e)
  {
	  this.exp = e;
	  return;
  }

  @Override
  public void visit(False e)
  {
	  this.exp = e;
	  return;
  }

  @Override
  public void visit(Id e)
  {
	  this.exp = e;
	  return;
  }

  @Override
  public void visit(Length e)
  {
	  this.exp = e;
	  return;
  }

  @Override
  public void visit(Lt e)
  {
	e.left.accept(this);
	Exp.T e1 = this.exp;
	e.right.accept(this);
	Exp.T e2 = this.exp;
	this.exp = new Lt(e1,e2,e.linenum);
    return;
  }

  @Override
  public void visit(NewIntArray e)
  {
	this.exp = e;
  }

  @Override
  public void visit(NewObject e)
  {
	this.exp = e;
    return;
  }

  @Override
  public void visit(Not e)
  {
	  e.exp.accept(this);
	  Exp.T e1 = this.exp;
	  this.exp = new Not(e1,e.linenum);
	  return;
  }

  @Override
  public void visit(Num e)
  {
	this.exp = e;
    return;
  }

  @Override
  public void visit(Sub e)
  {
	  e.left.accept(this);
	  ast.Ast.Exp.T e1 = this.exp;
	  e.right.accept(this);
	  ast.Ast.Exp.T e2 = this.exp;
	  if(e1 instanceof ast.Ast.Exp.Num && e2 instanceof ast.Ast.Exp.Num)
	  {
		  int nu = ((ast.Ast.Exp.Num)e1).num - ((ast.Ast.Exp.Num)e2).num;
		  this.exp = new ast.Ast.Exp.Num(nu);
		  this.t3 = false;
		  return;
	  }  
	  else
		  this.exp = e;
      return;
  }

  @Override
  public void visit(This e)
  {
	this.exp = e;
    return;
  }

  @Override
  public void visit(Times e)
  {
	  e.left.accept(this);
	  ast.Ast.Exp.T e1 = this.exp;
	  e.right.accept(this);
	  ast.Ast.Exp.T e2 = this.exp;
	  if((e1 instanceof ast.Ast.Exp.Num && ((Num)e1).num == 0) || 
	     (e2 instanceof ast.Ast.Exp.Num && ((Num)e2).num == 0))
	  {
		  this.exp = new Num(0);
		  this.t3 = false;
		  return;
	  }
	  if(e1 instanceof ast.Ast.Exp.Num && e2 instanceof ast.Ast.Exp.Num)
	  {
		  int nu = ((ast.Ast.Exp.Num)e1).num * ((ast.Ast.Exp.Num)e2).num;
		  this.exp = new ast.Ast.Exp.Num(nu);
		  this.t3 = false;
		  return;
	  }  
	  else
		  this.exp = e;
      return;
  }

  @Override
  public void visit(True e)
  {
	  this.exp = e;
	  return;
  }

  /////////////////////////////////////////
  // statements
  @Override
  public void visit(Assign s)
  {
	s.exp.accept(this);
	this.stm = new Assign(s.id,this.exp,s.linenum);
    return;
  }

  @Override
  public void visit(AssignArray s)
  {
	  s.exp.accept(this);
	  this.stm = new AssignArray(s.id,s.index,this.exp,s.linenum);
	  return;
  }

  @Override
  public void visit(Block s)
  {
	  java.util.LinkedList<Stm.T> stmm = new java.util.LinkedList<Stm.T>();
	  for(ast.Ast.Stm.T m:s.stms)
	  {
	     m.accept(this);
	     stmm.add(this.stm);
	  }
	  this.stm = new Block(stmm);
	  return;
  }

  @Override
  public void visit(If s)
  {
     s.condition.accept(this);
     ast.Ast.Exp.T s1 = this.exp;
     s.thenn.accept(this);
     ast.Ast.Stm.T s2 = this.stm;
     s.elsee.accept(this);
     this.stm = new If(s1,s2,this.stm,s.linenum); 
  }

  @Override
  public void visit(Print s)
  {
	  s.exp.accept(this);
	  this.stm = new Print(this.exp,s.linenum);
	  return;
  }

  @Override
  public void visit(While s)
  {
	  s.condition.accept(this);
	  ast.Ast.Exp.T s1 = this.exp;
	  s.body.accept(this);
	  ast.Ast.Stm.T s2 = this.stm;
	  this.stm = new While(s1,s2,s.linenum);
	  return;
  }

  // type
  @Override
  public void visit(Boolean t)
  {
  }

  @Override
  public void visit(ClassType t)
  {
  }

  @Override
  public void visit(Int t)
  {
  }

  @Override
  public void visit(IntArray t)
  {
  }

  // dec
  @Override
  public void visit(DecSingle d)
  {
    return;
  }

  // method
  @Override
  public void visit(MethodSingle m)
  {
	  java.util.LinkedList<ast.Ast.Stm.T> stms = new java.util.LinkedList<ast.Ast.Stm.T>();
	  for(ast.Ast.Stm.T stm:m.stms)
	  {
		 stm.accept(this);
		 if(this.stm != null)
	    	stms.add(this.stm);
	  }
	  this.methods.add(new ast.Ast.Method.MethodSingle(m.retType,m.id,m.formals,
													 m.locals,stms,m.retExp));
	  return;
  }

  // class
  @Override
  public void visit(ClassSingle c)
  {
	  this.methods = new java.util.LinkedList<ast.Ast.Method.T>();
	  for(ast.Ast.Method.T m : c.methods)
	  	m.accept(this);
	  this.classs.add(new ast.Ast.Class.ClassSingle(c.id, c.extendss, c.decs, this.methods));
	  return;
  }

  // main class
  @Override
  public void visit(MainClassSingle c)
  {
	c.stm.accept(this);
	this.mainClass = new MainClassSingle(c.id,c.arg,this.stm);
    return;
  }

  // program
  @Override
  public void visit(ProgramSingle p)
  {
	// You should comment out this line of code:
	p.mainClass.accept(this);
	this.classs = new java.util.LinkedList<ast.Ast.Class.T>();
	for(ast.Ast.Class.T clas:p.classes)
    	((Class.ClassSingle)clas).accept(this);
    this.program = new ast.Ast.Program.ProgramSingle(this.mainClass,this.classs);
    
    
    if (control.Control.trace.contains("ast.AlgSimp")&&!this.t3)
    {
      System.out.println("before AlgSimp optimization:");
      ast.PrettyPrintVisitor pp = new ast.PrettyPrintVisitor();
      p.accept(pp);
      System.out.println("after AlgSimp optimization:");
      this.program.accept(pp);
    }
    return;
  }

@Override
public void visit(Error t) {
	// TODO Auto-generated method stub
	
}
}
