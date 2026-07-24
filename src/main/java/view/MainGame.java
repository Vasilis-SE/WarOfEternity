package view;

import utils.MusicConfiguration;
import item.model.ItemModel;
import item.controller.ItemController;
import map.controller.MapController;
import command.controller.CommandParserController;
import serialization.model.LoadedGameData;
import map.controller.DockYardController;
import characters.controller.BattleController;
import characters.controller.EnemyController;
import characters.controller.PlayerController;
import characters.controller.TransactionController;
import characters.enums.PlayerClassesEnum;
import characters.model.PlayerModel;
import characters.service.BattleService;
import characters.service.EnemyService;
import characters.service.PlayerService;
import org.vosk.Model;
import view.enums.MainGameMessagesEnum;
import voice.controller.VoiceRecognitionController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * GUI form for the main game. This form is the main game form (the form which
 * the player plays the game) and it consists player data such as weight and gold,
 * a message area which all the messages of the game are shown, a command area
 * which the user types the commands, an image area which changes for every area
 * that the user is at and a inventory which displays the items the player is 
 * carrying.
 * 
 * @author Vasilis Triantaris
 */
public class MainGame extends javax.swing.JFrame {
    
    private PlayerModel player;
    private String commandTyped; 

    private Model voskModel;
    
    private final MusicConfiguration musicConfiguration;
    private final PlayerController playerController;
    private final DockYardController dockYardController;
    private final TransactionController transactionController;
    private final BattleController battleController;
    private final EnemyController enemyController;
    private final VoiceRecognitionController voiceRecognitionController;

    private MapController mapController;
    private ItemController itemController;


    public MainGame(String playerName, boolean newGameProcess, LoadedGameData loadedGameData, PlayerClassesEnum playerClass) {
        initComponents();

        playerController = new PlayerController(null, null, new PlayerService());
        mapController = new MapController();
        musicConfiguration = new MusicConfiguration();
        voiceRecognitionController = new VoiceRecognitionController();

        if(newGameProcess)
            this.settingDataForNewGame(playerName, playerClass);
        else
            this.settingDataForExistingGame(loadedGameData);

        dockYardController = new DockYardController(mapController.getAreasList(), itemController.getListOfItems());
        dockYardController.dockYardMainControllingMethod();

        transactionController = new TransactionController(mapController.getAreasList());
        transactionController.setMerchantSectionDataControllingMethod();

        enemyController = new EnemyController(mapController.getAreasList(), new EnemyService());
        enemyController.loadEnemiesForGame();

        battleController = new BattleController(itemController.getListOfItems(), new PlayerService(), new BattleService(), enemyController);

        this.voskModel = voiceRecognitionController.loadVoiceRecognitionModel();

        //-------------- Setting Form Data ----------------
        this.setBasicComponentData();


        musicConfiguration.setSoundFilePath("outdoor1.wav");
        musicConfiguration.setMusicStatus(true);
        musicConfiguration.playSoundFile();
        
        //Event listener for the text field.
        jTextField1.addKeyListener(new KeyAdapter() {
                
               @Override
               public void keyPressed(KeyEvent e){
                   if(e.getKeyCode() == KeyEvent.VK_ENTER)
                       enterKeyIsPressed();
                   else if(e.getKeyCode() == KeyEvent.VK_UP && !commandTyped.isEmpty())
                       jTextField1.setText(commandTyped);
               }  
            }
        );
        
        //Event Listener that occurres whenever the ESC button is pressed 
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put( KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "Cancel"); 
        getRootPane().getActionMap().put("Cancel", new AbstractAction(){
            
            @Override
            public void actionPerformed(ActionEvent e){
                StartGUI sgui = new StartGUI(true, player, battleController, transactionController, mapController, itemController);
                sgui.setVisible(true);
            }
        });
        
    }

