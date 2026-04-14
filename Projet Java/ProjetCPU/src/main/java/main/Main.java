package main;

import assembler.Assembler;
import cpu.CPU;

/**
 * Point d'entrée du simulateur de CPU.
 *
 * <p>Démontre les 5 étapes du projet à travers des programmes assembleur
 * de test :</p>
 * <ol>
 *   <li>Chargement et stockage (LOAD / STORE / BREAK)</li>
 *   <li>Assembleur (syntaxe lisible → code machine)</li>
 *   <li>Opérations ALU (ADD, SUB, MUL, DIV, OR, AND, XOR)</li>
 *   <li>Boucles et conditionnelles (JUMP, BEQ, BNE)</li>
 *   <li>Tableaux (LOAD_IDX, STORE_IDX, DATA, STRING)</li>
 * </ol>
 *
 * @author Projet CPU
 * @version 1.0
 */
public class Main {

    /**
     * Lance les démonstrations de toutes les étapes du projet.
     *
     * @param args arguments de la ligne de commande (non utilisés)
     */
    public static void main(String[] args) {
        System.out.println("=== Simulateur de CPU — Projet Carré Petit Utile ===\n");

        demoEtape1();
        demoEtape3();
        demoEtape4();
        demoEtape5();
    }

    // ---------------------------------------------------------------
    // Étape 1 — Load / Store / Break
    // ---------------------------------------------------------------

    /**
     * Démonstration de l'étape 1 : chargement de constantes et
     * d'une valeur depuis la mémoire, stockage du résultat.
     */
    private static void demoEtape1() {
        System.out.println("--- Étape 1 : LOAD / STORE / BREAK ---");
        CPU cpu = new CPU();
        Assembler asm = new Assembler(cpu.getMemory());

        // Pré-charge une valeur en mémoire à l'adresse 200
        cpu.getMemory().write(200, (byte) 42);

        String prog =
            "load r0, 5      ; charge la constante 5 dans r0\n" +
            "load r1, 6      ; charge la constante 6 dans r1\n" +
            "load r2, @200   ; charge mem[200] = 42 dans r2\n" +
            "store r0, @201  ; sauvegarde r0 en mem[201]\n" +
            "break\n";

        asm.assemble(prog);
        cpu.run();

        System.out.println("r0 = " + cpu.getRegisters().get(0) + "  (attendu : 5)");
        System.out.println("r1 = " + cpu.getRegisters().get(1) + "  (attendu : 6)");
        System.out.println("r2 = " + cpu.getRegisters().get(2) + "  (attendu : 42)");
        System.out.println("mem[201] = " + cpu.getMemory().read(201) + "  (attendu : 5)");
        System.out.println();
    }

    // ---------------------------------------------------------------
    // Étape 3 — ALU
    // ---------------------------------------------------------------

    /**
     * Démonstration de l'étape 3 : opérations arithmétiques et logiques.
     */
    private static void demoEtape3() {
        System.out.println("--- Étape 3 : ALU (ADD / SUB / MUL / DIV / OR / AND / XOR) ---");
        CPU cpu = new CPU();
        Assembler asm = new Assembler(cpu.getMemory());

        String prog =
            "load r0, 20      ; r0 = 20\n" +
            "load r1, 7       ; r1 = 7\n" +
            "add  r2, r0, r1  ; r2 = 20 + 7 = 27\n" +
            "sub  r3, r0, r1  ; r3 = 20 - 7 = 13\n" +
            "mul  r4, r5, r0, r1 ; {r4,r5} = 20 * 7 = 140\n" +
            "div  r6, r7, r0, r1 ; r6 = 20/7 = 2, r7 = 20%7 = 6\n" +
            "or   r8, r0, r1  ; r8 = 20 | 7 = 23\n" +
            "and  r9, r0, r1  ; r9 = 20 & 7 = 4\n" +
            "xor  r10, r0, r1 ; r10 = 20 ^ 7 = 19\n" +
            "break\n";

        asm.assemble(prog);
        cpu.run();

        System.out.println("ADD  r2  = " + cpu.getRegisters().get(2)  + "  (attendu : 27)");
        System.out.println("SUB  r3  = " + cpu.getRegisters().get(3)  + "  (attendu : 13)");
        System.out.println("MUL  r5  = " + (cpu.getRegisters().get(5) & 0xFF) + "  (attendu : 140, octet bas)");
        System.out.println("DIV  r6  = " + cpu.getRegisters().get(6)  + "  (attendu : 2, quotient)");
        System.out.println("DIV  r7  = " + cpu.getRegisters().get(7)  + "  (attendu : 6, reste)");
        System.out.println("OR   r8  = " + cpu.getRegisters().get(8)  + "  (attendu : 23)");
        System.out.println("AND  r9  = " + cpu.getRegisters().get(9)  + "  (attendu : 4)");
        System.out.println("XOR  r10 = " + cpu.getRegisters().get(10) + "  (attendu : 19)");
        System.out.println();
    }

