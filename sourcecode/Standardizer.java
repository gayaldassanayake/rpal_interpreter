import java.util.ArrayList;

/**
 * Standardizes the AST Nodes to convert it to an ST
 */
public class Standardizer {

  // 1.let
  /**
   * Standardizes let
   * 
   * @param node
   * @param ast
   * @throws Exception
   */
  static void standardizeLet(Node node, SyntaxTree ast) throws Exception {
    if (!node.getChild(0).getValue().equals("=")) {
      throw new Exception("Invalid AST");
    }
    // create gamma, lambda nodes
    Node gamma = new Node("gamma", node.getParent(), node.getDepth(), NodeType.NONTERMINAL);
    Node lambda = new Node("lambda", gamma, node.getDepth() + 1, NodeType.NONTERMINAL);

    // change parents child to gamma instead of Let
    if (node.getParent() != null) {
      ArrayList<Node> parentsChildren = node.getParent().getChildren();
      parentsChildren.set(parentsChildren.indexOf(node), gamma);
    } else
      ast.setRoot(gamma);
    // get X,E,P nodes
    Node X = node.getChild(0).getChild(0);
    Node E = node.getChild(0).getChild(1);
    Node P = node.getChild(1);

    // set lambdas children
    lambda.setChild(X);
    lambda.setChild(P);

    // set gamma's children
    gamma.setChild(lambda);
    gamma.setChild(E);

    // set X,E,P s new parents gamma, lambda
    X.setParent(lambda);
    E.setParent(gamma);
    P.setParent(lambda);
  }

  // 2.where
  /**
   * standardizes where
   * 
   * @param node
   * @param ast
   * @throws Exception
   */
  static void standardizeWhere(Node node, SyntaxTree ast) throws Exception {
    if (!node.getChild(1).getValue().equals("="))
      throw new Exception("Invalid AST");

    // create gamma, lambda nodes
    Node gamma = new Node("gamma", node.getParent(), node.getDepth(), NodeType.NONTERMINAL);
    Node lambda = new Node("lambda", gamma, node.getDepth() + 1, NodeType.NONTERMINAL);

    // change parents child to gamma instead of where
    if (node.getParent() != null) {
      ArrayList<Node> parentsChildren = node.getParent().getChildren();
      parentsChildren.set(parentsChildren.indexOf(node), gamma);
    } else
      ast.setRoot(gamma);
    // get X,E,P nodes
    Node X = node.getChild(1).getChild(0);
    Node E = node.getChild(1).getChild(1);
    Node P = node.getChild(0);

    // set lambdas children
    lambda.setChild(X);
    lambda.setChild(P);

    // set gamma's children
    gamma.setChild(lambda);
    gamma.setChild(E);

    // set X,E,P s new parents gamma, lambda
    X.setParent(lambda);
    E.setParent(gamma);
    P.setParent(lambda);
  }

  // 3.fcn_form
  /**
   * Standardizes function_form
   * 
   * @param node
   * @param ast
   */
  static void standardizeFcnForm(Node node, SyntaxTree ast) {
    // create equal node
    Node equal = new Node("=", node.getParent(), node.getDepth(), NodeType.NONTERMINAL);

    // change parents child to equal instead of fcn_form
    if (node.getParent() != null) {
      ArrayList<Node> parentsChildren = node.getParent().getChildren();
      parentsChildren.set(parentsChildren.indexOf(node), equal);
    } else
      ast.setRoot(equal);

    // get P
    Node P = node.getChild(0);
    P.setParent(equal);
    equal.setChild(P);

    Node parent = equal;

    // iterate through V+ and restructure with lambdas
    for (int i = 1; i < node.getChildren().size() - 1; i++) {
      Node lambda = new Node("lambda", parent, parent.getDepth() + 1, NodeType.NONTERMINAL);
      parent.setChild(lambda);
      lambda.setChild(node.getChild(i));

      parent = lambda;
    }

    // get E
    Node E = node.getChild(node.getChildren().size() - 1);
    E.setParent(parent);
    parent.setChild(E);
  }

