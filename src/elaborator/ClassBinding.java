package elaborator;

public class ClassBinding
{
  public String extendss; // null for non-existing extends
  public java.util.Hashtable<String, ast.type.T> fields;
  public java.util.Hashtable<String, MethodType> methods;
  public java.util.Hashtable<String, Boolean> used;
  public java.util.Hashtable<String, Integer> lineNum;

  public ClassBinding(String extendss)
  {
    this.extendss = extendss;
    this.fields = new java.util.Hashtable<String, ast.type.T>();
    this.methods = new java.util.Hashtable<String, MethodType>();
    this.used = new java.util.Hashtable<String, Boolean>();
    this.lineNum = new java.util.Hashtable<String, Integer>();
  }

  public ClassBinding(String extendss,
      java.util.Hashtable<String, ast.type.T> fields,
      java.util.Hashtable<String, MethodType> methods)
  {
    this.extendss = extendss;
    this.fields = fields;
    this.methods = methods;
    this.used = new java.util.Hashtable<String, Boolean>();
    this.lineNum = new java.util.Hashtable<String, Integer>();
  }

  public void put(String xid, ast.type.T type, int lineNum)
  {
    if (this.fields.get(xid) != null) {
      System.out.println("duplicated class field: " + xid);
      System.exit(1);
    }
    this.fields.put(xid, type);
    this.used.put(xid, false);
    this.lineNum.put(xid, new Integer(lineNum));
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
