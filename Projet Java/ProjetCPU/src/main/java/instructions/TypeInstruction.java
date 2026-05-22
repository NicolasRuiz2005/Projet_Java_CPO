package instructions;

/**
 * Enumère tous les types d'instructions supportés par le simulateur CPU.
 * Couvre les chargements/stockages mémoire, les opérations arithmétiques et logiques,
 * les branchements et les directives de données.
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
