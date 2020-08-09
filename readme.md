# Programming Languages Project

`D. M. G. C. Dassanayake`
`170097P`


## Overview

The project can be broadly segmented into three components namely,

1. AST Generator
2. ST Generator
3. Control Structure Generator
4. CSE Machine

Each component accepts input and passes the processed output to the next component along the pipeline.

## Components

### 1. AST Generator Component

The ASTGenerator reads the given AST from the input and processes it to create an AST.

#### a. ASTGenerator Class

A tree structure is used to store the entire AST.

The following methods are implemented within the class in order to achieve this functionality. 

<table>
  <tr>
    <td>ArrayList<String> readInput()</td>
    <td>Reads the input from the file and returns nodes as a string arraylist</td>
  </tr>
  <tr>
    <td>Void printAST(Node node)</td>
    <td>Prints the AST when the root node is given</td>
  </tr>
  <tr>
    <td>Node createNode(String name, Node parent, int depth)</td>
    <td>Creates a node in the AST</td>
  </tr>
  <tr>
    <td>int findDepth(String name)</td>
    <td>Find the depth from the root to a node given its name</td>
  </tr>
  <tr>
    <td>Node addNode(Node lastNode, String name)</td>
    <td>Creates a node using createNode() and sets its parent</td>
  </tr>
  <tr>
    <td>SyntaxTree createAST()</td>
    <td>Creates and returns the complete syntax tree</td>
  </tr>
</table>


### 2. ST Generator Component

The ST Generator accepts the AST as the input and standardizes all necessary Nodes.

The following types of nodes are standardized.

* let
* where
* function_form 
* Lambda
* within
* and
* rec

	

#### a. STGenerator Class

STGenerator acts as the driver class in the STGenerator Component. It will conduct a preorder traversal and call standardize method of Standardizer class to standardize each node.

The methods in the STGenerator class are,

<table>
  <tr>
    <td>void traverse(Node node, SyntaxTree ast)</td>
    <td>Traverses the ast in a preorder traversal</td>
  </tr>
  <tr>
    <td>void createST(SyntaxTree ast)</td>
    <td>The helper function calling traverse()</td>
  </tr>
</table>


#### b. Standardizer class

The Standardizer class identifies the type of each node and uses specific methods to standardize all required nodes.

The methods in Standardizer are,

<table>
  <tr>
    <td>static void standardize (Node node, SyntaxTree ast)</td>
    <td>Identifies the type of node and calls specific standardizer methods</td>
  </tr>
  <tr>
    <td>static void standardizeLet (Node node, SyntaxTree ast)</td>
    <td>Standardizes ‘let’ and alters the Syntax Tree</td>
  </tr>
  <tr>
    <td>static void standardizeWhere (Node node, SyntaxTree ast)</td>
    <td>Standardizes ‘where’ and alters the Syntax Tree</td>
  </tr>
  <tr>
    <td>static void standardizeFcnForm (Node node, SyntaxTree ast)</td>
    <td>Standardizes ‘function_form’ and alters the Syntax Tree</td>
  </tr>
  <tr>
    <td>static void standardizeTuple (Node node, SyntaxTree ast)</td>
    <td>Standardizes ‘tau’ and alters the Syntax Tree( obsolete due to CSE rules 9,10)</td>
  </tr>
  <tr>
    <td>static void standardizeMultiParameter (Node node, SyntaxTree ast)</td>
    <td>Standardizes ‘,’ and alters the Syntax Tree( obsolete due to CSE rule 11)</td>
  </tr>
  <tr>
    <td>static void standardizeWithin (Node node, SyntaxTree ast)</td>
    <td>Standardizes ‘within’ and alters the Syntax Tree</td>
  </tr>
  <tr>
    <td>static void standardizeUnary (Node node, SyntaxTree ast)</td>
    <td>Standardizes unary operators and alters the Syntax Tree( obsolete due to CSE rules 6)</td>
  </tr>
  <tr>
    <td>static void standardizeBinary (Node node, SyntaxTree ast)</td>
    <td>Standardizes binary operators and alters the Syntax Tree( obsolete due to CSE rule 7)</td>
  </tr>
  <tr>
    <td>static void standardizeAt (Node node, SyntaxTree ast)</td>
    <td>Standardizes @ and alters the Syntax Tree</td>
  </tr>
  <tr>
    <td>static void standardizeSimultaneous (Node node, SyntaxTree ast)</td>
    <td>Standardizes simultaneous nodes and alters the Syntax Tree</td>
  </tr>
  <tr>
    <td>static void standardizeConditional (Node node, SyntaxTree ast)</td>
    <td>Standardizes ‘->’ and alters the Syntax Tree( obsolete due to CSE rule 8)</td>
  </tr>
  <tr>
    <td>static void standardizeRec (Node node, SyntaxTree ast)</td>
    <td>Standardizes ‘rec’ and alters the Syntax Tree</td>
  </tr>
</table>


### 3. Control Structure Generator.

In order to feed the ST to the CSE machine, the control structures are generated using the control structure generator.

#### 	a. ControlStructGenerator Class

ControlStructGenerator traverses the ST in a preorder traversal and creates the control structures necessary to be fed into the CSE machine. It uses an arraylist of arraylists to store the set of control structures.

The methods in the class are,

<table>
  <tr>
    <td>ArrayList<ArrayList<ControlElement>> preOrderTraverseHelper (SyntaxTree st)</td>
    <td>Initializes the preorder traversal with the root node</td>
  </tr>
  <tr>
    <td>void preOrderTraverse(Node node, int currentcs)</td>
    <td>Recursively iterates through all nodes in a preorder manner and creates the control structures</td>
  </tr>
</table>


### 4. CSE Machine

CSE Machine takes the processed control Structure and processes the controls and produces the last result. The control and the environment stacks are implemented with Java arrays while the stack is implemented using a stack implementation.

#### a. CSEMachine class

CSEMachine class intakes the control structure and manages the control, stack and the environment according to the CSE rules to produce the final output.

<table>
  <tr>
    <td>void runCSE()</td>
    <td>Runs the CSE Machine according to rules and generate output</td>
  </tr>
  <tr>
    <td>ControlElement getCurrent(String key)</td>
    <td>Returns the element currently on the top of the control</td>
  </tr>
  <tr>
    <td>ArrayList<ControlElement> getTuple(String key)</td>
    <td>Returns a tuple of bounded elements of an element of a given name</td>
  </tr>
  <tr>
    <td>String tuplePrinter(ControlElement element)</td>
    <td>Implements the printer functionality</td>
  </tr>
  <tr>
    <td>boolean integerValidator(String integer)</td>
    <td>Returns true if given string can be parsed to int, false otherwise</td>
  </tr>
  <tr>
    <td>boolean booleanValidator (String integer)</td>
    <td>Returns true if given string can be parsed to int, false otherwise</td>
  </tr>
</table>


 

#### b. Environment class

The class that creates the blueprint for the environment stack elements.

### 5. Common Classes and Data Structures

#### a. Node Class- Tree node class

#### b. SyntaxTree Class- Tree class used to create AST and ST

#### c. ControlElement Class- Element class which is used to create objects that are stored in control and the stack.

#### d. NodeType enum- Type of the Node

