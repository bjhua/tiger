package slptest;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class TestSlp {
    public static slp.Slp.Stm prog1 = slp.SamplePrograms.sample1;

    @Test
    void testMaxArgs() {
        slp.MaxArgument max = new slp.MaxArgument();
        assertEquals(2, max.maxStm(prog1));
    }

    @Test
    public void testPrint() {
        slp.PrettyPrint pp = new slp.PrettyPrint();
        pp.ppStm(prog1);
    }

    // interpreter:
    @Test
    void testInterpreter() {
        slp.Interpreter interpreter = new slp.Interpreter();
        final StringBuilder builder = new StringBuilder();
        interpreter.interpStm(prog1, builder::append);
        assertEquals("8 7\n80\n", builder.toString());
    }

    //        // compiler to x64:
    // we disable this because we are not running on x64.
//    @Test
    void testCompiler() {
        slp.Compiler compiler = new slp.Compiler();
        String result = compiler.compile(prog1);
        assertEquals("8 7\n80\n", result);
    }
}
