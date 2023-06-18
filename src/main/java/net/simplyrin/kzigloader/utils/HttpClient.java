package net.simplyrin.kzigloader.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import lombok.Setter;

/**
 * Created by SimplyRin on 2019/03/25.
 *
 *  Copyright 2019 SimplyRin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class HttpClient {

	@Setter
	private URL url;
	@Setter
	private String userAgent = "Mozilla/5.0 (HttpClient by SimplyRin)";
	@Setter
	private String data;
	@Setter
	private List<String> headers = new ArrayList<>();
	@Setter
	private Proxy proxy;

	public HttpClient(String _url) {
		try {
			this.url = new URL(_url);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addHeader(String key, String value) {
		this.headers.add(key + ":" + value);
	}

	@Deprecated
	public void setPost(String data) {
		this.data = data;
	}

	public String getResult() {
		return this.toString();
	}

	@Override
	public String toString() {
		try {
			return IOUtils.toString(this.getInputStream(), Charset.forName("UTF-8"));
		} catch (IOException e) {
			// e.printStackTrace();
		}
		return null;
	}

	public JsonElement getJsonElement() {
		return new JsonParser().parse(this.toString());
	}

	public JsonObject getAsJsonObject() {
		return this.getJsonElement().getAsJsonObject();
	}

	public InputStream getInputStream() {
		try {
			return this.getHttpURLConnection().getInputStream();
		} catch (IOException e) {
			// e.printStackTrace();
		}
		return null;
	}

	public HttpURLConnection getHttpURLConnection() {
		HttpURLConnection connection = null;
		try {
			if (this.proxy != null) {
				connection = (HttpURLConnection) this.url.openConnection(this.proxy);
			} else {
				connection = (HttpURLConnection) this.url.openConnection();
			}
		} catch (Exception e) {
			// e.printStackTrace();
			return null;
		}
		connection.addRequestProperty("User-Agent", this.userAgent);
		// connection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
		if (this.headers != null) {
			for (String header : this.headers) {
				String key = "";
				String value = "";
				try {
					key = header.split(":")[0];
					value = header.split(":")[1];
				} catch (Exception e) {
				}
				connection.setRequestProperty(key, value);
			}
		}
		connection.setConnectTimeout(10000);
		connection.setReadTimeout(10000);
		if (this.data != null) {
			connection.setDoOutput(true);
			try {
				OutputStreamWriter outputStreamWriter = new OutputStreamWriter(connection.getOutputStream());
				outputStreamWriter.write(data);
				outputStreamWriter.close();
				connection.connect();
			} catch (Exception e) {
				// e.printStackTrace();
			}
		}

		return connection;
	}

	public static String fetch(String url) {
		try {
			return fetch(new URL(url));
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return null;
	}

	public static String fetch(String url, Proxy args) {
		try {
			return fetch(new URL(url), null, args);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return null;
	}

	public static String fetch(String url, String args) {
		try {
			return fetch(new URL(url), args);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return null;
	}

	public static String fetch(String url, String args, Proxy proxy) {
		try {
			return fetch(new URL(url), args, proxy);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return null;
	}

	public static String fetch(URL url) {
		return fetch(url, (Proxy) null);
	}

	public static String fetch(URL url, Proxy proxy) {
		return fetch(url, null, proxy);
	}

	public static String fetch(URL url, String args) {
		try {
			return IOUtils.toString(getInputStream(url, args, null), Charset.forName("UTF-8"));
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return null;
	}

	public static String fetch(URL url, String args, Proxy proxy) {
		try {
			return IOUtils.toString(getInputStream(url, args, proxy), Charset.forName("UTF-8"));
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return null;
	}

	public static InputStream getInputStream(String url, String args) {
		try {
			return getInputStream(new URL(url), args, null);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return null;
	}

	public static InputStream getInputStream(String url, String args, Proxy proxy) {
		try {
			return getInputStream(new URL(url), args, proxy);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return null;
	}

	public static InputStream getInputStream(URL url, String args, Proxy proxy) {
		try {
			return getHttpURLConnection(url, args, proxy).getInputStream();
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return null;
	}

	public static HttpURLConnection getHttpURLConnection(String url, String args, Proxy proxy) {
		try {
			return getHttpURLConnection(new URL(url), args, proxy);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return null;
	}

	public static HttpURLConnection getHttpURLConnection(URL url, String args, Proxy proxy) {
		HttpURLConnection connection = null;
		try {
			if (proxy != null) {
				connection = (HttpURLConnection) url.openConnection(proxy);
			} else {
				connection = (HttpURLConnection) url.openConnection();
			}
		} catch (Exception e) {
			// e.printStackTrace();
			return null;
		}
		connection.addRequestProperty("User-Agent", "Mozilla/5.0 (HttpClient by SimplyRin)");
		connection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
		connection.setConnectTimeout(10000);
		connection.setReadTimeout(10000);
		if (args != null) {
			connection.setDoOutput(true);
			try {
				OutputStreamWriter outputStreamWriter = new OutputStreamWriter(connection.getOutputStream());
				outputStreamWriter.write(args);
				outputStreamWriter.close();
				connection.connect();
			} catch (Exception e) {
				// e.printStackTrace();
			}
		}

		return connection;
	}

	public static JsonObject getAsJsonObject(String url) {
		try {
			return getAsJsonObject(new URL(url));
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return null;
	}

	public static JsonObject getAsJsonObject(URL url) {
		return new JsonParser().parse(fetch(url)).getAsJsonObject();
	}

}
