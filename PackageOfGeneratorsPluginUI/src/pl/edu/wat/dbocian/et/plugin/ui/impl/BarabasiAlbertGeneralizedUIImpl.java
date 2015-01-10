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
import pl.edu.wat.dbocian.et.plugin.generators.BarabasiAlbertGeneralized;
import pl.edu.wat.dbocian.et.plugin.ui.interfaces.BarabasiAlbertGeneralizedUI;
import pl.edu.wat.dbocian.et.plugin.ui.panels.BarabasiAlbertGeneralizedPanel;

/**
 * @author Daniel
 * 
 */
@ServiceProvider(service = BarabasiAlbertGeneralizedUI.class)
public class BarabasiAlbertGeneralizedUIImpl implements BarabasiAlbertGeneralizedUI {
    private BarabasiAlbertGeneralizedPanel panel;
    private BarabasiAlbertGeneralized generator;

    @Override
    public JPanel getPanel() {
        if (panel == null) {
            panel = new BarabasiAlbertGeneralizedPanel();
        }
        return BarabasiAlbertGeneralizedPanel.createValidationPanel(panel);        
    }

    @Override
    public void setup(Generator gen) {
        generator = (BarabasiAlbertGeneralized) gen;
        
        if (panel == null) 
            panel = new BarabasiAlbertGeneralizedPanel();
        
        panel.NField.setText(String.valueOf(generator.getN()));
        panel.m0Field.setText(String.valueOf(generator.getM0()));
        panel.MField.setText(String.valueOf(generator.getM()));
        panel.pField.setText(String.valueOf(generator.getP()));
        panel.qField.setText(String.valueOf(generator.getQ()));
        panel.passingBox.setSelected(generator.isPassingNodes());
        panel.miField.setText(String.valueOf(generator.getMi()));
        panel.sigmaField.setText(String.valueOf(generator.getSigma2()));
        panel.acceleratedBox.setSelected(generator.isAccelerated());
        panel.alphaField.setText(String.valueOf(generator.getAlpha()));
        panel.betaField.setText(String.valueOf(generator.getBeta()));
        
        panel.showDataLabels();
    }

    @Override
    public void unsetup() {
        generator.setN(Integer.valueOf(panel.NField.getText()));
        generator.setM0(Integer.valueOf(panel.m0Field.getText()));
        generator.setM(Integer.valueOf(panel.MField.getText()));
        generator.setP(Double.valueOf(panel.pField.getText()));
        generator.setQ(Double.valueOf(panel.qField.getText()));
        generator.setPassingNodes(panel.passingBox.isSelected());
        generator.setMi(Double.valueOf(panel.miField.getText()));
        generator.setSigma2(Double.valueOf(panel.sigmaField.getText()));
        generator.setAccelerated(panel.acceleratedBox.isSelected());
        generator.setAlpha(Double.valueOf(panel.alphaField.getText()));
        generator.setBeta(Double.valueOf(panel.betaField.getText()));
    }
    
}
