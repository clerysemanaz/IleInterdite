/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.aventuriers;

import java.util.ArrayList;
import model.grille.Grille;
import util.Utils;
import model.grille.Tuile;

/**
 *
 * @author sarrasie
 */
public abstract class Aventurier {
    private roleAventuriers role;
    private final String capacite;
    private String nomJ;
    private Tuile estSur;
    private int nbaction;
    private Grille grille;
    

    
    public Aventurier(roleAventuriers role ,String capacite, Tuile estSur){
        this.role = role;
        this.capacite = capacite;
        this.nbaction = 1;
        this.estSur = estSur;
    }
    public String getNom(){
        return nomJ;
    }

    public Tuile getEstSur() {
        return estSur;
    }

    public int getNbaction() {
        return nbaction;
    }

    public roleAventuriers getRole() {
        return role;
    }

    
    
    private int getTuile(){
       return (estSur.getNumLigne()+estSur.getNumColonne());
    }    
    
    public void setNom (String nom){
        this.nomJ = nom;
    }

    public void setEstSur(Tuile estSur) {
        this.estSur = estSur;
    }

    public void setNbaction(int nbaction) {
        this.nbaction = nbaction;
    }
    
    //Les methodes qui suivent sont les actions réalisées par l'aventurier
    
    public void assecherTuile(Tuile tuile){
        tuile.setStatut(Utils.EtatTuile.ASSECHEE);
        setNbaction(getNbaction() + 1);
    }
    
    public void seDeplacer(Tuile tuile){
              
        setEstSur(tuile);
        tuile.ajouterAventurier(this);
        setNbaction(getNbaction() + 1);
   
    
    }
    public void donnerCarteJoueur(){
        
    }

    public void setRole(roleAventuriers role) {
        this.role = role;
    }
    
    //rend un arraylist des tuiles sur lesquelles on peut se deplacer
     public ArrayList<Tuile> RecupererTuile(Tuile position,Grille grille){
        
        int l = position.getNumLigne();
        int c = position.getNumColonne();
        System.out.println("Vous etes en "+ l +","+ c);
        ArrayList<Tuile> tuiles = new ArrayList<>();
        ArrayList<Tuile> tuilesFin = new ArrayList<>();
        
        //tuile dessus//
        if (l>=1){
            int cDessus= c;
            int lDessus= l - 1;
            tuiles.add(grille.getTuiles()[lDessus][cDessus]);
        }
        //tuile Dessous//
        if (l<=4){
            int cDessous= c;
            int lDessous= l + 1;
            tuiles.add(grille.getTuiles()[lDessous][cDessous]);
        }
        //tuile gauche//
        if (c>=1){
            int cGauche= c -1 ;
            int lGauche= l;
            tuiles.add(grille.getTuiles()[lGauche][cGauche]);
        }
        //tuile droite//
        if (c<=4){
            int cDroite=c + 1;
            int lDroite=l;
            tuiles.add(grille.getTuiles()[lDroite][cDroite]);
        }
        
      for (Tuile tuile : tuiles) { //on ajoute toutes les tuiles sèche à tuilesFin
            if (tuile!=null && tuile.getStatut()==Utils.EtatTuile.ASSECHEE ){
                tuilesFin.add(tuile);
            }
        }
     
      return tuilesFin;
     }
     public void Afficher(ArrayList<Tuile> tuiles){
         tuiles=RecupererTuile(estSur, grille);
          System.out.println("Les tuiles sur lesquels vous pouvez vous déplacer sont : ");
        
                String positionPossible = "";
                if (!tuiles.isEmpty()){ //Si il y a des tuiles sur lesquels ont peut se déplacer
                        for(int k = 0; k < tuiles.size() - 1; k++){
                            positionPossible += (tuiles.get(k).getNumLigne()+","+tuiles.get(k).getNumColonne()+" ou ");
                        }
                    positionPossible += tuiles.get(tuiles.size()-1).getNumLigne()+","+tuiles.get(tuiles.size()-1).getNumColonne();
                   
                }else{
                    positionPossible = "Impossible de se déplacer";
                }
                System.out.println(positionPossible);
     }
   
    
}
