package cpu;

import assembler.Assembler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests d'intégration du CPU couvrant les 5 étapes du projet.
 */
class CPUTest {

    private CPU cpu;
    private Assembler asm;

    @BeforeEach
    void setUp() {
        cpu = new CPU();
        asm = new Assembler(cpu.getMemory());
    }

    // ---------------------------------------------------------------
    // Étape 1 — Load / Store / Break
    // ---------------------------------------------------------------

    @Test
    void testLoadConstante() {
        asm.assemble("load r0, 42\nbreak");
        cpu.run();
        assertEquals(42, cpu.getRegisters().get(0));
    }

    @Test
    void testLoadMemoire() {
        cpu.getMemory().write(500, (byte) 77);
        asm.assemble("load r3, @500\nbreak");
        cpu.run();
        assertEquals(77, cpu.getRegisters().get(3));
    }

    @Test
    void testStore() {
        asm.assemble("load r1, 55\nstore r1, @300\nbreak");
        cpu.run();
        assertEquals(55, cpu.getMemory().read(300));
    }

    @Test
    void testBreakArreteLExecution() {
        // Après break, r1 ne doit pas être modifié
        asm.assemble("break\nload r1, 99");
        cpu.run();
        assertEquals(0, cpu.getRegisters().get(1));
    }

    // ---------------------------------------------------------------
    // Étape 2 — Assembleur (syntaxe hexadécimale)
    // ---------------------------------------------------------------

    @Test
    void testAdresseHexadecimale() {
        asm.assemble("load r0, 10\nstore r0, @0x00C8\nbreak");
        cpu.run();
        assertEquals(10, cpu.getMemory().read(0x00C8)); // 0xC8 = 200
    }

    // ---------------------------------------------------------------
    // Étape 3 — ALU
    // ---------------------------------------------------------------

    @Test
    void testAdd() {
        asm.assemble("load r0, 10\nload r1, 5\nadd r2, r0, r1\nbreak");
        cpu.run();
        assertEquals(15, cpu.getRegisters().get(2));
    }

    @Test
    void testSub() {
        asm.assemble("load r0, 10\nload r1, 3\nsub r2, r0, r1\nbreak");
        cpu.run();
        assertEquals(7, cpu.getRegisters().get(2));
    }

    @Test
    void testMul() {
        asm.assemble("load r0, 12\nload r1, 10\nmul r2, r3, r0, r1\nbreak");
        cpu.run();
        // 12 * 10 = 120 → tout dans l'octet bas
        assertEquals(120, cpu.getRegisters().get(3) & 0xFF);
    }

    @Test
    void testDiv() {
        asm.assemble("load r0, 17\nload r1, 5\ndiv r2, r3, r0, r1\nbreak");
        cpu.run();
        assertEquals(3, cpu.getRegisters().get(2));  // quotient
        assertEquals(2, cpu.getRegisters().get(3));  // reste
    }

    @Test
    void testOr() {
        asm.assemble("load r0, 12\nload r1, 10\nor r2, r0, r1\nbreak");
        cpu.run();
        assertEquals(12 | 10, cpu.getRegisters().get(2) & 0xFF);
    }

    @Test
    void testAnd() {
        asm.assemble("load r0, 12\nload r1, 10\nand r2, r0, r1\nbreak");
        cpu.run();
        assertEquals(12 & 10, cpu.getRegisters().get(2) & 0xFF);
    }

    @Test
    void testXor() {
        asm.assemble("load r0, 12\nload r1, 10\nxor r2, r0, r1\nbreak");
        cpu.run();
        assertEquals(12 ^ 10, cpu.getRegisters().get(2) & 0xFF);
    }

    // ---------------------------------------------------------------
    // Étape 4 — Boucles et conditionnelles
    // ---------------------------------------------------------------

    @Test
    void testJump() {
        // Saute par-dessus une instruction
        asm.assemble(
            "jump @fin\n" +
            "load r0, 99\n" +  // ne doit pas s'exécuter
            "fin:\n" +
            "break"
        );
        cpu.run();
        assertEquals(0, cpu.getRegisters().get(0));
    }

    @Test
    void testBeqSautEffectue() {
        asm.assemble(
            "load r0, 5\n" +
            "load r1, 5\n" +
            "beq r0, r1, @fin\n" +
            "load r2, 99\n" +  // ne doit pas s'exécuter
            "fin:\n" +
            "break"
        );
        cpu.run();
        assertEquals(0, cpu.getRegisters().get(2));
    }

    @Test
    void testBeqSautNonEffectue() {
        asm.assemble(
            "load r0, 5\n" +
            "load r1, 3\n" +
            "beq r0, r1, @fin\n" +
            "load r2, 77\n" +  // doit s'exécuter
            "fin:\n" +
            "break"
        );
        cpu.run();
        assertEquals(77, cpu.getRegisters().get(2));
    }

    @Test
    void testBoucleCompteur() {
        // Calcule la somme 1+2+3+4+5 = 15
        asm.assemble(
            "load r0, 0\n" +
            "load r1, 1\n" +
            "load r2, 6\n" +
            "load r3, 1\n" +
            "boucle:\n" +
            "beq r1, r2, @fin\n" +
            "add r0, r0, r1\n" +
            "add r1, r1, r3\n" +
            "jump @boucle\n" +
            "fin:\n" +
            "break"
        );
        cpu.run();
        assertEquals(15, cpu.getRegisters().get(0));
    }

    // ---------------------------------------------------------------
    // Étape 5 — Tableaux
    // ---------------------------------------------------------------

    @Test
    void testLoadIndexe() {
        // tableau[3] doit valoir 40
        cpu.getMemory().write(0x100, (byte) 10);
        cpu.getMemory().write(0x101, (byte) 20);
        cpu.getMemory().write(0x102, (byte) 30);
        cpu.getMemory().write(0x103, (byte) 40);

        asm.assemble("load r0, 3\nload r1, @0x0100, r0\nbreak");
        cpu.run();
        assertEquals(40, cpu.getRegisters().get(1));
    }

    @Test
    void testStoreIndexe() {
        asm.assemble("load r0, 2\nload r1, 99\nstore r1, @0x0200, r0\nbreak");
        cpu.run();
        assertEquals(99, cpu.getMemory().read(0x0202));
    }
}
