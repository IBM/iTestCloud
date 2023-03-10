/*********************************************************************
 * Copyright (c) 2017, 2023 IBM Corporation and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *********************************************************************/
package itest.cloud.utils;

import static itest.cloud.scenario.ScenarioUtils.getParameterValue;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Arrays.copyOf;
import static java.util.Base64.getDecoder;
import static java.util.Base64.getEncoder;
import static javax.crypto.Cipher.DECRYPT_MODE;
import static javax.crypto.Cipher.ENCRYPT_MODE;

import java.security.*;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

import itest.cloud.scenario.errors.ScenarioFailedError;

/**
 * Utilities to perform encryption and decryption to handle sensitive information.
 * <p>
 * </p>
 */
public class EncryptionUtils {

	private static final String ALGORITHM = "AES";
	private static final String CIPHER_TRANSFORMATION = "AES/ECB/PKCS5Padding";
	private static final int KEY_LENGTH = 32;

/**
 * Decrypts a given encrypted text.
 *
 * @param encryptedText the encrypted text as {@link String}.
 *
 * @return the decrypted text as {@link String}.
 */
public static String decrypt(final String encryptedText) {
	return decrypt(encryptedText, getKey());
}

public static String decrypt(final String encryptedText, final String key) {
	try {
		final Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
		cipher.init(DECRYPT_MODE, generateKey(key));

		return new String(cipher.doFinal(getDecoder().decode(encryptedText)));
	}
	catch (IllegalStateException | IllegalArgumentException e) {
		// If reached here, it means that the original text is not encrypted. Therefore, return the particular text to the caller.
		return encryptedText;
	}
	catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
		throw new ScenarioFailedError(e, true /*print*/);
	}
}

/**
 * Encrypts a given text.
 *
 * @param text the text to be encrypted as {@link String}.
 *
 * @return the encrypted text as {@link String}.
 */
public static String encrypt(final String text) {
	return encrypt(text, getKey());
}

private static String encrypt(final String text, final String key) {
	try {
		final Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
		cipher.init(ENCRYPT_MODE, generateKey(key));

		return getEncoder().encodeToString(cipher.doFinal(text.getBytes(UTF_8)));

	}
	catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
		throw new ScenarioFailedError(e, true /*print*/);
	}
}

private static Key generateKey(final String key) {
	final String croppedKey = (key.length() > KEY_LENGTH) ? key.substring(0 /*beginIndex*/, KEY_LENGTH /*endIndex*/) : key;
	final byte[] croppedKeyArray = copyOf(croppedKey.getBytes(UTF_8), KEY_LENGTH);

	return new SecretKeySpec(croppedKeyArray, ALGORITHM);
}

private static String getKey() {
	return getParameterValue("key", false /*print*/);
}

public static void main(final String[] args) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
	if((args == null) || (args.length < 3)) {
		System.err.println("Invalid usage! The following is the expected usage:");
		System.err.println("<text> <secret key> <encrypt|decrypt>");
	}
	else {
		if (args[2].equalsIgnoreCase("encrypt")) {
			System.out.println("Encrypted text: " + encrypt(args[0], args[1]));
		}
		else {
			System.out.println("Descypted text: " + decrypt(args[0], args[1]));
		}
	}
}
}