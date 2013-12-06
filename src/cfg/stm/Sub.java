package cfg.stm;

import cfg.Visitor;

public class Sub extends T
{
  public String dst;
  // type of the destination variable
  public cfg.type.T ty;
  public cfg.operand.T left;
  public cfg.operand.T right;

  public Sub(String dst, cfg.type.T ty, cfg.operand.T left, cfg.operand.T right)
  {
    this.dst = dst;
    this.ty = ty;
    this.left = left;
    this.right = right;
  }

  @Override
  public void accept(Visitor v)
  {
    v.visit(this);
  }
}
