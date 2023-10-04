package it.polimi.db2.marketing.exception;

import javax.ejb.ApplicationException;

@ApplicationException
public class AnswerException extends RuntimeException {
    public AnswerException(String message) {
        super(message);
    }
}
