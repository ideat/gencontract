package mindware.com.typeHandler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.MissingNode;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Value container that transfer JSON from/into DB.
 * Main feature of this container is lazy initializing while reading values from DB.
 * It will build JsonNode object only at first call of {@link JsonNodeValue#get()}
 * and sometimes {@link JsonNodeValue#isEmpty()}.
 */
public class JsonNodeValue implements Serializable {

    private static final long serialVersionUID = 745861884668365334L;

    /**
     * Value container without any content.
     * You will not be able to call get() methods on this object.
     */
    public static JsonNodeValue EMPTY = new JsonNodeValue();

    private String source;

    private boolean dbSource;

    private transient JsonNode value;

    private JsonNodeValue() {
        this.source = null;
        this.value = null;
    }

    private JsonNodeValue(String source) {
        if (source != null) {
            source = source.trim();
            this.source = source.isEmpty() ? null : source;
        } else {
            this.source = null;
        }
        this.value = null;
    }

    private JsonNodeValue(JsonNode value) {
        this.value = value;
        this.source = null;
    }

    /**
     * Build value container from JsonNode object.
     * In this case {@link JsonNodeValue#get()} will never throw any exception.
     *
     * @param node JSON node or null
     */
    public static JsonNodeValue from(JsonNode node) {
        return node == null ? EMPTY : new JsonNodeValue(node);
    }

    /**
     * Build value container from JSON string.
     * NOTE if input is not valid JSON than exception in {@link JsonNodeValue#get()} will be thrown.
     *
     * @param json JSON string or null
     */
    public static JsonNodeValue from(String json) {
        if (json == null || json.isEmpty()) {
            return EMPTY;
        }
        json = json.trim();
        return json.isEmpty() ? EMPTY : new JsonNodeValue(json);
    }

    static JsonNodeValue fromDb(String json) {
        JsonNodeValue v = from(json);
        if (v.isPresent()) {
            v.dbSource = true;
        }
        return v;
    }

    /**
     * Test input value and return not null - value from input or empty object.
     */
    public static JsonNodeValue orEmpty(JsonNodeValue node) {
        return node == null || node.isNotPresent() ? EMPTY : node;
    }

    /**
     * Check if nested value is present (not null or empty JSON string).
     */
    public boolean isPresent() {
        return value != null || (source != null && !source.isEmpty());
    }

    /**
     * Opposite to {@link JsonNodeValue#isPresent()}.
     */
    public boolean isNotPresent() {
        return !isPresent();
    }

    /**
     * Return true if value is not present or if underlying JSON is empty object, array or null.
     * WARNING this method can throw same exceptions as {@link JsonNodeValue#get()} in a case if
     * source is invalid JSON string.
     */
    public boolean isEmpty() {
        if (!isPresent()) {
            return true;
        }

        JsonNode n = get();

        if ((n.isObject() || n.isArray()) && n.size() == 0) {
            return true;
        }

        if (n.isNull()) {
            return true;
        }

        return false;
    }

    /**
     * Opposite to {@link JsonNodeValue#isEmpty()}.
     */
    public boolean isNotEmpty() {
        return !isEmpty();
    }

    /**
     * Return COPY of JSON node value (will parse node from string at first call).
     * WARNING if object constructed with invalid JSON string, exception will be thrown.
     *
     * @return Copy of valid JsonNode or MissingNode if no data.
     * @throws RuntimeException On JSON parsing errors.
     */
    public JsonNode get() throws RuntimeException {
        if (!isPresent()) {
            return MissingNode.getInstance();
        }

        if (value == null) {
            synchronized (this) {
                if (value == null) {
                    try {
                        value = ReaderWriter.readTree(source);
                    } catch (Exception ex) {
                        throw new RuntimeException("Can not parse JSON string. " + ex.getMessage(), ex);
                    }
                }
            }
        }
        return value.deepCopy();
    }

    /**
     * Same as {@link JsonNodeValue#get()}.
     * Created for compatibility with frameworks that works with object properties,
     * thus require get* methods.
     */
    public JsonNode getValue() {
        return get();
    }

    boolean hasDbSource() {
        return this.dbSource && this.source != null;
    }

    String getSource() {
        return this.source;
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        if (this.source == null && this.value != null) {
            this.source = ReaderWriter.write(this.value);
        }
        oos.defaultWriteObject();
    }
}
