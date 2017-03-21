package com.waughrn2.ca4006.assignement1;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by iNfecteD on 21/03/2017.
 */
public class Parking {
    // plus prosaïquement, une boulangerie est une file d'attente de 1000 cases
    private BlockingQueue<Spot> queue =  new ArrayBlockingQueue<Spot>(1000) ;

    // on peut y déposer du pain, mais le boulanger n'est pas patient
    // si le panier de vente est plein, il s'en va
    public  boolean exit(Spot spot)  throws InterruptedException {
        return queue.offer(spot,  200, TimeUnit.MILLISECONDS) ;
    }

    // on peut en acheter, et le client n'est pas plus patient
    // que le boulanger
    public Spot entrance()  throws InterruptedException {
        return queue.poll(200, TimeUnit.MILLISECONDS) ;
    }

    // on peut interroger le stock
    public  int getStock() {
        return queue.size() ;
    }
}
