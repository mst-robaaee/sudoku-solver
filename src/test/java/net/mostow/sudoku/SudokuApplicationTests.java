package net.mostow.sudoku;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SudokuApplicationTests {
	@Value("http://localhost:${local.server.port}/sudoku/simple")
	private String url;

	@Autowired
	TestSamples testSamples;
	@Autowired
	private TestRestTemplate restTemplate;

	@Test
	@Order(1)
	void easy() {
        testWithMatrix(testSamples.sampleEasy);
    }
	@Test
	@Order(2)
	void normal() {
        testWithMatrix(testSamples.sampleNormal);
	}
	@Test
	@Order(3)
	void medium() {
        testWithMatrix(testSamples.sampleMedium);
	}
	@Test
	@Order(4)
	void hard() {
        testWithMatrix(testSamples.sampleHard);
	}
	@Test
	@Order(5)
	void veryHard() {
        testWithMatrix(testSamples.sampleVeryHard);
	}
	@Test
	@Order(6)
	void impossible() {
        testWithMatrix(testSamples.sampleImpossible);
	}

	private int testWithMatrix(String testMatrixAsString) {
		String MethodName = new Exception().getStackTrace()[1].getMethodName();
		ResponseEntity<String> response = this.restTemplate.postForEntity(url, testMatrixAsString, String.class);
		Assert.isTrue(response.getStatusCode().equals(HttpStatus.OK) && checkResultMatrix(response.getBody()), String.format("%s test failed", MethodName));

        String responseTime = response.getHeaders().get("X-Response-Time").get(0);
        System.out.println(String.format("test sample with %s difficulty completed in %sms", MethodName, responseTime));
		return Integer.parseInt(responseTime);
	}
	private boolean checkResultMatrix(String matrixAsJsonArray){
		JsonArray matrixRowArray = JsonParser.parseString(matrixAsJsonArray).getAsJsonArray();
		if(matrixRowArray.size() != 9){
			return false;
		}
		for (int row = 0; row < 9; row++) {
			JsonArray matrixColumnArray = matrixRowArray.get(row).getAsJsonArray();
			if (matrixColumnArray.size() != 9) {
				return false;
			}
			for (int column = 0; column < 9; column++) {
				int number = matrixColumnArray.get(column).getAsJsonPrimitive().getAsInt();
				if (number < 1 || number > 9){
					return false;
				}
			}
		}
		return true;
	}
}