    // ---------------------------------------------------------------
    // Étape 4 — Boucles et conditionnelles
    // ---------------------------------------------------------------

    /**
     * Démonstration de l'étape 4 : boucle qui additionne les nombres
     * de 1 à 5 dans r0 (résultat attendu : 15).
     */
    private static void demoEtape4() {
        System.out.println("--- Étape 4 : JUMP / BEQ / BNE (somme de 1 à 5) ---");
        CPU cpu = new CPU();
        Assembler asm = new Assembler(cpu.getMemory());

        // Calcule sum = 1 + 2 + 3 + 4 + 5 = 15
        // r0 = accumulateur (somme)
        // r1 = compteur (1 à 5)
        // r2 = limite (6, valeur d'arrêt)
        // r3 = constante 1 (pour incrémenter)
        String prog =
            "load r0, 0        ; accumulateur = 0\n" +
            "load r1, 1        ; compteur = 1\n" +
            "load r2, 6        ; limite = 6\n" +
            "load r3, 1        ; incrément = 1\n" +
            "boucle:\n" +
            "beq  r1, r2, @fin ; si compteur == 6, sortir\n" +
            "add  r0, r0, r1   ; accumulateur += compteur\n" +
            "add  r1, r1, r3   ; compteur++\n" +
            "jump @boucle      ; retour au début\n" +
            "fin:\n" +
            "break\n";

        asm.assemble(prog);
        cpu.run();

        System.out.println("Somme 1+2+3+4+5 = " + cpu.getRegisters().get(0) + "  (attendu : 15)");
        System.out.println();
    }

    // ---------------------------------------------------------------
    // Étape 5 — Tableaux
    // ---------------------------------------------------------------

    /**
     * Démonstration de l'étape 5 : parcours d'un tableau en mémoire
     * via chargement indexé, et écriture d'une chaîne de caractères.
     */
    private static void demoEtape5() {
        System.out.println("--- Étape 5 : LOAD_IDX / STORE_IDX / DATA / STRING ---");
        CPU cpu = new CPU();
        Assembler asm = new Assembler(cpu.getMemory());

        // Tableau de 5 valeurs à partir de l'adresse 0x0100
        // Lit l'élément d'index 2 (valeur 30) et le stocke en r1
        String prog =
            "load  r0, 2          ; r0 = index = 2\n" +
            "load  r1, @0x0100, r0 ; r1 = tableau[2]\n" +
            "store r1, @0x0200    ; sauvegarde tableau[2] en mem[0x200]\n" +
            "break\n" +
            "; données du tableau (après le break)\n" +
            "data  10, 20, 30, 40, 50\n" +
            "string \"CPU OK\"\n";

        // Pré-charge le tableau en mémoire à l'adresse 0x100
        cpu.getMemory().write(0x100, (byte) 10);
        cpu.getMemory().write(0x101, (byte) 20);
        cpu.getMemory().write(0x102, (byte) 30);
        cpu.getMemory().write(0x103, (byte) 40);
        cpu.getMemory().write(0x104, (byte) 50);

        asm.assemble(prog);
        cpu.run();

        System.out.println("tableau[2] = " + cpu.getRegisters().get(1) + "  (attendu : 30)");
        System.out.println("mem[0x200] = " + cpu.getMemory().read(0x200) + "  (attendu : 30)");
        System.out.println();
        System.out.println("=== Fin des démonstrations ===");
    }
}
