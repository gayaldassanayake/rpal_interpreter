import java.util.ArrayList;
import java.util.Stack;
import java.lang.Math;

/**
 * Template for elements in the Environment stack
 */
class Environment {
  private String key;
  private ControlElement boundEle;
  private NodeType type;
  private ArrayList<ControlElement> boundEleList = new ArrayList<>();

  Environment(String key, ControlElement boundEle) {
    this.key = key;
    this.boundEle = boundEle;
  }

  Environment(String key) {
    this.key = key;
  }

  public ControlElement getBoundEle() {
    return this.boundEle;
  }

  public void setBoundEle(ControlElement boundEle) {
    this.boundEle = boundEle;
  }

  public NodeType getType() {
    return this.type;
  }

  public void setType(NodeType type) {
    this.type = type;
  }

  public String getKey() {
    return this.key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public ArrayList<ControlElement> getBoundEleList() {
    return this.boundEleList;
  }

  public void setBoundEleList(ArrayList<ControlElement> boundEleList) {
    this.boundEleList = boundEleList;
  }

}

/**
 * Takes control structures as input and outputs the result
 */
public class CSEMachine {
  private ArrayList<ArrayList<ControlElement>> controlStructures;
  private ArrayList<ControlElement> control;
  private Stack<ControlElement> stack = new Stack<>();
  private ArrayList<Environment> env = new ArrayList<>();

  /**
   * nitialize the cse
   * 
   * @param controlStructures
   */
  CSEMachine(ArrayList<ArrayList<ControlElement>> controlStructures) {
    this.controlStructures = controlStructures;
    ControlElement initial = new ControlElement("0", NodeType.ENVIRONMENT);
    this.control = controlStructures.get(0);
    control.add(0, initial);
    this.stack.push(initial);
    Environment initialenv = new Environment("");
    this.env.add(initialenv);
  }

  /**
   * get the current element that is making the move
   * 
   * @param key
   * @return current control element
   */
  public ControlElement getCurrent(String key) {
    for (int i = 0; i < env.size(); i++) {
      if (env.get(i).getKey().equals(key))
        return env.get(i).getBoundEle();
    }
    return null;
  }

  /**
   * get the bounded elments of an element
   * 
   * @param key
   * @return arraylist of control elements
   */
  public ArrayList<ControlElement> getTuple(String key) {
    for (int i = 0; i < env.size(); i++) {
      if (env.get(i).getKey().equals(key))
        return env.get(i).getBoundEleList();
    }
    return null;
  }

  /**
   * Print functionality
   * 
   * @param element
   * @return String
   */
  public String tuplePrinter(ControlElement element) {
    String str = "";
    if (element.getValue().equals("tuple")) {
      str += "(";
      for (int i = 0; i < element.getElements().size(); i++) {
        if (i > 0)
          str += ",";
        str += tuplePrinter(element.getElements().get(i));
      }
      str += ")";
    } else
      str += element.getValue();

    return str;
  }

  /**
   * return true if the given string can be parsed to int
   * 
   * @param integer
   * @return boolean
   */
  public boolean integerValidator(String integer) {
    try {
      Integer.parseInt(integer);
    } catch (Exception err) {
      return false;
    }
    return true;
  }

  /**
   * return true if the given string can be parsed to boolean
   * 
   * @param bool
   * @return boolean
   */
  public boolean booleanValidator(String bool) {
    try {
      Boolean.parseBoolean(bool);
    } catch (Exception err) {
      return false;
    }
    return true;
  }