    private void enterKeyIsPressed() {
        final CommandParserController pc = new CommandParserController();

        player.setHealth(jProgressBar1.getValue());
        String textAreaContent = jTextArea1.getText();
        textAreaContent += "\n\n > "+jTextField1.getText();
        jTextArea1.setText(textAreaContent);
        commandTyped = jTextField1.getText();

        //Decide the parsing action from the verb of action command that the user typed
        String parsingDecision = pc.parserControllingMethodForActionDecision(jTextField1.getText());
        boolean basicCom = configureMaintenanceCommands(jTextField1.getText().trim());

        if(!basicCom){
            enemyController.loadEnemiesForGame();

            String actionResult = playerController.playerMainControllingMethodForActionDecision(player,
                    itemController.getListOfItems(), battleController, mapController.getAreasList(), dockYardController.getListOfDockYards(),
                    transactionController.getListOfMerchants(), musicConfiguration, parsingDecision, pc.getNounCommand(),
                    pc.getVerbCommand());

            //if the command that the user gave is invalid then ...
            if(actionResult.isEmpty()){
                textAreaContent += "\n\n"+parsingDecision;
                jTextArea1.setText(textAreaContent);
            } else{
                textAreaContent += "\n\n"+actionResult;
                jTextArea1.setText(textAreaContent);
            }
        }

        setImageAfterPlayerActionCommand();
        setPlayerDataAfterActionCommandExecuted();
        musicConfiguration.applyPostActionMusicChange(battleController.getBattleState());
        setEndingWindowWhenBossIsDead();
        //Clearing the text field after command submission
        jTextField1.setText("");
    }

    /**
     * Method that configures the basic maintenace commands that cannot be
     * initilized via text files.
     */
    private boolean configureMaintenanceCommands(String command){
        boolean status = false;

        switch(command){
            case "clear" :
            case "clean" :
                jTextArea1.setText("");
                status = true;
            break;
        }

        return status;
    }

    private void setImageAfterPlayerActionCommand(){
        //Tries to load the image file. If its does not succeed then it load another image
        try{
            ImageIcon icon = null; 
            if((!this.player.getLocation().getAreaImage().isEmpty()) && (!this.battleController.getBattleState()))
                icon = new ImageIcon(getClass().getResource("/AreaImages/" + this.player.getLocation().getAreaImage()));
            else if((this.player.getLocation().getAreaImage().isEmpty()) && (!this.battleController.getBattleState()))
                icon = new ImageIcon(getClass().getResource("/AreaImages/noImageAvailable.jpg"));
            else if (this.battleController.getBattleState())
                icon = new ImageIcon(getClass().getResource("/EnemyImages/" + playerController.getEnemyToBattle().getImage()));
            
            jLabel1.setIcon(icon);
        }
        catch(Exception ex){
            if(!battleController.getBattleState()){
                ImageIcon icon = new ImageIcon(getClass().getResource("/AreaImages/noImageAvailable.jpg"));
                jLabel1.setIcon(icon);
            }
        }
    }

