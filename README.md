1 Utilisation de la mémoire partagée

Le but de cet exercice est de coder une petite application Client/Serveur utilisant deux concepts important en informatique :
— les verrous sur des portions de fichiers
— des zones de mémoire associées à des fichiers (mapped-file memory) 

Les spécifications des programmes sont comme suit :

1.1 Seveur

Le serveur présente à l’utilisateur un petit menu (en console) permettant d’effectuer l’une des 4 actions suivantes :
— initialisation d’un fichier de N × 4 octets, initialisé par des octets nuls, permettant de contenir N entiers Java.
— commencement du monitoring d’un fichier. 

Toutes les 5 secondes, le serveur affiche dans la console la valeur contenu pour chaque entier du fichier.

— terminaison du monitoring (on arrête d’afficher le contenu du fichier dans la console)
— quitter : on quitte le programme, le serveur s’arrête.

Le nombre N d’entiers que l’on peut mettre dans le fichier ainsi que le chemin du fichier sont des constantes 
(par exemple 4 et "/tmp/tp01.map").

1.2 Client

Le client prend en argument sur la ligne de commande un entier i entre 0 et N − 1 (l’entier est ramené à ces bornes s’il dépasse). 

Le client effectue M fois de suite les opérations suivantes :

— lire l’entier n qui se trouve à la position i du fichier
— écrire n+1 à la positionidu fichier

On souhaite implémenter les clients de manière à ce qu’ils puissent compter indépendamment même si deux clients sont associés à la même case (dans ce cas, la case contiendra 2 × M en fin de calcul).
