package com.enchantment.genbatis.core;

/**
 * Created by liushuang on 10/9/16.
 */
public class GenbatisException extends Throwable {
    public GenbatisException() {
        super();
    }

    public GenbatisException(String message) {
        super(message);
    }

    public GenbatisException(String message, Throwable cause) {
        super(message, cause);
    }

    public GenbatisException(Throwable cause) {
        super(cause);
    }

    protected GenbatisException(String message, Throwable cause,
              boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
