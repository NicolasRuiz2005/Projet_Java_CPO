package materiel;

/**
 * Constantes byte représentant les codes opération (opcodes) du jeu d'instructions
 * du simulateur CPU "Carré Petit Utile".
 * Chaque constante correspond à l'octet de commande lu par le CPU lors de la phase
 * Fetch du cycle Fetch/Decode/Execute.
 */
public class OpcodesCPU {
    public static final byte BREAK         = 0;
    public static final byte LOAD_CONST    = 1;
    public static final byte LOAD_MEM      = 2;
    public static final byte STORE         = 3;
    public static final byte ADD           = 4;
    public static final byte SUB           = 5;
    public static final byte MUL           = 6;
    public static final byte DIV           = 7;
    public static final byte OU            = 8;
    public static final byte ET            = 9;
    public static final byte OU_EXCLUSIF   = 10;
    public static final byte JUMP          = 11;
    public static final byte BEQ           = 12;
    public static final byte BNE           = 13;
    public static final byte LOAD_INDEXE   = 14;
    public static final byte STORE_INDEXE  = 15;
}
