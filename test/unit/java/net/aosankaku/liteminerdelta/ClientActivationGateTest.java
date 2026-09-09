package net.aosankaku.liteminerdelta;

public final class ClientActivationGateTest {
    public static void main(String[] args) {
        ClientActivationGate gate = new ClientActivationGate();

        assertEquals(ClientActivationGate.Result.BLOCK_AND_NOTIFY, gate.evaluate(true, false),
                "the first hotkey attempt on an unsupported server should notify");
        assertEquals(ClientActivationGate.Result.BLOCK, gate.evaluate(true, false),
                "a held hotkey should not spam the notification");
        gate.reset();
        assertEquals(ClientActivationGate.Result.BLOCK_AND_NOTIFY, gate.evaluate(true, false),
                "releasing and pressing the hotkey should notify again");
        assertEquals(ClientActivationGate.Result.ALLOW, gate.evaluate(true, true),
                "a supported server should allow activation");
        assertEquals(ClientActivationGate.Result.BLOCK, gate.evaluate(false, false),
                "automatic activation should be blocked silently on an unsupported server");
    }

    private static void assertEquals(Object expected, Object actual, String message) {
        if (!expected.equals(actual)) {
            throw new AssertionError(message + ": expected=" + expected + ", actual=" + actual);
        }
    }
}
