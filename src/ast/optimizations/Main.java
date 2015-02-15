package ast.optimizations;

import ast.Ast.Program;

public class Main
{
  public ast.Ast.Program.T program;
  public boolean flag = true;
  int num = 1;
  public void accept(ast.Ast.Program.T ast)
  {
	while(true)
	{
		//System.out.println("----------------------------------");
		System.out.println("      The "+num+" round of optimization begin:");
	    DeadClass dceVisitor = new DeadClass();
	    control.CompilerPass deadClassPass = new control.CompilerPass(
	        "Dead class elimination", ast, dceVisitor);
	    if (control.Control.skipPass("ast.DeadClass"))
	    {
	    }
	    else
	    {
	       deadClassPass.doit();
	       ast = dceVisitor.program;
	    }
	    if(!dceVisitor.t1)
	    	System.out.println("Dead Class Elimination optimization finished!!!\n");
	    flag = dceVisitor.t1 ;

	    
	    DeadCode dcodeVisitor = new DeadCode();
	    control.CompilerPass deadCodePass = new control.CompilerPass(
	        "Dead code elimination", ast, dcodeVisitor);
	    if (control.Control.skipPass("ast.DeadCode"))
	    {
	    }
	    else
	    {
	    	deadCodePass.doit();
	        ast = dcodeVisitor.program;
	    }
	    if(!dcodeVisitor.t2)
	    	System.out.println("      Dead Code Elimination optimization finished!!!\n");
	    flag = flag && dcodeVisitor.t2 ;
	    
	    
	    AlgSimp algVisitor = new AlgSimp();
	    control.CompilerPass algPass = new control.CompilerPass(
	        "Algebraic simplification", ast, algVisitor);
	    if (control.Control.skipPass("ast.AlgSimp"))
	    {
	    }
	    else
	    {
	    	algPass.doit();
	      	ast = algVisitor.program;
	    }
	    if(!algVisitor.t3)
	    	System.out.println("Algebraic simplification optimization finished!!!\n");
	    flag = algVisitor.t3 && flag;
	    ConstFold cfVisitor = new ConstFold();
	    control.CompilerPass constFoldPass = new control.CompilerPass(
	    		"Const folding", ast, cfVisitor);
	    if (control.Control.skipPass("ast.ConstFold")){
	    }
	    else
	    {
	      constFoldPass.doit();
	      ast = cfVisitor.program;
	    }
	    if(!cfVisitor.t4)
	    	System.out.println("Const folding optimization finished!!!\n");
	    flag = flag && cfVisitor.t4;
		System.out.println("      The "+num+" round of optimization end.\n");
		//System.out.println("---------------------------------\n\n");
		num++;
	    if(flag)
	       break;
	}
    program = ast;
    return;
  }
}
