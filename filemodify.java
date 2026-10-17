package com.qa.framework.pages.ui;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Base64;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.qa.framework.pages.BasePage;

public class filemodify extends BasePage {

    /**
     * Modifies the given HTML report file by replacing certain tags, 
     * exception names, and other UI elements.
     *
     * @param htmlFilePath Full path to the ExtentSpark.html file
     * @throws IOException If any file read/write operation fails
     */
	public static void endmethod(String folderPath) throws IOException {
	    String latestFilePath = null;
	    File latestDirectory;
	    String imagePath = "src\\test\\resources\\CSS\\logo.png";

	    // 🔹 Convert logo image to Base64
	    byte[] imageBytes = Files.readAllBytes(Paths.get(imagePath));
	    String base64Image = Base64.getEncoder().encodeToString(imageBytes);
	    String base64ImageSrc = "data:image/png;base64," + base64Image;

	    System.out.println("Closing the browser and updating the report...");

	    // 🔹 If folderPath is given, use it. Otherwise, pick the latest report folder.
	    if (folderPath != null && !folderPath.trim().isEmpty()) {
	        latestDirectory = new File(folderPath);
	        if (!latestDirectory.exists() || !latestDirectory.isDirectory()) {
	            throw new IOException("Given folder path is invalid: " + folderPath);
	        }
	    } else {
	        File parentFolder = new File("src\\test\\resources\\report\\");
	        File[] directories = parentFolder.listFiles(File::isDirectory);
	        if (directories == null || directories.length == 0) {
	            throw new IOException("No report directories found in: " + parentFolder.getAbsolutePath());
	        }
	        Arrays.sort(directories, Comparator.comparingLong(File::lastModified).reversed());
	        latestDirectory = directories[0];
	    }
//	    System.out.println("Contains 'Features'? " + template.contains("Features"));
//	    System.out.println("Contains '<i class=\"fa fa-bar-chart\"></i>'? " + template.contains("<i class=\"fa fa-bar-chart\"></i>"));

	    // 🔹 Now find ExtentSpark.html inside the selected folder
	    File htmlFile = new File(latestDirectory, "ExtentSpark.html");
	    if (!htmlFile.exists()) {
	        throw new IOException("ExtentSpark.html not found in folder: " + latestDirectory.getAbsolutePath());
	    }

	    latestFilePath = htmlFile.getAbsolutePath();
	    System.out.println("Report file found: " + latestFilePath);

	    // 🔹 Prepare icon replacements
	    String targetTag = "<i class=\"fa fa-bar-chart\"></i>";
	    String newTag = "<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 576 512\" width=\"24\" height=\"24\" fill=\"white\"><path d=\"M16 32C7.2 32 0 39.2 0 48V464c0 8.8 7.2 16 16 16H560c8.8 0 16-7.2 16-16s-7.2-16-16-16H16V48c0-8.8-7.2-16-16-16zm144 96c-8.8 0-16 7.2-16 16v240c0 8.8 7.2 16 16 16s16-7.2 16-16V144c0-8.8-7.2-16-16-16zm128 96c-8.8 0-16 7.2-16 16v144c0 8.8 7.2 16 16 16s16-7.2 16-16V240c0-8.8-7.2-16-16-16zm128-96c-8.8 0-16 7.2-16 16v240c0 8.8 7.2 16 16 16s16-7.2 16-16V144c0-8.8-7.2-16-16-16z\"/></svg>";

	    String targetListTag = "<i class=\"fa fa-list\"></i>";
	    String newListTag = "<svg xmlns=\"http://www.w3.org/2000/svg\" viewBox=\"0 0 512 512\" width=\"24\" height=\"24\" fill=\"white\"><path d=\"M497.941 225.941L286.059 14.059C280.195 8.195 272.153 5 263.941 5H135.941c-17.673 0-32 14.327-32 32V140.94c0 8.213 3.194 16.255 9.059 22.118l211.882 211.882c12.497 12.497 32.758 12.497 45.255 0l127.882-127.882c12.497-12.497 12.497-32.758 0-45.255zM136 120c-13.255 0-24-10.745-24-24s10.745-24 24-24 24 10.745 24 24-10.745 24-24 24z\"/></svg>";

	    String targetBugTag = "<i class=\"fa fa-bug\"></i>";
	    String newBugTag = "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"24\" height=\"24\" fill=\"white\" viewBox=\"0 0 16 16\"><path d=\"M4.978.855a.5.5 0 1 0-.956.29l.41 1.352A5 5 0 0 0 3 6h10a5 5 0 0 0-1.432-3.503l.41-1.352a.5.5 0 1 0-.956-.29l-.291.956A5 5 0 0 0 8 1a5 5 0 0 0-2.731.811l-.29-.956z\"/><path d=\"M13 6v1H8.5v8.975A5 5 0 0 0 13 11h.5a.5.5 0 0 1 .5.5v.5a.5.5 0 1 0 1 0v-.5a1.5 1.5 0 0 0-1.5-1.5H13V9h1.5a.5.5 0 0 0 0-1H13V7h.5A1.5 1.5 0 0 0 15 5.5V5a.5.5 0 0 0-1 0v.5a.5.5 0 0 1-.5.5zM7.5 15.975V7H3V6h-.5a.5.5 0 0 1-.5-.5V5a.5.5 0 0 0-1 0v.5A1.5 1.5 0 0 0 2.5 7H3v1H1.5a.5.5 0 0 0 0 1H3v1h-.5A1.5 1.5 0 0 0 1 11.5v.5a.5.5 0 1 0 1 0v-.5a.5.5 0 0 1 .5-.5H3a5 5 0 0 0 4.5 4.975\"/></svg>";

	    // 🔹 Read & modify HTML
	    String template = new String(Files.readAllBytes(Paths.get(latestFilePath)));
	    String regexPattern = "\\.[a-zA-Z]+Exception(?!\\()";
	    Pattern pattern = Pattern.compile(regexPattern);
	    Matcher matcher = pattern.matcher(template);
	    String updatedHtmlContent = matcher.replaceAll(".Assertion Error");
	    
	    updatedHtmlContent = addDashboardCleanupScript(updatedHtmlContent);

	    updatedHtmlContent = updatedHtmlContent
	            .replace("Features", "Module")
	            .replace("features", "Module")
	            .replace("scenarios", "Testcase")
	            .replace("Scenarios", "Testcase")
	            .replace("others", "")
	            .replace(targetTag, newTag)
	            .replace(targetListTag, newListTag)
	            .replace(targetBugTag, newBugTag)
	            .replace("skipped", "No Run")
	            .replace("r.Assertion Error =", "r.readyException =");

	    // 🔹 Write updated content
	    try (FileWriter fileWriter = new FileWriter(latestFilePath)) {
	        fileWriter.write(updatedHtmlContent);
	        System.out.println("✅ HTML report updated successfully: " + latestFilePath);
	    }
	}
	
