package org.jfantasy.framework.crypto;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.*;
import java.security.*;

public class SymmetricCrypto implements SecurityInc {

    private SecretKey secretKey = null;

    private Cipher ecipher = null;

    private Cipher dcipher = null;

    private KeyPair keypair = null;

    private PublicKey publicKey = null;

    private PrivateKey privateKey = null;

    private Signature sSignature = null;

    private Signature vSignature = null;

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public SymmetricCrypto() throws CryptoException {
        try {
            this.secretKey = KeyGenerator.getInstance("DES").generateKey();

            this.ecipher = Cipher.getInstance("DES");
            this.ecipher.init(1, this.secretKey);

            this.dcipher = Cipher.getInstance("DES");
            this.dcipher.init(2, this.secretKey);

            this.keypair = generatorKeyPair();
            this.privateKey = this.keypair.getPrivate();
            this.publicKey = this.keypair.getPublic();

            this.sSignature = Signature.getInstance("DSA");
            this.sSignature.initSign(this.privateKey);

            this.vSignature = Signature.getInstance("DSA");
            this.vSignature.initVerify(this.publicKey);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            throw new CryptoException(e.getMessage(), e);
        }
    }

    private KeyPair generatorKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA");
        keyGen.initialize(1024, new SecureRandom());
        return keyGen.generateKeyPair();
    }

    @Override
    public byte[] encrypt(byte[] data) throws CryptoException {
        try {
            return this.ecipher.doFinal(data);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new CryptoException(e.getMessage(), e);
        }
    }

    @Override
    public byte[] decrypt(byte[] data) throws CryptoException {
        try {
            return this.dcipher.doFinal(data);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new CryptoException(e.getMessage(), e);
        }
    }

    @Override
    public byte[] signature(byte[] data) throws CryptoException {
        try {
            this.sSignature.update(data);
            return this.sSignature.sign();
        } catch (SignatureException e) {
            throw new CryptoException(e.getMessage(), e);
        }
    }

    @Override
    public boolean verify(byte[] buffer, byte[] signData) throws CryptoException {
        try {
            this.vSignature.update(buffer);
            return this.vSignature.verify(signData);
        } catch (SignatureException e) {
            throw new CryptoException(e.getMessage(), e);
        }
    }

}