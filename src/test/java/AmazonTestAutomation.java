import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AmazonTestAutomation {

    WebDriver driver;

    @BeforeEach
    public void setUp() {
        // Set path to your ChromeDriver (Make sure it's correct)
        System.setProperty("webdriver.chrome.driver", "/Users/myouse652/driver/chromedriver");  // Replace with the correct path

        // Create ChromeOptions object for custom configurations
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");  // Maximize the window on start
        // options.addArguments("--headless");  // Uncomment for headless mode (if needed)
        // options.addArguments("--disable-gpu"); // Uncomment for headless mode

        // Initialize the ChromeDriver with options
        driver = new ChromeDriver(options);

        // Open the Amazon website
        driver.get("https://www.amazon.com");

        // Optional: Maximize window if not running headless
        //
    }

    @Test
    public void testAmazonCheckout() {
        // Step 1: Login to Amazon
        loginToAmazon();

        // Step 2: Search for items in different categories and add to cart
        searchAndAddToCart("laptop", "Electronics");
        searchAndAddToCart("shoes", "Clothing");

        // Step 3: Go to the shopping cart and proceed to checkout
        checkoutCart();

        // Step 4: Check if the order was successful
        boolean orderSuccess = verifyOrderSuccess();

        // Assert the order was successful
        assertTrue(orderSuccess, "Order was not successfully placed.");
    }

    private void loginToAmazon() {
        // Click on the sign-in button
        WebElement signInButton = driver.findElement(By.id("nav-link-accountList"));
        signInButton.click();

        // Wait for the login page to load and enter credentials
        WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("ap_email")));

        // Enter email and password
        WebElement emailField = driver.findElement(By.id("ap_email"));
        emailField.sendKeys("your-email@example.com"); // Replace with your email
        emailField.sendKeys(Keys.RETURN);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("ap_password")));
        WebElement passwordField = driver.findElement(By.id("ap_password"));
        passwordField.sendKeys("your-password"); // Replace with your password
        passwordField.sendKeys(Keys.RETURN);

        // Wait for login to complete
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nav-cart")));
    }

    private void searchAndAddToCart(String searchTerm, String category) {
        // Find the search bar and input the search term
        WebElement searchBox = driver.findElement(By.id("twotabsearchtextbox"));
        searchBox.clear();
        searchBox.sendKeys(searchTerm);

        // Select the category from the dropdown
        WebElement categoryDropdown = driver.findElement(By.id("searchDropdownBox"));
        categoryDropdown.sendKeys(category);

        // Execute the search
        searchBox.sendKeys(Keys.RETURN);

        // Wait for search results
        WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".s-main-slot .s-result-item")));

        // Get the search results and add the first item to the cart
        List<WebElement> results = driver.findElements(By.cssSelector(".s-main-slot .s-result-item"));

        if (!results.isEmpty()) {
            WebElement addToCartButton = results.get(0).findElement(By.xpath(".//input[@value='Add to Cart']"));
            addToCartButton.click();
        } else {
            System.out.println("No results found for " + searchTerm + " in " + category);
        }

        // Wait a bit for the item to be added to the cart
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void checkoutCart() {
        // Go to the cart
        WebElement cartButton = driver.findElement(By.id("nav-cart"));
        cartButton.click();

        // Wait for the cart page to load
        WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("proceedToRetailCheckout")));

        // Click on the 'Proceed to Checkout' button
        WebElement checkoutButton = driver.findElement(By.name("proceedToRetailCheckout"));
        checkoutButton.click();

        // Wait for the checkout page to load
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("address-book-entry-0")));

        // Simulate proceeding to order (skipping payment for simplicity)
        WebElement placeOrderButton = driver.findElement(By.name("placeYourOrder1"));
        placeOrderButton.click();
    }

    private boolean verifyOrderSuccess() {
        try {
            // Wait for order confirmation message
            WebDriverWait wait = new WebDriverWait(driver, 10);
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".a-box-inner .a-alert-content")));

            WebElement confirmationMessage = driver.findElement(By.cssSelector(".a-box-inner .a-alert-content"));
            String confirmationText = confirmationMessage.getText();
            System.out.println("Order Confirmation Message: " + confirmationText);

            return confirmationText.contains("Thank you for your order");
        } catch (Exception e) {
            System.out.println("Order verification failed: " + e.getMessage());
            return false;
        }
    }

    @AfterEach
    public void tearDown() {
        // Wait for some time to verify and close the browser
        try {
            Thread.sleep(5000);  // Wait for 5 seconds before closing
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        driver.quit();
    }
}
