package org.sanjoy.uitest.driver;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.Select;
import org.sanjoy.uitest.config.Configuration;
import org.sanjoy.uitest.config.RunMode;

public class TestStepDriver {

	private WebDriver _driver;
	private BrowserName _driverName;
	private Configuration _config;

	public TestStepDriver(Configuration config) {
		_config = config;
	}

	public void takeSnapShot(String desc, String fileName, String include, String regions) {
		takeSnapShot(desc,fileName);
	}

	public void takeSnapShot(String desc, String fileName) {

		//Configuration config = Configuration.getInstance();

		String dirToCopy = (_config.getRunMode() == RunMode.STORE ? _config.getStoreImageDir() : _config.getCompareImageDir());

		String copyToFileName = dirToCopy + File.separatorChar + fileName;

		File imageFile = null;

		if (_driverName == BrowserName.FIREFOX) {
			imageFile = (File) ((FirefoxDriver)_driver).getScreenshotAs(OutputType.FILE);
		} else if (_driverName == BrowserName.CHROME)  {
			imageFile = (File) ((ChromeDriver)_driver).getScreenshotAs(OutputType.FILE);
		} else if (_driverName == BrowserName.IE) {
			imageFile = (File) ((InternetExplorerDriver)_driver).getScreenshotAs(OutputType.FILE);
		}

		try {
			FileUtils.copyFile(imageFile,new File(copyToFileName));
		} catch (IOException e) {
			throw new RuntimeException ("Failed to write captured file : " + copyToFileName + " : " + e.getMessage());
		}
	}

	public void scrollToBottom() {
		if (_driverName == BrowserName.FIREFOX) {
			((FirefoxDriver)_driver).executeScript("window.scrollTo(0, document.body.scrollHeight)");
		} else if (_driverName == BrowserName.CHROME)  {
			((ChromeDriver)_driver).executeScript("window.scrollTo(0, document.body.scrollHeight)");
		} else if (_driverName == BrowserName.IE) {
			((InternetExplorerDriver)_driver).executeScript("window.scrollTo(0, document.body.scrollHeight)");
		}
	}

	public void scrollToTop() {
		if (_driverName == BrowserName.FIREFOX) {
			((FirefoxDriver)_driver).executeScript("window.scrollTo(0, 0)");
		} else if (_driverName == BrowserName.CHROME)  {
			((ChromeDriver)_driver).executeScript("window.scrollTo(0, 0)");
		} else if (_driverName == BrowserName.IE) {
			((InternetExplorerDriver)_driver).executeScript("window.scrollTo(0, 0)");
		}
	}

	public void maximize() {
		_driver.manage().window().maximize();
	}

	public void openBrowser(String browserName) {
		openBrowser(browserName,null);
	}

	public void openBrowser(String browserName, String hostPort) {
		try {
			if (browserName.equalsIgnoreCase("Firefox")) {
				_driver = new FirefoxDriver();
				_driverName = BrowserName.FIREFOX;
			} else if (browserName.equalsIgnoreCase("chrome")) {
				_driver = new ChromeDriver();
				_driverName = BrowserName.CHROME;
			} else if (browserName.equalsIgnoreCase("IE")) {
				_driver = new InternetExplorerDriver();
				_driverName = BrowserName.IE;
			} else if (browserName.equalsIgnoreCase("remote")) {
				try {
					_driver = new RemoteWebDriver(new URL(hostPort), new DesiredCapabilities());
				} catch (MalformedURLException e) {
					throw new RuntimeException("Error executing keyword OpenBrowser , browser name (" + browserName + ") : " + e.getMessage());
				}
			}
		} catch (WebDriverException e) {
			throw new RuntimeException("Error executing keyword OpenBrowser : " + e.getMessage());
		}
	}

	public void enterURL(String URL) {
		_driver.navigate().to(URL);
	}

	public By locatorValue(String locatorType, String value) {
		By by;
		switch (locatorType) {
			case "id":
				by = By.id(value);
				break;
			case "name":
				by = By.name(value);
				break;
			case "xpath":
				by = By.xpath(value);
				break;
			case "css":
				by = By.cssSelector(value);
				break;
			case "linkText":
				by = By.linkText(value);
				break;
			case "partialLinkText":
				by = By.partialLinkText(value);
				break;
			case "class":
				by = By.className(value);
				break;
			default:
				by = null;
				break;
			}
		return by;
	}

	public void selectDropDown(String locatorType, String value, String text) {
		try {
			By locator;
			locator = locatorValue(locatorType, value);
			WebElement element = _driver.findElement(locator);
			Select dropdown = new Select(element);
			dropdown.selectByVisibleText(text);
		} catch (NoSuchElementException e) {
			throw new RuntimeException("No Element Found to enter text : locatorType[" + locatorType + "]" +
										" value[" + value +"]" +" text [" + text + "] : " + e);
		}
	}

	public void enterText(String locatorType, String value, String text) {
		try {
			By locator;
			locator = locatorValue(locatorType, value);
			WebElement element = _driver.findElement(locator);
			element.clear();
			element.sendKeys(text);
		} catch (NoSuchElementException e) {
			throw new RuntimeException("No Element Found to enter text : locatorType[" + locatorType + "]" +
										" value[" + value +"]" +" text [" + text + "] : " + e);
		}
	}

	public void clickOnLink(String locatorType, String value) {
		try {
			By locator;
			locator = locatorValue(locatorType, value);
			WebElement element = _driver.findElement(locator);
			element.click();
		} catch (NoSuchElementException e) {
			throw new RuntimeException("No Link/Button Found click: locatorType[" + locatorType + "] value[" + value +"]" + e);
		}
	}

	public void clickOnButton(String locatorType, String value) {
		clickOnLink(locatorType, value);
	}

	public void closeBrowser() {
		_driver.quit();
	}

	public void sleep(String time) {
		int sleepms = Integer.parseInt(time);
		try { Thread.sleep(sleepms); } catch(Exception ex) {;}
	}

	public void execute(String strMethodName, Object... inputArgs) {
		boolean isVerbose = _config.isVerbose();

		if (isVerbose) {
			System.err.println("Invoking : " + strMethodName + " using : " );
			int count = 1;
			for (Object parm : inputArgs) {
				System.err.println("\tArg "+ count++ + " : " + parm);
			}
		}

		Class<?> params[] = new Class[inputArgs.length];
		Object _instance = this;

		for (int i = 0; i < inputArgs.length; i++) {
			if (inputArgs[i] instanceof String) {
				params[i] = String.class;
			}
		}

		try {
			Method myMethod = this.getClass().getDeclaredMethod(strMethodName, params);
			myMethod.invoke(_instance, inputArgs);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException("Invalid parameters in test steps file, Method invoked with wrong number of arguments : Method : " + strMethodName);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException("Invalid Keyword or parameters in test steps file ::" + strMethodName + ":- method does not exists.");
		} catch (InvocationTargetException e) {
			throw new RuntimeException("Exception thrown by an invoked method : " + (e.getCause()!=null?e.getCause().getMessage():""));
		} catch (IllegalAccessException e) {
			throw new RuntimeException("Can not access a member of class with modifiers private: Method : " + strMethodName);
		}
	}

	public void tearDown() {
		if (_driver != null)
			_driver.quit();
	}
}