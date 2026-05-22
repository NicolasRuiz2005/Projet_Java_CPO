package assembleur;

/**
 * Représente un programme assembleur composé de son code source.
 * Un programme commence non assemblé ; il devient assemblé après l'appel à
 * {@link Assembleur#assembler(Programme, materiel.Memoire)}.
 * Le résultat de l'assemblage est écrit directement en mémoire — cette classe
 * ne stocke donc plus de liste d'instructions.
 */
public class Programme {

    private String codeSource;
    private boolean estAssemble;

    /**
     * Crée un programme à partir de son code source assembleur.
     *
     * @param codeSource le code source assembleur du programme
     */
    public Programme(String codeSource) {
        this.codeSource = codeSource;
        this.estAssemble = false;
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
