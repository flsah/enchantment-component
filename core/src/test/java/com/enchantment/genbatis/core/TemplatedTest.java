package com.enchantment.genbatis.core;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by liushuang on 10/8/16.
 */
public class TemplatedTest {
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
        info.setTable("t_group");
        info.setTablePrefix("t_");

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

//    @Test
    public void templatedServiceTest() {
        Templated tpd = new Templated(info);

        String tpl = tpd.templatedService();
        System.out.println(tpl);
    }

    @Test
    public void templatedControllerTest() {
        Templated tpd = new Templated(info);

        String tpl = tpd.templatedController();
        System.out.println(tpl);
    }

//    @Test
    public void templatedDaoTest() {
        Templated tpd = new Templated(info);

        String tpl = tpd.templatedDAO();
        System.out.println(tpl);
    }

//    @Test
    public void templatedDomainTest() {
        Templated tpd = new Templated(info);
        String tpl = tpd.templatedDomain(conn);
        System.out.println(tpl);
    }

//    @Test
    public void templatedXmlMapperTest() {
        Templated tpd = new Templated(info);
        String tpl = tpd.templatedXmlMapper(conn);
        System.out.println(tpl);
    }
}
