package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import model.grille.Grille;
import model.grille.Tuile;
 
public class VueGrille extends JPanel {
   
    private Tuile [][] tuiles;
    private Grille grille;
   
    public VueGrille(Grille grille) {
        this.setLayout(new GridLayout(6,6,5,5));//creation d'une grille 6*6 avec des espaces entre chaque bouton de la grille
      
        this.grille = grille;
        this.tuiles = new Tuile [6][6];
           for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                if (i == 0 && j == 0
                        || i == 0 && j == 1
                        || i == 0 && j == 4
                        || i == 0 && j == 5 // toutes les cases vides de la première ligne
                        || i == 1 && j == 0
                        || i == 1 && j == 5 // toutes les cases vides de la 2eme ligne
                        || i == 4 && j == 0
                        || i == 4 && j == 5 // toutes les cases vides de la 5eme ligne
                        || i == 5 && j == 0
                        || i == 5 && j == 1
                        || i == 5 && j == 4
                        || i == 5 && j == 5 // toutes les cases vides de la 6eme ligne
                        ) {
                    this.add(new JLabel(""));
                } else {                    // les tuiles non vide
                    TuileGrille tuile = new TuileGrille(i, j);
                    tuile.setText(i + "," + j);
                    tuile.setBorder(BorderFactory.createLineBorder(Color.green));// modifie la couleur de la bordure
                    this.add((JButton) tuile);
                }
            }

        }
        }
       
    }


    