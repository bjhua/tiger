package control;

import ast.Ast;

import java.util.LinkedList;
import java.util.List;

public class Control {

    public enum Verbose {
        L_1(-1),
        L0(0), // top level
        L1(1),
        L2(2);
        public final int order;

        Verbose(int i) {
            this.order = i;
        }
    }

    public static Verbose verbose = Verbose.L_1;


    static List<String> tracedMethods = new LinkedList<>();

    public static boolean beingTraced(String method) {
        return tracedMethods.contains(method);
    }


    // this is a special hack to test the compiler
    // without hacking the lexer and parser.
    public static Ast.Program.T bultinAst = null;

    // dot-related
    public static class Dot {
        public static boolean keep = false;
        public static String format = "png";
        public static List<String> irs = new LinkedList<>();

        public static boolean beingDotted(String ir) {
            return irs.contains(ir);
        }
    }

    // utils
    public static class Util {
        public static boolean dumpId = false;
    }


    // the lexer
    public static class Lexer {
        public static boolean shouldDumpToken = false;
    }

    // the parser
    public static class Parser {
    }

    // the type checker
    public static class Type {
    }

    // the CFG
    public static class Cfg {
    }

    // the x64
    public static class X64 {
        public static boolean embedComment = false;
        public static String assemFile = null;
        public static boolean dump = false;
    }

    // the allocator
    public static class Allocator {
        public enum Kind {
            Linear,
            Stack,
        }

        public static Kind strategy = Kind.Stack;
    }
}

