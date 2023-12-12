package slp;

import java.util.Arrays;
import java.util.List;
import slp.Slp.Exp.Eseq;
import slp.Slp.Exp.Id;
import slp.Slp.Exp.Num;
import slp.Slp.Exp.Op;
import slp.Slp.Stm;
import slp.Slp.Stm.Assign;
import slp.Slp.Stm.Compound;
import slp.Slp.Stm.Print;

public class Samples {
    public static Stm.T prog = new Compound(
        new Assign("a", new Op(new Num(3), "+", new Num(5))),
        new Compound(
            new Assign("b", new Eseq(new Print(Arrays.asList(
                                         new Id("a"),
                                         new Op(new Id("a"), "-", new Num(1)))),
                                     new Op(new Num(10), "-", new Id("a")))),
            new Print(List.of(new Id("b")))));

    public static Stm.T divideByZero =
        new Print(List.of(new Op(new Num(1), "/", new Num(0))));
}
