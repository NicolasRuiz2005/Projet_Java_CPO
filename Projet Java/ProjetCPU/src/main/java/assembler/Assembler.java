package assembler;

import cpu.Memory;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Assembleur à deux passes pour le simulateur de CPU.
 *
 * <p>Traduit du code assembleur lisible en codes machines (bytes) écrits
 * directement dans la {@link Memory} du CPU.</p>
 *
 * <h2>Syntaxe supportée</h2>
 * <pre>
 *  ; commentaire jusqu'en fin de ligne
 *  label:                        — définit un label (étiquette)
 *
 *  break                         — arrêt
 *  load  rX, N                   — charge la constante N dans rX
 *  load  rX, @addr               — charge mem[addr] dans rX
 *  load  rX, @addr, rY           — charge mem[addr + rY] dans rX (indexé)
 *  store rX, @addr               — sauvegarde rX en mem[addr]
 *  store rX, @addr, rY           — sauvegarde rX en mem[addr + rY] (indexé)
 *  add   rD, rA, rB              — rD = rA + rB
 *  sub   rD, rA, rB              — rD = rA - rB
 *  mul   rH, rL, rA, rB          — {rH,rL} = rA * rB
 *  div   rQ, rR, rA, rB          — rQ = rA/rB, rR = rA%rB
 *  or    rD, rA, rB              — rD = rA | rB
 *  and   rD, rA, rB              — rD = rA & rB
 *  xor   rD, rA, rB              — rD = rA ^ rB
 *  jump  @addr                   — saut inconditionnel
 *  beq   rA, rB, @addr           — saut si rA == rB
 *  bne   rA, rB, @addr           — saut si rA != rB
 *  data  v1, v2, ...             — écrit des valeurs brutes en mémoire
 *  string "texte"                — écrit une chaîne UTF-8 (terminée par 0)
 * </pre>
 *
 * <p>Les adresses peuvent être en décimal ({@code 100}) ou hexadécimal
 * ({@code 0x64}). Les labels peuvent être utilisés à la place des adresses.</p>
 *
 * <h2>Fonctionnement en deux passes</h2>
 * <ol>
 *   <li>Première passe : calcule l'adresse de chaque instruction et
 *       construit la table des labels.</li>
 *   <li>Deuxième passe : génère le code machine en remplaçant les
 *       références aux labels par leurs adresses réelles.</li>
 * </ol>
 *
 * @author Projet CPU
 * @version 1.0
 */
public class Assembler {

    /** Mémoire dans laquelle le code machine sera écrit. */
    private final Memory memory;

    /** Table des labels : nom → adresse en mémoire. */
    private final Map<String, Integer> labelTable;

    /** Pointeur d'écriture en mémoire. */
    private int writePointer;

    /**
     * Crée un assembleur lié à la mémoire donnée.
     *
     * @param memory la mémoire du CPU où écrire le code machine
     */
    public Assembler(Memory memory) {
        this.memory     = memory;
        this.labelTable = new HashMap<>();
        this.writePointer = 0;
    }

    /**
     * Assemble le code source donné et écrit le code machine en mémoire
     * à partir de l'adresse 0.
     *
     * @param source le code assembleur (une instruction par ligne)
     * @throws AssemblerException si une erreur de syntaxe est détectée
     */
    public void assemble(String source) {
        String[] lines = source.split("\n");
        labelTable.clear();
        writePointer = 0;

        // Première passe : construction de la table des labels
        firstPass(lines);

        // Deuxième passe : génération du code machine
        writePointer = 0;
        secondPass(lines);
    }

    /**
     * Retourne l'adresse courante du pointeur d'écriture après assemblage.
     * Correspond à la taille du programme en mémoire.
     *
     * @return le nombre de bytes écrits en mémoire
     */
    public int getWritePointer() {
        return writePointer;
    }

