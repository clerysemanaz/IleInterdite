/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ile_interdite;
import static ile_interdite.TypesMessages.Deplacer;
import java.awt.Color;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;
import java.util.Observable;
import java.util.Observer;
import model.aventuriers.Aventurier;
import model.aventuriers.Explorateur;
import model.aventuriers.Ingenieur;
import model.aventuriers.roleAventuriers;
import model.grille.Grille;
import model.grille.Tuile;
import view.*;
import util.Utils;
import model.aventuriers.*;
import model.cards.CarteInondation;
import model.cards.CarteTirage;
import model.cards.CarteTresor;
import model.cards.typeCarte;


/**
 *
 * @author semanazc
 */
public class Controleur2 implements Observer {
    //Attributs Controleur2:
private String aventurierCourant;
    //private final VueAventurier vue;
    private final VuePlateau vueP;
    private final HashMap<String,Aventurier> aventuriers;
    private Grille grille;
    private boolean premierClic;
    private ArrayList<Tuile> tuilesAutours;
    private ArrayList<Tuile> tuilesAutoursNonSeches;
    private boolean choixFinTour;
    private Observable observable;
   
    
    //attributs pour les cartes
    
    //les cartes tirage contiennent les tresors et la montee des eaux
    private Stack<CarteTirage> cartesTiragePioche;
    private Stack<CarteTirage> cartesTirageDefausse;
    
    //lees cartes innondations changent l'Etat des tuiles
    private Stack<CarteInondation> cartesInondationPioche;
    private Stack<CarteInondation> cartesDefausseInondation;
    private ArrayList<CarteInondation> cartesInondationEnJeu;
    
    int niveauEau;
    
    
    //constructeur
    public Controleur2() {
       
        //initialisation des attributs
        this.aventuriers = new HashMap<>();
        this.premierClic = true;
        this.tuilesAutours = new ArrayList<>();
        this.tuilesAutoursNonSeches = new ArrayList<>();
        this.choixFinTour = false;
        /*
         --------------------------------------------------------------
         |                  Lancement de la partie                    |
         --------------------------------------------------------------
        */
        System.out.println("Lancement de la partie");
        demarrerPartie();
        /*
            La ligne ci-dessous est à utiliser si l'on veut forcer un aventurier en particulier à une position particulière (/!\ Constructeur de pilote différent des autres)
        */
        //aventuriers.put("Gaspard", new Navigateur(getGrille().getTuiles()[4][3]));
        //vue = new VueAventurier("Gaspard", aventuriers.get("Gaspard").getRole().getNom(), aventuriers.get("Gaspard").getRole().getPion().getCouleur());
        afficherGrilleConsole();
        this.vueP = new VuePlateau(aventuriers.get("Gaspard"), getGrille());
        
        getVueP().addObserver(this);
     
        vueP.Affiche();
       
        if(aventuriers.get("Gaspard").getNbaction() > 3 || choixFinTour){
            //fermeture de la fenêtre et ouverture de celle du nouvel aventurier
            vueP.close();
        }
    }
    
    public final void demarrerPartie() {
        this.grille = new Grille();
        
        //to-do : récuperer les noms des différents joueurs, pour le moment je les aient initialisés à la main,
        ArrayList<String> joueurs = new ArrayList<>();
        joueurs.add("Gaspard");
        joueurs.add("Eddy");
        joueurs.add("Clery");
        joueurs.add("Sacha");
        
        //creation des aventuriers
        Explorateur explorateur = new Explorateur(getGrille().getTuiles()[2][4]);
        
        Ingenieur ingenieur = new Ingenieur(getGrille().getTuiles()[0][3]);
        
        Messager messager = new Messager(getGrille().getTuiles()[1][3]);
        
        Navigateur navigateur = new Navigateur(getGrille().getTuiles()[1][3]);
        
        Pilote pilote = new Pilote(getGrille().getTuiles()[2][1],false);
        
        Plongeur plongeur = new Plongeur(getGrille().getTuiles()[1][2]);
        
        ArrayList<Aventurier> listeaventuriersjouables = new ArrayList();
        listeaventuriersjouables.add(explorateur);
        listeaventuriersjouables.add(ingenieur);
        listeaventuriersjouables.add(messager);
        listeaventuriersjouables.add(navigateur);
        listeaventuriersjouables.add(pilote);
        listeaventuriersjouables.add(plongeur);
        Collections.shuffle((List<?>) listeaventuriersjouables); //melange des aventuriers
        
        
        for (int k = 0; k < 4; k++){ //comme il y a 4 joueurs on ajoute 4 aventuriers à notre collection aventuriers
              listeaventuriersjouables.get(k).setNom(joueurs.get(k)); // on donne un nom à l'aventurier
            getAventuriers().put(joueurs.get(k), listeaventuriersjouables.get(k)); // on l'ajoute à aventuriers
        }
    }
    
