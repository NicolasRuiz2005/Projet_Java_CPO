package materiel;
import assembleur.Programme;
import instructions.Instruction;
public class CPU {
    private int pc;
    private boolean enRoute;
    private Registre[] registres;
    private Memoire memoire;
    private ALU alu;
    private Programme programme;

    public CPU(Memoire memoire) {
        this.memoire = memoire;
        this.alu = new ALU();
        this.registres = new Registre[16];
        for (int i = 0; i < 16; i++) {
            registres[i] = new Registre(i);
        }
        this.pc = 0;
        this.enRoute = false;
    }

    public void chargerProgramme(Programme prog) {
        this.programme = prog;
    
    }

    public void executerProgramme() {
        if (programme == null || !programme.estAssemble()) {
            throw new IllegalStateException("Aucun programme assemblé à exécuter");
        }
        pc = 0;
        enRoute = true;
        while (enRoute && pc < programme.nombreInstructions()) {
            Instruction inst = programme.getInstruction(pc);
            int pcAvant = pc;
            inst.executer(this, memoire);
            // Si l'instruction n'a pas modifié le PC (pas de branchement), on l'incrémente
            if (pc == pcAvant) {
                pc++;
            }
        }
    }


    public void executerInstruction(Instruction i) {
        i.executer(this, memoire);
    }

    public Registre getRegistre(int num) {
        if (num < 0 || num >= 16) {
            throw new IllegalArgumentException("Numéro de registre invalide : " + num);
        }
        return registres[num];
    }

    public Memoire getMemoire() {
        return memoire;
    }


    public ALU getALU() {
        return alu;
    }

    public int getPc() {
        return pc;
    }

    public void setPc(int adresse) {
        this.pc = adresse;
    }

    public void incrementerPC() {
        pc++;
    }

    public void arreter() {
        this.enRoute = false;
    }
    
    public boolean estEnRoute() {
        return enRoute;
    }
}