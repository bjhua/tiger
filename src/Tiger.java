import static control.Control.ConAst.dumpAst;
import static control.Control.ConAst.testFac;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;

import ast.Ast.Program;
import lexer.Lexer;
import lexer.Token;
import parser.Parser;
import control.CommandLine;
import control.Control;

public class Tiger
{
  static Tiger tiger;
  static CommandLine cmd;
  static InputStream fstream;
  public ast.Ast.Program.T theAst;

  // lex and parse
  public void lexAndParse(String fname)
  {
    Parser parser;

    try {
      fstream = new BufferedInputStream(new FileInputStream(fname));
      parser = new Parser(fname, fstream);

      theAst = parser.parse();

      fstream.close();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
    return;
  }

  public void compile(String fname)
  {

    // /////////////////////////////////////////////////////
    // to test the pretty printer on the "test/Fac.java" program
    if (testFac) 
    {
      System.out.println("Testing the Tiger compiler on Fac.java starting:");
      ast.PrettyPrintVisitor pp = new ast.PrettyPrintVisitor();
      control.CompilerPass ppPass = new control.CompilerPass(
          "Pretty printing AST", ast.Fac.prog, pp);
      //ppPass.doit();

      // elaborate the given program, this step is necessary
      // for that it will annotate the AST with some
      // informations used by later phase.
      elaborator.ElaboratorVisitor elab = new elaborator.ElaboratorVisitor();
      control.CompilerPass elabPass = new control.CompilerPass(
          "Elaborating the AST", ast.Fac.prog, elab);
      elabPass.doit();

      // optimize the AST
      ast.optimizations.Main optAstPasses = new ast.optimizations.Main();
      control.CompilerPass optAstPass = new control.CompilerPass(
          "Optimizing AST", optAstPasses, ast.Fac.prog);
      optAstPass.doit();
      ast.Fac.prog = (ast.Ast.Program.T) optAstPasses.program;

      // Compile this program to C.
      codegen.C.TranslateVisitor transC = new codegen.C.TranslateVisitor();
      control.CompilerPass genCCodePasss = new control.CompilerPass(
          "Translation to C code", ast.Fac.prog, transC);
      genCCodePasss.doit();
      codegen.C.Ast.Program.T cAst = transC.program;

      if (control.Control.ConAst.dumpC) 
      {
        codegen.C.PrettyPrintVisitor ppC = new codegen.C.PrettyPrintVisitor();
        control.CompilerPass ppCCodePass = new control.CompilerPass(
            "C code printing", cAst, ppC);
        ppCCodePass.doit();
      }
      
      // translation to control-flow graph
      cfg.TranslateVisitor transCfg = new cfg.TranslateVisitor();
      control.CompilerPass genCfgCodePass = new control.CompilerPass(
          "Control-flow graph generation", cAst, transCfg);
      genCfgCodePass.doit();
      cfg.Cfg.Program.T cfgAst = transCfg.program;

      // visualize the control-flow graph, if necessary
      if (control.Control.visualize != Control.Visualize_Kind_t.None) 
      {
        cfg.VisualVisitor toDot = new cfg.VisualVisitor();
        control.CompilerPass genDotPass = new control.CompilerPass(
            "Draw control-flow graph", cfgAst, toDot);
        genDotPass.doit();
      }

      // optimizations on the control-flow graph
      cfg.optimizations.Main cfgOpts = new cfg.optimizations.Main();
      control.CompilerPass cfgOptPass = new control.CompilerPass(
          "Control-flow graph optimizations", cfgOpts, cfgAst);
      cfgOptPass.doit();

      // code generation
      switch (control.Control.ConCodeGen.codegen) {
      case Bytecode:
        codegen.bytecode.TranslateVisitor trans = new codegen.bytecode.TranslateVisitor();
        control.CompilerPass genBytecodePass = new control.CompilerPass(
            "Bytecode generation", ast.Fac.prog, trans);
        genBytecodePass.doit();
        codegen.bytecode.Ast.Program.T bytecodeAst = trans.program;

        codegen.bytecode.PrettyPrintVisitor ppbc = new codegen.bytecode.PrettyPrintVisitor();
        control.CompilerPass ppBytecodePass = new control.CompilerPass(
            "Bytecode printing", bytecodeAst, ppbc);
        ppBytecodePass.doit();
        break;
      case C:

        cfg.PrettyPrintVisitor ppCfg = new cfg.PrettyPrintVisitor();
        control.CompilerPass ppCfgCodePass = new control.CompilerPass(
            "C code printing", cfgAst, ppCfg);
        ppCfgCodePass.doit();
        break;
      case Dalvik:
        codegen.dalvik.TranslateVisitor transDalvik = new codegen.dalvik.TranslateVisitor();
        control.CompilerPass genDalvikCodePass = new control.CompilerPass(
            "Dalvik code generation", ast.Fac.prog, transDalvik);
        genDalvikCodePass.doit();
        codegen.dalvik.Ast.Program.T dalvikAst = transDalvik.program;

        codegen.dalvik.PrettyPrintVisitor ppDalvik = new codegen.dalvik.PrettyPrintVisitor();
        control.CompilerPass ppDalvikCodePass = new control.CompilerPass(
            "Dalvik code printing", dalvikAst, ppDalvik);
        ppDalvikCodePass.doit();
        break;
      case X86:
        // similar
        break;
      default:
        break;
      }
      return;
    }

    if (fname == null) 
    {
      cmd.usage();
      return;
    }
    Control.ConCodeGen.fileName = fname;

    // /////////////////////////////////////////////////////
    // it would be helpful to be able to test the lexer
    // independently.
    if (Control.ConLexer.test) 
    {
      System.out.println("Testing the lexer. All tokens:");
      try 
      {
        fstream = new BufferedInputStream(new FileInputStream(fname));
        Lexer lexer = new Lexer(fname, fstream);
        Token token = lexer.nextToken();

        while (token.kind != Token.Kind.TOKEN_EOF) 
        {
          System.out.println(token.toString());
          token = lexer.nextToken();
        }
        fstream.close();
       } 
       catch (Exception e) 
       {
         e.printStackTrace();
       }
      System.exit(1);
    }

    // /////////////////////////////////////////////////////////
    // normal compilation phases.

    control.CompilerPass lexAndParsePass = new control.CompilerPass(
        "lexAndParse", tiger, fname);
    lexAndParsePass.doitName("lexAndParse");

    // pretty printing the AST, if necessary
    if (dumpAst) 
    {
      ast.PrettyPrintVisitor pp = new ast.PrettyPrintVisitor();
      control.CompilerPass ppAstPass = new control.CompilerPass(
          "Pretty printing the AST", this.theAst, pp);
      ppAstPass.doit();
    }

    // elaborate the AST, report all possible errors.
    elaborator.ElaboratorVisitor elab = new elaborator.ElaboratorVisitor();
    control.CompilerPass elabAstPass = new control.CompilerPass(
        "Elaborating the AST", this.theAst, elab);
    elabAstPass.doit();
    // optimize the AST
    ast.optimizations.Main optAstPasses = new ast.optimizations.Main();
    control.CompilerPass optAstPass = new control.CompilerPass(
        "Optimizing the AST", optAstPasses, this.theAst);
    optAstPass.doitName("accept");
    this.theAst = optAstPasses.program;

    //-------------------------------------------------------------
    
    // Compile this program to C.
    codegen.C.TranslateVisitor transCC = new codegen.C.TranslateVisitor();
    control.CompilerPass genCCodePasss = new control.CompilerPass(
        "Translation to C code", this.theAst, transCC);
    genCCodePasss.doit();
    codegen.C.Ast.Program.T cAstt = transCC.program;
    
    if (control.Control.ConAst.dumpC) 
    {
      codegen.C.PrettyPrintVisitor ppC = new codegen.C.PrettyPrintVisitor();
      control.CompilerPass ppCCodePasss = new control.CompilerPass(
          "C code printing", cAstt, ppC);
      ppCCodePasss.doit();
    }
    // translation to control-flow graph
    cfg.TranslateVisitor transCfg = new cfg.TranslateVisitor();
    control.CompilerPass genCfgCodePass = new control.CompilerPass(
        "Control-flow graph generation", cAstt, transCfg);
    genCfgCodePass.doit();
    cfg.Cfg.Program.T cfgAst = transCfg.program;
    
    cfg.PrettyPrintVisitor pp = new cfg.PrettyPrintVisitor();
    cfgAst.accept(pp);
    
    // visualize the control-flow graph, if necessary
    if (control.Control.visualize != Control.Visualize_Kind_t.None) 
    {
      cfg.VisualVisitor toDot = new cfg.VisualVisitor();
      control.CompilerPass genDotPass = new control.CompilerPass(
          "Draw control-flow graph", cfgAst, toDot);
      genDotPass.doit();
    }

    // optimizations on the control-flow graph
    cfg.optimizations.Main cfgOpts = new cfg.optimizations.Main();
    control.CompilerPass cfgOptPass = new control.CompilerPass(
        "Control-flow graph optimizations", cfgOpts, cfgAst);
    
    cfgOptPass.doit();
	cfg.Cfg.Program.T Optimize_Ast = transCfg.program;
	cfg.PrettyPrintVisitor ppp = new cfg.PrettyPrintVisitor();
	Optimize_Ast.accept(ppp);
    //-------------------------------------------------------------
    
    // code generation
    switch (control.Control.ConCodeGen.codegen) {
    case Bytecode:
      codegen.bytecode.TranslateVisitor trans = new codegen.bytecode.TranslateVisitor();
      control.CompilerPass genBytecodePass = new control.CompilerPass(
          "Bytecode generation", this.theAst, trans);
      genBytecodePass.doit();
      codegen.bytecode.Ast.Program.T bytecodeAst = trans.program;
      codegen.bytecode.PrettyPrintVisitor ppbc = new codegen.bytecode.PrettyPrintVisitor();
      control.CompilerPass ppBytecodePass = new control.CompilerPass(
          "Bytecode printing", bytecodeAst, ppbc);
      ppBytecodePass.doit();
      break;
    case C:
      codegen.C.TranslateVisitor transC = new codegen.C.TranslateVisitor();
      control.CompilerPass genCCodePass = new control.CompilerPass(
          "C code generation", this.theAst, transC);
      genCCodePass.doit();
      codegen.C.Ast.Program.T cAst = transC.program;
      codegen.C.PrettyPrintVisitor ppc = new codegen.C.PrettyPrintVisitor();
      control.CompilerPass ppCCodePass = new control.CompilerPass(
          "C code printing", cAst, ppc);
      ppCCodePass.doit();
      break;
    case Dalvik:
      codegen.dalvik.TranslateVisitor transDalvik = new codegen.dalvik.TranslateVisitor();
      control.CompilerPass genDalvikCodePass = new control.CompilerPass(
          "Dalvik code generation", this.theAst, transDalvik);
      genDalvikCodePass.doit();
      codegen.dalvik.Ast.Program.T dalvikAst = transDalvik.program;

      codegen.dalvik.PrettyPrintVisitor ppDalvik = new codegen.dalvik.PrettyPrintVisitor();
      control.CompilerPass ppDalvikCodePass = new control.CompilerPass(
          "Dalvik code printing", dalvikAst, ppDalvik);
      ppDalvikCodePass.doit();
      break;
    case X86:
      // similar
      break;
    default:
      break;
    }
    return;
  }

  public void assemble(String str)
  {
    // Your code here:
  }

  public void link(String str)
  {
    // Your code here:
  }

  public void compileAndLink(String fname)
  {
    // compile
    control.CompilerPass compilePass = new control.CompilerPass("Compile",
        tiger, fname);
    compilePass.doitName("compile");

    // assembling
    control.CompilerPass assemblePass = new control.CompilerPass("Assembling",
        tiger, fname);
    assemblePass.doitName("assemble");

    // linking
    control.CompilerPass linkPass = new control.CompilerPass("Linking", tiger,
        fname);
    linkPass.doitName("link");

    return;
  }

  public static void main(String[] args)
  {
    // ///////////////////////////////////////////////////////
    // handle command line arguments
    tiger = new Tiger();
    cmd = new CommandLine();
    String fname = "";
    fname = cmd.scan(args);

    control.CompilerPass tigerAll = new control.CompilerPass("Tiger", tiger,
        fname);
    tigerAll.doitName("compileAndLink");
    return;
  }
}
