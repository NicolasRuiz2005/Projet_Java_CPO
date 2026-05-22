package materiel;

/**
 * Unité Arithmétique et Logique (ALU) du simulateur CPU.
 * Fournit les opérations arithmétiques (addition, soustraction, multiplication, division)
 * et logiques (OU, ET, XOR) sur des octets signés.
 */
public class ALU {

    /**
     * Additionne deux octets signés.
     *
     * @param a le premier opérande
     * @param b le deuxième opérande
     * @return le résultat de a + b tronqué sur 8 bits
     */
    public byte additionner(byte a, byte b) {
        return (byte) (a + b);
    }

    /**
     * Soustrait b de a.
     *
     * @param a le diminuende
     * @param b le diminuteur
     * @return le résultat de a - b tronqué sur 8 bits
     */
    public byte soustraire(byte a, byte b) {
        return (byte) (a - b);
    }

    /**
     * Multiplie deux octets signés ; le résultat est encodé sur 16 bits.
     *
     * @param a le premier opérande
     * @param b le deuxième opérande
     * @return le résultat sur 16 bits : poids fort dans l'octet haut, poids faible dans l'octet bas
     */
    public int multiplier(byte a, byte b) {
        return a * b;
    }

    /**
     * Divise a par b ; le quotient et le reste sont packés dans un entier 16 bits.
     *
     * @param a le dividende
     * @param b le diviseur (ne doit pas être zéro)
     * @return quotient dans l'octet haut, reste dans l'octet bas
     * @throws ArithmeticException si b vaut zéro
     */
    public int diviser(byte a, byte b) {
        if (b == 0) {
            throw new ArithmeticException("Division par zéro, non valide");
        }
        int quotient = a / b;
        int reste = a % b;
        return ((quotient & 0xFF) << 8) | (reste & 0xFF);
    }

    /**
     * Effectue un OU binaire entre deux octets.
     *
     * @param a le premier opérande
     * @param b le deuxième opérande
     * @return le résultat de a | b
     */
    public byte ouBinaire(byte a, byte b) {
        return (byte) (a | b);
    }

    /**
     * Effectue un ET binaire entre deux octets.
     *
     * @param a le premier opérande
     * @param b le deuxième opérande
     * @return le résultat de a & b
     */
    public byte etBinaire(byte a, byte b) {
        return (byte) (a & b);
    }

    /**
     * Effectue un OU exclusif (XOR) entre deux octets.
     *
     * @param a le premier opérande
     * @param b le deuxième opérande
     * @return le résultat de a ^ b
     */
    public byte ouExclusif(byte a, byte b) {
        return (byte) (a ^ b);
    }
}