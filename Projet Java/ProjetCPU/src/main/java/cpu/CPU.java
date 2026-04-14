package cpu;

import alu.ALU;
import assembler.InstructionSet;

/**
 * Simulateur du processeur (CPU) — cœur du projet.
 *
 * <p>Le CPU implémente un cycle classique Fetch → Decode → Execute :</p>
 * <ol>
 *   <li><b>Fetch</b> : lit le prochain byte en mémoire à l'adresse pointée
 *       par le compteur de programme (PC), puis incrémente le PC.</li>
 *   <li><b>Decode</b> : interprète le byte lu comme un opcode.</li>
 *   <li><b>Execute</b> : lit les paramètres nécessaires et effectue
 *       l'opération correspondante.</li>
 * </ol>
 *
 * <p>Le CPU s'arrête dès qu'il rencontre l'instruction {@code BREAK}.</p>
 *
 * <p>Composants internes :</p>
 * <ul>
 *   <li>{@link Memory} — 64 Ko de mémoire principale</li>
 *   <li>{@link RegisterFile} — 16 registres 8 bits (r0 à r15)</li>
 *   <li>{@link ProgramCounter} — compteur de programme 16 bits</li>
 * </ul>
 *
 * @author Projet CPU
 * @version 1.0
 * @see Memory
 * @see RegisterFile
 * @see ProgramCounter
 * @see ALU
 */
public class CPU {

    /** Mémoire principale du CPU (64 Ko). */
    private final Memory memory;

    /** Banc de registres généraux (16 registres 8 bits). */
    private final RegisterFile registers;

    /** Compteur de programme (adresse de la prochaine instruction). */
    private final ProgramCounter pc;

    /** Indique si le CPU est actuellement en cours d'exécution. */
    private boolean running;

    /**
     * Crée un nouveau CPU avec sa mémoire, ses registres et son PC
     * tous initialisés à zéro.
     */
    public CPU() {
        memory    = new Memory();
        registers = new RegisterFile();
        pc        = new ProgramCounter();
        running   = false;
    }

    // ---------------------------------------------------------------
    // Accesseurs publics
    // ---------------------------------------------------------------

    /**
     * Retourne la mémoire du CPU.
     *
     * @return la mémoire principale
     */
    public Memory getMemory() {
        return memory;
    }

    /**
     * Retourne le banc de registres.
     *
     * @return les registres généraux
     */
    public RegisterFile getRegisters() {
        return registers;
    }

    /**
     * Retourne le compteur de programme.
     *
     * @return le PC
     */
    public ProgramCounter getProgramCounter() {
        return pc;
    }

    /**
     * Indique si le CPU est en cours d'exécution.
     *
     * @return {@code true} si le CPU tourne, {@code false} sinon
     */
    public boolean isRunning() {
        return running;
    }

    // ---------------------------------------------------------------
    // Contrôle du CPU
    // ---------------------------------------------------------------

    /**
     * Réinitialise le CPU : remet les registres et le PC à zéro.
     * La mémoire n'est PAS effacée afin de conserver le programme chargé.
     */
    public void reset() {
        pc.reset();
        registers.reset();
        running = false;
    }

    /**
     * Lance l'exécution du programme depuis l'adresse courante du PC.
     * La boucle tourne jusqu'à rencontrer un {@code BREAK} ou une erreur.
     */
    public void run() {
        running = true;
        while (running) {
            byte opcode = fetch();
            execute(opcode);
        }
    }

    /**
     * Exécute une seule instruction (mode pas-à-pas).
     * Utile pour le débogage.
     *
     * @return {@code false} si l'instruction était BREAK, {@code true} sinon
     */
    public boolean step() {
        if (!running) {
            running = true;
        }
        byte opcode = fetch();
        execute(opcode);
        return running;
    }

    // ---------------------------------------------------------------
    // Cycle Fetch
    // ---------------------------------------------------------------

    /**
     * Lit le byte à l'adresse courante du PC et avance le PC d'une unité.
     *
     * @return le byte lu en mémoire
     */
    private byte fetch() {
        byte value = memory.read(pc.get());
        pc.increment();
        return value;
    }

    /**
     * Lit deux bytes consécutifs depuis la mémoire pour former une
     * adresse 16 bits (octet haut en premier, puis octet bas).
     *
     * @return l'adresse 16 bits reconstituée
     */
    private int fetchAddress() {
        int high = Byte.toUnsignedInt(fetch());
        int low  = Byte.toUnsignedInt(fetch());
        return (high << 8) | low;
    }