    /**
     * Retourne l'adresse d'un label défini dans le code source.
     *
     * @param label le nom du label
     * @return l'adresse en mémoire, ou -1 si non trouvé
     */
    public int getLabelAddress(String label) {
        return labelTable.getOrDefault(label, -1);
    }

    // ---------------------------------------------------------------
    // Première passe — collecte des labels
    // ---------------------------------------------------------------

    /**
     * Première passe : parcourt les lignes pour enregistrer les labels
     * et simuler l'avancement du pointeur sans écrire en mémoire.
     *
     * @param lines les lignes du code source
     */
    private void firstPass(String[] lines) {
        for (int lineNum = 0; lineNum < lines.length; lineNum++) {
            String line = cleanLine(lines[lineNum]);
            if (line.isEmpty()) continue;

            // Détection d'un label (ex: "loop:")
            if (line.endsWith(":")) {
                String label = line.substring(0, line.length() - 1).trim();
                labelTable.put(label, writePointer);
                continue;
            }

            // Label en début de ligne suivi d'une instruction (ex: "loop: add r0, r1, r2")
            if (line.contains(":")) {
                int colonIdx = line.indexOf(':');
                String label = line.substring(0, colonIdx).trim();
                labelTable.put(label, writePointer);
                line = line.substring(colonIdx + 1).trim();
                if (line.isEmpty()) continue;
            }

            // Simule la taille de l'instruction
            writePointer += computeInstructionSize(line, lineNum + 1);
        }
    }

    /**
     * Calcule le nombre de bytes qu'une instruction occupera en mémoire.
     *
     * @param line    la ligne de code assembleur (nettoyée)
     * @param lineNum numéro de ligne pour les messages d'erreur
     * @return le nombre de bytes de l'instruction
     */
    private int computeInstructionSize(String line, int lineNum) {
        String[] parts = tokenize(line);
        if (parts.length == 0) return 0;

        switch (parts[0].toLowerCase()) {
            case "break":  return 1;
            case "load":   return (parts.length == 4) ? 5 :
                           (parts[2].startsWith("@") ? 4 : 3);
            case "store":  return (parts.length == 4) ? 5 : 4;
            case "add":
            case "sub":
            case "or":
            case "and":
            case "xor":    return 4;  // opcode + 3 registres
            case "mul":
            case "div":    return 5;  // opcode + 4 registres
            case "jump":   return 3;  // opcode + adresse 2 bytes
            case "beq":
            case "bne":    return 5;  // opcode + 2 registres + adresse 2 bytes
            case "data":   return parts.length - 1;
            case "string": return computeStringSize(line);
            default:
                throw new AssemblerException("Instruction inconnue : '" + parts[0] + "' (ligne " + lineNum + ")");
        }
    }

    /**
     * Calcule la taille en mémoire d'une instruction {@code string}.
     * La taille correspond à la longueur UTF-8 de la chaîne + 1 (terminateur null).
     *
     * @param line la ligne contenant l'instruction string
     * @return le nombre de bytes
     */
    private int computeStringSize(String line) {
        String text = extractStringLiteral(line);
        return text.getBytes(StandardCharsets.UTF_8).length + 1; // +1 pour le '\0'
    }

    // ---------------------------------------------------------------
    // Deuxième passe — génération du code machine
    // ---------------------------------------------------------------

    /**
     * Deuxième passe : génère le code machine et l'écrit en mémoire.
     *
     * @param lines les lignes du code source
     */
    private void secondPass(String[] lines) {
        for (int lineNum = 0; lineNum < lines.length; lineNum++) {
            String line = cleanLine(lines[lineNum]);
            if (line.isEmpty()) continue;

            // Ignore les labels seuls
            if (line.endsWith(":")) continue;

            // Retire le label en tête de ligne si présent
            if (line.contains(":")) {
                line = line.substring(line.indexOf(':') + 1).trim();
                if (line.isEmpty()) continue;
            }

            parseLine(line, lineNum + 1);
        }
    }

