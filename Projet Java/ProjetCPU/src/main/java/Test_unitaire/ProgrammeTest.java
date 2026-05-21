package Test_unitaire;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import assembleur.Programme;
import instructions.Instruction;
import instructions.TypeInstruction;

class ProgrammeTest {

    @Test
    void constructeur_etEtatInitial() {
        Programme prog = new Programme("LOAD R0, 5");
        assertEquals("LOAD R0, 5", prog.getCodeSource());
        assertFalse(prog.estAssemble());
        assertEquals(0, prog.nombreInstructions());
    }

    @Test
    void marquerAssemble_passeFlagATrue() {
        Programme prog = new Programme("");
        prog.marquerAssemble();
        assertTrue(prog.estAssemble());
    }

    @Test
    void gestionInstructions() {
        Programme prog = new Programme("");
        assertEquals(0, prog.nombreInstructions());
        Instruction inst = new Instruction(TypeInstruction.JUMP, 1, new int[]{5});
        prog.ajouterInstruction(inst);
        assertEquals(1, prog.nombreInstructions());
        prog.ajouterInstruction(new Instruction(TypeInstruction.BREAK, 2, new int[]{}));
        assertEquals(2, prog.nombreInstructions());
        assertSame(inst, prog.getInstruction(0));
    }
}
