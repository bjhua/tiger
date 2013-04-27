package ast;

public interface Visitor
{
  // expressions
  public void visit(ast.exp.Add e);

  public void visit(ast.exp.And e);

  public void visit(ast.exp.ArraySelect e);

  public void visit(ast.exp.Call e);

  public void visit(ast.exp.False e);

  public void visit(ast.exp.Id e);

  public void visit(ast.exp.Length e);

  public void visit(ast.exp.Lt e);

  public void visit(ast.exp.NewIntArray e);

  public void visit(ast.exp.NewObject e);

  public void visit(ast.exp.Not e);

  public void visit(ast.exp.Num e);

  public void visit(ast.exp.Sub e);

  public void visit(ast.exp.This e);

  public void visit(ast.exp.Times e);

  public void visit(ast.exp.True e);

  // statements
  public void visit(ast.stm.Assign s);

  public void visit(ast.stm.AssignArray s);

  public void visit(ast.stm.Block s);

  public void visit(ast.stm.If s);

  public void visit(ast.stm.Print s);

  public void visit(ast.stm.While s);

  // type
  public void visit(ast.type.Boolean t);

  public void visit(ast.type.Class t);

  public void visit(ast.type.Int t);

  public void visit(ast.type.IntArray t);

  // dec
  public void visit(ast.dec.Dec d);

  // method
  public void visit(ast.method.Method m);

  // class
  public void visit(ast.classs.Class c);

  // main class
  public void visit(ast.mainClass.MainClass c);

  // program
  public void visit(ast.program.Program p);
}
