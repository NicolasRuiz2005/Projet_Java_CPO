package assembleur;

import instructions.Instruction;
import instructions.TypeInstruction;

/**
 * Assembleur du simulateur CPU.
 * Transforme le code source assembleur d'un {@link Programme} en une liste
 * d'objets {@link Instruction} exécutables par le CPU.
 * Supporte les mnémoniques : LOAD, STORE, ADD, SUB, MUL, DIV, OR, AND, XOR,
 * JUMP, BEQ, BNE et BREAK, ainsi que les commentaires {@code //} et les lignes vides.
 */
public class Assembleur {

    /**
     * Assemble le code source du programme en une liste d'instructions exécutables.
     *
     * @param prog le programme dont le code source doit être assemblé
     * @throws RuntimeException si une ligne contient un mnémonique inconnu ou une syntaxe invalide
     */
    public void assembler(Programme prog) {
        String[] lignes = prog.getCodeSource().split("\\r?\\n");
        for (int i = 0; i < lignes.length; i++) {
            String ligne = lignes[i].trim();
            // ignore les lignes vides et les commentaires
            if (ligne.isEmpty() || ligne.startsWith("//")) {
                continue;
            }
            // retire les commentaires en fin de ligne
            int idxComm = ligne.indexOf("//");
            if (idxComm >= 0) {
                ligne = ligne.substring(0, idxComm).trim();
            }
            Instruction inst = parserLigne(ligne, i + 1);
            if (inst != null) {
                prog.ajouterInstruction(inst);
            }
        }
        prog.marquerAssemble();
    }

    /**
     * Parse une ligne de code source et la convertit en une Instruction.
     *
     * @param ligne    la ligne de code source à analyser (déjà nettoyée)
     * @param numLigne le numéro de la ligne dans le fichier source, pour les messages d'erreur
     * @return l'instruction correspondante
     * @throws RuntimeException si le mnémonique est inconnu ou la syntaxe invalide
     */
    private Instruction parserLigne(String ligne, int numLigne) {
        // sépare le mnémonique des opérandes
        String[] parts = ligne.split("\\s+", 2);
        String mnem = parts[0].toUpperCase();
        String reste = parts.length > 1 ? parts[1] : "";
        String[] args = reste.isEmpty() ? new String[0] : reste.split("\\s*,\\s*");

        switch (mnem) {
            case "LOAD": {
                int reg = parseRegistre(args[0]);
                if (args[1].startsWith("[")) {
                    int adr = parseAdresse(args[1]);
                    return new Instruction(TypeInstruction.LOAD_MEMOIRE, numLigne,
                            new int[]{reg, adr});
                } else {
                    int val = Integer.parseInt(args[1]);
                    return new Instruction(TypeInstruction.LOAD_CONSTANTE, numLigne,
                            new int[]{reg, val});
                }
            }
            case "STORE": {
                int reg = parseRegistre(args[0]);
                int adr = parseAdresse(args[1]);
                return new Instruction(TypeInstruction.STORE, numLigne,
                        new int[]{reg, adr});
            }
            case "ADD": {
                int r1 = parseRegistre(args[0]);
                int r2 = parseRegistre(args[1]);
                int rd = parseRegistre(args[2]);
                return new Instruction(TypeInstruction.ADD, numLigne,
                        new int[]{r1, r2, rd});
            }
            case "SUB": {
                int r1 = parseRegistre(args[0]);
                int r2 = parseRegistre(args[1]);
                int rd = parseRegistre(args[2]);
                return new Instruction(TypeInstruction.SUB, numLigne,
                        new int[]{r1, r2, rd});
            }
            case "OR": {
                int r1 = parseRegistre(args[0]);
                int r2 = parseRegistre(args[1]);
                int rd = parseRegistre(args[2]);
                return new Instruction(TypeInstruction.OR, numLigne,
                        new int[]{r1, r2, rd});
            }
            case "AND": {
                int r1 = parseRegistre(args[0]);
                int r2 = parseRegistre(args[1]);
                int rd = parseRegistre(args[2]);
                return new Instruction(TypeInstruction.AND, numLigne,
                        new int[]{r1, r2, rd});
            }
            case "XOR": {
                int r1 = parseRegistre(args[0]);
                int r2 = parseRegistre(args[1]);
                int rd = parseRegistre(args[2]);
                return new Instruction(TypeInstruction.XOR, numLigne,
                        new int[]{r1, r2, rd});
            }
            case "MUL": {
                int r1 = parseRegistre(args[0]);
                int r2 = parseRegistre(args[1]);
                int rh = parseRegistre(args[2]);
                int rl = parseRegistre(args[3]);
                return new Instruction(TypeInstruction.MUL, numLigne,
                        new int[]{r1, r2, rh, rl});
            }
            case "DIV": {
                int r1 = parseRegistre(args[0]);
                int r2 = parseRegistre(args[1]);
                int rq = parseRegistre(args[2]);
                int rr = parseRegistre(args[3]);
                return new Instruction(TypeInstruction.DIV, numLigne,
                        new int[]{r1, r2, rq, rr});
            }
            case "JUMP": {
                int adr = Integer.parseInt(args[0]);
                return new Instruction(TypeInstruction.JUMP, numLigne,
                        new int[]{adr});
            }
            case "BEQ": {
                int r1 = parseRegistre(args[0]);
                int r2 = parseRegistre(args[1]);
                int adr = Integer.parseInt(args[2]);
                return new Instruction(TypeInstruction.BEQ, numLigne,
                        new int[]{r1, r2, adr});
            }
            case "BNE": {
                int r1 = parseRegistre(args[0]);
                int r2 = parseRegistre(args[1]);
                int adr = Integer.parseInt(args[2]);
                return new Instruction(TypeInstruction.BNE, numLigne,
                        new int[]{r1, r2, adr});
            }
            case "BREAK": {
                return new Instruction(TypeInstruction.BREAK, numLigne, new int[0]);
            }
            default:
                throw new RuntimeException(
                        "Erreur de syntaxe ligne " + numLigne + " : " + ligne);
        }
    }

    /**
     * Extrait le numéro de registre depuis une chaîne de type "R3".
     *
     * @param s la chaîne représentant le registre (ex : "R3" ou "r3")
     * @return le numéro du registre
     * @throws RuntimeException si la chaîne ne commence pas par 'R'
     */
    private int parseRegistre(String s) {
        s = s.trim().toUpperCase();
        if (!s.startsWith("R")) {
            throw new RuntimeException("Registre attendu : " + s);
        }
        return Integer.parseInt(s.substring(1));
    }

    /**
     * Extrait l'adresse mémoire depuis une chaîne de type "[1234]".
     *
     * @param s la chaîne représentant l'adresse mémoire (ex : "[1234]")
     * @return l'adresse mémoire sous forme d'entier
     * @throws RuntimeException si la chaîne n'est pas entourée de crochets
     */
    private int parseAdresse(String s) {
        s = s.trim();
        if (s.startsWith("[") && s.endsWith("]")) {
            return Integer.parseInt(s.substring(1, s.length() - 1));
        }
        throw new RuntimeException("Adresse mémoire attendue : " + s);
    }
}