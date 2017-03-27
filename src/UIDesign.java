import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Created by iNfecteD on 25/03/2017.
 */
public class UIDesign implements Runnable {
    /**
     * Simulations values, those are constants for initialisation
     */
    private static final int MAX_CAR            = 2000;
    private static final int MAX_SLOT           = 1000;
    private static final int SPEED              = 1;
    private static final int NB_ENTRANCE_EXIT   = 3;

    /**
     * Simulations values, needed if user change values
     */
    private int     maxCar          = 2000;
    private  int    maxSlot         = 1000;
    private int     queueSize       = 1;
    private boolean isFair          = true;
    private int     nbEntranceExit  = 3;

    /**
     * UI Main Components
     */
    private JFrame mFrame;
    private JPanel mGui;
    private JPanel mTopSidNav;

    /**
     * UI TopSideNav Components
     */
    private JComboBox   mNbCarChooser;
    private JComboBox   mNbSlotChooser;
    private JComboBox   mQueueSizeChooser;
    private JComboBox   mEntranceExitChooser;
    private JButton     resetValues;
    private JButton     stopSimulation;
    private JButton     startSimulation;
    private JCheckBox   setFair;

    /**
     * Dynamic displays components
     */
    private JPanel              printDynamic;

    private JPanel              printEntranceExit;

    private JSplitPane          splitPane;

    private JPanel              parkingDisplay;

    private JScrollPane         tableScroll;

    private DefaultTableModel   tableModel;

    /**
     * References on classes or main Elements
     */
    private CarParkManagement   mainApp;
    private UIDesign            thisGui;
    private Thread              mainThread;

    /**
     * Data linked to entrances
     */
    private int entranceCount                   = 0;
    private ArrayList<String[]> tablesEntrance  = new ArrayList<String[]>();
    private int displayedEntrance               = 0;


    /**
     * Default constructor
     */
    UIDesign(){
        thisGui = this;
    }


