package nl.dictu.prova;

import java.util.ArrayList;

import nl.dictu.prova.framework.TestSuite;
import nl.dictu.prova.plugins.input.InputPlugin;
import nl.dictu.prova.plugins.output.OutputPlugin;
import nl.dictu.prova.plugins.reporting.ReportingPlugin;

/**
 * Describes the functions that must be available for the other parts of the 
 * framework to run.
 * 
 * @author  Sjoerd Boerhout
 * @since   2016-04-06
 */
public interface TestRunner
{
  public String                     getPropertyValue(String key) throws Exception;
  public void                       setPropertyValue(String key, String value) throws Exception;
  public void                       printAllProperties() throws Exception;
  
  public void                       setRootTestSuite(TestSuite testSuite);

  public InputPlugin                getInputPlugin();
  public OutputPlugin               getWebActionPlugin();
  public OutputPlugin               getShellActionPlugin();
  public ArrayList<ReportingPlugin> getReportingPlugins();
}