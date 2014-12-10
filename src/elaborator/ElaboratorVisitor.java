package elaborator;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;

import ast.Ast;
import ast.Ast.Class;
import ast.Ast.Exp.False;
import ast.Ast.Exp.Id;
import ast.Ast.Exp.Length;
import ast.Ast.Exp.Lt;
import ast.Ast.Exp.NewIntArray;
import ast.Ast.Exp.NewObject;
import ast.Ast.Exp.Not;
import ast.Ast.Exp.Num;
import ast.Ast.Exp.Sub;
import ast.Ast.Exp.This;
import ast.Ast.Exp.Times;
import ast.Ast.Exp.True;
import ast.Ast.MainClass;
import ast.Ast.Class.ClassSingle;
import ast.Ast.Dec;
import ast.Ast.Dec.DecSingle;
import ast.Ast.Exp;
import ast.Ast.Exp.Add;
import ast.Ast.Exp.And;
import ast.Ast.Exp.ArraySelect;
import ast.Ast.Exp.Call;
import ast.Ast.Method;
import ast.Ast.Method.MethodSingle;
import ast.Ast.Program.ProgramSingle;
import ast.Ast.Stm;
import ast.Ast.Stm.Assign;
import ast.Ast.Stm.AssignArray;
import ast.Ast.Stm.Block;
import ast.Ast.Stm.If;
import ast.Ast.Stm.Print;
import ast.Ast.Stm.While;
import ast.Ast.Type;
import ast.Ast.Type.Error;
import ast.Ast.Type.ClassType;
import control.Control.ConAst;

public class ElaboratorVisitor implements ast.Visitor
{
  public ClassTable classTable; // symbol table for class
  public MethodTable methodTable; // symbol table for each method
  public String currentClass; // the class name being elaborated
  public Type.T type; // type of the expression being elaborated
  public Hashtable<String, Type.T> usedVarTable = new Hashtable<String, Type.T>(); // symbol table for each method
  public Hashtable<String, Integer> varTable = new Hashtable<String, Integer>(); // symbol table for each method
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
  public void visit(Add e)
  {
	 e.left.accept(this);
	 Type.T leftty = this.type;
	 e.right.accept(this);
	 Type.T rightty = this.type;
	
	 if(!this.type.toString().equals(leftty.toString()))
	 {
		if(leftty.getNum()==3 || rightty.getNum()==3)
		{
			this.type = new Type.Int();
    		return;
		}
		System.out.println("Error: The operator + is undefined for the argument type(s) "+leftty.toString()+", "+rightty.toString()+" at line "+e.linenum);	
	    this.type = new Type.Int();
	    return;
	 }
	 this.type = new Type.Int();
	 return;
  }

  @Override
  public void visit(And e)
  {
	 e.left.accept(this);
	 Type.T leftty = this.type;
	 
	 if(!(leftty instanceof Type.Boolean))
	 {
		System.out.println("Error: the operand isn't a boolean type at line "+e.linenum);
		this.type = new Type.Boolean();
		return;
	 }
	 e.right.accept(this);
	 if(!this.type.toString().equals(leftty.toString()))
	 {
		System.out.println("Error: The operator && is undefined for the argument type(s) "+leftty.toString()+", "+this.type.toString());	
		this.type = new Type.Boolean();
	 }
	 return ;
  }

  @Override
  public void visit(ArraySelect e)
  {
	 e.array.accept(this);
	 e.index.accept(this);
	 if(!(this.type instanceof Type.Int))
	 {
		System.out.println("Error: ArraySelect error at line "+e.linenum);
		this.type = new Type.Int();
	 }
	 return ;
  }

