package util;

import java.util.HashMap;

import static control.Control.Util.dumpId;

public class Id {
    private static int gCounter = 0;
    // used to print out a fancy name like: %n
    private final int counter;
    // null, if no source names
    private String origName;
    private static final String prefix = "%x_";
    // map original names to its Id
    private static final HashMap<String, Id> allIds = new HashMap<>();
    // a per-id map storing attributes of an id
    // this implements the famous "plist", see:
    // https://www.cs.cmu.edu/Groups/AI/html/cltl/clm/node108.html
    private final Plist plist;

    // the singleton design pattern
    private Id() {
        this.counter = gCounter++;
        this.origName = null;
        this.plist = new Plist();
    }

    private Id(String srcName) {
        this.counter = gCounter++;
        this.origName = srcName;
        this.plist = new Plist();
        allIds.put(this.origName, this);
    }

    // "id" without original names
    public static Id newNoname() {
        Id id = new Id();
        allIds.put(id.toString(), id);
        return id;
    }

    public static Id newName(String origName) {
        Id id = allIds.get(origName);
        if (id == null) {
            id = new Id(origName);
            allIds.put(origName, id);
        }
        return id;
    }

    // create an id with the same original name, but different counter
    // that is, the id is considered be a fresh one.
    public Id newSameOrigName() {
        if (this.origName == null) {
            throw new Error("no original name");
        }
        Id fresh = newNoname();
        fresh.origName = this.origName;
        return fresh;
    }

    public Plist getPlist() {
        return this.plist;
    }

    public static boolean originalEquals(Id id1, Id id2) {
        if (id1 == null || id2 == null) {
            throw new Error("null ids not allowed");
        }
        return id1.origName.equals(id2.origName);
    }

//    @Override
//    public boolean equals(Object o) {
//        if (o == null)
//            return false;
//        if (!(o instanceof Id))
//            return false;
//        return this.counter == ((Id) o).counter;
//    }
//
//    @Override
//    public int hashCode() {
//        return this.counter;
//    }

    @Override
    public String toString() {
        if (this.origName == null)
            return prefix + this.counter;
        if (dumpId) {
            return this.origName + "(" + prefix + this.counter;
        }
        return this.origName;
    }

}