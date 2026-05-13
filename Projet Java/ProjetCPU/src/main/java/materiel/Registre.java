package materiel;

public class Registre {
     private int numero;

    private byte valeur;

    public Registre(int numero) {

        this.numero = numero;

        this.valeur = 0;

    }

    public byte lire() {

        return valeur;

    }

    public void ecrire(byte val) {

        this.valeur = val;

    }

    public int getNumero() {

        return numero;

    }
}
