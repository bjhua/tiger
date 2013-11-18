package ast.optimizations;

public class Main
{
  public ast.program.T doit (ast.program.T ast)
  {
    control.CompilerPass deadClassPass
    = new control.CompilerPass("Dead class elimination");
    deadClassPass.start();
    DeadClass dceVisitor = new DeadClass();
    ast.accept(dceVisitor);
    ast = dceVisitor.program;
    deadClassPass.finish();
    
    control.CompilerPass deadCodePass
    = new control.CompilerPass("Dead code elimination");
    deadCodePass.start();
    DeadCode dcodeVisitor = new DeadCode();
    ast.accept(dcodeVisitor);
    ast = dcodeVisitor.program;
    deadCodePass.finish();
    
    control.CompilerPass algPass
    = new control.CompilerPass("Algebraic simplification");
    algPass.start();
    AlgSimp algVisitor = new AlgSimp();
    ast.accept(algVisitor);
    ast = algVisitor.program;
    algPass.finish();
    
    control.CompilerPass constFoldPass
    = new control.CompilerPass("Const folding");
    constFoldPass.start();
    ConstFold cfVisitor = new ConstFold();
    ast.accept(cfVisitor);
    ast = cfVisitor.program;
    constFoldPass.finish();
    
    return ast;
  }
}
