package com.coderscampus.assignment;

import java.util.List;

public class Assignment8Application {

	public static void main(String[] args) {
		
		Assignment8 assignment = new Assignment8();
		
        List<Integer> numbers = assignment.getData();
        
        assignment.countAndPrintUniqueNumbers(numbers);
       
		
	}

}
