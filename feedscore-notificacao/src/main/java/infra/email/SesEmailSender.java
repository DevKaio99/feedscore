package infra.email;

import application.gateways.EmailMessage;
import application.gateways.EmailSender;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

import java.util.List;

@ApplicationScoped
public class SesEmailSender implements EmailSender {


        private static final Logger LOG = Logger.getLogger(SesEmailSender.class);
        private static final String CHARSET = "UTF-8";

        private final SesClient sesClient;
        private final String sender;
        private final List<String> recipients;

        public SesEmailSender(SesClient sesClient,
                              @ConfigProperty(name = "feedscore.ses.sender") String sender,
                              @ConfigProperty(name = "feedscore.ses.recipients") List<String> recipients) {
            this.sesClient = sesClient;
            this.sender = sender;
            this.recipients = recipients;
        }

        @Override
        public void send(EmailMessage emailMessage) {
            SendEmailRequest request = SendEmailRequest.builder()
                    .source(sender)
                    .destination(Destination.builder()
                            .toAddresses(recipients)
                            .build())
                    .message(Message.builder()
                            .subject(content(emailMessage.subject()))
                            .body(Body.builder()
                                    .text(content(emailMessage.body()))
                                    .build())
                            .build())
                    .build();

            SendEmailResponse response = sesClient.sendEmail(request);

            LOG.infof("E-mail enviado pelo SES. MessageId: %s", response.messageId());
        }

        private Content content(String text) {
            return Content.builder()
                    .data(text)
                    .charset(CHARSET)
                    .build();
        }
}
