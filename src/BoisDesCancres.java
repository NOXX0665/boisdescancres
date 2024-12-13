import extensions.File;
import extensions.CSVFile;

class BoisDesCancres extends Program { //NE PAS OUBLIER DE CHANGER LE NOM DE LA CLASSE ICI ET DANS LE run.sh !!!
    Joueur joueur; //passer en paramètres et pas en variable globale
    final String CHEMIN_QUESTIONS = "ressources/questions.csv";
    final String CHEMIN_SAUVEGARDES = "ressources/sauvegardes/";

    void algorithm() {
        //Menu d'accueil
        //On demande au joueur s'il veut commencer une nouvelle partie ou charger une sauvegarde
        afficherImage("ressources/ascii_art/logo.txt");
        
        boolean valide = false;
        String choix = "";
        println(choix);
        while(!valide){
            println("Bienvenue dans le Bois Des Cancres !");
            println("1. Nouvelle partie");
            println("2. Ouvrir une sauvegarde");
            println("3. Quitter le jeu");
            print("> ");
            choix = readString();
            
            if(equals(choix,"3")){
                System.exit(0);
            }
            else if (equals(choix,"2")) {
                valide = menuChargerSave();
            } 
            else if (equals(choix,"1")) {
                joueur=creerJoueur();
                valide=true;
            } 
            else{
                println("Entrée invalide, veuillez réessayer : ");
            }
        }

        // On affiche un message du type "C'est parti pour le niveau {niveau du joueur}"
        String[] listeNiveaux = new String[]{"Facile", "Moyen", "Difficile", "Très Difficile"};
        println("C'est parti pour le niveau "+listeNiveaux[joueur.niveau-1]+" !");

        delay(1000);

        choix = "-1";

        println("\n\n\nVous vous réveillez dans une étrange forêt... LALALA CONTEXTE ICI");

        while (!equals(choix, "3")) {
            //Menu du jeu :
                println("\n\nQue voulez-vous faire ?");
                println("1. Continuer votre chemin");
                println("2. Regarder vos statistiques");
                println("3. Sauvegarder et quitter");
                print("> ");
            choix = readString();

            if (equals(choix, "1")) {
                //Choix d'une question parmis toutes les questions dans le fichier ../ressources/questions.csv
                Question question = questionAleatoire(tousLesId()); //à améliorer

                //On pose la question
                poserQuestion(question);

                //On demande la réponse
                //Une réponse valide est soit une bonne réponse, soit un indice, soit un passage de question (dans les deux derniers cas, c'est true seulement si le joueur a assez de points bonus)
                boolean reponseValide = false;
                while (!reponseValide) {
                    reponseValide = demanderReponse(question);
                }
                
            } else if (equals(choix, "2")) {
                menuStats(joueur);
            }

            
        }

        //Sauvegarde et quitter
        println("Sauvegarde en cours...");
        saveJoueur(joueur, joueur.nom+".csv");
        println("Partie sauvegardée. Au revoir !");
    }


    ////////////////////////////////////////////
    // Fonctions pour les menus et l'interface //
    ////////////////////////////////////////////


    boolean menuChargerSave() {
        clearScreen();
        println("Quelle sauvegarde voulez-vous charger ?");
        afficherListeSave();
        print("> ");
        String choixSave = toLowerCase(readString());
        joueur=chargerJoueur(choixSave+".csv");
        return true;
    }

    void menuStats(Joueur joueur) {
        clearScreen();
        //Affichage des statistiques du joueur
        afficherStatistiques();
        println("\n"); //Pour aérer un peu

        //On demande s'il veut voir ses stats avancées
        println("Que souhaitez-vous faire ?");
        println("1. Voir des statistique plus précises sur les questions");
        println("2. Revenir au jeu");
        print("> ");
        String choix = readString();
        if (equals(choix, "1")) {
            afficherStatAvancee();
            println("\nAppuyez sur Entrée pour revenir au jeu.");
            readString();
        }
    }

