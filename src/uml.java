import extensions.File;
import extensions.CSVFile;

class BoisDesCancres2 extends Program { //NE PAS OUBLIER DE CHANGER LE NOM DE LA CLASSE ICI ET DANS LE run.sh !!!
    Joueur JOUEUR;
    final String CHEMIN_QUESTIONS = "ressources/questions.csv";

    void algorithm() {
        //Menu d'accueil
        //On demande au JOUEUR s'il veut commencer une nouvelle partie ou charger une sauvegarde
        afficherImage("ressources/ascii_art/logo.txt");
        println("Bienvenue dans le Bois Des Cancres !");
        println("1. Nouvelle partie");
        println("2. Ouvrir une sauvegarde");
        print("> ");
        int choix = readInt();
        if (choix==2) {
            println("Quelle sauvegarde voulez-vous charger ?");
            afficherListeSave();
            print("> ");
            int choixSave = readInt();
            JOUEUR=chargerJoueur(choixSave); //à faire
        } else if (choix==1) {
            JOUEUR=creerJoueur();
        } else {
            println("Choix invalide. Veuillez redémarrer le jeu et choisir 1 ou 2.");
            System.exit(0); //Est-ce qu'on a le droit d'utiliser System.exit() ?
        }

        if (JOUEUR.score==1) {
            println("C'est parti pour le niveau Facile !");
        } else if (JOUEUR.score==2) {
            println("C'est parti pour le niveau Moyen !");
        } else if (JOUEUR.score==3) {
            println("C'est parti pour le niveau Difficile !");
        } else if (JOUEUR.score==4) {
            println("C'est parti pour le niveau Très Difficile !");
        }

        String reponse = "1";

        while (!equals(reponse, "3")) {
            clearScreen();
            if (equals(reponse, "1")) {
                
                //Choix d'une question basée sur le niveau du JOUEUR
                int idQuestion = questionAleatoire(); //à faire
                print("Question n°"+idQuestion+": ");
                println(getQuestion(idQuestion)+"\n\n"); //à faire
                print("Votre réponse > ");
                reponse=readString();
                if (estBonneReponse(idQuestion, reponse)) {
                    println("Bonne réponse !");
                    ajouterPoints(idQuestion); //à faire
                } else {
                    println("Mauvaise réponse...");
                    println("La bonne réponse était : "+getReponses(idQuestion)[0]);
                }
            } else if (equals(reponse, "2")) {
                println("Les statistiques ne sont pas encore disponibles:(");
            } else {
                println("Choix invalide. Veuillez choisir 1, 2 ou 3.");
            }

            //Menu du jeu :
            println("\n\nQue voulez-vous faire ?");
            println("1. Continuer votre chemin");
            println("2. Regarder vos statistiques");
            println("3. Sauvegarder et quitter");
            print("> ");
            reponse = readString();
        }

        //Sauvegarde et quitter

        println("Sauvegarde en cours...");
        delay(1000); //Un peu de délai pour faire croire qu'on seuvegarde vraiment :)
        println("Partie sauvegardée. Au revoir !");
    
        
        //à remplir
    }



    ////////////////////////////////////////////
    // Fonctions pour manipuler les questions //
    ////////////////////////////////////////////

        //Schéma de questions.csv :
        //ID,Difficulté,Question,RéponsesPossibles
        //RéponsesPossibles est une liste de réponses séparées par des points-virgules

    String getQuestion(int id) {
        //Retourne la question correspondant à l'ID
        CSVFile fichier = loadCSV(CHEMIN_QUESTIONS);
        return getCell(fichier, id, 2);
    }

    int getDifficulte(int id) {
        //Retourne la difficulté de la question correspondant à l'ID
        CSVFile fichier = loadCSV(CHEMIN_QUESTIONS);
        return stringToInt(getCell(fichier, id, 1));
    }

    String[] getReponses(int id) {
        //Retourne un tableau contenant les réponses possibles à la question correspondant à l'ID
        CSVFile fichier = loadCSV(CHEMIN_QUESTIONS);
        return split(getCell(fichier, id, 3), ';');
    }

