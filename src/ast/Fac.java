package ast;

import ast.Ast.Dec;
import ast.Ast.Exp;
import ast.Ast.Exp.Call;
import ast.Ast.Exp.Id;
import ast.Ast.Exp.Lt;
import ast.Ast.Exp.NewObject;
import ast.Ast.Exp.Num;
import ast.Ast.Exp.Sub;
import ast.Ast.Exp.This;
import ast.Ast.Exp.Times;
import ast.Ast.MainClass;
import ast.Ast.MainClass.MainClassSingle;
import ast.Ast.Method;
import ast.Ast.Program;
import ast.Ast.Program.ProgramSingle;
import ast.Ast.Stm;
import ast.Ast.Stm.Assign;
import ast.Ast.Stm.If;
import ast.Ast.Stm.Print;
import ast.Ast.Type;

public class Fac
{
  // Lab2, exercise 2: read the following code and make
  // sure you understand how the sample program "test/Fac.java" is represented.

  // /////////////////////////////////////////////////////
  // To represent the "Fac.java" program in memory manually
  // this is for demonstration purpose only, and
  // no one would want to do this in reality (boring and error-prone).
  /*
   * class Factorial { public static void main(String[] a) {
   * System.out.println(new Fac().ComputeFac(10)); } } class Fac { public int
   * ComputeFac(int num) { int num_aux; if (num < 1) num_aux = 1; else num_aux =
   * num * (this.ComputeFac(num-1)); return num_aux; } }
   */

  // // main class: "Factorial"
  static MainClass.T factorial = new MainClassSingle(
      "Factorial", "a", new Print(new Call(
          new NewObject("Fac"), "ComputeFac",
          new util.Flist<Exp.T>().list(new Num(10)))));

  // // class "Fac"
  static ast.Ast.Class.T fac = new ast.Ast.Class.ClassSingle("Fac", null,
      new util.Flist<Dec.T>().list(),
      new util.Flist<Method.T>().list(new Method.MethodSingle(
          new Type.Int(), "ComputeFac", new util.Flist<Dec.T>()
              .list(new Dec.DecSingle(new Type.Int(), "num")),
          new util.Flist<Dec.T>().list(new Dec.DecSingle(
              new Type.Int(), "num_aux")), new util.Flist<Stm.T>()
              .list(new If(new Lt(new Id("num"),
                  new Num(1)), new Assign("num_aux",
                  new Num(1)), new Assign("num_aux",
                  new Times(new Id("num"), new Call(
                      new This(), "ComputeFac",
                      new util.Flist<Exp.T>().list(new Sub(
                          new Id("num"), new Num(1)))))))),
          new Id("num_aux"))));

  // program
  public static Program.T prog = new ProgramSingle(factorial,
      new util.Flist<ast.Ast.Class.T>().list(fac));

  // Lab2, exercise 2: you should write some code to
  // represent the program "test/Sum.java".
  // Your code here:
  
  
}
