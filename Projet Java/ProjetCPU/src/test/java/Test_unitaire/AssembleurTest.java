package Test_unitaire;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import assembleur.Assembleur;
import assembleur.Programme;
import materiel.Memoire;
import materiel.OpcodesCPU;

class AssembleurTest {

    private Memoire memoire;
    private Assembleur assembleur;

    @BeforeEach
    void setUp() {
        memoire   = new Memoire();
        assembleur = new Assembleur();
    }

    private void assembler(String source) {
        assembleur.assembler(new Programme(source), memoire);
    }

    // =========================================================
    // Groupe 1 : état du Programme après assemblage
    // =========================================================

    @Test
    void assembler_marqueEstAssemble() {
        Programme prog = new Programme("BREAK");
        assembleur.assembler(prog, memoire);
        assertTrue(prog.estAssemble());
    }

    @Test
    void assembler_sourceVide_nEcritRien() {
        assembler("");
        assertEquals(0, memoire.lire(0)); // pas d'octet écrit
        assertEquals(0, assembleur.getAdresseFinale());
    }

    // =========================================================
    // Groupe 2 : BREAK
    // =========================================================

    @Test
    void parser_BREAK() {
        assembler("BREAK");
        assertEquals(OpcodesCPU.BREAK, memoire.lire(0));
        assertEquals(1, assembleur.getAdresseFinale());
    }

    // =========================================================
    // Groupe 3 : LOAD
    // =========================================================

    @Test
    void parser_LOAD_constante() {
        // LOAD R3, 42 → [1, 3, 42]
        assembler("LOAD R3, 42");
        assertEquals(OpcodesCPU.LOAD_CONST, memoire.lire(0));
        assertEquals(3,  memoire.lire(1));
        assertEquals(42, memoire.lire(2));
        assertEquals(3, assembleur.getAdresseFinale());
    }

    @Test
    void parser_LOAD_memoire() {
        // LOAD R1, [500] → [2, 1, 1, 244]  (500 = 0x01F4)
        assembler("LOAD R1, [500]");
        assertEquals(OpcodesCPU.LOAD_MEM, memoire.lire(0));
        assertEquals(1,   memoire.lire(1));
        assertEquals(1,   memoire.lire(2)); // adrH
        assertEquals((byte) 244, memoire.lire(3)); // adrL
        assertEquals(4, assembleur.getAdresseFinale());
    }

    @Test
    void parser_LOAD_indexe() {
        // LOAD R0, [100], R1 → [14, 0, 0, 100, 1]
        assembler("LOAD R0, [100], R1");
        assertEquals(OpcodesCPU.LOAD_INDEXE, memoire.lire(0));
        assertEquals(0,   memoire.lire(1)); // dest
        assertEquals(0,   memoire.lire(2)); // adrH
        assertEquals(100, memoire.lire(3)); // adrL
        assertEquals(1,   memoire.lire(4)); // regIdx
        assertEquals(5, assembleur.getAdresseFinale());
    }

    // =========================================================
    // Groupe 4 : STORE
    // =========================================================

    @Test
    void parser_STORE() {
        // STORE R2, [300] → [3, 2, 1, 44]  (300 = 0x012C)
        assembler("STORE R2, [300]");
        assertEquals(OpcodesCPU.STORE, memoire.lire(0));
        assertEquals(2,  memoire.lire(1));
        assertEquals(1,  memoire.lire(2)); // adrH
        assertEquals(44, memoire.lire(3)); // adrL
    }

    @Test
    void parser_STORE_indexe() {
        // STORE R0, [100], R1 → [15, 0, 0, 100, 1]
        assembler("STORE R0, [100], R1");
        assertEquals(OpcodesCPU.STORE_INDEXE, memoire.lire(0));
        assertEquals(0,   memoire.lire(1));
        assertEquals(0,   memoire.lire(2));
        assertEquals(100, memoire.lire(3));
        assertEquals(1,   memoire.lire(4));
    }

