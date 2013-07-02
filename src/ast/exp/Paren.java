package ast.exp;

public class Paren extends T {

	public T exp;
	
	public Paren(T exp)
	{
		this.exp = exp;
	}
	
	@Override
	  public void accept(ast.Visitor v)
	  {
	    v.visit(this);
	    return;
	  }
}
