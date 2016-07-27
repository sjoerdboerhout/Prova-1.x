package nl.dictu.prova.framework.soap;

import nl.dictu.prova.framework.TestAction;

public class SetProperties extends TestAction {
    
    String url;
    String user;
    String pass;
    String prefix;

    @Override
    public void setAttribute(String key, String value) throws Exception {
        switch(key.toLowerCase()){
            case "prova.properties.prefix": prefix = value;
                                            LOGGER.debug("SetProperties prefix set to " + prefix);
                                            break;
            case "prova.properties.user":   user = value;
                                            LOGGER.debug("SetProperties user set to " + user);
                                            break;
            case "prova.properties.pass":   pass = value;
                                            LOGGER.debug("SetProperties pass set to " + pass);
                                            break;
            case "prova.properties.url":    url = value;
                                            LOGGER.debug("SetProperties url set to " + url);
                                            break;
        }   
    }

    @Override
    public void execute() throws Exception {
        if(!isValid()){
            throw new Exception("testRunner or Properties not properly set!");
        }
        this.testRunner.getSoapActionPlugin().doSetProperties(url, user, pass, prefix);
    }

    @Override
    public boolean isValid() throws Exception {
        if (testRunner == null) return false;
        if (url == null) return false;
        if (prefix == null) return false;
        return true;
    }

}
