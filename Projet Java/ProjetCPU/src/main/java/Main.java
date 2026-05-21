import java.util.Scanner;

import simulateur.Simulateur;

public class Main {

    private static final Simulateur sim = new Simulateur(); 
    private static final Scanner scanner = new Scanner(System.in);

    // Registres nommés pour que l'utilisateur comprenne ce qu'il stocke
    // R0-R1 : réservés aux opérandes internes, R2-R11 : stockage utilisateur, R12-R15 : usage interne
    private static final String[] NOMS_REGISTRES = {
        "R0  (opérande interne A)", "R1  (opérande interne B)",
        "R2  (résultat 1)",         "R3  (résultat 2)",
        "R4  (stockage 1)",         "R5  (stockage 2)",
        "R6  (stockage 3)",         "R7  (stockage 4)",
        "R8  (stockage 5)",         "R9  (stockage 6)",
        "R10 (stockage 7)",         "R11 (stockage 8)",
        "R12 (interne)",            "R13 (interne)",
        "R14 (interne)",            "R15 (interne)"
    };

    /*
     * Point d'entrée du programme
     */
    public static void main(String[] args) {
        afficherBanniere();
        boolean continuer = true;
        while (continuer) {
            afficherRegistresActifs();
            afficherMenuPrincipal();
            int choix = lireEntier("Votre choix : ");
            switch (choix) {
                case 1 -> menuInstructions();
                case 2 -> menuRegistres();
                case 3 -> menuMemoire();
                case 4 -> reinitialiser();
                case 0 -> continuer = false;
                default -> System.out.println("  Choix invalide.");
            }
        }
        System.out.println("\nAu revoir !\n");
        scanner.close();
    }

    // =========================================================
    // AFFICHAGE PERMANENT DES REGISTRES
    // Seuls les registres != 0 sont affichés pour rester lisible
    // =========================================================

    /*
     * Affiche les registres actifs
     */
    private static void afficherRegistresActifs() {
        System.out.println("\n╔══════════════════════════════════════════╗");
        System.out.println("║         ÉTAT ACTUEL DES REGISTRES        ║");
        System.out.println("╠══════════════════════════════════════════╣");
        boolean aucun = true;
        for (int i = 0; i < 16; i++) {
            byte val = sim.consulterRegistre(i);
            if (val != 0) {
                int u = Byte.toUnsignedInt(val);
                System.out.printf("║  %-24s = %-5d (0x%02X)   ║%n",
                        NOMS_REGISTRES[i], val, u);
                aucun = false;
            }
        }
        if (aucun) {
            System.out.println("║  (tous les registres sont à zéro)        ║");
        }
        System.out.println("╚══════════════════════════════════════════╝");
    }

    /*
     * Affiche la bannière du simulateur
     */
    private static void afficherBanniere() {
        System.out.println("\n╔══════════════════════════════════════════╗");
        System.out.println("║   Simulateur CPU — Carré Petit Utile     ║");
        System.out.println("╚══════════════════════════════════════════╝");
    }

    /*
     * Affiche le menu principal
     */
    private static void afficherMenuPrincipal() {
        System.out.println("\n  MENU PRINCIPAL");
        System.out.println("  ──────────────────────────────────────");
        System.out.println("  1. Effectuer une instruction");
        System.out.println("  2. Gérer les registres");
        System.out.println("  3. Gérer la mémoire");
        System.out.println("  4. Réinitialiser tout");
        System.out.println("  0. Quitter");
        System.out.println("  ──────────────────────────────────────");
    }

    // =========================================================
    // MENU INSTRUCTIONS
    // L'utilisateur choisit une opération en français,
    // saisit ses opérandes (valeur directe ou depuis un registre)
    // et choisit où stocker le résultat
    // =========================================================

    /*
     * Affiche le menu des instructions disponibles
     */
    private static void menuInstructions() {
        boolean retour = false;
        while (!retour) {
            afficherRegistresActifs();
            System.out.println("\n  INSTRUCTIONS DISPONIBLES");
            System.out.println("  ──────────────────────────────────────");
            System.out.println("  1. Addition");
            System.out.println("  2. Soustraction");
            System.out.println("  3. Multiplication");
            System.out.println("  4. Division");
            System.out.println("  5. OU binaire");
            System.out.println("  6. ET binaire");
            System.out.println("  7. OU exclusif (XOR)");
            System.out.println("  8. Charger une valeur dans un registre");
            System.out.println("  9. Sauvegarder un registre en mémoire");
            System.out.println("  0. Retour");
            System.out.println("  ──────────────────────────────────────");

            int choix = lireEntier("Votre choix : ");
            switch (choix) {
                case 1 -> effectuerOperationBinaire("Addition",     "ADD");
                case 2 -> effectuerOperationBinaire("Soustraction", "SUB");
                case 3 -> effectuerMultiplication();
                case 4 -> effectuerDivision();
                case 5 -> effectuerOperationBinaire("OU binaire",   "OR");
                case 6 -> effectuerOperationBinaire("ET binaire",   "AND");
                case 7 -> effectuerOperationBinaire("OU exclusif",  "XOR");
                case 8 -> chargerValeur();
                case 9 -> sauvegarderEnMemoire();
                case 0 -> retour = true;
                default -> System.out.println("  Choix invalide.");
            }
        }
    }

