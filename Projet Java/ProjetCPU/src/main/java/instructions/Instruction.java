package instructions;

import materiel.CPU;
import materiel.Memoire;
import materiel.ALU;

public class Instruction {

    private TypeInstruction type;
    private int ligne;
    private int[] operandes;
    public Instruction(TypeInstruction type, int ligne, int[] operandes) {

        this.type = type;
        this.ligne = ligne;
        this.operandes = operandes;
    }

    public TypeInstruction getType() {
        return type;
    }

    public int getLigne() {
        return ligne;
    }

    public int[] getOperandes() {
        return operandes;
    }

    public void executer(CPU cpu, Memoire mem) {

        ALU alu = cpu.getALU();
        switch (type) {
            case LOAD_CONSTANTE: {
                int numReg = operandes[0];
                byte val = (byte) operandes[1];
                cpu.getRegistre(numReg).ecrire(val);
                break;
            }
            case LOAD_MEMOIRE: {
                int numReg = operandes[0];
                int adr = operandes[1];
                cpu.getRegistre(numReg).ecrire(mem.lire(adr));
                break;
            }
            case STORE: {
                int numReg = operandes[0];
                int adr = operandes[1];
                mem.ecrire(adr, cpu.getRegistre(numReg).lire());
                break;
            }
            case LOAD_INDEXE: {
                int numReg = operandes[0];
                int adrBase = operandes[1];
                int numIdx = operandes[2];
                int offset = cpu.getRegistre(numIdx).lire();
                cpu.getRegistre(numReg).ecrire(mem.lire(adrBase + offset));
                break;
            }
            case STORE_INDEXE: {
                int numReg = operandes[0];
                int adrBase = operandes[1];
                int numIdx = operandes[2];
                int offset = cpu.getRegistre(numIdx).lire();
                mem.ecrire(adrBase + offset, cpu.getRegistre(numReg).lire());
                break;
            }

            case ADD: {
                byte a = cpu.getRegistre(operandes[0]).lire();
                byte b = cpu.getRegistre(operandes[1]).lire();
                cpu.getRegistre(operandes[2]).ecrire(alu.additionner(a, b));
                break;
            }

            case SUB: {
                byte a = cpu.getRegistre(operandes[0]).lire();
                byte b = cpu.getRegistre(operandes[1]).lire();
                cpu.getRegistre(operandes[2]).ecrire(alu.soustraire(a, b));
                break;
            }

            case MUL: {
                byte a = cpu.getRegistre(operandes[0]).lire();
                byte b = cpu.getRegistre(operandes[1]).lire();
                int res = alu.multiplier(a, b);
                cpu.getRegistre(operandes[2]).ecrire((byte) ((res >> 8) & 0xFF));
                cpu.getRegistre(operandes[3]).ecrire((byte) (res & 0xFF));
                break;
            }

            case DIV: {
                byte a = cpu.getRegistre(operandes[0]).lire();
                byte b = cpu.getRegistre(operandes[1]).lire();
                int res = alu.diviser(a, b);
                cpu.getRegistre(operandes[2]).ecrire((byte) ((res >> 8) & 0xFF));
                cpu.getRegistre(operandes[3]).ecrire((byte) (res & 0xFF));
                break;
            }

            case OR: {
                byte a = cpu.getRegistre(operandes[0]).lire();
                byte b = cpu.getRegistre(operandes[1]).lire();
                cpu.getRegistre(operandes[2]).ecrire(alu.ouBinaire(a, b));
                break;
            }

            case AND: {
                byte a = cpu.getRegistre(operandes[0]).lire();
                byte b = cpu.getRegistre(operandes[1]).lire();
                cpu.getRegistre(operandes[2]).ecrire(alu.etBinaire(a, b));
                break;
            }

            case XOR: {
                byte a = cpu.getRegistre(operandes[0]).lire();
                byte b = cpu.getRegistre(operandes[1]).lire();
                cpu.getRegistre(operandes[2]).ecrire(alu.ouExclusif(a, b));
                break;
            }

            case JUMP: {
                cpu.setPc(operandes[0]);
                break;
            }

            case BEQ: {
                byte a = cpu.getRegistre(operandes[0]).lire();
                byte b = cpu.getRegistre(operandes[1]).lire();
                if (a == b) {
                    cpu.setPc(operandes[2]);
                }
                break;
            }

            case BNE: {
                byte a = cpu.getRegistre(operandes[0]).lire();
                byte b = cpu.getRegistre(operandes[1]).lire();
                if (a != b) {
                    cpu.setPc(operandes[2]);
                }
                break;
            }
            case BREAK: {
                cpu.arreter();
                break;
            }
            case DONNEE:

            case CHAINE:
                // Ces directives ne s'exécutent pas — elles servent l'assembleur
                // pour réserver et initialiser des octets en mémoire.
                break;
            default:
                throw new IllegalStateException("Type d'instruction inconnu : " + type);
        }
    }
}