# VMware Keygen GUI

Cet outil est un générateur de clés graphique pour les produits VMware. Il permet aux utilisateurs de générer, copier et exporter des clés produit pour diverses versions de VMware. L'application est construite avec Java Swing et offre une interface graphique intuitive.

## Fonctionnalités

- **Génération de clés produit :** Générez rapidement des clés produit pour les versions de VMware sélectionnées.
- **Copie dans le presse-papier :** Copiez la clé générée en un seul clic.
- **Exportation de clés :** Sauvegardez les clés générées dans un fichier.
- **Sélecteur de version :** Choisissez parmi plusieurs versions de VMware pour générer des clés.
- **Interface intuitive :** Comprend des bannières, une sélection de versions et des contrôles intuitifs.

## Prérequis

Avant de commencer, assurez-vous d'avoir installé :

1. **Java Development Kit (JDK) :** Version 23.0.1 ou supérieur requise.
2. **IntelliJ IDEA :** Édition Community ou Ultimate.

## Guide d'installation

### Cloner le dépôt

1. Ouvrez un terminal ou une invite de commande.
2. Clonez le dépôt avec la commande suivante :
   ```bash
   git clone https://github.com/danbenba/vmware-keygen.git
   ```
3. Accédez au répertoire du projet :
   ```bash
   cd vmware-keygen
   ```

### Ouvrir le projet dans IntelliJ IDEA

1. Lancez IntelliJ IDEA.
2. Cliquez sur **File** > **Open**.
3. Naviguez jusqu'au dossier du dépôt cloné et sélectionnez-le.
4. IntelliJ IDEA chargera le projet.

### Configurer le SDK

1. Allez dans **File** > **Project Structure**.
2. Sous **Project Settings**, cliquez sur **Project**.
3. Configurez le **Project SDK** avec une installation JDK valide (JDK 8 ou ultérieur).
4. Cliquez sur **OK**.

### Exécuter l'application

1. Localisez la classe `KeygenGUI` dans le dossier `src`.
2. Faites un clic droit sur `KeygenGUI` et sélectionnez **Run 'KeygenGUI.main()'**.
3. L'application se lancera et affichera l'interface graphique.

## Instructions d'utilisation

1. Sélectionnez une version de VMware dans le menu déroulant.
2. Cliquez sur **Generate Key** pour créer une nouvelle clé produit.
3. Copiez la clé générée en utilisant le bouton **Copy Key**.
4. Facultativement, exportez la clé vers un fichier en utilisant l'option **Export Key** dans le menu déroulant.
5. Fermez l'application en cliquant sur le bouton **Close**.

## Dépannage

- **Fichier de configuration manquant :** Assurez-vous que `vmkeygen.config` est présent dans le dossier `resources`.
- **Fichiers de clés introuvables :** Vérifiez que le dossier `keys` contient les fichiers de clés appropriés pour les versions de VMware.
- **Messages d'erreur :** Consultez la console IntelliJ IDEA pour les traces de pile et les détails des erreurs.

## Contribuer

Les contributions sont les bienvenues ! N'hésitez pas à ouvrir des issues ou à soumettre des pull requests sur le [dépôt GitHub](https://github.com/danbenba/vmware-keygen).

## Avertissement

Les clés générées par cette application sont uniquement à des fins éducatives. L'auteur ne garantit pas leur validité ou leur utilisabilité.

## Contact

Pour toute question ou problème, veuillez visiter le [dépôt GitHub](https://github.com/danbenba/vmware-keygen) et ouvrir une issue.

