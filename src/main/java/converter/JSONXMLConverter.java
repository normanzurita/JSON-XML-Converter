package converter;

public class JSONXMLConverter {


    public static void convert(Node node) {
        if (node.getNodes().size() > 1) {
            System.out.println("<root>");
            for (Node n : node.getNodes()) {
                convert(n, 4);
            }
            System.out.println("</root>");
        } else {
            convert(node.getNodes().get(0), 0);
        }

    }
    public static void convert(Node node, int level) {
        if (node.getKey() != null) {
            System.out.print(" ".repeat(level));
            System.out.print(openOpeningTag(node.getKey()));

            if (node.getAttributes().size() != 0) {
                node.getAttributes().forEach((key, value) -> {
                    System.out.print(addAttribute(key, value));
                });
            }
            System.out.print(">");
        } else {
            level -= 4;
        }

        if (node.getValue() != null) {
            System.out.print(printValue(node.getValue()));
        }

        for (Node childNodes : node.getNodes()) {
            System.out.println();
            convert(childNodes, level + 4);
        }

        if (node.getKey() != null && node.getNodes().size() == 0) {
            System.out.print(printClosingTag(node.getKey()));
        } else {
            System.out.println();
            System.out.println(" ".repeat(level) + printClosingTag(node.getKey()));
        }

    }

    public static void printNode(Node node) {
        System.out.println(node.getKey());
    }

    public static String openOpeningTag(String tagName) {
        return "<" + tagName;
    }

    public static String closeOpeningTag(String openingTag) {
        return openingTag + ">";
    }

    public static String printValue(String value) {
        return value.trim().replaceAll("\"","");
    }

    public static String addAttribute(String key, String value) {
        if (value.equals("")) {
            return " " + key + "=" + "\"\"";
        }
        return " " + key + "=" + value.substring(1);
    }

    public static String printClosingTag(String tagName) {
        return "</" + tagName + ">";
    }



}


