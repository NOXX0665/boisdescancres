import extensions.File;
import extensions.CSVFile;

class BoisDesCancres extends Program {
    final String CHEMIN_QUESTIONS = "ressources/questions.csv";
    final String CHEMIN_SAUVEGARDES = "ressources/sauvegardes/";
    final int POINTS = 10; //Le nombre de points (avant calcul avec coefficients) que le joueur gagne. Baisser pour rendre la question plus compliqué

    void algorithm() {
        Joueur joueur = new Joueur();

        //Menu d'accueil
        //On demande au joueur s'il veut commencer une nouvelle partie ou charger une sauvegarde
        clearScreen();
        text("green");
        afficherImage("ressources/ascii_art/logo.txt");
        text("white");

        println("Bienvenue dans le Bois Des Cancres !");
        println("1. Nouvelle partie");
        println("2. Ouvrir une sauvegarde");
        println("3. Quitter le jeu");
        String choix = demanderValeur(new String[]{"1","2","3"});
            
        if (choix.equals("3")) {
            System.exit(0);
        } else if (choix.equals("2")) {
            joueur = menuChargerSave(joueur);
        } else if (choix.equals("1")) {
            clearScreen();
            joueur=creerJoueur();
        }
        
        println("C'est parti pour le niveau "+joueur.niveau+" !");
        delay(1000);

        choix = "-1";

        clearScreen();
        printlncolor("Vous vous réveillez dans une étrange forêt... Vous voyez des cancres partout autour de vous !\nOn dirait que vous devez répondre à leurs questions pour vous échapper de ce lieu maudit.", "green");

        while (!equals(choix, "4")) { //Si le choix=4, cela veut dire que le joueur veut sauvegarder et quitter.
            //Menu du jeu :
            println("\n\nQue voulez-vous faire ?");
            println("1. Continuer votre chemin");
            println("2. Regarder vos statistiques");
            println("3. Réviser");
            println("4. Sauvegarder et quitter");
            choix = demanderValeur(new String[]{"1","2","3","4"});

            if (equals(choix, "1")) {
                clearScreen();
                //Choix d'une question parmis toutes les questions dans le fichier ../ressources/questions.csv
                Question question = questionAleatoire(questionsAdaptees(joueur), joueur);
                // Question question = questionAleatoire(new int[]{301}, joueur);
                //waypointvenere

                //On pose la question
                poserQuestion(question, joueur);

                //On demande la réponse
                //Une réponse valide est soit une bonne réponse, soit un indice, soit un passage de question (dans les deux derniers cas, c'est true seulement si le joueur a assez de points bonus)
                boolean reponseValide = false;
                while (!reponseValide) {
                    reponseValide = demanderReponse(question, joueur);
                }
                joueur.niveau = scoreIntoNiveau(joueur.score);
                
            } else if (equals(choix, "2")) {
                menuStats(joueur);
            } else if (equals(choix, "3")) {
                if(arrayEquals(questionRevisions(joueur), aucunId())){
                    clearScreen();
                    printlncolor("Impossible de réviser : vous connaissez déjà tout !","red");
                } else {
                    clearScreen();
                    Question question = questionAleatoire(questionRevisions(joueur), joueur);
                    poserQuestion(question, joueur);
                    boolean reponseValide = false;
                    while (!reponseValide) {
                        reponseValide = demanderReponse(question, joueur);
                    }
                    joueur.niveau = scoreIntoNiveau(joueur.score);
                }
            }   
        }

        //Sauvegarde et quitter
        printlncolor("Sauvegarde en cours...\nNe fermez pas le jeu !", "yellow");
        saveJoueur(joueur, joueur.nom+".csv");
        clearScreen();
        printlncolor("Partie sauvegardée. Au revoir !","green");
    }


    /////////////////////////////////////////////
    // Fonctions pour les menus et l'interface //
    /////////////////////////////////////////////


    Joueur menuChargerSave(Joueur joueur) {
        // Cette fonction demande quelle sauvegarde charger et charge le joueur correspondant.
        // Elle retourne le joueur avec les questions chargées.
        clearScreen();
        println("Quelle sauvegarde voulez-vous charger ?");
        afficherListeSave();
        String choixSave = toLowerCase(demanderValeur(listeSave()));
        joueur=chargerJoueur(choixSave+".csv");
        return joueur;
    }

