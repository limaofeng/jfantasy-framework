package org.jfantasy.framework.crypto;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.ShortBufferException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.*;
import java.util.Arrays;

public class AsymmetricCrypto implements SecurityInc {
    private KeyPair keypair = null;

    private PublicKey publicKey = null;

    private PrivateKey privateKey = null;

    private Cipher ecipher = null;

    private Cipher dcipher = null;

    private Signature sSignature = null;

    private Signature vSignature = null;

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    public AsymmetricCrypto() {
        try {
            this.keypair = generatorKeyPair();
            this.privateKey = this.keypair.getPrivate();
            this.publicKey = this.keypair.getPublic();

            System.out.println("=====================");
            System.out.println(this.privateKey.getAlgorithm());
            System.out.println(this.privateKey.getFormat());
            System.out.println(Arrays.toString(this.privateKey.getEncoded()));
            System.out.println("=====================");

            System.out.println(this.publicKey.getAlgorithm());
            System.out.println(this.publicKey.getFormat());
            System.out.println(Arrays.toString(this.publicKey.getEncoded()));

            System.out.println("=====================");

            this.ecipher = Cipher.getInstance("RSA/NONE/PKCS1PADDING");
            this.ecipher.init(1, this.publicKey);

            this.dcipher = Cipher.getInstance("RSA/NONE/PKCS1PADDING");
            this.dcipher.init(2, this.privateKey);

            this.sSignature = Signature.getInstance("MD5WithRSA");
            this.sSignature.initSign(this.privateKey);

            this.vSignature = Signature.getInstance("MD5WithRSA");
            this.vSignature.initVerify(this.publicKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private KeyPair generatorKeyPair() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(1024);
        return keyGen.genKeyPair();
    }

    public byte[] encrypt(byte[] data) throws CryptoException {
        int blockSize = this.ecipher.getBlockSize();
        int outputSize = this.ecipher.getOutputSize(data.length);
        int leavedSize = data.length % blockSize;
        int blocksSize = leavedSize != 0 ? data.length / blockSize + 1 : data.length / blockSize;
        byte[] encrypt = new byte[outputSize * blocksSize];
        int i = 0;
        try {
            while (data.length - i * blockSize > 0) {
                if (data.length - i * blockSize > blockSize)
                    this.ecipher.doFinal(data, i * blockSize, blockSize, encrypt, i * outputSize);
                else
                    this.ecipher.doFinal(data, i * blockSize, data.length - i * blockSize, encrypt, i * outputSize);
                i++;
            }
        } catch (ShortBufferException | IllegalBlockSizeException|  BadPaddingException e) {
            throw new CryptoException(e.getMessage(), e);
        }


        return encrypt;
    }

    public byte[] decrypt(byte[] encryptData) throws CryptoException {
        byte[] decodeEncryptData = encryptData;

        int blockSize = this.dcipher.getBlockSize();
        ByteArrayOutputStream decrypt = new ByteArrayOutputStream(64);
        int j = 0;

        try {
            while (decodeEncryptData.length - j * blockSize > 0) {
                decrypt.write(this.dcipher.doFinal(decodeEncryptData, j * blockSize, blockSize));
                j++;
            }
        } catch (IOException | IllegalBlockSizeException | BadPaddingException e) {
            throw new CryptoException(e.getMessage(), e);
        }

        return decrypt.toByteArray();

    }

    public byte[] signature(byte[] data) throws CryptoException {
        try {
            this.sSignature.update(data);
            return this.sSignature.sign();
        } catch (SignatureException e) {
            throw new CryptoException(e.getMessage(), e);
        }

    }

    public boolean verify(byte[] buffer, byte[] signData) throws CryptoException {
        try {
            this.vSignature.update(buffer);
            return this.vSignature.verify(signData);
        } catch (SignatureException e) {
            throw new CryptoException(e.getMessage(), e);
        }
    }

}