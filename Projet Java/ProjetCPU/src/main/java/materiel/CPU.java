package materiel;

import assembleur.Programme;
import instructions.Instruction;

/**
 * Processeur (CPU) du simulateur.
 * Gère le compteur de programme (PC), les 16 registres, l'ALU et l'exécution
 * d'un programme assemblé instruction par instruction.
 */
public class CPU {
    private int pc;
    private boolean enRoute;
    private Registre[] registres;
    private Memoire memoire;
    private ALU alu;
    private Programme programme;

    /**
     * Initialise le CPU avec la mémoire donnée, 16 registres à zéro et une ALU.
     *
     * @param memoire la mémoire principale partagée avec le simulateur
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

    /**
     * Charge un programme assemblé dans le CPU.
     *
     * @param prog le programme à charger
     */
    public void chargerProgramme(Programme prog) {
        this.programme = prog;
    }

    /**
     * Exécute le programme chargé instruction par instruction jusqu'à BREAK ou la fin du programme.
     *
     * @throws IllegalStateException si aucun programme assemblé n'est chargé
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

    /**
     * Exécute une seule instruction sur le CPU.
     *
     * @param i l'instruction à exécuter
     */
    public void executerInstruction(Instruction i) {
        i.executer(this, memoire);
    }

    /**
     * Retourne le registre correspondant au numéro donné.
     *
     * @param num le numéro du registre (0 à 15)
     * @return le registre correspondant
     * @throws IllegalArgumentException si le numéro est hors de l'intervalle [0, 15]
     */
    public Registre getRegistre(int num) {
        if (num < 0 || num >= 16) {
            throw new IllegalArgumentException("Numéro de registre invalide : " + num);
        }
        return registres[num];
    }

    /**
     * Retourne la mémoire principale du CPU.
     *
     * @return la mémoire du CPU
     */
    public Memoire getMemoire() {
        return memoire;
    }

    /**
     * Retourne l'unité arithmétique et logique (ALU) du CPU.
     *
     * @return l'ALU du CPU
     */
    public ALU getALU() {
        return alu;
    }

    /**
     * Retourne la valeur actuelle du compteur de programme.
     *
     * @return la valeur du compteur de programme (PC)
     */
    public int getPc() {
        return pc;
    }

    /**
     * Modifie le compteur de programme (utilisé par les instructions de branchement).
     *
     * @param adresse la nouvelle valeur du compteur de programme
     */
    public void setPc(int adresse) {
        this.pc = adresse;
    }

    /**
     * Incrémente le compteur de programme de 1.
     */
    public void incrementerPC() {
        pc++;
    }

    /**
     * Arrête l'exécution du programme (déclenché par l'instruction BREAK).
     */
    public void arreter() {
        this.enRoute = false;
    }

    /**
     * Indique si le CPU est en train d'exécuter un programme.
     *
     * @return {@code true} si le CPU est en cours d'exécution, {@code false} sinon
     */
    public boolean estEnRoute() {
        return enRoute;
    }
}