    /**
     * Lit un byte représentant un numéro de registre (0 à 15).
     *
     * @return l'index du registre
     */
    private int fetchRegisterIndex() {
        return Byte.toUnsignedInt(fetch());
    }

    // ---------------------------------------------------------------
    // Cycle Decode + Execute
    // ---------------------------------------------------------------

    /**
     * Décode l'opcode et dispatche vers la méthode d'exécution appropriée.
     *
     * @param opcode le code de l'instruction à exécuter
     * @throws IllegalStateException si l'opcode est inconnu
     */
    private void execute(byte opcode) {
        switch (opcode) {
            case InstructionSet.BREAK:      running = false;     break;
            case InstructionSet.LOAD_CONST: executeLoadConst();  break;
            case InstructionSet.LOAD_MEM:   executeLoadMem();    break;
            case InstructionSet.STORE_MEM:  executeStoreMem();   break;
            case InstructionSet.ADD:        executeAdd();        break;
            case InstructionSet.SUB:        executeSub();        break;
            case InstructionSet.MUL:        executeMul();        break;
            case InstructionSet.DIV:        executeDiv();        break;
            case InstructionSet.OR:         executeOr();         break;
            case InstructionSet.AND:        executeAnd();        break;
            case InstructionSet.XOR:        executeXor();        break;
            case InstructionSet.JUMP:       executeJump();       break;
            case InstructionSet.BEQ:        executeBeq();        break;
            case InstructionSet.BNE:        executeBne();        break;
            case InstructionSet.LOAD_IDX:   executeLoadIdx();    break;
            case InstructionSet.STORE_IDX:  executeStoreIdx();   break;
            default:
                throw new IllegalStateException(
                    "Opcode inconnu : " + opcode + " à l'adresse " + (pc.get() - 1));
        }
    }

    // ---------------------------------------------------------------
    // Étape 1 — Instructions de base
    // ---------------------------------------------------------------

    /**
     * Exécute LOAD_CONST : charge une constante dans un registre.
     * Paramètres lus : reg(1 byte), val(1 byte).
     */
    private void executeLoadConst() {
        int  reg   = fetchRegisterIndex();
        byte value = fetch();
        registers.set(reg, value);
    }

    /**
     * Exécute LOAD_MEM : charge en registre la valeur à une adresse mémoire.
     * Paramètres lus : reg(1), addrHigh(1), addrLow(1).
     */
    private void executeLoadMem() {
        int reg     = fetchRegisterIndex();
        int address = fetchAddress();
        registers.set(reg, memory.read(address));
    }

    /**
     * Exécute STORE_MEM : sauvegarde un registre en mémoire.
     * Paramètres lus : reg(1), addrHigh(1), addrLow(1).
     */
    private void executeStoreMem() {
        int reg     = fetchRegisterIndex();
        int address = fetchAddress();
        memory.write(address, registers.get(reg));
    }

    // ---------------------------------------------------------------
    // Étape 3 — ALU
    // ---------------------------------------------------------------

    /**
     * Exécute ADD : dest = rA + rB.
     * Paramètres lus : dest(1), a(1), b(1).
     */
    private void executeAdd() {
        int dest = fetchRegisterIndex();
        int a    = fetchRegisterIndex();
        int b    = fetchRegisterIndex();
        registers.set(dest, ALU.add(registers.get(a), registers.get(b)));
    }

    /**
     * Exécute SUB : dest = rA - rB.
     * Paramètres lus : dest(1), a(1), b(1).
     */
    private void executeSub() {
        int dest = fetchRegisterIndex();
        int a    = fetchRegisterIndex();
        int b    = fetchRegisterIndex();
        registers.set(dest, ALU.subtract(registers.get(a), registers.get(b)));
    }

    /**
     * Exécute MUL : {rHigh, rLow} = rA * rB (résultat 16 bits).
     * Paramètres lus : destHigh(1), destLow(1), a(1), b(1).
     */
    private void executeMul() {
        int destHigh = fetchRegisterIndex();
        int destLow  = fetchRegisterIndex();
        int a        = fetchRegisterIndex();
        int b        = fetchRegisterIndex();
        registers.set(destHigh, ALU.multiplyHigh(registers.get(a), registers.get(b)));
        registers.set(destLow,  ALU.multiplyLow(registers.get(a),  registers.get(b)));
    }