  // 4. tau
  /**
   * Standardizes Tuples - Obsolete
   * 
   * @param node
   * @param ast
   */
  static void standardizeTuple(Node node, SyntaxTree ast) {
    Node parent = node.getParent();
    for (int i = 0; i < node.getChildren().size(); i++) {
      // create gamma1 node and swapping nodes parents child
      Node gamma1 = new Node("gamma", parent, parent.getDepth() + 1, NodeType.NONTERMINAL);
      if (i == 0) {
        // change parents child to equal instead of fcn_form
        if (node.getParent() != null) {
          ArrayList<Node> parentsChildren = parent.getChildren();
          parentsChildren.set(parentsChildren.indexOf(node), gamma1);
        } else
          ast.setRoot(gamma1);
      } else {
        parent.setChild(1, gamma1);
      }
      // create gamma2
      Node gamma2 = new Node("gamma", gamma1, gamma1.getDepth() + 1, NodeType.NONTERMINAL);
      gamma1.setChild(0, gamma2);

      // setting tau element as right node of gamma1
      Node E = node.getChild(i);
      E.setParent(gamma1);
      gamma1.setChild(1, E);

      // set aug as left child of gamma2
      Node aug = new Node("aug", gamma2, gamma2.getDepth() + 1, NodeType.NONTERMINAL);
      gamma2.setChild(0, aug);
      parent = gamma2;
    }

    // set nil as right node of last gamma2
    Node nil = new Node("aug", parent, parent.getDepth() + 1, NodeType.NONTERMINAL);
    parent.setChild(1, nil);
  }

  // 5.multipara
  /**
   * Standardizes multiparameters - Obsolete
   */
  static void standardizeMultiParameter(Node node, SyntaxTree ast) {
    ArrayList<Node> children = node.getChildren();
    Node parent = node.getParent();
    if (children.size() > 2) {
      for (int i = 0; i < children.size() - 1; i++) {
        Node lambda = new Node("lambda", parent, parent.getDepth() + 1, NodeType.NONTERMINAL);
        if (i == 0) {
          // change parents child to new lambda instead of old
          if (node.getParent() != null) {
            ArrayList<Node> parentsChildren = parent.getChildren();
            parentsChildren.set(parentsChildren.indexOf(node), lambda);
          } else
            ast.setRoot(lambda);
        } else {
          parent.setChild(1, lambda);
        }
        // get nodes ith child
        Node X = children.get(i);
        // insert X into new place
        X.setParent(lambda);
        lambda.setChild(0, X);

        parent = lambda;
      }
      // set last element as the right child of the last lambda
      Node last = node.getChild(children.size() - 1);
      parent.setChild(1, last);

    }
  }

  // 6. within
  /**
   * Standardizes within
   * 
   * @param node
   * @param ast
   * @throws Exception
   */
  static void standardizeWithin(Node node, SyntaxTree ast) throws Exception {
    if (!node.getChild(0).getValue().equals("=") && !node.getChild(1).getValue().equals("=")) {
      throw new Exception("Invalid format for within");
    }
    // get already available nodes
    Node X1 = node.getChild(0).getChild(0);
    Node E1 = node.getChild(0).getChild(1);
    Node X2 = node.getChild(1).getChild(0);
    Node E2 = node.getChild(1).getChild(1);

    // create equal,gamma,lambda nodes
    Node equal = new Node("=", node.getParent(), node.getDepth(), NodeType.NONTERMINAL);
    Node gamma = new Node("gamma", equal, equal.getDepth() + 1, NodeType.NONTERMINAL);
    Node lambda = new Node("lambda", gamma, gamma.getDepth() + 1, NodeType.NONTERMINAL);
    //
    // replace equal as nodes parents new child
    if (node.getParent() != null) {
      ArrayList<Node> parentsChildren = node.getParent().getChildren();
      parentsChildren.set(parentsChildren.indexOf(node), equal);
    } else
      ast.setRoot(equal);

    // add child, parent relations
    equal.setChild(0, X2);
    X2.setParent(equal);
    equal.setChild(1, gamma);

    gamma.setChild(0, lambda);
    gamma.setChild(1, E1);
    E1.setParent(gamma);

    lambda.setChild(0, X1);
    X1.setParent(lambda);
    lambda.setChild(1, E2);
    E2.setParent(lambda);

  }

  /**
   * Standardizes unary- Obsolete
   * 
   * @param node
   * @param ast
   * @throws Exception
   */
  // 7. unary
  static void standardizeUnary(Node node, SyntaxTree ast) throws Exception {
    if (node.getChildren().size() > 1) {
      throw new Exception("More than one operand for unary operator");
    }
    Node E = node.getChild(0);
    // create gamma node
    Node parent = node.getParent();
    Node gamma = new Node("gamma", parent, parent.getDepth() + 1, NodeType.NONTERMINAL);
    Node newnode = new Node(node.getValue(), gamma, gamma.getDepth() + 1, NodeType.NONTERMINAL);
    // replace equal as nodes parents new child
    if (node.getParent() != null) {
      ArrayList<Node> parentsChildren = parent.getChildren();
      parentsChildren.set(parentsChildren.indexOf(node), gamma);
    } else
      ast.setRoot(gamma);
    // set parent child relations
    gamma.setChild(0, newnode);
    gamma.setChild(1, E);
    E.setParent(gamma);

  }

