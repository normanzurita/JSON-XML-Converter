package converter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {

    public static void main(String[] args) {
        Path path = Path.of("test.txt");
        String input;
        try {
            input = Files.readString(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ConverterFactory factory = new ConverterFactory();

        if (input.charAt(0) == '<') {
            Node node = factory.getConverter("XML").convert(input);
            String json  = XMLJSONConverter.convert(node);
            System.out.println(json);
        } else if (input.charAt(0) == '{') {
            System.out.println("JSON:");
            System.out.println("=====");
            System.out.println(input);
            System.out.println("XML:");
            System.out.println("====");
            Node node = factory.getConverter("JSON").convert(input);
            JSONXMLConverter.convert(node);
        } else {
            System.out.println("No Json or Xml object found!");
        }
    }
}