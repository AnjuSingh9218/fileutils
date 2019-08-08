package com.pe.fn.util;

import com.pe.fn.IComparator;
import com.pe.fn.Wrapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *This {@code Comparator} is responsible to compare HttpGET response object
 * And return value on compare main method.
 *
 */
@SuppressWarnings({"unchecked"})
public class Comparator implements IComparator<Scanner, Scanner> {
    private ScriptEngine engine;

    public Comparator() {
        this.engine = new ScriptEngineManager().getEngineByName("javascript");
    }


    /**
     *
     * @param fileScannerLeft           link to {@link Scanner}
     * @param fileScannerRight          link to {@link Scanner}
     * @return                  link to {@link Boolean}
     */
    public boolean compare(Scanner fileScannerLeft, Scanner fileScannerRight) {
        boolean isFullyMatched = true;
        boolean isReadNextLine = true;
        DefaultHttpClient httpClient = getDefaultHttpClient();
        DefaultHttpClient httpClient1 = getDefaultHttpClient();
        try {
            while (isReadNextLine) {
                if (fileScannerLeft.hasNextLine() && fileScannerRight.hasNextLine()) {

                    /*Create HttpGetRequest Object*/
                    HttpGet getRequestLeft = getHttpGet(fileScannerLeft.nextLine().trim());
                    HttpGet getRequestRight = getHttpGet(fileScannerRight.nextLine().trim());

                    HttpResponse responseLeft;
                    HttpResponse responseRight;

                    /*Execute HttpRequest*/
                    try {
                        responseLeft = httpClient.execute(getRequestLeft);
                        responseRight = httpClient1.execute(getRequestRight);
                    } catch (Exception exception) {
                        System.out.println("File 1 : " + getRequestLeft.getURI().toString() + " & File 2 : " + getRequestRight.getURI().toString() + " Not Matched");
                        isFullyMatched = false;
                        continue;
                    }

                    if (compareStatusCode(responseLeft, responseRight)) {
                        System.out.println("File 1 : " + getRequestLeft.getURI().toString() + " & File 2 : " + getRequestRight.getURI().toString() + " Not Matched");
                    }

                    /*Get Response Object into String*/
                    Map<String, Object> treeMapLeft = getMapFromString(getStringPayload(getHttpEntity(responseLeft)));
                    Map<String, Object> treeMapRight = getMapFromString(getStringPayload(getHttpEntity(responseRight)));

                    if (!isKeysetsEqual(treeMapLeft, treeMapRight)) {
                        System.out.println("File 1 : " + getRequestLeft.getURI().toString() + " & File 2 : " + getRequestRight.getURI().toString() + " Not Matched");
                        isFullyMatched = false;
                        continue;
                    }

                    if (compareMaps(treeMapLeft, treeMapRight)) {
                        System.out.println("File 1 : " + getRequestLeft.getURI().toString() + " & File 2 : " + getRequestRight.getURI().toString() + " Matched");
                    } else {
                        System.out.println("File 1 : " + getRequestLeft.getURI().toString() + " & File 2 : " + getRequestRight.getURI().toString() + " Not Matched");
                        isFullyMatched = false;
                    }
                } else
                    isReadNextLine = false;

            }
        } catch (Exception ex) {
            httpClient.getConnectionManager().shutdown();
            httpClient1.getConnectionManager().shutdown();
        }
        return isFullyMatched;
    }

    /**
     *
     * @param input         link to {@link String}
     * @return              link to {@link Map}
     * @throws Exception    link to {@link Exception}
     */
    public Map getMapFromString(String input) throws  Exception{
        return new TreeMap<>((Map<String, Object>) engine.eval("Java.asJSONCompatible(" + input + ")"));
    }

    /**
     *
     * @param responseLeft      link to {@link HttpResponse}
     * @param responseRight     link to {@link HttpResponse}
     * @return                  link to {@link Boolean}
     */
    public boolean compareStatusCode(HttpResponse responseLeft, HttpResponse responseRight){
        return (responseLeft.getStatusLine().getStatusCode() != 200 || responseRight.getStatusLine().getStatusCode() != 200);
    }

