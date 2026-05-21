package Test_unitaire;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import assembleur.Assembleur;
import assembleur.Programme;
import instructions.Instruction;
import instructions.TypeInstruction;

class AssembleurTest {

    // Helper : assemble une source et retourne la première instruction produite
    private Instruction assemble1(String source) {
        Programme prog = new Programme(source);
        new Assembleur().assembler(prog);
        return prog.getInstruction(0);
    }

    // Helper : assemble une source et retourne le Programme complet
    private Programme assembler(String source) {
        Programme prog = new Programme(source);
        new Assembleur().assembler(prog);
        return prog;
    }

    // =========================================================
    // Groupe 1 : état du Programme après assemblage
    // =========================================================

    @Test
    void assembler_marqueEstAssemble() {
        Programme prog = assembler("BREAK");
        assertTrue(prog.estAssemble());
    }

    @Test
    void assembler_programmeVide_aucuneInstruction_maisMarqueAssemble() {
        Programme prog = assembler("");
        assertEquals(0, prog.nombreInstructions());
        assertTrue(prog.estAssemble());
    }

    // =========================================================
    // Groupe 2 : parsing de chaque mnémonique
    // =========================================================

    @Test
    void parser_LOAD_constante() {
        Instruction inst = assemble1("LOAD R3, 42");
        assertEquals(TypeInstruction.LOAD_CONSTANTE, inst.getType());
        assertArrayEquals(new int[]{3, 42}, inst.getOperandes());
    }

    @Test
    void parser_LOAD_memoire() {
        Instruction inst = assemble1("LOAD R1, [500]");
        assertEquals(TypeInstruction.LOAD_MEMOIRE, inst.getType());
        assertArrayEquals(new int[]{1, 500}, inst.getOperandes());
    }

    @Test
    void parser_STORE() {
        Instruction inst = assemble1("STORE R2, [300]");
        assertEquals(TypeInstruction.STORE, inst.getType());
        assertArrayEquals(new int[]{2, 300}, inst.getOperandes());
    }

    @Test
    void parser_ADD() {
        Instruction inst = assemble1("ADD R0, R1, R2");
        assertEquals(TypeInstruction.ADD, inst.getType());
        assertArrayEquals(new int[]{0, 1, 2}, inst.getOperandes());
    }

    @Test
    void parser_SUB() {
        Instruction inst = assemble1("SUB R1, R2, R3");
        assertEquals(TypeInstruction.SUB, inst.getType());
        assertArrayEquals(new int[]{1, 2, 3}, inst.getOperandes());
    }

    @Test
    void parser_OR() {
        Instruction inst = assemble1("OR R0, R1, R2");
        assertEquals(TypeInstruction.OR, inst.getType());
        assertArrayEquals(new int[]{0, 1, 2}, inst.getOperandes());
    }

    @Test
    void parser_AND() {
        Instruction inst = assemble1("AND R0, R1, R2");
        assertEquals(TypeInstruction.AND, inst.getType());
        assertArrayEquals(new int[]{0, 1, 2}, inst.getOperandes());
    }

    @Test
    void parser_XOR() {
        Instruction inst = assemble1("XOR R0, R1, R2");
        assertEquals(TypeInstruction.XOR, inst.getType());
        assertArrayEquals(new int[]{0, 1, 2}, inst.getOperandes());
    }

    @Test
    void parser_MUL() {
        Instruction inst = assemble1("MUL R0, R1, R2, R3");
        assertEquals(TypeInstruction.MUL, inst.getType());
        assertArrayEquals(new int[]{0, 1, 2, 3}, inst.getOperandes());
    }

    @Test
    void parser_DIV() {
        Instruction inst = assemble1("DIV R4, R5, R6, R7");
        assertEquals(TypeInstruction.DIV, inst.getType());
        assertArrayEquals(new int[]{4, 5, 6, 7}, inst.getOperandes());
    }

    @Test
    void parser_JUMP() {
        Instruction inst = assemble1("JUMP 7");
        assertEquals(TypeInstruction.JUMP, inst.getType());
        assertArrayEquals(new int[]{7}, inst.getOperandes());
    }

    @Test
    void parser_BEQ() {
        Instruction inst = assemble1("BEQ R0, R1, 5");
        assertEquals(TypeInstruction.BEQ, inst.getType());
        assertArrayEquals(new int[]{0, 1, 5}, inst.getOperandes());
    }

