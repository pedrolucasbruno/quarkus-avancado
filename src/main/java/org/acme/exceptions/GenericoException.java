package org.acme.exceptions;

public class GenericoException extends RuntimeException {
    private int statusCode;
    public GenericoException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}
