package io.github.overlordsiii.util.adapter;

import java.lang.reflect.Type;
import java.time.LocalDateTime;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
	/**
	 * Gson invokes this call-back method during serialization when it encounters a field of the
	 * specified type.
	 *
	 * <p>In the implementation of this call-back method, you should consider invoking
	 * {@link JsonSerializationContext#serialize(Object, Type)} method to create JsonElements for any
	 * non-trivial field of the {@code src} object. However, you should never invoke it on the
	 * {@code src} object itself since that will cause an infinite loop (Gson will call your
	 * call-back method again).</p>
	 *
	 * @param src       the object that needs to be converted to Json.
	 * @param typeOfSrc the actual type (fully genericized version) of the source object.
	 * @param context
	 * @return a JsonElement corresponding to the specified object.
	 */
	@Override
	public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
		return new JsonPrimitive(src.toString());
	}

	/**
	 * Gson invokes this call-back method during deserialization when it encounters a field of the
	 * specified type.
	 * <p>In the implementation of this call-back method, you should consider invoking
	 * {@link JsonDeserializationContext#deserialize(JsonElement, Type)} method to create objects
	 * for any non-trivial field of the returned object. However, you should never invoke it on the
	 * same type passing {@code json} since that will cause an infinite loop (Gson will call your
	 * call-back method again).
	 *
	 * @param json    The Json data being deserialized
	 * @param typeOfT The type of the Object to deserialize to
	 * @param context
	 * @return a deserialized object of the specified type typeOfT which is a subclass of {@code T}
	 * @throws JsonParseException if json is not in the expected format of {@code typeofT}
	 */
	@Override
	public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		String dateTime = json.getAsString();
		return LocalDateTime.parse(dateTime);
	}
}