package com.qa.framework.pages;

import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Comment;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.PointerInput;
import org.openqa.selenium.interactions.Sequence;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.DefaultElementLocatorFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import com.qa.framework.engine.DriverManager;

public abstract class BasePage {

    private static final Logger LOG = LogManager.getLogger(BasePage.class);
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(60);
    private static final Duration SHORT_TIMEOUT = Duration.ofSeconds(10);

    private static final ThreadLocal<Queue<Runnable>> ACTION_QUEUE =
            ThreadLocal.withInitial(ConcurrentLinkedQueue::new);

    @FindBy(xpath = "//iframe[contains(@id,'ifr_LaunchWin')]")
    protected WebElement Mainframe_common;

    @FindBy(xpath = "(//iframe[contains(@id,'ifr_LaunchWin')])[2]")
    protected WebElement Mainframe_common1;

    @FindBy(xpath = "(//*[@class='SPNtbltwoC'])[1]")
    protected WebElement Instrum_errorMsg;

    @FindBy(xpath = "(//*[@class='SPNtbltwoC'])[1]")
    protected WebElement Instrum_text;

    @FindBy(className = "WNDtitletxt")
    protected WebElement Actual_Assert;

    @FindBy(xpath = "//*[contains(@src,'TempForward.jsp?action=SMSStartLogServlet&funcid=CSDTFACE')]")
    protected WebElement Frame_TFEntries;

    @FindBy(xpath = "//*[contains(@src,'TempForward.jsp?action=SMSStartLogServlet&funcid=CSDTFEVN')]")
    protected WebElement Frame_TFEvent;

    @FindBy(id = "1")
    protected WebElement SubScreen_Field_Path;

    @FindBy(xpath = "//button[contains(@class,'BTNtext') and contains(text(),'Fetch')]")
    protected WebElement Fetch;

    protected BasePage() {
        PageFactory.initElements(new DefaultElementLocatorFactory(new SearchContext() {
            @Override
            public WebElement findElement(By by) {
                return DriverManager.getDriver().findElement(by);
            }

            @Override
            public List<WebElement> findElements(By by) {
                return DriverManager.getDriver().findElements(by);
            }
        }), this);
    }

    protected WebDriver driver() {
        return DriverManager.getDriver();
    }

    protected WebDriverWait waitFor() {
        return new WebDriverWait(driver(), DEFAULT_TIMEOUT);
    }

    protected static void logInfo(String message) {
        LOG.info(message);
    }

    protected static void logError(String message) {
        LOG.error(message);
    }

    protected static void logError(String message, Throwable error) {
        LOG.error(message, error);
    }

    public void selectByVisibleText(WebElement element, String text) {
        waitForVisibility(element);
        new Select(element).selectByVisibleText(text);
    }

    public void selectByValue(WebElement element, String value) {
        waitForVisibility(element);
        new Select(element).selectByValue(value);
    }

    public void chooseFromDropdown(WebElement element, String text) {
        selectByVisibleText(element, text);
    }

    public String GetAttributeValue(WebElement element) {
        return getAttribute(element, "value");
    }

    public void waitForVisibility(WebElement element) {
        waitForVisibility(element, DEFAULT_TIMEOUT.getSeconds());
    }

    public void waitForVisibility(WebElement element, long seconds) {
        new WebDriverWait(driver(), Duration.ofSeconds(seconds))
                .until(ExpectedConditions.visibilityOf(element));
    }

    public void waitForVisibility(WebElement element, long seconds, String message) {
        try {
            waitForVisibility(element, seconds);
        } catch (TimeoutException e) {
            throw new TimeoutException(message, e);
        }
    }

