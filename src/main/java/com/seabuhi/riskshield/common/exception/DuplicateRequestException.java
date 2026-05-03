package com.seabuhi.riskshield.common.exception;

public class DuplicateRequestException extends RuntimeException {
    public DuplicateRequestException(String idempotencyKey) {
        super("Bu əməliyyat artıq icra edilib (Idempotency-Key: " + idempotencyKey + ")");
    }
}



