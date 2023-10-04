package it.polimi.db2.marketing.exception;

import javax.ejb.ApplicationException;

@ApplicationException
public class QuestionnaireException extends RuntimeException  {
    public QuestionnaireException(String message) {
        super(message);
    }
}
