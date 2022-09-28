package converter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XMLConverter extends Converter {

    public void recursiveXML(String input, String path) {
        String tag = findOpeningTag(input);
        System.out.println("Element:");
        if (path.equals("")) {
            System.out.println("path = " + tag);
        } else {
            System.out.println("path = " + path + ", " + tag);
        }
        String value = "";
        String rest = "";
        if (isDirectlyClosingTag(input.trim())) {
            System.out.println("value = null");
            handleAttributes(input, tag);
            System.out.println();
            rest = input.substring(input.indexOf(">") + 1);
        } else {
            value = getValueFromXML(input, tag).replace("\n", "").trim();
            if ((!value.contains("<") && !value.contains(">"))) {
                System.out.println("value = \"" + value + "\"");
                handleAttributes(input, tag);
                System.out.println();
            } else {
                handleAttributes(input, tag);
                System.out.println();
                if (path.isEmpty()) {
                    recursiveXML(value, tag);
                } else {
                    recursiveXML(value, path + ", " + tag);
                }

            }
            rest = cutTagFromInput(input.trim().replace("\n", ""), tag);
        }
        if (!rest.isEmpty()) {
            recursiveXML(rest, path);
        }
    }

    private String findOpeningTag(String input) {
        String openingTagsRegex_justTags = "(?<=[<])(?![<\\/])[#@A-Za-z=\"\\/]+";

        Pattern pattern = Pattern.compile(openingTagsRegex_justTags);
        Matcher matcher = pattern.matcher(input);

        return matcher.find() ? matcher.group() : null;
    }

    private boolean isDirectlyClosingTag (String input) {
        Pattern pattern = Pattern.compile("^<([^>]*)/>.*");
        Matcher matcher = pattern.matcher(input);

        return matcher.matches();
    }


    private void handleAttributes(String input, String tag) {
        if (!hasAttributes(input.trim(), tag)) {
            List<String> attributes = splitAttributeString(findAttributes(input.trim(), tag).trim());
            if (attributes.size() != 0) {
                System.out.println("attributes:");
                for (String s : attributes) {
                    System.out.println(s.replace("=", " = "));
                }
            }
        }
    }

    private boolean hasAttributes(String input, String tag) {
        String regex = "^<" + tag + "([^>/]*)/?>.*";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            return matcher.group(1).trim().isEmpty();
        }
        return false;
    }

    private List<String> splitAttributeString (String input) {
        List<String> result = new ArrayList<>();
        input = input.trim();
        while (input.indexOf("\"", input.indexOf("\"") + 1) != -1) {
            result.add(input.substring(0, input.indexOf("\"", input.indexOf("\"") + 1) + 1).trim());
            input = input.substring(input.indexOf("\"", input.indexOf("\"") + 1) + 1);
        }
        return result;
    }

    private String findAttributes (String input, String tag) {
        String regex = "^<" + tag + "([^>]*)/?>.*";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        String result = "";

        if (matcher.find()) {
            result = matcher.group(1);
            if (result.length() - 1 == result.lastIndexOf("/")) {
                result = result.substring(0, result.lastIndexOf("/"));
            }
        }
        return result;
    }


    private String getValueFromXML(String input, String tag) {

        String xmlValueRegex = "(?<=[><])[#@A-Za-z0-9\\s]+(?=[<>])";
        String xmlValueRegex2 = "<" + tag + ".*?" + ">([\\s\\S]*?)</" + tag + ">";
        String xmlValueRegex3 = "<" + tag + ".*>";


        Pattern pattern = Pattern.compile(xmlValueRegex2);
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            if (matcher.group(1) == null) {
                return matcher.group(2);
            } else {
                return matcher.group(1);
            }
        } else {
            return "null";
        }
    }

    private String cutTagFromInput(String input, String tag) {
        input = input.replace("\n", "");
        return input.substring(input.indexOf("/" + tag) + tag.length() + 2);
    }

    @Override
    public String convert(String input) {
        return null;
    }
}