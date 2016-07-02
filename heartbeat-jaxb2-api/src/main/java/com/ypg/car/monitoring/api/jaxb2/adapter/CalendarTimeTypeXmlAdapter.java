package com.ypg.car.monitoring.api.jaxb2.adapter;

import java.util.Calendar;

import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.annotation.adapters.XmlAdapter;

public class CalendarTimeTypeXmlAdapter extends XmlAdapter<String, Calendar> {

    public Calendar unmarshal(String value) {
        if (value == null || value.length() < 1) {
            return null;
        }
        return DatatypeConverter.parseDateTime(value);
    }

    public String marshal(Calendar value) {
        if (value == null) {
            return null;
        }
        return DatatypeConverter.printDateTime(value);
    }

}
