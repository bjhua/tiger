package cfg;

import ast.PrettyPrinter;
import ast.SamplePrograms;

public class Test {
    public static void main(String[] args) throws Exception {
        //
        Translate trans = new Translate();
        Cfg.Program.T cfg = trans.translate(SamplePrograms.progSumRec);
        Cfg.Program.pp(cfg);
    }
}
