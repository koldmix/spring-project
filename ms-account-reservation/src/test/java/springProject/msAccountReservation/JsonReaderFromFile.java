package springProject.msAccountReservation;

import org.springframework.stereotype.Component;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;


import java.util.Objects;

public class JsonReaderFromFile {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static <T> T readerJson(String path, Class<T> type){
        try {
            return objectMapper.readValue(Objects.requireNonNull(
                    JsonReaderFromFile.class.getClassLoader().getResource(path)), type);
        } catch (Exception e) {
            throw new RuntimeException("Failed to read test resource: " + path, e);
        }
    }
}
