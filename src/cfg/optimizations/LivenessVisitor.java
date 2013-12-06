package cfg.optimizations;

public class LivenessVisitor implements cfg.Visitor
{
  // gen, kill for one statement
  private java.util.HashSet<String> oneStmGen;
  private java.util.HashSet<String> oneStmKill;

  // gen, kill for one transfer
  private java.util.HashSet<String> oneTransferGen;
  private java.util.HashSet<String> oneTransferKill;

  // gen, kill for statements
  private java.util.HashMap<cfg.stm.T, java.util.HashSet<String>> stmGen;
  private java.util.HashMap<cfg.stm.T, java.util.HashSet<String>> stmKill;

  // gen, kill for transfers
  private java.util.HashMap<cfg.transfer.T, java.util.HashSet<String>> transferGen;
  private java.util.HashMap<cfg.transfer.T, java.util.HashSet<String>> transferKill;

  // gen, kill for blocks
  private java.util.HashMap<cfg.block.T, java.util.HashSet<String>> blockGen;
  private java.util.HashMap<cfg.block.T, java.util.HashSet<String>> blockKill;

  // liveIn, liveOut for blocks
  private java.util.HashMap<cfg.block.T, java.util.HashSet<String>> blockLiveIn;
  private java.util.HashMap<cfg.block.T, java.util.HashSet<String>> blockLiveOut;

  // liveIn, liveOut for statements
  public java.util.HashMap<cfg.stm.T, java.util.HashSet<String>> stmLiveIn;
  public java.util.HashMap<cfg.stm.T, java.util.HashSet<String>> stmLiveOut;

  // liveIn, liveOut for transfer
  public java.util.HashMap<cfg.transfer.T, java.util.HashSet<String>> transferLiveIn;
  public java.util.HashMap<cfg.transfer.T, java.util.HashSet<String>> transferLiveOut;

  // As you will walk the tree for many times, so
  // it will be useful to recored which is which:
  enum Liveness_Kind_t
  {
    None, StmGenKill, BlockGenKill, BlockInOut, StmInOut,
  }

  private Liveness_Kind_t kind = Liveness_Kind_t.None;

  public LivenessVisitor()
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

    this.blockLiveIn = new java.util.HashMap<>();
    this.blockLiveOut = new java.util.HashMap<>();

    this.stmLiveIn = new java.util.HashMap<>();
    this.stmLiveOut = new java.util.HashMap<>();

    this.transferLiveIn = new java.util.HashMap<>();
    this.transferLiveOut = new java.util.HashMap<>();

