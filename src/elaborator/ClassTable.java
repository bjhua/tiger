package elaborator;

import java.util.*;

import ast.Ast.Dec;
import ast.Ast.Type;
import util.Todo;

public class ClassTable
{
  // map each class name (a string), to the class bindings.
  public java.util.Hashtable<String, ClassBinding> table;
  public java.util.Hashtable<String, Integer> isuse;
  public ClassTable()
  {
    this.table = new java.util.Hashtable<String, ClassBinding>();
  }

  // Duplication is not allowed
  public void put(String c, ClassBinding cb)
  {
    if (this.table.get(c) != null) {
      System.out.println("duplicated class: " + c);
    }
    else
      this.table.put(c, cb);
  }

  // put a field into this table
  // Duplication is not allowed
  public void put(String c, String id, Type.T type)
  {
    ClassBinding cb = this.table.get(c);
    cb.put(id, type);
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
  public Type.T get(String className, String xid)
  {
    ClassBinding cb = this.table.get(className);
    Type.T type = cb.fields.get(xid);
    while (type == null) 
    { // search all parent classes until found or fail
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
    if(cb==null)
    {
    	System.out.println("Error: "+className+" is an undefined class name.");
    	return null;
    }
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
	 Hashtable<String, ClassBinding> x = this.table;
	 Enumeration enClass = x.keys();
	 while(enClass.hasMoreElements())
	 {
		 String classid = (String)enClass.nextElement();
		 ClassBinding eachClassBind = x.get(classid);
		 System.out.print("Class "+classid+" ");
	     if (eachClassBind.extendss != null)
	     {
	       System.out.print("extends: ");
	       System.out.println(eachClassBind.extendss);
	     }
	     System.out.println("\nfields: ");
	     outputField(eachClassBind.fields);
	     System.out.println("\nmethods:  ");
	     outputMethod(eachClassBind.methods);
	     System.out.println("");
	 }
  }

  public void outputField(Hashtable<String, Type.T> fields)
  {
	 System.out.println("{");
     Enumeration enField = fields.keys();
	 while(enField.hasMoreElements())
	 {
		 String classid = (String)enField.nextElement();
		 Type.T eachClassField = fields.get(classid);
		 System.out.println("  variable name:"+classid+" Type:"+eachClassField.toString());
	 }
	 System.out.println("}");
  }
  
  public void outputMethod(Hashtable<String, MethodType> methods)
  {
	  System.out.println("{");
	  Enumeration enMethod = methods.keys();
	  while(enMethod.hasMoreElements())
	  {
		  String Methodid = (String)enMethod.nextElement();
		  MethodType eachMethodType = methods.get(Methodid);
		  LinkedList<Dec.T> argsType = eachMethodType.argsType;
		  System.out.print("  Method name:"+Methodid);
		  System.out.println(" return type:"+eachMethodType.retType);
		  for(Dec.T r:argsType)
		  {
			  Dec.DecSingle res = (Dec.DecSingle)r;
			  System.out.println("  variable name:"+res.id+" variable type:"+res.type);
		  }
	  }
	  System.out.println("}");
  }
  
  @Override
  public String toString()
  {
    return this.table.toString();
  }
}
