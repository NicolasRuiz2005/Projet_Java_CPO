package cpu;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires de la classe {@link Memory}.
 */
class MemoryTest {

    private Memory memory;

    @BeforeEach
    void setUp() {
        memory = new Memory();
    }

    @Test
    void testTailleMemoire() {
        assertEquals(65536, memory.getSize());
    }

    @Test
    void testLectureInitiale() {
        // Toute la mémoire doit être à 0 à l'initialisation
        assertEquals(0, memory.read(0));
        assertEquals(0, memory.read(1000));
        assertEquals(0, memory.read(65535));
    }

    @Test
    void testEcritureEtLecture() {
        memory.write(100, (byte) 42);
        assertEquals(42, memory.read(100));
    }

    @Test
    void testValeurNegative() {
        memory.write(50, (byte) -1);
        assertEquals(-1, memory.read(50));
    }

    @Test
    void testAdresseLimiteHaute() {
        memory.write(65535, (byte) 99);
        assertEquals(99, memory.read(65535));
    }

    @Test
    void testAdresseInvalideNegative() {
        assertThrows(IllegalArgumentException.class, () -> memory.read(-1));
    }

    @Test
    void testAdresseInvalideDepassement() {
        assertThrows(IllegalArgumentException.class, () -> memory.read(65536));
    }

    @Test
    void testClear() {
        memory.write(10, (byte) 77);
        memory.clear();
        assertEquals(0, memory.read(10));
    }
}
