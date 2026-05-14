import simulateur.Simulateur;
public class Main {
    public static void main(String[] args) {
        Simulateur sim = new Simulateur();
        String code =
        "LOAD R1, 3\n" +
        "LOAD R2, 4\n" +
        "ADD R1, R2, R3\n" +
        "BREAK\n";
        
        sim.saisirProgramme(code);
        sim.assembler();
        sim.executerProgramme();
        byte r3 = sim.consulterRegistre(3);
        System.out.println("R3 = " + r3); // doit afficher 7
    }
}