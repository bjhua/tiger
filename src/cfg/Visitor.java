package cfg;

import cfg.Cfg.Block.BlockSingle;
import cfg.Cfg.Class.ClassSingle;
import cfg.Cfg.Dec.DecSingle;
import cfg.Cfg.MainMethod.MainMethodSingle;
import cfg.Cfg.Method.MethodSingle;
import cfg.Cfg.Operand.Int;
import cfg.Cfg.Operand.Var;
import cfg.Cfg.Program.ProgramSingle;
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
import cfg.Cfg.Vtable.VtableSingle;

public interface Visitor
{
  // operand
  public void visit(Int o);

  public void visit(Var o);

  // type
  public void visit(ClassType t);

  public void visit(IntType t);

  public void visit(IntArrayType t);

  // dec
  public void visit(DecSingle d);

  // transfer
  public void visit(If t);

  public void visit(Goto t);

  public void visit(Return t);

  // statement:
  public void visit(Add m);

  public void visit(InvokeVirtual m);

  public void visit(Lt m);

  public void visit(Move m);
  
  public void visit(NewObject m);
  
  public void visit(Print m);

  public void visit(Sub m);

  public void visit(Times m);

  // block
  public void visit(BlockSingle b);

  // method
  public void visit(MethodSingle m);

  // vtable
  public void visit(VtableSingle v);

  // class
  public void visit(ClassSingle c);

  // main method
  public void visit(MainMethodSingle c);

  // program
  public void visit(ProgramSingle p);
}
