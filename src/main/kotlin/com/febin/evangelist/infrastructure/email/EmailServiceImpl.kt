package com.febin.evangelist.infrastructure.email

import com.febin.evangelist.application.service.EmailService
import com.febin.evangelist.domain.model.User
import jakarta.mail.internet.MimeMessage
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service
import org.thymeleaf.context.Context
import org.thymeleaf.spring6.SpringTemplateEngine

@Service
class EmailServiceImpl(
    private val mailSender: JavaMailSender,
    private val templateEngine: SpringTemplateEngine
) : EmailService {

    @Value("\${app.base-url}")
    private lateinit var appBaseUrl: String

    override fun sendVerificationEmail(user: User) {
        val context = Context().apply {
            setVariable("user", user)
            setVariable("verificationUrl", "${appBaseUrl}/api/auth/verify?code=${user.verificationCode}")
        }

        val mail: MimeMessage = mailSender.createMimeMessage()
        val helper = MimeMessageHelper(mail, true)

        val htmlContent = templateEngine.process("verification-email", context)

        helper.setTo(user.username)
        helper.setSubject("Verify Your Account")
        helper.setText(htmlContent, true)

        mailSender.send(mail)
    }

    override fun sendPasswordResetEmail(user: User) {
        val context = Context().apply {
            setVariable("user", user)
            // Note: This URL will likely point to your frontend application
            setVariable("resetUrl", "${appBaseUrl}/password-reset?token=${user.passwordResetToken}")
        }

        val mail: MimeMessage = mailSender.createMimeMessage()
        val helper = MimeMessageHelper(mail, true)

        val htmlContent = templateEngine.process("password-reset-email", context)

        helper.setTo(user.username)
        helper.setSubject("Reset Your Password")
        helper.setText(htmlContent, true)

        mailSender.send(mail)
    }
}
