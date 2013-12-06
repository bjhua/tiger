package cfg.type;

import cfg.Visitor;

public class Class extends T
{
  public String id;

  public Class(String id)
  {
    this.id = id;
  }

  @Override
  public String toString()
  {
    return this.id;
  }

  @Override
  public void accept(Visitor v)
  {
    v.visit(this);
  }
}
