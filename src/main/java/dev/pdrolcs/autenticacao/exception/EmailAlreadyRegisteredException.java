package dev.pdrolcs.autenticacao.exception;

public class EmailAlreadyRegisteredException extends RuntimeException {

    public EmailAlreadyRegisteredException(String message) {
        super(message);
    }

    public EmailAlreadyRegisteredException() {
        super("Email already registered");
    }
}
