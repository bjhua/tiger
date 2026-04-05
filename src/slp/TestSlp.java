package slp;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class TestSlp {
    public static Slp2.Stm prog1 = SamplePrograms.sample1;

    @Test
    void testMaxArgs() {
        MaxArgument max = new MaxArgument();
        assertEquals(2, max.maxStm(prog1));
    }

    @Test
    public void testPrint() {
        PrettyPrint pp = new PrettyPrint();
        pp.ppStm(prog1);
    }

    // interpreter:
    @Test
    void testInterpreter() {
        Interpreter interpreter = new Interpreter();
        final StringBuilder builder = new StringBuilder();
        interpreter.interpreteStm(prog1, builder::append);
        assertEquals("8 7\n80\n", builder.toString());
    }

    //        // compiler to x64:
    @Test
    void testCompiler() {
        Compiler compiler = new Compiler();
        String result = compiler.compile(prog1);
        assertEquals("8 7\n80\n", result);
    }
}
