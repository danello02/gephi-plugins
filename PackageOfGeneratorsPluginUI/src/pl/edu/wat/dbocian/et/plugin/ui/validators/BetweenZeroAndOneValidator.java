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
package pl.edu.wat.dbocian.et.plugin.ui.validators;

import org.netbeans.validation.api.Problems;
import org.netbeans.validation.api.Validator;

/**
 * @author Daniel Bocian
 * 
 * base on Mathieu Bastian implementation:
 * http://www.massapi.com/source/gephi-0.8-alpha.sources/ValidationAPI/src/org/gephi/lib/validation/BetweenZeroAndOneValidator.java.html
 */
public class BetweenZeroAndOneValidator implements Validator<String> {
    private String name;

    public BetweenZeroAndOneValidator(String name) {
        this.name = name;
    }    

    public BetweenZeroAndOneValidator() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public boolean validate(Problems problems, String compName, String model) {
        boolean result;
        try {
            Double d = Double.parseDouble(model);
            result = d >= 0 && d <= 1;
        } catch (NumberFormatException e) {
            result = false;
        }
        if (!result) {
            String message = "<html>0 &le; " + name + " &le; 1</html>";
            problems.add(message);
        }
        return result;
    }
}
