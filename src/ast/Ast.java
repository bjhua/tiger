package ast;

import util.Todo;

import java.util.List;

public class Ast {
    //  ///////////////////////////////////////////////////////////
    //  type
    public static class Type {
        public sealed interface T
                permits Boolean, ClassType, Int, IntArray {
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

        public static boolean equals(Type.T ty1, Type.T ty2) {
            if (ty1 == ty2)
                return true;
            if (ty1 instanceof ClassType classType1 &&
                    ty2 instanceof ClassType classType2) {
                return classType1.id.equals(classType2.id);
            }
            return ty1.getClass().equals(ty2.getClass());
        }

        public static void output(Type.T ty) throws Exception {
            switch (ty) {
                case Type.Int() -> {
                    System.out.print("int");
                }
                default -> {
                    throw new Todo();
                }
            }
        }
    }

    // ///////////////////////////////////////////////////
    // declaration
    public static class Dec {
        public sealed interface T permits Singleton {
        }

        public record Singleton(Type.T type,
                                String id) implements T {
            @Override
            public boolean equals(Object obj) {
                if (obj == null)
                    return false;
//                if(!instanceof(obj, Singleton))
//                    return false;
                return Dec.isEqual(this, (Singleton) obj);
            }
        }

        // operations
        public static boolean isEqual(T x, T y) {
            switch (x) {
                case Singleton(
                        Type.T type1,
                        String id1
                ) -> {
                    switch (y) {
                        case Singleton(
                                Type.T type2,
                                String id2
                        ) -> {
                            return id1.equals(id2);
                        }
                    }
                }
            }
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
                           // these fields will
                           List<Type.T> calleeType_0,     // type of first field "exp"
                           List<Type.T> argTypes, // arguments type
                           List<Type.T> retType_0) implements T {
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
        public sealed interface T
                permits Assign, AssignArray, Block, If, Print, While {
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

        // System.out.println
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
        public sealed interface T permits Singleton {
        }

        public record Singleton(Type.T retType,
                                String id,
                                List<Dec.T> formals,
                                List<Dec.T> locals,
                                List<Stm.T> stms,
                                Exp.T retExp) implements T {
        }
    }

    // class
    public static class Class {
        public sealed interface T permits Singleton {
        }

        public record Singleton(String id,
                                String extends_, // null for non-existing "extends"
                                List<Dec.T> decs,
                                List<ast.Ast.Method.T> methods) implements T {
        }

        public static String getName(T cls) {
            switch (cls) {
                case Singleton(String id, _, _, _) -> {
                    return id;
                }
            }
        }
    }

    // main class
    public static class MainClass {
        public sealed interface T permits Singleton {
        }

        public record Singleton(String id,
                                String arg,
                                Stm.T stm) implements T {
        }
    }

    // whole program
    public static class Program {
        public sealed interface T permits Singleton {
        }

        public record Singleton(MainClass.T mainClass,
                                List<Class.T> classes) implements T {
        }

        // operations on the whole programs
        public static Class.T searchClass(T prog, String className) {
            switch (prog) {
                case Singleton(
                        _,
                        List<Class.T> classes
                ) -> {
                    for (Class.T cls : classes) {
                        switch (cls) {
                            case Ast.Class.Singleton(
                                    String id,
                                    _,
                                    _,
                                    _
                            ) -> {
                                if (id.equals(className))
                                    return cls;
                            }
                        }
                    }
                    return null;
                }
            }
        }

        public static List<Class.T> getClasses(T prog) {
            switch (prog) {
                case Singleton(
                        _,
                        List<Class.T> classes
                ) -> {
                    return classes;
                }
            }
        }

        public static MainClass.T getMainClass(T prog) {
            switch (prog) {
                case Singleton(
                        MainClass.T mainClass,
                        _
                ) -> {
                    return mainClass;
                }
            }
        }
    }
}
