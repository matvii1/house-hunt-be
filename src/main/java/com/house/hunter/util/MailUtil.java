package com.house.hunter.util;

import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MailUtil {
        private static final String LOGO_PATH = "static/logo.png";
        private static final int VERIFICATION_EXPIRATION_MINUTES = 30;

        public static MimeMessagePreparator buildRegistrationEmail(String recipientEmail, String verificationToken) {
                return mimeMessage -> {
                        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
                        messageHelper.setTo(recipientEmail);
                        messageHelper.setSubject("House Hunter - Confirm your email address");

                        String expirationTime = LocalDateTime.now().plusMinutes(VERIFICATION_EXPIRATION_MINUTES)
                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

                        String message = "<html><body>" +
                                "<img src='cid:logo' alt='House Hunter Logo' style='width: 200px; height: auto;'><br><br>" +
                                "<p>Thank you for registering with House Hunter. Please click on the below link to activate your account.</p>" +
                                "<p><a href='http://localhost:8080/api/v1/user/activate-account/verify?token=" + verificationToken + "'>Verify Email</a></p>" +
                                "<p>This verification link will expire at " + expirationTime + ".</p>" +
                                "<p>If you did not register with House Hunter, please ignore this email.</p>" +
                                "</body></html>";

                        messageHelper.setText(message, true);
                        messageHelper.addInline("logo", new ClassPathResource(LOGO_PATH));
                };
        }

        public static MimeMessagePreparator buildVerificationConfirmationEmail(String recipientEmail) {
                return mimeMessage -> {
                        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
                        messageHelper.setTo(recipientEmail);
                        messageHelper.setSubject("House Hunter - Email Verification Confirmation");

                        String message = "<html><body>" +
                                "<img src='cid:logo' alt='House Hunter Logo' style='width: 200px; height: auto;'><br><br>" +
                                "<p>Your email address has been successfully verified. You can now log in to your account.</p>" +
                                "</body></html>";

                        messageHelper.setText(message, true);
                        messageHelper.addInline("logo", new ClassPathResource(LOGO_PATH));
                };
        }

        public static MimeMessagePreparator buildResetPasswordEmail(String recipientEmail, String resetToken) {
                return mimeMessage -> {
                        MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
                        messageHelper.setTo(recipientEmail);
                        messageHelper.setSubject("House Hunter - Reset your password");

                        String message = "<html><body>" +
                                "<img src='cid:logo' alt='House Hunter Logo' style='width: 200px; height: auto;'><br><br>" +
                                "<p>You have requested to reset your password. Please click on the below link to reset your password.</p>" +
                                "<p><a href='http://localhost:8080/api/users/reset-password?token=" + resetToken + "'>Reset Password</a></p>" +
                                "<p>If you did not request a password reset, please ignore this email.</p>" +
                                "</body></html>";

                        messageHelper.setText(message, true);
                        messageHelper.addInline("logo", new ClassPathResource(LOGO_PATH));
                };
        }

}
