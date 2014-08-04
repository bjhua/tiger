package cfg;

import cfg.Cfg.Block;
import cfg.Cfg.Block.BlockSingle;
import cfg.Cfg.Class;
import cfg.Cfg.Class.ClassSingle;
import cfg.Cfg.Dec;
import cfg.Cfg.Dec.DecSingle;
import cfg.Cfg.MainMethod.MainMethodSingle;
import cfg.Cfg.Method;
import cfg.Cfg.Method.MethodSingle;
import cfg.Cfg.Operand;
import cfg.Cfg.Operand.Int;
import cfg.Cfg.Operand.Var;
import cfg.Cfg.Program.ProgramSingle;
import cfg.Cfg.Stm;
import cfg.Cfg.Stm.Add;
import cfg.Cfg.Stm.InvokeVirtual;
import cfg.Cfg.Stm.Lt;
import cfg.Cfg.Stm.Move;
import cfg.Cfg.Stm.NewObject;
import cfg.Cfg.Stm.Print;
import cfg.Cfg.Stm.Sub;
import cfg.Cfg.Stm.Times;
import cfg.Cfg.Transfer.Goto;
import cfg.Cfg.Transfer.If;
import cfg.Cfg.Transfer.Return;
import cfg.Cfg.Type.ClassType;
import cfg.Cfg.Type.IntArrayType;
import cfg.Cfg.Type.IntType;
import cfg.Cfg.Vtable;
import cfg.Cfg.Vtable.VtableSingle;
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
  public void visit(Int operand)
  {
    this.say(new Integer(operand.i).toString());
  }

  @Override
  public void visit(Var operand)
  {
    this.say(operand.id);
  }

  // statements
  @Override
  public void visit(Add s)
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
  public void visit(InvokeVirtual s)
  {
    this.printSpaces();
    this.say(s.dst + " = " + s.obj);
    this.say("->vptr->" + s.f + "("+s.obj);
    for (Operand.T x : s.args) {
      this.say(", ");
      x.accept(this);
    }
    this.say(");");
    return;
  }

  @Override
  public void visit(Lt s)
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
  public void visit(Move s)
  {
    this.printSpaces();
    this.say(s.dst + " = ");
    s.src.accept(this);
    this.say(";");
    return;
  }

  @Override
  public void visit(NewObject s)
  {
    this.printSpaces();
    this.say(s.dst +" = ((struct " + s.c + "*)(Tiger_new (&" + s.c
        + "_vtable_, sizeof(struct " + s.c + "))));");
    return;
  }

  @Override
  public void visit(Print s)
  {
    this.printSpaces();
    this.say("System_out_println (");
    s.arg.accept(this);
    this.sayln(");");
    return;
  }

  @Override
  public void visit(Sub s)
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
  public void visit(Times s)
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
  public void visit(If s)
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
  public void visit(Goto s)
  {
    this.printSpaces();
    this.say("goto " + s.label.toString()+";\n");
    return;
  }

  @Override
  public void visit(Return s)
  {
    this.printSpaces();
    this.say("return ");
    s.operand.accept(this);
    this.say(";");
    return;
  }

  // type
  @Override
  public void visit(ClassType t)
  {
    this.say("struct " + t.id + " *");
  }

  @Override
  public void visit(IntType t)
  {
    this.say("int");
  }

  @Override
  public void visit(IntArrayType t)
  {
  }

  // dec
  @Override
  public void visit(DecSingle d)
  {
    d.type.accept(this);
    this.say(" "+d.id);
    return;
  }
  
  // dec
  @Override
  public void visit(BlockSingle b)
  {
    this.say(b.label.toString()+":\n");
    for (Stm.T s: b.stms){
      s.accept(this);
      this.say("\n");
    }
    b.transfer.accept(this);
    return;
  }

  // method
  @Override
  public void visit(MethodSingle m)
  {
    m.retType.accept(this);
    this.say(" " + m.classId + "_" + m.id + "(");
    int size = m.formals.size();
    for (Dec.T d : m.formals) {
      DecSingle dec = (DecSingle) d;
      size--;
      dec.type.accept(this);
      this.say(" " + dec.id);
      if (size > 0)
        this.say(", ");
    }
    this.sayln(")");
    this.sayln("{");

    for (Dec.T d : m.locals) {
      DecSingle dec = (DecSingle) d;
      this.say("  ");
      dec.type.accept(this);
      this.say(" " + dec.id + ";\n");
    }
    this.sayln("");
    for (Block.T block : m.blocks){
      BlockSingle b = (BlockSingle)block;
      b.accept(this);
    }
    this.sayln("\n}");
    return;
  }

  @Override
  public void visit(MainMethodSingle m)
  {
    this.sayln("int Tiger_main ()");
    this.sayln("{");
    for (Dec.T dec : m.locals) {
      this.say("  ");
      DecSingle d = (DecSingle) dec;
      d.type.accept(this);
      this.say(" ");
      this.sayln(d.id + ";");
    }
    this.sayln("");
    for (Block.T block : m.blocks) {
      BlockSingle b = (BlockSingle) block;
      b.accept(this);
    }
    this.sayln("\n}\n");
    return;
  }

  // vtables
  @Override
  public void visit(VtableSingle v)
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

  private void outputVtable(VtableSingle v)
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
  public void visit(ClassSingle c)
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
    for (Class.T c : p.classes) {
      c.accept(this);
    }

    this.sayln("// vtables structures");
    for (Vtable.T v : p.vtables) {
      v.accept(this);
    }
    this.sayln("");

    this.sayln("// methods");
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
