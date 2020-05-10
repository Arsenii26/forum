package com.arsenii.usermicroservice.event;

import com.arsenii.usermicroservice.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class ResetPasswordListener implements
        ApplicationListener<OnResetPasswordEvent> {

    @Autowired
    private JavaMailSender mailSender;


    @Override
    public void onApplicationEvent(OnResetPasswordEvent event) {
        this.resetPassword(event);
    }

    // reset password by email
    private void resetPassword(OnResetPasswordEvent event) {
        User user = event.getUser();

        String recipientAddress = user.getEmail();
        String subject = "Password was changed";

        String message = "Your password was changed!";

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message + "\r\n" + "The new password is: " + event.getNotEncryptPassw() + "\r\nPlease change it after you login");
        mailSender.send(email);
    }
}
