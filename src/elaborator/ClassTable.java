package elaborator;

import java.util.Iterator;


public class ClassTable
{
  // map each class name (a string), to the class bindings.
  private java.util.Hashtable<String, ClassBinding> table;

  public ClassTable()
  {
    this.table = new java.util.Hashtable<String, ClassBinding>();
  }

  // Duplication is not allowed
  public void put(String c, ClassBinding cb)
  {
    if (this.table.get(c) != null) {
      System.out.println("duplicated class: " + c);
      System.exit(1);
    }
    this.table.put(c, cb);
  }

  // put a field into this table
  // Duplication is not allowed
  public void put(String c, String id, ast.type.T type, int lineNum)
  {
    ClassBinding cb = this.table.get(c);
    cb.put(id, type, lineNum);
    return;
  }

  // put a method into this table
  // Duplication is not allowed.
  // Also note that MiniJava does NOT allow overloading.
  public void put(String c, String id, MethodType type)
  {
    ClassBinding cb = this.table.get(c);
    cb.put(id, type);
    return;
  }

  // return null for non-existing class
  public ClassBinding get(String className)
  {
    return this.table.get(className);
  }

  // get type of some field
  // return null for non-existing field.
  public ast.type.T get(String className, String xid)
  {
    ClassBinding cb = this.table.get(className);
    ast.type.T type = cb.fields.get(xid);
    cb.used.put(xid, true);
    while (type == null) { // search all parent classes until found or fail
      if (cb.extendss == null)
        return type;

      cb = this.table.get(cb.extendss);
      type = cb.fields.get(xid);
    }
    return type;
  }

  // get type of some method
  // return null for non-existing method
  public MethodType getm(String className, String mid)
  {
    ClassBinding cb = this.table.get(className);
    MethodType type = cb.methods.get(mid);
    while (type == null) { // search all parent classes until found or fail
      if (cb.extendss == null)
        return type;

      cb = this.table.get(cb.extendss);
      type = cb.methods.get(mid);
    }
    return type;
  }

  public void dump()
  {
	  System.out.println("\nclass table dump infomation:");
	  for (Iterator<String> classNames = this.table.keySet().iterator(); 
			  classNames.hasNext();) {
		  String className = (String) classNames.next();
		  ClassBinding cb = this.table.get(className);
		  System.out.println("\nclass " + className + ":");
		  cb.toString();
		  }
  }

  @Override
  public String toString()
  {
    return this.table.toString();
  }
}
