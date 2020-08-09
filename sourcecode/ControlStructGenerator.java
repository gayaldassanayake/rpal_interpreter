import java.util.ArrayList;

/**
 * Generates Control Structure using ST
 */
public class ControlStructGenerator {
  private ArrayList<ArrayList<ControlElement>> controlStructures;
  private int cscount;

  ControlStructGenerator() {
    controlStructures = new ArrayList<ArrayList<ControlElement>>();
    cscount = 0;
  }

  /**
   * prints the CS
   */
  public void printCS() {
    for (int i = 0; i < controlStructures.size(); i++) {
      System.out.print(i + ". ");
      for (int j = 0; j < controlStructures.get(i).size(); j++) {
        System.out.print(controlStructures.get(i).get(j).getValue());
        System.out.print(" ");
      }
      System.out.println("");
    }
  }

  /**
   * preorder traverse and create the CS
   * 
   * @param node
   * @param currentcs
   */
  public void preOrderTraverse(Node node, int currentcs) {
    if (node.getValue().equals("lambda")) {
      Node boundNode = node.getChild(0);
      String boundVar = boundNode.getValue();
      node.getChildren().remove(0);

      int env = ++cscount;
      ControlElement cs;

      // tuples
      if (boundVar.equals(",")) {
        ArrayList<String> boundVars = new ArrayList<>();
        for (int i = 0; i < boundNode.getChildren().size(); i++) {
          boundVars.add(boundNode.getChild(i).getValue());
        }
        cs = new ControlElement(node.getValue(), boundVars, env, node.getType());
      } // normal
      else {
        cs = new ControlElement(node.getValue(), boundVar, env, node.getType());
      }

      controlStructures.get(currentcs).add(cs);
      currentcs = cscount;
      controlStructures.add(new ArrayList<ControlElement>());

      ArrayList<Node> children = node.getChildren();

      // recursive traverse
      for (int i = 0; i < children.size(); i++) {
        preOrderTraverse(node.getChild(i), currentcs);
      }

    } // condtion
    else if (node.getValue().equals("->")) {
      Node condition = node.getChild(0);
      Node trueBranch = node.getChild(1);
      Node falseBranch = node.getChild(2);

      cscount += 2;
      int tempcscount = cscount;

      ControlElement cs0 = new ControlElement("beta", NodeType.NONTERMINAL, tempcscount - 1);
      ControlElement cs1 = new ControlElement("delta", NodeType.IF);
      ControlElement cs2 = new ControlElement("delta", NodeType.ELSE);

      controlStructures.get(currentcs).add(cs1);
      controlStructures.get(currentcs).add(cs2);
      controlStructures.get(currentcs).add(cs0);

      controlStructures.add(new ArrayList<ControlElement>());
      controlStructures.add(new ArrayList<ControlElement>());

      preOrderTraverse(condition, currentcs);
      preOrderTraverse(trueBranch, tempcscount - 1);
      preOrderTraverse(falseBranch, tempcscount);

    } else {
      ControlElement cs;
      if (node.getValue().equals("tau")) {
        cs = new ControlElement("tau", node.getType(), node.getChildren().size());
      } else {
        cs = new ControlElement(node.getValue(), node.getType());
      }
      controlStructures.get(currentcs).add(cs);

      ArrayList<Node> children = node.getChildren();

      for (int i = 0; i < children.size(); i++) {
        preOrderTraverse(node.getChild(i), currentcs);
      }
    }

  }

  /**
   * Initializes preorder traversal
   * 
   * @param st
   * @return CS
   */
  public ArrayList<ArrayList<ControlElement>> preOrderTraverseHelper(SyntaxTree st) {
    Node root = st.getRoot();
    controlStructures.add(new ArrayList<ControlElement>());
    preOrderTraverse(root, 0);

    return controlStructures;
  }
}