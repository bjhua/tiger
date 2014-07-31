package codegen.dalvik;

import java.util.LinkedList;

import util.Label;

public class Ast
{
  // ////////////////////////////////////////////////
  // type
  public static class Type
  {
    public static abstract class T implements codegen.dalvik.Acceptable
    {
    }

    public static class ClassType extends T
    {
      public String id;

      public ClassType(String id)
      {
        this.id = id;
      }

      @Override
      public String toString()
      {
        return this.id;
      }

      @Override
      public void accept(Visitor v)
      {
        v.visit(this);
      }
    }

    public static class Int extends T
    {
      public Int()
      {
      }

      @Override
      public String toString()
      {
        return "@int";
      }

      @Override
      public void accept(Visitor v)
      {
        v.visit(this);
      }
    }

    public static class IntArray extends T
    {
      public IntArray()
      {
      }

      @Override
      public String toString()
      {
        return "@int[]";
      }

      @Override
      public void accept(Visitor v)
      {
        v.visit(this);
      }
    }
  }// end of type

  // ////////////////////////////////////////////////
  // dec
  public static class Dec
  {
    public static abstract class T implements codegen.dalvik.Acceptable
    {
    }

    public static class DecSingle extends T
    {
      public Type.T type;
      public String id;

      public DecSingle(Type.T type, String id)
      {
        this.type = type;
        this.id = id;
      }

      @Override
      public void accept(Visitor v)
      {
        v.visit(this);
      }
    }

  }// end of dec

  // ////////////////////////////////////////////////
  // statement
  public static class Stm
  {
    public static abstract class T implements codegen.dalvik.Acceptable
    {
    }

    public static class Const extends T
    {
      public String dst;
      public int i;

      public Const(String dst, int i)
      {
        this.dst = dst;
        this.i = i;
      }

      @Override
      public void accept(Visitor v)
      {
        v.visit(this);
      }
    }

    public static class Goto32 extends T
    {
      public Label l;

      public Goto32(Label l)
      {
        this.l = l;
      }

      @Override
      public void accept(Visitor v)
      {
        v.visit(this);
      }
    }

    public static class Iflt extends T
    {
      public String left, right;
      public Label l;

      public Iflt(String left, String right, Label l)
      {
        this.left = left;
        this.right = right;
        this.l = l;
      }

      @Override
      public void accept(Visitor v)
      {
        v.visit(this);
      }
    }

    public static class Ifne extends T
    {
      String left, right;
      public Label l;

      public Ifne(String left, String right, Label l)
      {
        this.left = left;
        this.right = right;
        this.l = l;
      }

      @Override
      public void accept(Visitor v)
      {
        v.visit(this);
      }
    }

    public static class Ifnez extends T
    {
      public String left;
      public Label l;

      public Ifnez(String left, Label l)
      {
        this.left = left;
        this.l = l;
      }

      @Override
      public void accept(Visitor v)
      {
        v.visit(this);
      }
    }

    public static class Invokevirtual extends T
    {
      public String f;
      public String c;
      public LinkedList<Type.T> at;
      public Type.T rt;

      public Invokevirtual(String f, String c, LinkedList<Type.T> at, Type.T rt)
      {
        this.f = f;
        this.c = c;
        this.at = at;
        this.rt = rt;
      }

      @Override
      public void accept(Visitor v)
      {
        v.visit(this);
      }
    }

    public static class LabelJ extends T
    {
      public util.Label l;

      public LabelJ(util.Label l)
      {
        this.l = l;
      }

      @Override
      public void accept(Visitor v)
      {
        v.visit(this);
      }
    }

    public static class Move16 extends T
    {
      public String left, right;

      public Move16(String left, String right)
      {
        this.left = left;
        this.right = right;
      }

      @Override
      public void accept(Visitor v)
      {
        v.visit(this);
      }
    }

    public static class Moveobject16 extends T
    {
      public String left, right;

      public Moveobject16(String left, String right)
      {
        this.left = left;
        this.right = right;
      }

      @Override
      public void accept(Visitor v)
      {
        v.visit(this);
      }
    }

    public static class Mulint extends T
    {
      public String dst, src1, src2;
      
      public Mulint(String dst, String src1, String src2)
      {
        this.dst = dst;
        this.src1 = src1;
        this.src2 = src2;
      }

