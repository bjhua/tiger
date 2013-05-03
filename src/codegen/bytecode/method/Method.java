package codegen.bytecode.method;

import codegen.bytecode.Visitor;

public class Method extends T
{
  public codegen.bytecode.type.T retType;
  public String id;
  public String classId;
  public java.util.LinkedList<codegen.bytecode.dec.T> formals;
  public java.util.LinkedList<codegen.bytecode.dec.T> locals;
  public java.util.LinkedList<codegen.bytecode.stm.T> stms;
  public int index; // number of index
  public int retExp;

  public Method(codegen.bytecode.type.T retType, String id, String classId,
      java.util.LinkedList<codegen.bytecode.dec.T> formals,
      java.util.LinkedList<codegen.bytecode.dec.T> locals,
      java.util.LinkedList<codegen.bytecode.stm.T> stms, int retExp, int index)
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
