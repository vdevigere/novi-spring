package org.novi.core.activations;

import org.novi.core.exceptions.ConfigurationParseException;

public interface BaseActivationFactory<T> {
    BaseActivation<T> apply(String configuration) throws ConfigurationParseException;
}
