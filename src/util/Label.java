package util;

public class Label {
    private final int i;
    private static int gCounter = 0;
    private final Plist plist;


    public Label() {
        this.i = gCounter++;
        this.plist = new Plist();
    }

    public Plist getPlist() {
        return plist;
    }
    
    @Override
    public String toString() {
        return "L_" + this.i;
    }
}

