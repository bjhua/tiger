package codegen.C.exp;

import codegen.C.Visitor;

public class ArraySelect extends T
{
  public T array;
  public T index;

  public ArraySelect(T array, T index)
  {
    this.array = array;
    this.index = index;
  }

  @Override
  public void accept(Visitor v)
  {
    v.visit(this);
    return;
  }
}
