package ast.stm;

public class Block extends T
{
  public java.util.LinkedList<T> stms;

  public Block(java.util.LinkedList<T> stms)
  {
    this.stms = stms;
  }

  @Override
  public void accept(ast.Visitor v)
  {
    v.visit(this);
  }
}