      @Override
      public void accept(Visitor v)
      {
        v.visit(this);
      }
    }
    
    public static class NewInstance extends T
    {
      public String dst;
      public String c;

      public NewInstance(String dst, String c)
      {
        this.dst = dst;
        this.c = c;
      }

      @Override
      public void accept(Visitor v)
      {
        v.visit(this);
      }
    }

    public static class Print extends T
    {
      public String stream;
      public String src;
      
      public Print(String stream, String src)
      {
        this.stream = stream;
        this.src = src;
      }

      @Override
      public void accept(Visitor v)
      {
        v.visit(this);
      }
    }

    public static class Return extends T
    {
      String src;
      
      public Return(String src)
      {
        this.src = src;
      }

      @Override
      public void accept(Visitor v)
      {
        v.visit(this);
      }
    }

    public static class ReturnObject extends T
    {
      public String src;
      
      public ReturnObject(String src)
      {
        this.src = src;
      }

      @Override
      public void accept(Visitor v)
      {
        v.visit(this);
      }
    }

    public static class Subint extends T
    {
      public String dst, src1, src2;
      
      public Subint(String dst, String src1, String src2)
      {
        this.dst = dst;
        this.src1 = src1;
        this.src2 = src2;
      }

      @Override
      public void accept(Visitor v)
      {
        v.visit(this);
      }
    }    
  }// end of statement

  // ////////////////////////////////////////////////
  // method
  public static class Method
  {
    public static abstract class T implements codegen.dalvik.Acceptable
    {
    }

    public static class MethodSingle extends T
    {
      public Type.T retType;
      public String id;
      public String classId;
      public LinkedList<Dec.T> formals;
      public LinkedList<Dec.T> locals;
      public LinkedList<Stm.T> stms;
      public int index; // number of index
      public int retExp;

      public MethodSingle(Type.T retType, String id, String classId,
          LinkedList<Dec.T> formals, LinkedList<Dec.T> locals,
          LinkedList<Stm.T> stms, int retExp, int index)
      {
        this.retType = retType;
        this.id = id;
        this.classId = classId;
        this.formals = formals;
        this.locals = locals;
        this.stms = stms;
        retExp = 0;
        this.index = index;
      }

      @Override
      public void accept(Visitor v)
      {
        v.visit(this);
      }
    }

  }// end of method

  // ////////////////////////////////////////////////
  // class
  public static class Class
  {
    public static abstract class T implements codegen.dalvik.Acceptable
    {
    }

    public static class ClassSingle extends T
    {
      public String id;
      public String extendss; // null for non-existing "extends"
      public LinkedList<Dec.T> decs;
      public LinkedList<Method.T> methods;

      public ClassSingle(String id, String extendss, LinkedList<Dec.T> decs,
          LinkedList<Method.T> methods)
      {
        this.id = id;
        this.extendss = extendss;
        this.decs = decs;
        this.methods = methods;
      }

      @Override
      public void accept(Visitor v)
      {
        v.visit(this);
      }
    }

  }// end of class

  // ////////////////////////////////////////////////
  // main class
  public static class MainClass
  {
    public static abstract class T implements codegen.dalvik.Acceptable
    {
    }

    public static class MainClassSingle extends T
    {
      public String id;
      public String arg;
      public LinkedList<Stm.T> stms;

      public MainClassSingle(String id, String arg, LinkedList<Stm.T> stms)
      {
        this.id = id;
        this.arg = arg;
        this.stms = stms;
      }

      @Override
      public void accept(Visitor v)
      {
        v.visit(this);
        return;
      }

    }

  }// end of main class

  // ////////////////////////////////////////////////
  // program
  public static class Program
  {
    public static abstract class T implements codegen.dalvik.Acceptable
    {
    }

    public static class ProgramSingle extends T
    {
      public MainClass.T mainClass;
      public LinkedList<Class.T> classes;

      public ProgramSingle(MainClass.T mainClass,
          java.util.LinkedList<Class.T> classes)
      {
        this.mainClass = mainClass;
        this.classes = classes;
      }

      @Override
      public void accept(Visitor v)
      {
        v.visit(this);
        return;
      }
    }

  }// end of program
}
