package cfg.stm;

import cfg.Visitor;

public class NewObject extends T
{
  public String dst;
  // type of the destination variable
  public String c;
  
  public NewObject(String dst, String c)
  {
    this.dst = dst;
    this.c = c;
  }
  
  @Override
  public void accept(Visitor v)
  {
    v.visit(this);
  }
}
