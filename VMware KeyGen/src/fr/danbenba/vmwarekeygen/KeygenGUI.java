package fr.danbenba.vmwarekeygen;

import javax.swing.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.List;
import java.util.ArrayList;
import java.io.*;
import java.util.*;
import java.util.concurrent.CountDownLatch;

@SuppressWarnings({"serial", "unused"})
public class KeygenGUI extends JFrame {

    private final JTextField txtProductKey;
    private final JButton btnGenerateKey;
    private JButton btnClose;
    private JLabel lblGitHubLink;
    private final JButton btnToggleAction;
    private final JLabel lblStatus;
    private final JComboBox<String> versionSelector;
    private JPopupMenu actionMenu;
    private boolean isGenerateMode = true;
    private java.util.List<String> keyList; // Liste pour stocker les clés
    private Random random;
    private final Properties config;
    private static final CountDownLatch latch = new CountDownLatch(1);


    public KeygenGUI() {
        // Charger la configuration
        config = loadConfig();

        // Configuration du Look and Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Personnalisation du message avec HTML pour centrer le texte
        String message = "<html><div style='text-align: center;'>"
                + "I don't guarantee the keys work, if one is not working,<br>"
                + "just try another one or change version."
                + "</div></html>";

        // Afficher la boîte de dialogue avec des options pour capturer le choix de l'utilisateur
        int result = JOptionPane.showOptionDialog(
                this,
                message,
                "NOTE",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null, // Pas d'icône personnalisée
                new Object[]{"    OK    "}, // Boutons affichés
                "OK" // Bouton par défaut
        );

        // Vérifier si l'utilisateur a cliqué sur la croix ou "OK"
        if (result == JOptionPane.CLOSED_OPTION) {
            System.exit(0); // Fermer le programme si l'utilisateur clique sur la croix
        }

        setTitle(config.getProperty("app.title"));
        setSize(510, 410);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        // Couleur personnalisée
        Color customColor = Color.decode("#010d12");
        getContentPane().setBackground(customColor);


        // Charger l'icône de l'application
        ImageIcon icon = new ImageIcon(getClass().getResource(config.getProperty("app.iconPath")));
        setIconImage(icon.getImage());

        // Ajouter une bannière redimensionnée
        try {
            ImageIcon originalIcon = new ImageIcon(getClass().getResource(config.getProperty("app.mainImage")));

            // Obtenir l'image originale
            Image originalImage = originalIcon.getImage();

            // Récupérer la largeur et la hauteur de l'image originale
            int originalWidth = originalIcon.getIconWidth();
            int originalHeight = originalIcon.getIconHeight();

            // Calculer le ratio de redimensionnement pour s'assurer que l'image tient dans un cadre
            double aspectRatio = (double) originalWidth / originalHeight;

            // Redimensionner l'image en bannière (par exemple 400px de large et proportionnellement réduit en hauteur)
            int newWidth = 400;
            int newHeight = (int) (newWidth / aspectRatio);

            // Redimensionner l'image
            Image scaledImage = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
            final ImageIcon bannerIcon = new ImageIcon(scaledImage);

            // Créer un JLabel pour afficher l'image redimensionnée et centrée
            JLabel lblImage = new JLabel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    // Centrer l'image dans le JLabel
                    int x = (getWidth() - bannerIcon.getIconWidth()) / 2;
                    int y = (getHeight() - bannerIcon.getIconHeight()) / 2;
                    g.drawImage(bannerIcon.getImage(), x, y, this);
                }
            };

            // Ajuster la taille et la position du JLabel pour qu'il occupe tout l'espace nécessaire à la bannière
            lblImage.setBounds(50, 10, 400, newHeight + 20);
            add(lblImage);

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Barre d'entrée pour la clé de produit
        txtProductKey = new JTextField("Generated product key will appear here");
        txtProductKey.setHorizontalAlignment(SwingConstants.CENTER);
        txtProductKey.setBounds(10, 230, 350, 25);
        txtProductKey.setEditable(false);
        add(txtProductKey);

