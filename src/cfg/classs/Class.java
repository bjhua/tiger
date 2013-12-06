package cfg.classs;

import cfg.Visitor;

public class Class extends T
{
  public String id;
  public java.util.LinkedList<cfg.Tuple> decs;

  public Class(String id, java.util.LinkedList<cfg.Tuple> decs)
  {
    this.id = id;
    this.decs = decs;
  }

  @Override
  public void accept(Visitor v)
  {
    v.visit(this);
  }

}
