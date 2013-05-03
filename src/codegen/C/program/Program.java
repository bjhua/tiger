package codegen.C.program;

import codegen.C.Visitor;

public class Program extends T
{
  public java.util.LinkedList<codegen.C.classs.T> classes;
  public java.util.LinkedList<codegen.C.vtable.T> vtables;
  public java.util.LinkedList<codegen.C.method.T> methods;
  public codegen.C.mainMethod.T mainMethod;

  public Program(java.util.LinkedList<codegen.C.classs.T> classes,
      java.util.LinkedList<codegen.C.vtable.T> vtables,
      java.util.LinkedList<codegen.C.method.T> methods,
      codegen.C.mainMethod.T mainMethod)
  {
    this.classes = classes;
    this.vtables = vtables;
    this.methods = methods;
    this.mainMethod = mainMethod;
  }

  @Override
  public void accept(Visitor v)
  {
    v.visit(this);
    return;
  }
}
