package simulateur;
import assembleur.Assembleur;
import assembleur.Programme;
import materiel.CPU;
import materiel.Memoire;
public class Simulateur {
    private CPU cpu;
    private Memoire memoire;
    private Assembleur assembleur;
    private Programme programme;

    /*
     * Initialise le simulateur avec la mémoire, le CPU et l'assembleur
     */
    public Simulateur() {
        this.memoire = new Memoire();
        this.cpu = new CPU(memoire);
        this.assembleur = new Assembleur();
        this.programme = null;
    }

     
    /*
     * Définit le code source du programme à simuler
     *
     * @param codeSource Le code source assembleur du programme
     */
    public void saisirProgramme(String codeSource) {
        this.programme = new Programme(codeSource);
    }

    /*
     * Assemble le programme saisi et le charge dans le CPU
     */
    public void assembler() {
        if (programme == null) {
            throw new IllegalStateException("Aucun programme à assembler");
        }

        assembleur.assembler(programme);
        cpu.chargerProgramme(programme);
    }

    /*
     * Exécute le programme assemblé
     */
    public void executerProgramme() {
        if (programme == null || !programme.estAssemble()) {
            throw new IllegalStateException("Programme non assemblé");
        }

        cpu.executerProgramme();
    }


    /*
     * Retourne la valeur du registre numéro num
     *
     * @param num Le numéro du registre (0 à 15)
     * @return La valeur stockée dans le registre
     */
    public byte consulterRegistre(int num) {
        if (num < 0 || num >= 16) {
            throw new IllegalArgumentException("Numéro de registre invalide");
        }

        return cpu.getRegistre(num).lire();
    }

    /*
     * Modifie la valeur d'un registre
     *
     * @param num Le numéro du registre (0 à 15)
     * @param val La valeur à écrire dans le registre
     */
    public void modifierRegistre(int num, byte val) {
        if (num < 0 || num >= 16) {
            throw new IllegalArgumentException("Numéro de registre invalide");
        }

        cpu.getRegistre(num).ecrire(val);
    }


    /*
     * Retourne l'octet stocké à l'adresse mémoire donnée
     *
     * @param adr L'adresse mémoire à consulter (0 à 65535)
     * @return L'octet stocké à cette adresse
     */
    public byte consulterMemoire(int adr) {
        if (adr < 0 || adr >= memoire.getTaille()) {
            throw new IllegalArgumentException("Adresse mémoire invalide");
        }

        return memoire.lire(adr);
    }


    /*
     * Écrit un octet à l'adresse mémoire donnée
     *
     * @param adr L'adresse mémoire où écrire (0 à 65535)
     * @param val La valeur à écrire
     */
    public void modifierMemoire(int adr, byte val) {
        if (adr < 0 || adr >= memoire.getTaille()) {
            throw new IllegalArgumentException("Adresse mémoire invalide");
        }
        
        memoire.ecrire(adr, val);
    }
}