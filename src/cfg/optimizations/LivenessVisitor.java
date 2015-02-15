package cfg.optimizations;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import cfg.Cfg.Stm.AssignArray;
import cfg.Cfg.Block;
import cfg.Cfg.Block.BlockSingle;
import cfg.Cfg.Class.ClassSingle;
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
import cfg.Cfg.Stm.And;
import cfg.Cfg.Stm.ArraySelect;
import cfg.Cfg.Stm.InvokeVirtual;
import cfg.Cfg.Stm.Length;
import cfg.Cfg.Stm.Lt;
import cfg.Cfg.Stm.Move;
import cfg.Cfg.Stm.NewIntArray;
import cfg.Cfg.Stm.NewObject;
import cfg.Cfg.Stm.Not;
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

public class LivenessVisitor implements cfg.Visitor
{
  // gen, kill for one statement
  private HashSet<String> oneStmGen;
  private HashSet<String> oneStmKill;

  // gen, kill for one transfer
  private HashSet<String> oneTransferGen;
  private HashSet<String> oneTransferKill;

  // gen, kill for statements
  private HashMap<Stm.T, HashSet<String>> stmGen;
  private HashMap<Stm.T, HashSet<String>> stmKill;

  // gen, kill for transfers
  private HashMap<Transfer.T, HashSet<String>> transferGen;
  private HashMap<Transfer.T, HashSet<String>> transferKill;

  // gen, kill for blocks
  private HashMap<Block.T, HashSet<String>> blockGen;
  private HashMap<Block.T, HashSet<String>> blockKill;

  // liveIn, liveOut for blocks
  private HashMap<Block.T, HashSet<String>> blockLiveIn;
  private HashMap<Block.T, HashSet<String>> blockLiveOut;

  // liveIn, liveOut for statements
  public HashMap<Stm.T, HashSet<String>> stmLiveIn;
  public HashMap<Stm.T, HashSet<String>> stmLiveOut;

  // liveIn, liveOut for transfer
  public HashMap<Transfer.T, HashSet<String>> transferLiveIn;
  public java.util.HashMap<Transfer.T, java.util.HashSet<String>> transferLiveOut;

  // As you will walk the tree for many times, so
  // it will be useful to recored which is which:
  enum Liveness_Kind_t
  {
    None, StmGenKill, BlockGenKill, BlockInOut, StmInOut,
  }

  private Liveness_Kind_t kind = Liveness_Kind_t.None;

