/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.daniel.generator.bamodel.ageing;

import org.gephi.lib.validation.BetweenZeroAndOneValidator;
import org.gephi.lib.validation.PositiveNumberValidator;
import org.netbeans.validation.api.Problems;
import org.netbeans.validation.api.Validator;
import org.netbeans.validation.api.builtin.Validators;
import org.netbeans.validation.api.ui.ValidationGroup;
import org.netbeans.validation.api.ui.ValidationPanel;

/**
 *
 * @author Daniel
 */
public class AgeingBAModelPanel extends javax.swing.JPanel {

    /**
     * Creates new form AgeingBAModelPanel
     */
    public AgeingBAModelPanel() {
        initComponents();
        showDataFields();
    }

    public static ValidationPanel createValidationPanel(AgeingBAModelPanel innerPanel) {
        ValidationPanel valPanel = new ValidationPanel();
        if (innerPanel == null) {
            innerPanel = new AgeingBAModelPanel();
        }
        valPanel.setInnerComponent(innerPanel);

        ValidationGroup group = valPanel.getValidationGroup();

        group.add(innerPanel.NField, Validators.REQUIRE_NON_EMPTY_STRING, new PositiveNumberValidator());
        group.add(innerPanel.M0Field, Validators.REQUIRE_NON_EMPTY_STRING, new PositiveNumberValidator());
        group.add(innerPanel.M0Field, Validators.REQUIRE_NON_EMPTY_STRING, new M0Validator(innerPanel));
        group.add(innerPanel.MField, Validators.REQUIRE_NON_EMPTY_STRING, new PositiveNumberValidator());
        group.add(innerPanel.MField, Validators.REQUIRE_NON_EMPTY_STRING, new MValidator(innerPanel));
        group.add(innerPanel.startAge, Validators.REQUIRE_NON_NEGATIVE_NUMBER, new BetweenZeroAndOneValidator());
        group.add(innerPanel.growingInterval, Validators.REQUIRE_NON_NEGATIVE_NUMBER, new BetweenZeroAndOneValidator());
        group.add(innerPanel.ageingInterval, Validators.REQUIRE_NON_NEGATIVE_NUMBER, new GettingOldParameterValidator(innerPanel));

        return valPanel;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        ageingTypeGroup = new javax.swing.ButtonGroup();
        NField = new javax.swing.JTextField();
        M0Field = new javax.swing.JTextField();
        MField = new javax.swing.JTextField();
        NLabel = new javax.swing.JLabel();
        M0Label = new javax.swing.JLabel();
        MLabel = new javax.swing.JLabel();
        LineAgeing = new javax.swing.JRadioButton();
        ageingLabel = new javax.swing.JLabel();
        ExponentialAgeing = new javax.swing.JRadioButton();
        HomographicAgeing = new javax.swing.JRadioButton();
        randomiseLabel = new javax.swing.JLabel();
        randomiseCheckBox = new javax.swing.JCheckBox();
        startAgeLabel = new javax.swing.JLabel();
        startAge = new javax.swing.JTextField();
        growingIntervalLabel = new javax.swing.JLabel();
        growingInterval = new javax.swing.JTextField();
        ageingIntervalLabel = new javax.swing.JLabel();
        ageingInterval = new javax.swing.JTextField();
        NoneAgeing = new javax.swing.JRadioButton();

        NField.setText(org.openide.util.NbBundle.getMessage(AgeingBAModelPanel.class, "AgeingBAModelPanel.NField.text")); // NOI18N
        NField.setToolTipText(org.openide.util.NbBundle.getMessage(AgeingBAModelPanel.class, "AgeingBAModelPanel.MField.toolTipText.Eng")); // NOI18N

        M0Field.setText(org.openide.util.NbBundle.getMessage(AgeingBAModelPanel.class, "AgeingBAModelPanel.M0Field.text")); // NOI18N
        M0Field.setToolTipText(org.openide.util.NbBundle.getMessage(AgeingBAModelPanel.class, "AgeingBAModelPanel.M0Field.toolTipText.Eng")); // NOI18N

        MField.setText(org.openide.util.NbBundle.getMessage(AgeingBAModelPanel.class, "AgeingBAModelPanel.MField.text")); // NOI18N
        MField.setToolTipText(org.openide.util.NbBundle.getMessage(AgeingBAModelPanel.class, "AgeingBAModelPanel.MField.toolTipText.Eng")); // NOI18N

        NLabel.setText(org.openide.util.NbBundle.getMessage(AgeingBAModelPanel.class, "AgeingBAModelPanel.NLabel.text")); // NOI18N
        NLabel.setToolTipText(org.openide.util.NbBundle.getMessage(AgeingBAModelPanel.class, "AgeingBAModelPanel.NLabel.toolTipText.Eng")); // NOI18N

        M0Label.setText(org.openide.util.NbBundle.getMessage(AgeingBAModelPanel.class, "AgeingBAModelPanel.M0Label.text")); // NOI18N
        M0Label.setToolTipText(org.openide.util.NbBundle.getMessage(AgeingBAModelPanel.class, "AgeingBAModelPanel.M0Field.toolTipText.Eng")); // NOI18N

        MLabel.setText(org.openide.util.NbBundle.getMessage(AgeingBAModelPanel.class, "AgeingBAModelPanel.MLabel.text")); // NOI18N
        MLabel.setToolTipText(org.openide.util.NbBundle.getMessage(AgeingBAModelPanel.class, "AgeingBAModelPanel.MLabel.toolTipText.Eng")); // NOI18N

        ageingTypeGroup.add(LineAgeing);
        LineAgeing.setText(org.openide.util.NbBundle.getMessage(AgeingBAModelPanel.class, "AgeingBAModelPanel.LineAgeing.text.Eng")); // NOI18N
        LineAgeing.setToolTipText(org.openide.util.NbBundle.getMessage(AgeingBAModelPanel.class, "AgeingBAModelPanel.LineAgeing.toolTipText.Eng")); // NOI18N
        LineAgeing.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LineAgeingActionPerformed(evt);
            }
        });

        ageingLabel.setText(org.openide.util.NbBundle.getMessage(AgeingBAModelPanel.class, "AgeingBAModelPanel.ageingLabel.text.Eng")); // NOI18N

        ageingTypeGroup.add(ExponentialAgeing);
        ExponentialAgeing.setText(org.openide.util.NbBundle.getMessage(AgeingBAModelPanel.class, "AgeingBAModelPanel.ExponentialAgeing.text.Eng")); // NOI18N
        ExponentialAgeing.setToolTipText(org.openide.util.NbBundle.getMessage(AgeingBAModelPanel.class, "AgeingBAModelPanel.ExponentialAgeing.toolTipText.Eng")); // NOI18N
        ExponentialAgeing.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ExponentialAgeingActionPerformed(evt);
            }
        });

        ageingTypeGroup.add(HomographicAgeing);
        HomographicAgeing.setText(org.openide.util.NbBundle.getMessage(AgeingBAModelPanel.class, "AgeingBAModelPanel.HomographicAgeing.text.Eng")); // NOI18N
        HomographicAgeing.setToolTipText(org.openide.util.NbBundle.getMessage(AgeingBAModelPanel.class, "AgeingBAModelPanel.HomographicAgeing.toolTipText.Eng")); // NOI18N
        HomographicAgeing.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                HomographicAgeingActionPerformed(evt);
            }
        });

        randomiseLabel.setText(org.openide.util.NbBundle.getMessage(AgeingBAModelPanel.class, "AgeingBAModelPanel.randomiseLabel.text.Eng")); // NOI18N

        randomiseCheckBox.setText(org.openide.util.NbBundle.getMessage(AgeingBAModelPanel.class, "AgeingBAModelPanel.randomiseCheckBox.text")); // NOI18N
        randomiseCheckBox.setToolTipText(org.openide.util.NbBundle.getMessage(AgeingBAModelPanel.class, "AgeingBAModelPanel.randomiseCheckBox.toolTipText.Eng")); // NOI18N
        randomiseCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                randomiseCheckBoxActionPerformed(evt);
            }
        });

        startAgeLabel.setText(org.openide.util.NbBundle.getMessage(AgeingBAModelPanel.class, "AgeingBAModelPanel.startAgeLabel.text.Eng")); // NOI18N

        startAge.setText(org.openide.util.NbBundle.getMessage(AgeingBAModelPanel.class, "AgeingBAModelPanel.startAge.text")); // NOI18N

        growingIntervalLabel.setText(org.openide.util.NbBundle.getMessage(AgeingBAModelPanel.class, "AgeingBAModelPanel.growingIntervalLabel.text.Eng")); // NOI18N

        growingInterval.setText(org.openide.util.NbBundle.getMessage(AgeingBAModelPanel.class, "AgeingBAModelPanel.growingInterval.text")); // NOI18N

        ageingIntervalLabel.setText(org.openide.util.NbBundle.getMessage(AgeingBAModelPanel.class, "AgeingBAModelPanel.ageingIntervalLabel.text.Eng")); // NOI18N

        ageingInterval.setText(org.openide.util.NbBundle.getMessage(AgeingBAModelPanel.class, "AgeingBAModelPanel.ageingInterval.text")); // NOI18N

        ageingTypeGroup.add(NoneAgeing);
        NoneAgeing.setSelected(true);
        NoneAgeing.setText(org.openide.util.NbBundle.getMessage(AgeingBAModelPanel.class, "AgeingBAModelPanel.NoneAgeing.text.Eng")); // NOI18N
        NoneAgeing.setToolTipText(org.openide.util.NbBundle.getMessage(AgeingBAModelPanel.class, "AgeingBAModelPanel.NoneAgeing.toolTipText.Eng")); // NOI18N
        NoneAgeing.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NoneAgeingActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(randomiseLabel)
                                .addComponent(startAgeLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(growingIntervalLabel, javax.swing.GroupLayout.Alignment.TRAILING))
                            .addComponent(ageingIntervalLabel)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(ageingLabel))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(MLabel)
                            .addComponent(M0Label)
                            .addComponent(NLabel))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 33, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(HomographicAgeing)
                        .addComponent(NoneAgeing)
                        .addComponent(LineAgeing)
                        .addComponent(ExponentialAgeing)
                        .addGroup(layout.createSequentialGroup()
                            .addGap(1, 1, 1)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(NField)
                                .addComponent(MField)
                                .addComponent(M0Field, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(ageingInterval, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(growingInterval, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(startAge, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(randomiseCheckBox))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(NField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(NLabel))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(M0Field, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(M0Label))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(MField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(MLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(NoneAgeing)
                    .addComponent(ageingLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(LineAgeing)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(ExponentialAgeing)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(HomographicAgeing)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(randomiseCheckBox)
                    .addComponent(randomiseLabel))
                .addGap(9, 9, 9)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(startAge, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(startAgeLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(growingInterval, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(growingIntervalLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ageingInterval, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ageingIntervalLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(116, Short.MAX_VALUE))
        );

        NField.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(AgeingBAModelPanel.class, "AgeingBAModelPanel.NField.AccessibleContext.accessibleName")); // NOI18N
        M0Field.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(AgeingBAModelPanel.class, "AgeingBAModelPanel.M0Field.AccessibleContext.accessibleName")); // NOI18N
        MField.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(AgeingBAModelPanel.class, "AgeingBAModelPanel.MField.AccessibleContext.accessibleName")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents

    private void LineAgeingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LineAgeingActionPerformed
        showDataFields();
    }//GEN-LAST:event_LineAgeingActionPerformed

    private void randomiseCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_randomiseCheckBoxActionPerformed
        showDataFields();
    }//GEN-LAST:event_randomiseCheckBoxActionPerformed

    private void ExponentialAgeingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExponentialAgeingActionPerformed
        showDataFields();
    }//GEN-LAST:event_ExponentialAgeingActionPerformed

    private void HomographicAgeingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_HomographicAgeingActionPerformed
        showDataFields();
    }//GEN-LAST:event_HomographicAgeingActionPerformed

    private void NoneAgeingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NoneAgeingActionPerformed
        showDataFields();
    }//GEN-LAST:event_NoneAgeingActionPerformed

    public void showDataFields() {
        if (NoneAgeing.isSelected()) {
            randomiseCheckBox.setVisible(false);
            randomiseLabel.setVisible(false);
            startAgeLabel.setVisible(false);
            startAge.setVisible(false);
            growingIntervalLabel.setVisible(false);
            growingInterval.setVisible(false);
            ageingIntervalLabel.setVisible(false);
            ageingInterval.setVisible(false);
        } else {
            randomiseCheckBox.setVisible(true);
            randomiseLabel.setVisible(true);
            if (randomiseCheckBox.isSelected()) {
                startAgeLabel.setVisible(false);
                startAge.setVisible(false);
                growingIntervalLabel.setVisible(false);
                growingInterval.setVisible(false);
                ageingIntervalLabel.setVisible(false);
                ageingInterval.setVisible(false);
            } else {
                int type = 1;
                if (LineAgeing.isSelected()) {
                    type = 1;
                } else if (ExponentialAgeing.isSelected()) {
                    type = 2;
                } else if (HomographicAgeing.isSelected()) {
                    type = 3;
                }

                startAgeLabel.setVisible(true);
                startAge.setVisible(true);
                if (type < 3) {
                    ageingIntervalLabel.setVisible(true);
                    ageingInterval.setVisible(true);
                    if (type < 2) {
                        growingIntervalLabel.setVisible(true);
                        growingInterval.setVisible(true);
                    } else {
                        growingIntervalLabel.setVisible(false);
                        growingInterval.setVisible(false);
                    }
                } else {
                    growingIntervalLabel.setVisible(false);
                    growingInterval.setVisible(false);
                    ageingIntervalLabel.setVisible(false);
                    ageingInterval.setVisible(false);
                }
            }
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JRadioButton ExponentialAgeing;
    protected javax.swing.JRadioButton HomographicAgeing;
    protected javax.swing.JRadioButton LineAgeing;
    protected javax.swing.JTextField M0Field;
    private javax.swing.JLabel M0Label;
    protected javax.swing.JTextField MField;
    private javax.swing.JLabel MLabel;
    protected javax.swing.JTextField NField;
    private javax.swing.JLabel NLabel;
    protected javax.swing.JRadioButton NoneAgeing;
    protected javax.swing.JTextField ageingInterval;
    private javax.swing.JLabel ageingIntervalLabel;
    private javax.swing.JLabel ageingLabel;
    protected javax.swing.ButtonGroup ageingTypeGroup;
    protected javax.swing.JTextField growingInterval;
    private javax.swing.JLabel growingIntervalLabel;
    protected javax.swing.JCheckBox randomiseCheckBox;
    private javax.swing.JLabel randomiseLabel;
    protected javax.swing.JTextField startAge;
    private javax.swing.JLabel startAgeLabel;
    // End of variables declaration//GEN-END:variables

    private static class MValidator implements Validator<String> {

        AgeingBAModelPanel panel;

        public MValidator(AgeingBAModelPanel panel) {
            this.panel = panel;
        }

        @Override
        public boolean validate(Problems problems, String compName, String model) {
            boolean result = false;

            try {
                Integer m0 = Integer.parseInt(panel.M0Field.getText());
                Integer M = Integer.parseInt(panel.MField.getText());
                result = M <= m0;
            } catch (Exception e) {
            }
            if (!result) {
                //String message = "M musi być mniejsze lub równe M0";
                String message = "M must be lower or equal M0";
                problems.add(message);
            }

            return result;
        }
    }

    private static class M0Validator implements Validator<String> {

        AgeingBAModelPanel panel;

        public M0Validator(AgeingBAModelPanel panel) {
            this.panel = panel;
        }

        @Override
        public boolean validate(Problems problems, String compName, String model) {
            boolean result = false;

            try {
                Integer N = Integer.parseInt(panel.NField.getText());
                Integer M0 = Integer.parseInt(panel.M0Field.getText());
                result = M0 <= N;
            } catch (Exception e) {
            }
            if (!result) {
                //String message = "M0 musi być mniejsze lub równe N";
                String message = "M0 must be lower or equal N";
                problems.add(message);
            }

            return result;
        }
    }

    private static class GettingOldParameterValidator implements Validator<String> {

        AgeingBAModelPanel panel;

        public GettingOldParameterValidator(AgeingBAModelPanel panel) {
            this.panel = panel;
        }

        @Override
        public boolean validate(Problems problems, String compName, String model) {
            boolean result = false;
            //String message = "Interwał starzenia musi być z przedziału [0.0, 1.0];
            String message = "Getting old interval must be in range [0.0, 1.0]";
            try {
                Double s = Double.parseDouble(panel.ageingInterval.getText());
                if (panel.ExponentialAgeing.isSelected()) {
                    result = s > 0.0;
                    //message = "Interwał starzenia musi być większy niż 0.0";
                    message = "Getting old interval must be grater than 0.0";
                } else {
                    result = s <= 1.0;
                    result &= s >= 0.0;
                }
            } catch (Exception e) {
                //message = "Interwał starzenia musi być liczbą";
                message = "Getting old interval must be a number";
                e.printStackTrace();
            }

            if (!result) {
                problems.add(message);
            }
            return result;
        }

    }
}