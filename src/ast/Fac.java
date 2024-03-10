package ast;

import ast.Ast.*;
import ast.Ast.Exp.*;
import ast.Ast.MainClass.MainClassSingle;
import ast.Ast.Program.ProgramSingle;
import ast.Ast.Stm.Assign;
import ast.Ast.Stm.If;
import ast.Ast.Stm.Print;
import util.Todo;

import java.util.List;

public class Fac {
    // Lab2, exercise 2: read the following code and make
    // sure you understand how the sample program "test/Fac.java" is
    // encoded.

    // /////////////////////////////////////////////////////
    // To represent the "Fac.java" program in memory manually
    // this is for demonstration purpose only, and
    // no one would want to do this in reality (boring and error-prone).
    /*
     * class Factorial {
     *     public static void main(String[] a) {
     *         System.out.println(new Fac().ComputeFac(10));
     *     }
     * }
     *
     * class Fac {
     *     public int ComputeFac(int num) {
     *         int num_aux;
     *         if (num < 1)
     *             num_aux = 1;
     *         else
     *             num_aux = num * (this.ComputeFac(num-1));
     *         return num_aux;
     *     }
     * }
     */

    // // main class: "Factorial"
    static MainClass.T factorial = new MainClassSingle(
        "Factorial", "a",
        new Print(new Call(new NewObject("Fac"), "ComputeFac",
                           List.of(new Num(10)), null, null, null)));

    // // class "Fac"
    static ast.Ast.Class.T fac = new ast.Ast.Class.ClassSingle(
        "Fac", null,
        List.of(), // arguments
        List.of(new Method.MethodSingle(
            new Type.Int(), "ComputeFac",
            List.of(new Dec.DecSingle(new Type.Int(), "num")),
            List.of(new Dec.DecSingle(new Type.Int(), "num_aux")),
            List.of(new If(
                new Bop(new Id("num", null, false), "<", new Num(1)),
                new Assign("num_aux", new Num(1), null),
                new Assign(
                    "num_aux",
                    new Bop(new Id("num", null, false), "*",
                            new Call(new This(), "ComputeFac",
                                     List.of(new Bop(new Id("num", null, false),
                                                     "-", new Num(1))),
                                     null, null, null)),
                    null))),
            new Id("num_aux", null, false))));

    // program
    public static Program.T prog = new ProgramSingle(factorial, List.of(fac));

    // Lab2, exercise 2: you should write some code to
    // encode the program "test/Sum.java".
    // Your code here:

}
