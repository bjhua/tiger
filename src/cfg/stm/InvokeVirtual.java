package cfg.stm;

import cfg.Visitor;

public class InvokeVirtual extends T
{
  public String dst;
  public String obj;
  public String f;
  // type of the destination variable
  public java.util.LinkedList<cfg.operand.T> args;
  
  public InvokeVirtual(String dst, String obj, String f
      , java.util.LinkedList<cfg.operand.T> args)
  {
    this.dst = dst;
    this.obj = obj;
    this.f = f;
    this.args = args;
  }
  
  @Override
  public void accept(Visitor v)
  {
    v.visit(this);
  }
}
