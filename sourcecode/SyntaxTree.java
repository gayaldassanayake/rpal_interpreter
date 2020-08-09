import java.util.ArrayList;

/**
 * Template class for Syntax Trees
 */
public class SyntaxTree {
  private Node root;
  private ArrayList<Node> nodes;

  SyntaxTree(String value) {
      root = new Node(value, null, 0, NodeType.NONTERMINAL);
      nodes = new ArrayList<Node>();
  }

  void addNode(Node node) {
      nodes.add(node);
  }

  Node getRoot() {
      return root;
  }

  void setRoot(Node root) {
      this.root = root;
  }

  /**
   * prints the Tree
   */
  void printTree() {
      String name = this.root.getValue();
      System.out.println(name);

      for (int i = 0; i < root.getChildren().size(); i++) {
          printTree(1, root.getChild(i));
      }
  }

  void printTree(int depth, Node node) {
      String name = "";
      for (int i = 0; i < depth; i++) {
          name += ".";
      }
      name += node.getValue();
      System.out.println(name);

      for (int i = 0; i < node.getChildren().size(); i++) {
          printTree(depth + 1, node.getChild(i));
      }
  }
}