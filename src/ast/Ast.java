package ast;

import java.util.List;

public class Ast {
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
    // expression
    public static class Exp {
        public interface T {
        }

        // binary operations
        public record Bop(T left, String op, T right) implements T {
        }

        // and, op is a boolean operator
        public record BopBool(T left, String op, T right) implements T {
        }

        // ArraySelect
        public record ArraySelect(T array, T index) implements T {
        }

        // Call
        public record Call(T exp,
                           String id,
                           List<T> args,
                           String type,     // type of first field "exp"
                           List<Type.T> at, // arg's type
                           Type.T rt) implements T {
        }

        // False
        public record False() implements T {
        }

        // Id
        public record Id(String id, Type.T type, boolean isField) implements T {
        }

        // length
        public record Length(T array) implements T {
        }

        // new int [e]
        public record NewIntArray(T exp) implements T {
        }

        // new A();
        public record NewObject(String id) implements T {
        }

        // !
        public record Uop(String op, T exp) implements T {
        }

        // number
        public record Num(int num) implements T {
        }

        // this
        public record This() implements T {
        }

        // True
        public record True() implements T {
        }

    }
    // end of expression

    // /////////////////////////////////////////////////////////
    // statement
    public static class Stm {
        public interface T {
        }

        // assign
        public record Assign(String id, Exp.T exp, Type.T type) implements T {
        }

        // assign-array
        public record AssignArray(String id, Exp.T index, Exp.T exp) implements T {
        }

        // block
        public record Block(List<T> stms) implements T {
        }

        // if
        public record If(Exp.T cond, T thenn, T elsee) implements T {
        }

        // Print
        public record Print(Exp.T exp) implements T {
        }

        // while
        public record While(Exp.T cond, T body) implements T {
        }
    }
    // end of statement

    // /////////////////////////////////////////////////////////
    // method
    public static class Method {
        public interface T {
        }

        public record MethodSingle(Type.T retType,
                                   String id,
                                   List<Dec.T> formals,
                                   List<Dec.T> locals,
                                   List<Stm.T> stms,
                                   Exp.T retExp) implements T {
        }
    }

    // class
    public static class Class {
        public interface T {
        }

        public record ClassSingle(String id,
                                  String extendss, // null for non-existing "extends"
                                  List<Dec.T> decs,
                                  List<ast.Ast.Method.T> methods) implements T {
        }
    }

    // main class
    public static class MainClass {
        public interface T {
        }

        public record MainClassSingle(String id, String arg, Stm.T stm)
                implements T {
        }
    }

    // whole program
    public static class Program {
        public interface T {
        }

        public record ProgramSingle(MainClass.T mainClass,
                                    List<Class.T> classes) implements T {
        }
    }
}
