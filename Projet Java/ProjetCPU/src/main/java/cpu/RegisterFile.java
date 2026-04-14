package cpu;

/**
 * Représente les 16 registres généraux 8 bits du CPU.
 *
 * <p>Chaque registre stocke une valeur de type {@code byte} (−128 à 127).
 * Les registres sont numérotés de 0 à 15 (r0 à r15).
 * Tout accès avec un index hors plage lève une {@link IllegalArgumentException}.</p>
 *
 * @author Projet CPU
 * @version 1.0
 */
public class RegisterFile {

    /** Nombre de registres disponibles. */
    public static final int NUM_REGISTERS = 16;

    /** Tableau interne des registres. */
    private final byte[] registers;

    /**
     * Crée un banc de 16 registres initialisés à zéro.
     */
    public RegisterFile() {
        registers = new byte[NUM_REGISTERS];
    }

    /**
     * Lit la valeur d'un registre.
     *
     * @param index numéro du registre (0 à 15)
     * @return la valeur byte contenue dans ce registre
     * @throws IllegalArgumentException si l'index est hors limites
     */
    public byte get(int index) {
        checkIndex(index);
        return registers[index];
    }

    /**
     * Écrit une valeur dans un registre.
     *
     * @param index numéro du registre (0 à 15)
     * @param value valeur à stocker
     * @throws IllegalArgumentException si l'index est hors limites
     */
    public void set(int index, byte value) {
        checkIndex(index);
        registers[index] = value;
    }

    /**
     * Remet tous les registres à zéro.
     */
    public void reset() {
        java.util.Arrays.fill(registers, (byte) 0);
    }

    /**
     * Retourne le nombre total de registres.
     *
     * @return {@value #NUM_REGISTERS}
     */
    public int getCount() {
        return NUM_REGISTERS;
    }

    /**
     * Affiche l'état de tous les registres sous forme lisible.
     *
     * @return une chaîne représentant les valeurs de tous les registres
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Registres:\n");
        for (int i = 0; i < NUM_REGISTERS; i++) {
            sb.append(String.format("  r%-2d = %4d  (0x%02X)%n",
                i, registers[i], registers[i] & 0xFF));
        }
        return sb.toString();
    }

    /**
     * Vérifie que l'index de registre est valide.
     *
     * @param index l'index à vérifier
     * @throws IllegalArgumentException si l'index est hors plage
     */
    private void checkIndex(int index) {
        if (index < 0 || index >= NUM_REGISTERS) {
            throw new IllegalArgumentException(
                "Index de registre invalide : " + index + " (plage 0-" + (NUM_REGISTERS - 1) + ")");
        }
    }
}
