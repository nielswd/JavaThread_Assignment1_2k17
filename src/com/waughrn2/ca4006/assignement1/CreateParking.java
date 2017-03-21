package com.waughrn2.ca4006.assignement1;

import java.util.Random;

/**
 * Created by iNfecteD on 21/03/2017.
 */
public class CreateParking {
    public  static  void main(String[] args) {

        // on initialise une boulangerie, et une variable aléatoire pour nos client
        final Parking boulangerie =  new Parking() ;
        final Random rand =  new Random() ;

        // notre boulanger est un runnable
        ParkingSecurity boulanger =  new ParkingSecurity(boulangerie) ;

        // notre mangeur est aussi un runnable
        StudentThread mangeur =  new StudentThread(boulangerie) ;

        Thread [] boulangers =  new Thread[1000] ;
        Thread [] mangeurs =  new Thread[2000] ;

        // préparation des boulangers
        for (int i =  0 ; i < boulangers.length ; i++) {
            boulangers[i] =  new Thread(boulanger) ;
        }

        // préparation des mangeurs
        for (int i =  0 ; i < mangeurs.length ; i++) {
            mangeurs[i] =  new Thread(mangeur) ;
        }

        // lancement des boulangers
        for (int i =  0 ; i < boulangers.length ; i++) {
            boulangers[i].start() ;
        }

        // lancement des mangeurs
        for (int i =  0 ; i < mangeurs.length ; i++) {
            mangeurs[i].start() ;
        }
    }
}
