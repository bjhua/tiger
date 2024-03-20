package checker;

import ast.Ast.Dec;
import ast.Ast.Type;

import java.util.List;

// record the argument and return types of a method.
public record MethodType(Type.T retType,
                         List<Dec.T> argsType) {
    @Override
    public String toString() {
        String s = "";
        for (Dec.T dec : this.argsType) {
            Dec.Singleton decc = (Dec.Singleton) dec;
            s = decc.type().toString() + "*" + s;
        }
        s = s + " -> " + this.retType.toString();
        return s;
    }
}