	private static String addDashboardCleanupScript(String htmlContent) {

	    final String marker = "custom-dashboard-cleanup-script";

	    // Prevent the script from being added multiple times
	    if (htmlContent.contains(marker)) {
	        return htmlContent;
	    }

	    String cleanupScript =
	            "\n<!-- " + marker + " -->\n" +
	            "<style>\n" +
	            "    .custom-hidden-dashboard-item {\n" +
	            "        display: none !important;\n" +
	            "    }\n" +
	            "\n" +
	            "    .custom-testcase-chart-full-width {\n" +
	            "        width: 100% !important;\n" +
	            "        max-width: 100% !important;\n" +
	            "        flex: 0 0 100% !important;\n" +
	            "    }\n" +
	            "</style>\n" +
	            "<script>\n" +
	            "(function () {\n" +
	            "\n" +
	            "    function normalizeText(value) {\n" +
	            "        return (value || '')\n" +
	            "            .replace(/\\s+/g, ' ')\n" +
	            "            .trim()\n" +
	            "            .toLowerCase();\n" +
	            "    }\n" +
	            "\n" +
	            "    function getDashboard() {\n" +
	            "        return document.querySelector('#dashboard-view') ||\n" +
	            "               document.querySelector('.dashboard-view') ||\n" +
	            "               document.querySelector('#dashboard') ||\n" +
	            "               document;\n" +
	            "    }\n" +
	            "\n" +
	            "    function getCardTitle(card) {\n" +
	            "        var titleElement = card.querySelector(\n" +
	            "            '.card-title, .card-header, .card-header-title, ' +\n" +
	            "            'h1, h2, h3, h4, h5, h6'\n" +
	            "        );\n" +
	            "\n" +
	            "        return titleElement ? normalizeText(titleElement.textContent) : '';\n" +
	            "    }\n" +
	            "\n" +
	            "    function getLayoutContainer(card) {\n" +
	            "        return card.closest(\n" +
	            "            '.col-sm-12, .col-md-4, .col-md-6, .col-lg-4, ' +\n" +
	            "            '.col-lg-6, [class*=\"col-md-\"], [class*=\"col-lg-\"]'\n" +
	            "        ) || card;\n" +
	            "    }\n" +
	            "\n" +
	            "    function hideChartByTitle(titleToRemove) {\n" +
	            "        var dashboard = getDashboard();\n" +
	            "        var cards = dashboard.querySelectorAll('.card');\n" +
	            "\n" +
	            "        cards.forEach(function (card) {\n" +
	            "            var cardTitle = getCardTitle(card);\n" +
	            "\n" +
	            "            if (cardTitle === normalizeText(titleToRemove)) {\n" +
	            "                var container = getLayoutContainer(card);\n" +
	            "                container.classList.add('custom-hidden-dashboard-item');\n" +
	            "            }\n" +
	            "        });\n" +
	            "    }\n" +
	            "\n" +
	            "    function expandTestcaseChart() {\n" +
	            "        var dashboard = getDashboard();\n" +
	            "        var cards = dashboard.querySelectorAll('.card');\n" +
	            "\n" +
	            "        cards.forEach(function (card) {\n" +
	            "            var cardTitle = getCardTitle(card);\n" +
	            "\n" +
	            "            if (cardTitle === 'testcase' ||\n" +
	            "                cardTitle === 'scenario' ||\n" +
	            "                cardTitle === 'scenarios') {\n" +
	            "\n" +
	            "                var container = getLayoutContainer(card);\n" +
	            "                container.classList.add('custom-testcase-chart-full-width');\n" +
	            "            }\n" +
	            "        });\n" +
	            "    }\n" +
	            "\n" +
	            "    function cleanupDashboard() {\n" +
	            "        hideChartByTitle('Module');\n" +
	            "        hideChartByTitle('Features');\n" +
	            "        hideChartByTitle('Steps');\n" +
	            "        hideChartByTitle('Timeline');\n" +
	            "\n" +
	            "        expandTestcaseChart();\n" +
	            "    }\n" +
	            "\n" +
	            "    if (document.readyState === 'loading') {\n" +
	            "        document.addEventListener('DOMContentLoaded', cleanupDashboard);\n" +
	            "    } else {\n" +
	            "        cleanupDashboard();\n" +
	            "    }\n" +
	            "\n" +
	            "    window.addEventListener('load', cleanupDashboard);\n" +
	            "\n" +
	            "    // ExtentReports can create dashboard elements dynamically.\n" +
	            "    // Observer ensures the unwanted charts are removed after rendering.\n" +
	            "    var observer = new MutationObserver(function () {\n" +
	            "        cleanupDashboard();\n" +
	            "    });\n" +
	            "\n" +
	            "    observer.observe(document.documentElement, {\n" +
	            "        childList: true,\n" +
	            "        subtree: true\n" +
	            "    });\n" +
	            "\n" +
	            "    // Stop observing after the report has fully loaded.\n" +
	            "    setTimeout(function () {\n" +
	            "        cleanupDashboard();\n" +
	            "        observer.disconnect();\n" +
	            "    }, 5000);\n" +
	            "\n" +
	            "})();\n" +
	            "</script>\n";

	    int bodyClosingTagPosition =
	            htmlContent.toLowerCase().lastIndexOf("</body>");

	    if (bodyClosingTagPosition >= 0) {
	        return htmlContent.substring(0, bodyClosingTagPosition)
	                + cleanupScript
	                + htmlContent.substring(bodyClosingTagPosition);
	    }

	    return htmlContent + cleanupScript;
	}

}
