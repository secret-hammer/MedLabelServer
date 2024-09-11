package com.vipa.medlabel.config.mongodbconfig;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.bson.types.ObjectId;

import java.io.IOException;
import java.util.regex.Pattern;

public class ObjectIdDeserializer extends JsonDeserializer<ObjectId> {
    private static final Pattern HEX_PATTERN = Pattern.compile("^[0-9a-fA-F]{24}$");

    @Override
    public ObjectId deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException {
        String hexString = jsonParser.readValueAs(String.class);
        if (!HEX_PATTERN.matcher(hexString).matches()) {
            throw new JsonMappingException(jsonParser, hexString);
        }
        return new ObjectId(hexString);
    }
}
