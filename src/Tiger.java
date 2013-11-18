import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;

import lexer.Lexer;
import lexer.Token;
import lexer.Token.Kind;

import control.CommandLine;
import control.Control;

import parser.Parser;

public class Tiger
{
  static CommandLine cmd;
  
  public static void compile(String fname)
  {
    InputStream fstream;
    Parser parser;

    // /////////////////////////////////////////////////////
    // to test the pretty printer on the "test/Fac.java" program
    if (control.Control.testFac) {
      control.CompilerPass ppPass = new control.CompilerPass(
          "Pretty printing AST");
      ppPass.start();
      ast.PrettyPrintVisitor pp = new ast.PrettyPrintVisitor();
      //ast.Fac.prog.accept(pp);
      ppPass.finish();

      // elaborate the given program, this step is necessary
      // for that it will annotate the AST with some
      // informations used by later phase.
      control.CompilerPass elabPass = new control.CompilerPass(
          "Elaborating the AST");
      elabPass.start();
      elaborator.ElaboratorVisitor elab = new elaborator.ElaboratorVisitor();
      ast.Fac.prog.accept(elab);
      elabPass.finish();
      
      // optimize the AST
      control.CompilerPass optAstPass = new control.CompilerPass(
          "Optimizing AST");
      optAstPass.start();
      ast.Fac.prog = (ast.program.Program)new ast.optimizations.Main().doit(ast.Fac.prog);
      optAstPass.finish();

      // Compile this program to C.
      // code generation
      switch (control.Control.codegen) {
      case Bytecode:
        control.CompilerPass genBytecodePass = new control.CompilerPass(
            "Bytecode generation");
        genBytecodePass.start();
        codegen.bytecode.TranslateVisitor trans = new codegen.bytecode.TranslateVisitor();
        ast.Fac.prog.accept(trans);
        codegen.bytecode.program.T bytecodeAst = trans.program;
        genBytecodePass.finish();

        control.CompilerPass ppBytecodePass = new control.CompilerPass(
            "Bytecode printing");
        ppBytecodePass.start();
        codegen.bytecode.PrettyPrintVisitor ppbc = new codegen.bytecode.PrettyPrintVisitor();
        bytecodeAst.accept(ppbc);
        genBytecodePass.finish();
        break;
      case C:
        control.CompilerPass genCCodePass = new control.CompilerPass(
            "C code generation");
        genCCodePass.start();
        codegen.C.TranslateVisitor transC = new codegen.C.TranslateVisitor();
        ast.Fac.prog.accept(transC);
        codegen.C.program.T cAst = transC.program;
        genCCodePass.finish();

        control.CompilerPass ppCCodePass = new control.CompilerPass(
            "C code printing");
        ppCCodePass.start();
        codegen.C.PrettyPrintVisitor ppc = new codegen.C.PrettyPrintVisitor();
        cAst.accept(ppc);
        ppCCodePass.finish();
        break;
      case Dalvik:
        control.CompilerPass genDalvikCodePass = new control.CompilerPass(
            "Dalvik code generation");
        genDalvikCodePass.start();
        codegen.dalvik.TranslateVisitor transDalvik = new codegen.dalvik.TranslateVisitor();
        ast.Fac.prog.accept(transDalvik);
        codegen.dalvik.program.T dalvikAst = transDalvik.program;
        genDalvikCodePass.finish();

        control.CompilerPass ppDalvikCodePass = new control.CompilerPass(
            "Dalvik code printing");
        ppDalvikCodePass.start();
        codegen.dalvik.PrettyPrintVisitor ppDalvik = new codegen.dalvik.PrettyPrintVisitor();
        dalvikAst.accept(ppDalvik);
        ppDalvikCodePass.finish();
        break;
      case X86:
        // similar
        break;
      default:
        break;
      }
      return;
    }

    if (fname == null) {
      cmd.usage();
      return;
    }
    Control.fileName = fname;

    
    // /////////////////////////////////////////////////////
    // it would be helpful to be able to test the lexer
    // independently.
    if (control.Control.testlexer) {
      System.out.println("Testing the lexer. All tokens:");
      try {
        fstream = new BufferedInputStream(new FileInputStream(fname));
        Lexer lexer = new Lexer(fname, fstream);
        Token token = lexer.nextToken();
        while (token.kind != Kind.TOKEN_EOF) {
          System.out.println(token.toString());
          token = lexer.nextToken();
        }
        fstream.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
      System.exit(1);
    }

    // /////////////////////////////////////////////////////////
    // normal compilation phases.
    ast.program.T theAst = null;

    control.CompilerPass lexAndParsePass = new control.CompilerPass(
        "Lex and parse");
    lexAndParsePass.start();
    // parsing the file, get an AST.
    try {
      fstream = new BufferedInputStream(new FileInputStream(fname));
      parser = new Parser(fname, fstream);

      theAst = parser.parse();

      fstream.close();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
    lexAndParsePass.finish();

    // pretty printing the AST, if necessary
    if (control.Control.dumpAst) {
      control.CompilerPass ppAstPass = new control.CompilerPass(
          "Pretty printing the AST");
      ppAstPass.start();
      ast.PrettyPrintVisitor pp = new ast.PrettyPrintVisitor();
      theAst.accept(pp);
      ppAstPass.finish();      
    }

    // elaborate the AST, report all possible errors.
    control.CompilerPass elabAstPass = new control.CompilerPass(
        "Elaborating the AST");
    elabAstPass.start();
    elaborator.ElaboratorVisitor elab = new elaborator.ElaboratorVisitor();
    theAst.accept(elab);
    elabAstPass.finish();
    
    // optimize the AST
    control.CompilerPass optAstPass = new control.CompilerPass(
        "Optimizing the AST");
    optAstPass.start();
    theAst = (ast.program.Program)new ast.optimizations.Main().doit(theAst);
    optAstPass.finish();

    // code generation
    switch (control.Control.codegen) {
    case Bytecode:
      control.CompilerPass genBytecodePass = new control.CompilerPass(
          "Bytecode generation");
      genBytecodePass.start();
      codegen.bytecode.TranslateVisitor trans = new codegen.bytecode.TranslateVisitor();
      theAst.accept(trans);
      codegen.bytecode.program.T bytecodeAst = trans.program;
      genBytecodePass.finish();

      control.CompilerPass ppBytecodePass = new control.CompilerPass(
          "Bytecode printing");
      ppBytecodePass.start();
      codegen.bytecode.PrettyPrintVisitor ppbc = new codegen.bytecode.PrettyPrintVisitor();
      bytecodeAst.accept(ppbc);
      genBytecodePass.finish();
      break;
    case C:
      control.CompilerPass genCCodePass = new control.CompilerPass(
          "C code generation");
      genCCodePass.start();
      codegen.C.TranslateVisitor transC = new codegen.C.TranslateVisitor();
      theAst.accept(transC);
      codegen.C.program.T cAst = transC.program;
      genCCodePass.finish();

      control.CompilerPass ppCCodePass = new control.CompilerPass(
          "C code printing");
      ppCCodePass.start();
      codegen.C.PrettyPrintVisitor ppc = new codegen.C.PrettyPrintVisitor();
      cAst.accept(ppc);
      ppCCodePass.finish();
      break;
    case Dalvik:
      control.CompilerPass genDalvikCodePass = new control.CompilerPass(
          "Dalvik code generation");
      genDalvikCodePass.start();
      codegen.dalvik.TranslateVisitor transDalvik = new codegen.dalvik.TranslateVisitor();
      theAst.accept(transDalvik);
      codegen.dalvik.program.T dalvikAst = transDalvik.program;
      genDalvikCodePass.finish();

      control.CompilerPass ppDalvikCodePass = new control.CompilerPass(
          "Dalvik code printing");
      ppDalvikCodePass.start();
      codegen.dalvik.PrettyPrintVisitor ppDalvik = new codegen.dalvik.PrettyPrintVisitor();
      dalvikAst.accept(ppDalvik);
      ppDalvikCodePass.finish();
      break;
    case X86:
      // similar
      break;
    default:
      break;
    }

    return;
  }

  public static void compileAndLink(String fname)
  {
    // compile
    control.CompilerPass compilePass = new control.CompilerPass("Compile");
    compilePass.start();
    compile(fname);
    compilePass.finish();
    
    // assembling
    control.CompilerPass assemblePass = new control.CompilerPass("Assembling");
    assemblePass.start();
    // Your code here:
    
    assemblePass.finish();
    
    // linking
    control.CompilerPass linkPass = new control.CompilerPass("Linking");
    linkPass.start();
    // Your code here
    
    linkPass.finish();
    

    return;
  }

  public static void main(String[] args)
  {
 // ///////////////////////////////////////////////////////
    // handle command line arguments
    cmd = new CommandLine();
    String fname = cmd.scan(args);
    
    control.CompilerPass tiger = new control.CompilerPass("Tiger");
    tiger.start();
    compileAndLink(fname);
    tiger.finish();
    return;
  }
}
