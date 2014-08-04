package cfg.optimizations;

import java.util.HashMap;
import java.util.HashSet;

import cfg.Cfg.Block;
import cfg.Cfg.Block.BlockSingle;
import cfg.Cfg.Class.ClassSingle;
import cfg.Cfg.Dec.DecSingle;
import cfg.Cfg.MainMethod.MainMethodSingle;
import cfg.Cfg.Method.MethodSingle;
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
import cfg.Cfg.Transfer;
import cfg.Cfg.Transfer.Goto;
import cfg.Cfg.Transfer.If;
import cfg.Cfg.Transfer.Return;
import cfg.Cfg.Type.ClassType;
import cfg.Cfg.Type.IntArrayType;
import cfg.Cfg.Type.IntType;
import cfg.Cfg.Vtable.VtableSingle;

public class ReachingDefinition implements cfg.Visitor
{
  // gen, kill for one statement
  private HashSet<Stm.T> oneStmGen;
  private HashSet<Stm.T> oneStmKill;

  // gen, kill for one transfer
  private HashSet<Stm.T> oneTransferGen;
  private HashSet<Stm.T> oneTransferKill;

  // gen, kill for statements
  private HashMap<Stm.T, HashSet<Stm.T>> stmGen;
  private HashMap<Stm.T, HashSet<Stm.T>> stmKill;

  // gen, kill for transfers
  private HashMap<Transfer.T, HashSet<Stm.T>> transferGen;
  private HashMap<Transfer.T, HashSet<Stm.T>> transferKill;

  // gen, kill for blocks
  private HashMap<Block.T, HashSet<Stm.T>> blockGen;
  private HashMap<Block.T, HashSet<Stm.T>> blockKill;

  // in, out for blocks
  private HashMap<Block.T, HashSet<Stm.T>> blockIn;
  private HashMap<Block.T, HashSet<Stm.T>> blockOut;

  // in, out for statements
  public HashMap<Stm.T, HashSet<Stm.T>> stmIn;
  public HashMap<Stm.T, HashSet<Stm.T>> stmOut;

  // liveIn, liveOut for transfer
  public HashMap<Transfer.T, HashSet<Stm.T>> transferIn;
  public HashMap<Transfer.T, HashSet<Stm.T>> transferOut;

  public ReachingDefinition()
  {
    this.oneStmGen = new HashSet<>();
    this.oneStmKill = new HashSet<>();

    this.oneTransferGen = new HashSet<>();
    this.oneTransferKill = new HashSet<>();

    this.stmGen = new HashMap<>();
    this.stmKill = new HashMap<>();

    this.transferGen = new HashMap<>();
    this.transferKill = new HashMap<>();

    this.blockGen = new HashMap<>();
    this.blockKill = new HashMap<>();

    this.blockIn = new HashMap<>();
    this.blockOut = new HashMap<>();

    this.stmIn = new HashMap<>();
    this.stmOut = new HashMap<>();

    this.transferIn = new HashMap<>();
    this.transferOut = new HashMap<>();
  }

  // /////////////////////////////////////////////////////
  // utilities

  // /////////////////////////////////////////////////////
  // operand
  @Override
  public void visit(Int operand)
  {
  }

  @Override
  public void visit(Var operand)
  {
  }

  // statements
  @Override
  public void visit(Add s)
  {
  }

  @Override
  public void visit(InvokeVirtual s)
  {
  }

  @Override
  public void visit(Lt s)
  {
  }

  @Override
  public void visit(Move s)
  {
  }

  @Override
  public void visit(NewObject s)
  {
  }

  @Override
  public void visit(Print s)
  {
  }

  @Override
  public void visit(Sub s)
  {
  }

  @Override
  public void visit(Times s)
  {
  }

  // transfer
  @Override
  public void visit(If s)
  {
  }

  @Override
  public void visit(Goto s)
  {
    return;
  }

  @Override
  public void visit(Return s)
  {
  }

  // type
  @Override
  public void visit(ClassType t)
  {
  }

  @Override
  public void visit(IntType t)
  {
  }

  @Override
  public void visit(IntArrayType t)
  {
  }

  // dec
  @Override
  public void visit(DecSingle d)
  {
  }

  // block
  @Override
  public void visit(BlockSingle b)
  {
  }

  // method
  @Override
  public void visit(MethodSingle m)
  {
    // Five steps:
    // Step 0: for each argument or local variable "x" in the
    // method m, calculate x's definition site set def(x).
    // Your code here:

    // Step 1: calculate the "gen" and "kill" sets for each
    // statement and transfer

    // Step 2: calculate the "gen" and "kill" sets for each block.
    // For this, you should visit statements and transfers in a
    // block sequentially.
    // Your code here:

    // Step 3: calculate the "in" and "out" sets for each block
    // Note that to speed up the calculation, you should use
    // a topo-sort order of the CFG blocks, and
    // crawl through the blocks in that order.
    // And also you should loop until a fix-point is reached.
    // Your code here:

    // Step 4: calculate the "in" and "out" sets for each
    // statement and transfer
    // Your code here:

  }

  @Override
  public void visit(MainMethodSingle m)
  {
    // Five steps:
    // Step 0: for each argument or local variable "x" in the
    // method m, calculate x's definition site set def(x).
    // Your code here:

    // Step 1: calculate the "gen" and "kill" sets for each
    // statement and transfer

    // Step 2: calculate the "gen" and "kill" sets for each block.
    // For this, you should visit statements and transfers in a
    // block sequentially.
    // Your code here:

    // Step 3: calculate the "in" and "out" sets for each block
    // Note that to speed up the calculation, you should use
    // a topo-sort order of the CFG blocks, and
    // crawl through the blocks in that order.
    // And also you should loop until a fix-point is reached.
    // Your code here:

    // Step 4: calculate the "in" and "out" sets for each
    // statement and transfer
    // Your code here:
  }

  // vtables
  @Override
  public void visit(VtableSingle v)
  {
  }

  // class
  @Override
  public void visit(ClassSingle c)
  {
  }

  // program
  @Override
  public void visit(ProgramSingle p)
  {
  }

}
