package codegen.dalvik;

import codegen.dalvik.type.*;
import codegen.dalvik.stm.*;
import codegen.dalvik.dec.*;
import codegen.dalvik.program.*;
import codegen.dalvik.mainClass.*;

public interface Visitor
{
  // statements
  public void visit(ReturnObject s);

  public void visit(Goto32 s);

  public void visit(Iflt s);

  public void visit(Ifne s);
  
  public void visit(Ifnez s);

  public void visit(Mulint s);

  public void visit(Return s);

  public void visit(Subint s);

  public void visit(Invokevirtual s);

  public void visit(Label s);
  
  public void visit(Move16 s);
  
  public void visit(Moveobject16 s);

  public void visit(Const s);

  public void visit(Print s);

  public void visit(NewInstance s);

  // type
  public void visit(codegen.dalvik.type.Class t);

  public void visit(Int t);

  public void visit(IntArray t);

  // dec
  public void visit(Dec d);

  // method
  public void visit(codegen.dalvik.method.Method m);

  // class
  public void visit(codegen.dalvik.classs.Class c);

  // main class
  public void visit(MainClass c);

  // program
  public void visit(Program p);
}
