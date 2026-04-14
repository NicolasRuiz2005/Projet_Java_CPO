package cpu;

/**
 * Représente la mémoire principale du CPU : 64 Ko (65 536 octets).
 *
 * <p>La mémoire est un tableau de bytes indexé de 0 à 65 535.
 * Elle stocke à la fois le programme à exécuter et les données.
 * Tout accès hors limites lève une {@link IllegalArgumentException}.</p>
 *
 * @author Projet CPU
 * @version 1.0
 */
public class Memory {

    /** Taille totale de la mémoire en octets (64 Ko). */
    public static final int SIZE = 65536;

    /** Tableau interne stockant les octets de la mémoire. */
    private final byte[] data;

    /**
     * Crée une mémoire de 64 Ko initialisée à zéro.
     */
    public Memory() {
        data = new byte[SIZE];
    }

    /**
     * Lit le byte à l'adresse donnée.
     *
     * @param address l'adresse mémoire (0 à 65 535)
     * @return la valeur byte stockée à cette adresse
     * @throws IllegalArgumentException si l'adresse est hors limites
     */
    public byte read(int address) {
        checkAddress(address);
        return data[address];
    }

    /**
     * Écrit un byte à l'adresse donnée.
     *
     * @param address l'adresse mémoire (0 à 65 535)
     * @param value   la valeur à écrire
     * @throws IllegalArgumentException si l'adresse est hors limites
     */
    public void write(int address, byte value) {
        checkAddress(address);
        data[address] = value;
    }

    /**
     * Remet toute la mémoire à zéro.
     */
    public void clear() {
        java.util.Arrays.fill(data, (byte) 0);
    }

    /**
     * Retourne la taille totale de la mémoire.
     *
     * @return {@value #SIZE} octets
     */
    public int getSize() {
        return SIZE;
    }

    /**
     * Vérifie que l'adresse est dans les bornes valides.
     *
     * @param address l'adresse à vérifier
     * @throws IllegalArgumentException si l'adresse est invalide
     */
    private void checkAddress(int address) {
        if (address < 0 || address >= SIZE) {
            throw new IllegalArgumentException(
                "Adresse mémoire invalide : " + address + " (plage 0-" + (SIZE - 1) + ")");
        }
    }
}
