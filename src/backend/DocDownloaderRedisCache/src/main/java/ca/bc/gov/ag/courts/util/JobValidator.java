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
            if (job.getStartDeliveryDtm() != null) {
                String startDeliveryDtm = job.getStartDeliveryDtm();
                DateTimeFormatter.ISO_DATE_TIME.parse(startDeliveryDtm);
                if (!DATE_FORMAT_PATTERN.matcher(startDeliveryDtm).matches()) {
                    throw new IllegalArgumentException(DATE_FORMAT_ERROR);
                }
            }
            if (job.getEndDeliveryDtm() != null) {
                String endDeliveryDtm = job.getEndDeliveryDtm();
                DateTimeFormatter.ISO_DATE_TIME.parse(endDeliveryDtm);
                if (!DATE_FORMAT_PATTERN.matcher(endDeliveryDtm).matches()) {
                    throw new IllegalArgumentException(DATE_FORMAT_ERROR);
                }
            }
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(DATE_FORMAT_ERROR, e);
        }
    }
}