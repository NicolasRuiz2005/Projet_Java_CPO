package assembleur;

import java.util.ArrayList;
import java.util.List;

import instructions.Instruction;

/**
 * Représente un programme assembleur composé d'un code source et de la liste
 * d'instructions produites après assemblage.
 * Un programme commence non assemblé ; il devient assemblé après l'appel à
 * {@link Assembleur#assembler(Programme)}.
 */
public class Programme {

    private String codeSource;
    private boolean estAssemble;
    private List<Instruction> instructions;

    /**
     * Crée un programme à partir de son code source assembleur.
     *
     * @param codeSource le code source assembleur du programme
     */
    public Programme(String codeSource) {
        this.codeSource = codeSource;
        this.estAssemble = false;
        this.instructions = new ArrayList<>();
    }

    /**
     * Retourne le code source du programme.
     *
     * @return le code source assembleur
     */
    public String getCodeSource() {
        return codeSource;
    }

    /**
     * Ajoute une instruction à la fin de la liste du programme.
     *
     * @param inst l'instruction à ajouter
     */
    public void ajouterInstruction(Instruction inst) {
        instructions.add(inst);
    }

    /**
     * Retourne l'instruction à l'index donné.
     *
     * @param index l'index de l'instruction dans la liste (à partir de 0)
     * @return l'instruction correspondante
     */
    public Instruction getInstruction(int index) {
        return instructions.get(index);
    }

    /**
     * Retourne le nombre d'instructions du programme.
     *
     * @return le nombre d'instructions
     */
    public int nombreInstructions() {
        return instructions.size();
    }

    /**
     * Marque le programme comme assemblé.
     */
    public void marquerAssemble() {
        this.estAssemble = true;
    }

    /**
     * Indique si le programme a été assemblé.
     *
     * @return {@code true} si le programme est assemblé, {@code false} sinon
     */
    public boolean estAssemble() {
        return estAssemble;
    }
}