package com.example.flickrclient;

import com.example.flickrclient.service.search.SearchApiResponse;
import com.example.flickrclient.service.search.SearchResultsJsonParser;

import org.json.JSONException;
import org.junit.Assert;
import org.junit.Test;

public class SearchResultsJsonParserUnitTest {

    @Test
    public void parseTest_success() {
        String json = "{ \"photos\": { \"page\": 1, \"pages\": \"231778\", \"perpage\": 2, \"total\": \"463556\", \n" +
                "    \"photo\": [\n" +
                "      { \"id\": \"45863413345\", \"owner\": \"159754169@N02\", \"secret\": \"8e2550d89a\", \"server\": \"4813\", \"farm\": 5, \"title\": \"There is nothing quite like hot tea on a cold day \uD83D\uDC71\u200D♀️\uD83C\uDF75\uD83C\uDF37 \uD83D\uDC51 Tea Room Reservation \uD83D\uDC49 http:\\/\\/bit.ly\\/2ByMyvY @misskika . . . . #teatime #teatimes #pinkwall #tea #breaktime #hottea #pinkpink #lalife #la #mydayi\", \"ispublic\": 1, \"isfriend\": 0, \"isfamily\": 0 },\n" +
                "      { \"id\": \"46725776622\", \"owner\": \"156051106@N07\", \"secret\": \"7d3f9a22c6\", \"server\": \"7865\", \"farm\": 8, \"title\": \"Eşi Benzeri Olmayan Midye Oyası Modeli\", \"ispublic\": 1, \"isfriend\": 0, \"isfamily\": 0 }\n" +
                "    ] }, \"stat\": \"ok\" }";
        SearchResultsJsonParser parser = new SearchResultsJsonParser();
        try {
            SearchApiResponse response = parser.parseJson(json);
            Assert.assertEquals(2, response.getPhotos().size());
            Assert.assertEquals(1, response.getPage());
            Assert.assertEquals(231777, response.getNumPagesRemaining());

            String expUrl1 = "http://farm5.static.flickr.com/4813/45863413345_8e2550d89a.jpg";
            Assert.assertEquals(expUrl1, response.getPhotos().get(0).getUrl());
            Assert.assertNotEquals(expUrl1, response.getPhotos().get(1).getUrl());
        } catch (JSONException e) {
            e.printStackTrace();
            Assert.fail("JSON parsing failed");
        }
    }

    @Test
    public void parseTest_fail() {
        String json = "{ \"photos\": { \"page\": 1, \"pages\": \"231778\", \"perpage\": 2, \"total\": \"463556\", \n" +
                "    \"photo\": [\n" +
                "      { \"id\": \"45863413345\", \"owner\": \"159754169@N02\", \"secret\": \"8e2550d89a\", \"server\": \"4813\", \"farm\": 5, \"title\": \"There is nothing quite like hot tea on a cold day \uD83D\uDC71\u200D♀️\uD83C\uDF75\uD83C\uDF37 \uD83D\uDC51 Tea Room Reservation \uD83D\uDC49 http:\\/\\/bit.ly\\/2ByMyvY @misskika . . . . #teatime #teatimes #pinkwall #tea #breaktime #hottea #pinkpink #lalife #la #mydayi\", \"ispublic\": 1, \"isfriend\": 0, \"isfamily\": 0 },\n" +
                "      { \"id\": \"46725776622\", \"owner\": \"156051106@N07\", \"secret\": \"7d3f9a22c6\", \"server\": \"7865\", \"farm\": 8, \"title\": \"Eşi Benzeri Olmayan Midye Oyası Modeli\", \"ispublic\": 1, \"isfriend\": 0, \"isfamily\": 0 }\n" +
                "    ] }, \"stat\": \"ok\" ";
        SearchResultsJsonParser parser = new SearchResultsJsonParser();
        boolean exceptionThrown = false;
        try {
            parser.parseJson(json);
        } catch (JSONException e) {
            exceptionThrown = true;
        }
        Assert.assertTrue(exceptionThrown);
    }
}