    public Tuile getPosition(Aventurier aventurier){
        Tuile position = aventurier.getEstSur();
        return position;
    }
    
    public int getLigne(Tuile position){
       int l =position.getNumLigne();
       return l;
    }
    
     public int getColonne(Tuile position){
       int c =position.getNumColonne();
       return c;
    }

    public Grille getGrille() {
        return grille;
    }
    
     public void afficherGrilleConsole(){
         for (int i = 0; i <6; i ++) {
             for (int k = 0; k <6; k++) {
                 if(grille.getTuiles()[i][k] != null){
                     System.out.print(" |" + i + ","+ k + "| ");
                 } else {
                     System.out.print( " |   | ");
                 }
            }
             System.out.print("\n");
         }
     }

    public HashMap<String, Aventurier> getAventuriers() {
        return aventuriers;
    }

    private void setPremierClic(boolean b) {
        this.premierClic = b;
    }

    public VuePlateau getVueP() {
        return vueP;
    }
    
    public Observable getObservable() {
        return observable;
    }
     @Override
    public void update(Observable o, Object arg) {
    Message msg = (Message) arg;
        Exception AucunePositionEntreeException = new Exception();
        Exception AssechementImpossibleException = new Exception();
        Exception DeplacementImpossibleException = new Exception();
         
        switch(msg.getCommande()){
            
            case BOUGER:
                try{if(aventuriers.get("Gaspard").getNbaction() > 3 || choixFinTour){
            //fermeture de la fenêtre et ouverture de celle du nouvel aventurier
            vueP.close();
        }
                    if (premierClic){
                        tuilesAutours = aventuriers.get("Gaspard").RecupererTuile(aventuriers.get("Gaspard").getEstSur(),grille);
                       aventuriers.get("Gaspard").Afficher(tuilesAutours);
                       vueP.setInformation(vueP.getInformation(),aventuriers.get("Gaspard").getAfficher(tuilesAutours));
                        setPremierClic(false);
                    }else { // on va entamer la procédure de deplacement du joueur sur les coordonnées entrées
                        if (!msg.texte.equals("")){ //si la case message à été remplie
                            String texte = msg.texte;

                            System.out.println("Deplacement en cours");
                             vueP.setInformation(vueP.getInformation(),"Deplacement en cours");

                            String[] positionString;
                            positionString = texte.split(","); //parsing des coordonnées
                            int[] position = new int[2];
                            position[0] = Integer.parseInt(positionString[0]); //ligne
                            position[1] = Integer.parseInt(positionString[1]); //colonne
                            
                            if (!tuilesAutours.contains(grille.getTuiles()[position[0]][position[1]])){
                                System.err.println("Deplacement impossible");
                                vueP.setInformation(vueP.getInformation(),"Deplacement impossible");
                                throw DeplacementImpossibleException;
                            }else{ //on finalise la procédure de déplacement

                                aventuriers.get("Gaspard").seDeplacer(getGrille().getTuiles()[position[0]][position[1]]);
                                System.out.println("Deplacement bien effectué, vous êtes maintenant en : " + aventuriers.get("Gaspard").getEstSur().getNumLigne()+ "," + aventuriers.get("Gaspard").getEstSur().getNumColonne());
                                vueP.setInformation(vueP.getInformation(),"Deplacement bien effectué, vous êtes maintenant sur la tuile : " + aventuriers.get("Gaspard").getEstSur().getNom());
                                premierClic = true;
                            }  

                        }else {
                            System.err.println("Aucune position entrée");
                            vueP.setInformation(vueP.getInformation(),"Aucune position entrée");
                           throw AucunePositionEntreeException;
                        }

                    }    
               }catch(Exception e){
                    System.err.println("Une erreur c'est produite merci de recommencer");
                    vueP.setInformation(vueP.getInformation(),"Une erreur c'est produite merci de recommencer");
                    setPremierClic(true);
                }    
                
                //vueP.setPosition(""); //clear de la zone de texte de la vue
            break;
           case ASSECHER:
                try {
                    if (premierClic){
                        tuilesAutoursNonSeches = aventuriers.get("Gaspard").AssecherTuile(aventuriers.get("Gaspard").getEstSur(),grille);
                        vueP.setInformation(vueP.getInformation(), aventuriers.get("Gaspard").getAfficherAssecher(tuilesAutoursNonSeches));
                        aventuriers.get("Gaspard").AfficherAssecher(tuilesAutoursNonSeches);
                      
                        setPremierClic(false);
                    }else { //on entamme la procédure d'asséchement
                        System.out.println("Vous avez choisit de sécher une case : \n ");
                        vueP.setInformation(vueP.getInformation(),"Vous avez choisit\nde sécher une case : \n ");
                        if (!msg.texte.equals("")){ //si la case message à été remplie
                            String texte = msg.texte;

                            System.out.println("Assechement en cours");
                            vueP.setInformation(vueP.getInformation(),"Assechement en cours");
                            String[] positionString;
                            positionString = texte.split(","); //parsing des coordonnées
                            int[] position = new int[2];
                            position[0] = Integer.parseInt(positionString[0]); //ligne
                            position[1] = Integer.parseInt(positionString[1]); //colonne
                            
                            if (!tuilesAutoursNonSeches.contains(grille.getTuiles()[position[0]][position[1]])){
                                System.err.println("Assechement impossible");
                                vueP.setInformation(vueP.getInformation(),"Assechement impossible");
                                throw AssechementImpossibleException;
                            }else{ //on finalise la procédure d'asséchement

                                aventuriers.get("Gaspard").assecherTuile(getGrille().getTuiles()[position[0]][position[1]]);
                                System.out.println("Asséchement bien effectué,la tuile : " + getGrille().getTuiles()[position[0]][position[1]].getNumLigne()+ ","
                                        + getGrille().getTuiles()[position[0]][position[1]].getNumColonne() + " est " + getGrille().getTuiles()[position[0]][position[1]].getStatut().toString());
                                premierClic = true;
                                
                                vueP.setInformation(vueP.getInformation(),"Asséchement bien effectué,la tuile : " + getGrille().getTuiles()[position[0]][position[1]].getNumLigne()+ ","
                                        + getGrille().getTuiles()[position[0]][position[1]].getNumColonne() + " est " + getGrille().getTuiles()[position[0]][position[1]].getStatut().toString());
                                
                         
                            }
                            
                        }else {
                            System.err.println("Aucune position entrée");
                            vueP.setInformation(vueP.getInformation(),"Aucune position entrée");
                            throw AucunePositionEntreeException;
                        }
                        
                        getVueP().getTuileGrille().updateGrille(grille);
                        getVueP().updateGrille(grille);
                        
                    }
                }catch(Exception e){
                    System.err.println("Une erreur c'est produite merci de recommencer");
                    vueP.setInformation(vueP.getInformation(),"Une erreur c'est produite merci de recommencer");
                    setPremierClic(true);
                }
               
                
                //vue.setPosition(""); //clear de la zone de texte de la vue
            break;
            case CAPACITE:
                
             ////////////////////////////////////////////////////////////////////////////
            //////////////////////////////Pilote////////////////////////////////////////
           ////////////////////////////////////////////////////////////////////////////
                try{
                    if (aventuriers.get("Gaspard").getRole()==roleAventuriers.pilote){
                        ((Pilote)aventuriers.get("Gaspard")).setVeutVoler(true);

                        if (premierClic){
                            
                            tuilesAutours = aventuriers.get("Gaspard").RecupererTuile(aventuriers.get("Gaspard").getEstSur(),grille);
                            aventuriers.get("Gaspard").Afficher(tuilesAutours);
                            vueP.setInformation(vueP.getInformation(),aventuriers.get("Gaspard").getAfficher(tuilesAutours));
                            setPremierClic(false);
                        }else { // on va entamer la procédure de deplacement du joueur sur les coordonnées entrées
                            if (!msg.texte.equals("")){ //si la case message à été remplie
                                String texte = msg.texte;
                                System.out.println("Deplacement en cours");
                                vueP.setInformation(vueP.getInformation(),"Deplacement en cours");
                                String[] positionString;
                                positionString = texte.split(","); //parsing des coordonnées
                                int[] position = new int[2];
                                position[0] = Integer.parseInt(positionString[0]); //ligne
                                position[1] = Integer.parseInt(positionString[1]); //colonne

                                if (!tuilesAutours.contains(grille.getTuiles()[position[0]][position[1]])){
                                    System.err.println("Deplacement impossible");
                                    vueP.setInformation(vueP.getInformation(),"Deplacement impossible");
                                    throw DeplacementImpossibleException;

                                }else{ //on finalise la procédure de déplacement

                                    aventuriers.get("Gaspard").seDeplacer(getGrille().getTuiles()[position[0]][position[1]]);
                                    System.out.println("Deplacement bien effectué, vous êtes maintenant en : " + aventuriers.get("Gaspard").getEstSur().getNumLigne()+ "," + aventuriers.get("Gaspard").getEstSur().getNumColonne());
                                    vueP.setInformation(vueP.getInformation(),"Deplacement bien effectué, vous êtes maintenant en : " + aventuriers.get("Gaspard").getEstSur().getNom());
                                    premierClic = true;
                                }  
                            }else {
                                System.err.println("Aucune position entrée");
                                vueP.setInformation(vueP.getInformation(),"Aucune position entrée");
                               throw AucunePositionEntreeException;
                            }
                        }
                    }
                } catch(Exception e){
                    System.err.println("Une erreur c'est produite merci de recommencer");
                    vueP.setInformation(vueP.getInformation(),"Une erreur c'est produite merci de recommencer");
                    setPremierClic(true);                    
                }
                
                 /////////////////////////////////////////////////////////////////////////////////
                ////////////////////////////////INGENIEUR////////////////////////////////////////
               /////////////////////////////////////////////////////////////////////////////////
                  if (aventuriers.get("Gaspard").getRole()==roleAventuriers.ingenieur){
                
                try {
                    if (premierClic){
                        tuilesAutoursNonSeches = aventuriers.get("Gaspard").AssecherTuile(aventuriers.get("Gaspard").getEstSur(),grille);
                        aventuriers.get("Gaspard").AfficherAssecher(tuilesAutoursNonSeches);
                        vueP.setInformation(vueP.getInformation(),aventuriers.get("Gaspard").getAfficherAssecher(tuilesAutoursNonSeches));
                        setPremierClic(false);
                    }else { //on entamme la procédure d'asséchement
                        System.out.println("Vous avez choisit de sécher une case : \n ");
                        vueP.setInformation(vueP.getInformation(),"Vous avez choisit de sécher une case : \n ");
                        if (!msg.texte.equals("")){ //si la case message à été remplie
                            String texte = msg.texte;

                            System.out.println("Assechement en cours");
                            vueP.setInformation(vueP.getInformation(),"Assechement en cours");
                            String[] positionString;
                            positionString = texte.split(","); //parsing des coordonnées
                            int[] position = new int[4];
                            position[0] = Integer.parseInt(positionString[0]); //ligne
                            position[1] = Integer.parseInt(positionString[1]); //colonne
                            position[2] = Integer.parseInt(positionString[2]);
                            position[3] = Integer.parseInt(positionString[3]);
                            
                            if(!tuilesAutoursNonSeches.contains(grille.getTuiles()[position[0]][position[1]]) || (!tuilesAutoursNonSeches.contains(grille.getTuiles()[position[2]][position[3]]))){
                                System.err.println("Assechement impossible");
                                vueP.setInformation(vueP.getInformation(),"Assechement impossible");
                                throw AssechementImpossibleException;
                            }else{ //on finalise la procédure d'asséchement
                                ((Ingenieur)aventuriers.get("Gaspard")).assecher2Tuiles(getGrille().getTuiles()[position[0]][position[1]],getGrille().getTuiles()[position[2]][position[3]]);
                                System.out.println("Asséchement bien effectué,la tuile : " + getGrille().getTuiles()[position[0]][position[1]].getNumLigne()+ ","
                                        + getGrille().getTuiles()[position[0]][position[1]].getNumColonne() + " est " + getGrille().getTuiles()[position[0]][position[1]].getStatut().toString());
                                 vueP.setInformation(vueP.getInformation(),"Assechement bien effectué, vous êtes maintenant en : " + aventuriers.get("Gaspard").getEstSur().getNom());
                                System.out.println("Asséchement bien effectué,la tuile : " + getGrille().getTuiles()[position[2]][position[3]].getNumLigne()+ ","
                                        + getGrille().getTuiles()[position[2]][position[3]].getNumColonne() + " est " + getGrille().getTuiles()[position[2]][position[3]].getStatut().toString());
                                premierClic = true;
                                 vueP.setInformation(vueP.getInformation(),"Assechement bien effectué, vous êtes maintenant en : " + aventuriers.get("Gaspard").getEstSur().getNom());
                            }

                        }else {
                            System.err.println("Aucune position entrée");
                            vueP.setInformation(vueP.getInformation(),"Aucune position entrée");
                            throw AucunePositionEntreeException;
                        }

                    }
                }catch(Exception e){
                    System.err.println("Une erreur c'est produite merci de recommencer");
                    vueP.setInformation(vueP.getInformation(),"Une erreur c'est produite merci de recommencer");
                    setPremierClic(true);
                }
      }
                //vue.setPosition("");
                //...
            break;
            case TERMINER:
                //vue.setPosition("Tour terminer, joueur suivant");
                choixFinTour = true;
            break;
        }
        
        if (aventuriers.get("Gaspard").getNbaction() > 3 || choixFinTour){
            System.out.println("fin du tour");
            vueP.setInformation(vueP.getInformation(),"fin du tour");
            //vue.close();
            aventuriers.get("Gaspard").setNbaction(1);
            System.exit(0);
        }
         System.out.println("Action n°"+ aventuriers.get("Gaspard").getNbaction());
    }

 
    
