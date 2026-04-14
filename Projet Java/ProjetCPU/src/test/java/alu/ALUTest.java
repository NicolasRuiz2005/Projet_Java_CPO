package alu;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires de la classe {@link ALU}.
 */
class ALUTest {

    @Test
    void testAddition() {
        assertEquals((byte) 15, ALU.add((byte) 10, (byte) 5));
    }

    @Test
    void testAdditionOverflow() {
        // 127 + 1 = -128 en byte signé (overflow attendu)
        assertEquals((byte) -128, ALU.add((byte) 127, (byte) 1));
    }

    @Test
    void testSoustraction() {
        assertEquals((byte) 5, ALU.subtract((byte) 10, (byte) 5));
    }

    @Test
    void testSoustractionNegative() {
        assertEquals((byte) -3, ALU.subtract((byte) 2, (byte) 5));
    }

    @Test
    void testMultiplicationBas() {
        assertEquals((byte) 20, ALU.multiplyLow((byte) 4, (byte) 5));
    }

    @Test
    void testMultiplicationGrandNombre() {
        // 100 * 100 = 10000 = 0x2710 → high=0x27=39, low=0x10=16
        assertEquals((byte) 39, ALU.multiplyHigh((byte) 100, (byte) 100));
        assertEquals((byte) 16, ALU.multiplyLow((byte) 100, (byte) 100));
    }

    @Test
    void testDivisionQuotient() {
        assertEquals((byte) 3, ALU.divideQuotient((byte) 10, (byte) 3));
    }

    @Test
    void testDivisionReste() {
        assertEquals((byte) 1, ALU.divideRemainder((byte) 10, (byte) 3));
    }

    @Test
    void testDivisionParZero() {
        assertThrows(ArithmeticException.class,
            () -> ALU.divideQuotient((byte) 5, (byte) 0));
    }

    @Test
    void testOr() {
        assertEquals((byte) (0b1010 | 0b0110), ALU.or((byte) 0b1010, (byte) 0b0110));
    }

    @Test
    void testAnd() {
        assertEquals((byte) (0b1010 & 0b0110), ALU.and((byte) 0b1010, (byte) 0b0110));
    }

    @Test
    void testXor() {
        assertEquals((byte) (0b1010 ^ 0b0110), ALU.xor((byte) 0b1010, (byte) 0b0110));
    }
}
