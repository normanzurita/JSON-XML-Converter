package converter;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {

    public static void main(String[] args) {
        Path path = Path.of("test.txt");
        String input = "";
        try {
            input = Files.readString(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ConverterFactory factory = new ConverterFactory();

        if (input.toString().charAt(0) == '<') {
            System.out.println(factory.getConverter("XML").convert(input));
        } else if (input.toString().charAt(0) == '{') {
            System.out.println(factory.getConverter("JSON").convert(input));
        } else {
            System.out.println("No Json or Xml object found!");
        }
    }
}