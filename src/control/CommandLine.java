package control;

import java.util.List;
import java.util.function.Consumer;

public class CommandLine {
    // alphabetically-ordered
    enum Kind {
        Bool,
        Empty,
        Int,
        String,
        StringList,
    }

    record Arg(
            String name,
            String option,
            String description,
            Kind kind,
            Consumer<Object> action) {
    }

    private final List<Arg> args;

    public void error(String message) {
        System.err.println("Error: " + message);
        usage();
        System.exit(1);
    }

    public CommandLine() {
        this.args = List.of(
                new Arg("dump",
                        "{token}",
                        "dump tokens from lexical analysis",
                        Kind.String,
                        (Object x) -> {
                            switch ((String) x) {
                                case "token" -> Control.Lexer.dumpToken = true;
                                default -> error("unknown argument: " + x);
                            }
                        }),
                new Arg(
                        "help",
                        null,
                        "show this help information",
                        Kind.Empty,
                        (_) -> {
                            usage();
                            System.exit(1);
                        })
        );
    }

    // scan the command line arguments, return the file name
    // in it; return null if there is no file name.
    // The file name should be unique.
    public String scan(String[] cmdLineArgs) {
        String filename = null;

        for (int i = 0; i < cmdLineArgs.length; i++) {
            String cmdArg = cmdLineArgs[i];
            if (!cmdArg.startsWith("-")) {
                if (null != filename) {
                    error("compile only one Java file one time");
                } else {
                    filename = cmdArg;
                    continue;
                }
            }

            // to crawl through arguments:
            cmdArg = cmdArg.substring(1);
            boolean foundArg = false;
            for (Arg arg : this.args) {
                if (!arg.name.equals(cmdArg))
                    continue;

                foundArg = true;
                String param = "";
                switch (arg.kind) {
                    case Kind.Empty -> arg.action.accept(null);
                    default -> {
                        i++;
                        if (i >= cmdLineArgs.length)
                            error("wants more arguments");
                        else {
                            param = cmdLineArgs[i];
                        }
                    }
                }
                switch (arg.kind) {
                    case Kind.Empty -> arg.action.accept(null);
                    case Kind.Bool -> {
                        switch (param) {
                            case "true" -> arg.action.accept(true);
                            case "false" -> arg.action.accept(false);
                            default -> error(arg.name + " requires a boolean");
                        }
                    }
                    case Int -> {
                        int num = 0;
                        try {
                            num = Integer.parseInt(param);
                        } catch (java.lang.NumberFormatException e) {
                            error(arg.name + " requires an integer");
                        }
                        arg.action.accept(num);
                    }
                    case String -> arg.action.accept(param);
                    case StringList -> {
                        String[] strArray = param.split(",");
                        arg.action.accept(strArray);
                    }
                    default -> error("bad argument");
                }
            }
            if (!foundArg) {
                error("invalid option: " + cmdLineArgs[i]);
            }
        }
        return filename;
    }

    private void outputSpace(int n) {
        if (n < 0)
            throw new util.Error();

        while (n-- != 0)
            System.out.print(" ");
    }

    public void output() {
        int max = 0;
        for (Arg a : this.args) {
            int current = a.name.length();
            if (a.option != null)
                current += a.option.length();
            if (current > max)
                max = current;
        }
        System.out.println("Available options:");
        for (Arg a : this.args) {
            int current = a.name.length();
            System.out.print("   -" + a.name);
            if (a.option != null) {
                current += a.option.length();
                System.out.print(a.option);
            }
            try {
                outputSpace(max - current + 1);
            } catch (Exception e) {
                throw new util.Error(e);
            }
            System.out.println(a.description);
        }
    }

    public void usage() {
        final int startYear = 2013;
        System.out.println(
                "The Tiger compiler. Copyright (C) " + startYear + "-, SSE of USTC.\n" +
                        "Usage: java Tiger [options] <filename>\n");
        output();
    }
}

