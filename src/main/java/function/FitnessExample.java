package function;

import fitnesse.responders.run.SuiteResponder;
import fitnesse.wiki.*;

public class FitnessExample {

    public static final String INCLUDE_TEARDOWN = "!include -teardown .";
    public static final String INCLUDE_SETUP = "!include -setup .";

    public String testableHtml(PageData pageData, boolean includeSuiteSetup) throws Exception {
        WikiPage wikiPage = pageData.getWikiPage();
        StringBuffer buffer = new StringBuffer();

        if (pageData.hasAttribute("Test")) {
            if (includeSuiteSetup) {
                WikiPage suiteSetup = PageCrawlerImpl.getInheritedPage(SuiteResponder.SUITE_SETUP_NAME, wikiPage);
                extracted(wikiPage, buffer, suiteSetup);
            }
            WikiPage setup = PageCrawlerImpl.getInheritedPage("SetUp", wikiPage);
            extracted(wikiPage, buffer, setup);
        }

        buffer.append(pageData.getContent());
        if (pageData.hasAttribute("Test")) {
            WikiPage teardown = PageCrawlerImpl.getInheritedPage("TearDown", wikiPage);
            extracted(wikiPage, buffer, teardown);
            if (includeSuiteSetup) {
                WikiPage suiteTeardown = PageCrawlerImpl.getInheritedPage(SuiteResponder.SUITE_TEARDOWN_NAME, wikiPage);
                extracted(wikiPage, buffer, suiteTeardown);
            }
        }

        pageData.setContent(buffer.toString());
        return pageData.getHtml();
    }

    private static void extracted(WikiPage wikiPage, StringBuffer buffer, WikiPage pageType) throws Exception {
        if (pageType != null) {
            WikiPagePath tearDownPath = getWikiPagePath(wikiPage, pageType);
            String tearDownPathName = getPathName(tearDownPath);
            extractedIncludeTeardown(buffer, tearDownPathName);
        }
    }

    private static void extractedIncludeTeardown(StringBuffer buffer, String tearDownPathName) {
        buffer.append(INCLUDE_TEARDOWN).append(tearDownPathName).append("\n");
    }

    private static void extractedIncludeSetup(StringBuffer buffer, String pagePathName) {
        buffer.append(INCLUDE_SETUP).append(pagePathName).append("\n");
    }

    private static String getPathName(WikiPagePath pagePath) {
        return PathParser.render(pagePath);
    }

    //중복
    private static WikiPagePath getWikiPagePath(WikiPage wikiPage, WikiPage suiteSetup) throws Exception {
        return wikiPage.getPageCrawler().getFullPath(suiteSetup);
    }
}
