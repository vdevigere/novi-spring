package org.novi.activations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.novi.core.activations.BaseActivation;
import org.novi.core.activations.BaseConfiguredActivation;
import org.novi.core.exceptions.ConfigurationParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class DateTimeActivation implements BaseActivation {

    private static final String DATE_FORMAT = "dd-MM-yyyy hh:mm";

    Logger logger = LoggerFactory.getLogger(DateTimeActivation.class);

    public record DateTimeActivationConfigRecord(Date startDateTime, Date endDateTime) {
    }

    @Override
    public String getName() {
        return this.getClass().getCanonicalName();
    }

    @Override
    public BaseConfiguredActivation<DateTimeActivationConfigRecord> whenConfiguredWith(String configuration) throws ConfigurationParseException {
        try {
            DateTimeActivationConfigRecord dateTimeActivationConfigRecord = mapper().readValue(configuration, new TypeReference<>() {
            });
            return new BaseConfiguredActivation<>(dateTimeActivationConfigRecord) {

                @Override
                public Boolean evaluateFor(Map<String, Object> context) {
                    SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT);
                    try {
                        Date currentDateTime = df.parse((String) context.get(DateTimeActivation.this.getName() + ".currentDateTime"));
                        logger.debug("Checking for {} <= {} < {}", this.getConfiguration().startDateTime, currentDateTime, this.getConfiguration().endDateTime);
                        return this.getConfiguration().startDateTime().compareTo(currentDateTime) <= 0 && this.getConfiguration().endDateTime().compareTo(currentDateTime) > 0;
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }
            };
        } catch (JsonProcessingException e) {
            throw new ConfigurationParseException(e);
        }
    }

    public ObjectMapper mapper() {
        SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT);
        ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat(df);
        return mapper;
    }
}
