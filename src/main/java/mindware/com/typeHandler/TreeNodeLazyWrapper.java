package mindware.com.typeHandler;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;

/**
 * Lazy JSON node wrapper, that will create generate real TreeNode after first call to it's methods.
 * Note, that in a case if input JSON string is invalid it may throw runtime exception from any method.
 */
public class TreeNodeLazyWrapper implements TreeNode, Serializable {

    private static final long serialVersionUID = -5553988352322235606L;

    private final String json;

    private JsonNode node;

    TreeNodeLazyWrapper(String json) {
        this.json = json;
    }

    /**
     * This will return source JSON string passed as argument into constructor.
     */
    public String getJsonSource() {
        return this.json;
    }

    private JsonNode tree() {
        if (this.node == null) {
            synchronized (this) {
                if (this.node == null) {
                    try {
                        node = ReaderWriter.readTree(json);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex.getMessage(), ex);
                    }
                }
            }
        }
        return this.node;
    }

    @Override
    public JsonToken asToken() {
        return tree().asToken();
    }

    @Override
    public JsonParser.NumberType numberType() {
        return tree().numberType();
    }

    @Override
    public int size() {
        return tree().size();
    }

    @Override
    public boolean isValueNode() {
        return tree().isValueNode();
    }

    @Override
    public boolean isContainerNode() {
        return tree().isContainerNode();
    }

    @Override
    public boolean isMissingNode() {
        return tree().isMissingNode();
    }

    @Override
    public boolean isArray() {
        return tree().isArray();
    }

    @Override
    public boolean isObject() {
        return tree().isObject();
    }

    @Override
    public TreeNode get(String string) {
        return tree().get(string);
    }

    @Override
    public TreeNode get(int i) {
        return tree().get(i);
    }

    @Override
    public TreeNode path(String string) {
        return tree().path(string);
    }

    @Override
    public TreeNode path(int i) {
        return tree().path(i);
    }

    @Override
    public Iterator<String> fieldNames() {
        return tree().fieldNames();
    }

    @Override
    public TreeNode at(JsonPointer jp) {
        return tree().at(jp);
    }

    @Override
    public TreeNode at(String string) throws IllegalArgumentException {
        return tree().at(string);
    }

    @Override
    public JsonParser traverse() {
        return tree().traverse();
    }

    @Override
    public JsonParser traverse(ObjectCodec oc) {
        return tree().traverse(oc);
    }

    @Override
    public String toString() {
        return tree().toString();
    }

    @Override
    public int hashCode() {
        return tree().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return tree().equals(o);
    }
}