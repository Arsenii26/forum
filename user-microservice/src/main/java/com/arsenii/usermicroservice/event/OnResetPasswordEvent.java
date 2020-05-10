package com.arsenii.usermicroservice.event;

import com.arsenii.usermicroservice.model.User;
import org.springframework.context.ApplicationEvent;

import java.util.Locale;

public class OnResetPasswordEvent extends ApplicationEvent {
    private String appUrl;
    private String notEncryptPassw;
    private Locale locale;
    private User user;

    public OnResetPasswordEvent(
            User user, Locale locale, String appUrl, String notEncryptPassw) {
        super(user);

        this.user = user;
        this.locale = locale;
        this.appUrl = appUrl;
        this.notEncryptPassw = notEncryptPassw;
    }

    // standard getters and setters

    public String getAppUrl() {
        return appUrl;
    }

    public void setAppUrl(String appUrl) {
        this.appUrl = appUrl;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getNotEncryptPassw() {
        return notEncryptPassw;
    }

    public void setNotEncryptPassw(String notEncryptPassw) {
        this.notEncryptPassw = notEncryptPassw;
    }
}