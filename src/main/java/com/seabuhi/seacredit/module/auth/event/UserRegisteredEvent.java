package com.seabuhi.seacredit.module.auth.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class UserRegisteredEvent extends ApplicationEvent {
    private final Long userId;
    private final String email;
    private final String fullName;

    public UserRegisteredEvent(Object source, Long userId, String email, String fullName) {
        super(source);
        this.userId = userId;
        this.email = email;
        this.fullName = fullName;
    }
}


