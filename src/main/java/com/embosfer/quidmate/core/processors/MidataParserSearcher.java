package com.embosfer.quidmate.core.processors;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author embosfer
 *
 */
public class MidataParserSearcher {

	private static final Logger log = LoggerFactory.getLogger(MidataParserSearcher.class);

	private static final String TWO_DIGITS = "%02d";

	private static final String ANY_CHAR = ".*";

	private static final String POUND_SYMBOL = "£";

	private static final String SEPARATOR = ";";

	private static final String ANY_DAY_OR_ANY_MONTH = "\\d\\d";
	private static final String ANY_YEAR = "\\d\\d\\d\\d";

	private List<String> allLines; // Lines of the file, represented as Strings

	public MidataParserSearcher(String fileUri) {
		try {
			allLines = Files.readAllLines(Paths.get(fileUri), StandardCharsets.ISO_8859_1);
		} catch (IOException ex) {
			log.error("Issue while loading file {}", fileUri, ex);
		}
	}
	
	// TODO: use Builder pattern

	/**
	 * @param day
	 *            if null, any day will be used
	 * @param month
	 *            if null, any month will be used
	 * @param year
	 *            if null, any year will be used
	 * @param keywords
	 *            array of keywords
	 * @return list of transactions. Each one is an array of fields following
	 *         this protocol: <br />
	 *         Date;Type;Merchant/Description;Debit/Credit;Balance;
	 */
	public List<String[]> transactions(Integer day, Integer month, Integer year, String[] keywords) {
		final String transacPeriodRegex = buildPeriodRegexFrom(day, month, year);
		final String keywordsRegex = buildKeywordRegexFrom(keywords);

		Stream<String[]> stream = allLines.stream()
				.skip(1)
				.filter(line -> line.matches(transacPeriodRegex))
				.map(line -> line.split(SEPARATOR));
		 
		if (keywordsRegex != null) 
			stream = stream.filter(array -> array[2].matches(keywordsRegex));
				
		return stream.collect(Collectors.toList());
	}

	private String buildPeriodRegexFrom(Integer day, Integer month, Integer year) {
		final StringBuilder period = new StringBuilder("^");
		period.append(day != null ? String.format(TWO_DIGITS, day) : ANY_DAY_OR_ANY_MONTH).append("/");
		period.append(month != null ? String.format(TWO_DIGITS, month) : ANY_DAY_OR_ANY_MONTH).append("/");
		period.append(year != null ? year : ANY_YEAR)
		.append(ANY_CHAR);

		System.out.println("Period: " + period.toString());
		return period.toString();
	}

	private String buildKeywordRegexFrom(String[] keywords) {
		if (keywords == null) return null;
		final StringBuilder keywordBuilder = new StringBuilder(".*("); // starts
		boolean isFirst = true;
		for (String keyword : keywords) {
			if (!isFirst)
				keywordBuilder.append("|");
			isFirst = false;
			keywordBuilder.append(keyword);
		}
		keywordBuilder.append(").*");
		return keywordBuilder.toString();
	}

	// Date;Type;Merchant/Description;Debit/Credit;Balance;
	private static ToDoubleFunction<String[]> debitCreditToDouble() {
		return csvArray -> Double.parseDouble(csvArray[3].replace(POUND_SYMBOL, ""));
	}

	public static void print(List<String[]> expenses) {
		final double totalExpenses = expenses.stream().mapToDouble(debitCreditToDouble()).sum(); // TODO: separate negative and positive
		expenses.stream().forEach(array -> System.out.println(Arrays.toString(array)));
		System.out.println("TOTAL EXPENSES(£): " + totalExpenses);
		System.out.println("-----\n");
	}

	public static void main(String[] args) {
		// 1. download the Midata document from your bank 
		final String fileUri = "/Users/embosfer/Downloads/Statements_Midata.csv";

		// 2. pass it to the constructor of the Midata processor
		final MidataParserSearcher midata = new MidataParserSearcher(fileUri);

		// 3. pass the key words you want the processor to capture as an array (null if you want everything) 
		// and also the period (null if you want everything)
		final String[] billsKeyWords = { "Amazon", "TV LICENCE", "THAMES", "TFL", "EDF", "EON", "SKY" };

		// Example 1: money paid for bills in 2016
		List<String[]> transactions = midata.transactions(null, null, 2016, billsKeyWords);
		print(transactions);
		
		// Example 2: money paid in total in 2016
		transactions = midata.transactions(null, null, 2016, null);
		print(transactions);
	}
}
