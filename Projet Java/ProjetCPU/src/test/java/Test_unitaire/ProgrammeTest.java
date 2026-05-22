package Test_unitaire;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import assembleur.Programme;

class ProgrammeTest {

    @Test
    void constructeur_etEtatInitial() {
        Programme prog = new Programme("LOAD R0, 5");
        assertEquals("LOAD R0, 5", prog.getCodeSource());
        assertFalse(prog.estAssemble());
    }

    @Test
    void marquerAssemble_passeFlagATrue() {
        Programme prog = new Programme("");
        assertFalse(prog.estAssemble());
        prog.marquerAssemble();
        assertTrue(prog.estAssemble());
    }

    @Test
    void codeSourceVide_accepte() {
        Programme prog = new Programme("");
        assertEquals("", prog.getCodeSource());
        assertFalse(prog.estAssemble());
    }
}
