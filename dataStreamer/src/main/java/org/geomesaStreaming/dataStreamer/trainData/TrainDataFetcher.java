package org.geomesaStreaming.dataStreamer.trainData;

import org.geomesaStreaming.dataStreamer.BasicHttp;
import org.geomesaStreaming.dataStreamer.DataElement;
import org.geomesaStreaming.dataStreamer.DataFetcher;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TrainDataFetcher implements DataFetcher {
    private final String dataUrl = "http://api.irishrail.ie/realtime/realtime.asmx/getCurrentTrainsXML";
    public List<DataElement> getDataElements() {
        List<DataElement> newData;

        try {
            newData = parseToTrainData(BasicHttp.getResponseAsString(dataUrl));
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
            newData = null;
        }

        return newData;
    }

    private List<DataElement> parseToTrainData(String xmlResponse) throws ParserConfigurationException, IOException, SAXException {
        List<DataElement> trainData = new ArrayList<>();

        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = dbFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(new InputSource(new StringReader(xmlResponse)));
        doc.getDocumentElement().normalize();

        NodeList trainPosList = doc.getElementsByTagName("objTrainPositions");
        for (int i = 0; i <= trainPosList.getLength() - 1; i++) {
            Node curNode = trainPosList.item(i);
            Element curElem = (Element) curNode;

            TrainData newDataElement = new TrainData();

            try {
                newDataElement.setValue("train_id", getXMLFieldVal(curElem, "TrainCode"));
                newDataElement.setValue("time", new Date());

                String pointStr = "POINT (" + getXMLFieldVal(curElem, "TrainLongitude") + " " + getXMLFieldVal(curElem, "TrainLatitude") + ")";
                newDataElement.setValue("loc", pointStr);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
                newDataElement = null;
            }

            trainData.add(newDataElement);
        }

        return trainData;
    }

    private static String getXMLFieldVal(Element element, String field) { return element.getElementsByTagName(field).item(0).getTextContent(); }
}
