package codegen.bytecode.program;

import codegen.bytecode.Visitor;

public class Program extends T
{
  public codegen.bytecode.mainClass.T mainClass;
  public java.util.LinkedList<codegen.bytecode.classs.T> classes;

  public Program(codegen.bytecode.mainClass.T mainClass,
      java.util.LinkedList<codegen.bytecode.classs.T> classes)
  {
    this.mainClass = mainClass;
    this.classes = classes;
  }

  @Override
  public void accept(Visitor v)
  {
    v.visit(this);
    return;
  }
}
