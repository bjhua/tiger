package slp;

import slp.Slp2.Exp;
import slp.Slp2.Stm;

import java.util.HashMap;
import java.util.List;

// an interpreter for the SLP language.
public class Interpreter {
    java.util.function.Consumer<String> consumer;

    // an abstract memory mapping each variable to its corresponding value
    HashMap<String, Integer> memory = new HashMap<>();

    // ///////////////////////////////////////////
    // print expression
    private int interpExp(Exp exp) {
        return switch (exp) {
            case Exp.Num(int n) -> n;
            case Exp.Id(String x) -> memory.get(x);
            case Exp.Op(Exp left, String bop, Exp right) -> {
                int v1 = interpExp(left);
                int v2 = interpExp(right);
                yield switch (bop) {
                    case "+" -> v1 + v2;
                    case "-" -> v1 - v2;
                    case "*" -> v1 * v2;
                    case "/" -> v1 / v2;
                    default -> 0;
                };
            }
            case Exp.Eseq(Stm stm, Exp e) -> {
                interpStm(stm);
                yield interpExp(e);
            }
        };
    }

    // ///////////////////////////////////////////
    // statement
    private void interpStm(Stm stm) {
        switch (stm) {
            case Stm.Compound(Stm s1, Stm s2) -> {
                interpStm(s1);
                interpStm(s2);
            }
            case Stm.Assign(String x, Exp e) -> {
                int v = interpExp(e);
                memory.put(x, v);
            }
            case Stm.Print(List<Exp> exps) -> {
                var values = exps.stream().map(this::interpExp);
                util.StreamConvert.appAllExceptLast(values, v -> {
                            consumer.accept(v.toString());
                        },
                        _ -> {
                            consumer.accept(" ");
                        });
                consumer.accept("\n");
            }
        }
    }

    public void interpretStm(Stm stm, java.util.function.Consumer<String> consumer) {
        this.consumer = consumer;
        interpStm(stm);
    }
}
