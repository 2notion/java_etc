package kbbridge;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.json.JSONObject;
import org.json.XML;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;


//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration({
//	"file:src/main/webapp/WEB-INF/config/springmvc/dispatcher-servlet.xml",
//    "classpath:spring/context-*.xml"}
//    )
//@TestPropertySource(properties = {
////	    "classpath:message/kb-*.xml",
//	    "jdbc.username=",
//	    "jdbc.password.dev=",
//	    "bridge.dev.domain=",
//	    "bridge.real.domain=",
//	    "bizdata.api.ip.dev=",
//	    "bizdata.api.ip.real=",
//	})
//@WebAppConfiguration
public class LiivOnDataParser  {

	/*
	 * private static final Logger logger =
	 * LoggerFactory.getLogger(LiivOnDataParser.class);
	 *
	 * @Autowired // Dependency Injection(의존성 주입) private WebApplicationContext wac;
	 * private MockMvc mock;
	 *
	 * @Before public void beforeTest(){ logger.info("===== beforeTest() =====");
	 * mock = MockMvcBuilders.webAppContextSetup(wac).build(); logger.info("wac: " +
	 * wac); logger.info("mock: " + mock); }
	 */

	public static <E> String postRequest(String pURL, HashMap < String, String > pList) {

        String myResult = "";

        URL url = null;
        HttpsURLConnection http = null;
        BufferedReader reader = null;

        try {

        	TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
        		public X509Certificate[] getAcceptedIssuers() {
        			return null;
        		}
        		public void checkClientTrusted(X509Certificate[] certs, String authType) {
        		}
        		public void checkServerTrusted(X509Certificate[] certs, String authType) {
        		}
        	}};

