/**
 * 
 */
package io.vtown.WeiTangApp.comment.net.https;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.content.Context;
import android.util.Log;
 
/**
 *@author 王永奎 E-mail:wangyongkui@ucfgroup.com
 *@department 互联网金融部
 *@version 创建时间：2015-7-29 下午3:21:47
 */

public class HttpsUtils {
	/**
	 * HttpUrlConnection 方式，支持指定load-der.crt证书验证，此种方式Android官方建议
	 * 
	 * @throws CertificateException
	 * @throws IOException
	 * @throws KeyStoreException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 */
	public void initSSL(Context context) throws CertificateException, IOException, KeyStoreException,
			NoSuchAlgorithmException, KeyManagementException {
		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		InputStream in = context.getAssets().open("load-der.crt");
		Certificate ca = cf.generateCertificate(in);

		KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
		keystore.load(null, null);
		keystore.setCertificateEntry("ca", ca);

		String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
		TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
		tmf.init(keystore);

		// Create an SSLContext that uses our TrustManager
		SSLContext sslcontext = SSLContext.getInstance("TLS");
		sslcontext.init(null, tmf.getTrustManagers(), null);
		URL url = new URL("https://certs.cac.washington.edu/CAtest/");
		HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
		urlConnection.setSSLSocketFactory(sslcontext.getSocketFactory());
		InputStream input = urlConnection.getInputStream();

		BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = reader.readLine()) != null) {
			result.append(line);
		}
		Log.e("TTTT", result.toString());
	}

	/**
	 * HttpUrlConnection支持所有Https免验证，不建议使用
	 * 
	 * @throws KeyManagementException
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 */
	public void initSSLALL() throws KeyManagementException, NoSuchAlgorithmException, IOException {
		URL url = new URL("https://certs.cac.washington.edu/CAtest/");
		SSLContext context = SSLContext.getInstance("TLS");
		context.init(null, new TrustManager[] { new TrustAllManager() }, null);
		HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
		HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {

			@Override
			public boolean verify(String arg0, SSLSession arg1) {
				return true;
			}
		});
		HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
		connection.setDoInput(true);
		connection.setDoOutput(false);
		connection.setRequestMethod("GET");
		connection.connect();
		InputStream in = connection.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String line = "";
		StringBuffer result = new StringBuffer();
		while ((line = reader.readLine()) != null) {
			result.append(line);
		}
		Log.e("TTTT", result.toString());
	}
	
	/**
	 * HttpClient方式实现，支持所有Https免验证方式链接
	 * 
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
//	public void initSSLAllWithHttpClient() throws ClientProtocolException, IOException {
//		int timeOut = 30 * 1000;
//		HttpParams param = new BasicHttpParams();
//		HttpConnectionParams.setConnectionTimeout(param, timeOut);
//		HttpConnectionParams.setSoTimeout(param, timeOut);
//		HttpConnectionParams.setTcpNoDelay(param, true);
//
//		SchemeRegistry registry = new SchemeRegistry();
//		registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
//		registry.register(new Scheme("https", TrustAllSSLSocketFactory.getDefault(), 443));
//		ClientConnectionManager manager = new ThreadSafeClientConnManager(param, registry);
//		DefaultHttpClient client = new DefaultHttpClient(manager, param);
//
//		HttpGet request = new HttpGet("https://certs.cac.washington.edu/CAtest/");
//		// HttpGet request = new HttpGet("https://www.alipay.com/");
//		HttpResponse response = client.execute(request);
//		HttpEntity entity = response.getEntity();
//		BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
//		StringBuilder result = new StringBuilder();
//		String line = "";
//		while ((line = reader.readLine()) != null) {
//			result.append(line);
//		}
//		Log.e("HTTPS TEST", result.toString());
//	}

	/**
	 * HttpClient方式实现，支持验证指定证书
	 * 
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public void initSSLCertainWithHttpClient(Context m) throws ClientProtocolException, IOException {
		int timeOut = 30 * 1000;
		HttpParams param = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(param, timeOut);
		HttpConnectionParams.setSoTimeout(param, timeOut);
		HttpConnectionParams.setTcpNoDelay(param, true);

		SchemeRegistry registry = new SchemeRegistry();
		registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
		registry.register(new Scheme("https", TrustCertainHostNameFactory.getDefault(m), 443));
		ClientConnectionManager manager = new ThreadSafeClientConnManager(param, registry);
		DefaultHttpClient client = new DefaultHttpClient(manager, param);

		// HttpGet request = new
		// HttpGet("https://certs.cac.washington.edu/CAtest/");
		HttpGet request = new HttpGet("https://www.alipay.com/");
		HttpResponse response = client.execute(request);
		HttpEntity entity = response.getEntity();
		BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
		StringBuilder result = new StringBuilder();
		String line = "";
		while ((line = reader.readLine()) != null) {
			result.append(line);
		}
		Log.e("HTTPS TEST", result.toString());
	}
/**
 * 
 *  免验证
 *
 */
	public class TrustAllManager implements X509TrustManager {

		@Override
		public void checkClientTrusted(X509Certificate[] arg0, String arg1)
				throws CertificateException {
			// TODO Auto-generated method stub

		}

		@Override
		public void checkServerTrusted(X509Certificate[] arg0, String arg1)
				throws CertificateException {
			// TODO Auto-generated method stub

		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			// TODO Auto-generated method stub
			return null;
		}
	}

}
