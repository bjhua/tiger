package slp;

import java.util.List;

public class Slp {
    // ////////////////////////////////////////////////
    // expression
    public static class Exp {
        // base class
        public interface T {}
        // derived
        public record Id(String id) implements T {}
        public record Num(int num) implements T {}
        public record Op(T left, String op, T right) implements T {}
        public record Eseq(Stm.T stm, T exp) implements T {} // eseq
    }
    // end of expression

    // ///////////////////////////////////////////////
    // statement
    public static class Stm {
        // the type
        public interface T {}

        // Compound (s1, s2)
        public record Compound(T s1, T s2) implements T {}
        // x := e
        public record Assign(String id, Exp.T exp) implements T {}
        // print (explist)
        public record Print(List<Exp.T> exps) implements T {}
    } // end of statement
}
