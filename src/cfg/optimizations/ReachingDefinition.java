package cfg.optimizations;

public class ReachingDefinition implements cfg.Visitor
{
  // gen, kill for one statement
  private java.util.HashSet<cfg.stm.T> oneStmGen;
  private java.util.HashSet<cfg.stm.T> oneStmKill;

  // gen, kill for one transfer
  private java.util.HashSet<cfg.stm.T> oneTransferGen;
  private java.util.HashSet<cfg.stm.T> oneTransferKill;

  // gen, kill for statements
  private java.util.HashMap<cfg.stm.T, java.util.HashSet<cfg.stm.T>> stmGen;
  private java.util.HashMap<cfg.stm.T, java.util.HashSet<cfg.stm.T>> stmKill;

  // gen, kill for transfers
  private java.util.HashMap<cfg.transfer.T, java.util.HashSet<cfg.stm.T>> transferGen;
  private java.util.HashMap<cfg.transfer.T, java.util.HashSet<cfg.stm.T>> transferKill;

  // gen, kill for blocks
  private java.util.HashMap<cfg.block.T, java.util.HashSet<cfg.stm.T>> blockGen;
  private java.util.HashMap<cfg.block.T, java.util.HashSet<cfg.stm.T>> blockKill;

  // in, out for blocks
  private java.util.HashMap<cfg.block.T, java.util.HashSet<cfg.stm.T>> blockIn;
  private java.util.HashMap<cfg.block.T, java.util.HashSet<cfg.stm.T>> blockOut;

  // in, out for statements
  public java.util.HashMap<cfg.stm.T, java.util.HashSet<cfg.stm.T>> stmIn;
  public java.util.HashMap<cfg.stm.T, java.util.HashSet<cfg.stm.T>> stmOut;

  // liveIn, liveOut for transfer
  public java.util.HashMap<cfg.transfer.T, java.util.HashSet<cfg.stm.T>> transferIn;
  public java.util.HashMap<cfg.transfer.T, java.util.HashSet<cfg.stm.T>> transferOut;

  public ReachingDefinition()
  {
    this.oneStmGen = new java.util.HashSet<>();
    this.oneStmKill = new java.util.HashSet<>();

    this.oneTransferGen = new java.util.HashSet<>();
    this.oneTransferKill = new java.util.HashSet<>();

    this.stmGen = new java.util.HashMap<>();
    this.stmKill = new java.util.HashMap<>();

    this.transferGen = new java.util.HashMap<>();
    this.transferKill = new java.util.HashMap<>();

    this.blockGen = new java.util.HashMap<>();
    this.blockKill = new java.util.HashMap<>();

    this.blockIn = new java.util.HashMap<>();
    this.blockOut = new java.util.HashMap<>();

    this.stmIn = new java.util.HashMap<>();
    this.stmOut = new java.util.HashMap<>();

    this.transferIn = new java.util.HashMap<>();
    this.transferOut = new java.util.HashMap<>();
  }

  // /////////////////////////////////////////////////////
  // utilities

  // /////////////////////////////////////////////////////
  // operand
  @Override
  public void visit(cfg.operand.Int operand)
  {
  }

  @Override
  public void visit(cfg.operand.Var operand)
  {
  }

  // statements
  @Override
  public void visit(cfg.stm.Add s)
  {
  }

  @Override
  public void visit(cfg.stm.InvokeVirtual s)
  {
  }

  @Override
  public void visit(cfg.stm.Lt s)
  {
  }

  @Override
  public void visit(cfg.stm.Move s)
  {
  }

  @Override
  public void visit(cfg.stm.NewObject s)
  {
  }

  @Override
  public void visit(cfg.stm.Print s)
  {
  }

  @Override
  public void visit(cfg.stm.Sub s)
  {
  }

  @Override
  public void visit(cfg.stm.Times s)
  {
  }

  // transfer
  @Override
  public void visit(cfg.transfer.If s)
  {
  }

  @Override
  public void visit(cfg.transfer.Goto s)
  {
    return;
  }

  @Override
  public void visit(cfg.transfer.Return s)
  {
  }

  // type
  @Override
  public void visit(cfg.type.Class t)
  {
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
  }

  // block
  @Override
  public void visit(cfg.block.Block b)
  {
  }

  // method
  @Override
  public void visit(cfg.method.Method m)
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
  public void visit(cfg.mainMethod.MainMethod m)
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
  public void visit(cfg.vtable.Vtable v)
  {
  }

  // class
  @Override
  public void visit(cfg.classs.Class c)
  {
  }

  // program
  @Override
  public void visit(cfg.program.Program p)
  {
  }

}
