package slp;

import slp.Slp2.Exp;
import slp.Slp2.Stm;
//import util.Todo;

public class MaxArgument {
    // ///////////////////////////////////////////
    // expression
    private int maxExp(Exp exp) {
        switch (exp) {
            case Exp.Eseq(Stm stm, Exp exp1) -> {
                return Math.max(maxStm(stm), maxExp(exp1));
            }
            case Exp.Id id -> {
                return 0;
            }
            case Exp.Num num -> {
                return 0;
            }
            case Exp.Op(Exp left, String op, Exp right) -> {
                return Math.max(maxExp(left), maxExp(right));
            }
        }
    }

    // ///////////////////////////////////////////
    // statement
    public int maxStm(Stm stm) {
        switch (stm) {
            case Stm.Assign(String id, Exp exp) -> {
                return maxExp(exp);
            }
            case Stm.Compound(Stm s1, Stm s2) -> {
                return Math.max(maxStm(s1), maxStm(s2));
            }
            case Stm.Print(java.util.List<Exp> exps) -> {
                final int[] max = new int[1];
                exps.forEach(x -> {
                    int m = maxExp(x);
                    if (m > max[0]) {
                        max[0] = m;
                    }
                });
                return Math.max(max[0], exps.size());
            }
        }
    }
}
