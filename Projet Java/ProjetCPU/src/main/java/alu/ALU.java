package alu;

/**
 * Unité Arithmétique et Logique (ALU) du simulateur de CPU.
 * <p>L'ALU fournit des méthodes statiques pour effectuer des opérations
 * <p>Cette classe regroupe toutes les opérations mathématiques et
 * bit-à-bit supportées par le processeur. Chaque opération travaille
 * sur des valeurs {@code byte} (8 bits signés, −128 à 127).</p>
 *
 * <p>Remarques importantes :</p>
 * <ul>
 *   <li>La multiplication peut produire un résultat sur 16 bits ;
 *       elle est donc scindée en partie haute et basse.</li>
 *   <li>La division par zéro lève une {@link ArithmeticException}.</li>
 *   <li>Les dépassements de capacité (overflow) se comportent comme
 *       en Java : le résultat est tronqué à 8 bits.</li>
 * </ul>
 *
 * @author Projet CPU
 * @version 1.0
 */
public final class ALU {

    /** Classe utilitaire non instanciable. */
    private ALU() {}

    // ---------------------------------------------------------------
    // Opérations arithmétiques
    // ---------------------------------------------------------------

    /**
     * Additionne deux bytes. Le résultat est tronqué à 8 bits.
     *
     * @param a premier opérande
     * @param b deuxième opérande
     * @return {@code (byte)(a + b)}
     */
    public static byte add(byte a, byte b) {
        return (byte) (a + b);
    }

    /**
     * Soustrait {@code b} de {@code a}. Le résultat est tronqué à 8 bits.
     *
     * @param a premier opérande
     * @param b deuxième opérande
     * @return {@code (byte)(a - b)}
     */
    public static byte subtract(byte a, byte b) {
        return (byte) (a - b);
    }

    /**
     * Multiplie deux bytes. Le résultat peut dépasser 8 bits.
     * Utilisez {@link #multiplyHigh} et {@link #multiplyLow} pour
     * récupérer les deux octets du résultat 16 bits.
     *
     * @param a premier opérande
     * @param b deuxième opérande
     * @return le résultat entier 16 bits de {@code a * b}
     */
    public static int multiply(byte a, byte b) {
        return (int) a * (int) b;
    }

    /**
     * Retourne l'octet de poids fort du résultat de {@code a * b}.
     *
     * @param a premier opérande
     * @param b deuxième opérande
     * @return octet haut du produit
     */
    public static byte multiplyHigh(byte a, byte b) {
        int result = multiply(a, b);
        return (byte) ((result >> 8) & 0xFF);
    }

    /**
     * Retourne l'octet de poids faible du résultat de {@code a * b}.
     *
     * @param a premier opérande
     * @param b deuxième opérande
     * @return octet bas du produit
     */
    public static byte multiplyLow(byte a, byte b) {
        int result = multiply(a, b);
        return (byte) (result & 0xFF);
    }

    /**
     * Retourne le quotient entier de la division {@code a / b}.
     *
     * @param a dividende
     * @param b diviseur (ne doit pas être 0)
     * @return quotient de la division entière
     * @throws ArithmeticException si {@code b == 0}
     */
    public static byte divideQuotient(byte a, byte b) {
        if (b == 0) {
            throw new ArithmeticException("Division par zéro");
        }
        return (byte) (a / b);
    }

    /**
     * Retourne le reste de la division entière {@code a % b}.
     *
     * @param a dividende
     * @param b diviseur (ne doit pas être 0)
     * @return reste de la division entière
     * @throws ArithmeticException si {@code b == 0}
     */
    public static byte divideRemainder(byte a, byte b) {
        if (b == 0) {
            throw new ArithmeticException("Division par zéro");
        }
        return (byte) (a % b);
    }

    // ---------------------------------------------------------------
    // Opérations logiques bit-à-bit
    // ---------------------------------------------------------------

    /**
     * Effectue un OU binaire bit-à-bit entre {@code a} et {@code b}.
     *
     * @param a premier opérande
     * @param b deuxième opérande
     * @return {@code (byte)(a | b)}
     */
    public static byte or(byte a, byte b) {
        return (byte) (a | b);
    }

    /**
     * Effectue un ET binaire bit-à-bit entre {@code a} et {@code b}.
     *
     * @param a premier opérande
     * @param b deuxième opérande
     * @return {@code (byte)(a & b)}
     */
    public static byte and(byte a, byte b) {
        return (byte) (a & b);
    }

    /**
     * Effectue un OU exclusif (XOR) bit-à-bit entre {@code a} et {@code b}.
     *
     * @param a premier opérande
     * @param b deuxième opérande
     * @return {@code (byte)(a ^ b)}
     */
    public static byte xor(byte a, byte b) {
        return (byte) (a ^ b);
    }
}
