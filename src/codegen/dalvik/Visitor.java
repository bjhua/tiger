package codegen.dalvik;

import codegen.dalvik.Ast.Class.ClassSingle;
import codegen.dalvik.Ast.Method.MethodSingle;
import codegen.dalvik.Ast.Type.*;
import codegen.dalvik.Ast.Stm.*;
import codegen.dalvik.Ast.Dec.*;
import codegen.dalvik.Ast.Program.*;
import codegen.dalvik.Ast.MainClass.*;

public interface Visitor
{
  // statements
  public void visit(ReturnObject s);

  public void visit(Goto32 s);

  public void visit(Iflt s);

  public void visit(Ifne s);
  
  public void visit(Ifnez s);

  public void visit(Mulint s);

  public void visit(Return s);

  public void visit(Subint s);

  public void visit(Invokevirtual s);

  public void visit(LabelJ s);
  
  public void visit(Move16 s);
  
  public void visit(Moveobject16 s);

  public void visit(Const s);

  public void visit(Print s);

  public void visit(NewInstance s);

  // type
  public void visit(ClassType t);

  public void visit(Int t);

  public void visit(IntArray t);

  // dec
  public void visit(DecSingle d);

  // method
  public void visit(MethodSingle m);

  // class
  public void visit(ClassSingle c);

  // main class
  public void visit(MainClassSingle c);

  // program
  public void visit(ProgramSingle p);
}