    /**
     *
     * @param mapLeft           link to {@link Map}
     * @param mapRight          link to {@link Map}
     * @return                  link to {@link Boolean}
     */
    public boolean isKeysetsEqual(Map<String, Object> mapLeft, Map<String, Object> mapRight){
        return mapLeft.keySet().equals(mapRight.keySet());
    }

    /**
     *
     * @return      link to {@link DefaultHttpClient}
     */
    public DefaultHttpClient getDefaultHttpClient(){
        return  new DefaultHttpClient();
    }

    /**
     *
     * @param url       link to {@link String}
     * @return          link to {@link HttpGet}
     */
    public HttpGet getHttpGet(String url){
        HttpGet httpGet = new HttpGet(url.trim());
        /*Set Header as application/json*/
        httpGet.addHeader(Constants.PARAMETER_TYPE, Constants.CONTENT_TYPE);
        return httpGet;
    }

    /**
     *
     * @param httpResponse      link to {@link HttpResponse}
     * @return                  link to {@link HttpEntity}
     */
    public HttpEntity getHttpEntity(HttpResponse httpResponse){
        return httpResponse.getEntity();
    }


    /**
     *
     * @param httpEntity        link to {@link HttpEntity}
     * @return                  link to {@link String}
     * @throws Exception        link to {@link Exception}
     */
    public String getStringPayload(HttpEntity httpEntity) throws  Exception{
        return EntityUtils.toString(httpEntity);
    }

    /**
     *
     * @param inputFileLeft        link to {@link Scanner}
     * @param inputFileRight       link to {@link Scanner}
     * @return                     link to {@link Scanner}
     * @throws FileNotFoundException
     */
    public Wrapper<Scanner, Scanner> getData(File inputFileLeft, File inputFileRight) throws FileNotFoundException {
        return new Wrapper<>(new Scanner(inputFileLeft, "UTF-8"), new Scanner(inputFileRight, "UTF-8"));
    }


    /**
     *
     * @param mapLeft           link to {@link Map}
     * @param mapRight          link to {@link Map}
     * @return                  link to {@link Boolean}
     */
    public boolean compareMaps(Map<String, Object> mapLeft, Map<String, Object> mapRight) {
        AtomicBoolean isMatched = new AtomicBoolean(true);
        if (mapLeft.keySet().size() != mapRight.size())
            return false;
        mapLeft.forEach((key, value) -> {
            if (Objects.nonNull(mapRight.get(key)) && value instanceof Map && (mapRight.get(key) instanceof Map))
                isMatched.set(compareMaps(new TreeMap<>((Map<String, Object>) value), new TreeMap<>((Map<String, Object>) mapRight.get(key))));
            else if (Objects.nonNull(mapRight.get(key)) && value instanceof List && (mapRight.get(key) instanceof List)) {
                isMatched.set(compareLists((List) value, (List) mapRight.get(key)));
            } else
                isMatched.set(value.equals(mapRight.get(key)) && isMatched.get());
        });
        return isMatched.get();
    }


    /**
     *
     * @param objectListLeft            link to {@link List}
     * @param objectListRight           link to {@link List}
     * @return                          link to {@link Boolean}
     */
    private boolean compareLists(List<Object> objectListLeft, List<Object> objectListRight) {
        AtomicBoolean isMatched = new AtomicBoolean(true);
        if (objectListLeft.size() != objectListRight.size())
            return false;
        if (isMatched.get()) {
            Iterator iterator1 = objectListRight.iterator();
            objectListLeft.forEach(a -> {
                Object object = iterator1.next();
                if (Objects.nonNull(object) && a instanceof Map && (object instanceof Map))
                    isMatched.set(compareMaps(new TreeMap<>((Map<String, Object>) a), new TreeMap<>((Map<String, Object>) object)));
                else if (Objects.nonNull(object) && a instanceof List && (object instanceof List))
                    compareLists((List) a, (List) object);
                else
                    isMatched.set(a.equals(object) && isMatched.get());
            });
        }
        return isMatched.get();
    }
}
