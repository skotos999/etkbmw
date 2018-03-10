import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;

import javax.imageio.ImageIO;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

class Main {

	static void extractCarData(final WebDriver driver, final By mgGroupsLocator, final By funcGroupsLocator, final By partsBlockLocator,
			final LinkedHashMap<String, String> carLinks) {
		carLinks.forEach((k, v) -> {
			System.out.println(Instant.now() + " - Started - " + k);
			driver.navigate().to(k);
			final List<WebElement> mgGroups = driver.findElements(mgGroupsLocator);
			final LinkedHashMap<String, String> mgGroupLinks = saveImages(v, mgGroups, false);

			mgGroupLinks.forEach((k2, v2) -> {
				driver.navigate().to(k2);
				final List<WebElement> funcGroups = driver.findElements(funcGroupsLocator);
				final LinkedHashMap<String, String> funcGroupLinks = saveImages(v2, funcGroups, true);

				funcGroupLinks.forEach((k3, v3) -> {
					driver.navigate().to(k3);
					try {
						savePartsImg(driver, v3, driver.findElement(By.xpath(".//*[@class='partsImg']")));
						final List<WebElement> partsBlocks = driver.findElements(partsBlockLocator);

						partsBlocks.forEach(partsBlock -> {
							final String fileName = v3 + "\\" + (partsBlocks.indexOf(partsBlock) + 1) + ".txt";

							try {
								final BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));
								if ("partsBlock".equals(partsBlock.getAttribute("class"))) {
									final String text = partsBlock.findElement(By.xpath(".//*[@class='label label-default']")).getText();
									writer.append(text);
									writer.append('\n');
									extractPartData(writer, partsBlock);
								} else {
									extractPartData(writer, partsBlock);
								}
								writer.close();
							} catch (final IOException e) {
								e.printStackTrace();
							}
						});

					} catch (Exception e) {
						return;
					}
				});
			});
			System.out.println(Instant.now() + " - Finished - " + k);
		});
	}

	private static void extractPartData(final BufferedWriter writer, final WebElement partsBlock) {
		final WebElement table = partsBlock.findElement(By.xpath(".//*[@class='groupParts table HU']"));
		final List<WebElement> rows = table.findElements(By.xpath("./tbody/tr"));
		rows.forEach(row -> {
			final List<WebElement> columns = row.findElements(By.xpath("./td"));
			final String no = columns.get(0).getText();
			final String desc = columns.get(1).getText();
			final String xtraInfo = columns.get(2).getText();
			final String condition = columns.get(3).getText();
			final String partNr = columns.get(5).getText();

			final String line = no + ";" + desc + ";" + xtraInfo + ";" + condition + ";" + partNr;
			try {
				writer.append(line);
				writer.append('\n');
			} catch (final IOException e) {
				e.printStackTrace();
			}
		});
	}

	static LinkedHashMap<String, String> getEuropeanCarLinks(final List<WebElement> cars, final String path) {
		System.out.println("Link extraction has been started.");
		final LinkedHashMap<String, String> result = new LinkedHashMap<>();
		for (final WebElement car : cars) {
			final String href = car.getAttribute("href");
			if (href.contains("ECE") && !href.contains("One") && !href.contains("Coop") && !href.contains("JCW")
					&& !href.contains("MOSP")) {
				final String eCode = href.split("/selectCar/")[1].split("/")[0];
				final String modelName = car.getAttribute("innerHTML");
				final String title = car.getAttribute("title");
				final String subdir = eCode + "\\" + modelName + "\\" + title;
				result.put(href, createFolders(path, subdir));
			}
		}
		System.out.println("All links have been extracted");
		return result;
	}

	private static LinkedHashMap<String, String> saveImages(final String path, final List<WebElement> elements, final boolean isFuncGroup) {
		final LinkedHashMap<String, String> result = new LinkedHashMap<>();
		for (final WebElement a : elements) {
			final String href = a.getAttribute("href");
			final String mgGroupId = isFuncGroup ? a.findElement(By.xpath(".//span")).getAttribute("innerHTML")
					: href.substring(href.length() - 2);
			final String imageFolderPath = createFolder(path, mgGroupId);

			final WebElement img = a.findElement(By.xpath(".//img"));
			final String title = isFuncGroup ? "thumbnail" : img.getAttribute("title").replaceAll("[\\\\/:_*?\"|<>.&]+", "_");
			final String src = img.getAttribute("src");
			saveImage(imageFolderPath, title, src);
			result.put(href, imageFolderPath);
		}
		return result;
	}

	private static void savePartsImg(final WebDriver driver, final String savePath, final WebElement img) {
		final String title = img.getAttribute("title").replaceAll("[\\\\/:_*?\"|<>.&]+", "_");
		final String src = img.getAttribute("src");
		driver.navigate().to(src);
		final String realSrc = driver.findElement(By.tagName("img")).getAttribute("src");
		saveImage(savePath, title, realSrc);
		driver.navigate().back();
	}

	private static String createFolder(final String path, final String subDir) {
		final File dir = new File(path + "\\" + subDir);
		if (!(dir.exists())) {
			dir.mkdir();
		}
		return dir.getPath();
	}

	private static String createFolders(final String path, final String subDir) {
		final File dir = new File(path + "\\" + subDir);
		if (!(dir.exists())) {
			dir.mkdirs();
		}
		return dir.getPath();
	}

	private static void saveImage(final String path, final String name, final String src) {
		final BufferedImage image;
		try {
			image = ImageIO.read(new URL(src));
			final File outputImage = new File(path + "\\" + name + ".jpg");
			if (!outputImage.exists()) {
				ImageIO.write(image, "jpg", outputImage);
			}
		} catch (final IOException e) {
			System.out.println("Something went wrong: " + src + " ; " + path + " ; " + name + " ;");
		}
	}
}