    public void waitForVisibility(By locator) {
        waitFor().until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected WebElement visibleElement(By locator) {
        return waitFor().until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public void waitForDisapear(WebElement element) {
        waitFor().until(ExpectedConditions.invisibilityOf(element));
    }

    public void clear(WebElement element) {
        waitForVisibility(element);
        element.clear();
        logInfo("Text cleared from element");
    }

    public void clear(WebElement element, String message) {
        clear(element);
        logInfo(message);
    }

    public static String getselectedvalue(WebElement element) {
        return new Select(element).getFirstSelectedOption().getText();
    }

    public String click(WebElement element) {
        waitFor().until(ExpectedConditions.elementToBeClickable(element)).click();
        logInfo("Clicked element");
        return "Clicking Element";
    }

    public void Click(WebElement element) {
        click(element);
    }

    public void click(WebElement element, int seconds) throws Exception {
        waitForVisibility(element, seconds);
        waitFor().until(ExpectedConditions.elementToBeClickable(element)).click();
    }

    public void click(WebElement element, String message) {
        waitFor().until(ExpectedConditions.elementToBeClickable(element)).click();
        logInfo(message);
    }

    public void click(By locator) throws Exception {
        waitFor().until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    public void click(By locator, String message) {
        waitFor().until(ExpectedConditions.elementToBeClickable(locator)).click();
        logInfo(message);
    }

    public void Close() {
        driver().quit();
    }

    public void popuptable(String text) {
        click(By.xpath("//a[contains(text(),'" + text + "')]"), "Clicked popup table value: " + text);
    }

    public static void Assertvalueforselect(WebElement element, String expected) {
        Assert.assertEquals(new Select(element).getFirstSelectedOption().getText().trim(),
                expected.trim());
    }

    public void scrollUntilElementDisplayed(WebElement element) {
        ((JavascriptExecutor) driver()).executeScript(
                "arguments[0].scrollIntoView({block:'center',inline:'nearest'});", element);
    }

    public void scrolltobottom(WebElement element) {
        ((JavascriptExecutor) driver()).executeScript(
                "window.scrollTo(0, document.body.scrollHeight);");
        waitForVisibility(element);
    }

    public void waitforelementtopresent(String locatorExpression) {
        if (locatorExpression == null || !locatorExpression.contains("|")) {
            throw new IllegalArgumentException(
                    "Locator must use format type|value. Received: " + locatorExpression);
        }

        String[] values = locatorExpression.split("\\|", 2);
        String type = values[0].trim().toLowerCase(Locale.ROOT);
        String value = values[1].trim();

        By locator;
        switch (type) {
            case "xpath":
                locator = By.xpath(value);
                break;
            case "id":
                locator = By.id(value);
                break;
            case "class":
            case "classname":
                locator = By.className(value);
                break;
            case "name":
                locator = By.name(value);
                break;
            case "css":
            case "cssselector":
                locator = By.cssSelector(value);
                break;
            default:
                throw new IllegalArgumentException("Unsupported locator type: " + type);
        }

        waitFor().until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    public void sendKeys(WebElement element, String text) throws Exception {
        try {
            waitFor().until(ExpectedConditions.visibilityOf(element));
            element.clear();
            element.sendKeys(text);
            logInfo("Entered text into element");
        } catch (RuntimeException e) {
            throw new Exception("There is no element available to enter the value", e);
        }
    }

    public void sendKeys(WebElement element, Keys key) throws Exception {
        try {
            waitFor().until(ExpectedConditions.visibilityOf(element));
            element.sendKeys(key);
        } catch (RuntimeException e) {
            throw new Exception("There is no element available to send the key", e);
        }
    }

    public void sendKeys(WebElement element, String text, String message) {
        waitForVisibility(element);
        element.sendKeys(text);
        logInfo(message);
    }

    public void clickAndSendKeys(WebElement element, String text) {
        clickAndSendKeys(element, text, "Entered text into element");
    }

    public void clickAndSendKeys(WebElement element, String text, String message) {
        waitFor().until(ExpectedConditions.elementToBeClickable(element)).click();
        element.sendKeys(text);
        logInfo(message);
    }

    public void clickAndSendKeys(By locator, String text) {
        clickAndSendKeys(locator, text, "Entered text into element");
    }

    public void clickAndSendKeys(By locator, String text, String message) {
        WebElement element = waitFor().until(ExpectedConditions.elementToBeClickable(locator));
        element.click();
        element.sendKeys(text);
        logInfo(message);
    }

    public String getAttribute(WebElement element, String attribute) {
        waitForVisibility(element);
        return element.getAttribute(attribute);
    }

    public String getAttribute(By locator, String attribute) {
        return waitFor().until(ExpectedConditions.presenceOfElementLocated(locator))
                .getAttribute(attribute);
    }

    public String getTextWithAttribute(WebElement element, String attribute) {
        return element.getAttribute(attribute);
    }

    public String getTextWithAttribute(WebElement element) {
        return element.getAttribute("value");
    }

    public static void Assert(WebElement element, String expected) {
        Assert.assertEquals(element.getText().trim(), expected.trim());
    }

    public static void Asserttext(WebElement element, String expected) {
        Assert.assertEquals(element.getText().trim(), expected.trim());
    }

    public static void Assertvalue(WebElement element, String expected) {
        Assert.assertEquals(element.getAttribute("value").trim(), expected.trim());
    }

    public void AsserttextWithVisibility(WebElement element, String expected) {
        waitForVisibility(element, 50);
        Assert.assertEquals(element.getText().trim(), expected.trim());
    }

    public static void Asserttext1(WebElement element, String expected) {
        String[] actual = element.getText().split(":", 2);
        Assert.assertTrue(actual.length == 2, "Text does not contain ':' separator");
        Assert.assertEquals(actual[1].trim(), expected.trim());
    }

    public static void Assertvalue1(WebElement element, int expected) {
        Assert.assertEquals(Integer.parseInt(element.getAttribute("value")), expected);
    }

    public static void AssertExist(WebElement element) {
        Assert.assertNotNull(element, "Element is null");
        Assert.assertTrue(element.isDisplayed(), "Element is not displayed");
    }

    public static String get_Attribute_value(WebElement element) {
        return element.getAttribute("value");
    }

    public static String gettext(WebElement element) {
        return element.getText().trim();
    }

    public void dynamic_Click(String dynamicData) {
        click(By.xpath("//*[contains(text(),'" + dynamicData + "')]"), "Clicked element with text: " + dynamicData);
    }

    public void jsclick(WebElement element) {
        waitForVisibility(element);
        ((JavascriptExecutor) driver()).executeScript("arguments[0].click();", element);
    }

    public void jsclickWithoutWait(WebElement element) throws Exception {
        try {
            ((JavascriptExecutor) driver()).executeScript("arguments[0].click();", element);
        } catch (RuntimeException e) {
            throw new Exception("There is no element available to click", e);
        }
    }

    public void waitForPageLoad() {
        new WebDriverWait(driver(), DEFAULT_TIMEOUT).until(webDriver ->
                "complete".equals(((JavascriptExecutor) webDriver)
                        .executeScript("return document.readyState")));
    }

    public void waitForPage() {
        waitForPageLoad();
    }

    public void waitFortellerPage() {
        waitForPageLoad();
    }

    public void waitForPageLoading() {
        By firstLoader = By.id("cmn_loader");
        By secondLoader = By.cssSelector("[data-bind='visible: loading']");
        WebDriverWait loaderWait = new WebDriverWait(driver(), Duration.ofSeconds(180));
        loaderWait.until(webDriver ->
                areAllInvisible(firstLoader) && areAllInvisible(secondLoader));
    }

    private boolean areAllInvisible(By locator) {
        List<WebElement> elements = driver().findElements(locator);
        return elements.isEmpty() || elements.stream().noneMatch(this::isDisplayedSafely);
    }

    private boolean isDisplayedSafely(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (RuntimeException e) {
            return false;
        }
    }

    public void waitForRetrieve(WebElement element) throws Exception {
        try {
            new WebDriverWait(driver(), SHORT_TIMEOUT)
                    .until(ExpectedConditions.attributeToBeNotEmpty(element, "value"));
        } catch (TimeoutException e) {
            throw new Exception("The data was not retrieved", e);
        }
    }

    public void enterCurrentDate(WebElement element) throws Exception {
        driver().switchTo().defaultContent();
        String currentDate = waitFor().until(ExpectedConditions.visibilityOfElementLocated(By.id("dateli"))).getText();
        switchToMainFrame();
        sendKeys(element, currentDate);
    }

    public void magnifierSearchWithoutWait(
            WebElement searchIcon, String title, String text) throws Exception {
        click(searchIcon);
        switchToSubScreen(title);
        sendKeys(SubScreen_Field_Path, text);
        jsclick(Fetch);
        click(By.xpath("//a[contains(text(),'" + text + "')]"), "Selected value: " + text);
        driver().switchTo().parentFrame();
    }

    public void magnifierSearchForDifferentElement(
            WebElement searchIcon, String title, String text, String chooseText) throws Exception {
        click(searchIcon);
        switchToSubScreen(title);
        sendKeys(SubScreen_Field_Path, text);
        jsclick(Fetch);
        click(By.xpath("//a[contains(text(),'" + chooseText + "')]"), "Selected value: " + chooseText);
        driver().switchTo().parentFrame();
    }

    public void switchToSubScreen(String title) throws Exception {
        switchToNamedFrame("ifrSubScreen", By.className("WNDtitletxt"), title);
    }

    public void switchto_Subscreen_Frame(String title) throws Exception {
        switchToSubScreen(title);
    }

    public void switchtoSubscreenFrame(String title) throws Exception {
        switchToSubScreen(title);
    }

    public void switchto_Alert_Frame(String title) throws Exception {
        switchToNamedFrame("ifr_AlertWin", By.className("WNDtitletxt"), title);
    }

    public void switchtoAlert_Frame(String title) throws Exception {
        switchToNamedFrame("ifr_AlertWin", By.xpath("//*[@id='wndtitle']/h1"), title);
    }

    private void switchToNamedFrame(String frameId, By titleLocator, String expectedTitle)
            throws Exception {
        try {
            waitFor().until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.id(frameId)));
            WebElement title = waitFor().until(ExpectedConditions.visibilityOfElementLocated(titleLocator));
            Assert.assertEquals(title.getText().trim(), expectedTitle.trim());
        } catch (RuntimeException | AssertionError e) {
            throw new Exception(
                    "There is no frame to switch with title: " + expectedTitle, e);
        }
    }

    public String switchto_Alert_Frame_without_Exception(String title) {
        waitFor().until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(By.id("ifr_AlertWin")));
        return waitFor().until(ExpectedConditions.visibilityOfElementLocated(By.className("WNDtitletxt"))).getText();
    }

    public void switchToFrame(WebElement element) throws Exception {
        try {
            waitFor().until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(element));
        } catch (RuntimeException e) {
            throw new Exception("Unable to switch to the supplied frame", e);
        }
    }

