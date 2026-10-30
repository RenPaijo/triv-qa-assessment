package com.mafaa.mobile.hooks;

import com.mafaa.mobile.drivers.DriverManager;
import io.cucumber.java.After;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.junit.jupiter.api.Assumptions;
import org.openqa.selenium.OutputType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class Hooks {

    private static final boolean DRY_RUN =
            Boolean.parseBoolean(System.getProperty("cucumber.execution.dry-run", "false"));
    private static int stepCounter;

    @Before
    public void setUp(Scenario scenario) {
        if (DRY_RUN) {
            return;
        }
        if (scenario.getSourceTagNames().contains("@LOG-002")
                && !Boolean.getBoolean("loginValidDataReady")) {
            Assumptions.assumeTrue(false,
                    "BLOCKED: no active authorized dummy account with a usable phone number is available. "
                            + "Use -DloginValidDataReady=true only after suitable test data is available.");
        }
        DriverManager.initializeDriver();
        String scenarioId = scenario.getSourceTagNames().stream()
                .filter(tag -> tag.matches("@(LOG|REG)-\\d{3}"))
                .findFirst()
                .orElse(scenario.getName());
        String safeScenarioName = (scenarioId + "-" + scenario.getName())
                .replaceAll("[^a-zA-Z0-9]+", "-");
        scenario.log("Scenario evidence: reports/screenshots/" + safeScenarioName);
        stepCounter = 0;
    }

    @AfterStep
    public void captureStepEvidence(Scenario scenario) {
        if (DRY_RUN || DriverManager.getDriver() == null) {
            return;
        }
        stepCounter++;
        try {
            String scenarioId = scenario.getSourceTagNames().stream()
                    .filter(tag -> tag.matches("@(LOG|REG)-\\d{3}"))
                    .findFirst()
                    .orElse(scenario.getName());
            String safeScenarioName = (scenarioId + "-" + scenario.getName())
                    .replaceAll("[^a-zA-Z0-9]+", "-");
            Path directory = Path.of("reports", "screenshots", safeScenarioName);
            Files.createDirectories(directory);
            String stepName = String.format("Step-%02d-%s", stepCounter, scenario.getName());
            Files.write(directory.resolve(stepName + ".png"),
                    DriverManager.getDriver().getScreenshotAs(OutputType.BYTES));
            Files.writeString(directory.resolve(stepName + ".xml"),
                    DriverManager.getDriver().getPageSource(), StandardCharsets.UTF_8);
        } catch (IOException | RuntimeException exception) {
            scenario.log("Step evidence could not be saved: " + exception.getMessage());
        }
    }

    @After
    public void tearDown(Scenario scenario) {
        if (DRY_RUN) {
            return;
        }
        try {
            DriverManager.quitDriver();
        } catch (RuntimeException exception) {
            scenario.log("The driver session could not be closed cleanly: " + exception.getMessage());
        }
    }
}