    // ─── ADD / SUB / OU / ET / XOR : deux opérandes, un résultat ───

    /*
        * Effectue une opération binaire (ADD, SUB, OU, ET, XOR) en demandant à l'utilisateur :
        * 1. Les deux opérandes, soit en entrant une valeur directement, soit en choisissant un registre existant
        * 2. Le registre de destination pour stocker le résultat (parmi les registres utilisateur R2-R11, R0-R1 étant réservés aux opérandes internes)
        * 3. Affiche le résultat de l'opération et où il a été stocké
        * 
        * @param nom Le nom de l'opération à afficher (ex : "Addition")
        * @param mnemo Le mnémonique de l'instruction à exécuter (ex : "ADD")
     */
    private static void effectuerOperationBinaire(String nom, String mnemo) {
        System.out.println("\n  --- " + nom + " ---");

        System.out.println("  Premier nombre — depuis :");
        int a = choisirValeurSource("  Valeur A");

        System.out.println("  Deuxième nombre — depuis :");
        int b = choisirValeurSource("  Valeur B");

        // On charge les opérandes dans R0 et R1 (réservés à ça)
        chargerDansRegistre(0, a);
        chargerDansRegistre(1, b);

        System.out.println("\n  Stocker le résultat dans quel registre ?");
        int dest = choisirRegistreDestination();

        executer(mnemo + " R0, R1, R" + dest + "\nBREAK\n");

        byte resultat = sim.consulterRegistre(dest);
        System.out.println("\n  → " + a + " " + symbolePour(mnemo) + " " + b
                + " = " + resultat + "  (stocké dans R" + dest + ")");
    }

    // ─── Multiplication : résultat réparti sur deux registres ───

    /*
        * Effectue une multiplication en demandant à l'utilisateur :
        * 1. Les deux opérandes, soit en entrant une valeur directement, soit en choisissant un registre existant
        * 2. Les deux registres de destination pour stocker le résultat (poids fort + poids faible)
        * 3. Affiche le résultat de la multiplication et où il a été stocké

    */
    private static void effectuerMultiplication() {
        System.out.println("\n  --- Multiplication ---");
        System.out.println("  Le résultat occupe deux registres (poids fort + poids faible).");

        System.out.println("  Premier nombre — depuis :");
        int a = choisirValeurSource("  Valeur A");
        System.out.println("  Deuxième nombre — depuis :");
        int b = choisirValeurSource("  Valeur B");

        chargerDansRegistre(0, a);
        chargerDansRegistre(1, b);

        System.out.println("\n  Registre pour le poids fort (octet haut) :");
        int rHaut = choisirRegistreDestination();
        System.out.println("  Registre pour le poids faible (octet bas) :");
        int rBas  = choisirRegistreDestination();

        executer("MUL R0, R1, R" + rHaut + ", R" + rBas + "\nBREAK\n");

        // On recombine les deux octets pour afficher le vrai résultat 16 bits
        byte pHaut = sim.consulterRegistre(rHaut);
        byte pBas  = sim.consulterRegistre(rBas);
        int resultat = (Byte.toUnsignedInt(pHaut) << 8) | Byte.toUnsignedInt(pBas);

        System.out.println("\n  → " + a + " × " + b + " = " + resultat);
        System.out.println("    Poids fort   R" + rHaut + " = " + Byte.toUnsignedInt(pHaut));
        System.out.println("    Poids faible R" + rBas  + " = " + Byte.toUnsignedInt(pBas));
    }

    // ─── Division : quotient et reste dans deux registres ───

