package Test_unitaire;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import materiel.ALU;

class ALUTest {

    private ALU alu;

    @BeforeEach
    void setUp() {
        alu = new ALU();
    }

    // --- additionner ---

    @Test
    void additionner_casNormal() {
        // 3 + 4 -> 7
        assertEquals((byte) 7, alu.additionner((byte) 3, (byte) 4));
        // 5 + 0 -> 5
        assertEquals((byte) 5, alu.additionner((byte) 5, (byte) 0));
        // 127 + 1 déborde en byte → -128
        assertEquals((byte) -128, alu.additionner((byte) 127, (byte) 1));
        // -6 + -4 -> -10
        assertEquals((byte) -10, alu.additionner((byte) -6, (byte) -4));
        // -5 + 5 -> 0
        assertEquals((byte) 0, alu.additionner((byte) -5, (byte) 5));
    }

    // --- soustraire ---

    @Test
    void soustraire_casNormal() {
        assertEquals((byte) 3, alu.soustraire((byte) 7, (byte) 4));
        assertEquals((byte) 0, alu.soustraire((byte) 10, (byte) 10));
        assertEquals((byte) -1, alu.soustraire((byte) 5, (byte) 6));
        // -128 - 1 déborde en byte → 127
        assertEquals((byte) 127, alu.soustraire((byte) -128, (byte) 1));
    }

    // --- multiplier ---

    @Test
    void multiplier_casNormal() {
        // 3 * 4 = 12, résultat tient sur 8 bits
        assertEquals(12, alu.multiplier((byte) 3, (byte) 4));
        assertEquals(0, alu.multiplier((byte) 42, (byte) 0));
        // 20 * 20 = 400 : poids fort = 1 (400/256), poids faible = 144 (400%256)
        int res = alu.multiplier((byte) 20, (byte) 20);
        assertEquals(400, res);
        assertEquals(1,   (res >> 8) & 0xFF);
        assertEquals(144, res & 0xFF);
        // 100 * 100 = 10000 : poids fort = 39, poids faible = 16
        res = alu.multiplier((byte) 100, (byte) 100);
        assertEquals(10000, res);
        assertEquals(39, (res >> 8) & 0xFF);
        assertEquals(16, res & 0xFF);
    }

    // --- diviser ---

    @Test
    void diviser_casNormal() {
        // 10 / 3 → quotient=3, reste=1
        int res = alu.diviser((byte) 10, (byte) 3);
        assertEquals(3, (res >> 8) & 0xFF);
        assertEquals(1, res & 0xFF);
        // 12 / 4 → quotient=3, reste=0
        res = alu.diviser((byte) 12, (byte) 4);
        assertEquals(3, (res >> 8) & 0xFF);
        assertEquals(0, res & 0xFF);
        // 2 / 5 → quotient=0, reste=2
        res = alu.diviser((byte) 2, (byte) 5);
        assertEquals(0, (res >> 8) & 0xFF);
        assertEquals(2, res & 0xFF);
    }

    @Test
    void diviser_parZero_lancerArithmeticException() {
        assertThrows(ArithmeticException.class, () -> alu.diviser((byte) 5, (byte) 0));
    }

    // --- ouBinaire (OR) ---

    @Test
    void ouBinaire_casNormal() {
        // 0b0101 | 0b1010 = 0b1111 = 15
        assertEquals((byte) 15, alu.ouBinaire((byte) 5, (byte) 10));
        assertEquals((byte) 42, alu.ouBinaire((byte) 42, (byte) 0));
        assertEquals((byte) 7,  alu.ouBinaire((byte) 7, (byte) 7));
    }

    // --- etBinaire (AND) ---

    @Test
    void etBinaire_casNormal() {
        // 0b1100 & 0b1010 = 0b1000 = 8
        assertEquals((byte) 8, alu.etBinaire((byte) 12, (byte) 10));
        assertEquals((byte) 0, alu.etBinaire((byte) 42, (byte) 0));
        assertEquals((byte) 7, alu.etBinaire((byte) 7, (byte) 7));
    }

    // --- ouExclusif (XOR) ---

    @Test
    void ouExclusif_casNormal() {
        // 0b1100 ^ 0b1010 = 0b0110 = 6
        assertEquals((byte) 6,  alu.ouExclusif((byte) 12, (byte) 10));
        assertEquals((byte) 0,  alu.ouExclusif((byte) 42, (byte) 42));
        assertEquals((byte) 42, alu.ouExclusif((byte) 42, (byte) 0));
    }
}
