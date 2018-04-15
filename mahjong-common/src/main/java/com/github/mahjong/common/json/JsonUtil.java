package com.github.mahjong.common.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class JsonUtil {
    private static final Logger log = LoggerFactory.getLogger(JsonUtil.class);

    private static final ObjectMapper MAPPER;

    static {
        MAPPER = new ObjectMapper();
        MAPPER.registerModule(new JavaTimeModule());
        MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    public static <T> T readValue(JsonNode node, Class<T> cls) throws IOException {
        try {
            return MAPPER.readerFor(cls).readValue(node);
        } catch (IOException e) {
            log.error("Failed to parse json: " + node.toString());
            throw new IOException(e);
        }
    }

    public static <T> T readValue(String jsonString, Class<T> cls) throws IOException {
        try {
            return MAPPER.readerFor(cls).readValue(jsonString);
        } catch (IOException e) {
            log.error("Failed to parse json: " + jsonString);
            throw new IOException(e);
        }
    }

    public static JsonNode writeValue(Object obj) {
        return MAPPER.valueToTree(obj);
    }

    private JsonUtil() {
    }

}
