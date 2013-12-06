package cfg.mainMethod;

import cfg.Visitor;

public class MainMethod extends T
{
  public java.util.LinkedList<cfg.dec.T> locals;
  public java.util.LinkedList<cfg.block.T> blocks;

  public MainMethod(java.util.LinkedList<cfg.dec.T> locals,
      java.util.LinkedList<cfg.block.T> blocks)
  {
    this.locals = locals;
    this.blocks = blocks;
  }

  @Override
  public void accept(Visitor v)
  {
    v.visit(this);
    return;
  }

}