    /**
     * Analyse une ligne d'instruction et écrit les bytes correspondants en mémoire.
     *
     * @param line    la ligne de code (nettoyée)
     * @param lineNum numéro de ligne pour les messages d'erreur
     */
    private void parseLine(String line, int lineNum) {
        String[] parts = tokenize(line);
        if (parts.length == 0) return;

        String mnemonic = parts[0].toLowerCase();

        switch (mnemonic) {
            case "break":  writeByte(InstructionSet.BREAK);              break;
            case "load":   parseLoad(parts, lineNum);                    break;
            case "store":  parseStore(parts, lineNum);                   break;
            case "add":    parseAluTriple(InstructionSet.ADD,  parts);   break;
            case "sub":    parseAluTriple(InstructionSet.SUB,  parts);   break;
            case "mul":    parseMul(parts);                              break;
            case "div":    parseDiv(parts);                              break;
            case "or":     parseAluTriple(InstructionSet.OR,   parts);   break;
            case "and":    parseAluTriple(InstructionSet.AND,  parts);   break;
            case "xor":    parseAluTriple(InstructionSet.XOR,  parts);   break;
            case "jump":   parseJump(parts);                             break;
            case "beq":    parseBranch(InstructionSet.BEQ, parts);       break;
            case "bne":    parseBranch(InstructionSet.BNE, parts);       break;
            case "data":   parseData(parts);                             break;
            case "string": parseString(line);                            break;
            default:
                throw new AssemblerException(
                    "Instruction inconnue : '" + mnemonic + "' (ligne " + lineNum + ")");
        }
    }

    // ---------------------------------------------------------------
    // Parseurs d'instructions
    // ---------------------------------------------------------------

    /**
     * Parse et génère le code pour les instructions LOAD et LOAD_IDX.
     *
     * @param parts   les tokens de la ligne
     * @param lineNum le numéro de ligne pour les erreurs
     */
    private void parseLoad(String[] parts, int lineNum) {
        int reg = parseRegister(parts[1], lineNum);
        String src = parts[2];

        if (parts.length == 4) {
            // load rX, @addr, rY  →  LOAD_IDX
            writeByte(InstructionSet.LOAD_IDX);
            writeByte((byte) reg);
            writeAddress(parseAddress(src, lineNum));
            writeByte((byte) parseRegister(parts[3], lineNum));
        } else if (src.startsWith("@")) {
            // load rX, @addr  →  LOAD_MEM
            writeByte(InstructionSet.LOAD_MEM);
            writeByte((byte) reg);
            writeAddress(parseAddress(src, lineNum));
        } else {
            // load rX, N  →  LOAD_CONST
            writeByte(InstructionSet.LOAD_CONST);
            writeByte((byte) reg);
            writeByte((byte) parseValue(src, lineNum));
        }
    }

    /**
     * Parse et génère le code pour les instructions STORE et STORE_IDX.
     *
     * @param parts   les tokens de la ligne
     * @param lineNum le numéro de ligne pour les erreurs
     */
    private void parseStore(String[] parts, int lineNum) {
        int reg = parseRegister(parts[1], lineNum);
        String dst = parts[2];

        if (parts.length == 4) {
            // store rX, @addr, rY  →  STORE_IDX
            writeByte(InstructionSet.STORE_IDX);
            writeByte((byte) reg);
            writeAddress(parseAddress(dst, lineNum));
            writeByte((byte) parseRegister(parts[3], lineNum));
        } else {
            // store rX, @addr  →  STORE_MEM
            writeByte(InstructionSet.STORE_MEM);
            writeByte((byte) reg);
            writeAddress(parseAddress(dst, lineNum));
        }
    }

    /**
     * Parse et génère le code pour les instructions ALU à 3 registres
     * (ADD, SUB, OR, AND, XOR) : opcode, dest, a, b.
     *
     * @param opcode le code de l'instruction
     * @param parts  les tokens
     */
    private void parseAluTriple(byte opcode, String[] parts) {
        writeByte(opcode);
        writeByte((byte) parseRegister(parts[1], 0));
        writeByte((byte) parseRegister(parts[2], 0));
        writeByte((byte) parseRegister(parts[3], 0));
    }

