import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Parses the AST and genates a tree structure
 */
public class ASTGenerator {

    /**
     * 
     * @return ArrayList{String}
     */
    ArrayList<String> readInput(String filename) {
        FileReader fr;
        ArrayList<String> inputs = new ArrayList<String>();
        try {
            fr = new FileReader(filename);
            Scanner sc = new Scanner(fr);
            while (sc.hasNextLine()) {
                inputs.add(sc.nextLine().strip());
            }
            sc.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return inputs;
    }

    /**
     * prints the AST given the root of it
     * 
     * @param node
     */
    void printAST(Node node) {
        String name = "";
        for (int i = 0; i < node.getDepth(); i++) {
            name += ".";
        }
        name += node.getValue();
        System.out.println(name);

        // calls print recursively for children nodes
        for (int i = 0; i < node.getChildren().size(); i++) {
            printAST(node.getChildren().get(i));
        }
    }

    /**
     * creates and returns a node with a type specified
     * 
     * @param name
     * @param parent
     * @param depth
     * @return Node
     */
    Node createNode(String name, Node parent, int depth) {
        String value;
        NodeType type;
        String content = name.substring(depth);

        // splits the input node names
        if (content.startsWith("<") && content.endsWith(">")) {
            if ((content.substring(1, 4)).equals("INT")) {
                value = content.substring(5, content.length() - 1);
                type = NodeType.INTEGER;
            } else if ((content.substring(1, 3)).equals("ID")) {
                value = content.substring(4, content.length() - 1);
                type = NodeType.IDENTIFIER;
            } else if ((content.substring(1, 4)).equals("STR")) {
                value = content.substring(5, content.length() - 1);
                type = NodeType.STRING;
            } else {
                value = content.substring(1, content.length() - 1);
                type = NodeType.BOOLEAN;
            }
        } else {
            value = content;
            type = NodeType.NONTERMINAL;
        }
        return new Node(value, parent, depth, type);

    }

    /**
     * returns the depth of a given node from the root
     * 
     * @param name
     * @return depth
     */
    int findDepth(String name) {
        int depth = 0;
        for (int i = 0; i < name.length(); i++) {
            if (name.charAt(i) == '.') {
                depth++;
            } else
                break;
        }
        return depth;
    }

    /**
     * creates the current node using createNode() and attaches to the Syntax Tree
     * 
     * @param lastNode
     * @param name
     * @return created Node
     */
    Node addNode(Node lastNode, String name) {
        int depth = findDepth(name);
        Node current;
        if (depth == lastNode.getDepth() + 1) {
            current = createNode(name, lastNode, depth);
            lastNode.setChild(current);

        } else {
            current = addNode(lastNode.getParent(), name);
        }
        return current;
    }

    /**
     * generates the AST and retuns
     * 
     * @return Syntax Tree
     */
    SyntaxTree createAST(String filename) {
        ArrayList<String> inputs = readInput(filename);
        SyntaxTree ast = new SyntaxTree(inputs.get(0));
        Node root = ast.getRoot();

        Node lastNode = root;
        for (int i = 1; i < inputs.size(); i++) {
            lastNode = addNode(lastNode, inputs.get(i));
            ast.addNode(lastNode);
        }
        return ast;
    }
}