package ast;

import java.util.List;

public class Ast {
    //  ///////////////////////////////////////////////////////////
    //  type
    public static class Type {
        public interface T extends ast.Acceptable {
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
            public String toString() {
                return "@boolean";
            }

            @Override
            public int getNum() {
                return -1;
            }

            @Override
            public void accept(Visitor v) {
                v.visit(this);
            }
        }

        // class
        public record ClassType(String id) implements T {
            @Override
            public String toString() {
                return this.id;
            }

            @Override
            public int getNum() {
                return 2;
            }

            @Override
            public void accept(Visitor v) {
                v.visit(this);
            }
        }

        // int
        public record Int() implements T {
            @Override
            public String toString() {
                return "@int";
            }

            @Override
            public void accept(Visitor v) {
                v.visit(this);
            }

            @Override
            public int getNum() {
                return 0;
            }
        }

        // int[]
        public record IntArray() implements T {
            @Override
            public String toString() {
                return "@int[]";
            }

            @Override
            public int getNum() {
                return 1;
            }

            @Override
            public void accept(Visitor v) {
                v.visit(this);
            }
        }
    }

    // ///////////////////////////////////////////////////
    // dec
    public static class Dec {
        public interface T extends ast.Acceptable {}

        public record DecSingle(Type.T type, String id) implements T {
            @Override
            public void accept(Visitor v) {
                v.visit(this);
            }
        }
    }

    // /////////////////////////////////////////////////////////
    // expression
    public static class Exp {
        public interface T extends ast.Acceptable {}

        // +
        public record Bop(T left, String op, T right) implements T {
            @Override
            public void accept(ast.Visitor v) {
                v.visit(this);
                return;
            }
        }

        // and
        public record BopBool(T left, String op, T right) implements T {
            @Override
            public void accept(ast.Visitor v) {
                v.visit(this);
                return;
            }
        }

        // ArraySelect
        public record ArraySelect(T array, T index) implements T {
            @Override
            public void accept(ast.Visitor v) {
                v.visit(this);
                return;
            }
        }

        // Call
        public record Call(T exp, String id, List<T> args,
                           String type,     // type of first field "exp"
                           List<Type.T> at, // arg's type
                           Type.T rt) implements T {
            @Override
            public void accept(ast.Visitor v) {
                v.visit(this);
                return;
            }
        }

        // False
        public record False() implements T {
            @Override
            public void accept(ast.Visitor v) {
                v.visit(this);
                return;
            }
        }

        // Id
        public record Id(String id, Type.T type, boolean isField) implements T {
            @Override
            public void accept(ast.Visitor v) {
                v.visit(this);
                return;
            }
        }

        // length
        public record Length(T array) implements T {
            @Override
            public void accept(ast.Visitor v) {
                v.visit(this);
                return;
            }
        }

        // new int [e]
        public record NewIntArray(T exp) implements T {
            @Override
            public void accept(ast.Visitor v) {
                v.visit(this);
                return;
            }
        }

        // new A();
        public record NewObject(String id) implements T {
            @Override
            public void accept(ast.Visitor v) {
                v.visit(this);
                return;
            }
        }

        // !
        public record Uop(String op, T exp) implements T {
            @Override
            public void accept(ast.Visitor v) {
                v.visit(this);
                return;
            }
        }

        // number
        public record Num(int num) implements T {
            @Override
            public void accept(ast.Visitor v) {
                v.visit(this);
                return;
            }
        }

        // this
        public record This() implements T {
            @Override
            public void accept(ast.Visitor v) {
                v.visit(this);
                return;
            }
        }

        // True
        public record True() implements T {
            @Override
            public void accept(ast.Visitor v) {
                v.visit(this);
                return;
            }
        }

    } // end of expression

    // /////////////////////////////////////////////////////////
    // statement
    public static class Stm {
        public interface T extends ast.Acceptable {}

        // assign
        public record Assign(String id, Exp.T exp, Type.T type) implements T {
            @Override
            public void accept(ast.Visitor v) {
                v.visit(this);
            }
        }

        // assign-array
        public record AssignArray(String id, Exp.T index, Exp.T exp)
            implements T {
            @Override
            public void accept(ast.Visitor v) {
                v.visit(this);
            }
        }

        // block
        public record Block(List<T> stms) implements T {
            @Override
            public void accept(ast.Visitor v) {
                v.visit(this);
            }
        }

        // if
        public record If(Exp.T cond, T thenn, T elsee) implements T {
            @Override
            public void accept(ast.Visitor v) {
                v.visit(this);
            }
        }

        // Print
        public record Print(Exp.T exp) implements T {
            @Override
            public void accept(ast.Visitor v) {
                v.visit(this);
            }
        }

        // while
        public record While(Exp.T cond, T body) implements T {
            @Override
            public void accept(ast.Visitor v) {
                v.visit(this);
            }
        }

    } // end of statement

    // /////////////////////////////////////////////////////////
    // method
    public static class Method {
        public interface T extends ast.Acceptable {}

        public record MethodSingle(Type.T retType, String id,
                                   List<Dec.T> formals, List<Dec.T> locals,
                                   List<Stm.T> stms, Exp.T retExp)
            implements T {
            @Override
            public void accept(Visitor v) {
                v.visit(this);
            }
        }
    }

    // class
    public static class Class {
        public interface T extends ast.Acceptable {}

        public record
        ClassSingle(String id,
                    String extendss, // null for non-existing "extends"
                    List<Dec.T> decs, List<ast.Ast.Method.T> methods)
            implements T {
            @Override
            public void accept(Visitor v) {
                v.visit(this);
            }
        }
    }

    // main class
    public static class MainClass {
        public interface T extends ast.Acceptable {}

        public record MainClassSingle(String id, String arg, Stm.T stm)
            implements T {
            @Override
            public void accept(Visitor v) {
                v.visit(this);
                return;
            }
        }
    }

    // whole program
    public static class Program {
        public interface T extends ast.Acceptable {}

        public record ProgramSingle(MainClass.T mainClass,
                                    List<Class.T> classes) implements T {
            @Override
            public void accept(Visitor v) {
                v.visit(this);
                return;
            }
        }
    }
}
