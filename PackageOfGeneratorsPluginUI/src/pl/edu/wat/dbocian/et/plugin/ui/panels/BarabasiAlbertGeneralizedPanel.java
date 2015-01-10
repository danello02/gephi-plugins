/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.edu.wat.dbocian.et.plugin.ui.panels;

import javax.sound.midi.MidiFileFormat;
import org.gephi.lib.validation.BetweenZeroAndOneValidator;
import org.gephi.lib.validation.Multiple4NumberValidator;
import org.gephi.lib.validation.PositiveNumberValidator;
import org.netbeans.validation.api.Problems;
import org.netbeans.validation.api.Validator;
import org.netbeans.validation.api.builtin.Validators;
import org.netbeans.validation.api.ui.ValidationGroup;
import org.netbeans.validation.api.ui.ValidationPanel;
import pl.edu.wat.dbocian.et.plugin.ui.validators.RealNumberValidator;

/**
 *
 * @author Daniel
 */
public class BarabasiAlbertGeneralizedPanel extends javax.swing.JPanel {

    /**
     * Creates new form BarabasiAlbertGeneralizedPanel
     */
    public BarabasiAlbertGeneralizedPanel() {
        initComponents();
    }
    
    public static ValidationPanel createValidationPanel(BarabasiAlbertGeneralizedPanel innerPanel) {
        ValidationPanel validationPanel = new ValidationPanel();
        if (innerPanel == null) {
            innerPanel = new BarabasiAlbertGeneralizedPanel();
        }
        validationPanel.setInnerComponent(innerPanel);
        
        ValidationGroup group = validationPanel.getValidationGroup();
        
        group.add(innerPanel.NField, Validators.REQUIRE_NON_EMPTY_STRING,
                new PositiveNumberValidator());
        group.add(innerPanel.m0Field, Validators.REQUIRE_NON_EMPTY_STRING,
                new PositiveNumberValidator());
        group.add(innerPanel.MField, Validators.REQUIRE_NON_EMPTY_STRING,
                new PositiveNumberValidator());
        group.add(innerPanel.MField, Validators.REQUIRE_NON_EMPTY_STRING,
                new MValidator(innerPanel));
        group.add(innerPanel.pField, Validators.REQUIRE_NON_EMPTY_STRING,
                new BetweenZeroAndOneValidator());
        group.add(innerPanel.pField, Validators.REQUIRE_NON_EMPTY_STRING,
                new pqValidator(innerPanel));
        group.add(innerPanel.qField, Validators.REQUIRE_NON_EMPTY_STRING,
                new BetweenZeroAndOneValidator());
        group.add(innerPanel.qField, Validators.REQUIRE_NON_EMPTY_STRING,
                new pqValidator(innerPanel));
        group.add(innerPanel.miField, Validators.REQUIRE_NON_EMPTY_STRING,
                new RealNumberValidator(innerPanel.miLabel.getText()));
        group.add(innerPanel.sigmaField, Validators.REQUIRE_NON_EMPTY_STRING,
                new RealNumberValidator(innerPanel.sigmaLabel.getText()));
        group.add(innerPanel.alphaField, Validators.REQUIRE_NON_EMPTY_STRING,
                new BetweenZeroAndOneValidator());
        group.add(innerPanel.betaField, Validators.REQUIRE_NON_EMPTY_STRING,
                new BetweenZeroAndOneValidator());
        
        return validationPanel;
    }
    
