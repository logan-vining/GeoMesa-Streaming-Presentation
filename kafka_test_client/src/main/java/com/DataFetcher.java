package com;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;

public class DataFetcher {
    public void test() {
        while (true) {
            System.out.println("***********************");
            System.out.println("*****FETCHING DATA*****");
            System.out.println("***********************");

            String xmlResponse = makeRequest();
            try {
                parseResponse(xmlResponse);
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            }

            System.out.println(System.lineSeparator() + "Use ctrl+c to exit" + System.lineSeparator());

            try {
                Thread.sleep(10000);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private String makeRequest() {
        String ret = "";

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            String url = "http://api.irishrail.ie/realtime/realtime.asmx/getCurrentTrainsXML";
            HttpGet request = new HttpGet(url);

            try (CloseableHttpResponse response = client.execute(request)) {
                System.out.println(response.getCode() + " " + response.getReasonPhrase());
                HttpEntity entity = response.getEntity();
                ret = EntityUtils.toString(entity);
                EntityUtils.consume(entity);
            }
            catch (ParseException ex) {
                ex.printStackTrace();
            }
            finally {
                client.close();
            }
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }

        return ret;
    }

    private void parseResponse(String response) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = dbFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(new InputSource(new StringReader(response)));
        doc.getDocumentElement().normalize();

        NodeList trainPosList = doc.getElementsByTagName("objTrainPositions");
        for (int i = 0; i <= trainPosList.getLength() - 1; i++) {
            Node curNode = trainPosList.item(i);
            Element curElem = (Element) curNode;

            System.out.println("-----TRAIN " + getXMLFieldVal(curElem, "TrainCode") + "-----");
            System.out.println("Lat: " + getXMLFieldVal(curElem, "TrainLatitude"));
            System.out.println("Long: " + getXMLFieldVal(curElem, "TrainLongitude"));
        }
    }

    private String getXMLFieldVal(Element element, String field) { return element.getElementsByTagName(field).item(0).getTextContent(); }
}
