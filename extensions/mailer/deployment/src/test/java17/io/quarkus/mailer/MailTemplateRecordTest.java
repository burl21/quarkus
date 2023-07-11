package io.quarkus.mailer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.mailer.MailTemplate.MailTemplateInstance;
import io.quarkus.qute.CheckedTemplate;
import io.quarkus.test.QuarkusUnitTest;
import io.vertx.ext.mail.MailMessage;
import jakarta.inject.Inject;

public class MailTemplateRecordTest {

    @RegisterExtension
    static final QuarkusUnitTest config = new QuarkusUnitTest()
            .withApplicationRoot(root -> root
                    .addClasses(confirmation.class)
                    .addAsResource("mock-config.properties", "application.properties")
                    .addAsResource(new StringAsset(""
                            + "<html>{name}</html>"), "templates/confirmation.html"));

    @Inject
    MockMailbox mockMailbox;

    @Test
    public void testMailTemplateRecord() {
        new confirmation("Ondrej").to("quarkus-reactive@quarkus.io").from("from-record@quarkus.io").subject("test mailer")
                .send().await().indefinitely();
        assertEquals(1, mockMailbox.getMailMessagesSentTo("quarkus-reactive@quarkus.io").size());
        MailMessage message = mockMailbox.getMailMessagesSentTo("quarkus-reactive@quarkus.io").get(0);
        assertEquals("from-record@quarkus.io", message.getFrom());
        assertEquals("<html>Ondrej</html>", message.getHtml());
    }

    @CheckedTemplate(basePath = "")
    record confirmation(String name) implements MailTemplateInstance {
    }

}
