package slp;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.*;
import java.lang.*;

import slp.Slp.Exp;
import slp.Slp.Exp.Eseq;
import slp.Slp.Exp.Id;
import slp.Slp.Exp.Num;
import slp.Slp.Exp.Op;
import slp.Slp.ExpList;
import slp.Slp.Stm.Print;
import slp.Slp.ExpList.*;
import slp.Slp.Stm;
import util.Bug;
import util.Todo;
import control.Control;

public class Main
{
  // ///////////////////////////////////////////
  // maximum number of args
	
  private int maxArgsExp(Exp.T exp)
  {
	int a,b;
	if(exp instanceof Op)
	{
		Op c;
		c = (Op)exp;
		a = maxArgsExp(c.left);
		b = maxArgsExp(c.right);
		return a >= b ? a:b;
	}
    if(exp instanceof Exp.Eseq)
    {
       Eseq d;
       d = (Eseq)exp;
       a = maxArgsStm(d.stm);
       b = maxArgsExp(d.exp);
       return a >= b ? a : b;
    }
    else
       return 0;
  }
  
  private int maxArgsStm(Stm.T stm)
  {
    if (stm instanceof Stm.Compound) {
      Stm.Compound s = (Stm.Compound) stm;
      int n1 = maxArgsStm(s.s1);
      int n2 = maxArgsStm(s.s2);
      return n1 >= n2 ? n1 : n2;
    } 
    else if (stm instanceof Stm.Assign) 
    {
      return maxArgsExp(((Stm.Assign) stm).exp);
    } 
    else if (stm instanceof Stm.Print) 
    {
      return countArgs(((Stm.Print)stm));
    } 
    else
      new Bug();
    return 0;
  }
  
  static int countArgs(Stm.Print s)
  {
	  return countExpression(s.explist);
  }
  
  static int countExpression(ExpList.T exps)
  {
	  if(exps==null)
		  return 0;
	  if(exps instanceof ExpList.Last)
		  return 1;
	  return 1+countExpression(((Pair)exps).list);
  }
  // ////////////////////////////////////////
  // interpreter
  Hashtable<String,Integer> res=new Hashtable<String,Integer>();
  private int interpExp(Exp.T exp)
  {
    if(exp instanceof Exp.Id)
    {
        return res.get(((Id)exp).id);
    }
    else if(exp instanceof Exp.Num)
    {
    	return ((Num)exp).num;
    }
    else if(exp instanceof Exp.Op)
    {
    	Op oe;
    	int r1,r2;
    	int val;
    	oe = (Op)exp;
    	r1 = interpExp(oe.left);
    	r2 = interpExp(oe.right);
    	switch(oe.op)
    	{
	    	case ADD  :{
	    		val = r1+r2;
	    		break;
	    	}
	    	case SUB  :{
	    		val = r1-r2;
	    		break;
	    	}
	    	case TIMES:{
	    		val = r1*r2;
	    		break;
	    	}
	    	case DIVIDE:{
	    		val = r1/r2;
	    		break;
	    	}
	    	default: 
	    		val = 0;
	    		break;
    	}
    	return val;
    }
    else //(exp instanceof Exp.Eseq)
    {
    	Exp.Eseq r1 = (Exp.Eseq)exp;
    	interpStm(r1.stm);
    	return interpExp(r1.exp);
    }
  }
  
  private void interpStm(Stm.T prog)
  {
	int intback;
    if (prog instanceof Stm.Compound) 
    {
      Stm.Compound r3;
      r3 = (Stm.Compound)prog;
      interpStm(r3.s1);
      interpStm(r3.s2);
    } 
    else if (prog instanceof Stm.Assign) 
    {
      Stm.Assign r1;
      r1 = (Stm.Assign)prog;
      intback = interpExp(((Stm.Assign)prog).exp);
      res.put(((Stm.Assign)prog).id, intback);
    }
    else if (prog instanceof Stm.Print) 
    {
        print((Stm.Print)prog);
    } 
    else
    	new Bug();
  }
  
