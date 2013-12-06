package cfg.operand;

import cfg.Visitor;

public class Var extends T
{
  public String id;

  public Var(String id)
  {
    this.id = id;
  }

  @Override
  public void accept(Visitor v)
  {
    v.visit(this);
  }
}
