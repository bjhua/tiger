package codegen.dalvik.stm;

import codegen.dalvik.Visitor;

public class ReturnObject extends T
{
  public String src;
  
  public ReturnObject(String src)
  {
    this.src = src;
  }

  @Override
  public void accept(Visitor v)
  {
    v.visit(this);
  }
}
