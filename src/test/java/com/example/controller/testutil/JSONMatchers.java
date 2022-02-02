package com.example.controller.testutil;

import groovy.util.logging.Slf4j;
import lombok.SneakyThrows;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import static com.example.controller.testutil.JSONAssertUtil.assertJSON;
import static com.example.controller.testutil.TestUtil.readFileContent;


@Slf4j
public class JSONMatchers {
    private static class JSONMatcher extends BaseMatcher<String> {
        private final String expectedJSON;

        JSONMatcher(String expectedJSON) {
            this.expectedJSON = expectedJSON;
        }

        @Override
        @SneakyThrows
        public boolean matches(Object actualResult) {
            String actualJSON = (String) actualResult;
            assertJSON(expectedJSON, actualJSON);
            return true; // never return false as it won't reach the return statement if exception happens
        }

        @Override
        public void describeTo(Description description) {
            // can be empty as if json doesn't match than it throws exception and "describeTo" is not run
        }
    }

    /**
     * @return matcher to assert json regardless formatting or order of fields.
     */
    public static Matcher<String> matchesJSON(String actualJSON) {
        return new JSONMatcher(actualJSON);
    }

    /**
     * @see JSONMatchers#matchesJSON
     */
    public static Matcher<String> matchesJSONInFile(String classpathFile) {
        String fileContent = readFileContent(classpathFile);
        return matchesJSON(fileContent);
    }
}
