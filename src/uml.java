import extensions.File;
import extensions.CSVFile;

class BoisDesCancres2 extends Program {
    Joueur joueur;

    void algorithm() {
        println("Bienvenue dans le Bois Des Cancres !");
        println("1. Nouvelle partie");
        println("2. Ouvrir une sauvegarde");
        int choix = readInt();
        if (choix==2) {
            print("Quelle sauvegarde voulez-vous charger ?");
            afficherListeSave(); //à faire
            joueur=chargerJoueur(); //à faire
        } else if (choix==1) {
            joueur=creerJoueur(); //en train de faire par Edi
        }

        if (joueur.score==1) {
            println("C'est parti pour le niveau Facile !");
        } else if (joueur.score==2) {
            println("C'est parti pour le niveau Moyen !");
        } else if (joueur.score==3) {
            println("C'est parti pour le niveau Difficile !");
        } else if (joueur.score==4) {
            println("C'est parti pour le niveau Très Difficile !");
        }

        //à remplir

    }

    void afficherListeSave() {
        print("afficherListeSave à faire");
    }

    Joueur creerJoueur() {
        print("Quel est votre nom ?\n> ");
        String nom = readString();
        println("Bienvenue, "+nom+", quel niveau pensez-vou avoir en Anglais ?");
        println("1. Mauvais");
        println("2. Moyen");
        println("3. Bon");
        println("4. Très bon");
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

    Joueur chargerJoueur() {
        //à remplir
        return new Joueur();
    }

    void afficherImage(File fichier){
        print("afficherImage à faire");
    }

    void saveJoueur() {
        //à remplir
    }
}