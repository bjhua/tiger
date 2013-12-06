package cfg;

import control.Control;

public class PrettyPrintVisitor implements Visitor
{
  private java.io.BufferedWriter writer;

  private void printSpaces()
  {
    this.say("  ");
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
  // operand
  @Override
  public void visit(cfg.operand.Int operand)
  {
    this.say(new Integer(operand.i).toString());
  }

  @Override
  public void visit(cfg.operand.Var operand)
  {
    this.say(operand.id);
  }

  // statements
  @Override
  public void visit(cfg.stm.Add s)
  {
    this.printSpaces();
    this.say(s.dst + " = ");
    s.left.accept(this);
    this.say(" + ");
    s.right.accept(this);
    this.say(";");
    return;
  }

  @Override
  public void visit(cfg.stm.InvokeVirtual s)
  {
    this.printSpaces();
    this.say(s.dst + " = " + s.obj);
    this.say("->vptr->" + s.f + "("+s.obj);
    for (cfg.operand.T x : s.args) {
      this.say(", ");
      x.accept(this);
    }
    this.say(");");
    return;
  }

  @Override
  public void visit(cfg.stm.Lt s)
  {
    this.printSpaces();
    this.say(s.dst + " = ");
    s.left.accept(this);
    this.say(" < ");
    s.right.accept(this);
    this.say(";");
    return;
  }

  @Override
  public void visit(cfg.stm.Move s)
  {
    this.printSpaces();
    this.say(s.dst + " = ");
    s.src.accept(this);
    this.say(";");
    return;
  }

  @Override
  public void visit(cfg.stm.NewObject s)
  {
    this.printSpaces();
    this.say(s.dst +" = ((struct " + s.c + "*)(Tiger_new (&" + s.c
        + "_vtable_, sizeof(struct " + s.c + "))));");
    return;
  }

  @Override
  public void visit(cfg.stm.Print s)
  {
    this.printSpaces();
    this.say("System_out_println (");
    s.arg.accept(this);
    this.sayln(");");
    return;
  }

  @Override
  public void visit(cfg.stm.Sub s)
  {
    this.printSpaces();
    this.say(s.dst + " = ");
    s.left.accept(this);
    this.say(" - ");
    s.right.accept(this);
    this.say(";");
    return;
  }

  @Override
  public void visit(cfg.stm.Times s)
  {
    this.printSpaces();
    this.say(s.dst + " = ");
    s.left.accept(this);
    this.say(" * ");
    s.right.accept(this);
    this.say(";");
    return;
  }

  // transfer
  @Override
  public void visit(cfg.transfer.If s)
  {
    this.printSpaces();
    this.say("if (");
    s.operand.accept(this);
    this.say(")\n");
    this.printSpaces();
    this.say("  goto " + s.truee.toString() + ";\n");
    this.printSpaces();
    this.say("else\n");
    this.printSpaces();
    this.say("  goto " + s.falsee.toString()+";\n");
    return;
  }

  @Override
  public void visit(cfg.transfer.Goto s)
  {
    this.printSpaces();
    this.say("goto " + s.label.toString()+";\n");
    return;
  }

  @Override
  public void visit(cfg.transfer.Return s)
  {
    this.printSpaces();
    this.say("return ");
    s.operand.accept(this);
    this.say(";");
    return;
  }

  // type
  @Override
  public void visit(cfg.type.Class t)
  {
    this.say("struct " + t.id + " *");
  }

  @Override
  public void visit(cfg.type.Int t)
  {
    this.say("int");
  }

  @Override
  public void visit(cfg.type.IntArray t)
  {
  }

  // dec
  @Override
  public void visit(cfg.dec.Dec d)
  {
    d.type.accept(this);
    this.say(" "+d.id);
    return;
  }
  
  // dec
  @Override
  public void visit(cfg.block.Block b)
  {
    this.say(b.label.toString()+":\n");
    for (cfg.stm.T s: b.stms){
      s.accept(this);
      this.say("\n");
    }
    b.transfer.accept(this);
    return;
  }

  // method
  @Override
  public void visit(cfg.method.Method m)
  {
    m.retType.accept(this);
    this.say(" " + m.classId + "_" + m.id + "(");
    int size = m.formals.size();
    for (cfg.dec.T d : m.formals) {
      cfg.dec.Dec dec = (cfg.dec.Dec) d;
      size--;
      dec.type.accept(this);
      this.say(" " + dec.id);
      if (size > 0)
        this.say(", ");
    }
    this.sayln(")");
    this.sayln("{");

    for (cfg.dec.T d : m.locals) {
      cfg.dec.Dec dec = (cfg.dec.Dec) d;
      this.say("  ");
      dec.type.accept(this);
      this.say(" " + dec.id + ";\n");
    }
    this.sayln("");
    for (cfg.block.T block : m.blocks){
      cfg.block.Block b = (cfg.block.Block)block;
      b.accept(this);
    }
    this.sayln("\n}");
    return;
  }

  @Override
  public void visit(cfg.mainMethod.MainMethod m)
  {
    this.sayln("int Tiger_main ()");
    this.sayln("{");
    for (cfg.dec.T dec : m.locals) {
      this.say("  ");
      cfg.dec.Dec d = (cfg.dec.Dec) dec;
      d.type.accept(this);
      this.say(" ");
      this.sayln(d.id + ";");
    }
    this.sayln("");
    for (cfg.block.T block : m.blocks) {
      cfg.block.Block b = (cfg.block.Block) block;
      b.accept(this);
    }
    this.sayln("\n}\n");
    return;
  }

  // vtables
  @Override
  public void visit(cfg.vtable.Vtable v)
  {
    this.sayln("struct " + v.id + "_vtable");
    this.sayln("{");
    for (cfg.Ftuple t : v.ms) {
      this.say("  ");
      t.ret.accept(this);
      this.sayln(" (*" + t.id + ")();");
    }
    this.sayln("};\n");
    return;
  }

  private void outputVtable(cfg.vtable.Vtable v)
  {
    this.sayln("struct " + v.id + "_vtable " + v.id + "_vtable_ = ");
    this.sayln("{");
    for (cfg.Ftuple t : v.ms) {
      this.say("  ");
      this.sayln(t.classs + "_" + t.id + ",");
    }
    this.sayln("};\n");
    return;
  }

  // class
  @Override
  public void visit(cfg.classs.Class c)
  {
    this.sayln("struct " + c.id);
    this.sayln("{");
    this.sayln("  struct " + c.id + "_vtable *vptr;");
    for (cfg.Tuple t : c.decs) {
      this.say("  ");
      t.type.accept(this);
      this.say(" ");
      this.sayln(t.id + ";");
    }
    this.sayln("};");
    return;
  }

  // program
  @Override
  public void visit(cfg.program.Program p)
  {
    // we'd like to output to a file, rather than the "stdout".
    try {
      String outputName = null;
      if (Control.outputName != null)
        outputName = Control.outputName;
      else if (Control.fileName != null)
        outputName = Control.fileName + ".c";
      else
        outputName = "a.c";

      this.writer = new java.io.BufferedWriter(new java.io.OutputStreamWriter(
          new java.io.FileOutputStream(outputName)));
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }

    this.sayln("// This is automatically generated by the Tiger compiler.");
    this.sayln("// Do NOT modify!\n");
    this.sayln("// Control-flow Graph\n");

    this.sayln("// structures");
    for (cfg.classs.T c : p.classes) {
      c.accept(this);
    }

    this.sayln("// vtables structures");
    for (cfg.vtable.T v : p.vtables) {
      v.accept(this);
    }
    this.sayln("");

    this.sayln("// methods");
    for (cfg.method.T m : p.methods) {
      m.accept(this);
    }
    this.sayln("");

    this.sayln("// vtables");
    for (cfg.vtable.T v : p.vtables) {
      outputVtable((cfg.vtable.Vtable) v);
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