    boolean poserQuestion(Question question) {
        //Pose une question au joueur et retourne vrai si la réponse est bonne, faux sinon
        print("Question n°"+question.id+": ");
        println(question.question+"\n\n");

        if (joueur.pointsBonus>0) {
            println("Il vous reste "+joueur.pointsBonus+" points bonus.\nVous pouvez passer la question en tapant 'passer' ou demander un indice en tapant 'indice'.\n");
        }

        return true;
    }

    boolean demanderReponse(Question question) {
        //Demande une réponse au joueur, vérifie si elle est valide et retourne vrai si la réponse est valide (soit bonne réponse, soit passer, soit indice), faux sinon.
        print("Votre réponse > ");
        String reponse = readString();
        question.nbRencontree++;
        if (!(coeffReponse(question, reponse)==-1)) { //Si c'est une bonne réponse
            clearScreen();
            println("Bonne réponse !");
            ajouterPointsBonus(question);
            joueur.score++;
            question.nbReussie++;
            return true;
        } else if (equals(reponse, "passer")) { //Si le joueur veut passer la question
            if (joueur.pointsBonus>0) {
                println("Vous avez passé la question.");
                question.nbSkip++;
                joueur.pointsBonus--;
                return true;
            } else {
                println("Vous n'avez pas assez de points bonus pour passer la question.");
                return false;
            }
        } else if (equals(reponse, "indice")) { //Si le joueur veut un indice
            if (joueur.pointsBonus>0) {
                println("Indice : "+question.indice);
                //Pas besoin d'incrémenter le compteur d'indices dans les stats car on peut le calculer avec les autres stats
                joueur.pointsBonus--;
                return false;
            } else {
                println("Vous n'avez pas assez de points bonus pour demander un indice.");
                return false;
            }
        } else { //Si ce n'est pas la bonne réponse
            println("Mauvaise réponse...");
            println("La bonne réponse était : "+question.reponses[0][0]);
            question.nbRatee++;
            return true;
        }
    }


    ////////////////////////////////////////////
    // Fonctions pour manipuler les questions //
    ////////////////////////////////////////////

        //Schéma de questions.csv :
        //ID,Difficulté,Question,RéponsesPossibles,Indice(s)   //Est-ce qu'on devrait faire plusieurs indices par question ?
        //RéponsesPossibles est une liste de réponses avec leurs coefficients séparées par des points-virgules dans ce schéma :
        //Réponse1;Coefficient1;Réponse2;Coefficient2;Réponse3,Coefficient3 ...
        //
        //La première question (ID=1) est une question test pour vérifier que tout fonctionne bien. Cette question est utilisée dans les fonctions de tests

        //La structure d'un type Question est la suivante :
        // int id;
        // int difficulte;
        // String question;
        // String[][] reponses;
        // String indice;
        // int nbRencontree;
        // int nbReussie;
        // int nbSkip;
        // int nbRatee;

    Question[] initToutesQuestions(String nomJoueur) { 
        //Initialise toutes les questions du jeu dans une liste de type Question
        CSVFile fichier = loadCSV(CHEMIN_QUESTIONS);
        int nbQuestions = rowCount(fichier);
        Question[] questions = new Question[nbQuestions-1];

        for (int i=1; i<nbQuestions; i++) {
            questions[i-1] = creerQuestion(i, nomJoueur);
        }

        return questions;
    }

