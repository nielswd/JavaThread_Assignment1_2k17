package com.waughrn2.ca4006.graphics;

import com.waughrn2.ca4006.MainProg;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class UIMain extends JFrame implements ActionListener{
    private MainProg mainApp;

    private JButton configureSimulation;
    private JButton startSimulation;


    public UIMain() {
        initUI();
    }

    private void initUI() {

        setTitle("CarPark Management 2k17");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setContentPane(buildContentPane());
        pack();
    }

    private JPanel buildContentPane(){
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.setBorder(new EmptyBorder(new Insets(40, 60, 40, 60)));

        configureSimulation = new JButton("Configure simulation");
        configureSimulation.addActionListener(this);
        startSimulation = new JButton("Start simulation");
        startSimulation.addActionListener(this);
        panel.add(configureSimulation);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(startSimulation);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        return panel;
    }


    private JPanel buildConfigurePane(){
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.setBorder(new EmptyBorder(new Insets(40, 60, 40, 60)));

        configureSimulation = new JButton("HOLA");
        configureSimulation.addActionListener(this);
        startSimulation = new JButton("AMIGOS");
        startSimulation.addActionListener(this);
        panel.add(configureSimulation);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(startSimulation);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));

        return panel;
    }

    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        System.out.println("Vous avez cliqué.");
        if(source == configureSimulation){
            JPanel configureMenu = buildConfigurePane();

            System.out.println("Vous avez cliqué ici.");
        } else if(source == startSimulation){
//            mainApp = new MainProg(this);
//            Thread t = new Thread(mainApp);
//            t.start();
            System.out.println("Vous avez cliqué là.");
        }
    }

        public static void main(String[] args){
        UIMain fenetre = new UIMain();
        fenetre.setVisible(true);
    }
}
