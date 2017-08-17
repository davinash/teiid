/*
 * Copyright Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags and
 * the COPYRIGHT.txt file distributed with this work.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.teiid.translator.geode;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.teiid.language.Select;
import org.teiid.logging.LogConstants;
import org.teiid.logging.LogManager;
import org.teiid.translator.DataNotAvailableException;
import org.teiid.translator.ResultSetExecution;
import org.teiid.translator.TranslatorException;


/**
 * Represents the execution of a command.
 */
public class GeodeExecution implements ResultSetExecution {


    private Select command;
    
    // Execution state
    Iterator<List<?>> results;
    int[] neededColumns;
    private Select query;

    /**
     * 
     */
    public GeodeExecution(Select query) {
        this.query = query;
    }
    
    @Override
    public void execute() throws TranslatorException {
        // Log our command
        LogManager.logDetail(LogConstants.CTX_CONNECTOR, GeodePlugin.UTIL.getString("execute_query", new Object[] { "geode", command })); //$NON-NLS-1$
    }    


    @Override
    public List<?> next() throws TranslatorException, DataNotAvailableException {
        if (results.hasNext()) {
            return projectRow(results.next(), neededColumns);
        }
        return null;
    }

    /**
     * @param row
     * @param neededColumns
     */
    static List<Object> projectRow(List<?> row, int[] neededColumns) {
        List<Object> output = new ArrayList<Object>(neededColumns.length);
        
        for(int i=0; i<neededColumns.length; i++) {
            output.add(row.get(neededColumns[i]-1));
        }
        
        return output;    
    }

    @Override
    public void close() {
        LogManager.logDetail(LogConstants.CTX_CONNECTOR, GeodePlugin.UTIL.getString("close_query")); //$NON-NLS-1$

    
    }

    @Override
    public void cancel() throws TranslatorException {
        LogManager.logDetail(LogConstants.CTX_CONNECTOR, GeodePlugin.UTIL.getString("cancel_query")); //$NON-NLS-1$
    }
}
