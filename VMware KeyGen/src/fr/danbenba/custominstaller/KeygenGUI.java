package fr.danbenba.custominstaller;

import javax.swing.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URI;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

@SuppressWarnings({"serial", "unused"})
public class KeygenGUI extends JFrame {

    private JTextField txtProductKey;
    private JButton btnGenerateKey;
    private JButton btnToggleAction;
    private JButton btnClose;
    private JLabel lblStatus;
    private JLabel lblGitHubLink;
    private JLabel lblFooter;
    private JLabel lblImage;
    private JPopupMenu actionMenu;
    private static CountDownLatch latch = new CountDownLatch(1);

    private Properties config;
    private BufferedReader keyReader;
    private boolean isGenerateMode = true;

    public KeygenGUI() {
        // Charger le fichier de configuration
        config = loadConfig();

        // Configuration du Look and Feel du système
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        JOptionPane.showMessageDialog(this, "Programme conçu pour le logiciel VMware Workstation 17 Pro, \n                 non compatible avec les autres versions.", "Error", JOptionPane.INFORMATION_MESSAGE);


        setTitle(config.getProperty("app.title"));
        setSize(500, 400);
        setResizable(false); // Set dialog to non-resizable
        ImageIcon icon = new ImageIcon(getClass().getResource(config.getProperty("app.iconPath")));
        setIconImage(icon.getImage());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);

        // Add Image at the Top
        ImageIcon originalIcon = new ImageIcon(getClass().getResource(config.getProperty("app.mainImage")));

        // Obtenir l'image originale
        Image originalImage = originalIcon.getImage();

        // Récupérer la largeur et la hauteur de l'image originale
        int originalWidth = originalIcon.getIconWidth();
        int originalHeight = originalIcon.getIconHeight();

        // Calculer le ratio de redimensionnement pour s'assurer que l'image tient dans un cadre 64x64 sans déformation
        double aspectRatio = (double) originalWidth / originalHeight;

        // Redimensionner l'image en bannière (par exemple 400px de large et proportionnellement réduit en hauteur)
        int newWidth = 400;
        int newHeight = (int) (newWidth / aspectRatio);

        // Redimensionner l'image
        Image scaledImage = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        final ImageIcon bannerIcon = new ImageIcon(scaledImage);

        // Créer un JLabel pour afficher l'image redimensionnée et centrée
        lblImage = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Centrer l'image dans le JLabel
                int x = (getWidth() - bannerIcon.getIconWidth()) / 2;
                int y = (getHeight() - bannerIcon.getIconHeight()) / 2;
                g.drawImage(bannerIcon.getImage(), x, y, this);
            }
        };

        lblImage.setBounds(50, 10, 400, newHeight + 20);
        add(lblImage);

        // Product Key input (initially empty)
        txtProductKey = new JTextField("Generated product key will appear here");
        txtProductKey.setHorizontalAlignment(SwingConstants.CENTER);
        txtProductKey.setBounds(10, 230, 470, 25);
        txtProductKey.setEditable(false);
        add(txtProductKey);

        // Status label (log text placed above the generate button)
        lblStatus = new JLabel("Click 'Generate Key' to get a product key.");
        lblStatus.setBounds(10, 260, 470, 25);
        lblStatus.setForeground(Color.BLACK);
        add(lblStatus);

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

        // Label Footer (aligned 3px above the buttons)
        lblFooter = new JLabel("---- Data not found ----");
        lblFooter.setBounds(10, 198, 470, 25);
        lblFooter.setForeground(Color.BLACK);
        lblFooter.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblFooter);

        updateVersionLabel();
        initializeKeyReader();
    }

    private Properties loadConfig() {
        Properties properties = new Properties();
        try (InputStream input = getClass().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new FileNotFoundException("Configuration file 'config.properties' not found in the classpath.");
            }
            properties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return properties;
    }

    private void updateVersionLabel() {
        try {
            URL url = new URL(config.getProperty("version.url"));
            String versionText = downloadText(url);
            lblFooter.setText("---- " + versionText + " ----");
        } catch (IOException e) {
            lblFooter.setText("Server Closed");
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Internal Error: Server Closed.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String downloadText(URL url) throws IOException {
        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }
        return response.toString();
    }

    private void initializeKeyReader() {
        try {
            URL keyUrl = new URL(String.valueOf(Objects.requireNonNull(getClass().getResource(config.getProperty("keys.url")))));
            keyReader = new BufferedReader(new InputStreamReader(keyUrl.openStream()));
        } catch (IOException e) {
            lblStatus.setText("Error: Unable to load product keys from the URL.");
            btnGenerateKey.setEnabled(false);
        }
    }

    private void generateProductKey() {
        try {
            String key = keyReader.readLine();
            if (key != null) {
                txtProductKey.setText(key);
                lblStatus.setText("New product key generated.");
            } else {
                lblStatus.setText("No more product keys available. Restarting...");
                initializeKeyReader();
                generateProductKey();
            }
        } catch (IOException e) {
            lblStatus.setText("Error reading product key: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new KeygenGUI().setVisible(true);
            }
        });

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }
    }
}
