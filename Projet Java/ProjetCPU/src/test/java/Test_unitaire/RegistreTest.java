package Test_unitaire;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import materiel.Registre;

class RegistreTest {

    @Test
    void constructeur_valeurInitialeEtNumero() {
        Registre r0 = new Registre(0);
        assertEquals(0, r0.lire());
        assertEquals(0, r0.getNumero());
        Registre r5 = new Registre(5);
        assertEquals(5, r5.getNumero());
        Registre r15 = new Registre(15);
        assertEquals(15, r15.getNumero());
    }

    @Test
    void ecrire_puisLire() {
        Registre r = new Registre(0);
        // Valeurs courantes
        r.ecrire((byte) 42);
        assertEquals(42, r.lire());
        r.ecrire((byte) -1);
        assertEquals(-1, r.lire());
        // Limites du type byte
        r.ecrire((byte) -128);
        assertEquals(-128, r.lire());
        r.ecrire((byte) 127);
        assertEquals(127, r.lire());
        // Écrasement de la valeur précédente
        r.ecrire((byte) 10);
        r.ecrire((byte) 20);
        assertEquals(20, r.lire());
        // Registres indépendants
        Registre r0 = new Registre(0);
        Registre r1 = new Registre(1);
        r0.ecrire((byte) 5);
        r1.ecrire((byte) 10);
        assertEquals(5,  r0.lire());
        assertEquals(10, r1.lire());
    }
}