   //manipuler niveau d'eau
    public int getNiveauEau() {
        return niveauEau;
    }

    /*
     * methodes permettant de manipuler les cartes
     */
    //pioche de cartes Inondation
    public Stack<CarteInondation> getPiocheInondation() {
        return cartesInondationPioche;
    }

    //defausse cartes inondation
    public Stack<CarteInondation> getDefausseInondation() {
        return cartesDefausseInondation;
    }

    //pioche de cartes tirage(tresor et montee des eaux)
    public Stack<CarteTirage> getPiocheTirage() {
        return cartesTiragePioche;
    }

    //defausse de cartes tijoueursrage(tresor et montee des eaux)
    public Stack<CarteTirage> getDefausseTirage() {
        return cartesTirageDefausse;
    }

    //permet de melanger une collection de cartes
    public void melangerCartes(Stack pile) {
        Collections.shuffle(pile);
    }

    //liste des cartes inondation jouées
    public ArrayList<CarteInondation> getCartesInondationEnJeu() {
        return cartesInondationEnJeu;
    }

    //permet de piocher deux cartes inondation s'execute pour chaque debut de tour de chaque aventurier
    public void piocherCarteinondation(Stack<CarteInondation> cartesInondation) {
       
       
        //pour savoir combien de cartes inondation sont a piocher
        int nombrecartespiochées = 0;
        if (getNiveauEau() == 1 || getNiveauEau() == 2) {
            nombrecartespiochées = 2;
        }
        if (getNiveauEau() == 3 || getNiveauEau() == 4 || getNiveauEau() == 5) {
            nombrecartespiochées = 3;
        }
        if (getNiveauEau() == 6 || getNiveauEau() == 7) {
            nombrecartespiochées = 4;
        }
        if (getNiveauEau() == 8 || getNiveauEau() == 9) {
            nombrecartespiochées = 5;
        }
       
       
       
        for (int i = 0; i < nombrecartespiochées; i++) {

            getCartesInondationEnJeu().add(cartesInondation.pop());
            if (cartesInondation.isEmpty()) {
                Collections.shuffle(getDefausseInondation());
            }   
            for (CarteInondation carteInondation : getDefausseInondation()) {
                getPiocheInondation().add(carteInondation);
                getDefausseInondation().remove(carteInondation);
               
            }
        }
    }

