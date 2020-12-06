/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.view;

import model.NguoiChoi;
import model.ToaDo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author VanAnh
 */
public class GameView extends javax.swing.JFrame {

    private final NguoiChoi nguoichoi;
    private char piece;
    private char opPiece;

    /**
     * Creates new form TableCaro
     */

    public void setTime(int time) {
        this.jLabel2.setText(((Integer) time).toString() + "s");
    }

    public int getTime() {
        return Integer.parseInt(this.jLabel2.getText().substring(0, this.jLabel2.getText().length() - 1));
    }

    public GameView(NguoiChoi nguoichoi) {
        initComponents();
        this.nguoichoi = nguoichoi;
        DefaultTableModel df = (DefaultTableModel) tblBanCo.getModel();
        df.setColumnCount(100);
        df.setNumRows(100);
        tblBanCo.setShowGrid(true);
        tblBanCo.setTableHeader(null);
        for (int i = 0; i < tblBanCo.getColumnCount(); i++) {
            tblBanCo.getColumnModel().getColumn(i).setMinWidth(20);
            tblBanCo.getColumnModel().getColumn(i).setMaxWidth(20);
            tblBanCo.getColumnModel().getColumn(0).setPreferredWidth(20);
        }
        jScrollPane1.setColumnHeader(null);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tblBanCo = new javax.swing.JTable();
        btnThoat = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel1.setText("Thời gian còn lại:");

        jLabel2.setText("0s");


        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Chơi game");

        tblBanCo.setModel(new javax.swing.table.DefaultTableModel(
                new Object [][] {
                        {},
                        {},
                        {},
                        {}
                },
                new String [] {

                }
        ));
        jScrollPane1.setViewportView(tblBanCo);
        btnThoat.setText("Thoát");
        btnThoat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 782, Short.MAX_VALUE)
                                .addContainerGap())
                        .addGroup(layout.createSequentialGroup()
                                .addGap(32, 32, 32)
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel2)
                                .addGap(204, 204, 204)
                                .addComponent(btnThoat, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 503, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(btnThoat, javax.swing.GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE)
                                        .addComponent(jLabel1)
                                        .addComponent(jLabel2))
                                .addContainerGap())
        );

        pack();
    }// </editor-fold>

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
    }


    public NguoiChoi getNguoichoi() {
        return nguoichoi;
    }

    public int showConfirmDialog(String message) {
        return JOptionPane.showConfirmDialog(null, message, "", JOptionPane.YES_NO_OPTION);
    }

    public void showMessageDialog(String message) {
        JOptionPane.showMessageDialog(null, message);
    }

    // Variables declaration - do not modify                     
    private javax.swing.JButton btnThoat;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tblBanCo;

    public void playerMove(ToaDo toaDo) {
        this.tblBanCo.setValueAt(piece, toaDo.getY(), toaDo.getX());
    }

    public void oponentMove(ToaDo toaDo) {
        this.tblBanCo.setValueAt(opPiece, toaDo.getY(), toaDo.getX());
    }

    public JTable getJTable() {
        return this.tblBanCo;
    }

    public char getPiece() {
        return piece;
    }

    public void addQuitButtonListener(java.awt.event.ActionListener actionListener) {
        btnThoat.addActionListener(actionListener);
    }
    public void setPiece(char piece) {
        this.piece = piece;
        this.opPiece = piece == 'x' ? 'o' : 'x';
    }
    // End of variables declaration                   
}
