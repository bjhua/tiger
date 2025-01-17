package checker;

import ast.Ast;
import ast.Ast.Dec;
import ast.Ast.Type;
import util.Id;
import util.Todo;
import util.Tuple;

import java.util.List;

// map each argument and local in a method, to its corresponding type.
// the method table is constructed for each method.
public class MethodTable {
    // map a variable, to its corresponding type and a fresh name.
    private final java.util.HashMap<Id, Tuple.Two<Type.T, Id>> table;

    public MethodTable() {
        this.table = new java.util.HashMap<>();
    }

    // Duplication is not allowed
    public void putFormalLocal(List<Dec.T> formals, List<Dec.T> locals) {
        for (Dec.T dec : formals) {
            Dec.Singleton decc = (Dec.Singleton) dec;
            Ast.AstId aid = decc.aid();
            Id freshId = aid.genFreshId();
            if (this.table.get(aid.id) != null) {
                System.out.println("duplicated parameter: " + aid.id);
                System.exit(1);
            }
            this.table.put(aid.id, new Tuple.Two<>(decc.type(), freshId));
        }

        for (Dec.T dec : locals) {
            Dec.Singleton decc = (Dec.Singleton) dec;
            Ast.AstId aid = decc.aid();
            Id freshId = aid.genFreshId();
            if (this.table.get(aid.id) != null) {
                System.out.println("duplicated variable: " + aid.id);
                System.exit(1);
            }
            this.table.put(aid.id, new Tuple.Two<>(decc.type(), freshId));
        }
    }

    // return null for non-existing keys
    public Tuple.Two<Type.T, Id> get(Id id) {
        return this.table.get(id);
    }

    // lab 2, exercise 7:
    public void dump() {
        throw new Todo();
    }

    @Override
    public String toString() {
        return this.table.toString();
    }
}