    void menuStats(Joueur joueur) {
        clearScreen();
        //Affichage des statistiques du joueur
        afficherStatistiques(joueur);
        println("\n"); //Pour aérer un peu

        //On demande s'il veut voir ses stats avancées
        println("Que souhaitez-vous faire ?");
        println("1. Voir des statistique plus précises sur les questions");
        println("2. Revenir au jeu");
        String choix = demanderValeur(new String[]{"1","2"});
        clearScreen();
        if (equals(choix, "1")) {
            afficherStatAvancee(joueur);
            println("\nAppuyez sur Entrée pour revenir au jeu.");
            readString();
        }
    }

    boolean poserQuestion(Question question, Joueur joueur) {
        //Pose une question au joueur en vérifiant s'il a assez de points bonus pour passer
        
        printlncolor(question.question,"green");

        if (joueur.pointsBonus>0) {
            println("\nIl vous reste "+joueur.pointsBonus+" points bonus.\nVous pouvez passer la question en tapant 'passer' ou demander un indice en tapant 'indice'.\n");
        }

        return true;
    }

    boolean demanderReponse(Question question, Joueur joueur) {
        //Demande une réponse au joueur, vérifie si elle est valide et retourne vrai si la réponse est valide (soit bonne réponse, soit passer, soit indice), faux sinon.
        print("Votre réponse > ");
        double tempsDebut=getTime();
        String reponse = readString(); //On ne peut pas utiliser demanderValeur()
        double tempsFin=getTime();
        double temps=tempsFin-tempsDebut;
        question.nbRencontree++;

        double coefficient = coeffReponse(question, reponse);
        int points = calculerPoints(question.difficulte, joueur.niveau, temps, coefficient);
        

        if (coeffReponse(question, reponse)!=-1) { //Si c'est une bonne réponse
            // clearScreen(); //waypomodif
            printlncolor("Bonne réponse !\n\nVous avez gagné "+points+" points.","green");
            
            // println("\nVous avez répondu en "+temps/1000+" secondes.");
            ajouterPointsBonus(question, joueur);

            joueur.score+=points;

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
            //clearScreen(); //waypoint
            printlncolor("Mauvaise réponse...","red");
            printlncolor("La bonne réponse était : "+question.reponses[0][0],"red");
            printlncolor("\nVous avez perdu "+abs(points)+" points :(","red");
            println("Votre réponse : "+reponse);
            joueur.score+=points;
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
        nomJoueur=toLowerCase(nomJoueur);
        if (!equals(nomJoueur, "nouveau")) {
            CSVFile fichierJoueur = loadCSV(CHEMIN_SAUVEGARDES+"/"+nomJoueur+".csv");
            nbRencontree = stringToInt(getCell(fichierJoueur, id, 1));
            nbReussie = stringToInt(getCell(fichierJoueur, id, 2));
            nbSkip = stringToInt(getCell(fichierJoueur, id, 3));
            nbRatee = stringToInt(getCell(fichierJoueur, id, 4));
        }

        Question q = newQuestion(id, difficulte, question, reponses, indice, nbRencontree, nbReussie, nbSkip, nbRatee);

        return q;
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

    void testGetReponses(){
        String[][] reponses = new String[][]{{"was","1"},{"were","1"}};
        assertTrue(arrayEquals(reponses,getReponses(15)));
        assertFalse(arrayEquals(reponses,getReponses(16)));
        reponses[0][1]="2";
        assertFalse(arrayEquals(reponses,getReponses(15)));
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

    Question questionAleatoire(int[] id_pool, Joueur joueur) {
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

    int[] aucunId(){
        return new int[rowCount(loadCSV(CHEMIN_QUESTIONS))-1];
    }

    int[] tableauPoids() {
        //Utilise les stats du joueur pour retourner un tableau de poids pour chaque question
        //A faire
        return new int[1];
    }


    int[] questionsAdaptees(Joueur joueur){
        CSVFile fichier = loadCSV(CHEMIN_QUESTIONS);
        int[] result = new int[rowCount(fichier)-1];
        for(int i = 1 ; i < length(result); i++){
            if((stringToInt(getCell(fichier,i,1)) < joueur.niveau+2) && (stringToInt(getCell(fichier,i,1)) > joueur.niveau-2)){
                result[i] = i;
            }
        }
        return result;
    }

    int[] questionRevisions(Joueur joueur){
        //Renvoie la liste des questions qui on été ratée ou passée + de fois qu'elle n'ont été réussies
        CSVFile fichier = loadCSV(CHEMIN_QUESTIONS);
        int[] result = new int[rowCount(fichier)-1];
        for(int i = 1 ; i < length(result); i++){
            if(joueur.listeQuestions[i].nbReussie < (joueur.listeQuestions[i].nbRatee + joueur.listeQuestions[i].nbSkip)){
                result[i] = i;
            }
        }
        return result;
    }

    double coeffReponse(Question question, String reponse) {
        //Cette fonction retourne -1 si la réponse n'est pas dans la liste des réponses des questions, sinon elle retourne le coefficient de la réponse
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

    void testCoeffReponse(){
        Question ques = creerQuestion(20,"nouveau");
        assertEquals(coeffReponse(ques,"good afternoon"),1.0);
        assertEquals(coeffReponse(ques,"hello"),0.5);
        assertEquals(coeffReponse(ques,"cat"),-1.0);
        ques = creerQuestion(30,"nouveau");
        println(ques.reponses[0][0]);
        println("I don't like vegetables");
        assertEquals(coeffReponse(ques,"I don't like vegetables"),1.0);
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
        String[] listeSave = listeSave();
        for(int i = 0 ; i < length(listeSave) ; i++){
            println(listeSave[i]);
        }

        if (length(listeSave)==0) {
            printlncolor("Aucune sauvegarde trouvé", "red");
        }

        // String[] tabFichiers = getAllFilesFromDirectory(CHEMIN_SAUVEGARDES);
        // if (length(tabFichiers)==0) {
        //     println("Aucune sauvegarde trouvée. Veuillez commencer une nouvelle partie.");
        // }
        // for (int i=0; i<length(tabFichiers); i++) {
        //     CSVFile fichier = loadCSV(CHEMIN_SAUVEGARDES+"/"+tabFichiers[i]);
        //     println(getCell(fichier,0,0));
        // }
    }

    String[] listeSave(){
        String[] tabFichiers = getAllFilesFromDirectory(CHEMIN_SAUVEGARDES);
        String[] listeSave = new String[length(tabFichiers)];
        for(int i = 0; i<length(listeSave);i++){
            listeSave[i] = getCell(loadCSV(CHEMIN_SAUVEGARDES+"/"+tabFichiers[i]),0,0);
        }
        return listeSave;
    }

    Joueur creerJoueur() {
        print("Quel est votre nom ?\n> ");
        String nom = readString();
        clearScreen();

        boolean valide = false;
        String niveauString = "";

        clearScreen();
        println("Bienvenue, "+nom+", quel niveau pensez-vous avoir en Anglais ?\n1. Mauvais\n2. Moyen\n3. Bon\n4. Très bon");
        niveauString = demanderValeur(new String[]{"1","2","3","4"});
        if(equals(niveauString,"1") || equals(niveauString,"2") || equals(niveauString,"3") || equals(niveauString,"4")){
            valide = true;
        }
        int niveau = stringToInt(niveauString);
        int score = niveau*100;

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
        int niveau = scoreIntoNiveau(score);
        //Création d'un tableau de 5 colonnes (pour l'id et les 4 stats) et d'autant de lignes que de questions
        Question[] listeQuestions = initToutesQuestions(nom);
        // Colonnes : 0 : id de la question | 1 : Nombre de fois où elle est tombée | 2 : Nb de fois réussie | 3 : Nb de fois skip | 4 : nbFois Ratée

        Joueur joueur = newJoueur(nom, score, niveau, pointsBonus, listeQuestions);

        return joueur;
    }



    void saveJoueur(Joueur joueur, String nomFichier) {
        String[][] saveString = new String[length(joueur.listeQuestions)+1][5];
        saveString[0][0] = joueur.nom;
        saveString[0][1] = "" + joueur.score;
        saveString[0][2] = "" + joueur.pointsBonus;
        saveString[0][3] = "" + joueur.niveau;

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

    void afficherStatistiques(Joueur joueur){
        // Cette fonction affiche les statistiques du joueur
        println("Vos statistiques :");
        println("Votre pseudo : " + joueur.nom);
        println("Votre score : " + joueur.score);
        println("Avancement avant le niveau " + (joueur.niveau+1) + " : " + scoreToString(joueur.score));
        // Quand on se sera mis d'accord sur l'xp nécessaire pour monter de niveau, on pourra rajouter un affichage de l'avancement avant le prochain avec les petits carrés ☐ et ■
        println("Votre niveau : " + joueur.niveau);
        println("Votre nombre de points bonus : " + joueur.pointsBonus);
    }

//waypoint
    String scoreToString(int score) {
        String chaine = "";

        for (int i=0;i<score%100;i++) {
            chaine+="■";
        } 

        for (int i=0; i<100-length(chaine);i++) {
            chaine+="☐";
        }

        return chaine;
    }

    void afficherStatAvancee(Joueur joueur){
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
        println("\nCes statistiques sont celles des questions que vous avez rencontrées au moins une fois.\nSi vous n'avez pas encore rencontré une question, elle n'aparaîtra pas dans ces statistiques.");
    }

    int scoreIntoNiveau(int score){
        return (score+1)/100;
    }

    void testScoreIntoNiveau() {
        println("Il faudrait faire un test pour cette fonction on sait jamais");
        println("Mais j'ai pas trop compris pourquoi on ajoute 1 aux points avant de diviser par 100 et aussi après avoir divisé par 100");
    }

    /////////////////////////
    // Fonctions de calcul //
    /////////////////////////


    int calculerPoints(int niveauQuestion, int niveauJoueur, double temps, double coefficient){
        int points = POINTS;
        int differenceNiveau = niveauQuestion-niveauJoueur;
        println("difference : " + differenceNiveau);
        //utilisation du coefficient de la réponse (1 si elle est bonne, 0.5 si elle est moins bien, -1 si elle est fausse)
        points = (int)(points*coefficient);
        println("Nombre de points après passage du coefficient : " + points);
        if(points > 0){
            //calcul des points en cas de réussite
            if (differenceNiveau == 1){
                points = (int)(points *1.5);
                println("La question est 1 niveau plus haut donc on fait x1.5");
            }
            else if(differenceNiveau == 2){
                points = (int)(points*2);
                println("La question est 2 niveau plus haut donc on fait x2");
            }
            else if(differenceNiveau == -1){
                points = (int)(points*0.5);
                println("La question est 1 niveau plus bas donc on fait x0.5");
            }
            else if(differenceNiveau == -2){
                points = (int)(points*0.25);
                println("La question est 2 niveau plus bas donc on fait x0.25");
            }
            //multiplicateur de temps : Si on mets moins de 10 secondes, on gagne un multiplicateur de points qui équivaut à +1% par seconde restantes (exemple : on répond bien en 1 seconde, on a un bonus de +9%)
            if(temps <10000){
                points =(int)(points * (1.5 - (temps/20000)));
                println("Tu as pris " + temps/1000 + " secondes donc on fait x" + (1.5 - (temps/20000)));
            }
        }
        else{
            if (differenceNiveau == 1){
                points = (int)(points *0.25);
                println("La question est 1 niveau plus haut donc on fait x0.25");
            }
            else if(differenceNiveau == 2){
                points = (int)(points*0.125);
                println("La question est 2 niveau plus haut donc on fait x0.125");
            }
            else if(differenceNiveau == -1){
                points = (int)(points*0.75);
                println("La question est 1 niveau plus bas donc on fait x0.75");
            }
        }
        // print("Points gagnés : " + points);
        return points;
    }

    void testCalculerPoints(){
        assertEquals(calculerPoints(2,2,50000.0,1),10);
        assertEquals(calculerPoints(2,3,50000,1),5);
    }
    //waypoint

    /////////////////////////////////////
    // Fonctions pour les points bonus //
    /////////////////////////////////////

    void ajouterPointsBonus(Question question, Joueur joueur) {
        //Vérifie si le joueur gagne un point bonus et l'ajoute à son score
        //Un joueur peut gagner un point bonus avec une probabilité dépendant de son niveau et de la difficulté de la question
        double proba = probaPoint(question, joueur);
        if (random()<proba) {
            joueur.pointsBonus++;
            println("\nVous avez gagné un point bonus !\nIl peut vous servir à passer une question ou à demander un indice.");
        }
    }

    double probaPoint(Question question, Joueur joueur) {
        //Retourne la probabilité d'obtenir un point bonus
        //La probabilité est calculée en fonction du niveau du joueur ainsi que du niveau de la question
        // Chance pt bonus = (niveau de la question)-(notre niveau) * 0.1 +0.1
        int niveauQuestion = question.difficulte;
        int niveauJoueur = joueur.score;
        return (niveauQuestion-niveauJoueur)*0.1+0.1;
    }


    /////////////////////////////////////

    int demanderValeur(int[] valeursPossibles) {
        //Demande une valeur à l'utilisateur et vérifie si elle est dans le tableau de valeurs possibles
        //On ne peux pas faire de tests sur cette fonction car elle demande une valeur à l'utilisateur

        boolean valide = false;
        String valeur = "";
        while (!valide) {
            print("\nVotre choix > ");
            valeur = readString();
            for (int i=0; i<length(valeursPossibles); i++) {
                valide = valide || equals(valeur, ""+valeursPossibles[i]);
            }
            if (!valide) {
                println("\n\nValeur invalide. Veuillez réessayer.");
            }
        }
        return stringToInt(valeur);
    }

    String demanderValeur(String[] valeursPossibles) {
        //Demande une valeur à l'utilisateur et vérifie si elle est dans le tableau de valeurs possibles
        //On ne peux pas faire de tests sur cette fonction car elle demande une valeur à l'utilisateur

        String valeur = "";
        boolean valide = false;
        while (!valide) {
            print("\nVotre choix > ");
            valeur = readString();
            for (int i=0; i<length(valeursPossibles); i++) {
                valide = valide || equals(valeur, valeursPossibles[i]);
            }
            if (!valide) {
                println("Valeur invalide. Veuillez réessayer.");
            }
        }
        return valeur;
    }

    void afficherImage(String chemin){
        //Affiche une image en ASCII art et supprime le contenu de la console.
        //Le chemin doit pointer vers un fichier texte contenant l'image en ASCII art.
        File fichier = new File(chemin);
        while (ready(fichier)){
            println(fichier.readLine());
        }
    }

    boolean arrayEquals(int[] tab1, int[] tab2){
        boolean result = true;
        if(length(tab1) !=length(tab2)){
            result = false;
        }
        else{
            for(int i = 0; i < length(tab1); i++){
                if(tab1[i] != tab2[i]){
                    result = false;
                }
            }
        }
        return result;
    }

    boolean arrayEquals(String[][] tab1,String[][] tab2){
        if((length(tab1,1) != length(tab2,1)) || (length(tab1,2) != length(tab2,2))){
            return false;
        } else {
            for(int i = 0 ; i < length(tab1,1);i++){
                for(int j = 0 ; j < length(tab1,2) ; j++){
                    if(!equals(tab1[i][j],tab2[i][j])){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    void testArrayEqualsDoubleString() {
        String[][] tabVide = new String[][]{{}};
        String[][] tab1 = new String[][]{{"bonjour","bonjour"},{"hello","test"}};
        String[][] tab2 = new String[][]{{"bonjour","bonjour"},{"hello","test"}};
        String[][] tab3 = new String[][]{{"hello","hjhfd"}};

        assertFalse(arrayEquals(tabVide,tab1)); // Tableau pas vide contre tableau vide
        assertTrue(arrayEquals(tab1,tab2)); //
        assertFalse(arrayEquals(tab1,tab3));

    }

    void testArrayEquals() {
        assertTrue(arrayEquals(new int[]{1,2,3}, new int[]{1,2,3}));
        assertFalse(arrayEquals(new int[]{1,2,3}, new int[]{1,2,4}));
        assertFalse(arrayEquals(new int[]{1,2,3}, new int[]{1,2,3,4}));
        assertTrue(arrayEquals(new int[]{}, new int[]{}));
        assertFalse(arrayEquals(new int[]{1}, new int[]{}));
    }

    void printlncolor(String text, String color) {
        // Cette fonction print une seule ligne dans la couleur color
        text(color);
        println(text);
        text("white");
    }
}