  public LivenessVisitor()
  {
    this.oneStmGen = new HashSet<>();
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
  public void visit(Int operand)
  {
    return;
  }

  @Override
  public void visit(Var operand)
  {
    this.oneStmGen.add(operand.id);
    return;
  }

  // statements
  @Override
  public void visit(Add s)
  {
    this.oneStmKill.add(s.dst);
    // Invariant: accept() of operand modifies "gen"
    s.left.accept(this);
    s.right.accept(this);
    return;
  }

  @Override
  public void visit(InvokeVirtual s)
  {
    this.oneStmKill.add(s.dst);
    this.oneStmGen.add(s.obj);
    for (Operand.T arg : s.args) 
    {
      arg.accept(this);
    }
    return;
  }

  @Override
  public void visit(Lt s)
  {
    this.oneStmKill.add(s.dst);
    // Invariant: accept() of operand modifies "gen"
    s.left.accept(this);
    s.right.accept(this);
    return;
  }

  @Override
  public void visit(Move s)
  {
    this.oneStmKill.add(s.dst);
    // Invariant: accept() of operand modifies "gen"
    s.src.accept(this);
    return;
  }

  @Override
  public void visit(NewObject s)
  {
    this.oneStmKill.add(s.dst);
    return;
  }

  @Override
  public void visit(Print s)
  {
    s.arg.accept(this);
    return;
  }

  @Override
  public void visit(Sub s)
  {
    this.oneStmKill.add(s.dst);
    // Invariant: accept() of operand modifies "gen"
    s.left.accept(this);
    s.right.accept(this);
    return;
  }

  @Override
  public void visit(Times s)
  {
    this.oneStmKill.add(s.dst);
    // Invariant: accept() of operand modifies "gen"
    s.left.accept(this);
    s.right.accept(this);
    return;
  }

  // transfer
  @Override
  public void visit(If s)
  {
    // Invariant: accept() of operand modifies "gen"
	if(s.operand instanceof Operand.Var)
	{
		String id = ((Operand.Var)s.operand).id;
		oneTransferGen.add(id);
	}
	return;
  }

  @Override
  public void visit(Goto s)
  {
    return;
  }

  @Override
  public void visit(Return s)
  {
    // Invariant: accept() of operand modifies "gen"
	if(s.operand instanceof Operand.Var)
	{
		String id = ((Operand.Var)s.operand).id;
		oneTransferGen.add(id);
	}
	return;
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

  // utility functions:
  HashMap<BlockSingle,HashMap<Stm.T,HashMap<String,Boolean>>> Map = 
		  new HashMap<BlockSingle,HashMap<Stm.T,HashMap<String,Boolean>>>();
  
  //Step 1
  private void calculateStmTransferGenKill(BlockSingle b)
  {
	HashMap<Stm.T,HashMap<String,Boolean>> map1 = new HashMap<Stm.T,HashMap<String,Boolean>>();
	LinkedList<String> exception = new LinkedList<String>();
    for (Stm.T s : b.stms) 
    {
      HashMap<String,Boolean> map = new HashMap<String,Boolean>();
      this.oneStmGen = new java.util.HashSet<>();
      this.oneStmKill = new java.util.HashSet<>();      
      s.accept(this);
      exception.addAll(this.oneStmGen);
      for(String str:this.oneStmKill)
      {
    	  if(exception.contains(str))
    	  {
    		  b.exceptions.add(str);
    		  map.put(str, true);
    	  }
    	  else
    		  map.put(str, false);
      }
      this.stmGen.put(s, this.oneStmGen);
      this.stmKill.put(s, this.oneStmKill);
      map1.put(s, map);
      if (control.Control.isTracing("liveness.step1")) 
      {
        System.out.print("\ngen, kill for statement:");
        System.out.println(s.toString());
        System.out.print("gen is:");
        for (String str : this.oneStmGen) 
        {
          System.out.print(str + ", ");
        }
        System.out.print("\nkill is:");
        for (String str : this.oneStmKill) 
        {
          System.out.print(str + ", ");
        }
      }
    }
    Map.put(b, map1);
    this.oneTransferGen = new java.util.HashSet<>();
    this.oneTransferKill = new java.util.HashSet<>();
    b.transfer.accept(this);
    this.transferGen.put(b.transfer, this.oneTransferGen);
    this.transferKill.put(b.transfer, this.oneTransferGen);
    if (control.Control.isTracing("liveness.step1")) 
    {
      System.out.print("\ngen, kill for transfer:");
      b.toString();
      System.out.print("\ngen is:");
      for (String str : this.oneTransferGen) 
      {
        System.out.print(str + ", ");
      }
      System.out.println("\nkill is:");
      for (String str : this.oneTransferKill) 
      {
        System.out.print(str + ", ");
      }
    }
    return;
  }
  
  //Step 2 caluculate the Block gen && kill
  private void calculateBlockGenKill(BlockSingle b)
  {
	java.util.HashSet<String> blockStmGen = new java.util.HashSet<>();
	java.util.HashSet<String> blockStmKill = new java.util.HashSet<>();
	
	if(b.transfer!=null)
		if(this.transferGen.get(b.transfer)!=null)
			blockStmGen.addAll(this.transferGen.get(b.transfer));
	for (int i = b.stms.size() - 1; i >= 0; i--) 
	{
		for (String str : this.stmKill.get(b.stms.get(i))) 
		{
			//it means that the var str is defined in the current block
			if (blockStmGen.contains(str))	
				blockStmGen.remove(str);

			if (!blockStmKill.contains(str))
				blockStmKill.add(str);
		}
		for (String str : this.stmGen.get(b.stms.get(i))) 
		{
			if (!blockStmGen.contains(str))
				blockStmGen.add(str);
		}
	}
	// print
	if (control.Control.isTracing("liveness.step2")) 
	{
		System.out.print("\nblock " + b.label.toString() + " gen is:");
		for (String str : blockStmGen) 
		{
			System.out.print(str + ", ");
		}
		System.out.print("\nblock " + b.label.toString() + " kill is:");
		for (String str : blockStmKill) 
		{
			System.out.print(str + ", ");
		}
		System.out.println("");
	}
	// add to block gen and kill
	this.blockGen.put(b, blockStmGen);
	this.blockKill.put(b, blockStmKill);
    return;
  }
  
  //Step 3
  java.util.HashSet<String> In = new java.util.HashSet<String>();
  boolean Flag = true;
  //逆序计算方法
  private void calculateBlockInOut(BlockSingle b)
  {
	  java.util.HashSet<String> blockIn = (java.util.HashSet<String>) In.clone();
	  java.util.HashSet<String> blockOut = (java.util.HashSet<String>) In.clone();
	  java.util.HashSet<String> temp = new java.util.HashSet<String>();

	  for (String str : this.blockGen.get(b))
	  {
		  if (!blockIn.contains(str))
		  {
			temp.add(str);
		  }
	  }
	  for (String str : this.blockKill.get(b))
	  if (blockIn.contains(str))
	  {
		blockIn.remove(str);
	  }
	  blockIn.addAll(temp);
	  blockIn.addAll(b.exceptions);
	  int num1,num2;
	  if(this.blockLiveIn.get(b)!=null)
		  num1 = this.blockLiveIn.get(b).size();
	  else 
		  num1 = 0;
	  
	  if(this.blockLiveOut.get(b)!=null)
		  num2 = this.blockLiveOut.get(b).size();
	  else 
		  num2 = 0;
	  
	  if(num1!=blockIn.size() || num2!=blockOut.size())
	  {
		  Flag = false;
	  }
	  In = (java.util.HashSet<String>) blockIn.clone();	
	  this.blockLiveIn.put(b, blockIn);
	  this.blockLiveOut.put(b, blockOut);
	  return;
  }
  //第二次访问节点计算方法
  private void calculateBlockInOut2(BlockSingle b)
  {
	  java.util.HashSet<String> blockOut = this.blockLiveOut.get(b);
	  java.util.HashSet<String> pseudo_blockIn	= (java.util.HashSet<String>) In.clone();
	  java.util.HashSet<String> temp = new java.util.HashSet<String>();
	  for(String str:pseudo_blockIn)
	  {
		  if(!blockOut.contains(str))
		  {
			  blockOut.add(str);
		  }
	  }
	  java.util.HashSet<String> blockIn = (HashSet<String>) blockOut.clone();
	  for(String str:this.blockGen.get(b))
	  {
		  if(!blockIn.contains(str))
		  {
			temp.add(str);
		  }
	  }
	  for (String str : this.blockKill.get(b))
	  {
		  if (blockIn.contains(str))
		  {
			blockIn.remove(str);
		  }
	  }
	  blockIn.addAll(temp);
	  this.blockLiveIn.put(b, blockIn);
	  this.blockLiveOut.put(b, blockOut);
	  return;
  }
  //Step 4
  private void calculateStmInOut(BlockSingle b)
  {
	    HashMap<Stm.T,HashMap<String,Boolean>> map1 = Map.get(b);
		HashSet<String> newLiveOut = (HashSet<String>) this.blockLiveOut.get(b).clone();
		HashSet<String> newLiveIn = (HashSet<String>)this.blockLiveOut.get(b).clone();
		HashSet<String> newTransferIn = (HashSet<String>) this.transferGen.get(b.transfer);
		newLiveIn.addAll(newTransferIn);
		for(int i = b.stms.size() - 1; i >= 0; i--) 
		{
			HashMap<String,Boolean> map = map1.get(b.stms.get(i));
			newLiveOut = (HashSet<String>) newLiveIn.clone();
			for(String str : this.stmKill.get(b.stms.get(i)))
				if (newLiveIn.contains(str) && !map.get(str))
					newLiveIn.remove(str);
			for(String str : this.stmGen.get(b.stms.get(i)))
			{
				if (!newLiveIn.contains(str))
				{
					newLiveIn.add(str);
				}
			}
			HashSet<String> temp = (HashSet<String>)newLiveIn.clone();
			HashSet<String> temp1 = (HashSet<String>)newLiveOut.clone();
			this.stmLiveIn.put(b.stms.get(i), temp);
			this.stmLiveOut.put(b.stms.get(i), temp1);
			b.stms.get(i).stmLiveIn.addAll(temp);
			b.stms.get(i).stmLiveOut.addAll(temp1);
		}
		if(control.Control.isTracing("liveness.step4")) 
		{
			for(int i = 0; i <= b.stms.size() - 1;i++)
			{
				System.out.print("\nblock " + b.label.toString()
						+ " stm"+ i +" live in is:");
				for(String str : this.stmLiveIn.get(b.stms.get(i))) 
				{
					System.out.print(str + ", ");
				}
				System.out.print("\nblock " + b.label.toString()
						+ " stm"+ i +" live out is:");
				for(String str : this.stmLiveOut.get(b.stms.get(i))) 
				{
					System.out.print(str + ", ");
				}
			}
			System.out.print("\nblock " + b.label.toString()
					+ " transfer live in is:");
			for(String str : newTransferIn) 
			{
				System.out.print(str + ", ");
			}
			System.out.print("\nblock " + b.label.toString()+ " transfer live out is:\n");
		}
	}
  
  private void printBlock(BlockSingle b)
  {
      System.out.print("\nblock " + b.label.toString() + " live in is:");
      for (String str : this.blockLiveIn.get(b)) 
      {
        System.out.print(str + ", ");
      }
      
      System.out.print("\nblock " + b.label.toString() + " live out is:");
      for (String str : this.blockLiveOut.get(b)) 
      {
        System.out.print(str + ", ");
      }
      System.out.println("");
  }
  // block
  @Override
  public void visit(BlockSingle b)
  {
    switch (this.kind) 
    {
    	case StmGenKill:
    		calculateStmTransferGenKill(b);
    		break;
    	case BlockGenKill:
    		calculateBlockGenKill(b);
    		break;
    	case BlockInOut:
    		if(mark==0)
    			calculateBlockInOut(b);
    		else 
    		{
    			calculateBlockInOut2(b);
    			mark = 0;
    		}
    		break;
    	case StmInOut:
    		calculateStmInOut(b);
    		break;
    	default:
    	return;
    }
  }
  
  public void trans(MethodSingle m)
  {
	  for(Block.T block:m.blocks)
	  {
		  util.Label label = ((BlockSingle)block).label;
		  for(Block.T blockkk:m.blocks)
		  {
			  if(!blockkk.equals(block))
			  {
				  if(((BlockSingle)blockkk).labels.contains(label))
				  {
					  BlockSingle blockk = (BlockSingle)block;
					  blockk.forward.add(((BlockSingle)blockkk).label);
				  }
			  }
		  }
	  }
  }
  
  public void flushVisited(MethodSingle m)
  {
	  for(Block.T block:m.blocks)
	  {
		  ((BlockSingle)block).visited = 0;
	  }
  }
  
  public BlockSingle getBlock(MethodSingle m,util.Label label)
  {
	  for(Block.T block:m.blocks)
	  {
		  if(((BlockSingle)block).label.equals(label))
		  {
			  BlockSingle b = (BlockSingle)block;
			  return b;
		  }
	  }
	  System.out.println("getBlock error!");
	  return null;
  }
  // 遍历函数
  int mark = 0;
  public void BlockSort(MethodSingle m,util.Label lable)
  {
	  BlockSingle start,first,second,b1,b2,b3,b4;
	  java.util.HashSet<String> temp;
	  start = getBlock(m,lable);
	  int num3 = start.forward.size(); //前驱个数
	  int num4 = start.labels.size(); //后继个数
	  if(((BlockSingle)start).visited > 1)
	  {
		  mark = 1;
		  start.accept(this);
		  mark = 0;
		  return;
	  }
	  else
		  mark = 0;
	  start.accept(this);
	  int num = start.forward.size();
	  if(num==1)
	  {
		  util.Label label = start.forward.getFirst();
		  first = getBlock(m,label);
		  ((BlockSingle)first).visited++;
		  BlockSort(m,first.label);
		  return;
	  }
	  else if(num==2)
	  {   
		  util.Label label1 = start.forward.get(0);
		  first = getBlock(m,label1);
		  ((BlockSingle)first).visited++;
		  temp = (HashSet<String>)In.clone();
		  BlockSort(m,first.label);
		  util.Label label2 = start.forward.get(1);
		  second = getBlock(m,label2);
		  ((BlockSingle)second).visited++;
		  In = temp;
		  BlockSort(m,second.label);
		  return;
	  }
	  return;
  }

  //method
  @Override
  public void visit(MethodSingle m)
  {
    // Four steps:
    // Step 1: calculate the "gen" and "kill" sets for each
    // statement and transfer
    this.kind = Liveness_Kind_t.StmGenKill;
    for (Block.T block : m.blocks) 
    {
      block.accept(this);
    }
    // Step 2: calculate the "gen" and "kill" sets for each block.
    // For this, you should visit statements and transfers in a
    // block in a reverse order.
    // Your code here:
    this.kind = Liveness_Kind_t.BlockGenKill;
    for(Block.T block:m.blocks)
    {
    	block.accept(this);
    }
    // Step 3: calculate the "liveIn" and "liveOut" sets for each block
    // Note that to speed up the calculation, you should first
    // calculate a reverse topo-sort order of the CFG blocks, and
    // crawl through the blocks in that order.
    // And also you should loop until a fix-point is reached.
    // Your code here:
    trans(m);		//确定每个block的前驱与后继block的个数
    this.kind = Liveness_Kind_t.BlockInOut;
    for(int i =0;i<=2;i++)
    {
    	flushVisited(m);
    	Flag = true;
    	In = new java.util.HashSet<String>();
    	util.Label label = ((BlockSingle)m.blocks.getLast()).label;
    	BlockSort(m,label);
    }
    if (control.Control.isTracing("liveness.step3")) 
      for(int i=m.blocks.size()-1;i >= 0;i--)
      {
    	 printBlock((BlockSingle)m.blocks.get(i));
      }
    // Step 4: calculate the "liveIn" and "liveOut" sets for each
    // statement and transfer
    // Your code here:
    this.kind = Liveness_Kind_t.StmInOut;
	for (int i = m.blocks.size() - 1; i >= 0; i--) 
	{
		m.blocks.get(i).accept(this);
	}
  }

  @Override
  public void visit(MainMethodSingle m)
  {
    // Four steps:
    // Step 1: calculate the "gen" and "kill" sets for each
    // statement and transfer
    this.kind = Liveness_Kind_t.StmGenKill;
    for (Block.T block : m.blocks) 
    {
      block.accept(this);
    }
    // Step 2: calculate the "gen" and "kill" sets for each block.
    // For this, you should visit statements and transfers in a
    // block in a reverse order.
    // Your code here:
    this.kind = Liveness_Kind_t.BlockGenKill;
    for(Block.T block:m.blocks)
    {
    	block.accept(this);
    }
    // Step 3: calculate the "liveIn" and "liveOut" sets for each block
    // Note that to speed up the calculation, you should first
    // calculate a reverse topo-sort order of the CFG blocks, and
    // crawl through the blocks in that order.
    // And also you should loop until a fix-point is reached.
    // Your code here:
    this.kind = Liveness_Kind_t.BlockInOut;
	In = new java.util.HashSet<String>();
    for(int i=m.blocks.size()-1;i >= 0;i--)
    {
    	m.blocks.get(i).accept(this); 	//reverse topo-sort order!
    }
    if (control.Control.isTracing("liveness.step3")) 
	    for(int i=m.blocks.size()-1;i >= 0;i--)
	    {
	    	printBlock((BlockSingle)m.blocks.get(i));
	    }
    // Step 4: calculate the "liveIn" and "liveOut" sets for each
    // statement and transfer
    // Your code here:
    this.kind = Liveness_Kind_t.StmInOut;
	for (int i = m.blocks.size() - 1; i >= 0; i--) 
	{
		m.blocks.get(i).accept(this);
	}
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
    p.mainMethod.accept(this);
    for (Method.T mth : p.methods) {
      mth.accept(this);
    }
    return;
  }

  @Override
  public void visit(NewIntArray newIntArray) {
	// TODO Auto-generated method stub
	
  }

  @Override
  public void visit(Not not) {
	// TODO Auto-generated method stub
	
  }

  @Override
  public void visit(Length length) {
	// TODO Auto-generated method stub
	
  }
 
  @Override
  public void visit(And and) {
	// TODO Auto-generated method stub
	
  }

  @Override
  public void visit(ArraySelect arr) {
	// TODO Auto-generated method stub
	
  }

  @Override
  public void visit(AssignArray assignArray) {
	// TODO Auto-generated method stub
	
  }

}