    Question creerQuestion(int id, String nomJoueur) {
        //Crée une question à partir de l'ID de la question
        //Utilise les statistiques du joueur pour initialiser les stats de la question
        CSVFile fichierQuestions = loadCSV(CHEMIN_QUESTIONS);

        String question = getCell(fichierQuestions, id, 2);
        int difficulte = stringToInt(getCell(fichierQuestions, id, 1));
        String[][] reponses = getReponses(id);
        String indice = getCell(fichierQuestions, id, 4);

        int nbRencontree = 0;
        int nbReussie = 0;
        int nbSkip = 0;
        int nbRatee = 0;

        //Recupérer les stats de la question
        if (!equals(nomJoueur, "nouveau")) {
            CSVFile fichierJoueur = loadCSV(CHEMIN_SAUVEGARDES+"/"+nomJoueur+".csv");
            nbRencontree = stringToInt(getCell(fichierJoueur, id, 1));
            nbReussie = stringToInt(getCell(fichierJoueur, id, 2));
            nbSkip = stringToInt(getCell(fichierJoueur, id, 3));
            nbRatee = stringToInt(getCell(fichierJoueur, id, 4));
        }

        return newQuestion(id, difficulte, question, reponses, indice, nbRencontree, nbReussie, nbSkip, nbRatee);
    }

    Question newQuestion(int id, int difficulte, String question, String[][] reponses, String indice, int nbRencontree, int nbReussie, int nbSkip, int nbRatee) {
        Question q = new Question();
        q.id = id;
        q.difficulte = difficulte;
        q.question = question;
        q.reponses = reponses;
        q.indice = indice;
        q.nbRencontree = nbRencontree;
        q.nbReussie = nbReussie;
        q.nbSkip = nbSkip;
        q.nbRatee = nbRatee;
        return q;
    }

