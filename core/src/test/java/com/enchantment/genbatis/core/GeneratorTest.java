package com.enchantment.genbatis.core;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static com.enchantment.genbatis.core.Generator.TYPE.*;

/**
 * Created by liushuang on 10/9/16.
 */
public class GeneratorTest {
    private static final Logger L = LoggerFactory.getLogger(TemplatedTest.class);

    private Connection conn;
    private GenerInfo info;

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

        info = new GenerInfo();
        info.setBasePackage("com.enchantment.eaas");
        info.setSchema("eaas");
        info.setTable("t_role");
        info.setTablePrefix("t_");
        info.setMapperPkg("resouces.mapper");
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
    @Test
    public void genTest() {
        Generator g = new Generator(info);
        g.setConn(conn);
        try {
            g.gen(CON, SER, DAO, DMA, MAP);
        } catch (GenbatisException e) {
            e.printStackTrace();
        }
    }
}
