package ast;

public class Test {
    public static void main(String[] args) throws Exception {
        // to test the pretty printer
        PrettyPrinter pp = new PrettyPrinter();
        pp.ppProgram(SamplePrograms.progSumRec);
    }
}