    /**
     * initMainJFrame
     */
    private void initFrame(){
        mFrame = new JFrame("CarParkManagement");
        mFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    /**
     * initMainJPanel
     */
    private void initJPanel(){
        mGui = new JPanel(new BorderLayout(6,6));
    }

    /**
     * Init TopSideNav
     */
    private void initTopSideNav(){
        mTopSidNav = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 4,4));
        mTopSidNav.setBorder(
                new TitledBorder("Configure and Launch Simulation") );
        initTopSideNavComponents();
    }

    /**
     * Init TopSideNav components and link them to TopSideNavBar
     */
    private void initTopSideNavComponents() {

        JLabel labelNbCars = initCarChooser();
        JLabel labelNbSlots = initSlotChooser();
        JLabel labelSpeed = initQueueSizeChooser();
        JLabel labelEntranceExit = initEntranceExitChooser();

        setFair = new JCheckBox("Use queue for Entrance/Exit", true);

        resetValues = new JButton("Reset Values");
        stopSimulation = new JButton("Stop Simulation");
        startSimulation = new JButton("Start Simulation");

        ActionListener mTopSideNavActionListener = createTopSideNavActionListener();
        mNbCarChooser.addActionListener(mTopSideNavActionListener);
        mNbSlotChooser.addActionListener(mTopSideNavActionListener);
        mQueueSizeChooser.addActionListener(mTopSideNavActionListener);
        mEntranceExitChooser.addActionListener(mTopSideNavActionListener);
        setFair.addActionListener(mTopSideNavActionListener);
        resetValues.addActionListener(mTopSideNavActionListener);
        stopSimulation.addActionListener(mTopSideNavActionListener);
        startSimulation.addActionListener(mTopSideNavActionListener);

        addTopSideComponents(labelNbCars, labelSpeed, labelNbSlots, labelEntranceExit);
    }


    /**
     * Init Car Chooser
     * @return JLabel (description of component)
     */
    //TODO Remove uncheck warning. Quick fix : supresswarning
    @SuppressWarnings("unchecked")
    private JLabel initCarChooser(){
        String[] numberCars = new String[10];
        for (int i = 0; i < 10; i++){
            numberCars[i] = Integer.toString(1000 * (i + 1));
        }

        JLabel labelNbCars = new JLabel("Select Maximum Cars:");
        mNbCarChooser = new JComboBox(numberCars);
        mNbCarChooser.setSelectedIndex(1);

        return labelNbCars;
    }

    /**
     * Init Slot Chooser
     * @return JLabel (description of component)
     */
    //TODO Remove uncheck warning. Quick fix : supresswarning
    @SuppressWarnings("unchecked")
    private JLabel initSlotChooser(){
        String[] availableSlots = new String[2];
        for (int i = 0; i < 2; i++){
            availableSlots[i] = Integer.toString(500 * (i + 1));
        }

        JLabel labelNbSlots = new JLabel("Select Available Slots:");
        mNbSlotChooser = new JComboBox(availableSlots);
        mNbSlotChooser.setSelectedIndex(1);

        return labelNbSlots;
    }

    /**
     * Init QueueSize Chooser
     * @return JLabel (description of component)
     */
    //TODO Remove uncheck warning. Quick fix : supresswarning
    @SuppressWarnings("unchecked")
    private JLabel initQueueSizeChooser(){
        String[] speed = new String[10];
        for (int a = 0; a < 10; a++){
            speed[a] = Integer.toString(500 * (a + 1));
        }

        JLabel labelSpeed = new JLabel("Select Maximum Queue Entrance/Exit:");
        mQueueSizeChooser = new JComboBox(speed);
        mQueueSizeChooser.setSelectedIndex(1);

        return labelSpeed;
    }

    /**
     * Init Entrance And Exit Chooser
     * @return JLabel (description of component)
     */
    //TODO Remove uncheck warning. Quick fix : supresswarning
    @SuppressWarnings("unchecked")
    private JLabel initEntranceExitChooser(){
        String[] entrance = new String[5];
        for (int a = 0; a < 5; a++){
            entrance[a] = Integer.toString(a + 1);
        }

        JLabel labelEntranceExit = new JLabel("Select Available Entrances/Exits:");
        mEntranceExitChooser = new JComboBox(entrance);
        mEntranceExitChooser.setSelectedIndex(2);

        return  labelEntranceExit;
    }

    /**
     * Add components to TopSideNav, take JLabels (description) of each component
     * @param labelNbCars
     * @param labelSpeed
     * @param labelNbSlots
     * @param labelEntranceExit
     */
    private void addTopSideComponents(JLabel labelNbCars, JLabel labelSpeed, JLabel labelNbSlots, JLabel labelEntranceExit){
        mTopSidNav.add(labelNbCars);
        mTopSidNav.add(mNbCarChooser);
        mTopSidNav.add(labelNbSlots);
        mTopSidNav.add(mNbSlotChooser);
        mTopSidNav.add(labelSpeed);
        mTopSidNav.add(mQueueSizeChooser);
        mTopSidNav.add(labelEntranceExit);
        mTopSidNav.add(mEntranceExitChooser);
        mTopSidNav.add(setFair);
        mTopSidNav.add(resetValues);
        mTopSidNav.add(startSimulation);
    }

    /**
     * Custom ActionListener for component of TopSideNav
     * @return ActionListener
     */
    private ActionListener createTopSideNavActionListener(){
        ActionListener topSideNavActionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object source = e.getSource();
                if(source == mNbCarChooser) {
                   setMaxCar((String) mNbCarChooser.getSelectedItem());
                } else if (source == mNbSlotChooser){
                    setMaxSlot((String) mNbSlotChooser.getSelectedItem());
                } else if (source == mQueueSizeChooser){
                    setQueueSize((String) mQueueSizeChooser.getSelectedItem());
                } else if (source == mEntranceExitChooser){
                    setNbEntranceExit((String) mEntranceExitChooser.getSelectedItem());
                } else if (source == setFair){
                    isFair = setFair.isSelected();
                } else if (source == resetValues){
                    if (mainApp != null){
                        JOptionPane.showConfirmDialog(startSimulation, "Simulation already running, you can't reset values");
                    } else {
                        resetAllValues();
                    }
                } else if (source == startSimulation){
                    if (mainApp == null) {
                        mainApp = new CarParkManagement(thisGui, maxCar, maxSlot, queueSize, isFair, nbEntranceExit);
                        mainThread = new Thread(mainApp);
                        mainThread.start();
                    } else {
                       JOptionPane.showConfirmDialog(startSimulation, "Simulation already running, please stop this one and restart app");
                    }
                }
            }
        };
        return  topSideNavActionListener;
    }

    /**
     * Called onClick button Reset Value. Well...it reset values of TopSideNav to default values :P
     */
    private void resetAllValues() {
        this.maxCar = MAX_CAR;
        this.maxSlot = MAX_SLOT;
        this.queueSize = SPEED;
        this.isFair = true;
        this.nbEntranceExit = NB_ENTRANCE_EXIT;

        mNbCarChooser.setSelectedIndex(1);
        mNbSlotChooser.setSelectedIndex(1);
        mQueueSizeChooser.setSelectedIndex(1);
        mEntranceExitChooser.setSelectedIndex(2);
        setFair.setSelected(true);
    }

    /**
     * Called on run. Init all components of UI. Sorry, I'll not have enough time to finish cleaning it...
     */
    public void run() {
        initFrame();
        initJPanel();
        initTopSideNav();

        mGui.add(mTopSidNav, BorderLayout.NORTH);

        printDynamic = new JPanel(new BorderLayout(5,5));
        printDynamic.setBorder(
                new TitledBorder("Current Entrances / Exits") );
        mGui.add(printDynamic, BorderLayout.WEST);

        printEntranceExit = new JPanel(new GridLayout(0,2,3,3));
        printEntranceExit.setBorder(
                new TitledBorder("") );

        printDynamic.add( new JScrollPane(printEntranceExit), BorderLayout.CENTER );

        initDataEntrances();


        String[] header = {"Name", "Value"};

        tableModel = new DefaultTableModel(header,0);

        String[] names = {"Name Entrance", "Total Capacity", "Actual Capacity", "Money won", "Number of Teacher in queue", "Number of Students in queue"};

        for (int ii=0; ii<names.length; ii++) {
            tableModel.addRow(new Object[]{names[ii],tablesEntrance.get(0)[ii]});
        }

        JTable table = new JTable(tableModel);
        try {
            table.setAutoCreateRowSorter(true);
        } catch(Exception continuewithNoSort) {
            continuewithNoSort.printStackTrace();
        }


        displayedEntrance = 0;

        tableScroll = new JScrollPane(table);
        Dimension tablePreferred = tableScroll.getPreferredSize();
        tableScroll.setPreferredSize(
                new Dimension(tablePreferred.width, tablePreferred.height/3) );

        createParkingDisplay(true);


        splitPane = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                tableScroll,
                new JScrollPane(parkingDisplay));
        mGui.add( splitPane, BorderLayout.CENTER );

        displayFrame();
    }

    public void createParkingDisplay(boolean init){
        parkingDisplay = new JPanel(new GridLayout(10, maxSlot/10));
        parkingDisplay.setBorder(
                new TitledBorder("Parking Display") );
        ParkingSlotDisplay[][] cell =  new ParkingSlotDisplay[10][maxSlot/10];
        for (int v = 0; v < 10; v++)
            for (int h = 0; h < maxSlot/10; h++)
                parkingDisplay.add(cell[v][h] = new ParkingSlotDisplay(v, h, Color.white));

    }

    private void displayFrame() {
        mFrame.setContentPane(mGui);

        mFrame.pack();

        mFrame.setLocationRelativeTo(null);
        try {
            mFrame.setLocationByPlatform(true);
            mFrame.setMinimumSize(mFrame.getSize());
        } catch(Throwable ignoreAndContinue) {
            ignoreAndContinue.printStackTrace();
        }

        mFrame.setVisible(true);
    }

    /**
     * Getter And setter for all default values
     */

    private void setMaxCar(String maxCar) {
        this.maxCar = Integer.valueOf(maxCar);
    }
    private void setMaxSlot(String maxSlot) {
        this.maxSlot = Integer.valueOf(maxSlot);
    }
    private void setQueueSize(String speed) {
        this.queueSize = Integer.valueOf(speed);
    }

    /**
     * Setter of value Entrance/Exit. Since this information need to be updated instantly in UI, recreate the views for entrances
     * @param nbEntranceExit
     */
    private void setNbEntranceExit(String nbEntranceExit) {
        this.nbEntranceExit = Integer.valueOf(nbEntranceExit);
        resetEntranceView();
        createEntranceView();
    }

    /**
     * Create one Entrance view
     */
    private void createEntranceView() {
        resetEntranceView();
        for(int g = 0; g < nbEntranceExit; g++){
            createEntrance();
            addDataEntrance(g);
        }
    }


    /**
     * Init list of data[] containing each entrance data[] with value's name :
     * entrance id, total capacity, remaining capacity, money won, null, null (null because not implemented,
     * but should be number of teacher and nb of students in queue
     */
    private void initDataEntrances(){
        for(int a = 0; a < nbEntranceExit;a++){
            String[] data = {"Entrance " + Integer.toString(a + 1), "1000", "1000","0.00€","0", "0"};
            tablesEntrance.add(data);
            createEntrance();
        }
    }

    /**
     * add data for freshly created entrance
     * @param index
     */
    private void addDataEntrance(int index){
        String[] data = {"Entrance " + Integer.toString(index + 1), "1000", "1000","0.00€","0", "0"};
        tablesEntrance.add(data);
    }

    /**
     * All next methods are views update
     */
    private void resetEntranceView(){
        for(int a = 0; a < printEntranceExit.getComponentCount(); a++){
            printEntranceExit.remove(0);
            tablesEntrance.remove(0);
            entranceCount = 0;

        }
        printEntranceExit.revalidate();
        printEntranceExit.repaint();
    }

    private void createEntrance(){
        JButton entranceButton =  new JButton("Entrance " + ++entranceCount);
        entranceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton button = (JButton) e.getSource();
                String text = button.getText();
                text = text.replace("Entrance ", "");
                int nbEntrance = Integer.valueOf(text);
                displayedEntrance = nbEntrance - 1;
                updateTableViewEntrance(tablesEntrance.get(nbEntrance - 1));
            }
        });
        printEntranceExit.add(entranceButton);
        mFrame.validate();
    }

    void updateParkingSlot(int index, boolean isStudent, boolean isA4x4User){
        ParkingSlotDisplay updatedComponent = (ParkingSlotDisplay ) parkingDisplay.getComponent(index);
        if (isA4x4User){
            updatedComponent.updateSlot(Color.red);
        } else if (isStudent){
            updatedComponent.updateSlot(Color.gray);
        } else {
            updatedComponent.updateSlot(Color.darkGray);
        }

        updatedComponent.revalidate();
        updatedComponent.repaint();
    }

    void restoreParkingSlot(int index){
        ParkingSlotDisplay updatedComponent = (ParkingSlotDisplay ) parkingDisplay.getComponent(index);
        updatedComponent.updateSlot(Color.white);
        updatedComponent.revalidate();
        updatedComponent.repaint();
    }

    void updateTableEntrance(int index, String[] data){
        tablesEntrance.set(index, data);
        if (index == displayedEntrance){
            updateTableViewEntrance(data);
        }
    }

    private void updateTableViewEntrance(String[] data){
        String[] names = {"Name Entrance", "Total Capacity", "Actual Capacity", "Money won", "Number of Teacher in queue", "Number of Students in queue"};
        int rowCount = tableModel.getRowCount();
        for (int ii=0; ii<rowCount; ii++) {
            tableModel.removeRow(0);
        }
        for (int ii=0; ii<names.length; ii++) {
            tableModel.addRow(new Object[]{names[ii],data[ii]});
        }
        tableScroll.revalidate();
        tableScroll.repaint();
    }

}
