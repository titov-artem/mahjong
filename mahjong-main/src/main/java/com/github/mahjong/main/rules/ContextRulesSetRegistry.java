package com.github.mahjong.main.rules;

import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.*;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

@Service
public class ContextRulesSetRegistry implements RulesSetRegistry {

    private final Map<String, RulesSet> rulesSets;

    @Inject
    public ContextRulesSetRegistry(List<RulesSet> rulesSets) {
        this.rulesSets = rulesSets.stream().collect(toMap(RulesSet::getCode, identity()));
    }

    @Override
    public Collection<RulesSet> getRegistered() {
        return Collections.unmodifiableCollection(rulesSets.values());
    }

    @Override
    public Optional<RulesSet> getRulesSet(String code) {
        return Optional.ofNullable(rulesSets.get(code));
    }
}
