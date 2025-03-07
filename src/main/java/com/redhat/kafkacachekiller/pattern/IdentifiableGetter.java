package com.redhat.kafkacachekiller.pattern;

import lombok.val;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class IdentifiableGetter<Id, IdB extends Identifiable<Id>> {

    private final Map<Id, IdB> cache = new HashMap<>();

    protected IdentifiableGetter(List<IdB> identifiableList) {
        if (CollectionUtils.isEmpty(identifiableList)) {
            return;
        }
        for (IdB aIdentifiable : identifiableList) {
            val identifier = aIdentifiable.getIdentifier();
            if (identifier == null) {
                throw new IllegalStateException("identifier is not exist");
            }
            if (cache.containsKey(identifier)) {
                throw new IllegalStateException("duplicate identifier");
            }
            cache.put(identifier, aIdentifiable);
        }
    }

    public IdB getIdentifiable(Id id) {
        return cache.get(id);
    }

    public Optional<IdB> getIdentifiableOpt(Id id) {
        return Optional.ofNullable(getIdentifiable(id));
    }

}