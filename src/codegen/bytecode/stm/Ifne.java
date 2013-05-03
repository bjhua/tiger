package codegen.bytecode.stm;

import util.Label;
import codegen.bytecode.Visitor;

public class Ifne extends T
{
  public Label l;

  public Ifne(Label l)
  {
    this.l = l;
  }

  @Override
  public void accept(Visitor v)
  {
    v.visit(this);
  }
}
