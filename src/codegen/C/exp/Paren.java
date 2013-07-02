package codegen.C.exp;

import codegen.C.Visitor;

public class Paren extends T {

	public T exp;
	
	public Paren(T exp) {
		this.exp = exp;
	}
	
  @Override
  public void accept(Visitor v)
  {
    v.visit(this);
    return;
  }

}
