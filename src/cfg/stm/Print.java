package cfg.stm;

import cfg.Visitor;

public class Print extends T
{
  public cfg.operand.T arg;
  
  public Print(cfg.operand.T arg)
  {
    this.arg = arg;
  }
  
  @Override
  public void accept(Visitor v)
  {
    v.visit(this);
  }
}
