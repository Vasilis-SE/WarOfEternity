package view;

import serialization.controller.SaveLoadController;
import characters.enums.PlayerClassesEnum;
import view.enums.NewGameMessagesEnum;

import java.awt.Window;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.UnsupportedLookAndFeelException;

public class NewGameForm extends javax.swing.JFrame {

    private final ButtonGroup group;
    private PlayerClassesEnum playerClass;

    private final SaveLoadController saveLoadController = new SaveLoadController();

    public NewGameForm() {
        initComponents();

        //Makes the three radio button a group
        this.group = new ButtonGroup();
        this.group.add(jRadioButton1);
        this.group.add(jRadioButton2);
        this.group.add(jRadioButton3);

        this.setFormDataIcons();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jCheckBox1 = new javax.swing.JCheckBox();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jRadioButton3 = new javax.swing.JRadioButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("War Of Eternity : New Game");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Adobe Garamond Pro", 0, 15)); // NOI18N
        jLabel1.setText("Enter Player Name : ");

        jTextArea1.setEditable(false);
        jTextArea1.setColumns(20);
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        jTextArea1.setText(NewGameMessagesEnum.TERMS_AND_CONDITIONS.getMessage());
        jScrollPane1.setViewportView(jTextArea1);

        jCheckBox1.setFont(new java.awt.Font("Adobe Garamond Pro", 0, 15)); // NOI18N
        jCheckBox1.setText("I agree with the terms and conditions");

        jButton1.setFont(new java.awt.Font("Adobe Garamond Pro", 0, 16)); // NOI18N
        jButton1.setText("Start Game");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setFont(new java.awt.Font("Adobe Garamond Pro", 0, 16)); // NOI18N
        jButton2.setText("< Back");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jRadioButton1.setText("Warrior");
        jRadioButton1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jRadioButton1MouseClicked(evt);
            }
        });

        jRadioButton2.setText("Rogue");
        jRadioButton2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jRadioButton2MouseClicked(evt);
            }
        });

        jRadioButton3.setText("Mage");
        jRadioButton3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jRadioButton3MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jCheckBox1)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 624, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton1)
                        .addGap(86, 86, 86)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jRadioButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jRadioButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jRadioButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(20, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jRadioButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 11, Short.MAX_VALUE)
                        .addComponent(jRadioButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addComponent(jRadioButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jCheckBox1)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton1)
                            .addComponent(jButton2)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 376, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(25, 25, 25))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    //Method for button "< Back"
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        StartGUI sgui = new StartGUI(false, null, null, null, null, null);
        sgui.setVisible(true);
        this.setVisible(false);
    }//GEN-LAST:event_jButton2ActionPerformed

    //Method for button "Start Game"
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        if(!jCheckBox1.isSelected()) {
            JOptionPane.showMessageDialog(this, NewGameMessagesEnum.TERMS_NOT_AGREED.getMessage(),
                    NewGameMessagesEnum.MESSAGE_PROMPT_TITLE.getMessage(), JOptionPane.OK_OPTION);
            return;
        }

        String playerName = jTextField1.getText().trim();
        if(playerName.isEmpty()){
            JOptionPane.showMessageDialog(this, NewGameMessagesEnum.NAME_REQUIRED.getMessage(),
                    NewGameMessagesEnum.MESSAGE_PROMPT_TITLE.getMessage(), JOptionPane.OK_OPTION);
            return;
        }

        if(playerClass == null){
            JOptionPane.showMessageDialog(this, NewGameMessagesEnum.CLASS_REQUIRED.getMessage(),
                    NewGameMessagesEnum.MESSAGE_PROMPT_TITLE.getMessage(), JOptionPane.OK_OPTION);
            return;
        }

        if(saveLoadController.playerNameExistsAsASaveFile(playerName)){
            JOptionPane.showMessageDialog(this, NewGameMessagesEnum.NAME_TAKEN.getMessage(),
                    NewGameMessagesEnum.MESSAGE_PROMPT_TITLE.getMessage(), JOptionPane.OK_OPTION);
            return;
        }

        MainGame mg = new MainGame(playerName, true, null, playerClass);

        //Closes all window forms
        System.gc();
        for (Window window : Window.getWindows()) {
            window.dispose();
        }

        JOptionPane.showMessageDialog(null,
          NewGameMessagesEnum.INTRO_STORY.getMessage(), NewGameMessagesEnum.INTRO_STORY_TITLE.getMessage(), WIDTH, null);

        mg.setVisible(true);

    }//GEN-LAST:event_jButton1ActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        StartGUI sgui = new StartGUI(false, null, null, null, null, null);
        sgui.setVisible(true);
        this.setVisible(false);
    }//GEN-LAST:event_formWindowClosing

    private void jRadioButton1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jRadioButton1MouseClicked
        jRadioButton1.setText("<html><font color=\"green\"><b>Warrior</b></font></html>");
        jRadioButton2.setText("<html><font color=\"black\">Rogue</font></html>");
        jRadioButton3.setText("<html><font color=\"black\">Mage</font></html>");
        
        this.playerClass = PlayerClassesEnum.WARRIOR;
    }//GEN-LAST:event_jRadioButton1MouseClicked

    private void jRadioButton2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jRadioButton2MouseClicked
        jRadioButton2.setText("<html><font color=\"green\"><b>Rogue</b></font></html>");
        jRadioButton3.setText("<html><font color=\"black\">Mage</font></html>");
        jRadioButton1.setText("<html><font color=\"black\">Warrior</font></html>");
        
        this.playerClass = PlayerClassesEnum.ROGUE;
    }//GEN-LAST:event_jRadioButton2MouseClicked

    private void jRadioButton3MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jRadioButton3MouseClicked
        jRadioButton3.setText("<html><font color=\"green\"><b>Mage</b></font></html>");
        jRadioButton1.setText("<html><font color=\"black\">Warrior</font></html>");
        jRadioButton2.setText("<html><font color=\"black\">Rogue</font></html>");
        
        this.playerClass = PlayerClassesEnum.MAGE;
    }//GEN-LAST:event_jRadioButton3MouseClicked

    private void setFormDataIcons(){
    
        ImageIcon icon = new ImageIcon(getClass().getResource("/ApplicationImages/warrioricon.png"));
        jRadioButton1.setIcon(icon);

        icon = new ImageIcon(getClass().getResource("/ApplicationImages/rogue.png"));
        jRadioButton2.setIcon(icon);

        icon = new ImageIcon(getClass().getResource("/ApplicationImages/mageicon.png"));
        jRadioButton3.setIcon(icon);

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
            java.util.logging.Logger.getLogger(NewGameForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new NewGameForm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JRadioButton jRadioButton3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}
