package nl.dictu.prova;

import java.lang.Thread.State;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import nl.dictu.prova.framework.TestCase;
import nl.dictu.prova.framework.TestSuite;
import nl.dictu.prova.framework.exceptions.SetUpActionException;
import nl.dictu.prova.framework.exceptions.TearDownActionException;
import nl.dictu.prova.framework.exceptions.TestActionException;
import nl.dictu.prova.plugins.input.InputPlugin;
import nl.dictu.prova.plugins.output.OutputPlugin;
import nl.dictu.prova.plugins.reporting.ReportingPlugin;
import nl.dictu.prova.util.PluginLoader;

/**
 * Core class Prova facilitates the whole process of executing the tests
 * 
 * @author  Sjoerd Boerhout
 * @since   2016-04-06
 */
public class Prova implements Runnable, TestRunner
{
  final static Logger LOGGER = LogManager.getLogger();

  private Thread                      thread;

  private InputPlugin                 inputPlugin;
  private OutputPlugin                shellOutputPlugin;
  private OutputPlugin                webOutputPlugin;
  private ArrayList<ReportingPlugin>  reportPlugins;
  
  private TestSuite                   rootTestSuite;
  private Properties                  properties = new Properties();
  
  
  /**
   * Set up the Prova runner. 
   * The argument 'project' indicates which project Prova starts.
   * 
   * @param project
   */
  public void setUp(String project, Properties provaProperties)
  {
    try
    {          
      if(project.trim().length() < 1)
      {
        throw new Exception("Invalid project name supplied! (" + project + ")");
      }
      
      thread = new Thread(this, project);
      
      // Add all System properties
      properties.putAll(System.getProperties());
      
      // Add all properties read from the config files
      properties.putAll(provaProperties);
    }
    catch(Exception eX)
    {
      LOGGER.fatal(eX);
    }
  }
  
  /**
   * Start Prova execution
   */
  public void start()
  { 
    LOGGER.trace("Starting Prova execution");
    this.thread.start();
  }

  /**
   * Initiate stop of Prova execution
   */
  public void stop()
  {
    LOGGER.trace("Interrupting Prova execution");
    this.thread.interrupt();
  }

  /**
   * Request a join of the thread 
   */
  public void join()
  {
    try
    {
      LOGGER.trace("join requested");
      
      this.thread.join();
    }
    catch(InterruptedException ex)
    {
      LOGGER.warn(ex);
    }
  }

  /**
   * Check if the Prova thread was interrupted 
   */
  public boolean isInterrupted()
  {
    LOGGER.trace("Thread isInterrupted requested");
    
    return this.thread.isInterrupted();
  }

  /**
   * Request the current state of the thread 
   */
  public State getState()
  {
    LOGGER.trace("Thread state requested");
    
    return this.thread.getState();
  }
  
  
  /**
   *  Called when the Prova thread starts.
   *  Run all steps of the test process in correct order
   */
  @Override 
  public void run()
  {
    try
    {
      LOGGER.debug("Starting Prova execution");
      
      LOGGER.trace("Starting Prova state: init");
      init();
      
      LOGGER.trace("Starting Prova state: setup");
      setup();
      
      LOGGER.trace("Starting Prova state: execute");
      execute();
      
      LOGGER.trace("Starting Prova state: teardown");
      tearDown();
    }
    catch(Exception ex)
    {
      LOGGER.fatal(ex);
    }
    
    try
    {
      LOGGER.trace("Starting Prova state: shutdown");
      shutDown();
      LOGGER.debug("Ending Prova thread execution");
    }
    catch(Exception eX)
    {
      LOGGER.error(eX);  
    }
  }
  
