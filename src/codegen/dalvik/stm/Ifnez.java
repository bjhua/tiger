package codegen.dalvik.stm;

import util.Label;
import codegen.dalvik.Visitor;

public class Ifnez extends T
{
  public String left;
  public Label l;

  public Ifnez(String left, Label l)
  {
    this.left = left;
    this.l = l;
  }

  @Override
  public void accept(Visitor v)
  {
    v.visit(this);
  }
}
