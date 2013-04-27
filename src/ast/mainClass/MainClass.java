package ast.mainClass;

import ast.Visitor;

public class MainClass extends T
{
  public String id;
  public String arg;
  public ast.stm.T stm;

  public MainClass(String id, String arg, ast.stm.T stm)
  {
    this.id = id;
    this.arg = arg;
    this.stm = stm;
  }

  @Override
  public void accept(Visitor v)
  {
    v.visit(this);
    return;
  }

}
