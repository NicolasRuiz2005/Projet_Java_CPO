package Test_unitaire;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import materiel.Registre;

class RegistreTest {

    @Test
    void constructeur_valeurInitialeZero() {
        Registre r = new Registre(0);
        assertEquals(0, r.lire());
    }

    @Test
    void getNumero_retourneNumeroCorrect() {
        Registre r = new Registre(5);
        assertEquals(5, r.getNumero());
    }

    @Test
    void getNumero_registre15() {
        Registre r = new Registre(15);
        assertEquals(15, r.getNumero());
    }

    @Test
    void ecrire_puisLire_retourneValeur() {
        Registre r = new Registre(0);
        r.ecrire((byte) 42);
        assertEquals(42, r.lire());
    }

    @Test
    void ecrire_valeurNegative() {
        Registre r = new Registre(0);
        r.ecrire((byte) -1);
        assertEquals(-1, r.lire());
    }

    @Test
    void ecrire_valeurMinByte() {
        Registre r = new Registre(0);
        r.ecrire((byte) -128);
        assertEquals(-128, r.lire());
    }

    @Test
    void ecrire_valeurMaxByte() {
        Registre r = new Registre(0);
        r.ecrire((byte) 127);
        assertEquals(127, r.lire());
    }

    @Test
    void ecrire_ecraseLaValeurPrecedente() {
        Registre r = new Registre(0);
        r.ecrire((byte) 10);
        r.ecrire((byte) 20);
        assertEquals(20, r.lire());
    }

    @Test
    void plusieursRegistres_sontIndependants() {
        Registre r0 = new Registre(0);
        Registre r1 = new Registre(1);
        r0.ecrire((byte) 5);
        r1.ecrire((byte) 10);
        assertEquals(5, r0.lire());
        assertEquals(10, r1.lire());
    }
}
