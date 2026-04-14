package cpu;

/**
 * Représente le compteur de programme (PC) du CPU sur 16 bits.
 *
 * <p>Le compteur de programme indique l'adresse en mémoire de la
 * prochaine instruction à exécuter. Sa valeur est comprise entre
 * 0 et 65 535 (adressage 16 bits pour 64 Ko de mémoire).
 * Il est initialisé à 0 et s'incrémente après chaque byte lu.</p>
 *
 * @author Projet CPU
 * @version 1.0
 */
public class ProgramCounter {

    /** Adresse maximale accessible (65 535 = 0xFFFF). */
    public static final int MAX_ADDRESS = 0xFFFF;

    /** Valeur courante du compteur de programme. */
    private int value;

    /**
     * Crée un compteur de programme initialisé à l'adresse 0.
     */
    public ProgramCounter() {
        value = 0;
    }

    /**
     * Retourne l'adresse courante du compteur de programme.
     *
     * @return l'adresse (0 à 65 535)
     */
    public int get() {
        return value;
    }

    /**
     * Positionne le compteur de programme à une adresse donnée.
     * Utilisé pour les instructions de saut (JUMP, BEQ, BNE).
     *
     * @param address la nouvelle adresse (0 à 65 535)
     * @throws IllegalArgumentException si l'adresse est hors plage
     */
    public void set(int address) {
        if (address < 0 || address > MAX_ADDRESS) {
            throw new IllegalArgumentException(
                "Adresse hors plage pour le PC : " + address);
        }
        value = address;
    }

    /**
     * Incrémente le compteur de programme d'une unité.
     * En cas de dépassement (0xFFFF → 0x10000), le PC revient à 0
     * (comportement de wrapping sur 16 bits).
     */
    public void increment() {
        value = (value + 1) & MAX_ADDRESS;
    }

    /**
     * Réinitialise le compteur de programme à 0.
     */
    public void reset() {
        value = 0;
    }

    /**
     * Retourne une représentation textuelle du compteur de programme.
     *
     * @return l'adresse en décimal et en hexadécimal
     */
    @Override
    public String toString() {
        return String.format("PC = %d (0x%04X)", value, value);
    }
}
