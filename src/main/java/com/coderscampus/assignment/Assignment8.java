package com.coderscampus.assignment;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Assignment8 {
	private List<Integer> numbers = null;
	private AtomicInteger i = new AtomicInteger(0);

	public Assignment8() {
		try {
			numbers = Files.readAllLines(Paths.get("output.txt")).stream().map(n -> Integer.parseInt(n))
					.collect(Collectors.toList());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method will return the numbers that you'll need to process from the list
	 * of Integers. However, it can only return 1000 records at a time. You will
	 * need to call this method 1,000 times in order to retrieve all 1,000,000
	 * numbers from the list
	 * 
	 * @return Integers from the parsed txt file, 1,000 numbers at a time
	 */
	public List<Integer> getNumbers() {
		int start, end;
		synchronized (i) {
			start = i.get();
			end = i.addAndGet(1000);

			System.out.println("Starting to fetch records " + start + " to " + (end));
		}
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
		}

		List<Integer> newList = new ArrayList<>();
		IntStream.range(start, end).forEach(n -> {
			newList.add(numbers.get(n));
		});
		System.out.println("Done Fetching records " + start + " to " + (end));
		return newList;
	}
		
	/**
	 * This method is to fetch all numbers and collect them into a list asynchronously as fast as possible.
	 * A CachedThreadPool seemed to give me the best result on my system.
	 * note: for STEP 1 of the assignment, I had it perform a System.out.println and return the list fetched for each
	 * 1000 numbers pool. I removed the System.out.println for STEP 2.
	 **/

	public List<Integer> getData() {
		Assignment8 assignment = new Assignment8();
		ExecutorService fixedPool = Executors.newCachedThreadPool();
		
		for (int i = 0; i < 1000; i++) {
			CompletableFuture.supplyAsync(() -> assignment.getNumbers(), fixedPool);
		}
		fixedPool.shutdown();
		return numbers;
	}
	
	/**
	 * This method takes the list created above and prints the unique numbers
	 * and their respective counts, again, as fast as possible.
	 * 
	 * I tried commenting out lines 45 and 58 to see the result without the "fetch" messages
	 * and I get my desired result in less than 2 seconds on my machine.
	 **/

	public void countAndPrintUniqueNumbers(List<Integer> allNumbers) {
		Map<Integer, Long> occurrences = allNumbers.parallelStream()
				.collect(Collectors.groupingByConcurrent(number -> number, Collectors.counting()));
		
		System.out.println("* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *");
		occurrences.forEach((number, count) -> System.out.print(number + "=" + count + ", "));
		System.out.println("\n* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *");
	}
}