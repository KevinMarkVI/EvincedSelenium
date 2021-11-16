package com.evinced.utils;

public class FileUtils {
	private static String baseUrl = "http://localhost:2222/";

	public static String getRelativePath(String filename) {
		return baseUrl + filename;
	}
}
