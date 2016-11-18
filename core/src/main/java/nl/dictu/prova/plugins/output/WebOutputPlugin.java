package nl.dictu.prova.plugins.output;

import nl.dictu.prova.framework.parameters.Xpath;

public interface WebOutputPlugin extends OutputPlugin {
	public void doCaptureScreen(String fileName) throws Exception;

	public void doClick(String xPath, Boolean rightClick, Boolean waitUntilPageLoaded) throws Exception;

	public void doDownloadFile(String url, String saveAs) throws Exception;

	public void doSelect(String xPath, Boolean select) throws Exception;
	
	public void doSelectDropdown(String value, String value2) throws Exception;

	public void doSendKeys(String xPath, String keys) throws Exception;

	public void doSetText(String xPath, String text, Boolean replace) throws Exception;

	public void doSleep(long waitTime) throws Exception;
  
  public void doStoreText(String xPath, String regex, String name, double timeout) throws Exception;
	
	public void doSwitchFrame(String xPath, Boolean alert, Boolean accept) throws Exception;

	public void doSwitchScreen(String name) throws Exception;

	public void doValidateElement(String xPath, Boolean exists, double timeOut) throws Exception;

	public void doValidateText(String xPath, String value, Boolean exists, double timeOut) throws Exception;

	public void doNavigate(String string);
}