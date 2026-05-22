import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

import simulateur.Simulateur;

/**
 * Point d'entrée de l'application.
 * Fournit une interface console interactive permettant à l'utilisateur
 * d'effectuer des opérations sur le simulateur CPU (instructions, registres, mémoire).
 */
public class Main {

    /** Instance unique du simulateur partagée par toutes les méthodes. */
    private static final Simulateur sim = new Simulateur();

    /** Scanner global pour lire les entrées utilisateur. */
    private static final Scanner scanner = new Scanner(System.in);

    /**
     * Noms lisibles des 16 registres affichés dans les menus.
     * R0 et R1 sont réservés aux opérandes internes du CPU.
     * R2 à R11 sont disponibles pour l'utilisateur.
     * R12 à R15 sont réservés à un usage interne.
     */
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

    /** Chemin du fichier programme lu automatiquement par l'option 5 du menu. */
    private static final String FICHIER_PROGRAMME = 
    System.getProperty("user.dir") + "/Projet Java/ProjetCPU/src/main/java/programme.txt";

    /**
     * Point d'entrée principal. Affiche la bannière et lance la boucle
     * du menu principal jusqu'à ce que l'utilisateur choisisse de quitter.
     *
     * @param args arguments de la ligne de commande (non utilisés)
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
                case 5 -> executerFichier();
                case 0 -> continuer = false;
                default -> System.out.println("  Choix invalide.");
            }
        }
        System.out.println("\nAu revoir !\n");
        scanner.close();
    }

    // =========================================================
    // AFFICHAGE
    // =========================================================

    /**
     * Affiche l'état actuel des registres non nuls.
     * Seuls les registres dont la valeur est différente de zéro sont affichés
     * afin de ne pas surcharger l'écran.
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

    /**
     * Affiche la bannière de bienvenue du simulateur.
     */
    private static void afficherBanniere() {
        System.out.println("\n╔══════════════════════════════════════════╗");
        System.out.println("║   Simulateur CPU — Carré Petit Utile     ║");
        System.out.println("╚══════════════════════════════════════════╝");
    }

    /**
     * Affiche les options du menu principal.
     */
    private static void afficherMenuPrincipal() {
        System.out.println("\n  MENU PRINCIPAL");
        System.out.println("  ──────────────────────────────────────");
        System.out.println("  1. Effectuer une instruction");
        System.out.println("  2. Gérer les registres");
        System.out.println("  3. Gérer la mémoire");
        System.out.println("  4. Réinitialiser tout");
        System.out.println("  5. Exécuter programme.txt");
        System.out.println("  0. Quitter");
        System.out.println("  ──────────────────────────────────────");
    }

    // =========================================================
    // MENU INSTRUCTIONS
    // =========================================================

    /**
     * Affiche le menu des instructions disponibles et redirige vers
     * la méthode correspondante selon le choix de l'utilisateur.
     * La boucle continue jusqu'à ce que l'utilisateur choisisse de revenir.
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
            System.out.println("  10. Charger une valeur depuis une adresse indexée (LOAD indexé)");
            System.out.println("  11. Écrire à une adresse indexée (STORE indexé)");
            System.out.println("  0. Retour");
            System.out.println("  ──────────────────────────────────────");

            int choix = lireEntier("Votre choix : ");
            switch (choix) {
                case 1  -> effectuerOperationBinaire("Addition",     "ADD");
                case 2  -> effectuerOperationBinaire("Soustraction", "SUB");
                case 3  -> effectuerMultiplication();
                case 4  -> effectuerDivision();
                case 5  -> effectuerOperationBinaire("OU binaire",   "OU");
                case 6  -> effectuerOperationBinaire("ET binaire",   "ET");
                case 7  -> effectuerOperationBinaire("OU exclusif",  "XOR");
                case 8  -> chargerValeur();
                case 9  -> sauvegarderEnMemoire();
                case 10 -> chargerDepuisTableau();
                case 11 -> ecrireDansTableau();
                case 0  -> retour = true;
                default -> System.out.println("  Choix invalide.");
            }
        }
    }

    /**
     * Effectue une opération binaire (ADD, SUB, OU, ET, XOR) sur deux opérandes
     * choisis par l'utilisateur et stocke le résultat dans un registre destination.
     * Les opérandes peuvent être des valeurs directes ou lues depuis un registre.
     *
     * @param nom   le nom lisible de l'opération (ex : "Addition")
     * @param mnemo le mnémonique assembleur correspondant (ex : "ADD")
     */
    private static void effectuerOperationBinaire(String nom, String mnemo) {
        System.out.println("\n  --- " + nom + " ---");

        System.out.println("  Premier nombre — depuis :");
        int a = choisirValeurSource("  Valeur A");

        System.out.println("  Deuxième nombre — depuis :");
        int b = choisirValeurSource("  Valeur B");

        // R0 et R1 sont réservés pour transporter les opérandes vers le CPU
        chargerDansRegistre(0, a);
        chargerDansRegistre(1, b);

        System.out.println("\n  Stocker le résultat dans quel registre ?");
        int dest = choisirRegistreDestination();

        executer(mnemo + " R0, R1, R" + dest + "\nBREAK\n");

        byte resultat = sim.consulterRegistre(dest);
        System.out.println("\n  → " + a + " " + symbolePour(mnemo) + " " + b
                + " = " + resultat + "  (stocké dans R" + dest + ")");
    }