    public void switchToFrame(WebElement element, String expectedText) throws Exception {
        switchToFrame(element);
        Assert(Actual_Assert, expectedText);
    }

    public void switchToFrame(int index) {
        waitFor().until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(index));
    }

    public void switchToMainFrame() throws Exception {
        driver().switchTo().defaultContent();
        switchToFrame(Mainframe_common);
    }

    public void switchMainFrame() throws Exception {
        driver().switchTo().defaultContent();
        switchToFrame(Mainframe_common1);
    }

    public void switchEventFrame() throws Exception {
        driver().switchTo().defaultContent();
        switchToFrame(Frame_TFEvent);
    }

    public void switchEntriesFrame() throws Exception {
        driver().switchTo().defaultContent();
        switchToFrame(Frame_TFEntries);
        Asserttext(waitFor().until(ExpectedConditions.visibilityOfElementLocated(By.className("WNDtitletxt"))),
                "Trade Finance Accounting Entries");
    }

    public void wait_for_Pulling(WebElement element) {
        waitForVisibility(element, 25);
    }

    public void Switch_Window_handling() {
        switchToAnotherWindow(2);
    }

    public void Switch_Window_handling(int numberOfWindows) {
        switchToAnotherWindow(numberOfWindows);
    }

    private void switchToAnotherWindow(int numberOfWindows) {
        String currentWindow = driver().getWindowHandle();
        waitFor().until(ExpectedConditions.numberOfWindowsToBe(numberOfWindows));
        for (String window : driver().getWindowHandles()) {
            if (!window.equals(currentWindow)) {
                driver().switchTo().window(window);
                return;
            }
        }
        throw new IllegalStateException("A child window was not found");
    }

    public void Switch_Window(int windowIndex) {
        waitFor().until(webDriver -> webDriver.getWindowHandles().size() > windowIndex);
        List<String> handles = new ArrayList<>(driver().getWindowHandles());
        if (windowIndex < 0 || windowIndex >= handles.size()) {
            throw new IllegalArgumentException("Window index does not exist: " + windowIndex);
        }
        driver().switchTo().window(handles.get(windowIndex));
    }

    public void scroll(String direction) {
        scroll(direction, 0.75);
    }

    public void scroll(String direction, double scrollRatio) {
        Dimension size = driver().manage().window().getSize();
        swipeForDirection(direction, scrollRatio,
                new Point(size.getWidth() / 2, size.getHeight() / 2), size);
    }

    public void scrollWithCoordinates(
            String direction, double scrollRatio, int x, int y) {
        Dimension size = driver().manage().window().getSize();
        swipeForDirection(direction, scrollRatio, new Point(x, y), size);
    }

    private void swipeForDirection(
            String direction, double ratio, Point midpoint, Dimension size) {
        if (ratio <= 0 || ratio > 1) {
            throw new IllegalArgumentException("Scroll ratio must be greater than 0 and at most 1");
        }

        int verticalOffset = (int) (size.getHeight() * ratio * 0.5);
        int horizontalOffset = (int) (size.getWidth() * ratio * 0.5);

        Point top = new Point(midpoint.x, midpoint.y - verticalOffset);
        Point bottom = new Point(midpoint.x, midpoint.y + verticalOffset);
        Point left = new Point(midpoint.x - horizontalOffset, midpoint.y);
        Point right = new Point(midpoint.x + horizontalOffset, midpoint.y);

        switch (direction.toUpperCase(Locale.ROOT)) {
            case "UP":
                swipe(bottom, top);
                break;
            case "DOWN":
                swipe(top, bottom);
                break;
            case "LEFT":
                swipe(right, left);
                break;
            case "RIGHT":
                swipe(left, right);
                break;
            default:
                throw new IllegalArgumentException("Unsupported direction: " + direction);
        }
    }

    private void swipe(Point source, Point target) {
        PointerInput finger = new PointerInput(PointerInput.Kind.TOUCH, "finger");
        Sequence sequence = new Sequence(finger, 0);
        sequence.addAction(finger.createPointerMove(
                Duration.ZERO, PointerInput.Origin.viewport(), source.x, source.y));
        sequence.addAction(finger.createPointerDown(PointerInput.MouseButton.LEFT.asArg()));
        sequence.addAction(finger.createPointerMove(
                Duration.ofMillis(700), PointerInput.Origin.viewport(), target.x, target.y));
        sequence.addAction(finger.createPointerUp(PointerInput.MouseButton.LEFT.asArg()));
        ((RemoteWebDriver) driver()).perform(List.of(sequence));
    }

    public void tapOnPosition(int x, int y) {
        swipe(new Point(x, y), new Point(x, y));
    }

    public void scrollUntilElementPresent(By locator, String direction, double ratio) {
        scrollUntilElementPresent(locator, direction, ratio, 60);
    }

    public void scrollUntilElementPresent(
            By locator, String direction, double ratio, long seconds) {
        Instant timeout = Instant.now().plusSeconds(seconds);
        while (driver().findElements(locator).isEmpty()) {
            scroll(direction, ratio);
            if (Instant.now().isAfter(timeout)) {
                throw new TimeoutException("Element is not present on the page: " + locator);
            }
        }
    }

    public void scrollUntilElementDisplayed(By locator, String direction, double ratio) {
        Instant timeout = Instant.now().plusSeconds(60);
        while (true) {
            List<WebElement> elements = driver().findElements(locator);
            if (!elements.isEmpty() && isDisplayedSafely(elements.get(0))) {
                return;
            }
            scroll(direction, ratio);
            if (Instant.now().isAfter(timeout)) {
                throw new TimeoutException("Element is not displayed on the page: " + locator);
            }
        }
    }

    public static void add(Runnable action) {
        ACTION_QUEUE.get().add(action);
    }

    public static void runAll() {
        Queue<Runnable> queue = ACTION_QUEUE.get();
        try {
            Runnable action;
            while ((action = queue.poll()) != null) {
                action.run();
            }
        } finally {
            ACTION_QUEUE.remove();
        }
    }

    public static void clear() {
        ACTION_QUEUE.remove();
    }

    public void queueClick(WebElement element) {
        add(() -> waitFor().until(ExpectedConditions.elementToBeClickable(element)).click());
    }

    public static String getNextFileAndRename(String baseDir, String fileName)
            throws IOException {
        Path oldPath = Paths.get(baseDir, fileName);
        if (!Files.isRegularFile(oldPath)) {
            throw new IOException("File not found: " + oldPath.toAbsolutePath());
        }

        Matcher matcher = Pattern.compile("(.*?)(\\d+)(\\.[^.]+)$").matcher(fileName);
        if (!matcher.matches()) {
            throw new IOException("Filename does not end with a numeric value: " + fileName);
        }

        String newName = matcher.group(1)
                + (Integer.parseInt(matcher.group(2)) + 1)
                + matcher.group(3);
        Path newPath = oldPath.resolveSibling(newName);
        return Files.move(oldPath, newPath).toAbsolutePath().toString();
    }

    public static void updateProperty(String key, String value, String configPath)
            throws IOException {
        Properties properties = new Properties();
        Path path = Paths.get(configPath);
        try (FileInputStream input = new FileInputStream(path.toFile())) {
            properties.load(input);
        }
        properties.setProperty(key, value);
        try (FileOutputStream output = new FileOutputStream(path.toFile())) {
            properties.store(output, "Updated property");
        }
    }

    public static String getLastInstrumentNo(
            String excelPath, String sheetName, String columnName) throws IOException {
        try (FileInputStream input = new FileInputStream(excelPath);
             XSSFWorkbook workbook = new XSSFWorkbook(input)) {
            XSSFSheet sheet = requireSheet(workbook, sheetName);
            Row header = requireHeader(sheet);
            int instrumentColumn = findColumn(header, columnName);
            int statusColumn = findColumn(header, "Status");
            String result = null;
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) continue;
                String status = cellText(row.getCell(statusColumn));
                if (!status.isBlank()) {
                    String instrument = cellText(row.getCell(instrumentColumn));
                    if (!instrument.isBlank()) result = instrument;
                }
            }
            return removeTrailingDecimalZero(result);
        }
    }

    public static String readexcelcell(
            String columnName, String excelPath, String sheetName) throws IOException {
        try (FileInputStream input = new FileInputStream(excelPath);
             XSSFWorkbook workbook = new XSSFWorkbook(input)) {
            XSSFSheet sheet = requireSheet(workbook, sheetName);
            int column = findColumn(requireHeader(sheet), columnName);
            String value = null;
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) continue;
                String candidate = cellText(row.getCell(column));
                if (!candidate.isBlank()) value = candidate;
            }
            return value;
        }
    }

    public static String readexcelcell_withstatuscheck(
            String columnName, String excelPath, String sheetName) throws IOException {
        try (FileInputStream input = new FileInputStream(excelPath);
             XSSFWorkbook workbook = new XSSFWorkbook(input)) {
            XSSFSheet sheet = requireSheet(workbook, sheetName);
            Row header = requireHeader(sheet);
            int valueColumn = findColumn(header, columnName);
            int statusColumn = findColumnIgnoreCase(header, "Status");
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) continue;
                if (cellText(row.getCell(statusColumn)).isBlank()) {
                    String value = cellText(row.getCell(valueColumn));
                    if (!value.isBlank()) return value;
                }
            }
            return null;
        }
    }

    public static Map<String, String> readExcelValuesWithHeaders(
            String[] headers, String excelPath, String sheetName) throws IOException {
        Map<String, String> result = new HashMap<>();
        try (FileInputStream input = new FileInputStream(excelPath);
             XSSFWorkbook workbook = new XSSFWorkbook(input)) {
            XSSFSheet sheet = requireSheet(workbook, sheetName);
            Row headerRow = requireHeader(sheet);
            Map<String, Integer> columns = getColumnIndexMap(headerRow);
            Integer statusColumn = findColumnOptional(columns, "Status");
            for (String requestedHeader : headers) {
                Integer column = findColumnOptional(columns, requestedHeader);
                if (column == null) {
                    throw new IllegalArgumentException("Header not found: " + requestedHeader);
                }
                for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                    Row row = sheet.getRow(rowIndex);
                    if (row == null) continue;
                    if (statusColumn != null && !cellText(row.getCell(statusColumn)).isBlank()) continue;
                    String value = cellText(row.getCell(column));
                    if (!value.isBlank()) {
                        result.put(requestedHeader, value);
                        break;
                    }
                }
            }
        }
        return result;
    }

    public static void writeMulitpleValuesToColumns(
            String excelPath, String sheetName, Map<String, String> values) throws IOException {
        updateWorkbook(excelPath, workbook -> {
            XSSFSheet sheet = requireSheet(workbook, sheetName);
            Map<String, Integer> columns = getColumnIndexMap(requireHeader(sheet));
            Row row = sheet.createRow(sheet.getLastRowNum() + 1);
            values.forEach((header, value) -> {
                Integer column = findColumnOptional(columns, header);
                if (column == null) throw new IllegalArgumentException("Column not found: " + header);
                row.createCell(column, CellType.STRING).setCellValue(value);
            });
        });
    }

    public static void writeValuesByReferenceColumn(
            String excelPath, String sheetName, String referenceColumnName,
            String referenceValue, Map<String, String> values) throws IOException {
        updateWorkbook(excelPath, workbook -> {
            XSSFSheet sheet = requireSheet(workbook, sheetName);
            Map<String, Integer> columns = getColumnIndexMap(requireHeader(sheet));
            Integer referenceColumn = findColumnOptional(columns, referenceColumnName);
            if (referenceColumn == null) {
                throw new IllegalArgumentException("Reference column not found: " + referenceColumnName);
            }
            Row target = null;
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row != null && cellText(row.getCell(referenceColumn)).equalsIgnoreCase(referenceValue)) {
                    target = row;
                    break;
                }
            }
            if (target == null) {
                throw new IllegalArgumentException(
                        "No row found where " + referenceColumnName + " = " + referenceValue);
            }
            final Row targetRow = target;
            values.forEach((header, value) -> {
                Integer column = findColumnOptional(columns, header);
                if (column == null) throw new IllegalArgumentException("Column not found: " + header);
                Cell cell = targetRow.getCell(column, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                cell.setCellValue(value);
            });
        });
    }

    public static void wrightexcelcell_status(
            String columnName, String excelPath, String inputValue, String sheetName)
            throws IOException {
        writexcelcell(columnName, excelPath, inputValue, sheetName);
    }

    public static void writexcelcell(
            String columnName, String excelPath, String inputValue, String sheetName)
            throws IOException {
        updateWorkbook(excelPath, workbook -> {
            XSSFSheet sheet = requireSheet(workbook, sheetName);
            int column = findColumn(requireHeader(sheet), columnName);
            if ("New_cheque_No".equalsIgnoreCase(columnName)) {
                BigInteger start = new BigInteger(inputValue).add(BigInteger.TEN);
                for (int i = 0; i < 25; i++) {
                    int rowIndex = findlastrow(sheet, column);
                    Row row = getOrCreateRow(sheet, rowIndex);
                    row.createCell(column, CellType.STRING)
                            .setCellValue(String.format("%014d", start.add(BigInteger.valueOf(i))));
                }
            } else {
                int rowIndex = findlastrow(sheet, column);
                Row row = getOrCreateRow(sheet, rowIndex);
                row.createCell(column, CellType.STRING).setCellValue(inputValue);
            }
        });
    }

    public static void writeExcelCellMultipleSheets(
            String columnName, String excelPath, String inputValue,
            List<String> sheetNames) throws IOException {
        updateWorkbook(excelPath, workbook -> {
            for (String sheetName : sheetNames) {
                XSSFSheet sheet = requireSheet(workbook, sheetName);
                int column = findColumn(requireHeader(sheet), columnName);
                Row row = getOrCreateRow(sheet, findlastrow(sheet, column));
                row.createCell(column, CellType.STRING).setCellValue(inputValue);
            }
        });
    }

    public static void writeToEmptyRow(
            String columnName, String excelPath, String inputValue, String sheetName)
            throws IOException {
        updateWorkbook(excelPath, workbook -> {
            XSSFSheet sheet = requireSheet(workbook, sheetName);
            int column = findColumn(requireHeader(sheet), columnName);
            int targetIndex = sheet.getLastRowNum() + 1;
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || isRowEmpty(row)) {
                    targetIndex = i;
                    break;
                }
            }
            Row row = getOrCreateRow(sheet, targetIndex);
            row.createCell(column, CellType.STRING).setCellValue(inputValue);
        });
    }

    public static void writeToExcel(
            String filePath, String sheetName, String matchHeader,
            String updateHeader, String matchValue, String updateValue) throws IOException {
        updateWorkbook(filePath, workbook -> {
            XSSFSheet sheet = requireSheet(workbook, sheetName);
            Row header = requireHeader(sheet);
            int matchColumn = findColumn(header, matchHeader);
            int updateColumn = findColumn(header, updateHeader);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                if (cellText(row.getCell(matchColumn)).equalsIgnoreCase(matchValue)) {
                    row.createCell(updateColumn, CellType.STRING).setCellValue(updateValue);
                }
            }
        });
    }

    public static int findlastrow(XSSFSheet sheet, int columnIndex) {
        int lastPopulated = 0;
        for (int rowIndex = 0; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row != null && !cellText(row.getCell(columnIndex)).isBlank()) {
                lastPopulated = rowIndex;
            }
        }
        return lastPopulated + 1;
    }

    private static XSSFSheet requireSheet(XSSFWorkbook workbook, String sheetName) {
        XSSFSheet sheet = workbook.getSheet(sheetName);
        if (sheet == null) throw new IllegalArgumentException("Sheet not found: " + sheetName);
        return sheet;
    }

    private static Row requireHeader(XSSFSheet sheet) {
        Row header = sheet.getRow(0);
        if (header == null) throw new IllegalArgumentException("Header row is missing");
        return header;
    }

    private static int findColumn(Row header, String name) {
        int index = findColumnIgnoreCase(header, name);
        if (index < 0) throw new IllegalArgumentException("Column not found: " + name);
        return index;
    }

    private static int findColumnIgnoreCase(Row header, String name) {
        for (Cell cell : header) {
            if (cellText(cell).equalsIgnoreCase(name)) return cell.getColumnIndex();
        }
        return -1;
    }

    private static Map<String, Integer> getColumnIndexMap(Row headerRow) {
        Map<String, Integer> result = new HashMap<>();
        for (Cell cell : headerRow) result.put(cellText(cell), cell.getColumnIndex());
        return result;
    }

    private static Integer findColumnOptional(Map<String, Integer> columns, String name) {
        return columns.entrySet().stream()
                .filter(entry -> entry.getKey().equalsIgnoreCase(name))
                .map(Map.Entry::getValue)
                .findFirst().orElse(null);
    }

    private static String cellText(Cell cell) {
        return cell == null ? "" : new DataFormatter().formatCellValue(cell).trim();
    }

    private static String removeTrailingDecimalZero(String value) {
        return value != null && value.matches(".*\\.0+")
                ? value.replaceFirst("\\.0+$", "") : value;
    }

    private static Row getOrCreateRow(XSSFSheet sheet, int index) {
        Row row = sheet.getRow(index);
        return row == null ? sheet.createRow(index) : row;
    }

    private static boolean isRowEmpty(Row row) {
        if (row == null) return true;
        for (Cell cell : row) if (!cellText(cell).isBlank()) return false;
        return true;
    }

    @FunctionalInterface
    private interface WorkbookAction {
        void accept(XSSFWorkbook workbook) throws IOException;
    }

    private static void updateWorkbook(String excelPath, WorkbookAction action)
            throws IOException {
        Path path = Paths.get(excelPath);
        try (FileInputStream input = new FileInputStream(path.toFile());
             XSSFWorkbook workbook = new XSSFWorkbook(input)) {
            action.accept(workbook);
            try (FileOutputStream output = new FileOutputStream(path.toFile())) {
                workbook.write(output);
            }
        }
    }

    public static void modify_Html(String filePath) throws IOException {

        Path resources = Paths.get("src", "test", "resources");
        Path stylePath = resources.resolve("style.txt");
        Path scriptPath = resources.resolve("script.txt");

        String css = Files.exists(stylePath)
                ? Files.readString(stylePath)
                : "";

        String script = Files.exists(scriptPath)
                ? Files.readString(scriptPath)
                : "";

        File inputFile = new File(filePath);

        Document document = Jsoup.parse(
                inputFile,
                StandardCharsets.UTF_8.name());

        Element styleElement = document.selectFirst("style");

        if (styleElement == null) {
            styleElement = document.head().appendElement("style");
        }

        styleElement.appendChild(new DataNode(css));

        Element scriptElement = document.select("script").last();

        if (scriptElement == null) {
            scriptElement = document.body().appendElement("script");
        }

        scriptElement.appendChild(new DataNode(script));

        for (Element element :
                document.select("script[src*=spark-script.js]")) {

            element.replaceWith(
                    new Comment(element.outerHtml()));
        }

        for (Element element :
                document.select("link[href*=spark-style.css]")) {

            element.replaceWith(
                    new Comment(element.outerHtml()));
        }

        Path reportDirectory = Paths.get(filePath)
                .toAbsolutePath()
                .getParent();

        for (Element image :
                document.select("img[src*=embedded]")) {

            String src = image.attr("src")
                    .replace('\\', '/');

            Path imagePath = reportDirectory
                    .resolve(src)
                    .normalize();

            if (!Files.isRegularFile(imagePath)) { //NEW
                imagePath = Paths.get(src).toAbsolutePath().normalize(); //NEW
            } //NEW

            if (Files.isRegularFile(imagePath)) {

                String encoded = Base64.getEncoder()
                        .encodeToString(
                                Files.readAllBytes(imagePath));

                String dataUri =
                        "data:image/png;base64," + encoded;

                image.attr("src", dataUri);
                image.attr("data-featherlight", dataUri);
            }
        }

        document.select("div.step:has(span:containsOwn(Hooks.))").attr("style", "background-color: rgba(229, 115, 115, 0.5);"); //NEW

        document.select("div.col-md-4:has(canvas#parent-analysis)").attr("style", "display: none !important;"); //NEW
        document.select("div.col-md-4:has(canvas#grandchild-analysis)").attr("style", "display: none !important;"); //NEW
        document.select("div.col-md-12:has(canvas#timeline)").attr("style", "display: none !important;"); //NEW

        document.select("span:contains(alahly)").remove();

        document.select("a#nav-category").forEach(element -> {
            if (element.parent() != null) {
                element.parent().remove();
            }
        });

        document.select("b[data-tooltip]").remove();

        document.select(
                "span.badge.fail-bg.log.float-right")
                .remove();

        /*
         * Use individual Element objects because the current Jsoup version
         * does not support Elements.text(String).
         */
        for (Element thElement :
                document.select("th:matchesOwn(^Skipped$)")) {

            thElement.text("No Run");
        }

        for (Element thElement :
                document.select("th:matchesOwn(^Others$)")) {

            thElement.text("Severity");
        }

        Files.writeString(
                Paths.get(filePath),
                document.outerHtml(),
                StandardCharsets.UTF_8);
    }
}