        // Menu déroulant pour les versions
        String[] vmwareVersions = {"None", "VMware 4", "VMware 5", "VMware 6",
                "VMware 7", "VMware 8", "VMware 9", "VMware 10",
                "VMware 11", "VMware 12", "VMware 14",
                "VMware 15", "VMware 16", "VMware 17"};
        versionSelector = new JComboBox<>(vmwareVersions);
        versionSelector.setBounds(370, 230, 110, 25);
        versionSelector.addActionListener(e -> loadKeysForVersion((String) versionSelector.getSelectedItem()));
        add(versionSelector);

        // Adding an infinite spinning progress bar below the status label
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setBounds(10, 290, 470, 15);
        add(progressBar);

        // Bouton Generate/Copy Key (initial state is Generate Key)
        btnGenerateKey = new JButton("Generate Key");
        btnGenerateKey.setBounds(20, 320, 180, 25);
        btnGenerateKey.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isGenerateMode) {
                    generateProductKey();
                    lblStatus.setText("New product key generated.");
                    isGenerateMode = false;
                    btnGenerateKey.setText("Copy Key");
                    btnToggleAction.setVisible(false);
                } else {
                    StringSelection stringSelection = new StringSelection(txtProductKey.getText());
                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    clipboard.setContents(stringSelection, null);
                    lblStatus.setText("Product key copied to clipboard.");
                }

                btnToggleAction.setVisible(true);
            }
        });
        add(btnGenerateKey);

        // Bouton Toggle Action (dropdown menu button)
        btnToggleAction = new JButton("...");
        btnToggleAction.setBounds(199, 320, 20, 25);
        btnToggleAction.setVisible(false);
        btnToggleAction.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actionMenu.show(btnToggleAction, 0, btnToggleAction.getHeight());
            }
        });
        add(btnToggleAction);

        // Dropdown menu to switch between Generate and Copy modes
        actionMenu = new JPopupMenu();
        JMenuItem generateItem = new JMenuItem("Generate Key");
        generateItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isGenerateMode = true;
                btnGenerateKey.setText("Generate Key");
                btnToggleAction.setVisible(false);
            }
        });
        actionMenu.add(generateItem);

        JMenuItem copyItem = new JMenuItem("Copy Key");
        copyItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isGenerateMode = false;
                btnGenerateKey.setText("Copy Key");
                btnToggleAction.setVisible(true);
            }
        });
        actionMenu.add(copyItem);

        JMenuItem exportItem = new JMenuItem("Export Key");
        exportItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportKeyToFile();
            }
        });
        actionMenu.add(exportItem);

        // Bouton Close
        btnClose = new JButton("Close");
        btnClose.setBounds(230, 320, 120, 25);
        btnClose.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        add(btnClose);

        // GitHub Link (placed to the right of the Close button)
        lblGitHubLink = new JLabel(config.getProperty("link.name"));
        lblGitHubLink.setBounds(370, 321, 230, 25);
        lblGitHubLink.setForeground(Color.BLUE);
        lblGitHubLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblGitHubLink.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                try {
                    Desktop.getDesktop().browse(new URI(config.getProperty("link.url")));
                } catch (IOException | URISyntaxException e1) {
                    e1.printStackTrace();
                }
            }
        });
        add(lblGitHubLink);

        // Label de statut
        lblStatus = new JLabel("Select a version and generate a product key.");
        lblStatus.setBounds(10, 260, 470, 25);
        lblStatus.setForeground(Color.BLACK);
        btnGenerateKey.setEnabled(false); // Désactiver le bouton
        add(lblStatus);

        btnGenerateKey.setOpaque(true);
        btnGenerateKey.setContentAreaFilled(true);

        btnToggleAction.setOpaque(true);
        btnToggleAction.setContentAreaFilled(true);

        btnClose.setOpaque(true);
        btnClose.setContentAreaFilled(true);

        txtProductKey.setBackground(customColor);
        txtProductKey.setForeground(Color.WHITE);

        progressBar.setBackground(customColor);
        progressBar.setForeground(Color.GREEN);

        btnToggleAction.setBackground(customColor);
        btnToggleAction.setForeground(Color.WHITE);

        lblStatus.setBackground(customColor);
        lblStatus.setForeground(Color.WHITE);


        initializeKeyReader();
    }

    private Properties loadConfig() {
        Properties properties = new Properties();
        try (InputStream input = getClass().getResourceAsStream("vmkeygen.config")) {
            if (input == null) {
                throw new FileNotFoundException("Configuration file 'vmkeygen.config' not found.");
            }
            properties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return properties;
    }

    private void loadKeysForVersion(String version) {
        if ("None".equals(version)) {
            keyList.clear();
            lblStatus.setText("No version selected. Please select a version.");
            isGenerateMode = true;
            btnGenerateKey.setText("Generate Key");
            btnToggleAction.setVisible(false);
            btnGenerateKey.setEnabled(false); // Désactiver le bouton
            return;
        }

        // Le reste du code existant pour charger les clés
        String fileName = "keys_" + version.replace("VMware ", "vmware") + ".txt";
        keyList.clear();
        try {
            URL keyUrl = getClass().getResource("/keys/" + fileName);
            if (keyUrl == null) {
                lblStatus.setText("Error: Key file not found for " + version + " (" + fileName + ").");
                isGenerateMode = true;
                btnGenerateKey.setText("Generate Key");
                btnToggleAction.setVisible(false);
                btnGenerateKey.setEnabled(false); // Désactiver le bouton
                return;
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(keyUrl.openStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    keyList.add(line);
                }
            }
            if (keyList.isEmpty()) {
                lblStatus.setText("No keys found in " + fileName);
                isGenerateMode = true;
                btnGenerateKey.setText("Generate Key");
                btnToggleAction.setVisible(false);
                btnGenerateKey.setEnabled(false); // Désactiver le bouton
            } else {
                lblStatus.setText("Keys loaded for " + version);
                isGenerateMode = true;
                btnGenerateKey.setText("Generate Key");
                btnToggleAction.setVisible(false);
                btnGenerateKey.setEnabled(true); // Activer le bouton
            }
        } catch (IOException e) {
            lblStatus.setText("Error loading keys for " + version + ": " + e.getMessage());
            isGenerateMode = true;
            btnGenerateKey.setText("Generate Key");
            btnToggleAction.setVisible(false);
            btnGenerateKey.setEnabled(false); // Désactiver le bouton
            e.printStackTrace();
        }
    }


    private void initializeKeyReader() {
        keyList = new ArrayList<>();
        random = new Random();
    }

    private void generateProductKey() {
        if (keyList == null || keyList.isEmpty()) {
            btnGenerateKey.setEnabled(false); // Désactiver le bouton
            lblStatus.setText("No keys available. Please select a version.");
            btnGenerateKey.setEnabled(false); // Désactiver le bouton
        } else {
            int randomIndex = random.nextInt(keyList.size());
            String key = keyList.get(randomIndex);
            txtProductKey.setText(key);
            lblStatus.setText("Product key generated.");
        }
    }

    private void exportKeyToFile() {
        String key = txtProductKey.getText();
        if (key == null || key.isEmpty() || key.equals("Generated product key will appear here")) {
            JOptionPane.showMessageDialog(this, "No key to export.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Key");
        fileChooser.setSelectedFile(new File("vmware_prod.key"));
        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave))) {
                writer.write(key);
                JOptionPane.showMessageDialog(this, "Key exported to " + fileToSave.getAbsolutePath(), "Success", JOptionPane.INFORMATION_MESSAGE);
                lblStatus.setText("Key exported to " + fileToSave.getAbsolutePath());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error exporting key: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                lblStatus.setText("Error exporting key.");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new KeygenGUI().setVisible(true));
    }
}
