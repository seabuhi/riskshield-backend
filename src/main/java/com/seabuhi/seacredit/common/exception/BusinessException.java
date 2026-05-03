package com.seabuhi.seacredit.common.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final String code;

    public BusinessException(String message) {
        super(message);
        this.code = "INTERNAL_ERROR";
    }

    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
    }
}


