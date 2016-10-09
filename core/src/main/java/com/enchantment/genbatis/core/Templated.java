package com.enchantment.genbatis.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Vector;

import static com.enchantment.genbatis.core.Generator.TYPE.*;

/**
 * <p>Generate code with templates.</p>
 * This class can generate code of controllers, services, daoes,
 * persistence entities and MyBatis mappers (include xml configuration
 * and java class with annotation).
 *
 * Created by liushuang on 9/30/16.
 */
class Templated {
    private static final Logger L = LoggerFactory.getLogger(Templated.class);

    private GenerInfo info;

    Templated(GenerInfo info) {
        this.info = info;
    }

    String templatedService() {
        return Template.getServiceTemplate()
                // insert package path
                .replaceFirst("\\$pkg", getPackage(SER))
                // insert dao import
                .replaceFirst("\\$dao", getPackage(DAO).concat(".")
                        .concat(upperName(info.getName())).concat("DAO"))
                // insert domain import
                .replaceFirst("\\$domain", getPackage(DMA).concat(".")
                        .concat(upperName(info.getName())))
                // insert class name
                .replaceAll("\\$name", upperName(info.getName()))
                // insert current time
                .replaceFirst("\\$date", currentTime())
                // insert lower case name
                .replaceAll("\\$lname", info.getName());
    }

    String templatedController() {
        return Template.getControllerTemplate()
                // insert package path
                .replaceFirst("\\$pkg", getPackage(CON))
                // insert service import
                .replaceFirst("\\$service", getPackage(SER).concat(".")
                        .concat(upperName(info.getName())).concat("Service"))
                // insert domain import
                .replaceFirst("\\$domain", getPackage(DMA).concat(".")
                        .concat(upperName(info.getName())))
                // insert class name
                .replaceAll("\\$name", upperName(info.getName()))
                // insert current time
                .replaceFirst("\\$date", currentTime())
                // insert lower case name
                .replaceAll("\\$lname", info.getName());
    }

    String templatedDAO() {
        return Template.getDAOTemplate()
                // insert package path
                .replaceFirst("\\$pkg", getPackage(DAO))
                // insert domain import
                .replaceFirst("\\$domain", getPackage(DMA).concat(".")
                        .concat(upperName(info.getName())))
                // insert class name
                .replaceAll("\\$name", upperName(info.getName()))
                // insert current time
                .replaceFirst("\\$date", currentTime())
                // insert lower case name
                .replaceAll("\\$lname", info.getName());
    }

    private LinkedHashMap<String, String> cols;

    String templatedDomain(Connection conn) {
        if (cols == null) {
            SimpleStorage storage = new SimpleStorage(conn);
            cols = storage.getColumns(info.getSchema(), info.getTable());
        }

        HashSet<String> imports = new HashSet<>();
        StringBuffer fields = new StringBuffer("");
        StringBuffer gs = new StringBuffer("");

        cols.keySet().forEach(key -> {
            String className = cols.get(key);
            String type = Template.TYP_MAP.get(className);
            String uKey = upperName(key);

            fields.append("    private ")
                    .append(type).append(" ")
                    .append(key).append(";\n");

            gs.append(Template.gstter().replaceAll("\\$type", type)
                    .replaceAll("\\$name", key)
                    .replaceAll("\\$uname", uKey));

            if (!className.startsWith("java.lang"))
                imports.add("import ".concat(className).concat(";\n"));
            else if (className.matches("java\\.lang\\.(?:Double|Float)"))
                imports.add("import java.math.BigDecimal;");
        });

        StringBuffer ims = new StringBuffer();
        imports.forEach(im -> ims.append(im));

        return Template.getDomainTemplate()
                // insert imports
                .replaceFirst("\\$import\n", ims.toString())
                // insert package path
                .replaceFirst("\\$pkg", getPackage(DMA))
                // insert fields
                .replaceFirst(" *\\$field\n", fields.toString())
                // insert getters and setters
                .replaceFirst(" *\\$get&set\n", gs.toString())
                // insert class name
                .replaceAll("\\$name", upperName(info.getName()))
                // insert current time
                .replaceFirst("\\$date", currentTime());
    }

    String templatedXmlMapper(Connection conn) {
        if (cols == null) {
            SimpleStorage storage = new SimpleStorage(conn);
            cols = storage.getColumns(info.getSchema(), info.getTable());
        }

        StringBuffer columns = new StringBuffer();
        StringBuffer insert = new StringBuffer();
        StringBuffer update = new StringBuffer();

        cols.keySet().forEach(key -> {
            String col = key.toLowerCase();
            columns.append(col).append(",");
            insert.append("#{").append(col).append("},");
            update.append("            ").append(col)
                    .append("=#{").append(col).append("},\n");
        });

        return Template.getXmlMapperTemplate()
                // insert class name
                .replaceAll("\\$name", upperName(info.getName()))
                // insert lower case name
                .replaceAll("\\$lname", info.getName())
                // replace table name
                .replaceAll("\\$table", info.getTable())
                // replace domain reference
                .replaceAll("\\$domain", getPackage(DMA).concat(".")
                        .concat(upperName(info.getName())))
                .replaceAll("\\$cols", columns.toString().replaceFirst(",$", ""))
                .replaceAll("\\$insert", insert.toString().replaceFirst(",$", ""))
                .replaceAll(" *\\$update\n", update.toString().replaceFirst(",$", ""));
    }

    private String getPackage(Generator.TYPE type) {
        String pkg = info.getBasePackage();
        if (pkg == null)
            pkg = "";
        else if (!pkg.endsWith("."))
            pkg = pkg.concat(".");

        switch (type) {
            case SER:
                return pkg.concat(info.getServicePkg());
            case CON:
                return pkg.concat(info.getControllerPkg());
            case DAO:
                return pkg.concat(info.getDaoPkg());
            case DMA:
                return pkg.concat(info.getDomainPkg());
            case MAP:
                if (GenerInfo.MAPPER_TYP.XML.equals(info.getMapperType()))
                    return null;
                return pkg.concat(info.getMapperPkg());
            default:
                return null;
        }
    }

    private String upperName(String name) {
        return name.substring(0, 1).toUpperCase()
                .concat(name.substring(1));
    }

    private static final SimpleDateFormat DT_FMT =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private String currentTime() {
        return DT_FMT.format(new Date());
    }

    private static class Template {
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
        private static String domain;
        private static String xmlMapper;

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

        static String getDomainTemplate() {
            if (domain == null)
                domain = read("domain_tpl");
            return domain;
        }

        private static final String GET_SET =
                "    public $type get$uname() {\n".concat(
                "        return $name;\n").concat(
                "    }\n\n").concat(
                "    public void set$uname($type $name) {\n").concat(
                "        this.$name = $name;\n").concat(
                "    }\n\n");
        /**
         * Getter and setter methods template
         *
         * @return
         */
        static String gstter() {
            return GET_SET;
        }

        static String getXmlMapperTemplate() {
            if (xmlMapper == null)
                xmlMapper = read("mapper_xml_tpl");
            return xmlMapper;
        }
    }
}
