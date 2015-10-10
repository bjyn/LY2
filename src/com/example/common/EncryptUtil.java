package com.example.common;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 移动接口加密解密工具类 <br/>
 * ClassName: EncryptUtil <br/>
 * date: 2014-10-9 下午4:00:44 <br/>
 * 
 * @author zhuhb
 * @version
 * @since JDK 1.6
 */
public class EncryptUtil {
	private static byte[] iv = { 1, 2, 3, 4, 5, 6, 7, 8 };

	/**
	 * 根据公钥加密 .
	 * 
	 * @author zhuhb
	 * @param encryptString
	 *            需要加密的字符串
	 * @param encryptKey
	 *            公钥
	 * @return 加密后的值
	 * @throws Exception
	 * @since JDK 1.6
	 */
	public static String encryptDES(String encryptString, String encryptKey)
			throws Exception {
		IvParameterSpec zeroIv = new IvParameterSpec(iv);
		SecretKeySpec key = new SecretKeySpec(encryptKey.getBytes(), "DES");
		Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, key, zeroIv);
		byte[] encryptedData = cipher.doFinal(encryptString.getBytes());
		return Base64.encode(encryptedData);
	}

	/**
	 * 根据公钥解密 .
	 * 
	 * @author zhuhb
	 * @param decryptString
	 *            需要解密的字符串
	 * @param decryptKey
	 *            公钥
	 * @return 加密后的值
	 * @throws Exception
	 * @since JDK 1.6
	 */
	public static String decryptDES(String decryptString, String decryptKey)
			throws Exception {
		byte[] byteMi = Base64.decode(decryptString);
		IvParameterSpec zeroIv = new IvParameterSpec(iv);
		SecretKeySpec key = new SecretKeySpec(decryptKey.getBytes(), "DES");
		Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, key, zeroIv);
		byte decryptedData[] = cipher.doFinal(byteMi);
		return new String(decryptedData);
	}
}