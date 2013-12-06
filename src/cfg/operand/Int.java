package cfg.operand;

import cfg.Visitor;

public class Int extends T
{
  public int i;
  
  public Int(int i)
  {
    this.i = i;
  }

  @Override
  public void accept(Visitor v)
  {
    v.visit(this);
  }
}
