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

// Constant folding optimizations on an AST.

public class ConstFold implements ast.Visitor
{
  private Class.T newClass;
  private MainClass.T mainClass;
  public Program.T program;
  public Exp.T exp;
  public Stm.T stm;
  private java.util.LinkedList<ast.Ast.Class.T> classs;
  private java.util.LinkedList<ast.Ast.Method.T> methods;
  public boolean t4 = true;
  public ConstFold()
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
	  this.exp = e;
  }

  @Override
  public void visit(And e)
  {
	  e.left.accept(this);
	  Exp.T e1 = this.exp;
	  e.right.accept(this);
	  Exp.T e2 = this.exp;
	  if(e1 instanceof True && e2 instanceof True)
	  {
		  this.exp = new True();
		  this.t4 = false;
	  }
	  return;
  }

  @Override
  public void visit(ArraySelect e)
  {
	  this.exp = e;
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
	if(e.left instanceof Num && e.right instanceof Num)
	{
		int e1 = ((Num)e.left).num;
		int e2 = ((Num)e.right).num;
		if(e1<e2)
		{
			this.exp = new True();
			this.t4 = false;
			return;
		}
	}
	this.exp = e;
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
	  this.exp = e;
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
	  this.exp = e;
	  return;
  }

  @Override
  public void visit(True e)
  {
	  this.exp = e;
	  return;
  }

  // statements
  @Override
  public void visit(Assign s)
  {
    this.stm = s;
    return;
  }

  @Override
  public void visit(AssignArray s)
  {
	  this.stm = s;
  }

  @Override
  public void visit(Block s)
  {
	  this.stm = s;
  }

  @Override
  public void visit(If s)
  {
	  s.condition.accept(this);
	  Exp.T s1 = this.exp;
	  s.thenn.accept(this);
	  Stm.T s2 = this.stm;
	  s.elsee.accept(this);
	  Stm.T s3 = this.stm;
	  this.stm = new If(s1,s2,s3,s.linenum);
	  return;
  }

  @Override
  public void visit(Print s)
  {
	  this.stm = s;
	  return;
  }

  @Override
  public void visit(While s)
  {
	  s.condition.accept(this);
	  Exp.T s1 = this.exp;
	  this.stm = new While(s1,s.body,s.linenum);
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
    this.t4 = true;
    // You should comment out this line of code:
	p.mainClass.accept(this);
	this.classs = new java.util.LinkedList<ast.Ast.Class.T>();
	for(ast.Ast.Class.T clas:p.classes)
		clas.accept(this);
	this.program = new ast.Ast.Program.ProgramSingle(this.mainClass,this.classs);
	
    if (control.Control.isTracing("ast.ConstFold")&&!this.t4)
    {
      System.out.println("before ConstFold optimization:");
      ast.PrettyPrintVisitor pp = new ast.PrettyPrintVisitor();
      p.accept(pp);
      System.out.println("after ConstFold optimization:");
      this.program.accept(pp);
    }
    return;
  }

@Override
public void visit(Error t) {
	// TODO Auto-generated method stub
	
}
}
