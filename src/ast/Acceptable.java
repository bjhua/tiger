package ast;

public interface Acceptable {
    void accept(Visitor v);
}
