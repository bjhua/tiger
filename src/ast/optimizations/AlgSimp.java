package ast.optimizations;

// Algebraic simplification optimizations on an AST.

public class AlgSimp implements ast.Visitor
{
  private ast.classs.T newClass;
  private ast.mainClass.T mainClass;
  public ast.program.T program;
  
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
    return;
  }

  @Override
  public void visit(ast.exp.False e)
  {
  }

  @Override
  public void visit(ast.exp.Id e)
  {
    return;
  }

  @Override
  public void visit(ast.exp.Length e)
  {
  }

  @Override
  public void visit(ast.exp.Lt e)
  {
    return;
  }

  @Override
  public void visit(ast.exp.NewIntArray e)
  {
  }

  @Override
  public void visit(ast.exp.NewObject e)
  {
    return;
  }

  @Override
  public void visit(ast.exp.Not e)
  {
  }

  @Override
  public void visit(ast.exp.Num e)
  {
    return;
  }

  @Override
  public void visit(ast.exp.Sub e)
  {
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
   
    return;
  }

  @Override
  public void visit(ast.stm.Print s)
  {
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
  }

  @Override
  public void visit(ast.type.IntArray t)
  {
  }

  // dec
  @Override
  public void visit(ast.dec.Dec d)
  {
    return;
  }

  // method
  @Override
  public void visit(ast.method.Method m)
  {
    return;
  }

  // class
  @Override
  public void visit(ast.classs.Class c)
  {
    
    return;
  }

  // main class
  @Override
  public void visit(ast.mainClass.MainClass c)
  {
    
    return;
  }

  // program
  @Override
  public void visit(ast.program.Program p)
  {
    
 // You should comment out this line of code:
    this.program = p;

    if (control.Control.trace.equals("ast.AlgSimp")){
      System.out.println("before optimization:");
      ast.PrettyPrintVisitor pp = new ast.PrettyPrintVisitor();
      p.accept(pp);
      System.out.println("after optimization:");
      this.program.accept(pp);
    }
    return;
  }
}
