package control;

public class CompilerPass
{
  private String name;
  private long startTime;
  private long endTime;
  private Object obj;
  private Object x;
  private static int indent = 0;
  private final int nest = 3;

  private void printSpaces()
  {
    int n = indent;
    if (n < 0) {
      System.out.println("compiler bug");
      System.exit(1);
    }
    while (n-- != 0) {
      System.out.print(" ");
    }
    return;
  }

  public CompilerPass(String name, Object obj, Object x)
  {
    this.name = name;
    this.startTime = 0;
    this.endTime = 0;
    this.obj = obj;
    this.x = x;
  }

  public void doit()
  {
    if (Control.verbose != Control.Verbose_t.Silent) {
      printSpaces();
      indent += nest;
      System.out.println(this.name + " starting");
      if (Control.verbose == Control.Verbose_t.Detailed) {
        this.startTime = System.nanoTime();
      }
    }

    // a dirty hack, it's NOT type safe!
    int i;
    try {
      java.lang.reflect.Method[] methods = this.obj.getClass().getMethods();
      for (i = 0; i < methods.length; i++) {
        if (methods[i].getName().equals("accept"))
          break;
      }
      methods[i].invoke(this.obj, this.x);
    } catch (Throwable o) {
      System.out.println("compiler bug");
      o.printStackTrace();
      System.exit(1);
    }

    if (Control.verbose != Control.Verbose_t.Silent){
      indent -= nest;
      printSpaces();
      System.out.print(this.name + " finished");
      if (Control.verbose == Control.Verbose_t.Detailed) {
        this.endTime = System.nanoTime();
        System.out.print(": @ " + (this.endTime - this.startTime) / 1000
        + "ms");
      }
      System.out.println("");
    }
    return;
  }
  
  public void doitName(String name)
  {
    if (Control.verbose != Control.Verbose_t.Silent) {
      printSpaces();
      indent += nest;
      System.out.println(this.name + " starting");
      if (Control.verbose == Control.Verbose_t.Detailed) {
        this.startTime = System.nanoTime();
      }
    }

    // a dirty hack, it's NOT type safe!
    int i;
    try {
      java.lang.reflect.Method[] methods = this.obj.getClass().getMethods();
      for (i = 0; i < methods.length; i++) {
        if (methods[i].getName().equals(name))
          break;
      }
      methods[i].invoke(this.obj, this.x);
    } catch (Throwable o) {
      System.out.println("compiler bug");
      o.printStackTrace();
      System.exit(1);
    }

    if (Control.verbose != Control.Verbose_t.Silent){
      indent -= nest;
      printSpaces();
      System.out.print(this.name + " finished");
      if (Control.verbose == Control.Verbose_t.Detailed) {
        this.endTime = System.nanoTime();
        System.out.print(": @ " + (this.endTime - this.startTime) / 1000
        + "ms");
      }
      System.out.println("");
    }
    return;
  }
}
