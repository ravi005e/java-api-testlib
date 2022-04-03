package io.github.mrs.functionaltest;

import com.github.javafaker.Faker;
import io.github.mrs.functionaltest.utils.PasswordHelper;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

public class FakeData {

	protected final Faker faker = new Faker();

	// general string manipulations

	/**
	 * @return random breed of cat
	 */
	public String cat() {
		return faker.cat().breed();
	}

	/**
	 * @param length desired length of string
	 * @return string with random letters
	 */
	public String letters(int length) {
		return RandomStringUtils.randomAlphabetic(length);
	}

	public String password(int length) {
		return PasswordHelper.generatePassword(length);
	}

	/**
	 * Lower-case the string, replace spaces with '-', and remove commas.
	 * @param s string to manipulate
	 * @return new updated string
	 */
	public String lowerDash(String s) {
		return s.toLowerCase()
				.replace(" ", "-")
				.replace(",", "");
	}

	/**
	 * Generate a random number
	 *
	 * @param lowerBound lower bound for number generated
	 * @param upperBound upper bound for number generated, inclusive
	 * @return random int
	 */
	public int number(int lowerBound, int upperBound) {
		return ThreadLocalRandom.current().nextInt(lowerBound, upperBound + 1);
	}

	/**
	 * Pad the given string to the desired length if it's shorter, will leave as-is
	 * if it's the desired length or longer.
	 *
	 * @param s string to pad
	 * @param desiredLength desired length of string
	 * @return padded string
	 */
	public String padTo(String s, int desiredLength) {
		if (s.length() < desiredLength) {
			int padCount = desiredLength - s.length();
			for (int i = 1; i <= padCount; i++) {
				s += '*';
			}
		}
		return s;
	}

	public String trimmed(String s, int maxLength) {
		if (s.length() > maxLength) {
			return s.substring(0, maxLength-1);
		} else {
			return s;
		}
	}

	public String numbers(int length) {
		return RandomStringUtils.randomNumeric(length);
	}

	// general fakes

	public String beer() {
		return faker.beer().style();
	}

	public String coffee() {
		return faker.resolve("coffee.variety");
	}

	public String coffeeBody() {
		return faker.resolve("coffee.body");
	}

	public String coffeeDescriptor() {
		return faker.resolve("coffee.descriptor");
	}

	public String color() { return faker.color().name(); }

	public String ingredient() {
		return faker.food().ingredient();
	}

	public String uuid() {
		return UUID.randomUUID().toString();
	}

	public String vegetable() {
		return faker.resolve("food.vegetables");
	}

	public String verb() {
		return faker.resolve("verbs.base");
	}

	public String addToCurrentUTC(int units) {
		Instant current = Instant.now();
		Instant change = current.plus(Duration.ofDays((long) units));
		return change.toString();
	}

	public String addToISOdate(long daysToAdd) {
		LocalDate localDate = LocalDate.now();
		LocalDate inTheFuture = localDate.plusDays(daysToAdd);
		return inTheFuture.toString() + "T00:00:00Z";
	}

	public String subtractFromCurrentUTC(int units) {
		Instant current = Instant.now();
		Instant change = current.minus(Duration.ofDays((long) units));
		return change.toString();
	}

	public String subtractFromISOdate(long daysToSubtract) {
		LocalDate localDate = LocalDate.now();
		LocalDate inThePast = localDate.minusDays(daysToSubtract);
		return inThePast.toString() + "T00:00:00Z";
	}

	public String emptyString() {
		return StringUtils.EMPTY;
	}

	public String firstName() {
		return faker.name().firstName();
	}

	public String lastName() {
		return faker.name().lastName();
	}

	public String fullName() {
		return faker.name().fullName();
	}

	public String username() {
		return (firstName().concat(lastName()).concat(numbers(6))).toLowerCase();
	}

	public String title() {
		return faker.name().title();
	}

	public String city() {
		return faker.address().city();
	}

	public String country() {
		return faker.address().country();
	}

	public String countryCode() {
		return faker.address().countryCode();
	}

	public String fullAddress() {
		return faker.address().fullAddress();
	}

	public String stateAbbr() {
		return faker.address().stateAbbr();
	}

	public String state() {
		return faker.address().state();
	}

	public String address() {
		return faker.address().streetAddress();
	}

	public String zipCode(String stateAbbr) {
		return faker.address().zipCodeByState(stateAbbr);
	}

	public String email() {
		return faker.internet().emailAddress();
	}

}
