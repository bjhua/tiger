package ast.optimizations;

import ast.Ast.Class.ClassSingle;
import ast.Ast.Dec.DecSingle;
import ast.Ast.Exp;
import ast.Ast.MainClass.MainClassSingle;
import ast.Ast.Method;
import ast.Ast.Method.MethodSingle;
import ast.Ast.Program.ProgramSingle;
import ast.Ast.Type.Boolean;
import ast.Ast.Type.ClassType;
import ast.Ast.Type.Error;
import ast.Ast.Type.Int;
import ast.Ast.Type.IntArray;
import ast.Ast.Exp.*;
import ast.Ast.Stm.*;
import ast.Ast.Type.*;

// Dead code elimination optimizations on an AST.

public class DeadCode implements ast.Visitor
{
  private ast.Ast.Class.T newClass;
  private ast.Ast.MainClass.T mainClass;
  public ast.Ast.Program.T program;
  private boolean isture;
  private java.util.LinkedList<ast.Ast.Class.T> classs;
  private java.util.LinkedList<ast.Ast.Method.T> methods;
  private ast.Ast.Stm.T stm;
  boolean t2 = true;
  public DeadCode()
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
	  return;
  }

  @Override
  public void visit(And e)
  {
	  return;
  }

  @Override
  public void visit(ArraySelect e)
  {
	  return;
  }

  @Override
  public void visit(Call e)
  {
    return;
  }

  @Override
  public void visit(False e)
  {
	  this.isture = false;
	  return;
  }

  @Override
  public void visit(Id e)
  {
    return;
  }

  @Override
  public void visit(Length e)
  {
	  return;
  }

  @Override
  public void visit(Lt e)
  {
    return;
  }

  @Override
  public void visit(NewIntArray e)
  {
	return;
  }

  @Override
  public void visit(NewObject e)
  {
    return;
  }

  @Override
  public void visit(Not e)
  {
	  e.exp.accept(this);
	  this.isture = !this.isture;
	  return;
  }

  @Override
  public void visit(Num e)
  {
    return;
  }

  @Override
  public void visit(Sub e)
  {
    return;
  }

  @Override
  public void visit(This e)
  {
    return;
  }

  @Override
  public void visit(Times e)
  {
    
    return;
  }

  @Override
  public void visit(True e)
  {
	  this.isture = true;
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
	  return;
  }

  @Override
  public void visit(Block s)
  {
	  this.stm = s;
	  return;
  }

  @Override
  public void visit(If s)
  {
    if(s.condition instanceof Exp.True)
    {
    	this.stm = s.thenn;
    	this.t2 = false;
    }
    else if(s.condition instanceof Exp.False)
    {
    	this.stm = s.elsee;
    	this.t2 = false;
    }
    else
    	this.stm = s;
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
	  if(s.condition instanceof False)
	  {
		  this.stm = null;
		  this.t2 = false;
	  }
	  else
		  this.stm = s;
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
  //You should comment out this line of code:
    p.mainClass.accept(this);
    this.classs = new java.util.LinkedList<ast.Ast.Class.T>();
    for(ast.Ast.Class.T clas:p.classes)
    	clas.accept(this);
    this.program = new ast.Ast.Program.ProgramSingle(this.mainClass,this.classs);
    
    if (control.Control.trace.contains("ast.DeadCode")&&!this.t2)
    {
      System.out.println("before DeadCode optimization:");
      ast.PrettyPrintVisitor pp = new ast.PrettyPrintVisitor();
      p.accept(pp);
      System.out.println("after DeadCode optimization:");
      this.program.accept(pp);
    }
   
    return;
  }

@Override
public void visit(Error t) 
{
}
}
