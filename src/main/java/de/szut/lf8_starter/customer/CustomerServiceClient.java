package de.szut.lf8_starter.customer;

import org.springframework.stereotype.Component;

/**
 * Customer Service dummy version
 */
@Component
public class CustomerServiceClient {

    /**
     * Prüft, ob ein Kunde mit der gegebenen ID existiert (Dummy).
     *
     * @param customerId Die ID des zu prüfenden Kunden.
     * @return true.
     */
    public boolean customerExists(Long customerId) {
        return true;
    }
}