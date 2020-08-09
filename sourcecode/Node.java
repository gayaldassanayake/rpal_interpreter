import java.util.ArrayList;

/**
 * Template class for Nodes
 */
public class Node {
  private String value;
  private Node parent;
  private ArrayList<Node> children;
  private int depth;
  private NodeType type;

  Node(String value, Node parent, int depth, NodeType type) {
      this.value = value;
      this.parent = parent;
      children = new ArrayList<Node>();
      this.depth = depth;
      this.type = type;
  }

  String getValue() {
      return this.value;
  }

  int getDepth() {
      return this.depth;
  }

  void setChild(Node child) {
      children.add(child);
  }

  void setChild(int index, Node child) {
      children.add(index, child);
  }

  Node getChild(int index) {
      return children.get(index);
  }

  ArrayList<Node> getChildren() {
      return this.children;
  }

  Node getParent() {
      return this.parent;
  }

  void setParent(Node parent) {
      this.parent = parent;
  }

  NodeType getType() {
      return this.type;
  }

}