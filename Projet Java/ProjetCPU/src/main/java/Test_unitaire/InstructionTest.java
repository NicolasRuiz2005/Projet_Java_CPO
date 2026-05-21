package Test_unitaire;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import instructions.Instruction;
import instructions.TypeInstruction;
import materiel.CPU;
import materiel.Memoire;

class InstructionTest {

    private Memoire memoire;
    private CPU cpu;

    @BeforeEach
    void setUp() {
        memoire = new Memoire();
        cpu = new CPU(memoire);
    }

    // =========================================================
    // Constructeur et getters
    // =========================================================

    @Test
    void constructeur_getters() {
        Instruction inst = new Instruction(TypeInstruction.ADD, 7, new int[]{0, 1, 2});
        assertEquals(TypeInstruction.ADD, inst.getType());
        assertEquals(7, inst.getLigne());
        assertArrayEquals(new int[]{0, 1, 2}, inst.getOperandes());
    }

    // =========================================================
    // LOAD_CONSTANTE
    // =========================================================

    @Test
    void executer_LOAD_CONSTANTE() {
        // Valeur positive
        new Instruction(TypeInstruction.LOAD_CONSTANTE, 0, new int[]{3, 100}).executer(cpu, memoire);
        assertEquals(100, cpu.getRegistre(3).lire());
        // Valeur négative
        new Instruction(TypeInstruction.LOAD_CONSTANTE, 0, new int[]{0, -50}).executer(cpu, memoire);
        assertEquals((byte) -50, cpu.getRegistre(0).lire());
        // Charge zéro (efface une valeur existante)
        cpu.getRegistre(1).ecrire((byte) 99);
        new Instruction(TypeInstruction.LOAD_CONSTANTE, 0, new int[]{1, 0}).executer(cpu, memoire);
        assertEquals(0, cpu.getRegistre(1).lire());
    }

    // =========================================================
    // LOAD_MEMOIRE
    // =========================================================

    @Test
    void executer_LOAD_MEMOIRE() {
        memoire.ecrire(500, (byte) 33);
        new Instruction(TypeInstruction.LOAD_MEMOIRE, 0, new int[]{2, 500}).executer(cpu, memoire);
        assertEquals(33, cpu.getRegistre(2).lire());
        // La case mémoire source n'est pas modifiée
        assertEquals(33, memoire.lire(500));
    }

    // =========================================================
    // STORE
    // =========================================================

    @Test
    void executer_STORE() {
        cpu.getRegistre(5).ecrire((byte) 88);
        new Instruction(TypeInstruction.STORE, 0, new int[]{5, 1000}).executer(cpu, memoire);
        assertEquals(88, memoire.lire(1000));
        // Le registre source n'est pas modifié
        assertEquals(88, cpu.getRegistre(5).lire());
    }

    // =========================================================
    // LOAD_INDEXE
    // =========================================================

    @Test
    void executer_LOAD_INDEXE() {
        // Offset positif : base=100, offset=10 → adresse 110
        memoire.ecrire(110, (byte) 66);
        cpu.getRegistre(1).ecrire((byte) 10);
        new Instruction(TypeInstruction.LOAD_INDEXE, 0, new int[]{0, 100, 1}).executer(cpu, memoire);
        assertEquals(66, cpu.getRegistre(0).lire());
        // Offset zéro : lit à l'adresse de base
        memoire.ecrire(200, (byte) 11);
        cpu.getRegistre(2).ecrire((byte) 0);
        new Instruction(TypeInstruction.LOAD_INDEXE, 0, new int[]{0, 200, 2}).executer(cpu, memoire);
        assertEquals(11, cpu.getRegistre(0).lire());
    }

    // =========================================================
    // STORE_INDEXE
    // =========================================================

    @Test
    void executer_STORE_INDEXE() {
        // Offset positif : base=300, offset=5 → adresse 305
        cpu.getRegistre(0).ecrire((byte) 77);
        cpu.getRegistre(1).ecrire((byte) 5);
        new Instruction(TypeInstruction.STORE_INDEXE, 0, new int[]{0, 300, 1}).executer(cpu, memoire);
        assertEquals(77, memoire.lire(305));
        // Offset zéro : écrit à l'adresse de base
        cpu.getRegistre(0).ecrire((byte) 22);
        cpu.getRegistre(3).ecrire((byte) 0);
        new Instruction(TypeInstruction.STORE_INDEXE, 0, new int[]{0, 400, 3}).executer(cpu, memoire);
        assertEquals(22, memoire.lire(400));
    }

    // =========================================================
    // ADD
    // =========================================================

