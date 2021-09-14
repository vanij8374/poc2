package com.poc.userservice;

import com.poc.userservice.dao.UserDao;
import com.poc.userservice.model.ErrorDetails;
import com.poc.userservice.model.User;
import com.poc.userservice.model.UsersResponse;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@SpringBootTest(classes = UserserviceApplication.class,webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
class UserserviceApplicationTests {

	@LocalServerPort
	private int port;

	@Autowired
    private TestRestTemplate restTemplate;

	@Autowired
	private UserDao dao;

	private String server = "http://localhost:";
	private String service = "/userservice/";

	@Test
	@Order(1)
	void callCreateUserWhenUserDataIsNotValid() {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		User user = new User();
		user.setId(null);
		HttpEntity<User> httpEntity = new HttpEntity<>(user,httpHeaders);
		String url = server+port+service+"user";
		ResponseEntity<ErrorDetails> error = restTemplate.exchange(url, HttpMethod.PUT,httpEntity, ErrorDetails.class);
		Assertions.assertEquals(400,error.getStatusCodeValue(),"Should be 400 bad request");
	}

	@Test
	@Order(2)
	void callCreateUserWhenInvalidDateOfBirthPassed() throws JSONException {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		JSONObject user = new JSONObject();
		user.put("id","1");
		user.put("name","name");
		user.put("surname","usersurname");
		user.put("pinCode",006600);
		user.put("isDeleted",false);
		user.put("joiningDate","11");
		user.put("dateOfBirth","11");

		HttpEntity<String> httpEntity = new HttpEntity<>(user.toString(),httpHeaders);
		String url = server+port+service+"user";
		ResponseEntity<ErrorDetails> error = restTemplate.exchange(url, HttpMethod.PUT,httpEntity, ErrorDetails.class);
		Assertions.assertEquals(400,error.getStatusCodeValue(),"Should be 400 bad request");
	}

	@Test
	@Order(3)
	void callCreateUserWithValidData() throws JSONException {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		JSONObject user = new JSONObject();
		user.put("id","1");
		user.put("name","name");
		user.put("surname","usersurname");
		user.put("pinCode",123456);
		user.put("isDeleted",false);
		user.put("joiningDate","2021-06-11");
		user.put("dateOfBirth","2021-06-11");

		HttpEntity<String> httpEntity = new HttpEntity<>(user.toString(),httpHeaders);
		String url = server+port+service+"user";
		ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.PUT,httpEntity, String.class);
		Assertions.assertEquals(200,responseEntity.getStatusCodeValue(),"Should be 200 and user created");
	}

	@Test
	@Order(4)
	void callUpdateUserWithValidData() throws JSONException {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
		User user = new User();
		user.setId("1");
		user.setName("name");
		user.setSurname("usersurname");
		user.setPinCode(123456);
		user.setJoiningDate(LocalDate.parse("2021-06-11"));
		user.setDateOfBirth(LocalDate.parse("2000-06-11"));
		dao.save(user);
		user.setSurname("surname");

		HttpEntity<User> httpEntity = new HttpEntity<>(user,httpHeaders);
		String url = server+port+service+"user";
		restTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory());
		ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.PATCH,httpEntity, String.class);
		Assertions.assertEquals(200,responseEntity.getStatusCodeValue(),"Should be 200 and user update");
		Optional<User> actualUserFromDB = dao.findById("1");
		Assertions.assertTrue(actualUserFromDB.isPresent());
		Assertions.assertEquals("surname",actualUserFromDB.get().getSurname());

	}

	@Test
	@Order(5)
	void callDeleteUserWithSoftDelete() throws JSONException {
		User user = new User();
		user.setId("1");
		user.setName("name");
		user.setSurname("usersurname");
		user.setPinCode(123456);
		user.setJoiningDate(LocalDate.parse("2021-06-11"));
		user.setDateOfBirth(LocalDate.parse("2000-06-11"));
		dao.save(user);
		HttpEntity<String> httpEntity = new HttpEntity<>(null);
		String url = server+port+service+"user/"+1+"?softDelete=true";
		ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.DELETE,httpEntity, String.class);
		Assertions.assertEquals(200,responseEntity.getStatusCodeValue(),"Should be 200 and user created");
		Optional<User> actualUserFromDB = dao.findById("1");
		Assertions.assertTrue(actualUserFromDB.isPresent());
		Assertions.assertTrue(actualUserFromDB.get().isDeleted());
	}

	@Test
	@Order(6)
	void callDeleteUserWhenUserNotPresent() throws JSONException {
		HttpEntity<String> httpEntity = new HttpEntity<>(null);
		String url = server+port+service+"user/"+1;
		ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.DELETE,httpEntity, String.class);
		Assertions.assertEquals(400,responseEntity.getStatusCodeValue(),"Should be 400 and user not found to delete");
	}

	@Test
	@Order(7)
	void callDeleteUserWhenUserDeleteSuccess() throws JSONException {
		User user = new User();
		user.setId("1");
		user.setName("name");
		user.setSurname("usersurname");
		user.setPinCode(123456);
		user.setJoiningDate(LocalDate.parse("2021-06-11"));
		user.setDateOfBirth(LocalDate.parse("2000-06-11"));
		dao.save(user);
		HttpEntity<String> httpEntity = new HttpEntity<>(null);
		String url = server+port+service+"user/"+1;
		ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.DELETE,httpEntity, String.class);
		Assertions.assertEquals(200,responseEntity.getStatusCodeValue(),"Should be 200 and user deleted");
		Optional<User> actualUserFromDB = dao.findById("1");
		Assertions.assertFalse(actualUserFromDB.isPresent());
	}

	@Test
	@Order(8)
	void callGetsUsersWithoutConditionWithValidData() throws JSONException {
		User user = new User();
		user.setId("1");
		user.setName("name");
		user.setSurname("usersurname");
		user.setPinCode(123456);
		user.setJoiningDate(LocalDate.parse("2021-06-11"));
		user.setDateOfBirth(LocalDate.parse("2000-06-11"));
		dao.save(user);
		HttpEntity<String> httpEntity = new HttpEntity<>(null);
		String url = server+port+service+"users";
		ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET,httpEntity, String.class);
		Assertions.assertEquals(400,responseEntity.getStatusCodeValue(),"Should be 400 and Condition Required");
	}

	@Test
	@Order(9)
	void callGetsUsersByPincodeWithValidData() throws JSONException {
		User user = new User();
		user.setId("1");
		user.setName("name");
		user.setSurname("usersurname");
		user.setPinCode(123456);
		user.setJoiningDate(LocalDate.parse("2021-06-11"));
		user.setDateOfBirth(LocalDate.parse("2000-06-11"));
		dao.save(user);
		HttpEntity<String> httpEntity = new HttpEntity<>(null);
		String url = server+port+service+"users"+"?pinCode=123456";
		ResponseEntity<UsersResponse> responseEntity = restTemplate.exchange(url, HttpMethod.GET,httpEntity, UsersResponse.class);
		Assertions.assertEquals(200,responseEntity.getStatusCodeValue(),"Should be 200 and Condition Required");
		UsersResponse response = responseEntity.getBody();
		Assertions.assertNotNull(response.getUsers());
		Assertions.assertFalse(((List<User>)response.getUsers()).isEmpty());
	}

	@Test
	@Order(10)
	void callGetsUsersBySurnameWithValidData() throws JSONException {
		User user = new User();
		user.setId("1");
		user.setName("name");
		user.setSurname("usersurname");
		user.setPinCode(123456);
		user.setJoiningDate(LocalDate.parse("2021-06-11"));
		user.setDateOfBirth(LocalDate.parse("2000-06-11"));
		dao.save(user);
		HttpEntity<String> httpEntity = new HttpEntity<>(null);
		String url = server+port+service+"users"+"?surname=usersurname";
		ResponseEntity<UsersResponse> responseEntity = restTemplate.exchange(url, HttpMethod.GET,httpEntity, UsersResponse.class);
		Assertions.assertEquals(200,responseEntity.getStatusCodeValue(),"Should be 200 and Condition Required");
		UsersResponse response = responseEntity.getBody();
		Assertions.assertNotNull(response.getUsers());
		Assertions.assertFalse(((List<User>)response.getUsers()).isEmpty());
	}

	@Test
	@Order(11)
	void callGetsUsersByNameWithValidData() throws JSONException {
		User user = new User();
		user.setId("1");
		user.setName("name");
		user.setSurname("usersurname");
		user.setPinCode(123456);
		user.setJoiningDate(LocalDate.parse("2021-06-11"));
		user.setDateOfBirth(LocalDate.parse("2000-06-11"));
		dao.save(user);
		HttpEntity<String> httpEntity = new HttpEntity<>(null);
		String url = server+port+service+"users"+"?name=name";
		ResponseEntity<UsersResponse> responseEntity = restTemplate.exchange(url, HttpMethod.GET,httpEntity, UsersResponse.class);
		Assertions.assertEquals(200,responseEntity.getStatusCodeValue(),"Should be 200 and Condition Required");
		UsersResponse response = responseEntity.getBody();
		Assertions.assertNotNull(response.getUsers());
		Assertions.assertFalse(((List<User>)response.getUsers()).isEmpty());
	}


	@AfterEach
	public void tearDown(){
		dao.deleteAll();
	}

}
