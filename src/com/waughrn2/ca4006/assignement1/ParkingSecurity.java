package com.waughrn2.ca4006.assignement1;

/**
 * Created by iNfecteD on 21/03/2017.
 */
public class ParkingSecurity  implements Runnable {

    private Parking park = new Parking();

    public ParkingSecurity(Parking parking){
        this.park = parking;
    }
    public  void run() {

        try {
            while (true) {

                // toutes les secondes un boulanger produit un pain
                Thread.sleep(1000) ;
                boolean added =  this.park.exit(new Spot()) ;

                if (added) {
                    System.out.println("[" + Thread.currentThread().getName() +  "]" +
                            "[" +  this.park.getStock() +  "] je livre.") ;
                }  else {
                    System.out.println("[" + Thread.currentThread().getName() +  "]" +
                            "[" +  this.park.getStock() +  "] la boulangerie est pleine.") ;
                }
            }

        }  catch (InterruptedException e) {
            System.out.println("[" + Thread.currentThread().getName() +  "] je m'arrête") ;
        }
    }
}
