package slp;

import slp.Slp2.Exp.*;
import slp.Slp2.Stm;
import slp.Slp2.Stm.*;

import java.util.List;

// two sample programs.
/*
 a = 5 + 3;
 b = (print(a, a - 1), 10*a);
 print(b);
 */
public class SamplePrograms {
    public static Stm sample1 = new Compound(
            new Assign("a", new Op(new Num(5), "+", new Num(3))),
            new Compound(
                    new Assign("b", new Eseq(new Print(List.of(
                            new Id("a"),
                            new Op(new Id("a"), "-", new Num(1)))),
                            new Op(new Num(10), "*", new Id("a")))),
                    new Print(List.of(new Id("b")))));

    // lab 1, exercise 3:
    // replace the "null" with your code:
    public static Stm sample2 = null;
}
