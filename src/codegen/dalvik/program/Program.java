package codegen.dalvik.program;

import codegen.dalvik.Visitor;

public class Program extends T
{
  public codegen.dalvik.mainClass.T mainClass;
  public java.util.LinkedList<codegen.dalvik.classs.T> classes;

  public Program(codegen.dalvik.mainClass.T mainClass,
      java.util.LinkedList<codegen.dalvik.classs.T> classes)
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
