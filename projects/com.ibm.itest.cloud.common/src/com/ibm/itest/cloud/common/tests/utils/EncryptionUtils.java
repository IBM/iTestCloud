/*********************************************************************
 * Copyright (c) 2017, 2022 IBM Corporation and others.
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
package com.ibm.itest.cloud.common.tests.utils;

import static com.ibm.itest.cloud.common.tests.scenario.ScenarioUtils.getParameterValue;

import java.util.Base64.Decoder;
import java.util.Base64.Encoder;

import com.ibm.automation.framework.security.Cryptography;

/**
 * Utilities to perform encryption and decryption to handle sensitive information.
 * <p>
 * </p>
 */
public class EncryptionUtils {

/**
 * Decrypt a given text.
 *
 * To provide a little more secrecy than standard Base64 (which can easily be
 * decoded on various Internet sites), the Base64 encoded value has been reversed.
 *
 * @param encryptedText The encrypted text.
 *
 * @return The decrypted text as {@link String}.
 */
public static String decrypt(final String encryptedText) {
	String secretKey = getSecretKey();
	if(secretKey == null) {
		// If a secret key is not provided, use the legacy approach to decrypt the text.
		Decoder decoder = java.util.Base64.getDecoder();
		return new String(decoder.decode(reverse(encryptedText).getBytes()));
	}
	// If a secret key is provided, use it to decrypt the text.
	try {
		return (new Cryptography()).decryptText(encryptedText, secretKey);
	}
	catch (IllegalStateException | IllegalArgumentException e) {
		// If reached here, it means that the original text is not encrypted. Therefore, return the particular text to the caller.
		return encryptedText;
	}
}

/**
 * Encrypt a given text.
 *
 * To provide a little more secrecy than standard Base64 (which can easily be
 * decoded on various Internet sites), the Base64 encoded value has been reversed.
 *
 * @param originalText The encrypted text.
 *
 * @return The encrypted text as {@link String}.
 */
public static String encrypt(final String originalText) {
	// If a secret key is provided, use it to encrypt the text.
	String secretKey = getSecretKey();
	if(secretKey != null) {
		return (new Cryptography()).encryptText(originalText, secretKey);
	}
	// If a secret key is not provided, use the legacy approach to encrypt the text.
	Encoder encoder = java.util.Base64.getEncoder();
	return reverse(new String(encoder.encode(originalText.getBytes())));
}

/**
 * Generate a secret key for encrypting texts.
 *
 * @return The generated secret key for encrypting texts as {@link String}.
 */
public static String generateSecretKey() {
	return (new Cryptography()).generateSecret();
}

public static String getSecretKey() {
	return getParameterValue("secretKey", false /*print*/);
}

public static void main(final String[] args) {
	if((args == null) || (args.length < 3)) {
		System.err.println("Invalid usage! The following is the expected usage:");
		System.err.println("<text> <secret key> <encrypt|decrypt>");
	}
	else {
		if (args[2].equalsIgnoreCase("encrypt")) {
			System.out.println("Encrypted text: " + (new Cryptography()).encryptText(args[0], args[1]));
		}
		else {
			System.out.println("Descypted text: " + (new Cryptography()).decryptText(args[0], args[1]));
		}
	}
}

/**
 * Reverse a Base64 encoded text string, keeping the equal signs at the end.
 *
 * @param text The text to reverse.
 *
 * @return The reversed text as {@link String}.
 */
private static String reverse(final String text) {
	String text2 = text;
	String equals = "";
	String reversedText = "";
	while (text2.endsWith("=")) {
		equals += "=";
		text2 = text2.substring(0, text2.length() - 1);
	}
	for (int i = text2.length() - 1; i >= 0; i--)
		reversedText += text2.charAt(i);
	return reversedText + equals;
}
}
