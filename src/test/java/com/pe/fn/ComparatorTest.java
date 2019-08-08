package com.pe.fn;

import com.pe.fn.util.Comparator;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.util.Map;
import java.util.Scanner;

@SuppressWarnings({"unchecked"})
public class ComparatorTest {

    private Comparator comparator;
    private Wrapper wrapper;
    private DefaultHttpClient httpClientLeft;
    private DefaultHttpClient httpClientRight;

    @BeforeClass
    private void setUp() {
        this.comparator = new Comparator();
        this.httpClientLeft = this.comparator.getDefaultHttpClient();
        this.httpClientRight = this.comparator.getDefaultHttpClient();
    }

    @BeforeMethod
    private void setUpData() throws Exception {
        this.wrapper = this.comparator.getData(new File(Comparator.class.getClassLoader().getResource("file1.txt").getFile()), new File(Comparator.class.getClassLoader().getResource("file2.txt").getFile()));
    }

    @Test
    private void testUsingFilePathAsInput() {
        this.comparator.compare((Scanner) this.wrapper.getX(), (Scanner) this.wrapper.getY());
    }

    @Test
    private void testPositiveRequest() throws Exception {
        HttpGet httpGetLeft = this.comparator.getHttpGet("https://reqres.in/api/users?page=1");
        HttpGet httpGetRight = this.comparator.getHttpGet("https://reqres.in/api/users?page=1");

        HttpResponse responseLeft = httpClientLeft.execute(httpGetLeft);
        HttpResponse responseRight = this.httpClientRight.execute(httpGetRight);

        Assert.assertEquals(false, this.comparator.compareStatusCode(responseLeft, responseRight), "In case status code is != 200");

        Map<String, Object> treeMapLeft = this.comparator.getMapFromString(this.comparator.getStringPayload(this.comparator.getHttpEntity(responseLeft)));
        Map<String, Object> treeMapRight = this.comparator.getMapFromString(this.comparator.getStringPayload(this.comparator.getHttpEntity(responseRight)));

        Assert.assertEquals(true, treeMapLeft.keySet().equals(treeMapRight.keySet()), "The Json has equal no of keys should be equal");

        Assert.assertEquals(true, this.comparator.isKeysetsEqual(treeMapLeft, treeMapRight), "Keys set to will not be same on both sides");

        Assert.assertEquals(true, this.comparator.compareMaps(treeMapLeft, treeMapRight), "The Json compare should be equal");
    }

    @Test
    private void testNegativeRequest() throws Exception {
        HttpGet httpGetLeft = this.comparator.getHttpGet("https://reqres.in/api/users?page=1");
        HttpGet httpGetRight = this.comparator.getHttpGet("https://reqres.in/api/users/3");

        HttpResponse responseLeft = httpClientLeft.execute(httpGetLeft);
        HttpResponse responseRight = this.httpClientRight.execute(httpGetRight);

        Assert.assertEquals(false, this.comparator.compareStatusCode(responseLeft, responseRight), "In case status code is != 200");

        Map<String, Object> treeMapLeft = this.comparator.getMapFromString(this.comparator.getStringPayload(this.comparator.getHttpEntity(responseLeft)));
        Map<String, Object> treeMapRight = this.comparator.getMapFromString(this.comparator.getStringPayload(this.comparator.getHttpEntity(responseRight)));

        Assert.assertEquals(false, this.comparator.isKeysetsEqual(treeMapLeft, treeMapRight), "Keys set to be same on both sides");

        Assert.assertEquals(false, this.comparator.compareMaps(treeMapLeft, treeMapRight), "The Json compare should be equal");
    }

