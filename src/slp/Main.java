package slp;

public class Main {


    public static void main(String[] args) throws Exception{
        // create a new interpreter and compiler
        InterpAndCompile interpAndCompile = new InterpAndCompile();
        // then apply to two sample programs, respectively.
        interpAndCompile.doit(SamplePrograms.prog);
        interpAndCompile.doit(SamplePrograms.divideByZero);
    }
}
