package com.wotiwan.medonline.integration.connector;

import com.wotiwan.medonline.database.entity.AppointmentStatus;
import com.wotiwan.medonline.dto.*;
import com.wotiwan.medonline.integration.connector.MisConnector;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Реализация интеграции с 1с: Медицина

@Component
@RequiredArgsConstructor
public class OneCMisConnector implements MisConnector {

    private final RestTemplate restTemplate;

    @Value("${integration.mis.onec.base-url}")
    private String BASE_URL;

    @Override
    public DoctorReadDto getDoctor(Long id) {

        String xml = sendGet("?mode=doctor&id=" + id);

        Element root = parse(xml).getDocumentElement();

        return new DoctorReadDto(
                parseInt(root, "id"),
                parseInt(root, "userId"),
                getText(root, "firstName"),
                getText(root, "middleName"),
                getText(root, "lastName"),
                parseInt(root, "specializationId"),
                parseBigDecimal(root, "consultationPrice"),
                getText(root, "description")
        );
    }

    @Override
    public List<TimeSlotReadDto> getSlots(Long doctorId, LocalDate date) {

        String session = checkAuth();
        init(session);

        String xmlRequest =
                "<request>" +
                        "<doctorId>" + doctorId + "</doctorId>" +
                        "<date>" + date + "</date>" +
                        "</request>";

        String response = sendPost(session, xmlRequest);

        return parseSlots(response);
    }

    @Override
    public AppointmentReadDto book(Long slotId) {

        String session = checkAuth();
        init(session);

        String response = sendGet(
                "?mode=book&sessid=" + session + "&slotId=" + slotId
        );

        Element root = parse(response).getDocumentElement();

        return new AppointmentReadDto(
                parseInt(root, "id"),
                null,
                null,
                null,
                AppointmentStatus.valueOf(getText(root, "status")),
                getText(root, "consultationResult"),
                LocalDateTime.parse(getText(root, "createdAt")),
                parseBigDecimal(root, "price"),
                List.of()
        );
    }

    private String checkAuth() {
        String xml = sendGet("?mode=checkauth");

        if (!xml.contains("success")) {
            throw new RuntimeException("1C auth failed");
        }

        return extractTag(xml, "sessionId");
    }

    private void init(String session) {
        sendGet("?mode=init&sessid=" + session);
    }

    private String sendPost(String session, String xml) {
        return restTemplate.postForObject(
                BASE_URL + "?mode=file&sessid=" + session,
                xml,
                String.class
        );
    }

    private String sendGet(String query) {
        return restTemplate.getForObject(BASE_URL + query, String.class);
    }

    private List<TimeSlotReadDto> parseSlots(String xml) {

        Document doc = parse(xml);
        NodeList nodes = doc.getElementsByTagName("slot");

        List<TimeSlotReadDto> result = new ArrayList<>();

        for (int i = 0; i < nodes.getLength(); i++) {

            Element e = (Element) nodes.item(i);

            result.add(new TimeSlotReadDto(
                    parseInt(e, "id"),
                    LocalDateTime.parse(getText(e, "startTime")),
                    LocalDateTime.parse(getText(e, "endTime")),
                    Boolean.parseBoolean(getText(e, "isBooked"))
            ));
        }

        return result;
    }

    private Document parse(String xml) {
        try {
            var factory = DocumentBuilderFactory.newInstance();
            var builder = factory.newDocumentBuilder();
            return builder.parse(new InputSource(new StringReader(xml)));
        } catch (Exception e) {
            throw new RuntimeException("XML parse error", e);
        }
    }

    private String getText(Element el, String tag) {
        NodeList list = el.getElementsByTagName(tag);
        if (list.getLength() == 0) return null;
        return list.item(0).getTextContent();
    }

    private Integer parseInt(Element el, String tag) {
        String v = getText(el, tag);
        return v == null ? null : Integer.parseInt(v);
    }

    private BigDecimal parseBigDecimal(Element el, String tag) {
        String v = getText(el, tag);
        return v == null ? null : new BigDecimal(v);
    }

    private String extractTag(String xml, String tag) {
        return getText(parse(xml).getDocumentElement(), tag);
    }
}