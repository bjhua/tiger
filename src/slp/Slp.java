package slp;

import java.util.LinkedList;

public class Slp
{
  // ////////////////////////////////////////////////
  // expression
  public static class Exp
  {
    // base class
    public static abstract class T
    {
    }

    // id
    public static class Id extends T
    {
      String id;

      public Id(String id)
      {
        this.id = id;
      }
    }

    // id
    public static class Num extends T
    {
      int num;

      public Num(int num)
      {
        this.num = num;
      }
    }

    // op
    public enum OP_T
    {
      ADD, SUB, TIMES, DIVIDE
    };

    public static class Op extends T
    {
      OP_T op;
      public T left;
      public T right;

      public Op(OP_T op, T left, T right)
      {
        this.op = op;
        this.left = left;
        this.right = right;
      }
    }

    // Eseq
    public static class Eseq extends T
    {
      public Stm.T stm;
      public T exp;

      public Eseq(Stm.T stm, T exp)
      {
        this.stm = stm;
        this.exp = exp;
      }
    }
  }// end of expression

  // ////////////////////////////////////////////////
  // explist
  public static class ExpList
  {
    // base class
    public static abstract class T
    {
    }

    // pair
    public static class Pair extends T
    {
      public Exp.T exp;
      public ExpList.T list;

      public Pair(Exp.T exp, T list)
      {
        super();
        this.exp = exp;
        this.list = list;
      }
    }

    // last
    public static class Last extends T
    {
      public Exp.T exp;

      public Last(Exp.T exp)
      {
        super();
        this.exp = exp;
      }
    }
  }// end of explist

  // ///////////////////////////////////////////////
  // statement
  public static class Stm
  {
    // base class
    public static abstract class T
    {
    }

    // Compound (s1, s2)
    public static class Compound extends T
    {
      public T s1;
      public T s2;

      public Compound(T s1, T s2)
      {
        this.s1 = s1;
        this.s2 = s2;
      }
    }

    // x := e
    public static class Assign extends T
    {
      public String id;
      public Exp.T exp;

      public Assign(String id, Exp.T exp)
      {
        this.id = id;
        this.exp = exp;
      }
    }

    // print (explist)
    public static class Print extends T
    {
      ExpList.T explist;

      public Print(ExpList.T explist)
      {
        this.explist = explist;
      }
    }

  }// end of statement

}