    /**
     * Method that sets the basic data such as images on the form on form load.
     */
    private void setBasicComponentData(){
        String itemsSelected = "";

        jProgressBar1.setStringPainted(true);
        jProgressBar1.setForeground(Color.BLACK);

        jLabel2.setText(playerController.displayPlayerInventoryWeight(player));
        
        for(ItemModel eachItemInInventory : player.getInventory())
            itemsSelected += eachItemInInventory.getItemName()+"\n";
        jTextArea2.setText(itemsSelected);
        
        ImageIcon icon = new ImageIcon(getClass().getResource("/ApplicationImages/weight.png")); 
        jLabel3.setIcon(icon);
        
        jLabel5.setText(playerController.displayPlayerGold(player));
        icon = new ImageIcon(getClass().getResource("/ApplicationImages/goldCoin.png")); 
        jLabel4.setIcon(icon);
        
        jLabel9.setText(player.getDamage()+"");
        icon = new ImageIcon(getClass().getResource("/ApplicationImages/attackIcon.png"));
        jLabel7.setIcon(icon);
        
        jLabel10.setText(this.player.getArmor()+"");
        icon = new ImageIcon(getClass().getResource("/ApplicationImages/shieldIcon.png"));
        jLabel8.setIcon(icon);
        
        jLabel12.setText(this.player.getLocation().getAreaName());
        icon = new ImageIcon(getClass().getResource("/ApplicationImages/mapIcon.png"));
        jLabel11.setIcon(icon);
        
        jLabel14.setText(playerController.displayPlayerLevel(player));
        icon = new ImageIcon(getClass().getResource("/ApplicationImages/levelIcon.png"));
        jLabel13.setIcon(icon);
        
        icon = new ImageIcon(getClass().getResource("/ApplicationImages/strengthIcon.png"));
        jLabel16.setText(player.getStrength()+"");
        jLabel15.setIcon(icon);
        
        icon = new ImageIcon(getClass().getResource("/ApplicationImages/agilityIcon.png"));
        jLabel18.setText(player.getAgility()+"");
        jLabel17.setIcon(icon);
        
        icon = new ImageIcon(getClass().getResource("/ApplicationImages/intelligenceIcon.png"));
        jLabel20.setText(player.getIntelligence()+"");
        jLabel19.setIcon(icon);
        
        icon = new ImageIcon(getClass().getResource("/ApplicationImages/recognitionIcon.png"));
        jButton1.setIcon(icon);
        
        icon = new ImageIcon(getClass().getResource("/ApplicationImages/question.png"));
        jButton2.setIcon(icon);
        
        icon = new ImageIcon(getClass().getResource("/ApplicationImages/sound.png"));
        jButton3.setIcon(icon);
        
    }
    
    /**
     * Method that sets the data of the inventory.
     */
    private void setPlayerDataAfterActionCommandExecuted(){
        String itemsSelected = "";
        for(ItemModel eachItemInInventory : player.getInventory())
            itemsSelected += eachItemInInventory.getItemName()+"\n";

        itemController.restockDepletedConsumables();

        jTextArea2.setText(itemsSelected);
        jLabel2.setText(playerController.displayPlayerInventoryWeight(player));
        jLabel5.setText(playerController.displayPlayerGold(player));
        jLabel9.setText(String.valueOf(player.getDamage()));
        jLabel10.setText(String.valueOf(player.getArmor()));
        jLabel12.setText(player.getLocation().getAreaName());
        jLabel14.setText(playerController.displayPlayerLevel(player));
        jLabel16.setText(String.valueOf(player.getStrength()));
        jLabel18.setText(String.valueOf(player.getAgility()));
        jLabel20.setText(String.valueOf(player.getIntelligence()));
        jProgressBar1.setValue(player.getHealth());
        jProgressBar2.setValue(player.getExperience());
        
        if(playerController.isPlayerDead(player)){
            JOptionPane.showConfirmDialog(this, MainGameMessagesEnum.PLAYER_DIED_MESSAGE.getMessage(), MainGameMessagesEnum.PLAYER_DIED_TITLE.getMessage(), JOptionPane.OK_OPTION);
            this.musicConfiguration.stopMusic();
            this.voskModel = null;
            StartGUI sgui = new StartGUI(false, null, null, null, null, null);
            sgui.setVisible(true);
            this.setVisible(false);
        }
    }

    /**
     * Method that sets the data after new game has been made.
     */
    private void settingDataForNewGame(String playerName, PlayerClassesEnum playerClass){
        player = playerController.initNewPlayer(playerClass, playerName,
                this.mapController.getAreasList().getFirst());

        jTextArea1.setText(this.player.getLocation().getAreaDescription());
        
        //Sets at the begining of the application the image of the first area.
        ImageIcon icon = new ImageIcon(getClass().getResource("/AreaImages/" + this.player.getLocation().getAreaImage()));
        jLabel1.setIcon(icon);

        itemController = new ItemController(mapController.getAreasList());
        itemController.setItemDataForGame();
    }
    
