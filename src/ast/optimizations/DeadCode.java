package ast.optimizations;

import ast.Ast.Class.ClassSingle;
import ast.Ast.Dec.DecSingle;
import ast.Ast.MainClass.MainClassSingle;
import ast.Ast.Method.MethodSingle;
import ast.Ast.Program.ProgramSingle;
import ast.Ast.Type.Boolean;
import ast.Ast.Type.ClassType;
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
  }

  @Override
  public void visit(And e)
  {
  }

  @Override
  public void visit(ArraySelect e)
  {
  }

  @Override
  public void visit(Call e)
  {
    return;
  }

  @Override
  public void visit(False e)
  {
  }

  @Override
  public void visit(Id e)
  {
    return;
  }

  @Override
  public void visit(Length e)
  {
  }

  @Override
  public void visit(Lt e)
  {
    return;
  }

  @Override
  public void visit(NewIntArray e)
  {
  }

  @Override
  public void visit(NewObject e)
  {
    return;
  }

  @Override
  public void visit(Not e)
  {
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
  }

  // statements
  @Override
  public void visit(Assign s)
  {
    
    return;
  }

  @Override
  public void visit(AssignArray s)
  {
  }

  @Override
  public void visit(Block s)
  {
  }

  @Override
  public void visit(If s)
  {
   
    return;
  }

  @Override
  public void visit(Print s)
  {
    return;
  }

  @Override
  public void visit(While s)
  {
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
    return;
  }

  // class
  @Override
  public void visit(ClassSingle c)
  {
    
    return;
  }

  // main class
  @Override
  public void visit(MainClassSingle c)
  {
    
    return;
  }

  // program
  @Override
  public void visit(ProgramSingle p)
  {
    
 // You should comment out this line of code:
    this.program = p;

    if (control.Control.trace.equals("ast.DeadCode")){
      System.out.println("before optimization:");
      ast.PrettyPrintVisitor pp = new ast.PrettyPrintVisitor();
      p.accept(pp);
      System.out.println("after optimization:");
      this.program.accept(pp);
    }
    return;
  }
}
