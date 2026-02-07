
public class ValidationResult {
    private final boolean valid;
    private final String message;

    /**
     * 
     *
     * @param valid true si el moviment es valid
     * @param message missatge descriptiu (pot ser buit si valid)
     */
    public ValidationResult(boolean valid, String message) {
        this.valid = valid;
        this.message = message;
    }

    /**
     * @return true si el moviment es valid
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * @return missatge de validacio (motiu d'error si invalid)
     */
    public String getMessage() {
        return message;
    }

    /**
     * Creador de resultat valid.
     */
    public static ValidationResult ok() {
        return new ValidationResult(true, "OK");
    }

    /**
     * Creador de resultat invalid.
     *
     * @param msg motiu concret
     */
    public static ValidationResult fail(String msg) {
        return new ValidationResult(false, msg);
    }
}
