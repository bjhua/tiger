package codegen.C.exp;

import codegen.C.Visitor;

public class Id extends T
{
  public String id;
  public boolean isField;

  public Id(String id, boolean isField)
  {
    this.id = id;
    this.isField = isField;
  }

  @Override
  public void accept(Visitor v)
  {
    v.visit(this);
    return;
  }
}
