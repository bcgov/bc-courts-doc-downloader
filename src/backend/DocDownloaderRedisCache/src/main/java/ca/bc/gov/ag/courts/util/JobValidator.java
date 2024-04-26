package ca.bc.gov.ag.courts.util;

import ca.bc.gov.ag.courts.model.Job;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

public class JobValidator {
    private static final Pattern DATE_FORMAT_PATTERN = Pattern.compile(".+[+-]\\d{2}:\\d{2}$");
    private static final String DATE_FORMAT_ERROR = "Date is not in ISO8601 format with offset. Ex: 2024-01-19T19:00-07:00";

    public static void validateJobDates(Job job) {
        try {
            if (job.getStartDelivery() != null) {
                String startDelivery = job.getStartDelivery();
                DateTimeFormatter.ISO_DATE_TIME.parse(startDelivery);
                if (!DATE_FORMAT_PATTERN.matcher(startDelivery).matches()) {
                    throw new IllegalArgumentException(DATE_FORMAT_ERROR);
                }
            }
            if (job.getEndDelivery() != null) {
                String endDelivery = job.getEndDelivery();
                DateTimeFormatter.ISO_DATE_TIME.parse(endDelivery);
                if (!DATE_FORMAT_PATTERN.matcher(endDelivery).matches()) {
                    throw new IllegalArgumentException(DATE_FORMAT_ERROR);
                }
            }
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(DATE_FORMAT_ERROR, e);
        }
    }
}