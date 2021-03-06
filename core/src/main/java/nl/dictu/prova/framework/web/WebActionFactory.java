/**
 *
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * <p>
 * http://ec.europa.eu/idabc/eupl
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 * <p>
 * Date:      18-12-2016
 * Author(s): Sjoerd Boerhout, Robert Bralts & Coos van der Galiën
 * <p>
 */
package nl.dictu.prova.framework.web;

import nl.dictu.prova.framework.ActionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.dictu.prova.framework.TestAction;

/**
 * A factory that allows the input plug-in to create a new web-action.
 * 
 * @author  Sjoerd Boerhout
 * @since   2016-04-19
 */
public class WebActionFactory implements ActionFactory
{
  protected final static Logger LOGGER = LogManager.getLogger();
  
  public final static String ACTION_CAPTURESCREEN   = "CAPTURESCREEN";
  public final static String ACTION_CLICK           = "CLICK";
  public final static String ACTION_DOWNLOADFILE    = "DOWNLOADFILE";
  public final static String ACTION_NAVIGATE	    = "NAVIGATE";
  public final static String ACTION_SELECT          = "SELECT";
  public final static String ACTION_SELECTDROPDOWN  = "SELECTDROPDOWN";
  public final static String ACTION_SENDKEYS        = "SENDKEYS";
  public final static String ACTION_SETTEXT         = "SETTEXT";
  public final static String ACTION_SLEEP           = "SLEEP";
  public final static String ACTION_STORETEXT       = "STORETEXT";
  public final static String ACTION_SWITCHFRAME     = "SWITCHFRAME";
  public final static String ACTION_SWITCHSCREEN    = "SWITCHSCREEN";
  public final static String ACTION_VALIDATEELEMENT = "VALIDATEELEMENT";
  public final static String ACTION_VALIDATETEXT    = "VALIDATETEXT";
  public final static String ACTION_WAITFORELEMENT    = "WAITFORELEMENT";
  public final static String ACTION_UPLOADFILE      = "UPLOADFILE";
    
  /**
   * Get the corresponding action for <name>
   * 
   * @param name
   * @return
   * @throws Exception
   */
  public TestAction getAction(String name) throws Exception
  {
    LOGGER.trace("Request to produce webaction '{}'", () -> name);
    
    switch(name.toUpperCase())
    {
      case ACTION_CAPTURESCREEN:   return new CaptureScreen();
      case ACTION_CLICK:           return new Click();
      case ACTION_DOWNLOADFILE:    return new DownloadFile();
      case ACTION_NAVIGATE:        return new Navigate();
      case ACTION_SELECT:          return new Select();
      case ACTION_SELECTDROPDOWN:  return new SelectDropdown();
      case ACTION_SENDKEYS:        return new SendKeys();
      case ACTION_SETTEXT:         return new SetText();
      case ACTION_SLEEP:           return new Sleep();
      case ACTION_STORETEXT:       return new StoreText();
      case ACTION_SWITCHFRAME:     return new SwitchFrame();
      case ACTION_SWITCHSCREEN:	   return new SwitchScreen();
      case ACTION_VALIDATEELEMENT: return new ValidateElement();
      case ACTION_VALIDATETEXT:    return new ValidateText();
      case ACTION_WAITFORELEMENT:  return new WaitForElement();
      case ACTION_UPLOADFILE:      return new UploadFile();
    }
    
    throw new Exception("Unknown action '" + name + "' requested");
  }
}
