package function;

import fitnesse.responders.run.SuiteResponder;
import fitnesse.wiki.*;

public class FitnessExample {

    public static final String INCLUDE_TEARDOWN = "!include -teardown .";
    public static final String INCLUDE_SETUP = "!include -setup .";
    public static final String TEST = "Test";
    public static final String SET_UP = "SetUp";
    public static final String TEAR_DOWN = "TearDown";
    public static final StringBuffer buffer = new StringBuffer();

    public String testableHtml(PageData pageData, boolean includeSuiteSetup) throws Exception {
        WikiPage wikiPage = pageData.getWikiPage();

        if (pageData.hasAttribute(TEST)) {
            if (includeSuiteSetup) {
                writeBuffered(extracted(wikiPage, getWikiPage(wikiPage, SuiteResponder.SUITE_SETUP_NAME)), INCLUDE_TEARDOWN);
            }
            writeBuffered(extracted(wikiPage, getWikiPage(wikiPage, SET_UP)), INCLUDE_TEARDOWN);
        }

        buffer.append(pageData.getContent());
        if (pageData.hasAttribute(TEST)) {
            writeBuffered(extracted(wikiPage, getWikiPage(wikiPage, TEAR_DOWN)), INCLUDE_SETUP);
            if (includeSuiteSetup) {
                writeBuffered(extracted(wikiPage, getWikiPage(wikiPage, SuiteResponder.SUITE_TEARDOWN_NAME)), INCLUDE_SETUP);
            }
        }

        pageData.setContent(buffer.toString());
        return pageData.getHtml();
    }

    private static WikiPage getWikiPage(WikiPage wikiPage, String suiteResponder) throws Exception {
        return PageCrawlerImpl.getInheritedPage(suiteResponder, wikiPage);
    }

    private static String extracted(WikiPage wikiPage, WikiPage includeTypeWikiPage) throws Exception {
        WikiPagePath wikiPagePath = null;
        if (includeTypeWikiPage != null) {
            wikiPagePath = getWikiPagePath(wikiPage, includeTypeWikiPage);
        }
        return getPathName(wikiPagePath);
    }

    private static void writeBuffered(String tearDownPathName, String type) {
        buffer.append(type).append(tearDownPathName).append("\n");
    }

    private static String getPathName(WikiPagePath pagePath) {
        return PathParser.render(pagePath);
    }

    //중복
    private static WikiPagePath getWikiPagePath(WikiPage wikiPage, WikiPage suiteSetup) throws Exception {
        return wikiPage.getPageCrawler().getFullPath(suiteSetup);
    }
}
