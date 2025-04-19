package com.rect.iot.controller;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;


@CrossOrigin
@RestController
@RequiredArgsConstructor
public class EmailSender {
    private final JavaMailSender javaMailSender;

    @GetMapping("/test")    
    public void send() {
        System.out.println("mail");
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("rect.server.iot@gmail.com");
        message.setTo("cibikomberi@gmail.com");
        message.setSubject("cibikomberi@gmail.com");
        message.setText("cibikomberi@gmail.com");
        javaMailSender.send(message);
    }
    public void send( String deviceName, String datastreamName, String value, String[] to) {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper;
        try {
            helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom("rect.server.iot@gmail.com");
            helper.setTo(to);
            helper.setSubject("Device Notification");
    
            String htmlContent = generateHtmlTemplate(deviceName, datastreamName, value);
            helper.setText(htmlContent, true); // true indicates the content is HTML
            System.out.println("mailed");
        } catch (MessagingException e) {
            System.out.println("not mailed");
        }


        javaMailSender.send(message);
        System.out.println("HTML email sent successfully!");
    }

    private String generateHtmlTemplate(String deviceName, String datastreamName, String value) {
        return """
                <html>
                    <head>
                        <style>
                            body {
                                font-family: 'IBM Plex Sans', Arial, sans-serif;
                                margin: 0;
                                padding: 0;
                                background-color: #1a1a1a;
                                color: #e0e0e0;
                                line-height: 1.6;
                            }
                            .container {
                                width: 100%;
                                max-width: 600px;
                                margin: 0 auto;
                                border: 1px solid #333;
                                border-radius: 10px;
                                overflow: hidden;
                                background-color: #2a2a2a;
                            }
                            .header {
                                background-color: #1a1a1a; /* IBM blue */
                                padding: 20px;
                                text-align: center;
                            }
                            .header img {
                                max-width: 80px;
                                vertical-align: middle;
                                border-radius: 50%;
                            }
                            .header h1 {
                                color: #ffffff;
                                font-size: 24px;
                                font-weight: bold;
                                margin: 10px 0 0;
                            }
                            .content {
                                padding: 20px;
                                text-align: center;
                            }
                            .content h2 {
                                font-size: 20px;
                                margin-bottom: 20px;
                                color: #ffffff;
                            }
                            .content p {
                                font-size: 16px;
                                margin: 10px 0;
                                color: #c6c6c6;
                            }
                            .content p span {
                                font-weight: bold;
                                color: #ffffff;
                            }
                            .footer {
                                text-align: center;
                                padding: 10px 20px;
                                font-size: 12px;
                                background-color: #1a1a1a;
                                color: #8d8d8d;
                            }
                            .footer a {
                                color: #0f62fe;
                                text-decoration: none;
                            }
                                body {
                                    background-image: url('blob:https://new.express.adobe.com/1e976d42-e846-4aaa-a2e5-326f775563c5');
                                    background-position: center;
                                }
                                .card {
                                    backdrop-filter: blur(16px) saturate(180%);
                                    -webkit-backdrop-filter: blur(16px) saturate(180%);
                                    background-color: rgba(17, 25, 40, 0.75);
                                    border-radius: 12px;
                                    border: 1px solid rgba(255, 255, 255, 0.125);
                                }
                        </style>
                    </head>
                    <body>
                        <div class="container">
                            <div class="header">
                                <img src="https://via.placeholder.com/80" alt="Rect Logo">
                                <h1>Rect</h1>
                            </div>
                            <div class="content">
                                <h2>Device Notification</h2>
                                <p><span>Device Name:</span> """ + deviceName + """
                                </p>
                                <p><span>Datastream Name:</span> """ + datastreamName + """
                                </p>
                                <p><span>Value:</span> """ + value + """
                                </p>
                            </div>
                            <div class="footer">
                                <p>You are receiving this email because of a device update.</p>
                                <p><a href="#">Unsubscribe</a> | <a href="#">Contact Support</a></p>
                            </div>
                        </div>
                    </body>
                </html>
                """;
    }
    
}
