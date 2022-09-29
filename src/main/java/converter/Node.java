package converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Node {

    private String key;
    private String value;
    private Map<String, String> attributes;
    private List<Node> nodes;
    private Map<String, String> keysAndValues;

    public Node() {
        this.nodes = new ArrayList<>();
        this.attributes = new TreeMap<>();
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }

    public void setKey(String key) {
        this.key = key;
    }


    public void setValue(String value) {
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {

        StringBuilder str = new StringBuilder();
        str.append("Node{");
        str.append("key='" + key + '\'');

        if (nodes.size() > 0) {
            str.append(", nodes=" + nodes +
                    '}');
        } else {
            str.append(", value='" + value + '\'');
        }

        return str.toString();
    }
}