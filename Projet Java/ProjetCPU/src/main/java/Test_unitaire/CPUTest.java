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
    void constructeur_pcInitialAZero() {
        assertEquals(0, cpu.getPc());
    }

    @Test
    void constructeur_pasDemarre() {
        assertFalse(cpu.estEnRoute());
    }

    @Test
    void constructeur_16RegistresInitialisesAZero() {
        for (int i = 0; i < 16; i++) {
            assertNotNull(cpu.getRegistre(i));
            assertEquals(i, cpu.getRegistre(i).getNumero());
            assertEquals(0, cpu.getRegistre(i).lire());
        }
    }

    @Test
    void constructeur_memoireEstLaMemeReference() {
        assertSame(memoire, cpu.getMemoire());
    }

    @Test
    void constructeur_aluNonNull() {
        assertNotNull(cpu.getALU());
    }

    // --- getRegistre ---

    @Test
    void getRegistre_premiereCase_valide() {
        assertNotNull(cpu.getRegistre(0));
    }

    @Test
    void getRegistre_derniereCase_valide() {
        assertNotNull(cpu.getRegistre(15));
    }

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
    void setPc_modifieCompteur() {
        cpu.setPc(10);
        assertEquals(10, cpu.getPc());
    }

    @Test
    void incrementerPC_augmenteDe1() {
        cpu.incrementerPC();
        assertEquals(1, cpu.getPc());
        cpu.incrementerPC();
        assertEquals(2, cpu.getPc());
    }

    // --- arreter / estEnRoute ---

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

    // --- executerInstruction : STORE / LOAD_MEMOIRE ---

    @Test
    void executerInstruction_STORE_ecritDansMemoire() {
        cpu.getRegistre(1).ecrire((byte) 99);
        cpu.executerInstruction(new Instruction(TypeInstruction.STORE, 0, new int[]{1, 200}));
        assertEquals(99, memoire.lire(200));
    }

    @Test
    void executerInstruction_LOAD_MEMOIRE_litDepuisMemoire() {
        memoire.ecrire(300, (byte) 77);
        cpu.executerInstruction(new Instruction(TypeInstruction.LOAD_MEMOIRE, 0, new int[]{2, 300}));
        assertEquals(77, cpu.getRegistre(2).lire());
    }

    // --- executerInstruction : LOAD_INDEXE / STORE_INDEXE ---

    @Test
    void executerInstruction_LOAD_INDEXE_litAvecOffset() {
        memoire.ecrire(105, (byte) 55);
        cpu.getRegistre(1).ecrire((byte) 5);
        cpu.executerInstruction(new Instruction(TypeInstruction.LOAD_INDEXE, 0, new int[]{0, 100, 1}));
        assertEquals(55, cpu.getRegistre(0).lire());
    }

    @Test
    void executerInstruction_STORE_INDEXE_ecritAvecOffset() {
        cpu.getRegistre(0).ecrire((byte) 88);
        cpu.getRegistre(1).ecrire((byte) 10);
        cpu.executerInstruction(new Instruction(TypeInstruction.STORE_INDEXE, 0, new int[]{0, 200, 1}));
        assertEquals(88, memoire.lire(210));
    }

    // --- executerInstruction : opérations arithmétiques ---

    @Test
    void executerInstruction_ADD_additionneRegistres() {
        cpu.getRegistre(0).ecrire((byte) 10);
        cpu.getRegistre(1).ecrire((byte) 20);
        cpu.executerInstruction(new Instruction(TypeInstruction.ADD, 0, new int[]{0, 1, 2}));
        assertEquals(30, cpu.getRegistre(2).lire());
    }

    @Test
    void executerInstruction_SUB_soustraitRegistres() {
        cpu.getRegistre(0).ecrire((byte) 15);
        cpu.getRegistre(1).ecrire((byte) 5);
        cpu.executerInstruction(new Instruction(TypeInstruction.SUB, 0, new int[]{0, 1, 2}));
        assertEquals(10, cpu.getRegistre(2).lire());
    }

    @Test
    void executerInstruction_MUL_poidsfortEtPoidsfaible() {
        // 4 * 5 = 20 : poids fort = 0, poids faible = 20
        cpu.getRegistre(0).ecrire((byte) 4);
        cpu.getRegistre(1).ecrire((byte) 5);
        cpu.executerInstruction(new Instruction(TypeInstruction.MUL, 0, new int[]{0, 1, 2, 3}));
        assertEquals((byte) 0,  cpu.getRegistre(2).lire());
        assertEquals((byte) 20, cpu.getRegistre(3).lire());
    }

    @Test
    void executerInstruction_DIV_quotientEtReste() {
        // 10 / 3 → quotient=3, reste=1
        cpu.getRegistre(0).ecrire((byte) 10);
        cpu.getRegistre(1).ecrire((byte) 3);
        cpu.executerInstruction(new Instruction(TypeInstruction.DIV, 0, new int[]{0, 1, 2, 3}));
        assertEquals(3, cpu.getRegistre(2).lire());
        assertEquals(1, cpu.getRegistre(3).lire());
    }

    // --- executerInstruction : opérations logiques ---

    @Test
    void executerInstruction_OR_ouBinaire() {
        cpu.getRegistre(0).ecrire((byte) 5);
        cpu.getRegistre(1).ecrire((byte) 10);
        cpu.executerInstruction(new Instruction(TypeInstruction.OR, 0, new int[]{0, 1, 2}));
        assertEquals(15, cpu.getRegistre(2).lire());
    }

    @Test
    void executerInstruction_AND_etBinaire() {
        cpu.getRegistre(0).ecrire((byte) 12);
        cpu.getRegistre(1).ecrire((byte) 10);
        cpu.executerInstruction(new Instruction(TypeInstruction.AND, 0, new int[]{0, 1, 2}));
        assertEquals(8, cpu.getRegistre(2).lire());
    }

    @Test
    void executerInstruction_XOR_ouExclusif() {
        cpu.getRegistre(0).ecrire((byte) 12);
        cpu.getRegistre(1).ecrire((byte) 10);
        cpu.executerInstruction(new Instruction(TypeInstruction.XOR, 0, new int[]{0, 1, 2}));
        assertEquals(6, cpu.getRegistre(2).lire());
    }

    // --- executerInstruction : branchements ---

    @Test
    void executerInstruction_JUMP_modifiePC() {
        cpu.executerInstruction(new Instruction(TypeInstruction.JUMP, 0, new int[]{5}));
        assertEquals(5, cpu.getPc());
    }

    @Test
    void executerInstruction_BEQ_saute_siRegistresEgaux() {
        cpu.getRegistre(0).ecrire((byte) 7);
        cpu.getRegistre(1).ecrire((byte) 7);
        cpu.executerInstruction(new Instruction(TypeInstruction.BEQ, 0, new int[]{0, 1, 9}));
        assertEquals(9, cpu.getPc());
    }

    @Test
    void executerInstruction_BEQ_neSautePas_siDifferents() {
        cpu.getRegistre(0).ecrire((byte) 3);
        cpu.getRegistre(1).ecrire((byte) 7);
        cpu.executerInstruction(new Instruction(TypeInstruction.BEQ, 0, new int[]{0, 1, 9}));
        assertEquals(0, cpu.getPc());
    }

    @Test
    void executerInstruction_BNE_saute_siDifferents() {
        cpu.getRegistre(0).ecrire((byte) 3);
        cpu.getRegistre(1).ecrire((byte) 7);
        cpu.executerInstruction(new Instruction(TypeInstruction.BNE, 0, new int[]{0, 1, 4}));
        assertEquals(4, cpu.getPc());
    }

    @Test
    void executerInstruction_BNE_neSautePas_siEgaux() {
        cpu.getRegistre(0).ecrire((byte) 5);
        cpu.getRegistre(1).ecrire((byte) 5);
        cpu.executerInstruction(new Instruction(TypeInstruction.BNE, 0, new int[]{0, 1, 4}));
        assertEquals(0, cpu.getPc());
    }

    // --- executerInstruction : BREAK ---

    @Test
    void executerInstruction_BREAK_arreteExecution() {
        cpu.executerInstruction(new Instruction(TypeInstruction.BREAK, 0, new int[]{}));
        assertFalse(cpu.estEnRoute());
    }

    // --- executerProgramme ---

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

    @Test
    void executerProgramme_miniProgramme_loadEtAdd() {
        // R0=5, R1=3, ADD R0+R1→R2, BREAK → R2 doit valoir 8
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
    }

    @Test
    void executerProgramme_reinitialisePC() {
        // Même si le PC était à 99 avant, executerProgramme repart de 0
        cpu.setPc(99);
        Programme prog = new Programme("");
        prog.ajouterInstruction(new Instruction(TypeInstruction.LOAD_CONSTANTE, 0, new int[]{0, 7}));
        prog.ajouterInstruction(new Instruction(TypeInstruction.BREAK,          1, new int[]{}));
        prog.marquerAssemble();
        cpu.chargerProgramme(prog);
        cpu.executerProgramme();
        assertEquals(7, cpu.getRegistre(0).lire());
    }

    @Test
    void executerProgramme_jump_sauteInstructions() {
        // Index 0 : JUMP vers index 2
        // Index 1 : LOAD_CONSTANTE R0=99 (ne doit PAS s'exécuter)
        // Index 2 : LOAD_CONSTANTE R0=42
        // Index 3 : BREAK
        Programme prog = new Programme("");
        prog.ajouterInstruction(new Instruction(TypeInstruction.JUMP,           0, new int[]{2}));
        prog.ajouterInstruction(new Instruction(TypeInstruction.LOAD_CONSTANTE, 1, new int[]{0, 99}));
        prog.ajouterInstruction(new Instruction(TypeInstruction.LOAD_CONSTANTE, 2, new int[]{0, 42}));
        prog.ajouterInstruction(new Instruction(TypeInstruction.BREAK,          3, new int[]{}));
        prog.marquerAssemble();
        cpu.chargerProgramme(prog);
        cpu.executerProgramme();
        assertEquals(42, cpu.getRegistre(0).lire());
    }
}
