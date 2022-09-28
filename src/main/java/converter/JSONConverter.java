package converter;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JSONConverter extends Converter {


    public static String quoteString (String input) {
        if (input.equals("null")) {
            return "null";
        }
        if (!input.startsWith("\"") && !input.endsWith("\"")) {
            return ("\"" + input + "\"");
        } else {
            return input;
        }
    }

    public static void sanitizeNode(JSONNode node) {
        JSONNode valueNode = null;
        for (JSONNode childNodes : node.getNodes()) {
            if (childNodes.getKey().equals("#" + node.getKey())) {
                valueNode = childNodes;
                break;
            }
        }

        boolean isValueObject = (valueNode != null);

        for (JSONNode childNode : node.getNodes()) {
            if (childNode == valueNode) {
                continue;
            }
            if (!childNode.getKey().startsWith("@") || childNode.getKey().length() < 2) {
                isValueObject = false;
                break;
            }
        }
        for (JSONNode childNode : node.getNodes()) {
            if (childNode.getKey().startsWith("@")) {
                if (!childNode.getNodes().isEmpty()) {
                    childNode.setKey(childNode.getKey().substring(1));
                    isValueObject = false;
                }
            }
        }

        if (isValueObject) {
            List<JSONNode> nodesToRemove = new ArrayList<>();
            if (valueNode.getNodes().isEmpty()) {
                node.setValue(valueNode.getValue());
            } else {
                node.setValue(null);
                for (JSONNode childNodes : valueNode.getNodes()) {
                    node.getNodes().add(childNodes);
                }
            }
            for (JSONNode childNode : node.getNodes()) {
                if (childNode.getKey().startsWith("@")) {
                    if (childNode.getValue().trim().equals("null")) {
                        childNode.setValue("");
                    }
                    nodesToRemove.add(childNode);
                    node.getAttributes().put(childNode.getKey().substring(1), childNode.getValue());
                }
            }
            nodesToRemove.add(valueNode);
            node.getNodes().removeAll(nodesToRemove);
        } else {
            List<JSONNode> nodesToRemove = new ArrayList<>();

            for (JSONNode childNode : node.getNodes()) {
                if (childNode.getKey().startsWith("#") || childNode.getKey().startsWith("@")) {
                    boolean alreadyExists = false;
                    String newKey = childNode.getKey().substring(1);
                    for (JSONNode subNodes : node.getNodes()) {
                        if (newKey.equals(subNodes.getKey())) {
                            alreadyExists = true;
                            break;
                        }
                    }
                    childNode.setKey(newKey);
                    if (alreadyExists) {
                        nodesToRemove.add(childNode);
                    }
                }

                if (childNode.getKey().length() < 1) {
                    nodesToRemove.add(childNode);
                }
            }
            node.getNodes().removeAll(nodesToRemove);
            if (!node.getNodes().isEmpty()) {
                node.setValue(null);
            }
        }

        if (node.getNodes().isEmpty() && node.getValue() != null) {
            if (node.getValue().contains("{")) {
                node.setValue("\"\"");
            }
        }

        if (!node.getNodes().isEmpty()) {
            for (JSONNode childNodes : node.getNodes()) {
                sanitizeNode(childNodes);
            }
        }

    }


    public static void printNode(JSONNode node, String path, PrintStream out) {
        out.println("Element:");
        if (path.isEmpty()) {
            path = node.getKey();
        } else {
            path = path + ", " + node.getKey();
        }
        out.println("path = " + path);
        if (node.getValue() != null) {
            out.println("value = " + quoteString(node.getValue().replace("\n", "").trim()));
        }

        if (!node.getAttributes().isEmpty()) {
            out.println("attributes:");

            node.getAttributes().forEach((key, value) -> out.println(key + " = " + quoteString(value.trim())));
        }
        out.println();

        for (JSONNode childNodes : node.getNodes()) {
            printNode(childNodes, path, out);
        }
    }

    public static JSONNode recursiveJSON(String input, String path) {
        List<String> listOfSubNodes = new ArrayList<>();
        List<String> listOfNodesInJSON = new ArrayList<>();
        JSONNode node = new JSONNode();
        node.setValue(input.replace("\n", ""));
        listOfNodesInJSON = splitByTags(input);

        for (String s : listOfNodesInJSON) {
            JSONNode childNode;
            String tag = s.substring(1, s.indexOf("\"", 1));
            String value = s.substring(s.indexOf(":") + 1);
            assert tag != null;

            if (value.contains("{")) {
                childNode = recursiveJSON(value, path);
                childNode.setValue(value.replace("\n", ""));
                childNode.setKey(tag);
                node.getNodes().add(childNode);
            } else {
                childNode = new JSONNode();
                childNode.setValue(value);
                childNode.setKey(tag);
                node.getNodes().add(childNode);
            }
        }
        return node;
    }

    public static int skipWS(String input, int index) {
        while (input.length() > index) {
            if (Character.isWhitespace(input.charAt(index))) {
                index++;
            } else {
                break;
            }
        }
        return index;
    }

    public static List<String> splitByTags(String input) {
        List<String> list = new ArrayList<String>();

        input = input.trim();
        if (input.startsWith("{")) {
            input = input.substring(1);
        }
        input = input.trim();
        while (!input.isEmpty()) {
            switch (input.charAt(0)) {
                case '"':
                    int startIndex = 0;
                    String key = input.substring(0, input.indexOf("\""));
                    startIndex = skipWS(input, input.indexOf("\"", 1) + 1);
                    if (input.charAt(startIndex) != ':') {
                        throw new IllegalArgumentException();
                    }
                    startIndex = skipWS(input, startIndex + 1);
                    if (input.charAt(startIndex) == '{') {
                        startIndex = searchClosingBracket(input) + 1;
                    } else {
                        // value

                    }
                    int endIndex = input.indexOf(",", startIndex);
                    if (endIndex == -1) {
                        endIndex = input.indexOf("}", startIndex);
                    }
                    list.add(input.substring(0, endIndex == -1 ? input.length() : endIndex));
                    if (endIndex == -1) {
                        return list;
                    }
                    input = input.substring(endIndex + 1).trim();
                    break;
                case '}':
                    return list;
            }
        }
        return list;
    }

    public static int searchClosingBracket(String input) {
        int startIndex = input.indexOf("{");
        int counter = 0;
        int endIndex;
        for (endIndex = startIndex; endIndex < input.length(); endIndex++) {
            if (input.charAt(endIndex) == '{') {
                counter++;
            } else if (input.charAt(endIndex) == '}') {
                counter--;
            }
            if (counter == 0) {
                break;
            }
        }
        return endIndex;
    }

    @Override
    public String convert(String input) {
        JSONNode node = recursiveJSON(input, "");
        sanitizeNode(node);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream printOut = new PrintStream(out);
        for (JSONNode childNode : node.getNodes()) {
            StringBuilder sb = new StringBuilder();

            printNode(childNode, "", printOut);
        }
        return out.toString();
    }
}