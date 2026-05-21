package Test_unitaire;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import materiel.Memoire;

class MemoireTest {

    private Memoire memoire;

    @BeforeEach
    void setUp() {
        memoire = new Memoire();
    }

    @Test
    void getTaille_retourne65536() {
        assertEquals(65536, memoire.getTaille());
    }

    @Test
    void lireEcrire_casNormaux() {
        // Lecture initiale → 0
        assertEquals(0, memoire.lire(0));
        assertEquals(0, memoire.lire(1000));
        assertEquals(0, memoire.lire(65535));
        // Écriture puis lecture
        memoire.ecrire(100, (byte) 42);
        assertEquals(42, memoire.lire(100));
        // Limites basse et haute
        memoire.ecrire(0, (byte) 1);
        assertEquals(1, memoire.lire(0));
        memoire.ecrire(65535, (byte) -1);
        assertEquals(-1, memoire.lire(65535));
        // Écrasement de la valeur précédente
        memoire.ecrire(50, (byte) 10);
        memoire.ecrire(50, (byte) 20);
        assertEquals(20, memoire.lire(50));
        // Cases indépendantes
        memoire.ecrire(10, (byte) 5);
        memoire.ecrire(20, (byte) 7);
        assertEquals(5, memoire.lire(10));
        assertEquals(7, memoire.lire(20));
    }

    @Test
    void lire_adresseNegative_lancerIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> memoire.lire(-1));
    }

    @Test
    void lire_adresseEgale65536_lancerIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> memoire.lire(65536));
    }

    @Test
    void ecrire_adresseNegative_lancerIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> memoire.ecrire(-1, (byte) 0));
    }

    @Test
    void ecrire_adresseEgale65536_lancerIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> memoire.ecrire(65536, (byte) 0));
    }
}
