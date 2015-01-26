/*
 * Copyright 2008-2010 Gephi
 * Authors : Daniel Bocian
 * Website : http://www.gephi.org
 * 
 * This file is part of Gephi.
 *
 * Gephi is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Gephi is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Gephi.  If not, see <http://www.gnu.org/licenses/>.
 */
package pl.edu.wat.dbocian.et.plugin.ui.panels;

import org.gephi.lib.validation.PositiveNumberValidator;
import org.netbeans.validation.api.builtin.Validators;
import org.netbeans.validation.api.ui.ValidationGroup;
import org.netbeans.validation.api.ui.ValidationPanel;
import pl.edu.wat.dbocian.et.plugin.ui.validators.BetweenZeroAndOneValidator;

/**
 * @author Daniel Bocian
 * 
 */
public class ErdosRenyiGnpPanel extends javax.swing.JPanel {

    /**
     * Creates new form ErdosRenyiGnpPanel
     */
    public ErdosRenyiGnpPanel() {
        initComponents();
    }
    
    public static ValidationPanel createValidationPanel(ErdosRenyiGnpPanel innerPanel) {
		ValidationPanel validationPanel = new ValidationPanel();
		if (innerPanel == null)
			innerPanel = new ErdosRenyiGnpPanel();
		validationPanel.setInnerComponent(innerPanel);

		ValidationGroup group = validationPanel.getValidationGroup();

		group.add(innerPanel.nField, Validators.REQUIRE_NON_EMPTY_STRING,
				new PositiveNumberValidator());
		group.add(innerPanel.pField, Validators.REQUIRE_NON_EMPTY_STRING,
				new BetweenZeroAndOneValidator(innerPanel.pLabel.getText()));

		return validationPanel;
	}

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        nLabel = new javax.swing.JLabel();
        nField = new javax.swing.JTextField();
        pLabel = new javax.swing.JLabel();
        pField = new javax.swing.JTextField();
        gridLabel = new javax.swing.JLabel();
        gridBox = new javax.swing.JCheckBox();

        nLabel.setLabelFor(nField);
        nLabel.setText(org.openide.util.NbBundle.getMessage(ErdosRenyiGnpPanel.class, "ErdosRenyiGnpPanel.nLabel.text")); // NOI18N

        nField.setText(org.openide.util.NbBundle.getMessage(ErdosRenyiGnpPanel.class, "ErdosRenyiGnpPanel.nField.text")); // NOI18N
        nField.setPreferredSize(new java.awt.Dimension(100, 20));

        pLabel.setLabelFor(pField);
        pLabel.setText(org.openide.util.NbBundle.getMessage(ErdosRenyiGnpPanel.class, "ErdosRenyiGnpPanel.pLabel.text")); // NOI18N

        pField.setText(org.openide.util.NbBundle.getMessage(ErdosRenyiGnpPanel.class, "ErdosRenyiGnpPanel.pField.text")); // NOI18N

        gridLabel.setLabelFor(gridBox);
        gridLabel.setText(org.openide.util.NbBundle.getMessage(ErdosRenyiGnpPanel.class, "ErdosRenyiGnpPanel.gridLabel.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(nLabel)
                    .addComponent(pLabel)
                    .addComponent(gridLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 24, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(gridBox)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(nField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(pField)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nLabel)
                    .addComponent(nField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pLabel)
                    .addComponent(pField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(gridBox)
                    .addComponent(gridLabel))
                .addContainerGap(30, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JCheckBox gridBox;
    private javax.swing.JLabel gridLabel;
    public javax.swing.JTextField nField;
    private javax.swing.JLabel nLabel;
    public javax.swing.JTextField pField;
    private javax.swing.JLabel pLabel;
    // End of variables declaration//GEN-END:variables
}