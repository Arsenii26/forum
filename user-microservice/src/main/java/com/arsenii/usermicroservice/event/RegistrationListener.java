package com.arsenii.usermicroservice.event;

import com.arsenii.usermicroservice.model.User;
import com.arsenii.usermicroservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RegistrationListener implements
        ApplicationListener<OnRegistrationCompleteEvent> {

    @Autowired
    private UserService service;

    @Qualifier("messageSource")
    @Autowired
    private MessageSource messages;

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {
        this.confirmRegistration(event);
    }

    // confirmRegistration method will receive the OnRegistrationCompleteEvent,
    // extract all the necessary User information from it,
    // create the verification token, persist it, and then send it as a parameter in the “Confirm Registration” link.
    private void confirmRegistration(OnRegistrationCompleteEvent event) {
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        service.createVerificationToken(user, token);

        String recipientAddress = user.getEmail();
        String subject = "Registration Confirmation";
        String confirmationUrl
//                = event.getAppUrl() + "/registrationConfirm?token=" + token;
                = token;
        String message = "Welcome!\nPlease confirm your account!";

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message + "\r\n" + "http://localhost:4200/confirm/" + confirmationUrl);
//        email.setText(message + "\r\n" + "http://localhost:8765/api/user/service" + confirmationUrl);
        mailSender.send(email);
    }
}