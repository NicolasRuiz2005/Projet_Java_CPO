package instructions;

/*
 * Enumère tous les types d'instructions supportés par le simulateur CPU
 */
public enum TypeInstruction {
	LOAD_CONSTANTE,
    LOAD_MEMOIRE,
    STORE,
    BREAK,
    ADD, SUB, MUL, DIV,
    OR, AND, XOR,
    JUMP, BEQ, BNE,
    LOAD_INDEXE,
    STORE_INDEXE,
    DONNEE,
    CHAINE
}
