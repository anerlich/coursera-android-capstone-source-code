package org.magnum.mobilecloud.integration.test;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

public class SymptomCheckerAuthenticator extends Authenticator {
    public PasswordAuthentication getPasswordAuthentication () {
        return new PasswordAuthentication ("coursera", "changeit".toCharArray());
    }

}