    /**
     * Parse et génère le code pour MUL : opcode, destHigh, destLow, a, b.
     *
     * @param parts les tokens
     */
    private void parseMul(String[] parts) {
        writeByte(InstructionSet.MUL);
        writeByte((byte) parseRegister(parts[1], 0));
        writeByte((byte) parseRegister(parts[2], 0));
        writeByte((byte) parseRegister(parts[3], 0));
        writeByte((byte) parseRegister(parts[4], 0));
    }

    /**
     * Parse et génère le code pour DIV : opcode, quot, rem, a, b.
     *
     * @param parts les tokens
     */
    private void parseDiv(String[] parts) {
        writeByte(InstructionSet.DIV);
        writeByte((byte) parseRegister(parts[1], 0));
        writeByte((byte) parseRegister(parts[2], 0));
        writeByte((byte) parseRegister(parts[3], 0));
        writeByte((byte) parseRegister(parts[4], 0));
    }

    /**
     * Parse et génère le code pour JUMP : opcode, addrHigh, addrLow.
     *
     * @param parts les tokens
     */
    private void parseJump(String[] parts) {
        writeByte(InstructionSet.JUMP);
        writeAddress(resolveAddress(parts[1]));
    }

    /**
     * Parse et génère le code pour BEQ/BNE : opcode, a, b, addrHigh, addrLow.
     *
     * @param opcode le code de l'instruction (BEQ ou BNE)
     * @param parts  les tokens
     */
    private void parseBranch(byte opcode, String[] parts) {
        writeByte(opcode);
        writeByte((byte) parseRegister(parts[1], 0));
        writeByte((byte) parseRegister(parts[2], 0));
        writeAddress(resolveAddress(parts[3]));
    }

    /**
     * Parse et écrit les bytes de l'instruction DATA directement en mémoire.
     *
     * @param parts les tokens (parts[0] = "data", parts[1..] = valeurs)
     */
    private void parseData(String[] parts) {
        for (int i = 1; i < parts.length; i++) {
            writeByte((byte) parseValue(parts[i], 0));
        }
    }

    /**
     * Parse et écrit une chaîne de caractères UTF-8 en mémoire,
     * terminée par un byte nul (0).
     *
     * @param line la ligne complète contenant l'instruction string
     */
    private void parseString(String line) {
        String text = extractStringLiteral(line);
        for (byte b : text.getBytes(StandardCharsets.UTF_8)) {
            writeByte(b);
        }
        writeByte((byte) 0); // terminateur null
    }

    // ---------------------------------------------------------------
    // Utilitaires de parsing
    // ---------------------------------------------------------------

    /**
     * Découpe une ligne en tokens en séparant par les espaces et les virgules.
     *
     * @param line la ligne à tokeniser
     * @return le tableau des tokens
     */
    private String[] tokenize(String line) {
        // Sépare sur espaces et virgules, ignore les tokens vides
        String[] rawParts = line.split("[,\\s]+");
        List<String> tokens = new ArrayList<>();
        for (String p : rawParts) {
            if (!p.isEmpty()) tokens.add(p);
        }
        return tokens.toArray(new String[0]);
    }

    /**
     * Nettoie une ligne : supprime les commentaires et les espaces en bordure.
     *
     * @param line la ligne brute
     * @return la ligne nettoyée
     */
    private String cleanLine(String line) {
        int commentIdx = line.indexOf(';');
        if (commentIdx >= 0) {
            line = line.substring(0, commentIdx);
        }
        return line.trim();
    }

    /**
     * Extrait la chaîne littérale entre guillemets d'une instruction string.
     *
     * @param line la ligne de code
     * @return le contenu de la chaîne (sans les guillemets)
     * @throws AssemblerException si les guillemets sont absents ou malformés
     */
    private String extractStringLiteral(String line) {
        int start = line.indexOf('"');
        int end   = line.lastIndexOf('"');
        if (start < 0 || end <= start) {
            throw new AssemblerException("Chaîne de caractères malformée : " + line);
        }
        return line.substring(start + 1, end);
    }

