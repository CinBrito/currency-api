package br.com.ada.currencyapi;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.junit.jupiter.api.Assertions;

public class CurrencyE2ETest {

    private WebDriver driver;

    @BeforeEach
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "src/test/resources/chromedriver");
        driver = new ChromeDriver();
    }

    @AfterEach
    public void tearDown() {
        if (driver != null) {
        driver.quit();
        }
    }

    public void testCreateCurrency() {
        driver.get("http://localhost:8080/new-currency");
        driver.findElement(By.id("name")).sendKeys("LCS");
        driver.findElement(By.id("description")).sendKeys("Moeda do lucas");
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        Assertions.assertEquals("http://localhost:8080/currency", driver.getCurrentUrl());


    }

}
