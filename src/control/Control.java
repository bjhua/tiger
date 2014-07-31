package control;

public class Control
{
  // the lexer
  public static class ConLexer
  {
    public static boolean test = false;
    public static boolean dump = false;
  }
  
  // the straight-line program interpreter
  public static class ConSlp
  {
    public enum T{NONE, ARGS, INTERP, COMPILE, TEST, DIV};
    
    public static T action = T.NONE;
    public static boolean div = false;
    public static boolean keepasm = false;
  }
  
  // Ast and elaborator
  public static class ConAst
  {
    public static boolean dumpAst = false;
    public static boolean testFac = false;

    // elaborator
    public static boolean elabClassTable = false;
    public static boolean elabMethodTable = false;
  }
  
  public static class ConCodeGen
  {
    public static String fileName = null;

    public static String outputName = null;

    public static enum Kind_t {
      Bytecode, C, Dalvik, X86
    }

    public static Kind_t codegen = Kind_t.C;
  }
}
