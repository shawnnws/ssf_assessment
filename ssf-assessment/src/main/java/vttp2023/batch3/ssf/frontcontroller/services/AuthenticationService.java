package vttp2023.batch3.ssf.frontcontroller.services;

import java.io.StringReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import vttp2023.batch3.ssf.frontcontroller.model.UserLoginState;
import vttp2023.batch3.ssf.frontcontroller.respositories.AuthenticationRepository;

@Service
public class AuthenticationService {

	@Autowired
	AuthenticationRepository authenticationRepository;

	private static final String AUTHEN_URL = "https://authservice-production-e8b2.up.railway.app";

	static class AuthPayload {
		public String message;

		public static AuthPayload deserialize(String payload) {
			AuthPayload deserialized = new AuthPayload();
			StringReader stringReader = new StringReader(payload);
			JsonReader reader = Json.createReader(stringReader);
			JsonObject object = reader.readObject();
			deserialized.message = object.getString("message");
			return deserialized;
		}
	}

	// Hashmap to keep track of a given username's authentication state
	private HashMap<String, UserLoginState> authStateHashMap = new HashMap<String, UserLoginState>();

	// TODO: Task 2
	// DO NOT CHANGE THE METHOD'S SIGNATURE
	// Write the authentication method in here
	public void authenticate(String username, String password) throws Exception {
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(new ArrayList<>(Collections.singletonList(MediaType.APPLICATION_JSON)));
		JSONObject payload = new JSONObject();
		payload.put("username", username);
		payload.put("password", password);

		HttpEntity<String> entity = new HttpEntity<String>(payload.toString(), headers);
		String authUrl = UriComponentsBuilder.fromUriString(AUTHEN_URL).toUriString();

		RestTemplate template = new RestTemplate();

		ResponseEntity<String> resp = null;

		try {
			resp = template.exchange(authUrl, HttpMethod.POST, entity, String.class);
		} 
		catch (HttpClientErrorException e) {
			if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
				UserLoginState singleAuthState = new UserLoginState(UserLoginState.AuthStatus.BAD_REQUEST, "Incorrect username or password", 0);
				authStateHashMap.put(username, singleAuthState);
				return;
			} 
			else if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
				UserLoginState singleAuthState = new UserLoginState(UserLoginState.AuthStatus.UNAUTHORIZED, "invalid payload", 0);
				authStateHashMap.put(username, singleAuthState);
				return;
			}
		}

		if (resp != null && resp.getStatusCode() == HttpStatus.CREATED) {
			String result = resp.getBody();
			AuthPayload desrialized = AuthPayload.deserialize(result);
			UserLoginState singleAuthState = new UserLoginState(UserLoginState.AuthStatus.ACCEPTED, desrialized.message, 0);
			authStateHashMap.put(username, singleAuthState);
		}
	}

	public UserLoginState getAuthState(String username) {
		return authStateHashMap.get(username);
	}

	public UserLoginState updateUserAsUnauthorized(String username) {
		UserLoginState prevAuthState = getAuthState(username);
		prevAuthState.setPrevAuthEnum(UserLoginState.AuthStatus.UNAUTHORIZED);
		prevAuthState.incrementIncorrectLoginAttempt();
		return prevAuthState;
	}

	public UserLoginState updateUserAsIncorrectCaptcha(String username) {
		UserLoginState prevAuthState = getAuthState(username);
		prevAuthState.setPrevAuthEnum(UserLoginState.AuthStatus.INCORRECT_CAPTCHA);
		prevAuthState.incrementIncorrectLoginAttempt();
		return prevAuthState;
	}

	public void resetIncorrectLoginAttempts(String username) {
		UserLoginState existingState = authStateHashMap.get(username);
		existingState.setIncorrectLoginAttempt(0);
	}

	public String getRedisLoginKey(String username) {
		return String.format("logged_in_%s", username);
	}

	public boolean userIsLoggedIn(String username) {
		Optional<String> loginTime = authenticationRepository.get(getRedisLoginKey(username));
		return loginTime.isPresent();
	}

	public void markUserAsLoggedIn(String username) {
		UserLoginState existingState = authStateHashMap.get(username);
		existingState.setPrevAuthEnum(UserLoginState.AuthStatus.ACCEPTED);
		existingState.setIncorrectLoginAttempt(0);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		String formattedTime = formatter.format(LocalDateTime.now());
		authenticationRepository.save(getRedisLoginKey(username), formattedTime);
	}

	public void markUserAsLoggedOut(String username) {
		authenticationRepository.remove(getRedisLoginKey(username));
	}

	// TODO: Task 3
	// DO NOT CHANGE THE METHOD'S SIGNATURE
	// Write an implementation to disable a user account for 30 mins
	public void disableUser(String username) {
	}

	// TODO: Task 5
	// DO NOT CHANGE THE METHOD'S SIGNATURE
	// Write an implementation to check if a given user's login has been disabled
	public boolean isLocked(String username) {
		return false;
	}
}
