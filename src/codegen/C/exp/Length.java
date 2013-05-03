package codegen.C.exp;

import codegen.C.Visitor;

public class Length extends T
{
  public T array;

  public Length(T array)
  {
    this.array = array;
  }

  @Override
  public void accept(Visitor v)
  {
    v.visit(this);
    return;
  }
}
