package edu.purdue.cs.tornado.helper;

import java.util.UUID;

public class GenerateUUID {
	public static String getUUID() {
		//generate random UUIDs
		UUID id = UUID.randomUUID();
		return id.toString();

	}
}
