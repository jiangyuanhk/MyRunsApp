/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cs.dartmouth.edu.myruns.gcm;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.log.InvalidRequestException;

/**
 * Helper class to send messages to the GCM service using an API Key.
 */
public class Sender {

	private String GCM_SEND_URL = "https://android.googleapis.com/gcm/send";
	/**
	 * Initial delay before first retry, without jitter.
	 */
	protected static final int BACKOFF_INITIAL_DELAY = 1000;
	/**
	 * Maximum delay before a retry.
	 */
	protected static final int MAX_BACKOFF_DELAY = 1024000;

	protected final Random random = new Random();
	protected static final Logger logger = Logger.getLogger(Sender.class
			.getName());

	private final String key;

	/**
	 * Default constructor.
	 * 
	 * @param key
	 *            API key obtained through the Google API Console.
	 */
	public Sender(String key) {
		this.key = nonNull(key);
	}

	/**
	 * Sends a message to one device, retrying in case of unavailability.
	 * 
	 * <p>
	 * <strong>Note: </strong> this method uses exponential back-off to retry in
	 * case of service unavailability and hence could block the calling thread
	 * for many seconds.
	 * 
	 * @param message
	 *            message to be sent, including the device's registration id.
	 * @param registrationId
	 *            device where the message will be sent.
	 * @param retries
	 *            number of retries in case of service unavailability errors.
	 * 
	 * @return result of the request (see its javadoc for more details).
	 * 
	 * @throws IllegalArgumentException
	 *             if registrationId is {@literal null}.
	 * @throws InvalidRequestException
	 *             if GCM didn't returned a 200 or 5xx status.
	 * @throws IOException
	 *             if message could not be sent.
	 */
	public Response send(String message, int retries)
			throws IOException {
		Response result;
		int attempt = 0;
		int backoff = BACKOFF_INITIAL_DELAY;
		boolean tryAgain;
		do {
			attempt++;
			result = send(message);
			tryAgain = (result == null || result.mHttpStatus != 200) && attempt <= retries;
			if (tryAgain) {
				int sleepTime = backoff / 2 + random.nextInt(backoff);
				try {
					Thread.sleep(sleepTime);
				} catch (Exception ex) {

				}
				if (2 * backoff < MAX_BACKOFF_DELAY) {
					backoff *= 2;
				}
			}
		} while (tryAgain);
		if (result == null) {
			throw new IOException("Could not send message after " + attempt
					+ " attempts");
		}

		return result;
	}

	public Response send(String message) throws IOException {
		Response result = new Response();

		HttpURLConnection conn;
		try {
			conn = post(GCM_SEND_URL, "application/x-www-form-urlencoded;charset=UTF-8", key,
					message);
//			conn = post(GCM_SEND_URL, "application/json", key,
//					message.toString());
			result.mHttpStatus = conn.getResponseCode();
		} catch (IOException e) {
			logger.log(Level.FINE, "IOException posting to GCM", e);
			return result;
		}
		if (result.mHttpStatus / 100 == 5) {
			logger.fine("GCM service is unavailable (status "
					+ result.mHttpStatus + ")");
			return result;
		}

		if (result.mHttpStatus != 200 && result.mHttpStatus != 400) {
			try {
				result.mMessage = getAndClose(conn.getErrorStream());
				logger.finest("Plain post error response: " + result.mMessage);
			} catch (IOException e) {
				result.mMessage = "N/A";
				logger.log(Level.FINE, "Exception reading response: ", e);
			}
		} else {
			try {
				result.mMessage = getAndClose(conn.getInputStream());
			} catch (IOException e) {
				logger.log(Level.WARNING, "Exception reading response: ", e);
				// return null so it can retry
				return null;
			}
		}

		return result;
	}

	private static void close(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (IOException e) {
				// ignore error
				logger.log(Level.FINEST, "IOException closing stream", e);
			}
		}
	}

	/**
	 * Makes an HTTP POST request to a given endpoint.
	 * 
	 * <p>
	 * <strong>Note: </strong> the returned connected should not be
	 * disconnected, otherwise it would kill persistent connections made using
	 * Keep-Alive.
	 * 
	 * @param url
	 *            endpoint to post the request.
	 * @param contentType
	 *            type of request.
	 * @param body
	 *            body of the request.
	 * 
	 * @return the underlying connection.
	 * 
	 * @throws IOException
	 *             propagated from underlying methods.
	 */
	protected HttpURLConnection post(String url, String contentType,
			String apiKey, String body) throws IOException {
		if (url == null || body == null) {
			throw new IllegalArgumentException("arguments cannot be null");
		}

		logger.fine("Sending POST to " + url);
		logger.finest("POST body: " + body);
		byte[] bytes = body.getBytes();
		HttpURLConnection conn = getConnection(url);
		conn.setDoOutput(true);
		conn.setUseCaches(false);
		conn.setFixedLengthStreamingMode(bytes.length);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", contentType);
		conn.setRequestProperty("Authorization", "key=" + key);
		OutputStream out = conn.getOutputStream();
		try {
			out.write(bytes);
		} finally {
			close(out);
		}
		return conn;
	}

	/**
	 * Creates a map with just one key-value pair.
	 */
	protected static final Map<String, String> newKeyValues(String key,
			String value) {
		Map<String, String> keyValues = new HashMap<String, String>(1);
		keyValues.put(nonNull(key), nonNull(value));
		return keyValues;
	}

	/**
	 * Creates a {@link StringBuilder} to be used as the body of an HTTP POST.
	 * 
	 * @param name
	 *            initial parameter for the POST.
	 * @param value
	 *            initial value for that parameter.
	 * @return StringBuilder to be used an HTTP POST body.
	 */
	protected static StringBuilder newBody(String name, String value) {
		return new StringBuilder(nonNull(name)).append('=').append(
				nonNull(value));
	}

	/**
	 * Adds a new parameter to the HTTP POST body.
	 * 
	 * @param body
	 *            HTTP POST body.
	 * @param name
	 *            parameter's name.
	 * @param value
	 *            parameter's value.
	 */
	protected static void addParameter(StringBuilder body, String name,
			String value) {
		nonNull(body).append('&').append(nonNull(name)).append('=')
				.append(nonNull(value));
	}

	/**
	 * Gets an {@link HttpURLConnection} given an URL.
	 */
	protected HttpURLConnection getConnection(String url) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) new URL(url)
				.openConnection();
		return conn;
	}

	/**
	 * Convenience method to convert an InputStream to a String.
	 * <p>
	 * If the stream ends in a newline character, it will be stripped.
	 * <p>
	 * If the stream is {@literal null}, returns an empty string.
	 */
	protected static String getString(InputStream stream) throws IOException {
		if (stream == null) {
			return "";
		}
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(stream));
		StringBuilder content = new StringBuilder();
		String newLine;
		do {
			newLine = reader.readLine();
			if (newLine != null) {
				content.append(newLine).append('\n');
			}
		} while (newLine != null);
		if (content.length() > 0) {
			// strip last newline
			content.setLength(content.length() - 1);
		}
		return content.toString();
	}

	private static String getAndClose(InputStream stream) throws IOException {
		try {
			return getString(stream);
		} finally {
			if (stream != null) {
				close(stream);
			}
		}
	}

	static <T> T nonNull(T argument) {
		if (argument == null) {
			throw new IllegalArgumentException("argument cannot be null");
		}
		return argument;
	}

}
