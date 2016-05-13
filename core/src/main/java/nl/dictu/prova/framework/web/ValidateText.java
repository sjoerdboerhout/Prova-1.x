package nl.dictu.prova.framework.web;

import nl.dictu.prova.framework.TestAction;
import nl.dictu.prova.framework.parameters.Bool;
import nl.dictu.prova.framework.parameters.Text;
import nl.dictu.prova.framework.parameters.TimeOut;
import nl.dictu.prova.framework.parameters.Xpath;

/**
 * Handles the Prova function 'validate text' to check if the given text
 * is (not) available on the web page.
 * 
 * @author  Sjoerd Boerhout
 * @since   0.0.1
 */
public class ValidateText extends TestAction
{
  // Action attribute names
  public final static String ATTR_VALUE   = "VALUE";
  public final static String ATTR_EXISTS  = "EXISTS";
  public final static String ATTR_TIMEOUT = "TIMEOUT";
  public final static String ATTR_XPATH   = "XPATH";

  private Text    text;
  private Bool    exists;
  private TimeOut timeOut;
  private Xpath   xPath;
  
  
  /**
   * Constructor
   * @throws Exception 
   */
  public ValidateText() throws Exception
  {
    super();
    
    // Create parameters with (optional) defaults and limits
    text = new Text();
    exists = new Bool(true);
    timeOut = new TimeOut(60000); // Ms
    xPath = new Xpath();
    
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
  public void setAttribute(String key, String value) throws Exception
  {
    LOGGER.trace("Request to set '{}' to '{}'", () -> key, () -> value);
    
    switch(key.toUpperCase())
    {
      case ATTR_PARAMETER:
      case ATTR_VALUE:  
        text.setValue(value); 
      break;
      
      case ATTR_EXISTS:
        exists.setValue(value); 
      break;
      
      case ATTR_TIMEOUT:
        timeOut.setValue(value); 
      break;
      
      case ATTR_XPATH:
        xPath.setValue(value); 
      break;
    }
    
    xPath.setAttribute(key, value); 
  }
  

  /**
   * Check if all requirements are met to execute this action
   */
  @Override
  public boolean isValid()
  {
    if(testRunner == null)  return false;
    if(!text.isValid())     return false;
    if(!exists.isValid())   return false;
    if(!timeOut.isValid())  return false;
    if(!xPath.isValid())    return false;
    
    return true;
  }  
  

  /**
   * Execute this action in the active output plug-in
   */
  @Override
  public void execute() throws Exception
  {
    if(!isValid())
      throw new Exception("Action is not validated!");
    
    testRunner.getWebActionPlugin().doValidateText(xPath.getValue(), text.getValue(), exists.getValue(), timeOut.getValue()); 
  }


  /**
   * Return a string representation of the objects content
   * 
   * @return 
   */
  @Override
  public String toString()
  {
    return("'" + this.getClass().getSimpleName().toUpperCase() + "': Validate that text '" + text.getValue() + "' " +
          (exists.getValue() ? "" : "doesn't ") + "exists. " +
          (xPath.getValue().length() > 0 ? "Element: " + xPath.getValue() + ". " : "") +
          " TimeOut: " + timeOut.getValue() );
  }
}