    @Test
    void executer_ADD() {
        // Cas normal
        cpu.getRegistre(0).ecrire((byte) 25);
        cpu.getRegistre(1).ecrire((byte) 17);
        new Instruction(TypeInstruction.ADD, 0, new int[]{0, 1, 2}).executer(cpu, memoire);
        assertEquals(42, cpu.getRegistre(2).lire());
        // Overflow byte : 127 + 1 → -128
        cpu.getRegistre(0).ecrire((byte) 127);
        cpu.getRegistre(1).ecrire((byte) 1);
        new Instruction(TypeInstruction.ADD, 0, new int[]{0, 1, 2}).executer(cpu, memoire);
        assertEquals((byte) -128, cpu.getRegistre(2).lire());
        // Destination = source gauche
        cpu.getRegistre(0).ecrire((byte) 5);
        cpu.getRegistre(1).ecrire((byte) 3);
        new Instruction(TypeInstruction.ADD, 0, new int[]{0, 1, 0}).executer(cpu, memoire);
        assertEquals(8, cpu.getRegistre(0).lire());
    }

    // =========================================================
    // SUB
    // =========================================================

    @Test
    void executer_SUB() {
        // Résultat positif
        cpu.getRegistre(0).ecrire((byte) 20);
        cpu.getRegistre(1).ecrire((byte) 8);
        new Instruction(TypeInstruction.SUB, 0, new int[]{0, 1, 2}).executer(cpu, memoire);
        assertEquals(12, cpu.getRegistre(2).lire());
        // Résultat négatif
        cpu.getRegistre(0).ecrire((byte) 3);
        cpu.getRegistre(1).ecrire((byte) 10);
        new Instruction(TypeInstruction.SUB, 0, new int[]{0, 1, 2}).executer(cpu, memoire);
        assertEquals((byte) -7, cpu.getRegistre(2).lire());
    }

    // =========================================================
    // MUL
    // =========================================================

    @Test
    void executer_MUL() {
        // 3 * 6 = 18 → poids fort = 0, poids faible = 18
        cpu.getRegistre(0).ecrire((byte) 3);
        cpu.getRegistre(1).ecrire((byte) 6);
        new Instruction(TypeInstruction.MUL, 0, new int[]{0, 1, 2, 3}).executer(cpu, memoire);
        assertEquals((byte) 0,  cpu.getRegistre(2).lire());
        assertEquals((byte) 18, cpu.getRegistre(3).lire());
        // 20 * 20 = 400 → poids fort = 1, poids faible = 144
        cpu.getRegistre(0).ecrire((byte) 20);
        cpu.getRegistre(1).ecrire((byte) 20);
        new Instruction(TypeInstruction.MUL, 0, new int[]{0, 1, 2, 3}).executer(cpu, memoire);
        assertEquals((byte) 1,   cpu.getRegistre(2).lire());
        assertEquals((byte) 144, cpu.getRegistre(3).lire());
        // Multiplication par zéro
        cpu.getRegistre(0).ecrire((byte) 50);
        cpu.getRegistre(1).ecrire((byte) 0);
        new Instruction(TypeInstruction.MUL, 0, new int[]{0, 1, 2, 3}).executer(cpu, memoire);
        assertEquals((byte) 0, cpu.getRegistre(2).lire());
        assertEquals((byte) 0, cpu.getRegistre(3).lire());
    }

    // =========================================================
    // DIV
    // =========================================================

    @Test
    void executer_DIV_casNormal() {
        // 17 / 5 → quotient=3, reste=2
        cpu.getRegistre(0).ecrire((byte) 17);
        cpu.getRegistre(1).ecrire((byte) 5);
        new Instruction(TypeInstruction.DIV, 0, new int[]{0, 1, 2, 3}).executer(cpu, memoire);
        assertEquals(3, cpu.getRegistre(2).lire());
        assertEquals(2, cpu.getRegistre(3).lire());
        // 15 / 3 → quotient=5, reste=0
        cpu.getRegistre(0).ecrire((byte) 15);
        cpu.getRegistre(1).ecrire((byte) 3);
        new Instruction(TypeInstruction.DIV, 0, new int[]{0, 1, 2, 3}).executer(cpu, memoire);
        assertEquals(5, cpu.getRegistre(2).lire());
        assertEquals(0, cpu.getRegistre(3).lire());
    }

    @Test
    void executer_DIV_parZero_lancerArithmeticException() {
        cpu.getRegistre(0).ecrire((byte) 10);
        cpu.getRegistre(1).ecrire((byte) 0);
        Instruction inst = new Instruction(TypeInstruction.DIV, 0, new int[]{0, 1, 2, 3});
        assertThrows(ArithmeticException.class, () -> inst.executer(cpu, memoire));
    }

    // =========================================================
    // OR
    // =========================================================

    @Test
    void executer_OR() {
        // 0b0011 | 0b0101 = 0b0111 = 7
        cpu.getRegistre(0).ecrire((byte) 3);
        cpu.getRegistre(1).ecrire((byte) 5);
        new Instruction(TypeInstruction.OR, 0, new int[]{0, 1, 2}).executer(cpu, memoire);
        assertEquals(7, cpu.getRegistre(2).lire());
    }

    // =========================================================
    // AND
    // =========================================================

