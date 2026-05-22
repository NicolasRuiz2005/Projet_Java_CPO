package materiel;

public class Registre {
    private int numero;
    private byte valeur;

    /*
     * Crée un registre avec son numéro, valeur initiale à zéro
     *
     * @param numero Le numéro du registre (0 à 15)
     */
    public Registre(int numero) {

        this.numero = numero;

        this.valeur = 0;

    }

    /*
     * Retourne la valeur actuelle du registre
     *
     * @return La valeur stockée dans le registre
     */
    public byte lire() {

        return valeur;

    }

    /*
     * Écrit une valeur dans le registre
     *
     * @param val La valeur à stocker
     */
    public void ecrire(byte val) {

        this.valeur = val;

    }

    /*
     * Retourne le numéro du registre
     *
     * @return Le numéro du registre (0 à 15)
     */
    public int getNumero() {

        return numero;

    }
}
