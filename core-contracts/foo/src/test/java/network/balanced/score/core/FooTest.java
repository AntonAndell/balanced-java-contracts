package network.balanced.score.core;

import com.iconloop.score.test.Account;
import com.iconloop.score.test.Score;
import com.iconloop.score.test.ServiceManager;
import com.iconloop.score.test.TestBase;
import com.iconloop.score.token.irc2.IRC2Mintable;
import org.junit.jupiter.api.*;

import org.junit.jupiter.api.Test;

import score.Context;
import score.Address;
import score.annotation.External;

import java.math.BigInteger;

@DisplayName("FooTest")
class FooTest extends TestBase {

    private static final ServiceManager sm = getServiceManager();

    private static final Account owner = sm.createAccount();
    private static Score foo;
    private static Score sicx;
    private static Score bnusd;

    // Sicx score deployment settings.
    private static final String nameSicx = "Staked icx";
    private static final String symbolSicx = "SICX";
    private static final int decimalsSicx = 18;
    private static final BigInteger initalsupplySicx = BigInteger.TEN.pow(50);

    // Bnusd score deployment settings.
    private static final String nameBnusd = "Balanced usd";
    private static final String symbolBnusd = "BNUSD";
    private static final int decimalsBnusd = 18;
    private static final BigInteger initalsupplyBnusd = BigInteger.TEN.pow(50);

    public static class IRC2BasicToken extends IRC2Mintable {
        public IRC2BasicToken(String _name, String _symbol, int _decimals, BigInteger _totalSupply) {
            super(_name, _symbol, _decimals);
            mintTo(Context.getCaller(), _totalSupply);
        }
    }

    @Test
    public void test() throws Exception {
        foo = sm.deploy(owner, Foo.class);
        sicx = sm.deploy(owner, IRC2BasicToken.class, nameSicx, symbolSicx, decimalsSicx, initalsupplySicx);
        bnusd = sm.deploy(owner, IRC2BasicToken.class, nameBnusd, symbolBnusd, decimalsBnusd, initalsupplyBnusd);

        sicx.invoke(owner, "transfer", foo.getAddress(), BigInteger.TEN.pow(18), new byte[0]);
        System.out.format("test sicx address: " + sicx.getAddress() + "\n");
    }
}