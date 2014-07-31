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
    public static boolean dumpC = false;
    public static boolean dumpCyclone = false;
    public static boolean dumpDot = false;

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
  
//graph visualization
 public enum Visualize_Kind_t {
   None, Bmp, Pdf, Ps, Jpg
 }
 public static Visualize_Kind_t visualize = Visualize_Kind_t.None;
  
  // verbose level
  public enum Verbose_t{
    Silent, Pass, Detailed
  }
  public static Verbose_t verbose = Verbose_t.Silent;
  
  // trace
  public static java.util.LinkedList<String> trace =
      new java.util.LinkedList<String>();
  public static void addTrace (String name)
  {
    trace.add(name);
    return;
  }
  
  public static boolean isTracing (String name)
  {
    for (String s: trace){
      if (s.equals(name))
        return true;
    }
    return false;
  }
  
  //
  public static java.util.LinkedList<String> skippedPasses =
      new java.util.LinkedList<String>();
  
  public static void addPass (String name)
  {
    skippedPasses.add(name);
    return;
  }
  
  public static boolean skipPass (String name)
  {
    for (String s: skippedPasses){
      if (s.equals(name))
        return true;
    }
    return false;
  }
}
