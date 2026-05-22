package Test_unitaire;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import materiel.CPU;
import materiel.Memoire;

class CPUTest {

    private Memoire memoire;
    private CPU cpu;

    @BeforeEach
    void setUp() {
        memoire = new Memoire();
        cpu = new CPU(memoire);
    }

    /** Écrit des octets consécutifs en mémoire à partir de l'adresse 0. */
    private void chargerBytes(byte... octets) {
        for (int i = 0; i < octets.length; i++) {
            memoire.ecrire(i, octets[i]);
        }
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

    // --- BREAK implicite (mémoire à zéro) ---

    @Test
    void executerProgramme_BREAK_immediat() {
        // La mémoire contient 0 partout → BREAK dès le premier octet
        cpu.executerProgramme();
        assertFalse(cpu.estEnRoute());
    }

    // --- LOAD_CONST ---

    @Test
    void executerProgramme_LOAD_CONST() {
        // LOAD R3, 42 → [1, 3, 42] ; BREAK implicite
        chargerBytes((byte) 1, (byte) 3, (byte) 42);
        cpu.executerProgramme();
        assertEquals(42, cpu.getRegistre(3).lire());
        // Valeur négative
        chargerBytes((byte) 1, (byte) 0, (byte) -50);
        cpu.executerProgramme();
        assertEquals((byte) -50, cpu.getRegistre(0).lire());
    }

    // --- LOAD_MEM ---

    @Test
    void executerProgramme_LOAD_MEM() {
        memoire.ecrire(500, (byte) 33);
        // LOAD R2, [500] → [2, 2, 1, 244] (500 = 0x01F4) ; BREAK implicite
        chargerBytes((byte) 2, (byte) 2, (byte) 1, (byte) 244);
        cpu.executerProgramme();
        assertEquals(33, cpu.getRegistre(2).lire());
        // La case mémoire source est inchangée
        assertEquals(33, memoire.lire(500));
    }

    // --- STORE ---

    @Test
    void executerProgramme_STORE() {
        cpu.getRegistre(5).ecrire((byte) 88);
        // STORE R5, [1000] → [3, 5, 3, 232] (1000 = 0x03E8) ; BREAK implicite
        chargerBytes((byte) 3, (byte) 5, (byte) 3, (byte) 232);
        cpu.executerProgramme();
        assertEquals(88, memoire.lire(1000));
        assertEquals(88, cpu.getRegistre(5).lire()); // registre source inchangé
    }

    // --- LOAD_INDEXE ---

    @Test
    void executerProgramme_LOAD_INDEXE() {
        // mem[105] = 55 ; R1 = 5 → LOAD_INDEXE R0, [100], R1 → R0 = mem[105]
        // Bytes: [14, 0, 0, 100, 1] ; BREAK implicite
        memoire.ecrire(105, (byte) 55);
        cpu.getRegistre(1).ecrire((byte) 5);
        chargerBytes((byte) 14, (byte) 0, (byte) 0, (byte) 100, (byte) 1);
        cpu.executerProgramme();
        assertEquals(55, cpu.getRegistre(0).lire());
    }

    // --- STORE_INDEXE ---

    @Test
    void executerProgramme_STORE_INDEXE() {
        // R0 = 88 ; R1 = 10 → STORE_INDEXE R0, [200], R1 → mem[210] = 88
        // Bytes: [15, 0, 0, 200, 1] ; BREAK implicite
        cpu.getRegistre(0).ecrire((byte) 88);
        cpu.getRegistre(1).ecrire((byte) 10);
        chargerBytes((byte) 15, (byte) 0, (byte) 0, (byte) 200, (byte) 1);
        cpu.executerProgramme();
        assertEquals(88, memoire.lire(210));
    }

    // --- ADD ---

    @Test
    void executerProgramme_ADD() {
        // LOAD R0, 10 ; LOAD R1, 20 ; ADD dest=R2 src1=R0 src2=R1
        // Bytes: [1,0,10, 1,1,20, 4,2,0,1]
        chargerBytes((byte) 1, (byte) 0, (byte) 10,
                     (byte) 1, (byte) 1, (byte) 20,
                     (byte) 4, (byte) 2, (byte) 0, (byte) 1);
        cpu.executerProgramme();
        assertEquals(30, cpu.getRegistre(2).lire());

        // Overflow : 127 + 1 → -128
        chargerBytes((byte) 1, (byte) 0, (byte) 127,
                     (byte) 1, (byte) 1, (byte) 1,
                     (byte) 4, (byte) 2, (byte) 0, (byte) 1);
        cpu.executerProgramme();
        assertEquals((byte) -128, cpu.getRegistre(2).lire());
    }

    // --- SUB ---

    @Test
    void executerProgramme_SUB() {
        // LOAD R0, 15 ; LOAD R1, 5 ; SUB dest=R2 src1=R0 src2=R1 → R2 = 10
        chargerBytes((byte) 1, (byte) 0, (byte) 15,
                     (byte) 1, (byte) 1, (byte) 5,
                     (byte) 5, (byte) 2, (byte) 0, (byte) 1);
        cpu.executerProgramme();
        assertEquals(10, cpu.getRegistre(2).lire());

        // Résultat négatif : 3 - 10 = -7
        chargerBytes((byte) 1, (byte) 0, (byte) 3,
                     (byte) 1, (byte) 1, (byte) 10,
                     (byte) 5, (byte) 2, (byte) 0, (byte) 1);
        cpu.executerProgramme();
        assertEquals((byte) -7, cpu.getRegistre(2).lire());
    }

    // --- MUL ---

    @Test
    void executerProgramme_MUL() {
        // LOAD R0, 4 ; LOAD R1, 5 ; MUL destH=R2 destL=R3 src1=R0 src2=R1 → 20
        // Bytes: [1,0,4, 1,1,5, 6,2,3,0,1]
        chargerBytes((byte) 1, (byte) 0, (byte) 4,
                     (byte) 1, (byte) 1, (byte) 5,
                     (byte) 6, (byte) 2, (byte) 3, (byte) 0, (byte) 1);
        cpu.executerProgramme();
        assertEquals((byte) 0,  cpu.getRegistre(2).lire()); // poids fort
        assertEquals((byte) 20, cpu.getRegistre(3).lire()); // poids faible

        // 20 * 20 = 400 → poids fort = 1, poids faible = 144
        chargerBytes((byte) 1, (byte) 0, (byte) 20,
                     (byte) 1, (byte) 1, (byte) 20,
                     (byte) 6, (byte) 2, (byte) 3, (byte) 0, (byte) 1);
        cpu.executerProgramme();
        assertEquals((byte) 1,   cpu.getRegistre(2).lire());
        assertEquals((byte) 144, cpu.getRegistre(3).lire());
    }

    // --- DIV ---

    @Test
    void executerProgramme_DIV() {
        // LOAD R0, 10 ; LOAD R1, 3 ; DIV quot=R2 reste=R3 src1=R0 src2=R1 → 3 r 1
        // Bytes: [1,0,10, 1,1,3, 7,2,3,0,1]
        chargerBytes((byte) 1, (byte) 0, (byte) 10,
                     (byte) 1, (byte) 1, (byte) 3,
                     (byte) 7, (byte) 2, (byte) 3, (byte) 0, (byte) 1);
        cpu.executerProgramme();
        assertEquals(3, cpu.getRegistre(2).lire()); // quotient
        assertEquals(1, cpu.getRegistre(3).lire()); // reste
    }

    // --- Opérations logiques ---

    @Test
    void executerProgramme_logique() {
        // OU : 5 | 10 = 15 → bytes: [1,0,5, 1,1,10, 8,2,0,1]
        chargerBytes((byte) 1, (byte) 0, (byte) 5,
                     (byte) 1, (byte) 1, (byte) 10,
                     (byte) 8, (byte) 2, (byte) 0, (byte) 1);
        cpu.executerProgramme();
        assertEquals(15, cpu.getRegistre(2).lire());

        // ET : 12 & 10 = 8 → bytes: [1,0,12, 1,1,10, 9,2,0,1]
        chargerBytes((byte) 1, (byte) 0, (byte) 12,
                     (byte) 1, (byte) 1, (byte) 10,
                     (byte) 9, (byte) 2, (byte) 0, (byte) 1);
        cpu.executerProgramme();
        assertEquals(8, cpu.getRegistre(2).lire());

        // XOR : 12 ^ 10 = 6 → bytes: [1,0,12, 1,1,10, 10,2,0,1]
        chargerBytes((byte) 1, (byte) 0, (byte) 12,
                     (byte) 1, (byte) 1, (byte) 10,
                     (byte) 10, (byte) 2, (byte) 0, (byte) 1);
        cpu.executerProgramme();
        assertEquals(6, cpu.getRegistre(2).lire());
    }

    // --- JUMP ---

    @Test
    void executerProgramme_JUMP() {
        // JUMP 6 ; LOAD R0, 99 (skipped) ; LOAD R0, 42 ; BREAK implicite
        // addr 0-2: [11, 0, 6]   addr 3-5: [1, 0, 99]   addr 6-8: [1, 0, 42]
        chargerBytes((byte) 11, (byte) 0, (byte) 6,
                     (byte) 1,  (byte) 0, (byte) 99,
                     (byte) 1,  (byte) 0, (byte) 42);
        cpu.executerProgramme();
        assertEquals(42, cpu.getRegistre(0).lire());
    }

    // --- BEQ ---

    @Test
    void executerProgramme_BEQ() {
        // R0=R1=7 → BEQ saute, LOAD R2, 99 est ignoré
        // addr 0-2: LOAD R0, 7 ; addr 3-5: LOAD R1, 7
        // addr 6-10: BEQ R0, R1, 14 → [12, 0, 1, 0, 14]
        // addr 11-13: LOAD R2, 99 (skipped) ; addr 14: BREAK implicite
        chargerBytes((byte) 1,  (byte) 0, (byte) 7,
                     (byte) 1,  (byte) 1, (byte) 7,
                     (byte) 12, (byte) 0, (byte) 1, (byte) 0, (byte) 14,
                     (byte) 1,  (byte) 2, (byte) 99);
        cpu.executerProgramme();
        assertEquals(0, cpu.getRegistre(2).lire()); // non chargé

        // R0≠R1 → BEQ ne saute pas, LOAD R2, 99 s'exécute
        chargerBytes((byte) 1,  (byte) 0, (byte) 5,
                     (byte) 1,  (byte) 1, (byte) 7,
                     (byte) 12, (byte) 0, (byte) 1, (byte) 0, (byte) 14,
                     (byte) 1,  (byte) 2, (byte) 99);
        cpu.executerProgramme();
        assertEquals(99, cpu.getRegistre(2).lire());
    }

    // --- BNE ---

    @Test
    void executerProgramme_BNE() {
        // R0≠R1 → BNE saute, LOAD R2, 99 est ignoré
        chargerBytes((byte) 1,  (byte) 0, (byte) 1,
                     (byte) 1,  (byte) 1, (byte) 2,
                     (byte) 13, (byte) 0, (byte) 1, (byte) 0, (byte) 14,
                     (byte) 1,  (byte) 2, (byte) 99);
        cpu.executerProgramme();
        assertEquals(0, cpu.getRegistre(2).lire()); // non chargé

        // R0=R1 → BNE ne saute pas, LOAD R2, 99 s'exécute
        chargerBytes((byte) 1,  (byte) 0, (byte) 5,
                     (byte) 1,  (byte) 1, (byte) 5,
                     (byte) 13, (byte) 0, (byte) 1, (byte) 0, (byte) 14,
                     (byte) 1,  (byte) 2, (byte) 99);
        cpu.executerProgramme();
        assertEquals(99, cpu.getRegistre(2).lire());
    }

    // --- Programme complet : LOAD + ADD + BREAK ---

    @Test
    void executerProgramme_sequenceComplete() {
        // R0=5, R1=3, ADD → R2=8
        chargerBytes((byte) 1, (byte) 0, (byte) 5,
                     (byte) 1, (byte) 1, (byte) 3,
                     (byte) 4, (byte) 2, (byte) 0, (byte) 1);
        cpu.executerProgramme();
        assertEquals(8, cpu.getRegistre(2).lire());
        assertFalse(cpu.estEnRoute());

        // Même si PC était à 99, executerProgramme repart de 0
        cpu.setPc(99);
        chargerBytes((byte) 1, (byte) 0, (byte) 7);
        cpu.executerProgramme();
        assertEquals(7, cpu.getRegistre(0).lire());
    }
}
