package com.enchantment.genbatis.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.util.HashMap;

import static com.enchantment.genbatis.core.GenerInfo.MAPPER_TYP.ANNOTATION;
import static com.enchantment.genbatis.core.Generator.TYPE.*;
import static java.io.File.separator;

/**
 * Generate code and output.
 *
 * Created by liushuang on 10/9/16.
 */
public class Generator {
    private static final Logger L = LoggerFactory.getLogger(Generator.class);

    public enum TYPE {
        SER, // service
        CON, // controller
        DAO, // dao
        DMA, // persistence
        MAP  // mapper
    }

    private GenerInfo info;

    public Generator(GenerInfo info) {
        this.info = info;
    }

    private String dest = "./codes";

    public void setDest(String dest) {
        this.dest = dest;
    }

    private Connection conn;

    public void setConn(Connection conn) {
        this.conn = conn;
    }

    Templated templated;

    public void gen(TYPE... types) throws GenbatisException {
        try {
            init();
            templated = new Templated(info);

            for (TYPE t : types) {
                String tpl = template(t);
                output(t, tpl);
            }
        } catch (Throwable e) {
            throw new GenbatisException(e);
        }
    }

    private static final HashMap<TYPE, String> PATH_MAP = new HashMap<>();

    private void init() {
        mkdir(dest, "");

        String base = info.getBasePackage();
        if (!StringUtils.isEmpty(base)) {
            base = mkdir(dest, info.getBasePackage());
        } else {
            base = dest;
        }

        if (!StringUtils.isEmpty(info.getControllerPkg())) {
            PATH_MAP.put(CON, mkdir(base, info.getControllerPkg()));
        }

        if (!StringUtils.isEmpty(info.getServicePkg())) {
            PATH_MAP.put(SER, mkdir(base, info.getServicePkg()));
        }

        if (!StringUtils.isEmpty(info.getDaoPkg())) {
            PATH_MAP.put(DAO, mkdir(base, info.getDaoPkg()));
        }

        if (!StringUtils.isEmpty(info.getDomainPkg())) {
            PATH_MAP.put(DMA, mkdir(base, info.getDomainPkg()));
        }

        if (!StringUtils.isEmpty(info.getMapperPkg())) {
            if (ANNOTATION.equals(info.getMapperType()))
                PATH_MAP.put(MAP, mkdir(base, info.getMapperPkg()));
            else
                PATH_MAP.put(MAP, mkdir(dest, info.getMapperPkg()));
        }
    }

    private String mkdir(final String parent, final String path) {
        String dir = parent;
        if (!dir.endsWith(separator))
            dir = dir.concat(separator);

        dir = dir.concat(path.replaceAll("\\.", separator));

        File destDir = new File(dir);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }

        return dir;
    }

    private String template(TYPE type) {
        switch (type) {
            case SER:
                return templated.templatedService();
            case CON:
                return templated.templatedController();
            case DAO:
                return templated.templatedDAO();
            case DMA:
                return templated.templatedDomain(conn);
            case MAP:
                if (ANNOTATION.equals(info.getMapperType()))
                    return null;  // TODO: implement annotation approach
                else
                    return templated.templatedXmlMapper(conn);
            default:
                return null;
        }
    }

    private void output(TYPE type, String content)
            throws GenbatisException {
        String path = PATH_MAP.get(type).concat(separator)
                .concat(info.getUpperName());
        switch (type) {
            case SER:
                path = path.concat("Service.java");
                break;
            case CON:
                path = path.concat("Controller.java");
                break;
            case DAO:
                path = path.concat("DAO.java");
                break;
            case DMA:
                path = path.concat(".java");
                break;
            case MAP:
                if (ANNOTATION.equals(info.getMapperType()))
                    path = path.concat("Mapper.java");
                else
                    path = path.concat("Mapper.xml");
                break;
            default:
                throw new GenbatisException("Unknown type[" + type + "].");
        }

        File code = new File(path);
        try (FileOutputStream out = new FileOutputStream(code)) {
            out.write(content.getBytes());
            out.flush();
        } catch (IOException e) {
            throw new GenbatisException(e);
        }
    }
}