  /**
   * The driver method of the CSE machine
   */
  public void runCSE() {

    // iterate while control is not empty
    while (control.size() > 0) {
      ControlElement current = control.remove(control.size() - 1);

      NodeType type = current.getType();
      String value = current.getValue();

      // use the bounded variables
      if (current.getType() == NodeType.IDENTIFIER && getCurrent(value) != null) {
        current = getCurrent(value);
        value = current.getValue();
      }

      if (type == NodeType.IDENTIFIER) {
        // Isinteger functionality
        if (value.equals("Isinteger")) {
          ControlElement element = stack.pop();

          boolean valid = integerValidator(element.getValue());
          stack.push(new ControlElement(Boolean.toString(valid), NodeType.BOOLEAN));

        } // is boolean functionality
        else if (value.equals("Istruthvalue")) {
          ControlElement element = stack.pop();

          boolean valid = booleanValidator(element.getValue());
          stack.push(new ControlElement(Boolean.toString(valid), NodeType.BOOLEAN));
        } // Stem of a string
        else if (value.equals("Stem")) {
          ControlElement element = stack.peek();

          element.setValue(element.getValue().substring(0, 1));
        } // Stern of a string
        else if (value.equals("Stern")) {
          ControlElement element = stack.peek();

          element.setValue(element.getValue().substring(1));
        } // Concaternate two strings
        if (value.equals("Conc")) {
          ControlElement element1 = stack.pop();
          ControlElement element2 = stack.pop();

          element1.setValue(element1.getValue() + element2.getValue());
          stack.push(element1);
        } else {
          stack.push(current);
        }

      }

      if (type == NodeType.STRING || type == NodeType.INTEGER) {
        stack.push(current);
      } else if (type == NodeType.NONTERMINAL) {
        try {
          if (value.equals("lambda")) {
            stack.push(current);
          } else if (value.equals("gamma")) {
            // apply (rator,rand)
            if (stack.peek().getValue().equals("lambda") && stack.peek().getType() == NodeType.NONTERMINAL) {
              ControlElement lambda = stack.pop();

              ControlElement ce = new ControlElement(Integer.toString(lambda.getEnv()), NodeType.ENVIRONMENT);

              ControlElement boundEle = stack.pop();

              if (lambda.getBoundVar() != null)
                env.add(new Environment(lambda.getBoundVar(), boundEle));
              else { // more than 1 bound variable.
                ArrayList<String> boundVars = lambda.getBoundVars();
                for (int i = 0; i < boundVars.size(); i++) {
                  env.add(new Environment(boundVars.get(i), boundEle.getElements().get(i)));
                }
              }

              stack.push(ce);
              control.add(ce);
              control.addAll(controlStructures.get(lambda.getEnv()));

            } // recursion
            else if (stack.peek().getValue() == "ystar" && stack.peek().getType() == NodeType.NONTERMINAL) {
              stack.pop();
              ControlElement lambda = stack.pop();
              stack.push(new ControlElement("rec", lambda.getBoundVar(), lambda.getEnv(), NodeType.NONTERMINAL));

            } //recursion f
            else if (stack.peek().getValue() == "rec" && stack.peek().getType() == NodeType.NONTERMINAL) {
              ControlElement rec = stack.peek();
              control.add(new ControlElement("gamma", NodeType.NONTERMINAL));
              control.add(new ControlElement("gamma", NodeType.NONTERMINAL));

              stack.push(new ControlElement("lambda", rec.getBoundVar(), rec.getEnv(), NodeType.NONTERMINAL));

            } // Print
            else if (stack.peek().getValue().equals("Print") && stack.peek().getType() == NodeType.IDENTIFIER) {
              stack.pop();
              ControlElement top = stack.pop();

              System.out.println(tuplePrinter(top));

              stack.push(new ControlElement("dummy", NodeType.NONTERMINAL));
            }
          } // if condition
          else if (value.equals("beta")) {
            if (stack.peek().getType() == NodeType.BOOLEAN) {
              ControlElement result = stack.pop();
              control.remove(control.size() - 1);
              control.remove(control.size() - 1);

              if (result.getValue().equals("true")) {
                control.addAll(controlStructures.get(current.getIfIndex()));
                controlStructures.remove(current.getIfIndex() + 1);
              } else {
                control.addAll(controlStructures.get(current.getIfIndex() + 1));
                controlStructures.remove(current.getIfIndex());
              }
            } else
              throw new Exception("beta should recieve a boolean");

          } // tuples 
          else if (value.equals("tau")) {
            ArrayList<ControlElement> elements = new ArrayList<>();
            for (int i = 0; i < current.getIfIndex(); i++) {
              elements.add(stack.pop());
            }
            stack.push(new ControlElement("tuple", elements, NodeType.STRING));
          } else if (value.equals("aug")) {
            ArrayList<ControlElement> elements = new ArrayList<>();
            ControlElement tuple = stack.pop();
            ControlElement element = stack.pop();
            if (tuple.getValue() == "nil") {
              elements.add(element);
              stack.push(new ControlElement("tuple", elements, NodeType.STRING));
            } else {
              tuple.getElements().add(element);
              stack.push(tuple);
            }

            stack.push(new ControlElement("tuple", elements, NodeType.STRING));

          }  // unary and binary operations
          else if (value.equals("+")) {
            int first = Integer.parseInt(stack.pop().getValue());
            int second = Integer.parseInt(stack.pop().getValue());
            stack.push(new ControlElement(Integer.toString(first + second), NodeType.INTEGER));
          } else if (value.equals("-")) {
            int first = Integer.parseInt(stack.pop().getValue());
            int second = Integer.parseInt(stack.pop().getValue());
            stack.push(new ControlElement(Integer.toString(first - second), NodeType.INTEGER));
          } else if (value.equals("*")) {
            int first = Integer.parseInt(stack.pop().getValue());
            int second = Integer.parseInt(stack.pop().getValue());
            stack.push(new ControlElement(Integer.toString(first * second), NodeType.INTEGER));
          } else if (value.equals("/")) {
            int first = Integer.parseInt(stack.pop().getValue());
            int second = Integer.parseInt(stack.pop().getValue());
            stack.push(new ControlElement(Integer.toString(first / second), NodeType.INTEGER));
          } else if (value.equals("neg")) {
            int first = Integer.parseInt(stack.pop().getValue());
            stack.push(new ControlElement(Integer.toString(-first), NodeType.INTEGER));
          } else if (value.equals("**")) {
            int first = Integer.parseInt(stack.pop().getValue());
            int second = Integer.parseInt(stack.pop().getValue());
            stack.push(new ControlElement(Integer.toString((int) (Math.pow((double) (first), (double) second))),
                NodeType.INTEGER));
          } else if (value.equals("or")) {
            ControlElement firstEle = stack.pop();
            ControlElement secondEle = stack.pop();
            if (firstEle.getType() != NodeType.BOOLEAN || secondEle.getType() != NodeType.BOOLEAN)
              throw new Exception("Need booleans for or");
            Boolean first = firstEle.getValue() == "true" ? true : false;
            Boolean second = secondEle.getValue() == "true" ? true : false;

            stack.push(new ControlElement(Boolean.toString(first || second), NodeType.BOOLEAN));

          } else if (value.equals("&")) {
            ControlElement firstEle = stack.pop();
            ControlElement secondEle = stack.pop();
            if (firstEle.getType() != NodeType.BOOLEAN || secondEle.getType() != NodeType.BOOLEAN)
              throw new Exception("Need booleans for &");
            Boolean first = firstEle.getValue() == "true" ? true : false;
            Boolean second = secondEle.getValue() == "true" ? true : false;

            stack.push(new ControlElement(Boolean.toString(first && second), NodeType.BOOLEAN));

          } else if (value.equals("not")) {
            ControlElement firstEle = stack.pop();
            if (firstEle.getType() != NodeType.BOOLEAN)
              throw new Exception("Need booleans for not");
            Boolean first = firstEle.getValue() == "true" ? true : false;

            stack.push(new ControlElement(Boolean.toString(!first), NodeType.BOOLEAN));
          } else if (value.equals("gr")) {
            int first = Integer.parseInt(stack.pop().getValue());
            int second = Integer.parseInt(stack.pop().getValue());
            stack.push(new ControlElement(Boolean.toString(first > second), NodeType.BOOLEAN));
          } else if (value.equals("ge")) {
            int first = Integer.parseInt(stack.pop().getValue());
            int second = Integer.parseInt(stack.pop().getValue());
            stack.push(new ControlElement(Boolean.toString(first >= second), NodeType.BOOLEAN));
          } else if (value.equals("ls")) {
            int first = Integer.parseInt(stack.pop().getValue());
            int second = Integer.parseInt(stack.pop().getValue());
            stack.push(new ControlElement(Boolean.toString(first < second), NodeType.BOOLEAN));
          } else if (value.equals("le")) {
            int first = Integer.parseInt(stack.pop().getValue());
            int second = Integer.parseInt(stack.pop().getValue());
            stack.push(new ControlElement(Boolean.toString(first <= second), NodeType.BOOLEAN));
          } else if (value.equals("eq")) {
            int first = Integer.parseInt(stack.pop().getValue());
            int second = Integer.parseInt(stack.pop().getValue());
            stack.push(new ControlElement(Boolean.toString(first == second), NodeType.BOOLEAN));
          } else if (value.equals("ne")) {
            int first = Integer.parseInt(stack.pop().getValue());
            int second = Integer.parseInt(stack.pop().getValue());
            stack.push(new ControlElement(Boolean.toString(first != second), NodeType.BOOLEAN));
          } else {
            stack.push(current);
          }

        } catch (Exception e) {
          e.printStackTrace();
        }
      } else if (type == NodeType.ENVIRONMENT) {
        ControlElement top = stack.pop();
        try {
          ControlElement env = stack.pop();
          if (!env.getValue().equals(value)) {
            throw new Exception("Inconsistant envs");
          }
          stack.push(top);
        } catch (Exception e) {
        }
      }
    }

    // uncomment to view the final stack
    
    // if (stack.isEmpty())
    // System.out.println("empty stack!");
    // else
    // System.out.println(stack.peek().getValue());
  }

}
