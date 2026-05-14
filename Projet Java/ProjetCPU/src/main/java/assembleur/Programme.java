package assembleur;

import java.util.ArrayList;
import java.util.List;

import instructions.Instruction;

public class Programme {

    private String codeSource;
    private boolean estAssemble;
    private List<Instruction> instructions;

    public Programme(String codeSource) {
        this.codeSource = codeSource;
        this.estAssemble = false;
        this.instructions = new ArrayList<>();
    }

    public String getCodeSource() {
        return codeSource;
    }

    public void ajouterInstruction(Instruction inst) {
        instructions.add(inst);
    }

    public Instruction getInstruction(int index) {
        return instructions.get(index);
    }

    public int nombreInstructions() {
        return instructions.size();
    }

    public void marquerAssemble() {
        this.estAssemble = true;
    }

    public boolean estAssemble() {
        return estAssemble;
    }
}