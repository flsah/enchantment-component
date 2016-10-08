package com.enchantment.genbatis.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.sql.Connection;
import java.util.*;

/**
 * Created by liushuang on 9/30/16.
 */
public class Templated {
    private static final Logger L = LoggerFactory.getLogger(Templated.class);

    private String schema;
    private String table;

    public Templated(String schema, String table) {
        this.schema = schema;
        this.table = table;
    }

    public String templatedService() {
        final String serviceTpl = Template.getServiceTemplate();

        return serviceTpl;
    }

    public String templatedController() {
        final String controllerTpl = Template.getControllerTemplate();

        return controllerTpl;
    }

    public String templatedDAO() {
        final String daoTpl = Template.getDAOTemplate();

        return daoTpl;
    }

    public String templatedDomain(Connection conn) {
        SimpleStorage storage = new SimpleStorage(conn);
        HashMap<String, String> cols = storage.getColumns(schema, table);

        HashSet<String> imports = new HashSet<>();
        StringBuffer fields = new StringBuffer("");
        StringBuffer gs = new StringBuffer("");

        cols.keySet().forEach(key -> {
            String className = cols.get(key);
            String type = Template.TYP_MAP.get(className);
            String uKey = key.substring(0, 1).toUpperCase().concat(key.substring(1));

            fields.append("private ")
                    .append(type).append(" ")
                    .append(key).append(";\n");

            gs.append(Template.gstter().replaceAll("\\$type", type)
                    .replaceAll("\\$name", key)
                    .replaceAll("\\$uname", uKey));

            if (!className.startsWith("java.lang"))
                imports.add("import ".concat(className).concat(";\n"));
            else if (className.endsWith("Double") || className.endsWith("Float"))
                imports.add("import java.math.BigDecimal;");
        });

        StringBuffer domain = new StringBuffer("");

        imports.forEach(im -> domain.append(im));
        domain.append("\n").append(fields)
                .append("\n").append(gs);

        return domain.toString();
    }

    protected static class Template {
        private static final Logger L = LoggerFactory.getLogger(Template.class);

        static final HashMap<String, String> TYP_MAP = new HashMap<>();
        static {
            TYP_MAP.put("java.lang.String",   "String");
            TYP_MAP.put("java.lang.Integer",  "int");
            TYP_MAP.put("java.lang.Double",   "BigDecimal");
            TYP_MAP.put("java.lang.Float",    "BigDecimal");
            TYP_MAP.put("java.sql.Blob",      "Blob");
            TYP_MAP.put("java.sql.Clob",      "Clob");
            TYP_MAP.put("java.sql.Date",      "Date");
            TYP_MAP.put("java.sql.Timestamp", "Timestamp");
        }

        private static String service;
        private static String controller;
        private static String dao;

        private static synchronized String read(String tplName) {
            String path = "/META-INF/".concat(tplName);

            try (BufferedInputStream tpl = new BufferedInputStream(
                    Template.class.getResourceAsStream(path))) {
                byte[] buf = new byte[2048];
                Vector<Byte> bufList = new Vector<>();

                while (tpl.read(buf) > -1) {
                    for (byte b : buf) {
                        if (b == 0)
                            break;
                        bufList.addElement(b);
                    }
                }

                buf = null;
                buf = new byte[bufList.size()];
                int i = 0;
                for (byte b : bufList) {
                    buf[i++] = b;
                }

                return new String(buf);
            } catch (IOException e) {
                L.error("Read template file [".concat(path)
                        .concat("] ocurred error."), e);
            }

            return null;
        }

        static String getServiceTemplate() {
            if (service == null)
                service = read("service_tpl");
            return service;
        }

        static String getControllerTemplate() {
            if (controller == null)
                controller = read("controller_tpl");
            return controller;
        }

        static String getDAOTemplate() {
            if (dao == null)
                dao = read("dao_tpl");
            return dao;
        }

        private static final String GET_SET =
                "public $type get$uname() {\n".concat(
                "    return $name;\n").concat(
                "}\n\n").concat(
                "public void set$uname($type $name) {\n").concat(
                "    this.$name = $name;\n").concat(
                "}\n\n");
        /**
         * Getter and setter methods template
         *
         * @return
         */
        static String gstter() {
            return GET_SET;
        }
    }
}
