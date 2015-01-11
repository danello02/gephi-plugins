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
import pl.edu.wat.dbocian.et.plugin.generators.ErdosRenyiGnp;
import pl.edu.wat.dbocian.et.plugin.ui.interfaces.ErdosRenyiGnpUI;
import pl.edu.wat.dbocian.et.plugin.ui.panels.ErdosRenyiGnpPanel;

/**
 * @author Daniel Bocian
 *
 */
@ServiceProvider(service = ErdosRenyiGnpUI.class)
public class ErdosRenyiGnpUIImpl implements ErdosRenyiGnpUI {

    private ErdosRenyiGnpPanel panel;
    private ErdosRenyiGnp generator;

    @Override
    public JPanel getPanel() {
        if (panel == null) {
            panel = new ErdosRenyiGnpPanel();
        }
        return ErdosRenyiGnpPanel.createValidationPanel(panel);
    }

    @Override
    public void setup(Generator gen) {
        generator = (ErdosRenyiGnp) gen;

        if (panel == null) {
            panel = new ErdosRenyiGnpPanel();
        }

        panel.nField.setText(String.valueOf(generator.getN()));
        panel.pField.setText(String.valueOf(generator.getP()));
        panel.gridBox.setSelected(generator.isGridAlgorithm());
    }

    @Override
    public void unsetup() {
        generator.setN(Integer.valueOf(panel.nField.getText()));
        generator.setP(Double.valueOf(panel.pField.getText()));
        generator.setGridAlgorithm(panel.gridBox.isSelected());
    }

}
