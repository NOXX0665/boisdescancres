import extensions.File;
import extensions.CSVFile;

class BoisDesCancres2 extends Program { //NE PAS OUBLIER DE CHANGER LE NOM DE LA CLASSE ICI ET DANS LE run.sh !!!
    Joueur joueur;
    Joueur joueur;
    final String CHEMIN_QUESTIONS = "ressources/questions.csv";

    void algorithm() {
        //Menu d'accueil
        //On demande au joueur s'il veut commencer une nouvelle partie ou charger une sauvegarde
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
            joueur=chargerJoueur(choixSave); //à faire
        } else if (choix==1) {
            joueur=creerJoueur();
        } else {
            //Je suis pas fière de moi là dessus, faudrait trouver une meilleure solution qui dit
            println("Choix invalide. Veuillez redémarrer le jeu et choisir 1 ou 2.");
            System.exit(0); //Est-ce qu'on a le droit d'utiliser System.exit() ?
        }

        if (joueur.score==1) {
        if (joueur.score==1) {
            println("C'est parti pour le niveau Facile !");
        } else if (joueur.score==2) {
        } else if (joueur.score==2) {
            println("C'est parti pour le niveau Moyen !");
        } else if (joueur.score==3) {
        } else if (joueur.score==3) {
            println("C'est parti pour le niveau Difficile !");
        } else if (joueur.score==4) {
        } else if (joueur.score==4) {
            println("C'est parti pour le niveau Très Difficile !");
        }
        delay(1000);

        String reponse = "1";

        while (!equals(reponse, "3")) {
            clearScreen();
            if (equals(reponse, "1")) {
                //Choix d'une question basée sur le niveau du joueur
                int idQuestion = questionAleatoire(); //à faire

                //On pose la question et on vérifie si la réponse est bonne
                boolean bonneReponse = poserQuestion(idQuestion); //en cours
            } else if (equals(reponse, "2")) {
                //Affichage des statistiques du joueur
                print("L'affichage des statistiques n'est pas encore implémenté.");
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
        delay(1000); //Il faut faire la fonction de sauvegarde
        delay(1000); //Il faut faire la fonction de sauvegarde
        println("Partie sauvegardée. Au revoir !");
    }


    boolean poserQuestion(int idQuestion) {
        //Pose une question au joueur et retourne vrai si la réponse est bonne, faux sinon
        print("Question n°"+idQuestion+": ");
        println(getQuestion(idQuestion)+"\n\n");
        }

        boolean reponseValide = false; //Une réponse valide est soit une bonne réponse, soit un indice, soit un passage de question (dans les deux derniers cas, c'est true seulement si le joueur a assez de points bonus)

        while (!reponseValide) {
            reponseValide = demanderReponse(idQuestion);
        }

        return true;
    }

    boolean demanderReponse(int idQuestion) {
        //Demande une réponse au joueur, vérifie si elle est valide et retourne vrai si la réponse est valide (soit bonne réponse, soit passer, soit indice), faux sinon.
        print("Votre réponse > ");
        String reponse = readString();
        if (estBonneReponse(idQuestion, reponse)) { //Si c'est une bonne réponse
            println("Bonne réponse !");
            return true;
        } else { //Si ce n'est pas la bonne réponse
            println("Mauvaise réponse...");
            println("La bonne réponse était : "+getReponses(1)[0]);
            return false;
        }
    }


    boolean poserQuestion(int idQuestion) {
        //Pose une question au joueur et retourne vrai si la réponse est bonne, faux sinon
        print("Question n°"+idQuestion+": ");
        println(getQuestion(idQuestion)+"\n\n");


        if (joueur.pointsBonus>0) {
            println("Il vous reste "+joueur.pointsBonus+" points bonus.\nVous pouvez passer la question en tapant 'passer' ou demander un indice en tapant 'indice'.");
            (Système des points bonus)
        }

        boolean reponseValide = false; //Une réponse valide est soit une bonne réponse, soit un indice, soit un passage de question (dans les deux derniers cas, c'est true seulement si le joueur a assez de points bonus)

        while (!reponseValide) {
            reponseValide = demanderReponse(idQuestion);
        }

        return true;
    }

    boolean demanderReponse(int idQuestion) {
        //Demande une réponse au joueur, vérifie si elle est valide et retourne vrai si la réponse est valide (soit bonne réponse, soit passer, soit indice), faux sinon.
        print("Votre réponse > ");
        String reponse = readString();
        if (estBonneReponse(idQuestion, reponse)) { //Si c'est une bonne réponse
            println("Bonne réponse !");
            return true;
            ajouterPointsBonus(idQuestion);
            return true;
        } else if (equals(reponse, "passer")) { //Si le joueur veut passer la question
            if (joueur.pointsBonus>0) {
                println("Vous avez passé la question.");
                joueur.pointsBonus--;
                return true;
            } else {
                println("Vous n'avez pas assez de points bonus pour passer la question.");
                return false;
            }
        } else if (equals(reponse, "indice")) { //Si le joueur veut un indice
            if (joueur.pointsBonus>0) {
                println("Indice : "+getIndices(idQuestion));
                joueur.pointsBonus--;
                return true;
            } else {
                println("Vous n'avez pas assez de points bonus pour demander un indice.");
                return false;
            }
        } else { //Si ce n'est pas la bonne réponse
            println("Mauvaise réponse...");
            println("La bonne réponse était : "+getReponses(1)[0]);
            return false;
        }
    }


    ////////////////////////////////////////////
    // Fonctions pour manipuler les questions //
    ////////////////////////////////////////////

        //Schéma de questions.csv :
        //ID,Difficulté,Question,RéponsesPossibles,Indice(s)   //Est-ce qu'on devrait faire plusieurs indices par question ?
        //ID,Difficulté,Question,RéponsesPossibles,Indice(s)   //Est-ce qu'on devrait faire plusieurs indices par question ?
        //RéponsesPossibles est une liste de réponses séparées par des points-virgules
        //
        //La première question (ID=1) est une question test pour vérifier que tout fonctionne bien. Cette question est utilisée dans les fonctions de tests

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
        //Retourne l'ID d'une question aléatoire en fonction du niveau du joueur
        int niveau = joueur.score;
        return 1;
    }