    this.kind = Liveness_Kind_t.None;
  }

  // /////////////////////////////////////////////////////
  // utilities

  private java.util.HashSet<String> getOneStmGenAndClear()
  {
    java.util.HashSet<String> temp = this.oneStmGen;
    this.oneStmGen = new java.util.HashSet<>();
    return temp;
  }

  private java.util.HashSet<String> getOneStmKillAndClear()
  {
    java.util.HashSet<String> temp = this.oneStmKill;
    this.oneStmKill = new java.util.HashSet<>();
    return temp;
  }

  private java.util.HashSet<String> getOneTransferGenAndClear()
  {
    java.util.HashSet<String> temp = this.oneTransferGen;
    this.oneTransferGen = new java.util.HashSet<>();
    return temp;
  }

  private java.util.HashSet<String> getOneTransferKillAndClear()
  {
    java.util.HashSet<String> temp = this.oneTransferKill;
    this.oneTransferKill = new java.util.HashSet<>();
    return temp;
  }

  // /////////////////////////////////////////////////////
  // operand
  @Override
  public void visit(cfg.operand.Int operand)
  {
    return;
  }

  @Override
  public void visit(cfg.operand.Var operand)
  {
    this.oneStmGen.add(operand.id);
    return;
  }

  // statements
  @Override
  public void visit(cfg.stm.Add s)
  {
    this.oneStmKill.add(s.dst);
    // Invariant: accept() of operand modifies "gen"
    s.left.accept(this);
    s.right.accept(this);
    return;
  }

  @Override
  public void visit(cfg.stm.InvokeVirtual s)
  {
    this.oneStmKill.add(s.dst);
    this.oneStmGen.add(s.obj);
    for (cfg.operand.T arg : s.args) {
      arg.accept(this);
    }
    return;
  }

  @Override
  public void visit(cfg.stm.Lt s)
  {
    this.oneStmKill.add(s.dst);
    // Invariant: accept() of operand modifies "gen"
    s.left.accept(this);
    s.right.accept(this);
    return;
  }

  @Override
  public void visit(cfg.stm.Move s)
  {
    this.oneStmKill.add(s.dst);
    // Invariant: accept() of operand modifies "gen"
    s.src.accept(this);
    return;
  }

  @Override
  public void visit(cfg.stm.NewObject s)
  {
    this.oneStmKill.add(s.dst);
    return;
  }

  @Override
  public void visit(cfg.stm.Print s)
  {
    s.arg.accept(this);
    return;
  }

  @Override
  public void visit(cfg.stm.Sub s)
  {
    this.oneStmKill.add(s.dst);
    // Invariant: accept() of operand modifies "gen"
    s.left.accept(this);
    s.right.accept(this);
    return;
  }

  @Override
  public void visit(cfg.stm.Times s)
  {
    this.oneStmKill.add(s.dst);
    // Invariant: accept() of operand modifies "gen"
    s.left.accept(this);
    s.right.accept(this);
    return;
  }

  // transfer
  @Override
  public void visit(cfg.transfer.If s)
  {
    // Invariant: accept() of operand modifies "gen"
    s.operand.accept(this);
    return;
  }

  @Override
  public void visit(cfg.transfer.Goto s)
  {
    return;
  }

  @Override
  public void visit(cfg.transfer.Return s)
  {
    // Invariant: accept() of operand modifies "gen"
    s.operand.accept(this);
    return;
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

  // utility functions:
  private void calculateStmTransferGenKill(cfg.block.Block b)
  {
    for (cfg.stm.T s : b.stms) {
      this.oneStmGen = new java.util.HashSet<>();
      this.oneStmKill = new java.util.HashSet<>();      
      s.accept(this);
      this.stmGen.put(s, this.oneStmGen);
      this.stmKill.put(s, this.oneStmKill);
      if (control.Control.isTracing("liveness.step1")) {
        System.out.print("\ngen, kill for statement:");
        s.toString();
        System.out.print("\ngen is:");
        for (String str : this.oneStmGen) {
          System.out.print(str + ", ");
        }
        System.out.print("\nkill is:");
        for (String str : this.oneStmKill) {
          System.out.print(str + ", ");
        }
      }
    }
    this.oneTransferGen = new java.util.HashSet<>();
    this.oneTransferKill = new java.util.HashSet<>();
    b.transfer.accept(this);
    this.transferGen.put(b.transfer, this.oneTransferGen);
    this.transferKill.put(b.transfer, this.oneTransferGen);
    if (control.Control.isTracing("liveness.step1")) {
      System.out.print("\ngen, kill for transfer:");
      b.toString();
      System.out.print("\ngen is:");
      for (String str : this.oneTransferGen) {
        System.out.print(str + ", ");
      }
      System.out.println("\nkill is:");
      for (String str : this.oneTransferKill) {
        System.out.print(str + ", ");
      }
    }
    return;
  }

  // block
  @Override
  public void visit(cfg.block.Block b)
  {
    switch (this.kind) {
    case StmGenKill:
      calculateStmTransferGenKill(b);
      break;
    default:
      // Your code here:
      return;
    }
  }

  // method
  @Override
  public void visit(cfg.method.Method m)
  {
    // Four steps:
    // Step 1: calculate the "gen" and "kill" sets for each
    // statement and transfer
    this.kind = Liveness_Kind_t.StmGenKill;
    for (cfg.block.T block : m.blocks) {
      block.accept(this);
    }

    // Step 2: calculate the "gen" and "kill" sets for each block.
    // For this, you should visit statements and transfers in a
    // block in a reverse order.
    // Your code here:

    // Step 3: calculate the "liveIn" and "liveOut" sets for each block
    // Note that to speed up the calculation, you should first
    // calculate a reverse topo-sort order of the CFG blocks, and
    // crawl through the blocks in that order.
    // And also you should loop until a fix-point is reached.
    // Your code here:

    // Step 4: calculate the "liveIn" and "liveOut" sets for each
    // statement and transfer
    // Your code here:

  }

  @Override
  public void visit(cfg.mainMethod.MainMethod m)
  {
    // Four steps:
    // Step 1: calculate the "gen" and "kill" sets for each
    // statement and transfer
    this.kind = Liveness_Kind_t.StmGenKill;
    for (cfg.block.T block : m.blocks) {
      block.accept(this);
    }

    // Step 2: calculate the "gen" and "kill" sets for each block.
    // For this, you should visit statements and transfers in a
    // block in a reverse order.
    // Your code here:

    // Step 3: calculate the "liveIn" and "liveOut" sets for each block
    // Note that to speed up the calculation, you should first
    // calculate a reverse topo-sort order of the CFG blocks, and
    // crawl through the blocks in that order.
    // And also you should loop until a fix-point is reached.
    // Your code here:

    // Step 4: calculate the "liveIn" and "liveOut" sets for each
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
    p.mainMethod.accept(this);
    for (cfg.method.T mth : p.methods) {
      mth.accept(this);
    }
    return;
  }

}
