import extensions.File;
import extensions.CSV;

class BoisDesCancres extends Program {
    Joueur joueur;

    void algorithm() {
        println("Bienvenue dans le Bois Des Cancres !");
        println("1. Nouvelle partie");
        println("2. Ouvrir une sauvegarde");
        int choix = readInt();
        if (choix==2) {
            print("Quelle sauvegarde voulez-vous charger ?");
            afficherListeSave();
            joueur=chargerJoueur();
        } else if (choix==1) {
            joueur=creerJoueur();
        }
    }

    void afficherListeSave() {
        print("afficherListeSave à faire");
    }

    Joueur creerJoueur() {
        //à remplier
        return new Joueur();    
    }

    Joueur chargerJoueur() {
        //à remplir
        return new Joueur();
    }

    void afficherImage(File fichier){
        
    }

    void saveJoueur() {
        //à remplir
    }
}