package codegen.bytecode;

import java.util.LinkedList;

import util.Label;

public class Ast
{
  // ////////////////////////////////////////////////
  // type
  public static class Type
  {
    public static abstract class T implements codegen.bytecode.Acceptable
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
    public static abstract class T implements codegen.bytecode.Acceptable
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
    public static abstract class T implements codegen.bytecode.Acceptable
    {
    }

    public static class Aload extends T
    {
      public int index;

      public Aload(int index)
      {
        this.index = index;
      }

      @Override
      public void accept(Visitor v)
      {
        v.visit(this);
      }
    }

    public static class Areturn extends T
    {
      public Areturn()
      {
      }

      @Override
      public void accept(Visitor v)
      {
        v.visit(this);
      }
    }

    public static class Astore extends T
    {
      public int index;

      public Astore(int index)
      {
        this.index = index;
      }

      @Override
      public void accept(Visitor v)
      {
        v.visit(this);
      }
    }

    public static class Goto extends T
    {
      public Label l;

      public Goto(Label l)
      {
        this.l = l;
      }

      @Override
      public void accept(Visitor v)
      {
        v.visit(this);
      }
    }

    public static class Ificmplt extends T
    {
      public Label l;

      public Ificmplt(Label l)
      {
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
      public Label l;

      public Ifne(Label l)
      {
        this.l = l;
      }

      @Override
      public void accept(Visitor v)
      {
        v.visit(this);
      }
    }

    public static class Iload extends T
    {
      public int index;

      public Iload(int index)
      {
        this.index = index;
      }

      @Override
      public void accept(Visitor v)
      {
        v.visit(this);
      }
    }

    public static class Imul extends T
    {
      public Imul()
      {
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

    public static class Ireturn extends T
    {
      public Ireturn()
      {
      }

      @Override
      public void accept(Visitor v)
      {
        v.visit(this);
      }
    }

    public static class Istore extends T
    {
      public int index;

      public Istore(int index)
      {
        this.index = index;
      }

      @Override
      public void accept(Visitor v)
      {
        v.visit(this);
      }
    }

    public static class Isub extends T
    {
      public Isub()
      {
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

    public static class Ldc extends T
    {
      public int i;

      public Ldc(int i)
      {
        this.i = i;
      }

      @Override
      public void accept(Visitor v)
      {
        v.visit(this);
      }
    }

    public static class New extends T
    {
      public String c;

      public New(String c)
      {
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
      public Print()
      {
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
    public static abstract class T implements codegen.bytecode.Acceptable
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
    public static abstract class T implements codegen.bytecode.Acceptable
    {
    }

    public static class ClassSingle extends T
    {
      public String id;
      public String extendss; // null for non-existing "extends"
      public LinkedList<Dec.T> decs;
      public LinkedList<Method.T> methods;

      public ClassSingle(String id, String extendss,
          LinkedList<Dec.T> decs,
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
    public static abstract class T implements codegen.bytecode.Acceptable
    {
    }

    public static class MainClassSingle extends T
    {
      public String id;
      public String arg;
      public LinkedList<Stm.T> stms;

      public MainClassSingle(String id, String arg,
          LinkedList<Stm.T> stms)
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
    public static abstract class T implements codegen.bytecode.Acceptable
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
