package cfg.dec;

import cfg.Visitor;

public class Dec extends T
{
  public cfg.type.T type;
  public String id;

  public Dec(cfg.type.T type, String id)
  {
    this.type = type;
    this.id = id;
  }

  @Override
  public void accept(Visitor v)
  {
    v.visit(this);
  }
}
