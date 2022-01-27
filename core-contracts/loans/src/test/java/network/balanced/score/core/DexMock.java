package network.balanced.score.core;

import score.Context;
import score.VarDB;
import score.DictDB;
import score.Address;
import score.annotation.External;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;

import java.math.BigInteger;

import java.util.Map;

import score.ByteArrayObjectWriter;
import score.Context;
import score.ObjectReader;

import java.math.BigInteger;


public class DexMock {
    private final Address leftToken;
    private final Address rightToken;

    public DexMock(Address leftToken, Address rightToken) {
          this.leftToken = leftToken;
          this.rightToken = rightToken;
    }

    @External
    public void tokenFallback(Address _from, BigInteger _value, byte[] _data) {
        Address token = Context.getCaller();

        Context.require(_value.signum() > 0, "Token Fallback: Token value should be a positive number");
        String unpackedData = new String(_data);
        if (unpackedData.equals("") ) {
            return;
        }

        JsonObject json = Json.parse(unpackedData).asObject();

        String method = json.get("method").asString();
        JsonObject params = json.get("params").asObject();

        if (method.equals("_swap")) {
            BigInteger leftLiquidity = (BigInteger) Context.call(leftToken, "balanceOf", Context.getAddress());
            BigInteger rightLiquidity  = (BigInteger) Context.call(rightToken, "balanceOf", Context.getAddress());
            System.out.format("\nRightL: " + rightLiquidity.divide(BigInteger.TEN.pow(18)).toString());
            System.out.format("\nLEFTL: " + leftLiquidity.divide(BigInteger.TEN.pow(18)).toString());
            if (token == rightToken) {
                BigInteger tokensReceived = leftLiquidity.multiply(_value).divide(rightLiquidity.add(_value));
                System.out.format("\nsend left: " + tokensReceived.divide(BigInteger.TEN.pow(18)).toString());
                Context.call(leftToken, "transfer", _from, tokensReceived, new byte[0]);
            } else if (token == leftToken){
                BigInteger tokensReceived = rightLiquidity.multiply(_value).divide(leftLiquidity.add(_value));
                System.out.format("\nsend right: " + tokensReceived.divide(BigInteger.TEN.pow(18)).toString());
                Context.call(rightToken, "transfer", _from, tokensReceived, new byte[0]);
            }
        }
        
    }

}
