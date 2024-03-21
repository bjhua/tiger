package cfg;

import util.Label;

import java.util.List;

public class Cfg {

    //  ///////////////////////////////////////////////////////////
    //  type
    public static class Type {
        public interface T {
            // boolean: -1
            // int: 0
            // int[]: 1
            // class: 2
            // Such that one can easily tell who is who
            public abstract int getNum();
        }

        // boolean
        public record Boolean() implements T {
            @Override
            public int getNum() {
                return -1;
            }
        }

        // class
        public record ClassType(String id) implements T {
            @Override
            public int getNum() {
                return 2;
            }
        }

        // int
        public record Int() implements T {
            @Override
            public int getNum() {
                return 0;
            }
        }

        // int[]
        public record IntArray() implements T {
            @Override
            public int getNum() {
                return 1;
            }
        }
    }

    // ///////////////////////////////////////////////////
    // declaration
    public static class Dec {
        public interface T {
        }

        public record DecSingle(Type.T type, String id) implements T {
        }
    }

    // /////////////////////////////////////////////////////////
    // virtual function table
    public static class Vtable {
        public record VtableEntry(Type.T retType,
                                  String funcName,
                                  List<Dec.T> argTypes) {
        }

        public record T(List<VtableEntry> funcTypes) {
        }
    }

    // /////////////////////////////////////////////////////////
    // structures
    public static class Struct {
        public record T(Vtable.T theVtable,
                        List<Dec.T> fields) {
        }
    }

    // /////////////////////////////////////////////////////////
    // values
    public static class Value {
        public interface T {
        }

        // integer constant
        public record Int(int n) implements T {
        }

        // variable
        public record Id(String x, Type.T ty) implements T {
        }
    }
    // end of value

    // /////////////////////////////////////////////////////////
    // statement
    public static class Stm {
        public interface T {
        }

        // assign
        public record Assign(String id, Value.T right, Type.T type) implements T {
        }

        // assign-array
        public record AssignArray(String id, Value.T index, Value.T right) implements T {
        }


        // Print
        public record Print(Value.T value) implements T {
        }
    }
    // end of statement

    // /////////////////////////////////////////////////////////
    // block
    public static class Block {
        public record T(Label label,
                        List<Stm.T> stms,
                        Transfer.T transfer) {
        }
    }

    // /////////////////////////////////////////////////////////
    // transfer
    public static class Transfer {
        public interface T {
        }

        public record Jmp(Label label) implements T {
        }
    }

    // /////////////////////////////////////////////////////////
    // function
    public static class Function {
        public record T(Type.T retType,
                        String id,
                        List<Dec.T> formals,
                        List<Dec.T> locals,
                        List<Block.T> blocks) {
        }
    }

    // whole program
    public static class Program {
        public record T(String entryFuncName, // name of the entry function
                        List<Vtable.T> vtables,
                        List<Struct.T> structs,
                        List<Function.T> functions) {
        }
    }
}