        	SSLContext sc = SSLContext.getInstance("TLS");
        	sc.init(null, trustAllCerts, new SecureRandom());
        	HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());


            //   URL 설정하고 접속하기
            url = new URL(pURL); // URL 설정
            http = (HttpsURLConnection) url.openConnection(); // 접속

            //--------------------------
            //   전송 모드 설정 - 기본적인 설정
            //--------------------------
            http.setDefaultUseCaches(false);
            http.setDoInput(true); // 서버에서 읽기 모드 지정

            http.setDoOutput(true); // 서버로 쓰기 모드 지정
            http.setRequestMethod("POST"); // 전송 방식은 POST



            //--------------------------
            // 헤더 세팅
            //--------------------------
            // 서버에게 웹에서 <Form>으로 값이 넘어온 것과 같은 방식으로 처리하라는 걸 알려준다
            http.setRequestProperty("content-type", "application/x-www-form-urlencoded");


            //--------------------------
            //   서버로 값 전송
            //--------------------------
            StringBuffer buffer = new StringBuffer();

            //HashMap으로 전달받은 파라미터가 null이 아닌경우 버퍼에 넣어준다
            if (pList != null) {

                Set key = pList.keySet();

                for (Iterator<E> iterator = key.iterator(); iterator.hasNext();) {
                    String keyName = (String) iterator.next();
                    String valueName = pList.get(keyName);
                    buffer.append(keyName).append("=").append(valueName);
                }
            }

            //--------------------------
            //   전송 예제1
            //--------------------------
			/*
			 * OutputStreamWriter outStream = new OutputStreamWriter(http.getOutputStream(),
			 * "UTF-8"); PrintWriter writer = new PrintWriter(outStream);
			 * writer.write(buffer.toString()); writer.flush();
			 */

            //--------------------------
            //   전송 예제2
            //--------------------------
            try (OutputStream out = http.getOutputStream()) {
				out.write((buffer.toString()).getBytes());
			}


            //--------------------------
            //   서버에서 전송받기
            //--------------------------
            if(http.getResponseCode() == HttpsURLConnection.HTTP_OK) {
            	reader = new BufferedReader(new InputStreamReader(http.getInputStream(), "UTF-8"));
                StringBuffer strData = new StringBuffer();

                String str;
                while ((str = reader.readLine()) != null) {
                	strData.append(str);
                }

                http.disconnect();
                reader.close();

                myResult = strData.toString();


            }else {
            	System.out.println("응답코드 : " + http.getResponseCode());
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException | IOException | KeyManagementException e) {
        	e.printStackTrace();
        } finally {
        	if (reader != null) {
                try {
                    reader.close();
                } catch (IOException exp) {
//                    Log.e(TAG, "", exp);
                }
            }
        }
        return myResult;
    }

	/**
	 * get JsonData in Element
	 * @return
	 */
	public JsonObject getJsonElementData(String parseJsonData, String strKey) {

		JsonObject returnData = null;

		try {

			if(!"".equals(parseJsonData)) {


				StringBuffer sb      =  new StringBuffer();
				   sb.append("<?xml version=\"1.0\" encoding=\"EUC-KR\"?>");
				   sb.append("<response>");
				   sb.append(" <response_header>");
				   sb.append("<version>2.0</version>");
				   sb.append("<primitive_type>SUBSCRIPTION_NOTIFICATION_RESPONSE</primitive_type>");
				   sb.append("<transaction_id>537844880</transaction_id>");
				   sb.append("<connection_id>CPTET_23000320</connection_id>");
				   sb.append("</response_header>");
				   sb.append("<subscription_notification_response>");
				   sb.append("<result>0</result>");
				   sb.append("<user_inf/o_result>");
				   sb.append("<user_info>");
				   sb.append("<type>MDN</type>");
				   sb.append("<id>0117991370</id>");
				   sb.append(" </user_info>");
				   sb.append("  <status>16</status>");
				   sb.append("</user_info_result>");
				   sb.append("</subscription_notification_response>");
				   sb.append("</response>");


				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuild = factory.newDocumentBuilder();


//				JsonParser jp = new JsonParser();
//				JsonObject jo = jp.parse(parseJsonData).getAsJsonObject();

				JSONObject jo = new JSONObject(parseJsonData);

				String xml = "<?xml version=\"1.0\" encoding=\"EUC-KR\"?>";
				xml += XML.toString(jo);

				InputSource is = new InputSource(new StringReader(sb.toString())); // XML entitydoc
				Document doc = docBuild.parse(is);
				doc.getDocumentElement().normalize();

				NodeList nodelist     =  doc.getElementsByTagName("status");

				   Node node       =  nodelist.item(0);//첫번째 element 얻기


				   System.out.println(node.getNodeName());

				   Node textNode      =  nodelist.item(0).getChildNodes().item(0);

				   //element의 text 얻기

				   System.out.println("SYS : " + textNode.getNodeValue());




//				SAXBuilder builder = new SAXBuilder();
//				Document doc = (Document) builder.build(new StringReader("<root><node1></node1></root>"));
//				Document doc = docBuild.parse(new InputSource(new StringReader(xml) ));

//	            TransformerFactory transformerFactory = TransformerFactory.newInstance();
//	            Transformer transformer = transformerFactory.newTransformer();
//	            DOMSource DOMsource = new DOMSource(doc);

//				System.out.println(DOMsource.getNode().getChildNodes());
//				System.out.println(XML.toJSONObject(parseJsonData));
//				Document parse = doc.parse("data/data.xml");

//				parse.getNodeName();



//				JsonParser jp = new JsonParser();
//				JsonOCbject jo = jp.parse(parseJsonData).getAsJsonObject();

				LinkedTreeMap<String, JsonElement> members =
					      new LinkedTreeMap<String, JsonElement>();


//				System.out.println("SYS : " + members.get(strKey));
				System.out.println("SYS : " + members.containsKey(strKey));
				members.containsKey(strKey);
				members.get(strKey);
//				for (Map.Entry<String, JsonElement> entry : jo.entrySet()) {


					// 키를 get
//					String getKey = entry.getKey();
//					entry.

//					entry.getValue()



//					System.out.println(entry.getKey());
//			      result.add(entry.getKey(), entry.getValue().deepCopy());
//			    }
//				if(jo != null) {
//					System.out.println(jo.getAsString());
// 					Iterator it = jo.entrySet().iterator();
//					while(it.hasNext()) {
//						System.out.println(it.next());
//
//					}
//
//				}


			}

		}catch(Exception e) {
			e.printStackTrace();
		}

		return returnData;
	}

	/**
	 *  get LiivonData JSON
	 */
	@Test
	public void getLiivOnJsonData() {

		String pURL = "https://onland.kbstar.com/quics?page=&QAction=828410&RType=json";

		HashMap<String, String> pList = new HashMap<String, String>();

		// Data Set
		pList.put("법정동대지역코드", "11");
//		pList.put("법정동중지역코드", "11140");

		// returnData
		String strJsonData = postRequest(pURL, pList);


		System.out.println(strJsonData);

		getJsonElementData(strJsonData, "ARRAY수1");

		Gson gson = new GsonBuilder()
				.setPrettyPrinting()
				.serializeNulls()
				.create();

		/*
		JsonParser jp = new JsonParser();
		JsonElement element = jp.parse(strJsonData);
		System.out.println(gson.toJson(element));

		Type type = new TypeToken<ArrayList<Map<String, String>>>(){}.getType();
		HashMap<String, Object> fields = gson.fromJson(strJsonData, HashMap.class);
//		List<Map<String, String>> myMap = gson.fromJson(strJsonData, type);
		System.out.println(fields);
		System.out.println(fields.get("ARRAY수1"));

		Set st = fields.keySet();
		Iterator<String> it = st.iterator();
		while(it.hasNext()) {
			System.out.println("키: " +  (String)  it.next() );
		}
//		fields.get("ARRAY수1");

		HashMap<String, Object> hashMap = new HashMap<>(Utility.jsonToMap(strJsonData)) ;
		System.out.println(hashMap);
		it = hashMap.keySet().iterator();
		while(it.hasNext()) {
			System.out.println("키: " +  (String)  it.next() );
		}
		HashMap<String, String> temp = (HashMap)hashMap.get("msg");
		System.out.println(temp);

		*/
	}
}
