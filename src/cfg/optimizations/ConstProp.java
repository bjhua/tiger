package cfg.optimizations;

import cfg.Cfg.Stm.AssignArray;
import cfg.Cfg.Block.BlockSingle;
import cfg.Cfg.Class.ClassSingle;
import cfg.Cfg.Dec.DecSingle;
import cfg.Cfg.MainMethod.MainMethodSingle;
import cfg.Cfg.Method.MethodSingle;
import cfg.Cfg.Operand.Int;
import cfg.Cfg.Operand.Var;
import cfg.Cfg.Program;
import cfg.Cfg.Program.ProgramSingle;
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
import cfg.Cfg.Transfer.Goto;
import cfg.Cfg.Transfer.If;
import cfg.Cfg.Transfer.Return;
import cfg.Cfg.Type.ClassType;
import cfg.Cfg.Type.IntArrayType;
import cfg.Cfg.Type.IntType;
import cfg.Cfg.Vtable.VtableSingle;

public class ConstProp implements cfg.Visitor
{
  public Program.T program;
  
  public ConstProp()
  {
    this.program = null;
  } 

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
  }

  @Override
  public void visit(MainMethodSingle m)
  {
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
    this.program = p;
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