    /**
     * Method that loads the data for an existing game.
     */
    private void settingDataForExistingGame(LoadedGameData loadedGameData){
        jTextArea1.setText("");

        mapController = loadedGameData.getMapController();
        this.player = loadedGameData.getPlayer();
        jTextArea1.setText(this.player.getLocation().getAreaDescription());

        //Sets at the image of the area that the user last saved to.
        ImageIcon icon = new ImageIcon(getClass().getResource("/AreaImages/" + this.player.getLocation().getAreaImage()));
        jLabel1.setIcon(icon);

        itemController = loadedGameData.getItemController();

        jProgressBar2.setValue(this.player.getExperience());
        jProgressBar1.setValue(this.player.getHealth());
    }
    
    @SuppressWarnings("unchecked")
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jTextField1 = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jToggleButton1 = new javax.swing.JToggleButton();
        jProgressBar2 = new javax.swing.JProgressBar();
        jButton1 = new javax.swing.JButton();
        jProgressBar1 = new javax.swing.JProgressBar();
        jLabel6 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("War Of Eternity");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jLabel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jTextArea1.setEditable(false);
        jTextArea1.setColumns(20);
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        jScrollPane3.setBorder(javax.swing.BorderFactory.createTitledBorder("Inventory"));

        jTextArea2.setEditable(false);
        jTextArea2.setColumns(20);
        jTextArea2.setLineWrap(true);
        jTextArea2.setRows(5);
        jScrollPane3.setViewportView(jTextArea2);

        jLabel2.setFont(new java.awt.Font("Tempus Sans ITC", 0, 14)); // NOI18N

        jLabel3.setToolTipText("Player Weight");

        jLabel4.setToolTipText("Player Gold");

        jLabel5.setFont(new java.awt.Font("Tempus Sans ITC", 0, 14)); // NOI18N

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setPreferredSize(new java.awt.Dimension(205, 92));

        jLabel7.setToolTipText("Player Damage");

        jLabel8.setToolTipText("Player Armor");

        jLabel9.setFont(new java.awt.Font("Tempus Sans ITC", 0, 14)); // NOI18N

        jLabel10.setFont(new java.awt.Font("Tempus Sans ITC", 0, 14)); // NOI18N

        jLabel11.setToolTipText("Areas Name");

        jLabel12.setFont(new java.awt.Font("Tempus Sans ITC", 0, 14)); // NOI18N

        jLabel14.setFont(new java.awt.Font("Tempus Sans ITC", 0, 14)); // NOI18N

        jLabel15.setText("jLabel15");
        jLabel15.setToolTipText("Strength Points");

        jLabel16.setFont(new java.awt.Font("Tempus Sans ITC", 0, 14)); // NOI18N

        jLabel17.setText("jLabel17");
        jLabel17.setToolTipText("Agility Points");

        jLabel18.setFont(new java.awt.Font("Tempus Sans ITC", 0, 14)); // NOI18N

        jLabel19.setText("jLabel19");
        jLabel19.setToolTipText("Intelligence Points");

        jLabel20.setFont(new java.awt.Font("Tempus Sans ITC", 0, 14)); // NOI18N

