package codegen.dalvik.stm;

import codegen.dalvik.Visitor;

public class Mulint extends T
{
  public String dst, src1, src2;
  
  public Mulint(String dst, String src1, String src2)
  {
    this.dst = dst;
    this.src1 = src1;
    this.src2 = src2;
  }

  @Override
  public void accept(Visitor v)
  {
    v.visit(this);
  }
}
