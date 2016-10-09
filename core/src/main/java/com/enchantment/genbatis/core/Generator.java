package com.enchantment.genbatis.core;

import org.springframework.util.StringUtils;

import java.io.File;
import java.sql.Connection;

import static com.enchantment.genbatis.core.GenerInfo.MAPPER_TYP.ANNOTATION;
import static java.io.File.separator;

/**
 * Generate code and output.
 *
 * Created by liushuang on 10/9/16.
 */
public class Generator {
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

    public void gen(TYPE... types) throws GenbatisException {
        try {
            init();
        } catch (Throwable e) {
            throw new GenbatisException(e);
        }
    }

    private void init() {
        mkdir(dest, "");

        String base = info.getBasePackage();
        if (!StringUtils.isEmpty(base)) {
            base = mkdir(dest, info.getBasePackage());
        } else {
            base = dest;
        }

        if (!StringUtils.isEmpty(info.getControllerPkg())) {
            mkdir(base, info.getControllerPkg());
        }

        if (!StringUtils.isEmpty(info.getServicePkg())) {
            mkdir(base, info.getServicePkg());
        }

        if (!StringUtils.isEmpty(info.getDaoPkg())) {
            mkdir(base, info.getDaoPkg());
        }

        if (!StringUtils.isEmpty(info.getDomainPkg())) {
            mkdir(base, info.getDomainPkg());
        }

        if (!StringUtils.isEmpty(info.getMapperPkg())) {
            if (ANNOTATION.equals(info.getMapperType()))
                mkdir(base, info.getMapperPkg());
            else
                mkdir(dest, info.getMapperPkg());
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
}
