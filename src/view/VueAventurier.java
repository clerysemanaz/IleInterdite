    /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

/**
 *
 * @author semanazc
 */

import java.util.Observable;

import ile_interdite.Message;
import ile_interdite.Observateur;
import ile_interdite.TypesMessages;
import java.awt.BorderLayout;
import java.awt.Color;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import static javax.swing.SwingConstants.CENTER;
import javax.swing.border.MatteBorder;
import model.aventuriers.Aventurier;
import util.Utils;
import util.Utils.Pion;

 
public class VueAventurier extends Observable {
     
    private final JPanel panelBoutons ;
    private final JPanel panelCentre ;
    private final JFrame window;
    private final JPanel panelAventurier;
    private final JPanel mainPanel;
    private final JButton btnAller  ;
    private final JButton btnAssecher;
    private final JButton btnAutreAction;
    private final JButton btnTerminerTour;
    private final JTextField position;
    private Observateur observateur;
    
    public VueAventurier (String nomJoueur, String nomAventurier, Color couleur){

        this.window = new JFrame();
        window.setSize(350, 200);

        window.setTitle(nomJoueur);
        mainPanel = new JPanel(new BorderLayout());
        this.window.add(mainPanel);

        mainPanel.setBackground(new Color(230, 230, 230));
        mainPanel.setBorder(BorderFactory.createLineBorder(couleur, 2)) ;
        
        window.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);

        // =================================================================================
        // NORD : le titre = nom de l'aventurier + nom du joueur sur la couleurActive du pion

        this.panelAventurier = new JPanel();
        panelAventurier.setBackground(couleur);
        panelAventurier.add(new JLabel(nomAventurier,SwingConstants.CENTER ));
        mainPanel.add(panelAventurier, BorderLayout.NORTH);
   
        // =================================================================================
        // CENTRE : 1 ligne pour position courante
        this.panelCentre = new JPanel(new GridLayout(2, 1));
        this.panelCentre.setOpaque(false);
        this.panelCentre.setBorder(new MatteBorder(0, 0, 2, 0, couleur));
        mainPanel.add(this.panelCentre, BorderLayout.CENTER);
        
        panelCentre.add(new JLabel ("Position", SwingConstants.CENTER));
        position = new  JTextField(30);
        position.setHorizontalAlignment(CENTER);
        panelCentre.add(position);


        // =================================================================================
        // SUD : les boutons
        this.panelBoutons = new JPanel(new GridLayout(2,2));
        this.panelBoutons.setOpaque(false);
        mainPanel.add(this.panelBoutons, BorderLayout.SOUTH);

        this.btnAller = new JButton("Aller") ;
        this.btnAssecher = new JButton( "Assecher");
        this.btnAutreAction = new JButton("AutreAction") ;
        this.btnTerminerTour = new JButton("Terminer Tour") ;
        
        this.panelBoutons.add(btnAller);
        this.panelBoutons.add(btnAssecher);
        this.panelBoutons.add(btnAutreAction);
        this.panelBoutons.add(btnTerminerTour);

        this.window.setVisible(true);
        mainPanel.repaint();
        
        btnAller.addActionListener(new ActionListener(){
            
            @Override
            public void actionPerformed(ActionEvent e) {
                Message m = new Message(Utils.Commandes.BOUGER, CENTER,CENTER, Utils.Tresor.PIERRE, CENTER);
                setObservateur(observateur);
                m.type = TypesMessages.Deplacer;
                m.texte = position.getText();
             
                observateur.traiterMessage(m);
            
        }
        });
        
        
        
        btnAutreAction.addActionListener(new ActionListener(){
            
            @Override
            public void actionPerformed(ActionEvent e) {
                Message m = new Message(Utils.Commandes.CHOISIR_CARTE, CENTER, CENTER, Utils.Tresor.PIERRE, CENTER);
                m.type = TypesMessages.Autre;
                m.texte = position.getText();
                observateur.traiterMessage(m);
            }
            
        });
         
        btnAssecher.addActionListener(new ActionListener(){
            
            @Override
            public void actionPerformed(ActionEvent e) {
                Message m = new Message(Utils.Commandes.ASSECHER, CENTER, CENTER, Utils.Tresor.PIERRE, CENTER);
                setObservateur(observateur);
                m.type = TypesMessages.Assecher;
                m.texte = position.getText();
                observateur.traiterMessage(m);
            }
            
        });
         
        btnTerminerTour.addActionListener(new ActionListener(){
            
            @Override
            public void actionPerformed(ActionEvent e) {
                Message m = new Message(Utils.Commandes.TERMINER, CENTER, CENTER, Utils.Tresor.PIERRE, CENTER);
                m.type = TypesMessages.Terminer;
                observateur.traiterMessage(m);
            }
            
        });
        
        
        
    }  
      public void setObservateur(Observateur observateur){
        this.observateur=observateur;
    }

     public JButton getBtnAutreAction() {
        return btnAutreAction;
    }

    public void setPosition(String pos) {
        this.position.setText(pos);
    }
    public String getTexte(){
        return this.position.getText();
    }

    public JButton getBtnAller() {
        return btnAller;
    }
    
    public JButton getBtnAssecher() {
        return btnAssecher;
    }

    public JButton getBtnTerminerTour() {
        return btnTerminerTour;
    }
 
    public void affiche(){
        
    }
    
    public void close(){
        window.setVisible(false);
        
                
    }
    
     public static void main(String [] args) {
        // Instanciation de la fenêtre
        VueAventurier vueAventurier = new VueAventurier ("Manon", "Explorateur",Pion.ROUGE.getCouleur() );
        
    }
}