    @Test
    void executer_AND() {
        // 0b1111 & 0b0101 = 0b0101 = 5
        cpu.getRegistre(0).ecrire((byte) 15);
        cpu.getRegistre(1).ecrire((byte) 5);
        new Instruction(TypeInstruction.AND, 0, new int[]{0, 1, 2}).executer(cpu, memoire);
        assertEquals(5, cpu.getRegistre(2).lire());
    }

    // =========================================================
    // XOR
    // =========================================================

    @Test
    void executer_XOR() {
        // 0b1010 ^ 0b1100 = 0b0110 = 6
        cpu.getRegistre(0).ecrire((byte) 10);
        cpu.getRegistre(1).ecrire((byte) 12);
        new Instruction(TypeInstruction.XOR, 0, new int[]{0, 1, 2}).executer(cpu, memoire);
        assertEquals(6, cpu.getRegistre(2).lire());
        // XOR avec soi-même → 0
        cpu.getRegistre(0).ecrire((byte) 42);
        new Instruction(TypeInstruction.XOR, 0, new int[]{0, 0, 1}).executer(cpu, memoire);
        assertEquals(0, cpu.getRegistre(1).lire());
    }

    // =========================================================
    // JUMP
    // =========================================================

    @Test
    void executer_JUMP() {
        // Saut vers un index quelconque
        new Instruction(TypeInstruction.JUMP, 0, new int[]{12}).executer(cpu, memoire);
        assertEquals(12, cpu.getPc());
        // Saut vers zéro (réinitialise le PC)
        cpu.setPc(8);
        new Instruction(TypeInstruction.JUMP, 0, new int[]{0}).executer(cpu, memoire);
        assertEquals(0, cpu.getPc());
    }

    // =========================================================
    // BEQ
    // =========================================================

    @Test
    void executer_BEQ() {
        // Saute si égaux
        cpu.getRegistre(0).ecrire((byte) 10);
        cpu.getRegistre(1).ecrire((byte) 10);
        new Instruction(TypeInstruction.BEQ, 0, new int[]{0, 1, 5}).executer(cpu, memoire);
        assertEquals(5, cpu.getPc());
        // Ne saute pas si différents
        cpu.setPc(0);
        cpu.getRegistre(1).ecrire((byte) 20);
        new Instruction(TypeInstruction.BEQ, 0, new int[]{0, 1, 5}).executer(cpu, memoire);
        assertEquals(0, cpu.getPc());
        // Les deux à zéro (valeur initiale) → doit sauter
        cpu.setPc(0);
        cpu.getRegistre(0).ecrire((byte) 0);
        cpu.getRegistre(1).ecrire((byte) 0);
        new Instruction(TypeInstruction.BEQ, 0, new int[]{0, 1, 3}).executer(cpu, memoire);
        assertEquals(3, cpu.getPc());
    }

    // =========================================================
    // BNE
    // =========================================================

    @Test
    void executer_BNE() {
        // Saute si différents
        cpu.getRegistre(0).ecrire((byte) 1);
        cpu.getRegistre(1).ecrire((byte) 2);
        new Instruction(TypeInstruction.BNE, 0, new int[]{0, 1, 7}).executer(cpu, memoire);
        assertEquals(7, cpu.getPc());
        // Ne saute pas si égaux
        cpu.setPc(0);
        cpu.getRegistre(0).ecrire((byte) 5);
        cpu.getRegistre(1).ecrire((byte) 5);
        new Instruction(TypeInstruction.BNE, 0, new int[]{0, 1, 7}).executer(cpu, memoire);
        assertEquals(0, cpu.getPc());
    }

    // =========================================================
    // BREAK
    // =========================================================

    @Test
    void executer_BREAK_arreteCPU() {
        new Instruction(TypeInstruction.BREAK, 0, new int[]{}).executer(cpu, memoire);
        assertFalse(cpu.estEnRoute());
    }

    // =========================================================
    // DONNEE et CHAINE (directives sans effet à l'exécution)
    // =========================================================

    @Test
    void executer_DONNEE_et_CHAINE() {
        cpu.getRegistre(0).ecrire((byte) 55);
        memoire.ecrire(0, (byte) 11);
        new Instruction(TypeInstruction.DONNEE, 0, new int[]{}).executer(cpu, memoire);
        assertEquals(55, cpu.getRegistre(0).lire());
        assertEquals(11, memoire.lire(0));
        assertEquals(0, cpu.getPc());
        assertFalse(cpu.estEnRoute());

        cpu.getRegistre(1).ecrire((byte) 33);
        memoire.ecrire(5, (byte) 22);
        new Instruction(TypeInstruction.CHAINE, 0, new int[]{}).executer(cpu, memoire);
        assertEquals(33, cpu.getRegistre(1).lire());
        assertEquals(22, memoire.lire(5));
        assertEquals(0, cpu.getPc());
        assertFalse(cpu.estEnRoute());
    }
}
