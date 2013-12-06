package cfg.transfer;

import cfg.Visitor;

public class Return extends T
{
  public cfg.operand.T operand;
  
  public Return(cfg.operand.T operand)
  {
    this.operand = operand;
  }

  @Override
  public void accept(Visitor v)
  {
    v.visit(this);
  }
}