    /*
        * Effectue une division en demandant à l'utilisateur :
        * 1. Le dividende et le diviseur, soit en entrant une valeur directement, soit en choisissant un registre existant
        * 2. Les deux registres de destination pour stocker le résultat (quotient + reste)
        * 3. Affiche le résultat de la division et où il a été stocké
     */
    private static void effectuerDivision() {
        System.out.println("\n  --- Division ---");
        System.out.println("  Le quotient et le reste sont stockés dans deux registres séparés.");

        System.out.println("  Dividende (nombre à diviser) — depuis :");
        int a = choisirValeurSource("  Dividende");
        System.out.println("  Diviseur — depuis :");
        int b = choisirValeurSource("  Diviseur");

        if (b == 0) {
            System.out.println("  Erreur : division par zéro impossible !");
            return;
        }

        chargerDansRegistre(0, a);
        chargerDansRegistre(1, b);

        System.out.println("\n  Registre pour le quotient :");
        int rQ = choisirRegistreDestination();
        System.out.println("  Registre pour le reste :");
        int rR = choisirRegistreDestination();

        executer("DIV R0, R1, R" + rQ + ", R" + rR + "\nBREAK\n");

        byte quotient = sim.consulterRegistre(rQ);
        byte reste    = sim.consulterRegistre(rR);

        System.out.println("\n  → " + a + " ÷ " + b + " :");
        System.out.println("    Quotient R" + rQ + " = " + quotient);
        System.out.println("    Reste    R" + rR + " = " + reste);
    }

    // ─── Charger une constante dans un registre ───

    /*
        * Charge une valeur constante dans un registre
     */
    private static void chargerValeur() {
        System.out.println("\n  --- Charger une valeur dans un registre ---");
        int valeur = lireEntier("  Valeur à charger (-128 à 127) : ");
        System.out.println("  Dans quel registre ?");
        int dest = choisirRegistreDestination();
        chargerDansRegistre(dest, valeur);
        System.out.println("  R" + dest + " = " + valeur + " ✓");
    }

    // ─── Sauvegarder un registre en mémoire ───

    /*
        * Sauvegarde le contenu d'un registre en mémoire
     */
    private static void sauvegarderEnMemoire() {
        System.out.println("\n  --- Sauvegarder un registre en mémoire ---");
        System.out.println("  Quel registre voulez-vous sauvegarder ?");
        int reg = choisirRegistreSource();
        int adr = lireEntier("  À quelle adresse mémoire (0-65535) : ");

        executer("STORE R" + reg + ", [" + adr + "]\nBREAK\n");

        byte val = sim.consulterMemoire(adr);
        System.out.println("  mem[" + adr + "] = " + val + " ✓");
    }

    // =========================================================
    // MENU REGISTRES
    // =========================================================

    /*  
     * Affiche le menu de gestion des registres
     */
    private static void menuRegistres() {
        boolean retour = false;
        while (!retour) {
            afficherRegistresActifs();
            System.out.println("\n  GESTION DES REGISTRES");
            System.out.println("  ──────────────────────────────────────");
            System.out.println("  1. Voir tous les registres");
            System.out.println("  2. Modifier un registre manuellement");
            System.out.println("  3. Copier un registre dans un autre");
            System.out.println("  0. Retour");
            System.out.println("  ──────────────────────────────────────");

            int choix = lireEntier("Votre choix : ");
            switch (choix) {
                case 1 -> afficherTousLesRegistres();
                case 2 -> modifierRegistreManuel();
                case 3 -> copierRegistre();
                case 0 -> retour = true;
                default -> System.out.println("  Choix invalide.");
            }
        }
    }

    /*  
     * Affiche tous les registres
     */
    private static void afficherTousLesRegistres() {
        System.out.println("\n  ┌──────────────────────────────┬────────┬──────────┬──────┐");
        System.out.println("  │ Registre                     │ Signé  │ Non sign.│  Hex │");
        System.out.println("  ├──────────────────────────────┼────────┼──────────┼──────┤");
        for (int i = 0; i < 16; i++) {
            byte val = sim.consulterRegistre(i);
            int u = Byte.toUnsignedInt(val);
            System.out.printf("  │ %-28s │ %6d │ %8d │ 0x%02X │%n",
                    NOMS_REGISTRES[i], val, u, u);
        }
        System.out.println("  └──────────────────────────────┴────────┴──────────┴──────┘");
    }

    /*  
     * Modifie un registre manuellement
     */
    private static void modifierRegistreManuel() {
        System.out.println("\n  Quel registre modifier ?");
        int reg = choisirRegistreDestination();
        int val = lireEntier("  Nouvelle valeur (-128 à 127) : ");
        sim.modifierRegistre(reg, (byte) val);
        System.out.println("  R" + reg + " = " + (byte) val + " ✓");
    }

