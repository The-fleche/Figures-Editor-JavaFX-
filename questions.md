# Questions sur le "Figures Editor"

	Nom : EL KHAZROUNI
	Prénom :Hicham
	Groupe : 3

## Objectif
L'objectif de ce petit questionnaire est de vous faire étudier le code du projet "Figures Editor" afin que vous puissiez mieux comprendre son fonctionnement.

## Questions

* _Pro Tip_ : Pour rechercher les différentes utilisations d'un symbole (attribut, méthode, etc.)
	* Dans VSCode : sur un symbole, Clic droit --> Find All References.
	* Dans Eclipse : sur un symbole, Clic droit --> References --> Project.

* A quoi sert l'attribut `currentTool` de la classe Controller ? Quelles peuvent être ses valeurs ?
	* Réponse : Il contient les informations sur l'outil courrant, il peut prendre les valeurs d'outil de modification et de création de formes

* Pourquoi toutes les figures sont elles clonables (On doit pouvoir créer des copies distinctes mais identiques de chaque figure) ?
	* Réponse : On aimerait pouvoir duppliquer les figures sans qu'une modification sur l'une des copies n'engendre une modification sur les autres. Donc on veut avoir des copies qui aient les même caractéristiques mais qui soient distinctes. 

* A quoi sert la classe `ColorFactory` que vous pouvez utiliser pour obtenir des instances de la classe javafx.scene.paint.Color ?
	* Réponse : elle permet d'avoir les couleurs sRGB utilisables dans l'interface.

* Quelles sont les classe filles de la classe `FocusedFigureTool` et à quoi servent elles ?
	* Réponse : TransformTool est l'unique classe fille que FocusedFigureTool et permet de déplacer, tourner et modifier la taille d'une figure.  
	Elle a besoin des informations de FocusedFigureTool qui permet de savoir sur quelle figure est le curseur.

## Pattern hunt
Recherchez dans le code du projet les différents Design patterns utilisés et listez les ci-dessous.

> Lorsque vous pensez avoir identifié un Pattern précisez les éléments qui vous indiquent la présence de ce pattern et à quoi ou pourquoi il est utilisé. En d'autres termes, ne vous contentez pas de nommer un pattern sans préciser l'endroit où il est utilisé et pour quoi faire.

### Liste des patterns utilisés dans le Figure Editor

* __Iterator__ : Inutile de le rechercher, il est partout dès que l'on a une collection comme dans `List<Figure> figures` dans la classe `figures.Drawing` par exemple.
* ...

* __Prototype__ : Présent dans Prototype.java et utilisé dans figure/mememto/originator, permet de créer rapidement des nouveaux objets en clonant le prototype via la méthode clone(). 

* __Singleton__ : Permet de différencier les figures entres elles lorsqu'elle sont de même classe ( getInstance() )

* __Factory Method__ : Présent absolument partout, pour CellFactory, ColorFactory, LoggerFactory, IconFactory 

* __Flyweight__ :  Présent dans FlyweightFactory.java entre autre, utilisé pour les couleurs et images/icones 

* __Chain of Responsibility__ : Utilisé pour la gestion des logs, présent dans LoggerFactory/LogHandler

* __Memento__ : Présent dans Drawing.java, utile pour la fonction de retour en arrière 

* __State__ : Pour pouvoir avoir des outils avec plusieurs états possible 

* __Observer__ : pour les boutons évidement mais c'est importer depuis javafx

