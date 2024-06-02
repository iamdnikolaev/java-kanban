package server;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import task.Task;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Адаптер типа LocalDateTime для обработки сериализации и десериализации поля даты начала {@link Task#getStartTime()}
 * @version 1.0
 * @author Николаев Д.В.
 */
public class LocalDateTimeTypeAdapter extends TypeAdapter<LocalDateTime> {
    /** Поле используемого формата даты и времени */
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    /**
     * Метод записи в формате JSON
     *
     * @param jsonWriter объект писателя
     * @param localDateTime записываемое значение даты начала
     */
    @Override
    public void write(final JsonWriter jsonWriter, final LocalDateTime localDateTime) throws IOException {
        if (localDateTime == null) {
            jsonWriter.nullValue();
        } else {
            jsonWriter.value(localDateTime.format(timeFormatter));
        }
    }

    /**
     * Метод чтения в формате JSON, чтобы распарсить объект класса LocalDateTime
     *
     * @param jsonReader объект читателя
     */
    @Override
    public LocalDateTime read(final JsonReader jsonReader) throws IOException {
        if (jsonReader.peek() == JsonToken.NULL) {
            jsonReader.nextNull();
            return null;
        } else {
            return LocalDateTime.parse(jsonReader.nextString(), timeFormatter);
        }
    }
}
