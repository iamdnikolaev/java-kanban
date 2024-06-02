package server;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import task.Task;

import java.io.IOException;
import java.time.Duration;

/**
 * Адаптер типа Duration для обработки сериализации и десериализации поля продолжительности {@link Task#getDuration()}
 * @version 1.0
 * @author Николаев Д.В.
 */
public class DurationTypeAdapter extends TypeAdapter<Duration>  {
    /**
     * Метод записи в формате JSON
     *
     * @param jsonWriter объект писателя
     * @param duration записываемое значение продолжительности
     */
    @Override
    public void write(final JsonWriter jsonWriter, final Duration duration) throws IOException {
        if (duration == null) {
            jsonWriter.nullValue();
        } else {
            jsonWriter.value(duration.toMinutes());
        }
    }

    /**
     * Метод чтения в формате JSON, чтобы распарсить объект класса Duration
     *
     * @param jsonReader объект читателя
     */
    @Override
    public Duration read(final JsonReader jsonReader) throws IOException {
        if (jsonReader.peek() == JsonToken.NULL) {
            jsonReader.nextNull();
            return null;
        } else {
            return Duration.ofMinutes(Long.parseLong(jsonReader.nextString()));
        }
    }
}