    // =========================================================
    // Groupe 5 : opérations arithmétiques
    // =========================================================

    @Test
    void parser_ADD() {
        // ADD R0, R1, R2 (src1=R0, src2=R1, dest=R2) → [4, 2, 0, 1]
        assembler("ADD R0, R1, R2");
        assertEquals(OpcodesCPU.ADD, memoire.lire(0));
        assertEquals(2, memoire.lire(1)); // dest
        assertEquals(0, memoire.lire(2)); // src1
        assertEquals(1, memoire.lire(3)); // src2
    }

    @Test
    void parser_SUB() {
        // SUB R1, R2, R3 → [5, 3, 1, 2]
        assembler("SUB R1, R2, R3");
        assertEquals(OpcodesCPU.SUB, memoire.lire(0));
        assertEquals(3, memoire.lire(1));
        assertEquals(1, memoire.lire(2));
        assertEquals(2, memoire.lire(3));
    }

    @Test
    void parser_MUL() {
        // MUL R0, R1, R2, R3 (src1=R0, src2=R1, destH=R2, destL=R3) → [6, 2, 3, 0, 1]
        assembler("MUL R0, R1, R2, R3");
        assertEquals(OpcodesCPU.MUL, memoire.lire(0));
        assertEquals(2, memoire.lire(1)); // destH
        assertEquals(3, memoire.lire(2)); // destL
        assertEquals(0, memoire.lire(3)); // src1
        assertEquals(1, memoire.lire(4)); // src2
    }

    @Test
    void parser_DIV() {
        // DIV R4, R5, R6, R7 (src1=R4, src2=R5, quot=R6, reste=R7) → [7, 6, 7, 4, 5]
        assembler("DIV R4, R5, R6, R7");
        assertEquals(OpcodesCPU.DIV, memoire.lire(0));
        assertEquals(6, memoire.lire(1)); // quot
        assertEquals(7, memoire.lire(2)); // reste
        assertEquals(4, memoire.lire(3)); // src1
        assertEquals(5, memoire.lire(4)); // src2
    }

    // =========================================================
    // Groupe 6 : opérations logiques
    // =========================================================

    @Test
    void parser_OU_et_OR() {
        // OR R0, R1, R2 → [8, 2, 0, 1]
        assembler("OR R0, R1, R2");
        assertEquals(OpcodesCPU.OU, memoire.lire(0));
        assertEquals(2, memoire.lire(1));
        // OU (mnémonique français) donne le même résultat
        memoire = new Memoire();
        assembler("OU R0, R1, R2");
        assertEquals(OpcodesCPU.OU, memoire.lire(0));
    }

    @Test
    void parser_ET_et_AND() {
        assembler("AND R0, R1, R2");
        assertEquals(OpcodesCPU.ET, memoire.lire(0));
        memoire = new Memoire();
        assembler("ET R0, R1, R2");
        assertEquals(OpcodesCPU.ET, memoire.lire(0));
    }

    @Test
    void parser_XOR() {
        // XOR R0, R1, R2 → [10, 2, 0, 1]
        assembler("XOR R0, R1, R2");
        assertEquals(OpcodesCPU.OU_EXCLUSIF, memoire.lire(0));
        assertEquals(2, memoire.lire(1));
        assertEquals(0, memoire.lire(2));
        assertEquals(1, memoire.lire(3));
    }

    // =========================================================
    // Groupe 7 : instructions de contrôle
    // =========================================================

    @Test
    void parser_JUMP() {
        // JUMP 7 → [11, 0, 7]
        assembler("JUMP 7");
        assertEquals(OpcodesCPU.JUMP, memoire.lire(0));
        assertEquals(0, memoire.lire(1)); // adrH
        assertEquals(7, memoire.lire(2)); // adrL
    }

