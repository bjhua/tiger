package ssa;

import cfg.Cfg.*;
import util.Id;
import util.Label;
import util.Todo;

import java.util.List;

// Conditional constant propagation.
public class Ccp {

    // /////////////////////////////////////////////////////////
    // statement
    private void doitStm(Stm.T t) {
        throw new Todo();
    }

    // /////////////////////////////////////////////////////////
    // transfer
    private void doitTransfer(Transfer.T t) {
        throw new Todo();
    }

    // /////////////////////////////////////////////////////////
    // block
    private void doitBlock(Block.T b) {
        switch (b) {
            case Block.Singleton(
                    Label label,
                    List<Stm.T> phis,
                    List<Stm.T> stms,
                    List<Transfer.T> transfer
            ) -> throw new Todo();
        }
    }

    // /////////////////////////////////////////////////////////
    // function
    private Function.T doitFunction(Function.T func) {
        switch (func) {
            case Function.Singleton(
                    Type.T retType,
                    Id classId,
                    Id functionId,
                    List<Dec.T> formals,
                    List<Dec.T> locals,
                    List<Block.T> blocks
            ) -> throw new Todo();
        }
    }

    // TODO: lab7.
    public Program.T doitProgram(Program.T prog) {
        switch (prog) {
            case Program.Singleton(
                    Id mainClassId,
                    Id mainFuncId,
                    List<Vtable.T> vtables,
                    List<Struct.T> structs,
                    List<Function.T> functions
            ) -> {
                var newFunctions =
                        functions.stream().map(this::doitFunction).toList();
                // TODO: your code here:
                //
                return prog;
            }
        }
    }
}
