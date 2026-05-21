package Test_unitaire;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import assembleur.Programme;
import instructions.Instruction;
import instructions.TypeInstruction;

class ProgrammeTest {

    @Test
    void constructeur_codeSourceCorrect() {
        Programme prog = new Programme("LOAD R0, 5");
        assertEquals("LOAD R0, 5", prog.getCodeSource());
    }

    @Test
    void estAssemble_falseAuDepart() {
        Programme prog = new Programme("");
        assertFalse(prog.estAssemble());
    }

    @Test
    void marquerAssemble_passeFlagATrue() {
        Programme prog = new Programme("");
        prog.marquerAssemble();
        assertTrue(prog.estAssemble());
    }

    @Test
    void nombreInstructions_listeVide_retourneZero() {
        Programme prog = new Programme("");
        assertEquals(0, prog.nombreInstructions());
    }

    @Test
    void ajouterInstruction_incrementeNombreInstructions() {
        Programme prog = new Programme("");
        prog.ajouterInstruction(new Instruction(TypeInstruction.BREAK, 1, new int[]{}));
        assertEquals(1, prog.nombreInstructions());
        prog.ajouterInstruction(new Instruction(TypeInstruction.BREAK, 2, new int[]{}));
        assertEquals(2, prog.nombreInstructions());
    }

    @Test
    void getInstruction_retourneLaBonneInstruction() {
        Programme prog = new Programme("");
        Instruction inst = new Instruction(TypeInstruction.JUMP, 1, new int[]{5});
        prog.ajouterInstruction(inst);
        assertSame(inst, prog.getInstruction(0));
    }
}
