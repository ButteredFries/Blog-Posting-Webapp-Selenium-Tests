package com.butteredfries;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;

import io.github.bonigarcia.wdm.WebDriverManager;

public class AppTest {
    
    private static WebDriver driver = null;
    private static String id = "";

    @Before
    public void openBrowser() throws MalformedURLException {
        System.out.println("Starting setup");

        WebDriverManager.chromedriver().setup();
        driver = WebDriverManager.chromedriver().create();
    }

    @Test
    public void testProject() {
        System.out.println("Starting testing");

        testAddPost();
        testGetPosts();
        testDeletePost();

        driver.close();

        System.out.println("All tests successfully completed!");
    }


    public void testAddPost() {

        System.out.println(new Object(){}.getClass().getEnclosingMethod().getName() + ": starting test");

        driver.get("http://localhost:80");

        WebElement sendPostPageButton = driver.findElement(By.id("mat-tab-link-2"));
        sendPostPageButton.click();

        WebElement authorField = fluentWait(By.name("Author"));
        WebElement titleField = fluentWait(By.name("Title"));
        WebElement contentField = fluentWait(By.name("Content"));
        WebElement tagsField = fluentWait(By.name("Tags"));

        authorField.sendKeys("testAuthor");
        titleField.sendKeys("testTitle");
        contentField.sendKeys("testContent");
        tagsField.sendKeys("testTag1,testTag2");

        WebElement submitButton = fluentWait(By.xpath("/html/body/app-root/app-get-posts/div/form/input[5]"));
        submitButton.click();

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {}
        
        WebElement response = fluentWait(By.xpath("//*[@id=\"code\"]"));
        System.out.println(new Object(){}.getClass().getEnclosingMethod().getName() + ": response: " + response.getText());
        
        assertTrue(response.getText().substring(0, 8).equals("Success!"));

        id = response.getText().substring(13, response.getText().length());
    }

    
    public void testDeletePost() {

        System.out.println(new Object(){}.getClass().getEnclosingMethod().getName() + ": starting test");

        // Valid ID test
        driver.get("http://localhost:80");

        WebElement deletePostPageButton = fluentWait(By.id("mat-tab-link-3"));
        deletePostPageButton.click();

        WebElement deleteField = fluentWait(By.xpath("/html/body/app-root/app-delete-posts/div/form/input[1]"));
        deleteField.sendKeys(id);

        WebElement deleteButton = fluentWait(By.xpath("/html/body/app-root/app-delete-posts/div/form/input[2]"));
        deleteButton.click();

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {}
        
        WebElement response = fluentWait(By.xpath("//*[@id=\"code\"]"));
        System.out.println(new Object(){}.getClass().getEnclosingMethod().getName() + ": success response: " + response.getText());
        
        assertTrue(response.getText().substring(0, 8).equals("Success!"));

        // Invalid ID test
        deleteField.clear();
        deleteField.sendKeys("Invalid ID");

        deleteButton.click();

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {}

        response = fluentWait(By.xpath("//*[@id=\"code\"]"));
        System.out.println(new Object(){}.getClass().getEnclosingMethod().getName() + ": 404 response: " + response.getText());

        assertTrue(response.getText().equals("Failure: HttpErrorResponse"));
    }

    public void testGetPosts() {

        System.out.println(new Object(){}.getClass().getEnclosingMethod().getName() + ": starting test");

        WebElement getPostsPageButton = fluentWait(By.id("mat-tab-link-1"));
        getPostsPageButton.click();

        int foundPosts = 0;

        try {
            while (true) {
                WebElement postID = fluentWait(By.xpath("/html/body/app-root/app-get-posts/app-post[" + (foundPosts+1) + "]/div/p[4]"));

                System.out.println(new Object(){}.getClass().getEnclosingMethod().getName() + 
                    ": Found post " + (foundPosts+1) + ", with an ID: " + postID.getText().substring(4, postID.getText().length()));

                foundPosts++;
            }

        } catch (Exception e) {}
        
        assertTrue(foundPosts > 0);

        System.out.println(new Object(){}.getClass().getEnclosingMethod().getName() + ": Found " + foundPosts + " posts");
    }




    private static WebElement fluentWait(final By locator) {
        Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
            .withTimeout(Duration.of(10, ChronoUnit.SECONDS))
            .pollingEvery(Duration.of(2, ChronoUnit.SECONDS))
            .ignoring(NoSuchElementException.class);

        WebElement foo = wait.until(new Function<WebDriver, WebElement>() {
            public WebElement apply(WebDriver driver) {
                return driver.findElement(locator);
            }
        });

        return foo;
    }
}
