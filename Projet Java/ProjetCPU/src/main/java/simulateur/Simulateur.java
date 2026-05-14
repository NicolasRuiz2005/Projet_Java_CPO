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

    public Simulateur() {
        this.memoire = new Memoire();
        this.cpu = new CPU(memoire);
        this.assembleur = new Assembleur();
        this.programme = null;
    }

    public void saisirProgramme(String codeSource) {
        this.programme = new Programme(codeSource);
    }

    public void assembler() {
        if (programme == null) {
            throw new IllegalStateException("Aucun programme à assembler");
        }

        assembleur.assembler(programme);
        cpu.chargerProgramme(programme);
    }

    public void executerProgramme() {
        if (programme == null || !programme.estAssemble()) {
            throw new IllegalStateException("Programme non assemblé");
        }

        cpu.executerProgramme();
    }


    public byte consulterRegistre(int num) {
        if (num < 0 || num >= 16) {
            throw new IllegalArgumentException("Numéro de registre invalide");
        }

        return cpu.getRegistre(num).lire();
    }

    public void modifierRegistre(int num, byte val) {
        if (num < 0 || num >= 16) {
            throw new IllegalArgumentException("Numéro de registre invalide");
        }

        cpu.getRegistre(num).ecrire(val);
    }


    public byte consulterMemoire(int adr) {
        if (adr < 0 || adr >= memoire.getTaille()) {
            throw new IllegalArgumentException("Adresse mémoire invalide");
        }

        return memoire.lire(adr);
    }


    public void modifierMemoire(int adr, byte val) {
        if (adr < 0 || adr >= memoire.getTaille()) {
            throw new IllegalArgumentException("Adresse mémoire invalide");
        }
        
        memoire.ecrire(adr, val);
    }
}