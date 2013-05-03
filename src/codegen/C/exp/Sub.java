package codegen.C.exp;

import codegen.C.Visitor;

public class Sub extends T
{
  public T left;
  public T right;

  public Sub(T left, T right)
  {
    this.left = left;
    this.right = right;
  }

  @Override
  public void accept(Visitor v)
  {
    v.visit(this);
    return;
  }
}