  /**
   * Initialize Prova and set everything ready for test execution.
   * - Read projects properties file(s)
   * - Check for a valid input plug-in
   * - Check for a valid output plug-in
   * - Check for (a) valid report plug-in(s)
   */
  private void init() throws Exception
  {
    PluginLoader pluginLoader = new PluginLoader();
    String pluginName = "";
    
    try
    {
      LOGGER.info("Initializing Prova");
      
      if(LOGGER.isTraceEnabled())         
      {
        for(String key : this.properties.stringPropertyNames())
        {
          LOGGER.trace(key + " => " + properties.getProperty(key));
        }  
      }

      LOGGER.debug("Load plug-in files");
      pluginLoader.addFiles(properties.getProperty(Config.PROVA_DIR) +
                            properties.getProperty(Config.PROVA_PLUGINS_DIR), 
                            properties.getProperty(Config.PROVA_PLUGINS_EXT));
     
      
      LOGGER.debug("Load and initialize input plug-in");
      pluginName = properties.getProperty(Config.PROVA_PLUGINS_INPUT_PACKAGE) +
                   properties.getProperty(Config.PROVA_PLUGINS_INPUT).toLowerCase() + "." +
                   properties.getProperty(Config.PROVA_PLUGINS_INPUT);
      
      inputPlugin = pluginLoader.getInstanceOf(pluginName, InputPlugin.class);
         
      if(inputPlugin != null)
        inputPlugin.init(this);
      else
        throw new Exception("Could not load input plugin '" + pluginName + "'");
      
      
      // TODO: Load and initialize output plug-in
      LOGGER.debug("Load and initialize output plug-in");
      pluginName = properties.getProperty("prova.plugins.out.web.package") +
          properties.getProperty("prova.plugins.out.web").toLowerCase() + "." +
          properties.getProperty("prova.plugins.out.web");

      webOutputPlugin = pluginLoader.getInstanceOf(pluginName, OutputPlugin.class);
      
      if(webOutputPlugin != null)
        webOutputPlugin.init(this);
      else
        throw new Exception("Could not load web output plugin '" + pluginName + "'");


      // TODO: Load and initialize report plug-in(s)
      LOGGER.debug("Load and initialize reporting plug-in");
    }
    catch(ClassNotFoundException eX)
    {
      throw new Exception("Plugin '" + pluginName + "' not found!");
    }
    catch(Exception eX)
    {
      LOGGER.error(eX);
      throw eX;
    }
    finally
    {
      //pluginLoader.close();
    }
  }
    
  /**
   *  Index and prepare test scripts for test execution.
   *  - Search for test scripts
   *  - Create structure of test suites and test cases
   *    - Only read the 'headers' and not the full test cases
   *  - Execute the (optional) setup commands to prepare the test environment
   */
  private void setup() throws Exception
  {
    try
    {
      LOGGER.info("Setting up Prova.");
      
      // Set the root location of the test scripts.
      inputPlugin.setTestRoot(properties.getProperty(Config.PROVA_TESTS_ROOT));
       
      // Set filters for test case labels
      inputPlugin.setLabels(properties.getProperty(Config.PROVA_TESTS_FILTERS).split(","));
      
      // TODO Add support for specific test cases
      // inputPlugin.setTestCaseFilter(String[] testCases);
      
      // Search for test scripts and read the headers
      inputPlugin.setUp();
      
      // TODO: Build structure of test suites and test cases
      
      // TODO: Run one time setup script(s)
    }
    catch(Exception eX)
    {
      LOGGER.error(eX);
      throw eX;
    } 
  }
    
  /**
   *  Execute all prepared tests
   */
  private void execute() throws Exception
  {
    try
    {
      LOGGER.info("Start executing test scripts");
      
      // Start test execution
      if(rootTestSuite != null)
      {
        executeTestSuite(rootTestSuite); 
      }
      else
      {
        LOGGER.warn("No tests found to execute!");
      }
    }
    catch(Exception eX)
    {
      LOGGER.error(eX);
      throw eX;
    }  
  }
  
  /**
   * Execute the given test suite recursively
   * 
   * @param testSuite
   */
  private void executeTestSuite(TestSuite testSuite)
  {
    try
    {
      LOGGER.debug("EXEC TS: {}", () -> testSuite.toString());
      
      // First execute all test cases
      for(Map.Entry<String, TestCase> entry : testSuite.getTestCases().entrySet())
      {
        try
        {
          // Load all details of the test script
          inputPlugin.loadTestCase(entry.getValue());
          
          if(webOutputPlugin != null)
            webOutputPlugin.setUp(entry.getValue());
          
          if(shellOutputPlugin != null)
            shellOutputPlugin.setUp(entry.getValue());
          
          // Execute the test script
          entry.getValue().execute();
          
          if(webOutputPlugin != null)
            webOutputPlugin.tearDown(entry.getValue());
          
          if(shellOutputPlugin != null)
            shellOutputPlugin.tearDown(entry.getValue());
        }
        catch(SetUpActionException eX)
        {
          LOGGER.warn(eX);
        }
        catch(TestActionException eX)
        {
          LOGGER.debug(eX);
        }
        catch(TearDownActionException eX)
        {
          LOGGER.warn(eX);
        }
        catch(Exception eX)
        {
          LOGGER.error(eX);
        }
      }
      
      // Second, execute all sub test suites
      for(Map.Entry<String, TestSuite> entry : testSuite.getTestSuites().entrySet())
      {
        try
        {
          executeTestSuite(entry.getValue());
        }
        catch(Exception eX)
        {
          LOGGER.error(eX);
        }
      }
    }
    catch(Exception eX)
    {
      LOGGER.error(eX);
    }
  }
  
  
    