    String[][] getReponses(int id) {
        //Retourne un tableau contenant les réponses possibles à la question correspondant à l'ID avec leurs coefficients

        CSVFile fichier = loadCSV(CHEMIN_QUESTIONS);
        String[] listeReponses = split(getCell(fichier, id, 3), ';');
        //Ici, on a une liste dans ce format : {Réponse1,Coefficient1,Réponse2,Coefficient2,Réponse3,Coefficient3 ...}
        //On veut la transformer en un tableau de tableaux de deux éléments : {Réponse1,Coefficient1},{Réponse2,Coefficient2},{Réponse3,Coefficient3 ...} :
        String[][] reponsesFinales = new String[length(listeReponses)/2][2];
        for (int i=0; i<length(listeReponses)/2; i++) {
            reponsesFinales[i][0] = listeReponses[2*i];
            reponsesFinales[i][1] = listeReponses[2*i+1];
        }

        return reponsesFinales;
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

    Question questionAleatoire(int[] id_pool) {
        //Retourne l'ID d'une question aléatoire dans la liste des id "id_pool"
        int idQuestion = -1;
        boolean valide = false;
        int i = 0;
        //boucles while imbriquées pour tirer une valeur aléatoire parmis toutes les questions, puis tester si elle est 
        //dans la liste des id "id_pool"
        while(!valide){
            idQuestion = (int)(random()*rowCount(loadCSV(CHEMIN_QUESTIONS))+1);
            while(idQuestion != id_pool[i] && i < length(id_pool)-1){
                i++; //On vérifie si l'id de la question qu'on vient de tirer est dans la liste des id "id_pool"
            }
            if(idQuestion==id_pool[i]){
                valide = true;
            }
            i = 0;
        }
        return joueur.listeQuestions[idQuestion];
    }

    int[] tousLesId(){
        int[] result = new int[rowCount(loadCSV(CHEMIN_QUESTIONS))-1];
        for(int i = 1 ; i < length(result); i++){
            result[i] = i;
        }
        return result;
        // Pour tester que "questionAleatoire" choisisse bien dans une liste d'id, commentez la ligne d'au dessus, et décommentez celle d'en dessous.
        // return new int[]{2,3,4};
    }

    int[] tableauPoids() {
        //Utilise les stats du joueur pour retourner un tableau de poids pour chaque question
        //A faire
        return new int[1];
    }

    

    double coeffReponse(Question question, String reponse) {
        //Cette fonction retourne -1 si la réponse est fausse, sinon elle retourne le coefficient de la réponse
        CSVFile fichier = loadCSV(CHEMIN_QUESTIONS);
        double coeff = -1;
        String[][] reponses = question.reponses;
        for (int i=0; i<length(reponses,1); i++) {
            if (equals(reponses[i][0], toLowerCase(reponse))) {
                coeff = stringToDouble(reponses[i][1]);
            }
        }
        return coeff;
    }

    String toString(String[][] tab) {
        String chaine = "";
        for (int i=0; i<length(tab); i++) {
            chaine = chaine + tab[i][0] + " ";
        }
        return chaine;
    }

    String toString(Question question) {
        String chaine = "";
        chaine = chaine + "ID : " + question.id + "\n";
        chaine = chaine + "Difficulté : " + question.difficulte + "\n";
        chaine = chaine + "Question : " + question.question + "\n";
        chaine = chaine + "Réponses : " + toString(question.reponses) + "\n";
        chaine = chaine + "Indice : " + question.indice + "\n";
        chaine = chaine + "Nombre de fois rencontrée : " + question.nbRencontree + "\n";
        chaine = chaine + "Nombre de fois réussie : " + question.nbReussie + "\n";
        chaine = chaine + "Nombre de fois passée : " + question.nbSkip + "\n";
        chaine = chaine + "Nombre de fois ratée : " + question.nbRatee + "\n";
        return chaine;
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
        String[] tabFichiers = getAllFilesFromDirectory(CHEMIN_SAUVEGARDES);
        if (length(tabFichiers)==0) {
            println("Aucune sauvegarde trouvée. Veuillez commencer une nouvelle partie.");
        }
        for (int i=0; i<length(tabFichiers); i++) {
            CSVFile fichier = loadCSV(CHEMIN_SAUVEGARDES+"/"+tabFichiers[i]);
            println(getCell(fichier,0,0));
        }
    }

    Joueur creerJoueur() {
        print("Quel est votre nom ?\n> ");
        String nom = readString();
        clearScreen();

        boolean valide = false;
        String niveauString = "";
        int score = 0;
        while(!valide){
            clearScreen();
            println("Bienvenue, "+nom+", quel niveau pensez-vous avoir en Anglais ?\n1. Mauvais\n2. Moyen\n3. Bon\n4. Très bon");
            print("> ");
            niveauString = readString();
            if(equals(niveauString,"1") || equals(niveauString,"2") || equals(niveauString,"3") || equals(niveauString,"4")){
                valide = true;
            }
        }
        int niveau = stringToInt(niveauString);

        //Il faudrait se mettre d'accord sur combien de points il faut pour chaque niveau
        //Dans ce cas, si le joueur dis qu'il est très bon, il faut lui mettre combien de points ?
        int nbQuestions = rowCount(loadCSV(CHEMIN_QUESTIONS));
        Question[] listeQuestions = initToutesQuestions("nouveau"); //On passe "nouveau" pour dire qu'on crée un nouveau joueur. Sinon, la fonction chercherait des stats pour un joueur dans un fichier qui n'existe pas.
        return newJoueur(nom, score, niveau, 3, listeQuestions);
    }

    Joueur newJoueur(String nom, int score, int niveau, int pointsBonus, Question[] listeQuestions) {
        Joueur j = new Joueur();
        j.nom = nom;
        j.score = score;
        j.niveau = niveau;
        j.pointsBonus = pointsBonus;
        j.listeQuestions = listeQuestions;
        return j;
    }

    Joueur chargerJoueur(String nomFichier) {
        CSVFile fichier = loadCSV(CHEMIN_SAUVEGARDES+"/"+nomFichier);
        String nom = getCell(fichier,0,0);
        int score = stringToInt(getCell(fichier,0,1));
        int pointsBonus = stringToInt(getCell(fichier,0,2));
        int niveau = stringToInt(getCell(fichier,0,3));
        //Création d'un tableau de 5 colonnes (pour l'id et les 4 stats) et d'autant de lignes que de questions
        Question[] listeQuestions = initToutesQuestions(nom);
        // Colonne 0 : id de la question | Colonne 1 : Nombre de fois où elle est tombée | 2 : Nb de fois réussie | 3 : Nb de fois skip | 4 : nbFois Ratée
        return newJoueur(nom,score, niveau, pointsBonus,listeQuestions);
    }


    int scoreToNiveau(int score) {
        //Retourne le niveau correspondant à un nombre de points
        //à remplir
        return 1;
    }

    void saveJoueur(Joueur joueur, String nomFichier) {
        String[][] saveString = new String[length(joueur.listeQuestions)+1][5];
        saveString[0][0] = joueur.nom;
        saveString[0][1] = "" + joueur.score;
        saveString[0][2] = "" + joueur.pointsBonus;

        Question[] questions = joueur.listeQuestions;
        for(int ligne = 0; ligne < length(questions); ligne++){
            saveString[ligne+1][0] = ""+questions[ligne].id;
            saveString[ligne+1][1] = ""+questions[ligne].nbRencontree;
            saveString[ligne+1][2] = ""+questions[ligne].nbReussie;
            saveString[ligne+1][3] = ""+questions[ligne].nbSkip;
            saveString[ligne+1][4] = ""+questions[ligne].nbRatee;
        }
        saveCSV(saveString,"ressources/sauvegardes/"+toLowerCase(nomFichier)); //On passe le nom du fichier en minuscules
    }

    //Squelette questions dans le fichier save :
    //id,nbRencontree,nbReussie,nbSkip,nbRatee

    void afficherStatistiques(){
        println("Vos statistiques :");
        println("Votre pseudo : " + joueur.nom);
        println("Votre score : " + joueur.score);
        // Quand on se sera mis d'accord sur l'xp nécessaire pour monter de niveau, on pourra rajouter un affichage de l'avancement avant le prochain avec les petits carrés ☐ et ■
        println("Votre niveau : " + joueur.niveau);
        println("Votre nombre de points bonus : " + joueur.pointsBonus);
    }

    void afficherStatAvancee(){
        Question[] questions = joueur.listeQuestions;
        println("Voici vos statistiques pour chacune des questions implémentées :");
        for(int i = 0; i < length(questions); i++){
            if (questions[i].nbRencontree!=0) { //On affiche seulement les questions qui ont été rencontrées
                println("\nQuestion numéro " + i + " : ");
                println("   Nombre de fois rencontrée : " + questions[i].nbRencontree);
                println("   Nombre de fois réussie : " + questions[i].nbReussie);
                println("   Nombre de fois que vous l'avez passée : " + questions[i].nbSkip);
                println("   Nombre de fois ratée : " + questions[i].nbRatee);
            }
        }
        println("\nLes questions non affichées n'ont pas encore été rencontrées.");
    }



    /////////////////////////////////////
    // Fonctions pour les points bonus //
    /////////////////////////////////////

    void ajouterPointsBonus(Question question) {
        //Vérifie si le joueur gagne un point bonus et l'ajoute à son score
        //Un joueur peut gagner un point bonus avec une probabilité dépendant de son niveau et de la difficulté de la question
        double proba = probaPoint(question);
        if (random()<proba) {
            joueur.pointsBonus++;
            println("\nVous avez gagné un point bonus !\nIl peut vous servir à passer une question ou à demander un indice.");
        }
    }

    double probaPoint(Question question) {
        //Retourne la probabilité d'obtenir un point bonus
        //La probabilité est calculée en fonction du niveau du joueur ainsi que du niveau de la question
        // Chance pt bonus = (niveau de la question)-(notre niveau) * 0.1 +0.1
        int niveauQuestion = question.difficulte;
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

    String toString(Question[] tab) {
        String chaine = "";
        for (int i=0; i<length(tab); i++) {
            chaine = chaine + tab[i] + "\n";
        }
        return chaine;
    }

    String toString(int[] tab) {
        String chaine = "";
        for (int i=0; i<length(tab); i++) {
            chaine = chaine + tab[i] + " ";
        }
        return chaine;
    }
}