package slp;

import java.util.List;

// this class defines the abstract syntax trees for the SLP language.
public class Slp {
    // ////////////////////////////////////////////////
    // expression
    public static class Exp {
        // the type
        public interface T {
        }

        // x
        public record Id(String id) implements T {
        }

        // n
        public record Num(int num) implements T {
        }

        // e bop e
        public record Op(T left, String op, T right) implements T {
        }

        // s, e
        public record Eseq(Stm.T stm, T exp) implements T {
        }
    }
    // end of expression

    // ///////////////////////////////////////////////
    // statement
    public static class Stm {
        // the type
        public interface T {
        }

        // s1; s2
        public record Compound(T s1, T s2) implements T {
        }

        // x := e
        public record Assign(String id, Exp.T exp) implements T {
        }

        // print(explist)
        public record Print(List<Exp.T> exps) implements T {
        }
    }
    // end of statement
}