  @Override
  public void visit(Call e)
  {
    Type.T leftty;
    Type.ClassType ty = null;

    e.exp.accept(this);
    leftty = this.type;
    if(leftty.getNum()==3)
    {
    	this.type = new Error();
        return ;
    }
    if (leftty instanceof ClassType) 
    {
      ty = (ClassType) leftty;
      e.type = ty.id;
    } 
    else
    {
      System.out.println("Error: there is an illegal classtype using call method at Line "+e.linenum+".");
      System.exit(1);
    }
    if(e.id==null || ty.id==null)
    {
    	System.out.println("Error: Call error");
    	error();
    }
    MethodType mty = this.classTable.getm(ty.id, e.id);
    if(mty==null)
    {   
    	System.out.println("Error: The method "+ e.id +" is undefined for the Class "+ty.id +" at Line "+e.linenum+".");
        this.type = new Error();
        return ;
    }
    java.util.LinkedList<Type.T> argsty = new LinkedList<Type.T>();
    for (Exp.T a : e.args) 
    {
      a.accept(this);
      argsty.addLast(this.type);
    }
    if (mty.argsType.size() != argsty.size())
    {
      System.out.println("Error: The method "+ e.id +"() is not applicable for the arguments at Line "+e.linenum+".");
      System.exit(1);
    }
    for (int i = 0; i < argsty.size(); i++) 
    {
      Dec.DecSingle dec = (Dec.DecSingle) mty.argsType.get(i);
      if (!(dec.type.toString().equals(argsty.get(i).toString())))
      {
    	  System.out.println("Error: The method "+ e.id +"() is not applicable for the arguments at Line "+e.linenum+".");
    	  this.type = mty.retType;
    	  e.at = argsty;
    	  e.rt = this.type;
    	  return;
      }
    }
    this.type = mty.retType;
    e.at = argsty;
    e.rt = this.type;
    return;
  }
  
  @Override
  public void visit(Error e)
  {
	  this.type = new Error();
	  return;
  }
  
  @Override
  public void visit(False e)
  {
	  this.type = new Type.Boolean();
	  return;
  }

  @Override
  public void visit(Id e)
  {
    // first look up the id in method table
    Type.T type = this.methodTable.get(e.id);
    // if search failed, then s.id must be a class field.
    if (type == null) {
      type = this.classTable.get(this.currentClass, e.id);
      // mark this id as a field id, this fact will be
      // useful in later phase.
      e.isField = true;
    }
    if (type == null)
    {                                   
      System.out.println("Error: "+e.id+" cannot be resolved to a variable at Line "+e.linenum+".");
      this.type = new Error();
      return;
    }
    e.isuse++;
    if(e.isuse==1)
      this.usedVarTable.put(e.id, type);             //insert in to the usedVarTable
    // record this type on this node for future use.
    e.type = type;
    this.type = type;
    return;
  }

  @Override
  public void visit(Length e)
  {
	 e.array.accept(this);
	 if(!(this.type instanceof Type.Int))
	 {
		 System.out.println("Error: length error at Line "+e.linenum+".");
		 this.type = new Type.Int();
	 }
  }

  @Override
  public void visit(Lt e)
  {
    e.left.accept(this);
    Type.T ty = this.type;
    e.right.accept(this);
    Type.T ty2 = this.type;
    if (!this.type.toString().equals(ty.toString()))
    {
    	if(ty.getNum()==3||ty2.getNum()==3)
    	{
    		this.type = new Type.Boolean();
    		return;
    	} 
    	System.out.println("Error: The opeartor < is undefined for the the argument type(s) "
    						+ty.toString()+", "+ty2.toString()+" at Line "+e.linenum+".");
    	this.type = new Type.Boolean();
    	return;
    }
    this.type = new Type.Boolean();
    return;
  }

  @Override
  public void visit(NewIntArray e)
  {
	  e.exp.accept(this);
	  if(!(this.type instanceof Type.Int))
	  {
	    System.out.println("Error: type mismatch cannot convert from "+ this.type +" to int at Line "+e.linenum+".");
	  }
	  this.type = new Type.IntArray();
  }
  @Override
  public void visit(NewObject e)
  {
    this.type = new Type.ClassType(e.id);
    return;
  }

  @Override
  public void visit(Not e)
  {
	  e.exp.accept(this);
	  if(!(this.type instanceof Type.Boolean))
	  {
		System.out.println("Error: type mismatch cannot convert from "+ this.type +" to boolean at Line "+e.linenum+".");
	  }
	  this.type = new Type.Boolean();
  }

  @Override
  public void visit(Num e)
  {
    this.type = new Type.Int();
    return;
  }

