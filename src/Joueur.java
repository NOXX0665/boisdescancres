class Joueur {
    String nom;
    int score;
    int niveau; //Le niveau que le joueur a déclaré être le sien.
    // Pour l'instant, le niveau reste celui que le joueur à déclaré. Plus tard, on calculera le niveau du joueur en fonction de son score.


    int pointsBonus;
    Question[] listeQuestions; //Cette liste est la liste de toutes les questions.
    //On met la liste des questions dans le joueur car les questions ont leur propres statistiques sur le joueur.
}