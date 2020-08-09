import java.util.ArrayList;

/**
 * Processes AST and converts into a ST
 */
public class STGenerator {

/**
 * Traverses the AST preorder and standardizes
 * @param node
 * @param ast
 */
  void traverse(Node node, SyntaxTree ast) {
      ArrayList<Node> children = node.getChildren();
      for (int i = 0; i < children.size(); i++) {
          traverse(children.get(i), ast);
      }
      Standardizer.standardize(node, ast);
  }

  /**
   * Initializes the traverse
   * @param ast
   */
  void createST(SyntaxTree ast) {
      Node root = ast.getRoot();
      traverse(root, ast);
  }
}