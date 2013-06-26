package elaborator;

import java.util.Iterator;


public class MethodTable
{
  private java.util.Hashtable<String, ast.type.T> table;
  private java.util.Hashtable<String, Boolean> used;
  private java.util.Hashtable<String, Integer> lineNum;

  public MethodTable()
  {
    this.table = new java.util.Hashtable<String, ast.type.T>();
    this.used = new java.util.Hashtable<String, Boolean>();
    this.lineNum = new java.util.Hashtable<String, Integer>();
  }

  // Duplication is not allowed
  public void put(java.util.LinkedList<ast.dec.T> formals,
      java.util.LinkedList<ast.dec.T> locals)
  {
    for (ast.dec.T dec : formals) {
      ast.dec.Dec decc = (ast.dec.Dec) dec;
      if (this.table.get(decc.id) != null) {
        System.out.println("duplicated parameter: " + decc.id);
        System.exit(1);
      }
      this.table.put(decc.id, decc.type);
      this.used.put(decc.id, false);
      this.lineNum.put(decc.id, decc.lineNum);
    }

    for (ast.dec.T dec : locals) {
      ast.dec.Dec decc = (ast.dec.Dec) dec;
      if (this.table.get(decc.id) != null) {
        System.out.println("duplicated variable: " + decc.id);
        System.exit(1);
      }
      this.table.put(decc.id, decc.type);
      this.used.put(decc.id, false);
      this.lineNum.put(decc.id, decc.lineNum);
    }

  }

  // return null for non-existing keys
  public ast.type.T get(String id)
  {
	  if (this.table.get(id) != null)
		  this.used.put(id, true);
	  return this.table.get(id);
  }
  
//check for unused variable
  public void checkUnused()
  {
	  for (Iterator<String> var = this.table.keySet().iterator(); var.hasNext();) {
		String id = (String) var.next();
		if (this.used.get(id) == false) {
			//warning
			System.out.println("Warning: variable " + id + " declared at line "
					+ this.lineNum.get(id) + " never used");
		}
	}
  }

  public void dump()
  {
	  System.out.println(this.table.toString());
  }

  @Override
  public String toString()
  {
    return this.table.toString();
  }
}
