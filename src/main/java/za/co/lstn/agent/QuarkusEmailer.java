package za.co.lstn.agent;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import jakarta.inject.Inject;

public class QuarkusEmailer {

    @Inject
    Mailer mailer;
}
