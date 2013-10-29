package codegen.dalvik.stm;

import codegen.dalvik.Visitor;

public class Return extends T
{
  String src;
  
  public Return(String src)
  {
    this.src = src;
  }

  @Override
  public void accept(Visitor v)
  {
    v.visit(this);
  }
}
