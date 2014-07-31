package codegen.C;

import codegen.C.Ast.Class.ClassSingle;
import codegen.C.Ast.Dec.DecSingle;
import codegen.C.Ast.Exp.Add;
import codegen.C.Ast.Exp.And;
import codegen.C.Ast.Exp.ArraySelect;
import codegen.C.Ast.Exp.Call;
import codegen.C.Ast.Exp.Id;
import codegen.C.Ast.Exp.Length;
import codegen.C.Ast.Exp.Lt;
import codegen.C.Ast.Exp.NewIntArray;
import codegen.C.Ast.Exp.NewObject;
import codegen.C.Ast.Exp.Not;
import codegen.C.Ast.Exp.Num;
import codegen.C.Ast.Exp.Sub;
import codegen.C.Ast.Exp.This;
import codegen.C.Ast.Exp.Times;
import codegen.C.Ast.MainMethod.MainMethodSingle;
import codegen.C.Ast.Method.MethodSingle;
import codegen.C.Ast.Program.ProgramSingle;
import codegen.C.Ast.Stm.Assign;
import codegen.C.Ast.Stm.AssignArray;
import codegen.C.Ast.Stm.Block;
import codegen.C.Ast.Stm.If;
import codegen.C.Ast.Stm.Print;
import codegen.C.Ast.Stm.While;
import codegen.C.Ast.Type.ClassType;
import codegen.C.Ast.Type.Int;
import codegen.C.Ast.Type.IntArray;
import codegen.C.Ast.Vtable.VtableSingle;

public interface Visitor
{
  // expressions
  public void visit(Add e);

  public void visit(And e);

  public void visit(ArraySelect e);

  public void visit(Call e);

  public void visit(Id e);

  public void visit(Length e);

  public void visit(Lt e);

  public void visit(NewIntArray e);

  public void visit(NewObject e);

  public void visit(Not e);

  public void visit(Num e);

  public void visit(Sub e);

  public void visit(This e);

  public void visit(Times e);

  // statements
  public void visit(Assign s);

  public void visit(AssignArray s);

  public void visit(Block s);

  public void visit(If s);

  public void visit(Print s);

  public void visit(While s);

  // type
  public void visit(ClassType t);

  public void visit(Int t);

  public void visit(IntArray t);

  // dec
  public void visit(DecSingle d);

  // method
  public void visit(MethodSingle m);

  // main method
  public void visit(MainMethodSingle m);

  // vtable
  public void visit(VtableSingle v);

  // class
  public void visit(ClassSingle c);

  // program
  public void visit(ProgramSingle p);
}