    /**
     * Parse un token représentant un registre (ex: "r0", "r15").
     *
     * @param token   le token à parser
     * @param lineNum le numéro de ligne pour les erreurs
     * @return l'index du registre (0 à 15)
     * @throws AssemblerException si le format est invalide
     */
    private int parseRegister(String token, int lineNum) {
        token = token.trim().toLowerCase();
        if (!token.startsWith("r")) {
            throw new AssemblerException(
                "Registre attendu, obtenu : '" + token + "' (ligne " + lineNum + ")");
        }
        try {
            int idx = Integer.parseInt(token.substring(1));
            if (idx < 0 || idx > 15) {
                throw new AssemblerException(
                    "Index de registre hors plage : " + idx + " (ligne " + lineNum + ")");
            }
            return idx;
        } catch (NumberFormatException e) {
            throw new AssemblerException(
                "Format de registre invalide : '" + token + "' (ligne " + lineNum + ")");
        }
    }

    /**
     * Parse un token représentant une adresse mémoire (ex: "@100", "@0x64").
     *
     * @param token   le token commençant par '@'
     * @param lineNum le numéro de ligne pour les erreurs
     * @return la valeur numérique de l'adresse
     * @throws AssemblerException si le format est invalide
     */
    private int parseAddress(String token, int lineNum) {
        token = token.trim();
        if (!token.startsWith("@")) {
            throw new AssemblerException(
                "Adresse attendue (doit commencer par '@') : '" + token + "' (ligne " + lineNum + ")");
        }
        return parseValue(token.substring(1), lineNum);
    }

    /**
     * Résout une adresse qui peut être un entier littéral ou un label.
     * Si le token commence par '@', le '@' est retiré avant résolution.
     *
     * @param token le token représentant l'adresse ou le label
     * @return la valeur numérique de l'adresse
     * @throws AssemblerException si le label n'est pas trouvé
     */
    private int resolveAddress(String token) {
        token = token.trim();
        if (token.startsWith("@")) {
            token = token.substring(1);
        }
        // Essaie de parser comme entier
        try {
            return parseValue(token, 0);
        } catch (AssemblerException e) {
            // C'est peut-être un label
            if (labelTable.containsKey(token)) {
                return labelTable.get(token);
            }
            throw new AssemblerException("Label ou adresse inconnu : '" + token + "'");
        }
    }

    /**
     * Parse une valeur entière en décimal ou hexadécimal.
     *
     * @param token   le token numérique (ex: "42", "0xFF")
     * @param lineNum le numéro de ligne pour les erreurs
     * @return la valeur entière
     * @throws AssemblerException si le format est invalide
     */
    private int parseValue(String token, int lineNum) {
        token = token.trim();
        try {
            if (token.startsWith("0x") || token.startsWith("0X")) {
                return Integer.parseInt(token.substring(2), 16);
            }
            return Integer.parseInt(token);
        } catch (NumberFormatException e) {
            throw new AssemblerException(
                "Valeur numérique invalide : '" + token + "' (ligne " + lineNum + ")");
        }
    }

    // ---------------------------------------------------------------
    // Écriture en mémoire
    // ---------------------------------------------------------------

    /**
     * Écrit un byte en mémoire à la position courante et avance le pointeur.
     *
     * @param value le byte à écrire
     */
    private void writeByte(byte value) {
        memory.write(writePointer++, value);
    }

    /**
     * Écrit une adresse 16 bits en mémoire : octet haut puis octet bas.
     *
     * @param address l'adresse à écrire (0 à 65 535)
     */
    private void writeAddress(int address) {
        memory.write(writePointer++, (byte) ((address >> 8) & 0xFF));
        memory.write(writePointer++, (byte) (address & 0xFF));
    }
}
