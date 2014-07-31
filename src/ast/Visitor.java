package ast;

import ast.Ast.Class;
import ast.Ast.Dec;
import ast.Ast.Exp.Add;
import ast.Ast.Exp.And;
import ast.Ast.Exp.ArraySelect;
import ast.Ast.Exp.Call;
import ast.Ast.Exp.False;
import ast.Ast.Exp.Id;
import ast.Ast.Exp.Length;
import ast.Ast.Exp.Lt;
import ast.Ast.Exp.NewIntArray;
import ast.Ast.Exp.NewObject;
import ast.Ast.Exp.Not;
import ast.Ast.Exp.Num;
import ast.Ast.Exp.Sub;
import ast.Ast.Exp.This;
import ast.Ast.Exp.Times;
import ast.Ast.Exp.True;
import ast.Ast.MainClass.MainClassSingle;
import ast.Ast.Method.MethodSingle;
import ast.Ast.Stm.Assign;
import ast.Ast.Stm.AssignArray;
import ast.Ast.Stm.Block;
import ast.Ast.Stm.If;
import ast.Ast.Stm.Print;
import ast.Ast.Stm.While;
import ast.Ast.Type.Boolean;
import ast.Ast.Type.ClassType;
import ast.Ast.Type.Int;
import ast.Ast.Type.IntArray;

public interface Visitor
{
  // expressions
  public void visit(Add e);

  public void visit(And e);

  public void visit(ArraySelect e);

  public void visit(Call e);

  public void visit(False e);

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

  public void visit(True e);

  // statements
  public void visit(Assign s);

  public void visit(AssignArray s);

  public void visit(Block s);

  public void visit(If s);

  public void visit(Print s);

  public void visit(While s);

  // type
  public void visit(Boolean t);

  public void visit(ClassType t);

  public void visit(Int t);

  public void visit(IntArray t);

  // dec
  public void visit(Dec.DecSingle d);

  // method
  public void visit(MethodSingle m);

  // class
  public void visit(Class.ClassSingle c);

  // main class
  public void visit(MainClassSingle c);

  // program
  public void visit(ast.Ast.Program.ProgramSingle p);
}
