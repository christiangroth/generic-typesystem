package de.chrgroth.generictypesystem.validation;

import java.util.Arrays;

/**
 * Represents a validation error with path and message information.
 *
 * @author Christian Groth
 */
public class ValidationError {

    private final String path;
    private final ValidationMessageKey messageKey;
    private final Object[] messageParameters;

    /**
     * Creates a new error instance.
     *
     * @param path
     *            the message path
     * @param messageKey
     *            the message key
     * @param messageParameters
     *            the message parameters
     */
    public ValidationError(String path, ValidationMessageKey messageKey, Object... messageParameters) {
        this.path = path != null ? path.trim() : "";
        this.messageKey = messageKey;
        this.messageParameters = messageParameters;
    }

    /**
     * Returns the message path.
     *
     * @return message path
     */
    public String getPath() {
        return path;
    }

    /**
     * Returns the message key.
     *
     * @return message key
     */
    public ValidationMessageKey getMessageKey() {
        return messageKey;
    }

    /**
     * Returns the message parameters
     *
     * @return message parameters
     */
    public Object[] getMessageParameters() {
        return Arrays.asList(messageParameters).toArray();
    }

    @Override
    public String toString() {
        return "ValidationError [path=" + path + ", messageKey=" + messageKey + ", messageParameters=" + Arrays.toString(messageParameters) + "]";
    }
}
