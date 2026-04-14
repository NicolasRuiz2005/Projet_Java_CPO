package cpu;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires de la classe {@link RegisterFile}.
 */
class RegisterFileTest {

    private RegisterFile registers;

    @BeforeEach
    void setUp() {
        registers = new RegisterFile();
    }

    @Test
    void testNombreDeRegistres() {
        assertEquals(16, registers.getCount());
    }

    @Test
    void testInitialisationAZero() {
        for (int i = 0; i < 16; i++) {
            assertEquals(0, registers.get(i));
        }
    }

    @Test
    void testEcritureEtLecture() {
        registers.set(0, (byte) 10);
        assertEquals(10, registers.get(0));

        registers.set(15, (byte) -5);
        assertEquals(-5, registers.get(15));
    }

    @Test
    void testReset() {
        registers.set(5, (byte) 99);
        registers.reset();
        assertEquals(0, registers.get(5));
    }

    @Test
    void testIndexInvalideNegatif() {
        assertThrows(IllegalArgumentException.class, () -> registers.get(-1));
    }

    @Test
    void testIndexInvalideDepassement() {
        assertThrows(IllegalArgumentException.class, () -> registers.set(16, (byte) 0));
    }
}
