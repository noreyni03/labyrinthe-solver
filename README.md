# Labyrinthe Solver

Ce projet est une application JavaFX qui permet de visualiser, générer et résoudre des labyrinthes. Il offre la possibilité de charger des labyrinthes à partir de fichiers texte, de générer des labyrinthes de manière aléatoire grâce à plusieurs algorithmes et de les résoudre en utilisant différents algorithmes de recherche de chemin (BFS, DFS, A*).

## Fonctionnalités

*   **Chargement de labyrinthes** : Possibilité de charger des labyrinthes à partir de fichiers texte.
*   **Génération de labyrinthes** : Génération aléatoire de labyrinthes de différentes tailles et de complexité en utilisant les algorithmes suivants :
    *   Récursion Backtracking (Recursive Backtracking)
    *   Algorithme de Prim
    *   Algorithme de Kruskal
    *   Génération par salles aléatoires (Random Rooms)
*   **Résolution de labyrinthes** : Résolution automatique de labyrinthes en utilisant les algorithmes suivants :
    *   Parcours en Largeur (BFS)
    *   Parcours en Profondeur (DFS)
    *   A* (A-Star)
*   **Comparaison des Algorithmes** : Une vue dédiée permet de comparer les performances des algorithmes de résolution (BFS, DFS, A*) en termes de longueur du chemin, du nombre d'étapes et du temps d'exécution.
* **Animation**: Résolution avec animation pour l'algorithme BFS.
*   **Interface graphique** : Utilisation de JavaFX pour une interface utilisateur intuitive.
* **Analyse**: l'application permet d'analyser le labyrinthe pour avoir des statistiques sur la taille, le nombre de murs , de chemins, le rapport des murs ,la longeur en ligne droite entre le point de départ et d'arrivé et des statistiques sur les algorithmes de résolution de labyrinthe.

## Prérequis

*   **Java Development Kit (JDK)** : Version 11 ou supérieure. Vous pouvez le télécharger sur le site officiel d'Oracle ou via un gestionnaire de paquets (par exemple, `apt-get install openjdk-17-jdk` sur Debian/Ubuntu).
*   **Maven** : Pour la gestion des dépendances et la construction du projet. Vous pouvez le télécharger sur le site officiel d'Apache Maven.
*   **IDE (Optionnel)** : Bien que non obligatoire, un environnement de développement intégré (IDE) comme IntelliJ IDEA ou Eclipse peut faciliter le développement et le débogage.

## Installation et Exécution

Voici les étapes pour exécuter le projet :

1.  **Clonage du dépôt** :
    *   Ouvrez votre terminal ou invite de commandes.
    *   Accédez au répertoire où vous souhaitez cloner le projet.
    *   Exécutez la commande :

    ```bash
    git clone https://github.com/noreyni03/labyrinthe-solver.git
    ```

2.  **Accéder au répertoire du projet** :

    ```bash
    cd labyrinthe-solver
    ```

3.  **Compilation du projet** :
    *   Utilisez Maven pour compiler le projet.
    *   Exécutez la commande :

    ```bash
    mvn clean compile
    ```

4.  **Exécution du projet** :
    *   Pour lancer l'application, utilisez la commande Maven :

    ```bash
    mvn javafx:run
    ```
    *   Ou bien, si vous préférez lancer l'application manuellement, vous pouvez exécuter la classe `LabyrintheApp`  qui se trouve dans `src/main/java/fr/esisar/labyrinthe/ui/` à partir de votre IDE.

5. **Utilisation** :
    * Une fois l'application lancée, vous aurez la possibilité de :
       * **Charger un fichier** : Cliquez sur "Charger" pour charger un fichier de labyrinthe.
       * **Générer un labyrinthe** : Cliquez sur "Générer" pour générer aléatoirement un nouveau labyrinthe en utilisant l'algorithme "Recursive Backtracking".
       * **Résoudre un labyrinthe** : Sélectionnez un algorithme dans la liste déroulante ("BFS", "DFS", "A*"), cochez ou non la case "Animation" et cliquez sur "Résoudre".
       * **Analyser un labyrinthe**: cliquez sur le button "Analyse".
       * **Visualiser le labyrinthe résolu** : Le chemin trouvé par l'algorithme sera affiché en jaune sur le canvas.
       * **Voir la comparaison des performances des algorithmes**: en cliquant sur le bouton "comparer".
## Structure du projet

Le projet est structuré de la manière suivante :

*   `src/main/java/fr/esisar/labyrinthe` : Contient le code source de l'application.
    *   `algorithm` : Classes implémentant les algorithmes de résolution (BFS, DFS, A*).
    *   `controller` : Classes contrôlant l'interface utilisateur (MazeController).
    *   `generator` : Classes générant les labyrinthes (MazeGenerator).
    *   `model` : Classes représentant les données (Maze, Point, MazeAnalyzer).
    * `ui`: Classes de l'application qui permettent de lancer l'application et faire la comparaison des algortihmes.
*   `src/main/resources/fr/esisar/labyrinthe/view` : Contient les fichiers FXML pour l'interface utilisateur.
    * `MazeView.fxml`: le fichier fxml principale.
    * `AlgorithmComparisonView.fxml`: le fichier pour la vue de comparaison des algorithmes.
*   `src/test/java/fr/esisar/labyrinthe`: contient les classes de test de l'application.
*   `pom.xml` : Le fichier de configuration de Maven.
