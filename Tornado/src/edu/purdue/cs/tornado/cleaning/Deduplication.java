package edu.purdue.cs.tornado.cleaning;

import java.util.ArrayList;
import java.util.HashMap;

import edu.purdue.cs.tornado.messages.DataObject;

public class Deduplication {
	public static Integer CACHE_SIZE = 200000; 
	private int max_cache_size;
	private HashMap<String, Integer> words_index = new HashMap<String, Integer>();
	private String min_string;
	private int min_freq;

	public Deduplication() {
		this.max_cache_size = CACHE_SIZE;
		min_freq = Integer.MAX_VALUE;
		min_string = "";

	}

	public boolean isDuplicate(DataObject o) {

		ArrayList<String> words = o.getObjectText();

		int overlap = 0;

		int total_words = words.size();

		for (String word : words) {

			if (words_index.containsKey(word)) {
				Integer freq = words_index.get(word);
				freq++;

				overlap++;

				if (freq < min_freq) {
					min_freq = freq;
					min_string = word;
				}
			}

			else //Not found in cache
			{

				if (words_index.size() > max_cache_size) //Evict LFU entry
				{
					words_index.remove(min_string);
				}

				words_index.put(word, 1);

				min_freq = 1;
				min_string = word;
			}

		}

		double overlap_ratio = ((double) overlap / (double) total_words);

		if (overlap_ratio > 0.8) {
			return true;
		}

		return false;

	}
}