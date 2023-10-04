package it.polimi.db2.marketing.exception;

import javax.ejb.ApplicationException;

@ApplicationException
public class ProductException extends RuntimeException   {
    public ProductException(String message) {
        super(message);
    }
}
