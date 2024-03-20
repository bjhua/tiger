package ast;

import ast.Ast.*;
import ast.Ast.Exp.*;
import ast.Ast.Stm.Assign;
import ast.Ast.Stm.If;
import ast.Ast.Stm.Print;

import java.util.List;

public class SamplePrograms {
    // Lab2, exercise 2: read the following code and make
    // sure you understand how the sample program "test/Factorial.java" is
    // encoded.

    // /////////////////////////////////////////////////////
    // To represent the "Factorial.java" program in memory manually
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
    static MainClass.T factorial = new MainClass.Singleton(
            "Factorial", "a",
            new Print(new Call(new NewObject("Fac"), "ComputeFac",
                    List.of(new Num(10)), null, null, null)));

    // // class "Fac"
    static ast.Ast.Class.T fac = new ast.Ast.Class.Singleton(
            "Fac", null,
            List.of(), // arguments
            List.of(new Method.Singleton(
                    new Type.Int(), "ComputeFac",
                    List.of(new Dec.Singleton(new Type.Int(), "num")),
                    List.of(new Dec.Singleton(new Type.Int(), "num_aux")),
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
    public static Program.T progFac = new Program.Singleton(factorial, List.of(fac));


    // to encode "test/SumRec.java"
//    class Sum {
//        public static void main(String[] a) {
//            System.out.println(new Doit().doit(101));
//        }
//    }
//
//    class Doit {
//        public int doit(int n) {
//            int sum;
//            int i;
//
//            i = 0;
//            sum = 0;
//            while (i<n)
//                sum = sum + i;
//            return sum;
//        }
//    }
    static MainClass.T sumRec = new MainClass.Singleton(
            "Factorial", "a",
            new Print(new Call(new NewObject("Doit"), "doit",
                    List.of(new Num(100)), null, null, null)));

    // // class "Fac"
    static ast.Ast.Class.T doitSumRec = new ast.Ast.Class.Singleton(
            "Doit",
            null,
            List.of(),
            List.of(new Method.Singleton(
                    new Type.Int(),
                    "doit",
                    List.of(new Dec.Singleton(new Type.Int(), "n")),
                    List.of(new Dec.Singleton(new Type.Int(), "sum")),
                    List.of(new If(
                            new Bop(new Id("n", null, false), "<", new Num(1)),
                            new Assign("sum", new Num(0), null),
                            new Assign(
                                    "sum",
                                    new Bop(new Id("n", null, false), "+",
                                            new Call(new This(), "doit",
                                                    List.of(new Bop(new Id("n", null, false),
                                                            "-", new Num(1))),
                                                    null, null, null)),
                                    null))),
                    new Id("sum", null, false))));

    public static Program.T progSumRec = new Program.Singleton(sumRec, List.of(doitSumRec));


    // Lab2, exercise 2: you should write some code to
    // encode the program "test/Sum.java".
    // Your code here:
    public static Program.T progSum = null;


}




