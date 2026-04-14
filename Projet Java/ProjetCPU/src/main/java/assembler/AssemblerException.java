package assembler;

/**
 * Exception levée lors d'une erreur de syntaxe ou de sémantique
 * dans le code assembleur fourni à l'{@link Assembler}.
 *
 * @author Projet CPU
 * @version 1.0
 */
public class AssemblerException extends RuntimeException {

    /**
     * Crée une exception avec un message descriptif de l'erreur.
     *
     * @param message le message d'erreur
     */
    public AssemblerException(String message) {
        super(message);
    }

    /**
     * Crée une exception avec un message et une cause sous-jacente.
     *
     * @param message le message d'erreur
     * @param cause   l'exception originale
     */
    public AssemblerException(String message, Throwable cause) {
        super(message, cause);
    }
}
