package codegen.C;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;

import codegen.C.Ast.Class.ClassSingle;
import codegen.C.Ast.Dec;
import codegen.C.Ast.Type;
import codegen.C.Ast.Dec.DecSingle;
import codegen.C.Ast.Exp;
import codegen.C.Ast.Exp.Add;
import codegen.C.Ast.Exp.And;
import codegen.C.Ast.Exp.ArraySelect;
import codegen.C.Ast.Exp.Call;
import codegen.C.Ast.Exp.Id;
import codegen.C.Ast.Exp.Length;
import codegen.C.Ast.Exp.Lt;
import codegen.C.Ast.Exp.NewIntArray;
import codegen.C.Ast.Exp.NewObject;
import codegen.C.Ast.Exp.Not;
import codegen.C.Ast.Exp.Num;
import codegen.C.Ast.Exp.Sub;
import codegen.C.Ast.Exp.This;
import codegen.C.Ast.Exp.Times;
import codegen.C.Ast.MainMethod.MainMethodSingle;
import codegen.C.Ast.Method;
import codegen.C.Ast.Method.MethodSingle;
import codegen.C.Ast.Program.ProgramSingle;
import codegen.C.Ast.Stm;
import codegen.C.Ast.Stm.Assign;
import codegen.C.Ast.Stm.AssignArray;
import codegen.C.Ast.Stm.Block;
import codegen.C.Ast.Stm.If;
import codegen.C.Ast.Stm.Print;
import codegen.C.Ast.Stm.While;
import codegen.C.Ast.Type.ClassType;
import codegen.C.Ast.Type.Int;
import codegen.C.Ast.Type.IntArray;
import codegen.C.Ast.Vtable;
import codegen.C.Ast.Vtable.VtableSingle;
import control.Control;
import elaborator.ClassBinding;

public class PrettyPrintVisitor implements Visitor
{ 
  Hashtable<String,Integer> classvar = new Hashtable<String,Integer>();
  public Hashtable<String,Integer> var = new Hashtable<String,Integer>();
  private int indentLevel;
  private java.io.BufferedWriter writer;
  int flag = 0;
  public PrettyPrintVisitor()
  {
    this.indentLevel = 2;
  }

  private void indent()
  {
    this.indentLevel += 2;
  }

  private void unIndent()
  {
    this.indentLevel -= 2;
  }

  private void indentx(int x)
  {
    this.indentLevel += x;
  }
  
  private void printSpaces()
  {
    int i = this.indentLevel;
    while (i-- != 0)
      this.say(" ");
  }

