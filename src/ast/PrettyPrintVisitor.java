package ast;

import java.util.ListIterator;

import ast.Ast.Class.ClassSingle;
import ast.Ast.Dec;
import ast.Ast.Dec.T;
import ast.Ast.Exp;
import ast.Ast.Exp.Add;
import ast.Ast.Exp.And;
import ast.Ast.Exp.ArraySelect;
import ast.Ast.Exp.Call;
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
import ast.Ast.Method;
import ast.Ast.Method.MethodSingle;
import ast.Ast.Program;
import ast.Ast.Stm;
import ast.Ast.Stm.Assign;
import ast.Ast.Stm.AssignArray;
import ast.Ast.Stm.Block;
import ast.Ast.Stm.If;
import ast.Ast.Stm.Print;
import ast.Ast.Stm.While;
import ast.Ast.Type.Boolean;
import ast.Ast.Type.ClassType;
import ast.Ast.Type.Int;
import ast.Ast.Type.IntArray;
import ast.Ast.Type.Error;

public class PrettyPrintVisitor implements Visitor
{
  private int indentLevel;
  
  public PrettyPrintVisitor()
  {
    this.indentLevel = 4;
  }
  
  private void indent()
  {
    this.indentLevel += 2;
  }
  
  private void indentx(int x)
  {
    this.indentLevel += x;
  }
  
  private void unIndent()
  {
    this.indentLevel -= 2;
  }

  private void printSpaces()
  {
    int i = this.indentLevel;
    while (i-- != 0)
      this.say(" ");
  }

  private void sayln(String s)
  {
    System.out.println(s);
  }

  private void say(String s)
  {
    System.out.print(s);
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
	  return ;
  }

  @Override  //zmk
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
    e.exp.accept(this);
    this.say("." + e.id + "(");
    int temp = 1;
    for (Exp.T x : e.args) {
      x.accept(this);
      if(temp!=e.args.size())
      this.say(", ");
      temp++;
    }
    this.say(")");
    temp = 1;
    return;
  }
  

  @Override //zmk
  public void visit(False e)
  {
	  //e.accept(this);
	  this.say(" false ");
  }

  @Override
  public void visit(Id e)
  {
    this.say(e.id);
  }

  @Override //zmk
  public void visit(Length e)
  {
	  e.array.accept(this);
	  //e.accept(this);
  }

  @Override
  public void visit(Lt e)
  {
    e.left.accept(this);
    this.say(" < ");
    e.right.accept(this);
    return;
  }

  @Override
  public void visit(NewIntArray e)
  {
	  this.say(" new int [");
	  e.exp.accept(this);
	  this.say("]");
	  //this.sayln(";");
  }

  @Override
  public void visit(NewObject e)
  {
    this.say("new " + e.id + "()");
    return;
  }

  @Override //zmk
  public void visit(Not e)
  {
	  this.say(" ! ");
	  e.exp.accept(this);
  }

  @Override
  public void visit(Num e)
  {
    System.out.print(e.num);
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

  @Override
  public void visit(True e)
  {
	  this.say(" ture ");
  }

  // statements
  @Override
  public void visit(Assign s)
  {
    this.printSpaces();
    this.say(s.id + " = ");
    s.exp.accept(this);
    this.sayln(";");
    return;
  }

  @Override  //zmk
  public void visit(AssignArray s)
  {
	  this.printSpaces();
	  this.say(s.id + "[");
	  s.index.accept(this);
	  this.say("] = ");
	  s.exp.accept(this);
	  this.sayln(";");
	  return;
  }

  @Override //zmk
  public void visit(Block s)
  {
	  indentx(-3);
	  printSpaces();
	  indentx(3);
	  this.sayln(" { ");
	  for (ast.Ast.Stm.T statement : s.stms) {
	      statement.accept(this);
	  }
	  indentx(-3);
	  printSpaces();
	  indentx(3);
	  this.sayln(" } ");
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
    this.sayln("");
    this.unIndent();
    return;
  }

  @Override
  public void visit(Print s)
  {
    this.printSpaces();
    this.say("System.out.println (");
    s.exp.accept(this);
    this.sayln(");");
    return;
  }

  @Override
  public void visit(While s)
  {
	 this.printSpaces();
	 this.say("while (");
	 s.condition.accept(this);
	 this.sayln(")");
	 this.indent();
	 s.body.accept(this);
	 this.unIndent();
	 this.sayln("");
	 return;
  }

  // type
  @Override //zmk
  public void visit(Boolean t)
  {
	  this.say("boolean");
  }

  @Override //zmk
  public void visit(ClassType t)
  {
	  this.say(t.id);
  }

  @Override
  public void visit(Int t)
  {
    this.say("int");
  }

  @Override //zmk
  public void visit(IntArray t)
  {
	  this.say("int[]");
  }

  // dec
  @Override //zmk
  public void visit(Dec.DecSingle d)
  {
	  d.type.accept(this);
	  this.say(" ");
	  this.sayln(d.id+";");
  }

  // method
  @Override
  public void visit(MethodSingle m)
  {
    this.say("  public ");
    m.retType.accept(this);
    this.say(" " + m.id + "(");
    int temp = 1;
    for (Dec.T d : m.formals) {
      Dec.DecSingle dec = (Dec.DecSingle) d;
      dec.type.accept(this);
      this.say(" " + dec.id);
      if(temp!=m.formals.size())
       this.say(", ");
      temp++;
    }
    this.sayln(")");
    this.sayln("  {");
    if(m.locals.size()!=0)
    {
      for (Dec.T d : m.locals) 
      {
        Dec.DecSingle dec = (Dec.DecSingle) d;
        this.say("    ");
        dec.type.accept(this);
        this.say(" " + dec.id + ";\n");
      }
      this.sayln("");
    }
    for (Stm.T s : m.stms)
      s.accept(this);
    this.say("    return ");
    m.retExp.accept(this);
    this.sayln(";");
    this.sayln("  }");
    return;
  }

  // class
  @Override
  public void visit(ClassSingle c)
  {
    this.say("class " + c.id);
    if (c.extendss != null)
      this.sayln(" extends " + c.extendss);
    else
      this.sayln("");

    this.sayln("{");

    for (Dec.T d : c.decs) {
      Dec.DecSingle dec = (Dec.DecSingle) d;
      this.say("  ");
      dec.type.accept(this);
      this.say(" ");
      this.sayln(dec.id + ";");
    }
    if(c.methods.size()!=0)
    {
      for (Method.T mthd : c.methods)
        mthd.accept(this);
      this.sayln("}");
    }
    return;
  }

  // main class
  @Override
  public void visit(MainClass.MainClassSingle c)
  {
    this.sayln("class " + c.id);
    this.sayln("{");
    this.sayln("  public static void main (String [] " + c.arg + ")");
    this.sayln("  {");
    c.stm.accept(this);
    this.sayln("  }");
    this.sayln("}");
    return;
  }

  // program
  @Override
  public void visit(Program.ProgramSingle p)
  {
    p.mainClass.accept(this);
    this.sayln("");
    for (ast.Ast.Class.T classs : p.classes) {
      classs.accept(this);
    }
    System.out.println("\n\n");
  }

@Override
public void visit(ast.Ast.Type.Error t) {
	return;
}
}