    int[] tableauPoids() {
        //Utilise les stats du joueur pour retourner un tableau de poids pour chaque question
        //A faire
        return new int[1];
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

    String getIndices(int id) {
        //Retourne les indices de la question correspondant à l'ID
        CSVFile fichier = loadCSV(CHEMIN_QUESTIONS);
        return getCell(fichier, id, 4);
    }

    void testGetIndices() {
        assertEquals("Les réponses sont A B ou C", getIndices(1));
    }


    ////////////////////////////////////////////
    // Fonctions pour manipuler les joueurs   //
    ////////////////////////////////////////////

    // Structure d'un joueur :

    // String nom;
    // int score;
    // int pointsBonus;
    // int[][] stats_questions;

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

        //Il faudrait se mettre d'accord sur combien de points il faut pour chaque niveau
        //Dans ce cas, si le joueur dis qu'il est très bon, il faut lui mettre combien de points ?
        return newJoueur(nom, niveau, 3, new int[4][2]); //Quelle taille pour le tableau d'un nouveau joueur ? Est-ce qu'il nous faudra une fonction pour agrandir le tableau au bout d'un moment vu qu'il y aura de plus en plus de stats ?
    }

    Joueur newJoueur(String nom, int score, int pointsBonus, int[][] stats_questions) {
        Joueur j = new Joueur();
        j.nom = nom;
        j.score = score;
        j.pointsBonus = pointsBonus;
        j.stats_questions = stats_questions;
        return j;
    }
//A vérifier
    Joueur chargerJoueur(String nomFichier) {
        CSVFile fichier = loadCSVFile(nomFichier);
        int nom = getCell(fichier,0,0);
        int score = getCell(fichier,0,1);
        int pointsBonus = getCell(fichier,0,2);
        //Création d'un tableau de 5 colonnes (pour l'id et les 4 stats) et d'autant de lignes que de questions
        int[][] stats_questions = new int[rowCount(fichier)-1][5];
        // Colonne 0 : id de la question | Colonne 1 : Nombre de fois où elle est tombée | 2 : Nb de fois réussie | 3 : Nb de fois skip | 4 : nbFois Ratée
        for(int ligne = 1; ligne <= rowCount(fichier); ligne++){
            for(int colonne = 0; colonne < 5; colonne++){
                stats_questions[ligne][colonne] = stringToInt(getCell(fichier,ligne,colonne));
            }
        }
        return newJoueur(nom,score,pointsBonus,stats_questions);
    }


