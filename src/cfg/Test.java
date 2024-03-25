package cfg;

import ast.PrettyPrinter;
import ast.SamplePrograms;
import checker.Checker;

public class Test {
    public static void main(String[] args) throws Exception {
        //
        Checker checker = new Checker();
        checker.checkProgram(SamplePrograms.progSumRec);
        Translate trans = new Translate();
        Cfg.Program.T cfg = trans.translate(SamplePrograms.progSumRec);
        Cfg.Program.pp(cfg);
    }
}
