package com.enchantment.genbatis.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.sql.Connection;

/**
 * Created by liushuang on 9/30/16.
 */
public class Templated {
    private static final Logger L = LoggerFactory.getLogger(Templated.class);

    private String schema;
    private String table;
    private Connection conn;

    public Templated(String schema, String table) {
        this.schema = schema;
        this.table = table;
    }

    public void setConnection(Connection conn) {
        this.conn =  conn;
    }

    public OutputStream templatedService() {

        return null;
    }

    public OutputStream templatedController() {
        return null;
    }

    public OutputStream templatedDAO() {
        return null;
    }

    public OutputStream templatedDomain() {
        return null;
    }

    protected static class Template {
        private static final String service = new String("");
        private static final String controller = new String("");
        private static final String dao = new String("");

        static String getServiceTemplate() {
            return service;
        }

        static String getControllerTemplate() {
            return controller;
        }

        static String getDAOTemplate() {
            return dao;
        }
    }
}
