package codegen;

import cfg.Cfg;
import util.*;

import java.util.List;

public class Layout {
    public final int vtablePtrOffsetInObject;

    // to record the size of a class
    private final Property<Id, Integer> sizeOfClassProp;
    // to record the offset of class fields and methods
    private final Property<Id, Integer> offsetProp;

    Layout() {
        this.vtablePtrOffsetInObject = 0;
        this.sizeOfClassProp = new Property<>(Id::getPlist);
        this.offsetProp = new Property<>(Id::getPlist);
    }

    private Object doitProgram0(Cfg.Program.T cfg) {
        // TODO: lab 4, exercise 2
        throw new Todo();
    }

    public Tuple.Two<Property<Id, Integer>,
            Property<Id, Integer>> doitProgram(Cfg.Program.T cfg) {
        switch (cfg) {
            case Cfg.Program.Singleton(
                    Id entryClassId,
                    Id entryFuncId,
                    List<Cfg.Vtable.T> vtables,
                    List<Cfg.Struct.T> structs,
                    List<Cfg.Function.T> functions
            ) -> {
                Trace<Cfg.Program.T, Object> trace =
                        new Trace<>("codegen.Layout.doitProgram",
                                this::doitProgram0,
                                cfg,
                                Cfg.Program::pp,
                                (_) -> {
                                    //
                                    structs.forEach((s) -> {
                                        switch (s) {
                                            case Cfg.Struct.Singleton(
                                                    Id clsId,
                                                    List<Cfg.Dec.T> fields
                                            ) -> {
                                                System.out.println("class " + clsId.toString() + " size = " + sizeOfClassProp.get(clsId).toString());
                                                for (var entry : fields) {
                                                    // TODO: lab 4, exercise 2
                                                    throw new Todo();
                                                }
                                            }
                                        }
                                    });
                                    //
                                    vtables.forEach((s) -> {
                                        // TODO: lab 4, exercise 2.
                                        throw new Todo();
                                    });
                                });
                trace.doit();
                return new Tuple.Two<>(sizeOfClassProp, offsetProp);
            }
        }
    }
}


