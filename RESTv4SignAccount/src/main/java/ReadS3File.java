import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;

import uk.co.lucasweb.aws.v4.signer.HttpRequest;
import uk.co.lucasweb.aws.v4.signer.Signer;
import uk.co.lucasweb.aws.v4.signer.credentials.AwsCredentials;

public class ReadS3File {

    public static void main(String[] args) {
        String contentSha256 = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";
        try {
            Scanner sc = new Scanner(System.in);
            DateTime datetime = new DateTime(DateTimeZone.UTC);
            String time = datetime.toString(DateTimeFormat.forPattern("YYYYMMdd'T'HHmmss'Z'"));
            System.out.println("Enter HTTP URL");
            String url = sc.nextLine();
            System.out.println("Enter AWS hostname");
            String hostname = sc.nextLine();
            System.out.println("Enter AWS region");
            String region = sc.nextLine();
            System.out.println("Enter S3 Access key");
            String aKey = sc.nextLine();
            System.out.println("Enter S3 Secret key");
            String sKey = sc.nextLine();


            HttpRequest request = new HttpRequest("GET",
                    new URI(url));
            String signature = Signer.builder()
                    .awsCredentials(new AwsCredentials(aKey, sKey))
                    .header("Host", hostname)
                    .header("x-amz-date", time)
                    .header("x-amz-content-sha256", contentSha256)
                    .region(region)
                    .buildS3(request, contentSha256)
                    .getSignature();

            System.out.println("Pass below header parameters to request");
            System.out.println("x-amz-date: " + time);
            System.out.println("Authorization: " + signature);
            System.out.println("x-amz-content-sha256: e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855");
        } catch (URISyntaxException ex) {

        }
    }
}