    /*  
     * Copie le contenu d'un registre vers un autre
     */
    private static void copierRegistre() {
        System.out.println("\n  Registre SOURCE (à copier) :");
        int src  = choisirRegistreSource();
        System.out.println("  Registre DESTINATION :");
        int dest = choisirRegistreDestination();
        byte val = sim.consulterRegistre(src);
        sim.modifierRegistre(dest, val);
        System.out.println("  R" + dest + " = R" + src + " = " + val + " ✓");
    }

    // =========================================================
    // MENU MÉMOIRE
    // =========================================================

    /*  
     * Affiche le menu de gestion de la mémoire
     */
    private static void menuMemoire() {
        boolean retour = false;
        while (!retour) {
            afficherRegistresActifs();
            System.out.println("\n  GESTION DE LA MÉMOIRE");
            System.out.println("  ──────────────────────────────────────");
            System.out.println("  1. Consulter une adresse");
            System.out.println("  2. Consulter une plage d'adresses");
            System.out.println("  3. Charger une valeur mémoire dans un registre");
            System.out.println("  4. Modifier une adresse manuellement");
            System.out.println("  0. Retour");
            System.out.println("  ──────────────────────────────────────");

            int choix = lireEntier("Votre choix : ");
            switch (choix) {
                case 1 -> consulterAdresse();
                case 2 -> consulterPlage();
                case 3 -> chargerDepuisMemoire();
                case 4 -> modifierAdresseManuelle();
                case 0 -> retour = true;
                default -> System.out.println("  Choix invalide.");
            }
        }
    }

    /*  
     * Consulter une adresse dans la mémoire
     */
    private static void consulterAdresse() {
        int adr = lireEntier("  Adresse (0-65535) : ");
        try {
            byte val = sim.consulterMemoire(adr);
            int u = Byte.toUnsignedInt(val);
            System.out.printf("  mem[%d] = %d  (non signé : %d  hex : 0x%02X)%n",
                    adr, val, u, u);
        } catch (IllegalArgumentException e) {
            System.out.println("  Erreur : " + e.getMessage());
        }
    }

    /*  
     * Consulter une plage d'adresses dans la mémoire
     */
    private static void consulterPlage() {
        int debut = lireEntier("  Adresse de début : ");
        int fin   = lireEntier("  Adresse de fin   : ");
        if (fin < debut || fin - debut > 255) {
            System.out.println("  Plage invalide ou trop large (max 256 octets).");
            return;
        }
        System.out.println("\n  Adresse  │ Signé │ Non signé │  Hex");
        System.out.println("  ─────────┼───────┼───────────┼──────");
        for (int adr = debut; adr <= fin; adr++) {
            try {
                byte val = sim.consulterMemoire(adr);
                int u = Byte.toUnsignedInt(val);
                System.out.printf("  %7d  │ %5d │ %9d │ 0x%02X%n", adr, val, u, u);
            } catch (IllegalArgumentException e) {
                System.out.println("  Erreur : " + e.getMessage());
                break;
            }
        }
    }

    /*  
     * Charge une valeur depuis la mémoire dans un registre
     */
    private static void chargerDepuisMemoire() {
        int adr = lireEntier("  Adresse mémoire à charger (0-65535) : ");
        System.out.println("  Dans quel registre ?");
        int dest = choisirRegistreDestination();
        executer("LOAD R" + dest + ", [" + adr + "]\nBREAK\n");
        byte val = sim.consulterRegistre(dest);
        System.out.println("  R" + dest + " = mem[" + adr + "] = " + val + " ✓");
    }

    /*  
     * Modifie une adresse mémoire manuellement
     */
    private static void modifierAdresseManuelle() {
        int adr = lireEntier("  Adresse à modifier (0-65535) : ");
        int val = lireEntier("  Nouvelle valeur (-128 à 127) : ");
        try {
            sim.modifierMemoire(adr, (byte) val);
            System.out.println("  mem[" + adr + "] = " + (byte) val + " ✓");
        } catch (IllegalArgumentException e) {
            System.out.println("  Erreur : " + e.getMessage());
        }
    }

    // =========================================================
    // RÉINITIALISATION
    // =========================================================

    /*  
     * Réinitialise tous les registres et la mémoire
     */
    private static void reinitialiser() {
        System.out.print("\n  Réinitialiser tous les registres et la mémoire ? (o/n) : ");
        String rep = scanner.nextLine().trim().toLowerCase();
        if (rep.equals("o")) {
            for (int i = 0; i < 16; i++) sim.modifierRegistre(i, (byte) 0);
            for (int i = 0; i < 65536; i++) {
                try { sim.modifierMemoire(i, (byte) 0); } catch (Exception ignored) {}
            }
            System.out.println("  Réinitialisation effectuée ✓");
        }
    }

