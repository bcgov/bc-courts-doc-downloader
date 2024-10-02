package ca.bc.gov.ag.courts;

import ca.bc.gov.ag.courts.model.Job;
import ca.bc.gov.ag.courts.util.JobValidator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class JobValidatorTest {

    @Test
    public void testValidateJobDatesWithValidDates() {
        Job job = new Job();
        job.setStartDeliveryDtm("2024-01-19T19:00:00-07:00");
        job.setEndDeliveryDtm("2024-01-20T19:00:00-07:00");

        assertDoesNotThrow(() -> JobValidator.validateJobDates(job));
    }

    @Test
    public void testValidateJobDatesWithInvalidDates() {
        Job job = new Job();
        job.setStartDeliveryDtm("2024-01-19T19:00");
        job.setEndDeliveryDtm("2024-01-20T19:00");

        assertThrows(IllegalArgumentException.class, () -> JobValidator.validateJobDates(job));
    }

    @Test
    public void testValidateJobDatesWithInvalidDateOffset() {
        Job job = new Job();
        job.setStartDeliveryDtm("2024-01-19T19:00+7:00");

        assertThrows(IllegalArgumentException.class, () -> JobValidator.validateJobDates(job));
    }

    @Test
    public void testValidateJobDatesWithInvalidDateOffset2() {
        Job job = new Job();
        job.setStartDeliveryDtm("2024-01-19T19:00:07");

        assertThrows(IllegalArgumentException.class, () -> JobValidator.validateJobDates(job));
    }
}