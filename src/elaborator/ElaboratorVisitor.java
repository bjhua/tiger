package elaborator;


public class ElaboratorVisitor implements ast.Visitor
{
  public ClassTable classTable; // symbol table for class
  public MethodTable methodTable; // symbol table for each method
  public String currentClass; // the class name being elaborated
  public ast.type.T type; // type of the expression being elaborated

  public ElaboratorVisitor()
  {
    this.classTable = new ClassTable();
    this.methodTable = new MethodTable();
    this.currentClass = null;
    this.type = null;
  }

  private void error()
  {
    System.out.println("type mismatch");
    System.exit(1);
  }

  // /////////////////////////////////////////////////////
  // expressions
  @Override
  public void visit(ast.exp.Add e)
  {
	  e.left.accept(this);
	  if (e.right != null) {
		  ast.type.T leftty = this.type;
		  e.right.accept(this);
		  if (!this.type.toString().equals(leftty.toString()))
			  error();
		  this.type = new ast.type.Int();
	  }
	  return;
  }

  @Override
  public void visit(ast.exp.And e)
  {
	  e.left.accept(this);
	  if (e.right != null) {
		  if (!this.type.toString().equals("@boolean"))
			  error();
		  e.right.accept(this);
		  if (!this.type.toString().equals("@boolean"))
			  error();
		  this.type = new ast.type.Boolean();
	  }
	return;
  }

  @Override
  public void visit(ast.exp.ArraySelect e)
  {
	  e.array.accept(this);
	  if (!this.type.toString().equals("@int[]"))
		  error();
	  e.index.accept(this);
	  if (!this.type.toString().equals("@int"))
		  error();
	  this.type = new ast.type.Int();
	  return;
  }

  @Override
  public void visit(ast.exp.Call e)
  {
    ast.type.T leftty;
    ast.type.Class ty = null;

    e.exp.accept(this);
    leftty = this.type;
    if (leftty instanceof ast.type.Class) {
      ty = (ast.type.Class) leftty;
      e.type = ty.id;
    } else
      error();
    MethodType mty = this.classTable.getm(ty.id, e.id);
    java.util.LinkedList<ast.type.T> argsty = new java.util.LinkedList<ast.type.T>();
    for (ast.exp.T a : e.args) {
      a.accept(this);
      argsty.addLast(this.type);
    }
    if (mty.argsType.size() != argsty.size())
      error();
    this.type = mty.retType;
    e.at = argsty;
    e.rt = this.type;
    for (int i = 0; i < argsty.size(); i++) {
      ast.dec.Dec dec = (ast.dec.Dec) mty.argsType.get(i);
      ast.type.T argT = argsty.get(i);
      if (!dec.type.toString().equals(argT.toString())) {
      	if (argT instanceof ast.type.Class && dec.type instanceof ast.type.Class) {
      		ClassBinding cb = this.classTable.get(argT.toString());
  			while (cb.extendss != null) {
  				if (dec.type.toString().equals(cb.extendss))
  					return;
  				cb = this.classTable.get(cb.extendss);
  			}
  			error();
      	}
      	else error();
      }
    }
    return;
  }

  @Override
  public void visit(ast.exp.False e)
  {
	  this.type = new ast.type.Boolean();
	  return;
  }

  @Override
  public void visit(ast.exp.Id e)
  {
    // first look up the id in method table
    ast.type.T type = this.methodTable.get(e.id);
    // if search failed, then s.id must be a class field.
    if (type == null) {
      type = this.classTable.get(this.currentClass, e.id);
      // mark this id as a field id, this fact will be
      // useful in later phase.
      e.isField = true;
    }
    if (type == null)
      error();
    this.type = type;
    // record this type on this node for future use.
    e.type = type;
    return;
  }

  @Override
  public void visit(ast.exp.Length e)
  {
	  e.array.accept(this);
	  if (!this.type.toString().equals("@int[]"))
		  error();
	  this.type = new ast.type.Int();
	  return;
  }

  @Override
  public void visit(ast.exp.Lt e)
  {
    e.left.accept(this);
    if (e.right != null) {
		ast.type.T ty = this.type;
		e.right.accept(this);
		if (!this.type.toString().equals(ty.toString()))
			error();
		this.type = new ast.type.Boolean();
	}
	return;
  }

  @Override
  public void visit(ast.exp.NewIntArray e)
  {
	  this.type = new ast.type.IntArray();
	  return;
  }

  @Override
  public void visit(ast.exp.NewObject e)
  {
    this.type = new ast.type.Class(e.id);
    return;
  }

  @Override
  public void visit(ast.exp.Not e)
  {
	  e.exp.accept(this);
	  if (!this.type.toString().equals("@boolean"))
		  error();
	  this.type = new ast.type.Boolean();
  }

  @Override
  public void visit(ast.exp.Num e)
  {
    this.type = new ast.type.Int();
    return;
  }

  @Override
  public void visit(ast.exp.Sub e)
  {
    e.left.accept(this);
    if (e.right != null) {
		ast.type.T leftty = this.type;
		e.right.accept(this);
		if (!this.type.toString().equals(leftty.toString()))
			error();
		this.type = new ast.type.Int();
	}
	return;
  }

  @Override
  public void visit(ast.exp.This e)
  {
    this.type = new ast.type.Class(this.currentClass);
    return;
  }