  @Override
  public void visit(Sub e)
  {
	 e.left.accept(this);
	 Type.T leftty = this.type;
	 e.right.accept(this);
	 Type.T rightty = this.type;
	 if(!this.type.toString().equals(leftty.toString()))
	 {
		if(leftty.getNum()==3 || rightty.getNum()==3)
		{
			this.type = new Type.Int();
    		return;
		}
		System.out.println("Error: The operator - is undefined for the argument type(s) "
						+leftty.toString()+", "+rightty.toString()+" at Line "+e.linenum+".");	
	    this.type = new Type.Int();
	    return;
	 }
	 this.type = new Type.Int();
	 return;
  }

  @Override
  public void visit(This e)
  {
    this.type = new Type.ClassType(this.currentClass);
    return;
  }

  @Override
  public void visit(Times e)
  {
    e.left.accept(this);
    Type.T leftty = this.type;
    e.right.accept(this);
    if (!this.type.toString().equals(leftty.toString()))
    {
       System.out.println("Error: the type of two operands isn't match at Line "+e.linenum+".");	
    }
    this.type = new Type.Int();
    return;
  }

  @Override
  public void visit(True e)
  {
	  this.type = new Type.Boolean();
	  return;
  }

  // statements
  @Override
  public void visit(Assign s)
  {
    // first look up the id in method table
    Type.T type = this.methodTable.get(s.id);
    // if search failed, then s.id must
    if (type == null)
       type = this.classTable.get(this.currentClass, s.id);
    if (type == null)
    {
        System.out.println("Error: "+s.id+" cannot be resolved to a variable at Line "+s.linenum+".");
        this.type = new Error();
        return;
    }
    this.usedVarTable.put(s.id, type);
    s.exp.accept(this);
    if(!(this.type.toString().equals(type.toString())))
    {
    	if(type.getNum()==3||this.type.getNum()==3)
    	{
    		this.type = type;
    		return;
    	} 
    	System.out.println("Error: the "+type.toString()+" and "+this.type.toString()+" is not match");
    }
    this.type = type;
    return;
  }

  @Override
  public void visit(AssignArray s)
  {
	Type.T type = this.methodTable.get(s.id);
	if(type == null)
		type = this.classTable.get(this.currentClass, s.id);
	if(type == null)
	{
		System.out.println("Error: "+s.id+" is undefined at Line "+s.linenum+".");
		this.type = new Type.IntArray();
		return;
	}
	s.index.accept(this);
	if(!(this.type instanceof Type.Int))
		error();
	s.exp.accept(this);
	this.type = new Type.IntArray();
  }

  @Override
  public void visit(Block s)
  {
	  LinkedList<Stm.T> stms = s.stms;
	  for(Stm.T res:stms)
	  {
		  res.accept(this);
	  }
	  return;
  }

  @Override
  public void visit(If s)
  {
    s.condition.accept(this);
    if(this.type.getNum()==3)
    {
        this.type = new Type.Boolean();
    }
    if (!this.type.toString().equals("@boolean"))
    {
	   System.out.println("Error: type mismatch, cannot convert from "+ this.type.toString() +" to boolean at Line "+s.linenum+".");
	   this.type = new Type.Int();
	}
    s.thenn.accept(this);
    s.elsee.accept(this);
    return;
  }

  @Override
  public void visit(Print s)
  {
	if(s.exp==null)
	{
		System.out.println("Error: There should be some expression in the println at Line "+s.linenum+".");
		return;
	}
    s.exp.accept(this);
    if(this.type.getNum()==3)
    {
    	this.type = new Type.Int();
    	return;
    }
    if (!this.type.toString().equals("@int"))
      error();
    return;
  }

  @Override
  public void visit(While s)
  {
	 s.condition.accept(this);
	 if(!(this.type.toString().equals("@boolean")))
	 {
		System.out.println("Error: type mismatch, cannot convert from "+ this.type.toString() 
							+" to boolean at Line "+s.linenum+".");
		this.type = new Type.Boolean();
	 }
	 s.body.accept(this);
	 return;
  }

  // type
  @Override
  public void visit(Type.Boolean t)
  {
	  this.type = new Type.Boolean();
	  return;
  }

