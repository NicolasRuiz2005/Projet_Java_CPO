package Test_unitaire;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import simulateur.Simulateur;

class SimulateurTest {

    private Simulateur sim;

    @BeforeEach
    void setUp() {
        sim = new Simulateur();
    }

    // =========================================================
    // Groupe 1 : état initial
    // =========================================================

    @Test
    void etatInitial() {
        for (int i = 0; i < 16; i++) {
            assertEquals(0, sim.consulterRegistre(i), "R" + i + " devrait valoir 0");
        }
        assertEquals(0, sim.consulterMemoire(0));
        assertEquals(0, sim.consulterMemoire(1000));
        assertEquals(0, sim.consulterMemoire(65535));
    }

    // =========================================================
    // Groupe 2 : assembler()
    // =========================================================

    @Test
    void assembler_sansSaisirProgramme_lancerIllegalStateException() {
        assertThrows(IllegalStateException.class, () -> sim.assembler());
    }

    @Test
    void assembler_apressSaisirProgramme_neLancePasException() {
        sim.saisirProgramme("BREAK");
        assertDoesNotThrow(() -> sim.assembler());
    }

    // =========================================================
    // Groupe 3 : executerProgramme()
    // =========================================================

    @Test
    void executerProgramme_sansProgramme_lancerIllegalStateException() {
        assertThrows(IllegalStateException.class, () -> sim.executerProgramme());
    }

    @Test
    void executerProgramme_sansAssembler_lancerIllegalStateException() {
        sim.saisirProgramme("BREAK");
        assertThrows(IllegalStateException.class, () -> sim.executerProgramme());
    }

    @Test
    void executerProgramme_fluxNormal_neLancePasException() {
        sim.saisirProgramme("BREAK");
        sim.assembler();
        assertDoesNotThrow(() -> sim.executerProgramme());
    }

    // =========================================================
    // Groupe 4 : consulterRegistre() / modifierRegistre()
    // =========================================================

    @Test
    void consulterEtModifierRegistre_casNormaux() {
        assertEquals(0, sim.consulterRegistre(3));
        sim.modifierRegistre(5, (byte) 42);
        assertEquals(42, sim.consulterRegistre(5));
        sim.modifierRegistre(0, (byte) 10);
        sim.modifierRegistre(0, (byte) 20);
        assertEquals(20, sim.consulterRegistre(0));
    }

    @Test
    void consulterRegistre_numeroNegatif_lancerIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> sim.consulterRegistre(-1));
    }

    @Test
    void consulterRegistre_numero16_lancerIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> sim.consulterRegistre(16));
    }

    @Test
    void modifierRegistre_numeroNegatif_lancerIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> sim.modifierRegistre(-1, (byte) 0));
    }

    @Test
    void modifierRegistre_numero16_lancerIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> sim.modifierRegistre(16, (byte) 0));
    }

    // =========================================================
    // Groupe 5 : consulterMemoire() / modifierMemoire()
    // =========================================================

    @Test
    void consulterEtModifierMemoire_casNormaux() {
        assertEquals(0, sim.consulterMemoire(500));
        sim.modifierMemoire(100, (byte) 77);
        assertEquals(77, sim.consulterMemoire(100));
        sim.modifierMemoire(50, (byte) 5);
        sim.modifierMemoire(50, (byte) 9);
        assertEquals(9, sim.consulterMemoire(50));
    }

    @Test
    void consulterMemoire_adresseNegative_lancerIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> sim.consulterMemoire(-1));
    }

    @Test
    void consulterMemoire_adresse65536_lancerIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> sim.consulterMemoire(65536));
    }

    @Test
    void modifierMemoire_adresseNegative_lancerIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> sim.modifierMemoire(-1, (byte) 0));
    }

    @Test
    void modifierMemoire_adresse65536_lancerIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> sim.modifierMemoire(65536, (byte) 0));
    }

    // =========================================================
    // Groupe 6 : intégration end-to-end
    // =========================================================

    @Test
    void integration_loadEtSub_resultatDansRegistre() {
        sim.saisirProgramme(
            "LOAD R1, 3\n" +
            "LOAD R2, 1\n" +
            "SUB R1, R2, R3\n" +
            "BREAK\n"
        );
        sim.assembler();
        sim.executerProgramme();
        assertEquals(2, sim.consulterRegistre(3));
    }

    @Test
    void integration_loadEtStore_ecritureMemoireVerifiable() {
        sim.saisirProgramme(
            "LOAD R0, 55\n" +
            "STORE R0, [1000]\n" +
            "BREAK\n"
        );
        sim.assembler();
        sim.executerProgramme();
        assertEquals(55, sim.consulterMemoire(1000));
    }

    @Test
    void integration_loadMemoire_viaModifierMemoire() {
        sim.modifierMemoire(200, (byte) 99);
        sim.saisirProgramme(
            "LOAD R0, [200]\n" +
            "BREAK\n"
        );
        sim.assembler();
        sim.executerProgramme();
        assertEquals(99, sim.consulterRegistre(0));
    }

    @Test
    void integration_jump_sauteInstruction() {
        sim.saisirProgramme(
            "JUMP 2\n" +
            "LOAD R0, 99\n" +
            "LOAD R0, 42\n" +
            "BREAK\n"
        );
        sim.assembler();
        sim.executerProgramme();
        assertEquals(42, sim.consulterRegistre(0));
    }

    @Test
    void integration_beq_sautSiRegistresEgaux() {
        sim.saisirProgramme(
            "LOAD R0, 7\n" +
            "LOAD R1, 7\n" +
            "BEQ R0, R1, 4\n" +
            "LOAD R2, 99\n" +
            "BREAK\n"
        );
        sim.assembler();
        sim.executerProgramme();
        assertEquals(0, sim.consulterRegistre(2));
    }
}
