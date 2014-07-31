package codegen.C;

import codegen.C.Ast.Dec;
import codegen.C.Ast.Type;

public class ClassTable
{
  private java.util.Hashtable<String, ClassBinding> table;

  public ClassTable()
  {
    this.table = new java.util.Hashtable<String, ClassBinding>();
  }

  public void init(String current, String extendss)
  {
    this.table.put(current, new ClassBinding(extendss));
    return;
  }

  public void initDecs(String current,
      java.util.LinkedList<Dec.T> decs)
  {
    ClassBinding cb = this.table.get(current);
    for (Dec.T dec : decs) {
      Dec.DecSingle decc = (Dec.DecSingle) dec;
      cb.put(current, decc.type, decc.id);
    }
    this.table.put(current, cb);
  }

  public void initMethod(String current, Type.T ret,
      java.util.LinkedList<Dec.T> args, String mid)
  {
    ClassBinding cb = this.table.get(current);
    cb.putm(current, ret, args, mid);
    return;
  }

  public void inherit(String c)
  {
    ClassBinding cb = this.table.get(c);
    if (cb.visited)
      return;

    if (cb.extendss == null) {
      cb.visited = true;
      return;
    }

    inherit(cb.extendss);

    ClassBinding pb = this.table.get(cb.extendss);
    // this tends to be very slow...
    // need a much fancier data structure.
    java.util.LinkedList<Tuple> newFields = new java.util.LinkedList<Tuple>();
    newFields.addAll(pb.fields);
    newFields.addAll(cb.fields);
    cb.update(newFields);
    // methods;
    java.util.ArrayList<Ftuple> newMethods = new java.util.ArrayList<Ftuple>();
    newMethods.addAll(pb.methods);
    for (codegen.C.Ftuple t : cb.methods) {
      int index = newMethods.indexOf(t);
      if (index == -1) {
        newMethods.add(t);
        continue;
      }
      newMethods.set(index, t);
    }
    cb.update(newMethods);
    // set the mark
    cb.visited = true;
    return;
  }

  // return null for non-existing keys
  public ClassBinding get(String c)
  {
    return this.table.get(c);
  }

  @Override
  public String toString()
  {
    return this.table.toString();
  }
}
