package org.novi.persistence;

import org.novi.core.Flag;
import org.springframework.data.repository.CrudRepository;

public interface FlagRepository extends CrudRepository<Flag, Long> {
}