    public void donnerCarte(CarteTresor carteTresor, Aventurier j2) {
        if (j2.getCartesMainAventurier().size() < 8) {
            j2.getCartesTresorMainAventurier().add(carteTresor);
            aventuriers.get(aventurierCourant).getCartesTresorMainAventurier().remove(carteTresor);
            aventuriers.get(aventurierCourant).getCartesMainAventurier().remove(carteTresor);
        } else {
            System.out.println("le joueur ne peut pas recevoir de carte, sa main est pleine.");
        }

    }

    public boolean partieTreminee() {
        boolean resultat = false;
        if(getNiveauEau() ==10){
            resultat = true;
        }
        return resultat;
    }
    public void initialiserCartesTirage(){
        for (int i = 0; i < 5; i++) {
            cartesTiragePioche.add(new CarteTirage(Utils.Tresor.PIERRE));
            cartesTiragePioche.add(new CarteTirage(Utils.Tresor.CALICE));
            cartesTiragePioche.add(new CarteTirage(Utils.Tresor.CRISTAL));
            cartesTiragePioche.add(new CarteTirage(Utils.Tresor.ZEPHYR));
        }
        cartesTiragePioche.add(new CarteTirage(typeCarte.carte_Montee_Des_Eaux));
        cartesTiragePioche.add(new CarteTirage(typeCarte.carte_Montee_Des_Eaux));
        Collections.shuffle(cartesTiragePioche);
       
    }
    public void piocherCarteTirage(Stack<CarteTirage> carteTirage){
        //si la main de l'aventurier n'est pas pleine
        if(aventuriers.get(aventurierCourant).getCartesMainAventurier().size()<8){
            CarteTirage nouvelleCarte = carteTirage.pop();
            aventuriers.get(aventurierCourant).getCartesMainAventurier().add(nouvelleCarte);
            if(nouvelleCarte.getType() == typeCarte.Carte_Tresor){
                aventuriers.get(aventurierCourant).getCartesTresorMainAventurier().add((CarteTresor) nouvelleCarte);
            }
            //alors on ajoute la carte de la pile à la main
            if (carteTirage.isEmpty()){
                Collections.shuffle(cartesTirageDefausse);
                for (CarteTirage carteTirage1 : cartesTirageDefausse) {
                    cartesTiragePioche.add(carteTirage1);                 
                }
            }
        }
        else{
            System.err.println("Votre main est pleine, la limte est de 9 cartes");
        }
    }
    

}