  void print(Stm.Print s)
  {
	  ExpList.T r1 = s.explist;
	  if(r1 instanceof ExpList.Last)
	  {
		 Exp.T r2 = ((ExpList.Last)r1).exp;
		 int x = interpExp(r2);
		 System.out.println(x);
	  }
	  if(r1 instanceof ExpList.Pair)
	  {
		 System.out.print(interpExp(((ExpList.Pair)r1).exp)+" ");
		 printExpList(((ExpList.Pair)r1).list);
	  }
  }
  
  void printExpList(ExpList.T s)
  {
	  if(s instanceof ExpList.Last)
	  {
		 Exp.T r2 = ((ExpList.Last)s).exp;
		 int x = interpExp(r2);
		 System.out.println(x);
	  }
	  if(s instanceof ExpList.Pair)
	  {
		 System.out.print(interpExp(((ExpList.Pair)s).exp)+" ");
		 printExpList(((ExpList.Pair)s).list);
	  }
  }
  
  // ////////////////////////////////////////
  // compile
  HashSet<String> ids;
  StringBuffer buf;

  private void emit(String s)
  {
    buf.append(s);
  }

  private void compileExp(Exp.T exp)
  {
    if (exp instanceof Id) {
      Exp.Id e = (Exp.Id) exp;
      String id = e.id;

      emit("\tmovl\t" + id + ", %eax\n");
    } else if (exp instanceof Num) {
      Exp.Num e = (Exp.Num) exp;
      int num = e.num;

      emit("\tmovl\t$" + num + ", %eax\n");
    } else if (exp instanceof Op) {
      Exp.Op e = (Exp.Op) exp;
      Exp.T left = e.left;
      Exp.T right = e.right;
      Exp.OP_T op = e.op;

      switch (op) {
      case ADD:
        compileExp(left);
        emit("\tpushl\t%eax\n");
        compileExp(right);
        emit("\tpopl\t%edx\n");
        emit("\taddl\t%edx, %eax\n");
        break;
      case SUB:
        compileExp(left);
        emit("\tpushl\t%eax\n");
        compileExp(right);
        emit("\tpopl\t%edx\n");
        emit("\tsubl\t%eax, %edx\n");
        emit("\tmovl\t%edx, %eax\n");
        break;
      case TIMES:
        compileExp(left);
        emit("\tpushl\t%eax\n");
        compileExp(right);
        emit("\tpopl\t%edx\n");
        emit("\timul\t%edx\n");
        break;
      case DIVIDE:
    	if(interpExp(right)!=0)
    	{
	      compileExp(left);
	      emit("\tpushl\t%eax\n");
	      compileExp(right);
	      emit("\tpopl\t%edx\n");
	      emit("\tmovl\t%eax, %ecx\n");
	      emit("\tmovl\t%edx, %eax\n");
	      emit("\tcltd\n");
	      emit("\tdiv\t%ecx\n");
	      break;
    	}
    	else
    	{
    	  emit("\tpushl\t$exception\n");
    	  emit("\tcall\tprintf\n");
    	  emit("\taddl\t$4, %esp\n");
    	  break;
    	}
      default:
        new Bug();
      }
    } else if (exp instanceof Eseq) {
      Eseq e = (Eseq) exp;
      Stm.T stm = e.stm;
      Exp.T ee = e.exp;

      compileStm(stm);
      compileExp(ee);
    } else
      new Bug();
  }