  @Override
  public void visit(ast.exp.Times e)
  {
    e.left.accept(this);
    if (e.right != null) {
		ast.type.T leftty = this.type;
		e.right.accept(this);
		if (!this.type.toString().equals(leftty.toString()))
			error();
		this.type = new ast.type.Int();
	}
	return;
  }

  @Override
  public void visit(ast.exp.True e)
  {
	  this.type = new ast.type.Boolean();
	  return;
  }

  // statements
  @Override
  public void visit(ast.stm.Assign s)
  {
    // first look up the id in method table
    ast.type.T type = this.methodTable.get(s.id);
    // if search failed, then s.id must
    if (type == null)
      type = this.classTable.get(this.currentClass, s.id);
    if (type == null)
      error();
    s.exp.accept(this);
    s.type = type;
    if (!this.type.toString().equals(type.toString())) {
    	if (type instanceof ast.type.Class) {
    		ClassBinding cb = this.classTable.get(s.id);
			while (cb.extendss != null) {
				cb = this.classTable.get(cb.extendss);
			    type = cb.fields.get(s.id);
				if (type != null && this.type.toString().equals(type.toString()))
					return;
			}
			error();
    	}
    	else error();
    }
    return;
  }

  @Override
  public void visit(ast.stm.AssignArray s)
  {
	  ast.type.T type = this.methodTable.get(s.id);
	  if (type == null)
		  type = this.classTable.get(this.currentClass, s.id);
	  if (type == null)
		  error();
	  if (!type.toString().equals("@int[]"))
		  error();
	  s.index.accept(this);
	  if (!this.type.toString().equals("@int"))
		  error();
	  s.exp.accept(this);
	  return;
  }

  @Override
  public void visit(ast.stm.Block s)
  {
	  for (ast.stm.T stm : s.stms)
		  stm.accept(this);
	  return;
  }

  @Override
  public void visit(ast.stm.If s)
  {
    s.condition.accept(this);
    if (!this.type.toString().equals("@boolean"))
      error();
    s.thenn.accept(this);
    s.elsee.accept(this);
    return;
  }

  @Override
  public void visit(ast.stm.Print s)
  {
    s.exp.accept(this);
    if (!this.type.toString().equals("@int"))
      error();
    return;
  }

  @Override
  public void visit(ast.stm.While s)
  {
	  s.condition.accept(this);
	  if (!this.type.toString().equals("@boolean"))
		  error();
	  s.body.accept(this);
	  return;
  }

  // type
  @Override
  public void visit(ast.type.Boolean t)
  {
  }

  @Override
  public void visit(ast.type.Class t)
  {
  }

  @Override
  public void visit(ast.type.Int t)
  {
    System.out.println("aaaa");
  }

  @Override
  public void visit(ast.type.IntArray t)
  {
  }

  // dec
  @Override
  public void visit(ast.dec.Dec d)
  {
	  
  }

  // method
  @Override
  public void visit(ast.method.Method m)
  {
    // construct the method table
    this.methodTable = new MethodTable();
    this.methodTable.put(m.formals, m.locals);

    if (control.Control.elabMethodTable) {
    	System.out.println("\n" + "method table dump infomation of " + 
    			m.id + "()\n");
        this.methodTable.dump();
    }

    for (ast.stm.T s : m.stms)
      s.accept(this);
    m.retExp.accept(this);
    return;
  }

  // class
  @Override
  public void visit(ast.classs.Class c)
  {
    this.currentClass = c.id;

    for (ast.method.T m : c.methods) {
      m.accept(this);
    }
    return;
  }

  // main class
  @Override
  public void visit(ast.mainClass.MainClass c)
  {
    this.currentClass = c.id;
    // "main" has an argument "arg" of type "String[]", but
    // one has no chance to use it. So it's safe to skip it...

    c.stm.accept(this);
    return;
  }

  // ////////////////////////////////////////////////////////
  // step 1: build class table
  // class table for Main class
  private void buildMainClass(ast.mainClass.MainClass main)
  {
    this.classTable.put(main.id, new ClassBinding(null));
  }

  // class table for normal classes
  private void buildClass(ast.classs.Class c)
  {
    this.classTable.put(c.id, new ClassBinding(c.extendss));
    for (ast.dec.T dec : c.decs) {
      ast.dec.Dec d = (ast.dec.Dec) dec;
      this.classTable.put(c.id, d.id, d.type);
    }
    for (ast.method.T method : c.methods) {
      ast.method.Method m = (ast.method.Method) method;
      this.classTable.put(c.id, m.id, new MethodType(m.retType, m.formals));
    }
  }

  // step 1: end
  // ///////////////////////////////////////////////////

  // program
  @Override
  public void visit(ast.program.Program p)
  {
    // ////////////////////////////////////////////////
    // step 1: build a symbol table for class (the class table)
    // a class table is a mapping from class names to class bindings
    // classTable: className -> ClassBinding{extends, fields, methods}
    buildMainClass((ast.mainClass.MainClass) p.mainClass);
    for (ast.classs.T c : p.classes) {
      buildClass((ast.classs.Class) c);
    }

    // we can double check that the class table is OK!
    if (control.Control.elabClassTable) {
      this.classTable.dump();
    }

    // ////////////////////////////////////////////////
    // step 2: elaborate each class in turn, under the class table
    // built above.
    p.mainClass.accept(this);
    for (ast.classs.T c : p.classes) {
      c.accept(this);
    }

  }
}
