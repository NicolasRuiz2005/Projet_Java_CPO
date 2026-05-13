package materiel;
public class Memoire {
    private static final int TAILLE = 65536;
    private byte[] cases;

    public Memoire() {
        this.cases = new byte[TAILLE];
    }

    public byte lire(int adr) {
        return cases[adr];
    }

    public void ecrire(int adr, byte val) {
        cases[adr] = val;
    }

    public int getTaille() {
        return TAILLE;  
    }

    // Méthode pour vérifier si une adresse est valide ou non dans la mémoire
    private void checkAddress(int address) {
        if (address < 0 || address >= TAILLE)
            throw new IllegalArgumentException("Adresse invalide : " + address);
    }

}