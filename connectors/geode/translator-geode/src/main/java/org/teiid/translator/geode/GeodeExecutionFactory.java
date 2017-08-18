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

import javax.resource.cci.ConnectionFactory;

import org.teiid.language.QueryExpression;
import org.teiid.language.Select;
import org.teiid.metadata.MetadataFactory;
import org.teiid.metadata.RuntimeMetadata;
import org.teiid.translator.ExecutionContext;
import org.teiid.translator.ExecutionFactory;
import org.teiid.translator.ResultSetExecution;
import org.teiid.translator.Translator;
import org.teiid.translator.TranslatorException;

@Translator(name = "typename", description = "geode custom translator")
public class GeodeExecutionFactory extends ExecutionFactory<ConnectionFactory, GeodeConnection> {


  public GeodeExecutionFactory() {
  }

  @Override
  public void start() throws TranslatorException {
  }

  @Override
  public ResultSetExecution createResultSetExecution(QueryExpression command,
                                                     ExecutionContext executionContext,
                                                     RuntimeMetadata metadata,
                                                     GeodeConnection connection)
      throws TranslatorException {
    return new GeodeExecution((Select) command, connection.getInstance());
  }

  public boolean supportsCompareCriteriaEquals() {
    return true;
  }

  public boolean supportsInCriteria() {
    return true;
  }

  @Override
  public boolean isSourceRequired() {
    return false;
  }

  @Override
  public void getMetadata(MetadataFactory metadataFactory, GeodeConnection connection)
      throws TranslatorException {
  }

  @Override
  public boolean supportsOnlyLiteralComparison() {
    return true;
  }

}
