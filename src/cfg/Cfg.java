package cfg;

import ast.Ast;
import util.Label;

import java.util.List;

public class Cfg {

    // /////////////////////////////////////////////////////////
    // virtual function table
    public static class Vtable {
        public record Entry(Ast.Type.T retType,
                            String funcName,
                            List<Ast.Dec.T> argTypes) {
        }

        public record T(String name,
                        List<Entry> funcTypes) {
        }
    }

    // /////////////////////////////////////////////////////////
    // structures
    public static class Struct {
        public record T(String name,
                        List<Ast.Dec.T> fields) {
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
        public record Id(String x, Ast.Type.T ty) implements T {
        }
    }
    // end of value

    // /////////////////////////////////////////////////////////
    // statement
    public static class Stm {
        public interface T {
        }

        // assign
        public record Assign(String id, Value.T right, Ast.Type.T type) implements T {
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
        public record T(Ast.Type.T retType,
                        String id,
                        List<Ast.Dec.T> formals,
                        List<Ast.Dec.T> locals,
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
