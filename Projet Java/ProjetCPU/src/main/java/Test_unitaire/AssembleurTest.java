package Test_unitaire;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import assembleur.Assembleur;
import assembleur.Programme;
import instructions.Instruction;
import instructions.TypeInstruction;

class AssembleurTest {

    private Instruction assemble1(String source) {
        Programme prog = new Programme(source);
        new Assembleur().assembler(prog);
        return prog.getInstruction(0);
    }

    private Programme assembler(String source) {
        Programme prog = new Programme(source);
        new Assembleur().assembler(prog);
        return prog;
    }

    // =========================================================
    // Groupe 1 : état du Programme après assemblage
    // =========================================================

    @Test
    void assembler_etatFinal() {
        Programme prog = assembler("BREAK");
        assertTrue(prog.estAssemble());

        Programme vide = assembler("");
        assertEquals(0, vide.nombreInstructions());
        assertTrue(vide.estAssemble());
    }

    // =========================================================
    // Groupe 2 : parsing — instructions de chargement/stockage
    // =========================================================

    @Test
    void parser_instructions_chargementMemoire() {
        Instruction load_c = assemble1("LOAD R3, 42");
        assertEquals(TypeInstruction.LOAD_CONSTANTE, load_c.getType());
        assertArrayEquals(new int[]{3, 42}, load_c.getOperandes());

        Instruction load_m = assemble1("LOAD R1, [500]");
        assertEquals(TypeInstruction.LOAD_MEMOIRE, load_m.getType());
        assertArrayEquals(new int[]{1, 500}, load_m.getOperandes());

        Instruction store = assemble1("STORE R2, [300]");
        assertEquals(TypeInstruction.STORE, store.getType());
        assertArrayEquals(new int[]{2, 300}, store.getOperandes());
    }

    // =========================================================
    // Groupe 3 : parsing — instructions arithmétiques
    // =========================================================

    @Test
    void parser_instructions_arithmetique() {
        Instruction add = assemble1("ADD R0, R1, R2");
        assertEquals(TypeInstruction.ADD, add.getType());
        assertArrayEquals(new int[]{0, 1, 2}, add.getOperandes());

        Instruction sub = assemble1("SUB R1, R2, R3");
        assertEquals(TypeInstruction.SUB, sub.getType());
        assertArrayEquals(new int[]{1, 2, 3}, sub.getOperandes());

        Instruction mul = assemble1("MUL R0, R1, R2, R3");
        assertEquals(TypeInstruction.MUL, mul.getType());
        assertArrayEquals(new int[]{0, 1, 2, 3}, mul.getOperandes());

        Instruction div = assemble1("DIV R4, R5, R6, R7");
        assertEquals(TypeInstruction.DIV, div.getType());
        assertArrayEquals(new int[]{4, 5, 6, 7}, div.getOperandes());
    }

    // =========================================================
    // Groupe 4 : parsing — instructions logiques
    // =========================================================

    @Test
    void parser_instructions_logique() {
        Instruction or = assemble1("OR R0, R1, R2");
        assertEquals(TypeInstruction.OR, or.getType());
        assertArrayEquals(new int[]{0, 1, 2}, or.getOperandes());

        Instruction and = assemble1("AND R0, R1, R2");
        assertEquals(TypeInstruction.AND, and.getType());
        assertArrayEquals(new int[]{0, 1, 2}, and.getOperandes());

        Instruction xor = assemble1("XOR R0, R1, R2");
        assertEquals(TypeInstruction.XOR, xor.getType());
        assertArrayEquals(new int[]{0, 1, 2}, xor.getOperandes());
    }

    // =========================================================
    // Groupe 5 : parsing — instructions de contrôle
    // =========================================================

