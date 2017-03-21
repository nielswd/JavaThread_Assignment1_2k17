package com.waughrn2.ca4006.assignement1;

import java.util.Random;

/**
 * Created by iNfecteD on 21/03/2017.
 */
public class StudentThread implements Runnable {

    private Parking park = new Parking();

    public StudentThread(Parking parking){
        this.park = parking;
    }

    public  void run() {

        try {

            while (true) {
                // nos mangeurs mangent de façon aléatoire...
                Random rand = new Random();
                Thread.sleep(rand.nextInt(1000)) ;
                Spot pain = this.park.entrance() ;
                if (pain != null) {
                    System.out.println("[" + Thread.currentThread().getName() +  "]" +
                            "[" + this.park.getStock() +  "] miam miam") ;
                }  else {
                    System.out.println("[" + Thread.currentThread().getName() +  "]" +
                            "[" +this.park.getStock() +  "] j'ai faim") ;
                }
            }

        }  catch (InterruptedException e) {
            System.out.println("[" + Thread.currentThread().getName() +  "] je m'arrête") ;
        }
    }
}
