package materiel;

/**
 * Processeur (CPU) du simulateur "Carré Petit Utile".
 * Implémente un vrai cycle Fetch/Decode/Execute : le compteur de programme (PC)
 * pointe une adresse mémoire réelle, et chaque instruction est lue octet par octet
 * depuis la mémoire.
 * Les opcodes sont définis dans {@link OpcodesCPU}.
 */
public class CPU {
    private int pc;
    private boolean enRoute;
    private final Registre[] registres;
    private final Memoire memoire;
    private final ALU alu;

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
     * Exécute le programme présent en mémoire à partir de l'adresse 0.
     * S'arrête quand l'instruction BREAK (opcode 0) est rencontrée.
     */
    public void executerProgramme() {
        pc = 0;
        enRoute = true;
        while (enRoute) {
            int opcode = fetchOctetUnsigned();  // FETCH
            executer(opcode);                   // DECODE + EXECUTE
        }
    }

    /**
     * Décode et exécute un opcode.
     *
     * @param opcode le code opération lu depuis la mémoire
     * @throws RuntimeException si l'opcode est inconnu
     */
    private void executer(int opcode) {
        switch (opcode) {
            case 0 -> enRoute = false;  // BREAK

            case 1 -> {  // LOAD_CONST : [reg][val]
                int reg = fetchOctetUnsigned();
                byte val = fetchOctet();
                registres[reg].ecrire(val);
            }

            case 2 -> {  // LOAD_MEM : [reg][adrH][adrL]
                int reg = fetchOctetUnsigned();
                int adr = fetchAdresse();
                registres[reg].ecrire(memoire.lire(adr));
            }

            case 3 -> {  // STORE : [reg][adrH][adrL]
                int reg = fetchOctetUnsigned();
                int adr = fetchAdresse();
                memoire.ecrire(adr, registres[reg].lire());
            }

            case 4 -> {  // ADD : [dest][src1][src2]
                int dest = fetchOctetUnsigned();
                int src1 = fetchOctetUnsigned();
                int src2 = fetchOctetUnsigned();
                registres[dest].ecrire(alu.additionner(registres[src1].lire(), registres[src2].lire()));
            }

            case 5 -> {  // SUB : [dest][src1][src2]
                int dest = fetchOctetUnsigned();
                int src1 = fetchOctetUnsigned();
                int src2 = fetchOctetUnsigned();
                registres[dest].ecrire(alu.soustraire(registres[src1].lire(), registres[src2].lire()));
            }

            case 6 -> {  // MUL : [destH][destL][src1][src2]
                int destH = fetchOctetUnsigned();
                int destL = fetchOctetUnsigned();
                int src1  = fetchOctetUnsigned();
                int src2  = fetchOctetUnsigned();
                int res = alu.multiplier(registres[src1].lire(), registres[src2].lire());
                registres[destH].ecrire((byte) ((res >> 8) & 0xFF));
                registres[destL].ecrire((byte) (res & 0xFF));
            }

            case 7 -> {  // DIV : [quot][reste][src1][src2]
                int quot  = fetchOctetUnsigned();
                int reste = fetchOctetUnsigned();
                int src1  = fetchOctetUnsigned();
                int src2  = fetchOctetUnsigned();
                int res = alu.diviser(registres[src1].lire(), registres[src2].lire());
                registres[quot].ecrire((byte) ((res >> 8) & 0xFF));
                registres[reste].ecrire((byte) (res & 0xFF));
            }

            case 8 -> {  // OU : [dest][src1][src2]
                int dest = fetchOctetUnsigned();
                int src1 = fetchOctetUnsigned();
                int src2 = fetchOctetUnsigned();
                registres[dest].ecrire(alu.ouBinaire(registres[src1].lire(), registres[src2].lire()));
            }

            case 9 -> {  // ET : [dest][src1][src2]
                int dest = fetchOctetUnsigned();
                int src1 = fetchOctetUnsigned();
                int src2 = fetchOctetUnsigned();
                registres[dest].ecrire(alu.etBinaire(registres[src1].lire(), registres[src2].lire()));
            }

            case 10 -> {  // OU_EXCLUSIF : [dest][src1][src2]
                int dest = fetchOctetUnsigned();
                int src1 = fetchOctetUnsigned();
                int src2 = fetchOctetUnsigned();
                registres[dest].ecrire(alu.ouExclusif(registres[src1].lire(), registres[src2].lire()));
            }

            case 11 -> {  // JUMP : [adrH][adrL]
                pc = fetchAdresse();
            }

            case 12 -> {  // BEQ : [src1][src2][adrH][adrL]
                int src1 = fetchOctetUnsigned();
                int src2 = fetchOctetUnsigned();
                int adr  = fetchAdresse();
                if (registres[src1].lire() == registres[src2].lire()) {
                    pc = adr;
                }
            }

            case 13 -> {  // BNE : [src1][src2][adrH][adrL]
                int src1 = fetchOctetUnsigned();
                int src2 = fetchOctetUnsigned();
                int adr  = fetchAdresse();
                if (registres[src1].lire() != registres[src2].lire()) {
                    pc = adr;
                }
            }

            case 14 -> {  // LOAD_INDEXE : [dest][adrH][adrL][regIdx]
                int dest   = fetchOctetUnsigned();
                int adrBase = fetchAdresse();
                int regIdx = fetchOctetUnsigned();
                int offset = Byte.toUnsignedInt(registres[regIdx].lire());
                registres[dest].ecrire(memoire.lire(adrBase + offset));
            }

            case 15 -> {  // STORE_INDEXE : [src][adrH][adrL][regIdx]
                int src    = fetchOctetUnsigned();
                int adrBase = fetchAdresse();
                int regIdx = fetchOctetUnsigned();
                int offset = Byte.toUnsignedInt(registres[regIdx].lire());
                memoire.ecrire(adrBase + offset, registres[src].lire());
            }

            default -> throw new RuntimeException("Opcode inconnu : " + opcode + " (adresse " + (pc - 1) + ")");
        }
    }

    // ---------------------------------------------------------------
    // Helpers de lecture mémoire séquentielle (avancent le PC)
    // ---------------------------------------------------------------

    /** Lit l'octet courant (signé) et avance le PC. */
    private byte fetchOctet() {
        return memoire.lire(pc++);
    }

    /** Lit l'octet courant (non signé, 0–255) et avance le PC. */
    private int fetchOctetUnsigned() {
        return Byte.toUnsignedInt(memoire.lire(pc++));
    }

    /** Lit deux octets consécutifs (big-endian) et les assemble en adresse 16 bits. */
    private int fetchAdresse() {
        return (fetchOctetUnsigned() << 8) | fetchOctetUnsigned();
    }

    // ---------------------------------------------------------------
    // Accesseurs publics
    // ---------------------------------------------------------------

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
