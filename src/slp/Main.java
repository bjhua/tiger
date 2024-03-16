package slp;

public class Main {

    public static void main(String[] args) throws Exception {
        // pretty print:
//        PrettyPrint pp = new PrettyPrint();
//        pp.ppStm(SamplePrograms.sample1);
//        pp.ppStm(SamplePrograms.sample2);
//
//        // maximum argument:
//        MaxArgument max = new MaxArgument();
//        max.maxStm(SamplePrograms.sample1);
//        max.maxStm(SamplePrograms.sample2);
//
//        // interpreter:
//        Interpreter interp = new Interpreter();
//        interp.interpStm(SamplePrograms.sample1);
//        interp.interpStm(SamplePrograms.sample2);

        // interpreter:
        Compiler compiler = new Compiler();
        compiler.compileStm(SamplePrograms.sample1);
//        compiler.compileStm(SamplePrograms.sample2);
    }
}
