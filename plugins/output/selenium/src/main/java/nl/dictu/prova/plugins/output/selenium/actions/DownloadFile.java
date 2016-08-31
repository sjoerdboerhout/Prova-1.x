/*
 *  
 *  Licensed under the EUPL, Version 1.1 or - as soon they will be approved by
 *  the European Commission - subsequent versions of the EUPL (the "Licence");
 *  You may not use this work except in compliance with the Licence.
 *  You may obtain a copy of the Licence at:
 *  
 *  http://ec.europa.eu/idabc/eupl
 *  
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the Licence is distributed on an "AS IS" basis,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the Licence for the specific language governing permissions and
 *  limitations under the Licence.
 *  
 *  Date:      23-08-2016
 *  Author(s): Sjoerd Boerhout
 *  
 */
package nl.dictu.prova.plugins.output.selenium.actions;

import nl.dictu.prova.framework.TestAction;
import nl.dictu.prova.framework.TestStatus;
import nl.dictu.prova.framework.parameters.FileName;
import nl.dictu.prova.framework.parameters.Url;
import nl.dictu.prova.plugins.output.selenium.Selenium;

/**
 *
 * @author Sjoerd Boerhout
 */
public class DownloadFile extends TestAction
{
  // Action attribute names
  public final static String ATTR_XPATH  = "XPATH";
  public final static String ATTR_URL    = "URL";
  public final static String ATTR_SAVEAS = "SAVEAS";
  
  private Selenium selenium;
  private Url      url; 
  private FileName saveAs;
  
  public DownloadFile(Selenium selenium)
  {
    this.selenium = selenium;
  }

  
  /**
   * Execute this action
   */  
  @Override
  public TestStatus execute()
  {
//    LOGGER.trace("> Execute test action: {}", () -> this.toString());
    
//    if(!isValid())
//    {
//      LOGGER.error("Action is not validated!");
//      return TestStatus.FAILED;
//    }
//    
//    try
//    {
//      return TestStatus.PASSED;
//    }
//    catch(Exception eX)
//    {
//      return TestStatus.FAILED;
//    }
      throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  
  /**
   * Return a string representation of the objects content
   * 
   * @return 
   */
  @Override
  public String toString()
  {
    return("'" + this.getClass().getSimpleName().toUpperCase() + "': Save '" + url.getValue() + "' as " + saveAs.getValue());
  }

  
  /**
   * Check if all requirements are met to execute this action
   */
  @Override
  public boolean isValid()
  {
    if(selenium == null)    return false;
    if(!url.isValid())      return false;
    if(!saveAs.isValid())   return false;
    
    return true;
  }
  
  
  /**
   * Set attribute <key> with <value>
   * - Unknown attributes are ignored
   * - Invalid values result in an exception
   * 
   * @param key
   * @param value
   * @throws Exception
   */
  @Override
  public void setAttribute(String key, String value)
  {
    try
    {
      LOGGER.trace("Request to set '{}' to '{}'", () -> key, () -> value);

      switch(key.toUpperCase())
      {
        case ATTR_XPATH:
        case ATTR_URL: 
          url.setValue(value); 
        break;

        case ATTR_PARAMETER:
        case ATTR_SAVEAS:  
          saveAs.setValue(value); 
        break;
      }
    }
    catch(Exception ex)
    {
      LOGGER.error("Exception while setting attribute to TestAction");
    }
  }
  
}