  /**
   *  TearDown after test execution
   *  - Execute the (optional) tear down commands to reset the test environment
   */
  private void tearDown() throws Exception
  {
    try
    {
      LOGGER.info("Teardown Prova");
      
      // TODO: Run one tear down script(s)
    }
    catch(Exception eX)
    {
      LOGGER.error(eX);
      throw eX;
    }       
  }
    
  /**
   *  Prepare Prova for exit 
   *  - Provide a test summary
   *  - Shutdown input plug-in
   *  - Shutdown output plug-in
   *  - Shutdown report plug-in(s) 
   */
  private void shutDown() throws Exception
  {
    try
    {
      LOGGER.info("Shutting down Prova");
      
      // TODO: Provide test summary
      
      // TODO: Shutdown input plug-in
      // TODO: Shutdown output plug-in
      // TODO: Shutdown report plug-in(s)
    }
    catch(Exception eX)
    {
      LOGGER.error(eX);
      throw eX;
    } 
  }
 
  /**
   * Set the root test suite.
   * This test suite should contain the whole structure of test suites and
   * test cases
   * 
   * @param rootTestSuite
   */
  @Override
  public void setRootTestSuite(TestSuite rootTestSuite)
  {
    if(rootTestSuite != null)
    {
      this.rootTestSuite = rootTestSuite;
    }
  }

  
  
  /**
   * Get a reference to the active input plug-in
   * 
   * @return
   */
  @Override
  public InputPlugin getInputPlugin()
  {
    return this.inputPlugin;
  }

  /**
   * Get a reference to the active web action plug-in
   * 
   * @return
   */
  @Override
  public OutputPlugin getWebActionPlugin()
  {
    LOGGER.trace("Request for web action plugin. ({})", () -> this.webOutputPlugin.getName() );
    
    return this.webOutputPlugin;
  }

  /**
   * Get a reference to the active shell action plug-in
   * 
   * @return
   */
  @Override
  public OutputPlugin getShellActionPlugin()
  {
    LOGGER.trace("Request for shell action plugin. ({})", () -> this.shellOutputPlugin.getName() );
    
    return this.shellOutputPlugin;
  }

  /**
   * Get a list of the active report plug-in(s)
   * 
   * @return
   */
  @Override
  public ArrayList<ReportingPlugin> getReportingPlugins()
  {
    // TODO Auto-generated method
    return this.reportPlugins;
  }

  
   
  /**
   * Add a set of properties to the current collection
   * 
   * @param properties
   */
  public void addProperties(Properties properties)
  {
    LOGGER.debug("Adding new properties: " + properties.size());
    
    this.properties.putAll(properties);
  }
  
  /**
   * Get the value of the property with key <key>
   * 
   * @param key
   * @return
   * @throws Exception
   */
  @Override
  public String getPropertyValue(String key) throws Exception
  {
    LOGGER.trace("Get value of property: '{}' ({})", 
                  () -> key, 
                  () -> properties.containsKey(key) ? properties.getProperty(key) : "Not found");
    
    return properties.getProperty(key);
  }
  
  /**
   * Set the value of the property with key <key> to <value
   * 
   * @param key
   * @param value
   * @throws Exception
   */
  @Override
  public void setPropertyValue(String key, String value) throws Exception
  {
    LOGGER.trace("Set value of property with key '{}' to '{}'", () -> key, () -> value);
    
    properties.setProperty(key, value);
  }
  

  
  /**
   * Print all properties (for debug purpose)
   * 
   */
  public void printAllProperties() throws Exception
  {
    for(String key : this.properties.stringPropertyNames())
    {
      System.out.println(key + " => " + properties.getProperty(key));
    }
  }
   
}