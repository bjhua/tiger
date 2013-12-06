package ast.optimizations;

public class Main
{
  public ast.program.T program;
  
  public void accept(ast.program.T ast)
  {
    DeadClass dceVisitor = new DeadClass();
    control.CompilerPass deadClassPass = new control.CompilerPass(
        "Dead class elimination", ast, dceVisitor);
    if (control.Control.skipPass("ast.DeadClass")){
    }else{
      deadClassPass.doit();
      ast = dceVisitor.program;
    }
    
    DeadCode dcodeVisitor = new DeadCode();
    control.CompilerPass deadCodePass = new control.CompilerPass(
        "Dead code elimination", ast, dcodeVisitor);
    if (control.Control.skipPass("ast.DeadCode")){
    }else{
      deadCodePass.doit();
      ast = dcodeVisitor.program;
    }
    

    AlgSimp algVisitor = new AlgSimp();
    control.CompilerPass algPass = new control.CompilerPass(
        "Algebraic simplification", ast, algVisitor);
    if (control.Control.skipPass("ast.AlgSimp")){
    }else{
      algPass.doit();
      ast = algVisitor.program;
    }

    ConstFold cfVisitor = new ConstFold();
    control.CompilerPass constFoldPass = new control.CompilerPass(
        "Const folding", ast, cfVisitor);
    if (control.Control.skipPass("ast.ConstFold")){
    }else{
      constFoldPass.doit();
      ast = cfVisitor.program;
    }    

    program = ast;
    
    return;
  }
}
