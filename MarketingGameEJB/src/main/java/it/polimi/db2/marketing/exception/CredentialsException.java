package it.polimi.db2.marketing.exception;

import javax.ejb.ApplicationException;

@ApplicationException
public class CredentialsException extends RuntimeException  {
    public CredentialsException(String message) {
        super("Could not verify credentials");
    }
}
