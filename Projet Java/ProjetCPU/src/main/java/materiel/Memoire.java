package materiel;

/**
 * Mémoire principale du simulateur CPU.
 * Modélise un espace d'adressage de 65 536 octets (adresses 0 à 65 535),
 * initialisés à zéro à la construction.
 */
public class Memoire {
    private static final int TAILLE = 65536;
    private byte[] cases;

    /**
     * Initialise la mémoire de 65 536 octets, tous à zéro.
     */
    public Memoire() {
        this.cases = new byte[TAILLE];
    }

    /**
     * Lit l'octet à l'adresse mémoire donnée.
     *
     * @param adr l'adresse mémoire à lire (0 à 65535)
     * @return l'octet stocké à cette adresse
     * @throws IllegalArgumentException si l'adresse est hors bornes
     */
    public byte lire(int adr) {
        checkAddress(adr);
        return cases[adr];
    }

    /**
     * Écrit un octet à l'adresse mémoire donnée.
     *
     * @param adr l'adresse mémoire où écrire (0 à 65535)
     * @param val la valeur à écrire
     * @throws IllegalArgumentException si l'adresse est hors bornes
     */
    public void ecrire(int adr, byte val) {
        checkAddress(adr);
        cases[adr] = val;
    }

    /**
     * Retourne la taille totale de la mémoire en octets.
     *
     * @return la taille de la mémoire (65 536)
     */
    public int getTaille() {
        return TAILLE;
    }

    /**
     * Remet toutes les cases mémoire à zéro.
     * Appelée par le simulateur avant chaque nouvel assemblage.
     */
    public void reinitialiser() {
        java.util.Arrays.fill(cases, (byte) 0);
    }

    /**
     * Vérifie que l'adresse est dans les bornes valides de la mémoire.
     *
     * @param address l'adresse à vérifier
     * @throws IllegalArgumentException si l'adresse est négative ou supérieure à 65535
     */
    private void checkAddress(int address) {
        if (address < 0 || address >= TAILLE)
            throw new IllegalArgumentException("Adresse invalide : " + address);
    }
}