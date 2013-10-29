package codegen.dalvik.stm;

import util.Label;
import codegen.dalvik.Visitor;

public class Ifne extends T
{
  String left, right;
  public Label l;

  public Ifne(String left, String right, Label l)
  {
    this.left = left;
    this.right = right;
    this.l = l;
  }

  @Override
  public void accept(Visitor v)
  {
    v.visit(this);
  }
}
