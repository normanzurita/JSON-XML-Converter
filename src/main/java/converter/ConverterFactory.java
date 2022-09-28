package converter;

public class ConverterFactory {

    public Converter getConverter (String type) {
        if (type == null) {
            return null;
        } else if (type.equalsIgnoreCase("XML")) {
            return new XMLConverter();
        } else if (type.equalsIgnoreCase("JSON")) {
            return new JSONConverter();
        } else {
            return null;
        }
    }
}