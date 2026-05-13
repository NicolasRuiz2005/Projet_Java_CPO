package materiel;
public class ALU {
    public byte additionner(byte a, byte b) {
        return (byte) (a + b);
    }

    public byte soustraire(byte a, byte b) {
        return (byte) (a - b);
    }

    // Renvoie sur 16 bits : poids fort dans le 2e octet, poids faible dans le 1er
    public int multiplier(byte a, byte b) {
        return a * b;
    }

    // Renvoie quotient (poids fort) et reste (poids faible) packés
    public int diviser(byte a, byte b) {
        if (b == 0) {
            throw new ArithmeticException("Division par zéro, non valide");
        }
        int quotient = a / b;
        int reste = a % b;
        return ((quotient & 0xFF) << 8) | (reste & 0xFF);
    }

    public byte ouBinaire(byte a, byte b) {
        return (byte) (a | b);
    }

    public byte etBinaire(byte a, byte b) {
        return (byte) (a & b);
    }

    public byte ouExclusif(byte a, byte b) {
        return (byte) (a ^ b);
    }
}