    public void showDataLabels() {
        miField.setVisible(passingBox.isSelected());
        miLabel.setVisible(passingBox.isSelected());
        sigmaField.setVisible(passingBox.isSelected());
        sigmaLabel.setVisible(passingBox.isSelected());
        
        alphaField.setVisible(acceleratedBox.isSelected());
        alphaLabel.setVisible(acceleratedBox.isSelected());
        betaField.setVisible(acceleratedBox.isSelected());
        betaLabel.setVisible(acceleratedBox.isSelected());       
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        NField = new javax.swing.JTextField();
        NLabel = new javax.swing.JLabel();
        m0Label = new javax.swing.JLabel();
        m0Field = new javax.swing.JTextField();
        MLabel = new javax.swing.JLabel();
        MField = new javax.swing.JTextField();
        pLabel = new javax.swing.JLabel();
        pField = new javax.swing.JTextField();
        qLabel = new javax.swing.JLabel();
        qField = new javax.swing.JTextField();
        passingBox = new javax.swing.JCheckBox();
        passingLabel = new javax.swing.JLabel();
        miLabel = new javax.swing.JLabel();
        miField = new javax.swing.JTextField();
        sigmaLabel = new javax.swing.JLabel();
        sigmaField = new javax.swing.JTextField();
        acceleratedRise = new javax.swing.JLabel();
        acceleratedBox = new javax.swing.JCheckBox();
        alphaLabel = new javax.swing.JLabel();
        alphaField = new javax.swing.JTextField();
        betaLabel = new javax.swing.JLabel();
        betaField = new javax.swing.JTextField();

        setPreferredSize(new java.awt.Dimension(230, 300));

        NField.setText(org.openide.util.NbBundle.getMessage(BarabasiAlbertGeneralizedPanel.class, "BarabasiAlbertGeneralizedPanel.NField.text")); // NOI18N

        NLabel.setLabelFor(NField);
        NLabel.setText(org.openide.util.NbBundle.getMessage(BarabasiAlbertGeneralizedPanel.class, "BarabasiAlbertGeneralizedPanel.NLabel.text")); // NOI18N

        m0Label.setLabelFor(m0Field);
        m0Label.setText(org.openide.util.NbBundle.getMessage(BarabasiAlbertGeneralizedPanel.class, "BarabasiAlbertGeneralizedPanel.m0Label.text")); // NOI18N

        m0Field.setText(org.openide.util.NbBundle.getMessage(BarabasiAlbertGeneralizedPanel.class, "BarabasiAlbertGeneralizedPanel.m0Field.text")); // NOI18N

        MLabel.setLabelFor(MField);
        MLabel.setText(org.openide.util.NbBundle.getMessage(BarabasiAlbertGeneralizedPanel.class, "BarabasiAlbertGeneralizedPanel.MLabel.text")); // NOI18N

        MField.setText(org.openide.util.NbBundle.getMessage(BarabasiAlbertGeneralizedPanel.class, "BarabasiAlbertGeneralizedPanel.MField.text")); // NOI18N

        pLabel.setLabelFor(pField);
        pLabel.setText(org.openide.util.NbBundle.getMessage(BarabasiAlbertGeneralizedPanel.class, "BarabasiAlbertGeneralizedPanel.pLabel.text")); // NOI18N

        pField.setText(org.openide.util.NbBundle.getMessage(BarabasiAlbertGeneralizedPanel.class, "BarabasiAlbertGeneralizedPanel.pField.text")); // NOI18N

        qLabel.setLabelFor(qField);
        qLabel.setText(org.openide.util.NbBundle.getMessage(BarabasiAlbertGeneralizedPanel.class, "BarabasiAlbertGeneralizedPanel.qLabel.text")); // NOI18N

        qField.setText(org.openide.util.NbBundle.getMessage(BarabasiAlbertGeneralizedPanel.class, "BarabasiAlbertGeneralizedPanel.qField.text")); // NOI18N

        passingBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                passingBoxActionPerformed(evt);
            }
        });

        passingLabel.setLabelFor(passingBox);
        passingLabel.setText(org.openide.util.NbBundle.getMessage(BarabasiAlbertGeneralizedPanel.class, "BarabasiAlbertGeneralizedPanel.passingLabel.text")); // NOI18N

        miLabel.setLabelFor(miField);
        miLabel.setText(org.openide.util.NbBundle.getMessage(BarabasiAlbertGeneralizedPanel.class, "BarabasiAlbertGeneralizedPanel.miLabel.text")); // NOI18N

        miField.setText(org.openide.util.NbBundle.getMessage(BarabasiAlbertGeneralizedPanel.class, "BarabasiAlbertGeneralizedPanel.miField.text")); // NOI18N

        sigmaLabel.setLabelFor(sigmaField);
        sigmaLabel.setText(org.openide.util.NbBundle.getMessage(BarabasiAlbertGeneralizedPanel.class, "BarabasiAlbertGeneralizedPanel.sigmaLabel.text")); // NOI18N

        sigmaField.setText(org.openide.util.NbBundle.getMessage(BarabasiAlbertGeneralizedPanel.class, "BarabasiAlbertGeneralizedPanel.sigmaField.text")); // NOI18N

        acceleratedRise.setText(org.openide.util.NbBundle.getMessage(BarabasiAlbertGeneralizedPanel.class, "BarabasiAlbertGeneralizedPanel.acceleratedRise.text")); // NOI18N

        acceleratedBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                acceleratedBoxActionPerformed(evt);
            }
        });

        alphaLabel.setLabelFor(alphaField);
        alphaLabel.setText(org.openide.util.NbBundle.getMessage(BarabasiAlbertGeneralizedPanel.class, "BarabasiAlbertGeneralizedPanel.alphaLabel.text")); // NOI18N

        alphaField.setText(org.openide.util.NbBundle.getMessage(BarabasiAlbertGeneralizedPanel.class, "BarabasiAlbertGeneralizedPanel.alphaField.text")); // NOI18N

        betaLabel.setLabelFor(betaField);
        betaLabel.setText(org.openide.util.NbBundle.getMessage(BarabasiAlbertGeneralizedPanel.class, "BarabasiAlbertGeneralizedPanel.betaLabel.text")); // NOI18N

        betaField.setText(org.openide.util.NbBundle.getMessage(BarabasiAlbertGeneralizedPanel.class, "BarabasiAlbertGeneralizedPanel.betaField.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(NLabel)
                    .addComponent(m0Label)
                    .addComponent(MLabel)
                    .addComponent(pLabel)
                    .addComponent(qLabel)
                    .addComponent(passingLabel)
                    .addComponent(miLabel)
                    .addComponent(sigmaLabel)
                    .addComponent(acceleratedRise)
                    .addComponent(alphaLabel)
                    .addComponent(betaLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 33, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(NField)
                    .addComponent(m0Field, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE)
                    .addComponent(MField)
                    .addComponent(pField)
                    .addComponent(qField)
                    .addComponent(passingBox)
                    .addComponent(miField)
                    .addComponent(sigmaField)
                    .addComponent(acceleratedBox)
                    .addComponent(alphaField)
                    .addComponent(betaField))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(NField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(NLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(m0Label)
                    .addComponent(m0Field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(MLabel)
                    .addComponent(MField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pLabel)
                    .addComponent(pField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(qLabel)
                    .addComponent(qField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(9, 9, 9)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(passingBox)
                    .addComponent(passingLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(miLabel)
                    .addComponent(miField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(sigmaLabel)
                    .addComponent(sigmaField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(acceleratedBox)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(alphaField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(alphaLabel)))
                    .addComponent(acceleratedRise))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(betaLabel)
                    .addComponent(betaField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(22, Short.MAX_VALUE))
        );

        NField.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(BarabasiAlbertGeneralizedPanel.class, "BarabasiAlbertGeneralizedPanel.NField.AccessibleContext.accessibleName")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents

    private void passingBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_passingBoxActionPerformed
        showDataLabels();
    }//GEN-LAST:event_passingBoxActionPerformed

    private void acceleratedBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_acceleratedBoxActionPerformed
        showDataLabels();
    }//GEN-LAST:event_acceleratedBoxActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JTextField MField;
    private javax.swing.JLabel MLabel;
    public javax.swing.JTextField NField;
    private javax.swing.JLabel NLabel;
    public javax.swing.JCheckBox acceleratedBox;
    private javax.swing.JLabel acceleratedRise;
    public javax.swing.JTextField alphaField;
    private javax.swing.JLabel alphaLabel;
    public javax.swing.JTextField betaField;
    private javax.swing.JLabel betaLabel;
    public javax.swing.JTextField m0Field;
    private javax.swing.JLabel m0Label;
    public javax.swing.JTextField miField;
    private javax.swing.JLabel miLabel;
    public javax.swing.JTextField pField;
    private javax.swing.JLabel pLabel;
    public javax.swing.JCheckBox passingBox;
    private javax.swing.JLabel passingLabel;
    public javax.swing.JTextField qField;
    private javax.swing.JLabel qLabel;
    public javax.swing.JTextField sigmaField;
    private javax.swing.JLabel sigmaLabel;
    // End of variables declaration//GEN-END:variables

    private static class MValidator implements Validator<String> {
        
        private final BarabasiAlbertGeneralizedPanel innerPanel;
        
        public MValidator(BarabasiAlbertGeneralizedPanel innerPanel) {
            this.innerPanel = innerPanel;
        }
        
        @Override
        public boolean validate(Problems problems, String compName, String model) {
            boolean result = false;
            
            try {
                Integer m0 = Integer.parseInt(innerPanel.m0Field.getText());
                Integer M = Integer.parseInt(innerPanel.MField.getText());
                result = M <= m0;
            } catch (NumberFormatException e) {
            }
            if (!result) {
                String message = "<html>M &lt;= m0</html>";
                problems.add(message);
            }
            
            return result;
        }
    }    
    
    private static class pqValidator implements Validator<String> {
        
        private final BarabasiAlbertGeneralizedPanel innerPanel;
        
        public pqValidator(BarabasiAlbertGeneralizedPanel innerPanel) {
            this.innerPanel = innerPanel;
        }
        
        @Override
        public boolean validate(Problems problems, String compName, String model) {
            boolean result = false;
            
            try {
                Double p = Double.parseDouble(innerPanel.pField.getText());
                Double q = Double.parseDouble(innerPanel.qField.getText());
                result = p + q < 1.0;
            } catch (NumberFormatException e) {
            }
            if (!result) {
                String message = "<html>p + q &lt; 1.0</html>";
                problems.add(message);
            }
            
            return result;
        }
    }
}
