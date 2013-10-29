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

  // elaborator
  public static boolean elabClassTable = false;
  public static boolean elabMethodTable = false;

  // code generator
  public static String outputName = null;

  public enum Codegen_Kind_t {
    Bytecode, C, Dalvik, X86
  }

  public static Codegen_Kind_t codegen = Codegen_Kind_t.C;

}