    @Test
    void parser_BEQ() {
        // BEQ R0, R1, 5 → [12, 0, 1, 0, 5]
        assembler("BEQ R0, R1, 5");
        assertEquals(OpcodesCPU.BEQ, memoire.lire(0));
        assertEquals(0, memoire.lire(1)); // src1
        assertEquals(1, memoire.lire(2)); // src2
        assertEquals(0, memoire.lire(3)); // adrH
        assertEquals(5, memoire.lire(4)); // adrL
    }

    @Test
    void parser_BNE() {
        // BNE R2, R3, 9 → [13, 2, 3, 0, 9]
        assembler("BNE R2, R3, 9");
        assertEquals(OpcodesCPU.BNE, memoire.lire(0));
        assertEquals(2, memoire.lire(1));
        assertEquals(3, memoire.lire(2));
        assertEquals(0, memoire.lire(3));
        assertEquals(9, memoire.lire(4));
    }

    // =========================================================
    // Groupe 8 : DATA et STRING
    // =========================================================

    @Test
    void parser_DATA() {
        assembler("DATA 10, 20, 30");
        assertEquals(10, memoire.lire(0));
        assertEquals(20, memoire.lire(1));
        assertEquals(30, memoire.lire(2));
        assertEquals(3, assembleur.getAdresseFinale());
    }

    @Test
    void parser_STRING() {
        assembler("STRING \"ab\"");
        assertEquals('a', memoire.lire(0));
        assertEquals('b', memoire.lire(1));
        assertEquals(0,   memoire.lire(2)); // terminateur nul
        assertEquals(3, assembleur.getAdresseFinale());
    }

    // =========================================================
    // Groupe 9 : lignes vides, commentaires, casse
    // =========================================================

    @Test
    void ignorer_lignesVidesEtCommentaires() {
        assembler("// commentaire\n\nBREAK");
        assertEquals(OpcodesCPU.BREAK, memoire.lire(0));
        assertEquals(1, assembleur.getAdresseFinale());
    }

    @Test
    void ignorer_commentaireEnFinDeLigne() {
        assembler("LOAD R0, 5 // chargement");
        assertEquals(OpcodesCPU.LOAD_CONST, memoire.lire(0));
        assertEquals(0, memoire.lire(1));
        assertEquals(5, memoire.lire(2));
    }

    @Test
    void mnemonique_enMinuscules_accepte() {
        assembler("load R0, 1");
        assertEquals(OpcodesCPU.LOAD_CONST, memoire.lire(0));
    }

    @Test
    void registre_enMinuscules_accepte() {
        assembler("LOAD r5, 10");
        assertEquals(5,  memoire.lire(1));
        assertEquals(10, memoire.lire(2));
    }

    // =========================================================
    // Groupe 10 : erreurs de syntaxe
    // =========================================================

    @Test
    void mnemonique_inconnu_lancerRuntimeException() {
        assertThrows(RuntimeException.class, () -> assembler("FOO R0, 1"));
    }

    @Test
    void adresse_sansCrochets_lancerRuntimeException() {
        assertThrows(RuntimeException.class, () -> assembler("STORE R0, 100"));
    }

    @Test
    void registre_invalide_lancerRuntimeException() {
        assertThrows(RuntimeException.class, () -> assembler("LOAD X0, 5"));
    }

    // =========================================================
    // Groupe 11 : adresses 16 bits big-endian
    // =========================================================

    @Test
    void adresse_16bits_bigEndian() {
        // LOAD R0, [1000] → 1000 = 0x03E8 → adrH=3, adrL=232
        assembler("LOAD R0, [1000]");
        assertEquals(3,   memoire.lire(2)); // adrH
        assertEquals((byte) 232, memoire.lire(3)); // adrL
    }

    @Test
    void adresseFinale_multiLignes() {
        // BREAK(1) + LOAD(3) + ADD(4) = 8 octets
        assembler("BREAK\nLOAD R0, 5\nADD R0, R1, R2");
        assertEquals(8, assembleur.getAdresseFinale());
    }
}
