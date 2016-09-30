package com.enchantment.genbatis.core;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * Created by liushuang on 9/29/16.
 */
public class SimpleStorageTest {
    private static final Logger L = LoggerFactory.getLogger(SimpleStorage.class);

    private Connection conn;

    @Before
    public void init() {
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            Connection conn = DriverManager.getConnection(
                    "jdbc:mariadb://localhost:3306/eaas", "eaas", "123456");
            L.info("Open database connection.");

            this.conn = conn;
        } catch (Exception e) {
            L.error("Initialize error", e);
        }
    }

//    @Test
    public void getColsTest() {
        SimpleStorage storage = new SimpleStorage(conn);
        HashMap<String, String> cols = storage.getColumns(null, "t_user");
        System.out.println(cols);
    }

    @Test
    public void getMultiColsTest() {
        SimpleStorage storage = new SimpleStorage(conn);
        HashMap<String, HashMap<String, String>> cols = storage
                .getMultiCols(null, "t_user", "T_GROUP", "T_ROLE");
        System.out.println(cols);

        assertNotNull(cols);
        assertEquals(cols.size(), 3);
        assertTrue(cols.containsKey("t_user"));
        assertTrue(cols.containsKey("T_GROUP"));
        assertTrue(cols.containsKey("T_ROLE"));
    }

    @After
    public void finalize() {
        try {
            if (conn != null && !conn.isClosed())
                conn.close();
            conn = null;
            L.info("Close database connection.");
        } catch (SQLException e) {
            L.error("Finalize error", e);
        }
    }
}
