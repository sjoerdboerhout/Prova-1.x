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

import nl.dictu.prova.framework.TestAction;
import nl.dictu.prova.framework.parameters.FileName;
import nl.dictu.prova.framework.parameters.Url;

/**
 * Handles the Prova function 'download file' to save a file from a web page.
 * 
 * @author Sjoerd Boerhout
 * @since 0.0.1
 */
public class DownloadFile extends TestAction {
    // Action attribute names
    public final static String ATTR_XPATH = "XPATH";
    public final static String ATTR_URL = "URL";
    public final static String ATTR_SAVEAS = "SAVEAS";

    private Url url;
    private FileName saveAs;

    /**
     * Constructor
     * 
     * @throws Exception
     */
    public DownloadFile() throws Exception {
        super();
        url = new Url();
        url.setMinLength(1);
        saveAs = new FileName();
    }

    /**
     * Set attribute <key> with <value> - Unknown attributes are ignored - Invalid values result in an exception
     * 
     * @param key
     * @param value
     * @throws Exception
     */
    @Override
    public void setAttribute(String key, String value) throws Exception {
        LOGGER.trace("Request to set '{}' to '{}'", () -> key, () -> value);

        switch (key.toUpperCase()) {
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

    /**
     * Check if all requirements are met to execute this action
     */
    @Override
    public boolean isValid() {
        if (testRunner == null)
            return false;
        if (!url.isValid())
            return false;
        if (!saveAs.isValid())
            return false;

        return true;
    }

    /**
     * Execute this action in the active output plug-in
     */
    @Override
    public void execute() throws Exception {
        LOGGER.trace("> Execute test action: {}", () -> this.toString());

        if (!isValid())
            throw new Exception("Action is not validated!");

        this.testRunner.getWebActionPlugin().doDownloadFile(url.getValue(), saveAs.getValue());
    }

    /**
     * Return a string representation of the objects content
     * 
     * @return
     */
    @Override
    public String toString() {
        return ("'" + this.getClass().getSimpleName().toUpperCase() + "': Save '" + url.getValue() + "' as "
                + saveAs.getValue());
    }
}
