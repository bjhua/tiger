package slp;

import slp.Slp.Exp;
import slp.Slp.Exp.Eseq;
import slp.Slp.Exp.Id;
import slp.Slp.Exp.Num;
import slp.Slp.Exp.Op;
import slp.Slp.ExpList.Last;
import slp.Slp.ExpList.Pair;
import slp.Slp.Stm;
import slp.Slp.Stm.Assign;
import slp.Slp.Stm.Compound;
import slp.Slp.Stm.Print;

public class Samples
{
  public static Stm.T prog = new Compound(new Assign("a", new Op(Exp.OP_T.ADD,
      new Num(3), new Num(5))), new Compound(new Assign("b", new Eseq(
      new Print(new Pair(new Id("a"), new Last(new Op(Exp.OP_T.SUB,
          new Id("a"), new Num(1))))), new Op(Exp.OP_T.TIMES, new Num(10),
          new Id("a")))), new Print(new Last(new Id("b")))));

  public static Stm.T dividebyzero =
      new Print (new Last (new Op(Exp.OP_T.DIVIDE, new Num (1), new Num(0))));
  
  
}
