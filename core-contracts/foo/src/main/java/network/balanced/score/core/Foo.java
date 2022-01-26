package network.balanced.score.core;

import score.Context;
import score.Address;
import score.annotation.External;

import java.math.BigInteger;


public class Foo {
    public Foo() {}

    @External
    public void tokenFallback(Address _from, BigInteger _value, byte[] _data) {
        System.out.format("token Fallback Caller: " + Context.getCaller() + "\n");
    }
}
