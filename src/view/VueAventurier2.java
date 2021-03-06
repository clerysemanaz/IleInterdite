/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import ile_interdite.Message;
import java.awt.BorderLayout;
import java.awt.Color;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observer;
import java.util.Observable;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import static javax.swing.SwingConstants.CENTER;
import javax.swing.border.MatteBorder;
import util.Utils;

public class VueAventurier2 extends JPanel implements Observer {
  
    @Override
    public void update(Observable o, Object arg) {
        MessageVue m = (MessageVue) arg;
        setPosition(m.texte);
    }
    /////////////////////////VUEAVENTURIER2 DEVIENT OBSERVABLE//////////////////////
    private static class MyObservable extends Observable {

        @Override
        public void setChanged() {
            super.setChanged();
        }

        @Override
        public void clearChanged() {
            super.clearChanged();
        }
    }
    private final JPanel panelBoutons;
    private final JPanel panelCentre;
    private final JPanel panelAventurier;
    private final JPanel mainPanel;
    private final JButton btnAller;
    private final JButton btnAssecher;
    private final JButton btnAutreAction;
    private final JButton btnTerminerTour;
    private final JTextField position;
    private final MyObservable observable = new MyObservable();

    public VueAventurier2(String nomAventurier, Color couleur, VueGrille vGrille) {
        
        ((Observable) vGrille.getObservable()).addObserver(this);
        mainPanel = new JPanel(new BorderLayout());

        mainPanel.setBackground(new Color(230, 230, 230));
        mainPanel.setBorder(BorderFactory.createLineBorder(couleur, 2));
        this.add(mainPanel);

        // =================================================================================
        // NORD : le titre = nom de l'aventurier + nom du joueur sur la couleurActive du pion
        this.panelAventurier = new JPanel();
        panelAventurier.setBackground(couleur);
        panelAventurier.add(new JLabel(nomAventurier, SwingConstants.CENTER));
        mainPanel.add(panelAventurier, BorderLayout.NORTH);

        // =================================================================================
        // CENTRE : 1 ligne pour position courante
        this.panelCentre = new JPanel(new GridLayout(2, 1));
        this.panelCentre.setOpaque(false);
        this.panelCentre.setBorder(new MatteBorder(0, 0, 2, 0, couleur));
        mainPanel.add(this.panelCentre, BorderLayout.CENTER);

        panelCentre.add(new JLabel("Position", SwingConstants.CENTER));
        position = new JTextField(50);
        position.setHorizontalAlignment(CENTER);
        panelCentre.add(position);
        

        // =================================================================================
        // SUD : les boutons
        this.panelBoutons = new JPanel(new GridLayout(2, 2));
        this.panelBoutons.setOpaque(false);
        mainPanel.add(this.panelBoutons, BorderLayout.SOUTH);

        this.btnAller = new JButton("Aller");
        this.btnAssecher = new JButton("Assecher");
        this.btnAutreAction = new JButton("AutreAction");
        this.btnTerminerTour = new JButton("Terminer Tour");

        this.panelBoutons.add(btnAller);
        this.panelBoutons.add(btnAssecher);
        this.panelBoutons.add(btnAutreAction);
        this.panelBoutons.add(btnTerminerTour);
       
        mainPanel.repaint();
        
////////////////////////////AJOUTE LISTENER ALLER///////////////////////////////////////////////////////
        btnAller.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                observable.setChanged();
                Message m = new Message(Utils.Commandes.BOUGER, CENTER, WIDTH, Utils.Tresor.PIERRE, WIDTH);
                m.texte = position.getText();
                observable.notifyObservers(m);
                observable.clearChanged();
                setPosition("Deplacer");
            }
        });
////////////////////////////AJOUTE LISTENER AUTRE///////////////////////////////////////////////////////
        btnAutreAction.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                observable.setChanged();
                Message m = new Message(Utils.Commandes.CAPACITE, CENTER, WIDTH, Utils.Tresor.PIERRE, WIDTH);
                m.texte = position.getText();
                observable.notifyObservers(m);
                observable.clearChanged();
                setPosition("Autre");
            }
        });
////////////////////////////AJOUTE LISTENER ASSECHER///////////////////////////////////////////////////////
        btnAssecher.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                observable.setChanged();
                Message m = new Message(Utils.Commandes.ASSECHER, CENTER, WIDTH, Utils.Tresor.PIERRE, WIDTH);
                m.texte = position.getText();
                observable.notifyObservers(m);
                observable.clearChanged();
                setPosition("Assecher"); 
            }
        });
////////////////////////////AJOUTE LISTENER TERMINER///////////////////////////////////////////////////////
        btnTerminerTour.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                observable.setChanged();
                Message m = new Message(Utils.Commandes.TERMINER, CENTER, WIDTH, Utils.Tresor.PIERRE, WIDTH);
                observable.notifyObservers(m);
                observable.clearChanged();
                setPosition("Terminer");
            }
        });
      
    }

///////////////////////GET_BOUTONS/////////////////////////////////////////////
     public JButton getBtnAutreAction() {
        return btnAutreAction;
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
///////////////////////////////////////////////////////////////////////////////

    
   public void setPosition(String pos) {
        this.position.setText(pos);
    }
   
   public MyObservable getObservable() {
        return observable;
    }
}




