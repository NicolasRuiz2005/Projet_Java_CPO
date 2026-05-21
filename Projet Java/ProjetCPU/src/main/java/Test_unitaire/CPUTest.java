package Test_unitaire;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import materiel.CPU;
import materiel.Memoire;
import assembleur.Programme;
import instructions.Instruction;
import instructions.TypeInstruction;

class CPUTest {

    private Memoire memoire;
    private CPU cpu;

    @BeforeEach
    void setUp() {
        memoire = new Memoire();
        cpu = new CPU(memoire);
    }

    // --- État initial ---

    @Test
    void constructeur() {
        assertEquals(0, cpu.getPc());
        assertFalse(cpu.estEnRoute());
        assertSame(memoire, cpu.getMemoire());
        assertNotNull(cpu.getALU());
        for (int i = 0; i < 16; i++) {
            assertNotNull(cpu.getRegistre(i));
            assertEquals(i, cpu.getRegistre(i).getNumero());
            assertEquals(0, cpu.getRegistre(i).lire());
        }
        assertNotNull(cpu.getRegistre(0));
        assertNotNull(cpu.getRegistre(15));
    }

    // --- getRegistre invalide ---

    @Test
    void getRegistre_numeroNegatif_lancerIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> cpu.getRegistre(-1));
    }

    @Test
    void getRegistre_numero16_lancerIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> cpu.getRegistre(16));
    }

    // --- PC ---

    @Test
    void pc_operations() {
        cpu.setPc(10);
        assertEquals(10, cpu.getPc());
        cpu.incrementerPC();
        assertEquals(11, cpu.getPc());
        cpu.incrementerPC();
        assertEquals(12, cpu.getPc());
    }

    // --- arreter ---

    @Test
    void arreter_maintientEnRouteAFalse_siDejaFalse() {
        cpu.arreter();
        assertFalse(cpu.estEnRoute());
    }

    // --- executerInstruction : LOAD_CONSTANTE ---

    @Test
    void executerInstruction_LOAD_CONSTANTE_chargeValeur() {
        cpu.executerInstruction(new Instruction(TypeInstruction.LOAD_CONSTANTE, 0, new int[]{0, 42}));
        assertEquals(42, cpu.getRegistre(0).lire());
    }

    // --- executerInstruction : mémoire directe ---

    @Test
    void executerInstruction_memoireDirecte() {
        // STORE : R1=99 → mem[200]
        cpu.getRegistre(1).ecrire((byte) 99);
        cpu.executerInstruction(new Instruction(TypeInstruction.STORE, 0, new int[]{1, 200}));
        assertEquals(99, memoire.lire(200));
        // LOAD_MEMOIRE : mem[300]=77 → R2
        memoire.ecrire(300, (byte) 77);
        cpu.executerInstruction(new Instruction(TypeInstruction.LOAD_MEMOIRE, 0, new int[]{2, 300}));
        assertEquals(77, cpu.getRegistre(2).lire());
    }

    // --- executerInstruction : mémoire indexée ---

    @Test
    void executerInstruction_memoireIndexee() {
        // LOAD_INDEXE : base=100, offset=R1=5 → mem[105]=55 dans R0
        memoire.ecrire(105, (byte) 55);
        cpu.getRegistre(1).ecrire((byte) 5);
        cpu.executerInstruction(new Instruction(TypeInstruction.LOAD_INDEXE, 0, new int[]{0, 100, 1}));
        assertEquals(55, cpu.getRegistre(0).lire());
        // STORE_INDEXE : R0=88, base=200, offset=R1=10 → mem[210]=88
        cpu.getRegistre(0).ecrire((byte) 88);
        cpu.getRegistre(1).ecrire((byte) 10);
        cpu.executerInstruction(new Instruction(TypeInstruction.STORE_INDEXE, 0, new int[]{0, 200, 1}));
        assertEquals(88, memoire.lire(210));
    }

    // --- executerInstruction : ADD et SUB ---

    @Test
    void executerInstruction_ADD_et_SUB() {
        // ADD : R0=10, R1=20 → R2=30
        cpu.getRegistre(0).ecrire((byte) 10);
        cpu.getRegistre(1).ecrire((byte) 20);
        cpu.executerInstruction(new Instruction(TypeInstruction.ADD, 0, new int[]{0, 1, 2}));
        assertEquals(30, cpu.getRegistre(2).lire());
        // SUB : R0=15, R1=5 → R2=10
        cpu.getRegistre(0).ecrire((byte) 15);
        cpu.getRegistre(1).ecrire((byte) 5);
        cpu.executerInstruction(new Instruction(TypeInstruction.SUB, 0, new int[]{0, 1, 2}));
        assertEquals(10, cpu.getRegistre(2).lire());
    }

    // --- executerInstruction : MUL et DIV ---

    @Test
    void executerInstruction_MUL_et_DIV() {
        // MUL : 4 * 5 = 20 → poids fort R2=0, poids faible R3=20
        cpu.getRegistre(0).ecrire((byte) 4);
        cpu.getRegistre(1).ecrire((byte) 5);
        cpu.executerInstruction(new Instruction(TypeInstruction.MUL, 0, new int[]{0, 1, 2, 3}));
        assertEquals((byte) 0,  cpu.getRegistre(2).lire());
        assertEquals((byte) 20, cpu.getRegistre(3).lire());
        // DIV : 10 / 3 → quotient R2=3, reste R3=1
        cpu.getRegistre(0).ecrire((byte) 10);
        cpu.getRegistre(1).ecrire((byte) 3);
        cpu.executerInstruction(new Instruction(TypeInstruction.DIV, 0, new int[]{0, 1, 2, 3}));
        assertEquals(3, cpu.getRegistre(2).lire());
        assertEquals(1, cpu.getRegistre(3).lire());
    }

    // --- executerInstruction : opérations logiques ---

    @Test
    void executerInstruction_logique() {
        // OR : 5 | 10 = 15
        cpu.getRegistre(0).ecrire((byte) 5);
        cpu.getRegistre(1).ecrire((byte) 10);
        cpu.executerInstruction(new Instruction(TypeInstruction.OR, 0, new int[]{0, 1, 2}));
        assertEquals(15, cpu.getRegistre(2).lire());
        // AND : 12 & 10 = 8
        cpu.getRegistre(0).ecrire((byte) 12);
        cpu.getRegistre(1).ecrire((byte) 10);
        cpu.executerInstruction(new Instruction(TypeInstruction.AND, 0, new int[]{0, 1, 2}));
        assertEquals(8, cpu.getRegistre(2).lire());
        // XOR : 12 ^ 10 = 6
        cpu.executerInstruction(new Instruction(TypeInstruction.XOR, 0, new int[]{0, 1, 2}));
        assertEquals(6, cpu.getRegistre(2).lire());
    }

    // --- executerInstruction : branchements ---

    @Test
    void executerInstruction_branchements() {
        // JUMP → PC = 5
        cpu.executerInstruction(new Instruction(TypeInstruction.JUMP, 0, new int[]{5}));
        assertEquals(5, cpu.getPc());
        // BEQ : R0==R1 → saute à 9
        cpu.getRegistre(0).ecrire((byte) 7);
        cpu.getRegistre(1).ecrire((byte) 7);
        cpu.executerInstruction(new Instruction(TypeInstruction.BEQ, 0, new int[]{0, 1, 9}));
        assertEquals(9, cpu.getPc());
        // BEQ : R0!=R1 → PC inchangé (reste à 9)
        cpu.getRegistre(0).ecrire((byte) 3);
        cpu.getRegistre(1).ecrire((byte) 7);
        cpu.executerInstruction(new Instruction(TypeInstruction.BEQ, 0, new int[]{0, 1, 99}));
        assertEquals(9, cpu.getPc());
        // BNE : R0!=R1 → saute à 4
        cpu.executerInstruction(new Instruction(TypeInstruction.BNE, 0, new int[]{0, 1, 4}));
        assertEquals(4, cpu.getPc());
        // BNE : R0==R1 → PC inchangé (reste à 4)
        cpu.getRegistre(0).ecrire((byte) 5);
        cpu.getRegistre(1).ecrire((byte) 5);
        cpu.executerInstruction(new Instruction(TypeInstruction.BNE, 0, new int[]{0, 1, 99}));
        assertEquals(4, cpu.getPc());
    }

    // --- executerInstruction : BREAK ---

    @Test
    void executerInstruction_BREAK_arreteExecution() {
        cpu.executerInstruction(new Instruction(TypeInstruction.BREAK, 0, new int[]{}));
        assertFalse(cpu.estEnRoute());
    }

    // --- executerProgramme : erreurs ---

    @Test
    void executerProgramme_sansProgramme_lancerIllegalStateException() {
        assertThrows(IllegalStateException.class, () -> cpu.executerProgramme());
    }

    @Test
    void executerProgramme_programmeNonAssemble_lancerIllegalStateException() {
        Programme prog = new Programme("");
        prog.ajouterInstruction(new Instruction(TypeInstruction.BREAK, 0, new int[]{}));
        cpu.chargerProgramme(prog);
        assertThrows(IllegalStateException.class, () -> cpu.executerProgramme());
    }

    // --- executerProgramme : cas normaux ---

    @Test
    void executerProgramme() {
        // R0=5, R1=3, ADD → R2=8, BREAK
        Programme prog = new Programme("");
        prog.ajouterInstruction(new Instruction(TypeInstruction.LOAD_CONSTANTE, 0, new int[]{0, 5}));
        prog.ajouterInstruction(new Instruction(TypeInstruction.LOAD_CONSTANTE, 1, new int[]{1, 3}));
        prog.ajouterInstruction(new Instruction(TypeInstruction.ADD,            2, new int[]{0, 1, 2}));
        prog.ajouterInstruction(new Instruction(TypeInstruction.BREAK,          3, new int[]{}));
        prog.marquerAssemble();
        cpu.chargerProgramme(prog);
        cpu.executerProgramme();
        assertEquals(8, cpu.getRegistre(2).lire());
        assertFalse(cpu.estEnRoute());

        // Même si PC était à 99, executerProgramme repart de 0
        cpu.setPc(99);
        Programme prog2 = new Programme("");
        prog2.ajouterInstruction(new Instruction(TypeInstruction.LOAD_CONSTANTE, 0, new int[]{0, 7}));
        prog2.ajouterInstruction(new Instruction(TypeInstruction.BREAK,          1, new int[]{}));
        prog2.marquerAssemble();
        cpu.chargerProgramme(prog2);
        cpu.executerProgramme();
        assertEquals(7, cpu.getRegistre(0).lire());

        // JUMP saute l'instruction à l'index 1
        Programme prog3 = new Programme("");
        prog3.ajouterInstruction(new Instruction(TypeInstruction.JUMP,           0, new int[]{2}));
        prog3.ajouterInstruction(new Instruction(TypeInstruction.LOAD_CONSTANTE, 1, new int[]{0, 99}));
        prog3.ajouterInstruction(new Instruction(TypeInstruction.LOAD_CONSTANTE, 2, new int[]{0, 42}));
        prog3.ajouterInstruction(new Instruction(TypeInstruction.BREAK,          3, new int[]{}));
        prog3.marquerAssemble();
        cpu.chargerProgramme(prog3);
        cpu.executerProgramme();
        assertEquals(42, cpu.getRegistre(0).lire());
    }
}