  // 8. binary
  /**
   * Standardizes binary- Obsolete
   * @param node
   * @param ast
   * @throws Exception
   */
  static void standardizeBinary(Node node, SyntaxTree ast) throws Exception {
    if (node.getChildren().size() > 2) {
      throw new Exception("More than 2 operands for binary operator");
    }
    Node E1 = node.getChild(0);
    Node E2 = node.getChild(1);

    // create gamma node
    Node parent = node.getParent();
    Node gamma1 = new Node("gamma", parent, parent.getDepth() + 1, NodeType.NONTERMINAL);
    Node gamma2 = new Node("gamma", gamma1, gamma1.getDepth() + 1, NodeType.NONTERMINAL);
    Node newnode = new Node(node.getValue(), gamma2, gamma2.getDepth() + 1, NodeType.NONTERMINAL);
    // replace equal as nodes parents new child
    if (node.getParent() != null) {
      ArrayList<Node> parentsChildren = parent.getChildren();
      parentsChildren.set(parentsChildren.indexOf(node), gamma1);
    } else
      ast.setRoot(gamma1);

    // set parent child relations
    gamma1.setChild(0, gamma2);
    gamma1.setChild(1, E2);
    E2.setParent(gamma1);

    gamma2.setChild(0, newnode);
    gamma2.setChild(1, E1);
    E1.setParent(gamma2);
  }

  // 9. at
  /**
   * Standardizes @
   * @param node
   * @param ast
   */
  static void standardizeAt(Node node, SyntaxTree ast) {
    Node parent = node.getParent();
    Node gamma1 = new Node("gamma", parent, parent.getDepth() + 1, NodeType.NONTERMINAL);
    Node gamma2 = new Node("gamma", gamma1, gamma1.getDepth() + 1, NodeType.NONTERMINAL);

    // E1, N, E2
    Node E1 = node.getChild(0);
    Node N = node.getChild(1);
    Node E2 = node.getChild(2);

    // replace gamma as @s parents new child
    if (node.getParent() != null) {
      ArrayList<Node> parentsChildren = parent.getChildren();
      parentsChildren.set(parentsChildren.indexOf(node), gamma1);
    } else
      ast.setRoot(gamma1);
    // set gamma1s children
    gamma1.setChild(0, gamma2);
    gamma1.setChild(1, E2);
    E2.setParent(gamma1);

    // set gamma2s children
    gamma2.setChild(0, N);
    gamma2.setChild(1, E1);
    N.setParent(gamma2);
    E1.setParent(gamma2);

  }

  // 10.simult
  /**
   * Standardizes simultaneous defns
   * @param node
   * @param ast
   */
  static void standardizeSimultaneous(Node node, SyntaxTree ast) {
    Node parent = node.getParent();
    ArrayList<Node> children = node.getChildren();

    Node equal = new Node("=", parent, parent.getDepth() + 1, NodeType.NONTERMINAL);
    Node comma = new Node("comma", equal, equal.getDepth() + 1, NodeType.NONTERMINAL);
    Node tau = new Node("tau", equal, equal.getDepth() + 1, NodeType.NONTERMINAL);

    if (node.getParent() != null) {
      ArrayList<Node> parentsChildren = parent.getChildren();
      parentsChildren.set(parentsChildren.indexOf(node), equal);
    } else
      ast.setRoot(equal);

    // set equals children
    equal.setChild(0, comma);
    equal.setChild(1, tau);
    for (int i = 0; i < children.size(); i++) {
      Node left = children.get(i).getChild(0);
      Node right = children.get(i).getChild(1);

      // set relations
      comma.setChild(left);
      left.setParent(comma);
      tau.setChild(right);
      right.setParent(tau);
    }
  }

