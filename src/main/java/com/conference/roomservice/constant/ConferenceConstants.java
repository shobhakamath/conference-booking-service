package com.conference.roomservice.constant;

import java.time.format.DateTimeFormatter;

public class ConferenceConstants {
    private ConferenceConstants(){

    }

    public static final String DATE_FORMAT = "dd MMM yyyy";
    public static final String TIME_FORMAT = "HH:mm";
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(TIME_FORMAT);

    public static final String DATE_FORMAT_REGEX = "^(0?[1-9]|[1-2][0-9]|3[0-1]) (Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec) (\\d{4})$";
    public static final String OVERLAP_MAINTENANCE = "The meeting overlaps with room maintenance time";
    public static final String CANNOT_CREATE_BOOKING = "Cannot create the booking";
}