    @Test
    private void testCompareEqualMaps() throws Exception {
        String left = "{\"page\":1,\"per_page\":3,\"total\":12,\"total_pages\":4,\"data\":[{\"id\":1,\"email\":\"george.bluth@reqres.in\",\"first_name\":\"George\",\"last_name\":\"Bluth\",\"avatar\":\"https://s3.amazonaws.com/uifaces/faces/twitter/calebogden/128.jpg\"},{\"id\":2,\"email\":\"janet.weaver@reqres.in\",\"first_name\":\"Janet\",\"last_name\":\"Weaver\",\"avatar\":\"https://s3.amazonaws.com/uifaces/faces/twitter/josephstein/128.jpg\"},{\"id\":3,\"email\":\"emma.wong@reqres.in\",\"first_name\":\"Emma\",\"last_name\":\"Wong\",\"avatar\":\"https://s3.amazonaws.com/uifaces/faces/twitter/olegpogodaev/128.jpg\"}]}";
        String right = "{\"page\":1,\"per_page\":3,\"total\":12,\"total_pages\":4,\"data\":[{\"id\":1,\"email\":\"george.bluth@reqres.in\",\"first_name\":\"George\",\"last_name\":\"Bluth\",\"avatar\":\"https://s3.amazonaws.com/uifaces/faces/twitter/calebogden/128.jpg\"},{\"id\":2,\"email\":\"janet.weaver@reqres.in\",\"first_name\":\"Janet\",\"last_name\":\"Weaver\",\"avatar\":\"https://s3.amazonaws.com/uifaces/faces/twitter/josephstein/128.jpg\"},{\"id\":3,\"email\":\"emma.wong@reqres.in\",\"first_name\":\"Emma\",\"last_name\":\"Wong\",\"avatar\":\"https://s3.amazonaws.com/uifaces/faces/twitter/olegpogodaev/128.jpg\"}]}";
        Map<String, Object> treeMapLeft = this.comparator.getMapFromString(left);
        Map<String, Object> treeMapRight = this.comparator.getMapFromString(right);

        Assert.assertEquals(true, this.comparator.isKeysetsEqual(treeMapLeft, treeMapRight), "Keys set to be same on both sides");
        Assert.assertEquals(true, this.comparator.compareMaps(treeMapLeft, treeMapRight), "The Json compare should be equal");
    }

    @Test
    private void testCompareDiffOrderedMaps() throws Exception {
        String left = "{\"page\":1,\"total_pages\":4,\"data\":[{\"id\":1,\"email\":\"george.bluth@reqres.in\",\"first_name\":\"George\",\"last_name\":\"Bluth\",\"avatar\":\"https://s3.amazonaws.com/uifaces/faces/twitter/calebogden/128.jpg\"},{\"id\":3,\"email\":\"emma.wong@reqres.in\",\"first_name\":\"Emma\",\"last_name\":\"Wong\",\"avatar\":\"https://s3.amazonaws.com/uifaces/faces/twitter/olegpogodaev/128.jpg\"},{\"id\":2,\"email\":\"janet.weaver@reqres.in\",\"first_name\":\"Janet\",\"last_name\":\"Weaver\",\"avatar\":\"https://s3.amazonaws.com/uifaces/faces/twitter/josephstein/128.jpg\"}],\"per_page\":3,\"total\":12}";
        String right = "{\"data\":[{\"id\":1,\"email\":\"george.bluth@reqres.in\",\"first_name\":\"George\",\"last_name\":\"Bluth\",\"avatar\":\"https://s3.amazonaws.com/uifaces/faces/twitter/calebogden/128.jpg\"},{\"id\":3,\"email\":\"emma.wong@reqres.in\",\"first_name\":\"Emma\",\"last_name\":\"Wong\",\"avatar\":\"https://s3.amazonaws.com/uifaces/faces/twitter/olegpogodaev/128.jpg\"},{\"id\":2,\"email\":\"janet.weaver@reqres.in\",\"first_name\":\"Janet\",\"last_name\":\"Weaver\",\"avatar\":\"https://s3.amazonaws.com/uifaces/faces/twitter/josephstein/128.jpg\"}],\"page\":1,\"total_pages\":4,\"per_page\":3,\"total\":12}";
        Map<String, Object> treeMapLeft = this.comparator.getMapFromString(left);
        Map<String, Object> treeMapRight = this.comparator.getMapFromString(right);

        Assert.assertEquals(true, this.comparator.isKeysetsEqual(treeMapLeft, treeMapRight), "Keys set to be same on both sides");
        Assert.assertEquals(true, this.comparator.compareMaps(treeMapLeft, treeMapRight), "The Json compare should be equal");
    }

    @AfterClass
    private void closeObjects() {
        httpClientLeft.getConnectionManager().shutdown();
    }

}
