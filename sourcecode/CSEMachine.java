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
  private int envcount;
  private ArrayList<ArrayList<ControlElement>> controlStructures;
  private ArrayList<ControlElement> control;
  private Stack<ControlElement> stack = new Stack<>();
  private ArrayList<Environment> env = new ArrayList<>();

  /**
   * initialize the cse
   * 
   * @param controlStructures controlStructures
   */
  CSEMachine(ArrayList<ArrayList<ControlElement>> controlStructures) {
    this.controlStructures = controlStructures;
    ControlElement initial = new ControlElement("0", NodeType.ENVIRONMENT);
    this.control = controlStructures.get(0);
    control.add(0, initial);
    this.stack.push(initial);
    Environment initialenv = new Environment("");
    this.env.add(initialenv);

    envcount = 1;
  }

  /**
   * get the current element that is making the move
   * 
   * @param key
   * @return current control element
   */
  public ControlElement getCurrent(String key) {
    for(int i= env.size()-1; i>=0; i--){
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
    for (Environment environment : env) {
      if (environment.getKey().equals(key))
        return environment.getBoundEleList();
    }
    return null;
  }

  /**
   * Print functionality
   * 
   * @param element control element to be printed
   * @return String
   */
  public String tuplePrinter(ControlElement element) {
    StringBuilder str = new StringBuilder();
    if (element.getValue().equals("tuple")) {
      str.append("(");
      for (int i = 0; i < element.getElements().size(); i++) {
        if (i > 0)
          str.append(",");
        str.append(tuplePrinter(element.getElements().get(i)));
      }
      str.append(")");
    } else
      str.append(element.getValue());

    return str.toString();
  }

  /**
   * return true if the given string can be parsed to int
   * 
   * @param integer value to be validated as an integer
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
   * @param bool boolean to be validated
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
        switch (value) {
          case "Isinteger": {
            ControlElement element = stack.pop();

            boolean valid = integerValidator(element.getValue(env));
            stack.push(new ControlElement(Boolean.toString(valid), NodeType.BOOLEAN));

          }
          case "Istruthvalue":{
            ControlElement element = stack.pop();

            boolean valid = booleanValidator(element.getValue(env));
            stack.push(new ControlElement(Boolean.toString(valid), NodeType.BOOLEAN));
          }
          case "Stem": {
            ControlElement element = stack.peek();

            element.setValue(element.getValue(env).substring(0, 1));
          }
          case "Stern": {
            ControlElement element = stack.peek();

            element.setValue(element.getValue(env).substring(1));
          }
        }
        if (value.equals("Conc")) {
          ControlElement element1 = stack.pop();
          ControlElement element2 = stack.pop();

          element1.setValue(element1.getValue(env) + element2.getValue());
          stack.push(element1);
        } else {
          stack.push(current);
        }

      }

      if (type == NodeType.STRING || type == NodeType.INTEGER) {
        stack.push(current);
      } else if (type == NodeType.NONTERMINAL) {
        try {
          switch (value) {
            case "lambda":
              stack.push(current);
              break;
            case "gamma":
              // apply (rator,rand)
              if (stack.peek().getValue().equals("lambda") && stack.peek().getType() == NodeType.NONTERMINAL) {
                ControlElement lambda = stack.pop();

                ControlElement ce = new ControlElement(Integer.toString(envcount), NodeType.ENVIRONMENT);
                envcount++;

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

              } // recursion y star
              else if (stack.peek().getValue().equals("ystar") && stack.peek().getType() == NodeType.NONTERMINAL) {
                stack.pop();
                ControlElement lambda = stack.pop();
                stack.push(new ControlElement("rec", lambda.getBoundVar(), lambda.getEnv(), NodeType.NONTERMINAL));

              } //recursion f
              else if (stack.peek().getValue().equals("rec") && stack.peek().getType() == NodeType.NONTERMINAL) {
                ControlElement rec = stack.peek();
                control.add(new ControlElement("gamma", NodeType.NONTERMINAL));
                control.add(new ControlElement("gamma", NodeType.NONTERMINAL));

                stack.push(new ControlElement("lambda", rec.getBoundVar(), rec.getEnv(), NodeType.NONTERMINAL));

              } // tuple
              else if (stack.peek().getValue().equals("tuple") && stack.peek().getType() == NodeType.STRING) {
                ControlElement tuple = stack.pop();
                int index = Integer.parseInt(stack.pop().getValue(env));
                ControlElement element = tuple.getElements().get(index);

                stack.push(element);

              } // Print
              else if (stack.peek().getValue().equals("Print") && stack.peek().getType() == NodeType.IDENTIFIER) {
                stack.pop();
                ControlElement top = stack.pop();

                System.out.println(tuplePrinter(top));

                stack.push(new ControlElement("dummy", NodeType.NONTERMINAL));
              }
              break;
            case "beta":
              if (stack.peek().getType() == NodeType.BOOLEAN) {
                ControlElement result = stack.pop();
                control.remove(control.size() - 1);
                control.remove(control.size() - 1);

                if (result.getValue().equals("true")) {
                  control.addAll(controlStructures.get(current.getIfIndex()));
//                  controlStructures.remove(current.getIfIndex()  + 1);
//                  iterations++;
                } else {
                  int temp = current.getIfIndex();
                  control.addAll(controlStructures.get(current.getIfIndex() + 1));
//                  controlStructures.remove(current.getIfIndex());
                }
              } else {
                throw new Exception("beta should recieve a boolean");
              }
              break;
            case "tau": {
              ArrayList<ControlElement> elements = new ArrayList<>();
              for (int i = 0; i < current.getIfIndex(); i++) {
                elements.add(stack.pop());
              }
              stack.push(new ControlElement("tuple", elements, NodeType.STRING));
              break;
            }
            case "aug": {
              ArrayList<ControlElement> elements = new ArrayList<>();
              ControlElement tuple = stack.pop();
              ControlElement element = stack.pop();
              if (tuple.getValue().equals("nil")) {
                elements.add(element);
                stack.push(new ControlElement("tuple", elements, NodeType.STRING));
              } else {
                tuple.getElements().add(element);
                stack.push(tuple);
              }

              stack.push(new ControlElement("tuple", elements, NodeType.STRING));

              break;
            }
            case "+": {
              int first = Integer.parseInt(stack.pop().getValue(env));
              int second = Integer.parseInt(stack.pop().getValue(env));
              stack.push(new ControlElement(Integer.toString(first + second), NodeType.INTEGER));
              break;
            }
            case "-": {
              int first = Integer.parseInt(stack.pop().getValue(env));
              int second = Integer.parseInt(stack.pop().getValue(env));
              stack.push(new ControlElement(Integer.toString(first - second), NodeType.INTEGER));
              break;
            }
            case "*": {
              int first = Integer.parseInt(stack.pop().getValue(env));
              int second = Integer.parseInt(stack.pop().getValue(env));
              stack.push(new ControlElement(Integer.toString(first * second), NodeType.INTEGER));
              break;
            }
            case "/": {
              int first = Integer.parseInt(stack.pop().getValue(env));
              int second = Integer.parseInt(stack.pop().getValue());
              stack.push(new ControlElement(Integer.toString(first / second), NodeType.INTEGER));
              break;
            }
            case "neg": {
              int first = Integer.parseInt(stack.pop().getValue(env));
              stack.push(new ControlElement(Integer.toString(-first), NodeType.INTEGER));
              break;
            }
            case "**": {
              int first = Integer.parseInt(stack.pop().getValue(env));
              int second = Integer.parseInt(stack.pop().getValue(env));
              stack.push(new ControlElement(Integer.toString((int) (Math.pow((double) (first), (double) second))),
                      NodeType.INTEGER));
              break;
            }
            case "or": {
              ControlElement firstEle = stack.pop();
              ControlElement secondEle = stack.pop();
              if (firstEle.getType() != NodeType.BOOLEAN || secondEle.getType() != NodeType.BOOLEAN)
                throw new Exception("Need booleans for or");
              Boolean first = firstEle.getValue(env).equals("true");
              Boolean second = secondEle.getValue(env).equals("true");

              stack.push(new ControlElement(Boolean.toString(first || second), NodeType.BOOLEAN));

              break;
            }
            case "&": {
              ControlElement firstEle = stack.pop();
              ControlElement secondEle = stack.pop();
              if (firstEle.getType() != NodeType.BOOLEAN || secondEle.getType() != NodeType.BOOLEAN)
                throw new Exception("Need booleans for &");
              Boolean first = firstEle.getValue(env).equals("true");
              Boolean second = secondEle.getValue(env).equals("true");

              stack.push(new ControlElement(Boolean.toString(first && second), NodeType.BOOLEAN));

              break;
            }
            case "not": {
              ControlElement firstEle = stack.pop();
//              if (firstEle.getType() != NodeType.BOOLEAN)
//                throw new Exception("Need booleans for not");
              boolean first = firstEle.getValue(env).equals("true");

              stack.push(new ControlElement(Boolean.toString(!first), NodeType.BOOLEAN));
              break;
            }
            case "gr": {
              int first = Integer.parseInt(stack.pop().getValue(env));
              int second = Integer.parseInt(stack.pop().getValue(env));
              stack.push(new ControlElement(Boolean.toString(first > second), NodeType.BOOLEAN));
              break;
            }
            case "ge": {
              int first = Integer.parseInt(stack.pop().getValue(env));
              int second = Integer.parseInt(stack.pop().getValue(env));
              stack.push(new ControlElement(Boolean.toString(first >= second), NodeType.BOOLEAN));
              break;
            }
            case "ls": {
              int first = Integer.parseInt(stack.pop().getValue(env));
              int second = Integer.parseInt(stack.pop().getValue(env));
              stack.push(new ControlElement(Boolean.toString(first < second), NodeType.BOOLEAN));
              break;
            }
            case "le": {
              int first = Integer.parseInt(stack.pop().getValue(env));
              int second = Integer.parseInt(stack.pop().getValue(env));
              stack.push(new ControlElement(Boolean.toString(first <= second), NodeType.BOOLEAN));
              break;
            }
            case "eq": {
              String temp = stack.pop().getValue(env);
              int first = Integer.parseInt(temp);
              int second = Integer.parseInt(stack.pop().getValue(env));
              stack.push(new ControlElement(Boolean.toString(first == second), NodeType.BOOLEAN));
              break;
            }
            case "ne": {
              int first = Integer.parseInt(stack.pop().getValue(env));
              int second = Integer.parseInt(stack.pop().getValue(env));
              stack.push(new ControlElement(Boolean.toString(first != second), NodeType.BOOLEAN));
              break;
            }
            default:
              stack.push(current);
              break;
          }

        } catch (Exception e) {
          e.printStackTrace();
        }
      } else if (type == NodeType.ENVIRONMENT) {
        ControlElement top = stack.pop();
        try {
          ControlElement env = stack.pop();
          if (!env.getValue().equals(value)) {
            throw new Exception("Inconsistent envs");
          }
          stack.push(top);

          this.env.remove(this.env.size()-1);
        } catch (Exception e) {
        }
      }
    }

    // uncomment to view the final stack
    
//     if (stack.isEmpty())
//     System.out.println("empty stack!");
//     else
//     System.out.println(stack.peek().getValue());
  }

}