  private int compileExpList(ExpList.T explist)
  {
    if (explist instanceof ExpList.Pair) {
      ExpList.Pair pair = (ExpList.Pair) explist;
      Exp.T exp = pair.exp;
      ExpList.T list = pair.list;
      
      compileExp(exp);
    //判断是否会出现被除数为0
      if(pair.exp instanceof Exp.Op)
      {
    	  Exp.Op r = (Exp.Op)pair.exp;
    	  if(r.op==Exp.OP_T.DIVIDE && interpExp(r.right)==0)
    	  {
    		  return 0;
    	  }
      }
      emit("\tpushl\t%eax\n");
      emit("\tpushl\t$slp_format\n");
      emit("\tcall\tprintf\n");
      emit("\taddl\t$4, %esp\n");
      compileExpList(list);
      return 1;
    } else if (explist instanceof ExpList.Last) {
      ExpList.Last last = (ExpList.Last) explist;
      Exp.T exp = last.exp;
    //判断是否会出现被除数为0
      compileExp(exp);
      if(last.exp instanceof Exp.Op)
      {
    	  Exp.Op r = (Exp.Op)last.exp;
    	  if(r.op==Exp.OP_T.DIVIDE && interpExp(r.right)==0)
    	  {
    		  return 0;
    	  }
      }
      emit("\tpushl\t%eax\n");
      emit("\tpushl\t$slp_format\n");
      emit("\tcall\tprintf\n");
      emit("\taddl\t$4, %esp\n");
      return 1;
    } else
    {
      new Bug();
      return 1;
    }
  }

  private void compileStm(Stm.T prog)
  {
    if (prog instanceof Stm.Compound) {
      Stm.Compound s = (Stm.Compound) prog;
      Stm.T s1 = s.s1;
      Stm.T s2 = s.s2;

      compileStm(s1);
      compileStm(s2);
    } else if (prog instanceof Stm.Assign) {
      Stm.Assign s = (Stm.Assign) prog;
      String id = s.id;
      Exp.T exp = s.exp;

      ids.add(id);
      compileExp(exp);
      emit("\tmovl\t%eax, " + id + "\n");
    } else if (prog instanceof Stm.Print) {
      Stm.Print s = (Stm.Print) prog;
      ExpList.T explist = s.explist;
      if(compileExpList(explist)==1)
      { 
    	  emit("\tpushl\t$newline\n");
    	  emit("\tcall\tprintf\n");
    	  emit("\taddl\t$4, %esp\n");
      }
    } else
      new Bug();
  }

  // ////////////////////////////////////////
  public void doit(Stm.T prog)
  {
    // return the maximum number of arguments
    if (Control.ConSlp.action == Control.ConSlp.T.ARGS) {
      int numArgs = maxArgsStm(prog);
      System.out.println(numArgs);
    }

    // interpret a given program
    if (Control.ConSlp.action == Control.ConSlp.T.INTERP) {
       interpStm(prog);
    }

    // compile a given SLP program to x86
    if (Control.ConSlp.action == Control.ConSlp.T.COMPILE) {
      ids = new HashSet<String>();
      buf = new StringBuffer();
      
      compileStm(prog);       //编译生成汇编码送往buf
      try {
        // FileOutputStream out = new FileOutputStream();
        FileWriter writer = new FileWriter("slp_gen.s");
        writer.write("// Automatically generated by the Tiger compiler, do NOT edit.\n\n");
        writer.write("\t.data\n");
        writer.write("slp_format:\n");
        writer.write("\t.string \"%d \"\n");
        writer.write("newline:\n");
        writer.write("\t.string \"\\n\"\n");
        writer.write("exception:\n");
        writer.write("\t.string \"\\divide by zero\"\n");
        for (String s : this.ids) {
          writer.write(s + ":\n");
          writer.write("\t.int 0\n");
        }
        writer.write("\n\n\t.text\n");
        writer.write("\t.globl main\n");
        writer.write("main:\n");
        writer.write("\tpushl\t%ebp\n");
        writer.write("\tmovl\t%esp, %ebp\n");
        writer.write(buf.toString());
        writer.write("\tleave\n\tret\n\n");
        writer.close();
        Process child = Runtime.getRuntime().exec("gcc slp_gen.s");
        child.waitFor();
        if (!Control.ConSlp.div)
          Runtime.getRuntime().exec("rm -rf slp_gen.s");
      } catch (Exception e) {
        e.printStackTrace();
        System.exit(0);
      }
      // System.out.println(buf.toString());
    }
  }
}
