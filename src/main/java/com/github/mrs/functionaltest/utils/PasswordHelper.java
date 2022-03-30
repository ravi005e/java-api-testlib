package com.github.mrs.functionaltest.utils;

import java.security.SecureRandom;
import java.util.Random;

public class PasswordHelper {

	public static String generatePassword (int length) {

		//minimum length of 6
		if (!(length >= 6)) {
			length = 6;
		}

		final char[] lowercase = "abcdefghijklmnopqrstuvwxyz".toCharArray();
		final char[] uppercase = "ABCDEFGJKLMNPRSTUVWXYZ".toCharArray();
		final char[] numbers = "0123456789".toCharArray();
		final char[] symbols = "^$?!@#%&".toCharArray();
		final char[] allAllowed = "abcdefghijklmnopqrstuvwxyzABCDEFGJKLMNPRSTUVWXYZ0123456789^$?!@#%&".toCharArray();

		//Use cryptographically secure random number generator
		Random random = new SecureRandom();

		StringBuilder password = new StringBuilder(length);

		//Ensure password policy is met by inserting required random chars in positions
		password.insert(0, lowercase[random.nextInt(lowercase.length)]);
		password.insert(1, uppercase[random.nextInt(uppercase.length)]);
		password.insert(2, numbers[random.nextInt(numbers.length)]);
		password.insert(3, symbols[random.nextInt(symbols.length)]);

		for (int i = password.length(); i < length; i++) {
			password.append(allAllowed[random.nextInt(allAllowed.length)]);
		}

		return password.toString();

	}

    public static void main(String []args){
        System.out.println(PasswordHelper.generatePassword(4));
     }

}
