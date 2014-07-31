package codegen.C;

import codegen.C.Ast.Type;

public class Tuple
{
  public String classs; // name of the class
  public Type.T type; // type of the field
  public String id; // name of the field or method

  public Tuple(String classs, Type.T type, String id)
  {
    this.classs = classs;
    this.type = type;
    this.id = id;
  }

  @Override
  // This is a specialized version of "equals", for
  // it compares whether the second field is equal,
  // but ignores the first field.
  public boolean equals(Object t)
  {
    if (t == null)
      return false;

    if (!(t instanceof Tuple))
      return false;

    return this.id.equals(((Tuple) t).id);
  }

}
