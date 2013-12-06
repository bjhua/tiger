package cfg;

public class VisualVisitor implements Visitor
{
  public StringBuffer strb;
  
  public VisualVisitor ()
  {
    this.strb = new StringBuffer();
  }
  
  // ///////////////////////////////////////////////////
  private void emit(String s)
  {
    strb.append(s);
    return;
  }

  // /////////////////////////////////////////////////////
  // operand
  @Override
  public void visit(cfg.operand.Int operand)
  {
    emit (new Integer(operand.i).toString());
  }

  @Override
  public void visit(cfg.operand.Var operand)
  {
    emit (operand.id);
  }

  // statements
  @Override
  public void visit(cfg.stm.Add s)
  {
    emit(s.dst + " = ");
    s.left.accept(this);
    emit(" + ");
    s.right.accept(this);
    emit(";");
    return;
  }

  @Override
  public void visit(cfg.stm.InvokeVirtual s)
  {
    emit(s.dst + " = " + s.obj);
    emit("->vptr->" + s.f + "("+s.obj);
    for (cfg.operand.T x : s.args) {
      emit(", ");
      x.accept(this);
    }
    emit(");");
    return;
  }

  @Override
  public void visit(cfg.stm.Lt s)
  {
    emit(s.dst + " = ");
    s.left.accept(this);
    emit(" < ");
    s.right.accept(this);
    emit(";");
    return;
  }

  @Override
  public void visit(cfg.stm.Move s)
  {
    emit(s.dst + " = ");
    s.src.accept(this);
    emit(";");
    return;
  }

  @Override
  public void visit(cfg.stm.NewObject s)
  {
    emit(s.dst +" = ((struct " + s.c + "*)(Tiger_new (&" + s.c
        + "_vtable_, sizeof(struct " + s.c + "))));");
    return;
  }

  @Override
  public void visit(cfg.stm.Print s)
  {
    emit("System_out_println (");
    s.arg.accept(this);
    emit(");");
    return;
  }

  @Override
  public void visit(cfg.stm.Sub s)
  {
    emit(s.dst + " = ");
    s.left.accept(this);
    emit(" - ");
    s.right.accept(this);
    emit(";");
    return;
  }

  @Override
  public void visit(cfg.stm.Times s)
  {
    emit(s.dst + " = ");
    s.left.accept(this);
    emit(" * ");
    s.right.accept(this);
    emit(";");
    return;
  }

  // transfer
  @Override
  public void visit(cfg.transfer.If s)
  {
    emit("if (");
    s.operand.accept(this);
    emit(")\n");
    emit("  goto " + s.truee.toString() + ";\n");
    emit("else\n");
    emit("  goto " + s.falsee.toString()+";\n");
    return;
  }

  @Override
  public void visit(cfg.transfer.Goto s)
  {
    emit("goto " + s.label.toString()+";\n");
    return;
  }

  @Override
  public void visit(cfg.transfer.Return s)
  {
    
    return;
  }

  // type
  @Override
  public void visit(cfg.type.Class t)
  {
    emit("struct " + t.id + " *");
  }

  @Override
  public void visit(cfg.type.Int t)
  {
  }

  @Override
  public void visit(cfg.type.IntArray t)
  {
  }

  // dec
  @Override
  public void visit(cfg.dec.Dec d)
  {
    return;
  }
  
  // dec
  @Override
  public void visit(cfg.block.Block b)
  {
    
    return;
  }

  // method
  @Override
  public void visit(cfg.method.Method m)
  {
    java.util.HashMap<util.Label, cfg.block.T> map
    = new java.util.HashMap<util.Label, cfg.block.T>();
    for(cfg.block.T block : m.blocks) {
      cfg.block.Block b = (cfg.block.Block)block;
      util.Label label = b.label;
      map.put(label, b);
    }
    
    util.Graph<cfg.block.T> graph
    = new util.Graph<cfg.block.T>(m.classId+"_"+m.id);
    
    for(cfg.block.T block : m.blocks) {
      graph.addNode(block);
    }
    for(cfg.block.T block : m.blocks) {
      cfg.block.Block b = (cfg.block.Block)block;
      cfg.transfer.T transfer = b.transfer;
      if (transfer instanceof cfg.transfer.Goto){
        cfg.transfer.Goto gotoo = (cfg.transfer.Goto)transfer;
        cfg.block.T to = map.get(gotoo.label);
        graph.addEdge(block, to);
      }
      else if (transfer instanceof cfg.transfer.If){
        cfg.transfer.If iff = (cfg.transfer.If)transfer;
        cfg.block.T truee = map.get(iff.truee);
        graph.addEdge(block, truee);
        cfg.block.T falsee = map.get(iff.falsee);
        graph.addEdge(block, falsee);
      }
    }
    graph.visualize();
    return;
  }

  @Override
  public void visit(cfg.mainMethod.MainMethod m)
  {
    java.util.HashMap<util.Label, cfg.block.T> map
    = new java.util.HashMap<util.Label, cfg.block.T>();
    for(cfg.block.T block : m.blocks) {
      cfg.block.Block b = (cfg.block.Block)block;
      util.Label label = b.label;
      map.put(label, b);
    }
    
    util.Graph<cfg.block.T> graph
    = new util.Graph<>("Tiger_main");
    
    for(cfg.block.T block : m.blocks) {
      graph.addNode(block);
    }
    for(cfg.block.T block : m.blocks) {
      cfg.block.Block b = (cfg.block.Block)block;
      cfg.transfer.T transfer = b.transfer;
      if (transfer instanceof cfg.transfer.Goto){
        cfg.transfer.Goto gotoo = (cfg.transfer.Goto)transfer;
        cfg.block.T to = map.get(gotoo.label);
        graph.addEdge(block, to);
      }
      else if (transfer instanceof cfg.transfer.If){
        cfg.transfer.If iff = (cfg.transfer.If)transfer;
        cfg.block.T truee = map.get(iff.truee);
        graph.addEdge(block, truee);
        cfg.block.T falsee = map.get(iff.falsee);
        graph.addEdge(block, falsee);
      }
    }
    graph.visualize();
    return;
  }

  // vtables
  @Override
  public void visit(cfg.vtable.Vtable v)
  {
    return;
  }

  // class
  @Override
  public void visit(cfg.classs.Class c)
  {
    return;
  }

  // program
  @Override
  public void visit(cfg.program.Program p)
  {
    // we'd like to output to a file, rather than the "stdout".
    for (cfg.method.T m : p.methods) {
      m.accept(this);
    }    
    p.mainMethod.accept(this);
  }

}
