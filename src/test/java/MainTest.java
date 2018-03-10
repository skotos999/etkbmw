import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class MainTest {

	private static final String PATH = "c:\\Users\\skoto\\Desktop\\etk_bot\\";
	private static final By modelsLocator = By.xpath("//*[@class='thumbnails models']/li/a");
	private static final By mgGroupsLocator = By.xpath("//*[@class='thumbnail']");
	private static final By funcGroupsLocator = By.xpath("//*[@class='thumbnail hoverLegend']");
	private static final By partsBlockLocator = By.xpath("//*[contains(@class, 'partsBlock')]");
	private static ChromeOptions options;
	private static LinkedHashMap<String, String> carLinks;

	@BeforeClass
	public static void init() {
		System.setProperty("webdriver.chrome.driver", "d:\\!DEV_NEW\\jetbrains\\chromedriver.exe");
		options = new ChromeOptions();
		options.setBinary("c:\\Program Files (x86)\\Google\\Chrome Beta\\Application\\chrome.exe");
		// options.addArguments("--start-maximized");
		options.addArguments("headless");
		options.addArguments("window-size=1920x1080");

		final WebDriver driver = new ChromeDriver(options);
		driver.get("https://www.etkbmw.com/");

		final List<WebElement> cars = driver.findElements(modelsLocator);
		carLinks = Main.getEuropeanCarLinks(cars, PATH);

		driver.close();
	}

	@Test
	public void extractCarDataOne() {
		final WebDriver driver = new ChromeDriver(options);
		driver.get("https://www.etkbmw.com/");
		final LinkedHashMap<String, String> partialCarLinks = getPartialLinkedHashMap(carLinks, 653, 657);
		Main.extractCarData(driver, mgGroupsLocator, funcGroupsLocator, partsBlockLocator, partialCarLinks);
		driver.close();
	}

	@Test
	public void extractCarDataTwo() {
		final WebDriver driver = new ChromeDriver(options);
		driver.get("https://www.etkbmw.com/");
		final LinkedHashMap<String, String> partialCarLinks = getPartialLinkedHashMap(carLinks, 657, 661);
		Main.extractCarData(driver, mgGroupsLocator, funcGroupsLocator, partsBlockLocator, partialCarLinks);
		driver.close();
	}

	@Test
	public void extractCarDataThree() {
		final WebDriver driver = new ChromeDriver(options);
		driver.get("https://www.etkbmw.com/");
		final LinkedHashMap<String, String> partialCarLinks = getPartialLinkedHashMap(carLinks, 661, 665);
		Main.extractCarData(driver, mgGroupsLocator, funcGroupsLocator, partsBlockLocator, partialCarLinks);
		driver.close();
	}

	@Test
	public void extractCarDataFour() {
		final WebDriver driver = new ChromeDriver(options);
		driver.get("https://www.etkbmw.com/");
		final LinkedHashMap<String, String> partialCarLinks = getPartialLinkedHashMap(carLinks, 665, 669);
		Main.extractCarData(driver, mgGroupsLocator, funcGroupsLocator, partsBlockLocator, partialCarLinks);
		driver.close();
	}

	@Ignore
	@Test
	public void extractCarDataFive() {
		final WebDriver driver = new ChromeDriver(options);
		driver.get("https://www.etkbmw.com/");
		final LinkedHashMap<String, String> partialCarLinks = getPartialLinkedHashMap(carLinks, 640, 641);
		Main.extractCarData(driver, mgGroupsLocator, funcGroupsLocator, partsBlockLocator, partialCarLinks);
		driver.close();
	}

	private LinkedHashMap<String, String> getPartialLinkedHashMap(final LinkedHashMap<String, String> map, final int from, int to) {
		System.out.println("The partition has been started: [" + from + ";" + to + "[.");
		final List<String> keyList = new ArrayList<>(map.keySet());
		final List<String> valueList = new ArrayList<>(map.values());

		if (to <= from) {
			to = from + 1;
		}

		final List<String> subKeyList = keyList.subList(from - 1, to - 1);
		final List<String> subValueList = valueList.subList(from - 1, to - 1);

		final LinkedHashMap<String, String> result = new LinkedHashMap<>();

		for (int i = 0; i < (to - from); i++) {
			result.put(subKeyList.get(i), subValueList.get(i));
		}
		System.out.println("The partition has been completed.");
		return result;
	}
}
