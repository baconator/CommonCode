package com.github.sunnybat.error;

import java.awt.datatransfer.*;
import java.awt.Toolkit;

/**
 *
 * @author SunnyBat
 */
public class ErrorWindow extends javax.swing.JFrame {

  private final Throwable myError;

  /**
   * Creates a new ErrorForm. Note that this does not
   */
  protected ErrorWindow() {
    this(null);
  }

  protected ErrorWindow(Throwable error) {
    myError = error;
    initComponents();
    JBError.setEnabled(myError != null);
  }

  protected void showWindow() {
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        setVisible(true);
      }
    });
  }

  public void setTitleText(final String text) {
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        setTitle(text);
      }
    });
  }

  public void setErrorText(final String text) {
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        JLTitle.setText(text);
      }
    });
  }

  public void setInformationText(final String text) {
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        JTAError.setText(text);
        JTAError.setCaretPosition(0);
      }
    });
  }

  public void setExtraButtonText(final String text) {
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        JBError.setText(text);
      }
    });
  }

  public void setExtraButtonEnabled(final boolean enabled) {
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        JBError.setEnabled(enabled);
      }
    });
  }

  public void setLineWrap(final boolean lineWrap) {
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        JTAError.setLineWrap(lineWrap);
      }
    });
  }

  @Override
  public void dispose() {
    ErrorDisplay.errWindowClosed();
    super.dispose();
  }

  /**
   * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always
   * regenerated by the Form Editor.
   */
  @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    JLTitle = new javax.swing.JLabel();
    JBClose = new javax.swing.JButton();
    JSPError = new javax.swing.JScrollPane();
    jScrollPane1 = new javax.swing.JScrollPane();
    JTAError = new javax.swing.JTextArea();
    JTAError.setWrapStyleWord(true);
    JBError = new javax.swing.JButton();

    setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
    setTitle("Error Information");
    setAlwaysOnTop(true);
    setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    setResizable(false);

    JLTitle.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
    JLTitle.setForeground(new java.awt.Color(255, 0, 0));
    JLTitle.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    JLTitle.setText("ERROR");
    JLTitle.setMaximumSize(new java.awt.Dimension(730, 29));

    JBClose.setText("Close Window");
    JBClose.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        JBCloseActionPerformed(evt);
      }
    });

    JSPError.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

    jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    jScrollPane1.setMaximumSize(new java.awt.Dimension(730, 172));

    JTAError.setEditable(false);
    JTAError.setColumns(20);
    JTAError.setFont(new java.awt.Font("Monospaced", 0, 18)); // NOI18N
    JTAError.setForeground(new java.awt.Color(255, 0, 0));
    JTAError.setLineWrap(true);
    JTAError.setRows(3);
    JTAError.setText("Error Message Here");
    jScrollPane1.setViewportView(JTAError);

    JSPError.setViewportView(jScrollPane1);

    JBError.setText("Error Information");
    JBError.setToolTipText("");
    JBError.setEnabled(false);
    JBError.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        JBErrorActionPerformed(evt);
      }
    });

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
          .addComponent(JSPError)
          .addGroup(layout.createSequentialGroup()
            .addGap(0, 0, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
              .addComponent(JLTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 730, javax.swing.GroupLayout.PREFERRED_SIZE)
              .addGroup(layout.createSequentialGroup()
                .addComponent(JBClose, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(114, 114, 114)
                .addComponent(JBError)))))
        .addContainerGap())
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addComponent(JLTitle, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(JSPError, javax.swing.GroupLayout.DEFAULT_SIZE, 172, Short.MAX_VALUE)
        .addGap(18, 18, 18)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(JBClose, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(JBError))
        .addContainerGap())
    );

    setSize(new java.awt.Dimension(766, 308));
    setLocationRelativeTo(null);
  }// </editor-fold>//GEN-END:initComponents

  private void JBCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_JBCloseActionPerformed
    // TODO add your handling code here:
    dispose();
  }//GEN-LAST:event_JBCloseActionPerformed

  private void JBErrorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_JBErrorActionPerformed
    // TODO add your handling code here:
    if (myError == null) {
      StringSelection stringSelection = new StringSelection(JTAError.getText());
      Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
      clpbrd.setContents(stringSelection, null);
    } else {
      ErrorDisplay.detailedReport(myError);
    }
  }//GEN-LAST:event_JBErrorActionPerformed

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton JBClose;
  private javax.swing.JButton JBError;
  private javax.swing.JLabel JLTitle;
  public javax.swing.JScrollPane JSPError;
  private javax.swing.JTextArea JTAError;
  private javax.swing.JScrollPane jScrollPane1;
  // End of variables declaration//GEN-END:variables
}