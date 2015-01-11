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
package pl.edu.wat.dbocian.et.plugin.ui.impl;

import javax.swing.JPanel;
import org.gephi.io.generator.spi.Generator;
import org.openide.util.lookup.ServiceProvider;
import pl.edu.wat.dbocian.et.plugin.generators.WattsStrogatzBeta;
import pl.edu.wat.dbocian.et.plugin.ui.interfaces.WattsStrogatzBetaUI;
import pl.edu.wat.dbocian.et.plugin.ui.panels.WattsStrogatzBetaPanel;

/**
 * @author Daniel
 * 
 */
@ServiceProvider(service = WattsStrogatzBetaUI.class)
public class WattsStrogatzBetaUIImpl implements WattsStrogatzBetaUI {
    private WattsStrogatzBetaPanel panel;
    private WattsStrogatzBeta generator;

    @Override
    public JPanel getPanel() {
        if (panel == null) {
            panel = new WattsStrogatzBetaPanel();
        }
        return WattsStrogatzBetaPanel.createValidationPanel(panel);        
    }

    @Override
    public void setup(Generator gen) {
        generator = (WattsStrogatzBeta) gen;
        
        if (panel == null) 
            panel = new WattsStrogatzBetaPanel();
        
        panel.NField.setText(String.valueOf(generator.getN()));
        panel.KField.setText(String.valueOf(generator.getK()));
        panel.betaField.setText(String.valueOf(generator.getBeta()));
        panel.dependentBox.setSelected(generator.isDependentProbability());
        panel.rField.setText(String.valueOf(generator.getR()));
        
        panel.showFields();
    }

    @Override
    public void unsetup() {
        generator.setN(Integer.valueOf(panel.NField.getText()));
        generator.setK(Integer.valueOf(panel.KField.getText()));
        generator.setBeta(Double.valueOf(panel.betaField.getText()));
        generator.setDependentProbability(panel.dependentBox.isSelected());
        generator.setR(Double.valueOf(panel.rField.getText()));
    }
    
}
