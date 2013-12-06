package cfg.vtable;

import cfg.Visitor;

public class Vtable extends T
{
  public String id; // name of the class
  public java.util.LinkedList<cfg.Ftuple> ms; // all methods

  public Vtable(String id, java.util.LinkedList<cfg.Ftuple> ms)
  {
    this.id = id;
    this.ms = ms;
  }

  @Override
  public void accept(Visitor v)
  {
    v.visit(this);
  }
}
