package util;

public class Triple<X, Y, Z>
{
  public X x;
  public Y y;
  public Z z;

  public Triple(X x, Y y, Z z)
  {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  @Override
  public boolean equals(Object arg0)
  {
    if (arg0 == null) {
      return false;
    }
    if (!(arg0 instanceof Triple)) {
      return false;
    }

    Triple<X, Y, Z> e = (Triple<X, Y, Z>) arg0;
    return (this.x.equals(e.x) && this.y.equals(e.y) && this.z.equals(e.z));
  }

  public int hashCode()
  {
    return (x.hashCode() + y.hashCode() + z.hashCode());
  }

  public String toString()
  {
    return ("(" + this.x.toString() + ", " + this.y.toString() + ", "
        + this.z.toString() + ")");
  }
}
