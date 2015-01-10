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
 * @author Daniel
 * 
 */
public class RealNumberValidator implements Validator<String> {
    private String name;

    public RealNumberValidator(String name) {
        this.name = name;
    }    
    
    @Override
    public boolean validate(Problems problems, String compName, String model) {
        boolean result = true;
        try {
            Double d = Double.parseDouble(model);
        } catch (NumberFormatException e) {
            result = false;
        }
        if (!result) {
            String message = name + " must be valid real number";
            problems.add(message);
        }
        return result;
    }
}
