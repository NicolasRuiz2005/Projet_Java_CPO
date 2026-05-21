package materiel;
public class Memoire {
    private static final int TAILLE = 65536;
    private byte[] cases;

    /*
     * Initialise la mémoire de 65 536 octets, tous à zéro
     */
    public Memoire() {
        this.cases = new byte[TAILLE];
    }

    /*
     * Lit l'octet à l'adresse mémoire donnée
     *
     * @param adr L'adresse mémoire à lire (0 à 65535)
     * @return L'octet stocké à cette adresse
     */
    public byte lire(int adr) {
        return cases[adr];
    }

    /*
     * Écrit un octet à l'adresse mémoire donnée
     *
     * @param adr L'adresse mémoire où écrire (0 à 65535)
     * @param val La valeur à écrire
     */
    public void ecrire(int adr, byte val) {
        cases[adr] = val;
    }

    /*
     * Retourne la taille totale de la mémoire en octets
     *
     * @return La taille de la mémoire (65 536)
     */
    public int getTaille() {
        return TAILLE;  
    }

    /*
     * Vérifie que l'adresse est dans les bornes valides de la mémoire
     *
     * @param address L'adresse à vérifier
     */
    private void checkAddress(int address) {
        if (address < 0 || address >= TAILLE)
            throw new IllegalArgumentException("Adresse invalide : " + address);
    }

}