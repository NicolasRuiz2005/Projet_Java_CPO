package assembleur;

import materiel.Memoire;
import materiel.OpcodesCPU;

/**
 * Assembleur du simulateur CPU "Carré Petit Utile".
 * Transforme le code source assembleur d'un {@link Programme} en octets écrits
 * directement en mémoire à partir de l'adresse 0.
 * Supporte les mnémoniques : LOAD, STORE, ADD, SUB, MUL, DIV, OU (ou OR),
 * ET (ou AND), XOR, JUMP, BEQ, BNE, BREAK, DATA et STRING,
 * ainsi que les commentaires {@code //} et les lignes vides.
 */
public class Assembleur {

    /** Adresse mémoire courante où le prochain octet sera écrit. */
    private int adresseEcriture;

    /**
     * Assemble le code source du programme et écrit les octets en mémoire à partir de l'adresse 0.
     * La mémoire doit avoir été remise à zéro par l'appelant avant cet appel.
     *
     * @param prog    le programme dont le code source doit être assemblé
     * @param memoire la mémoire dans laquelle les octets sont écrits
     * @throws RuntimeException si une ligne contient un mnémonique inconnu ou une syntaxe invalide
     */
    public void assembler(Programme prog, Memoire memoire) {
        adresseEcriture = 0;
        String[] lignes = prog.getCodeSource().split("\\r?\\n");
        for (int i = 0; i < lignes.length; i++) {
            String ligne = lignes[i].trim();
            if (ligne.isEmpty() || ligne.startsWith("//")) {
                continue;
            }
            int idxComm = ligne.indexOf("//");
            if (idxComm >= 0) {
                ligne = ligne.substring(0, idxComm).trim();
            }
            if (!ligne.isEmpty()) {
                ecrireLigne(ligne, i + 1, memoire);
            }
        }
        prog.marquerAssemble();
    }

