package elaborator;

import java.util.Hashtable;

import ast.Ast.Type;

public class ClassBinding
{
  public String extendss; // null for non-existing extends
  public java.util.Hashtable<String, Type.T> fields;
  public java.util.Hashtable<String, MethodType> methods;

  public ClassBinding(String extendss)
  {
    this.extendss = extendss;
    this.fields = new Hashtable<String, Type.T>();
    this.methods = new Hashtable<String, MethodType>();
  }

  public ClassBinding(String extendss,
      java.util.Hashtable<String, Type.T> fields,
      java.util.Hashtable<String, MethodType> methods)
  {
    this.extendss = extendss;
    this.fields = fields;
    this.methods = methods;
  }

  public void put(String xid, Type.T type)
  {
    if (this.fields.get(xid) != null) {
      System.out.println("duplicated class field: " + xid);
      System.exit(1);
    }
    this.fields.put(xid, type);
  }

  public void put(String mid, MethodType mt)
  {
    if (this.methods.get(mid) != null) {
      System.out.println("duplicated class method: " + mid);
      System.exit(1);
    }
    this.methods.put(mid, mt);
  }

  @Override
  public String toString()
  {
    System.out.print("extends: ");
    if (this.extendss != null)
      System.out.println(this.extendss);
    else
      System.out.println("<>");
    System.out.println("\nfields:\n  ");
    System.out.println(fields.toString());
    System.out.println("\nmethods:\n  ");
    System.out.println(methods.toString());

    return "";
  }

}
