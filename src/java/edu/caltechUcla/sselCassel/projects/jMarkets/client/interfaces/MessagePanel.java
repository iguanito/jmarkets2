/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * MessagePanel.java
 *
 * Created on Apr 29, 2009, 12:09:04 PM
 */

package edu.caltechUcla.sselCassel.projects.jMarkets.client.interfaces;

import java.io.StringReader;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.html.HTMLEditorKit;

/**
 *
 * @author wmyuan
 */
public class MessagePanel extends javax.swing.JPanel {

    /** Creates new form MessagePanel */
    public MessagePanel() {
        initComponents();
        HTMLEditorKit editKit = new HTMLEditorKit();
        msgEP.setEditorKit((EditorKit)editKit);
    }

    protected void appendMsg(String msg){
        StringBuffer buffer = new StringBuffer();
        String openTag = MONITOR_OPEN_TAG;
        String fromAddr = "Monitor";

        buffer.append(openTag).append(fromAddr).append(CLOSE_TAG);
        buffer.append(": ").append(msg);

        try{
            HTMLEditorKit editKit = (HTMLEditorKit)msgEP.getEditorKit();
            editKit.read(new StringReader(buffer.toString()), msgEP.getDocument(), msgEP.getDocument().getLength());

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    protected void clearMsgWindow(){
        Document doc = msgEP.getDocument();
        try{
            doc.remove(0,  doc.getLength());
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        titleLabel = new javax.swing.JLabel();
        msgSP = new javax.swing.JScrollPane();
        msgEP = new javax.swing.JEditorPane();
        sendMsgSP = new javax.swing.JScrollPane();
        sendMsgEP = new javax.swing.JEditorPane();
        toListLabel = new javax.swing.JLabel();
        toListComboBox = new javax.swing.JComboBox();
        sendButton = new javax.swing.JButton();
        selectToButton = new javax.swing.JButton();

        titleLabel.setFont(new java.awt.Font("Tahoma", 1, 14));
        titleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titleLabel.setText("Message Board");

        msgEP.setBorder(javax.swing.BorderFactory.createTitledBorder("Message Window"));
        msgEP.setEditable(false);
        msgSP.setViewportView(msgEP);

        sendMsgEP.setEnabled(false);
        sendMsgSP.setViewportView(sendMsgEP);

        toListLabel.setFont(new java.awt.Font("Tahoma", 1, 11));
        toListLabel.setText("To List");
        toListLabel.setEnabled(false);

        toListComboBox.setEnabled(false);

        sendButton.setFont(new java.awt.Font("Tahoma", 1, 11));
        sendButton.setText("Send Message");
        sendButton.setEnabled(false);

        selectToButton.setFont(new java.awt.Font("Tahoma", 1, 11));
        selectToButton.setText("Select To List");
        selectToButton.setEnabled(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(toListLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(toListComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(selectToButton, javax.swing.GroupLayout.DEFAULT_SIZE, 109, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(sendButton))
                    .addComponent(msgSP, javax.swing.GroupLayout.DEFAULT_SIZE, 407, Short.MAX_VALUE)
                    .addComponent(titleLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 407, Short.MAX_VALUE)
                    .addComponent(sendMsgSP))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(titleLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(msgSP, javax.swing.GroupLayout.DEFAULT_SIZE, 184, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(sendMsgSP, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(toListLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sendButton)
                    .addComponent(toListComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(selectToButton))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JEditorPane msgEP;
    private javax.swing.JScrollPane msgSP;
    private javax.swing.JButton selectToButton;
    private javax.swing.JButton sendButton;
    private javax.swing.JEditorPane sendMsgEP;
    private javax.swing.JScrollPane sendMsgSP;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JComboBox toListComboBox;
    private javax.swing.JLabel toListLabel;
    // End of variables declaration//GEN-END:variables

    public static final String CLOSE_TAG="</b></font>";
    public static final String MONITOR_OPEN_TAG = "<font color=\"red\"><b>";

}