    /**
     * Effectue une multiplication entre deux opérandes.
     * Le résultat 16 bits est réparti sur deux registres :
     * un pour le poids fort (octet haut) et un pour le poids faible (octet bas).
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

        // Recombinaison des deux octets pour afficher le résultat complet sur 16 bits
        byte pHaut = sim.consulterRegistre(rHaut);
        byte pBas  = sim.consulterRegistre(rBas);
        int resultat = (Byte.toUnsignedInt(pHaut) << 8) | Byte.toUnsignedInt(pBas);

        System.out.println("\n  → " + a + " × " + b + " = " + resultat);
        System.out.println("    Poids fort   R" + rHaut + " = " + Byte.toUnsignedInt(pHaut));
        System.out.println("    Poids faible R" + rBas  + " = " + Byte.toUnsignedInt(pBas));
    }

    /**
     * Effectue une division entière entre deux opérandes.
     * Le quotient et le reste sont stockés dans deux registres distincts.
     * Une vérification empêche la division par zéro avant l'exécution.
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

    /**
     * Charge une valeur constante choisie par l'utilisateur dans un registre destination.
     */
    private static void chargerValeur() {
        System.out.println("\n  --- Charger une valeur dans un registre ---");
        int valeur = lireEntier("  Valeur à charger (-128 à 127) : ");
        System.out.println("  Dans quel registre ?");
        int dest = choisirRegistreDestination();
        chargerDansRegistre(dest, valeur);
        System.out.println("  R" + dest + " = " + valeur + " ✓");
    }

    /**
     * Sauvegarde la valeur d'un registre choisi par l'utilisateur
     * à une adresse mémoire donnée via l'instruction STORE.
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

    /**
     * Charge dans un registre destination la valeur lue à l'adresse
     * mem[adresseBase + valeur du registre d'index] via l'instruction LOAD indexé.
     * Permet de parcourir un tableau en mémoire avec un registre d'index variable.
     */
    private static void chargerDepuisTableau() {
        System.out.println("\n  --- Charger depuis un tableau (LOAD indexé) ---");
        System.out.println("  Principe : charge mem[adresse de base + valeur du registre index]");

        int adrBase = lireEntier("  Adresse de base du tableau (0-65535) : ");

        // Le registre d'index contient le décalage à appliquer à l'adresse de base
        System.out.println("  Registre d'index (sa valeur = décalage dans le tableau) :");
        int regIdx = choisirRegistreSource();
        byte valIdx = sim.consulterRegistre(regIdx);
        int adresseReelle = adrBase + Byte.toUnsignedInt(valIdx);
        System.out.println("  → R" + regIdx + " = " + Byte.toUnsignedInt(valIdx)
                + "  donc on lira mem[" + adrBase + " + " + Byte.toUnsignedInt(valIdx)
                + "] = mem[" + adresseReelle + "]");

        System.out.println("  Stocker le résultat dans quel registre ?");
        int dest = choisirRegistreDestination();

        executer("LOAD R" + dest + ", [" + adrBase + "], R" + regIdx + "\nBREAK\n");

        byte resultat = sim.consulterRegistre(dest);
        System.out.println("\n  → R" + dest + " = mem[" + adresseReelle + "] = " + resultat + " ✓");
    }

