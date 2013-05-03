package codegen.bytecode.stm;

import util.Label;
import codegen.bytecode.Visitor;

public class Ificmplt extends T
{
  public Label l;

  public Ificmplt(Label l)
  {
    this.l = l;
  }

  @Override
  public void accept(Visitor v)
  {
    v.visit(this);
  }
}