        jToggleButton1.setText("map");
        jToggleButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jToggleButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(11, 11, 11)
                .addComponent(jToggleButton1)
                .addContainerGap(15, Short.MAX_VALUE))
        );

        jProgressBar2.setToolTipText("Experience Bar");
        jProgressBar2.setPreferredSize(new java.awt.Dimension(146, 12));
        jProgressBar2.setRequestFocusEnabled(false);
        jProgressBar2.setString("Experience");

        jButton1.setText("Recognize");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jProgressBar1.setToolTipText("Health Bar");
        jProgressBar1.setValue(100);

        jLabel6.setText("Health Bar :");

        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 600, Short.MAX_VALUE)
                            .addComponent(jScrollPane1))
                        .addGap(24, 24, 24)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jButton1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton2))))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jProgressBar2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel6)
                            .addComponent(jProgressBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(38, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(13, 13, 13)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                            .addComponent(jScrollPane3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jProgressBar2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 354, Short.MAX_VALUE)
                    .addComponent(jScrollPane1))
                .addGap(8, 8, 8)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton2)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton1)
                        .addComponent(jButton3)))
                .addGap(26, 26, 26))
        );

        jButton2.getAccessibleContext().setAccessibleDescription("");

        pack();
        setLocationRelativeTo(null);
    }

    //Event on form close.
    private void formWindowClosing(java.awt.event.WindowEvent evt) {
        int confirmed = JOptionPane.showConfirmDialog(null,
            MainGameMessagesEnum.EXIT_CONFIRM_MESSAGE.getMessage(), MainGameMessagesEnum.EXIT_CONFIRM_TITLE.getMessage(),
            JOptionPane.YES_NO_OPTION);

        if (confirmed == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    //Event method that is triggered whenever the "Map" toggle button is used.
    private void jToggleButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton1ActionPerformed
        MapForm mf = new MapForm(mapController);

        if(jToggleButton1.isSelected()){
            mf.setVisible(true);
        }
        else{
            //Closes the form thats is classes name is "class main.java.View.View.MapForm"
            System.gc();
            for (Window window : Window.getWindows()) {
                if(window.getClass().toString().equals("class main.java.View.View.MapForm"))
                    window.dispose();
            }
        }
    }//GEN-LAST:event_jToggleButton1ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        if (this.voskModel == null) {
            JOptionPane.showMessageDialog(this,
                    MainGameMessagesEnum.VOICE_MODEL_NOT_FOUND.getMessage(),
                    MainGameMessagesEnum.RECOGNITION_UNAVAILABLE_TITLE.getMessage(), JOptionPane.ERROR_MESSAGE, null);
            return;
        }
        jButton1.setEnabled(false);
        new Thread(() -> {
            try {
                String recognized = voiceRecognitionController.recognizeSpeechFromMicrophone(voskModel);
                SwingUtilities.invokeLater(() -> {
                    jTextField1.setText(recognized);
                    jButton1.setEnabled(true);
                });
            } catch (Exception ex) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(MainGame.this,
                            MainGameMessagesEnum.MICROPHONE_ERROR.getMessage(), MainGameMessagesEnum.RECOGNITION_ERROR_TITLE.getMessage(),
                            JOptionPane.ERROR_MESSAGE);
                    jButton1.setEnabled(true);
                });
            }
        }).start();
    }//GEN-LAST:event_jButton1ActionPerformed

    //Help button
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        HelpForm hp = new HelpForm();
        hp.setVisible(true);
    }//GEN-LAST:event_jButton2ActionPerformed

    //Music button event.
    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        musicConfiguration.toggleMusicForCurrentState(battleController.getBattleState());

        String iconFile = musicConfiguration.getMusicStatus() ? "sound.png" : "nosound.png";
        jButton3.setIcon(new ImageIcon(getClass().getResource("/ApplicationImages/" + iconFile)));
    }//GEN-LAST:event_jButton3ActionPerformed

    private void setEndingWindowWhenBossIsDead(){

        if(playerController.hasPlayerReachedFinalArea(player) && !battleController.getBattleState()){
            JOptionPane.showMessageDialog(this,
                  MainGameMessagesEnum.ENDING_STORY.getMessage(), MainGameMessagesEnum.ENDING_TITLE.getMessage(), JOptionPane.OK_OPTION);
            this.musicConfiguration.stopMusic();
            this.voskModel = null;
            StartGUI sgui = new StartGUI(false, null, null, null, null, null);
            sgui.setVisible(true);
            this.setVisible(false);
        }
    }
 
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainGame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
               
                new MainGame("", true, null, null).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JProgressBar jProgressBar2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JToggleButton jToggleButton1;
    // End of variables declaration//GEN-END:variables
}
