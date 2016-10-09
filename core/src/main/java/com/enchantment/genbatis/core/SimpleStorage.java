package com.enchantment.genbatis.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * <p>Data storage util class</p>
 *
 * Created by liushuang on 9/29/16.
 */
class SimpleStorage {
    private static final Logger L = LoggerFactory.getLogger(SimpleStorage.class);

    private static final String GET_SQL = "select * from $tab where 1<0";

    private Connection conn;

    SimpleStorage(Connection conn) {
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
    LinkedHashMap<String, String> getColumns(final String schema, final String table) {
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
            LinkedHashMap<String, String> cols = new LinkedHashMap<>(count);
            for (int i = 1; i <= count; i++) {
                cols.put(meta.getColumnName(i).toLowerCase(), meta.getColumnClassName(i));
            }

            return cols;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    LinkedHashMap<String, HashMap<String, String>> getMultiCols(
            final String schema, final String... tables) {
        LinkedHashMap<String, HashMap<String, String>> multiCols = new LinkedHashMap<>(tables.length);
        Arrays.stream(tables)
                .map(tab -> multiCols.put(tab, getColumns(schema, tab)))
                .count();

        return multiCols;
    }
}
