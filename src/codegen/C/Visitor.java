package codegen.C;

public interface Visitor
{
  // expressions
  public void visit(codegen.C.exp.Add e);

  public void visit(codegen.C.exp.And e);

  public void visit(codegen.C.exp.ArraySelect e);

  public void visit(codegen.C.exp.Call e);

  public void visit(codegen.C.exp.Id e);

  public void visit(codegen.C.exp.Length e);

  public void visit(codegen.C.exp.Lt e);

  public void visit(codegen.C.exp.NewIntArray e);

  public void visit(codegen.C.exp.NewObject e);

  public void visit(codegen.C.exp.Not e);

  public void visit(codegen.C.exp.Num e);

  public void visit(codegen.C.exp.Sub e);

  public void visit(codegen.C.exp.This e);

  public void visit(codegen.C.exp.Times e);

  // statements
  public void visit(codegen.C.stm.Assign s);

  public void visit(codegen.C.stm.AssignArray s);

  public void visit(codegen.C.stm.Block s);

  public void visit(codegen.C.stm.If s);

  public void visit(codegen.C.stm.Print s);

  public void visit(codegen.C.stm.While s);

  // type
  public void visit(codegen.C.type.Class t);

  public void visit(codegen.C.type.Int t);

  public void visit(codegen.C.type.IntArray t);

  // dec
  public void visit(codegen.C.dec.Dec d);

  // method
  public void visit(codegen.C.method.Method m);

  // main method
  public void visit(codegen.C.mainMethod.MainMethod m);

  // vtable
  public void visit(codegen.C.vtable.Vtable v);

  // class
  public void visit(codegen.C.classs.Class c);

  // program
  public void visit(codegen.C.program.Program p);
}
