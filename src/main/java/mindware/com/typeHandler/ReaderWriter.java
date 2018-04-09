package mindware.com.typeHandler;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.io.IOException;

final class ReaderWriter {

    private static final ObjectReader READER;

    private static final ObjectWriter WRITER;

    static {
        ObjectMapper mapper = new ObjectMapper();
        WRITER = mapper.writer();

        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        mapper.configure(JsonParser.Feature.ALLOW_NUMERIC_LEADING_ZEROS, true);
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        READER = mapper.reader();

    }

    static JsonNode readTree(String json) throws IOException {
        return READER.readTree(json);
    }

    static String write(TreeNode tree) throws JsonProcessingException {
        return WRITER.writeValueAsString(tree);
    }
}
