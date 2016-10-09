package com.enchantment.genbatis.core;

/**
 * <p>Generated classes information</p>
 *
 * Created by liushuang on 10/9/16.
 */
public class GenerInfo {
    /** Database schema which contains generated tables */
    private String schema;
    /** The prefix of table name, like T_ of T_User */
    private String tablePrefix;
    /** The suffix of table name, like _VIEW of T_USER_VIEW */
    private String tableSuffix;
    /** Generated table */
    private String table;
    /** The parent package of generated classes */
    private String basePackage;
    /** Service classes package */
    private String servicePkg = "service";
    /** Controller classes package */
    private String controllerPkg = "controller";
    /** Persistence classes package */
    private String domainPkg = "domain";
    /** DAO classes package */
    private String daoPkg = "dao";
    /** Mapper classes package */
    private String mapperPkg;
    /** MyBatis mapper type */
    private MAPPER_TYP mapperType = MAPPER_TYP.XML;

    /**
     * The enumerations of MyBatis configuration's type.<br/>
     * <ul>
     *   <li><code>XML</code> use mapper.xml file</li>
     *   <li><code>ANNOTATION</code> use java class file with anntations</li>
     * </ul>
     */
    public enum MAPPER_TYP {
        XML,
        ANNOTATION
    }

    /**
     * Database schema which contains generated tables.
     *
     * @param schema database schema name
     */
    public void setSchema(String schema) {
        this.schema = schema;
    }

    public void setTablePrefix(String tablePrefix) {
        this.tablePrefix = tablePrefix;
    }

    public void setTableSuffix(String tableSuffix) {
        this.tableSuffix = tableSuffix;
    }

    /**
     * Generated table.<br/>
     * If <code>tables</code> field is indicated, this field will not work.
     *
     * @param table table name
     */
    public void setTable(String table) {
        this.table = table;
    }

    /**
     * The parent package of generated classes.<br/>
     * If classes need put to different parent packages,
     * this field will be <code>null</code> or empty,
     * and program will use servicePkg, controllerPkg,
     * domainPkg, daoPkg, so these fields required to
     * indicate full package path.
     *
     * @param basePackage
     */
    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    /**
     * Service classes package.<br/>
     * If <code>basePackage</code> is not indicated,
     * this field should be full package path.
     *
     * @param servicePkg service package path
     */
    public void setServicePkg(String servicePkg) {
        this.servicePkg = servicePkg;
    }

    /**
     * Controller classes package.<br/>
     * If <code>basePackage</code> is not indicated,
     * this field should be full package path.
     *
     * @param controllerPkg controller package path
     */
    public void setControllerPkg(String controllerPkg) {
        this.controllerPkg = controllerPkg;
    }

    /**
     * Persistence classes package.<br/>
     * If <code>basePackage</code> is not indicated,
     * this field should be full package path.
     *
     * @param domainPkg persistence package path
     */
    public void setDomainPkg(String domainPkg) {
        this.domainPkg = domainPkg;
    }

    /**
     * DAO classes package.<br/>
     * If <code>basePackage</code> is not indicated,
     * this field should be full package path.
     *
     * @param daoPkg dao package path
     */
    public void setDaoPkg(String daoPkg) {
        this.daoPkg = daoPkg;
    }

    /**
     * Mapper classes package.<br/>
     * If <code>basePackage</code> is not indicated,
     * this field should be full package path.
     *
     * @param mapperPkg mapper classes package path
     */
    public void setMapperPkg(String mapperPkg) {
        this.mapperPkg = mapperPkg;
    }

    /**
     * MyBatis configuration type.<br/>
     *
     * @see MAPPER_TYP
     * @param mapperType enumerations of MAPPER_TYP
     */
    public void setMapperType(MAPPER_TYP mapperType) {
        this.mapperType = mapperType;
    }

    public String getSchema() {
        return schema;
    }

    public String getTablePrefix() {
        return tablePrefix;
    }

    public String getTableSuffix() {
        return tableSuffix;
    }

    public String getTable() {
        return table;
    }

    public String getBasePackage() {
        return basePackage;
    }

    public String getServicePkg() {
        return servicePkg;
    }

    public String getControllerPkg() {
        return controllerPkg;
    }

    public String getDomainPkg() {
        return domainPkg;
    }

    public String getDaoPkg() {
        return daoPkg;
    }

    public String getMapperPkg() {
        return mapperPkg;
    }

    public MAPPER_TYP getMapperType() {
        return mapperType;
    }

    private String name;
    public String getName() {
        if (name != null)
            return name;

        name = table.toLowerCase();
        if (tablePrefix != null && tablePrefix.length() > 0) {
            name = name.replaceFirst("^".concat(tablePrefix.toLowerCase()), "");
        }
        if (tableSuffix != null && tableSuffix.length() > 0) {
            name = name.replaceFirst(tablePrefix.toLowerCase().concat("$"), "");
        }
        return name;
    }
}
