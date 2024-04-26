package ca.bc.gov.ag.courts.util;

import ca.bc.gov.ag.courts.model.Job;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class JobValidator {
    private static final String DATE_FORMAT_PATTERN = ".+[+-]\\d{2}:\\d{2}$";
    private static final String DATE_FORMAT_ERROR = "Date is not in ISO8601 format with offset";
    private static final String DATE_FORMAT_ISO_ERROR = "Date is not in ISO8601 format with offset";

    public static void validateJobDates(Job job) {
        try {
            if (job.getStartDelivery() != null) {
                String startDelivery = job.getStartDelivery();
                DateTimeFormatter.ISO_DATE_TIME.parse(startDelivery);
                if (!startDelivery.matches(DATE_FORMAT_PATTERN)) {
                    throw new IllegalArgumentException(DATE_FORMAT_ERROR);
                }
            }
            if (job.getEndDelivery() != null) {
                String endDelivery = job.getEndDelivery();
                DateTimeFormatter.ISO_DATE_TIME.parse(endDelivery);
                if (!endDelivery.matches(DATE_FORMAT_PATTERN)) {
                    throw new IllegalArgumentException(DATE_FORMAT_ERROR);
                }
            }
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(DATE_FORMAT_ISO_ERROR, e);
        }
    }
}