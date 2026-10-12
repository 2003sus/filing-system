package com.example.filing.worker;

import com.microsoft.playwright.*;
import org.springframework.stereotype.Component;

@Component
public class CourtPageFiller {

    /** 打开假法院页面，填表，提交，返回申请编号 */
    public String fillAndSubmit(String snapshotJson) {
        // ★ 强制指定浏览器路径
        System.setProperty("playwright.browsers.path", "E:\\playwright-browsers");

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(
                    new BrowserType.LaunchOptions().setHeadless(false)
            );
            Page page = browser.newPage();

            page.navigate("http://localhost:8080/mock-court.html");

            String plaintiff = extract(snapshotJson, "plaintiff");
            String defendant = extract(snapshotJson, "defendant");
            String title = extract(snapshotJson, "title");
            String amount = extract(snapshotJson, "amount");
            String description = extract(snapshotJson, "description");

            page.fill("#plaintiff", plaintiff);
            page.fill("#defendant", defendant);
            page.fill("#title", title);
            if (amount != null && !amount.isBlank() && !"null".equals(amount)) {
                page.fill("#amount", amount);
            }
            page.fill("#description", description);

            page.click("#submitBtn");
            page.waitForSelector("#result", new Page.WaitForSelectorOptions().setTimeout(5000));

            String applyNo = page.locator("#applyNo").textContent();

            Thread.sleep(2000);
            browser.close();

            return applyNo;
        } catch (Exception e) {
            throw new RuntimeException("填表失败: " + e.getMessage(), e);
        }
    }

    private String extract(String json, String key) {
        String pattern = "\"" + key + "\":";
        int i = json.indexOf(pattern);
        if (i < 0) return "";
        i += pattern.length();
        if (json.charAt(i) == '"') {
            int end = json.indexOf('"', i + 1);
            return json.substring(i + 1, end);
        } else {
            int end = json.indexOf(',', i);
            if (end < 0) end = json.indexOf('}', i);
            return json.substring(i, end).trim();
        }
    }
}