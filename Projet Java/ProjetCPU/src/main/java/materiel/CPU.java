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

    /*
     * Initialise le CPU avec la mémoire donnée, 16 registres et une ALU
     *
     * @param memoire La mémoire principale utilisée par le CPU
     */
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

    /*
     * Charge un programme assemblé dans le CPU
     *
     * @param prog Le programme à charger
     */
    public void chargerProgramme(Programme prog) {
        this.programme = prog;
    
    }

    /*
     * Exécute le programme chargé instruction par instruction jusqu'à une instruction BREAK ou la fin du programme
     */
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


    /*
     * Exécute une seule instruction sur le CPU
     *
     * @param i L'instruction à exécuter
     */
    public void executerInstruction(Instruction i) {
        i.executer(this, memoire);
    }

    /*
     * Retourne le registre correspondant au numéro donné
     *
     * @param num Le numéro du registre (0 à 15)
     * @return Le registre correspondant
     */
    public Registre getRegistre(int num) {
        if (num < 0 || num >= 16) {
            throw new IllegalArgumentException("Numéro de registre invalide : " + num);
        }
        return registres[num];
    }

    /*
     * Retourne la mémoire principale du CPU
     *
     * @return La mémoire du CPU
     */
    public Memoire getMemoire() {
        return memoire;
    }


    /*
     * Retourne l'unité arithmétique et logique (ALU) du CPU
     *
     * @return L'ALU du CPU
     */
    public ALU getALU() {
        return alu;
    }

    /*
     * Retourne la valeur actuelle du compteur de programme
     *
     * @return La valeur du compteur de programme (PC)
     */
    public int getPc() {
        return pc;
    }

    /*
     * Modifie le compteur de programme (utilisé par les instructions de branchement)
     *
     * @param adresse La nouvelle valeur du compteur de programme
     */
    public void setPc(int adresse) {
        this.pc = adresse;
    }

    /*
     * Incrémente le compteur de programme de 1
     */
    public void incrementerPC() {
        pc++;
    }

    /*
     * Arrête l'exécution du programme (déclenché par l'instruction BREAK)
     */
    public void arreter() {
        this.enRoute = false;
    }
    
    /*
     * Indique si le CPU est en train d'exécuter un programme
     *
     * @return true si le CPU est en cours d'exécution, false sinon
     */
    public boolean estEnRoute() {
        return enRoute;
    }
}