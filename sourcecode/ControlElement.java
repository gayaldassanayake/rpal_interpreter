import java.util.ArrayList;

/**
 * Template for control elements in CSE machine
 */
public class ControlElement {
    private String value;
    private String boundVar;
    private ArrayList<String> boundVars;
    private int env;
    private NodeType type;
    private int ifIndex;
    private ArrayList<ControlElement> elements;

    ControlElement(String value, NodeType type) {
        this.value = value;
        this.type = type;
    }

    ControlElement(String value, NodeType type, int ifIndex) {
        this.value = value;
        this.type = type;
        this.ifIndex = ifIndex;
    }

    ControlElement(String value, ArrayList<ControlElement> elements, NodeType type) {
        this.value = value;
        this.elements = elements;
        this.type = type;
    }

    ControlElement(String value, String boundVar, int env, NodeType type) {
        this.value = value;
        this.boundVar = boundVar;
        this.env = env;
        this.type = type;
    }

    ControlElement(String value, ArrayList<String> boundVars, int env, NodeType type) {
        this.value = value;
        this.boundVars = boundVars;
        this.env = env;
        this.type = type;
    }

    public String getValue() {
        return this.value;
    }

    public String getValue(ArrayList<Environment> env) {
        for(int i= env.size()-1; i>=0; i--){
            if (env.get(i).getKey().equals(this.getValue()))
                return env.get(i).getBoundEle().getValue();
        }
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getBoundVar() {
        return this.boundVar;
    }

    public void setBoundVar(String boundVar) {
        this.boundVar = boundVar;
    }

    public int getEnv() {
        return this.env;
    }

    public void setEnv(int env) {
        this.env = env;
    }

    public ArrayList<String> getBoundVars() {
        return this.boundVars;
    }

    public void setBoundVars(ArrayList<String> boundVars) {
        this.boundVars = boundVars;
    }

    public NodeType getType() {
        return this.type;
    }

    public void setType(NodeType type) {
        this.type = type;
    }

    public int getIfIndex() {
        return this.ifIndex;
    }

    public void setIfIndex(int ifIndex) {
        this.ifIndex = ifIndex;
    }

    public ArrayList<ControlElement> getElements() {
        return this.elements;
    }

    public void setElements(ArrayList<ControlElement> elements) {
        this.elements = elements;
    }

}
