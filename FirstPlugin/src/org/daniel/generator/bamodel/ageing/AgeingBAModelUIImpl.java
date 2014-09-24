/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.daniel.generator.bamodel.ageing;

import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.plaf.basic.BasicBorders;
import org.gephi.io.generator.spi.Generator;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Daniel
 */

@ServiceProvider(service = AgeingBAModelUI.class)
public class AgeingBAModelUIImpl implements AgeingBAModelUI {
    private AgeingBAModelPanel panel;
    private AgeingBAModel generator;

    @Override
    public JPanel getPanel() {
        if (panel == null) {
            panel = new AgeingBAModelPanel();
        }
        return AgeingBAModelPanel.createValidationPanel(panel);
    }

    @Override
    public void setup(Generator generator) {
        this.generator = (AgeingBAModel) generator;
        
        if (panel == null) 
            panel = new AgeingBAModelPanel();
        
        panel.NField.setText(String.valueOf(this.generator.getN()));
        panel.M0Field.setText(String.valueOf(this.generator.getM0()));
        panel.MField.setText(String.valueOf(this.generator.getM()));
        int type = this.generator.getAgeingType();
        if (type == 2) panel.ExponentialAgeing.setSelected(true);
        else if (type == 3) panel.HomographicAgeing.setSelected(true);
        else panel.LineAgeing.setSelected(true); // LineAgeing type is default and type == 1
        panel.randomiseCheckBox.setSelected(this.generator.isRandomise());
        panel.startAge.setText(String.valueOf(this.generator.getStartAge()));
        panel.growingInterval.setText(String.valueOf(this.generator.getGrowingInterval()));
        panel.ageingInterval.setText(String.valueOf(this.generator.getAgeingInterval()));
        panel.showDataFields();
        
    }

    @Override
    public void unsetup() {
        generator.setN(Integer.parseInt(panel.NField.getText()));
        generator.setM0(Integer.parseInt(panel.M0Field.getText()));
        generator.setM(Integer.parseInt(panel.MField.getText()));
        int type = 1;
        if (panel.LineAgeing.isSelected()) type = 1;
        else if (panel.ExponentialAgeing.isSelected()) type = 2;
        else if (panel.HomographicAgeing.isSelected()) type = 3;
        generator.setAgeingType(type);
        generator.setRandomise(panel.randomiseCheckBox.isSelected());
        generator.setStartAge(Double.parseDouble(panel.startAge.getText()));
        generator.setGrowingInterval(Double.parseDouble(panel.growingInterval.getText()));
        generator.setAgeingInterval(Double.parseDouble(panel.ageingInterval.getText()));
        panel = null;
    }
    
}
