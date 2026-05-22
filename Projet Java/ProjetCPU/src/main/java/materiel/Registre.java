package materiel;

/**
 * Registre du CPU, identifié par un numéro (0 à 15).
 * Stocke une valeur sur un octet signé, initialisée à zéro à la construction.
 */
public class Registre {
    private int numero;
    private byte valeur;

    /**
     * Crée un registre avec son numéro ; la valeur initiale est zéro.
     *
     * @param numero le numéro du registre (0 à 15)
     */
    public Registre(int numero) {
        this.numero = numero;
        this.valeur = 0;
    }

    /**
     * Retourne la valeur actuelle du registre.
     *
     * @return la valeur stockée dans le registre
     */
    public byte lire() {
        return valeur;
    }

    /**
     * Écrit une valeur dans le registre.
     *
     * @param val la valeur à stocker
     */
    public void ecrire(byte val) {
        this.valeur = val;
    }

    /**
     * Retourne le numéro du registre.
     *
     * @return le numéro du registre (0 à 15)
     */
    public int getNumero() {
        return numero;
    }
}
