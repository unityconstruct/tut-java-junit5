package com.uc.qaa.junit5tut;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.function.Executable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class BMICalculatorTest {

	private String environment = "test";

	/**
	 * Best for expensive setup ops like starting servers or connecting to DBs
	 */
	@BeforeAll
	static void beforeAll() {
		System.out.println("Before all unit tests");
	}

	/**
	 * Best for shutting down servers or closing DB connections
	 */
	@AfterAll
	static void afterAll() {
		System.out.println("After all unit tests");
	}

	@Nested
	class IsDietRecommendedTests {

		@Test
		@DisabledOnOs(OS.LINUX)
		void should_RunOnWindows() {
			assertTrue(true);
		}
		
		@Test
		@DisabledOnOs(OS.WINDOWS)
		void should_RunOnLinux() {
			assertTrue(true);
		}
		
		
		@Test
		@Disabled
		void disabledTest() {
			assertTrue(true);
		}
		
		@ParameterizedTest
		@ValueSource(doubles = { 89.0, 95.0, 110.0 })
		void should_ReturnTrue_When_DietRecommended(Double coderWeight) {
			// given - arrange
			double weight = coderWeight;
			double height = 1.72;

			// when - act
			boolean recommended = BMICalculator.isDietRecommended(weight, height);

			// then - assert
			assertTrue(recommended);
		}

		@ParameterizedTest(name = "weight={0}, height={1}")
		@CsvSource(value = { "89.0, 1.72", "95.0, 1.75", "110.0, 1.78" })
		void should_ReturnTrue_When_DietRecommended(Double coderWeight, Double coderHeight) {
			// given - arrange
			double weight = coderWeight;
			double height = coderHeight;

			// when - act
			boolean recommended = BMICalculator.isDietRecommended(weight, height);

			// then - assert
			assertTrue(recommended);
		}

		@ParameterizedTest(name = "weight={0}, height={1}")
		@CsvFileSource(resources = "/diet-recommended-input-data.csv", numLinesToSkip = 1)
		void should_ReturnTrue_When_DietRecommended_CsvFile(Double coderWeight, Double coderHeight) {
			// given - arrange
			double weight = coderWeight;
			double height = coderHeight;

			// when - act
			boolean recommended = BMICalculator.isDietRecommended(weight, height);

			// then - assert
			assertTrue(recommended);
		}

		@Test
		void should_ReturnFalse_When_DietNotRecommended() {
			// given - arrange
			double weight = 50.0;
			double height = 1.92;

			// when - act
			boolean recommended = BMICalculator.isDietRecommended(weight, height);

			// then - assert
			assertFalse(recommended);
		}

		@Test
		void should_ThrowArithmeticException_When_HeightZero(TestInfo info) {
			System.out.println(info.getDisplayName() + "\n" + info.getTestMethod().get().getName());
			
			// given - arrange
			double weight = 50.0;
			double height = 0.00;

			// when - act
			// the lambda expression is an exectuable, but is NOT executed yet
			Executable executable = () -> BMICalculator.isDietRecommended(weight, height);

			// then - assert
			// here asserThrows will execute the executable, then expect ArithmeticException
			// to be thrown
			assertThrows(ArithmeticException.class, executable);
		}
	}

	@Nested
	class GetBMIScoresTests {
		@Test
		void should_ReturnCorrectBMIScoreArray_When_CoderListNotEmpty() {
			// given
			List<Coder> coders = new ArrayList<>();
			coders.add(new Coder(1.80, 60.0));
			coders.add(new Coder(1.82, 98.0));
			coders.add(new Coder(1.82, 64.7));
			double[] expected = { 18.52, 29.59, 19.53 };

			// when
			double[] bmiScores = BMICalculator.getBMIScores(coders);

			// then
			assertAll(() -> assertTrue(Arrays.equals(expected, bmiScores)),
					() -> assertArrayEquals(expected, bmiScores));
		}

		@Test
		void should_ReturnInCorrectBMIScoreArray_When_CoderListNotEmpty() {
			// given
			List<Coder> coders = new ArrayList<>();
			coders.add(new Coder(1.80, 60.0));
			coders.add(new Coder(1.82, 98.0));
			coders.add(new Coder(1.82, 64.7));
			double[] expected = { 18.00, 29.59, 19.53 };

			// when
			double[] bmiScores = BMICalculator.getBMIScores(coders);

			// then
			assertFalse(Arrays.equals(expected, bmiScores));
		}
	}

	@Nested
	@DisplayName("NESTED: FindCoderWithWorstBMI")
	class FindCoderWithWorstBMI {

		@Test
		@DisplayName("Sample display Name")
		void should_ReturnCoderWithWorstBMI_When_CoderListNotEmpty() {
			// given
			List<Coder> coders = new ArrayList<>();
			coders.add(new Coder(1.80, 60.0));
			coders.add(new Coder(1.82, 98.0));
			coders.add(new Coder(1.82, 64.7));

			// when
			Coder coderWorstBMI = BMICalculator.findCoderWithWorstBMI(coders);

			// then
			assertAll(() -> assertEquals(1.82, coderWorstBMI.getHeight()),
					() -> assertEquals(98.0, coderWorstBMI.getWeight()));
		}

		@Test
		void should_ReturnNullrWithWorstBMI_When_CoderListEmpty() {
			// given
			List<Coder> coders = new ArrayList<>();

			// when
			Coder coderWorstBMI = BMICalculator.findCoderWithWorstBMI(coders);

			// then
			assertNull(coderWorstBMI);
		}

		@Test
		void should_ReturnCoderWithWorstBMIIn1Ms_When_CoderListHast10KElements() {
			// given
			List<Coder> coders = new ArrayList<>();
			for (int i = 0; i < 10000; i++) {
				coders.add(new Coder(1.0 + i, 10.0 + i));
			}

			// when
			Executable executable = () -> BMICalculator.findCoderWithWorstBMI(coders);

			// then
			assertTimeout(Duration.ofMillis(10), executable);
		}

		@Test
		void should_ReturnCoderWithWorstBMIIn1Ms_When_CoderListHast10KElements_WithAssumptions() {
			// given
			assumeTrue(BMICalculatorTest.this.environment.equals("prod"));
			List<Coder> coders = new ArrayList<>();
			for (int i = 0; i < 10000; i++) {
				coders.add(new Coder(1.0 + i, 10.0 + i));
			}

			// when
			Executable executable = () -> BMICalculator.findCoderWithWorstBMI(coders);

			// then
			assertTimeout(Duration.ofMillis(10), executable);
		}
	}
}
