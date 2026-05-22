package simulateur;

import assembleur.Assembleur;
import assembleur.Programme;
import materiel.CPU;
import materiel.Memoire;

/**
 * Façade principale du simulateur CPU.
 * Orchestre la mémoire ({@link Memoire}), le processeur ({@link CPU}) et l'assembleur
 * ({@link Assembleur}) en exposant une interface de haut niveau pour saisir, assembler
 * et exécuter un programme, ainsi que pour consulter et modifier registres et mémoire.
 */
public class Simulateur {
    private CPU cpu;
    private Memoire memoire;
    private Assembleur assembleur;
    private Programme programme;
    /** Taille en octets du dernier programme assemblé — sert à effacer l'ancienne zone programme. */
    private int finDernierProgramme = 0;

    /**
     * Initialise le simulateur avec une mémoire vierge, un CPU et un assembleur.
     */
    public Simulateur() {
        this.memoire = new Memoire();
        this.cpu = new CPU(memoire);
        this.assembleur = new Assembleur();
        this.programme = null;
    }

    /**
     * Définit le code source du programme à simuler.
     *
     * @param codeSource le code source assembleur du programme
     */
    public void saisirProgramme(String codeSource) {
        this.programme = new Programme(codeSource);
    }

    /**
     * Assemble le programme saisi : remet la mémoire à zéro, puis écrit les octets
     * du programme en mémoire à partir de l'adresse 0.
     *
     * @throws IllegalStateException si aucun programme n'a été saisi au préalable
     */
    public void assembler() {
        if (programme == null) {
            throw new IllegalStateException("Aucun programme à assembler");
        }
        // Efface uniquement l'ancienne zone programme pour éviter les octets parasites.
        // Les données utilisateur situées en dehors de cette zone sont préservées.
        for (int i = 0; i < finDernierProgramme; i++) {
            memoire.ecrire(i, (byte) 0);
        }
        assembleur.assembler(programme, memoire);
        finDernierProgramme = assembleur.getAdresseFinale();
    }

    /**
     * Exécute le programme assemblé.
     *
     * @throws IllegalStateException si le programme n'a pas encore été assemblé
     */
    public void executerProgramme() {
        if (programme == null || !programme.estAssemble()) {
            throw new IllegalStateException("Programme non assemblé");
        }
        cpu.executerProgramme();
    }

    /**
     * Retourne la valeur du registre numéro {@code num}.
     *
     * @param num le numéro du registre (0 à 15)
     * @return la valeur stockée dans le registre
     * @throws IllegalArgumentException si le numéro est hors de l'intervalle [0, 15]
     */
    public byte consulterRegistre(int num) {
        if (num < 0 || num >= 16) {
            throw new IllegalArgumentException("Numéro de registre invalide");
        }
        return cpu.getRegistre(num).lire();
    }

    /**
     * Modifie la valeur d'un registre.
     *
     * @param num le numéro du registre (0 à 15)
     * @param val la valeur à écrire dans le registre
     * @throws IllegalArgumentException si le numéro est hors de l'intervalle [0, 15]
     */
    public void modifierRegistre(int num, byte val) {
        if (num < 0 || num >= 16) {
            throw new IllegalArgumentException("Numéro de registre invalide");
        }
        cpu.getRegistre(num).ecrire(val);
    }

    /**
     * Retourne l'octet stocké à l'adresse mémoire donnée.
     *
     * @param adr l'adresse mémoire à consulter (0 à 65535)
     * @return l'octet stocké à cette adresse
     * @throws IllegalArgumentException si l'adresse est hors bornes
     */
    public byte consulterMemoire(int adr) {
        if (adr < 0 || adr >= memoire.getTaille()) {
            throw new IllegalArgumentException("Adresse mémoire invalide");
        }
        return memoire.lire(adr);
    }

    /**
     * Retourne la valeur courante du compteur de programme (PC).
     *
     * @return l'adresse mémoire pointée par le PC après la dernière exécution
     */
    public int consulterPC() {
        return cpu.getPc();
    }

    /**
     * Écrit un octet à l'adresse mémoire donnée.
     *
     * @param adr l'adresse mémoire où écrire (0 à 65535)
     * @param val la valeur à écrire
     * @throws IllegalArgumentException si l'adresse est hors bornes
     */
    public void modifierMemoire(int adr, byte val) {
        if (adr < 0 || adr >= memoire.getTaille()) {
            throw new IllegalArgumentException("Adresse mémoire invalide");
        }
        memoire.ecrire(adr, val);
    }
}