package elaborator;

import ast.Ast.Dec;
import ast.Ast.Type;
import java.util.List;

public class MethodType {
    public Type.T retType;
    public List<Dec.T> argsType;

    public MethodType(Type.T retType, List<Dec.T> decs) {
        this.retType = retType;
        this.argsType = decs;
    }

    @Override
    public String toString() {
        String s = "";
        for (Dec.T dec : this.argsType) {
            Dec.DecSingle decc = (Dec.DecSingle)dec;
            s = decc.type().toString() + "*" + s;
        }
        s = s + " -> " + this.retType.toString();
        return s;
    }
}
