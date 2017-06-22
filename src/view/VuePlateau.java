/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import ile_interdite.Controleur2;
import ile_interdite.Message;
import ile_interdite.Observateur;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import model.aventuriers.*;
import model.grille.Grille;
import model.grille.Tuile;
import util.Utils;

/**
 *
 * @author sarrasie
 */
public class VuePlateau extends Observable implements Observer {

    private Grille grille;
    private final Aventurier joueur;
    private Observable observable;
    private VueGrille tuileGrille;
    
   JFrame fenetre = new JFrame("Plateau de jeu");

    public VuePlateau(Aventurier joueur, Grille grille) {
       this.joueur=joueur;
       this.grille = grille;
       
        /////////////////////////////////////
        //Instanciation de la fenêtre
        fenetre.setSize(1000, 1000);
        fenetre.setLayout(new BorderLayout());
        fenetre.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
        
        /////////////////////////////////////
        //Creation de panel et ajout d'un titre
        JPanel panelNord = new JPanel();
        panelNord.setLayout(new GridBagLayout());
        panelNord.setPreferredSize(new Dimension(0, 75)); // modification de la taille du panel
        ////TITRE///
        JLabel titre = new JLabel("Île interdite");
        titre.setForeground(Color.BLUE);
        titre.setFont(new Font("Serif", Font.BOLD, 20));
        panelNord.add(titre);
        panelNord.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // permet de centrer le texte au milieu du panel
        fenetre.add(panelNord, BorderLayout.NORTH);
        
        /////////////////////////////////////
        // Création des tuiles du plateau
        this.tuileGrille = new VueGrille(grille);
        fenetre.add(tuileGrille, BorderLayout.CENTER);
        tuileGrille.repaint();
       

        /////////////////////////////////////
        //Création de la fenêtre de saisie et des boutons
        VueAventurier2 aventurier = new VueAventurier2(joueur.getRole().getNom(),joueur.getRole().getPion().getCouleur(), tuileGrille);
        JPanel panelSud = new JPanel();
        JPanel panelEast = new JPanel(new GridLayout(7,3));
        JButton niveau = new JButton("Niveau d'eau");

        panelSud.add(aventurier);
        fenetre.add(panelSud, BorderLayout.SOUTH);
        fenetre.add(panelEast,BorderLayout.EAST);
        
       ///BOUTON NIVEAU///
        for(int i = 0; i < 21;i++){
            if(i==11){
                panelEast.add(niveau);
            }else{
                panelEast.add(new JLabel(""));
            }
        }
        ////////////////////
        
        ///LISTENER BOUTON NIVEAU///
        niveau.addMouseListener(new MouseListener() {
            VueNiveau niveauEau = new VueNiveau(1);
           @Override
           public void mouseClicked(MouseEvent e) {
           }
           @Override
           public void mousePressed(MouseEvent e) {
           }
           @Override
           public void mouseReleased(MouseEvent e) {
           }
           @Override
           public void mouseEntered(MouseEvent e) {
               niveauEau.Affiche();
           }

           @Override
           public void mouseExited(MouseEvent e) {
               niveauEau.close();
           }
       });
       ////////////////////////
       // Observable
       ((Observable) aventurier.getObservable()).addObserver(this);
    
    }
  

    public JFrame getFenetre() {
        return fenetre;
    }

    ////////////////////GET_SET_VUEGRILLE///////////////////////////////////////////
    public VueGrille getTuileGrille() {
        return tuileGrille;
    }
    public void setTuileGrille(VueGrille tuileGrille) {
        this.tuileGrille = tuileGrille;
    }
    
    
    
    public void updateGrille(Grille grille) {
        tuileGrille.setVisible(false);
        VueGrille vueGrille = new VueGrille(grille);
        fenetre.add(vueGrille, BorderLayout.CENTER);
    }
    
    
    
    //////////////TRANSFERT DU MESSAGE DE LA VUE AVENTURIER AU CONTROLEUR///////////////
    @Override
    public void update(Observable o, Object arg) {
        setChanged(); 
        Message m = (Message) arg;
        System.out.println(m.texte);
        notifyObservers(m);
        clearChanged();
    }
    
    /////////////////////AFFICHER/FERMER_FENETRE/////////////////////////////////
     public void close(){
        fenetre.setVisible(false);
    }
    public void Affiche(){
        fenetre.setVisible(true);
        fenetre.repaint();
    }
    

}


