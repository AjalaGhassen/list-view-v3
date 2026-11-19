# Compte Rendu — TP Android Liste de Contacts (Version 3)

**Étudiant :** Ghassen Ajala  
**Dépôt GitHub V3 :** [https://github.com/AjalaGhassen/list-view-v3.git](https://github.com/AjalaGhassen/list-view-v3.git)  
**Technologies :** Kotlin, Single-Activity Architecture, AndroidX Fragments, RecyclerView, Shared ViewModel, Leaflet / OpenStreetMap / Google Maps integration.

---

## 1. Description du projet (Version 3)
La **Version 3** fait évoluer l'application de gestion de contacts en adoptant une **architecture moderne à Activité Unique (Single-Activity Architecture)** hébergeant plusieurs **Fragments dynamiques** (`FragmentContainerView`), une **RecyclerView filtrable en temps réel** et une **carte cartographique interactive** affichant la géolocalisation GPS des contacts.

---

## 2. Architecture & Composants

### A. Activité Principale (`MainActivity`)
- Héberge un unique `<androidx.fragment.app.FragmentContainerView android:id="@+id/fragment_container" />`.
- Gère la navigation initiale vers `ListeContactsFragment`.

### B. Shared ViewModel (`ContactViewModel.kt`)
- Partagé au niveau de l'activité (`by activityViewModels()`).
- Conserve la liste réactive des contacts (`LiveData<MutableList<Contact>>`) en mémoire.
- Propose la recherche par identifiant unique (`getContactById(id)`) et la mise à jour atomique (`updateContact(...)`).

### C. Écran Liste & Recherche (`ListeContactsFragment.kt` & `ContactRecyclerAdapter.kt`)
- Remplace la `ListView` classique par une **`RecyclerView`** hautement performante.
- Champ de recherche en temps réel (`etRecherche`) filtrant instantanément par nom, prénom ou téléphone (insensible à la casse).
- Gestion de l'état vide : Affiche un message explicite `"Aucun contact ne correspond à la recherche."` si aucun résultat.
- Clic sur un contact filtré : transmet l'identifiant unique `contact_id` au `DetailContactFragment` via un `Bundle`.

### D. Écran Détail & Édition (`DetailContactFragment.kt`)
- Permet de consulter et modifier le prénom, nom, téléphone, e-mail, latitude (-90 à 90) et longitude (-180 à 180).
- Actions disponibles :
  - **Galerie** & **Caméra** pour changer la photo.
  - **Appeler** (Intent implicite `ACTION_DIAL`).
  - **Envoyer E-mail** (Intent implicite `ACTION_SENDTO`).
  - **Voir sur la carte** : Valide les coordonnées et ouvre `CarteContactFragment`.
  - **Enregistrer** & **Annuler** avec gestion du `BackStack`.

### E. Écran Cartographique (`CarteContactFragment.kt`)
- Affiche la position GPS exacte du contact sur une carte cartographique interactive.
- Centre la carte sur la position `(latitude, longitude)` à l'ouverture et dépose un marqueur avec le nom complet du contact.
- Gestion d'erreur : Si les coordonnées sont absentes ou invalides, affiche un message d'erreur explicite sans faire planter l'application.

---

## 3. Réponses aux Questions de Compréhension (Section 11)

1. **Quelle différence existe-t-il entre une Activity et un Fragment ?**
   - Une `Activity` représente un composant système de haut niveau avec sa propre fenêtre et sa déclaration dans le `AndroidManifest.xml`.
   - Un `Fragment` est un composant réutilisable et modulaire d'interface utilisateur vivant à l'intérieur d'une Activity host. Les fragments s'associent pour former une interface dynamique et réactive dans une seule activité.

2. **Pourquoi utiliser `FragmentContainerView` et `addToBackStack()` ?**
   - `FragmentContainerView` est le conteneur officiel AndroidX résolvant les conflits d'insets, de z-index et d'animations.
   - `addToBackStack()` enregistre la transaction de remplacement de fragment dans la pile de retour du `FragmentManager`, permettant à l'utilisateur d'utiliser le bouton Retour physique/système pour revenir au fragment précédent sans fermer l'application.

3. **Quel est le rôle de `RecyclerView.Adapter` et de `ViewHolder` ?**
   - `RecyclerView.Adapter` assure la liaison entre le jeu de données et la vue.
   - `ViewHolder` mémorise les références des vues d'un item (`findViewById`), évitant les réallocations mémoire lors du défilement pour garantir une fluidité parfaite à 60/120 fps.

4. **Comment éviter une erreur d'indice après filtrage ?**
   - En ne basant jamais l'ouverture d'un contact sur l'index de la liste filtrée temporaire. Chaque contact possède un `id` unique. On extrait l'`id` lors du clic et on le recherche dans le ViewModel global via `getContactById(id)`.

5. **Pourquoi transmettre l'identifiant d'un contact plutôt que la position de sa ligne ?**
   - La position est dynamique et varie selon les filtres, le tri ou la suppression d'éléments. L'identifiant est immuable et désigne de façon absolue l'entité Contact dans le modèle.

6. **Comment garantir qu'une clé Google Maps ne soit pas exposée dans GitHub ?**
   - En utilisant le plugin `Secrets Gradle Plugin for Android`. La clé API est stockée exclusivement dans le fichier non versionné `local.properties` (`MAPS_API_KEY=...`) et injectée lors du build dans le manifeste sans figurer dans le dépôt public Git.

---

## 4. Tests effectués
- [x] Recherche en temps réel par nom, prénom, téléphone avec gestion de la casse.
- [x] Affichage du message d'erreur si la recherche ne renvoie aucun résultat.
- [x] Clic sur un contact filtré -> Ouverture exacte de la fiche sans erreur d'indice.
- [x] Modification des informations et des coordonnées GPS + Enregistrement.
- [x] Ouverture de la carte interactif centré sur la position GPS avec marqueur du nom.
- [x] Gestion des coordonnées manquantes ou invalides.
- [x] Navigation retour (`addToBackStack`) et persistance de l'état lors de la rotation d'écran.
