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
import util.NomTuileEnum;

/**
 *
 * @author belhasss
 */
public class Controleur3 implements Observer {

    //Attributs Controleur2:
    private String aventurierCourant;
    //private final VueAventurier vue;
    private final VuePlateau vueP;
    private final HashMap<String, Aventurier> aventuriers;
    private Grille grille;
    private boolean premierClic;
    private ArrayList<Tuile> tuilesAutours;
    private ArrayList<Tuile> tuilesAutoursNonSeches;
    private boolean choixFinTour;
    private Observable observable;

    //attributs pour les cartes
    //les cartes tirage contiennent les tresors et la montee des eaux
    private Stack<CarteTirage> cartesTiragePioche = new Stack<>();
    private Stack<CarteTirage> cartesTirageDefausse = new Stack<>();

    //lees cartes innondations changent l'Etat des tuiles
    private Stack<CarteInondation> cartesInondationPioche = new Stack<>();
    private Stack<CarteInondation> cartesDefausseInondation = new Stack<>();

    int niveauEau;

    //constructeur
    public Controleur3() {

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
        aventuriers.put("Gaspard", new Pilote(getGrille().getTuiles()[4][3], false));
        //vue = new VueAventurier("Gaspard", aventuriers.get("Gaspard").getRole().getNom(), aventuriers.get("Gaspard").getRole().getPion().getCouleur());
        afficherGrilleConsole();
        this.vueP = new VuePlateau(aventuriers.get("Gaspard"), getGrille());

        getVueP().addObserver(this);

        vueP.Affiche();

        if (aventuriers.get("Gaspard").getNbaction() > 3 || choixFinTour) {
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

        Pilote pilote = new Pilote(getGrille().getTuiles()[2][1], false);

        Plongeur plongeur = new Plongeur(getGrille().getTuiles()[1][2]);

        ArrayList<Aventurier> listeaventuriersjouables = new ArrayList();
        listeaventuriersjouables.add(explorateur);
        listeaventuriersjouables.add(ingenieur);
        listeaventuriersjouables.add(messager);
        listeaventuriersjouables.add(navigateur);
        listeaventuriersjouables.add(pilote);
        listeaventuriersjouables.add(plongeur);
        Collections.shuffle((List<?>) listeaventuriersjouables); //melange des aventuriers

        for (int k = 0; k < 4; k++) { //comme il y a 4 joueurs on ajoute 4 aventuriers à notre collection aventuriers
            listeaventuriersjouables.get(k).setNom(joueurs.get(k)); // on donne un nom à l'aventurier
            getAventuriers().put(joueurs.get(k), listeaventuriersjouables.get(k)); // on l'ajoute à aventuriers
        }
    }

    public Tuile getPosition(Aventurier aventurier) {
        Tuile position = aventurier.getEstSur();
        return position;
    }

    public int getLigne(Tuile position) {
        int l = position.getNumLigne();
        return l;
    }

    public int getColonne(Tuile position) {
        int c = position.getNumColonne();
        return c;
    }

    public Grille getGrille() {
        return grille;
    }

    public void afficherGrilleConsole() {
        for (int i = 0; i < 6; i++) {
            for (int k = 0; k < 6; k++) {
                if (grille.getTuiles()[i][k] != null) {
                    System.out.print(" |" + i + "," + k + "| ");
                } else {
                    System.out.print(" |   | ");
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

    public void update(Observable o, Object arg) {
        Message msg = (Message) arg;
        Exception AucunePositionEntreeException = new Exception();
        Exception AssechementImpossibleException = new Exception();
        Exception DeplacementImpossibleException = new Exception();

        switch (msg.getCommande()) {

            case BOUGER:

                try {
                    if (aventuriers.get("Gaspard").getNbaction() > 3 || choixFinTour) {
                        //fermeture de la fenêtre et ouverture de celle du nouvel aventurier
                        vueP.close();
                    }
                    if (premierClic) {
                        tuilesAutours = aventuriers.get("Gaspard").RecupererTuile(aventuriers.get("Gaspard").getEstSur(), grille);
                        aventuriers.get("Gaspard").Afficher(tuilesAutours);
                        vueP.setInformation(vueP.getInformation(), aventuriers.get("Gaspard").getAfficher(tuilesAutours));
                        setPremierClic(false);
                    } else // on va entamer la procédure de deplacement du joueur sur les coordonnées entrées
                    if (!msg.texte.equals("")) { //si la case message à été remplie
                        String texte = msg.texte;

                        System.out.println("Deplacement en cours");
                        vueP.setInformation(vueP.getInformation(), "Deplacement en cours");

                        String[] positionString;
                        positionString = texte.split(","); //parsing des coordonnées
                        int[] position = new int[2];
                        position[0] = Integer.parseInt(positionString[0]); //ligne
                        position[1] = Integer.parseInt(positionString[1]); //colonne

                        if (!tuilesAutours.contains(grille.getTuiles()[position[0]][position[1]])) {
                            System.err.println("Deplacement impossible");
                            vueP.setInformation(vueP.getInformation(), "Deplacement impossible");
                            throw DeplacementImpossibleException;
                        } else { //on finalise la procédure de déplacement

                            aventuriers.get("Gaspard").seDeplacer(getGrille().getTuiles()[position[0]][position[1]]);
                            System.out.println("Deplacement bien effectué, vous êtes maintenant en : " + aventuriers.get("Gaspard").getEstSur().getNumLigne() + "," + aventuriers.get("Gaspard").getEstSur().getNumColonne());
                            vueP.setInformation(vueP.getInformation(), "Deplacement bien effectué, vous êtes maintenant sur la tuile : " + aventuriers.get("Gaspard").getEstSur().getNom());
                            premierClic = true;
                        }

                    } else {
                        System.err.println("Aucune position entrée");
                        vueP.setInformation(vueP.getInformation(), "Aucune position entrée");
                        throw AucunePositionEntreeException;
                    }
                } catch (Exception e) {
                    System.err.println("Une erreur c'est produite merci de recommencer");
                    vueP.setInformation(vueP.getInformation(), "Une erreur c'est produite merci de recommencer");
                    setPremierClic(true);
                }

                //vueP.setPosition(""); //clear de la zone de texte de la vue
                break;
            case ASSECHER:
                try {
                    if (premierClic) {
                        tuilesAutoursNonSeches = aventuriers.get("Gaspard").AssecherTuile(aventuriers.get("Gaspard").getEstSur(), grille);
                        vueP.setInformation(vueP.getInformation(), aventuriers.get("Gaspard").getAfficherAssecher(tuilesAutoursNonSeches));
                        aventuriers.get("Gaspard").AfficherAssecher(tuilesAutoursNonSeches);

                        setPremierClic(false);
                    } else { //on entamme la procédure d'asséchement
                        System.out.println("Vous avez choisit de sécher une case : \n ");
                        vueP.setInformation(vueP.getInformation(), "Vous avez choisit\nde sécher une case : \n ");
                        if (!msg.texte.equals("")) { //si la case message à été remplie
                            String texte = msg.texte;

                            System.out.println("Assechement en cours");
                            vueP.setInformation(vueP.getInformation(), "Assechement en cours");
                            String[] positionString;
                            positionString = texte.split(","); //parsing des coordonnées
                            int[] position = new int[2];
                            position[0] = Integer.parseInt(positionString[0]); //ligne
                            position[1] = Integer.parseInt(positionString[1]); //colonne

                            if (!tuilesAutoursNonSeches.contains(grille.getTuiles()[position[0]][position[1]])) {
                                System.err.println("Assechement impossible");
                                vueP.setInformation(vueP.getInformation(), "Assechement impossible");
                                throw AssechementImpossibleException;
                            } else { //on finalise la procédure d'asséchement

                                aventuriers.get("Gaspard").assecherTuile(getGrille().getTuiles()[position[0]][position[1]]);
                                System.out.println("Asséchement bien effectué,la tuile : " + getGrille().getTuiles()[position[0]][position[1]].getNumLigne() + ","
                                        + getGrille().getTuiles()[position[0]][position[1]].getNumColonne() + " est " + getGrille().getTuiles()[position[0]][position[1]].getStatut().toString());
                                premierClic = true;

                                vueP.setInformation(vueP.getInformation(), "Asséchement bien effectué,la tuile : " + getGrille().getTuiles()[position[0]][position[1]].getNumLigne() + ","
                                        + getGrille().getTuiles()[position[0]][position[1]].getNumColonne() + " est " + getGrille().getTuiles()[position[0]][position[1]].getStatut().toString());

                            }

                        } else {
                            System.err.println("Aucune position entrée");
                            vueP.setInformation(vueP.getInformation(), "Aucune position entrée");
                            throw AucunePositionEntreeException;
                        }

                        getVueP().getTuileGrille().updateGrille(grille);
                        getVueP().updateGrille(grille);

                    }
                } catch (Exception e) {
                    System.err.println("Une erreur c'est produite merci de recommencer");
                    vueP.setInformation(vueP.getInformation(), "Une erreur c'est produite merci de recommencer");
                    setPremierClic(true);
                }

                //vue.setPosition(""); //clear de la zone de texte de la vue
                break;
            case CAPACITE:

                ////////////////////////////////////////////////////////////////////////////
                //////////////////////////////Pilote////////////////////////////////////////
                ////////////////////////////////////////////////////////////////////////////
                try {
                    if (aventuriers.get("Gaspard").getRole() == roleAventuriers.pilote) {
                        ((Pilote) aventuriers.get("Gaspard")).setVeutVoler(true);

                        if (premierClic) {

                            tuilesAutours = aventuriers.get("Gaspard").RecupererTuile(aventuriers.get("Gaspard").getEstSur(), grille);
                            aventuriers.get("Gaspard").Afficher(tuilesAutours);
                            vueP.setInformation(vueP.getInformation(), aventuriers.get("Gaspard").getAfficher(tuilesAutours));
                            setPremierClic(false);
                        } else // on va entamer la procédure de deplacement du joueur sur les coordonnées entrées
                        if (!msg.texte.equals("")) { //si la case message à été remplie
                            String texte = msg.texte;
                            System.out.println("Deplacement en cours");
                            vueP.setInformation(vueP.getInformation(), "Deplacement en cours");
                            String[] positionString;
                            positionString = texte.split(","); //parsing des coordonnées
                            int[] position = new int[2];
                            position[0] = Integer.parseInt(positionString[0]); //ligne
                            position[1] = Integer.parseInt(positionString[1]); //colonne

                            if (!tuilesAutours.contains(grille.getTuiles()[position[0]][position[1]])) {
                                System.err.println("Deplacement impossible");
                                vueP.setInformation(vueP.getInformation(), "Deplacement impossible");
                                throw DeplacementImpossibleException;

                            } else { //on finalise la procédure de déplacement

                                aventuriers.get("Gaspard").seDeplacer(getGrille().getTuiles()[position[0]][position[1]]);
                                System.out.println("Deplacement bien effectué, vous êtes maintenant en : " + aventuriers.get("Gaspard").getEstSur().getNumLigne() + "," + aventuriers.get("Gaspard").getEstSur().getNumColonne());
                                vueP.setInformation(vueP.getInformation(), "Deplacement bien effectué, vous êtes maintenant en : " + aventuriers.get("Gaspard").getEstSur().getNom());
                                premierClic = true;
                            }
                        } else {
                            System.err.println("Aucune position entrée");
                            vueP.setInformation(vueP.getInformation(), "Aucune position entrée");
                            throw AucunePositionEntreeException;
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Une erreur c'est produite merci de recommencer");
                    vueP.setInformation(vueP.getInformation(), "Une erreur c'est produite merci de recommencer");
                    setPremierClic(true);
                }

                /////////////////////////////////////////////////////////////////////////////////
                ////////////////////////////////INGENIEUR////////////////////////////////////////
                /////////////////////////////////////////////////////////////////////////////////
                if (aventuriers.get("Gaspard").getRole() == roleAventuriers.ingenieur) {

                    try {
                        if (premierClic) {
                            tuilesAutoursNonSeches = aventuriers.get("Gaspard").AssecherTuile(aventuriers.get("Gaspard").getEstSur(), grille);
                            aventuriers.get("Gaspard").AfficherAssecher(tuilesAutoursNonSeches);
                            vueP.setInformation(vueP.getInformation(), aventuriers.get("Gaspard").getAfficherAssecher(tuilesAutoursNonSeches));
                            setPremierClic(false);
                        } else { //on entamme la procédure d'asséchement
                            System.out.println("Vous avez choisit de sécher une case : \n ");
                            vueP.setInformation(vueP.getInformation(), "Vous avez choisit de sécher une case : \n ");
                            if (!msg.texte.equals("")) { //si la case message à été remplie
                                String texte = msg.texte;

                                System.out.println("Assechement en cours");
                                vueP.setInformation(vueP.getInformation(), "Assechement en cours");
                                String[] positionString;
                                positionString = texte.split(","); //parsing des coordonnées
                                int[] position = new int[4];
                                position[0] = Integer.parseInt(positionString[0]); //ligne
                                position[1] = Integer.parseInt(positionString[1]); //colonne
                                position[2] = Integer.parseInt(positionString[2]);
                                position[3] = Integer.parseInt(positionString[3]);

                                if (!tuilesAutoursNonSeches.contains(grille.getTuiles()[position[0]][position[1]]) || (!tuilesAutoursNonSeches.contains(grille.getTuiles()[position[2]][position[3]]))) {
                                    System.err.println("Assechement impossible");
                                    vueP.setInformation(vueP.getInformation(), "Assechement impossible");
                                    throw AssechementImpossibleException;
                                } else { //on finalise la procédure d'asséchement
                                    ((Ingenieur) aventuriers.get("Gaspard")).assecher2Tuiles(getGrille().getTuiles()[position[0]][position[1]], getGrille().getTuiles()[position[2]][position[3]]);
                                    System.out.println("Asséchement bien effectué,la tuile : " + getGrille().getTuiles()[position[0]][position[1]].getNumLigne() + ","
                                            + getGrille().getTuiles()[position[0]][position[1]].getNumColonne() + " est " + getGrille().getTuiles()[position[0]][position[1]].getStatut().toString());
                                    vueP.setInformation(vueP.getInformation(), "Assechement bien effectué, vous êtes maintenant en : " + aventuriers.get("Gaspard").getEstSur().getNom());
                                    System.out.println("Asséchement bien effectué,la tuile : " + getGrille().getTuiles()[position[2]][position[3]].getNumLigne() + ","
                                            + getGrille().getTuiles()[position[2]][position[3]].getNumColonne() + " est " + getGrille().getTuiles()[position[2]][position[3]].getStatut().toString());
                                    premierClic = true;
                                    vueP.setInformation(vueP.getInformation(), "Assechement bien effectué, vous êtes maintenant en : " + aventuriers.get("Gaspard").getEstSur().getNom());
                                }

                            } else {
                                System.err.println("Aucune position entrée");
                                vueP.setInformation(vueP.getInformation(), "Aucune position entrée");
                                throw AucunePositionEntreeException;
                            }

                        }
                    } catch (Exception e) {
                        System.err.println("Une erreur c'est produite merci de recommencer");
                        vueP.setInformation(vueP.getInformation(), "Une erreur c'est produite merci de recommencer");
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

        if (aventuriers.get("Gaspard").getNbaction() > 3 || choixFinTour) {
            System.out.println("fin du tour");
            vueP.setInformation(vueP.getInformation(), "fin du tour");
            //vue.close();
            aventuriers.get("Gaspard").setNbaction(1);
            System.exit(0);
        }
        System.out.println("Action n°" + aventuriers.get("Gaspard").getNbaction());
        vueP.setInformation(vueP.getInformation(), "Action n°" + aventuriers.get("Gaspard").getNbaction());
    }

    public boolean partieTreminee() {
        boolean resultat = false;
        if (getNiveauEau() == 10) {
            resultat = true;
        }
        
        if (aventuriers.get(aventurierCourant).getTresors().size() == 4 && getGrille().getTuiles()[2][3].getPossede().size() == joueurs.size()){
            resultat = true;
        }
        if (){
            
        }
        return resultat;
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

        //puis on pioche
        for (int i = 0; i < nombrecartespiochées; i++) {
            CarteInondation carte = cartesInondation.pop();
            String nomCartepiochee = carte.getNomCarte().toString();
            for (int j = 0; j < 6; j++) {
                for (int k = 0; k < 6; k++) {
                    String nomTuile = getGrille().getTuiles()[j][k].getNom();
                    if(nomTuile == nomCartepiochee){
                        if (getGrille().getTuiles()[j][k].getStatut()== Utils.EtatTuile.ASSECHEE){
                            getGrille().getTuiles()[j][k].setStatut(Utils.EtatTuile.INONDEE);
                        }
                        if (getGrille().getTuiles()[j][k].getStatut()== Utils.EtatTuile.INONDEE){
                            getGrille().getTuiles()[j][k].setStatut(Utils.EtatTuile.COULEE);
                        }
                        if (getGrille().getTuiles()[j][k].getStatut()== Utils.EtatTuile.COULEE){
                            getGrille().getTuiles()[j][k].setStatut(Utils.EtatTuile.COULEE);
                        }
                    }
                    
                }
                
            }
            getDefausseInondation().add(carte);
            
            if (cartesInondation.isEmpty()) {
                melangerCartes(getDefausseInondation());
                for (CarteInondation carteInondation : getDefausseInondation()) {
                    getPiocheInondation().add(carteInondation);
                    getDefausseInondation().remove(carteInondation);

                }

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

    public void initialiserCartesTirage() {
        for (int i = 0; i < 5; i++) {
            cartesTiragePioche.push(new CarteTirage(Utils.Tresor.PIERRE));
            cartesTiragePioche.push(new CarteTirage(Utils.Tresor.CALICE));
            cartesTiragePioche.push(new CarteTirage(Utils.Tresor.CRISTAL));
            cartesTiragePioche.push(new CarteTirage(Utils.Tresor.ZEPHYR));
        }
        cartesTiragePioche.push(new CarteTirage(typeCarte.carte_Montee_Des_Eaux));
        cartesTiragePioche.push(new CarteTirage(typeCarte.carte_Montee_Des_Eaux));
        melangerCartes(cartesTiragePioche);

    }

    public void piocherCarteTirage(Stack<CarteTirage> carteTirage) {

        //si la main de l'aventurier n'est pas pleine
        if (aventuriers.get(aventurierCourant).getCartesMainAventurier().size() < 8) {
            CarteTirage nouvelleCarte = carteTirage.pop();//carte piochee
            aventuriers.get(aventurierCourant).getCartesMainAventurier().add(nouvelleCarte);//carte piochee s'ajoute à la main

            //si cette carte esr une carte tresor
            if (nouvelleCarte.getType() == typeCarte.Carte_Tresor) {
                //aventuriers.get(aventurierCourant).getCartesMainAventurier().add(nouvelleCarte);//sajoute à sa main de cartes tresor
                cartesTirageDefausse.push(nouvelleCarte);//puis la carte se defausse
            }

            //si cette carte est une carte montee des eaux((CarteTresor) nouvelleCarte)
            if (nouvelleCarte.getType() == typeCarte.carte_Montee_Des_Eaux) {
                niveauEau = getNiveauEau() + 1;//le niveau de l'eau monte
                cartesTirageDefausse.push(nouvelleCarte);//puis la carte se defausse
            }
            //si la pioche est vide
            if (carteTirage.isEmpty()) {
                melangerCartes(cartesTirageDefausse);
                for (CarteTirage carteTirage1 : cartesTirageDefausse) {
                    cartesTiragePioche.add(carteTirage1);//on melange la defausse puis on les ajoute à la pioche
                    cartesTirageDefausse.remove(carteTirage1);//puis on l'enleve de la defausse
                }
            }
        } //ou la main est pleine et l'action ne sera pas executée
        else {
            System.err.println("Votre main est pleine, la limte est de 9 cartes");
        }
    }

    //permet d'initialiser la pioche avec les valeur de chaque carte puis les melange
    public void initialiserCartesInondation() {

        cartesInondationPioche.push(new CarteInondation(NomTuileEnum.Heliport));
        cartesInondationPioche.push(new CarteInondation(NomTuileEnum.La_Caverne_Des_Ombres));
        cartesInondationPioche.push(new CarteInondation(NomTuileEnum.La_Caverne_Du_Brasier));
        cartesInondationPioche.push(new CarteInondation(NomTuileEnum.La_Foret_Pourpre));
        cartesInondationPioche.push(new CarteInondation(NomTuileEnum.La_Porte_D_Argent));
        cartesInondationPioche.push(new CarteInondation(NomTuileEnum.La_Porte_De_Bronze));
        cartesInondationPioche.push(new CarteInondation(NomTuileEnum.La_Porte_De_Cuivre));
        cartesInondationPioche.push(new CarteInondation(NomTuileEnum.La_Porte_De_Fer));
        cartesInondationPioche.push(new CarteInondation(NomTuileEnum.La_Porte_d_Or));
        cartesInondationPioche.push(new CarteInondation(NomTuileEnum.La_Tour_Du_Guet));
        cartesInondationPioche.push(new CarteInondation(NomTuileEnum.Le_Jardin_Des_Hurlements));
        cartesInondationPioche.push(new CarteInondation(NomTuileEnum.Le_Jardin_Des_Murmures));
        cartesInondationPioche.push(new CarteInondation(NomTuileEnum.Le_Lagon_Perdu));
        cartesInondationPioche.push(new CarteInondation(NomTuileEnum.Le_Marais_Brumeux));
        cartesInondationPioche.push(new CarteInondation(NomTuileEnum.Le_Palai_De_Corail));
        cartesInondationPioche.push(new CarteInondation(NomTuileEnum.Le_Palais_Des_Marees));
        cartesInondationPioche.push(new CarteInondation(NomTuileEnum.Le_Pont_Des_Abimes));
        cartesInondationPioche.push(new CarteInondation(NomTuileEnum.Le_Rocher_Fantome));
        cartesInondationPioche.push(new CarteInondation(NomTuileEnum.Le_Temple_De_La_Lune));
        cartesInondationPioche.push(new CarteInondation(NomTuileEnum.Le_Temple_Du_Soleil));
        cartesInondationPioche.push(new CarteInondation(NomTuileEnum.Le_Val_Du_Crepuscule));
        cartesInondationPioche.push(new CarteInondation(NomTuileEnum.Les_Dunes_De_L_Illusion));
        cartesInondationPioche.push(new CarteInondation(NomTuileEnum.Les_Falaises_De_L_Oubli));
        cartesInondationPioche.push(new CarteInondation(NomTuileEnum.Observatoire));
        melangerCartes(cartesInondationPioche);

    }

    //placeholder a supprimer plus tard
    public void test() {
        int c1 = 0;
        initialiserCartesTirage();
        System.out.println("affichage des cartes tirages");
        for (CarteTirage carte : getPiocheTirage()) {
            System.out.println(carte.getType().toString());
            c1++;
        }

        System.out.println(c1);
        System.out.println();
        System.out.println("pioche des cartes");
        aventurierCourant = "Gaspard";
        piocherCarteTirage(cartesTiragePioche);
        piocherCarteTirage(cartesTiragePioche);
        piocherCarteTirage(cartesTiragePioche);
        piocherCarteTirage(cartesTiragePioche);
        piocherCarteTirage(cartesTiragePioche);
        piocherCarteTirage(cartesTiragePioche);
        piocherCarteTirage(cartesTiragePioche);
        
        System.out.println("affichage de la pioche apres piochage");
        System.out.println();
        c1 = 0;
        for (CarteTirage carte : getPiocheTirage()) {
            System.out.println(carte.getType().toString());
            c1++;
        }
        System.out.println(c1);

        System.out.println();
        System.out.println("affichage de la defausse");
        for (CarteTirage tamer : cartesTirageDefausse) {
            System.out.println(tamer.getType().toString());
        }
        
        System.out.println("affichage de la main de l'aventurier");
        aventuriers.get(aventurierCourant).afficherCartesMain();
    }
}