    @Test
    void parser_BNE() {
        Instruction inst = assemble1("BNE R2, R3, 9");
        assertEquals(TypeInstruction.BNE, inst.getType());
        assertArrayEquals(new int[]{2, 3, 9}, inst.getOperandes());
    }

    @Test
    void parser_BREAK() {
        Instruction inst = assemble1("BREAK");
        assertEquals(TypeInstruction.BREAK, inst.getType());
        assertEquals(0, inst.getOperandes().length);
    }

    // =========================================================
    // Groupe 3 : numéro de ligne (1-indexé depuis le source)
    // =========================================================

    @Test
    void numLigne_premiereInstruction_est1() {
        Instruction inst = assemble1("LOAD R0, 0");
        assertEquals(1, inst.getLigne());
    }

    @Test
    void numLigne_apresLigneVide_est2() {
        // ligne 1 vide, ligne 2 = instruction → numLigne doit être 2
        Instruction inst = assemble1("\nLOAD R0, 5");
        assertEquals(2, inst.getLigne());
    }

    @Test
    void numLigne_apresCommentaire_est2() {
        // ligne 1 est un commentaire (ignorée), ligne 2 = instruction
        Instruction inst = assemble1("// commentaire\nBREAK");
        assertEquals(2, inst.getLigne());
    }

    // =========================================================
    // Groupe 4 : gestion des lignes et commentaires
    // =========================================================

    @Test
    void lignesVides_ignorees() {
        Programme prog = assembler("\nLOAD R0, 5\n");
        assertEquals(1, prog.nombreInstructions());
    }

    @Test
    void ligneCommentaire_ignoree() {
        Programme prog = assembler("// commentaire\nBREAK");
        assertEquals(1, prog.nombreInstructions());
        assertEquals(TypeInstruction.BREAK, prog.getInstruction(0).getType());
    }

    @Test
    void commentaireEnFinDeLigne_retire() {
        Instruction inst = assemble1("LOAD R0, 5 // chargement de la constante");
        assertEquals(TypeInstruction.LOAD_CONSTANTE, inst.getType());
        assertArrayEquals(new int[]{0, 5}, inst.getOperandes());
    }

    @Test
    void programmeMultiLignes_toutesInstructionsAssemblees() {
        String source =
            "LOAD R1, 3\n" +
            "LOAD R2, 1\n" +
            "SUB R1, R2, R3\n" +
            "BREAK\n";
        Programme prog = assembler(source);
        assertEquals(4, prog.nombreInstructions());
        assertEquals(TypeInstruction.LOAD_CONSTANTE, prog.getInstruction(0).getType());
        assertEquals(TypeInstruction.LOAD_CONSTANTE, prog.getInstruction(1).getType());
        assertEquals(TypeInstruction.SUB,            prog.getInstruction(2).getType());
        assertEquals(TypeInstruction.BREAK,          prog.getInstruction(3).getType());
    }

    // =========================================================
    // Groupe 5 : robustesse syntaxique
    // =========================================================

    @Test
    void mnemoniqueMinuscule_accepte() {
        Instruction inst = assemble1("load R0, 1");
        assertEquals(TypeInstruction.LOAD_CONSTANTE, inst.getType());
    }

    @Test
    void registreMinuscule_accepte() {
        Instruction inst = assemble1("LOAD r5, 10");
        assertEquals(5, inst.getOperandes()[0]);
    }

    @Test
    void espacesSupplementairesAutourOperandes_acceptes() {
        Instruction inst = assemble1("ADD  R0 ,  R1 ,  R2");
        assertEquals(TypeInstruction.ADD, inst.getType());
        assertArrayEquals(new int[]{0, 1, 2}, inst.getOperandes());
    }

    // =========================================================
    // Groupe 6 : erreurs de syntaxe
    // =========================================================

    @Test
    void mnémoniqueInconnu_lancerRuntimeException() {
        // LOAD_INDEXE n'est pas dans le parser → RuntimeException
        // (LOAD_INDEXE, STORE_INDEXE, DONNEE, CHAINE ne sont pas gérés par l'assembleur)
        assertThrows(RuntimeException.class, () -> assembler("FOO R0, 1"));
    }

    @Test
    void adresseSansCrochets_lancerRuntimeException() {
        // STORE attend une adresse entre crochets ; sans crochets → RuntimeException
        assertThrows(RuntimeException.class, () -> assembler("STORE R0, 100"));
    }

    @Test
    void registreInvalide_lancerRuntimeException() {
        // Préfixe attendu : R ; ici X0 → RuntimeException
        assertThrows(RuntimeException.class, () -> assembler("LOAD X0, 5"));
    }
}
