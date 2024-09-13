package cfg;

import util.Id;
import util.Label;
import util.Todo;
import util.Tuple;
import util.set.Set;

import java.util.HashMap;
import java.util.List;

public class Liveness {

    // record information within a single "map"
    private final HashMap<Object, Tuple.Two<Set<Id>, Set<Id>>>
            useDefMap;

    // for "block", "transfer", and "statement".
    private final HashMap<Object, Tuple.Two<Set<Id>, Set<Id>>>
            liveInOutMap;

    public Liveness() {
        useDefMap = new HashMap<>();
        liveInOutMap = new HashMap<>();
    }


    // /////////////////////////////////////////////////////////
    // statement
    private void doitStm(Cfg.Stm.T t) {
        throw new Todo();
    }
    // end of statement

    // /////////////////////////////////////////////////////////
    // transfer
    private void doitTransfer(Cfg.Transfer.T t) {
        throw new Todo();
    }

    // /////////////////////////////////////////////////////////
    // block
    private void doitBlock(Cfg.Block.T b) {
        switch (b) {
            case Cfg.Block.Singleton(
                    Label label,
                    List<Cfg.Stm.T> phis,
                    List<Cfg.Stm.T> stms,
                    List<Cfg.Transfer.T> transfer
            ) -> throw new Todo();
        }
    }

    // /////////////////////////////////////////////////////////
    // function
    // TODO: lab3, exercise 9.
    private boolean stillChanging = true;

    private void doitFunction(Cfg.Function.T func) {
        switch (func) {
            case Cfg.Function.Singleton(
                    Cfg.Type.T retType,
                    Id classId,
                    Id functionId,
                    List<Cfg.Dec.T> formals,
                    List<Cfg.Dec.T> locals,
                    List<Cfg.Block.T> blocks
            ) -> throw new Todo();
        }
    }

    public HashMap<Object, Tuple.Two<Set<Id>, Set<Id>>>
    doitProgram(Cfg.Program.T prog) {
        switch (prog) {
            case Cfg.Program.Singleton(
                    Id mainClassId,
                    Id mainFuncId,
                    List<Cfg.Vtable.T> vtables,
                    List<Cfg.Struct.T> structs,
                    List<Cfg.Function.T> functions
            ) -> {
                functions.forEach(this::doitFunction);
                return liveInOutMap;
            }
        }
    }
}
