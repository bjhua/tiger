package codegen.dalvik.stm;

import util.Label;
import codegen.dalvik.Visitor;

public class Goto32 extends T
{
  public Label l;

  public Goto32(Label l)
  {
    this.l = l;
  }

  @Override
  public void accept(Visitor v)
  {
    v.visit(this);
  }
}
