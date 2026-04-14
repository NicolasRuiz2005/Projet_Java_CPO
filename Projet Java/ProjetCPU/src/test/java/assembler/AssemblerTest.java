package assembler;

import cpu.CPU;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires de l'{@link Assembler}.
 * Vérifie que le code machine généré est correct pour chaque instruction.
 */
class AssemblerTest {

    private CPU cpu;
    private Assembler asm;

    @BeforeEach
    void setUp() {
        cpu = new CPU();
        asm = new Assembler(cpu.getMemory());
    }

    @Test
    void testBreak() {
        asm.assemble("break");
        assertEquals(InstructionSet.BREAK, cpu.getMemory().read(0));
        assertEquals(1, asm.getWritePointer());
    }

    @Test
    void testLoadConstante() {
        asm.assemble("load r2, 5");
        assertEquals(InstructionSet.LOAD_CONST, cpu.getMemory().read(0));
        assertEquals(2, cpu.getMemory().read(1));
        assertEquals(5, cpu.getMemory().read(2));
        assertEquals(3, asm.getWritePointer());
    }

    @Test
    void testLoadMemoire() {
        asm.assemble("load r2, @100");
        assertEquals(InstructionSet.LOAD_MEM, cpu.getMemory().read(0));
        assertEquals(2,   cpu.getMemory().read(1));
        assertEquals(0,   cpu.getMemory().read(2)); // adresse haute
        assertEquals(100, cpu.getMemory().read(3) & 0xFF); // adresse basse
    }

    @Test
    void testLoadIndexe() {
        asm.assemble("load r0, @0x0100, r1");
        assertEquals(InstructionSet.LOAD_IDX, cpu.getMemory().read(0));
        assertEquals(0, cpu.getMemory().read(1)); // reg dest
        assertEquals(1, cpu.getMemory().read(2)); // adresse haute
        assertEquals(0, cpu.getMemory().read(3)); // adresse basse
        assertEquals(1, cpu.getMemory().read(4)); // reg index
    }

    @Test
    void testAdresseHexadecimale() {
        asm.assemble("load r0, @0x6500");
        // 0x6500 = 101*256 + 0 → high=101, low=0
        assertEquals(101, cpu.getMemory().read(2) & 0xFF);
        assertEquals(0,   cpu.getMemory().read(3) & 0xFF);
    }

    @Test
    void testLabelResolution() {
        asm.assemble("jump @cible\ncible:\nbreak");
        // jump = opcode(1) + addr(2) = 3 bytes → cible est à l'adresse 3
        assertEquals(3, asm.getLabelAddress("cible"));
    }

    @Test
    void testCommentaireIgnore() {
        asm.assemble("; ceci est un commentaire\nbreak ; fin");
        assertEquals(InstructionSet.BREAK, cpu.getMemory().read(0));
        assertEquals(1, asm.getWritePointer());
    }

    @Test
    void testData() {
        asm.assemble("break\ndata 1, 2, 3");
        assertEquals(1, cpu.getMemory().read(1));
        assertEquals(2, cpu.getMemory().read(2));
        assertEquals(3, cpu.getMemory().read(3));
    }

    @Test
    void testString() {
        asm.assemble("break\nstring \"AB\"");
        assertEquals('A', cpu.getMemory().read(1));
        assertEquals('B', cpu.getMemory().read(2));
        assertEquals(0,   cpu.getMemory().read(3)); // terminateur null
    }

    @Test
    void testInstructionInconnue() {
        assertThrows(AssemblerException.class,
            () -> asm.assemble("nop"));
    }
}