    // =========================================================
    // UTILITAIRES — SAISIE GUIDÉE
    // =========================================================


    /*  
     * Propose d'entrer une valeur directement ou de la lire depuis un registre
     *
     * @param label Le label de la valeur à choisir
     * @return La valeur choisie
     */
    private static int choisirValeurSource(String label) {
        System.out.println("  1. Entrer une valeur directement");
        System.out.println("  2. Utiliser la valeur d'un registre");
        int choix = lireEntier("  " + label + " — choix : ");
        if (choix == 2) {
            System.out.println("  Quel registre ?");
            int reg = choisirRegistreSource();
            int val = sim.consulterRegistre(reg);
            System.out.println("  → R" + reg + " = " + val);
            return val;
        }
        return lireEntier("  " + label + " (-128 à 127) : ");
    }

    // Liste tous les registres avec leur valeur pour choisir la source
    private static int choisirRegistreSource() {
        System.out.println("  Registres disponibles :");
        for (int i = 0; i < 16; i++) {
            byte val = sim.consulterRegistre(i);
            System.out.printf("    %2d. %-24s = %d%n", i, NOMS_REGISTRES[i], val);
        }
        return lireEntierBorne("  Numéro du registre : ", 0, 15);
    }

    // Liste les registres utilisateur (R2-R11) pour choisir la destination
    /*  
     * Permet de choisir un registre comme destination pour une opération
     * R0 et R1 sont réservés aux opérandes internes, R12-R15 sont réservés à l'usage interne du CPU, donc on propose à l'utilisateur de choisir parmi R2-R11 pour éviter les conflits
     *
     * @return Le numéro du registre choisi
     */
    private static int choisirRegistreDestination() {
        System.out.println("  Registres disponibles :");
        for (int i = 2; i < 12; i++) {
            byte val = sim.consulterRegistre(i);
            System.out.printf("    %2d. %-24s (actuellement : %d)%n",
                    i, NOMS_REGISTRES[i], val);
        }
        return lireEntierBorne("  Numéro du registre : ", 2, 11);
    }

    // =========================================================
    // UTILITAIRES — EXÉCUTION
    // =========================================================

    /*  
     * Charge une valeur dans un registre directement via le simulateur
     *
     * @param reg Le numéro du registre
     * @param valeur La valeur à charger
     */
    private static void chargerDansRegistre(int reg, int valeur) {
        sim.modifierRegistre(reg, (byte) valeur);
    }

    
    /*  
     * Permet d'exécuter une instruction ou une séquence d'instructions sans devoir créer un programme complet à l'avance
     * 
     * @param code Le code à exécuter
     */
    private static void executer(String code) {
        try {
            sim.saisirProgramme(code);
            sim.assembler();
            sim.executerProgramme();
        } catch (Exception e) {
            System.out.println("  Erreur d'exécution : " + e.getMessage());
        }
    }

    /*
     * Retourne le symbole mathématique associé à un mnémonique.
     *
     * @param mnemo Le mnémonique pour lequel obtenir le symbole
     * @return Le symbole mathématique correspondant
     */
    private static String symbolePour(String mnemo) {
        switch (mnemo) {
            case "ADD" -> { return "+"; }
            case "SUB" -> { return "-"; }
            case "OR"  -> { return "|"; }
            case "AND" -> { return "&"; }
            case "XOR" -> { return "^"; }
            default    -> { return mnemo; }
        }
    }

    // =========================================================
    // UTILITAIRES — LECTURE CONSOLE
    // =========================================================

    /*
     * Lit un entier dans la console, avec une valeur bornée.
     *
     * @param message Le message à afficher pour demander la saisie
     * @return L'entier saisi par l'utilisateur, garanti d'être dans la plage
     */
    private static int lireEntier(String message) {
        while (true) {
            System.out.print(message);
            try {
                return Integer.parseInt(scanner.nextLine().trim()); // On accepte que les entiers
            } catch (NumberFormatException e) {
                System.out.println("  Veuillez entrer un nombre entier.");
            }
        }
    }
 
    /*
     * Lit un entier dans la console, avec une valeur bornée.

        * @param message Le message à afficher pour demander la saisie
        * @param min La valeur minimale acceptée (inclusive)
        * @param max La valeur maximale acceptée (inclusive)
        * @return L'entier saisi par l'utilisateur, garanti d'être dans la plage

     */
    private static int lireEntierBorne(String message, int min, int max) {
        while (true) {
            int val = lireEntier(message);
            if (val >= min && val <= max) return val;
            System.out.println("  Valeur hors plage [" + min + "-" + max + "]. Réessayez.");
        }
    }
}