package cfg.transfer;

import cfg.Visitor;

public class If extends T
{
  public cfg.operand.T operand;
  public util.Label truee;
  public util.Label falsee;
  
  public If(cfg.operand.T operand, util.Label truee, util.Label falsee)
  {
    this.operand = operand;
    this.truee = truee;
    this.falsee = falsee;
  }

  @Override
  public void accept(Visitor v)
  {
    v.visit(this);
  }
}