    /**
     * Écrit la valeur d'un registre source à l'adresse
     * mem[adresseBase + valeur du registre d'index] via l'instruction STORE indexé.
     * Permet d'écrire dans un tableau en mémoire avec un registre d'index variable.
     */
    private static void ecrireDansTableau() {
        System.out.println("\n  --- Écrire dans un tableau (STORE indexé) ---");
        System.out.println("  Principe : écrit la valeur d'un registre dans mem[adresse de base + valeur du registre index]");

        System.out.println("  Quel registre voulez-vous écrire dans le tableau ?");
        int regSrc = choisirRegistreSource();
        byte valSrc = sim.consulterRegistre(regSrc);

        int adrBase = lireEntier("  Adresse de base du tableau (0-65535) : ");

        // Le registre d'index contient le décalage
        System.out.println("  Registre d'index (sa valeur = décalage dans le tableau) :");
        int regIdx = choisirRegistreSource();
        byte valIdx = sim.consulterRegistre(regIdx);
        int adresseReelle = adrBase + Byte.toUnsignedInt(valIdx);
        System.out.println("  → R" + regIdx + " = " + Byte.toUnsignedInt(valIdx)
                + "  donc on écrira dans mem[" + adrBase + " + " + Byte.toUnsignedInt(valIdx)
                + "] = mem[" + adresseReelle + "]");

        executer("STORE R" + regSrc + ", [" + adrBase + "], R" + regIdx + "\nBREAK\n");

        System.out.println("\n  → mem[" + adresseReelle + "] = " + valSrc + " ✓");
    }

    // =========================================================
    // MENU REGISTRES
    // =========================================================

    /**
     * Affiche le sous-menu de gestion des registres et redirige vers
     * la fonctionnalité choisie par l'utilisateur.
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

    /**
     * Affiche la valeur de tous les registres (R0 à R15) sous forme de tableau
     * avec les représentations signée, non signée et hexadécimale.
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

    /**
     * Permet à l'utilisateur de modifier manuellement la valeur d'un registre
     * en saisissant un numéro de registre et une nouvelle valeur.
     */
    private static void modifierRegistreManuel() {
        System.out.println("\n  Quel registre modifier ?");
        int reg = choisirRegistreDestination();
        int val = lireEntier("  Nouvelle valeur (-128 à 127) : ");
        sim.modifierRegistre(reg, (byte) val);
        System.out.println("  R" + reg + " = " + (byte) val + " ✓");
    }

    /**
     * Copie la valeur d'un registre source dans un registre destination.
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

    /**
     * Affiche le sous-menu de gestion de la mémoire et redirige vers
     * la fonctionnalité choisie par l'utilisateur.
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

    /**
     * Affiche la valeur stockée à une adresse mémoire saisie par l'utilisateur,
     * sous les formats signé, non signé et hexadécimal.
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

    /**
     * Affiche les valeurs d'une plage d'adresses mémoire consécutives.
     * La plage est limitée à 256 octets maximum pour rester lisible.
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

    /**
     * Charge dans un registre destination la valeur lue à une adresse mémoire
     * saisie par l'utilisateur, via l'instruction LOAD mémoire.
     */
    private static void chargerDepuisMemoire() {
        int adr = lireEntier("  Adresse mémoire à charger (0-65535) : ");
        System.out.println("  Dans quel registre ?");
        int dest = choisirRegistreDestination();
        executer("LOAD R" + dest + ", [" + adr + "]\nBREAK\n");
        byte val = sim.consulterRegistre(dest);
        System.out.println("  R" + dest + " = mem[" + adr + "] = " + val + " ✓");
    }

    /**
     * Modifie manuellement la valeur d'une case mémoire à une adresse donnée.
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


    /**
     * Lit le fichier "programme.txt" situé à la racine du projet,
     * assemble son contenu et l'exécute sur le simulateur.
     * Le fichier doit contenir du code assembleur valide, une instruction par ligne.
     * Si le fichier est absent ou contient des erreurs, un message est affiché.
     */
    private static void executerFichier() {
        System.out.println("\n  --- Exécution de " + FICHIER_PROGRAMME + " ---");
        try {
            // Lecture de toutes les lignes du fichier
            String code = new String(Files.readAllBytes(Paths.get(FICHIER_PROGRAMME)));

            // Affichage du contenu pour que l'utilisateur voie ce qui va être exécuté
            System.out.println("  Contenu du fichier :");
            System.out.println("  ──────────────────────────────────────");
            String[] lignes = code.split("\n");
            for (int i = 0; i < lignes.length; i++) {
                System.out.printf("  %3d | %s%n", i + 1, lignes[i]);
            }
            System.out.println("  ──────────────────────────────────────");

            // Assemblage et exécution
            sim.saisirProgramme(code);
            sim.assembler();
            sim.executerProgramme();

            System.out.println("  Exécution terminée ✓");
            System.out.println("  État des registres après exécution :");
            afficherTousLesRegistres();

        } catch (java.nio.file.NoSuchFileException e) {
            System.out.println("  Fichier introuvable : " + FICHIER_PROGRAMME);
            System.out.println("  Créez ce fichier à la racine du projet et écrivez votre programme dedans.");
        } catch (RuntimeException e) {
            System.out.println("  Erreur : " + e.getMessage());
        } catch (Exception e) {
            System.out.println("  Erreur de lecture : " + e.getMessage());
        }
    }

