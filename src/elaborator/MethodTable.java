package elaborator;

import java.util.*;

import ast.Ast.Dec;
import ast.Ast.Type;
import util.Todo;

public class MethodTable
{
  public java.util.Hashtable<String, Type.T> table;
  public java.util.Hashtable<String, Integer> isuse;
  public MethodTable()
  {
    this.table = new java.util.Hashtable<String, Type.T>();
    this.isuse = new Hashtable<String, Integer>();
  }

  public void clear()
  {
	  this.table.clear();
  }
  
  // Duplication is not allowed
  public void put(LinkedList<Dec.T> formals,LinkedList<Dec.T> locals)
  {
    for (Dec.T dec : formals) 
    {
      Dec.DecSingle decc = (Dec.DecSingle) dec;
      if (this.table.get(decc.id) != null) 
      {
        System.out.println("duplicated parameter: " + decc.id);
      }
      else
      {
        this.table.put(decc.id, decc.type);
        this.isuse.put(decc.id, (Integer)decc.linenum);
      }
    }

    for (Dec.T dec : locals) 
    {
      Dec.DecSingle decc = (Dec.DecSingle) dec;
      if (this.table.get(decc.id) != null) 
      {
        System.out.println("duplicated variable: " + decc.id);
      }
      else
      {
    	this.isuse.put(decc.id, (Integer)decc.linenum);
        this.table.put(decc.id, decc.type);
      }
    }
  }

  
  // return null for non-existing keys
  public Type.T get(String id)
  {
    return this.table.get(id);
  }

  public void dump()
  {
	  Hashtable<String, Type.T> table = this.table;
	  Enumeration<String> enClass = table.keys();
	  while(enClass.hasMoreElements())
	  {
		String name = (String)enClass.nextElement();
		Type.T type = table.get(name);
		System.out.println("  variable name:"+name+"  variable type:"+type.toString());
	  }
  }

  @Override
  public String toString()
  {
    return this.table.toString();
  }
}
