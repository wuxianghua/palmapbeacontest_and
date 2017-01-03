package com.palmap.BluetoothUtils.security;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by eric3 on 2016/10/14.
 */

public class EncrypUtil {

  public static String encrypt(String seed, String cleartext) throws Exception {
    byte[] rawKey = getRawKey(seed.getBytes());
    byte[] result = encrypt(rawKey, cleartext.getBytes());
    return toHex(result);
  }

  public static String decrypt(String seed, String encrypted) throws Exception {
    byte[] rawKey = getRawKey(seed.getBytes());
    byte[] enc = toByte(encrypted);
    byte[] result = decrypt(rawKey, enc);
    return new String(result);
  }


  private static byte[] getRawKey(byte[] seed) throws Exception {
    KeyGenerator kgen = KeyGenerator.getInstance("AES");
    SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "Crypto");
    sr.setSeed(seed);
    kgen.init(128, sr); // 192 and 256 bits may not be available
    SecretKey skey = kgen.generateKey();
    byte[] raw = skey.getEncoded();
    return raw;
  }



  private static byte[] encrypt(byte[] raw, byte[] clear) throws Exception {
    SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
    Cipher cipher = Cipher.getInstance("AES");
    cipher.init(Cipher.ENCRYPT_MODE, skeySpec,new IvParameterSpec(new byte[cipher.getBlockSize()]));
    byte[] encrypted = cipher.doFinal(clear);
    return encrypted;
  }


  private static byte[] decrypt(byte[] raw, byte[] encrypted) throws Exception {
    SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
    Cipher cipher = Cipher.getInstance("AES");
    cipher.init(Cipher.DECRYPT_MODE, skeySpec,new IvParameterSpec(new byte[cipher.getBlockSize()]));
    byte[] decrypted = cipher.doFinal(encrypted);
    return decrypted;
  }


  private static String toHex(String txt) {
    return toHex(txt.getBytes());
  }
  private static String fromHex(String hex) {
    return new String(toByte(hex));
  }

  private static byte[] toByte(String hexString) {
    int len = hexString.length()/2;
    byte[] result = new byte[len];
    for (int i = 0; i < len; i++)
      result[i] = Integer.valueOf(hexString.substring(2*i, 2*i+2), 16).byteValue();
    return result;
  }


  private static String toHex(byte[] buf) {
    if (buf == null)
      return "";
    StringBuffer result = new StringBuffer(2*buf.length);
    for (int i = 0; i < buf.length; i++) {
      appendHex(result, buf[i]);
    }
    return result.toString();
  }
  private final static String HEX = "0123456789ABCDEF";
  private static void appendHex(StringBuffer sb, byte b) {
    sb.append(HEX.charAt((b>>4)&0x0f)).append(HEX.charAt(b&0x0f));
  }
//
//  // KeyGenerator 提供对称密钥生成器的功能，支持各种算法
//  private KeyGenerator keygen;
//  // SecretKey 负责保存对称密钥
//  private SecretKey deskey;
//  // Cipher负责完成加密或解密工作
//  private Cipher c;
//  // 该字节数组负责保存加密的结果
//  private byte[] cipherByte;
//
//  public EncrypUtil() throws NoSuchAlgorithmException, NoSuchPaddingException {
//    Security.addProvider(new com.sun.crypto.provider.SunJCE());
//    // 实例化支持DES算法的密钥生成器(算法名称命名需按规定，否则抛出异常)
//    keygen = KeyGenerator.getInstance("DESede");
//    // 生成密钥SunJCE
//    deskey = keygen.generateKey();
//    // 生成Cipher对象,指定其支持的DES算法
//    c = Cipher.getInstance("DESede");
////    c = Cipher.getInstance("DES/ECB/NoPadding");
//  }
//
//  /**
//   * 获得密钥
//   *
//   * @param secretKey
//   * @return
//   * @throws NoSuchAlgorithmException
//   * @throws InvalidKeyException
//   * @throws InvalidKeySpecException
//   */
//  private SecretKey generateKey(String secretKey) throws NoSuchAlgorithmException,InvalidKeyException,InvalidKeySpecException {
//
//    SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
//    DESKeySpec keySpec = new DESKeySpec(secretKey.getBytes());
//    keyFactory.generateSecret(keySpec);
//    return keyFactory.generateSecret(keySpec);
//  }
//  /**
//   * 对字符串加密
//   *
//   * @param str
//   * @return
//   * @throws InvalidKeyException
//   * @throws IllegalBlockSizeException
//   * @throws BadPaddingException
//   */
//  public byte[] Encrytor(String str) throws InvalidKeyException,
//      IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException {
//    // 根据密钥，对Cipher对象进行初始化，ENCRYPT_MODE表示加密模式
//    c.init(Cipher.ENCRYPT_MODE, deskey);
//    byte[] src = str.getBytes("UTF-8");
//    // 加密，结果保存进cipherByte
//    cipherByte = c.doFinal(src);
//    return cipherByte;
//  }
//
//  /**
//   * 对字符串解密
//   *
//   * @param buff
//   * @return
//   * @throws InvalidKeyException
//   * @throws IllegalBlockSizeException
//   * @throws BadPaddingException
//   */
//  public byte[] Decryptor(byte[] buff) throws InvalidKeyException,
//      IllegalBlockSizeException, BadPaddingException {
//    // 根据密钥，对Cipher对象进行初始化，DECRYPT_MODE表示加密模式
////    int ivIndex = encrypted.length - Constants.AES_BYTE_LENGTH;
//    c.init(Cipher.DECRYPT_MODE, deskey);
//    cipherByte = c.doFinal(buff);
//    return cipherByte;
//  }


}
