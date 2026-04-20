# Questions sur le "Figures Editor"

	Nom :
	Prénom :
	Groupe :

## Objectif
L'objectif de ce petit questionnaire est de vous faire étudier le code du projet "Figures Editor" afin que vous puissiez mieux comprendre son fonctionnement.

## Questions

* _Pro Tip_ : Pour rechercher les différentes utilisations d'un symbole (attribut, méthode, etc.)
	* Dans VSCode : sur un symbole, Clic droit --> Find All References.
	* Dans Eclipse : sur un symbole, Clic droit --> References --> Project.

* A quoi sert l'attribut `currentTool` de la classe Controller ? Quelles peuvent être ses valeurs ?
	* Réponse :

* Pourquoi toutes les figures sont elles clonables (On doit pouvoir créer des copies distinctes mais identiques de chaque figure) ?
	* Réponse :

* A quoi sert la classe `ColorFactory` que vous pouvez utiliser pour obtenir des instances de la classe javafx.scene.paint.Color ?
	* Réponse :

* Quelles sont les classe filles de la classe `FocusedFigureTool` et à quoi servent elles ?
	* Réponse :

## Pattern hunt
Recherchez dans le code du projet les différents Design patterns utilisés et listez les ci-dessous.

> Lorsque vous pensez avoir identifié un Pattern précisez les éléments qui vous indiquent la présence de ce pattern et à quoi ou pourquoi il est utilisé. En d'autres termes, ne vous contentez pas de nommer un pattern sans préciser l'endroit où il est utilisé et pour quoi faire.

### Liste des patterns utilisés dans le Figure Editor

* __Iterator__ : Inutile de le rechercher, il est partout dès que l'on a une collection comme dans `List<Figure> figures` dans la classe `figures.Drawing` par exemple.
* ...
