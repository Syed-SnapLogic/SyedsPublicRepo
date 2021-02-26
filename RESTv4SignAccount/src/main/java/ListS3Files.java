import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;

import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

/**
 * Example: Signing AWS Requests with Signature Version 4 in Java(Test class).
 * @reference: http://docs.aws.amazon.com/general/latest/gr/sigv4_signing.html
 */
public class ListS3Files {
    public static void main(String[] args) {


        Scanner sc = new Scanner(System.in);
        System.out.println("Enter AWS hostname");
        String hostname = sc.nextLine();
        System.out.println("Enter canonical URL");
        String url = sc.nextLine();
        System.out.println("Enter AWS region");
        String region = sc.nextLine();
        System.out.println("Enter S3 Access key");
        String aKey = sc.nextLine();
        System.out.println("Enter S3 Secret key");
        String sKey = sc.nextLine();
        System.out.println("Pass below Headers in request:");
        System.out.println("----------------");

        /**
         * Add host without http or https protocol.
         * You can also add other parameters based on your amazon service requirement.
         */
        TreeMap<String, String> awsHeaders = new TreeMap<String, String>();
        awsHeaders.put("host", hostname);

        AWSV4Auth aWSV4Auth = new AWSV4Auth.Builder(aKey, sKey)
                .regionName(region)
                .serviceName("s3") // es - elastic search. use your service name
                .httpMethodName("GET") //GET, PUT, POST, DELETE, etc...
                .canonicalURI(url)
                .queryParametes(null) //query parameters if any
                .awsHeaders(awsHeaders) //aws header parameters
                .payload(null) // payload if any
                //.debug() // turn on the debug mode
                .build();

        /* Get header calculated for request */
        Map<String, String> header = aWSV4Auth.getHeaders();
        System.out.println("----------------");
        for (Map.Entry<String, String> entrySet : header.entrySet()) {
            String key = entrySet.getKey();
            String value = entrySet.getValue();
            System.out.println(key);
            System.out.println(value);
            System.out.println("----------------");

            /* Attach header in your request */
            /* Simple get request */
            //HttpGet httpGet = new HttpGet(url);
            //httpGet.addHeader(key, value);
        }
        // execute httpGet
    }
}