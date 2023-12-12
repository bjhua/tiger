package control;

import control.Control.ConSlp;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import util.Bug;

public class CommandLine {
    interface F<X> {
        void f(X x);
    }

    static enum Kind {
        Empty,
        Bool,
        Int,
        String,
        StringList,
    }

    static class Arg<X> {
        String name;
        String option;
        String desription;
        Kind kind;
        F<X> action;

        public Arg(String name, String option, String description, Kind kind,
                   F<X> action) {
            this.name = name;
            this.option = option;
            this.desription = description;
            this.kind = kind;
            this.action = action;
        }
    }

    private final List<Arg<Object>> args;

    public CommandLine() {
        this.args = Arrays.asList(
            new Arg<Object>("help", null, "show this help information",
                            Kind.Empty,
                            (s) -> {
            usage();
            System.exit(1);
            return;
                            }),
            new Arg<Object>("lex", null, "dump the result of lexical analysis",
                            Kind.Empty,
                            (s) -> {
            Control.ConLexer.dump = true;
            return;
                            }),
            new Arg<Object>("slp", "{args|interp|compile}",
                            "run the SLP interpreter", Kind.String,
                            (ss) -> {
            String s = (String)ss;

            switch (s) {
                                    case "args" -> ConSlp.action = ConSlp.T.ARGS;
                                    case "interp" -> ConSlp.action = ConSlp.T.INTERP;
                                    case "compile" -> ConSlp.action = ConSlp.T.COMPILE;
                                    case "div" -> ConSlp.div = true;
                                    case "keepasm" -> ConSlp.keepasm = true;
                                    default -> {
                                        System.out.println("bad argument: " + s);
                                        output();
                                        System.exit(1);
                                    }
                                }
                                return;
                            }),
            new Arg<Object>("testlexer", null,
                            "whether or not to test the lexer", Kind.Empty,
                            (s) -> {
                                Control.ConLexer.test = true;
                                return;
                            }));
    }

    // scan the command line arguments, return the file name
    // in it. The file name should be unique.
    public String scan(String[] cargs) {
        String filename = null;

        for (int i = 0; i < cargs.length; i++) {
            if (!cargs[i].startsWith("-")) {
                if (filename == null) {
                    filename = cargs[i];
                    continue;
                }
                System.out.println(
                    "Error: can only compile one Java file a time");
                System.exit(1);
            }

            boolean found = false;
            for (Arg<Object> arg : this.args) {
                                            if (!arg.name.equals(
                                                    cargs[i].substring(1)))
                                                continue;

                                            found = true;
                                            if (Objects.requireNonNull(
                                                    arg.kind) == Kind.Empty) {
                                                arg.action.f(null);
                                            } else {
                                                if (i >= cargs.length - 1) {
                                                    System.out.println(
                                                        "Error: " + cargs[i] +
                                                        ": requires an argument");
                                                    this.output();
                                                    System.exit(1);
                                                }
                                                i++;
                                            }

                                            String theArg = cargs[i];
                                            switch (arg.kind) {
                                            case Bool:
                                                if (theArg.equals("true"))
                                                    arg.action.f((true));
                                                else if (theArg.equals("false"))
                                                    arg.action.f((false));
                                                else {
                                                    System.out.println(
                                                        "Error: " + arg.name +
                                                        ": requires a boolean");
                                                    this.output();
                                                    System.exit(1);
                                                }
                                                break;
                                            case Int:
                                                int num = 0;
                                                try {
                                                    num = Integer.parseInt(
                                                        theArg);
                                                } catch (
                                                    java.lang
                                                        .NumberFormatException
                                                            e) {
                                                    System.out.println(
                                                        "Error: " + arg.name +
                                                        ": requires an integer");
                                                    this.output();
                                                    System.exit(1);
                                                }
                                                arg.action.f(num);
                                                break;
                                            case String:
                                                arg.action.f(theArg);
                                                break;
                                            case StringList:
                                                String[] strArray =
                                                    theArg.split(",");
                                                arg.action.f(strArray);
                                                break;
                                            default:
                                                break;
                                            }
                                            break;
                                        }
                                        if (!found) {
                                            System.out.println(
                                                "invalid option: " + cargs[i]);
                                            this.output();
                                            System.exit(1);
                                        }
                                    }
                                    return filename;
    }

    private void outputSpace(int n) {
            if (n < 0)
                new Bug();

            while (n-- != 0)
                System.out.print(" ");
            return;
    }

    public void output() {
            int max = 0;
            for (Arg<Object> a : this.args) {
                int current = a.name.length();
                if (a.option != null)
                    current += a.option.length();
                if (current > max)
                    max = current;
            }
            System.out.println("Available options:");
            for (Arg<Object> a : this.args) {
                int current = a.name.length();
                System.out.print("   -" + a.name + " ");
                if (a.option != null) {
                    current += a.option.length();
                    System.out.print(a.option);
                }
                outputSpace(max - current + 1);
                System.out.println(a.desription);
            }
            return;
    }

    public void usage() {
        System.out.println(
                """
                        The Tiger compiler. Copyright (C) 2013-, SSE of USTC.
                        Usage: java Tiger [options] <filename>
                        """);
        output();
        //        return;
    }
    }
