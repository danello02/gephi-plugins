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
import pl.edu.wat.dbocian.et.plugin.generators.ErdosRenyiGnm;
import pl.edu.wat.dbocian.et.plugin.ui.interfaces.ErdosRenyiGnmUI;
import pl.edu.wat.dbocian.et.plugin.ui.panels.ErdosRenyiGnmPanel;

/**
 * @author Daniel Bocian
 * 
 */
@ServiceProvider(service = ErdosRenyiGnmUI.class)
public class ErdosRenyiGnmUIImpl implements ErdosRenyiGnmUI{
private ErdosRenyiGnmPanel panel;
    private ErdosRenyiGnm generator;

    @Override
    public JPanel getPanel() {
        if (panel == null) {
            panel = new ErdosRenyiGnmPanel();
        }
        return ErdosRenyiGnmPanel.createValidationPanel(panel);        
    }

    @Override
    public void setup(Generator gen) {
        generator = (ErdosRenyiGnm) gen;
        
        if (panel == null) 
            panel = new ErdosRenyiGnmPanel();
        
        panel.nField.setText(String.valueOf(generator.getN()));
        panel.mField.setText(String.valueOf(generator.getM()));
    }

    @Override
    public void unsetup() {
        generator.setN(Integer.valueOf(panel.nField.getText()));
        generator.setM(Integer.valueOf(panel.mField.getText()));
    }
    
}
