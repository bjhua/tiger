package elaborator;

import java.util.LinkedList;

import ast.Ast.Dec;
import ast.Ast.Type;

public class MethodType
{
  public Type.T retType;
  public LinkedList<Dec.T> argsType;

  public MethodType(Type.T retType, LinkedList<Dec.T> decs)
  {
    this.retType = retType;
    this.argsType = decs;
  }

  @Override
  public String toString()
  {
    String s = "";
    for (Dec.T dec : this.argsType) {
      Dec.DecSingle decc = (Dec.DecSingle) dec;
      s = decc.type.toString() + "*" + s;
    }
    s = s + " -> " + this.retType.toString();
    return s;
  }
}
