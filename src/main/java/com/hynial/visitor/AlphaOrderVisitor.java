package com.hynial.visitor;

import java.util.stream.Collectors;

public class AlphaOrderVisitor extends AbstractOrder {
    @Override
    public void visit() {
        this.fieldStrings = this.fieldStrings.stream().sorted().collect(Collectors.toList());
    }
}