    // =========================================================
    // RÉINITIALISATION
    // =========================================================

    /**
     * Réinitialise tous les registres et toute la mémoire à zéro
     * après confirmation de l'utilisateur.
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

    /**
     * Propose à l'utilisateur de saisir une valeur soit directement au clavier,
     * soit en lisant le contenu d'un registre existant.
     *
     * @param label le libellé affiché pour identifier l'opérande (ex : "Valeur A")
     * @return la valeur entière choisie par l'utilisateur
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

    /**
     * Affiche la liste de tous les registres avec leur valeur courante
     * et demande à l'utilisateur d'en choisir un comme source (R0 à R15).
     *
     * @return le numéro du registre choisi (entre 0 et 15)
     */
    private static int choisirRegistreSource() {
        System.out.println("  Registres disponibles :");
        for (int i = 0; i < 16; i++) {
            byte val = sim.consulterRegistre(i);
            System.out.printf("    %2d. %-24s = %d%n", i, NOMS_REGISTRES[i], val);
        }
        return lireEntierBorne("  Numéro du registre : ", 0, 15);
    }

    /**
     * Affiche la liste des registres utilisateur disponibles comme destination (R2 à R11)
     * avec leur valeur courante, et demande à l'utilisateur d'en choisir un.
     * R0 et R1 sont exclus car réservés aux opérandes internes.
     *
     * @return le numéro du registre destination choisi (entre 2 et 11)
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

    /**
     * Charge directement une valeur entière dans un registre via le simulateur,
     * sans passer par l'assembleur.
     *
     * @param reg    le numéro du registre cible (0 à 15)
     * @param valeur la valeur à charger (sera castée en byte)
     */
    private static void chargerDansRegistre(int reg, int valeur) {
        sim.modifierRegistre(reg, (byte) valeur);
    }

    /**
     * Saisit, assemble et exécute un mini-programme assembleur one-shot.
     * Utilisé en interne pour chaque opération demandée par l'utilisateur.
     * Les registres sont conservés entre les appels grâce au simulateur partagé.
     *
     * @param code le code assembleur à exécuter (terminé par BREAK)
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

    /**
     * Retourne le symbole mathématique correspondant à un mnémonique assembleur.
     * Utilisé uniquement pour l'affichage du résultat à l'utilisateur.
     *
     * @param mnemo le mnémonique assembleur (ADD, SUB, OU, ET, XOR)
     * @return le symbole mathématique associé (+, -, |, &, ^)
     */
    private static String symbolePour(String mnemo) {
        switch (mnemo) {
            case "ADD" -> { return "+"; }
            case "SUB" -> { return "-"; }
            case "OU"  -> { return "|"; }
            case "ET"  -> { return "&"; }
            case "XOR" -> { return "^"; }
            default    -> { return mnemo; }
        }
    }

    // =========================================================
    // UTILITAIRES — LECTURE CONSOLE
    // =========================================================

    /**
     * Lit un entier saisi par l'utilisateur dans la console.
     * Redemande tant que la saisie n'est pas un entier valide.
     *
     * @param message le message à afficher avant la saisie
     * @return l'entier saisi par l'utilisateur
     */
    private static int lireEntier(String message) {
        while (true) {
            System.out.print(message);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("  Veuillez entrer un nombre entier.");
            }
        }
    }

    /**
     * Lit un entier dans la console en vérifiant qu'il est compris dans [min, max].
     * Redemande tant que la valeur est hors de la plage autorisée.
     *
     * @param message le message à afficher avant la saisie
     * @param min     la valeur minimale acceptée (incluse)
     * @param max     la valeur maximale acceptée (incluse)
     * @return l'entier saisi, compris entre min et max
     */
    private static int lireEntierBorne(String message, int min, int max) {
        while (true) {
            int val = lireEntier(message);
            if (val >= min && val <= max) return val;
            System.out.println("  Valeur hors plage [" + min + "-" + max + "]. Réessayez.");
        }
    }
}