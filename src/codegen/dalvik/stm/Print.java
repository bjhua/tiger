package codegen.dalvik.stm;

import codegen.dalvik.Visitor;

public class Print extends T
{
  public String stream;
  public String src;
  
  public Print(String stream, String src)
  {
    this.stream = stream;
    this.src = src;
  }

  @Override
  public void accept(Visitor v)
  {
    v.visit(this);
  }
}
