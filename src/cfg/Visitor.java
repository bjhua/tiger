package cfg;

public interface Visitor
{
  // operand
  public void visit(cfg.operand.Int o);

  public void visit(cfg.operand.Var o);

  // type
  public void visit(cfg.type.Class t);

  public void visit(cfg.type.Int t);

  public void visit(cfg.type.IntArray t);

  // dec
  public void visit(cfg.dec.Dec d);

  // transfer
  public void visit(cfg.transfer.If t);

  public void visit(cfg.transfer.Goto t);

  public void visit(cfg.transfer.Return t);

  // statement:
  public void visit(cfg.stm.Add m);

  public void visit(cfg.stm.InvokeVirtual m);

  public void visit(cfg.stm.Lt m);

  public void visit(cfg.stm.Move m);
  
  public void visit(cfg.stm.NewObject m);
  
  public void visit(cfg.stm.Print m);

  public void visit(cfg.stm.Sub m);

  public void visit(cfg.stm.Times m);

  // block
  public void visit(cfg.block.Block b);

  // method
  public void visit(cfg.method.Method m);

  // vtable
  public void visit(cfg.vtable.Vtable v);

  // class
  public void visit(cfg.classs.Class c);

  // main method
  public void visit(cfg.mainMethod.MainMethod c);

  // program
  public void visit(cfg.program.Program p);
}
