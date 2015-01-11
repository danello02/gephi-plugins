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
import pl.edu.wat.dbocian.et.plugin.generators.Kleinberg;
import pl.edu.wat.dbocian.et.plugin.ui.interfaces.KleinbergUI;
import pl.edu.wat.dbocian.et.plugin.ui.panels.KleinbergPanel;

/**
 * @author Daniel
 * 
 */
@ServiceProvider(service = KleinbergUI.class)
public class KleinbergUIImpl implements KleinbergUI {
    private KleinbergPanel panel;
    private Kleinberg generator;

    @Override
    public JPanel getPanel() {
        if (panel == null) {
            panel = new KleinbergPanel();
        }
        return KleinbergPanel.createValidationPanel(panel);        
    }

    @Override
    public void setup(Generator gen) {
        generator = (Kleinberg) gen;
        
        if (panel == null) 
            panel = new KleinbergPanel();
        
        panel.nField.setText(String.valueOf(generator.getN()));
        panel.pField.setText(String.valueOf(generator.getP()));
        panel.qField.setText(String.valueOf(generator.getQ()));
        panel.rField.setText(String.valueOf(generator.getR()));
        panel.toursBox.setSelected(generator.isTorusBased());
        panel.localBox.setSelected(generator.isLocal());
        panel.SField.setText(String.valueOf(generator.getS()));
        
        panel.showFields();
    }

    @Override
    public void unsetup() {
        generator.setN(Integer.valueOf(panel.nField.getText()));
        generator.setP(Integer.valueOf(panel.pField.getText()));
        generator.setQ(Integer.valueOf(panel.qField.getText()));
        generator.setR(Double.valueOf(panel.rField.getText()));
        generator.setTorusBased(panel.toursBox.isSelected());
        generator.setLocal(panel.localBox.isSelected());
        generator.setS(Integer.valueOf(panel.SField.getText()));
    }
    
}
