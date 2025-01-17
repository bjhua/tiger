package util;

import control.Control;

import java.util.function.Function;

// a compiler pass is a type from "FromType" to "ToType"
public class Pass<FromType, ToType> {
    // name of this pass
    private final String passName;
    // the translator
    private final Function<FromType, ToType> transformation;
    // the source data structure
    private final FromType from;
    // at which level this pass should be seen
    private final Control.Verbose verbose;
    // when does this pass start
    private long startTime;
    // when does this pass end
    private long endTime;


    // pretty printing
    private static int indentSize = 0;
    private final int steps = 3;

    private void printSpaces() {
        int n = indentSize;
        if (n < 0) {
            throw new util.Error("compiler bug");
        }
        while (n-- != 0) {
            System.out.print(" ");
        }
    }

    private void indent() {
        indentSize += steps;
    }

    private void unindent() {
        indentSize -= steps;
    }

    public Pass(String passName,
                Function<FromType, ToType> transformation,
                FromType from,
                Control.Verbose verbose) {
        this.passName = passName;
        this.transformation = transformation;
        this.from = from;
        this.verbose = verbose;
        this.startTime = 0;
        this.endTime = 0;

    }

    public ToType apply() {
        if (Control.verbose.order >= this.verbose.order) {
            printSpaces();
            indent();
            System.out.println(this.passName + " starting");
            if (Control.verbose.order >= Control.Verbose.L1.order) {
                this.startTime = System.nanoTime();
            }
        }

        ToType result = this.transformation.apply(this.from);

        if (Control.verbose.order >= this.verbose.order) {
            unindent();
            printSpaces();
            System.out.print(this.passName + " finished");
            if (Control.verbose.order >= Control.Verbose.L1.order) {
                this.endTime = System.nanoTime();
                System.out.print(": @ " + ((this.endTime - this.startTime) / 1000000.00));
            }
            System.out.println();
        }
        return result;
    }
}

