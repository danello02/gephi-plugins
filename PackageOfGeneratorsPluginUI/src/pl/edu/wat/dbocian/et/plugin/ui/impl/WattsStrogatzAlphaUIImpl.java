/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pl.edu.wat.dbocian.et.plugin.ui.impl;

import javax.swing.JPanel;
import org.gephi.io.generator.spi.Generator;
import org.openide.util.lookup.ServiceProvider;
import pl.edu.wat.dbocian.et.plugin.generators.WattsStrogatzAlpha;
import pl.edu.wat.dbocian.et.plugin.ui.interfaces.WattsStrogatzAlphaUI;
import pl.edu.wat.dbocian.et.plugin.ui.panels.WattsStrogatzAlphaPanel;

/**
 *
 * @author Daniel Bocian
 */
@ServiceProvider(service = WattsStrogatzAlphaUI.class)
public class WattsStrogatzAlphaUIImpl implements WattsStrogatzAlphaUI {
    private WattsStrogatzAlphaPanel panel;
    private WattsStrogatzAlpha generator;

    @Override
    public JPanel getPanel() {
        if (panel == null) {
            panel = new WattsStrogatzAlphaPanel();
        }
        return WattsStrogatzAlphaPanel.createValidationPanel(panel);        
    }

    @Override
    public void setup(Generator gen) {
        generator = (WattsStrogatzAlpha) gen;
        
        if (panel == null) 
            panel = new WattsStrogatzAlphaPanel();
        
        panel.nField.setText(String.valueOf(generator.getN()));
        panel.kField.setText(String.valueOf(generator.getK()));
        panel.alphaField.setText(String.valueOf(generator.getAlpha()));
        
        if (generator.getTopology() == WattsStrogatzAlpha.RING) panel.ringRadio.setSelected(true);
        else if (generator.getTopology() == WattsStrogatzAlpha.GNP) panel.gnpRadio.setSelected(true);
        else if (generator.getTopology() == WattsStrogatzAlpha.GNM) panel.gnmRadio.setSelected(true);
        else panel.ringRadio.setSelected(true);
        
        panel.pField.setText(String.valueOf(generator.getP()));
        panel.mField.setText(String.valueOf(generator.getM()));
        
        panel.showFields();
    }

    @Override
    public void unsetup() {
        generator.setN(Integer.valueOf(panel.nField.getText()));
        generator.setK(Integer.valueOf(panel.kField.getText()));
        generator.setAlpha(Double.valueOf(panel.alphaField.getText()));
        
        if (panel.ringRadio.isSelected()) generator.setTopology(WattsStrogatzAlpha.RING);
        else if (panel.gnpRadio.isSelected()) generator.setTopology(WattsStrogatzAlpha.GNP);
        else if (panel.gnmRadio.isSelected()) generator.setTopology(WattsStrogatzAlpha.GNM);
        
        
        generator.setP(Double.valueOf(panel.pField.getText()));
        generator.setM(Integer.valueOf(panel.mField.getText()));
    }
    
}