  @Override
  public void visit(Type.ClassType t)
  {
	 this.type = new Type.ClassType(t.id);
	 return;
  }

  @Override
  public void visit(Type.Int t)
  {
	this.type = new Type.Int();
    return;
  }

  @Override
  public void visit(Type.IntArray t)
  {
	  this.type = new Type.IntArray();
	  return;
  }

  // dec
  @Override
  public void visit(Dec.DecSingle d)
  {
	 switch(d.type.getNum())
	 {
	  case 0:
		  this.type = new Type.Int();
		  break;
	  case -1:
		  this.type = new Type.Boolean();
		  break;
	  case  2:
		  this.type = new Type.ClassType(d.id);
		  break;
	  case  1:
		  this.type = new Type.IntArray();
		  break;
	  default:
		  error();
	 }
	  //this.classTable.put(this.currentClass, d.id, d.type);
	  return;
  }

  // method
  @Override
  public void visit(Method.MethodSingle m)
  {
    this.methodTable.put(m.formals, m.locals);
    System.out.println("");
    if (ConAst.elabMethodTable)
    {
      System.out.println("Class "+this.currentClass+" Method "+m.id+"()");
      System.out.println("{");
      this.methodTable.dump();
      System.out.println("}");
    }
    
    for (Stm.T s : m.stms)
      s.accept(this);
    m.retExp.accept(this);
    isuse(this.methodTable.table);
    this.methodTable.clear();
    return;
  }

  // class
  @Override
  public void visit(Class.ClassSingle c)
  {
    this.currentClass = c.id;
    for (Method.T m : c.methods) 
    {
      ((MethodSingle)m).accept(this);
    }
    return;
  }

  // main class
  @Override
  public void visit(MainClass.MainClassSingle c)
  {
    this.currentClass = c.id;
    // "main" has an argument "arg" of type "String[]", but
    // one has no chance to use it. So it's safe to skip it...
    c.stm.accept(this);
    return;
  }

  public void isuse(Hashtable<String, Type.T> fields)
  {
     Enumeration enField = fields.keys();
	 while(enField.hasMoreElements())
	 {
		 String classid = (String)enField.nextElement();
		// System.out.println(classid+"!!!");
		 if(this.usedVarTable.get(classid)==null)
		 {
		   System.out.println("Warning: variable "+classid+" declared at line "+this.methodTable.isuse.get(classid)+" never used.");
		 }
	 }
  }
  
  // ////////////////////////////////////////////////////////
  // step 1: build class table
  // class table for Main class
  private void buildMainClass(MainClass.MainClassSingle main)
  {
    this.classTable.put(main.id, new ClassBinding(null));
  }

  // class table for normal classes
  private void buildClass(ClassSingle c)
  {
    this.classTable.put(c.id, new ClassBinding(c.extendss));
    for (Dec.T dec : c.decs) 
    {
      Dec.DecSingle d = (Dec.DecSingle) dec;
      this.classTable.put(c.id, d.id, d.type);
    }
    for (Method.T method : c.methods) 
    {
      MethodSingle m = (MethodSingle) method;
      this.classTable.put(c.id, m.id, new MethodType(m.retType, m.formals));
    }
  }

  // step 1: end
  // ///////////////////////////////////////////////////

  // program
  @Override
  public void visit(ProgramSingle p)
  {
    // ////////////////////////////////////////////////
    // step 1: build a symbol table for class (the class table)
    // a class table is a mapping from class names to class bindings
    // classTable: className -> ClassBinding{extends, fields, methods}
    buildMainClass((MainClass.MainClassSingle) p.mainClass);  //bulid class for mainclass
    for (Class.T c : p.classes) {                             //bulid class for other class
      buildClass((ClassSingle) c);
    }

    // we can double check that the class table is OK!
    if (control.Control.ConAst.elabClassTable) {
      this.classTable.dump();
    }

    // ////////////////////////////////////////////////
    // step 2: elaborate each class in turn, under the class table
    // built above.
    p.mainClass.accept(this);
    
    for (Class.T c : p.classes) {
      c.accept(this);
    }
    
  }
}