    int pointsToNiveau(int points) {
        //Retourne le niveau correspondant à un nombre de points
        //à remplir
        return 1;
    }
//A vérifier
    void saveJoueur(Joueur joueur, String nomFichier) {
        String[][] saveString = new String[length(joueur.stats_questions,1)][5];
        saveString[0][0] = joueur.nom;
        saveString[0][1] = "" + joueur.score;
        saveString[0][2] = "" + joueur.pointsBonus;
        for(int ligne = 1; ligne <= length(joueur.stats_questions,1); ligne++){
            for(int colonne = 0; colonne < 5; colonne++){
                saveString[ligne][colonne] = joueur.stats_questions[ligne][colonne];
            }
        }
        saveCSV(saveString,nomFichier);
    }

    //Squelette questions dans le fichier save :
    //id,nbRencontree,nbReussie,nbSkip,nbRatee


//A vérifier
    void afficherStatistiques(Joueur joueur){
        println("Vos statistiques :");
        println("Votre pseudo : " + joueur.nom);
        println("Votre score : " + joueur.score);
        // Quand on se sera mis d'accord sur l'xp nécessaire pour monter de niveau, on pourra rajouter un affichage du niveau et de l'avancement avant le prochain avec les petits carrés ☐ et ■
        println("Votre nombre de points bonus : " + joueur.pointsBonus);
    }
//A verifier
    void afficherStatAvancee(Joueur joueur){
        println("Voici vos statistiques pour chacune des questions implémentées :")
        for(int i = 0; i < length(joueur.stats_questions,1); i++){
            println("Question numéro " + i + " : ");
            println("   Nombre de fois rencontrée : " + joueur.stats_questions[i][0]);
            println("   Nombre de fois réussie : " + joueur.stats_questions[i][1]);
            println("   Nombre de fois que vous l'avez passée : " + joueur.stats_questions[i][2]);
            println("   Nombre de fois ratée : " + joueur.stats_questions[i][3]);
        }
    }
    /////////////////////////////////////
    // Fonctions pour les points bonus //
    /////////////////////////////////////

    void ajouterPointsBonus(int idQuestion) {
        //Vérifie si le joueur gagne un point bonus et l'ajoute à son score
        //Un joueur peut gagner un point bonus avec une probabilité dépendant de son niveau et de la difficulté de la question
        double proba = probaPoint(idQuestion);
        if (random()<proba) {
            joueur.pointsBonus++;
            println("Vous avez gagné un point bonus !\nIl peut vous servir à passer une question ou à demander un indice.");
        }
    }

    double probaPoint(int idQuestion) {
        //Retourne la probabilité d'obtenir un point bonus
        //La probabilité est calculée en fonction du niveau du joueur ainsi que du niveau de la question
        // Chance pt bonus = (niveau de la question)-(notre niveau) * 0.1 +0.1
        int niveauQuestion = getDifficulte(idQuestion);
        int niveauJoueur = joueur.score;
        return (niveauQuestion-niveauJoueur)*0.1+0.1;
    }


    /////////////////////////////////////

    void afficherImage(String chemin){
        //Affiche une image en ASCII art et supprime le contenu de la console.
        //Le chemin doit pointer vers un fichier texte contenant l'image en ASCII art.
        clearScreen();
        File fichier = new File(chemin);
        while (ready(fichier)){
            println(fichier.readLine());
        }
    }


}