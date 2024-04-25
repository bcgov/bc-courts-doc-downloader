package ca.bc.gov.ag.courts.queue;

public interface MessagePublisher {

    void publish(final String message);
}