    String[] split(String chaine, char separateur) {
        //Retourne un tableau contenant les éléments de la chaîne séparés par le séparateur
        int nbSeparateurs = 0;
        for (int i=0; i<length(chaine); i++) {
            if (charAt(chaine, i)==separateur) {
                nbSeparateurs++;
            }
        }
        String[] tab = new String[nbSeparateurs+1];
        int j=0;
        String mot = "";
        for (int i=0; i<length(chaine); i++) {
            if (charAt(chaine, i)==separateur) {
                tab[j] = mot;
                mot = "";
                j++;
            } else {
                mot = mot + charAt(chaine, i);
            }
        }
        tab[j] = mot;
        return tab;
    }

    void testSplit() {
        String[] tab = split("Bonjour;je;m'appelle;Jean", ';');
        assertEquals("Bonjour", tab[0]);
        assertEquals("je", tab[1]);
        assertEquals("m'appelle", tab[2]);
        assertEquals("Jean", tab[3]);
    }

    int questionAleatoire() {
        //Retourne l'ID d'une question aléatoire en fonction du niveau du JOUEUR
        int niveau = JOUEUR.score;
        return 1;
    }

    boolean estBonneReponse(int id, String reponse) {
        //Retourne vrai si la réponse du joueur est dans la liste des bonne réponses, faux sinon
        CSVFile fichier = loadCSV(CHEMIN_QUESTIONS);
        boolean bonneReponse = false;
        String[] reponses = getReponses(id);
        for (int i=0; i<length(reponses); i++) {
            if (equals(reponses[i], toLowerCase(reponse))) {
                bonneReponse = true;
            }
        }
        return bonneReponse;
    }

    void testEstBonneReponse() {
        assertTrue(estBonneReponse(1, "A"));
        assertTrue(estBonneReponse(1, "B"));
        assertTrue(estBonneReponse(1, "C"));
        assertFalse(estBonneReponse(1, "D"));
    }

    



    ////////////////////////////////////////////
    // Fonctions pour manipuler les joueurs   //
    ////////////////////////////////////////////

    void afficherListeSave() {
        //Doit afficher tout le contenu du dossier de sauvegarde
        String[] tabFichiers = getAllFilesFromDirectory("ressources/saves");
        if (length(tabFichiers)==0) {
            println("Aucune sauvegarde trouvée. Veuillez commencer une nouvelle partie.");
        }
        for (int i=0; i<length(tabFichiers); i++) {
            println(tabFichiers[i]);
        }
    }

    Joueur creerJoueur() {
        print("Quel est votre nom ?\n> ");
        String nom = readString();
        println("Bienvenue, "+nom+", quel niveau pensez-vous avoir en Anglais ?\n1. Mauvais\n2. Moyen\n3. Bon\n4. Très bon");
        print("> ");
        int niveau = readInt();

        //Il faudrait se mettre d'accord sur quels points il faut pour chaque niveau
        //Dans ce cas, si le joueur dis qu'il est très bon, il faut lui mettre combien de points ?
        return newJoueur(nom, niveau, new int[4][2]); //Quelle taille pour le tableau d'un nouveau joueur ? Est-ce qu'il nous faudra une fonction pour agrandir le tableau au bout d'un moment ?
    }

    Joueur newJoueur(String nom, int score, int[][] stats_questions) {
        Joueur j = new Joueur();
        j.nom = nom;
        j.score = score;
        j.stats_questions = stats_questions;
        return j;
    }

    Joueur chargerJoueur(int choix) {
        //à remplir
        return new Joueur();
    }

    void saveJoueur() {
        //à remplir
    }

    ////////////////////////////////////////////



    void afficherImage(String chemin){
        //Affiche une image en ASCII art et supprime le contenu de la console.
        //Le chemin doit pointer vers un fichier texte contenant l'image en ASCII art.
        clearScreen();
        File fichier = new File(chemin);
        while (ready(fichier)){
            println(fichier.readLine());
        }
    }

    void ajouterPoints(int id) {
        println("!!!!!!!!!!!!!! Fonction ajouterPoints à faire !");
        //à remplir
    }
}