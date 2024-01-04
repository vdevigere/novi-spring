package org.novi.core.activations;

public interface BaseActivation<T> extends BaseActivationFactory<T> {

    default String getName() {
        return this.getClass().getCanonicalName();
    }

    public abstract Boolean evaluate(String context);

    public abstract T configuration();
}
