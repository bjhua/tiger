package ast;
//访问者模式
public interface Acceptable
{
  public void accept(Visitor v);
}
