package codegen.C.exp;

import codegen.C.Visitor;

public class NewIntArray extends T
{
  public T exp;
  // Lab4, exercise 1: this field
  // is used to name the allocation.
  public String name;

  public NewIntArray(T exp)
  {
    this.exp = exp;
  }

  @Override
  public void accept(Visitor v)
  {
    v.visit(this);
    return;
  }
}
