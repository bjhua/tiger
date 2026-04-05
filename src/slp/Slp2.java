package slp;

import java.util.List;

// the abstract syntax trees for the SLP language.
public class Slp2 {
    // ////////////////////////////////////////////////
    // expression
    public sealed interface Exp permits Exp.Eseq, Exp.Id, Exp.Op, Exp.Num {
        // s, e
        record Eseq(Stm stm, Exp exp) implements Exp {
        }

        // x
        record Id(String id) implements Exp {
        }

        // e bop e
        record Op(Exp left, String op, Exp right) implements Exp {
        }

        // n
        record Num(int num) implements Exp {
        }
    }
    // end of expression

    // ///////////////////////////////////////////////
    // statement
    public sealed interface Stm permits Stm.Assign, Stm.Compound, Stm.Print {

        // x := e
        record Assign(String id, Exp exp) implements Stm {
        }

        // s1; s2
        record Compound(Stm s1, Stm s2) implements Stm {
        }

        // print(explist)
        record Print(List<Exp> exps) implements Stm {
        }
    }
    // end of statement
}