  private void sayln(String s)
  {
    say(s);
    try {
      this.writer.write("\n");
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  private void say(String s)
  {
    try {
      this.writer.write(s);
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

  // /////////////////////////////////////////////////////
  // expressions
  @Override //zmk
  public void visit(Add e)
  {
	  e.left.accept(this);
	  this.say(" + ");
	  e.right.accept(this);
	  return;
  }

  @Override //zmk
  public void visit(And e)
  {
	  e.left.accept(this);
	  this.say(" && ");
	  e.right.accept(this);
	  return;
  }

  @Override //zmk
  public void visit(ArraySelect e)
  {
	  e.array.accept(this);  
	  this.say("[");
	  e.index.accept(this);
	  this.say("]");
  }

  @Override 
  public void visit(Call e)
  {
	  this.say("(");
	  if(flag==1)
	  {
		  this.say("frame.");
	  }
	  this.say(e.assign + "=");
	  e.exp.accept(this);
	  this.say(", ");
	  if(flag==1)
		  this.say("frame.");
	  this.say(e.assign+"->vptr->"+e.id+"(");
	  if(flag==1)
		  this.say("frame.");
	  this.say(e.assign);
	  int size = e.args.size();
	  if (size == 0) {
	    this.say("))");
	    return;
	  }
	  for (Exp.T x : e.args) {
	     this.say(", ");
	     x.accept(this);
	  }
	  this.say("))");
	  return;
  }

  @Override
  public void visit(Id e)
  {
	if(classvar.get(e.id)!=null)
		this.say("this->"+e.id);
	else if(var.get(e.id)==null)
       this.say("frame."+e.id);
	else 
	   this.say(e.id);
    return;
  }

  @Override //zmk
  public void visit(Length e)
  {
	  this.say("*(");
	  e.array.accept(this);
	  this.say("+2)");
	  return;
  }

  @Override
  public void visit(Lt e)
  {
    e.left.accept(this);
    this.say(" < ");
    e.right.accept(this);
    return;
  }

  @Override //zmk
  public void visit(NewIntArray e)
  {
	  e.exp.accept(this);
	  return;
  }
  @Override
  public void visit(NewObject e)
  {
    this.say("((struct "+e.id+"*)(Tiger_new(&"+e.id+"_vtable_,sizeof(struct "+e.id+"))))");
	return;
  }

  @Override //zmk
  public void visit(Not e)
  {
	  this.say(" ! (");
	  e.exp.accept(this);
	  this.say(")");
	  return;
  }

  @Override
  public void visit(Num e)
  {
    this.say(Integer.toString(e.num));
    return;
  }

  @Override
  public void visit(Sub e)
  {
    e.left.accept(this);
    this.say(" - ");
    e.right.accept(this);
    return;
  }

  @Override
  public void visit(This e)
  {
    this.say("this");
  }

  @Override
  public void visit(Times e)
  {
    e.left.accept(this);
    this.say(" * ");
    e.right.accept(this);
    return;
  }

  // statements
  @Override
  public void visit(Assign s)
  {
    this.printSpaces();
    if(s.exp instanceof NewIntArray)
    {
   	   if(classvar.get(s.id)!=null)
   	   {
   		   this.say("this->"+s.id+" = (int *)Tiger_new_array (");
		   s.exp.accept(this);
		   this.say(");");
		   return;
   	   }
   	   
       if(var.get(s.id)==null)
           this.say("frame.");
       this.say(s.id + " = (int *)Tiger_new_array (");
	   s.exp.accept(this);
	   this.say(");");
       return;
    }
    if(classvar.get(s.id)!=null)
	{
       this.say("this->"+s.id+" = ");
	   s.exp.accept(this);
	   this.say(";");
	   return;
	}
    if(var.get(s.id)==null)
    	this.say("frame.");
    this.say(s.id+" = ");
    s.exp.accept(this);
    this.say(";");
    return;
  }

  @Override  //zmk
  public void visit(AssignArray s)
  {
	  this.printSpaces();
	  if(classvar.get(s.id)!=null)
      {
		  this.say("this->"+s.id+"[");
		  s.exp.accept(this);
		  this.say("] = ");
		  s.index.accept(this);
		  this.say(";");
		  return;
	  }
	  this.say(s.id + "[");
	  s.exp.accept(this);
	  this.say("] = ");
	  s.index.accept(this);
	  this.say(";");
	  return;
  }

  @Override	//zmk
  public void visit(Block s)
  {
	  indentx(-3);
	  printSpaces();
	  indentx(3);
	  this.sayln(" { ");
	  for (codegen.C.Ast.Stm.T statement : s.stms) {
	      statement.accept(this);
	      this.sayln("");
	  }
	  indentx(-3);
	  printSpaces();
	  indentx(3);
	  this.say(" } ");  
  }

  @Override
  public void visit(If s)
  {
    this.printSpaces();
    this.say("if (");
    s.condition.accept(this);
    this.sayln(")");
    this.indent();
    s.thenn.accept(this);
    this.unIndent();
    this.sayln("");
    this.printSpaces();
    this.sayln("else");
    this.indent();
    s.elsee.accept(this);
    this.unIndent();
    return;
  }

  @Override
  public void visit(Print s)
  {
    this.printSpaces();
    this.say("System_out_println (");
    s.exp.accept(this);
    this.sayln(");");
    return;
  }

  @Override	//zmk
  public void visit(While s)
  {
	  this.printSpaces();
	  this.say("while (");
	  s.condition.accept(this);
	  this.sayln(")");
	  this.indent();
	  s.body.accept(this);
      this.unIndent();
	  return;
  }

  // type
  @Override
  public void visit(ClassType t)
  {
    this.say("struct " + t.id + " *");
  }

  @Override
  public void visit(Int t)
  {
    this.say("int");
  }

  @Override //zmk
  public void visit(IntArray t)
  {
	  this.say("int*");
  }

  // dec
  @Override	//zmk
  public void visit(DecSingle d)
  {
	  d.type.accept(this);
	  this.sayln(" "+d.id+";");
  }

  public void GC_map_frame(MethodSingle m)
  {
	 this.sayln("struct "+m.classId+"_"+m.id+"_gc_frame{");
	 this.sayln("  void *prev;");
	 this.sayln("  char *arguments_gc_map;");
	 this.sayln("  int *arguments_base_address;");
	 this.sayln("  int local_count;");
	 for (Dec.T d : m.locals) 
	 {
       DecSingle dec = (DecSingle) d;
       if(!(dec.type instanceof Type.Int))
    	   this.sayln("  struct "+dec.type+" *"+dec.id+";");
	 }
     this.sayln("};");
     return;
  }
  
  @Override
  public void visit(MethodSingle m)
  {
	classvar = m.classvar;
	var = m.var;
	flag = 1;
	int num = 0;
	this.say("char *"+m.classId+"_"+m.id+"_arguments_gc_map = \"");
    for(Dec.T d : m.formals)
    {
      DecSingle dec = (DecSingle) d;
      if(dec.type instanceof Type.Int)
    	  this.say("0");
      else
    	  this.say("1");
    }
    this.sayln("\";");
    
    //this.say("char *"+m.classId+"_"+m.id+"_locals_gc_map = \"");
    for (Dec.T d : m.locals) 
    {
      DecSingle dec = (DecSingle) d;
      if(!(dec.type instanceof Type.Int))
    	  num++;
    }
    
    this.sayln("// Local_frame");
    GC_map_frame(m);
    this.sayln("");
    m.retType.accept(this);
    this.say(" " + m.classId + "_" + m.id + "(");
    int size = m.formals.size();
    for (Dec.T d : m.formals) 
    {
      DecSingle dec = (DecSingle) d;
      size--;
      dec.type.accept(this);
      this.say(" " + dec.id);
      if (size > 0)
        this.say(", ");
    }
    
    this.sayln(")");
    this.sayln("{");

    this.sayln("  struct "+m.classId+"_"+m.id+"_gc_frame frame;");
    this.sayln("  frame.prev = prev;");
    this.sayln("  prev = &frame;");
    this.sayln("  frame.arguments_gc_map = "+m.classId+"_"+m.id+"_arguments_gc_map;");
    this.sayln("  frame.arguments_base_address = (int *)&this;");
    this.sayln("  frame.local_count = "+num +";");

    for (Dec.T d : m.locals) 
    {
      DecSingle dec = (DecSingle) d;
      if(dec.type instanceof Type.Int)
    	  this.sayln("  int "+dec.id+";");
    }
    for (Stm.T s : m.stms)
    {
      s.accept(this);
      this.sayln("");
    }
    this.sayln("  prev = frame.prev;");
    this.say("  return ");
    m.retExp.accept(this);
    this.sayln(";");
    this.sayln("}");
    return;
  }

  @Override
  public void visit(MainMethodSingle m)
  {
	  flag = 0;
	  this.sayln("int Tiger_main ()");
	  this.sayln("{");
	  for (Dec.T dec : m.locals) {
	      this.say("  ");
	      DecSingle d = (DecSingle) dec;
	      d.type.accept(this);
	      this.say(" ");
	      this.sayln(d.id + ";");
	  }
      m.stm.accept(this);
      this.sayln("}\n");
	  return;
  }

  // vtables
  @Override
  public void visit(VtableSingle v)
  {
    this.sayln("struct " + v.id + "_vtable");
    this.sayln("{");
    this.sayln("  char *"+v.id+"_gc_map;"); 
    for (codegen.C.Ftuple t : v.ms) {
      this.say("  ");
      t.ret.accept(this);
      this.sayln(" (*" + t.id + ")();");
    }
    this.sayln("};\n");
    return;
  }

  private void outputVtable(VtableSingle v)
  {
    this.sayln("struct " + v.id + "_vtable " + v.id + "_vtable_ = ");
    this.sayln("{");
    int num = v.ms.size();
    this.sayln("  \""+v.gc_map+"\",");
    for (codegen.C.Ftuple t : v.ms) {
      this.say("  ");
      this.say(t.classs + "_" + t.id);
      if(num!=1)
    	  this.sayln(",");
      else
    	  this.sayln("");
    }
    this.sayln("};\n");
    return;
  }

  private void decVtable(VtableSingle v)
  {
	  this.sayln("struct " + v.id + "_vtable " + v.id + "_vtable_ ; ");
	  return;
  }
  // class
  @Override
  public void visit(ClassSingle c)
  {
	classvar = c.classvar;	
    this.sayln("struct " + c.id);
    this.sayln("{");
    this.sayln("  struct " + c.id + "_vtable *vptr;");
    this.sayln("  int isObjOrArray;");
    this.sayln("  unsigned length;");
    this.sayln("  void *forwarding;");
    for (codegen.C.Tuple t : c.decs) {
      this.say("  ");
      t.type.accept(this);
      this.say(" ");
      this.sayln(t.id + ";");
    }
    this.sayln("};");
    return;
  }

  public void Init(LinkedList<Dec.T> k)
  {
	  for(Dec.T k1:k)
	  {
		  Dec.DecSingle k2 = (Dec.DecSingle)k1;
		  if(k2.type instanceof Type.Int)
		  {
			  this.sayln("static int "+k2.id+" = 0;");
		  }
		  else if(k2.type instanceof Type.IntArray)
		  {
			  this.sayln("static int* "+k2.id+" = NULL;");
		  }
		  else{
			  k2.type.accept(this);
			  this.sayln(" "+k2.id+" = NULL;");
		  }
	  }
  }
  // program
  @Override
  public void visit(ProgramSingle p)
  {
    // we'd like to output to a file, rather than the "stdout".
    try {
      String outputName = null;
      if (Control.ConCodeGen.outputName != null)
        outputName = Control.ConCodeGen.outputName;
      else if (Control.ConCodeGen.fileName != null)
        outputName = Control.ConCodeGen.fileName + ".c";
      else
        outputName = "a.c.c";

      this.writer = new java.io.BufferedWriter(new java.io.OutputStreamWriter(
          new java.io.FileOutputStream(outputName)));
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
    
    this.sayln("// This is automatically generated by the Tiger compiler.");
    this.sayln("// Do NOT modify!\n");

    this.sayln("#include<stdio.h>");
    this.sayln("#include<stdlib.h>");
    
    this.sayln("// structures");
    for (codegen.C.Ast.Class.T c : p.classes) {
      c.accept(this);
    }
    this.sayln("");
    
    this.sayln("// vtables structures");
    for (Vtable.T v : p.vtables) {
      v.accept(this);
    }
    this.sayln("");
    
    this.sayln("// vtables declare");
    for (Vtable.T v : p.vtables) {
       decVtable((VtableSingle) v);
    }
    this.sayln("");
    
    this.sayln("// methods");
    this.sayln("void *prev = 0;");
    for (Method.T m : p.methods) {
      m.accept(this);
    }
    this.sayln("");
    
    this.sayln("// vtables");
    for (Vtable.T v : p.vtables) {
      outputVtable((VtableSingle) v);
    }
    this.sayln("");
    
    this.sayln("// main method");
    p.mainMethod.accept(this);
    this.sayln("");
    this.say("\n\n");

    try {
      this.writer.close();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }
}
