package com.enchantment.genbatis.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Arrays;
import java.util.HashMap;

/**
 * <p>Data storage util class</p>
 *
 * Created by liushuang on 9/29/16.
 */
public class SimpleStorage {
    private static final Logger L = LoggerFactory.getLogger(SimpleStorage.class);

    protected static final String GET_SQL = "select * from $tab where 1<0";

    private Connection conn;

    public SimpleStorage(Connection conn) {
        this.conn = conn;
    }

    public SimpleStorage(DataSource dataSource) throws SQLException {
        this.conn = dataSource.getConnection();
    }

    /**
     *
     * @param schema
     * @param table
     * @return
     */
    public HashMap<String, String> getColumns(final String schema, final String table) {
        String tab = table;
        if (schema != null && !schema.equals("")) {
            tab = schema.concat(".").concat(table);
        }

        try {
            // Create sql statement
            Statement st = conn.createStatement(ResultSet.TYPE_FORWARD_ONLY,
                    ResultSet.CONCUR_READ_ONLY, ResultSet.CLOSE_CURSORS_AT_COMMIT);
            // Execute query with param table and schema
            String sql = GET_SQL.replaceFirst("\\$tab", tab);
            L.debug("Exec SQL: ".concat(sql));
            ResultSet result = st.executeQuery(sql);
            // Obtain meta data of table columns
            ResultSetMetaData meta = result.getMetaData();

            int count = meta.getColumnCount();
            HashMap<String, String> cols = new HashMap<>(count);
            for (int i = 1; i <= count; i++) {
                cols.put(meta.getColumnName(i), meta.getColumnClassName(i));
            }

            return cols;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public HashMap<String, HashMap<String, String>> getMultiCols(
            final String schema, final String... tables) {
        HashMap<String, HashMap<String, String>> multiCols = new HashMap<>(tables.length);
        Arrays.stream(tables)
                .map(tab -> multiCols.put(tab, getColumns(schema, tab)))
                .count();

        return multiCols;
    }
}
