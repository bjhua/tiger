package cfg.type;

import cfg.Visitor;

public class Int extends T
{
  public Int()
  {
  }

  @Override
  public String toString()
  {
    return "@int";
  }

  @Override
  public void accept(Visitor v)
  {
    v.visit(this);
  }
}
