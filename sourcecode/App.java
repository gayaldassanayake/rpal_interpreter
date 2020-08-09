import java.util.ArrayList;

/**
 * The driver class of the project
 */
public class App {
    public static void main(String[] args) throws Exception {
        String fileName = args[0];
        System.out.println("Output of the above program is:");
        // create the AST from the input
        ASTGenerator astgen = new ASTGenerator();
        SyntaxTree ast = astgen.createAST(fileName);

        // uncomment to view the AST
        // ast.printTree

        // create the ST from the AST
        STGenerator stgen = new STGenerator();
        stgen.createST(ast);

        // uncomment to view the ST
        // ast.printTree

        // Generate the control Stuctures
        ControlStructGenerator csg = new ControlStructGenerator();
        ArrayList<ArrayList<ControlElement>> cstruct = csg.preOrderTraverseHelper(ast);
        // uncomment to view the Control Structs
        // csg.printCS();

        // Create and run the CSE machine with control structures as input
        CSEMachine csemachine = new CSEMachine(cstruct);
        csemachine.runCSE();
    }
}