    @Test
    void parser_instructions_controle() {
        Instruction jump = assemble1("JUMP 7");
        assertEquals(TypeInstruction.JUMP, jump.getType());
        assertArrayEquals(new int[]{7}, jump.getOperandes());

        Instruction beq = assemble1("BEQ R0, R1, 5");
        assertEquals(TypeInstruction.BEQ, beq.getType());
        assertArrayEquals(new int[]{0, 1, 5}, beq.getOperandes());

        Instruction bne = assemble1("BNE R2, R3, 9");
        assertEquals(TypeInstruction.BNE, bne.getType());
        assertArrayEquals(new int[]{2, 3, 9}, bne.getOperandes());

        Instruction brk = assemble1("BREAK");
        assertEquals(TypeInstruction.BREAK, brk.getType());
        assertEquals(0, brk.getOperandes().length);
    }

    // =========================================================
    // Groupe 6 : numéro de ligne (1-indexé depuis le source)
    // =========================================================

    @Test
    void numLigne() {
        // Première instruction : ligne 1
        assertEquals(1, assemble1("LOAD R0, 0").getLigne());
        // Ligne vide en tête : instruction sur la ligne 2
        assertEquals(2, assemble1("\nLOAD R0, 5").getLigne());
        // Commentaire en tête : instruction sur la ligne 2
        assertEquals(2, assemble1("// commentaire\nBREAK").getLigne());
    }

    // =========================================================
    // Groupe 7 : gestion des lignes et commentaires
    // =========================================================

    @Test
    void gestion_lignesEtCommentaires() {
        // Lignes vides ignorées
        assertEquals(1, assembler("\nLOAD R0, 5\n").nombreInstructions());

        // Ligne de commentaire ignorée
        Programme prog = assembler("// commentaire\nBREAK");
        assertEquals(1, prog.nombreInstructions());
        assertEquals(TypeInstruction.BREAK, prog.getInstruction(0).getType());

        // Commentaire en fin de ligne retiré
        Instruction inst = assemble1("LOAD R0, 5 // chargement de la constante");
        assertEquals(TypeInstruction.LOAD_CONSTANTE, inst.getType());
        assertArrayEquals(new int[]{0, 5}, inst.getOperandes());

        // Programme multi-lignes complet
        Programme multi = assembler("LOAD R1, 3\nLOAD R2, 1\nSUB R1, R2, R3\nBREAK\n");
        assertEquals(4, multi.nombreInstructions());
        assertEquals(TypeInstruction.LOAD_CONSTANTE, multi.getInstruction(0).getType());
        assertEquals(TypeInstruction.LOAD_CONSTANTE, multi.getInstruction(1).getType());
        assertEquals(TypeInstruction.SUB,            multi.getInstruction(2).getType());
        assertEquals(TypeInstruction.BREAK,          multi.getInstruction(3).getType());
    }

    // =========================================================
    // Groupe 8 : robustesse syntaxique
    // =========================================================

    @Test
    void robustesse_syntaxique() {
        // Mnémonique en minuscules accepté
        assertEquals(TypeInstruction.LOAD_CONSTANTE, assemble1("load R0, 1").getType());
        // Registre en minuscules accepté
        assertEquals(5, assemble1("LOAD r5, 10").getOperandes()[0]);
        // Espaces supplémentaires autour des opérandes
        Instruction inst = assemble1("ADD  R0 ,  R1 ,  R2");
        assertEquals(TypeInstruction.ADD, inst.getType());
        assertArrayEquals(new int[]{0, 1, 2}, inst.getOperandes());
    }

    // =========================================================
    // Groupe 9 : erreurs de syntaxe
    // =========================================================

    @Test
    void mnémoniqueInconnu_lancerRuntimeException() {
        assertThrows(RuntimeException.class, () -> assembler("FOO R0, 1"));
    }

    @Test
    void adresseSansCrochets_lancerRuntimeException() {
        assertThrows(RuntimeException.class, () -> assembler("STORE R0, 100"));
    }

    @Test
    void registreInvalide_lancerRuntimeException() {
        assertThrows(RuntimeException.class, () -> assembler("LOAD X0, 5"));
    }
}
