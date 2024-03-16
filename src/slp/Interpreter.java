package slp;

import slp.Slp.Exp;
import slp.Slp.Stm;
import util.Todo;

import java.util.HashMap;

// this defines an interpreter for the SLP language.
public class Interpreter {
    // an abstract memory to hold the values
    HashMap<String, Integer> memory = new HashMap<>();

    // ///////////////////////////////////////////
    // interpret an expression
    private int interpExp(Exp.T exp) throws Exception {
        throw new Todo();
    }

    // ///////////////////////////////////////////
    // interpret a statement
    public void interpStm(Stm.T stm) throws Exception {
        throw new Todo();
    }
}
