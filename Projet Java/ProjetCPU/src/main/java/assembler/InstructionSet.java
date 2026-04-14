package assembler;

/**
 * Définit les codes d'opération (opcodes) de toutes les instructions
 * supportées par le simulateur de CPU.
 *
 * <p>Chaque constante correspond à un byte stocké en mémoire pour
 * identifier une instruction. Le CPU lit cet opcode, puis lit les
 * paramètres qui suivent selon l'instruction concernée.</p>
 *
 * <p>Table des instructions :</p>
 * <pre>
 *  0  BREAK          — arrêt du programme (0 paramètre)
 *  1  LOAD_CONST     — charge une constante dans un registre (reg, val)
 *  2  LOAD_MEM       — charge depuis la mémoire (reg, addrHigh, addrLow)
 *  3  STORE_MEM      — sauvegarde en mémoire (reg, addrHigh, addrLow)
 *  4  ADD            — addition (dest, a, b)
 *  5  SUB            — soustraction (dest, a, b)
 *  6  MUL            — multiplication (destHigh, destLow, a, b)
 *  7  DIV            — division (quot, rem, a, b)
 *  8  OR             — ou binaire (dest, a, b)
 *  9  AND            — et binaire (dest, a, b)
 * 10  XOR            — ou exclusif (dest, a, b)
 * 11  JUMP           — saut inconditionnel (addrHigh, addrLow)
 * 12  BEQ            — saut si égaux (a, b, addrHigh, addrLow)
 * 13  BNE            — saut si différents (a, b, addrHigh, addrLow)
 * 14  LOAD_IDX       — load indexé (dest, addrHigh, addrLow, indexReg)
 * 15  STORE_IDX      — store indexé (src, addrHigh, addrLow, indexReg)
 * </pre>
 *
 * @author Projet CPU
 * @version 1.0
 */
public final class InstructionSet {

    // ---------------------------------------------------------------
    // Étape 1 — Instructions de base
    // ---------------------------------------------------------------

    /** Arrête l'exécution du programme. Aucun paramètre. */
    public static final byte BREAK      = 0;

    /** Charge une valeur constante dans un registre. Params : reg(1), val(1). */
    public static final byte LOAD_CONST = 1;

    /** Charge la valeur à une adresse mémoire dans un registre. Params : reg(1), addr(2). */
    public static final byte LOAD_MEM   = 2;

    /** Sauvegarde la valeur d'un registre en mémoire. Params : reg(1), addr(2). */
    public static final byte STORE_MEM  = 3;

    // ---------------------------------------------------------------
    // Étape 3 — Unité arithmétique et logique
    // ---------------------------------------------------------------

    /** Addition de deux registres. Params : dest(1), a(1), b(1). */
    public static final byte ADD  = 4;

    /** Soustraction de deux registres. Params : dest(1), a(1), b(1). */
    public static final byte SUB  = 5;

    /** Multiplication. Résultat sur 16 bits → deux registres. Params : destHigh(1), destLow(1), a(1), b(1). */
    public static final byte MUL  = 6;

    /** Division entière. Params : quot(1), rem(1), a(1), b(1). */
    public static final byte DIV  = 7;

    /** OU binaire. Params : dest(1), a(1), b(1). */
    public static final byte OR   = 8;

    /** ET binaire. Params : dest(1), a(1), b(1). */
    public static final byte AND  = 9;

    /** OU exclusif (XOR). Params : dest(1), a(1), b(1). */
    public static final byte XOR  = 10;

    // ---------------------------------------------------------------
    // Étape 4 — Boucles et conditionnelles
    // ---------------------------------------------------------------

    /** Saut inconditionnel. Params : addr(2). */
    public static final byte JUMP = 11;

    /** Saut si deux registres sont égaux (Branch if Equal). Params : a(1), b(1), addr(2). */
    public static final byte BEQ  = 12;

    /** Saut si deux registres sont différents (Branch if Not Equal). Params : a(1), b(1), addr(2). */
    public static final byte BNE  = 13;

    // ---------------------------------------------------------------
    // Étape 5 — Gestion des tableaux
    // ---------------------------------------------------------------

    /** Chargement indexé depuis la mémoire. Params : dest(1), addr(2), indexReg(1). */
    public static final byte LOAD_IDX  = 14;

    /** Stockage indexé en mémoire. Params : src(1), addr(2), indexReg(1). */
    public static final byte STORE_IDX = 15;

    /** Classe utilitaire, non instanciable. */
    private InstructionSet() {}
}
