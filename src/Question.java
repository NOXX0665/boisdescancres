class Question {
    int id;
    int difficulte;
    String question;
    String[][] reponses;
    // Exemple Reponses = {{"Hello",1},{"Hallo",0.5},{"ello",0.5}}
    // Le premier élément de chaque sous-tableau est la réponse, le deuxième est le multiplicateur de points que cette réponse vaut
    String indice;

    // Statistiques du joueur sur cette question
    int nbRencontree;
    int nbReussie;
    int nbSkip;
    int nbRatee;
}