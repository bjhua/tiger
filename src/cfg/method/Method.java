package cfg.method;

import cfg.Visitor;

public class Method extends T
{
  public cfg.type.T retType;
  public String id;
  public String classId;
  public java.util.LinkedList<cfg.dec.T> formals;
  public java.util.LinkedList<cfg.dec.T> locals;
  public java.util.LinkedList<cfg.block.T> blocks;
  public util.Label entry;
  public util.Label exit;
  public cfg.operand.T retValue;

  public Method(cfg.type.T retType, String id, String classId,
      java.util.LinkedList<cfg.dec.T> formals,
      java.util.LinkedList<cfg.dec.T> locals,
      java.util.LinkedList<cfg.block.T> blocks, 
      util.Label entry,
      util.Label exit,
      cfg.operand.T retValue)
  {
    this.retType = retType;
    this.id = id;
    this.classId = classId;
    this.formals = formals;
    this.locals = locals;
    this.blocks = blocks;
    this.entry = null;
    this.exit = null;
    this.retValue = null;
  }

  @Override
  public void accept(Visitor v)
  {
    v.visit(this);
  }
}
