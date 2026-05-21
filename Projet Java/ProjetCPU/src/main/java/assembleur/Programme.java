package assembleur;

import java.util.ArrayList;
import java.util.List;

import instructions.Instruction;

public class Programme {

    private String codeSource;
    private boolean estAssemble;
    private List<Instruction> instructions;

    /*
     * Crée un programme à partir de son code source
     *
     * @param codeSource Le code source assembleur du programme
     */
    public Programme(String codeSource) {
        this.codeSource = codeSource;
        this.estAssemble = false;
        this.instructions = new ArrayList<>();
    }

    /*
     * Retourne le code source du programme
     *
     * @return Le code source assembleur
     */
    public String getCodeSource() {
        return codeSource;
    }

    /*
     * Ajoute une instruction à la liste du programme
     *
     * @param inst L'instruction à ajouter
     */
    public void ajouterInstruction(Instruction inst) {
        instructions.add(inst);
    }

    /*
     * Retourne l'instruction à l'index donné
     *
     * @param index L'index de l'instruction dans la liste
     * @return L'instruction correspondante
     */
    public Instruction getInstruction(int index) {
        return instructions.get(index);
    }

    /*
     * Retourne le nombre d'instructions du programme
     *
     * @return Le nombre d'instructions
     */
    public int nombreInstructions() {
        return instructions.size();
    }

    /*
     * Marque le programme comme assemblé
     */
    public void marquerAssemble() {
        this.estAssemble = true;
    }

    /*
     * Indique si le programme a été assemblé
     *
     * @return true si le programme est assemblé, false sinon
     */
    public boolean estAssemble() {
        return estAssemble;
    }
}