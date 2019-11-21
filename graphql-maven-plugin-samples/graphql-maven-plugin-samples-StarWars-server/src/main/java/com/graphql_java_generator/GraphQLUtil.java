package com.graphql_java_generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class GraphQLUtil {

	/**
	 * Transform an {@link Iterable} (which can be a {@link List}), into a {@link List} of items of the same type. It's
	 * usefull to transform the native type from Spring Data repositories (which needs concrete class to map into) into
	 * the list of relevant GraphQL interface
	 * 
	 * @param <I>
	 * @param iterable
	 * @return
	 */
	public <I> List<I> iterableToList(Iterable<I> iterable) {
		List<I> ret = new ArrayList<I>();
		for (I i : iterable) {
			ret.add(i);
		}
		return ret;
	}

	/**
	 * Transform an {@link Iterable} (which can be a {@link List}) of a concrete class, into a {@link List} of the I
	 * interface or superclass. It's usefull to transform the native type from Spring Data repositories (which needs
	 * concrete class to map into) into the list of relevant GraphQL interface
	 * 
	 * @param <I>
	 * @param iterable
	 * @return
	 */
	public <I> List<I> iterableConcreteClassToListInterface(Iterable<? extends I> iterable) {
		List<I> ret = new ArrayList<I>();
		for (I i : iterable) {
			ret.add(i);
		}
		return ret;
	}

	/**
	 * Transform an {@link Optional}, as returned by Spring Data repositories, into a standard Java, which is null if
	 * there is no value.
	 * 
	 * @param optional
	 * @return
	 */
	public <T> T optionnalToObject(Optional<T> optional) {
		return optional.isPresent() ? optional.get() : null;
	}

	/**
	 * Convert the given byte in its hexadecimal String representation.
	 * 
	 * @param num
	 * @return A String which contains the given byte in a tow hexadecimal digit (e.g. "ff")
	 */
	public String byteToHex(byte num) {
		char[] hexDigits = new char[2];
		hexDigits[0] = java.lang.Character.forDigit((num >> 4) & 0xF, 16);
		hexDigits[1] = java.lang.Character.forDigit((num & 0xF), 16);
		return new String(hexDigits);
	}

	/**
	 * Convert the given byte array into a UUID
	 * 
	 * @param bytes
	 *            A byte array of 16 bytes (as a UUID has 128 bits). May be null.
	 * @return The corresponding UUID. Or null, if bytes is null.
	 * @throws IllegalArgumentException
	 *             When the byte array is not null, and not 16 bytes long.
	 */
	public UUID convertByteArrayToUUID(byte[] bytes) {
		if (bytes == null) {
			return null;
		} else if (bytes.length != 16) {
			throw new IllegalArgumentException("A UUID is 128 bits long, but the given byte array is " + bytes.length
					+ " long (instead of 16 exepected)");
		} else {
			// a UUID has this format: "00000000-0000-0000-0000-000000000028"
			StringBuffer sb = new StringBuffer();
			int i = 0;
			sb.append(byteToHex(bytes[i++])).append(byteToHex(bytes[i++])).append(byteToHex(bytes[i++]))
					.append(byteToHex(bytes[i++]));
			sb.append("-");
			sb.append(byteToHex(bytes[i++])).append(byteToHex(bytes[i++]));
			sb.append("-");
			sb.append(byteToHex(bytes[i++])).append(byteToHex(bytes[i++]));
			sb.append("-");
			sb.append(byteToHex(bytes[i++])).append(byteToHex(bytes[i++]));
			sb.append("-");
			sb.append(byteToHex(bytes[i++])).append(byteToHex(bytes[i++])).append(byteToHex(bytes[i++]))
					.append(byteToHex(bytes[i++])).append(byteToHex(bytes[i++])).append(byteToHex(bytes[i++]));

			return UUID.fromString(sb.toString());
		}
	}

	/**
	 * Convert a list of UUIDs, stored as byte arrays, into a real list of UUIDs.
	 * 
	 * @param bytes
	 *            a list of byte arrays
	 * @return A list of UUIDs. Or null, if bytes is null.
	 */
	public List<UUID> convertListByteArrayToListUUID(List<byte[]> bytes) {
		if (bytes == null) {
			return null;
		} else {
			List<UUID> ret = new ArrayList<>(bytes.size());
			for (byte[] b : bytes) {
				ret.add(convertByteArrayToUUID(b));
			}
			return ret;
		}
	}

}