    /**
     * Exécute DIV : rQuot = rA / rB, rRem = rA % rB.
     * Paramètres lus : quot(1), rem(1), a(1), b(1).
     */
    private void executeDiv() {
        int quot = fetchRegisterIndex();
        int rem  = fetchRegisterIndex();
        int a    = fetchRegisterIndex();
        int b    = fetchRegisterIndex();
        registers.set(quot, ALU.divideQuotient(registers.get(a),  registers.get(b)));
        registers.set(rem,  ALU.divideRemainder(registers.get(a), registers.get(b)));
    }

    /**
     * Exécute OR : dest = rA | rB.
     * Paramètres lus : dest(1), a(1), b(1).
     */
    private void executeOr() {
        int dest = fetchRegisterIndex();
        int a    = fetchRegisterIndex();
        int b    = fetchRegisterIndex();
        registers.set(dest, ALU.or(registers.get(a), registers.get(b)));
    }

    /**
     * Exécute AND : dest = rA & rB.
     * Paramètres lus : dest(1), a(1), b(1).
     */
    private void executeAnd() {
        int dest = fetchRegisterIndex();
        int a    = fetchRegisterIndex();
        int b    = fetchRegisterIndex();
        registers.set(dest, ALU.and(registers.get(a), registers.get(b)));
    }

    /**
     * Exécute XOR : dest = rA ^ rB.
     * Paramètres lus : dest(1), a(1), b(1).
     */
    private void executeXor() {
        int dest = fetchRegisterIndex();
        int a    = fetchRegisterIndex();
        int b    = fetchRegisterIndex();
        registers.set(dest, ALU.xor(registers.get(a), registers.get(b)));
    }

    // ---------------------------------------------------------------
    // Étape 4 — Boucles et conditionnelles
    // ---------------------------------------------------------------

    /**
     * Exécute JUMP : saut inconditionnel à l'adresse donnée.
     * Paramètres lus : addrHigh(1), addrLow(1).
     */
    private void executeJump() {
        int address = fetchAddress();
        pc.set(address);
    }

    /**
     * Exécute BEQ : saut si rA == rB.
     * Paramètres lus : a(1), b(1), addrHigh(1), addrLow(1).
     */
    private void executeBeq() {
        int a       = fetchRegisterIndex();
        int b       = fetchRegisterIndex();
        int address = fetchAddress();
        if (registers.get(a) == registers.get(b)) {
            pc.set(address);
        }
    }

    /**
     * Exécute BNE : saut si rA != rB.
     * Paramètres lus : a(1), b(1), addrHigh(1), addrLow(1).
     */
    private void executeBne() {
        int a       = fetchRegisterIndex();
        int b       = fetchRegisterIndex();
        int address = fetchAddress();
        if (registers.get(a) != registers.get(b)) {
            pc.set(address);
        }
    }

    // ---------------------------------------------------------------
    // Étape 5 — Tableaux
    // ---------------------------------------------------------------

    /**
     * Exécute LOAD_IDX : charge depuis mem[baseAddr + rIndex] dans rDest.
     * Paramètres lus : dest(1), addrHigh(1), addrLow(1), indexReg(1).
     */
    private void executeLoadIdx() {
        int dest     = fetchRegisterIndex();
        int baseAddr = fetchAddress();
        int indexReg = fetchRegisterIndex();
        int offset   = Byte.toUnsignedInt(registers.get(indexReg));
        registers.set(dest, memory.read(baseAddr + offset));
    }

    /**
     * Exécute STORE_IDX : stocke rSrc dans mem[baseAddr + rIndex].
     * Paramètres lus : src(1), addrHigh(1), addrLow(1), indexReg(1).
     */
    private void executeStoreIdx() {
        int src      = fetchRegisterIndex();
        int baseAddr = fetchAddress();
        int indexReg = fetchRegisterIndex();
        int offset   = Byte.toUnsignedInt(registers.get(indexReg));
        memory.write(baseAddr + offset, registers.get(src));
    }

    // ---------------------------------------------------------------
    // Affichage
    // ---------------------------------------------------------------

    /**
     * Affiche l'état complet du CPU : PC et tous les registres.
     *
     * @return une représentation textuelle de l'état du CPU
     */
    @Override
    public String toString() {
        return pc.toString() + "\n" + registers.toString();
    }
}