  /**
   * Standardizes conditional
   * @param node
   * @param ast
   */
  // 11. conditional
  static void standardizeConditional(Node node, SyntaxTree ast) {
    Node parent = node.getParent();

    // create new nodes
    Node gamma1 = new Node("gamma", parent, parent.getDepth() + 1, NodeType.NONTERMINAL);
    Node gamma2 = new Node("gamma", gamma1, gamma1.getDepth() + 1, NodeType.NONTERMINAL);
    Node gamma3 = new Node("gamma", gamma2, gamma2.getDepth() + 1, NodeType.NONTERMINAL);
    Node gamma4 = new Node("gamma", gamma3, gamma3.getDepth() + 1, NodeType.NONTERMINAL);
    Node lambda1 = new Node("lambda", gamma2, gamma2.getDepth() + 1, NodeType.NONTERMINAL);
    Node lambda2 = new Node("lambda", gamma3, gamma3.getDepth() + 1, NodeType.NONTERMINAL);
    Node nil = new Node("nil", gamma1, gamma1.getDepth() + 1, NodeType.NONTERMINAL);
    Node cond = new Node("Cond", gamma4, gamma4.getDepth() + 1, NodeType.NONTERMINAL);
    Node bracket1 = new Node("()", lambda1, lambda1.getDepth() + 1, NodeType.NONTERMINAL);
    Node bracket2 = new Node("()", lambda2, lambda2.getDepth() + 1, NodeType.NONTERMINAL);

    // get already available nodes
    Node B = node.getChild(0);
    Node T = node.getChild(1);
    Node E = node.getChild(2);

    // replace gamma as @s parents new child
    ArrayList<Node> parentsChildren = parent.getChildren();
    parentsChildren.set(parentsChildren.indexOf(node), gamma1);

    // set children relations
    gamma1.setChild(0, gamma2);
    gamma1.setChild(1, nil);
    gamma2.setChild(0, gamma3);
    gamma2.setChild(1, lambda1);
    gamma3.setChild(0, gamma4);
    gamma3.setChild(1, lambda2);
    lambda1.setChild(0, bracket1);
    lambda1.setChild(1, E);
    E.setParent(lambda1);
    gamma4.setChild(0, cond);
    gamma4.setChild(1, B);
    B.setParent(gamma4);
    lambda2.setChild(0, bracket2);
    lambda2.setChild(1, T);
    T.setParent(lambda2);
  }

  // 12. rec
  /**
   * Standardizes rec
   * @param node
   * @param ast
   */
  static void standardizeRec(Node node, SyntaxTree ast) {
    Node parent = node.getParent();

    // get available nodes
    Node equal = node.getChild(0);
    Node X = equal.getChild(0);
    Node E = equal.getChild(1);

    // create new nodes
    Node gamma = new Node("gamma", equal, equal.getDepth() + 1, NodeType.NONTERMINAL);
    Node ystar = new Node("ystar", gamma, gamma.getDepth() + 1, NodeType.NONTERMINAL);
    Node lambda = new Node("lambda", gamma, gamma.getDepth() + 1, NodeType.NONTERMINAL);

    if (node.getParent() != null) {
      ArrayList<Node> parentsChildren = parent.getChildren();
      parentsChildren.set(parentsChildren.indexOf(node), equal);
    } else
      ast.setRoot(equal);

    // redefine relations
    equal.setChild(0, X);
    equal.setChild(1, gamma);

    gamma.setChild(0, ystar);
    gamma.setChild(1, lambda);

    lambda.setChild(0, X);
    lambda.setChild(1, E);
    X.setParent(lambda);
    E.setParent(lambda);
  }

  /**
   * Call specialist Standardize method depending on the typ of node
   */
  static void standardize(Node node, SyntaxTree ast) {
    // let
    // where
    // fcn_form
    // within
    // @
    // simultaneous op (and, ..)
    // conditional op
    // rec
    switch (node.getValue()) {
      case "let":
        try {
          standardizeLet(node, ast);
        } catch (Exception e) {
          e.printStackTrace();
        }
        break;
      case "where":
        try {
          standardizeWhere(node, ast);
        } catch (Exception e) {
          e.printStackTrace();
        }
        break;
      case "function_form":
        standardizeFcnForm(node, ast);
        break;
      case "lambda":
        standardizeMultiParameter(node, ast);
        break;
      case "within":
        try {
          standardizeWithin(node, ast);
        } catch (Exception e) {
          e.printStackTrace();
        }
        break;
      case "@":
        standardizeAt(node, ast);
        break;
      case "and":
        standardizeSimultaneous(node, ast);
        break;
      case "rec":
        standardizeRec(node, ast);
        break;
      default:
        break;
    }

  }
}