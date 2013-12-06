package cfg.transfer;

import cfg.Visitor;

public class Goto extends T
{
  public util.Label label;
  
  public Goto(util.Label label)
  {
    this.label = label;
  }

  @Override
  public void accept(Visitor v)
  {
    v.visit(this);
  }
}
