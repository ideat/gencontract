package mindware.com.typeHandler;


import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.ibatis.executor.result.ResultMapException;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Map JSON column string as TreeNode.
 * Return MissingNode instead of null.
 * Use JSON string representation as intermediate data format.
 *
 * @see TreeNode
 */
@MappedTypes({JsonNode.class, TreeNode.class, ArrayNode.class, ObjectNode.class})
public class TreeNodeTypeHandler extends BaseTypeHandler<TreeNode> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, TreeNode parameter, JdbcType jdbcType) throws SQLException {
        try {
            ps.setString(i, ReaderWriter.write(parameter));
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    @Override
    public TreeNode getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String jsonSource = rs.getString(columnName);
        return fromString(jsonSource);
    }

    @Override
    public TreeNode getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String jsonSource = rs.getString(columnIndex);
        return fromString(jsonSource);
    }

    @Override
    public TreeNode getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String jsonSource = cs.getString(columnIndex);
        return fromString(jsonSource);
    }

    private TreeNode fromString(String source) {
        if (source == null || source.isEmpty()) {
            // This is where we replace null result with empty node
            return MissingNode.getInstance();
        } else {
            // I really hope that source will be valid JSON string  (^_^)
            return new TreeNodeLazyWrapper(source);
        }
    }

    /*
    Override BaseTypeHandler in such way that result will never be null
     */
    @Override
    public TreeNode getResult(ResultSet rs, String columnName) throws SQLException {
        try {
            return getNullableResult(rs, columnName);
        } catch (Exception e) {
            throw new ResultMapException("Error attempting to get column '" + columnName + "' from result set.  Cause: " + e, e);
        }
    }

    @Override
    public TreeNode getResult(ResultSet rs, int columnIndex) throws SQLException {
        try {
            return getNullableResult(rs, columnIndex);
        } catch (Exception e) {
            throw new ResultMapException("Error attempting to get column #" + columnIndex + " from result set.  Cause: " + e, e);
        }
    }

    @Override
    public TreeNode getResult(CallableStatement cs, int columnIndex) throws SQLException {
        try {
            return getNullableResult(cs, columnIndex);
        } catch (Exception e) {
            throw new ResultMapException("Error attempting to get column #" + columnIndex + " from callable statement.  Cause: " + e, e);
        }
    }
}