    /**
     * Parse une ligne de code source et écrit les octets correspondants en mémoire.
     *
     * @param ligne    la ligne de code source nettoyée (sans commentaire)
     * @param numLigne le numéro de la ligne source, pour les messages d'erreur
     * @param memoire  la mémoire cible
     * @throws RuntimeException si le mnémonique est inconnu ou la syntaxe invalide
     */
    private void ecrireLigne(String ligne, int numLigne, Memoire memoire) {
        String[] parts = ligne.split("\\s+", 2);
        String mnem = parts[0].toUpperCase();
        String reste = parts.length > 1 ? parts[1] : "";
        String[] args = reste.isEmpty() ? new String[0] : reste.split("\\s*,\\s*");

        switch (mnem) {
            case "LOAD": {
                int reg = parseRegistre(args[0]);
                if (args[1].trim().startsWith("[")) {
                    if (args.length == 3) {
                        // LOAD_INDEXE : [14][dest][adrH][adrL][regIdx]
                        int adrBase = parseAdresse(args[1]);
                        int regIdx  = parseRegistre(args[2]);
                        ecrire(memoire, OpcodesCPU.LOAD_INDEXE);
                        ecrire(memoire, (byte) reg);
                        ecrireAdresse(memoire, adrBase);
                        ecrire(memoire, (byte) regIdx);
                    } else {
                        // LOAD_MEM : [2][reg][adrH][adrL]
                        int adr = parseAdresse(args[1]);
                        ecrire(memoire, OpcodesCPU.LOAD_MEM);
                        ecrire(memoire, (byte) reg);
                        ecrireAdresse(memoire, adr);
                    }
                } else {
                    // LOAD_CONST : [1][reg][val]
                    int val = Integer.parseInt(args[1].trim());
                    ecrire(memoire, OpcodesCPU.LOAD_CONST);
                    ecrire(memoire, (byte) reg);
                    ecrire(memoire, (byte) val);
                }
                break;
            }
            case "STORE": {
                int reg = parseRegistre(args[0]);
                if (args.length == 3) {
                    // STORE_INDEXE : [15][src][adrH][adrL][regIdx]
                    int adrBase = parseAdresse(args[1]);
                    int regIdx  = parseRegistre(args[2]);
                    ecrire(memoire, OpcodesCPU.STORE_INDEXE);
                    ecrire(memoire, (byte) reg);
                    ecrireAdresse(memoire, adrBase);
                    ecrire(memoire, (byte) regIdx);
                } else {
                    // STORE : [3][reg][adrH][adrL]
                    int adr = parseAdresse(args[1]);
                    ecrire(memoire, OpcodesCPU.STORE);
                    ecrire(memoire, (byte) reg);
                    ecrireAdresse(memoire, adr);
                }
                break;
            }
            case "ADD": {
                // ADD src1, src2, dest → [4][dest][src1][src2]
                int src1 = parseRegistre(args[0]);
                int src2 = parseRegistre(args[1]);
                int dest = parseRegistre(args[2]);
                ecrire(memoire, OpcodesCPU.ADD);
                ecrire(memoire, (byte) dest);
                ecrire(memoire, (byte) src1);
                ecrire(memoire, (byte) src2);
                break;
            }
            case "SUB": {
                // SUB src1, src2, dest → [5][dest][src1][src2]
                int src1 = parseRegistre(args[0]);
                int src2 = parseRegistre(args[1]);
                int dest = parseRegistre(args[2]);
                ecrire(memoire, OpcodesCPU.SUB);
                ecrire(memoire, (byte) dest);
                ecrire(memoire, (byte) src1);
                ecrire(memoire, (byte) src2);
                break;
            }
            case "MUL": {
                // MUL src1, src2, destH, destL → [6][destH][destL][src1][src2]
                int src1  = parseRegistre(args[0]);
                int src2  = parseRegistre(args[1]);
                int destH = parseRegistre(args[2]);
                int destL = parseRegistre(args[3]);
                ecrire(memoire, OpcodesCPU.MUL);
                ecrire(memoire, (byte) destH);
                ecrire(memoire, (byte) destL);
                ecrire(memoire, (byte) src1);
                ecrire(memoire, (byte) src2);
                break;
            }
            case "DIV": {
                // DIV src1, src2, quot, regReste → [7][quot][regReste][src1][src2]
                int src1     = parseRegistre(args[0]);
                int src2     = parseRegistre(args[1]);
                int quot     = parseRegistre(args[2]);
                int regReste = parseRegistre(args[3]);
                ecrire(memoire, OpcodesCPU.DIV);
                ecrire(memoire, (byte) quot);
                ecrire(memoire, (byte) regReste);
                ecrire(memoire, (byte) src1);
                ecrire(memoire, (byte) src2);
                break;
            }
            case "OU":
            case "OR": {
                // OU/OR src1, src2, dest → [8][dest][src1][src2]
                int src1 = parseRegistre(args[0]);
                int src2 = parseRegistre(args[1]);
                int dest = parseRegistre(args[2]);
                ecrire(memoire, OpcodesCPU.OU);
                ecrire(memoire, (byte) dest);
                ecrire(memoire, (byte) src1);
                ecrire(memoire, (byte) src2);
                break;
            }
            case "ET":
            case "AND": {
                // ET/AND src1, src2, dest → [9][dest][src1][src2]
                int src1 = parseRegistre(args[0]);
                int src2 = parseRegistre(args[1]);
                int dest = parseRegistre(args[2]);
                ecrire(memoire, OpcodesCPU.ET);
                ecrire(memoire, (byte) dest);
                ecrire(memoire, (byte) src1);
                ecrire(memoire, (byte) src2);
                break;
            }
            case "XOR": {
                // XOR src1, src2, dest → [10][dest][src1][src2]
                int src1 = parseRegistre(args[0]);
                int src2 = parseRegistre(args[1]);
                int dest = parseRegistre(args[2]);
                ecrire(memoire, OpcodesCPU.OU_EXCLUSIF);
                ecrire(memoire, (byte) dest);
                ecrire(memoire, (byte) src1);
                ecrire(memoire, (byte) src2);
                break;
            }
            case "JUMP": {
                // JUMP addr → [11][adrH][adrL]
                int adr = Integer.parseInt(args[0].trim());
                ecrire(memoire, OpcodesCPU.JUMP);
                ecrireAdresse(memoire, adr);
                break;
            }
            case "BEQ": {
                // BEQ src1, src2, addr → [12][src1][src2][adrH][adrL]
                int src1 = parseRegistre(args[0]);
                int src2 = parseRegistre(args[1]);
                int adr  = Integer.parseInt(args[2].trim());
                ecrire(memoire, OpcodesCPU.BEQ);
                ecrire(memoire, (byte) src1);
                ecrire(memoire, (byte) src2);
                ecrireAdresse(memoire, adr);
                break;
            }
            case "BNE": {
                // BNE src1, src2, addr → [13][src1][src2][adrH][adrL]
                int src1 = parseRegistre(args[0]);
                int src2 = parseRegistre(args[1]);
                int adr  = Integer.parseInt(args[2].trim());
                ecrire(memoire, OpcodesCPU.BNE);
                ecrire(memoire, (byte) src1);
                ecrire(memoire, (byte) src2);
                ecrireAdresse(memoire, adr);
                break;
            }
            case "DATA": {
                // Écrit des valeurs brutes : DATA 0, 1, 2, 3
                for (String arg : args) {
                    ecrire(memoire, (byte) Integer.parseInt(arg.trim()));
                }
                break;
            }
            case "STRING": {
                // Écrit les octets UTF-8 de la chaîne + terminateur nul : STRING "abc"
                String chaine = reste.trim();
                if (chaine.startsWith("\"") && chaine.endsWith("\"")) {
                    chaine = chaine.substring(1, chaine.length() - 1);
                }
                for (char c : chaine.toCharArray()) {
                    ecrire(memoire, (byte) c);
                }
                ecrire(memoire, (byte) 0);
                break;
            }
            case "BREAK": {
                ecrire(memoire, OpcodesCPU.BREAK);
                break;
            }
            default:
                throw new RuntimeException("Erreur de syntaxe ligne " + numLigne + " : " + ligne);
        }
    }

    // ---------------------------------------------------------------
    // Helpers d'écriture mémoire séquentielle
    // ---------------------------------------------------------------

    /**
     * Écrit un octet à l'adresse courante et incrémente le pointeur d'écriture.
     */
    private void ecrire(Memoire memoire, byte valeur) {
        memoire.ecrire(adresseEcriture++, valeur);
    }

    /**
     * Écrit une adresse 16 bits en big-endian (poids fort en premier).
     */
    private void ecrireAdresse(Memoire memoire, int adresse) {
        memoire.ecrire(adresseEcriture++, (byte) ((adresse >> 8) & 0xFF));
        memoire.ecrire(adresseEcriture++, (byte) (adresse & 0xFF));
    }

    // ---------------------------------------------------------------
    // Helpers de parsing
    // ---------------------------------------------------------------

    /**
     * Retourne l'adresse mémoire suivant le dernier octet écrit lors du dernier assemblage.
     * Permet au Simulateur de savoir quelle zone programme effacer avant le prochain assemblage.
     *
     * @return la taille en octets du dernier programme assemblé
     */
    public int getAdresseFinale() {
        return adresseEcriture;
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
