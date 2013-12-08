package util;

import java.util.LinkedList;

public class Graph<X>
{

  // graph node
  public class Node
  {
    X data;
    public LinkedList<Edge> edges;

    public Node()
    {
      this.data = null;
      this.edges = null;
    }

    public Node(X data)
    {
      this.data = data;
      this.edges = new LinkedList<Edge>();
      ;
    }

    @Override
    public String toString()
    {
      return data.toString();
    }
  }

  // graph edge
  public class Edge
  {
    Node from;
    Node to;

    public Edge(Node from, Node to)
    {
      this.from = from;
      this.to = to;
    }

    @Override
    public boolean equals(Object o)
    {
      if (o == null)
        return false;
      if (!(o instanceof Graph.Edge))
        return false;

      return (this == o);
    }

    @Override
    public String toString()
    {
      return (this.from.toString() + "->" + this.to.toString());
    }
  }

  // the graph
  LinkedList<Node> graph;
  String gname;

  public Graph(String name)
  {
    this.gname = name;
    this.graph = new LinkedList<Node>();
  }

  private void addNode(Node node)
  {
    this.graph.addLast(node);
  }

  public void addNode(X data)
  {
    for (Node n : this.graph)
      if (n.data.equals(data))
        new util.Error();

    Node node = new Node(data);
    this.addNode(node);
  }

  public Node lookupNode(X data)
  {
    for (Node node : this.graph) {
      if (node.data.equals(data))
        return node;
    }
    return null;
  }

  private void addEdge(Node from, Node to)
  {
    from.edges.addLast(new Edge(from, to));
  }

  public void addEdge(X from, X to)
  {
    Node f = this.lookupNode(from);
    Node t = this.lookupNode(to);

    if (f == null || t == null)
      new util.Error();

    this.addEdge(f, t);
  }

  public void dfsDoit(Node n, java.util.HashSet<Node> visited)
  {
    visited.add(n);
    // System.out.println("now visiting: "+n);

    for (Edge edge : n.edges)
      if (!visited.contains(edge.to))
        dfsDoit(edge.to, visited);
    return;
  }

  public void dfs(X start)
  {
    Node startNode = this.lookupNode(start);
    if (startNode == null)
      new util.Error();

    java.util.HashSet<Node> visited = new java.util.HashSet<Node>();

    this.dfsDoit(startNode, visited);

    // For control-flow graph, we don't need this.
    /*
     * for (Node n : this.graph){ if (!visited.contains(n)) dfsDoit(n, visited);
     * }
     */
    return;
  }

  public void visualize()
  {
    Dot dot = new Dot();
    String fname;

    fname = this.gname;

    for (Node node : this.graph) {
      for (Edge edge : node.edges)
        dot.insert(edge.from.toString(), edge.to.toString());
    }

    try {
      dot.visualize(fname);
    } catch (Exception e) {
      e.printStackTrace();
      new util.Error();
    }
  }
}
