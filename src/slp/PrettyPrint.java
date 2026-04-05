package slp;

import slp.Slp2.Exp;
import slp.Slp2.Stm;

import java.util.List;

import static java.lang.System.out;

// this defines a pretty printer for the SLP language.
public class PrettyPrint {
    // a few print functions for convenience.
    private static void print(String s) {
        out.print(s);
    }

    private static void println(String s) {
        out.println(s);
    }


    // ///////////////////////////////////////////
    // print expression
    private void ppExp(Exp exp) {
        switch (exp) {
            case Exp.Num(int n) -> print(Integer.valueOf(n).toString());
            case Exp.Id(String x) -> print(x);
            case Exp.Op(
                    Exp left,
                    String bop,
                    Exp right
            ) -> {
                ppExp(left);
                print(bop);
                ppExp(right);
            }
            case Exp.Eseq(Stm stm, Exp e) -> {
                print("(");
                ppStm(stm);
                print(", ");
                ppExp(e);
                print(")");
            }
        }
    }

    // ///////////////////////////////////////////
    // print statement
    public void ppStm(Stm stm) {
        switch (stm) {
            case Stm.Compound(
                    Stm s1,
                    Stm s2
            ) -> {
                ppStm(s1);
                println(";");
                ppStm(s2);
            }
            case Stm.Assign(
                    String x,
                    Exp e
            ) -> {
                print(x + " = ");
                ppExp(e);
            }
            case Stm.Print(List<Exp> exps) -> {
                System.out.print("print(");
                
                exps.forEach(x -> {
                            ppExp(x);
                            print(", ");
                        }
                );
                System.out.print(")");
            }
        }
    }
}
