package ast;

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
  @Override
  public void visit(ast.exp.Add e)
  {
    // Lab2, exercise4: filling in missing code.
    // Similar for other methods with empty bodies.
    // Your code here:
	  e.left.accept(this);
	  if (e.right != null) {
		this.say(" + ");
		e.right.accept(this);
	  }
	  return;
  }

  @Override
  public void visit(ast.exp.And e)
  {
	  e.left.accept(this);
	  if (e.right != null) {
		this.say(" && ");
		e.right.accept(this);
	  }
	return;
  }

  @Override
  public void visit(ast.exp.ArraySelect e)
  {
	  e.array.accept(this);
	  this.say("[");
	  e.index.accept(this);
	  this.say("]");
  }

  
  //TODO:bug needs to be fixed
  @Override
  public void visit(ast.exp.Call e)
  {
    e.exp.accept(this);
    this.say("." + e.id + "(");
    for (ast.exp.T x : e.args) {
      x.accept(this);
      if (x != e.args.getLast()) {
		this.say(", ");
      }
    }
    this.say(")");
    return;
  }

  @Override
  public void visit(ast.exp.False e)
  {
	  this.say("false");
  }

  @Override
  public void visit(ast.exp.Id e)
  {
    this.say(e.id);
  }

  @Override
  public void visit(ast.exp.Length e)
  {
	  e.array.accept(this);
	  this.say(".length");
	  return;
  }

  @Override
  public void visit(ast.exp.Lt e)
  {
    e.left.accept(this);
    if (e.right != null) {
		this.say(" < ");
		e.right.accept(this);
	}
	return;
  }

  @Override
  public void visit(ast.exp.NewIntArray e)
  {
	  this.say("new int[");
	  e.exp.accept(this);
	  this.say("]");
	  return;
  }

  @Override
  public void visit(ast.exp.NewObject e)
  {
    this.say("new " + e.id + "()");
    return;
  }

  @Override
  public void visit(ast.exp.Not e)
  {
	  this.say("!");
	  e.exp.accept(this);
  }

  @Override
  public void visit(ast.exp.Num e)
  {
    System.out.print(e.num);
    return;
  }

  @Override
  public void visit(ast.exp.Sub e)
  {
    e.left.accept(this);
    if (e.right != null) {
		this.say(" - ");
		e.right.accept(this);
	}
	return;
  }

  @Override
  public void visit(ast.exp.This e)
  {
    this.say("this");
  }

  @Override
  public void visit(ast.exp.Times e)
  {
    e.left.accept(this);
    if (e.right != null) {
		this.say(" * ");
		e.right.accept(this);
	}
	return;
  }

  @Override
  public void visit(ast.exp.True e)
  {
	  this.say("true");
  }

  // statements
  @Override
  public void visit(ast.stm.Assign s)
  {
    this.printSpaces();
    this.say(s.id + " = ");
    s.exp.accept(this);
    this.sayln(";");
    return;
  }

  @Override
  public void visit(ast.stm.AssignArray s)
  {
	  this.printSpaces();
	  this.say(s.id + "[");
	  s.index.accept(this);
	  this.say("] = ");
	  s.exp.accept(this);
	  this.sayln(";");
	  return;
  }

  @Override
  public void visit(ast.stm.Block s)
  {
	  this.unIndent();
	  this.printSpaces();
	  this.sayln("{");
	  this.indent();
	  for (ast.stm.T b : s.stms) {
		  b.accept(this);
	  }
	  this.unIndent();
	  this.printSpaces();
	  this.sayln("}");
	  this.indent();
	  return;
  }

  @Override
  public void visit(ast.stm.If s)
  {
    this.printSpaces();
    this.say("if (");
    s.condition.accept(this);
    this.sayln(")");
    this.indent();
    s.thenn.accept(this);
    this.unIndent();
    this.printSpaces();
    this.sayln("else");
	this.indent();
    s.elsee.accept(this);
    this.unIndent();
    return;
  }

  @Override
  public void visit(ast.stm.Print s)
  {
    this.printSpaces();
    this.say("System.out.println (");
    s.exp.accept(this);
    this.sayln(");");
    return;
  }

  @Override
  public void visit(ast.stm.While s)
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
  public void visit(ast.type.Boolean t)
  {
	  this.say("boolean");
  }

  @Override
  public void visit(ast.type.Class t)
  {
	  this.say(t.id);
  }

  @Override
  public void visit(ast.type.Int t)
  {
    this.say("int");
  }

  @Override
  public void visit(ast.type.IntArray t)
  {
	  this.say("int[]");
  }

  // dec
  @Override
  public void visit(ast.dec.Dec d)
  {
	  this.printSpaces();
      d.type.accept(this);
      this.sayln(" " + d.id + ";");
      return;
  }

  //TODO:bug needs to be fixed
  // method
  @Override
  public void visit(ast.method.Method m)
  {
    this.say("  public ");
    m.retType.accept(this);
    this.say(" " + m.id + "(");
    for (ast.dec.T d : m.formals) {
      ast.dec.Dec dec = (ast.dec.Dec) d;
      dec.type.accept(this);
      this.say(" " + dec.id);
      if (d != m.formals.getLast()) {
		this.say(", ");
	}
    }
    this.sayln(")");
    this.sayln("  {");

    for (ast.dec.T d : m.locals) {
      ast.dec.Dec dec = (ast.dec.Dec) d;
      this.say("    ");
      dec.type.accept(this);
      this.sayln(" " + dec.id + ";");
    }
    this.sayln("");
    for (ast.stm.T s : m.stms)
      s.accept(this);
    this.say("    return ");
    m.retExp.accept(this);
    this.sayln(";");
    this.sayln("  }");
    return;
  }

  // class
  @Override
  public void visit(ast.classs.Class c)
  {
    this.say("class " + c.id);
    if (c.extendss != null)
      this.sayln(" extends " + c.extendss);
    else
      this.sayln("");

    this.sayln("{");

    for (ast.dec.T d : c.decs) {
      ast.dec.Dec dec = (ast.dec.Dec) d;
      this.say("  ");
      dec.type.accept(this);
      this.say(" ");
      this.sayln(dec.id + ";");
    }
    for (ast.method.T mthd : c.methods)
    {
      mthd.accept(this);
      this.sayln("");
    }
    this.sayln("}");
    return;
  }

  // main class
  @Override
  public void visit(ast.mainClass.MainClass c)
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
  public void visit(ast.program.Program p)
  {
    p.mainClass.accept(this);
    this.sayln("");
    for (ast.classs.T classs : p.classes) {
      classs.accept(this);
      this.sayln("");
    }
    System.out.println("\n\n");
  }
}
