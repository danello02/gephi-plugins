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
import pl.edu.wat.dbocian.et.plugin.generators.BarabasiAlbert;
import pl.edu.wat.dbocian.et.plugin.ui.interfaces.BarabasiAlbertUI;
import pl.edu.wat.dbocian.et.plugin.ui.panels.BarabasiAlbertPanel;

/**
 * @author Daniel Bocian
 * 
 */
@ServiceProvider(service = BarabasiAlbertUI.class)
public class BarabasiAlbertUIImpl implements BarabasiAlbertUI {
    private BarabasiAlbertPanel panel;
    private BarabasiAlbert generator;

    @Override
    public JPanel getPanel() {
        if (panel == null) {
            panel = new BarabasiAlbertPanel();
        }
        return BarabasiAlbertPanel.createValidationPanel(panel);
    }

    @Override
    public void setup(Generator generator) {
        this.generator = (BarabasiAlbert) generator;
        
        if (panel == null) 
            panel = new BarabasiAlbertPanel();
        
        panel.NField.setText(String.valueOf(this.generator.getN()));
        panel.M0Field.setText(String.valueOf(this.generator.getM0()));
        panel.MField.setText(String.valueOf(this.generator.getM()));
        int type = this.generator.getAgeingType();
        if (type == 1) panel.LineAgeing.setSelected(true);
        if (type == 2) panel.ExponentialAgeing.setSelected(true);
        else if (type == 3) panel.HomographicAgeing.setSelected(true);
        else panel.NoneAgeing.setSelected(true); // NoneAgeing type is default (type == 0)
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
        int type = 0;
        if (panel.NoneAgeing.isSelected()) type = 0;
        else if (panel.LineAgeing.isSelected()) type = 1;
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
