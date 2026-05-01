package com.decstorage.service;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DefaultGasProvider;

public class BlockchainService {

    // ── CONFIG — update these three values ─────────────────────
    private static final String GANACHE_URL =
        "http://127.0.0.1:8545";

    private static final String PRIVATE_KEY =
        ""; // paste from Ganache key

    private static final String FILE_REGISTRY_ADDRESS =
        ""; //FileRegistery Contract key here

    private static final String ACCESS_CONTROL_ADDRESS =
        ""; //AccessControl Contract key here
    // ────────────────────────────────────────────────────────────

    private static Web3j web3j;
    private static Credentials credentials;

    static {
        try {
            web3j      = Web3j.build(new HttpService(GANACHE_URL));
            credentials = Credentials.create(PRIVATE_KEY);
            System.out.println("Blockchain connected. Address: "
                + credentials.getAddress());
        } catch (Exception e) {
            System.err.println("Blockchain init failed: " + e.getMessage());
        }
    }

    // ── Register file on blockchain ──────────────────────────────
    public static String registerFile(String cid, String policyJson) {
        try {
            Function function = new Function(
                "registerFile",
                Arrays.asList(new Utf8String(cid), new Utf8String(policyJson)),
                Collections.singletonList(new TypeReference<Uint256>() {})
            );

            String encodedFunction = FunctionEncoder.encode(function);

            EthGetTransactionCount ethGetTransactionCount = web3j
                .ethGetTransactionCount(
                    credentials.getAddress(),
                    DefaultBlockParameterName.LATEST)
                .send();
            BigInteger nonce = ethGetTransactionCount.getTransactionCount();

            Transaction transaction = Transaction.createFunctionCallTransaction(
                credentials.getAddress(),
                nonce,
                DefaultGasProvider.GAS_PRICE,
                DefaultGasProvider.GAS_LIMIT,
                FILE_REGISTRY_ADDRESS,
                encodedFunction
            );

            EthSendTransaction response = web3j
                .ethSendRawTransaction(
                    signTransaction(transaction, nonce, encodedFunction))
                .send();

            if (response.hasError()) {
                System.err.println("TX Error: " + response.getError().getMessage());
                return null;
            }

            String txHash = response.getTransactionHash();
            System.out.println("File registered on blockchain. TX: " + txHash);
            return txHash;

        } catch (Exception e) {
            System.err.println("registerFile error: " + e.getMessage());
            return null;
        }
    }

    // ── Log access attempt on blockchain ────────────────────────
    public static String logAccess(long fileId, boolean granted) {
        try {
            Function function = new Function(
                "logAccess",
                Arrays.asList(
                    new Uint256(BigInteger.valueOf(fileId)),
                    new Bool(granted)
                ),
                Collections.emptyList()
            );

            String encodedFunction = FunctionEncoder.encode(function);

            EthGetTransactionCount ethGetTransactionCount = web3j
                .ethGetTransactionCount(
                    credentials.getAddress(),
                    DefaultBlockParameterName.LATEST)
                .send();
            BigInteger nonce = ethGetTransactionCount.getTransactionCount();

            EthSendTransaction response = web3j
                .ethSendRawTransaction(
                    signTransaction(transaction(nonce, ACCESS_CONTROL_ADDRESS,
                        encodedFunction), nonce, encodedFunction))
                .send();

            if (response.hasError()) {
                System.err.println("logAccess TX Error: "
                    + response.getError().getMessage());
                return null;
            }

            String txHash = response.getTransactionHash();
            System.out.println("Access logged on blockchain. TX: " + txHash);
            return txHash;

        } catch (Exception e) {
            System.err.println("logAccess error: " + e.getMessage());
            return null;
        }
    }

    // ── Read file info from blockchain ───────────────────────────
    public static String[] getFileFromChain(long fileId) {
        try {
            Function function = new Function(
                "getFile",
                Collections.singletonList(
                    new Uint256(BigInteger.valueOf(fileId))),
                Arrays.asList(
                    new TypeReference<Utf8String>() {},
                    new TypeReference<Utf8String>() {},
                    new TypeReference<Address>() {},
                    new TypeReference<Uint256>() {}
                )
            );

            String encodedFunction = FunctionEncoder.encode(function);

            EthCall response = web3j.ethCall(
                Transaction.createEthCallTransaction(
                    credentials.getAddress(),
                    FILE_REGISTRY_ADDRESS,
                    encodedFunction),
                DefaultBlockParameterName.LATEST
            ).send();

            List<Type> result = FunctionReturnDecoder.decode(
                response.getValue(), function.getOutputParameters());

            if (result.isEmpty()) return null;

            return new String[]{
                result.get(0).getValue().toString(), // cid
                result.get(1).getValue().toString(), // policyJson
                result.get(2).getValue().toString(), // owner address
                result.get(3).getValue().toString()  // timestamp
            };

        } catch (Exception e) {
            System.err.println("getFile error: " + e.getMessage());
            return null;
        }
    }

    // ── Helpers ──────────────────────────────────────────────────
    private static String signTransaction(Transaction tx,
            BigInteger nonce, String data) throws Exception {
        org.web3j.crypto.RawTransaction rawTx =
            org.web3j.crypto.RawTransaction.createTransaction(
                nonce,
                DefaultGasProvider.GAS_PRICE,
                DefaultGasProvider.GAS_LIMIT,
                tx.getTo(),
                BigInteger.ZERO,
                data
            );
        byte[] signedMessage =
            org.web3j.crypto.TransactionEncoder.signMessage(rawTx, credentials);
        return org.web3j.utils.Numeric.toHexString(signedMessage);
    }

    private static Transaction transaction(BigInteger nonce,
            String to, String data) {
        return Transaction.createFunctionCallTransaction(
            credentials.getAddress(), nonce,
            DefaultGasProvider.GAS_PRICE,
            DefaultGasProvider.GAS_LIMIT,
            to, data
        );
    }
}
