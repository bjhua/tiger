package slp;

import slp.Slp.Exp;
import slp.Slp.Stm;
import util.Bug;

import java.util.List;

// this defines a pretty printer for the SLP language.
public class PrettyPrint {
    // ///////////////////////////////////////////
    // print expression
    private void ppExp(Exp.T exp) throws Exception {
        switch (exp) {
            case Exp.Num(int n) -> {
                System.out.print(n);
            }
            case Exp.Id(String x) -> {
                System.out.print(x);
            }
            case Exp.Op(Exp.T left, String bop, Exp.T right) -> {
                ppExp(left);
                System.out.print(bop);
                ppExp(right);
            }
            case Exp.Eseq(Stm.T stm, Exp.T e) -> {
                System.out.print("(");
                ppStm(stm);
                System.out.print(", ");
                ppExp(e);
                System.out.print(")");
            }
            default -> {
                throw new Bug();
            }
        }
    }

    // ///////////////////////////////////////////
    // print statement
    public void ppStm(Stm.T stm) throws Exception {
        switch (stm) {
            case Stm.Compound(Stm.T s1, Stm.T s2) -> {
                ppStm(s1);
                System.out.println(";");
                ppStm(s2);
            }
            case Stm.Assign(String x, Exp.T e) -> {
                System.out.print(x + " = ");
                ppExp(e);
            }
            case Stm.Print(List<Exp.T> exps) -> {
                System.out.print("print(");
                for (Exp.T exp : exps) {
                    ppExp(exp);
                    System.out.print(", ");
                }
                System.out.print(")");
            }
            default -> {
                throw new Bug();
            }
        }
    }
}
