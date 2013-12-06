package control;

public class Control
{
  // source file
  public static String fileName = null;

  // compiler testing and debugging
  public static boolean testlexer = false;
  public static boolean testFac = false;

  // lexer and parser
  public static boolean lex = false;

  // ast
  public static boolean dumpAst = false;
  public static boolean dumpC = false;
  public static boolean dumpCyclone = false;
  public static boolean dumpDot = false;
  
  // graph visualization
  public enum Visualize_Kind_t {
    None, Bmp, Pdf, Ps, Jpg
  }
  public static Visualize_Kind_t visualize = Visualize_Kind_t.None;


  
  // elaborator
  public static boolean elabClassTable = false;
  public static boolean elabMethodTable = false;
  

  // code generator
  public static String outputName = null;

  public enum Codegen_Kind_t {
    Bytecode, C, Dalvik, X86
  }
  public static Codegen_Kind_t codegen = Codegen_Kind_t.C;
  
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
