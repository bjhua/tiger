package cfg.program;

import cfg.Visitor;

public class Program extends T
{
  public java.util.LinkedList<cfg.classs.T> classes;
  public java.util.LinkedList<cfg.vtable.T> vtables;
  public java.util.LinkedList<cfg.method.T> methods;
  public cfg.mainMethod.T mainMethod;

  public Program(java.util.LinkedList<cfg.classs.T> classes,
      java.util.LinkedList<cfg.vtable.T> vtables,
      java.util.LinkedList<cfg.method.T> methods,
      cfg.mainMethod.T mainMethod)
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
